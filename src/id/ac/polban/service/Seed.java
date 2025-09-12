package id.ac.polban.service;

import id.ac.polban.model.Dosen;
import id.ac.polban.model.Jadwal;
import id.ac.polban.model.Kelas;
import id.ac.polban.model.Mahasiswa;
import id.ac.polban.model.MataKuliah;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Seed {
  private final List<Dosen> dosen;
  private final List<MataKuliah> mataKuliah;
  private final List<Kelas> kelas;
  private final List<Mahasiswa> mahasiswa;

  public Seed(
      List<Dosen> dosen,
      List<MataKuliah> mataKuliah,
      List<Kelas> kelas,
      List<Mahasiswa> mahasiswa) {
    this.dosen = dosen;
    this.mataKuliah = mataKuliah;
    this.kelas = kelas;
    this.mahasiswa = mahasiswa;
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

  public static Seed loadFromCsvs(String basePath) throws IOException {
    Map<String, Dosen> dosenMap = readDosenCsv(basePath);
    Map<String, MataKuliah> mataKuliahMap = readMataKuliahCsv(basePath);
    Map<String, Kelas> kelasMap = readJadwalCsv(basePath, dosenMap, mataKuliahMap);
    List<Mahasiswa> mahasiswaList = readMahasiswaCsv(basePath, kelasMap);

    return new Seed(
        new ArrayList<>(dosenMap.values()),
        new ArrayList<>(mataKuliahMap.values()),
        new ArrayList<>(kelasMap.values()),
        mahasiswaList);
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
        if (parts.length < 3) {
          System.err.println(
              "Format baris tidak valid di matakuliah.csv (baris "
                  + (i + 1)
                  + "): "
                  + line
                  + ". Kolom tidak lengkap.");
          continue;
        }
        int sks = Integer.parseInt(parts[2].trim());
        MataKuliah mk = new MataKuliah(parts[0].trim(), parts[1].trim(), sks);
        mataKuliahMap.put(mk.getKodeMk(), mk);
      } catch (NumberFormatException e) {
        System.err.println(
            "Error parsing SKS di matakuliah.csv (baris "
                + (i + 1)
                + "): "
                + line
                + ". Pastikan SKS adalah angka.");
      } catch (ArrayIndexOutOfBoundsException e) {
        System.err.println(
            "Error parsing matakuliah.csv (baris "
                + (i + 1)
                + "): "
                + line
                + ". Pastikan format 'kode_mk,nama_mk,sks'.");
      }
    }
    return mataKuliahMap;
  }

  private static Map<String, Kelas> readJadwalCsv(
      String basePath, Map<String, Dosen> dosenMap, Map<String, MataKuliah> mataKuliahMap)
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

        Kelas kelas = kelasMap.computeIfAbsent(kodeKelas, k -> new Kelas(k));

        DayOfWeek hari = DayOfWeek.valueOf(hariStr.toUpperCase());
        LocalTime jamMulai = LocalTime.parse(jamMulaiStr);
        LocalTime jamSelesai = LocalTime.parse(jamSelesaiStr);

        if (mk == null || dosen == null) {
          System.err.println(
              "Data tidak valid di jadwal.csv (baris "
                  + (i + 1)
                  + "): "
                  + line
                  + ". Mata Kuliah atau Dosen tidak ditemukan.");
          continue;
        }

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

        Mahasiswa m = new Mahasiswa(nim, nama, null);
        allMahasiswa.add(m);

        if (kelas != null) {
          kelas.tambahMahasiswa(m);
        } else {
          System.err.println(
              "Kelas dengan kode '"
                  + kodeKelas
                  + "' tidak ditemukan untuk mahasiswa "
                  + nama
                  + " (baris "
                  + (i + 1)
                  + "). Melewati penambahan ke kelas.");
        }
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
}
