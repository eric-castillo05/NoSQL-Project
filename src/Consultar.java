import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Consultar extends JFrame{
    private JPanel consultarFrame;
    private JButton regresarButton;
    private JTable table1;
    private JButton consultarButton;

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
        consultarButton.addActionListener(new ActionListener() {
            /**
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                MongoDB mongoDB = new MongoDB();
                showResults(mongoDB.returnAll());
            }
        });
    }

    public void showResults(List<Document> results){
        if (results == null || results.isEmpty()) {
            JOptionPane.showMessageDialog(Consultar.this, "No hay información para mostrar", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<String> columns = getColumns(results);
        System.out.println("Columns: " + columns);
        String[] columnNames = columns.toArray(new String[0]);
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (Document d : results) {
            Object id = d.get("_id");
            String idStr = id != null ? id.toString() : "";
            Document fechaDoc = d.get("fecha", Document.class);
            String cliente = d.getString("cliente");
            Object productosObj = d.get("productos");

            String fechaStr = "";
            if (fechaDoc != null) {
                try {
                    fechaStr = String.format("%d-%02d-%02d %02d:%02d",
                            fechaDoc.getInteger("year", 0),
                            fechaDoc.getInteger("mes", 0),
                            fechaDoc.getInteger("dia", 0),
                            fechaDoc.getInteger("hora", 0),
                            fechaDoc.getInteger("minuto", 0));
                } catch (Exception e) {
                    System.out.println("Error formatting fecha: " + e.getMessage());
                }
            }

            List<Document> productos = new ArrayList<>();
            if (productosObj instanceof List) {
                for (Object item : (List<?>) productosObj) {
                    if (item instanceof Document) {
                        productos.add((Document) item);
                    }
                }
            } else if (productosObj instanceof Document) {
                productos.add((Document) productosObj);
            }

            for (Document prod : productos) {
                Object[] row = new Object[columnNames.length];
                row[0] = idStr; // _id
                row[1] = fechaStr; // fecha
                row[2] = cliente != null ? cliente : ""; // cliente
                row[3] = prod.getString("id_p"); // id_p
                row[4] = prod.getString("nombre"); // nombre
                row[5] = prod.getDouble("PUV"); // PUV
                row[6] = prod.getInteger("Cantidad"); // Cantidad
                row[7] = prod.getDouble("IVA"); // IVA
                row[8] = prod.getDouble("PUC"); // PUC
                tableModel.addRow(row);
            }

            if (productos.isEmpty()) {
                Object[] row = new Object[columnNames.length];
                row[0] = idStr;
                row[1] = fechaStr;
                row[2] = cliente != null ? cliente : "";
                tableModel.addRow(row);
            }
        }

        table1.setModel(tableModel);
        table1.repaint();
        table1.revalidate();
    }


    public List<String> getColumns(List<Document> d){
        return Arrays.asList("_id", "fecha", "cliente", "id_p", "nombre", "PUV", "Cantidad", "IVA", "PUC");
    }
}
