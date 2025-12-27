# Gwent Deck Service - REST API

Spring Boot REST API сервіс для управління колодами та картами з гри Gwent.



### Сутності

**Deck (Колода)** - основна сутність
- id (PK)
- name (унікальне)
- faction (фракція: Monsters, Northern Realms тощо)
- leaderAbility (здібність лідера)
- provisionLimit (ліміт провізії: 100-200)
- categories (категорії колоди)
- description (опис)
- timestamps (created_at, updated_at)

**Card (Карта)** - пов'язана Many-to-One з Deck
- id (PK)
- name
- deck_id (FK -> decks)
- provision (вартість: 0-20)
- power (сила: 0-50)
- type (тип: Unit, Special, Artifact, Stratagem)
- faction (фракція карти)
- description
- timestamps (created_at, updated_at)

##  Швидкий старт


### 1. Клонування репозиторію
```bash
git clone https://github.com/duskmage2009/gwent-service.git
cd gwent-service
```

### 2. Налаштування PostgreSQL

Створіть базу даних:
```sql
CREATE DATABASE gwent_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8';
```

### 3. Конфігурація додатку

Відредагуйте `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gwent_db
spring.datasource.username=postgres
spring.datasource.password=ваш_пароль
```

### 4. Запуск додатку
```bash
mvn spring-boot:run
```

Додаток запуститься на `http://localhost:8080`

Liquibase автоматично створить таблиці та додасть тестові дані.

### 5. Перевірка роботи
```bash
# Отримати список всіх колод
curl http://localhost:8080/api/decks

# Отримати колоду по ID
curl http://localhost:8080/api/decks/1
```

##  API Endpoints

### Deck (Колоди)

| Метод | Endpoint | Опис |
|-------|----------|------|
| GET | `/api/decks` | Отримати список усіх колод |
| GET | `/api/decks/{id}` | Отримати колоду по ID |
| POST | `/api/decks` | Створити нову колоду |
| PUT | `/api/decks/{id}` | Оновити колоду |
| DELETE | `/api/decks/{id}` | Видалити колоду |

### Card (Карти)

| Метод | Endpoint | Опис |
|-------|----------|------|
| GET | `/api/cards/{id}` | Отримати карту по ID (з даними колоди) |
| POST | `/api/cards` | Створити нову карту |
| PUT | `/api/cards/{id}` | Оновити карту |
| DELETE | `/api/cards/{id}` | Видалити карту |
| POST | `/api/cards/_list` | Отримати список з пагінацією та фільтрацією |
| POST | `/api/cards/_report` | Згенерувати Excel звіт |
| POST | `/api/cards/upload` | Імпортувати карти з JSON файлу |

##  Приклади використання

### Створити колоду
```bash
curl -X POST http://localhost:8080/api/decks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Моя Власна Колода",
    "faction": "Monsters",
    "leaderAbility": "Overwhelming Hunger",
    "provisionLimit": 165,
    "categories": "Контроль, Поглинання",
    "description": "Тестова колода"
  }'
```

**Відповідь:**
```json
{
  "id": 8,
  "name": "Моя Власна Колода",
  "faction": "Monsters",
  "leaderAbility": "Overwhelming Hunger",
  "provisionLimit": 165,
  "categories": "Контроль, Поглинання",
  "description": "Тестова колода",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### Створити карту
```bash
curl -X POST http://localhost:8080/api/cards \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ghoul",
    "deckId": 1,
    "provision": 5,
    "power": 3,
    "type": "Unit",
    "faction": "Monsters",
    "description": "Deploy: Поглинути юніта"
  }'
```

**Відповідь:**
```json
{
  "id": 1,
  "name": "Ghoul",
  "deck": {
    "id": 1,
    "name": "Monster Swarm",
    "faction": "Monsters",
    "leaderAbility": "Overwhelming Hunger",
    "provisionLimit": 165
  },
  "provision": 5,
  "power": 3,
  "type": "Unit",
  "faction": "Monsters",
  "description": "Deploy: Поглинути юніта",
  "createdAt": "2024-01-15T10:35:00",
  "updatedAt": "2024-01-15T10:35:00"
}
```

### Отримати список карт з фільтрацією
```bash
curl -X POST http://localhost:8080/api/cards/_list \
  -H "Content-Type: application/json" \
  -d '{
    "deckId": 1,
    "type": "Unit",
    "minPower": 4,
    "page": 1,
    "size": 10
  }'
```

**Відповідь:**
```json
{
  "list": [
    {
      "id": 1,
      "name": "Nekker",
      "deckName": "Monster Swarm",
      "provision": 4,
      "power": 4,
      "type": "Unit",
      "faction": "Monsters"
    },
    {
      "id": 2,
      "name": "Werewolf",
      "deckName": "Monster Swarm",
      "provision": 6,
      "power": 5,
      "type": "Unit",
      "faction": "Monsters"
    }
  ],
  "totalPages": 1,
  "totalElements": 2,
  "currentPage": 1,
  "pageSize": 10
}
```

### Згенерувати Excel звіт
```bash
curl -X POST "http://localhost:8080/api/cards/_report?deckId=1&type=Unit" \
  -o звіт_карт.xlsx
```

Завантажить файл `звіт_карт.xlsx` з відфільтрованими картами.

### Імпортувати карти з JSON
```bash
curl -X POST http://localhost:8080/api/cards/upload \
  -F "file=@cards-import.json"
```

**Формат JSON файлу:**
```json
[
  {
    "name": "Ghoul",
    "deckName": "Monster Swarm",
    "provision": 5,
    "power": 3,
    "type": "Unit",
    "faction": "Monsters",
    "description": "Deploy: Поглинути юніта"
  }
]
```

**Відповідь:**
```json
{
  "successCount": 15,
  "failureCount": 0,
  "errors": []
}
```

При помилках:
```json
{
  "successCount": 14,
  "failureCount": 1,
  "errors": [
    "Row 5 (Invalid Card): Deck not found: Неіснуюча Колода"
  ]
}
```

##  Валідація

### Deck
- `name`: 3-200 символів, унікальне
- `faction`: обов'язкове (Monsters, Northern Realms, Nilfgaard, Scoia'tael, Skellige, Syndicate, Neutral)
- `leaderAbility`: 3-200 символів
- `provisionLimit`: 100-200

### Card
- `name`: 2-200 символів
- `deckId`: обов'язкове, колода має існувати
- `provision`: 0-20
- `power`: 0-50
- `type`: обов'язкове (Unit, Special, Artifact, Stratagem)
- `faction`: обов'язкове

### Приклади помилок валідації
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/cards",
  "validationErrors": {
    "name": "Card name is required",
    "provision": "Provision must be at least 0",
    "power": "Power cannot be negative"
  }
}
```

##  Тестування

### Запуск всіх тестів
```bash
mvn test
```

### Запуск тільки інтеграційних тестів
```bash
mvn test -Dtest=*IntegrationTest
```

### Тестове покриття

Усі endpoints покриті інтеграційними тестами:
-  CRUD операції для Deck та Card
-  Валідація некоректних даних
-  Обробка помилок (404, 409, 400)
-  Пагінація та фільтрація
-  Генерація Excel звітів
-  Імпорт даних з JSON
-  Перевірка унікальності імен колод

Тести використовують **TestContainers** з реальною PostgreSQL в Docker.

##  База даних

### Схема
```sql
decks
├── id (PK)
├── name (UNIQUE)
├── faction
├── leader_ability
├── provision_limit
├── categories
├── description
├── created_at
└── updated_at

cards
├── id (PK)
├── name
├── deck_id (FK -> decks.id)
├── provision
├── power
├── type
├── faction
├── description
├── created_at
└── updated_at
```

### Індекси для оптимізації

- `idx_deck_name` - пошук колод за іменем
- `idx_deck_faction` - фільтрація за фракцією
- `idx_card_name` - пошук карт за іменем
- `idx_card_deck` - пошук карт за колодою
- `idx_card_type` - фільтрація за типом
- `idx_card_faction` - фільтрація за фракцією

### Тестові дані

При запуску додатку Liquibase створює 7 колод:
1. Monster Swarm (Monsters)
2. Northern Tactics (Northern Realms)
3. Nilfgaard Control (Nilfgaard)
4. Scoia'tael Harmony (Scoia'tael)
5. Skellige Warriors (Skellige)
6. Syndicate Coins (Syndicate)
7. Neutral Mix (Neutral)

Ви можете імпортувати тестові карти з файлу `src/main/resources/sample-data/cards-import.json`.



##  Конфігурація

### application.properties
```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/gwent_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# Liquibase
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```


