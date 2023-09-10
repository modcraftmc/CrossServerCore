package fr.modcraftmc.crossservercore;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import fr.modcraftmc.crossservercore.message.BaseMessage;
import fr.modcraftmc.crossservercore.message.MessageHandler;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncServer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CrossServerCoreAPI {
    private static boolean isCrossServerCoreLoaded = false;
    private static List<Runnable> runWhenCSCIsReady = new ArrayList<>();

    public static void registerCrossMessage(String messageName, Function<JsonObject, ? extends BaseMessage> deserializer) {
        if(MessageHandler.isMessageRegistered(messageName)){
            CrossServerCore.LOGGER.warn("Trying to register a message via API that is already registered (message id : {})", messageName);
            return;
        }
        MessageHandler.registerCrossMessage(messageName, deserializer);
    }

    public static void sendCrossMessageToAllOtherServer(BaseMessage message) {
        if(!MessageHandler.isMessageRegistered(message.getMessageName())){
            CrossServerCore.LOGGER.warn("Trying to send a message via API that is not registered (message id : {})", message.getMessageName());
            return;
        }
        CrossServerCore.getServerCluster().sendMessageExceptCurrent(message.serializeToString());
    }

    public static void sendCrossMessageToServer(BaseMessage message, String serverName) {
        if(!MessageHandler.isMessageRegistered(message.getMessageName())){
            CrossServerCore.LOGGER.warn("Trying to send a message via API that is not registered (message id : {})", message.getMessageName());
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

    public static Optional<SyncServer> findPlayer(String playerName) {
        return CrossServerCore.getServerCluster().findPlayer(playerName);
    }

    public static MongoCollection<Document> getOrCreateMongoCollection(String collectionName) {
        return CrossServerCore.getMongodbConnection().getCollection(collectionName);
    }

    public static SecurityWatcher getSynchronizationSecurityWatcher() {
        return CrossServerCore.getSynchronizationSecurityWatcher();
    }

    public static void registerOnPlayerJoinedCluster(BiConsumer<String, SyncServer> runnable) {
        CrossServerCore.getPlayersLocation().playerJoinedEvent.add(runnable);
    }

    public static void registerOnPlayerLeftCluster(BiConsumer<String, SyncServer> runnable) {
        CrossServerCore.getPlayersLocation().playerLeavedEvent.add(runnable);
    }

    public static void runWhenCSCIsReady(Runnable runnable) {
        if(isCrossServerCoreLoaded)
            runnable.run();
        else
            runWhenCSCIsReady.add(runnable);
    }

    public static void APIInit() {
        CrossServerCore.LOGGER.info("Enabling CrossServerCore API");
        isCrossServerCoreLoaded = true;
        runWhenCSCIsReady.forEach(Runnable::run);
    }
}
