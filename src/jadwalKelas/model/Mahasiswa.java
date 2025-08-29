package jadwalKelas.model;

import java.util.Objects;

public class Mahasiswa {
    private String nim;
    private String nama;
    private Kelas kelas;

    public Mahasiswa(String nim, String nama, Kelas kelas) {
        this.nim = Objects.requireNonNull(nim);
        this.nama = Objects.requireNonNull(nama);
        this.kelas = kelas;
    }

    public String getNim() {
        return nim;
    }
    public String getNama() {
        return nama;
    }
    public Kelas getKelas() {
        return kelas;
    }

    public void setNama(String nama) {
        this.nama = Objects.requireNonNull(nama);
    }
    public void setKelas(Kelas kelas) {
        this.kelas = kelas;
    }
}
