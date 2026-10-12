@echo off
setlocal EnableExtensions

set "RAIZ=%~dp0"
if "%RAIZ:~-1%"=="\" set "RAIZ=%RAIZ:~0,-1%"
set "FRONTEND=%RAIZ%\frontend"
set "MODO=%~1"
cd /d "%RAIZ%"

echo ======================================================================
echo   Sistema de Agendamento de Transferencias Financeiras
echo ======================================================================
echo.

where java >nul 2>&1
if errorlevel 1 goto :erroJava

where npm >nul 2>&1
if errorlevel 1 goto :erroNode

call :portaAtiva 8080
if not errorlevel 1 goto :erroPorta8080

call :portaAtiva 4200
if not errorlevel 1 goto :erroPorta4200

if exist "%FRONTEND%\node_modules" goto :iniciarServicos
echo [PREPARO] Primeira execucao: instalando dependencias do frontend...
pushd "%FRONTEND%"
call npm install
if errorlevel 1 goto :erroNpmInstall
popd

:iniciarServicos
if /I "%MODO%"=="janelas" goto :abrirJanelas
where wt.exe >nul 2>&1
if errorlevel 1 goto :abrirJanelas

echo [1/2] Abrindo Windows Terminal com as guias Backend e Frontend...
start "" wt.exe -w new new-tab --title "Backend - Spring Boot + H2" -d "%RAIZ%" cmd /k mvnw.cmd spring-boot:run ; new-tab --title "Frontend - Angular" -d "%FRONTEND%" cmd /k npm start
goto :aguardarServicos

:abrirJanelas
echo [1/2] Abrindo Backend e Frontend em janelas separadas do CMD...
start "Backend - Spring Boot + H2" /D "%RAIZ%" cmd /k mvnw.cmd spring-boot:run
start "Frontend - Angular" /D "%FRONTEND%" cmd /k npm start

:aguardarServicos
echo [2/2] Aguardando os servicos responderem nas portas 8080 e 4200...
set /a TENTATIVAS=0

:verificarPortas
set /a TENTATIVAS+=1
ping -n 3 127.0.0.1 >nul
call :portaAtiva 8080
if errorlevel 1 goto :aguardarNovamente
call :portaAtiva 4200
if errorlevel 1 goto :aguardarNovamente
goto :servicosProntos

:aguardarNovamente
if %TENTATIVAS% GEQ 90 goto :tempoEsgotado
goto :verificarPortas

:servicosProntos
echo.
echo ======================================================================
echo   Backend, Frontend e banco H2 no ar!
echo.
echo   * Frontend - Interface Web:  http://localhost:4200
echo   * Backend  - API REST:       http://localhost:8080/api/transferencias
echo   * Console do banco H2:       http://localhost:8080/h2-console
echo       JDBC URL: jdbc:h2:mem:transferencias_db  /  Usuario: sa  /  Senha: vazia
echo ======================================================================
start "" http://localhost:4200
ping -n 16 127.0.0.1 >nul
exit /b 0

:tempoEsgotado
echo.
echo [AVISO] Os servicos nao responderam em 3 minutos.
echo         Verifique as guias/janelas Backend e Frontend para ver o erro.
echo         Se nenhuma guia apareceu, execute: iniciar.bat janelas
pause
exit /b 1

:erroJava
echo [ERRO] Java nao encontrado no PATH. Instale o JDK 11 ou superior.
pause
exit /b 1

:erroNode
echo [ERRO] Node.js / npm nao encontrado no PATH. Instale o Node.js 18 ou superior.
pause
exit /b 1

:erroPorta8080
echo [ERRO] A porta 8080 ja esta em uso. Feche o backend que ja esta rodando.
pause
exit /b 1

:erroPorta4200
echo [ERRO] A porta 4200 ja esta em uso. Feche o frontend que ja esta rodando.
pause
exit /b 1

:erroNpmInstall
popd
echo [ERRO] Falha ao instalar as dependencias do frontend com npm install.
pause
exit /b 1

:portaAtiva
powershell -NoProfile -Command "if (Get-NetTCPConnection -LocalPort %~1 -State Listen -ErrorAction SilentlyContinue) { exit 0 } else { exit 1 }"
exit /b %ERRORLEVEL%
