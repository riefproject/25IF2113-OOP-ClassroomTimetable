package id.ac.polban.service;

import id.ac.polban.model.Jadwal;
import id.ac.polban.model.Kelas;
import id.ac.polban.service.Seed;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class JadwalSearch {
  private JadwalSearch() {}

  public static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");

  public static void searchByKelas(Seed seed, String kodeKelas, String hariArg) {
    Optional<Kelas> opt =
        seed.getKelas().stream()
            .filter(k -> k.getKodeKelas().equalsIgnoreCase(kodeKelas))
            .findFirst();

    if (opt.isEmpty()) {
      System.out.printf("Kelas dengan kode '%s' tidak ditemukan.%n", kodeKelas);
      return;
    }
    Kelas kelas = opt.get();

    List<Jadwal> source = kelas.getDaftarJadwal();
    if (source.isEmpty()) {
      System.out.println("(belum terjadwal)");
      return;
    }

    if (!"all".equalsIgnoreCase(hariArg)) {
      DayOfWeek target = parseHari(hariArg);
      if (target == null) {
        System.out.printf("Hari '%s' tidak dikenali.%n", hariArg);
        return;
      }
      source = source.stream().filter(j -> j.getHari() == target).collect(Collectors.toList());
      if (source.isEmpty()) {
        System.out.printf("(tidak ada jadwal pada hari %s)%n", displayHariId(target));
        return;
      }
    }

    // Header
    System.out.printf("== JADWAL KELAS %s ==%n", kelas.getKodeKelas());
    System.out.printf(
        "%-10s | %-25s | %-30s | %-13s | %-15s%n", "Hari", "Matkul", "Dosen", "Waktu", "Ruang");
    System.out.println(
        "------------------------------------------------------------------------------------------");

    // Body
    source.stream()
        .sorted(Comparator.comparing(Jadwal::getHari).thenComparing(Jadwal::getJamMulai))
        .forEach(
            j -> {
              String waktu = HM.format(j.getJamMulai()) + "-" + HM.format(j.getJamSelesai());
              System.out.printf(
                  "%-10s | %-25s | %-30s | %-13s | %-15s%n",
                  displayHariId(j.getHari()),
                  j.getMataKuliah().getNamaMk(),
                  j.getDosen().getNama(), // <- diambil dari Jadwal
                  waktu,
                  j.getRuangan());
            });
  }

  private static DayOfWeek parseHari(String s) {
    if (s == null) return null;
    String x = s.trim().toLowerCase(Locale.ROOT);
    switch (x) {
      case "senin":
        return DayOfWeek.MONDAY;
      case "selasa":
        return DayOfWeek.TUESDAY;
      case "rabu":
        return DayOfWeek.WEDNESDAY;
      case "kamis":
        return DayOfWeek.THURSDAY;
      case "jumat":
      case "jum'at":
        return DayOfWeek.FRIDAY;
      case "sabtu":
        return DayOfWeek.SATURDAY;
      case "minggu":
      case "ahad":
        return DayOfWeek.SUNDAY;
    }
    try {
      return DayOfWeek.valueOf(x.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException ignored) {
    }
    return null;
  }

  public static String displayHariId(DayOfWeek d) {
    switch (d) {
      case MONDAY:
        return "Senin";
      case TUESDAY:
        return "Selasa";
      case WEDNESDAY:
        return "Rabu";
      case THURSDAY:
        return "Kamis";
      case FRIDAY:
        return "Jumat";
      case SATURDAY:
        return "Sabtu";
      case SUNDAY:
        return "Minggu";
    }
    return d.name();
  }
}
