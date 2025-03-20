# Task Management System

Простая система управления задачами, разработанная с использованием Java 17, Spring Boot, Spring Security и PostgreSQL. Проект предоставляет REST API для создания, редактирования, удаления и просмотра задач с поддержкой аутентификации через JWT и ролевой системы (ADMIN и USER).

## Основные возможности
- Создание, редактирование, удаление и просмотр задач.
- Поля задач: заголовок, описание, статус (`PENDING`, `IN_PROGRESS`, `COMPLETED`), приоритет (`LOW`, `MEDIUM`, `HIGH`), автор, исполнитель, комментарии.
- Аутентификация и авторизация через email и пароль с использованием JWT.
- Роли:
    - `ADMIN`: полный доступ ко всем задачам.
    - `USER`: управление своими задачами (как автор или исполнитель).
- Фильтрация задач по статусу, автору, исполнителю с пагинацией.
- Обработка ошибок с понятными сообщениями.
- Документация API через Swagger UI.

## Требования
- **Java**: 17 или выше
- **Maven**: 3.8+
- **Docker**: для запуска через Docker Compose
- **PostgreSQL**: для базы данных (локально или в Docker)

## Установка и настройка

### 1. Клонирование репозитория
Склонируйте проект с помощью Git:
```bash
git clone https://github.com/Tonkorg/ManageSystem.git
cd ManageSystem
```


### 2. Настройка переменных окружения
Проект использует файл .env для конфигурации. Создайте файл .env в корне проекта и заполните его по шаблону ниже. Вы можете использовать свои значения или оставить значения по умолчанию.

Шаблон .env

```

# PostgreSQL настройки
POSTGRES_DB=name_db
POSTGRES_USER=your_username
POSTGRES_PASSWORD=your_password

# Spring DataSource (локальный и Docker)
SPRING_DATASOURCE_URL_LOCAL=jdbc:postgresql://localhost:port/name_db
SPRING_DATASOURCE_URL_DOCKER=jdbc:postgresql://db:port/name_db
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# JWT настройки
JWT_SECRET=your_very_secure_secret_key_with_at_least_64_chars
JWT_EXPIRATION=expirationTime

# Порты
SERVER_PORT=port
DB_PORT=port

```


Пример заполненного .env:

```
POSTGRES_DB=task_management_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=securePass123
SPRING_DATASOURCE_URL_LOCAL=jdbc:postgresql://localhost:5432/task_management_db
SPRING_DATASOURCE_URL_DOCKER=jdbc:postgresql://db:5432/task_management_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=securePass123
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
JWT_SECRET=Zm9vYmFyMTIzNDU2Nzg5MGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6MTIzNDU2Nzg5MGFiYw==
JWT_EXPIRATION=86400000
SERVER_PORT=8080
DB_PORT=5432
```

### 3. Сборка проекта

Соберите проект с помощью Maven:

```bash
mvn clean install
```
## Локальный запуск
Требования:   

Установленный PostgreSQL на локальной машине.   
База данных с именем task_management_db (или другим, указанным в .env).


### Шаги
1. Запустите PostgreSQL локально: Убедитесь, что PostgreSQL работает и доступен по адресу localhost:5432. Создайте базу данных:

``` sql
CREATE DATABASE task_management_db;
```

2. Обновите application.yml (если нужно): Если вы не используете .env для локального запуска, добавьте настройки в src/main/resources/application.yml:

3. Запустите приложение:

```bash
mvn spring-boot:run
```
4. Проверка:

API доступно по адресу: http://localhost:8080/api.   
Swagger UI: http://localhost:8080/swagger-ui.html.


## Запуск через Docker Compose

1. Убедитесь, что .env настроен: Проверьте, что файл .env в корне проекта заполнен
2. Запустите контейнеры: 


```bash
docker-compose up --build
```

3. Проверка:
   API: http://localhost:8080/api (или другой порт, указанный в SERVER_PORT).  
   Swagger UI: http://localhost:8080/swagger-ui.html.


# Использование API
Основные эндпоинты  
Регистрация:

POST /api/auth/register
```json
{
"email": "user@example.com",
"password": "pass123",
"roles": ["USER"]
}
```
Вход:

POST /api/auth/login  

Тело запроса:
```json
{
"email": "user@example.com",
"password": "pass123"
}
```

Ответ:
```json

{
"token": "jwt-token-here"
}
```

Создание задачи:

POST /api/tasks    

Заголовок: Authorization: Bearer <jwt-token> Тело запроса:   
```json
{
"title": "New Task",
"description": "Task description",
"priority": "HIGH",
"assigneeId": 2
}
```

Получение задач:

GET /api/tasks?page=0&size=10&status=PENDING&authorId=1  

Заголовок: Authorization: Bearer <jwt-token>   
Получение комментариев:

GET /api/tasks/{taskId}/comments  
Заголовок: Authorization: Bearer <jwt-token>  

Полная документация доступна через Swagger UI: http://localhost:8080/swagger-ui.html.

## Тестирование
Проект включает юнит- и интеграционные тесты:

TaskServiceTest: Проверка CRUD операций и фильтрации задач.  
CommentServiceTest: Проверка операций с комментариями.   
AuthControllerTest: Проверка аутентификации и регистрации.  

Запустите тесты:

```bash 
mvn test
```

## Структура проекта

#### src/main/java/ru/test/ManageSystem:  
#### controller: Контроллеры REST API.  
#### service: Бизнес-логика. 
#### entity: Сущности JPA.
#### repository: Репозитории для работы с базой данных.
#### dto: Объекты передачи данных.
#### security: Настройки безопасности и JWT.
#### exception: Обработка ошибок.
#### src/main/resources:
#### application.yml: Базовая конфигурация Spring Boot.
#### .env: Переменные окружения для настройки.