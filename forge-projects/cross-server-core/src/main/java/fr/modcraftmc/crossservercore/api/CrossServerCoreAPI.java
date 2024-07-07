package fr.modcraftmc.crossservercore.api;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mongodb.client.MongoCollection;
import fr.modcraftmc.crossservercore.api.dataintegrity.ISecurityWatcher;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.message.IMessageHandler;
import fr.modcraftmc.crossservercore.api.message.autoserializer.IMessageAutoPropertySerializer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.IServerCluster;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;
import org.bson.Document;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CrossServerCoreAPI {
    private static final Logger logger = LogUtils.getLogger();
    private static boolean loaded = false;
    private static ISyncServer server;
    private static IServerCluster serverCluster;
    private static IMessageHandler messageHandler;
    private static IMessageAutoPropertySerializer messageAutoPropertySerializer;
    private static Function<String, MongoCollection<Document>> mongodbCollectionProvider;
    private static ISecurityWatcher securityWatcher;

    public CrossServerCoreAPI() {
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static void initAPI(ISyncServer api_server, IServerCluster api_serverCluster, IMessageHandler api_messageHandler, IMessageAutoPropertySerializer api_messageAutoPropertySerializer, Function<String, MongoCollection<Document>> api_mongodbCollectionProvider, ISecurityWatcher api_securityWatcher) {
        logger.info("Initializing cross-server-core api");

        server = api_server;
        serverCluster = api_serverCluster;
        messageHandler = api_messageHandler;
        messageAutoPropertySerializer = api_messageAutoPropertySerializer;
        mongodbCollectionProvider = api_mongodbCollectionProvider;
        securityWatcher = api_securityWatcher;

        loaded = true;
    }

    public static void registerCrossMessage(String messageName, Function<JsonObject, ? extends BaseMessage> deserializer) {
        if(messageHandler.isMessageRegistered(messageName)){
            logger.warn("Trying to register a message via API that is already registered (message id : {})", messageName);
            return;
        }
        messageHandler.registerCrossMessage(messageName, deserializer);
    }

    public static void sendCrossMessageToAllOtherServer(BaseMessage message) {
        if(!messageHandler.isMessageRegistered(message.getMessageName())){
            logger.warn("Trying to send a message via API that is not registered (message id : {})", message.getMessageName());
            return;
        }
        serverCluster.sendMessageExceptCurrent(message);
    }

    public static void sendCrossMessageToServer(BaseMessage message, String serverName) {
        if(!messageHandler.isMessageRegistered(message.getMessageName())){
            logger.warn("Trying to send a message via API that is not registered (message id : {})", message.getMessageName());
            return;
        }
        serverCluster.getServer(serverName).ifPresent(server -> server.sendMessage(message));
    }

    public static String getServerName() {
        return server.getName();
    }

    public static ISyncServer getServer() {
        return server;
    }

    public static Optional<? extends ISyncPlayer> getPlayer(String playerName) {
        return serverCluster.getPlayer(playerName);
    }

    public static List<? extends ISyncPlayer> getAllPlayersOnCluster() {
        return serverCluster.getPlayers();
    }

    public static MongoCollection<Document> getOrCreateMongoCollection(String collectionName) {
        return mongodbCollectionProvider.apply(collectionName);
    }

    public static ISecurityWatcher getSynchronizationSecurityWatcher() {
        return securityWatcher;
    }

    public static IMessageAutoPropertySerializer getMessageAutoPropertySerializer() {
        return messageAutoPropertySerializer;
    }
}
