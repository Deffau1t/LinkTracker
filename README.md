![Build](https://github.com/central-university-dev/backend-academy-2025-spring-template/actions/workflows/build.yaml/badge.svg)

# Link Tracker

<!-- этот файл можно и нужно менять -->

Проект сделан в рамках курса Академия Бэкенда.

Приложение для отслеживания обновлений контента по ссылкам.
При появлении новых событий отправляется уведомление в Telegram.

Проект написан на `Java 23` с использованием `Spring Boot 3`.

Проект состоит из 2-х приложений:
* Bot - Telegram-бот, отправляющий уведомления пользователям.
* Scrapper - сервис для мониторинга обновлений на GitHub и StackOverflow.

# Scrapper & Bot Application

## 🚀 Запуск приложения

### 🔹 **1. Клонирование репозитория**
```sh
git clone https://github.com/your-repo/scrapper-bot.git
cd scrapper-bot
```

### 🔹 **2. Настройка переменных окружения**
Создайте `.env` файл и укажите в нем:
```env
TELEGRAM_TOKEN=your_telegram_bot_token
SCRAPPER_BASE_URL=http://localhost:8081
GITHUB_TOKEN=your_github_token
SO_KEY=your_stackoverflow_key
POSTGRES_DB=your_postgres_db
POSTGRES_URL=your_postgres_url
POSTGRES_USERNAME=your_postgres_username
POSTGRES_PASSWORD=your_postgres_password
```

### 🔹 **3. Сборка проекта**
```sh
mvn clean install
```

### 🔹 **5. Запуск Scrapper**
```sh
cd scrapper
mvn spring-boot:run
```

### 🔹 **6. Запуск Bot**
```sh
cd bot
mvn spring-boot:run
```

---

## 🛠 **Тестирование**

### 🔹 **Запуск тестов**
```sh
mvn test
```

### 🔹 **Проверка кода с Checkstyle**
```sh
mvn checkstyle:check
```

---

## 📖 **Документация API**
После запуска Scrapper API доступен по адресу:
```
http://localhost:8081/swagger-ui/index.html
```


