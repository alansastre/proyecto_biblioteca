#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# ci-local.sh  --  Integracion continua en local.
#
# Ejecuta la misma secuencia que haria un servidor de CI:
#   1. Verifica que el JDK esta disponible (lo instala si no)
#   2. Compila el proyecto
#   3. Ejecuta todos los tests
#   4. Empaqueta la aplicacion (genera el .jar)
#
# Uso: ./ci-local.sh
#
# Funciona en Linux y macOS. En Windows usa ci-local.cmd.
# ---------------------------------------------------------------------------
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PASO_ACTUAL=0
PASOS_TOTAL=4
ERRORES=0

# -- Funcion para mostrar el paso actual ------------------------------------
paso() {
    PASO_ACTUAL=$((PASO_ACTUAL + 1))
    echo ""
    echo "============================================================"
    echo "  Paso $PASO_ACTUAL/$PASOS_TOTAL: $1"
    echo "============================================================"
    echo ""
}

# -- 1. Configurar JDK -----------------------------------------------------
paso "Verificar JDK"

# Cargar setup-jdk.sh para configurar JAVA_HOME y PATH
source "$SCRIPT_DIR/setup-jdk.sh"

if ! command -v java &>/dev/null; then
    echo "ERROR: No se pudo configurar el JDK. Ejecuta setup-jdk.sh primero."
    exit 1
fi

echo ""
echo "JAVA_HOME: ${JAVA_HOME:-no definido}"
echo ""

# -- 2. Compilar ------------------------------------------------------------
paso "Compilar (mvn clean compile)"

cd "$SCRIPT_DIR"

if ./mvnw clean compile -q; then
    echo "Compilacion correcta."
else
    echo "ERROR: La compilacion ha fallado."
    ERRORES=$((ERRORES + 1))
fi

# -- 3. Tests ---------------------------------------------------------------
paso "Ejecutar tests (mvn test)"

if ./mvnw test -q; then
    echo "Todos los tests han pasado."
else
    echo "ERROR: Algunos tests han fallado."
    ERRORES=$((ERRORES + 1))
fi

# -- 4. Empaquetar ----------------------------------------------------------
paso "Empaquetar (mvn package -DskipTests)"

if ./mvnw package -DskipTests -q; then
    echo "Empaquetado correcto."

    # Mostrar el JAR generado
    JAR_FILE=$(find "$SCRIPT_DIR/target" -maxdepth 1 -name "*.jar" ! -name "*-sources.jar" | head -1)
    if [ -n "$JAR_FILE" ]; then
        JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
        echo "JAR generado: $JAR_FILE ($JAR_SIZE)"
    fi
else
    echo "ERROR: El empaquetado ha fallado."
    ERRORES=$((ERRORES + 1))
fi

# -- Resumen ----------------------------------------------------------------
echo ""
echo "============================================================"
if [ "$ERRORES" -eq 0 ]; then
    echo "  RESULTADO: TODO CORRECTO"
    echo ""
    echo "  Tu proyecto compila, los tests pasan y el JAR se genera."
    echo "  Puedes arrancarlo con: ./mvnw spring-boot:run"
else
    echo "  RESULTADO: $ERRORES PASO(S) CON ERRORES"
    echo ""
    echo "  Revisa los mensajes de error de arriba."
    echo "  Si no sabes como solucionarlo, pregunta al profesor."
fi
echo "============================================================"
echo ""

exit $ERRORES
