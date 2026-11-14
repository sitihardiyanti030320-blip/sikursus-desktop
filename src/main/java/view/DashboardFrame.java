package view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import model.User;
import util.Theme;
import util.UserSession;
import util.WindowManager;

public class DashboardFrame extends JFrame {

    private JPanel mainPanel;
    private JPanel contentPanel;

    private JLabel lblUsername;
    private JLabel lblRole;
    private JLabel lblWelcome;
    private JLabel lblRoleDescription;

    private JPopupMenu popupMenu;
    private JScrollPane scrollPane;

    // Menu bar (Master, Transaksi, Laporan, Keluar)
    private JMenu menuMaster, menuTransaksi, menuLaporan, menuKeluar;
    private JMenuItem miPeserta, miInstruktur, miKursus, miKelas, miUser;
    private JMenuItem miPendaftaran, miPembayaran;
    private JMenuItem miLaporanPeserta, miLaporanPendaftaran;
    private JMenuItem miLogout, miTutup;

    private User currentUser;

    public DashboardFrame() {
        currentUser = UserSession.getCurrentUser();

        if (currentUser == null) {
            Theme.showMessage(
                    null,
                    "Session tidak ditemukan. Silakan login kembali.",
                    "Session Berakhir",
                    JOptionPane.WARNING_MESSAGE
            );

            SwingUtilities.invokeLater(() -> {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            });
            dispose();
            return;
        }

        initComponents();
        applyRoleAccess();

        setTitle("Dashboard - SIKURSUS Desktop");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeApplication();
            }
        });
    }

    // =====================================================
    // PANEL BULAT (dipakai kartu, kapsul, dan info akses)
    // =====================================================

    private static class RoundPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private final Color fill;
        private final Color line;
        private final Color stripe;
        private final int arc;

        RoundPanel(LayoutManager layout, Color fill, Color line,
                   Color stripe, int arc) {
            super(layout);
            this.fill = fill;
            this.line = line;
            this.stripe = stripe;
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

            if (stripe != null) {
                g2.setClip(new RoundRectangle2D.Float(
                        0, 0, w - 1, h - 1, arc, arc));
                g2.setColor(stripe);
                g2.fillRect(0, 0, w, 6);
                g2.setClip(null);
            }

            if (line != null) {
                g2.setColor(line);
                g2.setStroke(new BasicStroke(1.6f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);
            }

            g2.dispose();
        }
    }

    // =====================================================
    // INISIALISASI
    // =====================================================

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.CREAM);

        createPopupMenu();
        createMenuBar();
        createMainPanel();

        add(mainPanel, BorderLayout.CENTER);

        attachPopupMenu(getContentPane());
    }

    // =====================================================
    // POPUP MENU
    // =====================================================

    private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBackground(Theme.IVORY);
        popupMenu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(5, 5, 5, 5)
        ));

        JMenuItem itemTentang = new JMenuItem("Tentang Aplikasi");
        JMenuItem itemRefresh = new JMenuItem("Refresh Dashboard");

        stylePopupItem(itemTentang);
        stylePopupItem(itemRefresh);

        popupMenu.add(itemTentang);
        popupMenu.add(itemRefresh);

        itemTentang.addActionListener(e -> showAbout());
        itemRefresh.addActionListener(e -> refreshDashboard());
    }

    private void stylePopupItem(JMenuItem item) {
        item.setFont(Theme.font(Font.PLAIN, 12));
        item.setForeground(Theme.TEXT);
        item.setBackground(Theme.IVORY);
        item.setOpaque(true);
        item.setBorder(new EmptyBorder(7, 12, 7, 12));
        item.setPreferredSize(new Dimension(190, 32));
    }

    private void attachPopupMenu(Component component) {
        if (component instanceof JComponent) {
            ((JComponent) component).setComponentPopupMenu(popupMenu);
        }

        if (component instanceof Container) {
            Component[] children = ((Container) component).getComponents();

            for (Component child : children) {
                attachPopupMenu(child);
            }
        }
    }

    // =====================================================
    // TENTANG APLIKASI
    // =====================================================

    private void showAbout() {
        String message =
                "<html><div style='width:300px;'>"
                + "<h2 style='color:#C2467E;'>SIKURSUS Desktop</h2>"
                + "<p><b>Sistem Informasi Kursus dan Pelatihan</b></p>"
                + "<p>Aplikasi untuk membantu pengelolaan data peserta, "
                + "instruktur, kursus, kelas, pendaftaran, dan pembayaran.</p>"
                + "<hr>"
                + "<p>Versi Aplikasi: 1.0</p>"
                + "<p>Pengguna: " + currentUser.getUsername() + "</p>"
                + "<p>Role: " + currentUser.getRole() + "</p>"
                + "</div></html>";

        Theme.showMessage(
                this,
                message,
                "Tentang Aplikasi",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // REFRESH DASHBOARD
    // =====================================================

    private void refreshDashboard() {
        lblUsername.setText(
                "Pengguna: " + currentUser.getUsername()
        );

        lblRole.setText("ROLE: " + currentUser.getRole());

        lblWelcome.setText(
                "Selamat Datang, " + currentUser.getUsername() + "!"
        );

        applyRoleAccess();

        if (scrollPane != null) {
            scrollPane.getVerticalScrollBar().setValue(0);
        }

        contentPanel.revalidate();
        contentPanel.repaint();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // =====================================================
    // MENU BAR (Master, Transaksi, Laporan, Keluar)
    // =====================================================

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Theme.DARK_BROWN);
        menuBar.setBorder(new EmptyBorder(2, 8, 2, 8));

        menuMaster = new JMenu("Master");
        menuTransaksi = new JMenu("Transaksi");
        menuLaporan = new JMenu("Laporan");
        menuKeluar = new JMenu("Keluar");

        for (JMenu m : new JMenu[]{
            menuMaster, menuTransaksi, menuLaporan, menuKeluar}) {
            m.setForeground(Color.WHITE);
            m.setFont(Theme.font(Font.BOLD, 12));
            m.setBorder(new EmptyBorder(6, 12, 6, 12));
        }

        // Item menu tanpa icon.
        miPeserta = new JMenuItem("Peserta");
        miInstruktur = new JMenuItem("Instruktur");
        miKursus = new JMenuItem("Kursus");
        miKelas = new JMenuItem("Kelas");
        miUser = new JMenuItem("Manajemen User");

        miPendaftaran = new JMenuItem("Pendaftaran");
        miPembayaran = new JMenuItem("Pembayaran");

        miLaporanPeserta = new JMenuItem("Laporan Peserta");
        miLaporanPendaftaran = new JMenuItem("Laporan Pendaftaran");

        miLogout = new JMenuItem("Logout");
        miTutup = new JMenuItem("Tutup Aplikasi");

        menuMaster.add(miPeserta);
        menuMaster.add(miInstruktur);
        menuMaster.add(miKursus);
        menuMaster.add(miKelas);

        // Manajemen User hanya ditampilkan untuk ADMIN.
        if (currentUser.isAdmin()) {
            menuMaster.addSeparator();
            menuMaster.add(miUser);
        }

        menuTransaksi.add(miPendaftaran);
        menuTransaksi.add(miPembayaran);

        menuLaporan.add(miLaporanPeserta);
        menuLaporan.add(miLaporanPendaftaran);

        menuKeluar.add(miLogout);
        menuKeluar.addSeparator();
        menuKeluar.add(miTutup);

        menuBar.add(menuMaster);
        menuBar.add(menuTransaksi);
        menuBar.add(menuLaporan);
        menuBar.add(menuKeluar);
        setJMenuBar(menuBar);

        miPeserta.addActionListener(e -> openPeserta());
        miInstruktur.addActionListener(e -> openInstruktur());
        miKursus.addActionListener(e -> openKursus());
        miKelas.addActionListener(e -> openKelas());
        miUser.addActionListener(e -> openUser());

        miPendaftaran.addActionListener(e ->
                showUnderDevelopment("Modul Pendaftaran"));
        miPembayaran.addActionListener(e ->
                showUnderDevelopment("Modul Pembayaran"));

        miLaporanPeserta.addActionListener(e -> showReportPlaceholder());
        miLaporanPendaftaran.addActionListener(e -> showReportPlaceholder());

        miLogout.addActionListener(e -> logout());
        miTutup.addActionListener(e -> closeApplication());
    }

    // =====================================================
    // PANEL UTAMA
    // =====================================================

    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.CREAM);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Theme.CREAM);
        contentPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        // ---------- BANNER SAMBUTAN ----------
        contentPanel.add(createHeroBanner());
        contentPanel.add(Box.createVerticalStrut(26));

        // ---------- JUDUL BAGIAN ----------
        JPanel sectionRow = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0));
        sectionRow.setOpaque(false);
        sectionRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JComponent dot = new JComponent() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.ACCENT);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(10, 10));

        JLabel lblSection = new JLabel("Menu Utama");
        lblSection.setFont(Theme.font(Font.BOLD, 16));
        lblSection.setForeground(Theme.DARK_BROWN);

        sectionRow.add(dot);
        sectionRow.add(lblSection);

        contentPanel.add(sectionRow);
        contentPanel.add(Box.createVerticalStrut(14));

        // ---------- KARTU MENU ----------
        int jumlahKartu = currentUser.isAdmin() ? 5 : 4;
        int baris = (jumlahKartu + 1) / 2;
        int tinggiKartu = 175;

        JPanel cardPanel = new JPanel(new GridLayout(0, 2, 18, 18));
        cardPanel.setOpaque(false);
        cardPanel.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                baris * tinggiKartu + (baris - 1) * 18));
        cardPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardPanel.add(createInfoCard(
                "MASTER PESERTA",
                "Pengelolaan data peserta kursus",
                "Buka Data Peserta",
                this::openPeserta,
                true
        ));

        cardPanel.add(createInfoCard(
                "MASTER INSTRUKTUR",
                "Pengelolaan data instruktur",
                "Buka Data Instruktur",
                this::openInstruktur,
                currentUser.isAdmin()
        ));

        cardPanel.add(createInfoCard(
                "MASTER KURSUS",
                "Pengelolaan informasi kursus",
                "Buka Data Kursus",
                this::openKursus,
                currentUser.isAdmin()
        ));

        cardPanel.add(createInfoCard(
                "MASTER KELAS",
                "Pengelolaan kelas pelatihan",
                "Buka Data Kelas",
                this::openKelas,
                currentUser.isAdmin()
        ));

        if (currentUser.isAdmin()) {
            cardPanel.add(createInfoCard(
                    "MANAJEMEN USER",
                    "Mengelola akun Admin dan Operator",
                    "Buka Manajemen User",
                    this::openUser,
                    true
            ));
        }

        contentPanel.add(cardPanel);
        contentPanel.add(Box.createVerticalStrut(26));

        // ---------- INFORMASI HAK AKSES ----------
        contentPanel.add(createRoleInfo());

        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Theme.CREAM);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel footer = new JLabel(
                "SIKURSUS Desktop | Sistem Informasi Manajemen Kursus & Pelatihan",
                SwingConstants.CENTER
        );
        footer.setFont(Theme.font(Font.PLAIN, 10));
        footer.setForeground(Theme.MUTED);
        footer.setBorder(new EmptyBorder(8, 28, 12, 28));

        mainPanel.add(footer, BorderLayout.SOUTH);
    }

    // =====================================================
    // BANNER SAMBUTAN
    // =====================================================

    private JPanel createHeroBanner() {
        JPanel hero = new JPanel(new BorderLayout(20, 0)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                g2.setClip(new RoundRectangle2D.Float(
                        0, 0, w, h, 30, 30));

                g2.setPaint(new GradientPaint(
                        0, 0, Theme.DARK_BROWN,
                        w, h, Theme.ACCENT));
                g2.fillRect(0, 0, w, h);

                g2.setColor(new Color(255, 255, 255, 34));
                g2.fillOval(w - 170, -80, 240, 240);
                g2.setColor(new Color(255, 255, 255, 24));
                g2.fillOval(w - 330, h - 70, 150, 150);
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(-40, h - 50, 120, 120);

                g2.dispose();
            }
        };
        hero.setOpaque(false);
        hero.setBorder(new EmptyBorder(26, 30, 26, 30));
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        hero.setPreferredSize(new Dimension(100, 150));

        // ----- teks kiri -----
        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));

        JLabel lblBrand = new JLabel("SIKURSUS DESKTOP");
        lblBrand.setFont(Theme.font(Font.BOLD, 11));
        lblBrand.setForeground(new Color(0xFFE0EC));
        lblBrand.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblWelcome = new JLabel(
                "Selamat Datang, " + currentUser.getUsername() + "!"
        );
        lblWelcome.setFont(new Font("Serif", Font.BOLD, 28));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblRoleDescription = new JLabel();
        lblRoleDescription.setFont(Theme.font(Font.PLAIN, 13));
        lblRoleDescription.setForeground(new Color(0xFFEAF2));
        lblRoleDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

        textBox.add(Box.createVerticalGlue());
        textBox.add(lblBrand);
        textBox.add(Box.createVerticalStrut(6));
        textBox.add(lblWelcome);
        textBox.add(Box.createVerticalStrut(6));
        textBox.add(lblRoleDescription);
        textBox.add(Box.createVerticalGlue());

        // ----- kapsul pengguna kanan -----
        RoundPanel userPill = new RoundPanel(
                new GridLayout(2, 1, 0, 4),
                new Color(255, 255, 255, 52), null, null, 22);
        userPill.setBorder(new EmptyBorder(12, 20, 12, 20));

        lblUsername = new JLabel(
                "Pengguna: " + currentUser.getUsername()
        );
        lblUsername.setFont(Theme.font(Font.BOLD, 13));
        lblUsername.setForeground(Color.WHITE);
        lblUsername.setHorizontalAlignment(SwingConstants.CENTER);

        lblRole = new JLabel("ROLE: " + currentUser.getRole());
        lblRole.setFont(Theme.font(Font.BOLD, 11));
        lblRole.setForeground(new Color(0xFFE0EC));
        lblRole.setHorizontalAlignment(SwingConstants.CENTER);

        userPill.add(lblUsername);
        userPill.add(lblRole);

        JPanel pillWrap = new JPanel(new GridBagLayout());
        pillWrap.setOpaque(false);
        pillWrap.add(userPill);

        hero.add(textBox, BorderLayout.CENTER);
        hero.add(pillWrap, BorderLayout.EAST);

        return hero;
    }

    // =====================================================
    // KARTU INFORMASI
    // =====================================================

    private JPanel createInfoCard(
            String title,
            String description,
            String buttonText,
            Runnable action,
            boolean enabled) {

        RoundPanel card = new RoundPanel(
                new BorderLayout(14, 0),
                Theme.IVORY,
                Theme.BORDER,
                enabled ? Theme.ACCENT : Theme.BORDER,
                26);
        card.setBorder(new EmptyBorder(24, 20, 18, 20));

        // ----- badge huruf -----
        final String letter = title.contains(" ")
                ? title.substring(title.indexOf(' ') + 1, title.indexOf(' ') + 2)
                : title.substring(0, 1);
        final boolean aktif = enabled;

        JComponent badge = new JComponent() {
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

                if (aktif) {
                    g2.setPaint(new GradientPaint(
                            0, 0, Theme.ACCENT, w, h, Theme.DARK_BROWN));
                } else {
                    g2.setColor(Theme.BORDER);
                }
                g2.fillOval(0, 0, w - 1, h - 1);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Serif", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(letter,
                        (w - fm.stringWidth(letter)) / 2,
                        (h - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(50, 50));

        JPanel badgeWrap = new JPanel(new BorderLayout());
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge, BorderLayout.NORTH);

        // ----- teks + tombol -----
        JPanel right = new JPanel(new BorderLayout(0, 10));
        right.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(Theme.font(Font.BOLD, 13));
        lblTitle.setForeground(enabled ? Theme.DARK_BROWN : Theme.MUTED);

        JLabel lblDescription = new JLabel(
                "<html><div style='width:200px;'>"
                + description
                + "</div></html>"
        );
        lblDescription.setFont(Theme.font(Font.PLAIN, 11));
        lblDescription.setForeground(
                enabled ? Theme.TEXT : Theme.MUTED);

        JPanel textBox = new JPanel(new BorderLayout(0, 4));
        textBox.setOpaque(false);
        textBox.add(lblTitle, BorderLayout.NORTH);
        textBox.add(lblDescription, BorderLayout.CENTER);

        JButton button = Theme.createButton(
                buttonText, Theme.BTN_SAVE
        );
        button.addActionListener(e -> action.run());
        button.setEnabled(enabled);

        // Tombol berada di kiri kartu.
        JPanel bottom = new JPanel(new FlowLayout(
                FlowLayout.LEFT, 0, 0
        ));
        bottom.setOpaque(false);
        bottom.add(button);

        right.add(textBox, BorderLayout.CENTER);
        right.add(bottom, BorderLayout.SOUTH);

        card.add(badgeWrap, BorderLayout.WEST);
        card.add(right, BorderLayout.CENTER);

        return card;
    }

    // =====================================================
    // INFORMASI HAK AKSES
    // =====================================================

    private JPanel createRoleInfo() {
        RoundPanel roleInfo = new RoundPanel(
                new BorderLayout(0, 8),
                Theme.IVORY, Theme.BORDER, null, 24);
        roleInfo.setBorder(new EmptyBorder(18, 24, 18, 24));
        roleInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel lblInfoTitle = new JLabel("Informasi Hak Akses");
        lblInfoTitle.setFont(Theme.font(Font.BOLD, 14));
        lblInfoTitle.setForeground(Theme.DARK_BROWN);

        JComponent dot = new JComponent() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.ACCENT);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(10, 10));

        JPanel titleRow = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        titleRow.add(dot);
        titleRow.add(lblInfoTitle);

        JLabel lblInfoText = new JLabel();
        lblInfoText.setFont(Theme.font(Font.PLAIN, 12));
        lblInfoText.setForeground(Theme.TEXT);

        if (currentUser.isAdmin()) {
            lblInfoText.setText(
                    "<html>Anda login sebagai ADMIN. "
                    + "Anda dapat mengelola data Master, Transaksi, "
                    + "Laporan, dan akun pengguna melalui Manajemen User."
                    + "</html>"
            );
        } else {
            lblInfoText.setText(
                    "<html>Anda login sebagai OPERATOR. "
                    + "Anda dapat mengakses menu Peserta, Transaksi, "
                    + "dan Laporan sesuai hak akses yang diberikan."
                    + "</html>"
            );
        }

        roleInfo.add(titleRow, BorderLayout.NORTH);
        roleInfo.add(lblInfoText, BorderLayout.CENTER);

        return roleInfo;
    }

    // =====================================================
    // HAK AKSES
    // =====================================================

    private void applyRoleAccess() {
        boolean admin = currentUser.isAdmin();

        // Menu bar: item master selain Peserta hanya untuk ADMIN.
        miPeserta.setEnabled(true);
        miInstruktur.setEnabled(admin);
        miKursus.setEnabled(admin);
        miKelas.setEnabled(admin);
        miUser.setEnabled(admin);

        menuMaster.setEnabled(true);
        menuTransaksi.setEnabled(true);
        menuLaporan.setEnabled(true);
        menuKeluar.setEnabled(true);

        if (admin) {
            lblRoleDescription.setText(
                    "Anda masuk sebagai Administrator. "
                    + "Anda memiliki akses pengelolaan penuh."
            );
        } else {
            lblRoleDescription.setText(
                    "Anda masuk sebagai Operator. "
                    + "Gunakan menu yang tersedia sesuai hak akses Anda."
            );
        }
    }

    // =====================================================
    // MENU MASTER
    // =====================================================

    private void openPeserta() {
        WindowManager.open(PesertaFrame.class, PesertaFrame::new);
    }

    private void openInstruktur() {
        if (!currentUser.isAdmin()) {
            showAccessDenied();
            return;
        }
        WindowManager.open(InstrukturFrame.class, InstrukturFrame::new);
    }

    private void openKursus() {
        if (!currentUser.isAdmin()) {
            showAccessDenied();
            return;
        }
        WindowManager.open(KursusFrame.class, KursusFrame::new);
    }

    private void openKelas() {
        if (!currentUser.isAdmin()) {
            showAccessDenied();
            return;
        }
        WindowManager.open(KelasFrame.class, KelasFrame::new);
    }

    private void openUser() {
        if (!currentUser.isAdmin()) {
            showAccessDenied();
            return;
        }
        WindowManager.open(UserFrame.class, UserFrame::new);
    }

    // =====================================================
    // PESAN
    // =====================================================

    private void showAccessDenied() {
        Theme.showMessage(
                this,
                "Maaf, menu ini hanya dapat diakses oleh ADMIN.",
                "Akses Ditolak",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showReportPlaceholder() {
        Theme.showMessage(
                this,
                "Modul laporan diaktifkan pada Pertemuan 12 dengan Jaspersoft Studio.",
                "Informasi",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showUnderDevelopment(String module) {
        Theme.showMessage(
                this,
                module + " sedang dikembangkan.",
                "Informasi",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    private void logout() {
        int confirm = Theme.showConfirm(
                this,
                "Apakah Anda yakin ingin logout?",
                "Konfirmasi Logout"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            closeChildWindows();
            UserSession.clear();
            dispose();

            SwingUtilities.invokeLater(() -> {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            });
        }
    }

    // =====================================================
    // TUTUP APLIKASI
    // =====================================================

    private void closeApplication() {
        int confirm = Theme.showConfirm(
                this,
                "Apakah Anda yakin ingin menutup aplikasi SIKURSUS?",
                "Konfirmasi Keluar"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            closeChildWindows();
            UserSession.clear();
            dispose();
            System.exit(0);
        }
    }

    private void closeChildWindows() {
        WindowManager.closeAll();
    }
}