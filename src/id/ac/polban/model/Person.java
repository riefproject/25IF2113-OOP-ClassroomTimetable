package id.ac.polban.model;

import java.util.Objects;

/**
 * Base class untuk entitas manusia (aktor) seperti Dosen dan Mahasiswa.
 * Dipisahkan dari entitas akademik non-manusia (MataKuliah, Jurusan, Prodi, Kampus)
 * agar relasi "is-a" lebih kuat dan masuk akal secara domain.
 */
public class Person {
    private String kode; // dapat berperan sebagai NIM/NID atau kode unik lain
    private String nama;
    private boolean isActive = false; 

    public Person(String kode, String nama) {
        this.kode = Objects.requireNonNull(kode);
        this.nama = Objects.requireNonNull(nama);
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public String getKode() { return kode; }
    public String getNama() { return nama; }

    public void setKode(String kode) { this.kode = Objects.requireNonNull(kode); }
    public void setNama(String nama) { this.nama = Objects.requireNonNull(nama); }

    /**
     * Dapat dioverride oleh turunan untuk menambahkan informasi identitas.
     */
    public String getIdentitas() {
        return kode + " - " + nama;
    }

    @Override
    public String toString() {
        return getIdentitas();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person that = (Person) o;
        return kode.equals(that.kode);
    }

    @Override
    public int hashCode() {
        return kode.hashCode();
    }
}
