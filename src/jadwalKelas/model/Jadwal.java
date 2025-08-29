package jadwalKelas.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class Jadwal {
    private final Kelas kelas;
    private final MataKuliah mataKuliah;   // 1..1
    private final Dosen dosen;            // 1..1 (pengajar untuk slot ini)
    private DayOfWeek hari;
    private LocalTime jamMulai;
    private LocalTime jamSelesai;
    private String ruangan;

    public Jadwal(Kelas kelas, MataKuliah mataKuliah, Dosen dosen,
                  DayOfWeek hari, LocalTime jamMulai, LocalTime jamSelesai, String ruangan) {
        this.kelas = Objects.requireNonNull(kelas);
        this.mataKuliah = Objects.requireNonNull(mataKuliah);
        this.dosen = Objects.requireNonNull(dosen);
        this.hari = Objects.requireNonNull(hari);
        this.jamMulai = Objects.requireNonNull(jamMulai);
        this.jamSelesai = Objects.requireNonNull(jamSelesai);
        this.ruangan = Objects.requireNonNull(ruangan);
        kelas.tambahJadwal(this);
    }

    public Kelas getKelas() {
        return kelas;
    }
    public MataKuliah getMataKuliah() {
        return mataKuliah;
    }
    public Dosen getDosen() {
        return dosen;
    }
    public DayOfWeek getHari() {
        return hari;
    }
    public LocalTime getJamMulai() {
        return jamMulai;
    }
    public LocalTime getJamSelesai() {
        return jamSelesai;
    }
    public String getRuangan() {
        return ruangan;
    }

    public void setHari(DayOfWeek hari) {
        this.hari = Objects.requireNonNull(hari);
    }
    public void setJamMulai(LocalTime jamMulai) {
        this.jamMulai = Objects.requireNonNull(jamMulai);
    }
    public void setJamSelesai(LocalTime jamSelesai) {
        this.jamSelesai = Objects.requireNonNull(jamSelesai);
    }
    public void setRuangan(String ruangan) {
        this.ruangan = Objects.requireNonNull(ruangan);
    }
}
