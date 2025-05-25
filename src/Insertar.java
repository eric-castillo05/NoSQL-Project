import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.bson.Document;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Insertar extends JFrame {
    private JPanel insertarFrame;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JButton agregarButton;
    private JTable table1;
    private JButton agregarProductoButton;
    private JTextField textField7;
    private DefaultTableModel tableModel;
    private List<List<Object>> productos = new ArrayList<>();
    private MongoDB mongoDB = new MongoDB();

    public Insertar(){
        setTitle("Ventana de Consulta");
        setContentPane(insertarFrame);
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        String[] columnNames = {"id_p", "nombre", "PUV", "Cantidad", "IVA", "PUC"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table1.setModel(tableModel);
        agregarButton.addActionListener(new ActionListener() {
            /**
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean error = false;
                String[] campos = {"id_p", "nombre", "PUV", "Cantidad", "IVA", "PUC"};
                String[] valores = new String[6];
                String text1 = textField1.getText();
                String text2 = textField2.getText();
                String text3 = textField3.getText();
                String text4 = textField4.getText();
                String text5 = textField5.getText();
                String text6 = textField6.getText();
                String text7 = textField7.getText();
                valores[0] = text1 != null ? text1.trim() : "";
                valores[1] = text2 != null ? text2.trim() : "";
                valores[2] = text3 != null ? text3.trim() : "";
                valores[3] = text4 != null ? text4.trim() : "";
                valores[4] = text5 != null ? text5.trim() : "";
                valores[5] = text6 != null ? text6.trim() : "";
                for (int i = 0; i < campos.length; i++) {
                    if (valores[i] != null && !valores[i].isEmpty()) {
                        if (i == 3) {
                            try {
                                Integer.parseInt(valores[i]);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(Insertar.this,
                                        String.format("El campo %s debe de ser un número", campos[i]),
                                        "Fallo",
                                        JOptionPane.ERROR_MESSAGE);
                                error = true;
                                break;
                            }
                        } else if (i == 2 || i == 5 ||  i == 4) {
                            try {
                                Double.parseDouble(valores[i]);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(Insertar.this,
                                        String.format("El campo %s debe de ser un número", campos[i]),
                                        "Fallo",
                                        JOptionPane.ERROR_MESSAGE);
                                error = true;
                                break;
                            }

                        }
                    } else if (i != 4){
                            JOptionPane.showMessageDialog(Insertar.this,
                                    String.format("Todos los campos son obligatorion con exception del IVA", campos[i]),
                                    "Fallo",
                                    JOptionPane.ERROR_MESSAGE);
                            error = true;
                            break;

                    }


                }
                if (text7 == null || text7.isEmpty()) {
                    JOptionPane.showMessageDialog(Insertar.this,
                            "El campo nombre es oblogatorio",
                            "Fallo",
                            JOptionPane.ERROR_MESSAGE);
                    error = true;
                }
                if (error) return;
                List<Object> producto = Arrays.asList(
                        valores[0], // id_p (String)
                        valores[1], // nombre (String)
                        valores[2].isEmpty() ? 0.0 : Double.parseDouble(valores[2]), // PUV (Double)
                        valores[3].isEmpty() ? 0 : Integer.parseInt(valores[3]), // Cantidad (Integer)
                        valores[4].isEmpty() ? 1.16 : Double.parseDouble(valores[4]), // IVA (Double)
                        valores[5].isEmpty() ? 0.0 : Double.parseDouble(valores[5]) // PUC (Double)
                );
                if (!producto.isEmpty()){
                    productos.add(producto);
                }

                tableModel.addRow(new Object[]{
                        valores[0],
                        valores[1],
                        valores[2],
                        valores[3],
                        valores[4].isEmpty() ? "1.16" : valores[4],
                        valores[5]
                });
                table1.repaint();
                textField1.setEnabled(true);
                cleanFields();
            }
        });
        agregarProductoButton.addActionListener(new ActionListener() {
            /**
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (productos.isEmpty()) {
                    JOptionPane.showMessageDialog(Insertar.this, "Agrega al menos un producto", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    // Crear lista Document para MongoDB
                    List<Document> productosDocs = new ArrayList<>();
                    for (List<Object> p : productos) {
                        Document prodDoc = new Document()
                                .append("id_p", p.get(0))
                                .append("nombre", p.get(1))
                                .append("PUV", (Double)p.get(2))
                                .append("Cantidad", (Integer)p.get(3))
                                .append("IVA", (Double)p.get(4))
                                .append("PUC", (Double)p.get(5));
                        productosDocs.add(prodDoc);
                    }
                    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                    Document fecha = new Document()
                            .append("dia", now.getDayOfMonth())
                            .append("mes", now.getMonthValue())
                            .append("year", now.getYear())
                            .append("hora", now.getHour())
                            .append("minuto", now.getMinute());
                    Document d = new Document();

                    d.append("fecha", fecha).append("nombre",textField7.getText()).append("productos", (productosDocs));
                    System.out.println(d.toJson());
                    try {
                        mongoDB.insertOne(d);
                        productos.clear();
                        tableModel.setRowCount(0);
                        cleanFields();
                        JOptionPane.showMessageDialog(Insertar.this, "Insertado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex){
                        System.out.println(ex);
                        JOptionPane.showMessageDialog(Insertar.this, "Hubo un fallo al insertar", "Fallo", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                    JOptionPane.showMessageDialog(Insertar.this, "Hubo un fallo al insertar", "Fallo", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    public void cleanFields(){
        textField1.setText("");
        textField2.setText("");
        textField3.setText("");
        textField4.setText("");
        textField5.setText("");
        textField6.setText("");
    }
}
