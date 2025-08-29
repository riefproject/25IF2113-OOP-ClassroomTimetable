package jadwalKelas.app;

import jadwalKelas.model.*;

import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {
        DataSeeder.Seed seed = DataSeeder.seed();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== MENU JADWAL KELAS ===");
            System.out.println("1) Cari jadwal by Kelas");
            System.out.println("2) Lihat daftar kode Kelas");
            System.out.println("3) Ringkasan data (Dosen, Kelas, Jadwal)");
            System.out.println("0) Exit");
            System.out.print("Pilih: ");

            String pilih = sc.nextLine().trim();
            if (pilih.equals("0")) {
                System.out.println("Bye!");
                break;
            }

            switch (pilih) {
                case "1": {
                    System.out.print("Masukkan kode kelas (contoh: D3-2A): ");
                    String kode = sc.nextLine().trim();
                    if (kode.isEmpty()) {
                        System.out.println("Kode kelas tidak boleh kosong.");
                        break;
                    }
                    System.out.print("Filter hari? ketik 'all' atau nama hari: ");
                    String hariArg = sc.nextLine().trim();
                    if (hariArg.isEmpty()) hariArg = "all";

                    JadwalSearch.searchByKelas(seed, kode, hariArg);
                    break;
                }
                case "2": {
                    System.out.println("== DAFTAR KODE KELAS TERSEDIA ==");
                    for (Kelas k : seed.getKelas()) {
                        System.out.printf("- %s | %d mahasiswa | %d jadwal%n",
                                k.getKodeKelas(),
                                k.getDaftarMahasiswa().size(),
                                k.getDaftarJadwal().size());
                    }
                    break;
                }
                case "3": {
                    printRingkasan(seed);
                    break;
                }
                default:
                    System.out.println("Input tidak dikenali. Pilih 0/1/2/3.");
            }
        }

        sc.close();
    }

    private static void printRingkasan(DataSeeder.Seed seed) {
        System.out.println("\n=== DAFTAR DOSEN & MATA KULIAH DIAMPU ===");
        for (Dosen d : seed.getDosen()) {
            System.out.printf("- %s (%s)%n", d.getNama(), d.getNip());
            d.getMataKuliahDiampu().forEach(mk ->
                    System.out.printf("    • %s [%s] (%d SKS)%n", mk.getNamaMk(), mk.getKodeMk(), mk.getSks())
            );
        }

        System.out.println("\n=== DAFTAR KELAS & MAHASISWA ===");
        for (Kelas k : seed.getKelas()) {
            System.out.printf("- Kelas %s%n", k.getKodeKelas());
            System.out.println("  Mahasiswa:");
            if (k.getDaftarMahasiswa().isEmpty()) {
                System.out.println("    (belum ada)");
            } else {
                k.getDaftarMahasiswa().forEach(m ->
                        System.out.printf("    • %s (%s)%n", m.getNama(), m.getNim())
                );
            }
        }

        System.out.println("\n=== JADWAL PER KELAS (Matkul | Dosen | Waktu @ Ruang) ===");
        for (Kelas k : seed.getKelas()) {
            System.out.printf("- %s%n", k.getKodeKelas());
            if (k.getDaftarJadwal().isEmpty()) {
                System.out.println("    (belum terjadwal)");
            } else {
                k.getDaftarJadwal().forEach(j -> {
                    String waktu = HM.format(j.getJamMulai()) + "-" + HM.format(j.getJamSelesai());
                    System.out.printf("    • %s | %s | %s @ %s%n",
                            j.getMataKuliah().getNamaMk(),
                            j.getDosen().getNama(),
                            waktu,
                            j.getRuangan());
                });
            }
        }
    }
}
