package id.ac.polban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Jurusan sebagai unit akademik (bukan lokasi fisik).
 */
public class Jurusan extends AkademikEntity {
    private Kampus kampus;
    private final List<Prodi> daftarProdi = new ArrayList<>();
    private String alias; // singkatan, mis. "JTK"

    public Jurusan(String kode, String nama, Kampus kampus) {
        super(kode, nama);
        this.kampus = Objects.requireNonNull(kampus);
    }
    public Jurusan(String kode, String nama, String alias, Kampus kampus) {
        super(kode, nama);
        this.alias = alias;
        this.kampus = Objects.requireNonNull(kampus);
    }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public Kampus getKampus() { return kampus; }
    public void setKampus(Kampus kampus) { this.kampus = Objects.requireNonNull(kampus); }

    public List<Prodi> getDaftarProdi() {
        return Collections.unmodifiableList(daftarProdi);
    }
    public void tambahProdi(Prodi p) {
        if (p != null && !daftarProdi.contains(p)) {
            daftarProdi.add(p);
            if (p.getJurusan() != this) p.setJurusan(this);
        }
    }
    public void hapusProdi(Prodi p) {
        if (p != null && daftarProdi.remove(p)) {
            if (p.getJurusan() == this) p.setJurusan(null);
        }
    }
    
    public boolean canActivate() {
        return kampus.getIsActive() && daftarProdi.stream().anyMatch(Prodi::getIsActive);
    }

    @Override
    public void setIsActive(boolean active) {
        if (active && !canActivate()) {
            throw new IllegalStateException("Jurusan " + getKode() + " tidak dapat diaktifkan karena Kampus tidak aktif atau tidak memiliki Prodi yang aktif.");
        }
        super.setIsActive(active);
    }

    @Override
    public String getIdentitas() {
        String base = super.getIdentitas();
        String extra = "";
        if (alias != null && !alias.isEmpty()) extra += " | Alias: " + alias;
        if (kampus != null) extra += " | Kampus: " + kampus.getNama();
        return base + extra;
    }
}
