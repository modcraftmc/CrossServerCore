package fr.modcraftmc.crossservercore;

import com.mojang.logging.LogUtils;
import fr.modcraftmc.crossservercore.api.CrossServerCoreAPI;
import fr.modcraftmc.crossservercore.api.events.CrossServerCoreReadyEvent;
import fr.modcraftmc.crossservercore.dataintegrity.SecurityWatcher;
import fr.modcraftmc.crossservercore.message.MessageHandler;
import fr.modcraftmc.crossservercore.message.PlayerJoined;
import fr.modcraftmc.crossservercore.message.PlayerLeaved;
import fr.modcraftmc.crossservercore.message.ProxyExtensionHandshake;
import fr.modcraftmc.crossservercore.message.autoserializer.MessageAutoPropertySerializer;
import fr.modcraftmc.crossservercore.mongodb.MongodbConnection;
import fr.modcraftmc.crossservercore.mongodb.MongodbConnectionBuilder;
import fr.modcraftmc.crossservercore.networkdiscovery.ServerCluster;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncPlayer;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncServer;
import fr.modcraftmc.crossservercore.rabbitmq.*;
import fr.modcraftmc.crossservercore.api.CrossServerCoreProxyExtensionAPI;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerNegotiationEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.LogManager;

@Mod("crossservercore")
public class CrossServerCore {
    public static final String MOD_ID = "crossservercore";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final ServerCluster serverCluster = new ServerCluster();
    private static final MessageHandler messageHandler = new MessageHandler();
    private static final MessageAutoPropertySerializer messageAutoPropertySerializer = new MessageAutoPropertySerializer();
    private static final SecurityWatcher SynchronizationSecurityWatcher = new SecurityWatcher("synchronization security watcher");;

    private static String serverName;
    private static SyncServer syncServer;

    private static MongodbConnection mongodbConnection;
    private static RabbitmqConnection rabbitmqConnection;

    private static final CrossServerCoreProxyExtension crossServerCoreProxyExtension = new CrossServerCoreProxyExtension();

    public CrossServerCore() {
        CrossServerCore.LOGGER.info("CrossServerCore is here !");
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::serverSetup);

        MinecraftForge.EVENT_BUS.addListener(this::onServerStop);
        MinecraftForge.EVENT_BUS.addListener(CrossServerCore::onPreLogin);
        MinecraftForge.EVENT_BUS.addListener(CrossServerCore::onPlayerJoin);
        MinecraftForge.EVENT_BUS.addListener(CrossServerCore::onPlayerLeave);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
    }

    @SubscribeEvent
    public void serverSetup(FMLDedicatedServerSetupEvent event){

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

        loadConfig();
    }

    @SubscribeEvent
    public void serverStarted(ServerStartedEvent event) {
        CrossServerCore.LOGGER.debug("Initializing main modules");
        initializeDatabaseConnection();
        initializeMessageSystem();
        messageAutoPropertySerializer.init();
        messageHandler.init();
        initializeNetworkDiscovery();
        CrossServerCore.LOGGER.info("Main modules initialized");
        initAPIs();

        CrossServerCore.LOGGER.info("Checking for CrossServerCoreProxyExtension...");
        sendProxyMessage(new ProxyExtensionHandshake(serverName));

        MinecraftForge.EVENT_BUS.post(new CrossServerCoreReadyEvent());
    }

    private void initializeDatabaseConnection(){
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
        CrossServerCore.LOGGER.info("Connected to MongoDB");
    }

    private void initializeMessageSystem(){
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

            CrossServerCore.LOGGER.info("Connected to RabbitMQ");
            CrossServerCore.LOGGER.debug("Initializing message streams");
            new RabbitmqPublisher(rabbitmqConnection);
            new RabbitmqSubscriber(rabbitmqConnection);
            new RabbitmqDirectPublisher(rabbitmqConnection);
            new RabbitmqDirectSubscriber(rabbitmqConnection);
            CrossServerCore.LOGGER.debug("Message streams initialized");
        } catch (IOException | TimeoutException e) {
            CrossServerCore.LOGGER.error("Error while connecting to RabbitMQ : %s".formatted(e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    public static void loadConfig(){
        ConfigManager.loadConfigFile();
        serverName = ConfigManager.serverName;
    }

    private void initializeNetworkDiscovery() {
        CrossServerCore.LOGGER.debug("Initializing network identity");
        syncServer = serverCluster.attach();
        CrossServerCore.LOGGER.info("Network identity initialized");
    }

    public void initAPIs() {
        CrossServerCoreAPI.initAPI(syncServer, serverCluster, messageHandler, messageAutoPropertySerializer, mongodbConnection::getCollection, SynchronizationSecurityWatcher);
        CrossServerCoreProxyExtensionAPI.initProxyExtensionAPI(crossServerCoreProxyExtension);
    }

    public void onServerStop(ServerStoppingEvent event){
        syncServer = null;
        serverCluster.detach();
    }

    public static void onPreLogin(PlayerNegotiationEvent event){
        if(!SynchronizationSecurityWatcher.isSecure()){
            event.getConnection().disconnect(Component.literal("CrossServerCore is not secure, you cannot join the server. Reason(s) : \n" + SynchronizationSecurityWatcher.getReason()));
        }
    }

    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        serverCluster.sendMessage(new PlayerJoined(event.getEntity().getUUID(), event.getEntity().getName().getString(), CrossServerCore.syncServer));
    }

    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event){
        serverCluster.internalGetPlayer(event.getEntity().getUUID()).ifPresent(player -> {
            serverCluster.sendMessage(new PlayerLeaved(player));
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
        try {
            RabbitmqDirectPublisher.instance.publish("proxy", message.serialize().toString());
        } catch (IOException e) {
            CrossServerCore.LOGGER.error("Failed to send proxy message {} : {}", message.getMessageName(), e.getMessage());
        }
    }

    public static ServerCluster getServerCluster(){
        return serverCluster;
    }

    public static String getServerName() {
        return serverName;
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
}
