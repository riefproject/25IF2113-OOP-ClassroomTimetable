package id.ac.polban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Kampus dalam konteks organisasi akademik POLBAN (bukan lokasi fisik).
 */
public class Kampus extends AkademikEntity {
    private final List<Jurusan> daftarJurusan = new ArrayList<>();
    private String alias; // singkatan, opsional

    public Kampus(String kode, String nama) {
        super(kode, nama);
    }
    public Kampus(String kode, String nama, String alias) {
        super(kode, nama);
        this.alias = alias;
    }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public List<Jurusan> getDaftarJurusan() {
        return Collections.unmodifiableList(daftarJurusan);
    }
    public void tambahJurusan(Jurusan j) {
        if (j != null && !daftarJurusan.contains(j)) daftarJurusan.add(j);
    }
    public void hapusJurusan(Jurusan j) { daftarJurusan.remove(j); }

    public boolean canActivate() {
        return daftarJurusan.stream().anyMatch(Jurusan::getIsActive);
    }

    @Override
    public void setIsActive(boolean active) {
        if (active && !canActivate()) {
            throw new IllegalStateException("Kampus " + getKode() + " tidak dapat diaktifkan karena tidak memiliki Jurusan yang aktif.");
        }
        super.setIsActive(active);
    }

    @Override
    public String getIdentitas() {
        String base = super.getIdentitas();
        if (alias != null && !alias.isEmpty()) base += " | Alias: " + alias;
        return base + " | Jurusan: " + daftarJurusan.size();
    }
}
