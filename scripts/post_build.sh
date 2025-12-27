#!/bin/bash

echo "=== POST_BUILD.SH: Пост-обработка сборки ==="

# Создаём папку out
mkdir -p out

# Копируем JAR
if ls target/*.jar 1> /dev/null 2>&1; then
    cp target/*.jar out/
    echo "✔ JAR файлы скопированы в out/"
else
    echo "⚠ JAR файлы не найдены в target/"
fi

# Генерируем CHANGELOG
echo "Генерирую CHANGELOG.md..."
echo "## Последние изменения" > CHANGELOG.md
git log --pretty=format:"- %s" -10 >> CHANGELOG.md
echo "" >> CHANGELOG.md

# Настройка git
git config --global user.name "github-actions"
git config --global user.email "actions@github.com"

# Коммитим изменения, если есть
if ! git diff --quiet; then
    echo "Есть изменения — коммичу..."
    git add .
    git commit -m "Auto-update: обновление после сборки"
else
    echo "Нет изменений для коммита."
fi

# Пушим изменения
echo "Пробую пушить изменения..."
git push https://x-access-token:${GITHUB_TOKEN}@github.com/${GITHUB_REPOSITORY} HEAD:main || echo "⚠ Не удалось выполнить push"

echo "=== Пост-обработка завершена ==="
