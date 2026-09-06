"""MTProto pipeline: scrape t.me → geo (ip-api) → push Firebase RTDB ``a``.

Uses extract_proxies.js (DOM) via Selenium. Newest (page bottom) first → highest
update_at (app shows them on top). Aborts write/push if fewer than MIN_PROXIES.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import random
import socket
import struct
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path
from typing import Any
from urllib.parse import parse_qs

from selenium import webdriver
from selenium.common.exceptions import TimeoutException, WebDriverException
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

ROOT = Path(__file__).resolve().parent
TOOLS = ROOT / "tools"
if str(TOOLS) not in sys.path:
    sys.path.insert(0, str(TOOLS))

from firebase_resolver import (  # noqa: E402
    IP_API_BATCH_MAX,
    IP_API_BATCH_RPM,
    apply_geo_mtproto,
    build_geo_query,
    fetch_geo_batch_with_headers,
)

CHANNEL_URL = "https://t.me/s/ProxyMTProto"
OUT_FILE = ROOT / "proxies.txt"
EXTRACT_JS = ROOT / "extract_proxies.js"

FIREBASE_BASE = "https://mtprotolist.firebaseio.com"
FIREBASE_NODE = "a"  # MTProto
MIN_PROXIES = 20
TARGET_PROXIES = 60  # достаточно; не тянем весь канал
UPDATE_AT_STEP_MS = 1000
GEO_CHUNK = IP_API_BATCH_MAX  # 100 max per official docs
PUSH_DELAY_SEC = 0.05  # unused (single-shot PUT)
HTTP_TIMEOUT = 60
DNS_SERVERS = ("1.1.1.1", "8.8.8.8", "9.9.9.9")


def load_extract_script() -> str:
    if not EXTRACT_JS.is_file():
        raise FileNotFoundError(f"Missing {EXTRACT_JS}")
    return EXTRACT_JS.read_text(encoding="utf-8")


def md5_hex(text: str) -> str:
    return hashlib.md5(text.encode("utf-8")).hexdigest()


def share_url(host: str, port: str, secret: str) -> str:
    return f"https://t.me/proxy?server={host}&port={port}&secret={secret}"


def firebase_key(host: str, port: str, secret: str) -> str:
    return md5_hex(share_url(host, port, secret))


def _is_ipv4(value: str) -> bool:
    parts = value.split(".")
    if len(parts) != 4:
        return False
    try:
        return all(0 <= int(p) <= 255 for p in parts)
    except ValueError:
        return False


def _is_public_ipv4(ip: str) -> bool:
    """Reject loopback/private — system DNS often sinkholes junk hosts to 127.x."""
    if not _is_ipv4(ip):
        return False
    a, b, c, d = (int(x) for x in ip.split("."))
    if a == 0 or a == 10 or a == 127 or a >= 224:
        return False
    if a == 169 and b == 254:
        return False
    if a == 172 and 16 <= b <= 31:
        return False
    if a == 192 and b == 168:
        return False
    if a == 100 and 64 <= b <= 127:  # CGNAT
        return False
    return True


def _dns_query_a_udp(hostname: str, server: str, timeout: float = 3.0) -> str | None:
    """Minimal DNS A lookup against a public resolver (bypass broken system DNS)."""
    name = hostname.strip().rstrip(".").lower()
    if not name:
        return None
    transaction_id = random.randint(0, 65535)
    header = struct.pack("!HHHHHH", transaction_id, 0x0100, 1, 0, 0, 0)
    question = b"".join(
        bytes([len(label)]) + label.encode("ascii") for label in name.split(".")
    ) + b"\x00" + struct.pack("!HH", 1, 1)  # QTYPE=A, QCLASS=IN
    packet = header + question

    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.settimeout(timeout)
    try:
        sock.sendto(packet, (server, 53))
        data, _ = sock.recvfrom(512)
    except OSError:
        return None
    finally:
        sock.close()

    if len(data) < 12:
        return None
    recv_id, flags, qdcount, ancount, _, _ = struct.unpack("!HHHHHH", data[:12])
    if recv_id != transaction_id or ancount == 0:
        return None
    # skip question
    offset = 12
    for _ in range(qdcount):
        while offset < len(data) and data[offset] != 0:
            # compression or label
            if data[offset] & 0xC0 == 0xC0:
                offset += 2
                break
            offset += 1 + data[offset]
        else:
            offset += 1  # null
        offset += 4  # qtype+qclass
    for _ in range(ancount):
        if offset >= len(data):
            break
        if data[offset] & 0xC0 == 0xC0:
            offset += 2
        else:
            while offset < len(data) and data[offset] != 0:
                offset += 1 + data[offset]
            offset += 1
        if offset + 10 > len(data):
            break
        rtype, _, _, rdlength = struct.unpack("!HHIH", data[offset : offset + 10])
        offset += 10
        rdata = data[offset : offset + rdlength]
        offset += rdlength
        if rtype == 1 and rdlength == 4:
            ip = ".".join(str(b) for b in rdata)
            if _is_public_ipv4(ip):
                return ip
    return None


def _dns_query_a_doh(hostname: str, timeout: float = 8.0) -> str | None:
    name = hostname.strip().rstrip(".").lower()
    q = urllib.parse.urlencode({"name": name, "type": "A"})
    req = urllib.request.Request(
        f"https://cloudflare-dns.com/dns-query?{q}",
        headers={"Accept": "application/dns-json"},
        method="GET",
    )
    try:
        with urllib.request.urlopen(req, timeout=timeout) as resp:
            data = json.loads(resp.read().decode("utf-8"))
    except (OSError, urllib.error.URLError, json.JSONDecodeError, ValueError):
        return None
    for ans in data.get("Answer") or []:
        if ans.get("type") == 1:
            ip = str(ans.get("data") or "")
            if _is_public_ipv4(ip):
                return ip
    return None


def resolve_public_ip(host: str) -> str | None:
    """Resolve host to a public IPv4 via public DNS (not system sinkhole)."""
    host = (host or "").strip().rstrip(".")
    if not host:
        return None
    if _is_ipv4(host):
        return host if _is_public_ipv4(host) else None

    for server in DNS_SERVERS:
        ip = _dns_query_a_udp(host, server)
        if ip:
            return ip
    return _dns_query_a_doh(host)


def parse_tg_proxy(link: str) -> dict[str, str] | None:
    link = link.strip()
    if not link.startswith("tg://proxy?"):
        return None
    qs = parse_qs(link.split("?", 1)[1])
    host = (qs.get("server") or [None])[0]
    port = (qs.get("port") or [None])[0]
    secret = (qs.get("secret") or [None])[0]
    if not host or not port or not secret or not str(port).isdigit():
        return None
    return {"host": host, "port": str(port), "secret": secret}


def build_driver(headless: bool, socks: str | None = None) -> webdriver.Chrome:
    options = Options()
    if headless:
        options.add_argument("--headless=new")
    options.add_argument("--disable-gpu")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--window-size=1280,1600")
    options.add_argument("--lang=en-US")
    options.add_argument("--disable-blink-features=AutomationControlled")
    options.page_load_strategy = "eager"
    options.add_argument(
        "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"
    )
    if socks:
        options.add_argument(f"--proxy-server={socks}")
    driver = webdriver.Chrome(options=options, service=Service())
    driver.set_page_load_timeout(90)
    return driver


def inject_extract_js(driver: webdriver.Chrome, script: str) -> None:
    driver.execute_script("window.__MTPROTO_EXTRACT_NO_AUTO__ = true;")
    driver.execute_script(script)


def collect_via_extract_js(driver: webdriver.Chrome) -> list[str]:
    result = driver.execute_script(
        "return (typeof window.extractMtprotoProxies === 'function') "
        "? window.extractMtprotoProxies() : [];"
    )
    if not result:
        return []
    out: list[str] = []
    seen: set[str] = set()
    for item in result:
        url = str(item).strip()
        if not url.startswith("tg://proxy?") or url in seen:
            continue
        seen.add(url)
        out.append(url)
    return out


def scroll_up_load_history(
    driver: webdriver.Chrome,
    script: str,
    scrolls: int,
    pause: float,
    target: int = TARGET_PROXIES,
) -> None:
    inject_extract_js(driver, script)
    last_height = 0
    stagnant = 0
    for i in range(scrolls):
        count = len(collect_via_extract_js(driver))
        print(
            f"  scroll-up {i + 1}/{scrolls}: visible≈{count} (target={target})",
            flush=True,
        )
        if count >= target:
            print(f"  reached target {target} — stop scrolling", flush=True)
            break

        driver.execute_script("window.scrollTo(0, 0);")
        time.sleep(pause)
        driver.execute_script("window.scrollBy(0, 200);")
        time.sleep(0.25)
        driver.execute_script("window.scrollTo(0, 0);")
        time.sleep(pause)
        height = int(driver.execute_script("return document.body.scrollHeight") or 0)

        if height == last_height:
            stagnant += 1
            if stagnant >= 3:
                print("  page stopped growing — stop scrolling", flush=True)
                break
        else:
            stagnant = 0
        last_height = height


def scrape_proxies(
    url: str,
    scrolls: int,
    pause: float,
    headless: bool,
    socks: str | None,
    target: int = TARGET_PROXIES,
) -> list[str]:
    script = load_extract_script()
    driver = None
    try:
        print(f"Selenium → {url}", flush=True)
        print(f"Target proxies: {target}", flush=True)
        if socks:
            print(f"SOCKS: {socks}", flush=True)
        driver = build_driver(headless=headless, socks=socks)
        driver.get(url)
        WebDriverWait(driver, 25).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, "body"))
        )
        try:
            WebDriverWait(driver, 15).until(
                EC.presence_of_element_located(
                    (By.CSS_SELECTOR, ".tgme_widget_message, a[href*='/proxy?']")
                )
            )
        except TimeoutException:
            print("Warning: proxy links not ready yet — continuing", flush=True)
        time.sleep(2.0)
        scroll_up_load_history(
            driver, script, scrolls=scrolls, pause=pause, target=target
        )
        inject_extract_js(driver, script)
        links = collect_via_extract_js(driver)
        # newest-first; keep only target count
        if len(links) > target:
            links = links[:target]
            print(f"Trimmed to {target} newest proxies", flush=True)
        return links
    finally:
        if driver is not None:
            driver.quit()


def links_to_records(links: list[str], base_update_at: int | None = None) -> list[dict[str, Any]]:
    """Newest-first links → records with descending update_at."""
    now = base_update_at if base_update_at is not None else int(time.time() * 1000)
    records: list[dict[str, Any]] = []
    seen_keys: set[str] = set()
    for i, link in enumerate(links):
        parsed = parse_tg_proxy(link)
        if not parsed:
            continue
        key = firebase_key(parsed["host"], parsed["port"], parsed["secret"])
        if key in seen_keys:
            continue
        seen_keys.add(key)
        records.append(
            {
                "host": parsed["host"],
                "port": parsed["port"],
                "secret": parsed["secret"],
                "enabled": True,
                "code": "",
                "update_at": now - i * UPDATE_AT_STEP_MS,
                "_key": key,
                "_tg": link,
            }
        )
    return records


def resolve_geo(records: list[dict[str, Any]], chunk_size: int = GEO_CHUNK) -> tuple[int, int]:
    """
    Batch geo via http://ip-api.com/batch
    Limits (free): max 100 IPs / request, 15 batch requests / minute.
    Headers: X-Rl (remaining), X-Ttl (seconds to reset).
    For TARGET_PROXIES=60 → usually ONE request.
    """
    chunk_size = min(max(1, chunk_size), IP_API_BATCH_MAX)
    print(
        f"ip-api batch limits: ≤{IP_API_BATCH_MAX} IPs/req, "
        f"≤{IP_API_BATCH_RPM} req/min; chunk={chunk_size}",
        flush=True,
    )

    ok = 0
    err = 0
    total = len(records)
    for offset in range(0, total, chunk_size):
        chunk = records[offset : offset + chunk_size]
        queries: list[dict[str, str]] = []
        index_map: list[int] = []
        for idx, row in enumerate(chunk):
            host = str(row.get("host") or "").strip().rstrip(".")
            ip = resolve_public_ip(host)
            if not ip:
                row["_geo_error"] = "cannot resolve public IP"
                err += 1
                continue
            row["_resolved_ip"] = ip
            queries.append(build_geo_query(ip))
            index_map.append(idx)
        if not queries:
            continue
        print(
            f"  geo BATCH {offset + 1}-{offset + len(chunk)}/{total} "
            f"→ POST {len(queries)} IPs in one request...",
            flush=True,
        )
        try:
            geos, headers = fetch_geo_batch_with_headers(queries)
            rl = headers.get("X-Rl") or "?"
            ttl = headers.get("X-Ttl") or "?"
            print(f"  rate-limit X-Rl={rl} X-Ttl={ttl}s", flush=True)
            try:
                if int(rl) <= 0 and str(ttl).isdigit():
                    wait = int(ttl) + 1
                    print(f"  X-Rl=0 → sleep {wait}s", flush=True)
                    time.sleep(wait)
            except ValueError:
                pass
        except urllib.error.HTTPError as e:
            if e.code == 429:
                ttl = e.headers.get("X-Ttl") or e.headers.get("x-ttl") or "60"
                wait = int(ttl) if str(ttl).isdigit() else 60
                print(f"  HTTP 429 → sleep {wait}s and retry once", flush=True)
                time.sleep(wait)
                try:
                    geos, headers = fetch_geo_batch_with_headers(queries)
                except Exception as e2:
                    for i in index_map:
                        chunk[i]["_geo_error"] = str(e2)
                    err += len(index_map)
                    continue
            else:
                for i in index_map:
                    chunk[i]["_geo_error"] = f"HTTP {e.code}"
                err += len(index_map)
                continue
        except (OSError, urllib.error.URLError, ValueError, json.JSONDecodeError) as e:
            for i in index_map:
                chunk[i]["_geo_error"] = str(e)
            err += len(index_map)
            time.sleep(2.0)
            continue

        for chunk_idx, geo in zip(index_map, geos):
            row = chunk[chunk_idx]
            apply_geo_mtproto(row, geo)
            if row.get("_geo_error"):
                err += 1
            else:
                ok += 1

        # Soft pace between batches: 15 req/min → ≥4s apart
        if offset + chunk_size < total:
            time.sleep(4.1)
    return ok, err


def record_payload(row: dict[str, Any]) -> dict[str, Any]:
    """JSON body for Firebase (no private _ fields)."""
    payload: dict[str, Any] = {
        "host": row.get("host") or "",
        "port": row.get("port") or "",
        "secret": row.get("secret") or "",
        "enabled": bool(row.get("enabled", True)),
        "code": row.get("code") or "",
        "update_at": int(row.get("update_at") or 0),
    }
    for field in (
        "city",
        "regionName",
        "country",
        "region",
        "zip",
        "timezone",
        "isp",
        "org",
        "as",
    ):
        val = row.get(field)
        if val is not None and val != "":
            payload[field] = val
    for field in ("lat", "lon"):
        val = row.get(field)
        if val is None or val == "":
            payload[field] = 0
        else:
            try:
                payload[field] = float(val)
            except (TypeError, ValueError):
                payload[field] = 0
    return payload


def push_firebase(
    records: list[dict[str, Any]],
    dry_run: bool = False,
) -> tuple[int, int]:
    """One PUT replaces the whole RTDB node ``a`` with the ready map."""
    if not records:
        print("ABORT push: empty records", flush=True)
        return 0, 1

    node: dict[str, Any] = {}
    for row in records:
        key = row.get("_key") or firebase_key(row["host"], row["port"], row["secret"])
        node[key] = record_payload(row)

    url = f"{FIREBASE_BASE}/{FIREBASE_NODE}.json"
    body = json.dumps(node, ensure_ascii=False)
    size_kb = len(body.encode("utf-8")) / 1024
    print(
        f"  PUT {url}  ({len(node)} proxies, {size_kb:.1f} KB) "
        f"{'(dry-run)' if dry_run else ''}",
        flush=True,
    )
    if dry_run:
        return len(node), 0

    req = urllib.request.Request(
        url,
        data=body.encode("utf-8"),
        headers={"Content-Type": "application/json"},
        method="PUT",
    )
    try:
        with urllib.request.urlopen(req, timeout=HTTP_TIMEOUT) as resp:
            if 200 <= resp.status < 300:
                print(f"  OK HTTP {resp.status}", flush=True)
                return len(node), 0
            print(f"  HTTP {resp.status}", flush=True)
            return 0, 1
    except urllib.error.HTTPError as e:
        print(f"  HTTPError {e.code}: {e.reason}", flush=True)
        return 0, 1
    except (OSError, urllib.error.URLError) as e:
        print(f"  Error: {e}", flush=True)
        return 0, 1


def run_pipeline(
    url: str = CHANNEL_URL,
    out: Path = OUT_FILE,
    scrolls: int = 15,
    pause: float = 1.2,
    headless: bool = True,
    socks: str | None = None,
    min_proxies: int = MIN_PROXIES,
    target_proxies: int = TARGET_PROXIES,
    do_geo: bool = True,
    do_push: bool = True,
    dry_run: bool = False,
) -> int:
    print(f"Extract JS → {EXTRACT_JS.name}", flush=True)
    print(f"Min proxies: {min_proxies}, target: {target_proxies}", flush=True)

    try:
        links = scrape_proxies(
            url=url,
            scrolls=scrolls,
            pause=pause,
            headless=headless,
            socks=socks,
            target=target_proxies,
        )
    except TimeoutException as e:
        print(f"Timeout: {e}", flush=True)
        return 1
    except WebDriverException as e:
        print(f"Selenium error: {e}", flush=True)
        print("Hint: Proxifier Direct for chrome, or --socks socks5://127.0.0.1:9150", flush=True)
        return 1
    except FileNotFoundError as e:
        print(str(e), flush=True)
        return 1

    if len(links) < min_proxies:
        print(
            f"ABORT: scrape дал {len(links)} < {min_proxies}. "
            f"Не пишем файл и не пушим в Firebase.",
            flush=True,
        )
        return 1

    out.write_text("\n".join(links) + "\n", encoding="utf-8")
    print(f"Сохранено {len(links)} ссылок (newest-first) → {out}", flush=True)

    records = links_to_records(links)
    if len(records) < min_proxies:
        print(
            f"ABORT: после парсинга {len(records)} < {min_proxies}. Firebase не трогаем.",
            flush=True,
        )
        return 1

    print(
        f"Parsed {len(records)} records; #1 host={records[0].get('host')} "
        f"(должен быть самым свежим)",
        flush=True,
    )

    if do_geo:
        print("Resolving geo via ip-api.com/batch ...", flush=True)
        geo_ok, geo_err = resolve_geo(records)
        print(f"Geo: ok={geo_ok} err={geo_err}", flush=True)
    else:
        print("Geo skipped (--no-geo)", flush=True)

    if not do_push:
        print("Push skipped (--no-push)", flush=True)
        return 0

    # Re-check before Firebase
    if len(records) < min_proxies:
        print("ABORT before push: too few records", flush=True)
        return 1

    print(
        f"Pushing whole node {FIREBASE_BASE}/{FIREBASE_NODE}.json "
        f"({len(records)} items)"
        f"{' [dry-run]' if dry_run else ''} ...",
        flush=True,
    )
    push_ok, push_err = push_firebase(records, dry_run=dry_run)
    print(f"Push: ok={push_ok} err={push_err}", flush=True)
    return 0 if push_err == 0 else 1


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Scrape MTProto proxies → geo → Firebase node a"
    )
    parser.add_argument("--url", default=CHANNEL_URL)
    parser.add_argument("--out", type=Path, default=OUT_FILE)
    parser.add_argument("--scrolls", type=int, default=15)
    parser.add_argument("--pause", type=float, default=1.2)
    parser.add_argument("--headed", action="store_true")
    parser.add_argument("--socks", default=None, help="e.g. socks5://127.0.0.1:9150")
    parser.add_argument("--min", type=int, default=MIN_PROXIES, dest="min_proxies")
    parser.add_argument(
        "--target",
        type=int,
        default=TARGET_PROXIES,
        dest="target_proxies",
        help=f"Stop after N newest proxies (default {TARGET_PROXIES})",
    )
    parser.add_argument("--no-geo", action="store_true", help="Skip ip-api geo")
    parser.add_argument("--no-push", action="store_true", help="Only scrape + save txt")
    parser.add_argument("--dry-run", action="store_true", help="Resolve but do not PUT")
    args = parser.parse_args()
    return run_pipeline(
        url=args.url,
        out=args.out,
        scrolls=args.scrolls,
        pause=args.pause,
        headless=not args.headed,
        socks=args.socks,
        min_proxies=args.min_proxies,
        target_proxies=args.target_proxies,
        do_geo=not args.no_geo,
        do_push=not args.no_push,
        dry_run=args.dry_run,
    )


if __name__ == "__main__":
    raise SystemExit(main())
