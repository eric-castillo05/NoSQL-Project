import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class MongoDB {
    private final MongoClient mongoClient = MongoClients.create("mongodb://root:root@localhost:27017");
    private final String databaseName = "fact";
    private final String collectionName = "fact";

    /**
     * Recupera todos los documentos de la colección 'fact' en la base de datos.
     * Imprime los documentos en formato JSON en la consola y los retorna como lista.
     *
     * @return Lista de documentos obtenidos de la colección.
     */
    public List<Document> returnAll(){
        List<Document> results = new ArrayList<>();
        try {
            MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
            for (Document document : collection.find()) {
                System.out.println(document.toJson());
            }
            results = collection.find().into(new ArrayList<>());
        } catch (Exception e) {
            System.out.println("Error");
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Inserta un único documento en la colección 'fact'.
     *
     * @param d Documento a insertar.
     */
    public void insertOne(Document d) {
        MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
        collection.insertOne(d);
    }
}
