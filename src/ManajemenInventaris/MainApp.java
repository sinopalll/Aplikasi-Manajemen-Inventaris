package ManajemenInventaris;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoadingPanel loadingPanel;

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

        loadingPanel = new LoadingPanel(); // Inisialisasi di sini

        mainPanel.add(new LoginPanel(this), "LOGIN");
        mainPanel.add(loadingPanel, "LOADING"); // Tambahkan panel yang sudah disimpan

        add(mainPanel);

        add(mainPanel);
        
        /** Tampilkan Login dulu */
        showLoginView();
    }

    /**
     * Switch to the login view.
     */
    public void showLoginView() {
        cardLayout.show(mainPanel, "LOGIN");
    }

    /**
     * Switch to the inventory view for the given user.
     * Creates a new inventory panel session for each login.
     * @param username the username of the logged-in user
     */
    public void showInventoryView(String username) {
        
        // Reset progress bar ke 0 setiap kali mau loading
        loadingPanel.setProgress(0);
        cardLayout.show(mainPanel, "LOADING");

        // SwingWorker<Result, ProgressType> -> Kita pakai Integer untuk progress
        SwingWorker<ManajemenInventarisGUI, Integer> worker = new SwingWorker<>() {
            
            @Override
            protected ManajemenInventarisGUI doInBackground() throws Exception {
                // Simulasi Loading Bertahap (Total 2 Detik)
                
                // Tahap 1: Koneksi (0-30%)
                publish(10); Thread.sleep(200);
                publish(30); Thread.sleep(500);

                // Tahap 2: Load Data (30-70%)
                publish(50); Thread.sleep(500);
                publish(70); 

                // Proses Berat Asli (Load GUI & Data)
                ManajemenInventarisGUI gui = new ManajemenInventarisGUI(MainApp.this, username);
                
                // Tahap 3: Finalisasi (70-100%)
                publish(90); Thread.sleep(300);
                publish(100); Thread.sleep(200); // Jeda dikit biar user lihat 100%

                return gui;
            }

            // Method ini jalan di UI Thread setiap kali kita panggil publish()
            @Override
            protected void process(java.util.List<Integer> chunks) {
                // Ambil update terakhir
                int latestProgress = chunks.get(chunks.size() - 1);
                loadingPanel.setProgress(latestProgress);
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
                    JOptionPane.showMessageDialog(MainApp.this, "Gagal: " + e.getMessage());
                    showLoginView();
                }
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}