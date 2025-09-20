package id.ac.polban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MataKuliah extends AkademikEntity {
    private int sks;
    private CourseType courseType;
    private final List<Prodi> prodiList = new ArrayList<>();
    private final List<Dosen> pengampuList = new ArrayList<>();

    public MataKuliah(String courseCode, String courseName, int sks, CourseType courseType) {
        super(courseCode, courseName);
        this.sks = sks;
        this.courseType = Objects.requireNonNull(courseType);
    }

    // --- Getters & Setters ---
    public int getSks() { return sks; }
    public void setSks(int sks) { this.sks = sks; }
    public CourseType getCourseType() { return courseType; }

    // --- Logika Bisnis Relasi ---
    public List<Prodi> getProdiList() { return Collections.unmodifiableList(prodiList); }
    public void addProdi(Prodi prodi) { if (prodi != null && !prodiList.contains(prodi)) prodiList.add(prodi); }
    public void removeProdi(Prodi prodi) { prodiList.remove(prodi); }
    public List<Dosen> getPengampuList() { return Collections.unmodifiableList(pengampuList); }
    void tambahPengampu(Dosen dosen) { if (dosen != null && !pengampuList.contains(dosen)) pengampuList.add(dosen); }
    void hapusPengampu(Dosen dosen) { pengampuList.remove(dosen); }

    // --- Info Lebih Detail ---
    @Override
    public String getIdentity() {
        return super.getIdentity() + " | SKS: " + sks + " | Tipe: " + courseType + " | Status: " + (isActive() ? "Aktif" : "Non-Aktif");
    }

    // --- Implementasi Interface Activable ---
    @Override
    public void activate() { this.isActive = true; }

    // --- Implementasi Displayable ---
    @Override
    public List<String> getTableHeader() {
        return List.of("Kode MK", "Nama Mata Kuliah", "SKS", "Tipe", "Status", "Prodi Terkait");
    }

    @Override
    public List<String> getTableRowData() {
        String prodiInfo = prodiList.stream().map(Prodi::getAlias).collect(Collectors.joining(", "));
        if (courseType == CourseType.UMUM) prodiInfo = "UMUM";
        if (prodiInfo.isEmpty()) prodiInfo = "-";

        return List.of(
            getCode(),
            getName(),
            String.valueOf(sks),
            courseType.toString(),
            isActive() ? "Aktif" : "Non-Aktif",
            prodiInfo
        );
    }

    // --- Implementasi Persistable ---
    @Override
    public String toPersistableFormat() {
        return super.toPersistableFormat() + "," + sks + "," + courseType + "," + isActive();
    }
}