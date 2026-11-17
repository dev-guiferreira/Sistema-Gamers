package sistemagamess;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sistemagames.view.TelaPrincipal;

public class SistemaGamess {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ex) {
                // Mantém o look and feel padrão caso Nimbus não esteja disponível.
            }
            new TelaPrincipal().setVisible(true);
        });
    }
}
