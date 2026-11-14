@echo off
rem Abre a administracao (cadastro e apuracao). Precisa do urna.jar e do urna.propriedades nesta mesma pasta.
cd /d "%~dp0"
if exist "%JAVA_HOME%\bin\javaw.exe" (
    start "" "%JAVA_HOME%\bin\javaw.exe" -jar "%~dp0urna.jar" administracao
) else (
    start "" javaw -jar "%~dp0urna.jar" administracao
)