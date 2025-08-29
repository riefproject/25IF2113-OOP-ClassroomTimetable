package jadwalKelas.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MataKuliah {
    private String kodeMk;
    private String namaMk;
    private int sks;
    private final List<Dosen> pengampu = new ArrayList<>();

    public MataKuliah(String kodeMk, String namaMk, int sks) {
        this.kodeMk = Objects.requireNonNull(kodeMk);
        this.namaMk = Objects.requireNonNull(namaMk);
        this.sks = sks;
    }

    public String getKodeMk() {
        return kodeMk;
    }
    public int getSks() {
        return sks;
    }
    public String getNamaMk() {
        return namaMk;
    }

    public void setKodeMk(String kodeMk) {
        this.kodeMk = Objects.requireNonNull(kodeMk);
    }
    public void setNamaMk(String namaMk) {
        this.namaMk = Objects.requireNonNull(namaMk);
    }
    public void setSks(int sks) {
        this.sks = sks;
    }

    public List<Dosen> getPengampu() {
        return Collections.unmodifiableList(pengampu);
    }

    // dipanggil dari Dosen.ampu/lepas untuk jaga konsistensi
    void tambahPengampu(Dosen d) {
        if (d != null && !pengampu.contains(d)) pengampu.add(d);
    }
    void hapusPengampu(Dosen d) {
        pengampu.remove(d);
    }
}
