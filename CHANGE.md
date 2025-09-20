## Perubahan Logika Aktivasi dan Refaktorisasi

Perubahan ini mengintroduksi pola aktivasi untuk validasi entitas dan melakukan refaktorisasi untuk memindahkan logika umum ke kelas abstrak (`AkademikEntity` dan `Person`).

### `id.ac.polban.model.AkademikEntity`
- `[+] protected isActive: boolean`
- `[+] public abstract activate(): void`
- `[+] public deactivate(): void`
- `[+] public isActive(): boolean`

### `id.ac.polban.model.Person`
- `[+] protected isActive: boolean`
- `[+] public abstract activate(): void`
- `[+] public deactivate(): void`
- `[+] public isActive(): boolean`

### `id.ac.polban.model.Kampus`
- ~~`[-] private isActive: boolean`~~
- ~~`[-] public deactivate(): void`~~
- ~~`[-] public isActive(): boolean`~~
- `[+] @Override public activate(): void`

### `id.ac.polban.model.Jurusan`
- ~~`[-] private isActive: boolean`~~
- ~~`[-] public isActive(): boolean`~~
- `[+] @Override public activate(): void`
- ~~`[-] public deactivate(): void`~~
- `[+] @Override public deactivate(): void`

### `id.ac.polban.model.Prodi`
- ~~`[-] private isActive: boolean`~~
- ~~`[-] public isActive(): boolean`~~
- `[+] @Override public activate(): void`
- ~~`[-] public deactivate(): void`~~
- `[+] @Override public deactivate(): void`

### `id.ac.polban.model.Kelas`
- ~~`[-] private isActive: boolean`~~
- ~~`[-] public isActive(): boolean`~~
- `[+] @Override public activate(): void`
- ~~`[-] public deactivate(): void`~~
- `[+] @Override public deactivate(): void`

### `id.ac.polban.model.MataKuliah`
- ~~`[-] private isActive: boolean`~~
- ~~`[-] public deactivate(): void`~~
- ~~`[-] public isActive(): boolean`~~
- `[+] @Override public activate(): void`

### `id.ac.polban.model.Dosen`
- ~~`[-] private isActive: boolean`~~
- ~~`[-] public deactivate(): void`~~
- ~~`[-] public isActive(): boolean`~~
- `[+] @Override public activate(): void`

### `id.ac.polban.model.Mahasiswa`
- ~~`[-] private isActive: boolean`~~
- ~~`[-] public deactivate(): void`~~
- ~~`[-] public isActive(): boolean`~~
- `[+] @Override public activate(): void`

### `id.ac.polban.service.Seed`
- `[+] private static activateAll(Collection<Dosen>, Collection<MataKuliah>, Collection<Kampus>, Collection<Jurusan>, Collection<Prodi>, Collection<Kelas>, Collection<Mahasiswa>): void`
