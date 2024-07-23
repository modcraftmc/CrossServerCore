package fr.modcraftmc.crossservercore;

import com.mojang.logging.LogUtils;
import fr.modcraftmc.crossservercore.api.events.CrossServerCoreReadyEvent;
import fr.modcraftmc.crossservercore.api.events.PlayerJoinClusterEvent;
import fr.modcraftmc.crossservercore.api.events.PlayerLeaveClusterEvent;
import fr.modcraftmc.crossservercore.dataintegrity.SecurityWatcher;
import fr.modcraftmc.crossservercore.events.MongodbConnectionReadyEvent;
import fr.modcraftmc.crossservercore.events.RabbitmqConnectionReadyEvent;
import fr.modcraftmc.crossservercore.message.MessageHandler;
import fr.modcraftmc.crossservercore.message.PlayerJoined;
import fr.modcraftmc.crossservercore.message.PlayerLeaved;
import fr.modcraftmc.crossservercore.message.ProxyExtensionHandshake;
import fr.modcraftmc.crossservercore.message.autoserializer.MessageAutoPropertySerializer;
import fr.modcraftmc.crossservercore.message.streams.MessageStreamsManager;
import fr.modcraftmc.crossservercore.mongodb.MongodbConnection;
import fr.modcraftmc.crossservercore.mongodb.MongodbConnectionBuilder;
import fr.modcraftmc.crossservercore.networkdiscovery.ServerCluster;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncPlayer;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncServer;
import fr.modcraftmc.crossservercore.rabbitmq.*;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerNegotiationEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Mod("crossservercore")
public class CrossServerCore {
    public static final String MOD_ID = "crossservercore";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static String serverName;
    private static SyncServer syncServer;

    private static final CrossServerCoreProxyExtension crossServerCoreProxyExtension = new CrossServerCoreProxyExtension();

    private static MongodbConnection mongodbConnection;
    private static RabbitmqConnection rabbitmqConnection;

    private static final ServerCluster serverCluster = new ServerCluster();
    private static final MessageHandler messageHandler = new MessageHandler();
    private static final MessageAutoPropertySerializer messageAutoPropertySerializer = new MessageAutoPropertySerializer();
    private static final SecurityWatcher SynchronizationSecurityWatcher = new SecurityWatcher("synchronization security watcher");;
    private static final MessageStreamsManager messageStreamsManager = new MessageStreamsManager();

    public CrossServerCore() {
        CrossServerCore.LOGGER.info("Cross Server Core is here !");
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::serverSetup);

        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST ,this::onServerStop);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST ,CrossServerCore::onPlayerJoin);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST ,CrossServerCore::onPlayerLeave);
        MinecraftForge.EVENT_BUS.addListener(CrossServerCore::onPreLogin);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
    }

    @SubscribeEvent
    public void serverSetup(FMLDedicatedServerSetupEvent event){
        loadConfig();
    }

    private static void initializeSynchronizationSecurityWatcher() {
        SynchronizationSecurityWatcher.registerOnInsecureEvent(() -> {
            kickAllPlayers("CrossServerCore is not secure, you cannot join the server. Reason(s) : \n" + SynchronizationSecurityWatcher.getReason());
            CrossServerCore.LOGGER.error("Synchronization security is not ensured, server is now inaccessible.");
            CrossServerCore.LOGGER.error("Reason(s) : \n" + SynchronizationSecurityWatcher.getReason());

            new Thread(() -> {
                try {
                    Thread.sleep(10000);
                    while(!SynchronizationSecurityWatcher.isSecure()){
                        CrossServerCore.LOGGER.error("Synchronization security is not ensured.");
                        CrossServerCore.LOGGER.error("Reason(s) : \n" + SynchronizationSecurityWatcher.getReason());

                        Thread.sleep(10000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
        SynchronizationSecurityWatcher.registerOnSecureEvent(() -> CrossServerCore.LOGGER.warn("Synchronization security is ensured again, server is now accessible"));
    }

    @SubscribeEvent
    public void serverStarted(ServerStartedEvent event) {
        CrossServerCore.LOGGER.debug("Initializing main modules");
        initializeMongodbConnection();
        initializeRabbitmqConnection();
        initializeMessageSystem();
        initializeNetworkDiscoverySystem();
        initializeSynchronizationSecurityWatcher();
        CrossServerCore.LOGGER.info("Main modules initialized");
        initializeAPIs();

        CrossServerCore.LOGGER.info("Checking for CrossServerCoreProxyExtension...");
        sendProxyMessage(new ProxyExtensionHandshake(serverName));

        MinecraftForge.EVENT_BUS.post(new CrossServerCoreReadyEvent());
    }

    public static void loadConfig(){
        ConfigManager.loadConfigFile();
        serverName = ConfigManager.serverName;
    }

    private void initializeMessageSystem() {
        messageAutoPropertySerializer.init();
        messageHandler.init();
    }

    private void initializeMongodbConnection(){
        CrossServerCore.LOGGER.debug("Connecting to MongoDB");
        ConfigManager.MongodbConfigData mongodbConfigData = ConfigManager.mongodbConfigData;

        if(mongodbConnection != null) mongodbConnection.close();
        mongodbConnection = new MongodbConnectionBuilder()
                .host(mongodbConfigData.host)
                .port(mongodbConfigData.port)
                .username(mongodbConfigData.username)
                .password(mongodbConfigData.password)
                .authsource(mongodbConfigData.database)
                .database(mongodbConfigData.database)
                .onHeartbeatFailed(() -> CrossServerCore.SynchronizationSecurityWatcher.addIssue(SecurityWatcher.MONGODB_CONNECTION_ISSUE))
                .onHeartbeatSucceeded(() -> CrossServerCore.SynchronizationSecurityWatcher.removeIssue(SecurityWatcher.MONGODB_CONNECTION_ISSUE))
                .build();

        MinecraftForge.EVENT_BUS.post(new MongodbConnectionReadyEvent(mongodbConnection));
        CrossServerCore.LOGGER.info("Connected to MongoDB");
    }

    private void initializeRabbitmqConnection(){
        CrossServerCore.LOGGER.debug("Connecting to RabbitMQ");
        ConfigManager.RabbitmqConfigData rabbitmqConfigData = ConfigManager.rabbitmqConfigData;
        if(rabbitmqConnection != null) rabbitmqConnection.close();

        try {
            rabbitmqConnection = new RabbitmqConnectionBuilder()
                    .host(rabbitmqConfigData.host)
                    .port(rabbitmqConfigData.port)
                    .username(rabbitmqConfigData.username)
                    .password(rabbitmqConfigData.password)
                    .virtualHost(rabbitmqConfigData.vhost)
                    .onHeartbeatFailed(() -> CrossServerCore.SynchronizationSecurityWatcher.addIssue(SecurityWatcher.RABBITMQ_CONNECTION_ISSUE))
                    .onHeartbeatSucceeded(() -> CrossServerCore.SynchronizationSecurityWatcher.removeIssue(SecurityWatcher.RABBITMQ_CONNECTION_ISSUE))
                    .build();

            MinecraftForge.EVENT_BUS.post(new RabbitmqConnectionReadyEvent(rabbitmqConnection));
            CrossServerCore.LOGGER.info("Connected to RabbitMQ");
        } catch (IOException | TimeoutException e) {
            CrossServerCore.LOGGER.error("Error while connecting to RabbitMQ : %s".formatted(e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    private void initializeNetworkDiscoverySystem() {
        CrossServerCore.LOGGER.debug("Initializing network identity");
        syncServer = serverCluster.attach();
        CrossServerCore.LOGGER.info("Network identity initialized");
    }

    public void initializeAPIs() {
        new CrossServerCoreAPIImpl(syncServer, serverCluster, messageHandler, messageAutoPropertySerializer, SynchronizationSecurityWatcher);
        new CrossServerCoreProxyExtensionAPIImpl(crossServerCoreProxyExtension);
    }

    public void onServerStop(ServerStoppingEvent event){
        serverCluster.detach();
        mongodbConnection.close();
        rabbitmqConnection.close();
        syncServer = null;
    }

    public static void onPreLogin(PlayerNegotiationEvent event){
        if(!SynchronizationSecurityWatcher.isSecure()){
            event.getConnection().disconnect(Component.literal("CrossServerCore is not secure, you cannot join the server. Reason(s) : \n" + SynchronizationSecurityWatcher.getReason()));
        }
    }

    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        SyncPlayer player = CrossServerCore.getServerCluster().setPlayerLocation(event.getEntity().getUUID(), event.getEntity().getName().getString(), CrossServerCore.syncServer);
        MinecraftForge.EVENT_BUS.post(new PlayerJoinClusterEvent(player, true));
        serverCluster.sendMessageExceptCurrent(new PlayerJoined(event.getEntity().getUUID(), event.getEntity().getName().getString(), CrossServerCore.syncServer));
    }

    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event){
        serverCluster.getPlayer(event.getEntity().getUUID()).ifPresent(player -> {
            serverCluster.sendMessageExceptCurrent(new PlayerLeaved(player));
            CrossServerCore.getServerCluster().removePlayer(player);
            MinecraftForge.EVENT_BUS.post(new PlayerLeaveClusterEvent(player, true));
        });
    }

    public static void kickAllPlayers(String reason){
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if(server == null) return;

        for(ServerPlayer player : server.getPlayerList().getPlayers()){
            player.connection.disconnect(Component.literal(reason));
        }
    }

    public static void sendProxyMessage(BaseMessage message){
        messageStreamsManager.sendDirectMessage("proxy", message.serialize().toString());
    }

    public static String getServerName() {
        return serverName;
    }

    public static SyncServer getServer() {
        return syncServer;
    }

    public static ServerCluster getServerCluster(){
        return serverCluster;
    }

    public static MongodbConnection getMongodbConnection() {
        return mongodbConnection;
    }

    public static RabbitmqConnection getRabbitmqConnection() {
        return rabbitmqConnection;
    }

    public static SecurityWatcher getSynchronizationSecurityWatcher() {
        return SynchronizationSecurityWatcher;
    }

    public static MessageHandler getMessageHandler(){
        return messageHandler;
    }

    public static MessageAutoPropertySerializer getMessageAutoPropertySerializer() {
        return messageAutoPropertySerializer;
    }

    public static CrossServerCoreProxyExtension getCrossServerCoreProxyExtension() {
        return crossServerCoreProxyExtension;
    }

    public static MessageStreamsManager getMessageStreamsManager() {
        return messageStreamsManager;
    }
}
