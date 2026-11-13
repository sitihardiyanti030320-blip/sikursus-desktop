package view;

import dao.InstrukturDAO;
import dao.KelasDAO;
import dao.KursusDAO;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Kelas;
import util.LookupItem;
import util.Validation;

/**
 * Master Kelas: CRUD + pencarian. Memilih Kursus dan Instruktur lewat combo box.
 */
public class KelasFrame extends JFrame {

    private final JComboBox<LookupItem> kursus = new JComboBox<>();
    private final JComboBox<LookupItem> instruktur = new JComboBox<>();
    private final JTextField nama = new JTextField();
    private final JTextField tanggal = new JTextField(ViewUtil.today());
    private final JTextField kapasitas = new JTextField("20");
    private final JTextField search = new JTextField(18);
    private final JTable table = new JTable(ViewUtil.tableModel(
            "ID", "Kelas", "Kursus", "Instruktur", "Tanggal Mulai", "Kapasitas"));
    private final KelasDAO dao = new KelasDAO();
    private List<Kelas> cache = new ArrayList<>(); // data yang sedang tampil di tabel
    private int selected = -1;

    public KelasFrame() {
        super("Master Kelas - SIKURSUS Desktop");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        ViewUtil.setupFrame(this, 1040, 720);
        build();
        loadLookups();
        load("");
    }

    // ------------------------------------------------------------ tampilan

    private void build() {
        JPanel root = ViewUtil.root("Master Data Kelas");
        JPanel body = ViewUtil.body();

        JPanel form = ViewUtil.formPanel();
        ViewUtil.addFormRow(form, 0, "Kursus", kursus);
        ViewUtil.addFormRow(form, 1, "Instruktur", instruktur);
        ViewUtil.addFormRow(form, 2, "Nama Kelas", nama);
        ViewUtil.addFormRow(form, 3, "Tanggal Mulai (yyyy-MM-dd)", tanggal);
        ViewUtil.addFormRow(form, 4, "Kapasitas", kapasitas);

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

        body.add(ViewUtil.section("Form Kelas", formBox), BorderLayout.NORTH);
        body.add(ViewUtil.section("Data Kelas", tableBox), BorderLayout.CENTER);
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

    private void loadLookups() {
        try {
            kursus.removeAllItems();
            for (LookupItem item : new KursusDAO().lookup()) {
                kursus.addItem(item);
            }
            instruktur.removeAllItems();
            for (LookupItem item : new InstrukturDAO().lookup()) {
                instruktur.addItem(item);
            }
        } catch (SQLException ex) {
            ViewUtil.sqlError(this, "Memuat pilihan kursus/instruktur", ex);
        }
    }

    private Kelas data() {
        LookupItem k = (LookupItem) kursus.getSelectedItem();
        LookupItem i = (LookupItem) instruktur.getSelectedItem();
        if (k == null || i == null) {
            throw new IllegalArgumentException(
                    "Data Kursus dan Instruktur harus sudah tersedia terlebih dahulu");
        }
        Kelas x = new Kelas();
        x.setIdKelas(Math.max(selected, 0));
        x.setIdKursus(k.getId());
        x.setIdInstruktur(i.getId());
        x.setNamaKelas(nama.getText());
        x.setTanggalMulai(Validation.date(tanggal.getText(), "Tanggal mulai"));
        x.setKapasitas(Validation.positiveInt(kapasitas.getText(), "Kapasitas"));
        return x;
    }

    private void load(String keyword) {
        try {
            cache = dao.search(keyword);
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            for (Kelas x : cache) {
                model.addRow(new Object[]{
                    x.getIdKelas(),
                    x.getNamaKelas(),
                    x.getKodeKursus() + " - " + x.getNamaKursus(),
                    x.getNamaInstruktur(),
                    x.getTanggalMulai(),
                    x.getKapasitas()
                });
            }
        } catch (SQLException ex) {
            ViewUtil.sqlError(this, "Memuat kelas", ex);
        }
    }

    private void pick() {
        int r = table.getSelectedRow();
        if (r < 0) {
            return;
        }
        selected = Integer.parseInt(table.getValueAt(r, 0).toString());
        for (Kelas x : cache) {
            if (x.getIdKelas() == selected) {
                nama.setText(x.getNamaKelas());
                tanggal.setText(x.getTanggalMulai().toString());
                kapasitas.setText(String.valueOf(x.getKapasitas()));
                ViewUtil.selectLookup(kursus, x.getIdKursus());
                ViewUtil.selectLookup(instruktur, x.getIdInstruktur());
                break;
            }
        }
    }

    // --------------------------------------------------------------- aksi

    private void save() {
        try {
            dao.insert(data());
            load("");
            clear();
        } catch (Exception ex) {
            handle(ex, "Menyimpan kelas");
        }
    }

    private void update() {
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Pilih kelas pada tabel terlebih dahulu.");
            return;
        }
        try {
            dao.update(data());
            load("");
            clear();
        } catch (Exception ex) {
            handle(ex, "Mengubah kelas");
        }
    }

    private void delete() {
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Pilih kelas pada tabel terlebih dahulu.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Hapus kelas ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.delete(selected);
            load("");
            clear();
        } catch (SQLException ex) {
            ViewUtil.sqlError(this, "Menghapus kelas", ex);
        }
    }

    private void clear() {
        selected = -1;
        nama.setText("");
        tanggal.setText(ViewUtil.today());
        kapasitas.setText("20");
        table.clearSelection();
        loadLookups(); // segarkan pilihan kursus/instruktur (bisa berubah dari form lain)
        nama.requestFocus();
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