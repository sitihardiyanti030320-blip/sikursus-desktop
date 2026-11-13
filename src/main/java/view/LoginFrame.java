package view;

import dao.UserDAO;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.*;
import model.User;
import util.UserSession;

/**
 * Halaman awal aplikasi. Login yang benar membuka DashboardFrame.
 */
public class LoginFrame extends JFrame {

    private final JLabel lblTitle = new JLabel("SIKURSUS Desktop", SwingConstants.CENTER);
    private final JLabel lblInfo = new JLabel("Masukkan akun SIKURSUS", SwingConstants.CENTER);
    private final JTextField txtUsername = new JTextField(22);
    private final JPasswordField pwdPassword = new JPasswordField(22);
    private final JCheckBox chkTampil = new JCheckBox("Tampilkan password");
    private final JButton btnLogin = ViewUtil.primaryButton("Login");
    private final JButton btnKeluar = ViewUtil.dangerButton("Keluar");
    private final UserDAO userDAO = new UserDAO();
    private final char defaultEcho = pwdPassword.getEchoChar();

    public LoginFrame() {
        super("Login - SIKURSUS Desktop");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        ViewUtil.setupFrame(this, 620, 520);
        build();
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btnLogin); // tombol Enter = Login
    }

    private void build() {
        JPanel root = ViewUtil.root("Login SIKURSUS Desktop");
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(ViewUtil.BG);

        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(ViewUtil.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewUtil.BORDER),
                BorderFactory.createEmptyBorder(22, 28, 22, 28)));

        // Judul
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel subtitle = ViewUtil.note("Sistem Informasi Manajemen Kursus & Pelatihan");
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel north = new JPanel(new GridLayout(3, 1, 0, 4));
        north.setBackground(ViewUtil.CARD);
        north.add(lblTitle);
        north.add(subtitle);
        north.add(lblInfo);
        card.add(north, BorderLayout.NORTH);

        // Form
        chkTampil.setBackground(ViewUtil.CARD);
        JPanel form = ViewUtil.formPanel();
        form.setBackground(ViewUtil.CARD);
        ViewUtil.addFormRow(form, 0, "Username", txtUsername);
        ViewUtil.addFormRow(form, 1, "Password", pwdPassword);
        ViewUtil.addFormRow(form, 2, "", chkTampil);
        card.add(form, BorderLayout.CENTER);

        // Tombol
        JPanel south = new JPanel(new BorderLayout(0, 8));
        south.setBackground(ViewUtil.CARD);
        south.add(ViewUtil.actionPanel(btnLogin, btnKeluar), BorderLayout.NORTH);
        JLabel demo = ViewUtil.note("Demo: admin / Admin@123   |   operator / Operator@123");
        demo.setHorizontalAlignment(SwingConstants.CENTER);
        south.add(demo, BorderLayout.SOUTH);
        card.add(south, BorderLayout.SOUTH);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(18, 18, 18, 18);
        outer.add(card, c);

        root.add(outer, BorderLayout.CENTER);
        setContentPane(root);

        // Event
        btnLogin.addActionListener(e -> doLogin());
        btnKeluar.addActionListener(e -> System.exit(0));
        chkTampil.addActionListener(e ->
                pwdPassword.setEchoChar(chkTampil.isSelected() ? (char) 0 : defaultEcho));
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        char[] password = pwdPassword.getPassword();

        if (username.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi.");
            Arrays.fill(password, '\0');
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnLogin.setEnabled(false);
        try {
            User user = userDAO.authenticate(username, password);
            if (user == null) {
                JOptionPane.showMessageDialog(this,
                        "Login gagal. Periksa username, password, atau status user.",
                        "Login Gagal", JOptionPane.WARNING_MESSAGE);
                pwdPassword.setText("");
                pwdPassword.requestFocus();
                return;
            }
            UserSession.setCurrentUser(user);
            openDashboard();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Tidak dapat mengakses database: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, '\0');
            btnLogin.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Dashboard dibuat dan ditampilkan DULU, baru login ditutup. Jika pembuatan
     * Dashboard gagal, pesan errornya ditampilkan (bukan hilang diam-diam).
     */
    private void openDashboard() {
        try {
            DashboardFrame dashboard = new DashboardFrame();
            dashboard.setVisible(true);
            dispose();
        } catch (Throwable t) {
            t.printStackTrace();
            UserSession.clear();
            JOptionPane.showMessageDialog(this,
                    "Gagal membuka Dashboard:\n" + t,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Agar LoginFrame bisa dijalankan sendiri (Run File). */
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}