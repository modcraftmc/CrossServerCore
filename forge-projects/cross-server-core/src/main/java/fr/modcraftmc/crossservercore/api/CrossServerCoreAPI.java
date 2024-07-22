package fr.modcraftmc.crossservercore.api;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mongodb.client.MongoCollection;
import fr.modcraftmc.crossservercore.api.dataintegrity.ISecurityWatcher;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.message.IMessageHandler;
import fr.modcraftmc.crossservercore.api.message.autoserializer.IMessageAutoPropertySerializer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.bson.Document;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public abstract class CrossServerCoreAPI {
    private static final Logger logger = LogUtils.getLogger();
    protected static boolean loaded = false;

    protected static CrossServerCoreAPI instance;
    protected ISyncServer server;
    protected IServerCluster serverCluster;
    protected IMessageHandler messageHandler;
    protected IMessageAutoPropertySerializer messageAutoPropertySerializer;
    protected ISecurityWatcher securityWatcher;

    public static boolean isLoaded() {
        return loaded;
    }

    public static void registerCrossMessage(String messageName, Function<JsonObject, ? extends BaseMessage> deserializer) {
        if(instance.messageHandler.isMessageRegistered(messageName)){
            logger.warn("Trying to register a message via API that is already registered (message id : {})", messageName);
            return;
        }
        instance.messageHandler.registerCrossMessage(messageName, deserializer);
    }

    public static void sendCrossMessageToAllOtherServer(BaseMessage message) {
        if(!instance.messageHandler.isMessageRegistered(message.getMessageName())){
            logger.warn("Trying to send a message via API that is not registered (message id : {})", message.getMessageName());
            return;
        }
        instance.serverCluster.sendMessageExceptCurrent(message);
    }

    public static void sendCrossMessageToServer(BaseMessage message, String serverName) {
        if(!instance.messageHandler.isMessageRegistered(message.getMessageName())){
            logger.warn("Trying to send a message via API that is not registered (message id : {})", message.getMessageName());
            return;
        }
        instance.serverCluster.getServer(serverName).ifPresent(server -> server.sendMessage(message));
    }

    public static String getServerName() {
        return instance.server.getName();
    }

    public static ISyncServer getServer() {
        return instance.server;
    }

    public static ISyncServerProxy getImmediateServer(String serverName) {
        return instance.serverCluster.getImmediateServer(serverName);
    }

    public static Optional<? extends ISyncServer> getServer(String serverName) {
        return instance.serverCluster.getServer(serverName);
    }

    public static ISyncPlayerProxy getImmediatePlayer(UUID playerUUID, String playerName) {
        return instance.serverCluster.getImmediatePlayer(playerUUID, playerName);
    }

    public static ISyncPlayerProxy getImmediatePlayer(Player player) {
        return instance.serverCluster.getImmediatePlayer(player);
    }

    public static Optional<? extends ISyncPlayer> getPlayer(String playerName) {
        return instance.serverCluster.getPlayer(playerName);
    }

    public static Optional<? extends ISyncPlayer> getPlayer(UUID playerUUID) {
        return instance.serverCluster.getPlayer(playerUUID);
    }

    public static List<? extends ISyncPlayer> getAllPlayersOnCluster() {
        return instance.serverCluster.getPlayers();
    }

    public static ISecurityWatcher getSynchronizationSecurityWatcher() {
        return instance.securityWatcher;
    }
    public static IServerCluster getServerCluster() {
        return instance.serverCluster;
    }

    public static IMessageAutoPropertySerializer getMessageAutoPropertySerializer() {
        return instance.messageAutoPropertySerializer;
    }
}
