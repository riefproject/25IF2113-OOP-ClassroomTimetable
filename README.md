# 🚀 Proyek Jadwal Kuliah OOP - Polban 2025

Selamat datang di repositori Proyek Jadwal Kuliah! Ini adalah proyek yang dibuat untuk memenuhi tugas mata kuliah **Pemrograman Berorientasi Objek (OOP)** di Politeknik Negeri Bandung.

Aplikasi ini adalah sebuah Command-Line Interface (CLI) sederhana yang memungkinkan pengguna untuk melihat dan mencari jadwal perkuliahan berdasarkan data yang ada. Simpel, cepat, dan langsung ke intinya!

## ✨ Fitur Keren

Aplikasi ini punya beberapa fitur utama:

1.  **Cari Jadwal by Kelas**: Langsung lihat jadwal lengkap untuk kelas tertentu. Bisa juga difilter berdasarkan hari, jadi nggak perlu scroll panjang-panjang.
2.  **Lihat Daftar Kelas**: Lupa kode kelasmu? Tenang, ada daftar semua kelas yang tersedia di sistem.
3.  **Ringkasan Data**: Penasaran ada siapa aja dosennya atau apa aja mata kuliahnya? Fitur ini menampilkan ringkasan data dosen, kelas, beserta mahasiswanya.

## 🛠️ Teknologi yang Digunakan

Proyek ini dibangun dengan teknologi fundamental, fokus pada penerapan konsep OOP:

-   **Java**: Tentu saja, bahasa utama yang digunakan.
-   **Prinsip OOP**: Menerapkan konsep-konsep inti seperti *Encapsulation*, *Association*, dan *Composition* untuk memodelkan entitas seperti Mahasiswa, Dosen, dan Jadwal.
-   **No External Libraries**: Dibuat murni dengan Java Development Kit (JDK) standar untuk memaksimalkan pemahaman dasar.

## 📂 Struktur Proyek

-   `src/`: Berisi semua kode sumber Java, diorganisir dalam beberapa package:
    -   `app`: Titik masuk aplikasi (Main.java).
    -   `model`: Kelas-kelas yang merepresentasikan data (Dosen, Mahasiswa, dll.).
    -   `service`: Logika bisnis, seperti memuat data dari CSV dan fungsionalitas pencarian.
-   `data/`: Kumpulan file `.csv` yang menjadi "database" untuk aplikasi ini.

## ⚠️ Catatan Mengenai Data

Data yang digunakan dalam proyek ini (`dosen.csv`, `mahasiswa.csv`, `jadwal.csv`, `matakuliah.csv`) adalah **data riil perkuliahan di Polban**. Namun, untuk menjaga privasi, **semua identitas pribadi seperti nama dosen dan mahasiswa telah disamarkan**. Jadi, jangan heran kalau menemukan nama-nama yang unik atau lucu, ya!

## 💻 Cara Menjalankan

Sudah tidak sabar untuk mencoba? Ikuti langkah-langkah berikut:

1.  **Compile Kode**
    Buka terminal atau command prompt, lalu jalankan perintah di bawah ini dari direktori root proyek:

    ```bash
    # Untuk pengguna Linux/macOS
    ./build.sh

    # Atau secara manual
    javac -d out src/id/ac/polban/**/*.java
    ```

2.  **Jalankan Aplikasi**
    Setelah berhasil dicompile, jalankan aplikasi dengan perintah:

    ```bash
    # Untuk pengguna Linux/macOS
    ./run.sh

    # Atau secara manual
    java -cp out id.ac.polban.app.Main
    ```

Setelah itu, menu interaktif akan muncul di terminal Anda. Selamat mencoba!

## 📦 Rilis

File JAR yang sudah dicompile (`AkademikByArief.jar`) akan diunggah ke bagian [Rilis](https://github.com/riefproject/25IF2113-OOP-ClassroomTimetable/releases) repositori ini setiap ada pembaruan.

## Lisensi

Proyek ini dilisensikan di bawah [Lisensi MIT](LICENSE).

---

Dibuat dengan semangat dan sedikit kafein. Semoga bermanfaat! 😉
