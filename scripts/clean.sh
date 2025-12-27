#!/bin/bash

echo "=== CLEAN.SH: ОЧИСТКА СТАРЫХ ИСХОДНИКОВ ==="

# Удаляем старые Java-файлы
if [ -d src/main/java ]; then
    echo "Удаляю src/main/java/*"
    rm -rf src/main/java/*
else
    echo "Папка src/main/java не найдена — создаю"
    mkdir -p src/main/java
fi

# Удаляем старые ресурсы
if [ -d src/main/resources ]; then
    echo "Удаляю src/main/resources/*"
    rm -rf src/main/resources/*
else
    echo "Папка src/main/resources не найдена — создаю"
    mkdir -p src/main/resources
fi

# Удаляем мусор
echo "Удаляю временные файлы..."
rm -rf target
rm -rf out
rm -rf build
rm -rf *.iml
rm -rf .idea

echo "Очистка завершена."
