package ManajemenInventaris;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Utility class to provide database connections and ensure the inventory table exists.
 * - Loads DB credentials from an external `db.properties` file.
 * - Provides `getKoneksi()` to obtain a JDBC Connection (or null with an error dialog on failure).
 * - Provides `buatTabelJikaBelumAda()` to create the `inventaris` table if it does not yet exist.
 */
public class KoneksiDatabase {
    
    private static Properties config = new Properties();

    /**
     * Attempt to read DB config and return a JDBC Connection.
     * @return Connection object or null if connection fails
     */
    public static Connection getKoneksi() {
        try {
            config.load(new FileInputStream("db.properties"));
            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            return DriverManager.getConnection(
                config.getProperty("db.url"),
                config.getProperty("db.user"),
                config.getProperty("db.password")
            );
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, 
                "File konfigurasi 'db.properties' tidak ditemukan!", "Config Error", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Gagal koneksi ke Database: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    /**
     * Create `inventaris` table if it doesn't already exist.
     * Uses SQL Server T-SQL IF NOT EXISTS check.
     */
    public static void buatTabelJikaBelumAda() {
        String sqlInventaris = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='inventaris' AND xtype='U') "
                   + "BEGIN "
                   + "CREATE TABLE inventaris ("
                   + " kode VARCHAR(50) NOT NULL PRIMARY KEY,"
                   + " nama VARCHAR(100) NOT NULL,"
                   + " jumlah INT,"
                   + " harga DECIMAL(18, 2)"
                   + ") "
                   + "END";

        try (Connection conn = getKoneksi()) {
            if (conn != null) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sqlInventaris);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error cek tabel: " + e.getMessage());
        }
    }
}