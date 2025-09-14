package id.ac.polban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Kelas extends AkademikEntity {
    private Prodi prodi;
    private final List<Mahasiswa> daftarMahasiswa = new ArrayList<>();
    private final List<Jadwal> daftarJadwal = new ArrayList<>();

    public Kelas(String kodeKelas, Prodi prodi) {
        super(kodeKelas, "Kelas " + kodeKelas);
        this.prodi = Objects.requireNonNull(prodi);
    }

    public String getKodeKelas() {
        return getKode();
    }

    public void setKodeKelas(String kodeKelas) {
        super.setKode(Objects.requireNonNull(kodeKelas));
    }

    // Relasi ke Prodi
    public Prodi getProdi() { return prodi; }
    public void setProdi(Prodi prodi) { this.prodi = Objects.requireNonNull(prodi); }

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

    public boolean canActivate() {
        return prodi.getIsActive() && daftarMahasiswa.stream().anyMatch(Mahasiswa::getIsActive);
    }

    @Override
    public void setIsActive(boolean active) {
        if (active && !canActivate()) {
            throw new IllegalStateException("Kelas " + getKode() + " tidak dapat diaktifkan karena Prodi tidak aktif atau tidak memiliki Mahasiswa yang aktif.");
        }
        super.setIsActive(active);
    }
}