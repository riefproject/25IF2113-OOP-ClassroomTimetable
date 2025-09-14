package id.ac.polban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MataKuliah extends AkademikEntity {
    private int sks;
    private CourseType tipeMataKuliah;
    private final List<Prodi> daftarProdi = new ArrayList<>(); 
    private final List<Dosen> pengampu = new ArrayList<>();
    
    public MataKuliah(String kodeMk, String namaMk, int sks, CourseType tipeMataKuliah) {
        super(kodeMk, namaMk);
        this.sks = sks;
        this.tipeMataKuliah = Objects.requireNonNull(tipeMataKuliah);

        // Logika untuk asosiasi Prodi akan ditangani secara eksternal (di Seed.java)
        // Untuk PRODI_SPECIFIC, daftarProdi akan diisi nanti.
        // Untuk UMUM, daftarProdi harus tetap kosong.
    }

    public String getKodeMk() {
        return getKode();
    }
    public int getSks() {
        return sks;
    }
    public String getNamaMk() {
        return getNama();
    }

    public void setKodeMk(String kodeMk) {
        super.setKode(Objects.requireNonNull(kodeMk));
    }
    public void setNamaMk(String namaMk) {
        super.setNama(Objects.requireNonNull(namaMk));
    }
    public void setSks(int sks) {
        this.sks = sks;
    }

    public CourseType getTipeMataKuliah() {
        return tipeMataKuliah;
    }

    public List<Prodi> getDaftarProdi() { // Renamed and returns a list
        return Collections.unmodifiableList(daftarProdi);
    }

    // Metode untuk mengelola hubungan many-to-many
    public void tambahProdi(Prodi p) {
        // Memastikan link dua arah jika diperlukan, tapi untuk saat ini, Prodi.tambahMataKuliah menangani sisinya
        if (p != null && !daftarProdi.contains(p)) {
            daftarProdi.add(p);
        }
    }

    public void hapusProdi(Prodi p) {
        if (p != null) {
            daftarProdi.remove(p);
        }
    }

    public List<Dosen> getPengampu() {
        return Collections.unmodifiableList(pengampu);
    }

    // dipanggil dari Dosen.ampu/lepas untuk jaga konsistensi
    void tambahPengampu(Dosen d) {
        if (d != null && !pengampu.contains(d)) pengampu.add(d);
    }
    void hapusPengampu(Dosen d) {
        pengampu.remove(d);
    }

    public boolean canActivate() {
        if (tipeMataKuliah == CourseType.UMUM) {
            return pengampu.stream().anyMatch(Dosen::getIsActive);
        } else if (tipeMataKuliah == CourseType.PRODI_SPECIFIC) {
            return daftarProdi.stream().anyMatch(Prodi::getIsActive);
        }
        return false;
    }

    @Override
    public void setIsActive(boolean active) {
        if (active && !canActivate()) {
            throw new IllegalStateException("Mata Kuliah " + getKodeMk() + " tidak dapat diaktifkan karena tidak memenuhi kriteria aktivasi (Dosen aktif atau Prodi aktif).");
        }
        super.setIsActive(active);
    }

    @Override
    public String getIdentitas() {
        String base = super.getIdentitas() + " | SKS: " + sks;
        String typeInfo = " | Tipe: " + tipeMataKuliah;
        String prodiInfo = "";
        if (tipeMataKuliah == CourseType.PRODI_SPECIFIC && !daftarProdi.isEmpty()) {
            prodiInfo = " | Prodi: " + daftarProdi.stream()
                                                .map(Prodi::getAlias)
                                                .collect(Collectors.joining(", "));
        }
        return base + typeInfo + prodiInfo;
    }
}