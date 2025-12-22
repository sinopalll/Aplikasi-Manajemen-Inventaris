@echo off
title Aplikasi Inventaris Launcher
cls

REM Pindah ke direktori dimana file ini berada (Fix masalah path)
cd /d "%~dp0"

echo Sedang menjalankan Aplikasi Inventaris...
echo Mohon jangan tutup jendela ini.
echo.

REM Jalankan aplikasi
java -jar AplikasiInventaris.jar

REM Jika aplikasi ditutup atau crash, jendela tidak langsung hilang
pause