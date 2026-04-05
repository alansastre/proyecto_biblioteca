@echo off
rem --------------------------------------------------------------------------
rem ci-local.cmd -- Integracion continua en local.
rem
rem Ejecuta la misma secuencia que haria un servidor de CI:
rem   1. Verifica que el JDK esta disponible (lo instala si no)
rem   2. Compila el proyecto
rem   3. Ejecuta todos los tests
rem   4. Empaqueta la aplicacion (genera el .jar)
rem
rem Uso: ci-local.cmd
rem --------------------------------------------------------------------------

set "PROJECT_DIR=%~dp0"
if "%PROJECT_DIR:~-1%"=="\" set "PROJECT_DIR=%PROJECT_DIR:~0,-1%"

echo.
echo ################################################################
echo   INTEGRACION CONTINUA LOCAL
echo ################################################################

set "ERRORES=0"

rem -- PASO 1: JDK ---------------------------------------------------------
echo.
echo ================================================================
echo   Paso 1/4: Verificar JDK
echo ================================================================

call "%PROJECT_DIR%\setup-jdk.cmd" /nopause

where java >nul 2>&1
if errorlevel 1 goto :no_java

echo.
echo   JAVA_HOME = %JAVA_HOME%
goto :do_compile

:no_java
echo.
echo ERROR: No se pudo configurar el JDK.
echo Ejecuta setup-jdk.cmd manualmente para ver el error.
pause
exit /b 1

rem -- PASO 2: Compilar -----------------------------------------------------
:do_compile
echo.
echo ================================================================
echo   Paso 2/4: Compilar (mvn clean compile)
echo ================================================================
echo.

cd /d "%PROJECT_DIR%"
call "%PROJECT_DIR%\mvnw.cmd" clean compile -q
if errorlevel 1 goto :compile_fail
echo   Compilacion correcta.
goto :do_test

:compile_fail
echo.
echo   ** ERROR: La compilacion ha fallado. **
set /a ERRORES+=1

rem -- PASO 3: Tests --------------------------------------------------------
:do_test
echo.
echo ================================================================
echo   Paso 3/4: Ejecutar tests (mvn test)
echo ================================================================
echo.

call "%PROJECT_DIR%\mvnw.cmd" test -q
if errorlevel 1 goto :test_fail
echo   Todos los tests han pasado.
goto :do_package

:test_fail
echo.
echo   ** ERROR: Algunos tests han fallado. **
set /a ERRORES+=1

rem -- PASO 4: Empaquetar ---------------------------------------------------
:do_package
echo.
echo ================================================================
echo   Paso 4/4: Empaquetar (mvn package -DskipTests)
echo ================================================================
echo.

call "%PROJECT_DIR%\mvnw.cmd" package -DskipTests -q
if errorlevel 1 goto :package_fail
echo   Empaquetado correcto.
for %%f in ("%PROJECT_DIR%\target\*.jar") do echo   JAR generado: %%f
goto :summary

:package_fail
echo.
echo   ** ERROR: El empaquetado ha fallado. **
set /a ERRORES+=1

rem -- Resumen --------------------------------------------------------------
:summary
echo.
echo ################################################################
if %ERRORES% equ 0 goto :summary_ok
echo   RESULTADO: %ERRORES% PASOS CON ERRORES
echo.
echo   Revisa los mensajes de error de arriba.
echo   Si no sabes como solucionarlo, pregunta al profesor.
goto :summary_end

:summary_ok
echo   RESULTADO: TODO CORRECTO
echo.
echo   Tu proyecto compila, los tests pasan y el JAR se genera.
echo   Puedes arrancarlo con: mvnw.cmd spring-boot:run

:summary_end
echo ################################################################
echo.
pause
exit /b %ERRORES%
