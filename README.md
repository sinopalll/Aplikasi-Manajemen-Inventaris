# 📦 Aplikasi Manajemen Inventaris (Inventory Pro)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SQL Server](https://img.shields.io/badge/SQL%20Server-CC2927?style=for-the-badge&logo=microsoft-sql-server&logoColor=white)
![Azure](https://img.shields.io/badge/Azure-0078D4?style=for-the-badge&logo=microsoft-azure&logoColor=white)
![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)

> **Proyek Tugas Akhir Mandiri Praktikum Pemrograman Berorientasi Objek (PPBO)**

Aplikasi desktop berbasis **Java Swing** untuk pengelolaan stok barang dengan sistem **Multi-User** yang terintegrasi dengan **Microsoft SQL Server (Azure Cloud)**. Aplikasi ini dirancang dengan arsitektur *Single Window*, menerapkan konsep **Asynchronous Loading** untuk performa tinggi, dan antarmuka *Dark Mode* yang modern.

---

## ✨ Fitur Unggulan

| Fitur | Deskripsi |
| :--- | :--- |
| ☁️ **Cloud Database** | Terintegrasi langsung dengan Azure SQL Database (Real-time). |
| 👥 **Multi-User System** | Isolasi data pengguna (User A tidak bisa melihat data User B). |
| ⚡ **Async Loading** | Menggunakan `SwingWorker` agar aplikasi tidak *freeze* saat memuat data besar. |
| 🔐 **Secure Login** | Sistem autentikasi dan registrasi pengguna baru. |
| 📝 **CRUD Lengkap** | Tambah, Edit, Hapus, dan Lihat data barang dengan validasi. |
| 🔍 **Smart Search** | Pencarian data barang secara *real-time* (Live Search). |
| 📊 **Dashboard** | Statistik total item dan valuasi aset otomatis. |
| 📥 **Export Data** | Fitur ekspor laporan stok ke format `.csv`. |

---

## 🛠️ Teknologi yang Digunakan

* **Bahasa Pemrograman:** Java (JDK 11+)
* **GUI Framework:** Java Swing (CardLayout, GridBagLayout)
* **Database:** Microsoft SQL Server (Azure / Localhost)
* **Driver:** MSSQL JDBC Driver
* **Konsep OOP:** Inheritance, Encapsulation, Polymorphism, Abstraction
* **Threading:** SwingWorker (Background Process)

---

## 📂 Struktur Proyek

```text
Aplikasi-Inventaris/
├── src/
│   └── ManajemenInventaris/
│       ├── MainApp.java           # Controller Utama (JFrame)
│       ├── LoginPanel.java        # Interface Login & Register
│       ├── LoadingPanel.java      # Animasi Loading (Thread)
│       ├── ManajemenInventarisGUI.java # Dashboard & CRUD Logic
│       └── KoneksiDatabase.java   # Utility Koneksi JDBC
├── lib/
│   └── mssql-jdbc-12.8.1.jre11.jar # Driver Database
├── image/
│   └── logo.png                   # Aset Gambar
├── db.properties                  # Konfigurasi Database (PENTING)
├── database.sql                   # Script Query SQL
└── README.md                      # Dokumentasi Proyek