#!/bin/bash
JAR_NAME="AkademikByArief"
rm -rf out
mkdir out

echo "[1/2] Mengkompilasi semua file .java..."
# Perubahan: Menggunakan command substitution '$()' yang lebih standar
# untuk memastikan semua file .java di bawah src terkompilasi.
javac -d out $(find src -name "*.java")

echo "[2/2] Membuat file ${JAR_NAME}.jar..."
jar cf ${JAR_NAME}.jar -C out .

echo ""
echo " Build Selesai!"