package id.ac.polban.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import id.ac.polban.contract.Displayable;
import id.ac.polban.contract.Persistable;

public class Jadwal implements Displayable, Persistable {
    private final Kelas kelas;
    private final MataKuliah mataKuliah;
    private final Dosen dosen;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;

    public Jadwal(Kelas kelas, MataKuliah mataKuliah, Dosen dosen,
                  DayOfWeek day, LocalTime startTime, LocalTime endTime, String room) {
        this.kelas = Objects.requireNonNull(kelas);
        this.mataKuliah = Objects.requireNonNull(mataKuliah);
        this.dosen = Objects.requireNonNull(dosen);
        this.day = Objects.requireNonNull(day);
        this.startTime = Objects.requireNonNull(startTime);
        this.endTime = Objects.requireNonNull(endTime);
        this.room = Objects.requireNonNull(room);
        kelas.addJadwal(this);
    }

    // --- Getters & Setters ---
    public Kelas getKelas() { return kelas; }
    public MataKuliah getMataKuliah() { return mataKuliah; }
    public Dosen getDosen() { return dosen; }
    public DayOfWeek getDay() { return day; }
    public void setDay(DayOfWeek day) { this.day = Objects.requireNonNull(day); }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = Objects.requireNonNull(startTime); }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = Objects.requireNonNull(endTime); }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = Objects.requireNonNull(room); }

    // --- Implementasi Displayable ---
    @Override
    public List<String> getTableHeader() {
        return List.of("Hari", "Waktu", "Mata Kuliah", "Dosen Pengajar", "Ruangan");
    }

    @Override
    public List<String> getTableRowData() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String waktu = startTime.format(timeFormatter) + " - " + endTime.format(timeFormatter);
        return List.of(
            day.toString(),
            waktu,
            mataKuliah.getName(),
            dosen.getName(),
            room
        );
    }

    // --- Implementasi Persistable ---
    @Override
    public String toPersistableFormat() {
        return String.join(",",
                kelas.getCode(), mataKuliah.getCode(), dosen.getId(),
                day.toString(), startTime.toString(), endTime.toString(), room);
    }
}
