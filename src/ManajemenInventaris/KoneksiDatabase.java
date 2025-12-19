package ManajemenInventaris;

import java.sql.*;
import java.util.Properties;
import java.io.FileInputStream;

/**
 * Kelas utilitas untuk mengelola koneksi JDBC ke Microsoft SQL Server.
 * <p>
 * Menyediakan metode statis untuk mendapatkan objek koneksi dan
 * inisialisasi skema database secara otomatis.
 */
public class KoneksiDatabase {
    
    /** Objek koneksi tunggal (Singleton) */
    private static Connection koneksi;
    
    /**
     * Constructor private untuk mencegah instansiasi kelas utilitas.
     */
    public KoneksiDatabase() {
        // Utility class
    }
    
    /**
     * Mendapatkan instance koneksi database (Singleton Pattern sederhana).
     * Membaca file konfigurasi 'db.properties'.
     * @return Connection objek koneksi aktif, atau null jika gagal.
     */
    public static Connection getKoneksi() {
        if (koneksi == null) {
            try {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream("db.properties")) {
                    props.load(fis);
                } catch (Exception e) {
                    System.err.println("File db.properties tidak ditemukan, cek folder project.");
                }

                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                
                koneksi = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password")
                );
            } catch (Exception e) {
                System.err.println("Koneksi Gagal: " + e.getMessage());
            }
        }
        return koneksi;
    }

    /**
     * Memastikan tabel 'users' dan 'inventaris' tersedia di database.
     * Menggunakan pengecekan IF NOT EXISTS pada SQL Server.
     */
    public static void buatTabelJikaBelumAda() {
        String sqlUsers = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='users' AND xtype='U') "
                + "CREATE TABLE users ("
                + " username VARCHAR(50) NOT NULL PRIMARY KEY,"
                + " password VARCHAR(50) NOT NULL"
                + ")";

        String sqlInventaris = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='inventaris' AND xtype='U') "
                + "CREATE TABLE inventaris ("
                + " kode VARCHAR(50) NOT NULL,"
                + " nama VARCHAR(100) NOT NULL,"
                + " jumlah INT,"
                + " harga DECIMAL(18, 2),"
                + " pemilik VARCHAR(50) NOT NULL,"
                + " CONSTRAINT PK_Inventaris_User PRIMARY KEY (kode, pemilik),"
                + " CONSTRAINT FK_Inventaris_Users FOREIGN KEY (pemilik) REFERENCES users(username) ON DELETE CASCADE"
                + ")";

        try (Connection c = getKoneksi(); Statement s = c.createStatement()) {
            s.execute(sqlUsers);
            s.execute(sqlInventaris);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}