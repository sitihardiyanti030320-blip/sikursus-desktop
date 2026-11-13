package view;

import dao.InstrukturDAO;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Instruktur;

public class InstrukturFrame extends JFrame {
    private final JTextField nama = new JTextField();
    private final JTextField keahlian = new JTextField();
    private final JTextField hp = new JTextField();
    private final JTextField email = new JTextField();
    private final JTextField search = new JTextField(18);
    private final JTable table = new JTable(ViewUtil.tableModel("ID", "Nama", "Keahlian", "No. HP", "Email"));
    private final InstrukturDAO dao = new InstrukturDAO();
    private int selected = -1;

    public InstrukturFrame() {
        super("Master Instruktur - SIKURSUS Desktop");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        ViewUtil.setupFrame(this, 940, 650);
        build();
        load("");
    }

    private void build() {
        JPanel root = ViewUtil.root("Master Data Instruktur");
        JPanel body = ViewUtil.body();

        JPanel form = ViewUtil.formPanel();
        ViewUtil.addFormRow(form, 0, "Nama Instruktur", nama);
        ViewUtil.addFormRow(form, 1, "Keahlian", keahlian);
        ViewUtil.addFormRow(form, 2, "Nomor HP", hp);
        ViewUtil.addFormRow(form, 3, "Email", email);

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

        body.add(ViewUtil.section("Form Instruktur", formBox), BorderLayout.NORTH);
        body.add(ViewUtil.section("Data Instruktur", tableBox), BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);

        simpan.addActionListener(e -> save());
        ubah.addActionListener(e -> update());
        hapus.addActionListener(e -> delete());
        reset.addActionListener(e -> clear());
        cari.addActionListener(e -> load(search.getText()));
        search.addActionListener(e -> load(search.getText()));
        table.getSelectionModel().addListSelectionListener(e -> pick());
        getRootPane().setDefaultButton(simpan);
    }

    private Instruktur data() {
        Instruktur x = new Instruktur();
        x.setId(Math.max(selected, 0));
        x.setNama(nama.getText());
        x.setKeahlian(keahlian.getText());
        x.setNoHp(hp.getText());
        x.setEmail(email.getText());
        return x;
    }

    private void save() { try { dao.insert(data()); load(""); clear(); } catch (Exception ex) { handle(ex, "Menyimpan instruktur"); } }
    private void update() { if (selected < 0) { JOptionPane.showMessageDialog(this, "Pilih instruktur."); return; } try { dao.update(data()); load(""); clear(); } catch (Exception ex) { handle(ex, "Mengubah instruktur"); } }
    private void delete() { if (selected < 0) return; if (JOptionPane.showConfirmDialog(this, "Hapus instruktur?", "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return; try { dao.delete(selected); load(""); clear(); } catch (SQLException ex) { ViewUtil.sqlError(this, "Menghapus instruktur", ex); } }

    private void load(String q) {
        try {
            List<Instruktur> list = dao.search(q);
            DefaultTableModel m = (DefaultTableModel) table.getModel();
            m.setRowCount(0);
            for (Instruktur x : list) m.addRow(new Object[]{x.getId(), x.getNama(), x.getKeahlian(), x.getNoHp(), x.getEmail()});
        } catch (SQLException ex) { ViewUtil.sqlError(this, "Memuat instruktur", ex); }
    }

    private void pick() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        selected = Integer.parseInt(table.getValueAt(r, 0).toString());
        nama.setText(table.getValueAt(r, 1).toString());
        keahlian.setText(table.getValueAt(r, 2).toString());
        hp.setText(String.valueOf(table.getValueAt(r, 3)));
        email.setText(String.valueOf(table.getValueAt(r, 4)));
    }

    private void clear() { selected = -1; nama.setText(""); keahlian.setText(""); hp.setText(""); email.setText(""); table.clearSelection(); nama.requestFocus(); }
    private void handle(Exception ex, String action) { if (ex instanceof SQLException) ViewUtil.sqlError(this, action, (SQLException) ex); else JOptionPane.showMessageDialog(this, ex.getMessage()); }
}