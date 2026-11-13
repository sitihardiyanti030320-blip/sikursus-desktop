package view;

import dao.PesertaDAO;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Peserta;

/**
 * Master Peserta: CRUD + pencarian dengan JTable.
 */
public class PesertaFrame extends JFrame {

    private static final String LAKI = "Laki-laki";
    private static final String PEREMPUAN = "Perempuan";

    private final JTextField nim = new JTextField();
    private final JTextField nama = new JTextField();
    private final JComboBox<String> jenisKelamin = new JComboBox<>(new String[]{LAKI, PEREMPUAN});
    private final JTextField prodi = new JTextField();
    private final JTextField hp = new JTextField();
    private final JTextField email = new JTextField();
    private final JTextField search = new JTextField(18);
    private final JTable table = new JTable(
            ViewUtil.tableModel("ID", "NIM", "Nama", "JK", "Program Studi", "No. HP", "Email"));
    private final PesertaDAO dao = new PesertaDAO();
    private int selected = -1;

    public PesertaFrame() {
        super("Master Peserta - SIKURSUS Desktop");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // jangan mematikan aplikasi
        ViewUtil.setupFrame(this, 1040, 720);
        build();
        load("");
    }

    // ------------------------------------------------------------ tampilan

    private void build() {
        JPanel root = ViewUtil.root("Master Data Peserta");
        JPanel body = ViewUtil.body();

        JPanel form = ViewUtil.formPanel();
        ViewUtil.addFormRow(form, 0, "NIM", nim);
        ViewUtil.addFormRow(form, 1, "Nama Peserta", nama);
        ViewUtil.addFormRow(form, 2, "Jenis Kelamin", jenisKelamin);
        ViewUtil.addFormRow(form, 3, "Program Studi", prodi);
        ViewUtil.addFormRow(form, 4, "Nomor HP", hp);
        ViewUtil.addFormRow(form, 5, "Email", email);

        JButton simpan = ViewUtil.primaryButton("Simpan");
        JButton ubah = ViewUtil.updateButton("Ubah");
        JButton hapus = ViewUtil.dangerButton("Hapus");
        JButton reset = ViewUtil.neutralButton("Reset");

        JPanel formBox = new JPanel(new BorderLayout());
        formBox.setBackground(ViewUtil.BG);
        formBox.add(form, BorderLayout.CENTER);
        formBox.add(ViewUtil.actionPanel(simpan, ubah, hapus, reset), BorderLayout.SOUTH);

        JButton cari = ViewUtil.updateButton("Cari");
        JPanel tableBox = new JPanel(new BorderLayout(0, 8));
        tableBox.setBackground(ViewUtil.CARD);
        tableBox.add(ViewUtil.searchPanel("Pencarian", search, cari), BorderLayout.NORTH);
        tableBox.add(ViewUtil.tableScroll(table), BorderLayout.CENTER);

        body.add(ViewUtil.section("Form Peserta", formBox), BorderLayout.NORTH);
        body.add(ViewUtil.section("Data Peserta", tableBox), BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);

        simpan.addActionListener(e -> save());
        ubah.addActionListener(e -> update());
        hapus.addActionListener(e -> delete());
        reset.addActionListener(e -> clear());
        cari.addActionListener(e -> load(search.getText()));
        search.addActionListener(e -> load(search.getText()));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                pick();
            }
        });
        getRootPane().setDefaultButton(simpan);
    }

    // --------------------------------------------------------------- data

    private Peserta data() {
        String jk = PEREMPUAN.equals(jenisKelamin.getSelectedItem()) ? "P" : "L";
        // Urutan constructor: (id, nama, jenisKelamin, prodi, noHp, email, nim)
        return new Peserta(Math.max(selected, 0), nama.getText(), jk,
                prodi.getText(), hp.getText(), email.getText(), nim.getText());
    }

    private void load(String keyword) {
        try {
            List<Peserta> list = dao.search(keyword);
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            for (Peserta p : list) {
                model.addRow(new Object[]{
                    p.getId(), p.getNim(), p.getNama(), p.getJenisKelamin(),
                    p.getProdi(), p.getNoHp(), p.getEmail()
                });
            }
        } catch (SQLException ex) {
            ViewUtil.sqlError(this, "Memuat peserta", ex);
        }
    }

    private void pick() {
        int r = table.getSelectedRow();
        if (r < 0) {
            return;
        }
        selected = Integer.parseInt(cell(r, 0));
        nim.setText(cell(r, 1));
        nama.setText(cell(r, 2));
        jenisKelamin.setSelectedItem("P".equals(cell(r, 3)) ? PEREMPUAN : LAKI);
        prodi.setText(cell(r, 4));
        hp.setText(cell(r, 5));
        email.setText(cell(r, 6));
    }

    private String cell(int row, int col) {
        Object v = table.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }

    // --------------------------------------------------------------- aksi

    private void save() {
        try {
            dao.insert(data());
            load("");
            clear();
        } catch (Exception ex) {
            handle(ex, "Menyimpan peserta");
        }
    }

    private void update() {
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Pilih peserta pada tabel terlebih dahulu.");
            return;
        }
        try {
            dao.update(data());
            load("");
            clear();
        } catch (Exception ex) {
            handle(ex, "Mengubah peserta");
        }
    }

    private void delete() {
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Pilih peserta pada tabel terlebih dahulu.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Hapus peserta ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.delete(selected);
            load("");
            clear();
        } catch (SQLException ex) {
            ViewUtil.sqlError(this, "Menghapus peserta", ex);
        }
    }

    private void clear() {
        selected = -1;
        nim.setText("");
        nama.setText("");
        jenisKelamin.setSelectedIndex(0);
        prodi.setText("");
        hp.setText("");
        email.setText("");
        table.clearSelection();
        nim.requestFocus();
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