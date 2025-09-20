package id.ac.polban.model;

import id.ac.polban.contract.Activable;
import id.ac.polban.contract.Displayable;
import id.ac.polban.contract.Persistable;

import java.util.Objects;

/**
 * Fondasi buat semua entitas orang di sistem ini.
 * Dibuat abstract biar ga bisa sembarangan bikin object Person,
 * kan aneh kalo ada "orang" tapi gajelas dia Dosen atau Mahasiswa.
 */
public abstract class Person implements Displayable, Persistable, Activable {
    private String id; // bisa NIM, bisa NIP. yang penting unik
    private String name;
    protected boolean isActive = false;

    public Person(String id, String name) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
    }

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = Objects.requireNonNull(id); }
    public String getName() { return name; }
    public void setName(String name) { this.name = Objects.requireNonNull(name); }

    public String getIdentity() { return id + " - " + name; }

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

    // --- Implementasi Interface Persistable ---
    @Override
    public String toPersistableFormat() {
        return id + "," + name;
    }

    // --- Override Method Bawaan Java ---
    @Override
    public String toString() { return getIdentity(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id.equals(person.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
