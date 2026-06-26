@echo off
chcp 65001 >nul
title EasySchool Backend

echo ============================================
echo   EasySchool Backend - Setup Automatico
echo ============================================
echo.

REM --- Verificar Docker ---
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Docker nao esta rodando.
    echo        Abra o Docker Desktop e tente novamente.
    pause
    exit /b 1
)

REM --- Subir PostgreSQL ---
echo [1/3] Subindo banco de dados PostgreSQL...
docker compose up -d
if %errorlevel% neq 0 (
    echo [ERRO] Falha ao subir o PostgreSQL.
    pause
    exit /b 1
)
echo       OK
echo.

REM --- Copiar application.properties se nao existir ---
if not exist "src\main\resources\application.properties" (
    echo [2/3] Criando arquivo de configuracao...
    copy "src\main\resources\application.properties-example" "src\main\resources\application.properties" >nul
    echo       OK
) else (
    echo [2/3] Arquivo de configuracao ja existe. OK
)
echo.

REM --- Detectar JAVA_HOME ---
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" goto java_ok
)

echo       JAVA_HOME nao encontrado, procurando Java...
for /d %%d in ("%USERPROFILE%\.jdks\*") do (
    if exist "%%d\bin\java.exe" (
        set "JAVA_HOME=%%d"
        goto java_ok
    )
)
for /d %%d in ("%ProgramFiles%\Java\*") do (
    if exist "%%d\bin\java.exe" (
        set "JAVA_HOME=%%d"
        goto java_ok
    )
)
for /d %%d in ("%ProgramFiles%\Eclipse Adoptium\*") do (
    if exist "%%d\bin\java.exe" (
        set "JAVA_HOME=%%d"
        goto java_ok
    )
)

echo [ERRO] Java (JDK 17+) nao encontrado.
echo        Instale o JDK 17 e tente novamente.
pause
exit /b 1

:java_ok
echo [3/3] Iniciando aplicacao (JAVA_HOME=%JAVA_HOME%)...
echo.
echo       Acesse: http://localhost:8080
echo       Pressione Ctrl+C para parar
echo ============================================
echo.

call mvnw.cmd spring-boot:run

pause
