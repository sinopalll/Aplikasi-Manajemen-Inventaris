# Aplikasi Manajemen Inventaris Pro (Java Swing + SQL Server)

![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![SQL Server](https://img.shields.io/badge/SQL%20Server-CC2927?style=for-the-badge&logo=microsoft-sql-server&logoColor=white)

Aplikasi desktop untuk pengelolaan stok barang gudang yang dirancang dengan antarmuka modern (Dark Mode), aman, dan efisien. Proyek ini dibangun untuk memenuhi **Tugas Akhir Mata Kuliah Pemrograman Berorientasi Objek (PBO)** Semester 3.

---

## 🚀 Fitur Unggulan

1.  **CRUD Lengkap:** Tambah, Baca, Edit, dan Hapus data barang dengan mudah.
2.  **Modern Dark UI:** Desain antarmuka gelap yang nyaman di mata dan terlihat profesional.
3.  **Smart Pagination:** Menampilkan data per halaman (15 baris) untuk performa aplikasi yang ringan, meskipun data mencapai ribuan.
4.  **Real-time Search:** Pencarian barang berdasarkan Nama atau Kode secara instan tanpa perlu reload.
5.  **Smart Input Masking:** Kolom harga otomatis memformat angka menjadi format mata uang Rupiah (Rp) saat tidak diedit.
6.  **Validasi Ketat:** Mencegah input huruf pada kolom angka dan mencegah duplikasi Kode Barang (Primary Key).
7.  **Export to CSV:** Fitur pelaporan untuk mengunduh data stok ke dalam format Excel/CSV.
8.  **Database Terintegrasi:** Menggunakan SQL Server untuk penyimpanan data yang persisten dan aman.

---

## 🛠️ Teknologi yang Digunakan

* **Bahasa:** Java (JDK 23, dikompilasi dengan kompatibilitas JDK 11).
* **GUI Library:** Java Swing (JFrame, JPanel, GridBagLayout, Custom Renderer).
* **Database:** Microsoft SQL Server 2022.
* **Driver JDBC:** `mssql-jdbc-13.2.1.jre11.jar`.
* **Tools:** Visual Studio Code, SQL Server Management Studio (SSMS).

---

## ⚙️ Persyaratan Sistem (Prerequisites)

Sebelum menjalankan aplikasi, pastikan komputer Anda memiliki:
1.  **Java Runtime Environment (JRE)** minimal versi 11.
2.  **SQL Server** (Developer/Express Edition).
3.  **SQL Server Management Studio (SSMS)** untuk import database.

---

## 📥 Cara Instalasi & Menjalankan

### Langkah 1: Persiapan Database
1.  Buka **SSMS (SQL Server Management Studio)**.
2.  Buka file `database.sql` yang ada di dalam repository ini.
3.  Jalankan (Execute) script tersebut. Database `db_inventaris` dan tabelnya akan otomatis dibuat beserta data contohnya.
4.  Pastikan **TCP/IP Port 1433** sudah diaktifkan di *SQL Server Configuration Manager*.

### Langkah 2: Konfigurasi Koneksi
1.  Download file `db.properties` dari repository ini.
2.  Buka file tersebut dengan Notepad.
3.  Sesuaikan `db.user` dan `db.password` dengan login SQL Server di komputer Anda.

```properties
db.url=jdbc:sqlserver://localhost:1433;databaseName=db_inventaris;encrypt=true;trustServerCertificate=true;
db.user=sa
db.password=PasswordLaptopAnda