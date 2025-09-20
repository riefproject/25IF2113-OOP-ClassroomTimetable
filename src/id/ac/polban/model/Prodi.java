package id.ac.polban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Prodi extends AkademikEntity {
    private Jurusan jurusan;
    private String alias;
    private String jenjang;
    private final List<Kelas> classList = new ArrayList<>();
    private final List<MataKuliah> courseList = new ArrayList<>();

    public Prodi(String code, String name) {
        super(code, name);
    }

    public Prodi(String code, String name, String alias, String jenjang) {
        super(code, name);
        this.alias = alias;
        this.jenjang = jenjang;
    }

    // --- Getters & Setters ---
    public Jurusan getJurusan() { return jurusan; }
    void setJurusan(Jurusan jurusan) { this.jurusan = jurusan; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public String getJenjang() { return jenjang; }
    public void setJenjang(String jenjang) { this.jenjang = jenjang; }

    // --- Logika Bisnis ---
    public List<Kelas> getClassList() { return Collections.unmodifiableList(classList); }
    public void addKelas(Kelas kelas) {
        if (kelas != null && !classList.contains(kelas)) {
            classList.add(kelas);
            if (kelas.getProdi() != this) kelas.setProdi(this);
        }
    }
    public void removeKelas(Kelas kelas) {
        if (kelas != null && classList.remove(kelas)) {
            if (kelas.getProdi() == this) kelas.setProdi(null);
        }
    }
    public List<MataKuliah> getCourseList() { return Collections.unmodifiableList(courseList); }
    public void addMataKuliah(MataKuliah mk) {
        if (mk != null && mk.getCourseType() == CourseType.PRODI_SPECIFIC && !courseList.contains(mk)) {
            courseList.add(mk);
            mk.addProdi(this);
        }
    }
    public void removeMataKuliah(MataKuliah mk) {
        if (mk != null && courseList.remove(mk)) {
            mk.removeProdi(this);
        }
    }

    // --- Info Lebih Detail ---
    @Override
    public String getIdentity() {
        String base = super.getIdentity();
        String extra = "";
        if (alias != null && !alias.isEmpty()) extra += " | Alias: " + alias;
        if (jenjang != null && !jenjang.isEmpty()) extra += " | Jenjang: " + jenjang;
        if (jurusan != null) extra += " | Jurusan: " + jurusan.getName();
        extra += " | Status: " + (isActive() ? "Aktif" : "Non-Aktif");
        return base + extra;
    }

    // --- Implementasi Displayable ---
    @Override
    public List<String> getTableHeader() {
        return List.of("Kode Prodi", "Nama Prodi", "Jenjang", "Jurusan", "Status");
    }

    @Override
    public List<String> getTableRowData() {
        return List.of(
            getCode(),
            getName(),
            (jenjang != null ? jenjang : "-"),
            (jurusan != null ? jurusan.getName() : "-"),
            (isActive() ? "Aktif" : "Non-Aktif")
        );
    }

    // --- Implementasi Persistable ---
    @Override
    public String toPersistableFormat() {
        String base = super.toPersistableFormat();
        String jurusanCode = (jurusan != null) ? jurusan.getCode() : "null";
        return base + "," + alias + "," + jenjang + "," + jurusanCode;
    }

    // --- Implementasi Activable ---
    @Override
    public void activate() {
        // Prodi bisa aktif jika jurusannya aktif dan memiliki minimal 1 kelas
        if (this.jurusan != null && this.jurusan.isActive() && !this.classList.isEmpty()) {
            this.isActive = true;
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        // Jika prodi dinonaktifkan, semua kelas di bawahnya juga ikut non-aktif
        for (Kelas kelas : classList) {
            kelas.deactivate();
        }
    }
}
