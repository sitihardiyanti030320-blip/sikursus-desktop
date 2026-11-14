package view;

import config.Database;
import model.User;
import util.PasswordUtil;
import util.Theme;
import util.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class UserFrame extends JFrame {

    private JTextField txtUsername, txtCari;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JCheckBox chkAktif;

    private JTable tblUser;
    private DefaultTableModel model;

    private JButton btnSimpan, btnUbah, btnHapus;
    private JButton btnReset, btnMuatUlang, btnCari;

    private int idTerpilih = -1;

    public UserFrame() {
        setTitle("Manajemen User - SIKURSUS");
        setSize(950, 680);
        setMinimumSize(new Dimension(850, 580));
        Theme.styleFrame(this);

        buatTampilan();
        tampilkanUser("");
    }

    private boolean isAkunSendiri(int idUser) {
        User user = UserSession.getCurrentUser();
        return user != null && user.getIdUser() == idUser;
    }

    private void buatTampilan() {
        setContentPane(Theme.createPage(
                "MANAJEMEN USER",
                "Kelola akun, hak akses, dan status pengguna",
                buatPanelForm(),
                buatPanelTabel()));
    }

    private JPanel buatPanelForm() {
        JPanel panelForm = Theme.createCard(
                new GridBagLayout(), "FORM USER");

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        cmbRole = new JComboBox<>(new String[]{"OPERATOR", "ADMIN"});
        chkAktif = new JCheckBox("Akun aktif", true);

        Theme.addField(panelForm, 0, 0, "Username", txtUsername);
        Theme.addField(panelForm, 0, 1, "Password", txtPassword);
        Theme.addField(panelForm, 1, 0, "Role", cmbRole);
        Theme.addField(panelForm, 1, 1, "Status Akun", chkAktif);

        JLabel lblInfo = new JLabel(
                "Password minimal 8 karakter. Kosongkan saat mengubah "
                + "jika tidak ingin mengganti password.");
        lblInfo.setFont(Theme.font(Font.ITALIC, 11));
        lblInfo.setForeground(Theme.MUTED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 0, 0);
        panelForm.add(lblInfo, gbc);

        btnSimpan = Theme.createButton("Simpan", Theme.BTN_SAVE);
        btnUbah = Theme.createButton("Ubah", Theme.BTN_EDIT);
        btnHapus = Theme.createButton("Hapus", Theme.BTN_DELETE);
        btnReset = Theme.createButton("Reset", Theme.BTN_NEUTRAL);
        btnMuatUlang = Theme.createButton("Muat Ulang", Theme.BTN_SUCCESS);

        Theme.addButtonRow(panelForm, 3, 5,
                btnSimpan, btnUbah, btnHapus, btnReset, btnMuatUlang);

        btnSimpan.addActionListener(e -> tambahUser());
        btnUbah.addActionListener(e -> ubahUser());
        btnHapus.addActionListener(e -> hapusUser());
        btnReset.addActionListener(e -> resetForm());
        btnMuatUlang.addActionListener(e -> {
            txtCari.setText("");
            tampilkanUser("");
        });

        return panelForm;
    }

    private JPanel buatPanelTabel() {
        JPanel panel = Theme.createCard(
                new BorderLayout(0, 10), "DATA USER");

        txtCari = new JTextField();
        txtCari.setToolTipText("Cari berdasarkan username atau role");

        btnCari = Theme.createButton("Cari", Theme.BTN_EDIT);

        model = new DefaultTableModel(
                new Object[]{"ID", "Username", "Role", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblUser = new JTable(model);
        Theme.styleTable(tblUser);
        tblUser.setAutoCreateRowSorter(true);

        Theme.TableRenderer renderer =
                new Theme.TableRenderer(SwingConstants.CENTER, null);

        for (int i = 0; i < tblUser.getColumnCount(); i++) {
            tblUser.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        Theme.fixedWidth(tblUser, 0, 60);
        tblUser.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblUser.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblUser.getColumnModel().getColumn(3).setPreferredWidth(150);

        tblUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pilihData();
            }
        });

        panel.add(Theme.createSearchBar(txtCari, btnCari),
                BorderLayout.NORTH);
        panel.add(Theme.createScroll(tblUser), BorderLayout.CENTER);

        btnCari.addActionListener(e ->
                tampilkanUser(txtCari.getText().trim()));
        txtCari.addActionListener(e ->
                tampilkanUser(txtCari.getText().trim()));

        return panel;
    }

    private void tampilkanUser(String keyword) {
        model.setRowCount(0);

        String sql = "SELECT id_user, username, role, aktif "
                + "FROM users "
                + "WHERE username LIKE ? OR role LIKE ? "
                + "ORDER BY id_user";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String cari = "%" + keyword + "%";
            ps.setString(1, cari);
            ps.setString(2, cari);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getInt("aktif") == 1 ? "Aktif" : "Nonaktif"
                    });
                }
            }

        } catch (SQLException ex) {
            Theme.showMessage(this,
                    "Gagal memuat data user:\n" + ex.getMessage(),
                    "Kesalahan Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tambahUser() {
        String username = txtUsername.getText().trim();
        char[] password = txtPassword.getPassword();

        try {
            String role = cmbRole.getSelectedItem().toString();
            int aktif = chkAktif.isSelected() ? 1 : 0;

            if (username.isEmpty() || password.length == 0) {
                Theme.showMessage(this,
                        "Username dan password wajib diisi.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (password.length < 8) {
                Theme.showMessage(this,
                        "Password minimal 8 karakter.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "INSERT INTO users "
                    + "(username, password_hash, role, aktif) "
                    + "VALUES (?, ?, ?, ?)";

            try (Connection conn = Database.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, username);
                ps.setString(2,
                        PasswordUtil.hash(password));
                ps.setString(3, role);
                ps.setInt(4, aktif);
                ps.executeUpdate();

                Theme.showMessage(this, "User berhasil ditambahkan.",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);

                resetForm();
                tampilkanUser(txtCari.getText().trim());
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                Theme.showMessage(this, "Username sudah digunakan.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                Theme.showMessage(this,
                        "Gagal menambahkan user:\n" + ex.getMessage(),
                        "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            Theme.showMessage(this,
                    "Terjadi kesalahan:\n" + ex.getMessage(),
                    "Kesalahan", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void ubahUser() {
        if (idTerpilih == -1) {
            Theme.showMessage(this,
                    "Pilih user pada tabel terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = txtUsername.getText().trim();
        char[] password = txtPassword.getPassword();

        try {
            String role = cmbRole.getSelectedItem().toString();
            int aktif = chkAktif.isSelected() ? 1 : 0;

            if (username.isEmpty()) {
                Theme.showMessage(this, "Username wajib diisi.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (isAkunSendiri(idTerpilih)) {
                if (aktif == 0) {
                    Theme.showMessage(this,
                            "Akun yang sedang digunakan tidak boleh dinonaktifkan.",
                            "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!"ADMIN".equals(role)) {
                    Theme.showMessage(this,
                            "Role ADMIN yang sedang digunakan tidak boleh diturunkan.",
                            "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (password.length > 0 && password.length < 8) {
                Theme.showMessage(this,
                        "Password baru minimal 8 karakter.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql;
            if (password.length == 0) {
                sql = "UPDATE users SET username=?, role=?, aktif=? "
                        + "WHERE id_user=?";
            } else {
                sql = "UPDATE users SET username=?, password_hash=?, "
                        + "role=?, aktif=? WHERE id_user=?";
            }

            try (Connection conn = Database.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, username);

                if (password.length == 0) {
                    ps.setString(2, role);
                    ps.setInt(3, aktif);
                    ps.setInt(4, idTerpilih);
                } else {
                    ps.setString(2,
                            PasswordUtil.hash(password));
                    ps.setString(3, role);
                    ps.setInt(4, aktif);
                    ps.setInt(5, idTerpilih);
                }

                int hasil = ps.executeUpdate();

                if (hasil > 0) {
                    Theme.showMessage(this, "Data user berhasil diperbarui.",
                            "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    resetForm();
                    tampilkanUser(txtCari.getText().trim());
                } else {
                    Theme.showMessage(this, "Data user tidak ditemukan.",
                            "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                Theme.showMessage(this, "Username sudah digunakan.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                Theme.showMessage(this,
                        "Gagal mengubah user:\n" + ex.getMessage(),
                        "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            Theme.showMessage(this,
                    "Terjadi kesalahan:\n" + ex.getMessage(),
                    "Kesalahan", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void hapusUser() {
        if (idTerpilih == -1) {
            Theme.showMessage(this, "Pilih user yang akan dihapus.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isAkunSendiri(idTerpilih)) {
            Theme.showMessage(this,
                    "Akun yang sedang digunakan tidak dapat dihapus.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int baris = tblUser.getSelectedRow();
        if (baris == -1) return;

        baris = tblUser.convertRowIndexToModel(baris);
        String username = model.getValueAt(baris, 1).toString();

        int konfirmasi = Theme.showConfirm(
                this, "Yakin ingin menghapus akun " + username + "?",
                "Konfirmasi Hapus");

        if (konfirmasi != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM users WHERE id_user=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idTerpilih);
            int hasil = ps.executeUpdate();

            if (hasil > 0) {
                Theme.showMessage(this, "User berhasil dihapus.",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                resetForm();
                tampilkanUser(txtCari.getText().trim());
            } else {
                Theme.showMessage(this, "Data user tidak ditemukan.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException ex) {
            Theme.showMessage(this,
                    "Gagal menghapus user:\n" + ex.getMessage(),
                    "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pilihData() {
        int baris = tblUser.getSelectedRow();
        if (baris == -1) return;

        baris = tblUser.convertRowIndexToModel(baris);
        idTerpilih = Integer.parseInt(
                model.getValueAt(baris, 0).toString());

        txtUsername.setText(model.getValueAt(baris, 1).toString());
        cmbRole.setSelectedItem(model.getValueAt(baris, 2).toString());
        chkAktif.setSelected(
                model.getValueAt(baris, 3).toString().equals("Aktif"));

        txtPassword.setText("");
    }

    private void resetForm() {
        idTerpilih = -1;
        txtUsername.setText("");
        txtPassword.setText("");
        cmbRole.setSelectedIndex(0);
        chkAktif.setSelected(true);
        tblUser.clearSelection();
        txtUsername.requestFocus();
    }
}