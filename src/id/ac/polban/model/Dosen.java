package id.ac.polban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Dosen {
    private String kodeDosen;
    private String nama;
    private String email;
    private final List<MataKuliah> mataKuliahDiampu = new ArrayList<>();

    public Dosen(String kodeDosen, String nama, String email) {
        this.kodeDosen = Objects.requireNonNull(kodeDosen);
        this.nama = Objects.requireNonNull(nama);
        this.email = Objects.requireNonNull(email);
    }

    // Getter & Setter (enkapsulasi)
    public String getKodeDosen() {
        return kodeDosen;
    }
    public String getNama() {
        return nama;
    }
    public String getEmail() {
        return email;
    }

    public void setkodeDosen(String kodeDosen) {
        this.kodeDosen = Objects.requireNonNull(kodeDosen);
    }
    public void setNama(String nama) {
        this.nama = Objects.requireNonNull(nama);
    }
    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email);
    }

    // Relasi many-to-many: expose as read-only
    public List<MataKuliah> getMataKuliahDiampu() {
        return Collections.unmodifiableList(mataKuliahDiampu);
    }
    public void ampu(MataKuliah mk) {
        if (mk != null && !mataKuliahDiampu.contains(mk)) {
            mataKuliahDiampu.add(mk);
            mk.tambahPengampu(this); // sinkron sisi MK
        }
    }
    public void lepas(MataKuliah mk) {
        if (mk != null && mataKuliahDiampu.remove(mk)) {
            mk.hapusPengampu(this);
        }
    }
}
