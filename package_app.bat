@echo off
echo Packaging Facturo...

:: Determine version
set VERSION=1.0-SNAPSHOT

:: Check if JAVA_HOME is set
if "%JAVA_HOME%"=="" (
    echo Error: JAVA_HOME is not set.
    exit /b 1
)

:: Check for jpackage
set JPACKAGE="%JAVA_HOME%\bin\jpackage.exe"
if not exist %JPACKAGE% (
    echo Error: jpackage not found at %JPACKAGE%
    echo Please ensure you are using JDK 14 or later.
    exit /b 1
)

echo Building project...
call mvn clean package dependency:copy-dependencies
if %ERRORLEVEL% NEQ 0 (
    echo Build failed.
    exit /b 1
)

echo.
echo Creating input directory for jpackage...
if not exist target\jpackage-input mkdir target\jpackage-input
copy target\facturo-%VERSION%.jar target\jpackage-input\
xcopy /E /Y target\dependency target\jpackage-input\

echo.
echo Running jpackage...
%JPACKAGE% ^
  --type app-image ^
  --input target/jpackage-input ^
  --main-jar facturo-%VERSION%.jar ^
  --main-class com.facturo.App ^
  --name Facturo ^
  --dest dist ^
  --win-console ^
  --win-shortcut ^
  --win-menu ^
  --java-options "--module-path target/dependency" ^
  --module-path target/dependency

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Packaging successful!
    echo Executable located in dist\Facturo
) else (
    echo.
    echo Packaging failed.
)
pause
