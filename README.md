# Tucil3_13524002_13524092
## Author

- Daniel Anindito Nugroho (13524002)
- Timothy Bernard Soeharto (13524092)

## Deskripsi Singkat

Program ini adalah solver untuk **Ice Sliding Puzzle** pada tugas kecil IF2211 Strategi Algoritma. Puzzle direpresentasikan sebagai papan/grid berisi start (`Z`), goal (`O`), wall (`X`), lava (`L`), path (`*`), dan checkpoint berurutan (`0`, `1`, `2`, dan seterusnya).

Aktor bergerak dengan mekanisme sliding: ketika memilih arah, aktor terus meluncur sampai berhenti sebelum wall. Program mencari solusi gerakan menggunakan algoritma:

- Uniform Cost Search (UCS)
- Greedy Best First Search (GBFS)
- A*

Program juga menyediakan GUI Java Swing untuk memilih file input, memilih algoritma dan heuristic, menampilkan solusi, cost, jumlah iterasi, waktu eksekusi, playback langkah solusi atau explored states, serta menyimpan hasil ke file `.txt`.

## Requirement

Requirement program:

- Java Development Kit (JDK) 8 atau lebih baru
- PowerShell atau terminal lain yang dapat menjalankan `javac` dan `java`

Program tidak menggunakan dependency eksternal.

## Cara Kompilasi

Jalankan command berikut dari root repository:

```powershell
javac -d bin (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```

Command tersebut akan mengompilasi semua file `.java` dari folder `src` ke folder `bin`.

## Cara Menjalankan Program

Setelah kompilasi berhasil, jalankan GUI dengan command:

```powershell
java -cp bin gui.Main
```

## Cara Menggunakan Program

1. Klik tombol `Choose Input File`.
2. Pilih file testcase `.txt`, misalnya dari folder `testcases/valid`.
3. Pilih algoritma: `UCS`, `GBFS`, atau `A*`.
4. Jika memilih `GBFS` atau `A*`, pilih heuristic yang tersedia:
   - `H1 - Manhattan Next Target`
   - `H2 - Manhattan Ordered Chain`
   - `H3 - Chebyshev Next Target`
   - `H4 - Euclidean Next Target`
5. Klik tombol `Solve`.
6. Program akan menampilkan:
   - solusi gerakan,
   - total cost,
   - jumlah iterasi,
   - jumlah explored states,
   - waktu eksekusi,
   - visualisasi papan.
7. Gunakan kontrol playback untuk melihat `Solution steps` atau `Explored states`.
8. Klik `Save Result` untuk menyimpan hasil pencarian ke file `.txt`.

## Format Input

Format file input:

```text
N M
<N baris grid papan>
<N baris grid cost>
```

Contoh:

```text
3 4
XXXX
XZOX
XXXX
999 999 999 999
999 1 2 999
999 999 999 999
```

Simbol papan:

- `Z`: start
- `O`: goal
- `X`: wall
- `L`: lava
- `*`: path
- `0..9`: checkpoint berurutan

## Testcase

Beberapa testcase tersedia pada folder:

```text
testcases/valid
testcases/invalid
```

File pada `testcases/valid` digunakan untuk menguji solusi puzzle, sedangkan file pada `testcases/invalid` digunakan untuk menguji validasi parser.


