"""Geo and web-proxy type resolution (loader MUtils parity)."""

from __future__ import annotations

import json
import socket
import urllib.error
import urllib.parse
import urllib.request
from typing import Any
from urllib.parse import urlparse

IP_API_BATCH_URL = "http://ip-api.com/batch"
HTTP_TIMEOUT_SEC = 20
PROXY_BODY_LIMIT = 512_000


def _is_ip(value: str) -> bool:
    parts = value.split(".")
    if len(parts) != 4:
        return False
    try:
        return all(0 <= int(part) <= 255 for part in parts)
    except ValueError:
        return False


def resolve_host_to_ip(host: str | None) -> str | None:
    if not host:
        return None
    host = host.strip()
    if not host:
        return None
    if _is_ip(host):
        return host
    try:
        return socket.gethostbyname(host)
    except OSError:
        return None


def resolve_webproxy_host(proxy_url: str | None) -> str | None:
    if not proxy_url:
        return None
    try:
        parsed = urlparse(proxy_url.strip())
        hostname = parsed.hostname
        if not hostname:
            return None
        ascii_host = hostname.encode("idna").decode("ascii")
        return resolve_host_to_ip(ascii_host)
    except (OSError, UnicodeError, ValueError):
        return None


def build_geo_query(ip: str) -> dict[str, str]:
    return {"query": ip, "lang": "EN"}


def fetch_geo_batch(queries: list[dict[str, str]]) -> list[dict[str, Any]]:
    if not queries:
        return []
    payload = json.dumps(queries).encode("utf-8")
    request = urllib.request.Request(
        IP_API_BATCH_URL,
        data=payload,
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    with urllib.request.urlopen(request, timeout=HTTP_TIMEOUT_SEC) as response:
        body = response.read().decode("utf-8")
    data = json.loads(body)
    if not isinstance(data, list):
        raise ValueError("ip-api batch response must be a list")
    return data


def apply_geo_mtproto(row: dict[str, Any], ip_info: dict[str, Any]) -> None:
    if ip_info.get("status") == "fail":
        row["_geo_error"] = ip_info.get("message", "geo lookup failed")
        return
    row.pop("_geo_error", None)
    row["city"] = ip_info.get("city")
    row["regionName"] = ip_info.get("regionName")
    row["lat"] = ip_info.get("lat")
    row["lon"] = ip_info.get("lon")
    row["code"] = ip_info.get("countryCode")
    row["country"] = ip_info.get("country")
    row["region"] = ip_info.get("region")
    row["zip"] = ip_info.get("zip")
    row["timezone"] = ip_info.get("timezone")
    row["isp"] = ip_info.get("isp")
    row["org"] = ip_info.get("org")
    row["as"] = ip_info.get("as")


def apply_geo_webproxy(row: dict[str, Any], ip_info: dict[str, Any]) -> None:
    if ip_info.get("status") == "fail":
        row["_geo_error"] = ip_info.get("message", "geo lookup failed")
        return
    row.pop("_geo_error", None)
    row["city"] = ip_info.get("city")
    row["regionName"] = ip_info.get("regionName")
    row["lat"] = ip_info.get("lat")
    row["lon"] = ip_info.get("lon")
    row["code"] = ip_info.get("countryCode")
    row["country"] = ip_info.get("country")
    row["region"] = ip_info.get("region")
    row["zip"] = ip_info.get("zip")
    row["timezone"] = ip_info.get("timezone")
    row["isp"] = ip_info.get("isp")
    row["org"] = ip_info.get("org")
    row["as"] = ip_info.get("as")
    ip = row.get("ip") or ""
    if len(str(ip)) < 6:
        row["ip"] = ip_info.get("query")


def detect_proxy_type(proxy_url: str | None) -> str:
    if not proxy_url:
        raise ValueError("proxyUrl is empty")
    request = urllib.request.Request(
        proxy_url,
        headers={"User-Agent": "MTProto-List-Firebase-Viewer/1.0"},
        method="GET",
    )
    with urllib.request.urlopen(request, timeout=HTTP_TIMEOUT_SEC) as response:
        body = response.read(PROXY_BODY_LIMIT).decode("utf-8", errors="replace")
    if "Glype" in body:
        return "Glype"
    if "PHP-Proxy" in body:
        return "PHP-Proxy"
    if "PHProxy" in body:
        return "PHProxy"
    return "Other"


def resolve_geo_for_rows(rows: list[dict[str, Any]], kind: str, chunk_size: int = 100) -> tuple[int, int]:
    """Returns (success_count, error_count)."""
    if not rows:
        return 0, 0

    success = 0
    errors = 0

    for offset in range(0, len(rows), chunk_size):
        chunk = rows[offset : offset + chunk_size]
        queries: list[dict[str, str]] = []
        index_map: list[int] = []

        for index, row in enumerate(chunk):
            if kind == "webproxy":
                ip = resolve_webproxy_host(str(row.get("proxyUrl") or ""))
            else:
                ip = resolve_host_to_ip(str(row.get("host") or ""))

            if not ip:
                row["_geo_error"] = "cannot resolve host to IP"
                errors += 1
                continue

            queries.append(build_geo_query(ip))
            index_map.append(index)

        if not queries:
            continue

        try:
            geo_results = fetch_geo_batch(queries)
        except (OSError, urllib.error.URLError, ValueError, json.JSONDecodeError) as error:
            for row in chunk:
                row["_geo_error"] = str(error)
            errors += len(chunk)
            continue

        for chunk_index, geo in zip(index_map, geo_results):
            row = chunk[chunk_index]
            if kind == "webproxy":
                apply_geo_webproxy(row, geo)
            else:
                apply_geo_mtproto(row, geo)
            if row.get("_geo_error"):
                errors += 1
            else:
                success += 1

    return success, errors


def resolve_type_for_rows(rows: list[dict[str, Any]]) -> tuple[int, int]:
    """Returns (success_count, error_count)."""
    success = 0
    errors = 0
    for row in rows:
        proxy_url = row.get("proxyUrl")
        if not proxy_url:
            row["_type_error"] = "proxyUrl is empty"
            errors += 1
            continue
        try:
            row["type"] = detect_proxy_type(str(proxy_url))
            row.pop("_type_error", None)
            success += 1
        except (OSError, urllib.error.URLError, ValueError) as error:
            row["_type_error"] = str(error)
            errors += 1
    return success, errors
