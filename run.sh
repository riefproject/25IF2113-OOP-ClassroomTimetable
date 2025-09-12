#!/bin/bash
echo "[1/2] Mengkompilasi App.java dengan library..."
javac -cp "JadwalLib.jar" tApp.java

echo "[2/2] Menjalankan App..."
echo "----------------------------------------------------"
clear
java -cp ".:JadwalLib.jar" TestApp
echo "----------------------------------------------------"