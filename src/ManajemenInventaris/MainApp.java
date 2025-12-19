package ManajemenInventaris;

import javax.swing.*;
import java.awt.*;

/**
 * Controller utama aplikasi yang menggunakan arsitektur Single Window (CardLayout).
 * <p>
 * Mengatur navigasi antar panel dan menjalankan proses background (SwingWorker).
 */
public class MainApp extends JFrame {

    /** Layout manager untuk perpindahan halaman */
    private CardLayout cardLayout;
    
    /** Panel utama penampung kartu-kartu */
    private JPanel mainPanel;
    
    /** Panel loading screen */
    private LoadingPanel loadingPanel;

    /**
     * Konstruktor utama aplikasi. Menginisialisasi frame dan komponen.
     */
    public MainApp() {
        setTitle("Sistem Manajemen Inventaris");
        setSize(1100, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            ImageIcon icon = new ImageIcon("image/logo.png");
            setIconImage(icon.getImage());
        } catch (Exception ignored) {}

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        loadingPanel = new LoadingPanel(); 

        mainPanel.add(new LoginPanel(this), "LOGIN");
        mainPanel.add(loadingPanel, "LOADING");

        add(mainPanel);
        showLoginView();
    }

    /**
     * Menampilkan layar Login.
     */
    public void showLoginView() {
        cardLayout.show(mainPanel, "LOGIN");
    }

    /**
     * Memulai proses login dan memuat data inventaris secara Asinkron.
     * @param username Username pengguna yang login.
     */
    public void showInventoryView(String username) {
        loadingPanel.setProgress(0);
        cardLayout.show(mainPanel, "LOADING");

        SwingWorker<ManajemenInventarisGUI, Integer> worker = new SwingWorker<>() {
            @Override
            protected ManajemenInventarisGUI doInBackground() throws Exception {
                publish(10); Thread.sleep(300);
                publish(50); Thread.sleep(300);
                publish(70); 

                ManajemenInventarisGUI gui = new ManajemenInventarisGUI(MainApp.this, username);
                
                publish(100); Thread.sleep(200);
                return gui;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                loadingPanel.setProgress(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                try {
                    ManajemenInventarisGUI inventoryPanel = get();
                    String sessionKey = "INVENTARIS_" + System.currentTimeMillis();
                    mainPanel.add(inventoryPanel, sessionKey);
                    cardLayout.show(mainPanel, sessionKey);
                    setTitle("Aplikasi Manajemen Inventaris - User: " + username);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(MainApp.this, "Gagal memuat: " + e.getMessage());
                    showLoginView();
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Titik masuk utama (Main Entry Point) aplikasi.
     * @param args Argumen baris perintah.
     */
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}