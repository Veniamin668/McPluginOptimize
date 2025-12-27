#!/bin/bash

echo "=== AUTO_FIX_ERRORS.SH: Автоматическое исправление ошибок сборки ==="

LOG_FILE="maven_build.log"

if [ ! -f "$LOG_FILE" ]; then
    echo "Лог сборки не найден — автофиксы пропущены."
    exit 0
fi

# === 1. Создание отсутствующих классов ===
grep -E "cannot find symbol|class .* does not exist" "$LOG_FILE" | grep "class" | while read -r line; do
    CLASS=$(echo "$line" | sed -E 's/.*class ([A-Za-z0-9_]+).*/\1/')
    PACKAGE=$(echo "$line" | sed -E 's/.*package ([a-zA-Z0-9_.]+).*/\1/' | tr '.' '/')

    if [ -z "$CLASS" ]; then continue; fi

    echo "Создаю отсутствующий класс: $CLASS ($PACKAGE)"

    mkdir -p src/main/java/$PACKAGE

    cat > src/main/java/$PACKAGE/$CLASS.java <<EOF
package $(echo "$PACKAGE" | tr '/' '.');

public class $CLASS {
    public $CLASS() {}
}
EOF
done

# === 2. Создание отсутствующих методов ===
grep "cannot find symbol" "$LOG_FILE" | grep "method" | while read -r line; do
    METHOD=$(echo "$line" | sed -E 's/.*method ([A-Za-z0-9_]+).*/\1/')
    CLASS_FILE=$(grep -B2 "$METHOD" "$LOG_FILE" | grep ".java" | head -n1 | sed -E 's/.*src\/main\/java\///')

    if [ -z "$METHOD" ] || [ -z "$CLASS_FILE" ]; then continue; fi

    FILE_PATH="src/main/java/$CLASS_FILE"

    echo "Добавляю отсутствующий метод $METHOD() в $FILE_PATH"

    echo "
    public void $METHOD() {}" >> "$FILE_PATH"
done

# === 3. Создание отсутствующих конструкторов ===
grep "constructor" "$LOG_FILE" | grep "cannot be applied" | while read -r line; do
    CLASS=$(echo "$line" | sed -E 's/.*constructor ([A-Za-z0-9_]+).*/\1/')
    FILE=$(grep -R "class $CLASS" -n src/main/java | cut -d: -f1 | head -n1)

    if [ -z "$CLASS" ] || [ -z "$FILE" ]; then continue; fi

    echo "Добавляю пустой конструктор в $FILE"

    echo "
    public $CLASS() {}" >> "$FILE"
done

echo "=== Автофиксы завершены ==="
