# MCOptimize — Документация и сборки

Русская версия (ниже — английская):

## Описание
MCOptimize — это универсальный плагин для серверов Minecraft, включающий оптимизации, улучшения качества игры и дополнительные модули.  
Плагин поддерживает работу на Paper 1.13+ и сборку под разные версии Java.

### Включает:
- Авторизацию (`/login`, `/register`) с таймаутом и хэшированием паролей  
- NPC‑систему с рандомными моделями и командой `/npc`  
- Защиту от спама (rate‑limit, проверка дубликатов, временные муты)  
- Проверку изменения IP при входе  
- Улучшенный таб‑лист с поддержкой плейсхолдеров (`{online}`)  
- Базовый модуль косметики (заготовка для будущего расширения)  
- Оптимизации сервера (очистка сущностей/предметов, лимиты нагрузки)

---

## Особенности сборок
Репозиторий предоставляет **публичные сборки MCOptimize**, ориентированные на широкое использование:

- Поддержка нескольких Java‑таргетов  
- Возможность сборки под разные версии Minecraft  
- Универсальный jar, совместимый с Paper 1.13+  
- Чистая структура проекта без привязки к конкретному серверу  

---

## Как собрать

### Основная локальная сборка:
```bash
mvn -DskipTests clean package
```

### Сборка под конкретные Java‑таргеты:
```bash
mvn -Pmc-java8 -DskipTests clean package
mvn -Pmc-java17 -DskipTests clean package
mvn -Pmc-java21 -DskipTests clean package
```

(Профили можно удалить, если используется универсальная сборка под Java 8.)

---

## Лицензия
Проект распространяется под лицензией **GPL v3** (см. файл `LICENSE`).

---

## Contributing
Перед внесением изменений, пожалуйста, ознакомьтесь с `CONTRIBUTING.md` и `CODE_OF_CONDUCT.md`.  
Открывайте issue или pull‑request в GitHub.

---

# English version

## Description
MCOptimize is a universal Minecraft server plugin that provides optimizations, gameplay improvements, and additional modules.  
The plugin supports multi‑target builds for different Java and Minecraft versions starting from Paper 1.13+.

### Includes:
- Authentication (`/login`, `/register`) with timeout and hashed passwords  
- NPC system with randomized models and `/npc` management  
- Anti‑spam (rate limiting, duplicate checks, temporary mutes)  
- IP change detection on login  
- Tablist improvements with placeholder support (`{online}`)  
- Basic cosmetics module (placeholder for future expansion)  
- Server optimizations (entity/item cleanup and load limits)

---

## Builds
The repository provides **public MCOptimize builds** designed for wide distribution:

- Multiple Java targets  
- Multi‑MC version support  
- Universal jar compatible with Paper 1.13+  
- Clean project structure without server‑specific branding  

---

## How to build

### Default local build:
```bash
mvn -DskipTests clean package
```

### Build specific Java targets:
```bash
mvn -Pmc-java8 -DskipTests clean package
mvn -Pmc-java17 -DskipTests clean package
mvn -Pmc-java21 -DskipTests clean package
```

---

## License
Licensed under **GPL v3** (see `LICENSE`).

---

## Contributing
Please read `CONTRIBUTING.md` and `CODE_OF_CONDUCT.md` before contributing.  
Open issues or PRs on GitHub.
