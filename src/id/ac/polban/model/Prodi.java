package id.ac.polban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Prodi extends AkademikEntity {
    private Jurusan jurusan;
    private String alias;   // singkatan/alias, mis. "TI"
    private String jenjang; // mis. "D3", "D4", "S2"
    private final List<Kelas> daftarKelas = new ArrayList<>();
    private final List<MataKuliah> daftarMataKuliah = new ArrayList<>();

    public Prodi(String kode, String nama) {
        super(kode, nama);
    }
    public Prodi(String kode, String nama, String alias, String jenjang) {
        super(kode, nama);
        this.alias = alias;
        this.jenjang = jenjang;
    }

    public Jurusan getJurusan() { return jurusan; }
    void setJurusan(Jurusan jurusan) { this.jurusan = jurusan; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getJenjang() { return jenjang; }
    public void setJenjang(String jenjang) { this.jenjang = jenjang; }

    public List<Kelas> getDaftarKelas() { return Collections.unmodifiableList(daftarKelas); }
    public void tambahKelas(Kelas k) {
        if (k != null && !daftarKelas.contains(k)) {
            daftarKelas.add(k);
            if (k.getProdi() != this) k.setProdi(this);
        }
    }
    public void hapusKelas(Kelas k) {
        if (k != null && daftarKelas.remove(k)) {
            if (k.getProdi() == this) k.setProdi(null);
        }
    }

    public List<MataKuliah> getDaftarMataKuliah() { return Collections.unmodifiableList(daftarMataKuliah); }
    public void tambahMataKuliah(MataKuliah mk) {
        if (mk != null && mk.getTipeMataKuliah() == CourseType.PRODI_SPECIFIC && !daftarMataKuliah.contains(mk)) {
            daftarMataKuliah.add(mk);
            mk.tambahProdi(this);
        }
    }
    public void hapusMataKuliah(MataKuliah mk) {
        if (mk != null && daftarMataKuliah.remove(mk)) {
            mk.hapusProdi(this);
        }
    }
    
    public boolean canActivate() {
        return jurusan.getIsActive() && daftarKelas.stream().anyMatch(Kelas::getIsActive);
    }

    @Override
    public void setIsActive(boolean active) {
        if (active && !canActivate()) {
            throw new IllegalStateException("Prodi " + getKode() + " tidak dapat diaktifkan karena Jurusan tidak aktif atau tidak memiliki Kelas yang aktif.");
        }
        super.setIsActive(active);
    }

    @Override
    public String getIdentitas() {
        String base = super.getIdentitas();
        String extra = "";
        if (alias != null && !alias.isEmpty()) extra += " | Alias: " + alias;
        if (jenjang != null && !jenjang.isEmpty()) extra += " | Jenjang: " + jenjang;
        if (jurusan != null) extra += " | Jurusan: " + jurusan.getNama();
        return base + extra;
    }
}
