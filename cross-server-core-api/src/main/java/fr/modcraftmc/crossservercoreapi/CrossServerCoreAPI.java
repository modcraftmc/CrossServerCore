package fr.modcraftmc.crossservercoreapi;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import fr.modcraftmc.crossservercoreapi.dataintegrity.ISecurityWatcher;
import fr.modcraftmc.crossservercoreapi.message.BaseMessage;
import fr.modcraftmc.crossservercoreapi.message.IMessageHandler;
import fr.modcraftmc.crossservercoreapi.networkdiscovery.IPlayersLocation;
import fr.modcraftmc.crossservercoreapi.networkdiscovery.IServerCluster;
import fr.modcraftmc.crossservercoreapi.networkdiscovery.ISyncServer;
import org.bson.Document;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CrossServerCoreAPI {
    private static CrossServerCoreAPI instance;
    private static boolean isCrossServerCoreLoaded = false;
    private static List<Runnable> runWhenCSCIsReady = new ArrayList<>();
    private final Logger logger;
    private final String serverName;
    private final IServerCluster serverCluster;
    private final IPlayersLocation playersLocation;
    private final IMessageHandler messageHandler;
    private final Function<String, MongoCollection<Document>> mongodbCollectionProvider;
    private final ISecurityWatcher securityWatcher;

    public CrossServerCoreAPI(Logger logger, String serverName, IServerCluster serverCluster, IPlayersLocation playersLocation, IMessageHandler messageHandler, Function<String, MongoCollection<Document>> mongodbCollectionProvider, ISecurityWatcher securityWatcher) {
        this.logger = logger;
        this.serverName = serverName;
        this.serverCluster = serverCluster;
        this.playersLocation = playersLocation;
        this.messageHandler = messageHandler;
        this.mongodbCollectionProvider = mongodbCollectionProvider;
        this.securityWatcher = securityWatcher;
        instance = this;

        logger.info("Initializing fr.modcraftmc.crossservercoreapi.CrossServerCoreAPI");
        isCrossServerCoreLoaded = true;
        runWhenCSCIsReady.forEach(Runnable::run);
    }

    public void registerCrossMessage(String messageName, Function<JsonObject, ? extends BaseMessage> deserializer) {
        if(messageHandler.isMessageRegistered(messageName)){
            logger.warn("Trying to register a fr.modcraftmc.crossservercoreapi.message via API that is already registered (fr.modcraftmc.crossservercoreapi.message id : {})", messageName);
            return;
        }
        messageHandler.registerCrossMessage(messageName, deserializer);
    }

    public void sendCrossMessageToAllOtherServer(BaseMessage message) {
        if(!messageHandler.isMessageRegistered(message.getMessageName())){
            logger.warn("Trying to send a fr.modcraftmc.crossservercoreapi.message via API that is not registered (fr.modcraftmc.crossservercoreapi.message id : {})", message.getMessageName());
            return;
        }
        serverCluster.sendMessageExceptCurrent(message.serializeToString());
    }

    public void sendCrossMessageToServer(BaseMessage message, String serverName) {
        if(!messageHandler.isMessageRegistered(message.getMessageName())){
            logger.warn("Trying to send a fr.modcraftmc.crossservercoreapi.message via API that is not registered (fr.modcraftmc.crossservercoreapi.message id : {})", message.getMessageName());
            return;
        }
        serverCluster.getServer(serverName).ifPresent(server -> server.sendMessage(message.serializeToString()));
    }

    public String getServerName() {
        return serverName;
    }

    public Map<String, ISyncServer> getPlayerLocationMap(){
        return Map.copyOf(playersLocation.getPlayerServerMap());
    }

    public Optional<? extends ISyncServer> findPlayer(String playerName) {
        return serverCluster.findPlayer(playerName);
    }

    public MongoCollection<Document> getOrCreateMongoCollection(String collectionName) {
        return mongodbCollectionProvider.apply(collectionName);
    }

    public ISecurityWatcher getSynchronizationSecurityWatcher() {
        return securityWatcher;
    }

    public void registerOnPlayerJoinedCluster(BiConsumer<String, ISyncServer> runnable) {
        playersLocation.registerOnPlayerJoinedClusterEvent(runnable);
    }

    public void registerOnPlayerLeftCluster(BiConsumer<String, ISyncServer> runnable) {
        playersLocation.registerOnPlayerLeavedClusterEvent(runnable);
    }

    public static void runWhenCSCIsReady(Runnable runnable) {
        if(isCrossServerCoreLoaded)
            runnable.run();
        else
            runWhenCSCIsReady.add(runnable);
    }
}
