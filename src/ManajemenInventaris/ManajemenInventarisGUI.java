package ManajemenInventaris;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Aplikasi Manajemen Inventaris
 */
public class ManajemenInventarisGUI extends JPanel {

    private MainApp mainApp;
    private String currentUser;
    
    /**
     * Main GUI class for the inventory application.
     * Contains Swing components, layout, event handlers and helper methods.
     * 
     * UI components created here are reused across methods (form fields, buttons, table).
     * Styling constants are defined for easy theme adjustments.
     * Database calls are performed in-place; consider moving to background thread for large datasets.
     */

    // ============ UI COMPONENTS ============
    private JTable tabelInventaris;
    private DefaultTableModel modelTabel;
    private JTextField fieldKode, fieldNama, fieldJumlah, fieldHarga, fieldCari;
    private JButton tombolTambah, tombolEdit, tombolHapus, tombolClear, tombolExit, tombolExport;
    private JLabel labelTotalItem, labelTotalAset;

    // ============ CONSTANTS & STYLING ============
    private final String[] namaKolom = {"Kode Barang", "Nama Barang", "Jumlah (Stok)", "Harga (Rp)"};
    @SuppressWarnings("deprecation")
    private final NumberFormat kursIndonesia = NumberFormat.getCurrencyInstance(new Locale("id","ID"));
    
    /** Dark Mode Colors */
    private static final Color BG_BASE = Color.decode("#121212");       
    private static final Color BG_PANEL = Color.decode("#1E1E1E");      
    private static final Color TEXT_PRIMARY = Color.decode("#FFFFFF");  
    private static final Color TEXT_SECONDARY = Color.decode("#BDBDBD");
    private static final Color BORDER_SOFT = Color.decode("#2C2C2C");
    private static final Color BORDER_FOCUSED = Color.decode("#3D5AFE");
    private static final Color INPUT_BG = Color.decode("#1E1E1E");
    private static final Color INPUT_BORDER = Color.decode("#2C2C2C");
    private static final Color INPUT_TEXT = Color.decode("#FFFFFF");

    /** Fonts */
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    /**
     * Creates a ManajemenInventarisGUI panel.
     * @param app reference to the main application
     * @param username the username of the logged-in user
     */
    public ManajemenInventarisGUI(MainApp app, String username) {
        this.mainApp = app;
        this.currentUser = username;

        this.setBackground(BG_BASE); 
        
        KoneksiDatabase.buatTabelJikaBelumAda();
        initComponents();
        loadData();
    }

    /**
     * Initialize and layout all UI components (header, dashboard, main content, footer).
     */
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));

        // Header & Dashboard
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setBackground(BG_BASE);
        topWrapper.add(createHeaderPanel(), BorderLayout.NORTH);
        topWrapper.add(createDashboardPanel(), BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        // Main Content
        add(createMainContentPanel(), BorderLayout.CENTER);

        setupEventListeners();
    }

    /**
     * Create top header with logo and application title.
     * @return the header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setBackground(BG_PANEL);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblJudul = new JLabel("User: " + currentUser);
        lblJudul.setFont(HEADER_FONT);
        lblJudul.setForeground(TEXT_PRIMARY);
        headerPanel.add(lblJudul, BorderLayout.WEST);
        
        tombolExit = createStyledButton("Logout", new Color[]{Color.decode("#D32F2F"), Color.decode("#E57373"), Color.decode("#B71C1C")});
        tombolExit.setPreferredSize(new Dimension(80, 35));
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        logoutPanel.setBackground(BG_PANEL);
        logoutPanel.add(tombolExit);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        
        return headerPanel;
    }

    /**
     * Create dashboard panel with summary cards.
     * @return the dashboard panel
     */
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

    /**
     * Construct a small dashboard card with an accent bar.
     * @param title the card title
     * @param valueLabel the value label
     * @param accentColor the accent color
     * @return the card panel
     */
    private JPanel createCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_SOFT),
            BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor)
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

    /**
     * Compose main content: form (north) and table (center).
     * @return the main content panel
     */
    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BG_BASE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        mainPanel.add(createFormPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * Build the input form (Kode, Nama, Jumlah, Harga).
     * @return the form panel
     */
    private JPanel createFormPanel() {
        JPanel topContainer = new JPanel(new GridBagLayout());
        topContainer.setBackground(BG_PANEL);
        topContainer.setBorder(new CompoundBorder(
            new LineBorder(BORDER_SOFT, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        fieldKode = createStyledTextField();
        fieldNama = createStyledTextField();
        fieldJumlah = createStyledTextField();
        fieldHarga = createStyledTextField();

        fieldKode.addActionListener(e -> fieldNama.requestFocus());
        fieldNama.addActionListener(e -> fieldJumlah.requestFocus());
        fieldJumlah.addActionListener(e -> fieldHarga.requestFocus());

        setNumericFilter(fieldJumlah);
        setNumericFilter(fieldHarga);

        setCurrencyMask(fieldHarga);

        addFormRowCustom(topContainer, gbc, 0, "Kode Barang:", fieldKode);
        addFormRowCustom(topContainer, gbc, 1, "Nama Barang:", fieldNama);
        addFormRowCustom(topContainer, gbc, 2, "Jumlah Stok:", fieldJumlah);
        addFormRowCustom(topContainer, gbc, 3, "Harga Satuan:", fieldHarga);

        JPanel formButtonPanel = createButtonPanel();
        GridBagConstraints gbcFooter = new GridBagConstraints();
        gbcFooter.gridx = 0; gbcFooter.gridy = 4; gbcFooter.gridwidth = 2; gbcFooter.fill = GridBagConstraints.HORIZONTAL;
        topContainer.add(formButtonPanel, gbcFooter);

        return topContainer;
    }

    /**
     * Create action buttons and set preferred sizes.
     * @return the button panel
     */
    private JPanel createButtonPanel() {
        JPanel formButtonPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        formButtonPanel.setBackground(BG_PANEL);
        formButtonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        tombolTambah = createStyledButton("Simpan", new Color[]{Color.decode("#1E88E5"), Color.decode("#2196F3"), Color.decode("#1565C0")});
        tombolEdit = createStyledButton("Update", new Color[]{Color.decode("#43A047"), Color.decode("#4CAF50"), Color.decode("#2E7D32")});
        tombolHapus = createStyledButton("Hapus", new Color[]{Color.decode("#E53935"), Color.decode("#EF5350"), Color.decode("#C62828")});
        tombolClear = createStyledButton("Clear Form", new Color[]{Color.decode("#6C757D"), Color.decode("#7C8790"), Color.decode("#565E64")});

        tombolClear.setPreferredSize(new Dimension(300, 35));
        Dimension aksiSize = new Dimension(200, 35);
        tombolTambah.setPreferredSize(aksiSize);
        tombolEdit.setPreferredSize(aksiSize);
        tombolHapus.setPreferredSize(aksiSize);

        JPanel row1Panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row1Panel.setBackground(BG_PANEL);
        row1Panel.add(tombolClear);

        JPanel row2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        row2Panel.setBackground(BG_PANEL);
        row2Panel.add(tombolTambah);
        row2Panel.add(tombolEdit);
        row2Panel.add(tombolHapus);

        formButtonPanel.add(row1Panel);
        formButtonPanel.add(row2Panel);
        return formButtonPanel;
    }

    /**
     * Create searchable table panel and export controls (no pagination).
     * @return the table panel
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(BG_BASE);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(BG_BASE);
        JLabel lblCari = new JLabel("Cari Barang: ");
        lblCari.setForeground(TEXT_SECONDARY);
        lblCari.setFont(LABEL_FONT);
        searchPanel.add(lblCari, BorderLayout.WEST);
        
        fieldCari = createStyledTextField();
        fieldCari.putClientProperty("JTextField.placeholderText", "Ketik nama atau kode barang...");
        searchPanel.add(fieldCari, BorderLayout.CENTER);
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        modelTabel = new DefaultTableModel(namaKolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 2: return Integer.class;
                    case 3: return Double.class;
                    default: return String.class;
                }
            }
        };
        
        tabelInventaris = new JTable(modelTabel);
        tabelInventaris.setAutoCreateRowSorter(true);
        setupTableStyle();

        JScrollPane scrollPane = new JScrollPane(tabelInventaris);
        scrollPane.setBorder(new LineBorder(BORDER_SOFT));
        scrollPane.getViewport().setBackground(Color.decode("#1E1E1E"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(BG_BASE);

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportPanel.setBackground(BG_BASE);
        tombolExport = createStyledButton("Export CSV", new Color[]{Color.decode("#00897B"), Color.decode("#009688"), Color.decode("#00695C")});
        tombolExport.setPreferredSize(new Dimension(150, 35));
        exportPanel.add(tombolExport);

        bottomPanel.add(exportPanel, BorderLayout.EAST);
        tablePanel.add(bottomPanel, BorderLayout.SOUTH);

        return tablePanel;
    }

    // ============ LOGIC & EVENTS ============

    /**
     * Attach action handlers for buttons, search field, and table selection.
     */
    private void setupEventListeners() {
        tombolTambah.addActionListener(e -> aksiTambah());
        tombolEdit.addActionListener(e -> aksiEdit());
        tombolHapus.addActionListener(e -> aksiHapus());
        tombolExport.addActionListener(e -> aksiExport());
        tombolClear.addActionListener(e -> clearFormulir());
        
        tombolExit.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Logout dari akun?", "Konfirmasi", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                    mainApp.showLoginView();
            }
        });

        fieldCari.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadData();
            }
        });

        tabelInventaris.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelInventaris.getSelectedRow() != -1) {
                isiFormulirDariTabel();
            }
        });
    }

    /**
     * Load (filtered) data from the database into the table model.
     * Note: synchronous DB call; consider moving to a SwingWorker for heavy datasets.
     */
    private void loadData() {
        String keyword = fieldCari.getText().trim(); // Tambahkan .trim() untuk hapus spasi
        modelTabel.setRowCount(0);

        /** Default query: Ambil semua data milik user ini */
        String sqlData = "SELECT * FROM inventaris WHERE pemilik = ? ORDER BY kode";

        /** Jika ada pencarian, ganti querynya */
        if (!keyword.isEmpty()) {
            sqlData = "SELECT * FROM inventaris WHERE pemilik = ? AND (nama LIKE ? OR kode LIKE ?) ORDER BY kode";
        }

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sqlData)) {

            pstmt.setString(1, currentUser); // Parameter 1 selalu pemilik

            /** Jika sedang mencari, set parameter tambahan */
            if (!keyword.isEmpty()) {
                pstmt.setString(2, "%" + keyword + "%");
                pstmt.setString(3, "%" + keyword + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                modelTabel.addRow(new Object[]{
                    rs.getString("kode"),
                    rs.getString("nama"),
                    rs.getInt("jumlah"),
                    rs.getDouble("harga")
                });
            }

            /** Update statistik juga biar sinkron */
            calculateGlobalStats();

        } catch (Exception e) {
            e.printStackTrace(); // Biar kelihatan errornya di terminal
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        }
    }

    /**
     * Calculate and update dashboard statistics: total items and total assets.
     */
    private void calculateGlobalStats() {
        /** UBAH QUERY: Tambahkan WHERE pemilik = ? */
        String sql = "SELECT COUNT(*), SUM(jumlah * harga) FROM inventaris WHERE pemilik = ?";
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) { /** Ganti Statement jadi PreparedStatement */
            
            pstmt.setString(1, currentUser); /** Filter User */
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                labelTotalItem.setText(rs.getInt(1) + " Item");
                double aset = rs.getDouble(2);
                labelTotalAset.setText(kursIndonesia.format(aset));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Handle insert (Simpan) action: validate, insert into DB, refresh table.
     */
    private void aksiTambah() {
        if (!validasiInput()) return;
        /** UBAH QUERY: Tambah kolom pemilik */
        String sql = "INSERT INTO inventaris (kode, nama, jumlah, harga, pemilik) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fieldKode.getText());
            pstmt.setString(2, fieldNama.getText());
            pstmt.setInt(3, Integer.parseInt(fieldJumlah.getText()));
            pstmt.setDouble(4, getDoubleFromHargaField());
            pstmt.setString(5, currentUser); /** Masukkan username yang login */
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Berhasil disimpan!");
            loadData(); 
            clearFormulir();
        } catch (SQLException e) { handleSQLError(e); }
    }

    /**
     * Handle edit (Update) action for the selected row.
     */
    private void aksiEdit() {
        if (tabelInventaris.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris dulu!"); return;
        }
        if (!validasiInput()) return;
        
        String kode = fieldKode.getText();
        String sql = "UPDATE inventaris SET nama=?, jumlah=?, harga=? WHERE kode=? AND pemilik=?";

        try (Connection conn = KoneksiDatabase.getKoneksi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fieldNama.getText());
            pstmt.setInt(2, Integer.parseInt(fieldJumlah.getText()));
            pstmt.setDouble(3, getDoubleFromHargaField());
            pstmt.setString(4, kode);
            pstmt.setString(5, currentUser); 
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!");
            loadData(); 
            clearFormulir();
        } catch (SQLException e) { handleSQLError(e); }
    }

    /**
     * Handle delete (Hapus) action with confirmation dialog.
     */
    private void aksiHapus() {
        int rowView = tabelInventaris.getSelectedRow();
        if (rowView == -1) { JOptionPane.showMessageDialog(this, "Pilih baris terlebih dahulu!"); return; }
        
        int rowModel = tabelInventaris.convertRowIndexToModel(rowView);
        String kode = modelTabel.getValueAt(rowModel, 0).toString();
        
        int konfirmasi = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menghapus data: " + kode + "?\nData yang dihapus tidak bisa dikembalikan.", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE); 
            
        if (konfirmasi == JOptionPane.YES_OPTION) {
            try (Connection conn = KoneksiDatabase.getKoneksi();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM inventaris WHERE kode=? AND pemilik=?")) {
                pstmt.setString(1, kode);
                pstmt.setString(2, currentUser);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                loadData(); 
                clearFormulir();
            } catch (SQLException e) { handleSQLError(e); }
        }
    }

    /**
     * Parse numeric value from the formatted price field (remove 'Rp' and separators).
     * @return the numeric value
     */
    private double getDoubleFromHargaField() {
        String text = fieldHarga.getText().replaceAll("[^0-9]", ""); /** Hapus 'Rp', titik, koma */
        if (text.isEmpty()) return 0;
        return Double.parseDouble(text);
    }

    /**
     * Apply currency mask: show raw digits while editing, format as Rupiah when focus lost.
     * @param field the field to apply masking to
     */
    private void setCurrencyMask(JTextField field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String text = field.getText().replaceAll("[^0-9]", "");
                field.setText(text);
            }
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    String text = field.getText().replaceAll("[^0-9]", "");
                    if (!text.isEmpty()) {
                        double val = Double.parseDouble(text);
                        field.setText(kursIndonesia.format(val).replace(",00", "")); // Format cantik
                    }
                } catch (NumberFormatException ex) {}
            }
        });
    }

    /**
     * Populate form fields from the selected table row and switch UI to edit mode.
     */
    private void isiFormulirDariTabel() {
        int rowView = tabelInventaris.getSelectedRow();
        if (rowView != -1) {
            int rowModel = tabelInventaris.convertRowIndexToModel(rowView);
            
            fieldKode.setText(modelTabel.getValueAt(rowModel, 0).toString());
            fieldNama.setText(modelTabel.getValueAt(rowModel, 1).toString());
            fieldJumlah.setText(modelTabel.getValueAt(rowModel, 2).toString());
            
            double harga = (Double) modelTabel.getValueAt(rowModel, 3);
            fieldHarga.setText(kursIndonesia.format(harga).replace(",00", ""));

            fieldKode.setEditable(false);
            fieldKode.setBackground(Color.decode("#252525"));
            tombolTambah.setEnabled(false);
            tombolEdit.setEnabled(true);
        }
    }

    /**
     * Clear all form fields and reset button states.
     */
    private void clearFormulir() {
        fieldKode.setText(""); fieldNama.setText(""); fieldJumlah.setText(""); fieldHarga.setText(""); fieldCari.setText("");
        tabelInventaris.clearSelection();
        fieldKode.setEditable(true);
        fieldKode.setBackground(INPUT_BG);
        tombolTambah.setEnabled(true);
        tombolEdit.setEnabled(true);
    }
    
    /**
     * Helper to add a labeled form row to the GridBagLayout-based form.
     * @param panel the panel to add to
     * @param gbc the GridBagConstraints
     * @param row the row index
     * @param labelText the label text
     * @param field the form field component
     */
    private void addFormRowCustom(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component field) {
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_SECONDARY);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.1; gbc.anchor = GridBagConstraints.EAST;
        panel.add(label, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.9; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(field, gbc);
    }

    /**
     * Create a styled JTextField used throughout the form.
     * Provides consistent height, colors, and focus border styling.
     * @return the styled text field
     */
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(INPUT_FONT);
        field.setPreferredSize(new Dimension(0, 35));
        field.setBackground(INPUT_BG);
        field.setForeground(INPUT_TEXT);
        field.setCaretColor(TEXT_PRIMARY);
        
        Border paddingBorder = new EmptyBorder(0, 10, 0, 10);
        field.setBorder(new CompoundBorder(new LineBorder(INPUT_BORDER, 1), paddingBorder));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { 
                field.setBorder(new CompoundBorder(new LineBorder(BORDER_FOCUSED, 2), paddingBorder)); 
            }
            public void focusLost(FocusEvent e) { 
                field.setBorder(new CompoundBorder(new LineBorder(INPUT_BORDER, 1), paddingBorder)); 
            }
        });
        return field;
    }

    /**
     * Create a styled JButton with simple hover and pressed color states.
     * @param text the button text
     * @param colors array of colors [base, hover, pressed]
     * @return the styled button
     */
    private JButton createStyledButton(String text, Color[] colors) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(colors[0]);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(colors[1]); }
            public void mouseExited(MouseEvent e) { btn.setBackground(colors[0]); }
            public void mousePressed(MouseEvent e) { btn.setBackground(colors[2]); }
        });
        return btn;
    }
    
    /**
     * Enforce digit-only input for numeric fields (jumlah, harga while editing).
     * @param textField the field to apply the filter to
     */
    private void setNumericFilter(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar()) && e.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
                    e.consume(); Toolkit.getDefaultToolkit().beep();
                }
            }
        });
    }
    
    /**
     * Validate required inputs and numeric parsing before DB operations.
     * @return true if validation passes, false otherwise
     */
    private boolean validasiInput() {
        if (fieldKode.getText().isEmpty() || fieldNama.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode dan Nama wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(fieldJumlah.getText());
            if (getDoubleFromHargaField() < 0) return false; 
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Input angka salah!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    /**
     * Display friendly messages for common SQL errors (e.g., duplicate primary key).
     * @param e the SQLException
     */
    private void handleSQLError(SQLException e) {
        if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) JOptionPane.showMessageDialog(this, "Kode Barang sudah ada!");
        else JOptionPane.showMessageDialog(this, "Error Database: " + e.getMessage());
    }
    
    /**
     * Export current table data to CSV file chosen by the user.
     */
    private void aksiExport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan ke CSV");
        String userHome = System.getProperty("user.home");
        File folderKhusus = new File(userHome + File.separator + "Documents" + File.separator + "Laporan_Inventaris");
        if (!folderKhusus.exists()) folderKhusus.mkdirs();
        fileChooser.setCurrentDirectory(folderKhusus);
        fileChooser.setSelectedFile(new File("Laporan_Inventaris.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter fw = new java.io.FileWriter(fileChooser.getSelectedFile())) {
                for (int i = 0; i < modelTabel.getColumnCount(); i++) fw.write(modelTabel.getColumnName(i) + (i==modelTabel.getColumnCount()-1?"":","));
                fw.write("\n");
                for (int i = 0; i < modelTabel.getRowCount(); i++) {
                    for (int j = 0; j < modelTabel.getColumnCount(); j++) {
                        String data = modelTabel.getValueAt(i, j).toString().replace(",", "");
                        fw.write(data + (j==modelTabel.getColumnCount()-1?"":","));
                    }
                    fw.write("\n");
                }
                JOptionPane.showMessageDialog(this, "Export Berhasil!");
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal export: " + e.getMessage()); }
        }
    }
    
    /**
     * Configure table appearance: header renderer with sort marker, striped rows and currency display.
     */
    private void setupTableStyle() {
        tabelInventaris.setRowHeight(35);
        tabelInventaris.setShowVerticalLines(false);
        tabelInventaris.setGridColor(Color.decode("#383838"));
        tabelInventaris.setBackground(Color.decode("#1E1E1E"));
        tabelInventaris.setForeground(Color.WHITE);
        tabelInventaris.setSelectionBackground(Color.decode("#333C57"));
        tabelInventaris.setSelectionForeground(Color.WHITE);
        
        JTableHeader header = tabelInventaris.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Color.decode("#2C2C2C"));
        header.setForeground(Color.WHITE);
        
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.decode("#2C2C2C"));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.decode("#383838")));
                
                int sortColumn = -1;
                javax.swing.SortOrder sortOrder = null;
                if (table.getRowSorter() != null) {
                    java.util.List<? extends javax.swing.RowSorter.SortKey> sortKeys = table.getRowSorter().getSortKeys();
                    if (!sortKeys.isEmpty()) {
                        sortColumn = sortKeys.get(0).getColumn();
                        sortOrder = sortKeys.get(0).getSortOrder();
                    }
                }
                
                if (column == sortColumn && sortOrder != null) {
                    String arrow = sortOrder == javax.swing.SortOrder.ASCENDING ? " ▲" : " ▼";
                    label.setText(value + arrow);
                    label.setForeground(Color.decode("#FFD700"));
                } else {
                    label.setText(value.toString());
                    label.setForeground(Color.WHITE);
                }
                return label;
            }
        });
        
        DefaultTableCellRenderer stripeRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.decode("#1E1E1E") : Color.decode("#232323"));
                
                if (column == 2) setHorizontalAlignment(CENTER);
                else if (column == 3) setHorizontalAlignment(RIGHT);
                else setHorizontalAlignment(LEFT);
                
                if (column == 3 && value instanceof Double) setText(kursIndonesia.format(value));
                return c;
            }
        };
        for (int i = 0; i < tabelInventaris.getColumnCount(); i++) {
            tabelInventaris.getColumnModel().getColumn(i).setCellRenderer(stripeRenderer);
        }
    }
}