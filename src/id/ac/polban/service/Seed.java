package id.ac.polban.service;

import id.ac.polban.model.*;
import id.ac.polban.model.CourseType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Seed {
  private final List<Dosen> dosen;
  private final List<MataKuliah> mataKuliah;
  private final List<Kelas> kelas;
  private final List<Mahasiswa> mahasiswa;
  // NEW: opsional organisasi
  private final List<Kampus> kampus;
  private final List<Jurusan> jurusan;
  private final List<Prodi> prodi;

  // Konstruktor lama (kompatibel)
  public Seed(
      List<Dosen> dosen,
      List<MataKuliah> mataKuliah,
      List<Kelas> kelas,
      List<Mahasiswa> mahasiswa) {
    this(dosen, mataKuliah, kelas, mahasiswa, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
  }

  // Konstruktor lengkap (baru)
  public Seed(
      List<Dosen> dosen,
      List<MataKuliah> mataKuliah,
      List<Kelas> kelas,
      List<Mahasiswa> mahasiswa,
      List<Kampus> kampus,
      List<Jurusan> jurusan,
      List<Prodi> prodi) {
    this.dosen = dosen;
    this.mataKuliah = mataKuliah;
    this.kelas = kelas;
    this.mahasiswa = mahasiswa;
    this.kampus = kampus;
    this.jurusan = jurusan;
    this.prodi = prodi;
  }

  public List<Dosen> getDosen() {
    return dosen;
  }

  public List<MataKuliah> getMataKuliah() {
    return mataKuliah;
  }
  public List<Kelas> getKelas() {
    return kelas;
  }
  public List<Mahasiswa> getMahasiswa() {
    return mahasiswa;
  }
  
  public List<Kampus> getKampus() { return kampus; }
  public List<Jurusan> getJurusan() { return jurusan; }
  public List<Prodi> getProdi() { return prodi; }

  public static Seed loadFromCsvs(String basePath) throws IOException {
    Map<String, Dosen> dosenMap = readDosenCsv(basePath);

    // organisasi akademik
    List<Kampus> kampus = readKampusCsvIfExists(basePath);
    Map<String, Jurusan> jurusanMap = readJurusanCsvIfExists(basePath, kampus);
    Map<String, Prodi> prodiMap = readProdiCsvIfExists(basePath, jurusanMap);

    Map<String, MataKuliah> mataKuliahMap = readMataKuliahCsv(basePath); // Updated call

    List<Map.Entry<String, String>> mataKuliahProdiMappings = readMataKuliahProdiMapping(basePath);
    for (Map.Entry<String, String> entry : mataKuliahProdiMappings) {
      String kodeMk = entry.getKey();
      String kodeProdi = entry.getValue();

      MataKuliah mk = mataKuliahMap.get(kodeMk);
      Prodi prodi = prodiMap.get(kodeProdi);

      if (mk != null && prodi != null) {
        if (mk.getTipeMataKuliah() == CourseType.PRODI_SPECIFIC) {
          prodi.tambahMataKuliah(mk); 
        } else {
          System.err.println("Peringatan: Mata Kuliah '" + kodeMk + "' (tipe UMUM) tidak boleh memiliki asosiasi prodi di matakuliah_prodi.csv. Melewati.");
        }
      } else {
        System.err.println("Peringatan: Mapping Mata Kuliah-Prodi tidak valid (MK: " + kodeMk + ", Prodi: " + kodeProdi + "). Melewati.");
      }
    }

    Map<String, String> kelasProdiMapping = readKelasProdiMapping(basePath);
    Map<String, Kelas> kelasMap = readJadwalCsv(basePath, dosenMap, mataKuliahMap, prodiMap, kelasProdiMapping);

    List<Mahasiswa> mahasiswaList = readMahasiswaCsv(basePath, kelasMap);

    Seed seed = new Seed(
        new ArrayList<>(dosenMap.values()),
        new ArrayList<>(mataKuliahMap.values()),
        new ArrayList<>(kelasMap.values()),
        mahasiswaList,
        kampus,
        new ArrayList<>(jurusanMap.values()),
        new ArrayList<>(prodiMap.values()));

    seed.activateEntities(); // Call the activation logic

    return seed;
  }

  private void activateEntities() {
    boolean changed;
    do {
      changed = false;

        // Mengaktifkan Mahasiswa jika Kelasnya aktif
        for (Mahasiswa m : this.mahasiswa) {
            if (!m.getIsActive() && m.canActivate()) {
                try {
                    m.setIsActive(true);
                    changed = true;
                } catch (IllegalStateException e) {
                    System.err.println("Gagal mengaktifkan Mahasiswa " + m.getNim() + ": " + e.getMessage());
                }
            }
        }

        // Mengaktifkan Kelas jika minimal ada satu Mahasiswa aktif dan Prodinya aktif
        for (Kelas k : this.kelas) {
            if (!k.getIsActive() && k.canActivate()) {
                try {
                    k.setIsActive(true);
                    changed = true;
                } catch (IllegalStateException e) {
                    System.err.println("Gagal mengaktifkan Kelas " + k.getKodeKelas() + ": " + e.getMessage());
                }
            }
        }

        // Mengaktifkan Prodi jika minimal ada satu Kelas aktif dan Jurusannya aktif
        for (Prodi p : this.prodi) {
            if (!p.getIsActive() && p.canActivate()) {
                try {
                    p.setIsActive(true);
                    changed = true;
                } catch (IllegalStateException e) {
                    System.err.println("Gagal mengaktifkan Prodi " + p.getKode() + ": " + e.getMessage());
                }
            }
        }

        // Mengaktifkan Jurusan jika minimal ada satu Prodi aktif dan Kampusnya aktif
        for (Jurusan j : this.jurusan) {
            if (!j.getIsActive() && j.canActivate()) {
                try {
                    j.setIsActive(true);
                    changed = true;
                } catch (IllegalStateException e) {
                    System.err.println("Gagal mengaktifkan Jurusan " + j.getKode() + ": " + e.getMessage());
                }
            }
        }

        // Mengaktifkan Kampus jika minimal ada satu Jurusan aktif
        for (Kampus k : this.kampus) {
            if (!k.getIsActive() && k.canActivate()) {
                try {
                    k.setIsActive(true);
                    changed = true;
                } catch (IllegalStateException e) {
                    System.err.println("Gagal mengaktifkan Kampus " + k.getKode() + ": " + e.getMessage());
                }
            }
        }

        // Mengaktifkan Dosen jika minimal mengajar satu Mata Kuliah aktif
      for (Dosen d : this.dosen) {
        if (!d.getIsActive() && d.canActivate()) {
          try {
            d.setIsActive(true);
            changed = true;
          } catch (IllegalStateException e) {
            System.err.println("Gagal mengaktifkan Dosen " + d.getKodeDosen() + ": " + e.getMessage());
          }
        }
      }

      // Aktifkan MataKuliah jika kriteria cocok (Dosen/Prodi aktif)
      for (MataKuliah mk : this.mataKuliah) {
        if (!mk.getIsActive() && mk.canActivate()) {
          try {
            mk.setIsActive(true);
            changed = true;
          } catch (IllegalStateException e) {
            System.err.println("Gagal mengaktifkan Mata Kuliah " + mk.getKodeMk() + ": " + e.getMessage());
          }
        }
      }

    } while (changed); 
  }

  private static Map<String, Dosen> readDosenCsv(String basePath) throws IOException {
    List<String> lines = Files.readAllLines(Paths.get(basePath, "dosen.csv"));
    Map<String, Dosen> dosenMap = new HashMap<>();
    for (int i = 1; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.trim().isEmpty()) continue;
      try {
        String[] parts = line.split(",");
        if (parts.length < 3) {
          System.err.println(
              "Format baris tidak valid di dosen.csv (baris "
                  + (i + 1)
                  + "): "
                  + line
                  + ". Kolom tidak lengkap.");
          continue;
        }
        Dosen d = new Dosen(parts[0].trim(), parts[1].trim(), parts[2].trim());
        dosenMap.put(d.getKodeDosen(), d);
      } catch (ArrayIndexOutOfBoundsException e) {
        System.err.println(
            "Error parsing dosen.csv (baris "
                + (i + 1)
                + "): "
                + line
                + ". Pastikan format 'kode_dosen,nama,email'.");
      } catch (Exception e) {
        System.err.println(
            "Error tidak terduga di dosen.csv (baris "
                + (i + 1)
                + "): "
                + line
                + ". Error: "
                + e.getMessage());
      }
    }
    return dosenMap;
  }

  private static Map<String, MataKuliah> readMataKuliahCsv(String basePath) throws IOException {
    List<String> lines = Files.readAllLines(Paths.get(basePath, "matakuliah.csv"));
    Map<String, MataKuliah> mataKuliahMap = new HashMap<>();
    for (int i = 1; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.trim().isEmpty()) continue;
      try {
        String[] parts = line.split(",");
        if (parts.length < 4) { // Minimum 4 columns for kode_mk, nama_mk, sks, tipe_mk
          System.err.println(
              "Format baris tidak valid di matakuliah.csv (baris "
                  + (i + 1)
                  + "): "
                  + line
                  + ". Kolom tipe_mk tidak ditemukan.");
          continue;
        }
        String kodeMk = parts[0].trim();
        String namaMk = parts[1].trim();
        int sks = Integer.parseInt(parts[2].trim());
        CourseType tipeMataKuliah = CourseType.valueOf(parts[3].trim().toUpperCase());

        MataKuliah mk = new MataKuliah(kodeMk, namaMk, sks, tipeMataKuliah);
        mataKuliahMap.put(mk.getKodeMk(), mk);
      } catch (NumberFormatException e) {
        System.err.println(
            "Error parsing SKS di matakuliah.csv (baris "
                + (i + 1)
                + "): "
                + line
                + ". Pastikan SKS adalah angka.");
      } catch (IllegalArgumentException e) {
        System.err.println(
            "Error parsing tipe mata kuliah di matakuliah.csv (baris "
                + (i + 1)
                + "): "
                + line
                + ". Pastikan tipe adalah UMUM atau PRODI_SPECIFIC. Error: " + e.getMessage());
      } catch (ArrayIndexOutOfBoundsException e) {
        System.err.println(
            "Error parsing matakuliah.csv (baris "
                + (i + 1)
                + "): "
                + line
                + ". Pastikan format 'kode_mk,nama_mk,sks,tipe_mk'.");
      } catch (Exception e) {
        System.err.println(
            "Error tidak terduga di matakuliah.csv (baris "
                + (i + 1)
                + "): "
                + line
                + ". Error: "
                + e.getMessage());
      }
    }
    return mataKuliahMap;
  }

  private static Map<String, Kelas> readJadwalCsv(
      String basePath, Map<String, Dosen> dosenMap, Map<String, MataKuliah> mataKuliahMap, Map<String, Prodi> prodiMap, Map<String, String> kelasProdiMapping)
      throws IOException {
    Map<String, Kelas> kelasMap = new HashMap<>();
    List<String> lines = Files.readAllLines(Paths.get(basePath, "jadwal.csv"));

    for (int i = 1; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.trim().isEmpty()) continue;
      try {
        String[] parts = line.split(",");
        if (parts.length < 7) {
          System.err.println(
              "Format baris tidak valid di jadwal.csv (baris "
                  + (i + 1)
                  + "): "
                  + line
                  + ". Kolom tidak lengkap.");
          continue;
        }
        String kodeKelas = parts[0].trim();
        String kodeMk = parts[1].trim();
        String kodeDosen = parts[2].trim();
        String hariStr = parts[3].trim();
        String jamMulaiStr = parts[4].trim();
        String jamSelesaiStr = parts[5].trim();
        String ruangan = parts[6].trim();

        MataKuliah mk = mataKuliahMap.get(kodeMk);
        Dosen dosen = dosenMap.get(kodeDosen);

        String kodeProdi = kelasProdiMapping.get(kodeKelas);
        Prodi prodi = null;
        if (kodeProdi != null) {
            prodi = prodiMap.get(kodeProdi);
        }

        if (mk == null || dosen == null || prodi == null) {
            System.err.println(
                "Peringatan: Data tidak valid di jadwal.csv (baris "
                    + (i + 1)
                    + "): "
                    + line
                    + ". Mata Kuliah, Dosen, atau Prodi tidak ditemukan. Kelas ini tidak akan ditambahkan.");
            continue; 
        }

        // Memastikan Prodi not null sebelum bikin Kelas
        Prodi finalProdi = prodi;
        Kelas kelas = kelasMap.computeIfAbsent(kodeKelas, k -> {
            Kelas newKelas = new Kelas(k, finalProdi);
            finalProdi.tambahKelas(newKelas);
            return newKelas;
        });

        DayOfWeek hari = DayOfWeek.valueOf(hariStr.toUpperCase());
        LocalTime jamMulai = LocalTime.parse(jamMulaiStr);
        LocalTime jamSelesai = LocalTime.parse(jamSelesaiStr);

        new Jadwal(kelas, mk, dosen, hari, jamMulai, jamSelesai, ruangan);
        dosen.ampu(mk);
      } catch (IllegalArgumentException e) {
        System.err.println(
            "Error parsing waktu/hari di jadwal.csv (baris "
                + (i + 1)
                + "): "
                + line
                + ". Pastikan format hari (MONDAY) dan waktu (HH:mm) benar.");
      } catch (ArrayIndexOutOfBoundsException e) {
        System.err.println(
            "Error parsing jadwal.csv (baris "
                + (i + 1)
                + "): "
                + line
                + ". Pastikan format 'kode_kelas,kode_mk,kode_dosen,hari,jam_mulai,jam_selesai,ruangan'.");
      } catch (Exception e) {
        System.err.println(
            "Error tidak terduga di jadwal.csv (baris "
                + (i + 1)
                + "): "
                + line
                + ". Error: "
                + e.getMessage());
      }
    }
    return kelasMap;
  }

  private static List<Mahasiswa> readMahasiswaCsv(String basePath, Map<String, Kelas> kelasMap)
      throws IOException {
    List<String> lines = Files.readAllLines(Paths.get(basePath, "mahasiswa.csv"));
    List<Mahasiswa> allMahasiswa = new ArrayList<>();

    for (int i = 1; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.trim().isEmpty()) continue;
      try {
        String[] parts = line.split(",");
        if (parts.length < 3) {
          System.err.println(
              "Format baris tidak valid di mahasiswa.csv (baris "
                  + (i + 1)
                  + "): "
                  + line
                  + ". Kolom tidak lengkap.");
          continue;
        }
        String nim = parts[0].trim();
        String nama = parts[1].trim();
        String kodeKelas = parts[2].trim();

        Kelas kelas = kelasMap.get(kodeKelas);

        if (kelas == null) {
          System.err.println(
              "Peringatan: Kelas dengan kode '"
                  + kodeKelas
                  + "' tidak ditemukan untuk mahasiswa "
                  + nama
                  + " (baris "
                  + (i + 1)
                  + "). Mahasiswa ini tidak akan ditambahkan.");
          continue; // Skip buat Mahasiswa jika Kelas is not found
        }

        Mahasiswa m = new Mahasiswa(nim, nama, kelas);
        allMahasiswa.add(m);
        kelas.tambahMahasiswa(m);
      } catch (ArrayIndexOutOfBoundsException e) {
        System.err.println(
            "Error parsing mahasiswa.csv (baris "
                + (i + 1)
                + "): "
                + line
                + ". Pastikan format 'nim,nama,kode_kelas'.");
      } catch (Exception e) {
        System.err.println(
            "Error tidak terduga di mahasiswa.csv (baris "
                + (i + 1)
                + "): "
                + line
                + ". Error: "
                + e.getMessage());
      }
    }
    return allMahasiswa;
  }
  
  // ================= CSV ORGANISASI =================
  private static List<Kampus> readKampusCsvIfExists(String basePath) throws IOException {
    Path p = Paths.get(basePath, "kampus.csv");
    List<Kampus> list = new ArrayList<>();
    if (!Files.exists(p)) return list;
    List<String> lines = Files.readAllLines(p);
    for (int i = 1; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.trim().isEmpty()) continue;
      String[] parts = line.split(",");
      if (parts.length < 3) {
        System.err.println("Format baris tidak valid di kampus.csv (baris " + (i+1) + "): " + line);
        continue;
      }
      String kode = parts[0].trim();
      String nama = parts[1].trim();
      String alias = parts[2].trim();
      list.add(new Kampus(kode, nama, alias));
    }
    return list;
  }

  private static Map<String, Jurusan> readJurusanCsvIfExists(String basePath, List<Kampus> kampusList) throws IOException {
    Path p = Paths.get(basePath, "jurusan.csv");
    Map<String, Jurusan> map = new HashMap<>();
    if (!Files.exists(p)) return map;
    Map<String, Kampus> kampusMap = new HashMap<>();
    for (Kampus k : kampusList) kampusMap.put(k.getKode(), k);
    List<String> lines = Files.readAllLines(p);
    for (int i = 1; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.trim().isEmpty()) continue;
      String[] parts = line.split(",");
      if (parts.length < 4) {
        System.err.println("Format baris tidak valid di jurusan.csv (baris " + (i+1) + "): " + line);
        continue;
      }
      String kode = parts[0].trim();
      String nama = parts[1].trim();
      String alias = parts[2].trim();
      String kodeKampus = parts[3].trim();
      Kampus k = kampusMap.get(kodeKampus);
      if (k == null) {
        System.err.println("Peringatan: Kampus dengan kode '" + kodeKampus + "' tidak ditemukan untuk jurusan " + nama + " (baris " + (i+1) + "). Jurusan ini tidak akan ditambahkan.");
        continue; // Skip buat Jurusan jika Kampus is not found
      }
      Jurusan j = new Jurusan(kode, nama, alias, k);
      k.tambahJurusan(j);
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
      String line = lines.get(i);
      if (line.trim().isEmpty()) continue;
      String[] parts = line.split(",");
      if (parts.length < 5) {
        System.err.println("Format baris tidak valid di prodi.csv (baris " + (i+1) + "): " + line);
        continue;
      }
      String kode = parts[0].trim();
      String nama = parts[1].trim();
      String alias = parts[2].trim();
      String jenjang = parts[3].trim();
      String kodeJurusan = parts[4].trim();
      Jurusan j = jurusanMap.get(kodeJurusan);
      if (j == null) {
        System.err.println("Peringatan: Jurusan dengan kode '" + kodeJurusan + "' tidak ditemukan untuk prodi " + nama + " (baris " + (i+1) + "). Prodi ini tidak akan ditambahkan.");
        continue; // Skip load Prodi jika Jurusan is not found
      }
      Prodi pr = new Prodi(kode, nama, alias, jenjang);
      j.tambahProdi(pr);
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
      String line = lines.get(i);
      if (line.trim().isEmpty()) continue;
      String[] parts = line.split(",");
      if (parts.length < 2) {
        System.err.println("Format baris tidak valid di kelas_prodi.csv (baris " + (i+1) + "): " + line);
        continue;
      }
      String kodeKelas = parts[0].trim();
      String kodeProdi = parts[1].trim();
      map.put(kodeKelas, kodeProdi);
    }
    return map;
  }

  private static List<Map.Entry<String, String>> readMataKuliahProdiMapping(String basePath) throws IOException {
    Path p = Paths.get(basePath, "matakuliah_prodi.csv");
    List<Map.Entry<String, String>> mappings = new ArrayList<>();
    if (!Files.exists(p)) return mappings;

    List<String> lines = Files.readAllLines(p);
    for (int i = 1; i < lines.size(); i++) { // Skip header
      String line = lines.get(i);
      if (line.trim().isEmpty()) continue;
      String[] parts = line.split(",");
      if (parts.length < 2) {
        System.err.println("Format baris tidak valid di matakuliah_prodi.csv (baris " + (i+1) + "): " + line);
        continue;
      }
      String kodeMk = parts[0].trim();
      String kodeProdi = parts[1].trim();
      mappings.add(new AbstractMap.SimpleEntry<>(kodeMk, kodeProdi));
    }
    return mappings;
  }
}