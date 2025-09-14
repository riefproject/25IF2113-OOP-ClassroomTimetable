package id.ac.polban.app;

import id.ac.polban.model.*;
import id.ac.polban.model.CourseType;
import id.ac.polban.service.JadwalSearch;
import id.ac.polban.service.Seed;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MenuApp {
    private final Seed data;
    private final Scanner scanner;

    public MenuApp(Seed data) {
        this.data = data;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            displayMainMenu();
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("0") || input.equalsIgnoreCase("exit")) {
                System.out.println("Terima kasih telah menggunakan aplikasi. Sampai jumpa!");
                break;
            }
            handleMainMenu(input);
        }
        scanner.close();
    }

    private void displayMainMenu() {
        System.out.println("\n=================================================");
        System.out.println("   Sistem Informasi Akademik Politeknik Bandung");
        System.out.println("=================================================");
        System.out.println("Menu Utama:");
        System.out.println("1. Tampilkan Mahasiswa");
        System.out.println("2. Tampilkan Dosen");
        System.out.println("3. Tampilkan Mata Kuliah");
        System.out.println("4. Tampilkan Jadwal Kuliah");
        System.out.println("0. Keluar");
        System.out.println("-------------------------------------------------");
        System.out.print("Pilih menu (bisa dengan shorthand, cth: '1 JTK D3-TI'): ");
    }

    private void handleMainMenu(String input) {
        if (input.isEmpty()) {
            System.out.println("Input tidak boleh kosong.");
            return;
        }

        String[] parts = input.split("\\s+");
        String command = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        switch (command) {
            case "1":
                navigateToMahasiswa(args);
                break;
            case "2":
                navigateToDosen(args);
                break;
            case "3":
                navigateToMataKuliah(args);
                break;
            case "4":
                navigateToJadwal(args);
                break;
            default:
                System.out.println("Pilihan menu tidak valid. Silakan coba lagi.");
                break;
        }
    }

    // ALUR NAVIGASI

    private void navigateToMahasiswa(String[] args) {
        // Pilih Jurusan
        Jurusan jurusan = selectionPrompt(
                "Jurusan",
                data.getJurusan(),
                Jurusan::getNama,
                Jurusan::getAlias,
                args, 0
        );
        if (jurusan == null) return;

        // Pilih Program Studi
        Prodi prodi = selectionPrompt(
                "Program Studi",
                jurusan.getDaftarProdi(),
                p -> p.getJenjang() + " " + p.getNama(),
                Prodi::getAlias,
                args, 1
        );
        if (prodi == null) return;

        // Pilih Kelas
        Kelas kelas = selectionPrompt(
                "Kelas",
                prodi.getDaftarKelas(),
                Kelas::getKodeKelas,
                null,
                args, 2
        );
        if (kelas == null) return;

        // Tampilkan data mahasiswa
        printMahasiswa(kelas);
    }

    private void navigateToJadwal(String[] args) {
        // Pilih Jurusan
        Jurusan jurusan = selectionPrompt(
                "Jurusan",
                data.getJurusan(),
                Jurusan::getNama,
                Jurusan::getAlias,
                args, 0
        );
        if (jurusan == null) return;

        // Pilih Program Studi
        Prodi prodi = selectionPrompt(
                "Program Studi",
                jurusan.getDaftarProdi(),
                p -> p.getJenjang() + " " + p.getNama(),
                Prodi::getAlias,
                args, 1
        );
        if (prodi == null) return;

        // Pilih Kelas
        Kelas kelas = selectionPrompt(
                "Kelas",
                prodi.getDaftarKelas(),
                Kelas::getKodeKelas,
                null,
                args, 2
        );
        if (kelas == null) return;

        // Tampilkan jadwal
        JadwalSearch.searchByKelas(data, kelas.getKodeKelas(), "all");
        waitForEnter();
    }

    private void navigateToDosen(String[] args) {
        // Pilih Jurusan
        Jurusan jurusan = selectionPrompt(
                "Jurusan",
                data.getJurusan(),
                Jurusan::getNama,
                Jurusan::getAlias,
                args, 0
        );
        if (jurusan == null) return;

        // Pilih Program Studi 
        Prodi prodi = selectionPrompt(
                "Program Studi",
                jurusan.getDaftarProdi(),
                p -> p.getJenjang() + " " + p.getNama(),
                Prodi::getAlias,
                args, 1
        );
        if (prodi == null) return;

        // Tampilkan daftar dosen
        Set<Dosen> uniqueDosen = new HashSet<>();
        prodi.getDaftarMataKuliah().forEach(mk -> uniqueDosen.addAll(mk.getPengampu()));
        printDosen(new ArrayList<>(uniqueDosen));
    }

    private void navigateToMataKuliah(String[] args) {
        // Pilih Jurusan
        Jurusan jurusan = selectionPrompt(
                "Jurusan",
                data.getJurusan(),
                Jurusan::getNama,
                Jurusan::getAlias,
                args, 0
        );
        if (jurusan == null) return;

        // Pilih Program Studi
        Prodi prodi = selectionPrompt(
                "Program Studi",
                jurusan.getDaftarProdi(),
                p -> p.getJenjang() + " " + p.getNama(),
                Prodi::getAlias,
                args, 1
        );
        if (prodi == null) return;

        // Tampilkan mata kuliah
        List<MataKuliah> filteredMataKuliah = data.getMataKuliah().stream()
                .filter(mk -> mk.getTipeMataKuliah() == CourseType.UMUM || mk.getDaftarProdi().contains(prodi))
                .collect(Collectors.toList());
        printMataKuliah(filteredMataKuliah, prodi);
    }


    // HELPER UNTUK PEMILIHAN MENU 

    private <T> T selectionPrompt(String entityName, List<T> items, Function<T, String> nameExtractor, Function<T, String> aliasExtractor, String[] args, int argIndex) {
        // Cek shorthand
        if (argIndex < args.length) {
            String shorthand = args[argIndex];
            Optional<T> found = items.stream()
                    .filter(item -> {
                        boolean nameMatch = nameExtractor.apply(item).equalsIgnoreCase(shorthand);
                        boolean aliasMatch = aliasExtractor != null && aliasExtractor.apply(item).equalsIgnoreCase(shorthand);
                        return nameMatch || aliasMatch;
                    })
                    .findFirst();
            if (found.isPresent()) {
                return found.get();
            }
            System.out.printf("Shorthand '%s' untuk %s tidak ditemukan. Silakan pilih manual.%n", shorthand, entityName);
        }

        // Pemilihan manual
        while (true) {
            System.out.printf("\n--- Pilih %s ---\n", entityName);
            if (items.isEmpty()) {
                System.out.printf("Tidak ada data %s yang tersedia.%n", entityName);
                waitForEnter();
                return null;
            }

            for (int i = 0; i < items.size(); i++) {
                String name = nameExtractor.apply(items.get(i));
                String alias = aliasExtractor != null ? " (" + aliasExtractor.apply(items.get(i)) + ")" : "";
                System.out.printf("%d. %s%s%n", i + 1, name, alias);
            }
            System.out.println("0. Kembali");
            System.out.print("Pilih nomor atau ketik nama/alias: ");

            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("0") || input.equalsIgnoreCase("kembali")) {
                return null;
            }

            // Coba parsing nomor
            try {
                int choice = Integer.parseInt(input);
                if (choice > 0 && choice <= items.size()) {
                    return items.get(choice - 1);
                }
            } catch (NumberFormatException e) {
                // Bukan nomor
                String shorthand = input;
                Optional<T> found = items.stream()
                        .filter(item -> {
                            boolean nameMatch = nameExtractor.apply(item).equalsIgnoreCase(shorthand);
                            boolean aliasMatch = aliasExtractor != null && aliasExtractor.apply(item).equalsIgnoreCase(shorthand);
                            return nameMatch || aliasMatch;
                        })
                        .findFirst();

                if (found.isPresent()) {
                    return found.get();
                } else {
                    System.out.printf("Input '%s' tidak dikenali sebagai nomor atau %s. Coba lagi.%n", shorthand, entityName);
                }
            }
        }
    }

    // FUNGSI TAMPILAN DATA

    private void printMahasiswa(Kelas kelas) {
        List<Mahasiswa> mahasiswaList = kelas.getDaftarMahasiswa();
        System.out.printf("\n--- Daftar Mahasiswa Kelas %s ---\n", kelas.getKodeKelas());
        if (mahasiswaList.isEmpty()) {
            System.out.println("Tidak ada mahasiswa di kelas ini.");
        } else {
            System.out.println("-------------------------------------------------");
            System.out.printf("| %-12s | %-30s |%n", "NIM", "Nama Mahasiswa");
            System.out.println("-------------------------------------------------");
            mahasiswaList.stream()
                    .sorted(Comparator.comparing(Mahasiswa::getNim))
                    .forEach(m -> System.out.printf("| %-12s | %-30s |%n", m.getNim(), m.getNama()));
            System.out.println("-------------------------------------------------");
            System.out.println("Total: " + mahasiswaList.size() + " mahasiswa.");
        }
        waitForEnter();
    }

    private void printDosen(List<Dosen> dosenList) {
        System.out.printf("\n--- Daftar Dosen ---\n");
        if (dosenList.isEmpty()) {
            System.out.println("Tidak ada dosen yang mengajar mata kuliah di program studi ini.");
        } else {
            System.out.println("-------------------------------------------------");
            System.out.printf("| %-15s | %-27s |%n", "NIP", "Nama Dosen");
            System.out.println("-------------------------------------------------");
            dosenList.stream()
                    .sorted(Comparator.comparing(Dosen::getKodeDosen))
                    .forEach(d -> System.out.printf("| %-15s | %-27s |%n", d.getKodeDosen(), d.getNama()));
            System.out.println("-------------------------------------------------");
            System.out.println("Total: " + dosenList.size() + " dosen.");
        }
        waitForEnter();
    }

    private void printMataKuliah(List<MataKuliah> mataKuliahList, Prodi selectedProdi) {
        System.out.printf("\n--- Daftar Mata Kuliah untuk Program Studi %s %s ---\n", selectedProdi.getJenjang(), selectedProdi.getNama());
        if (mataKuliahList.isEmpty()) {
            System.out.println("Tidak ada mata kuliah yang terkait dengan program studi ini.");
        } else {
            System.out.println("-----------------------------------------------------------------------------------------------------------------");
            System.out.printf("| %-10s | %-40s | %-5s | %-10s | %-25s |\n", "Kode", "Nama Mata Kuliah", "SKS", "Tipe", "Prodi Terkait");
            System.out.println("-----------------------------------------------------------------------------------------------------------------");
            mataKuliahList.stream()
                    .sorted(Comparator.comparing(MataKuliah::getKode))
                    .forEach(mk -> {
                        String prodiTerikat = mk.getDaftarProdi().stream()
                                .map(Prodi::getAlias)
                                .collect(Collectors.joining(", "));
                        if (mk.getTipeMataKuliah() == CourseType.UMUM) {
                            prodiTerikat = "UMUM";
                        } else if (prodiTerikat.isEmpty()) {
                            prodiTerikat = "-";
                        }
                        System.out.printf("| %-10s | %-40s | %-5d | %-10s | %-25s |\n", mk.getKode(), mk.getNama(), mk.getSks(), mk.getTipeMataKuliah(), prodiTerikat);
                    });
            System.out.println("-----------------------------------------------------------------------------------------------------------------");
            System.out.println("Total: " + mataKuliahList.size() + " mata kuliah.");
        }
        waitForEnter();
    }

    private void waitForEnter() {
        System.out.print("\nTekan Enter untuk melanjutkan...");
        scanner.nextLine();
    }
}