package id.ac.polban.model;

import java.util.List;

public class Mahasiswa extends Person {
    private Kelas kelas;

    public Mahasiswa(String nim, String nama, Kelas kelas) {
        super(nim, nama);
        this.kelas = kelas;
    }

    public Kelas getKelas() {
        return kelas;
    }

    public void setKelas(Kelas kelas) {
        this.kelas = kelas;
    }

    // --- info lebih detail ---
    @Override
    public String getIdentity() {
        String baseIdentity = super.getIdentity();
        String extra = "";
        if (kelas != null) extra += " | Kelas: " + kelas.getCode();
        extra += " | Status: " + (isActive() ? "Aktif" : "Non-Aktif");
        return baseIdentity + extra;
    }

    // --- Implementasi Displayable ---
    @Override
    public List<String> getTableHeader() {
        return List.of("NIM", "Nama Mahasiswa", "Kelas", "Status");
    }

    @Override
    public List<String> getTableRowData() {
        return List.of(
            getId(),
            getName(),
            (kelas != null ? kelas.getCode() : "-"),
            (isActive() ? "Aktif" : "Non-Aktif")
        );
    }

    // --- Implementasi Persistable ---
    @Override
    public String toPersistableFormat() {
        String personData = super.toPersistableFormat();
        String kelasId = (kelas != null) ? kelas.getCode() : "null";
        return personData + "," + kelasId;
    }

    // --- Implementasi Activable ---
    @Override
    public void activate() {
        // Mahasiswa bisa aktif jika kelasnya aktif
        if (this.kelas != null && this.kelas.isActive()) {
            this.isActive = true;
        }
    }
}
