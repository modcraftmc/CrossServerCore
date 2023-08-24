package fr.modcraftmc.crossservercore.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongodbConnection {
    private final MongoClient mongoClient;
    private final String databaseName;

    public MongodbConnection(MongoClient mongoClient, String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        return mongoClient.getDatabase(databaseName).getCollection(collectionName);
    }

    public void close() {
        mongoClient.close();
    }
}
