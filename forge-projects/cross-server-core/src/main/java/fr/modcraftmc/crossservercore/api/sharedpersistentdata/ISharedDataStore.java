package fr.modcraftmc.crossservercore.api.sharedpersistentdata;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public interface ISharedDataStore {
    public MongoCollection<Document> access() throws SharedDataStoreNotReadyException;
}
