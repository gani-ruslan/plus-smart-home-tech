# Smart Home Telemetry System

Проект системы сбора и обработки телеметрии для "Умного дома".
На текущем этапе реализован сервис **Collector**, отвечающий за прием событий от датчиков и хабов, их валидацию, преобразование в формат Avro и отправку в Kafka.

## Структура проекта

Проект разделен на модули:

*   **`telemetry/collector`** — Spring Boot приложение. Входная точка для событий. Предоставляет REST API для устройств.
*   **`telemetry/serialization/avro-schemas`** — Модуль, содержащий IDL-схемы Avro (`.avdl`) для генерации Java-классов событий.
*   **`commerce`** — (В разработке) Модуль электронной коммерции.
*   **`infra`** — Инфраструктурные конфигурации.

## Функциональность Collector Service

Сервис `collector` выполняет следующие задачи:

1.  **Прием событий через REST API:**
    *   Поддерживает полиморфные JSON-структуры событий.
    *   Валидирует входящие данные (Hibernate Validator / Jakarta Validation).
2.  **Маппинг данных:**
    *   Преобразует DTO объекты в Avro-формат с помощью мапперов (`HubEventMapper`, `SensorEventMapper`).
3.  **Интеграция с Kafka:**
    *   Отправляет сериализованные события в топики Kafka для дальнейшей обработки.

## API Эндпоинты

Сервис слушает события по адресу `/events`:

### 1. События от датчиков (Sensors)
**POST** `/events/sensors`

Принимает метрики окружающей среды. Тип события определяется полем `type`.

**Поддерживаемые типы (`SensorEvent`):**
*   `LIGHT_SENSOR_EVENT` — Освещенность, качество связи.
*   `MOTION_SENSOR_EVENT` — Движение, напряжение.
*   `CLIMATE_SENSOR_EVENT` — Температура, влажность, CO2.
*   `SWITCH_SENSOR_EVENT` — Состояние переключателя (вкл/выкл).
*   `TEMPERATURE_SENSOR_EVENT` — Температура (C/F).

### 2. События от хабов (Hubs)
**POST** `/events/hubs`

Принимает административные события системы.

**Поддерживаемые типы (`HubEvent`):**
*   `DEVICE_ADDED` — Регистрация нового устройства.
*   `DEVICE_REMOVED` — Удаление устройства.
*   `SCENARIO_ADDED` — Создание сценария автоматизации (условия + действия).
*   `SCENARIO_REMOVED` — Удаление сценария.

## Стек технологий

*   **Java SDK:** 23
*   **Framework:** Spring Boot 3.x
*   **Build Tool:** Maven
*   **Message Broker:** Apache Kafka
*   **Serialization:** Apache Avro
*   **JSON Processing:** Jackson (с поддержкой полиморфизма и Java Time)
*   **Utils:** Lombok