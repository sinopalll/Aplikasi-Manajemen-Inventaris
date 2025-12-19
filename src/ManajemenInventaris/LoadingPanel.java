package ManajemenInventaris;

import javax.swing.*;
import java.awt.*;

public class LoadingPanel extends JPanel {

    private JProgressBar progressBar;
    private JLabel lblText;

    public LoadingPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.decode("#121212"));

        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);

        // Setup Progress Bar
        progressBar = new JProgressBar(0, 100); // Range 0 - 100
        progressBar.setValue(0);
        progressBar.setStringPainted(true); // Tampilkan teks persen (misal "50%")
        progressBar.setPreferredSize(new Dimension(300, 25)); // Sedikit lebih tebal
        
        // Styling Progress Bar
        progressBar.setForeground(Color.decode("#3D5AFE")); // Warna bar biru
        progressBar.setBackground(Color.decode("#1E1E1E")); // Warna background bar
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
     * Update nilai progress bar (0-100)
     */
    public void setProgress(int value) {
        progressBar.setValue(value);
        if (value < 30) lblText.setText("Menghubungkan ke Database...");
        else if (value < 70) lblText.setText("Mengambil Data Inventaris...");
        else if (value < 90) lblText.setText("Menyiapkan Tampilan...");
        else lblText.setText("Selesai!");
    }
}