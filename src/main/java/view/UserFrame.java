package view;

import dao.UserDAO;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.User;
import util.UserSession;

/**
 * Master Users (khusus ADMIN): tambah user, ubah role/status, reset password, hapus.
 */
public class UserFrame extends JFrame {

    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final JComboBox<String> role = new JComboBox<>(new String[]{"ADMIN", "OPERATOR"});
    private final JCheckBox aktif = new JCheckBox("Akun aktif", true);
    private final JTextField search = new JTextField(18);
    private final JTable table = new JTable(
            ViewUtil.tableModel("ID", "Username", "Role", "Aktif"));
    private final UserDAO dao = new UserDAO();
    private int selected = -1;

    public UserFrame() {
        super("Master Users - SIKURSUS Desktop");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        ViewUtil.setupFrame(this, 940, 680);
        build();
        load("");
    }

    // ------------------------------------------------------------ tampilan

    private void build() {
        JPanel root = ViewUtil.root("Master Data Users");
        JPanel body = ViewUtil.body();

        aktif.setBackground(ViewUtil.CARD);
        JPanel form = ViewUtil.formPanel();
        ViewUtil.addFormRow(form, 0, "Username", username);
        ViewUtil.addFormRow(form, 1, "Password (user baru / reset)", password);
        ViewUtil.addFormRow(form, 2, "Role", role);
        ViewUtil.addFormRow(form, 3, "Status", aktif);

        JButton simpan = ViewUtil.primaryButton("Simpan User Baru");
        JButton ubah = ViewUtil.updateButton("Ubah Role/Status");
        JButton resetPw = ViewUtil.updateButton("Reset Password");
        JButton hapus = ViewUtil.dangerButton("Hapus");
        JButton reset = ViewUtil.neutralButton("Reset Form");

        JPanel formBox = new JPanel(new BorderLayout());
        formBox.setBackground(ViewUtil.BG);
        formBox.add(form, BorderLayout.CENTER);
        formBox.add(ViewUtil.actionPanel(simpan, ubah, resetPw, hapus, reset), BorderLayout.SOUTH);

        JButton cari = ViewUtil.updateButton("Cari");
        JPanel tableBox = new JPanel(new BorderLayout(0, 8));
        tableBox.setBackground(ViewUtil.CARD);
        tableBox.add(ViewUtil.searchPanel("Pencarian", search, cari), BorderLayout.NORTH);
        tableBox.add(ViewUtil.tableScroll(table), BorderLayout.CENTER);

        body.add(ViewUtil.section("Form User", formBox), BorderLayout.NORTH);
        body.add(ViewUtil.section("Data User", tableBox), BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);

        simpan.addActionListener(e -> save());
        ubah.addActionListener(e -> update());
        resetPw.addActionListener(e -> resetPassword());
        hapus.addActionListener(e -> delete());
        reset.addActionListener(e -> clear());
        cari.addActionListener(e -> load(search.getText()));
        search.addActionListener(e -> load(search.getText()));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                pick();
            }
        });
    }

    // --------------------------------------------------------------- data

    private void load(String keyword) {
        try {
            List<User> list = dao.search(keyword);
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            for (User u : list) {
                model.addRow(new Object[]{
                    u.getIdUser(), u.getUsername(), u.getRole(), u.isAktif() ? "Ya" : "Tidak"
                });
            }
        } catch (SQLException ex) {
            ViewUtil.sqlError(this, "Memuat user", ex);
        }
    }

    private void pick() {
        int r = table.getSelectedRow();
        if (r < 0) {
            return;
        }
        selected = Integer.parseInt(table.getValueAt(r, 0).toString());
        username.setText(table.getValueAt(r, 1).toString());
        username.setEditable(false); // username tidak boleh diganti
        role.setSelectedItem(table.getValueAt(r, 2));
        aktif.setSelected("Ya".equals(table.getValueAt(r, 3)));
        password.setText("");
    }

    private boolean isOwnAccount() {
        User me = UserSession.getCurrentUser();
        return me != null && me.getIdUser() == selected;
    }

    // --------------------------------------------------------------- aksi

    private void save() {
        if (selected >= 0) {
            JOptionPane.showMessageDialog(this,
                    "Klik \"Reset Form\" dahulu untuk menambah user baru.");
            return;
        }
        char[] pw = password.getPassword();
        try {
            String name = username.getText().trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Username wajib diisi");
            }
            if (pw.length == 0) {
                throw new IllegalArgumentException("Password wajib diisi");
            }
            dao.insert(name, pw, role.getSelectedItem().toString(), aktif.isSelected());
            load("");
            clear();
        } catch (Exception ex) {
            handle(ex, "Menyimpan user");
        } finally {
            Arrays.fill(pw, '\0');
        }
    }

    private void update() {
        if (!requireSelection()) {
            return;
        }
        String newRole = role.getSelectedItem().toString();
        if (isOwnAccount() && (!aktif.isSelected()
                || !newRole.equals(UserSession.getCurrentUser().getRole()))) {
            JOptionPane.showMessageDialog(this,
                    "Anda tidak dapat menonaktifkan atau mengubah role akun yang sedang dipakai.");
            return;
        }
        try {
            dao.update(selected, newRole, aktif.isSelected());
            load("");
            clear();
        } catch (SQLException ex) {
            ViewUtil.sqlError(this, "Mengubah user", ex);
        }
    }

    private void resetPassword() {
        if (!requireSelection()) {
            return;
        }
        char[] pw = password.getPassword();
        try {
            if (pw.length == 0) {
                throw new IllegalArgumentException("Isi password baru terlebih dahulu");
            }
            dao.resetPassword(selected, pw);
            JOptionPane.showMessageDialog(this, "Password berhasil direset.");
            clear();
        } catch (Exception ex) {
            handle(ex, "Reset password");
        } finally {
            Arrays.fill(pw, '\0');
        }
    }

    private void delete() {
        if (!requireSelection()) {
            return;
        }
        if (isOwnAccount()) {
            JOptionPane.showMessageDialog(this, "Akun yang sedang dipakai tidak dapat dihapus.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Hapus user ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.delete(selected);
            load("");
            clear();
        } catch (SQLException ex) {
            ViewUtil.sqlError(this, "Menghapus user", ex);
        }
    }

    private boolean requireSelection() {
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Pilih user pada tabel terlebih dahulu.");
            return false;
        }
        return true;
    }

    private void clear() {
        selected = -1;
        username.setText("");
        username.setEditable(true);
        password.setText("");
        role.setSelectedIndex(0);
        aktif.setSelected(true);
        table.clearSelection();
        username.requestFocus();
    }

    private void handle(Exception ex, String action) {
        if (ex instanceof SQLException) {
            ViewUtil.sqlError(this, action, (SQLException) ex);
        } else {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Validasi", JOptionPane.WARNING_MESSAGE);
        }
    }
}