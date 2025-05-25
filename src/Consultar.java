import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Consultar extends JFrame{
    private JPanel consultarFrame;
    private JButton regresarButton;

    public Consultar() {
        setTitle("Ventana de Consulta");
        setContentPane(consultarFrame);
        setSize(800, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        regresarButton.addActionListener(new ActionListener() {
            /**
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                new main();
                dispose();
            }
        });
    }
}
