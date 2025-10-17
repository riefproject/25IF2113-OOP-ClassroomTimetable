package id.ac.polban.app;

import id.ac.polban.service.Seed;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Seed seed;
        try {
            seed = Seed.loadFromCsvs("data");
        } catch (IOException e) {
            System.err.println("FATAL: Gagal membaca data dari direktori 'data'. Aplikasi tidak dapat berjalan.");
            System.err.println("Detail Error: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        MenuApp menu = new MenuApp(seed);
        menu.run();
    }
}
