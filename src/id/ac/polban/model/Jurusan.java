package id.ac.polban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Jurusan extends AkademikEntity {
    private Kampus kampus;
    private final List<Prodi> prodiList = new ArrayList<>();
    private String alias;

    public Jurusan(String code, String name, Kampus kampus) {
        super(code, name);
        this.kampus = Objects.requireNonNull(kampus);
    }

    public Jurusan(String code, String name, String alias, Kampus kampus) {
        super(code, name);
        this.alias = alias;
        this.kampus = Objects.requireNonNull(kampus);
    }

    // --- Getters & Setters ---
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public Kampus getKampus() { return kampus; }
    public void setKampus(Kampus kampus) { this.kampus = Objects.requireNonNull(kampus); }

    // --- Logika Bisnis ---
    public List<Prodi> getProdiList() { return Collections.unmodifiableList(prodiList); }
    public void addProdi(Prodi prodi) {
        if (prodi != null && !prodiList.contains(prodi)) {
            prodiList.add(prodi);
            if (prodi.getJurusan() != this) prodi.setJurusan(this);
        }
    }
    public void removeProdi(Prodi prodi) {
        if (prodi != null && prodiList.remove(prodi)) {
            if (prodi.getJurusan() == this) prodi.setJurusan(null);
        }
    }

    // --- Override untuk Info Lebih Detail ---
    @Override
    public String getIdentity() {
        String base = super.getIdentity();
        String extra = "";
        if (alias != null && !alias.isEmpty()) extra += " | Alias: " + alias;
        if (kampus != null) extra += " | Kampus: " + kampus.getName();
        extra += " | Status: " + (isActive() ? "Aktif" : "Non-Aktif");
        return base + extra;
    }

    // --- Implementasi Displayable ---
    @Override
    public List<String> getTableHeader() {
        return List.of("Kode Jurusan", "Nama Jurusan", "Alias", "Kampus", "Status");
    }

    @Override
    public List<String> getTableRowData() {
        return List.of(
            getCode(),
            getName(),
            (alias != null ? alias : "-"),
            (kampus != null ? kampus.getName() : "-"),
            (isActive() ? "Aktif" : "Non-Aktif")
        );
    }

    // --- Implementasi Persistable ---
    @Override
    public String toPersistableFormat() {
        String base = super.toPersistableFormat();
        String kampusCode = (kampus != null) ? kampus.getCode() : "null";
        return base + "," + alias + "," + kampusCode;
    }

    // --- Implementasi Activable ---
    @Override
    public void activate() {
        // Jurusan bisa aktif jika kampusnya aktif dan memiliki minimal 1 prodi
        if (this.kampus != null && this.kampus.isActive() && !this.prodiList.isEmpty()) {
            this.isActive = true;
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        // Jika jurusan dinonaktifkan, semua prodi di bawahnya juga ikut non-aktif
        for (Prodi prodi : prodiList) {
            prodi.deactivate();
        }
    }
}
