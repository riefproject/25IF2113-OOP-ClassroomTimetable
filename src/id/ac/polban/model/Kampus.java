package id.ac.polban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Kampus extends AkademikEntity {
    private final List<Jurusan> jurusanList = new ArrayList<>();
    private String alias;

    public Kampus(String code, String name) {
        super(code, name);
    }

    public Kampus(String code, String name, String alias) {
        super(code, name);
        this.alias = alias;
    }

    // --- Getters & Setters ---
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    // --- Logika Bisnis ---
    public List<Jurusan> getJurusanList() { return Collections.unmodifiableList(jurusanList); }
    public void addJurusan(Jurusan jurusan) { if (jurusan != null && !jurusanList.contains(jurusan)) jurusanList.add(jurusan); }
    public void removeJurusan(Jurusan jurusan) { jurusanList.remove(jurusan); }

    // --- Override untuk Info Lebih Detail ---
    @Override
    public String getIdentity() {
        String base = super.getIdentity();
        if (alias != null && !alias.isEmpty()) base += " | Alias: " + alias;
        base += " | Jumlah Jurusan: " + jurusanList.size();
        return base + " | Status: " + (isActive() ? "Aktif" : "Non-Aktif");
    }

    // --- Implementasi Displayable ---
    @Override
    public List<String> getTableHeader() {
        return List.of("Kode Kampus", "Nama Kampus", "Alias", "Jumlah Jurusan", "Status");
    }

    @Override
    public List<String> getTableRowData() {
        return List.of(
            getCode(),
            getName(),
            (alias != null ? alias : "-"),
            String.valueOf(jurusanList.size()),
            (isActive() ? "Aktif" : "Non-Aktif")
        );
    }

    // --- Implementasi Persistable ---
    @Override
    public String toPersistableFormat() {
        return super.toPersistableFormat() + "," + alias;
    }

    // --- Implementasi Activable ---
    @Override
    public void activate() {
        // Kampus bisa aktif jika memiliki minimal 1 jurusan
        if (!this.jurusanList.isEmpty()) {
            this.isActive = true;
        }
    }
}
