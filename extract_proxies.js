/**
 * Извлечение MTProto-прокси со страницы t.me/s/ProxyMTProto.
 *
 * Порядок DOM: сверху вниз = старше → свежее (низ страницы = самые новые).
 * Возвращаем в обратном порядке: свежие первыми → верх списка в Firebase
 * (приложение сортирует по update_at desc).
 *
 * window.extractMtprotoProxies() — для консоли и для fetch_proxies.py (Selenium).
 *
 * Консоль:
 * 1. Открой https://t.me/s/ProxyMTProto
 * 2. Прокрути ВВЕРХ, чтобы подгрузить больше постов
 * 3. F12 → Console → вставь этот файл
 */
window.extractMtprotoProxies = function extractMtprotoProxies() {
    const seen = {};
    const olderToNewer = [];

    Array.from(document.querySelectorAll('a[href*="/proxy?"]')).forEach(function (a) {
        let url = a.href || "";
        if (url.indexOf("t.me/proxy") !== -1) {
            url = url.replace(/https?:\/\/t\.me\/proxy\?/i, "tg://proxy?");
        }
        if (typeof url !== "string" || url.indexOf("tg://proxy?") !== 0) {
            return;
        }
        if (seen[url]) {
            return;
        }
        seen[url] = true;
        olderToNewer.push(url);
    });

    // Низ сайта (свежие) → начало массива (верх Firebase)
    return olderToNewer.slice().reverse();
};

window.MTPROTO_MIN_PROXIES = 20;

// Автозапуск только в DevTools (не когда Selenium выставил флаг).
(function () {
    if (typeof window !== "undefined" && window.__MTPROTO_EXTRACT_NO_AUTO__) {
        return;
    }
    if (typeof document === "undefined") {
        return;
    }

    console.log("%cНачинаю поиск прокси-ссылок...", "color: orange; font-weight: bold;");
    const proxyLinks = extractMtprotoProxies();
    const minCount = window.MTPROTO_MIN_PROXIES || 20;

    if (proxyLinks.length < minCount) {
        console.warn(
            "Мало прокси: " +
                proxyLinks.length +
                " < " +
                minCount +
                ". Не копирую / не пушить в Firebase. Прокрути ВВЕРХ и повтори."
        );
        return;
    }

    if (proxyLinks.length > 0) {
        const result = proxyLinks.join("\n");
        console.log(
            "%cУспех! Найдено уникальных прокси: " +
                proxyLinks.length +
                " (свежие первыми)",
            "color: green; font-weight: bold; font-size: 14px;"
        );
        console.log(result);
        try {
            if (typeof copy === "function") {
                copy(result);
                console.log(
                    "%cСписок прокси скопирован (newest-first).",
                    "color: blue; font-style: italic;"
                );
            }
        } catch (e) {
            console.log(
                "%cНе удалось скопировать. Выделите список выше и Ctrl+C.",
                "color: red;"
            );
        }
    } else {
        console.warn(
            "Прокси не найдены. Прокрутите страницу ВВЕРХ, чтобы загрузить посты."
        );
    }
})();
