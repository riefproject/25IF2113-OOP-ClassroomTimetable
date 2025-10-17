#!/bin/bash
JAR_NAME="AkademikByArief"
rm -rf out
mkdir out

echo "[1/2] Mengkompilasi semua file .java (kode produksi saja)..."
# Kompilasi hanya sumber produksi di src/main/java; file uji tetap membutuhkan dependency eksternal.
javac -d out $(find src/main/java -name "*.java")

echo "[2/2] Membuat file ${JAR_NAME}.jar..."
jar cf ${JAR_NAME}.jar -C out .

echo ""
echo " Build Selesai!"
