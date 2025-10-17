package id.ac.polban.service;

import id.ac.polban.model.Jadwal;
import id.ac.polban.model.Kelas;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class JadwalSearch {
    private JadwalSearch() {}

    public static List<Jadwal> searchByKelas(Seed seed, String kodeKelas, String hariArg) {
        Optional<Kelas> opt = seed.getKelas().stream()
                .filter(k -> k.getCode().equalsIgnoreCase(kodeKelas))
                .findFirst();

        if (opt.isEmpty()) {
            System.out.printf("Kelas dengan kode '%s' tidak ditemukan.%n", kodeKelas);
            return Collections.emptyList(); // kembalikan list kosong
        }
        Kelas kelas = opt.get();

        List<Jadwal> source = kelas.getJadwalList();
        if (source.isEmpty()) {
            return Collections.emptyList();
        }

        // filter berdasarkan hari
        if (!"all".equalsIgnoreCase(hariArg)) {
            DayOfWeek target = parseHari(hariArg);
            if (target == null) {
                System.out.printf("Hari '%s' tidak dikenali.%n", hariArg);
                return Collections.emptyList();
            }
            source = source.stream().filter(j -> j.getDay() == target).collect(Collectors.toList());
        }

        // urutkan dan kembalikan hasilnya
        return source.stream()
                .sorted(Comparator.comparing(Jadwal::getDay).thenComparing(Jadwal::getStartTime))
                .collect(Collectors.toList());
    }

    private static DayOfWeek parseHari(String s) {
        if (s == null) return null;
        String x = s.trim().toLowerCase(Locale.ROOT);
        switch (x) {
            case "senin": return DayOfWeek.MONDAY;
            case "selasa": return DayOfWeek.TUESDAY;
            case "rabu": return DayOfWeek.WEDNESDAY;
            case "kamis": return DayOfWeek.THURSDAY;
            case "jumat": case "jum'at": return DayOfWeek.FRIDAY;
            case "sabtu": return DayOfWeek.SATURDAY;
            case "minggu": case "ahad": return DayOfWeek.SUNDAY;
        }
        try {
            return DayOfWeek.valueOf(x.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {}
        return null;
    }
}
