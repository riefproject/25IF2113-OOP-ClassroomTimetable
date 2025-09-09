package id.ac.polban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Kelas {
    private String kodeKelas;
    private final List<Mahasiswa> daftarMahasiswa = new ArrayList<>();
    private final List<Jadwal> daftarJadwal = new ArrayList<>();

    public Kelas(String kodeKelas) {
        this.kodeKelas = Objects.requireNonNull(kodeKelas);
    }

    public String getKodeKelas() {
        return kodeKelas;
    }

    public void setKodeKelas(String kodeKelas) {
        this.kodeKelas = Objects.requireNonNull(kodeKelas);
    }

    public List<Mahasiswa> getDaftarMahasiswa() {
        return Collections.unmodifiableList(daftarMahasiswa);
    }
    public void tambahMahasiswa(Mahasiswa m) {
        if (m != null && !daftarMahasiswa.contains(m)) {
            daftarMahasiswa.add(m);
            if (m.getKelas() != this) m.setKelas(this);
        }
    }
    public void hapusMahasiswa(Mahasiswa m) {
        if (daftarMahasiswa.remove(m) && m.getKelas() == this) {
            m.setKelas(null);
        }
    }

    public List<Jadwal> getDaftarJadwal() {
        return Collections.unmodifiableList(daftarJadwal);
    }
    void tambahJadwal(Jadwal j) {
        if (j != null && !daftarJadwal.contains(j)) daftarJadwal.add(j);
    }
    void hapusJadwal(Jadwal j) { daftarJadwal.remove(j); }
}
