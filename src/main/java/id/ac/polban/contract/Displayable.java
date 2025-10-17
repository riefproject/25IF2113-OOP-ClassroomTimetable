package id.ac.polban.contract;

import java.util.List;

/**
 * Kontrak untuk objek yang datanya bisa ditampilkan dalam format tabel.
 * Ini memisahkan DATA dari PRESENTASI.
 * Objek cuma perlu nyediain data header dan baris, nanti view yg ngatur nampilinnya.
 */
public interface Displayable {
    /**
     * Mengembalikan daftar judul kolom untuk tabel.
     * Contoh: ["NIM", "Nama Mahasiswa", "Status"]
     * @return List of string headers.
     */
    List<String> getTableHeader();

    /**
     * Mengembalikan daftar nilai (sebagai string) untuk satu baris data.
     * Urutannya harus sama persis dengan header.
     * Contoh: ["231511001", "John Doe", "Aktif"]
     * @return List of string row data.
     */
    List<String> getTableRowData();
}