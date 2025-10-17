package id.ac.polban.service;

import id.ac.polban.contract.Displayable;
import id.ac.polban.model.CourseType;
import id.ac.polban.model.Dosen;
import id.ac.polban.model.Jadwal;
import id.ac.polban.model.Kelas;
import id.ac.polban.model.MataKuliah;
import id.ac.polban.model.Prodi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class JadwalSearchTest {

    private Prodi prodi;
    private MataKuliah algoritma;
    private MataKuliah struktur;
    private Dosen dosen;

    @Mock
    private id.ac.polban.service.Seed seed;

    @BeforeAll
    void initFixtures() {
        prodi = new Prodi("TI", "Teknik Informatika");
        prodi.setAlias("IF");
        prodi.setJenjang("D4");
        algoritma = new MataKuliah("ALG", "Algoritma", 3, CourseType.PRODI_SPECIFIC);
        struktur = new MataKuliah("STR", "Struktur Data", 3, CourseType.UMUM);
        dosen = new Dosen("DS01", "Pak Dosen", "dosen@polban.ac.id");
    }

    @BeforeEach
    void resetSeedMock() {
        reset(seed);
    }

    @AfterEach
    void verifySeedUsage() {
        verify(seed).getKelas();
        verifyNoMoreInteractions(seed);
    }

    @AfterAll
    void releaseFixtures() {
        prodi = null;
        algoritma = null;
        struktur = null;
        dosen = null;
    }

    private Kelas kelas(String code) {
        Kelas kelas = new Kelas(code, prodi);
        prodi.addKelas(kelas);
        return kelas;
    }

    @Test
    void searchByKelas_returnsEmpty_whenKelasNotFound() {
        List<Kelas> kelasList = List.of(kelas("TI-UNUSED"));
        when(seed.getKelas()).thenReturn(kelasList);

        List<Jadwal> results = JadwalSearch.searchByKelas(seed, "TI-TIDAKADA", "all");

        assertTrue(results.isEmpty(), "Semestinya kosong saat kelas tidak ditemukan");
    }

    @Test
    void searchByKelas_returnsEmpty_whenKelasHasNoSchedule() {
        Kelas kelas = kelas("TI-NOSCHED");
        when(seed.getKelas()).thenReturn(List.of(kelas));

        List<Jadwal> results = JadwalSearch.searchByKelas(seed, "TI-NOSCHED", "all");

        assertTrue(results.isEmpty(), "Kelas tanpa jadwal harus menghasilkan list kosong");
    }

    @Test
    void searchByKelas_returnsEmpty_whenHariInvalid() {
        Kelas kelas = kelas("TI-HARICHECK");
        new Jadwal(kelas, algoritma, dosen, DayOfWeek.MONDAY, LocalTime.of(7, 0), LocalTime.of(9, 0), "R101");
        when(seed.getKelas()).thenReturn(List.of(kelas));

        List<Jadwal> results = JadwalSearch.searchByKelas(seed, "TI-HARICHECK", "libur");

        assertTrue(results.isEmpty(), "Filter hari tidak valid harus mengembalikan list kosong");
    }

    @Test
    void searchByKelas_filtersAndSortsForSpecificDay() {
        Kelas kelas = kelas("TI-MONDAY");
        Jadwal pagi = new Jadwal(kelas, algoritma, dosen, DayOfWeek.MONDAY, LocalTime.of(7, 0), LocalTime.of(9, 0), "R101");
        Jadwal siang = new Jadwal(kelas, struktur, dosen, DayOfWeek.MONDAY, LocalTime.of(13, 0), LocalTime.of(15, 0), "R201");
        new Jadwal(kelas, algoritma, dosen, DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(10, 0), "R102");
        when(seed.getKelas()).thenReturn(List.of(kelas));

        List<Jadwal> results = JadwalSearch.searchByKelas(seed, "TI-MONDAY", "Senin");

        assertEquals(List.of(pagi, siang), results, "Jadwal hari Senin harus difilter dan diurutkan");
    }

    @Test
    void searchByKelas_returnsAllSortedForAllKeyword() {
        Kelas kelas = kelas("TI-SEMUA");
        Jadwal mondayPagi = new Jadwal(kelas, algoritma, dosen, DayOfWeek.MONDAY, LocalTime.of(7, 0), LocalTime.of(8, 30), "R101");
        Jadwal wednesday = new Jadwal(kelas, struktur, dosen, DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(10, 0), "R202");
        Jadwal mondaySiang = new Jadwal(kelas, algoritma, dosen, DayOfWeek.MONDAY, LocalTime.of(13, 0), LocalTime.of(15, 0), "R301");
        when(seed.getKelas()).thenReturn(List.of(kelas));

        List<Jadwal> results = JadwalSearch.searchByKelas(seed, "TI-SEMUA", "ALL");

        assertEquals(List.of(mondayPagi, mondaySiang, wednesday), results, "Keyword 'all' harus mengembalikan semua jadwal terurut");
        Displayable displayable = results.get(0);
        assertEquals("MONDAY", displayable.getTableRowData().get(0), "Implementasi Displayable harus konsisten dengan urutan jadwal");
    }
}
