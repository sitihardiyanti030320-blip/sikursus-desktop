
package view;

import dao.PesertaDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Peserta;

public class PesertaFrame extends JFrame {

    private final PesertaDAO dao = new PesertaDAO();

    private int selectedId = -1;

    private JTextField txtNim;
    private JTextField txtNama;
    private JTextField txtProdi;
    private JTextField txtNoHp;
    private JTextField txtEmail;
    private JTextField txtCari;

    private JComboBox<String> cmbJenisKelamin;

    private JTable tblPeserta;
    private DefaultTableModel model;

    private JButton btnSimpan;
    private JButton btnUbah;
    private JButton btnHapus;
    private JButton btnReset;
    private JButton btnCari;
    private JButton btnCetak;

    private final Color warnaLatar = new Color(235, 245, 250);
    private final Color warnaBiru = new Color(21, 87, 160);
    private final Color warnaPutih = Color.WHITE;

    public PesertaFrame() {
        setTitle("Master Data Peserta - SIKURSUS Desktop");
        setSize(1050, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildForm();
        configureTable();
        loadTable();
        clearForm();
    }

    // Tampilan utama
    private void buildForm() {

        JPanel utama = new JPanel(new BorderLayout(10, 10));
        utama.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        utama.setBackground(warnaLatar);

        JLabel lblJudul = new JLabel(
                "MASTER DATA PESERTA",
                SwingConstants.CENTER
        );

        lblJudul.setFont(new Font("Arial", Font.BOLD, 22));
        lblJudul.setForeground(warnaPutih);
        lblJudul.setOpaque(true);
        lblJudul.setBackground(warnaBiru);
        lblJudul.setPreferredSize(new Dimension(100, 65));

        utama.add(lblJudul, BorderLayout.NORTH);

        JPanel tengah = new JPanel(new BorderLayout(10, 10));
        tengah.setBackground(warnaLatar);

        tengah.add(createFormPanel(), BorderLayout.NORTH);
        tengah.add(createTablePanel(), BorderLayout.CENTER);

        utama.add(tengah, BorderLayout.CENTER);

        setContentPane(utama);
    }

    // Form peserta
    private JPanel createFormPanel() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Form Peserta"));
        panel.setBackground(warnaPutih);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 10, 7, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNim = new JTextField(30);
        txtNama = new JTextField(30);
        txtProdi = new JTextField(30);
        txtNoHp = new JTextField(30);
        txtEmail = new JTextField(30);

        cmbJenisKelamin = new JComboBox<>(
                new String[]{"L", "P"}
        );

        addFormRow(panel, gbc, 0, "NIM", txtNim);
        addFormRow(panel, gbc, 1, "Nama Peserta", txtNama);
        addFormRow(panel, gbc, 2, "Jenis Kelamin", cmbJenisKelamin);
        addFormRow(panel, gbc, 3, "Program Studi", txtProdi);
        addFormRow(panel, gbc, 4, "Nomor HP", txtNoHp);
        addFormRow(panel, gbc, 5, "Email", txtEmail);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelTombol.setBackground(warnaPutih);

        btnSimpan = new JButton("Simpan");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnReset = new JButton("Reset");
        btnCetak = new JButton("Cetak Semua");

        styleButton(btnSimpan, new Color(21, 130, 190));
        styleButton(btnUbah, new Color(21, 130, 190));
        styleButton(btnHapus, new Color(190, 60, 60));
        styleButton(btnReset, new Color(100, 130, 150));
        styleButton(btnCetak, new Color(20, 160, 120));

        panelTombol.add(btnSimpan);
        panelTombol.add(btnUbah);
        panelTombol.add(btnHapus);
        panelTombol.add(btnReset);
        panelTombol.add(btnCetak);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        panel.add(panelTombol, gbc);

        btnSimpan.addActionListener(e -> save());
        btnUbah.addActionListener(e -> update());
        btnHapus.addActionListener(e -> delete());
        btnReset.addActionListener(e -> clearForm());
        btnCetak.addActionListener(e -> printData());

        return panel;
    }

    // Menambahkan baris form
    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            JComponent component) {

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(component, gbc);
    }

    // Panel tabel dan pencarian
    private JPanel createTablePanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Data Peserta"));
        panel.setBackground(warnaPutih);

        JPanel panelCari = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelCari.setBackground(warnaPutih);

        txtCari = new JTextField(25);
        btnCari = new JButton("Cari");

        styleButton(btnCari, warnaBiru);

        panelCari.add(new JLabel("Pencarian"));
        panelCari.add(txtCari);
        panelCari.add(btnCari);

        panel.add(panelCari, BorderLayout.NORTH);

        String[] kolom = {
            "ID", "NIM", "Nama", "JK",
            "Prodi", "No. HP", "Email"
        };

        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPeserta = new JTable(model);

        configureTable();

        JScrollPane scroll = new JScrollPane(tblPeserta);
        panel.add(scroll, BorderLayout.CENTER);

        btnCari.addActionListener(e -> search());
        txtCari.addActionListener(e -> search());

        tblPeserta.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                pickData();
            }
        });

        return panel;
    }

    // Mengatur tombol
    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(warnaPutih);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(110, 32));
    }

    // Mengatur tabel
    private void configureTable() {
        if (tblPeserta == null) {
            return;
        }

        tblPeserta.setRowHeight(25);
        tblPeserta.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPeserta.getTableHeader().setReorderingAllowed(false);
        tblPeserta.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    // Mengambil data form
    private Peserta getFormData() {

        Peserta p = new Peserta();

        p.setId(selectedId < 0 ? 0 : selectedId);
        p.setNim(txtNim.getText().trim());
        p.setNama(txtNama.getText().trim());
        p.setJenisKelamin(cmbJenisKelamin.getSelectedItem().toString());
        p.setProdi(txtProdi.getText().trim());
        p.setNoHp(txtNoHp.getText().trim());
        p.setEmail(txtEmail.getText().trim());

        return p;
    }

    // Validasi form
    private boolean validateForm() {

        if (txtNim.getText().trim().isEmpty()
                || txtNama.getText().trim().isEmpty()
                || txtProdi.getText().trim().isEmpty()
                || txtNoHp.getText().trim().isEmpty()
                || txtEmail.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Semua kolom harus diisi!",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE
            );

            return false;
        }

        return true;
    }

    // CREATE
    private void save() {

        if (!validateForm()) {
            return;
        }

        try {
            Peserta p = getFormData();

            if (dao.insert(p)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Data peserta berhasil disimpan!"
                );

                refreshData();
                clearForm();
            }

        } catch (IllegalArgumentException ex) {
            showValidationError(ex);
        } catch (SQLException ex) {
            showError("Menyimpan data", ex);
        }
    }

    // UPDATE
    private void update() {

        if (selectedId < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih data peserta dari tabel terlebih dahulu!"
            );
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            Peserta p = getFormData();

            if (dao.update(p)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Data peserta berhasil diubah!"
                );

                refreshData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Data peserta tidak ditemukan!"
                );
            }

        } catch (IllegalArgumentException ex) {
            showValidationError(ex);
        } catch (SQLException ex) {
            showError("Mengubah data", ex);
        }
    }

    // DELETE
    private void delete() {

        if (selectedId < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih data peserta yang ingin dihapus!"
            );
            return;
        }

        int jawab = JOptionPane.showConfirmDialog(
                this,
                "Apakah kamu yakin ingin menghapus data peserta ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION
        );

        if (jawab != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (dao.delete(selectedId)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Data peserta berhasil dihapus!"
                );

                refreshData();
                clearForm();
            }

        } catch (SQLException ex) {
            showError("Menghapus data", ex);
        }
    }

    // READ
    private void loadTable() {

        try {
            fillTable(dao.findAll());
        } catch (SQLException ex) {
            showError("Memuat data peserta", ex);
        }
    }

    // Mengisi tabel
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

    // Refresh data
    private void refreshData() {
        loadTable();
    }

    // SEARCH
    private void search() {

        try {
            fillTable(dao.search(txtCari.getText().trim()));
        } catch (SQLException ex) {
            showError("Mencari data peserta", ex);
        }
    }

    // Memilih data tabel
    private void pickData() {

        int row = tblPeserta.getSelectedRow();

        if (row < 0) {
            return;
        }

        selectedId = Integer.parseInt(
                tblPeserta.getValueAt(row, 0).toString()
        );

        txtNim.setText(String.valueOf(tblPeserta.getValueAt(row, 1)));
        txtNama.setText(String.valueOf(tblPeserta.getValueAt(row, 2)));
        cmbJenisKelamin.setSelectedItem(
                String.valueOf(tblPeserta.getValueAt(row, 3))
        );
        txtProdi.setText(String.valueOf(tblPeserta.getValueAt(row, 4)));
        txtNoHp.setText(String.valueOf(tblPeserta.getValueAt(row, 5)));
        txtEmail.setText(String.valueOf(tblPeserta.getValueAt(row, 6)));
    }

    // RESET
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
        txtNim.requestFocus();

        loadTable();
    }

    // Cetak data
    private void printData() {

        try {
            boolean berhasil = tblPeserta.print();

            if (berhasil) {
                JOptionPane.showMessageDialog(
                        this,
                        "Data peserta berhasil dicetak!"
                );
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal mencetak data: " + ex.getMessage(),
                    "Cetak Data",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Pesan validasi
    private void showValidationError(IllegalArgumentException ex) {
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Validasi",
                JOptionPane.WARNING_MESSAGE
        );
    }

    // Pesan database
    private void showError(String action, SQLException ex) {
        JOptionPane.showMessageDialog(
                this,
                "Gagal " + action + ":\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PesertaFrame().setVisible(true);
        });
    }
}
