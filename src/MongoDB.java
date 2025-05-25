import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MongoDB {
    private final MongoClient mongoClient = MongoClients.create("mongodb://root:root@localhost:27017");
    private final String databaseName = "fact";
    private final String collectionName = "fact";


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

    public void insertOne(Document d) {
        MongoCollection<Document> collection = mongoClient.getDatabase(databaseName).getCollection(collectionName);
        collection.insertOne(d);
    }
}
