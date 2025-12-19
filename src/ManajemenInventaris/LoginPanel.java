package ManajemenInventaris;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

/**
 * Panel antarmuka untuk Autentikasi (Login) dan Registrasi pengguna baru.
 * Bertugas memvalidasi kredensial input terhadap data di tabel 'users'.
 */
public class LoginPanel extends JPanel {

    /** Referensi ke controller utama untuk navigasi */
    private MainApp mainApp;
    
    /** Field input untuk username */
    private JTextField userField;
    
    /** Field input untuk password (tersembunyi) */
    private JPasswordField passField;

    /**
     * Konstruktor LoginPanel.
     * @param app Referensi ke MainApp.
     */
    public LoginPanel(MainApp app) {
        this.mainApp = app;
        setLayout(new GridBagLayout());
        setBackground(Color.decode("#121212"));
        
        KoneksiDatabase.buatTabelJikaBelumAda();
        initComponents();
    }

    /**
     * Inisialisasi komponen UI Login.
     */
    private void initComponents() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.decode("#1E1E1E"));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel title = new JLabel("LOGIN SISTEM", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        gbc.gridwidth = 2;
        card.add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy++;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setForeground(Color.WHITE);
        card.add(lblUser, gbc);

        gbc.gridx = 1;
        userField = new JTextField(15);
        card.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setForeground(Color.WHITE);
        card.add(lblPass, gbc);

        gbc.gridx = 1;
        passField = new JPasswordField(15);
        card.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JButton btnLogin = new JButton("Masuk");
        btnLogin.setBackground(Color.decode("#3D5AFE"));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.addActionListener(e -> handleLogin());
        card.add(btnLogin, gbc);

        gbc.gridy++;
        JButton btnRegister = new JButton("Daftar Akun Baru");
        btnRegister.setBackground(Color.decode("#2C2C2C"));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.addActionListener(e -> handleRegister());
        card.add(btnRegister, gbc);

        add(card);
    }

    /**
     * Menangani logika login saat tombol ditekan.
     */
    private void handleLogin() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username/Password tidak boleh kosong!");
            return;
        }

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {
            
            stmt.setString(1, user);
            stmt.setString(2, pass);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                mainApp.showInventoryView(user);
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    /**
     * Menangani logika registrasi user baru.
     */
    private void handleRegister() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi username & password dulu!");
            return;
        }

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            
            stmt.setString(1, user);
            stmt.setString(2, pass);
            stmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Registrasi Berhasil! Silakan Login.");
        } catch (SQLException ex) {
            if (ex.getMessage().contains("PRIMARY KEY")) {
                JOptionPane.showMessageDialog(this, "Username sudah dipakai!");
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}