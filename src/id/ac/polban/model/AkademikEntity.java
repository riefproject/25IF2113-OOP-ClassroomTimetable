package id.ac.polban.model;

import java.util.Objects;

public class AkademikEntity {
    private String kode;
    private String nama;
    private boolean isActive = false; // Default to inactive

    public AkademikEntity(String kode, String nama) {
        this.kode = Objects.requireNonNull(kode);
        this.nama = Objects.requireNonNull(nama);
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public String getKode() { return kode; }
    public String getNama() { return nama; }

    public void setKode(String kode) { this.kode = Objects.requireNonNull(kode); }
    public void setNama(String nama) { this.nama = Objects.requireNonNull(nama); }

    /**
     * Dapat dioverride oleh turunan untuk menambahkan informasi identitas.
     */
    public String getIdentitas() {
        return kode + " - " + nama;
    }

    @Override
    public String toString() {
        return getIdentitas();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AkademikEntity that = (AkademikEntity) o;
        return kode.equals(that.kode);
    }

    @Override
    public int hashCode() {
        return kode.hashCode();
    }
}
