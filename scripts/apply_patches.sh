#!/bin/bash

echo "=== APPLY_PATCHES.SH: Применение патчей из папки fixes ==="

# Проверяем, существует ли папка fixes
if [ ! -d fixes ]; then
    echo "Папка fixes не найдена — пропускаю применение патчей."
    exit 0
fi

# Применяем каждый патч
for patch in fixes/*.patch; do
    # Если нет файлов — пропускаем
    [ -e "$patch" ] || continue

    echo "Применяю патч: $patch"

    # Пробуем применить патч
    git apply "$patch"

    # Проверяем статус
    if [ $? -ne 0 ]; then
        echo "⚠️  Не удалось применить патч: $patch"
    else
        echo "✔ Патч успешно применён: $patch"
    fi
done

echo "=== Применение патчей завершено ==="
