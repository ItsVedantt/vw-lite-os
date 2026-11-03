@echo off
set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.21.21-hotspot"
set "ANDROID_HOME=C:\Users\Kishor\AppData\Local\Android\Sdk"
set "PATH=%JAVA_HOME%\bin;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\build-tools\28.0.3;C:\Gradle\gradle-8.4\bin;%PATH%"
cd /d "d:\tvLauncherrr"
call gradlew.bat assembleRelease assembleDebug
