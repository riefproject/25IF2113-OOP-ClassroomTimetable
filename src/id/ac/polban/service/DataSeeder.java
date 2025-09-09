package id.ac.polban.service;

import id.ac.polban.model.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public final class DataSeeder {

    private DataSeeder() {}

    public static Seed seed() {
        // Mata kuliah
        MataKuliah mkDasar = new MataKuliah("25IF2101", "Pemrograman Dasar", 3);
        MataKuliah mkStruk = new MataKuliah("25IF2102", "Struktur Data", 3);
        MataKuliah mkBasis  = new MataKuliah("25IF2103", "Basis Data", 3);

        // Dosen
        Dosen d1 = new Dosen("251511001", "Dr. Andi Putra Wijaya", "andi@polban.ac.id");
        Dosen d2 = new Dosen("251511002", "Maulana Ishak, M.Kom", "maul@polban.ac.id");
        Dosen d3 = new Dosen("251511003", "Tisan Arun, M.Si", "tisan@polban.ac.id");

        // Relasi dosen <-> matkul (many-to-many)
        d1.ampu(mkDasar);
        d1.ampu(mkStruk);
        d2.ampu(mkBasis);
        d2.ampu(mkStruk);

        // Kelas
        Kelas D32A = new Kelas("D3-2A");
        Kelas D32B = new Kelas("D3-2B");
        Kelas D32C = new Kelas("D3-2A");

        // Mahasiswa
        Mahasiswa m1 = new Mahasiswa("2500001", "Arief Wijaya", null);
        Mahasiswa m2 = new Mahasiswa("2500002", "Nisa Uswatun", null);
        Mahasiswa m3 = new Mahasiswa("2500003", "Hasbi Hardian", null);
        Mahasiswa m4 = new Mahasiswa("2500004", "Farras Rasras", null);
        Mahasiswa m5 = new Mahasiswa("2500005", "Wafi Fifi", null);
        Mahasiswa m6 = new Mahasiswa("2500006", "Farrel Relrel", null);
        Mahasiswa m7 = new Mahasiswa("2500007", "Faliq Liqliq", null);

        // Daftarkan mahasiswa ke kelas (via method enkapsulasi)
        D32A.tambahMahasiswa(m1);
        D32A.tambahMahasiswa(m2);
        D32A.tambahMahasiswa(m3);

        D32B.tambahMahasiswa(m4);
        D32B.tambahMahasiswa(m5);

        D32C.tambahMahasiswa(m6);
        D32C.tambahMahasiswa(m7);

        new Jadwal(D32A, mkDasar, d1,DayOfWeek.MONDAY,    LocalTime.of(8, 0),  LocalTime.of(9, 40),  "R101");
        new Jadwal(D32A, mkStruk, d2,DayOfWeek.MONDAY,    LocalTime.of(8, 0),  LocalTime.of(9, 40),  "R101");
        new Jadwal(D32A, mkBasis, d3,DayOfWeek.THURSDAY,  LocalTime.of(10, 0), LocalTime.of(11, 40), "R102");
        new Jadwal(D32A, mkStruk, d1,DayOfWeek.THURSDAY,  LocalTime.of(10, 0), LocalTime.of(11, 40), "R102");

        new Jadwal(D32B, mkStruk, d2, DayOfWeek.TUESDAY,   LocalTime.of(13, 0), LocalTime.of(14, 40), "Lab Algoritma");
        new Jadwal(D32B, mkBasis, d1, DayOfWeek.FRIDAY,    LocalTime.of(8, 0),  LocalTime.of(9, 40),  "R203");

        new Jadwal(D32C, mkStruk, d3, DayOfWeek.WEDNESDAY, LocalTime.of(9, 0),  LocalTime.of(10, 40), "Lab Basis Data");

        return new Seed(
                List.of(d1, d2),
                List.of(mkDasar, mkStruk, mkBasis),
                List.of(D32A, D32B, D32C),
                List.of(m1, m2, m3, m4, m5)
        );
    }


    public static final class Seed {
        private final List<Dosen> dosen;
        private final List<MataKuliah> mataKuliah;
        private final List<Kelas> kelas;
        private final List<Mahasiswa> mahasiswa;

        public Seed(List<Dosen> dosen, List<MataKuliah> mataKuliah, List<Kelas> kelas, List<Mahasiswa> mahasiswa) {
            this.dosen = dosen;
            this.mataKuliah = mataKuliah;
            this.kelas = kelas;
            this.mahasiswa = mahasiswa;
        }

        public List<Dosen> getDosen() { return dosen; }
        public List<MataKuliah> getMataKuliah() { return mataKuliah; }
        public List<Kelas> getKelas() { return kelas; }
        public List<Mahasiswa> getMahasiswa() { return mahasiswa; }
    }
}
