# 🔗 Link Tracker

Приложение для отслеживания обновлений контента по ссылкам с уведомлениями в Telegram.

## ⚙️ Технологии
- Java 23
- Spring Boot 3
- Apache Kafka
- Redis
- PostgreSQL
- Docker Compose
- Testcontainers
- Swagger UI

## 🚀 Запуск приложения

### 1️⃣ Клонирование репозитория
```sh
git clone https://github.com/your-repo/scrapper-bot.git
cd scrapper-bot
```
### 2️⃣ Настройка переменных окружения
Создайте .env файл:
```
TELEGRAM_TOKEN=your_telegram_bot_token
SCRAPPER_BASE_URL=http://localhost:8081
GITHUB_TOKEN=your_github_token
SO_KEY=your_stackoverflow_key
POSTGRES_DB=scrapper
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092
REDIS_HOST=localhost
REDIS_PORT=6379 
```
### 3️⃣ Сборка проекта
```sh
mvn clean install
```

### 4️⃣ Запуск Docker Compose
```sh
docker-compose up -d
```
Контейнеры:
```
scrapper-db — PostgreSQL
kafka — Kafka Broker
zookeeper — Zookeeper для Kafka
redis — Кэш для бота
```

### 5️⃣ Запуск приложений
В отдельных терминалах:
```sh
cd scrapper
mvn spring-boot:run
```
```sh
cd bot
mvn spring-boot:run
```
### 🛠 Тестирование
Запуск тестов
```sh
mvn test
```
Проверка кода
```sh
mvn checkstyle:check
```

### 📖 Документация API
После запуска доступно по адресу:
http://localhost:8081/swagger-ui/index.html
