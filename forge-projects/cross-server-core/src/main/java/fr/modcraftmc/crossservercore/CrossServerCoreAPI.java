package fr.modcraftmc.crossservercore;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;
import fr.modcraftmc.crossservercore.dataintegrity.SecurityWatcher;
import fr.modcraftmc.crossservercore.message.BaseMessage;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncServer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Deprecated()
public class CrossServerCoreAPI {
    private static boolean isCrossServerCoreLoaded = false;
    private static List<Runnable> runWhenCSCIsReady = new ArrayList<>();


    public static void registerCrossMessage(String messageName, Function<JsonObject, ? extends BaseMessage> deserializer) {
        if(CrossServerCore.getMessageHandler().isMessageRegistered(messageName)){
            CrossServerCore.LOGGER.warn("Trying to register a message via API that is already registered (message id : {})", messageName);
            return;
        }
        CrossServerCore.getMessageHandler().oldRegisterCrossMessage(messageName, deserializer);
    }

    public static void sendCrossMessageToAllOtherServer(BaseMessage message) {
        if(!CrossServerCore.getMessageHandler().isMessageRegistered(message.getMessageName())){
            CrossServerCore.LOGGER.warn("Trying to send a message via API that is not registered (message id : {})", message.getMessageName());
            return;
        }
        CrossServerCore.getServerCluster().sendMessageExceptCurrent(message.serializeToString());
    }

    public static void sendCrossMessageToServer(BaseMessage message, String serverName) {
        if(!CrossServerCore.getMessageHandler().isMessageRegistered(message.getMessageName())){
            CrossServerCore.LOGGER.warn("Trying to send a message via API that is not registered (message id : {})", message.getMessageName());
            return;
        }
        CrossServerCore.getServerCluster().getServer(serverName).ifPresent(server -> server.sendMessage(message.serializeToString()));
    }

    public static String getServerName() {
        return CrossServerCore.getServerName();
    }

    public static Map<String, SyncServer> getPlayerLocationMap(){
        Map<String, ? extends ISyncServer> newMap = CrossServerCore.getPlayersLocation().getPlayerServerMap();
        Map<String, SyncServer> oldMap = (Map<String, SyncServer>) newMap;
        return Map.copyOf(oldMap);
    }

    public static Optional<SyncServer> findPlayer(String playerName) {
        return (Optional<SyncServer>) CrossServerCore.getServerCluster().findPlayer(playerName);
    }

    public static MongoCollection<Document> getOrCreateMongoCollection(String collectionName) {
        return CrossServerCore.getMongodbConnection().getCollection(collectionName);
    }

    public static SecurityWatcher getSynchronizationSecurityWatcher() {
        return CrossServerCore.getSynchronizationSecurityWatcher();
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