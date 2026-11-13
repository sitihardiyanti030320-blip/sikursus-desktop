package view;

import dao.KursusDAO;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Kursus;

/**
 * Master Kursus: CRUD + pencarian dengan JTable.
 */
public class KursusFrame extends JFrame {

    private final JTextField kode = new JTextField();
    private final JTextField nama = new JTextField();
    private final JComboBox<String> level =
            new JComboBox<>(new String[]{"BASIC", "INTERMEDIATE", "ADVANCED"});
    private final JTextField biaya = new JTextField();
    private final JTextField search = new JTextField(18);
    private final JTable table = new JTable(
            ViewUtil.tableModel("ID", "Kode", "Nama Kursus", "Level", "Biaya"));
    private final KursusDAO dao = new KursusDAO();
    private int selected = -1;

    public KursusFrame() {
        super("Master Kursus - SIKURSUS Desktop");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        ViewUtil.setupFrame(this, 940, 650);
        build();
        load("");
    }

    // ------------------------------------------------------------ tampilan

    private void build() {
        JPanel root = ViewUtil.root("Master Data Kursus");
        JPanel body = ViewUtil.body();

        JPanel form = ViewUtil.formPanel();
        ViewUtil.addFormRow(form, 0, "Kode Kursus", kode);
        ViewUtil.addFormRow(form, 1, "Nama Kursus", nama);
        ViewUtil.addFormRow(form, 2, "Level", level);
        ViewUtil.addFormRow(form, 3, "Biaya", biaya);

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

        body.add(ViewUtil.section("Form Kursus", formBox), BorderLayout.NORTH);
        body.add(ViewUtil.section("Data Kursus", tableBox), BorderLayout.CENTER);
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

    private Kursus data() {
        double nilaiBiaya;
        try {
            nilaiBiaya = Double.parseDouble(biaya.getText().trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Biaya harus berupa angka");
        }
        return new Kursus(Math.max(selected, 0), kode.getText(), nama.getText(),
                level.getSelectedItem().toString(), nilaiBiaya);
    }

    private void load(String keyword) {
        try {
            List<Kursus> list = dao.search(keyword);
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            for (Kursus k : list) {
                // Locale.US: pemisah ribuan selalu "," sehingga mudah dikembalikan ke angka
                model.addRow(new Object[]{
                    k.getIdKursus(), k.getKode(), k.getNama(), k.getLevel(),
                    String.format(Locale.US, "%,.0f", k.getBiaya())
                });
            }
        } catch (SQLException ex) {
            ViewUtil.sqlError(this, "Memuat kursus", ex);
        }
    }

    private void pick() {
        int r = table.getSelectedRow();
        if (r < 0) {
            return;
        }
        selected = Integer.parseInt(table.getValueAt(r, 0).toString());
        kode.setText(table.getValueAt(r, 1).toString());
        nama.setText(table.getValueAt(r, 2).toString());
        level.setSelectedItem(table.getValueAt(r, 3));
        biaya.setText(table.getValueAt(r, 4).toString().replace(",", ""));
    }

    // --------------------------------------------------------------- aksi

    private void save() {
        try {
            dao.insert(data());
            load("");
            clear();
        } catch (Exception ex) {
            handle(ex, "Menyimpan kursus");
        }
    }

    private void update() {
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Pilih kursus pada tabel terlebih dahulu.");
            return;
        }
        try {
            dao.update(data());
            load("");
            clear();
        } catch (Exception ex) {
            handle(ex, "Mengubah kursus");
        }
    }

    private void delete() {
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Pilih kursus pada tabel terlebih dahulu.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Hapus kursus ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.delete(selected);
            load("");
            clear();
        } catch (SQLException ex) {
            ViewUtil.sqlError(this, "Menghapus kursus", ex);
        }
    }

    private void clear() {
        selected = -1;
        kode.setText("");
        nama.setText("");
        level.setSelectedIndex(0);
        biaya.setText("");
        table.clearSelection();
        kode.requestFocus();
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