package view;

import dao.InstrukturDAO;
import dao.KelasDAO;
import dao.KursusDAO;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Kelas;
import util.LookupItem;
import util.Theme;

public class KelasFrame extends JFrame {

    private JTextField txtNama, txtTanggal, txtKapasitas, txtCari;
    private JComboBox<LookupItem> cmbKursus, cmbInstruktur;
    private JTable tabel;
    private DefaultTableModel model;

    private int idTerpilih = -1;
    private List<Kelas> dataKelas = new ArrayList<>();

    private final KelasDAO dao = new KelasDAO();
    private final KursusDAO kursusDao = new KursusDAO();
    private final InstrukturDAO instrukturDao = new InstrukturDAO();

    public KelasFrame() {
        setTitle("SIKURSUS - Data Kelas");
        setSize(1050, 700);
        Theme.styleFrame(this);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        initComponents();
        loadCombo();
        tampilData("");
    }

    private void initComponents() {
        JPanel form = Theme.createCard(
                new GridBagLayout(), "FORM KELAS");

        txtNama = new JTextField();
        txtTanggal = new JTextField();
        txtTanggal.setToolTipText("Contoh: 2026-01-31");

        txtKapasitas = new JTextField();

        cmbKursus = new JComboBox<>();
        cmbInstruktur = new JComboBox<>();

        Theme.addField(form, 0, 0, "Nama Kelas", txtNama);
        Theme.addField(form, 0, 1, "Kapasitas", txtKapasitas);
        Theme.addField(form, 1, 0, "Program Kursus", cmbKursus);
        Theme.addField(form, 1, 1, "Instruktur", cmbInstruktur);
        Theme.addField(form, 2, 0,
                "Tanggal Mulai (YYYY-MM-DD)", txtTanggal);

        JButton btnSimpan = Theme.createButton("Simpan", Theme.BTN_SAVE);
        JButton btnUbah = Theme.createButton("Ubah", Theme.BTN_EDIT);
        JButton btnHapus = Theme.createButton("Hapus", Theme.BTN_DELETE);
        JButton btnReset = Theme.createButton("Reset", Theme.BTN_NEUTRAL);

        Theme.addButtonRow(form, 3, 4,
                btnSimpan, btnUbah, btnHapus, btnReset);

        JPanel tablePanel = Theme.createCard(
                new BorderLayout(0, 12), "DATA KELAS");

        txtCari = new JTextField();
        txtCari.setToolTipText(
                "Cari nama kelas, kursus, atau instruktur");

        JButton btnCari = Theme.createButton("Cari", Theme.BTN_EDIT);

        model = new DefaultTableModel(
                new String[]{
                    "ID", "Nama Kelas", "Kursus", "Instruktur",
                    "Tanggal Mulai", "Kapasitas"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabel = new JTable(model);
        Theme.styleTable(tabel);
        tabel.setAutoCreateRowSorter(true);

        Theme.fixedWidth(tabel, 0, 50);
        Theme.fixedWidth(tabel, 5, 90);

        tablePanel.add(
                Theme.createSearchBar(txtCari, btnCari),
                BorderLayout.NORTH);
        tablePanel.add(Theme.createScroll(tabel), BorderLayout.CENTER);

        setContentPane(Theme.createPage(
                "DATA KELAS",
                "Kelola kelas, program kursus, dan instruktur",
                form, tablePanel));

        btnSimpan.addActionListener(e -> simpan());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnReset.addActionListener(e -> reset());

        btnCari.addActionListener(e ->
                tampilData(txtCari.getText().trim()));
        txtCari.addActionListener(e ->
                tampilData(txtCari.getText().trim()));

        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && tabel.getSelectedRow() >= 0) {
                pilihData();
            }
        });
    }

    private void loadCombo() {
        cmbKursus.removeAllItems();
        cmbInstruktur.removeAllItems();

        try {
            for (LookupItem item : kursusDao.lookup()) {
                cmbKursus.addItem(item);
            }

            for (LookupItem item : instrukturDao.lookup()) {
                cmbInstruktur.addItem(item);
            }
        } catch (SQLException ex) {
            showError("Memuat daftar kursus/instruktur", ex);
        }
    }

    private void tampilData(String keyword) {
        model.setRowCount(0);

        try {
            dataKelas = dao.search(keyword);

            for (Kelas k : dataKelas) {
                model.addRow(new Object[]{
                    k.getIdKelas(),
                    k.getNamaKelas(),
                    k.getNamaKursus(),
                    k.getNamaInstruktur(),
                    k.getTanggalMulai(),
                    k.getKapasitas()
                });
            }
        } catch (SQLException ex) {
            dataKelas = new ArrayList<>();
            showError("Memuat data kelas", ex);
        }
    }

    private void pilihData() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;

        row = tabel.convertRowIndexToModel(row);

        idTerpilih = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        txtNama.setText(Theme.str(model.getValueAt(row, 1)));
        txtTanggal.setText(Theme.str(model.getValueAt(row, 4)));
        txtKapasitas.setText(Theme.str(model.getValueAt(row, 5)));

        for (Kelas k : dataKelas) {
            if (k.getIdKelas() == idTerpilih) {
                pilihCombo(cmbKursus, k.getIdKursus());
                pilihCombo(cmbInstruktur, k.getIdInstruktur());
                break;
            }
        }
    }

    private void pilihCombo(JComboBox<LookupItem> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private boolean validasi() {
        if (txtNama.getText().trim().isEmpty()
                || txtTanggal.getText().trim().isEmpty()
                || txtKapasitas.getText().trim().isEmpty()) {
            Theme.showMessage(this,
                    "Semua data kelas wajib diisi.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (cmbKursus.getSelectedItem() == null
                || cmbInstruktur.getSelectedItem() == null) {
            Theme.showMessage(this,
                    "Data kursus dan instruktur harus tersedia terlebih dahulu.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Date.valueOf(txtTanggal.getText().trim());

            int kapasitas = Integer.parseInt(
                    txtKapasitas.getText().trim());

            if (kapasitas <= 0) {
                Theme.showMessage(this,
                        "Kapasitas harus lebih dari 0.",
                        "Validasi", JOptionPane.WARNING_MESSAGE);
                return false;
            }

        } catch (IllegalArgumentException e) {
            Theme.showMessage(this,
                    "Tanggal harus berformat YYYY-MM-DD dan kapasitas harus berupa angka.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    // Membuat objek Kelas dari isi form.
    private Kelas getFormData() {
        LookupItem kursus = (LookupItem) cmbKursus.getSelectedItem();
        LookupItem instruktur = (LookupItem) cmbInstruktur.getSelectedItem();

        Kelas k = new Kelas();
        k.setIdKelas(idTerpilih < 0 ? 0 : idTerpilih);
        k.setIdKursus(kursus.getId());
        k.setIdInstruktur(instruktur.getId());
        k.setNamaKelas(txtNama.getText().trim());
        k.setTanggalMulai(Date.valueOf(txtTanggal.getText().trim()));
        k.setKapasitas(Integer.parseInt(txtKapasitas.getText().trim()));
        return k;
    }

    private void simpan() {
        if (!validasi()) return;

        try {
            if (dao.insert(getFormData())) {
                Theme.showMessage(this,
                        "Data kelas berhasil disimpan.",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                reset();
            } else {
                Theme.showMessage(this,
                        "Gagal menyimpan data kelas.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            Theme.showMessage(this, ex.getMessage(),
                    "Validasi", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            showError("Menyimpan data kelas", ex);
        }
    }

    private void ubah() {
        if (idTerpilih == -1) {
            Theme.showMessage(this,
                    "Pilih data yang ingin diubah.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validasi()) return;

        try {
            if (dao.update(getFormData())) {
                Theme.showMessage(this,
                        "Data kelas berhasil diubah.",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                reset();
            } else {
                Theme.showMessage(this,
                        "Data kelas tidak ditemukan.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            Theme.showMessage(this, ex.getMessage(),
                    "Validasi", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            showError("Mengubah data kelas", ex);
        }
    }

    private void hapus() {
        if (idTerpilih == -1) {
            Theme.showMessage(this,
                    "Pilih data yang ingin dihapus.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = Theme.showConfirm(
                this, "Hapus data kelas ini?",
                "Konfirmasi Hapus");

        if (konfirmasi != JOptionPane.YES_OPTION) return;

        try {
            if (dao.delete(idTerpilih)) {
                Theme.showMessage(this,
                        "Data kelas berhasil dihapus.",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                reset();
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                Theme.showMessage(this,
                        "Data kelas tidak dapat dihapus karena sudah digunakan pada pendaftaran.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                showError("Menghapus data kelas", ex);
            }
        }
    }

    private void reset() {
        idTerpilih = -1;
        tabel.clearSelection();

        txtNama.setText("");
        txtTanggal.setText("");
        txtKapasitas.setText("");
        txtCari.setText("");

        if (cmbKursus.getItemCount() > 0) {
            cmbKursus.setSelectedIndex(0);
        }

        if (cmbInstruktur.getItemCount() > 0) {
            cmbInstruktur.setSelectedIndex(0);
        }

        tampilData("");
        txtNama.requestFocus();
    }

    private void showError(String aksi, SQLException ex) {
        Theme.showMessage(this,
                "Gagal " + aksi + ":\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}