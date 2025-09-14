package id.ac.polban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Dosen extends Person {
    private String email;
    private final List<MataKuliah> mataKuliahDiampu = new ArrayList<>();

    public Dosen(String kodeDosen, String nama, String email) {
        super(kodeDosen, nama); // penggunaan super di konstruktor
        this.email = Objects.requireNonNull(email);
    }

    // Getter & Setter (enkapsulasi)
    public String getKodeDosen() {
        return getKode();
    }
    public String getNama() {
        return super.getNama();
    }
    public String getEmail() {
        return email;
    }

    public void setkodeDosen(String kodeDosen) {
        super.setKode(Objects.requireNonNull(kodeDosen));
    }
    public void setNama(String nama) {
        super.setNama(Objects.requireNonNull(nama));
    }
    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email);
    }

    // Override contoh: menambahkan email pada identitas
    @Override
    public String getIdentitas() {
        return super.getIdentitas() + " | " + email;
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
    
    public boolean canActivate() {
        return mataKuliahDiampu.stream().anyMatch(MataKuliah::getIsActive);
    }

    @Override
    public void setIsActive(boolean active) {
        if (active && !canActivate()) {
            throw new IllegalStateException("Dosen " + getKodeDosen() + " tidak dapat diaktifkan karena tidak mengampu Mata Kuliah yang aktif.");
        }
        super.setIsActive(active);
    }
}
