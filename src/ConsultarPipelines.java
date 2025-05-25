import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConsultarPipelines extends JFrame{
    private JPanel pipelinesFrame;
    private JComboBox comboBox1;
    private JButton button1;
    private JTable table1;

    public ConsultarPipelines() {
        setTitle("Ventana de Consulta de Pipelines");
        setContentPane(pipelinesFrame);
        setSize(800, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);


        comboBox1.addItem("Primera Consulta");
        comboBox1.addItem("Segunda Consulta");
        comboBox1.addItem("Tercera Consulta");
        comboBox1.addItem("Cuarta Consulta");
        comboBox1.addItem("Quinta Consulta");
        comboBox1.addItem("Sexta Consulta");

        button1.addActionListener(new ActionListener() {
            /**
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Pipelines pipelines = new Pipelines();
                String seleccion = (String) comboBox1.getSelectedItem();
                if (seleccion.equals("Primera Consulta")) {
                    pipelines.primeraConsulta();
                } else if (seleccion.equals("Segunda Consulta")) {
                    pipelines.segundaConsulta();
                } else if (seleccion.equals("Tercera Consulta")) {
                    pipelines.terceraConsulta();
                } else if (seleccion.equals("Cuarta Consulta")) {
                    pipelines.cuartaConsulta();
                } else if (seleccion.equals("Quinta Consulta")) {
                    pipelines.quintaConsulta();
                } else if (seleccion.equals("Sexta Consulta")) {
                    pipelines.sextaConsulta();
                }
            }
        });
    }

}
