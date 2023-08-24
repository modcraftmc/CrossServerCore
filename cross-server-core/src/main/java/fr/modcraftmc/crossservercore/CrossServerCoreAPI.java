package fr.modcraftmc.crossservercore;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import fr.modcraftmc.crossservercore.message.BaseMessage;
import fr.modcraftmc.crossservercore.message.MessageHandler;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncServer;
import org.bson.Document;

import java.util.Map;
import java.util.function.Function;

public class CrossServerCoreAPI {
    public static void registerCrossMessage(String messageName, Function<JsonObject, ? extends BaseMessage> deserializer) {
        MessageHandler.registerCrossMessage(messageName, deserializer);
    }

    public static void sendCrossMessageToAllOtherServer(BaseMessage message) {
        if(!MessageHandler.isMessageRegistered(message.getMessageName())){
            CrossServerCore.LOGGER.warn("Trying to send a message via API that is not registered");
            return;
        }
        CrossServerCore.getServerCluster().sendMessageExceptCurrent(message.serializeToString());
    }

    public static void sendCrossMessageToServer(BaseMessage message, String serverName) {
        if(!MessageHandler.isMessageRegistered(message.getMessageName())){
            CrossServerCore.LOGGER.warn("Trying to send a message via API that is not registered");
            return;
        }
        CrossServerCore.getServerCluster().getServer(serverName).ifPresent(server -> server.sendMessage(message.serializeToString()));
    }

    public static String getServerName() {
        return CrossServerCore.getServerName();
    }

    public static Map<String, SyncServer> getPlayerLocationMap(){
        return Map.copyOf(CrossServerCore.getPlayersLocation().getPlayerServerMap());
    }

    public static SyncServer findPlayer(String playerName) {
        return CrossServerCore.getServerCluster().findPlayer(playerName);
    }

    public static MongoCollection<Document> getOrCreateMongoCollection(String collectionName) {
        return CrossServerCore.getMongodbConnection().getCollection(collectionName);
    }

    public static SecurityWatcher getSynchronizationSecurityWatcher() {
        return CrossServerCore.getSynchronizationSecurityWatcher();
    }
}
