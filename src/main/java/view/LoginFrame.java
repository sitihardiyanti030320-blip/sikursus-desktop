package view;

import dao.UserDAO;
import java.sql.SQLException;
import model.User;
import util.Theme;
import util.UserSession;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox cbTampilkanPassword;

    private JButton btnLogin;
    private JLabel lblKeluar;

    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {

        setTitle("Login - SIKURSUS");
        setSize(900, 560);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                keluarAplikasi();
            }
        });
    }

    // ==============================================
    // LATAR BERGRADASI PINK DENGAN DEKORASI
    // ==============================================

    private static class BackgroundPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        BackgroundPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setPaint(new GradientPaint(
                    0, 0, Theme.SIDEBAR_DARK,
                    w, h, Theme.ACCENT));
            g2.fillRect(0, 0, w, h);

            // lingkaran dekoratif
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillOval(-120, -120, 380, 380);
            g2.setColor(new Color(255, 255, 255, 22));
            g2.fillOval(w - 260, h - 200, 360, 360);
            g2.setColor(new Color(255, 255, 255, 18));
            g2.fillOval(w / 2 - 80, -90, 200, 200);

            // titik-titik kecil
            g2.setColor(new Color(255, 255, 255, 60));
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 4; j++) {
                    g2.fillOval(40 + i * 22, h - 130 + j * 22, 5, 5);
                }
            }

            g2.dispose();
        }
    }

    // ==============================================
    // KARTU LOGIN (PUTIH, BULAT, BERBAYANG)
    // ==============================================

    private static class CardPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        CardPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int m = 10; // ruang untuk bayangan

            // bayangan lembut
            for (int i = 0; i < m; i++) {
                int alpha = 4 + i * 2;
                g2.setColor(new Color(90, 20, 55, alpha));
                g2.fillRoundRect(i, i + 4, w - 2 * i, h - 2 * i - 4,
                        34, 34);
            }

            // badan kartu
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(m, m, w - 2 * m, h - 2 * m - 4, 30, 30);

            // garis gradasi di bagian atas kartu
            g2.setClip(new RoundRectangle2D.Float(
                    m, m, w - 2 * m, h - 2 * m - 4, 30, 30));
            g2.setPaint(new GradientPaint(
                    m, 0, Theme.ACCENT, w - m, 0, Theme.LIGHT_BROWN));
            g2.fillRect(m, m, w - 2 * m, 7);

            g2.dispose();
        }
    }

    private void initComponents() {

        // ==========================================
        // PANEL UTAMA (LATAR PINK)
        // ==========================================

        BackgroundPanel panelUtama = new BackgroundPanel(new GridBagLayout());

        // ==========================================
        // BRANDING (KIRI)
        // ==========================================

        JPanel panelBrand = new JPanel();
        panelBrand.setOpaque(false);
        panelBrand.setLayout(new BoxLayout(panelBrand, BoxLayout.Y_AXIS));
        panelBrand.setBorder(new EmptyBorder(0, 10, 0, 10));

        JComponent logo = new JComponent() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // cincin luar transparan
                g2.setColor(new Color(255, 255, 255, 70));
                g2.fillOval(0, 0, w - 1, h - 1);

                // lingkaran putih
                g2.setColor(Color.WHITE);
                g2.fillOval(8, 8, w - 17, h - 17);

                // huruf S
                g2.setColor(Theme.DARK_BROWN);
                g2.setFont(new Font("Serif", Font.BOLD, 38));
                FontMetrics fm = g2.getFontMetrics();
                String t = "S";
                g2.drawString(t,
                        (w - fm.stringWidth(t)) / 2,
                        (h - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        Dimension logoSize = new Dimension(86, 86);
        logo.setPreferredSize(logoSize);
        logo.setMinimumSize(logoSize);
        logo.setMaximumSize(logoSize);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNama = new JLabel("SIKURSUS");
        lblNama.setFont(new Font("Serif", Font.BOLD, 44));
        lblNama.setForeground(Color.WHITE);
        lblNama.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDeskripsi = new JLabel("Sistem Informasi Kursus");
        lblDeskripsi.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblDeskripsi.setForeground(new Color(0xFFE6F0));
        lblDeskripsi.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblKeterangan = new JLabel(
                "Belajar, Berkembang, dan Berkarya");
        lblKeterangan.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblKeterangan.setForeground(new Color(0xFFD3E4));
        lblKeterangan.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel garis = new JPanel();
        garis.setBackground(new Color(255, 255, 255, 170));
        garis.setMaximumSize(new Dimension(70, 4));
        garis.setPreferredSize(new Dimension(70, 4));
        garis.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelBrand.add(logo);
        panelBrand.add(Box.createVerticalStrut(22));
        panelBrand.add(lblNama);
        panelBrand.add(Box.createVerticalStrut(10));
        panelBrand.add(garis);
        panelBrand.add(Box.createVerticalStrut(14));
        panelBrand.add(lblDeskripsi);
        panelBrand.add(Box.createVerticalStrut(6));
        panelBrand.add(lblKeterangan);

        // ==========================================
        // KARTU FORM LOGIN (KANAN)
        // ==========================================

        CardPanel panelForm = new CardPanel(new GridBagLayout());
        panelForm.setBorder(new EmptyBorder(38, 46, 36, 46));
        panelForm.setPreferredSize(new Dimension(410, 440));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ----- judul -----
        JLabel lblJudul = new JLabel("Selamat Datang");
        lblJudul.setFont(new Font("Serif", Font.BOLD, 28));
        lblJudul.setForeground(Theme.DARK_BROWN);
        lblJudul.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        panelForm.add(lblJudul, gbc);

        JLabel lblSubjudul = new JLabel("Silakan login untuk melanjutkan");
        lblSubjudul.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSubjudul.setForeground(Theme.MUTED);
        lblSubjudul.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        panelForm.add(lblSubjudul, gbc);

        // ----- username -----
        JLabel lblUsername = new JLabel("USERNAME");
        lblUsername.setFont(Theme.font(Font.BOLD, 11));
        lblUsername.setForeground(Theme.MID_BROWN);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 3, 0);
        panelForm.add(lblUsername, gbc);

        txtUsername = new JTextField();
        Theme.styleTextField(txtUsername);
        txtUsername.setPreferredSize(new Dimension(300, 40));

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 16, 0);
        panelForm.add(txtUsername, gbc);

        // ----- password -----
        JLabel lblPassword = new JLabel("PASSWORD");
        lblPassword.setFont(Theme.font(Font.BOLD, 11));
        lblPassword.setForeground(Theme.MID_BROWN);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 3, 0);
        panelForm.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        Theme.stylePasswordField(txtPassword);
        txtPassword.setPreferredSize(new Dimension(300, 40));

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 6, 0);
        panelForm.add(txtPassword, gbc);

        // ----- checkbox tampilkan password -----
        cbTampilkanPassword = new JCheckBox("Tampilkan password");
        cbTampilkanPassword.setFont(Theme.font(Font.PLAIN, 11));
        cbTampilkanPassword.setForeground(Theme.MID_BROWN);
        cbTampilkanPassword.setOpaque(false);
        cbTampilkanPassword.setFocusPainted(false);
        cbTampilkanPassword.setBorderPainted(false);
        cbTampilkanPassword.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final char echoChar = txtPassword.getEchoChar();

        cbTampilkanPassword.addActionListener(e -> {
            if (cbTampilkanPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar(echoChar);
            }
        });

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 20, 0);
        panelForm.add(cbTampilkanPassword, gbc);

        // ----- tombol login (lebar penuh, di tengah) -----
        btnLogin = Theme.createButton("LOGIN", Theme.BTN_SAVE);
        btnLogin.setPreferredSize(new Dimension(300, 44));

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 14, 0);
        panelForm.add(btnLogin, gbc);

        // ----- tautan keluar (di tengah) -----
        lblKeluar = new JLabel("Keluar dari aplikasi");
        lblKeluar.setFont(Theme.font(Font.BOLD, 12));
        lblKeluar.setForeground(Theme.MUTED);
        lblKeluar.setHorizontalAlignment(SwingConstants.CENTER);
        lblKeluar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        lblKeluar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                lblKeluar.setForeground(Theme.BTN_DELETE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblKeluar.setForeground(Theme.MUTED);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                keluarAplikasi();
            }
        });

        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 0, 0);
        panelForm.add(lblKeluar, gbc);

        // ==========================================
        // AKSI TOMBOL
        // ==========================================

        btnLogin.addActionListener(e -> prosesLogin());

        txtPassword.addActionListener(e -> prosesLogin());

        txtUsername.addActionListener(e -> txtPassword.requestFocus());

        getRootPane().setDefaultButton(btnLogin);

        // ==========================================
        // GABUNGKAN PANEL
        // ==========================================

        GridBagConstraints kiri = new GridBagConstraints();
        kiri.gridx = 0;
        kiri.gridy = 0;
        kiri.weightx = 1;
        kiri.anchor = GridBagConstraints.CENTER;
        panelUtama.add(panelBrand, kiri);

        GridBagConstraints kanan = new GridBagConstraints();
        kanan.gridx = 1;
        kanan.gridy = 0;
        kanan.weightx = 0;
        kanan.insets = new Insets(0, 0, 0, 40);
        kanan.anchor = GridBagConstraints.CENTER;
        panelUtama.add(panelForm, kanan);

        setContentPane(panelUtama);
    }

    // ==============================================
    // PROSES LOGIN
    // ==============================================

    private void prosesLogin() {

        String username = txtUsername.getText().trim();
        char[] password = txtPassword.getPassword();

        if (username.isEmpty() || password.length == 0) {
            Theme.showMessage(
                    this,
                    "Username dan password wajib diisi.",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE
            );
            Arrays.fill(password, '\0');
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnLogin.setEnabled(false);

        try {
            User user = userDAO.authenticate(username, password);

            if (user == null) {
                Theme.showMessage(
                        this,
                        "Login gagal. Periksa username, password, atau status user.",
                        "Login Gagal",
                        JOptionPane.WARNING_MESSAGE
                );
                txtPassword.setText("");
                txtPassword.requestFocus();
                return;
            }

            // Session harus di-set SEBELUM DashboardFrame dibuat.
            UserSession.setCurrentUser(user);
            dispose();

            SwingUtilities.invokeLater(() -> {
                DashboardFrame dashboard = new DashboardFrame();
                dashboard.setVisible(true);
            });

        } catch (SQLException ex) {
            Theme.showMessage(
                    this,
                    "Tidak dapat mengakses database: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            Arrays.fill(password, '\0');
            btnLogin.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    // ==============================================
    // KELUAR APLIKASI
    // ==============================================

    private void keluarAplikasi() {

        int pilihan = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin keluar dari aplikasi?",
                "Konfirmasi Keluar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (pilihan == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // ==============================================
    // MAIN
    // ==============================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}