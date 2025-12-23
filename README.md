# VenikOptimize / VenikCraft — Документация и сборки

Русская версия (ниже — английская):

Описание
--------
Этот репозиторий содержит адаптированную версию плагина с дополнительными модулями и возможностью сборки под разные версии Java и Minecraft.

Включает:
- Авторизацию (/login, /register) с таймаутом и хранящимися хэшами паролей
- NPC система с рандомными моделями и командой `/npc`
- Защита от спама (rate limit, проверка дубликатов, временные муты)
- Проверка изменения IP при входе
- Улучшения таб-листа и поддержка плейсхолдеров (`{online}`)
- Базовый модуль косметики (placeholder для дальнейшего развития)
- Оптимизации (очистка сущностей/предметов и лимиты)

Особенности сборок
------------------
  - `MCOptimize` — публичные сборки для публикации (Modrinth/CurseForge/GitHub). Поддержка нескольких Java‑таргетов и профилей сборки для разных версий Minecraft.

Как собрать
-----------
- Локальная сборка (основной):
  mvn -DskipTests clean package
- Сборка конкретных профилей:
  mvn -Pmc-java8 -DskipTests clean package    # MCOptimize (Java8 target)
  mvn -Pmc-java17 -DskipTests clean package   # MCOptimize (Java17 target)
  mvn -Pmc-java21 -DskipTests clean package   # MCOptimize (Java21 target)
  
Лицензия: GPL v3 (см. `LICENSE`).

Contributing
------------
Пожалуйста, прочитайте `CONTRIBUTING.md` и `CODE_OF_CONDUCT.md` перед внесением изменений. Открывайте issue/PR в GitHub.

---

English version
---------------

Description
-----------
This repository contains an adapted plugin build with additional modules and multi-target build support for different Java and Minecraft versions.

Includes:
- Authentication (/login, /register) with timeout and hashed passwords
- NPC system with randomized models and `/npc` management
- Anti-spam (rate limiting, duplicate checks, temporary mutes)
- IP change detection on login
- Tablist improvements and placeholder support (`{online}`)
- Basic cosmetics module (placeholder)
- Optimizations (entity/item cleanup and limits)

Builds
------
- Two artifact families:
  - `MCOptimize` — public builds for distribution. Multi-Java and multi-MC profiles available.
- Use the provided `build-venikcraft-all.ps1` script to produce VenikCraft variants in `dist\VenikCraft`.

How to build
------------
- Default local build:
  mvn -DskipTests clean package
- Build specific profiles:
  mvn -Pmc-java8 -DskipTests clean package
  mvn -Pmc-java17 -DskipTests clean package
  mvn -Pmc-java21 -DskipTests clean package

License: GPL v3 (see `LICENSE`).

Contributing
------------
Please read `CONTRIBUTING.md` and `CODE_OF_CONDUCT.md` before contributing. Open issues or PRs on GitHub.
