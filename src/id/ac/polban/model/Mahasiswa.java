package id.ac.polban.model;

import java.util.Objects;

public class Mahasiswa extends Person {
    private Kelas kelas;

    public Mahasiswa(String nim, String nama, Kelas kelas) {
        super(nim, nama);
        this.kelas = kelas;
    }

    public String getNim() {
        return getKode();
    }
    public String getNama() {
        return super.getNama();
    }
    public Kelas getKelas() {
        return kelas;
    }

    public void setNama(String nama) {
        super.setNama(Objects.requireNonNull(nama));
    }
    public void setKelas(Kelas kelas) {
        this.kelas = kelas;
    }
    
    public boolean canActivate() {
        return kelas != null && kelas.getIsActive();
    }

    @Override
    public void setIsActive(boolean active) {
        if (active && !canActivate()) {
            throw new IllegalStateException("Mahasiswa " + getNim() + " tidak dapat diaktifkan karena Kelas tidak ada atau tidak aktif.");
        }
        super.setIsActive(active);
    }

    @Override
    public String getIdentitas() {
        String base = super.getIdentitas();
        return kelas != null ? base + " | Kelas: " + kelas.getKodeKelas() : base;
    }
}