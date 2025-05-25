import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.mongodb.client.model.Accumulators.addToSet;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Filters.eq;

public class Pipelines {
    private final MongoClient mongoClient = MongoClients.create("mongodb://root:root@localhost:27017");
    private final String databaseName = "fact";
    private final String collectionName = "fact";
    private List<Document> results = new ArrayList<>();

    /**
     * Obtener el número de facturas existentes por cada cliente en la colección fact.
     *
     * @return Lista de documentos con el cliente y el total de facturas.
     */
    public List<Document> primeraConsulta(){
        try {
            MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
            Bson group = group("$cliente", sum("totalFact", 1));
            results = collection.aggregate(List.of(group)).into(new ArrayList<>());
            for (Document doc : results) {
                System.out.println(doc.toJson());
            }
        } catch (Exception e) {
            System.out.println("Error");
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Obtener el número de facturas existentes por cada cliente de cada año en la
     * colección fact. Nota: en tu colección debe haber facturas asociados a un
     * mismo cliente pero de diferentes años
     *
     * @return Lista de documentos con cliente, año y total de facturas.
     */
    public List<Document> segundaConsulta(){
        try{
            MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
            Bson group = group(
                    new Document ("cliente", "$cliente").append("year", "$fecha.year"),
                            sum("totalFact", 1)
            );
            Bson sort = sort(ascending("_id.cliente"));
            results = collection.aggregate(List.of(group, sort)).into(new ArrayList<>());
            for (Document doc : results) {
                System.out.println(doc.toJson());
            }

        } catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Obtener el id del producto y sus ventas en pesos, que generó más ventas en
     * pesos durante el año 2020.
     *
     * @return Lista con un solo documento que contiene el producto con menor total.
     */
    public List<Document> terceraConsulta(){
        try{
            MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
            Bson match = match(eq("fecha.year", 2020));
            Bson unwind = unwind("$productos");
            Bson group = group("$productos.id_p",
                    sum("total", new Document("$multiply", Arrays.asList("$productos.PUV", "$productos.Cantidad")))
            );
            Bson sort = sort(ascending("total"));
            Bson limit = limit(1);
            results = collection.aggregate(List.of(match, unwind, group, sort, limit)).into(new ArrayList<>());
            for (Document doc : results) {
                System.out.println(doc.toJson());
            }
        } catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Obtener por cada cliente, el número de productos diferentes que ha
     * comprado y cuales son dichos productos (seleccionar el nombre del
     * producto).
     *
     * @return Lista de documentos con cliente, productos distintos y conteo total.
     */
    public List<Document> cuartaConsulta(){
        try{
            MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
            Bson unwind = unwind("$productos");
            Bson group = group("$cliente",
                    addToSet("diff_product", "$productos.nombre")
            );
            Bson project = project(new Document("_id", 1)
                    .append("diff_product", 1)
                    .append("total", new Document("$size", "$diff_product")));
            results = collection.aggregate(List.of(unwind, group, project)).into(new ArrayList<>());
            for (Document doc : results) {
                System.out.println(doc.toJson());
            }
        } catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Obtener el gran total en pesos de cada factura. Considera para este calculo
     * el IVA.
     *
     * @return Lista de documentos con cliente y total de ventas con IVA.
     */
    public List<Document> quintaConsulta(){
        try{
            MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
            Bson unwind = unwind("$productos");
            Bson group = group("$cliente",
                    sum("total_IVA", new Document("$multiply", Arrays.asList(
                            new Document("$toDouble", "$productos.PUV"),
                            new Document("$toDouble", "$productos.Cantidad"),
                            new Document("$toDouble", "$productos.IVA")
                    ))),
                    sum("total", new Document("$multiply", Arrays.asList(
                            new Document("$toDouble", "$productos.PUV"),
                            new Document("$toDouble", "$productos.Cantidad")
                    )))
            );

//            Bson group = group("$cliente",
//                    sum("total_IVA", new Document("$multiply", Arrays.asList("$productos.PUV", "$productos.Cantidad", "productos.IVA"))),
//                    sum("total", new Document("$multiply", Arrays.asList("$productos.PUV", "$productos.Cantidad")))
//            );
            Bson project = project(new Document("_id", 1)
                            .append("total_IVA", new Document("$add", Arrays.asList("$total_IVA", "$total")))
                    );
            results = collection.aggregate(List.of(unwind, group, project)).into(new ArrayList<>());
            for (Document doc : results) {
                System.out.println(doc.toJson());
            }
        } catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Obtener la ganancia neta por cada factura. La ganancia por cada producto
     * vendido se obtiene restando PUV – PUC.
     *
     * @return Lista de documentos con cliente y ganancia total.
     */
    public List<Document> sextaConsulta(){
        try{
            MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
            Bson unwind = unwind("$productos");
//            Bson group = group("$cliente",
//                    sum("ganancia",
//                            new Document("$multiply",
//                                    Arrays.asList(
//                                            new Document("$substract", Arrays.asList("$productos.PUV", "$productos.PUC")), "$productos.Cantidad")))
//            );
            Bson group = group("$cliente",
                    sum("ganancia", new Document("$multiply", Arrays.asList(
                            new Document("$subtract", Arrays.asList(
                                    new Document("$toDouble", "$productos.PUV"),
                                    new Document("$toDouble", "$productos.PUC")
                            )),
                            new Document("$toDouble", "$productos.Cantidad")
                    )))
            );

            results = collection.aggregate(List.of(unwind, group)).into(new ArrayList<>());
            for (Document doc : results) {
                System.out.println(doc.toJson());
            }
        } catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
        }
        return results;
    }
}
