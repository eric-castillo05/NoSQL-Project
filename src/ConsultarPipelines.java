import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                List<Document> results = new ArrayList<>();
                String seleccion = (String) comboBox1.getSelectedItem();
                if (seleccion.equals("Primera Consulta")) {
                    showResults(pipelines.primeraConsulta());
                } else if (seleccion.equals("Segunda Consulta")) {
                    showResults(pipelines.segundaConsulta());
                } else if (seleccion.equals("Tercera Consulta")) {
                    pipelines.terceraConsulta();
                } else if (seleccion.equals("Cuarta Consulta")) {
                    pipelines.cuartaConsulta();
                } else if (seleccion.equals("Quinta Consulta")) {
                    pipelines.quintaConsulta();
                } else if (seleccion.equals("Sexta Consulta")) {
                    pipelines.sextaConsulta();
                }
                showResults(results);
            }
        });


    }
    public void showResults(List<Document> results){
        if (results.isEmpty() && results == null) {
            JOptionPane.showMessageDialog(ConsultarPipelines.this, "No hay información para mostrar", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Document firstR = results.get(0);
        Set<String> columns = new HashSet<>();
        for (Document d : results) {
            columns.addAll(d.keySet());
        }
        System.out.println(columns);
        String [] columnNames = columns.toArray(new String[0]);
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        tableModel.setRowCount(0);
        for(Document d : results){
            Object[] row = new Object[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                row[i] = d.get(columnNames[i]);
            }
            tableModel.addRow(row);
        }
        table1.setModel(tableModel);
    }

}
