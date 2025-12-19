package ManajemenInventaris;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.io.File;
import java.io.FileWriter;

/**
 * Panel dashboard utama untuk manajemen data inventaris.
 * Melakukan operasi CRUD, pencarian, dan pelaporan.
 */
public class ManajemenInventarisGUI extends JPanel {

    /** Referensi ke aplikasi utama */
    private MainApp mainApp;
    
    /** Username pengguna yang sedang aktif */
    private String currentUser;
    
    // UI Components - Fields
    /** Tabel untuk menampilkan data barang */
    private JTable tabelInventaris;
    
    /** Model data untuk tabel */
    private DefaultTableModel modelTabel;
    
    /** Field Input: Kode Barang */
    private JTextField fieldKode;
    /** Field Input: Nama Barang */
    private JTextField fieldNama;
    /** Field Input: Jumlah Stok */
    private JTextField fieldJumlah;
    /** Field Input: Harga Barang */
    private JTextField fieldHarga;
    /** Field Input: Pencarian */
    private JTextField fieldCari;
    
    // UI Components - Buttons
    /** Tombol aksi tambah data */
    private JButton tombolTambah;
    /** Tombol aksi edit data */
    private JButton tombolEdit;
    /** Tombol aksi hapus data */
    private JButton tombolHapus;
    /** Tombol bersihkan form */
    private JButton tombolClear;
    /** Tombol logout */
    private JButton tombolExit;
    /** Tombol export CSV */
    private JButton tombolExport;
    
    /** Label statistik total item */
    private JLabel labelTotalItem;
    /** Label statistik total nilai aset */
    private JLabel labelTotalAset;

    // Constants & Styling
    /** Definisi nama kolom tabel */
    private final String[] namaKolom = {"Kode Barang", "Nama Barang", "Jumlah (Stok)", "Harga (Rp)"};
    
    /** Formatter mata uang Rupiah */
    @SuppressWarnings("deprecation")
    private final NumberFormat kursIndonesia = NumberFormat.getCurrencyInstance(new Locale("id","ID"));
    
    // Warna dan Font
    private static final Color BG_BASE = Color.decode("#121212");       
    private static final Color BG_PANEL = Color.decode("#1E1E1E");      
    private static final Color TEXT_PRIMARY = Color.decode("#FFFFFF");  
    private static final Color TEXT_SECONDARY = Color.decode("#BDBDBD");
    private static final Color BORDER_SOFT = Color.decode("#2C2C2C");
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);

    /**
     * Konstruktor Panel Inventaris.
     * @param app Referensi MainApp.
     * @param username User pemilik sesi.
     */
    public ManajemenInventarisGUI(MainApp app, String username) {
        this.mainApp = app;
        this.currentUser = username;
        this.setBackground(BG_BASE); 
        KoneksiDatabase.buatTabelJikaBelumAda();
        initComponents();
        loadData(); 
    }

    /** Inisialisasi seluruh komponen GUI */
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setBackground(BG_BASE);
        topWrapper.add(createHeaderPanel(), BorderLayout.NORTH);
        topWrapper.add(createDashboardPanel(), BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        setupEventListeners();
    }

    /** Membuat panel header (Judul & Logout) */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setBackground(BG_PANEL);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblJudul = new JLabel("User: " + currentUser);
        lblJudul.setFont(HEADER_FONT);
        lblJudul.setForeground(TEXT_PRIMARY);
        headerPanel.add(lblJudul, BorderLayout.WEST);
        
        tombolExit = createStyledButton("Logout", new Color[]{Color.decode("#D32F2F"), Color.decode("#E57373")});
        tombolExit.setPreferredSize(new Dimension(80, 35));
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        logoutPanel.setBackground(BG_PANEL);
        logoutPanel.add(tombolExit);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        return headerPanel;
    }

    /** Membuat panel dashboard statistik */
    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        dashboardPanel.setBackground(BG_BASE);
        dashboardPanel.setBorder(new EmptyBorder(10, 20, 0, 20));

        labelTotalItem = new JLabel("0");
        dashboardPanel.add(createCard("Total Jenis Barang", labelTotalItem, new Color(0, 137, 123)));

        labelTotalAset = new JLabel("Rp0,00");
        dashboardPanel.add(createCard("Total Nilai Aset", labelTotalAset, new Color(255, 143, 0)));
        return dashboardPanel;
    }

    /** Membuat kartu statistik individual */
    private JPanel createCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_SOFT), BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor)
        ));
        card.setBorder(new CompoundBorder(card.getBorder(), new EmptyBorder(15, 20, 15, 20)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(TEXT_SECONDARY);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(TEXT_PRIMARY);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /** Membuat container konten utama (Form & Tabel) */
    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BG_BASE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.add(createFormPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        return mainPanel;
    }

    /** Membuat panel form input */
    private JPanel createFormPanel() {
        JPanel topContainer = new JPanel(new GridBagLayout());
        topContainer.setBackground(BG_PANEL);
        topContainer.setBorder(new CompoundBorder(new LineBorder(BORDER_SOFT, 1), new EmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        fieldKode = createStyledTextField();
        fieldNama = createStyledTextField();
        fieldJumlah = createStyledTextField();
        fieldHarga = createStyledTextField();

        addFormRow(topContainer, gbc, 0, "Kode Barang:", fieldKode);
        addFormRow(topContainer, gbc, 1, "Nama Barang:", fieldNama);
        addFormRow(topContainer, gbc, 2, "Jumlah Stok:", fieldJumlah);
        addFormRow(topContainer, gbc, 3, "Harga Satuan:", fieldHarga);

        JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        formButtonPanel.setBackground(BG_PANEL);
        tombolTambah = createStyledButton("Simpan", new Color[]{Color.decode("#1E88E5"), Color.decode("#2196F3")});
        tombolEdit = createStyledButton("Update", new Color[]{Color.decode("#43A047"), Color.decode("#4CAF50")});
        tombolHapus = createStyledButton("Hapus", new Color[]{Color.decode("#E53935"), Color.decode("#EF5350")});
        tombolClear = createStyledButton("Clear", new Color[]{Color.decode("#6C757D"), Color.decode("#7C8790")});

        Dimension btnSize = new Dimension(120, 35);
        tombolTambah.setPreferredSize(btnSize);
        tombolEdit.setPreferredSize(btnSize);
        tombolHapus.setPreferredSize(btnSize);
        tombolClear.setPreferredSize(btnSize);

        formButtonPanel.add(tombolTambah); formButtonPanel.add(tombolEdit);
        formButtonPanel.add(tombolHapus); formButtonPanel.add(tombolClear);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        topContainer.add(formButtonPanel, gbc);
        return topContainer;
    }

    /** Menambahkan baris form */
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.1;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(TEXT_SECONDARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.9;
        panel.add(field, gbc);
    }

    /** Membuat panel tabel dan pencarian */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(BG_BASE);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(BG_BASE);
        JLabel lblCari = new JLabel("Cari Barang: ");
        lblCari.setForeground(TEXT_SECONDARY);
        searchPanel.add(lblCari, BorderLayout.WEST);
        
        fieldCari = createStyledTextField();
        searchPanel.add(fieldCari, BorderLayout.CENTER);
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        modelTabel = new DefaultTableModel(namaKolom, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelInventaris = new JTable(modelTabel);
        tabelInventaris.setBackground(Color.decode("#2C2C2C"));
        tabelInventaris.setForeground(TEXT_PRIMARY);
        tabelInventaris.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tabelInventaris);
        scrollPane.getViewport().setBackground(Color.decode("#1E1E1E"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        tombolExport = createStyledButton("Export CSV", new Color[]{Color.decode("#00897B"), Color.decode("#009688")});
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(BG_BASE);
        bottom.add(tombolExport);
        tablePanel.add(bottom, BorderLayout.SOUTH);

        return tablePanel;
    }

    /** Helper: Membuat textfield bergaya khusus */
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.decode("#2C2C2C"));
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return field;
    }

    /** Helper: Membuat tombol bergaya khusus */
    private JButton createStyledButton(String text, Color[] colors) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(colors[0]);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }

    /** Setup listener event untuk interaksi user */
    private void setupEventListeners() {
        tombolTambah.addActionListener(e -> aksiTambah());
        tombolEdit.addActionListener(e -> aksiEdit());
        tombolHapus.addActionListener(e -> aksiHapus());
        tombolClear.addActionListener(e -> clearFormulir());
        tombolExit.addActionListener(e -> mainApp.showLoginView());
        tombolExport.addActionListener(e -> aksiExport());

        fieldCari.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { loadData(); }
        });

        tabelInventaris.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelInventaris.getSelectedRow() != -1) {
                int row = tabelInventaris.getSelectedRow();
                fieldKode.setText(modelTabel.getValueAt(row, 0).toString());
                fieldNama.setText(modelTabel.getValueAt(row, 1).toString());
                fieldJumlah.setText(modelTabel.getValueAt(row, 2).toString());
                fieldHarga.setText(modelTabel.getValueAt(row, 3).toString().replace("Rp", "").replace(".", "").replace(",00", ""));
            }
        });
    }

    /** Memuat data dari database ke tabel */
    private void loadData() {
        String keyword = fieldCari.getText().trim(); 
        modelTabel.setRowCount(0);
        String sqlData = "SELECT * FROM inventaris WHERE pemilik = ? ORDER BY kode";
        if (!keyword.isEmpty()) {
            sqlData = "SELECT * FROM inventaris WHERE pemilik = ? AND (nama LIKE ? OR kode LIKE ?) ORDER BY kode";
        }

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sqlData)) {
            pstmt.setString(1, currentUser);
            if (!keyword.isEmpty()) {
                pstmt.setString(2, "%" + keyword + "%");
                pstmt.setString(3, "%" + keyword + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                modelTabel.addRow(new Object[]{
                    rs.getString("kode"), rs.getString("nama"),
                    rs.getInt("jumlah"), rs.getDouble("harga")
                });
            }
            calculateGlobalStats();
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** Menghitung statistik global (total item & aset) */
    private void calculateGlobalStats() {
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*), SUM(jumlah * harga) FROM inventaris WHERE pemilik = ?")) {
            pstmt.setString(1, currentUser);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                labelTotalItem.setText(rs.getInt(1) + " Item");
                labelTotalAset.setText(kursIndonesia.format(rs.getDouble(2)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /** Logika aksi tambah data */
    private void aksiTambah() {
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO inventaris (kode, nama, jumlah, harga, pemilik) VALUES (?, ?, ?, ?, ?)")) {
            pstmt.setString(1, fieldKode.getText());
            pstmt.setString(2, fieldNama.getText());
            pstmt.setInt(3, Integer.parseInt(fieldJumlah.getText()));
            pstmt.setDouble(4, Double.parseDouble(fieldHarga.getText()));
            pstmt.setString(5, currentUser);
            pstmt.executeUpdate();
            loadData(); clearFormulir();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    /** Logika aksi edit data */
    private void aksiEdit() {
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE inventaris SET nama=?, jumlah=?, harga=? WHERE kode=? AND pemilik=?")) {
            pstmt.setString(1, fieldNama.getText());
            pstmt.setInt(2, Integer.parseInt(fieldJumlah.getText()));
            pstmt.setDouble(3, Double.parseDouble(fieldHarga.getText()));
            pstmt.setString(4, fieldKode.getText());
            pstmt.setString(5, currentUser);
            pstmt.executeUpdate();
            loadData(); clearFormulir();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    /** Logika aksi hapus data */
    private void aksiHapus() {
        if (JOptionPane.showConfirmDialog(this, "Hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = KoneksiDatabase.getKoneksi();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM inventaris WHERE kode=? AND pemilik=?")) {
                pstmt.setString(1, fieldKode.getText());
                pstmt.setString(2, currentUser);
                pstmt.executeUpdate();
                loadData(); clearFormulir();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }

    /** Logika export data ke CSV */
    private void aksiExport() {
        try (FileWriter fw = new FileWriter(new File("Export_Inventaris_" + currentUser + ".csv"))) {
            fw.write("Kode,Nama,Jumlah,Harga\n");
            for (int i = 0; i < modelTabel.getRowCount(); i++) {
                fw.write(modelTabel.getValueAt(i, 0) + "," + modelTabel.getValueAt(i, 1) + "," 
                       + modelTabel.getValueAt(i, 2) + "," + modelTabel.getValueAt(i, 3) + "\n");
            }
            JOptionPane.showMessageDialog(this, "Data berhasil diexport!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal export: " + e.getMessage()); }
    }

    /** Membersihkan form input */
    private void clearFormulir() {
        fieldKode.setText(""); fieldNama.setText(""); fieldJumlah.setText(""); fieldHarga.setText("");
    }
}