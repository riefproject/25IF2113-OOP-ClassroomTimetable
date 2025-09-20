package id.ac.polban.service;

import id.ac.polban.contract.Activable;
import id.ac.polban.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Seed {
    private final List<Dosen> dosen;
    private final List<MataKuliah> mataKuliah;
    private final List<Kelas> kelas;
    private final List<Mahasiswa> mahasiswa;
    private final List<Kampus> kampus;
    private final List<Jurusan> jurusan;
    private final List<Prodi> prodi;

    public Seed(
            List<Dosen> dosen, List<MataKuliah> mataKuliah, List<Kelas> kelas, List<Mahasiswa> mahasiswa,
            List<Kampus> kampus, List<Jurusan> jurusan, List<Prodi> prodi) {
        this.dosen = dosen;
        this.mataKuliah = mataKuliah;
        this.kelas = kelas;
        this.mahasiswa = mahasiswa;
        this.kampus = kampus;
        this.jurusan = jurusan;
        this.prodi = prodi;
    }

    // --- Getters ---
    public List<Dosen> getDosen() { return dosen; }
    public List<MataKuliah> getMataKuliah() { return mataKuliah; }
    public List<Kelas> getKelas() { return kelas; }
    public List<Mahasiswa> getMahasiswa() { return mahasiswa; }
    public List<Kampus> getKampus() { return kampus; }
    public List<Jurusan> getJurusan() { return jurusan; }
    public List<Prodi> getProdi() { return prodi; }

    public static Seed loadFromCsvs(String basePath) throws IOException {
        Map<String, Dosen> dosenMap = readDosenCsv(basePath);
        List<Kampus> kampus = readKampusCsvIfExists(basePath);
        Map<String, Jurusan> jurusanMap = readJurusanCsvIfExists(basePath, kampus);
        Map<String, Prodi> prodiMap = readProdiCsvIfExists(basePath, jurusanMap);
        Map<String, MataKuliah> mataKuliahMap = readMataKuliahCsv(basePath);

        // Hubungkan MataKuliah dengan Prodi
        List<Map.Entry<String, String>> mataKuliahProdiMappings = readMataKuliahProdiMapping(basePath);
        for (Map.Entry<String, String> entry : mataKuliahProdiMappings) {
            MataKuliah mk = mataKuliahMap.get(entry.getKey());
            Prodi prodi = prodiMap.get(entry.getValue());
            if (mk != null && prodi != null) {
                
                if (mk.getCourseType() == CourseType.PRODI_SPECIFIC) {
                    prodi.addMataKuliah(mk);
                } else {
                    System.err.printf("Peringatan: Mata Kuliah '%s' (tipe UMUM) tidak boleh punya asosiasi prodi. Melewati.%n", mk.getCode());
                }
            }
        }

        Map<String, String> kelasProdiMapping = readKelasProdiMapping(basePath);
        Map<String, Kelas> kelasMap = readJadwalCsv(basePath, dosenMap, mataKuliahMap, prodiMap, kelasProdiMapping);
        List<Mahasiswa> mahasiswaList = readMahasiswaCsv(basePath, kelasMap);

        // Aktifkan semua entitas setelah semua data dimuat dan terhubung
        activateAll(dosenMap.values(), mataKuliahMap.values(), kampus, jurusanMap.values(), prodiMap.values(), kelasMap.values(), mahasiswaList);

        return new Seed(
                new ArrayList<>(dosenMap.values()), new ArrayList<>(mataKuliahMap.values()),
                new ArrayList<>(kelasMap.values()), mahasiswaList, kampus,
                new ArrayList<>(jurusanMap.values()), new ArrayList<>(prodiMap.values()));
    }

    private static void activateAll(Collection<Dosen> dosen, Collection<MataKuliah> mataKuliah, Collection<Kampus> kampus, Collection<Jurusan> jurusan, Collection<Prodi> prodi, Collection<Kelas> kelas, Collection<Mahasiswa> mahasiswa) {
        // Aktivasi entitas yang tidak memiliki dependensi
        dosen.forEach(Activable::activate);
        mataKuliah.forEach(Activable::activate);

        // Aktivasi hierarkis secara top-down
        kampus.forEach(Activable::activate);
        jurusan.forEach(Activable::activate);
        prodi.forEach(Activable::activate);
        kelas.forEach(Activable::activate);
        mahasiswa.forEach(Activable::activate);
    }

    private static Map<String, Dosen> readDosenCsv(String basePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(basePath, "dosen.csv"));
        Map<String, Dosen> dosenMap = new HashMap<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length < 3) continue;
            Dosen d = new Dosen(parts[0].trim(), parts[1].trim(), parts[2].trim());
            
            dosenMap.put(d.getId(), d);
        }
        return dosenMap;
    }

    private static Map<String, MataKuliah> readMataKuliahCsv(String basePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(basePath, "matakuliah.csv"));
        Map<String, MataKuliah> mataKuliahMap = new HashMap<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length < 4) continue;
            String kodeMk = parts[0].trim();
            String namaMk = parts[1].trim();
            int sks = Integer.parseInt(parts[2].trim());
            CourseType tipeMataKuliah = CourseType.valueOf(parts[3].trim().toUpperCase());
            MataKuliah mk = new MataKuliah(kodeMk, namaMk, sks, tipeMataKuliah);
            
            mataKuliahMap.put(mk.getCode(), mk);
        }
        return mataKuliahMap;
    }

    private static Map<String, Kelas> readJadwalCsv(
            String basePath, Map<String, Dosen> dosenMap, Map<String, MataKuliah> mataKuliahMap,
            Map<String, Prodi> prodiMap, Map<String, String> kelasProdiMapping) throws IOException {
        Map<String, Kelas> kelasMap = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(basePath, "jadwal.csv"));
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length < 7) continue;
            String kodeKelas = parts[0].trim();
            String kodeMk = parts[1].trim();
            String kodeDosen = parts[2].trim();

            MataKuliah mk = mataKuliahMap.get(kodeMk);
            Dosen dosen = dosenMap.get(kodeDosen);
            Prodi prodi = prodiMap.get(kelasProdiMapping.get(kodeKelas));

            if (mk == null || dosen == null || prodi == null) continue;

            Prodi finalProdi = prodi;
            Kelas kelas = kelasMap.computeIfAbsent(kodeKelas, k -> {
                Kelas newKelas = new Kelas(k, finalProdi);
                
                finalProdi.addKelas(newKelas);
                return newKelas;
            });

            DayOfWeek hari = DayOfWeek.valueOf(parts[3].trim().toUpperCase());
            LocalTime jamMulai = LocalTime.parse(parts[4].trim());
            LocalTime jamSelesai = LocalTime.parse(parts[5].trim());
            String ruangan = parts[6].trim();

            new Jadwal(kelas, mk, dosen, hari, jamMulai, jamSelesai, ruangan);
            dosen.ampu(mk);
        }
        return kelasMap;
    }

    private static List<Mahasiswa> readMahasiswaCsv(String basePath, Map<String, Kelas> kelasMap) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(basePath, "mahasiswa.csv"));
        List<Mahasiswa> allMahasiswa = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length < 3) continue;
            String nim = parts[0].trim();
            String nama = parts[1].trim();
            String kodeKelas = parts[2].trim();
            Kelas kelas = kelasMap.get(kodeKelas);
            if (kelas == null) continue;

            Mahasiswa m = new Mahasiswa(nim, nama, kelas);
            allMahasiswa.add(m);

            kelas.addMahasiswa(m);
        }
        return allMahasiswa;
    }

    private static List<Kampus> readKampusCsvIfExists(String basePath) throws IOException {
        Path p = Paths.get(basePath, "kampus.csv");
        List<Kampus> list = new ArrayList<>();
        if (!Files.exists(p)) return list;
        List<String> lines = Files.readAllLines(p);
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length < 3) continue;
            list.add(new Kampus(parts[0].trim(), parts[1].trim(), parts[2].trim()));
        }
        return list;
    }

    private static Map<String, Jurusan> readJurusanCsvIfExists(String basePath, List<Kampus> kampusList) throws IOException {
        Path p = Paths.get(basePath, "jurusan.csv");
        Map<String, Jurusan> map = new HashMap<>();
        if (!Files.exists(p)) return map;
        Map<String, Kampus> kampusMap = new HashMap<>();
        
        for (Kampus k : kampusList) kampusMap.put(k.getCode(), k);
        List<String> lines = Files.readAllLines(p);
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length < 4) continue;
            String kode = parts[0].trim();
            String nama = parts[1].trim();
            String alias = parts[2].trim();
            String kodeKampus = parts[3].trim();
            Kampus k = kampusMap.get(kodeKampus);
            if (k == null) continue;
            Jurusan j = new Jurusan(kode, nama, alias, k);
            k.addJurusan(j);
            map.put(kode, j);
        }
        return map;
    }

    private static Map<String, Prodi> readProdiCsvIfExists(String basePath, Map<String, Jurusan> jurusanMap) throws IOException {
        Path p = Paths.get(basePath, "prodi.csv");
        Map<String, Prodi> map = new HashMap<>();
        if (!Files.exists(p)) return map;
        List<String> lines = Files.readAllLines(p);
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length < 5) continue;
            String kode = parts[0].trim();
            String nama = parts[1].trim();
            String alias = parts[2].trim();
            String jenjang = parts[3].trim();
            String kodeJurusan = parts[4].trim();
            Jurusan j = jurusanMap.get(kodeJurusan);
            if (j == null) continue;
            Prodi pr = new Prodi(kode, nama, alias, jenjang);
            
            j.addProdi(pr);
            map.put(kode, pr);
        }
        return map;
    }

    private static Map<String, String> readKelasProdiMapping(String basePath) throws IOException {
        Path p = Paths.get(basePath, "kelas_prodi.csv");
        Map<String, String> map = new HashMap<>();
        if (!Files.exists(p)) return map;
        List<String> lines = Files.readAllLines(p);
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length < 2) continue;
            map.put(parts[0].trim(), parts[1].trim());
        }
        return map;
    }

    private static List<Map.Entry<String, String>> readMataKuliahProdiMapping(String basePath) throws IOException {
        Path p = Paths.get(basePath, "matakuliah_prodi.csv");
        List<Map.Entry<String, String>> mappings = new ArrayList<>();
        if (!Files.exists(p)) return mappings;
        List<String> lines = Files.readAllLines(p);
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length < 2) continue;
            mappings.add(new AbstractMap.SimpleEntry<>(parts[0].trim(), parts[1].trim()));
        }
        return mappings;
    }
}
