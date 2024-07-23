package fr.modcraftmc.crossservercore.api.sharedpersistentdata;

import com.mongodb.client.MongoCollection;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.sharedpersistentdata.ISharedDataStore;
import fr.modcraftmc.crossservercore.api.sharedpersistentdata.NotAccessibleException;
import fr.modcraftmc.crossservercore.api.sharedpersistentdata.SharedDataStoreNotReadyException;
import fr.modcraftmc.crossservercore.events.MongodbConnectionReadyEvent;
import net.minecraftforge.common.MinecraftForge;
import org.bson.Document;

public class SharedDataStore implements ISharedDataStore {
    private String id;
    private MongoCollection<Document> collection;

    private boolean ready = false;

    public SharedDataStore(String id){
        this.id = id;

        if(CrossServerCore.getMongodbConnection() != null){
            collection = CrossServerCore.getMongodbConnection().getCollection(id);
            ready = true;
        } else {
            MinecraftForge.EVENT_BUS.addListener(this::onMongodbConnectionReady);
        }
    }

    public void onMongodbConnectionReady(MongodbConnectionReadyEvent event){
        collection = event.getMongodbConnection().getCollection(id);
        ready = true;
    }

    public MongoCollection<Document> access() throws SharedDataStoreNotReadyException {
        if(!ready) throw new SharedDataStoreNotReadyException();
        return collection;
    }

    public MongoCollection<Document> accessOrThrow() {
        if(!ready) throw new NotAccessibleException();
        return collection;
    }
}
