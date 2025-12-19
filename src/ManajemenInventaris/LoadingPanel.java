package ManajemenInventaris;

import javax.swing.*;
import java.awt.*;

/**
 * Panel transisi yang menampilkan animasi loading dan progress bar.
 * Digunakan saat aplikasi melakukan proses asinkron (background task).
 */
public class LoadingPanel extends JPanel {

    /** Komponen progress bar untuk visualisasi persentase */
    private JProgressBar progressBar;
    
    /** Label untuk menampilkan teks status loading */
    private JLabel lblText;

    /**
     * Inisialisasi komponen UI LoadingPanel.
     * Mengatur layout, warna background, dan properti progress bar.
     */
    public LoadingPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.decode("#121212")); 

        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(300, 25));
        
        progressBar.setForeground(Color.decode("#3D5AFE")); 
        progressBar.setBackground(Color.decode("#1E1E1E"));
        progressBar.setBorderPainted(false);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        lblText = new JLabel("Sedang memuat data...", SwingConstants.CENTER);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblText.setForeground(Color.WHITE);

        content.add(lblText, BorderLayout.NORTH);
        content.add(progressBar, BorderLayout.CENTER);

        add(content);
    }

    /**
     * Mengupdate nilai progress bar dan teks status secara dinamis.
     * @param value Nilai progress (0-100).
     */
    public void setProgress(int value) {
        progressBar.setValue(value);
        if (value < 30) lblText.setText("Menghubungkan ke Database...");
        else if (value < 70) lblText.setText("Mengambil Data Inventaris...");
        else if (value < 90) lblText.setText("Menyiapkan Tampilan...");
        else lblText.setText("Selesai!");
    }
}