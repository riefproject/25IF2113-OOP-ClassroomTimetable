#!/bin/bash
JAR_NAME="AkademikByArief"
rm -rf out
mkdir out

echo "[1/2] Mengkompilasi semua file .java..."
javac -d out src/id/ac/polban/app/*.java src/id/ac/polban/model/*.java src/id/ac/polban/service/*.java

echo "[2/2] Membuat file JadwalLib.jar..."
jar cf ${JAR_NAME}.jar -C out .

echo ""
echo " Build Selesai!"