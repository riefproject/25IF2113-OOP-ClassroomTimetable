# Analisis dan Justifikasi Desain Arsitektur: Abstract Class dan Interface

Dokumen ini menguraikan keputusan desain arsitektur untuk proyek penjadwalan akademik. Fokus utamanya adalah pada penggunaan strategis `abstract class` untuk membangun fondasi hierarki dan `interface` untuk memberikan kemampuan tambahan, yang secara efektif mensimulasikan keuntungan dari *multiple inheritance* dalam ekosistem Java.

## 1. Fondasi Hierarki: Peran `Abstract Class`

Dalam sistem ini, terdapat beberapa entitas yang berbagi karakteristik fundamental. Daripada menduplikasi atribut di setiap kelas, pendekatan yang lebih baik adalah dengan menciptakan sebuah basis atau fondasi bersama menggunakan `abstract class`.

-   **Justifikasi Penggunaan `abstract class`:**
    1.  **Berbagi State dan Implementasi**: `abstract class` dapat berisi *state* (atribut) dan implementasi metode yang sama untuk semua turunannya, sehingga mengurangi duplikasi kode.
    2.  **Mencegah Instansiasi yang Tidak Logis**: Sebuah konsep abstrak tidak seharusnya bisa diinstansiasi secara langsung. Menjadikan kelas sebagai `abstract` akan mencegah `new KonsepAbstrak()` dan memaksa penggunaan kelas turunan yang lebih konkret.
    3.  **Mendefinisikan Hubungan "IS A"**: Penggunaan `extends` secara jelas mendefinisikan hubungan "IS A" (adalah sebuah), yang menciptakan hierarki warisan yang logis dan kuat.

--- 

### Studi Kasus 1: Entitas `Person`

Contoh pertama adalah `Mahasiswa` dan `Dosen`. Keduanya adalah "orang" yang memiliki atribut dasar seperti nama. Untuk itu, kita mendefinisikan `abstract class Person`.

-   `Mahasiswa` **adalah sebuah** `Person`.
-   `Dosen` **adalah sebuah** `Person`.
-   Objek `new Person()` tidak logis, sehingga kelasnya harus `abstract`.

--- 

### Studi Kasus 2: Entitas `AkademikEntity`

Prinsip yang sama berlaku untuk `Jurusan`, `Prodi`, `MataKuliah`, dan `Kampus`. Semua entitas ini berbagi atribut fundamental: sebuah `kode` unik dan sebuah `nama`. Untuk itu, kita dapat mendefinisikan `abstract class AkademikEntity`.

-   `Jurusan` **adalah sebuah** `AkademikEntity`.
-   `Prodi` **adalah sebuah** `AkademikEntity`.
-   Objek `new AkademikEntity()` tidak logis, sehingga kelasnya harus `abstract`.
-   Kelas `AkademikEntity` menjadi fondasi yang menyediakan atribut (`kode`, `nama`) dan implementasi (`getIdentitas`, `equals`) untuk semua turunannya.

Dengan demikian, kita menciptakan dua pilar hierarki utama dalam desain: `Person` untuk entitas manusia dan `AkademikEntity` untuk entitas kelembagaan akademik.

## 2. Ekstensi Kemampuan: Simulasi Multi-Inheritance dengan Interface

Setelah fondasi "IS A" terbentuk, tantangan berikutnya muncul. Sebuah `Mahasiswa` bukan hanya seorang `Person`; ia juga memiliki "peran" atau "kemampuan" lain. Misalnya, datanya perlu **bisa ditampilkan**, statusnya perlu **bisa diatur (aktif/cuti)**, dan datanya perlu **bisa disimpan** ke file.

Java tidak mendukung *multiple inheritance* untuk kelas, artinya `class Mahasiswa` tidak bisa `extends Person, extends DisplayableEntity, extends ActivableEntity` secara bersamaan. Pembatasan ini ada untuk menghindari "Diamond Problem" yang kompleks.

Solusi Java untuk ini adalah `interface`. Jika `abstract class` mendefinisikan **apa sebuah objek itu (IS A)**, maka `interface` mendefinisikan **apa yang sebuah objek bisa lakukan (CAN DO)**. Sebuah kelas bisa `extends` satu `abstract class`, tetapi bisa `implements` banyak `interface`. Inilah cara kita mensimulasikan keuntungan dari *multiple inheritance*: sebuah kelas dapat "mewarisi" berbagai "kontrak kemampuan" dari sumber yang berbeda.

## 3. Rancangan Kontrak Perilaku (Interfaces)

Untuk mendefinisikan kemampuan-kemampuan tersebut secara konsisten di seluruh aplikasi, tiga interface utama diusulkan. Semua ditempatkan dalam package `id.ac.polban.contract` untuk memisahkan kontrak dari implementasi.

---

### a. Interface `Displayable`
-   **Path File**: `src/id/ac/polban/contract/Displayable.java`
-   **Justifikasi**: Banyak entitas (`Mahasiswa`, `Dosen`, `Jadwal`) perlu ditampilkan ke pengguna. Agar kode di bagian *view* (misalnya, kelas `MenuApp`) tidak perlu memeriksa tipe setiap objek (`if (obj instanceof Mahasiswa)`), kita ciptakan kontrak `Displayable`. Kode di *view* cukup memanggil `obj.displayInfo()` pada objek apa pun yang mengimplementasikan interface ini, sebuah demonstrasi polimorfisme yang kuat.
-   **Calon Kelas Implementor**: `Mahasiswa`, `Dosen`, `MataKuliah`, `Jadwal`, `Prodi`, `Jurusan`, `Kelas`, `Kampus`.
-   **Kode**:
    ```java
    package id.ac.polban.contract;

    public interface Displayable {
        /** Menampilkan informasi detail objek ke output standar. */
        void displayInfo();
    }
    ```

---

### b. Interface `Activable`
-   **Path File**: `src/id/ac/polban/contract/Activable.java`
-   **Justifikasi**: Konsep status "aktif" dan "tidak aktif" berlaku untuk banyak entitas: `Mahasiswa` (aktif/cuti), `Dosen` (mengajar/tugas belajar), atau `MataKuliah` (ditawarkan/tidak). Menyeragamkan manajemen status ini melalui interface `Activable` akan menciptakan API yang konsisten dan membuat logika bisnis lebih mudah dikelola.
-   **Calon Kelas Implementor**: `Mahasiswa`, `Dosen`, `MataKuliah`.
-   **Kode**:
    ```java
    package id.ac.polban.contract;

    public interface Activable {
        void activate();
        void deactivate();
        boolean isActive();
    }
    ```

---

### c. Interface `Persistable`
-   **Path File**: `src/id/ac/polban/contract/Persistable.java`
-   **Justifikasi**: Untuk memisahkan logika penyimpanan data (misalnya, ke file CSV) dari model itu sendiri. Dengan `Persistable`, setiap objek bertanggung jawab untuk menyediakan representasi datanya dalam format yang bisa disimpan. Kelas `DataManager` atau `Seed` menjadi lebih sederhana; ia tidak perlu tahu detail internal setiap model, cukup memanggil `toPersistableFormat()` untuk menyimpan dan `fromPersistableFormat()` untuk memuat.
-   **Calon Kelas Implementor**: `Mahasiswa`, `Dosen`, `MataKuliah`, `Jadwal`, `Prodi`, `Jurusan`, `Kelas`, `Kampus`.
-   **Kode**:
    ```java
    package id.ac.polban.contract;

    public interface Persistable {
        String toPersistableFormat();
        void fromPersistableFormat(String data);
    }
    ```

## 4. Studi Kasus: Sinergi `Abstract Class` dan `Interface` pada `Mahasiswa.java`

Kelas `Mahasiswa` menjadi contoh sempurna bagaimana kedua konsep ini bekerja sama untuk menciptakan desain yang kuat dan fleksibel.

-   **Path File**: `src/id/ac/polban/model/Mahasiswa.java` (Contoh modifikasi)
-   **Kode Deklarasi**:
    ```java
    public class Mahasiswa extends Person implements Displayable, Activable, Persistable {
        // ... implementasi ...
    }
    ```

-   **Analisis Deklarasi**:
    -   `extends Person`: Menetapkan identitas inti `Mahasiswa` sebagai turunan dari `Person`. Ia **mewarisi state** (seperti `nama`) dan **implementasi metode** dari `Person`. Ini adalah hubungan **"IS A"**.
    -   `implements Displayable, Activable, Persistable`: Memberikan `Mahasiswa` tiga kemampuan atau peran tambahan. Ia **berjanji untuk memenuhi kontrak** dari ketiga interface tersebut dengan menyediakan implementasi untuk metode-metode mereka. Ini adalah hubungan **"CAN DO"**.

Dengan pendekatan ini, kelas `Mahasiswa` mendapatkan "DNA" dari `Person` dan pada saat yang sama mengadopsi berbagai "keterampilan" dari interface yang diimplementasikannya.

## 5. Kesimpulan Desain

Kombinasi penggunaan `abstract class` untuk hierarki dasar ("IS A") dan `interface` untuk kemampuan tambahan ("CAN DO") menghasilkan arsitektur yang:
-   **Terstruktur dengan Baik**: Ada pemisahan yang jelas antara identitas inti sebuah objek dan peran-peran yang bisa dijalankannya.
-   **Fleksibel dan Dapat Diperluas**: Sangat mudah untuk menambahkan kemampuan baru (misalnya, `interface Exportable`) di masa depan tanpa mengganggu hierarki kelas yang sudah ada.
-   **Mendukung *Loose Coupling***: Komponen lain dapat berinteraksi dengan objek melalui kontrak `interface`-nya, tanpa perlu terikat pada implementasi kelas konkret.
-   **Memanfaatkan Polimorfisme secara Maksimal**: Memungkinkan penulisan kode yang lebih generik, bersih, dan mudah dipelihara.

Desain ini menjadi fondasi yang kokoh untuk pengembangan fitur-fitur selanjutnya dalam aplikasi.
