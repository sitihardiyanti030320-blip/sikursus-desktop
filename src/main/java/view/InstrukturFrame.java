package view;

import dao.InstrukturDAO;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Instruktur;
import util.Theme;

public class InstrukturFrame extends JFrame {

    private JTextField txtNama, txtKeahlian, txtNoHp, txtEmail, txtCari;
    private JTable tabel;
    private DefaultTableModel model;
    private int idTerpilih = -1;

    private final InstrukturDAO dao = new InstrukturDAO();

    public InstrukturFrame() {
        setTitle("SIKURSUS - Data Instruktur");
        setSize(950, 650);
        Theme.styleFrame(this);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        initComponents();
        tampilData("");
    }

    private void initComponents() {
        JPanel form = Theme.createCard(
                new GridBagLayout(), "FORM INSTRUKTUR");

        txtNama = new JTextField();
        txtKeahlian = new JTextField();
        txtNoHp = new JTextField();
        txtEmail = new JTextField();

        Theme.addField(form, 0, 0, "Nama Instruktur", txtNama);
        Theme.addField(form, 0, 1, "Keahlian", txtKeahlian);
        Theme.addField(form, 1, 0, "Nomor HP", txtNoHp);
        Theme.addField(form, 1, 1, "Email", txtEmail);

        JButton btnSimpan = Theme.createButton("Simpan", Theme.BTN_SAVE);
        JButton btnUbah = Theme.createButton("Ubah", Theme.BTN_EDIT);
        JButton btnHapus = Theme.createButton("Hapus", Theme.BTN_DELETE);
        JButton btnReset = Theme.createButton("Reset", Theme.BTN_NEUTRAL);

        Theme.addButtonRow(form, 2, 4,
                btnSimpan, btnUbah, btnHapus, btnReset);

        JPanel tablePanel = Theme.createCard(
                new BorderLayout(0, 12), "DATA INSTRUKTUR");

        txtCari = new JTextField();
        txtCari.setToolTipText("Cari nama atau keahlian instruktur");

        JButton btnCari = Theme.createButton("Cari", Theme.BTN_EDIT);

        model = new DefaultTableModel(
                new String[]{"ID", "Nama", "Keahlian", "No. HP", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabel = new JTable(model);
        Theme.styleTable(tabel);
        tabel.setAutoCreateRowSorter(true);
        Theme.fixedWidth(tabel, 0, 50);

        tablePanel.add(
                Theme.createSearchBar(txtCari, btnCari),
                BorderLayout.NORTH);
        tablePanel.add(Theme.createScroll(tabel), BorderLayout.CENTER);

        setContentPane(Theme.createPage(
                "DATA INSTRUKTUR",
                "Kelola informasi instruktur kursus",
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

    private void tampilData(String keyword) {
        model.setRowCount(0);

        try {
            List<Instruktur> data = dao.search(keyword);

            for (Instruktur i : data) {
                model.addRow(new Object[]{
                    i.getId(),
                    i.getNama(),
                    i.getKeahlian(),
                    i.getNoHp(),
                    i.getEmail()
                });
            }
        } catch (SQLException ex) {
            showError("Memuat data instruktur", ex);
        }
    }

    private void pilihData() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;

        row = tabel.convertRowIndexToModel(row);

        idTerpilih = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        txtNama.setText(Theme.str(model.getValueAt(row, 1)));
        txtKeahlian.setText(Theme.str(model.getValueAt(row, 2)));
        txtNoHp.setText(Theme.str(model.getValueAt(row, 3)));
        txtEmail.setText(Theme.str(model.getValueAt(row, 4)));
    }

    private boolean validasi() {
        if (txtNama.getText().trim().isEmpty()
                || txtKeahlian.getText().trim().isEmpty()) {
            Theme.showMessage(this,
                    "Nama dan keahlian wajib diisi.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // Membuat objek Instruktur dari isi form.
    // Validasi format HP dan email dilakukan oleh class model.
    private Instruktur getFormData() {
        return new Instruktur(
                idTerpilih < 0 ? 0 : idTerpilih,
                txtNama.getText().trim(),
                txtNoHp.getText().trim(),
                txtKeahlian.getText().trim(),
                txtEmail.getText().trim());
    }

    private void simpan() {
        if (!validasi()) return;

        try {
            if (dao.insert(getFormData())) {
                Theme.showMessage(this,
                        "Data instruktur berhasil disimpan.",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                reset();
            } else {
                Theme.showMessage(this,
                        "Gagal menyimpan data instruktur.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            Theme.showMessage(this, ex.getMessage(),
                    "Validasi", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            showError("Menyimpan data instruktur", ex);
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
                        "Data instruktur berhasil diubah.",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                reset();
            } else {
                Theme.showMessage(this,
                        "Data instruktur tidak ditemukan.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            Theme.showMessage(this, ex.getMessage(),
                    "Validasi", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            showError("Mengubah data instruktur", ex);
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
                this, "Hapus data instruktur ini?",
                "Konfirmasi Hapus");

        if (konfirmasi != JOptionPane.YES_OPTION) return;

        try {
            if (dao.delete(idTerpilih)) {
                Theme.showMessage(this,
                        "Data berhasil dihapus.",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                reset();
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                Theme.showMessage(this,
                        "Instruktur tidak dapat dihapus karena masih digunakan oleh kelas.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                showError("Menghapus data instruktur", ex);
            }
        }
    }

    private void reset() {
        idTerpilih = -1;
        tabel.clearSelection();

        txtNama.setText("");
        txtKeahlian.setText("");
        txtNoHp.setText("");
        txtEmail.setText("");
        txtCari.setText("");

        tampilData("");
        txtNama.requestFocus();
    }

    private void showError(String aksi, SQLException ex) {
        Theme.showMessage(this,
                "Gagal " + aksi + ":\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}