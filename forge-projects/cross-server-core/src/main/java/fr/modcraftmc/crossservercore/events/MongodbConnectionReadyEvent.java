package fr.modcraftmc.crossservercore.events;

import fr.modcraftmc.crossservercore.mongodb.MongodbConnection;
import net.minecraftforge.eventbus.api.Event;

public class MongodbConnectionReadyEvent extends Event {
    private MongodbConnection mongodbConnection;

    public MongodbConnectionReadyEvent(MongodbConnection mongodbConnection) {
        this.mongodbConnection = mongodbConnection;
    }

    public MongodbConnection getMongodbConnection() {
        return mongodbConnection;
    }
}
