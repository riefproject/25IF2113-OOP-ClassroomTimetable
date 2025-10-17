package id.ac.polban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Dosen extends Person {
    private String email;
    private final List<MataKuliah> mataKuliahDiampu = new ArrayList<>();

    public Dosen(String kodeDosen, String nama, String email) {
        super(kodeDosen, nama);
        this.email = Objects.requireNonNull(email);
    }

    // --- Getters & Setters ---
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = Objects.requireNonNull(email); }

    // --- Logika bisnis buat ngatur mata kuliah yg diampu ---
    public List<MataKuliah> getMataKuliahDiampu() {
        return Collections.unmodifiableList(mataKuliahDiampu);
    }

    public void ampu(MataKuliah mk) {
        if (mk != null && !mataKuliahDiampu.contains(mk)) {
            mataKuliahDiampu.add(mk);
            mk.tambahPengampu(this);
        }
    }

    public void lepas(MataKuliah mk) {
        if (mk != null && mataKuliahDiampu.remove(mk)) {
            mk.hapusPengampu(this);
        }
    }

    // --- info lebih detail ---
    @Override
    public String getIdentity() {
        return super.getIdentity() + " | " + email + " | Status: " + (isActive() ? "Aktif" : "Non-Aktif");
    }

    // --- Implementasi Displayable ---
    @Override
    public List<String> getTableHeader() {
        return List.of("NIP", "Nama Dosen", "Email", "Status");
    }

    @Override
    public List<String> getTableRowData() {
        return List.of(
            getId(),
            getName(),
            this.email,
            (isActive() ? "Aktif" : "Non-Aktif")
        );
    }

    // --- Implementasi Persistable ---
    @Override
    public String toPersistableFormat() {
        return super.toPersistableFormat() + "," + this.email;
    }

    // --- Implementasi Activable ---
    @Override
    public void activate() {
        this.isActive = true;
    }
}
