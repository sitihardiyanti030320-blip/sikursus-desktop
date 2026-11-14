package util;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 * Tema visual SIKURSUS: pink sedang (rose pink) dengan desain kartu modern.
 * Nama konstanta warna dipertahankan agar semua frame tetap kompatibel.
 */
public final class Theme {

    // ===================== PALET WARNA (PINK SEDANG) =====================
    public static final Color CREAM = new Color(0xFFF0F5);       // latar halaman
    public static final Color IVORY = new Color(0xFFFAFC);       // latar kartu
    public static final Color WHITE = Color.WHITE;

    public static final Color DARK_BROWN = new Color(0xC2467E);  // pink tua
    public static final Color MID_BROWN = new Color(0xD4608F);   // pink sedang
    public static final Color LIGHT_BROWN = new Color(0xF4B6CE); // pink muda
    public static final Color ACCENT = new Color(0xE8689A);      // pink cerah

    public static final Color BORDER = new Color(0xF3CADA);
    public static final Color TEXT = new Color(0x4F2A3C);
    public static final Color MUTED = new Color(0xA8808F);

    public static final Color SIDEBAR = new Color(0xC2467E);
    public static final Color SIDEBAR_DARK = new Color(0x9E2F62);
    public static final Color SIDEBAR_HOVER = new Color(0xD4608F);

    public static final Color ROW_ALT = new Color(0xFFF5F9);
    public static final Color SELECT = new Color(0xFAD2E2);

    public static final Color BTN_SAVE = new Color(0xE0558F);
    public static final Color BTN_EDIT = new Color(0xA66BC4);
    public static final Color BTN_DELETE = new Color(0xE0525E);
    public static final Color BTN_NEUTRAL = new Color(0xA8909C);
    public static final Color BTN_SUCCESS = new Color(0x4FAE87);

    private static final Color FIELD_BG = new Color(0xFFF7FA);
    private static final Color HEADER_BG = new Color(0xFCDDE9);
    private static final Color DISABLED_BG = new Color(0xF0DEE6);
    private static final Color DISABLED_FG = new Color(0xBCA3AE);

    static {
        install();
    }

    private Theme() {}

    // ===================== INSTALASI LOOK & FEEL =====================
    /** Dipanggil otomatis saat class Theme pertama kali dipakai. */
    public static void install() {
        UIManager.put("Panel.background", CREAM);
        UIManager.put("OptionPane.background", CREAM);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("OptionPane.messageFont", font(Font.PLAIN, 13));
        UIManager.put("OptionPane.buttonFont", font(Font.BOLD, 12));
        UIManager.put("OptionPane.yesButtonText", "Ya");
        UIManager.put("OptionPane.noButtonText", "Tidak");
        UIManager.put("OptionPane.cancelButtonText", "Batal");
        UIManager.put("OptionPane.okButtonText", "OK");

        UIManager.put("Button.background", IVORY);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Button.disabledText", DISABLED_FG);
        UIManager.put("Button.select", SELECT);
        UIManager.put("Button.focus", LIGHT_BROWN);

        UIManager.put("Label.foreground", TEXT);
        UIManager.put("CheckBox.background", IVORY);
        UIManager.put("CheckBox.foreground", TEXT);

        UIManager.put("ScrollBar.thumb", LIGHT_BROWN);
        UIManager.put("ScrollBar.thumbShadow", LIGHT_BROWN);
        UIManager.put("ScrollBar.thumbHighlight", LIGHT_BROWN);
        UIManager.put("ScrollBar.thumbDarkShadow", LIGHT_BROWN);
        UIManager.put("ScrollBar.track", ROW_ALT);

        UIManager.put("MenuBar.background", DARK_BROWN);
        UIManager.put("MenuBar.foreground", WHITE);
        UIManager.put("Menu.background", DARK_BROWN);
        UIManager.put("Menu.foreground", WHITE);
        UIManager.put("Menu.selectionBackground", SIDEBAR_HOVER);
        UIManager.put("Menu.selectionForeground", WHITE);
        UIManager.put("MenuItem.background", IVORY);
        UIManager.put("MenuItem.foreground", TEXT);
        UIManager.put("MenuItem.selectionBackground", SELECT);
        UIManager.put("MenuItem.selectionForeground", DARK_BROWN);
        UIManager.put("MenuItem.disabledForeground", DISABLED_FG);
        UIManager.put("Menu.disabledForeground", DISABLED_FG);
        UIManager.put("PopupMenu.background", IVORY);
        UIManager.put("PopupMenu.border",
                BorderFactory.createLineBorder(BORDER));
        UIManager.put("Separator.foreground", BORDER);
        UIManager.put("Separator.background", IVORY);

        UIManager.put("ComboBox.background", WHITE);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("ComboBox.selectionBackground", SELECT);
        UIManager.put("ComboBox.selectionForeground", DARK_BROWN);
        UIManager.put("List.selectionBackground", SELECT);
        UIManager.put("List.selectionForeground", DARK_BROWN);
        UIManager.put("ToolTip.background", IVORY);
        UIManager.put("ToolTip.foreground", TEXT);
    }

    // ===================== FONT & TEKS =====================
    public static Font font(int style, int size) {
        return new Font("SansSerif", style, size);
    }

    public static Font titleFont(int size) {
        return new Font("Serif", Font.BOLD, size);
    }

    public static String str(Object value) {
        return value == null ? "" : value.toString();
    }

    private static Color blend(Color a, Color b, float t) {
        int r = Math.round(a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, g, bl);
    }

    private static void smooth(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    // ===================== BORDER KARTU (JUDUL DI DALAM) =====================
    private static class CardBorder extends AbstractBorder {
        private static final long serialVersionUID = 1L;

        private final String title;

        CardBorder(String title) {
            this.title = title;
        }

        @Override
        public void paintBorder(Component c, Graphics g,
                                int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);

            // garis tepi membulat
            g2.setColor(BORDER);
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawRoundRect(x + 1, y + 1, w - 3, h - 3, 24, 24);

            // titik aksen + judul
            g2.setColor(ACCENT);
            g2.fillOval(x + 20, y + 17, 9, 9);

            g2.setFont(font(Font.BOLD, 12));
            g2.setColor(DARK_BROWN);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, x + 36, y + 17 + fm.getAscent() - 2);

            // garis pemisah di bawah judul
            g2.setColor(BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(x + 20, y + 38, x + w - 21, y + 38);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(50, 20, 18, 20);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(50, 20, 18, 20);
            return insets;
        }
    }

    // ===================== FRAME & HALAMAN =====================
    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(CREAM);
        frame.setLocationRelativeTo(null);
    }

    /** Panel dengan latar gradasi vertikal (dipakai sidebar & login). */
    public static JPanel gradientPanel(
            final Color top, final Color bottom, LayoutManager layout) {

        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(
                        0, 0, top, 0, getHeight(), bottom));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    /** Kartu header halaman: badge huruf, judul, subjudul, label di kanan. */
    private static JPanel headerCard(String title, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(16, 0)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                smooth(g2);
                int w = getWidth();
                int h = getHeight();

                g2.setColor(IVORY);
                g2.fillRoundRect(0, 0, w - 1, h - 1, 26, 26);

                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1.6f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 26, 26);

                // garis gradasi tipis di dasar kartu
                g2.setPaint(new GradientPaint(
                        26, 0, ACCENT, w - 26, 0, LIGHT_BROWN));
                g2.fillRoundRect(26, h - 8, w - 52, 4, 4, 4);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 22, 20, 22));

        final String letter = title == null || title.isEmpty()
                ? "S" : title.substring(0, 1).toUpperCase();

        JComponent badge = new JComponent() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                smooth(g2);
                int w = getWidth();
                int h = getHeight();

                g2.setPaint(new GradientPaint(
                        0, 0, ACCENT, w, h, DARK_BROWN));
                g2.fillOval(0, 0, w - 1, h - 1);

                g2.setColor(WHITE);
                g2.setFont(titleFont(24));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(letter,
                        (w - fm.stringWidth(letter)) / 2,
                        (h - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(54, 54));

        JPanel badgeWrap = new JPanel(new GridBagLayout());
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(titleFont(22));
        lblTitle.setForeground(DARK_BROWN);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(font(Font.PLAIN, 12));
        lblSub.setForeground(MUTED);

        JPanel textBox = new JPanel(new GridLayout(2, 1, 0, 2));
        textBox.setOpaque(false);
        textBox.add(lblTitle);
        textBox.add(lblSub);

        JComponent pill = new JComponent() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                smooth(g2);
                int w = getWidth();
                int h = getHeight();

                g2.setColor(HEADER_BG);
                g2.fillRoundRect(0, 0, w - 1, h - 1, h, h);

                g2.setColor(DARK_BROWN);
                g2.setFont(font(Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                String t = "SIKURSUS";
                g2.drawString(t,
                        (w - fm.stringWidth(t)) / 2,
                        (h - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        pill.setPreferredSize(new Dimension(104, 28));

        JPanel pillWrap = new JPanel(new GridBagLayout());
        pillWrap.setOpaque(false);
        pillWrap.add(pill);

        card.add(badgeWrap, BorderLayout.WEST);
        card.add(textBox, BorderLayout.CENTER);
        card.add(pillWrap, BorderLayout.EAST);
        return card;
    }

    /**
     * Susunan halaman master: kartu header di atas,
     * lalu kartu form dan kartu tabel di bawahnya.
     */
    public static JPanel createPage(
            String title, String subtitle,
            JPanel formCard, JPanel tableCard) {

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(CREAM);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(20, 26, 0, 26));
        top.add(headerCard(title, subtitle), BorderLayout.CENTER);
        page.add(top, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setBackground(CREAM);
        body.setBorder(BorderFactory.createEmptyBorder(18, 26, 24, 26));
        body.add(formCard, BorderLayout.NORTH);
        body.add(tableCard, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);

        return page;
    }

    // ===================== KARTU & FORM =====================
    public static JPanel createCard(LayoutManager layout, String title) {
        JPanel card = new JPanel(layout) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                smooth(g2);
                g2.setColor(IVORY);
                g2.fillRoundRect(0, 0, getWidth() - 1,
                        getHeight() - 1, 24, 24);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new CardBorder(title));
        return card;
    }

    /**
     * Menambah satu kolom form (label di atas, input di bawah).
     * Form memiliki 2 kolom: col 0 dan col 1.
     */
    public static void addField(JPanel form, int row, int col,
                                String label, JComponent input) {

        styleInput(input);

        JLabel lbl = new JLabel(label);
        lbl.setFont(font(Font.BOLD, 11));
        lbl.setForeground(MID_BROWN);

        int left = col == 0 ? 0 : 14;
        int right = col == 0 ? 14 : 0;

        GridBagConstraints gl = new GridBagConstraints();
        gl.gridx = col;
        gl.gridy = row * 2;
        gl.weightx = 1;
        gl.anchor = GridBagConstraints.WEST;
        gl.fill = GridBagConstraints.HORIZONTAL;
        gl.insets = new Insets(8, left, 3, right);
        form.add(lbl, gl);

        GridBagConstraints gi = new GridBagConstraints();
        gi.gridx = col;
        gi.gridy = row * 2 + 1;
        gi.weightx = 1;
        gi.anchor = GridBagConstraints.WEST;
        gi.fill = GridBagConstraints.HORIZONTAL;
        gi.insets = new Insets(0, left, 2, right);
        form.add(input, gi);
    }

    /** Baris tombol di bawah form (rata TENGAH, melebar 2 kolom). */
    public static void addButtonRow(JPanel form, int row, int count,
                                    JButton... buttons) {

        JPanel bar = new JPanel();
        bar.setLayout(new javax.swing.BoxLayout(
                bar, javax.swing.BoxLayout.X_AXIS));
        bar.setOpaque(false);

        bar.add(javax.swing.Box.createHorizontalGlue());

        for (int i = 0; i < buttons.length; i++) {
            if (i > 0) {
                bar.add(javax.swing.Box.createHorizontalStrut(12));
            }
            bar.add(buttons[i]);
        }

        bar.add(javax.swing.Box.createHorizontalGlue());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row * 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(18, 0, 0, 0);
        form.add(bar, gbc);
    }

    private static void styleInput(JComponent input) {
        if (input instanceof JPasswordField) {
            stylePasswordField((JPasswordField) input);
        } else if (input instanceof JTextField) {
            styleTextField((JTextField) input);
        } else if (input instanceof JComboBox) {
            styleCombo((JComboBox<?>) input);
        } else if (input instanceof JCheckBox) {
            JCheckBox cb = (JCheckBox) input;
            cb.setOpaque(false);
            cb.setFont(font(Font.PLAIN, 13));
            cb.setForeground(TEXT);
            cb.setFocusPainted(false);
            cb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cb.setPreferredSize(new Dimension(100, 36));
        }
    }

    // ===================== INPUT (GAYA GARIS BAWAH) =====================
    private static Border underline(Color line, int thickness) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, thickness, 0, line),
                BorderFactory.createEmptyBorder(
                        7, 8, 6 - (thickness - 2), 8));
    }

    public static void styleTextField(final JTextField field) {
        field.setFont(font(Font.PLAIN, 13));
        field.setForeground(TEXT);
        field.setBackground(FIELD_BG);
        field.setCaretColor(ACCENT);
        field.setSelectionColor(SELECT);
        field.setSelectedTextColor(DARK_BROWN);
        field.setBorder(underline(LIGHT_BROWN, 2));
        field.setPreferredSize(new Dimension(100, 38));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBackground(WHITE);
                field.setBorder(underline(ACCENT, 3));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBackground(FIELD_BG);
                field.setBorder(underline(LIGHT_BROWN, 2));
            }
        });
    }

    public static void stylePasswordField(JPasswordField field) {
        styleTextField(field);
    }

    public static void styleCombo(JComboBox<?> combo) {
        combo.setFont(font(Font.PLAIN, 13));
        combo.setForeground(TEXT);
        combo.setBackground(WHITE);
        combo.setPreferredSize(new Dimension(100, 38));
        combo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ===================== TOMBOL (PERSEGI MEMBULAT, FLAT) =====================
    public static JButton createButton(String text, Color base) {
        return new RoundButton(text, base);
    }

    private static class RoundButton extends JButton {
        private static final long serialVersionUID = 1L;

        private final Color base;
        private boolean hover;
        private boolean pressed;

        RoundButton(String text, Color base) {
            super(text);
            this.base = base;

            setFont(font(Font.BOLD, 12));
            setForeground(WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            FontMetrics fm = getFontMetrics(getFont());
            setPreferredSize(new Dimension(
                    Math.max(96, fm.stringWidth(text) + 40), 38));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    pressed = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        // Agar BoxLayout tidak memperkecil tinggi tombol.
        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);

            Color fill;
            if (!isEnabled()) {
                fill = DISABLED_BG;
            } else if (pressed) {
                fill = blend(base, Color.BLACK, 0.15f);
            } else if (hover) {
                fill = blend(base, Color.WHITE, 0.15f);
            } else {
                fill = base;
            }

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    // ===================== TABEL =====================
    public static void styleTable(JTable table) {
        table.setFont(font(Font.PLAIN, 12));
        table.setForeground(TEXT);
        table.setBackground(WHITE);
        table.setRowHeight(36);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xFBE6EE));
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(SELECT);
        table.setSelectionForeground(DARK_BROWN);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        TableRenderer renderer = new TableRenderer(SwingConstants.LEFT, null);
        table.setDefaultRenderer(Object.class, renderer);
        table.setDefaultRenderer(Number.class, renderer);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(100, 38));
        header.setDefaultRenderer(new HeaderRenderer());
    }

    public static void fixedWidth(JTable table, int column, int width) {
        TableColumn c = table.getColumnModel().getColumn(column);
        c.setMinWidth(width);
        c.setMaxWidth(width);
        c.setPreferredWidth(width);
    }

    public static JScrollPane createScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scroll.getViewport().setBackground(WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    public static JPanel createSearchBar(JTextField field, JButton button) {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);

        JLabel lbl = new JLabel("Cari");
        lbl.setFont(font(Font.BOLD, 12));
        lbl.setForeground(MID_BROWN);

        styleTextField(field);

        bar.add(lbl, BorderLayout.WEST);
        bar.add(field, BorderLayout.CENTER);
        bar.add(button, BorderLayout.EAST);
        return bar;
    }

    /** Renderer sel: zebra, perataan, dan format teks opsional. */
    public static class TableRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        private final int align;
        private final Function<Object, String> formatter;

        public TableRenderer(int align, Function<Object, String> formatter) {
            this.align = align;
            this.formatter = formatter;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {

            String text = formatter != null
                    ? formatter.apply(value) : str(value);

            super.getTableCellRendererComponent(
                    table, text, selected, false, row, column);

            setHorizontalAlignment(align);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

            if (selected) {
                setBackground(SELECT);
                setForeground(DARK_BROWN);
            } else {
                setBackground(row % 2 == 0 ? WHITE : ROW_ALT);
                setForeground(TEXT);
            }
            return this;
        }
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {

            super.getTableCellRendererComponent(
                    table, value, false, false, row, column);

            setFont(font(Font.BOLD, 12));
            setForeground(DARK_BROWN);
            setBackground(HEADER_BG);
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(
                            0, 0, 2, 0, ACCENT),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)));
            return this;
        }
    }

    // ===================== DIALOG =====================
    public static void showMessage(Component parent, String message,
                                   String title, int type) {
        JOptionPane.showMessageDialog(parent, message, title, type);
    }

    public static int showConfirm(Component parent, String message,
                                  String title) {
        return JOptionPane.showConfirmDialog(parent, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }
}