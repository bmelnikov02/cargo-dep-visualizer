@echo off
chcp 65001 >nul
javac -encoding UTF-8 -d out src\*.java || exit /b 1
java -cp out Main %*