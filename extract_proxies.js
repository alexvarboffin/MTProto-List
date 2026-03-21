/**
 * Скрипт для извлечения MTProto-прокси из веб-версии Telegram (t.me/s/ProxyMTProto)
 * Инструкция:
 * 1. Открой https://t.me/s/ProxyMTProto в браузере.
 * 2. Прокрути страницу вверх, чтобы подгрузить больше постов.
 * 3. Открой консоль разработчика (F12 -> Console).
 * 4. Скопируй и вставь этот код.
 */

(function() {
    console.log("%cНачинаю поиск прокси-ссылок...", "color: orange; font-weight: bold;");

    // Ищем все ссылки, содержащие "/proxy?" (подходит для t.me/proxy и tg://proxy)
    const proxyLinks = Array.from(document.querySelectorAll('a[href*="/proxy?"]'))
        .map(a => {
            let url = a.href;
            // Преобразуем https://t.me/proxy?... в tg://proxy?...
            if (url.includes('t.me/proxy')) {
                url = url.replace(/https?:\/\/t\.me\/proxy\?/, 'tg://proxy?');
            }
            return url;
        })
        // Оставляем только уникальные ссылки
        .filter((value, index, self) => self.indexOf(value) === index);

    if (proxyLinks.length > 0) {
        const result = proxyLinks.join('\n');
        
        console.log(`%cУспех! Найдено уникальных прокси: ${proxyLinks.length}`, "color: green; font-weight: bold; font-size: 14px;");
        console.log(result);
        
        // Попытка скопировать в буфер обмена (работает в большинстве современных браузеров в консоли)
        try {
            copy(result);
            console.log("%cСписок прокси автоматически скопирован в буфер обмена!", "color: blue; font-style: italic;");
        } catch (e) {
            console.log("%cНе удалось автоматически скопировать. Выделите список выше и нажмите Ctrl+C.", "color: red;");
        }
    } else {
        console.warn("Прокси-ссылки не найдены. Попробуйте прокрутить страницу, чтобы загрузить посты.");
    }
})();
