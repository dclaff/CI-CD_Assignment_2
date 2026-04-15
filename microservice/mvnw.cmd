@echo off
SETLOCAL ENABLEDELAYEDEXPANSION
set MAVEN_PROJECTBASEDIR=%~dp0
set WRAPPER_DIR=%MAVEN_PROJECTBASEDIR%.mvn\wrapper
set WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
set PROJECT_SETTINGS=%MAVEN_PROJECTBASEDIR%settings.xml

REM Prefer local mvn if available on PATH
where mvn >nul 2>nul
if %ERRORLEVEL%==0 (
  if exist "%PROJECT_SETTINGS%" (
    mvn -s "%PROJECT_SETTINGS%" %*
  ) else (
    mvn %*
  )
  exit /b %ERRORLEVEL%
)

REM Fall back to Maven Wrapper
if not exist "%WRAPPER_JAR%" (
  echo Downloading maven-wrapper.jar...
  if exist "%SystemRoot%\System32\WindowsPowerShell\v1.0\powershell.exe" (
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar','%WRAPPER_JAR%')"
  ) else (
    bitsadmin /transfer downloadWrapper /priority normal https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar "%WRAPPER_JAR%"
  )
)
if exist "%PROJECT_SETTINGS%" (
  java -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain -s "%PROJECT_SETTINGS%" %*
) else (
  java -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
)
