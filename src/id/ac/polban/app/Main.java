package id.ac.polban.app;

import id.ac.polban.model.Dosen;
import id.ac.polban.model.Kelas;
import id.ac.polban.service.CsvDataSeeder;
import id.ac.polban.service.DataSeeder;
import id.ac.polban.service.JadwalSearch;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    

    public static void main(String[] args) {
        DataSeeder.Seed seed;
        try {
            seed = CsvDataSeeder.seedFromCsvs("data");
        } catch (IOException e) {
            System.err.println("Gagal membaca data dari CSV: " + e.getMessage());
            e.printStackTrace();
            return; // Keluar dari aplikasi jika gagal membaca data
        }
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
        System.out.println("\n\n=== DAFTAR DOSEN & MATA KULIAH DIAMPU ===");
        System.out.println("----------------------------------------------------------------------------------");
        System.out.printf("| %-12s | %-26s | %-34s |%n", "KODE DOSEN", "NAMA DOSEN", "EMAIL");
        if(seed.getDosen().isEmpty())
            System.out.println("----------------------------------------------------------------------------------");
        for (Dosen d : seed.getDosen()) {
        System.out.println("----------------------------------------------------------------------------------");
            System.out.printf("| %-12s | %-26s | %-34s |%n", d.getKodeDosen(), d.getNama(), d.getEmail());
                System.out.println("----------------------------------------------------------------------------------");
            if (!d.getMataKuliahDiampu().isEmpty()) {
                System.out.println("  Mata Kuliah Diampu:");
                System.out.println("    -------------------------------------------------");
                System.out.printf("    | %-10s | %-26s | %-3s |%n", "KODE MK", "NAMA MATA KULIAH", "SKS");
                System.out.println("    -------------------------------------------------");
                d.getMataKuliahDiampu().forEach(mk ->
                        System.out.printf("    | %-10s | %-26s | %-3d |%n", mk.getKodeMk(), mk.getNamaMk(), mk.getSks())
                );
                System.out.println("    -------------------------------------------------\n");
            }
        }
        System.out.println("----------------------------------------------------------------------------------\n\n");

        System.out.println("\n\n=== DAFTAR KELAS & MAHASISWA ===");
        System.out.println("-------------------------------------------");
        System.out.printf("| %-10s | %-26s |%n", "KODE KELAS", "JUMLAH MAHASISWA");
        if(seed.getKelas().isEmpty())
            System.out.println("-------------------------------------------");
        for (Kelas k : seed.getKelas()) {
            System.out.println("-------------------------------------------");
            System.out.printf("| %-10s | %-26d |%n", k.getKodeKelas(), k.getDaftarMahasiswa().size());
            if (k.getDaftarMahasiswa().isEmpty()) continue;

            System.out.println("-------------------------------------------");
            System.out.println("  Mahasiswa:");
            System.out.println("    -------------------------------------------");
            System.out.printf("    | %-10s | %-26s |%n", "NIM", "NAMA MAHASISWA");
            System.out.println("    -------------------------------------------");
            k.getDaftarMahasiswa().forEach(m ->
                    System.out.printf("    | %-10s | %-26s |%n", m.getNim(), m.getNama())
            );
            System.out.println("    -------------------------------------------");
        }
        System.out.println("----------------------------------------------------");

        System.out.println("\n\n=== JADWAL PER KELAS ===");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-5s | %-10s | %-25s | %-30s | %-13s | %-5s |%n", "KELAS", "HARI", "MATKUL", "DOSEN", "WAKTU", "RUANG");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        for (Kelas k : seed.getKelas()) {
            if (k.getDaftarJadwal().isEmpty()){
                System.out.printf("Jadwal %-5s: -", k.getKodeKelas());
            };

            k.getDaftarJadwal().forEach(j -> {
                String waktu = JadwalSearch.HM.format(j.getJamMulai()) + "-" + JadwalSearch.HM.format(j.getJamSelesai());
                System.out.printf("| %-5s | %-10s | %-25s | %-30s | %-13s | %-5s |%n",
                        k.getKodeKelas(),
                        JadwalSearch.displayHariId(j.getHari()),
                        j.getMataKuliah().getNamaMk(),
                        j.getDosen().getNama(),
                        waktu,
                        j.getRuangan());
            });
            System.out.println("-----------------------------------------------------------------------------------------------------------");
        }
    }
}
