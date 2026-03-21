import requests
from bs4 import BeautifulSoup
import re

def fetch_proxies():
    url = "https://t.me/s/ProxyMTProto"
    # Эмулируем браузер, чтобы Telegram не заблокировал запрос
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
    }
    
    print(f"Загрузка страницы {url}...")
    try:
        response = requests.get(url, headers=headers, timeout=10)
        if response.status_code != 200:
            print(f"Ошибка при загрузке страницы: {response.status_code}")
            return
    except Exception as e:
        print(f"Произошла ошибка при запросе: {e}")
        return

    soup = BeautifulSoup(response.text, 'html.parser')
    
    # В Telegram Web Preview ссылки часто находятся внутри <a> или в тексте сообщений
    # Мы соберем все ссылки из href и попробуем найти ссылки в текстовых блоках
    proxies = set()
    
    # 1. Ищем в атрибутах href
    for a in soup.find_all('a', href=True):
        href = a['href']
        if href.startswith('tg://proxy?'):
            proxies.add(href)
            
    # 2. Ищем в текстовом содержимом (на случай, если ссылка просто текстом)
    text_proxies = re.findall(r'tg://proxy\?server=[^&\s]+&port=\d+&secret=[^&\s]+', response.text)
    for p in text_proxies:
        proxies.add(p)
    
    if not proxies:
        print("Прокси не найдены. Возможно, структура страницы изменилась или Telegram блокирует запрос.")
        return

    print(f"Найдено уникальных прокси: {len(proxies)}")
    
    # Выводим в консоль
    for p in sorted(proxies):
        print(f"- {p}")

    # Сохраняем в файл
    with open('proxies.txt', 'w', encoding='utf-8') as f:
        for p in sorted(proxies):
            f.write(p + '\n')
            
    print(f"\nВсе прокси сохранены в файл: proxies.txt")

if __name__ == "__main__":
    fetch_proxies()
