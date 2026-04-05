#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# setup-jdk.sh -- Descarga e instala JDK Eclipse Temurin 25.
#
# Ejecutar UNA sola vez al comienzo del curso. El script:
#   1. Descarga el JDK en ~/.jdks/temurin-25 (carpeta del usuario)
#   2. Configura JAVA_HOME y PATH de forma PERMANENTE (~/.bashrc o ~/.zshrc)
#
# El JDK se instala en tu carpeta de usuario (igual que IntelliJ IDEA),
# asi que no depende de donde este el proyecto. Puedes borrar, mover o
# clonar el proyecto sin perder la instalacion de Java.
#
# Uso:  ./setup-jdk.sh  o  source setup-jdk.sh
#
# Es idempotente: si ya esta instalado y configurado, no hace nada.
# ---------------------------------------------------------------------------
set -euo pipefail

JDK_VERSION=25
JDK_DIR="$HOME/.jdks/temurin-$JDK_VERSION"

get_java_major() {
    "$1" -version 2>&1 | head -1 | sed 's/.*"\([0-9]*\).*/\1/'
}

echo ""
echo "================================================================"
echo "  Instalacion de JDK Eclipse Temurin $JDK_VERSION"
echo "================================================================"
echo ""

# -- 1. Comprobar si Java 25 ya esta disponible ----------------------------
echo "[1/4] Comprobando Java..."

if command -v java &>/dev/null; then
    GLOBAL_VER=$(get_java_major java)
    if [ "$GLOBAL_VER" = "$JDK_VERSION" ]; then
        echo "      Java $JDK_VERSION ya esta disponible."
        java -version 2>&1
        export JAVA_HOME="${JAVA_HOME:-$(dirname "$(dirname "$(command -v java)")")}"
        export PATH="$JAVA_HOME/bin:$PATH"
        echo ""
        echo "================================================================"
        echo "  JDK Temurin $JDK_VERSION listo. No necesitas hacer nada mas."
        echo "================================================================"
        echo ""
        return 0 2>/dev/null || exit 0
    fi
    echo "      Version $GLOBAL_VER detectada, se necesita $JDK_VERSION."
else
    echo "      No se encontro Java en el PATH."
fi

# -- 2. Comprobar instalacion en carpeta del usuario -----------------------
echo ""
echo "[2/4] Comprobando $JDK_DIR/..."

NEED_DOWNLOAD=true
if [ -x "$JDK_DIR/bin/java" ]; then
    LOCAL_VER=$(get_java_major "$JDK_DIR/bin/java")
    if [ "$LOCAL_VER" = "$JDK_VERSION" ]; then
        echo "      JDK encontrado y funcional en carpeta del usuario."
        NEED_DOWNLOAD=false
    else
        echo "      Version incorrecta ($LOCAL_VER). Reinstalando..."
        rm -rf "$JDK_DIR"
    fi
else
    echo "      No encontrado."
fi

# -- 3. Descargar e instalar (si es necesario) -----------------------------
if [ "$NEED_DOWNLOAD" = true ]; then
    OS_RAW=$(uname -s)
    ARCH_RAW=$(uname -m)

    case "$OS_RAW" in
        Linux)  ADOPTIUM_OS="linux" ;;
        Darwin) ADOPTIUM_OS="mac" ;;
        *)
            echo "ERROR: Sistema operativo no soportado: $OS_RAW"
            echo "En Windows, usa setup-jdk.cmd"
            return 1 2>/dev/null || exit 1
            ;;
    esac

    case "$ARCH_RAW" in
        x86_64|amd64)  ADOPTIUM_ARCH="x64" ;;
        aarch64|arm64) ADOPTIUM_ARCH="aarch64" ;;
        *)
            echo "ERROR: Arquitectura no soportada: $ARCH_RAW"
            return 1 2>/dev/null || exit 1
            ;;
    esac

    DOWNLOAD_URL="https://api.adoptium.net/v3/binary/latest/$JDK_VERSION/ga/$ADOPTIUM_OS/$ADOPTIUM_ARCH/jdk/hotspot/normal/eclipse"

    echo ""
    echo "[3/4] Descargando JDK Temurin $JDK_VERSION ($ADOPTIUM_OS/$ADOPTIUM_ARCH)..."
    echo "      Destino: $JDK_DIR"
    echo ""

    mkdir -p "$HOME/.jdks"
    ARCHIVE="/tmp/temurin-$JDK_VERSION.tar.gz"

    if command -v curl &>/dev/null; then
        curl -fSL --progress-bar -o "$ARCHIVE" "$DOWNLOAD_URL"
    elif command -v wget &>/dev/null; then
        wget -q --show-progress -O "$ARCHIVE" "$DOWNLOAD_URL"
    else
        echo "ERROR: Se necesita curl o wget."
        return 1 2>/dev/null || exit 1
    fi

    echo ""
    echo "      Extrayendo..."
    TEMP_DIR=$(mktemp -d)
    tar -xzf "$ARCHIVE" -C "$TEMP_DIR"

    rm -rf "$JDK_DIR"
    EXTRACTED_DIR=$(find "$TEMP_DIR" -maxdepth 1 -type d -name "jdk-*" | head -1)

    if [ -z "$EXTRACTED_DIR" ]; then
        echo "ERROR: No se encontro jdk-* tras la extraccion."
        rm -rf "$TEMP_DIR" "$ARCHIVE"
        return 1 2>/dev/null || exit 1
    fi

    if [ "$ADOPTIUM_OS" = "mac" ] && [ -d "$EXTRACTED_DIR/Contents/Home" ]; then
        mv "$EXTRACTED_DIR/Contents/Home" "$JDK_DIR"
    else
        mv "$EXTRACTED_DIR" "$JDK_DIR"
    fi

    rm -rf "$TEMP_DIR" "$ARCHIVE"
    echo "      Extraccion completada."
else
    echo ""
    echo "[3/4] Descarga no necesaria."
fi

# -- Configurar para la sesion actual --------------------------------------
export JAVA_HOME="$JDK_DIR"
export PATH="$JDK_DIR/bin:$PATH"

# -- 4. Persistir en el perfil del shell -----------------------------------
echo ""
echo "[4/4] Guardando JAVA_HOME y PATH de forma permanente..."

PROFILE_FILE=""
if [ -n "${ZSH_VERSION:-}" ] || [ "$(basename "${SHELL:-bash}")" = "zsh" ]; then
    PROFILE_FILE="$HOME/.zshrc"
elif [ -f "$HOME/.bashrc" ]; then
    PROFILE_FILE="$HOME/.bashrc"
else
    PROFILE_FILE="$HOME/.profile"
fi

if grep -q "temurin-$JDK_VERSION" "$PROFILE_FILE" 2>/dev/null; then
    echo "      Ya estaba configurado en $PROFILE_FILE"
else
    # Eliminar entradas antiguas de Java si las hay
    if grep -q 'JAVA_HOME' "$PROFILE_FILE" 2>/dev/null; then
        # Crear backup y limpiar entradas de JAVA_HOME antiguas
        cp "$PROFILE_FILE" "$PROFILE_FILE.bak"
        grep -v 'JAVA_HOME\|# JDK.*setup-jdk' "$PROFILE_FILE" > "$PROFILE_FILE.tmp" || true
        mv "$PROFILE_FILE.tmp" "$PROFILE_FILE"
        echo "      Entradas antiguas de JAVA_HOME eliminadas."
    fi

    {
        echo ""
        echo "# JDK Eclipse Temurin $JDK_VERSION (anadido por setup-jdk.sh)"
        echo "export JAVA_HOME=\"$JDK_DIR\""
        echo "export PATH=\"\$JAVA_HOME/bin:\$PATH\""
    } >> "$PROFILE_FILE"
    echo "      Guardado en $PROFILE_FILE"
fi

echo ""
java -version 2>&1

echo ""
echo "================================================================"
echo "  JDK Temurin $JDK_VERSION listo"
echo "================================================================"
echo ""
echo "  JAVA_HOME = $JDK_DIR"
echo ""
echo "  La configuracion es PERMANENTE. No necesitas ejecutar"
echo "  este script de nuevo. Cualquier terminal nuevo que abras"
echo "  tendra Java $JDK_VERSION disponible."
echo ""
echo "  El JDK esta en tu carpeta de usuario, no dentro del proyecto."
echo "  Puedes borrar, mover o clonar el proyecto sin perder Java."
echo ""
