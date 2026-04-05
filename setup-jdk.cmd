@echo off
rem --------------------------------------------------------------------------
rem setup-jdk.cmd -- Descarga e instala JDK Eclipse Temurin 25.
rem
rem Ejecutar UNA sola vez al comienzo del curso. El script:
rem   1. Descarga el JDK en %USERPROFILE%\.jdks\temurin-25  (carpeta del usuario)
rem   2. Limpia cualquier JDK antiguo del PATH y JAVA_HOME
rem   3. Configura JAVA_HOME y PATH de forma PERMANENTE
rem
rem El JDK se instala en tu carpeta de usuario (igual que IntelliJ IDEA),
rem asi que no depende de donde este el proyecto. Puedes borrar, mover o
rem clonar el proyecto sin perder la instalacion de Java.
rem
rem Es idempotente: si ya esta instalado y configurado, no hace nada.
rem --------------------------------------------------------------------------

echo.
echo ================================================================
echo   Instalacion de JDK Eclipse Temurin 25
echo ================================================================
echo.

set "NOPAUSE="
if "%~1"=="/nopause" set "NOPAUSE=1"

set "JDK_VERSION=25"
set "JDK_DIR=%USERPROFILE%\.jdks\temurin-25"

rem -- PASO 1: Comprobar si Java 25 ya esta en el PATH --------------------
echo [1/4] Comprobando Java...

where java >nul 2>&1
if errorlevel 1 goto :check_local

set "GVER="
for /f "tokens=3" %%a in ('java -version 2^>^&1 ^| findstr /i /c:"openjdk version"') do set "GVER=%%~a"
if not defined GVER for /f "tokens=3" %%a in ('java -version 2^>^&1 ^| findstr /i /c:"java version"') do set "GVER=%%~a"
if not defined GVER goto :check_local

set "GMAJOR="
for /f "delims=." %%m in ("%GVER%") do set "GMAJOR=%%m"

if "%GMAJOR%"=="%JDK_VERSION%" goto :already_ok
echo       Java %GMAJOR% encontrada en el PATH, se necesita %JDK_VERSION%.

rem -- PASO 2: Comprobar instalacion en carpeta del usuario ----------------
:check_local
echo.
echo [2/4] Comprobando %JDK_DIR%\...

if not exist "%JDK_DIR%\bin\java.exe" goto :need_download

"%JDK_DIR%\bin\java.exe" -version >nul 2>&1
if errorlevel 1 goto :need_download

echo       JDK encontrado y funcional en carpeta del usuario.
goto :configure

:need_download
echo       No encontrado.

rem -- PASO 3: Descargar e instalar ----------------------------------------
echo.
echo [3/4] Descargando JDK Eclipse Temurin %JDK_VERSION%...

set "ADOPTIUM_ARCH=x64"
if "%PROCESSOR_ARCHITECTURE%"=="ARM64" set "ADOPTIUM_ARCH=aarch64"

set "DL_URL=https://api.adoptium.net/v3/binary/latest/%JDK_VERSION%/ga/windows/%ADOPTIUM_ARCH%/jdk/hotspot/normal/eclipse"
set "ARCHIVE=%TEMP%\temurin-%JDK_VERSION%.zip"

echo       Destino: %JDK_DIR%
echo       Arquitectura: windows/%ADOPTIUM_ARCH%
echo.

if not exist "%USERPROFILE%\.jdks" mkdir "%USERPROFILE%\.jdks"

echo       Descargando (puede tardar 1-2 minutos)...
echo.

where curl.exe >nul 2>&1
if errorlevel 1 goto :dl_powershell

curl.exe -fSL --progress-bar -o "%ARCHIVE%" "%DL_URL%"
if exist "%ARCHIVE%" goto :dl_verify

:dl_powershell
powershell -NoProfile -Command "try { [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; $ProgressPreference='SilentlyContinue'; Invoke-WebRequest -Uri '%DL_URL%' -OutFile '%ARCHIVE%' } catch { Write-Host ('ERROR: ' + $_.Exception.Message); exit 1 }"

:dl_verify
if not exist "%ARCHIVE%" goto :err_download
for %%f in ("%ARCHIVE%") do set "FSIZE=%%~zf"
echo.
echo       Descargado: %FSIZE% bytes
if %FSIZE% LSS 1000000 goto :err_small

echo       Extrayendo...
if exist "%JDK_DIR%" rmdir /s /q "%JDK_DIR%" 2>nul

powershell -NoProfile -Command "try { $tmp=Join-Path $env:TEMP ('jdk_extract_'+[guid]::NewGuid().ToString('N').Substring(0,8)); Expand-Archive -Path '%ARCHIVE%' -DestinationPath $tmp -Force; $jdk=Get-ChildItem $tmp -Directory | Where-Object { $_.Name -like 'jdk-*' } | Select-Object -First 1; if(-not $jdk){throw 'No se encontro jdk-*'}; Move-Item $jdk.FullName '%JDK_DIR%'; Remove-Item $tmp -Recurse -Force -ErrorAction SilentlyContinue; Write-Host '       OK' } catch { Write-Host ('ERROR: ' + $_.Exception.Message); exit 1 }"
if errorlevel 1 goto :err_extract
del "%ARCHIVE%" 2>nul

rem -- PASO 4: Configurar entorno permanentemente --------------------------
:configure
echo.
echo [4/4] Configurando JAVA_HOME y PATH permanentemente...
echo       Limpiando JDKs antiguos del sistema...
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0_setup-jdk-env.ps1" -JdkDir "%JDK_DIR%"
set "NEEDS_ADMIN=%errorlevel%"

rem Configurar la sesion actual
set "JAVA_HOME=%JDK_DIR%"
set "PATH=%JDK_DIR%\bin;%PATH%"

echo.
"%JDK_DIR%\bin\java.exe" -version 2>&1

rem -- Exito ----------------------------------------------------------------
:already_ok
echo.
echo ================================================================
echo   JDK Temurin %JDK_VERSION% listo
echo ================================================================
echo.
echo   JAVA_HOME = %JDK_DIR%
echo.
if "%NEEDS_ADMIN%"=="1" goto :msg_admin
echo   La configuracion es PERMANENTE. No necesitas ejecutar
echo   este script de nuevo. Cualquier terminal nuevo que abras
echo   tendra Java %JDK_VERSION% disponible.
echo.
echo   El JDK esta en tu carpeta de usuario, no dentro del proyecto.
echo   Puedes borrar, mover o clonar el proyecto sin perder Java.
echo.
goto :msg_end

:msg_admin
echo   IMPORTANTE: Quedan JDKs antiguos en el PATH del sistema.
echo   Haz clic derecho en setup-jdk.cmd y selecciona
echo   "Ejecutar como administrador" para limpiarlos.
echo.

:msg_end
if not defined NOPAUSE pause
goto :eof

rem -- Errores --------------------------------------------------------------
:err_download
echo   ERROR: No se pudo descargar el JDK. Comprueba tu conexion.
goto :err_end
:err_small
echo   ERROR: Archivo descargado demasiado pequeno (%FSIZE% bytes).
del "%ARCHIVE%" 2>nul
goto :err_end
:err_extract
echo   ERROR: No se pudo extraer el JDK.
:err_end
echo   Si no sabes como solucionarlo, pregunta al profesor.
if not defined NOPAUSE pause
exit /b 1
