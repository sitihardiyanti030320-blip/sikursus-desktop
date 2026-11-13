package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.net.URL;
import javax.swing.*;
import model.User;
import util.UserSession;
import util.WindowManager;

/**
 * Jendela utama setelah login: menu, role, dan navigasi antar modul.
 */
public class DashboardFrame extends JFrame {

    // Label sesi
    private final JLabel lblUser = new JLabel();
    private final JLabel lblRole = new JLabel();

    // Menu utama
    private final JMenu menuMaster = new JMenu("Master");
    private final JMenu menuTransaksi = new JMenu("Transaksi");
    private final JMenu menuLaporan = new JMenu("Laporan");
    private final JMenu menuKeluar = new JMenu("Keluar");

    // Master
    private final JMenuItem miUsers = new JMenuItem("Users");
    private final JMenuItem miPeserta = new JMenuItem("Peserta");
    private final JMenuItem miInstruktur = new JMenuItem("Instruktur");
    private final JMenuItem miKursus = new JMenuItem("Kursus");
    private final JMenuItem miKelas = new JMenuItem("Kelas");

    // Transaksi
    private final JMenuItem miPendaftaran = new JMenuItem("Pendaftaran");
    private final JMenuItem miPembayaran = new JMenuItem("Pembayaran");

    // Laporan
    private final JMenuItem miLaporanPeserta = new JMenuItem("Peserta - Semua");
    private final JMenuItem miLaporanPendaftaran = new JMenuItem("Pendaftaran - Semua");
    private final JMenuItem miLaporanPembayaran = new JMenuItem("Pembayaran - Semua");

    // Keluar
    private final JMenuItem miLogout = new JMenuItem("Logout");
    private final JMenuItem miTutup = new JMenuItem("Tutup Aplikasi");

    // Popup menu
    private final JPopupMenu popupDashboard = new JPopupMenu();
    private final JMenuItem miTentang = new JMenuItem("Tentang Aplikasi");
    private final JMenuItem miRefresh = new JMenuItem("Refresh");

    public DashboardFrame() {
        super("Dashboard - SIKURSUS Desktop");
        setDefaultCloseOperation(EXIT_ON_CLOSE); // jendela utama
        ViewUtil.setupFrame(this, 1040, 650);
        buildPopup();
        buildMenu();
        buildBody();
        setLocationRelativeTo(null);
        applySession();
    }

    // ------------------------------------------------------------ tampilan

    private void buildBody() {
        JPanel root = ViewUtil.root("SIKURSUS Desktop - Dashboard");
        JPanel body = ViewUtil.body();

        JPanel session = new JPanel(new GridLayout(2, 1, 2, 2));
        session.setBackground(ViewUtil.CARD);
        session.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(ViewUtil.MUTED);
        session.add(lblUser);
        session.add(lblRole);
        body.add(ViewUtil.section("Sesi Pengguna", session), BorderLayout.NORTH);

        JTextArea info = new JTextArea(
                "SIKURSUS Desktop - Sistem Informasi Manajemen Kursus & Pelatihan\n\n"
                + "Gunakan menu di bagian atas untuk mengelola master, transaksi, dan laporan.\n\n"
                + "Alur utama:\n"
                + "Login -> Dashboard -> Master -> Transaksi -> Laporan -> Logout / Keluar\n\n"
                + "Database  : db_sikursus\n"
                + "Arsitektur: View -> DAO -> JDBC -> MySQL\n\n"
                + "Tips: klik kanan pada area ini untuk membuka popup menu.");
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        info.setForeground(ViewUtil.TEXT);
        info.setBackground(ViewUtil.CARD);
        info.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        body.add(ViewUtil.section("Ringkasan Sistem", info), BorderLayout.CENTER);

        // Popup menu pada area dashboard
        root.setComponentPopupMenu(popupDashboard);
        body.setComponentPopupMenu(popupDashboard);
        info.setComponentPopupMenu(popupDashboard);

        root.add(body, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void buildPopup() {
        popupDashboard.add(miTentang);
        popupDashboard.add(miRefresh);
        miTentang.addActionListener(e -> showTentang());
        miRefresh.addActionListener(e -> {
            applySession();
            repaint();
        });
    }

    private void buildMenu() {
        JMenuBar bar = new JMenuBar();
        bar.setBackground(new Color(239, 246, 255));

        menuMaster.add(miUsers);
        menuMaster.add(miPeserta);
        menuMaster.add(miInstruktur);
        menuMaster.add(miKursus);
        menuMaster.add(miKelas);

        menuTransaksi.add(miPendaftaran);
        menuTransaksi.add(miPembayaran);

        menuLaporan.add(miLaporanPeserta);
        menuLaporan.add(miLaporanPendaftaran);
        menuLaporan.add(miLaporanPembayaran);

        menuKeluar.add(miLogout);
        menuKeluar.addSeparator();
        menuKeluar.add(miTutup);

        bar.add(menuMaster);
        bar.add(menuTransaksi);
        bar.add(menuLaporan);
        bar.add(menuKeluar);
        setJMenuBar(bar);

        // Master: satu instance per form (WindowManager)
        miUsers.addActionListener(e -> WindowManager.open(UserFrame.class, UserFrame::new));
        miPeserta.addActionListener(e -> WindowManager.open(PesertaFrame.class, PesertaFrame::new));
        miInstruktur.addActionListener(e -> WindowManager.open(InstrukturFrame.class, InstrukturFrame::new));
        miKursus.addActionListener(e -> WindowManager.open(KursusFrame.class, KursusFrame::new));
        miKelas.addActionListener(e -> WindowManager.open(KelasFrame.class, KelasFrame::new));

        // Transaksi
        miPendaftaran.addActionListener(e ->
                WindowManager.open(FormPendaftaranKursus.class, FormPendaftaranKursus::new));
        miPembayaran.addActionListener(e -> showPlaceholder("Modul Pembayaran"));

        // Laporan (diaktifkan di Pertemuan 12)
        miLaporanPeserta.addActionListener(e -> showLaporanInfo());
        miLaporanPendaftaran.addActionListener(e -> showLaporanInfo());
        miLaporanPembayaran.addActionListener(e -> showLaporanInfo());

        // Keluar
        miLogout.addActionListener(e -> doLogout());
        miTutup.addActionListener(e -> doExit());

        // Icon dari resource project (bukan path komputer)
        miPeserta.setIcon(loadIcon("/icons/peserta.png"));
        miInstruktur.setIcon(loadIcon("/icons/instruktur.png"));
        miKursus.setIcon(loadIcon("/icons/kursus.png"));
        miKelas.setIcon(loadIcon("/icons/kelas.png"));
    }

    private ImageIcon loadIcon(String path) {
        URL url = getClass().getResource(path);
        return url == null ? null : new ImageIcon(url);
    }

    // ---------------------------------------------------------------- sesi

    private void applySession() {
        User user = UserSession.getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Session tidak ditemukan. Silakan login ulang.");
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            return;
        }
        lblUser.setText("Pengguna: " + user.getUsername());
        lblRole.setText("Role: " + user.getRole());

        // ADMIN: semua aktif. OPERATOR: hanya Peserta, Transaksi, Laporan.
        boolean admin = user.isAdmin();
        miUsers.setEnabled(admin);
        miInstruktur.setEnabled(admin);
        miKursus.setEnabled(admin);
        miKelas.setEnabled(admin);
        miPeserta.setEnabled(true);
        menuTransaksi.setEnabled(true);
        menuLaporan.setEnabled(true);
    }

    // --------------------------------------------------------------- aksi

    private void doLogout() {
        int pilihan = JOptionPane.showConfirmDialog(this,
                "Logout dari aplikasi?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (pilihan == JOptionPane.YES_OPTION) {
            WindowManager.closeAll();   // 1. tutup semua form child
            UserSession.clear();        // 2. hapus session
            dispose();                  // 3. tutup dashboard
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true)); // 4. kembali ke login
        }
    }

    private void doExit() {
        int pilihan = JOptionPane.showConfirmDialog(this,
                "Tutup aplikasi SIKURSUS?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (pilihan == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void showPlaceholder(String modul) {
        JOptionPane.showMessageDialog(this,
                modul + " sedang dikembangkan pada tahap project berikutnya.");
    }

    private void showLaporanInfo() {
        JOptionPane.showMessageDialog(this,
                "Modul laporan diaktifkan pada Pertemuan 12 dengan Jaspersoft Studio.");
    }

    private void showTentang() {
        JOptionPane.showMessageDialog(this,
                "SIKURSUS Desktop\nProject PBO - Program Studi Informatika");
    }
}