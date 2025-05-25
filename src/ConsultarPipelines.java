import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

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
                    results = (pipelines.primeraConsulta());
                } else if (seleccion.equals("Segunda Consulta")) {
                    results = (pipelines.segundaConsulta());
                } else if (seleccion.equals("Tercera Consulta")) {
                    results = (pipelines.terceraConsulta());
                } else if (seleccion.equals("Cuarta Consulta")) {
                    results = (pipelines.cuartaConsulta());
                } else if (seleccion.equals("Quinta Consulta")) {
                    results = (pipelines.quintaConsulta());
                } else if (seleccion.equals("Sexta Consulta")) {
                    results = pipelines.sextaConsulta();
                }
                showResults(results);
            }
        });
    }

    public void showResults(List<Document> results){
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(ConsultarPipelines.this, "No hay información para mostrar", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List<String> columns = getColumns(results);
        System.out.println(columns);
        String [] columnNames = columns.toArray(new String[0]);
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        //tableModel.setRowCount(0);
        for(Document d : results){
            Object[] row = new Object[columnNames.length];
            Map<String, Object> flatMap = new HashMap<>();

            for (String key : d.keySet()) {
                Object value = d.get(key);
                if (value instanceof Document nested) {
                    for (String nestedKey : nested.keySet()) {
                        flatMap.put(nestedKey, nested.get(nestedKey));
                    }
                } else {
                    flatMap.put(key, value);
                }
            }

            for (int i = 0; i < columnNames.length; i++) {
                row[i] = flatMap.get(columnNames[i]);
            }

            tableModel.addRow(row);
        }

        table1.setModel(tableModel);
    }

    public List<String> getColumns(List<Document> d){
        Set<String> columns = new LinkedHashSet<>();
        for (Document doc : d) {
            for(String key : doc.keySet()){
                Object value = doc.get(key);
                if (value instanceof Document nested) {
                    columns.addAll(nested.keySet());
                } else {
                    columns.add(key);
                }
            }
        }
        return new ArrayList<>(columns);
    }

}
