package id.ac.polban.app;

import id.ac.polban.contract.Displayable;
import id.ac.polban.model.*;
import id.ac.polban.service.JadwalSearch;
import id.ac.polban.service.Seed;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MenuApp {
    private final Seed data;
    private final Scanner scanner;
    private final Map<String, Consumer<String[]>> menuActions;

    public MenuApp(Seed data) {
        this.data = data;
        this.scanner = new Scanner(System.in);
        this.menuActions = new HashMap<>();
        initializeMenuActions();
    }


    private void initializeMenuActions() {
        menuActions.put("1", this::navigateToMahasiswa);
        menuActions.put("2", this::navigateToDosen);
        menuActions.put("3", this::navigateToMataKuliah);
        menuActions.put("4", this::navigateToJadwal);
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
        Consumer<String[]> action = menuActions.get(command);
        if (action != null) {
            action.accept(args);
        } else {
            System.out.println("Pilihan menu tidak valid. Silakan coba lagi.");
        }
    }

    // --- ALUR NAVIGASI ---

    private void navigateToMahasiswa(String[] args) {
        Jurusan jurusan = selectionPrompt("Jurusan", data.getJurusan(), Jurusan::getName, Jurusan::getAlias, args, 0);
        if (jurusan == null) return;
        Prodi prodi = selectionPrompt("Program Studi", jurusan.getProdiList(), p -> p.getJenjang() + " " + p.getName(), Prodi::getAlias, args, 1);
        if (prodi == null) return;
        Kelas kelas = selectionPrompt("Kelas", prodi.getClassList(), Kelas::getCode, null, args, 2);
        if (kelas == null) return;
        displayList("Daftar Mahasiswa Kelas " + kelas.getCode(), kelas.getMahasiswaList());
    }

    private void navigateToJadwal(String[] args) {
        Jurusan jurusan = selectionPrompt("Jurusan", data.getJurusan(), Jurusan::getName, Jurusan::getAlias, args, 0);
        if (jurusan == null) return;
        Prodi prodi = selectionPrompt("Program Studi", jurusan.getProdiList(), p -> p.getJenjang() + " " + p.getName(), Prodi::getAlias, args, 1);
        if (prodi == null) return;
        Kelas kelas = selectionPrompt("Kelas", prodi.getClassList(), Kelas::getCode, null, args, 2);
        if (kelas == null) return;
        
        // REFACTOR: Panggil search, lalu hasilnya lempar ke displayList
        List<Jadwal> jadwalList = JadwalSearch.searchByKelas(data, kelas.getCode(), "all");
        displayList("Jadwal Kuliah Kelas " + kelas.getCode(), jadwalList);
    }

    private void navigateToDosen(String[] args) {
        Jurusan jurusan = selectionPrompt("Jurusan", data.getJurusan(), Jurusan::getName, Jurusan::getAlias, args, 0);
        if (jurusan == null) return;
        Prodi prodi = selectionPrompt("Program Studi", jurusan.getProdiList(), p -> p.getJenjang() + " " + p.getName(), Prodi::getAlias, args, 1);
        if (prodi == null) return;
        Set<Dosen> uniqueDosen = new HashSet<>();
        prodi.getCourseList().forEach(mk -> uniqueDosen.addAll(mk.getPengampuList()));
        displayList("Daftar Dosen Prodi " + prodi.getName(), new ArrayList<>(uniqueDosen));
    }

    private void navigateToMataKuliah(String[] args) {
        Jurusan jurusan = selectionPrompt("Jurusan", data.getJurusan(), Jurusan::getName, Jurusan::getAlias, args, 0);
        if (jurusan == null) return;
        Prodi prodi = selectionPrompt("Program Studi", jurusan.getProdiList(), p -> p.getJenjang() + " " + p.getName(), Prodi::getAlias, args, 1);
        if (prodi == null) return;
        List<MataKuliah> filteredMataKuliah = data.getMataKuliah().stream()
                .filter(mk -> mk.getCourseType() == CourseType.UMUM || mk.getProdiList().contains(prodi))
                .collect(Collectors.toList());
        displayList("Daftar Mata Kuliah Prodi " + prodi.getName(), filteredMataKuliah);
    }

    // --- HELPER TAMPILAN & INPUT ---

    private <T extends Displayable> void displayList(String title, List<T> items) {
        System.out.println("\n--- " + title + " ---");
        if (items.isEmpty()) {
            System.out.println("Tidak ada data yang tersedia.");
            waitForEnter();
            return;
        }

        List<String> headers = items.get(0).getTableHeader();
        List<List<String>> rows = items.stream().map(Displayable::getTableRowData).collect(Collectors.toList());
        int numColumns = headers.size();

        List<Integer> columnWidths = new ArrayList<>();
        for (int i = 0; i < numColumns; i++) {
            int maxWidth = headers.get(i).length();
            for (List<String> row : rows) {
                if (row.get(i).length() > maxWidth) {
                    maxWidth = row.get(i).length();
                }
            }
            columnWidths.add(maxWidth);
        }

        StringBuilder format = new StringBuilder("|");
        for (Integer width : columnWidths) {
            format.append(" %-").append(width).append("s |");
        }
        format.append("\n");

        StringBuilder separator = new StringBuilder("+");
        for (Integer width : columnWidths) {
            separator.append("-".repeat(width + 2)).append("+");
        }

        System.out.println(separator);
        System.out.printf(format.toString(), headers.toArray());
        System.out.println(separator);
        for (List<String> row : rows) {
            System.out.printf(format.toString(), row.toArray());
        }
        System.out.println(separator);
        System.out.println("Total: " + items.size() + " data.");

        waitForEnter();
    }

    private <T> T selectionPrompt(String entityName, List<T> items, Function<T, String> nameExtractor, Function<T, String> aliasExtractor, String[] args, int argIndex) {
        if (argIndex < args.length) {
            Optional<T> found = findItemByNameOrAlias(args[argIndex], items, nameExtractor, aliasExtractor);
            if (found.isPresent()) return found.get();
            System.out.printf("Shorthand '%s' untuk %s tidak ditemukan. Pilih manual.%n", args[argIndex], entityName);
        }
        while (true) {
            System.out.printf("%n--- Pilih %s ---%n", entityName);
            if (items.isEmpty()) {
                System.out.printf("Tidak ada data %s yang tersedia.%n", entityName);
                waitForEnter();
                return null;
            }
            for (int i = 0; i < items.size(); i++) {
                String name = nameExtractor.apply(items.get(i));
                String alias = (aliasExtractor != null && aliasExtractor.apply(items.get(i)) != null) ? " (" + aliasExtractor.apply(items.get(i)) + ")" : "";
                System.out.printf("%d. %s%s%n", i + 1, name, alias);
            }
            System.out.println("0. Kembali");
            System.out.print("Pilih nomor atau ketik nama/alias: ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("0") || input.equalsIgnoreCase("kembali")) return null;
            Optional<T> found = findItemFromManualInput(input, items, nameExtractor, aliasExtractor);
            if (found.isPresent()) {
                return found.get();
            } else {
                System.out.printf("Input '%s' tidak dikenali. Coba lagi.%n", input);
            }
        }
    }

    private <T> Optional<T> findItemFromManualInput(String input, List<T> items, Function<T, String> nameExtractor, Function<T, String> aliasExtractor) {
        try {
            int choice = Integer.parseInt(input);
            if (choice > 0 && choice <= items.size()) {
                return Optional.of(items.get(choice - 1));
            }
        } catch (NumberFormatException e) {
            // Lanjut
        }
        return findItemByNameOrAlias(input, items, nameExtractor, aliasExtractor);
    }

    private <T> Optional<T> findItemByNameOrAlias(String input, List<T> items, Function<T, String> nameExtractor, Function<T, String> aliasExtractor) {
        return items.stream()
                .filter(item -> {
                    boolean nameMatch = nameExtractor.apply(item).equalsIgnoreCase(input);
                    boolean aliasMatch = aliasExtractor != null && aliasExtractor.apply(item) != null && aliasExtractor.apply(item).equalsIgnoreCase(input);
                    return nameMatch || aliasMatch;
                })
                .findFirst();
    }

    private void waitForEnter() {
        System.out.print("\nTekan Enter untuk melanjutkan...");
        scanner.nextLine();
    }
}
