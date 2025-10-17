package id.ac.polban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Kelas extends AkademikEntity {
    private Prodi prodi;
    private final List<Mahasiswa> mahasiswaList = new ArrayList<>();
    private final List<Jadwal> jadwalList = new ArrayList<>();

    public Kelas(String classCode, Prodi prodi) {
        super(classCode, "Kelas " + classCode);
        this.prodi = Objects.requireNonNull(prodi);
    }

    // --- Getters & Setters ---
    public Prodi getProdi() { return prodi; }
    public void setProdi(Prodi prodi) { this.prodi = Objects.requireNonNull(prodi); }

    // --- Logika Bisnis ---
    public List<Mahasiswa> getMahasiswaList() { return Collections.unmodifiableList(mahasiswaList); }
    public void addMahasiswa(Mahasiswa mahasiswa) {
        if (mahasiswa != null && !mahasiswaList.contains(mahasiswa)) {
            mahasiswaList.add(mahasiswa);
            if (mahasiswa.getKelas() != this) mahasiswa.setKelas(this);
        }
    }
    public void removeMahasiswa(Mahasiswa mahasiswa) {
        if (mahasiswaList.remove(mahasiswa) && mahasiswa.getKelas() == this) {
            mahasiswa.setKelas(null);
        }
    }
    public List<Jadwal> getJadwalList() { return Collections.unmodifiableList(jadwalList); }
    void addJadwal(Jadwal jadwal) { if (jadwal != null && !jadwalList.contains(jadwal)) jadwalList.add(jadwal); }
    void removeJadwal(Jadwal jadwal) { jadwalList.remove(jadwal); }

    // --- Info Lebih Detail ---
    @Override
    public String getIdentity() {
        String base = super.getIdentity();
        String extra = "";
        if (prodi != null) extra += " | Prodi: " + prodi.getName();
        extra += " | Jumlah Mahasiswa: " + mahasiswaList.size();
        extra += " | Status: " + (isActive() ? "Aktif" : "Non-Aktif");
        return base + extra;
    }

    // --- Implementasi Displayable ---
    @Override
    public List<String> getTableHeader() {
        return List.of("Kode Kelas", "Program Studi", "Jumlah Mahasiswa", "Status");
    }

    @Override
    public List<String> getTableRowData() {
        return List.of(
            getCode(),
            (prodi != null ? prodi.getName() : "-"),
            String.valueOf(mahasiswaList.size()),
            (isActive() ? "Aktif" : "Non-Aktif")
        );
    }

    // --- Implementasi Persistable ---
    @Override
    public String toPersistableFormat() {
        String base = super.toPersistableFormat();
        String prodiCode = (prodi != null) ? prodi.getCode() : "null";
        return base + "," + prodiCode;
    }

    // --- Implementasi Activable ---
    @Override
    public void activate() {
        // Kelas bisa aktif jika prodinya aktif
        if (this.prodi != null && this.prodi.isActive()) {
            this.isActive = true;
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        // Jika kelas dinonaktifkan, semua mahasiswa di dalamnya juga ikut non-aktif
        for (Mahasiswa mahasiswa : mahasiswaList) {
            mahasiswa.deactivate();
        }
    }
}