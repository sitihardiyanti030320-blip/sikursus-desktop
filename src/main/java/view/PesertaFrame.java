package view;

import dao.PesertaDAO;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Peserta;
import util.Theme;

public class PesertaFrame extends JFrame {

    private final PesertaDAO dao = new PesertaDAO();
    private int selectedId = -1;

    private JTextField txtNim, txtNama, txtProdi;
    private JTextField txtNoHp, txtEmail, txtCari;

    private JComboBox<String> cmbJenisKelamin;
    private JTable tblPeserta;
    private DefaultTableModel model;

    public PesertaFrame() {
        setTitle("Master Data Peserta - SIKURSUS");
        setSize(1100, 760);
        setMinimumSize(new Dimension(900, 650));
        Theme.styleFrame(this);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        buildForm();
        loadTable();
        clearForm();
    }

    private void buildForm() {
        setContentPane(Theme.createPage(
                "MASTER DATA PESERTA",
                "Kelola data peserta kursus",
                createFormPanel(),
                createTablePanel()));
    }

    private JPanel createFormPanel() {
        JPanel panel = Theme.createCard(
                new GridBagLayout(), "FORM PESERTA");

        txtNim = new JTextField();
        txtNama = new JTextField();
        txtProdi = new JTextField();
        txtNoHp = new JTextField();
        txtEmail = new JTextField();

        cmbJenisKelamin = new JComboBox<>(
                new String[]{"L", "P"});

        Theme.addField(panel, 0, 0, "NIM", txtNim);
        Theme.addField(panel, 0, 1, "Nama Peserta", txtNama);
        Theme.addField(panel, 1, 0,
                "Jenis Kelamin", cmbJenisKelamin);
        Theme.addField(panel, 1, 1, "Program Studi", txtProdi);
        Theme.addField(panel, 2, 0, "Nomor HP", txtNoHp);
        Theme.addField(panel, 2, 1, "Email", txtEmail);

        JButton btnSimpan = Theme.createButton(
                "Simpan", Theme.BTN_SAVE);
        JButton btnUbah = Theme.createButton(
                "Ubah", Theme.BTN_EDIT);
        JButton btnHapus = Theme.createButton(
                "Hapus", Theme.BTN_DELETE);
        JButton btnReset = Theme.createButton(
                "Reset", Theme.BTN_NEUTRAL);
        JButton btnCetak = Theme.createButton(
                "Cetak Semua", Theme.BTN_SUCCESS);

        Theme.addButtonRow(panel, 3, 5,
                btnSimpan, btnUbah, btnHapus,
                btnReset, btnCetak);

        btnSimpan.addActionListener(e -> save());
        btnUbah.addActionListener(e -> update());
        btnHapus.addActionListener(e -> delete());
        btnReset.addActionListener(e -> clearForm());
        btnCetak.addActionListener(e -> printData());

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = Theme.createCard(
                new BorderLayout(0, 12), "DATA PESERTA");

        txtCari = new JTextField();
        txtCari.setToolTipText(
                "Cari NIM, nama, program studi, nomor HP, atau email");

        JButton btnCari = Theme.createButton(
                "Cari", Theme.BTN_EDIT);

        model = new DefaultTableModel(
                new String[]{
                    "ID", "NIM", "Nama", "JK",
                    "Prodi", "No. HP", "Email"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPeserta = new JTable(model);
        Theme.styleTable(tblPeserta);
        tblPeserta.setAutoCreateRowSorter(true);

        Theme.fixedWidth(tblPeserta, 0, 50);
        Theme.fixedWidth(tblPeserta, 3, 50);

        panel.add(
                Theme.createSearchBar(txtCari, btnCari),
                BorderLayout.NORTH);
        panel.add(Theme.createScroll(tblPeserta),
                BorderLayout.CENTER);

        btnCari.addActionListener(e -> search());
        txtCari.addActionListener(e -> search());

        tblPeserta.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                pickData();
            }
        });

        return panel;
    }

    private Peserta getFormData() {
        Peserta p = new Peserta();

        p.setId(selectedId < 0 ? 0 : selectedId);
        p.setNim(txtNim.getText().trim());
        p.setNama(txtNama.getText().trim());
        p.setJenisKelamin(
                cmbJenisKelamin.getSelectedItem().toString());
        p.setProdi(txtProdi.getText().trim());
        p.setNoHp(txtNoHp.getText().trim());
        p.setEmail(txtEmail.getText().trim());

        return p;
    }

    private boolean validateForm() {
        if (txtNim.getText().trim().isEmpty()
                || txtNama.getText().trim().isEmpty()
                || txtProdi.getText().trim().isEmpty()
                || txtNoHp.getText().trim().isEmpty()
                || txtEmail.getText().trim().isEmpty()) {

            Theme.showMessage(this,
                    "Semua kolom harus diisi!",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void save() {
        if (!validateForm()) return;

        try {
            if (dao.insert(getFormData())) {
                Theme.showMessage(this,
                        "Data peserta berhasil disimpan!",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);

                clearForm();
            }

        } catch (IllegalArgumentException ex) {
            showValidationError(ex);
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                Theme.showMessage(this,
                        "NIM sudah terdaftar. Gunakan NIM yang berbeda.",
                        "Validasi", JOptionPane.WARNING_MESSAGE);
            } else {
                showError("Menyimpan data", ex);
            }
        }
    }

    private void update() {
        if (selectedId < 0) {
            Theme.showMessage(this,
                    "Pilih data peserta dari tabel terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm()) return;

        try {
            if (dao.update(getFormData())) {
                Theme.showMessage(this,
                        "Data peserta berhasil diubah!",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);

                clearForm();
            } else {
                Theme.showMessage(this,
                        "Data peserta tidak ditemukan!",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
            }

        } catch (IllegalArgumentException ex) {
            showValidationError(ex);
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                Theme.showMessage(this,
                        "NIM sudah dipakai peserta lain.",
                        "Validasi", JOptionPane.WARNING_MESSAGE);
            } else {
                showError("Mengubah data", ex);
            }
        }
    }

    private void delete() {
        if (selectedId < 0) {
            Theme.showMessage(this,
                    "Pilih data peserta yang ingin dihapus!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int jawab = Theme.showConfirm(
                this,
                "Apakah kamu yakin ingin menghapus data peserta ini?",
                "Konfirmasi Hapus");

        if (jawab != JOptionPane.YES_OPTION) return;

        try {
            if (dao.delete(selectedId)) {
                Theme.showMessage(this,
                        "Data peserta berhasil dihapus!",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);

                clearForm();
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                Theme.showMessage(this,
                        "Peserta tidak dapat dihapus karena sudah memiliki data pendaftaran.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                showError("Menghapus data", ex);
            }
        }
    }

    private void loadTable() {
        try {
            fillTable(dao.findAll());
        } catch (SQLException ex) {
            showError("Memuat data peserta", ex);
        }
    }

    private void fillTable(List<Peserta> data) {
        model.setRowCount(0);

        for (Peserta p : data) {
            model.addRow(new Object[]{
                p.getId(),
                p.getNim(),
                p.getNama(),
                p.getJenisKelamin(),
                p.getProdi(),
                p.getNoHp(),
                p.getEmail()
            });
        }
    }

    private void search() {
        try {
            String keyword = txtCari.getText().trim();

            if (keyword.isEmpty()) {
                loadTable();
            } else {
                fillTable(dao.search(keyword));
            }

        } catch (SQLException ex) {
            showError("Mencari data peserta", ex);
        }
    }

    private void pickData() {
        int row = tblPeserta.getSelectedRow();
        if (row < 0) return;

        row = tblPeserta.convertRowIndexToModel(row);

        selectedId = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        txtNim.setText(Theme.str(model.getValueAt(row, 1)));
        txtNama.setText(Theme.str(model.getValueAt(row, 2)));

        cmbJenisKelamin.setSelectedItem(
                Theme.str(model.getValueAt(row, 3)));

        txtProdi.setText(Theme.str(model.getValueAt(row, 4)));
        txtNoHp.setText(Theme.str(model.getValueAt(row, 5)));
        txtEmail.setText(Theme.str(model.getValueAt(row, 6)));
    }

    private void clearForm() {
        selectedId = -1;

        txtNim.setText("");
        txtNama.setText("");
        txtProdi.setText("");
        txtNoHp.setText("");
        txtEmail.setText("");
        txtCari.setText("");

        cmbJenisKelamin.setSelectedIndex(0);
        tblPeserta.clearSelection();

        loadTable();
        txtNim.requestFocus();
    }

    private void printData() {
        try {
            if (tblPeserta.print()) {
                Theme.showMessage(this,
                        "Data peserta berhasil dicetak!",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            Theme.showMessage(this,
                    "Gagal mencetak data: " + ex.getMessage(),
                    "Cetak Data", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showValidationError(IllegalArgumentException ex) {
        Theme.showMessage(this,
                ex.getMessage(),
                "Validasi", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String action, SQLException ex) {
        Theme.showMessage(this,
                "Gagal " + action + ":\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new PesertaFrame().setVisible(true));
    }
}