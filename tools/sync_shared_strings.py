#!/usr/bin/env python3
"""Sync translatable strings into kmp/mtprotoshared androidMain/res."""

from __future__ import annotations

import re
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
STRINGS_SOURCE = ROOT / "tools" / "strings_source" / "mtproto" / "res"
MTPROTO_RES = (
    STRINGS_SOURCE
    if (STRINGS_SOURCE / "values" / "strings.xml").exists()
    else ROOT / "mtproto" / "src" / "main" / "res"
)
COMPOSE_SOURCE = ROOT / "tools" / "strings_source" / "compose_strings.xml"
COMPOSE_RES = ROOT / "mtprotocompose" / "src" / "main" / "res"
SHARED_RES = ROOT / "kmp" / "mtprotoshared" / "src" / "androidMain" / "res"
WALHALLA_SHARED = Path(r"C:\Synced\WalhallaUI\shared\src\main\res")

LOCALES = [
    "en",
    "es",
    "fr",
    "de",
    "it",
    "pt",
    "el",
    "ru",
    "ja",
    "zh-rCN",
    "zh-rTW",
    "ko",
    "ar",
    "uk",
    "vi",
    "az",
    "uz",
]

# Keys that stay in app modules (ads, legacy junk).
APP_ONLY_KEYS = {
    "app_name",
    "facebook_app_id",
    "nav_header_title",
    "nav_header_subtitle",
    "nav_header_desc",
    "menu_home",
    "menu_gallery",
    "menu_slideshow",
    "menu_tools",
    "menu_share",
    "menu_send",
    "admob_app_id",
    "b1",
    "ad_unit_id",
    "run1",
    "rewardedId",
}

EXTRA_DEFAULT = {
    "tab_mtproto": "MTProto",
    "tab_webproxy": "WebProxy",
    "action_settings": "Settings",
    "action_share_app": "Share App",
    "action_rate_app": "Rate App",
    "action_discover_more_app": "Discover More Apps",
    "action_about": "About",
    "action_privacy_policy": "Privacy Policy",
    "action_exit": "Exit",
    "pref_title_contributors": "Contributors",
    "pref_title_night_mode": "Night mode",
    "pref_summary_contributors": "WhD, Abuzer Rafey, Siddiqov Mukhriddin",
    "dialog_proxy_title": "PROXY %1$s",
    "dialog_webproxy_title": "WEB-PROXY %1$s",
    "webview_title": "WebProxy (%1$s)",
    "action_disable": "DISABLE",
    "action_enable": "ENABLE",
    "action_update_geo": "UPDATE GEO",
    "err_maps_not_installed": "Google Maps not installed",
}

# Menu / UI strings per locale (merged when missing in mtproto locale file).
# hint / validation strings per locale (only in base English in mtproto).
HINT_LOCALE: dict[str, dict[str, str]] = {
    "ru": {
        "hint_server": "Сервер",
        "hint_port": "Порт",
        "hint_secret": "Секрет",
        "error_empty_field": "Пустое поле",
        "status": "Статус: %1$s",
    },
    "uk": {
        "hint_server": "Сервер",
        "hint_port": "Порт",
        "hint_secret": "Секрет",
        "error_empty_field": "Порожнє поле",
        "status": "Статус: %1$s",
    },
    "es": {
        "hint_server": "Servidor",
        "hint_port": "Puerto",
        "hint_secret": "Secreto",
        "error_empty_field": "Campo vacío",
        "status": "Estado: %1$s",
    },
    "fr": {
        "hint_server": "Serveur",
        "hint_port": "Port",
        "hint_secret": "Secret",
        "error_empty_field": "Champ vide",
        "status": "Statut : %1$s",
    },
    "de": {
        "hint_server": "Server",
        "hint_port": "Port",
        "hint_secret": "Geheimnis",
        "error_empty_field": "Leeres Feld",
        "status": "Status: %1$s",
    },
    "it": {
        "hint_server": "Server",
        "hint_port": "Porta",
        "hint_secret": "Segreto",
        "error_empty_field": "Campo vuoto",
        "status": "Stato: %1$s",
    },
    "pt": {
        "hint_server": "Servidor",
        "hint_port": "Porta",
        "hint_secret": "Segredo",
        "error_empty_field": "Campo vazio",
        "status": "Status: %1$s",
    },
    "el": {
        "hint_server": "Διακομιστής",
        "hint_port": "Θύρα",
        "hint_secret": "Μυστικό",
        "error_empty_field": "Κενό πεδίο",
        "status": "Κατάσταση: %1$s",
    },
    "ja": {
        "hint_server": "サーバー",
        "hint_port": "ポート",
        "hint_secret": "シークレット",
        "error_empty_field": "空のフィールド",
        "status": "ステータス: %1$s",
    },
    "zh-rCN": {
        "hint_server": "服务器",
        "hint_port": "端口",
        "hint_secret": "密钥",
        "error_empty_field": "字段为空",
        "status": "状态：%1$s",
    },
    "zh-rTW": {
        "hint_server": "伺服器",
        "hint_port": "連接埠",
        "hint_secret": "密鑰",
        "error_empty_field": "欄位為空",
        "status": "狀態：%1$s",
    },
    "ko": {
        "hint_server": "서버",
        "hint_port": "포트",
        "hint_secret": "비밀키",
        "error_empty_field": "빈 필드",
        "status": "상태: %1$s",
    },
    "ar": {
        "hint_server": "الخادم",
        "hint_port": "المنفذ",
        "hint_secret": "السر",
        "error_empty_field": "حقل فارغ",
        "status": "الحالة: %1$s",
    },
    "vi": {
        "hint_server": "Máy chủ",
        "hint_port": "Cổng",
        "hint_secret": "Mật khẩu",
        "error_empty_field": "Trường trống",
        "status": "Trạng thái: %1$s",
    },
    "az": {
        "hint_server": "Server",
        "hint_port": "Port",
        "hint_secret": "Sirr",
        "error_empty_field": "Boş sahə",
        "status": "Status: %1$s",
    },
    "uz": {
        "hint_server": "Server",
        "hint_port": "Port",
        "hint_secret": "Sir",
        "error_empty_field": "Bo'sh maydon",
        "status": "Holat: %1$s",
    },
}

LOCALE_UI: dict[str, dict[str, str]] = {
    "ru": {
        "action_settings": "Настройки",
        "action_share_app": "Поделиться приложением",
        "action_rate_app": "Оставить отзыв",
        "action_discover_more_app": "Другие приложения",
        "action_about": "О программе",
        "action_privacy_policy": "Политика конфиденциальности",
        "action_exit": "Выход",
        "pref_title_contributors": "Участники",
        "pref_title_night_mode": "Ночной режим",
        "dialog_proxy_title": "ПРОКСИ %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "ОТКЛЮЧИТЬ",
        "action_enable": "ВКЛЮЧИТЬ",
        "action_update_geo": "ОБНОВИТЬ GEO",
        "err_maps_not_installed": "Google Maps не установлен",
        "action_glype_connect": "Перейти к прокси",
        "wv_menu_clear_cookies": "Очистить cookies",
        "wwPageUpdated": "Страница обновлена",
    },
    "uk": {
        "action_settings": "Налаштування",
        "action_share_app": "Надіслати другу",
        "action_rate_app": "Залишити відгук",
        "action_discover_more_app": "Ще додатків…",
        "action_about": "Про додаток",
        "action_privacy_policy": "Політика конфіденційності",
        "action_exit": "Вихід",
        "pref_title_contributors": "Учасники",
        "pref_title_night_mode": "Нічний режим",
        "dialog_proxy_title": "ПРОКСІ %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "ВИМКНУТИ",
        "action_enable": "УВІМКНУТИ",
        "action_update_geo": "ОНОВИТИ GEO",
        "err_maps_not_installed": "Google Maps не встановлено",
        "action_glype_connect": "Перейти до проксі",
        "wv_menu_clear_cookies": "Очистити cookies",
        "wwPageUpdated": "Сторінку оновлено",
    },
    "es": {
        "action_settings": "Ajustes",
        "action_share_app": "Compartir aplicación",
        "action_rate_app": "Valorar aplicación",
        "action_discover_more_app": "Descubrir más apps",
        "action_about": "Acerca de",
        "action_privacy_policy": "Política de privacidad",
        "action_exit": "Salir",
        "pref_title_contributors": "Colaboradores",
        "pref_title_night_mode": "Modo nocturno",
        "dialog_proxy_title": "PROXY %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "DESACTIVAR",
        "action_enable": "ACTIVAR",
        "action_update_geo": "ACTUALIZAR GEO",
        "err_maps_not_installed": "Google Maps no está instalado",
        "action_glype_connect": "Ir al proxy",
        "wv_menu_clear_cookies": "Borrar cookies",
        "wwPageUpdated": "Página actualizada",
    },
    "fr": {
        "action_settings": "Paramètres",
        "action_share_app": "Partager l'application",
        "action_rate_app": "Noter l'application",
        "action_discover_more_app": "Plus d'applications",
        "action_about": "À propos",
        "action_privacy_policy": "Politique de confidentialité",
        "action_exit": "Quitter",
        "pref_title_contributors": "Contributeurs",
        "pref_title_night_mode": "Mode nuit",
        "dialog_proxy_title": "PROXY %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "DÉSACTIVER",
        "action_enable": "ACTIVER",
        "action_update_geo": "METTRE À JOUR GEO",
        "err_maps_not_installed": "Google Maps n'est pas installé",
        "action_glype_connect": "Aller au proxy",
        "wv_menu_clear_cookies": "Effacer les cookies",
        "wwPageUpdated": "Page mise à jour",
    },
    "de": {
        "action_settings": "Einstellungen",
        "action_share_app": "App teilen",
        "action_rate_app": "App bewerten",
        "action_discover_more_app": "Weitere Apps",
        "action_about": "Über",
        "action_privacy_policy": "Datenschutzrichtlinie",
        "action_exit": "Beenden",
        "pref_title_contributors": "Mitwirkende",
        "pref_title_night_mode": "Nachtmodus",
        "dialog_proxy_title": "PROXY %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "DEAKTIVIEREN",
        "action_enable": "AKTIVIEREN",
        "action_update_geo": "GEO AKTUALISIEREN",
        "err_maps_not_installed": "Google Maps ist nicht installiert",
        "action_glype_connect": "Zum Proxy gehen",
        "wv_menu_clear_cookies": "Cookies löschen",
        "wwPageUpdated": "Seite aktualisiert",
    },
    "it": {
        "action_settings": "Impostazioni",
        "action_share_app": "Condividi app",
        "action_rate_app": "Valuta app",
        "action_discover_more_app": "Altre app",
        "action_about": "Informazioni",
        "action_privacy_policy": "Informativa sulla privacy",
        "action_exit": "Esci",
        "pref_title_contributors": "Collaboratori",
        "pref_title_night_mode": "Modalità notturna",
        "dialog_proxy_title": "PROXY %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "DISABILITA",
        "action_enable": "ABILITA",
        "action_update_geo": "AGGIORNA GEO",
        "err_maps_not_installed": "Google Maps non installato",
        "action_glype_connect": "Vai al proxy",
        "wv_menu_clear_cookies": "Cancella cookie",
        "wwPageUpdated": "Pagina aggiornata",
    },
    "pt": {
        "action_settings": "Configurações",
        "action_share_app": "Compartilhar app",
        "action_rate_app": "Avaliar app",
        "action_discover_more_app": "Mais apps",
        "action_about": "Sobre",
        "action_privacy_policy": "Política de privacidade",
        "action_exit": "Sair",
        "pref_title_contributors": "Contribuidores",
        "pref_title_night_mode": "Modo noturno",
        "dialog_proxy_title": "PROXY %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "DESATIVAR",
        "action_enable": "ATIVAR",
        "action_update_geo": "ATUALIZAR GEO",
        "err_maps_not_installed": "Google Maps não instalado",
        "action_glype_connect": "Ir para o proxy",
        "wv_menu_clear_cookies": "Limpar cookies",
        "wwPageUpdated": "Página atualizada",
    },
    "el": {
        "action_settings": "Ρυθμίσεις",
        "action_share_app": "Κοινοποίηση εφαρμογής",
        "action_rate_app": "Αξιολόγηση",
        "action_discover_more_app": "Περισσότερες εφαρμογές",
        "action_about": "Σχετικά",
        "action_privacy_policy": "Πολιτική απορρήτου",
        "action_exit": "Έξοδος",
        "pref_title_contributors": "Συντελεστές",
        "pref_title_night_mode": "Νυχτερινή λειτουργία",
        "dialog_proxy_title": "PROXY %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "ΑΠΕΝΕΡΓΟΠΟΙΗΣΗ",
        "action_enable": "ΕΝΕΡΓΟΠΟΙΗΣΗ",
        "action_update_geo": "ΕΝΗΜΕΡΩΣΗ GEO",
        "err_maps_not_installed": "Το Google Maps δεν είναι εγκατεστημένο",
        "action_glype_connect": "Μετάβαση στο proxy",
        "wv_menu_clear_cookies": "Διαγραφή cookies",
        "wwPageUpdated": "Η σελίδα ενημερώθηκε",
    },
    "ja": {
        "action_settings": "設定",
        "action_share_app": "アプリを共有",
        "action_rate_app": "アプリを評価",
        "action_discover_more_app": "他のアプリ",
        "action_about": "アプリについて",
        "action_privacy_policy": "プライバシーポリシー",
        "action_exit": "終了",
        "pref_title_contributors": "貢献者",
        "pref_title_night_mode": "ナイトモード",
        "dialog_proxy_title": "PROXY %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "無効",
        "action_enable": "有効",
        "action_update_geo": "GEO更新",
        "err_maps_not_installed": "Google Mapsがインストールされていません",
        "action_glype_connect": "プロキシへ移動",
        "wv_menu_clear_cookies": "Cookieを消去",
        "wwPageUpdated": "ページを更新しました",
    },
    "zh-rCN": {
        "action_settings": "设置",
        "action_share_app": "分享应用",
        "action_rate_app": "评价应用",
        "action_discover_more_app": "发现更多应用",
        "action_about": "关于",
        "action_privacy_policy": "隐私政策",
        "action_exit": "退出",
        "pref_title_contributors": "贡献者",
        "pref_title_night_mode": "夜间模式",
        "dialog_proxy_title": "代理 %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "禁用",
        "action_enable": "启用",
        "action_update_geo": "更新 GEO",
        "err_maps_not_installed": "未安装 Google 地图",
        "action_glype_connect": "打开代理",
        "wv_menu_clear_cookies": "清除 Cookie",
        "wwPageUpdated": "页面已更新",
    },
    "zh-rTW": {
        "action_settings": "設定",
        "action_share_app": "分享應用程式",
        "action_rate_app": "評價應用程式",
        "action_discover_more_app": "探索更多應用程式",
        "action_about": "關於",
        "action_privacy_policy": "隱私權政策",
        "action_exit": "退出",
        "pref_title_contributors": "貢獻者",
        "pref_title_night_mode": "夜間模式",
        "dialog_proxy_title": "PROXY %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "停用",
        "action_enable": "啟用",
        "action_update_geo": "更新 GEO",
        "err_maps_not_installed": "未安裝 Google 地圖",
        "action_glype_connect": "前往代理",
        "wv_menu_clear_cookies": "清除 Cookie",
        "wwPageUpdated": "頁面已更新",
    },
    "ko": {
        "action_settings": "설정",
        "action_share_app": "앱 공유",
        "action_rate_app": "앱 평가",
        "action_discover_more_app": "더 많은 앱",
        "action_about": "정보",
        "action_privacy_policy": "개인정보 처리방침",
        "action_exit": "종료",
        "pref_title_contributors": "기여자",
        "pref_title_night_mode": "야간 모드",
        "dialog_proxy_title": "PROXY %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "비활성화",
        "action_enable": "활성화",
        "action_update_geo": "GEO 업데이트",
        "err_maps_not_installed": "Google 지도가 설치되어 있지 않습니다",
        "action_glype_connect": "프록시로 이동",
        "wv_menu_clear_cookies": "쿠키 삭제",
        "wwPageUpdated": "페이지가 업데이트되었습니다",
    },
    "ar": {
        "action_settings": "الإعدادات",
        "action_share_app": "مشاركة التطبيق",
        "action_rate_app": "قيّم التطبيق",
        "action_discover_more_app": "اكتشف المزيد",
        "action_about": "حول",
        "action_privacy_policy": "سياسة الخصوصية",
        "action_exit": "خروج",
        "pref_title_contributors": "المساهمون",
        "pref_title_night_mode": "الوضع الليلي",
        "dialog_proxy_title": "PROXY %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "تعطيل",
        "action_enable": "تفعيل",
        "action_update_geo": "تحديث GEO",
        "err_maps_not_installed": "Google Maps غير مثبت",
        "action_glype_connect": "انتقل إلى البروكسي",
        "wv_menu_clear_cookies": "مسح ملفات تعريف الارتباط",
        "wwPageUpdated": "تم تحديث الصفحة",
    },
    "vi": {
        "action_settings": "Cài đặt",
        "action_share_app": "Chia sẻ ứng dụng",
        "action_rate_app": "Đánh giá ứng dụng",
        "action_discover_more_app": "Khám phá thêm",
        "action_about": "Giới thiệu",
        "action_privacy_policy": "Chính sách quyền riêng tư",
        "action_exit": "Thoát",
        "pref_title_contributors": "Cộng tác viên",
        "pref_title_night_mode": "Chế độ ban đêm",
        "dialog_proxy_title": "PROXY %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "TẮT",
        "action_enable": "BẬT",
        "action_update_geo": "CẬP NHẬT GEO",
        "err_maps_not_installed": "Chưa cài Google Maps",
        "action_glype_connect": "Mở proxy",
        "wv_menu_clear_cookies": "Xóa cookie",
        "wwPageUpdated": "Đã cập nhật trang",
    },
    "az": {
        "action_settings": "Parametrlər",
        "action_share_app": "Tətbiqi paylaş",
        "action_rate_app": "Tətbiqi qiymətləndir",
        "action_discover_more_app": "Daha çox tətbiq",
        "action_about": "Haqqında",
        "action_privacy_policy": "Məxfilik siyasəti",
        "action_exit": "Çıxış",
        "pref_title_contributors": "Töhfəverənlər",
        "pref_title_night_mode": "Gecə rejimi",
        "dialog_proxy_title": "PROXY %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "DEAKTIV ET",
        "action_enable": "AKTIV ET",
        "action_update_geo": "GEO YENİLƏ",
        "err_maps_not_installed": "Google Maps quraşdırılmayıb",
        "action_glype_connect": "Proxya keç",
        "wv_menu_clear_cookies": "Cookie-ləri sil",
        "wwPageUpdated": "Səhifə yeniləndi",
    },
    "uz": {
        "action_settings": "Sozlamalar",
        "action_share_app": "Ilovani ulashish",
        "action_rate_app": "Ilovani baholash",
        "action_discover_more_app": "Boshqa ilovalar",
        "action_about": "Haqida",
        "action_privacy_policy": "Maxfiylik siyosati",
        "action_exit": "Chiqish",
        "pref_title_contributors": "Hissa qo\'shganlar",
        "pref_title_night_mode": "Tungi rejim",
        "dialog_proxy_title": "PROXY %1$s",
        "dialog_webproxy_title": "WEB-PROXY %1$s",
        "webview_title": "WebProxy (%1$s)",
        "action_disable": "O\'CHIRISH",
        "action_enable": "YOQISH",
        "action_update_geo": "GEO YANGILASH",
        "err_maps_not_installed": "Google Maps o\'rnatilmagan",
        "action_glype_connect": "Proxyga o\'tish",
        "wv_menu_clear_cookies": "Cookie-larni tozalash",
        "wwPageUpdated": "Sahifa yangilandi",
    },
}


def parse_strings(path: Path) -> dict[str, tuple[str, bool]]:
    if not path.exists():
        return {}
    text = path.read_text(encoding="utf-8")
    result: dict[str, tuple[str, bool]] = {}
    for match in re.finditer(r'<string\s+([^>]*?)>(.*?)</string>', text, re.DOTALL):
        attrs, body = match.group(1), match.group(2)
        name_match = re.search(r'name="([^"]+)"', attrs)
        if not name_match:
            continue
        name = name_match.group(1)
        translatable = 'translatable="false"' not in attrs
        value = body.strip()
        result[name] = (value, translatable)
    return result


def escape_android_string(value: str) -> str:
    value = value.replace("\\'", "'")
    out: list[str] = []
    for ch in value:
        if ch == "'":
            out.append("\\'")
        elif ch == "&":
            out.append("&amp;")
        elif ch == "<":
            out.append("&lt;")
        elif ch == ">":
            out.append("&gt;")
        else:
            out.append(ch)
    return "".join(out)


def write_strings(path: Path, items: dict[str, tuple[str, bool]]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    lines = ['<?xml version="1.0" encoding="utf-8"?>', "<resources>"]
    for name in sorted(items.keys()):
        value, translatable = items[name]
        escaped = escape_android_string(value)
        if not translatable:
            lines.append(f'    <string name="{name}" translatable="false">{escaped}</string>')
        else:
            lines.append(f'    <string name="{name}">{escaped}</string>')
    lines.append("</resources>")
    lines.append("")
    path.write_text("\n".join(lines), encoding="utf-8")


def locale_dir(locale: str) -> str:
    return "values" if locale == "en" else f"values-{locale}"


def main() -> None:
    base = parse_strings(MTPROTO_RES / "values" / "strings.xml")
    compose_path = COMPOSE_SOURCE if COMPOSE_SOURCE.exists() else COMPOSE_RES / "values" / "strings.xml"
    compose = parse_strings(compose_path)

    default: dict[str, tuple[str, bool]] = {}
    for key, (value, translatable) in base.items():
        if key in APP_ONLY_KEYS:
            continue
        default[key] = (value, translatable)
    for key, (value, translatable) in compose.items():
        if key in APP_ONLY_KEYS:
            continue
        default.setdefault(key, (value, translatable))
    for key, value in EXTRA_DEFAULT.items():
        default.setdefault(key, (value, key not in {"tab_mtproto", "tab_webproxy", "pref_summary_contributors"}))

    default["tab_mtproto"] = ("MTProto", False)
    default["tab_webproxy"] = ("WebProxy", False)
    default["pref_summary_contributors"] = (EXTRA_DEFAULT["pref_summary_contributors"], False)
    if "msg_no_notes2" in compose:
        default["msg_no_notes2"] = compose["msg_no_notes2"]

    write_strings(SHARED_RES / "values" / "strings.xml", default)

    arrays_src = COMPOSE_RES / "values" / "arrays.xml"
    if not arrays_src.exists():
        arrays_src = ROOT / "tools" / "strings_source" / "compose_arrays.xml"
    if arrays_src.exists():
        (SHARED_RES / "values" / "arrays.xml").write_text(
            arrays_src.read_text(encoding="utf-8"), encoding="utf-8"
        )

    for locale in LOCALES:
        folder = locale_dir(locale)
        merged = dict(default)
        mtproto_locale = parse_strings(MTPROTO_RES / folder / "strings.xml")
        walhalla_locale = parse_strings(WALHALLA_SHARED / folder / "strings.xml")
        extra = LOCALE_UI.get(locale, {})
        hints = HINT_LOCALE.get(locale, {})

        for key in list(merged.keys()):
            if key in mtproto_locale:
                merged[key] = mtproto_locale[key]
                continue
            if not merged[key][1]:
                continue
            if key in walhalla_locale and walhalla_locale[key][1]:
                merged[key] = (walhalla_locale[key][0], True)
            elif key in extra:
                merged[key] = (extra[key], True)
            elif key in hints:
                merged[key] = (hints[key], True)

        if locale == "en":
            continue
        else:
            write_strings(SHARED_RES / folder / "strings.xml", merged)

    print(f"Wrote shared strings to {SHARED_RES}")


if __name__ == "__main__":
    main()
