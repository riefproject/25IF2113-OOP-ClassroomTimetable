package id.ac.polban.model;

import id.ac.polban.contract.Activable;
import id.ac.polban.contract.Displayable;
import id.ac.polban.contract.Persistable;

import java.util.List;
import java.util.Objects;

public abstract class AkademikEntity implements Displayable, Persistable, Activable {
    private String code;
    private String name;
        protected boolean isActive = false;

    public AkademikEntity(String code, String name) {
        this.code = Objects.requireNonNull(code);
        this.name = Objects.requireNonNull(name);
    }

    // --- Getters & Setters ---
    public String getCode() { return code; }
    public void setCode(String code) { this.code = Objects.requireNonNull(code); }
    public String getName() { return name; }
    public void setName(String name) { this.name = Objects.requireNonNull(name); }

    public String getIdentity() { return code + " - " + name; }

    // --- Implementasi Interface Displayable ---
    @Override
    public abstract List<String> getTableHeader();
    @Override
    public abstract List<String> getTableRowData();

    // --- Implementasi Interface Persistable ---
    @Override
    public String toPersistableFormat() {
        return code + "," + name;
    }

    // --- Implementasi Interface Activable ---
    @Override
    public abstract void activate();

    @Override
    public void deactivate() {
        this.isActive = false;
    }

    @Override
    public boolean isActive() {
        return this.isActive;
    }

    // --- Override Method Bawaan Java ---
    @Override
    public String toString() { return getIdentity(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AkademikEntity that = (AkademikEntity) o;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() { return code.hashCode(); }
}
