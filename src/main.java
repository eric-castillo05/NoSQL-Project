import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class main extends JFrame{
    private JButton consultarButton;
    private JButton insertarButton;
    private JButton consultasPipelinesButton;
    private JPanel mainFrame;

    public main() {
        setTitle("Sistema de Gestión de Pipelines");
        setContentPane(mainFrame);
        setSize(800, 300);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        consultarButton.addActionListener(new ActionListener() {
            /**
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                new Consultar();
                dispose();
            }
        });

        consultasPipelinesButton.addActionListener(new ActionListener() {
            /**
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                new ConsultarPipelines();
                dispose();
            }
        });

        insertarButton.addActionListener(new ActionListener() {
            /**
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                new Insertar();
                dispose();

            }
        });
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("GTK+".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new main();
        });
    }
}
