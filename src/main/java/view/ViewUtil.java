package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import util.LookupItem;

public final class ViewUtil {
    public static final Color BG = new Color(243, 246, 251);
    public static final Color CARD = Color.WHITE;
    public static final Color BORDER = new Color(209, 213, 219);
    public static final Color TEXT = new Color(31, 41, 55);
    public static final Color MUTED = new Color(107, 114, 128);
    private static final Color PRIMARY = new Color(37, 99, 235);
    private static final Color UPDATE = new Color(14, 116, 144);
    private static final Color DANGER = new Color(220, 38, 38);
    private static final Color NEUTRAL = new Color(100, 116, 139);

    private ViewUtil() {}

    public static void setupFrame(JFrame f, int w, int h) {
        f.setSize(w, h);
        f.setMinimumSize(new Dimension(Math.min(w, 600), Math.min(h, 400)));
        f.setLocationRelativeTo(null);
    }

    public static JPanel root(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        JLabel header = new JLabel(title);
        header.setOpaque(true);
        header.setBackground(PRIMARY);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        p.add(header, BorderLayout.NORTH);
        return p;
    }

    public static JPanel body() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        return p;
    }

    public static JPanel section(String title, JComponent content) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER), title));
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    public static JPanel formPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        return p;
    }

    public static void addFormRow(JPanel form, int row, String label, JComponent field) {
        GridBagConstraints l = new GridBagConstraints();
        l.gridx = 0; l.gridy = row; l.anchor = GridBagConstraints.WEST;
        l.insets = new Insets(4, 4, 4, 12);
        form.add(new JLabel(label), l);
        GridBagConstraints f = new GridBagConstraints();
        f.gridx = 1; f.gridy = row; f.weightx = 1.0;
        f.fill = GridBagConstraints.HORIZONTAL;
        f.insets = new Insets(4, 0, 4, 4);
        form.add(field, f);
    }

    private static JButton button(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return b;
    }

    public static JButton primaryButton(String t) { return button(t, PRIMARY); }
    public static JButton updateButton(String t) { return button(t, UPDATE); }
    public static JButton dangerButton(String t) { return button(t, DANGER); }
    public static JButton neutralButton(String t) { return button(t, NEUTRAL); }

    public static JPanel actionPanel(JButton... buttons) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        p.setBackground(CARD);
        for (JButton b : buttons) p.add(b);
        return p;
    }

    public static JPanel searchPanel(String label, JTextField field, JButton button) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setBackground(CARD);
        p.add(new JLabel(label));
        p.add(field);
        p.add(button);
        return p;
    }

    public static JScrollPane tableScroll(JTable table) {
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        return new JScrollPane(table);
    }

    public static DefaultTableModel tableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
    }

    public static JLabel note(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(MUTED);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        return l;
    }

    public static String today() {
        return LocalDate.now().toString(); // yyyy-MM-dd
    }

    public static void selectLookup(JComboBox<LookupItem> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    public static void sqlError(Component parent, String action, SQLException ex) {
        String msg = ex.getMessage();
        if (ex.getErrorCode() == 1062) msg = "Data duplikat (nilai unik sudah ada).";
        else if (ex.getErrorCode() == 1451) msg = "Data masih dipakai data lain sehingga tidak bisa dihapus.";
        JOptionPane.showMessageDialog(parent,
                action + " gagal:\n" + msg, "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}