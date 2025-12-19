package ManajemenInventaris;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class LoginPanel extends JPanel {

    private MainApp mainApp;
    private JTextField fieldUser;
    private JPasswordField fieldPass;
    private JButton btnLogin, btnRegister;

    /** Dark Mode Colors */
    private static final Color BG_BASE = Color.decode("#121212");
    private static final Color BG_PANEL = Color.decode("#1E1E1E");
    private static final Color ACCENT_COLOR = Color.decode("#3D5AFE");
    private static final Color TEXT_WHITE = Color.WHITE;

    /**
     * Creates a LoginPanel with the given MainApp reference.
     * @param app Reference to the main application
     */
    public LoginPanel(MainApp app) {
        this.mainApp = app;
        setLayout(new GridBagLayout());
        setBackground(BG_BASE);
        initComponents();
    }

    /**
     * Initialize UI components for the login panel.
     */
    private void initComponents() {
        JPanel cardPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        cardPanel.setBackground(BG_PANEL);
        cardPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        // Judul
        JLabel title = new JLabel("INVENTORY PRO", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ACCENT_COLOR);
        cardPanel.add(title);

        /** Input Username */
        JLabel lblUser = new JLabel("Username");
        lblUser.setForeground(TEXT_WHITE);
        fieldUser = new JTextField();
        styleField(fieldUser);
        cardPanel.add(lblUser);
        cardPanel.add(fieldUser);

        /** Input Password */
        JLabel lblPass = new JLabel("Password");
        lblPass.setForeground(TEXT_WHITE);
        fieldPass = new JPasswordField();
        styleField(fieldPass);
        cardPanel.add(lblPass);
        cardPanel.add(fieldPass);
        
        // Setup Enter key navigation
        fieldUser.addActionListener(e -> fieldPass.requestFocus());
        fieldPass.addActionListener(e -> aksiLogin());

        /** Button Panel */
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(BG_PANEL);

        btnRegister = new JButton("Daftar");
        styleButton(btnRegister, Color.decode("#424242"));
        btnRegister.addActionListener(e -> aksiDaftar());

        btnLogin = new JButton("Masuk");
        styleButton(btnLogin, ACCENT_COLOR);
        btnLogin.addActionListener(e -> aksiLogin());

        btnPanel.add(btnRegister);
        btnPanel.add(btnLogin);
        cardPanel.add(btnPanel);

        add(cardPanel);
    }

    /**
     * Apply styling to a JTextField.
     * @param field the field to style
     */
    private void styleField(JTextField field) {
        field.setPreferredSize(new Dimension(250, 35));
        field.setBackground(Color.decode("#2C2C2C"));
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(TEXT_WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    /**
     * Apply styling to a JButton.
     * @param btn the button to style
     * @param bg the background color
     */
    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(TEXT_WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    /**
     * Handle login action by validating credentials against the database.
     */
    private void aksiLogin() {
        String user = fieldUser.getText();
        String pass = new String(fieldPass.getPassword());

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {
            pst.setString(1, user);
            pst.setString(2, pass);
            if (pst.executeQuery().next()) {
                /** LOGIN SUKSES -> Panggil MainApp untuk ganti halaman */
                fieldUser.setText(""); fieldPass.setText("");
                mainApp.showInventoryView(user);
            } else {
                JOptionPane.showMessageDialog(this, "Username/Password Salah!", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Handle registration action by inserting new user into the database.
     */
    private void aksiDaftar() {
        String user = fieldUser.getText();
        String pass = new String(fieldPass.getPassword());
        if(user.isEmpty() || pass.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Isi data lengkap!"); return; 
        }
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement pst = conn.prepareStatement("INSERT INTO users VALUES (?, ?)")) {
            pst.setString(1, user);
            pst.setString(2, pass);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Berhasil Daftar! Silakan Login.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Username sudah dipakai!");
        }
    }
}