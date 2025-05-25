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

    public void primeraConsulta(){
        try {
            MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
            Bson group = group("$cliente", sum("totalFact", 1));
            List<Document> results = collection.aggregate(List.of(group)).into(new ArrayList<>());
            for (Document doc : results) {
                System.out.println(doc.toJson());
            }
        } catch (Exception e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    public void segundaConsulta(){
        try{
            MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
            Bson group = group(
                    new Document ("cliente", "$cliente").append("year", "$fecha.year"),
                            sum("totalFact", 1)
            );
            Bson sort = sort(ascending("_id.cliente"));
            List<Document> results = collection.aggregate(List.of(group, sort)).into(new ArrayList<>());
            for (Document doc : results) {
                System.out.println(doc.toJson());
            }

        } catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    public void terceraConsulta(){
        try{
            MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
            Bson match = match(eq("fecha.year", 2020));
            Bson unwind = unwind("$productos");
            Bson group = group("$productos.id_p",
                    sum("total", new Document("$multiply", Arrays.asList("$productos.PUV", "$productos.Cantidad")))
            );
            Bson sort = sort(ascending("total"));
            Bson limit = limit(1);
            List<Document> results = collection.aggregate(List.of(match, unwind, group, sort, limit)).into(new ArrayList<>());
            for (Document doc : results) {
                System.out.println(doc.toJson());
            }
        } catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    public void cuartaConsulta(){
        try{
            MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
            Bson unwind = unwind("$productos");
            Bson group = group("$cliente",
                    addToSet("diff_product", "$productos.nombre")
            );
            Bson project = project(new Document("_id", 1)
                    .append("diff_product", 1)
                    .append("total", new Document("$size", "$diff_product")));
            List<Document> results = collection.aggregate(List.of(unwind, group, project)).into(new ArrayList<>());
            for (Document doc : results) {
                System.out.println(doc.toJson());
            }
        } catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
        }
    }
    public void quintaConsulta(){
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
            List<Document> results = collection.aggregate(List.of(unwind, group, project)).into(new ArrayList<>());
            for (Document doc : results) {
                System.out.println(doc.toJson());
            }
        } catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
        }
    }
    public void sextaConsulta(){
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

            List<Document> results = collection.aggregate(List.of(unwind, group)).into(new ArrayList<>());
            for (Document doc : results) {
                System.out.println(doc.toJson());
            }
        } catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
        }
    }
}
