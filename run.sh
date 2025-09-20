#!/bin/bash
echo "[1/2] Mengkompilasi App.java dengan library..."
javac -cp "JadwalLib.jar" main.java

echo "[2/2] Menjalankan App..."
echo "----------------------------------------------------"
clear
java -cp ".:JadwalLib.jar" Main
echo "----------------------------------------------------"