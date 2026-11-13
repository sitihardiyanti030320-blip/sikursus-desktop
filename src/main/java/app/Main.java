package app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.LoginFrame;

public class Main {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        // Aplikasi selalu dimulai dari LoginFrame
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}