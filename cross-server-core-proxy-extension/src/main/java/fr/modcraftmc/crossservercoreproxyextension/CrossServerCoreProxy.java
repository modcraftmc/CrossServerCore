package fr.modcraftmc.crossservercoreproxyextension;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.modcraftmc.crossservercoreproxyextension.message.BaseMessage;
import fr.modcraftmc.crossservercoreproxyextension.message.MessageHandler;
import fr.modcraftmc.crossservercoreproxyextension.rabbitmq.*;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Plugin(id = "crossservercoreproxy", name = "CrossServerCoreProxy", version = "0.1.0",
        description = "amplify capabilities of CrossServerCore", authors = {"ModCraftMC"}, url = "https://modcraftmc.fr")
public class CrossServerCoreProxy {
    public static CrossServerCoreProxy instance;
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private RabbitmqConnection rabbitmqConnection;
    public List<Runnable> onConfigLoad = new ArrayList<>();

    @Inject
    public CrossServerCoreProxy(ProxyServer server, @DataDirectory Path dataDirectory, Logger logger) {
        instance = this;
        this.server = server;
        this.dataDirectory = dataDirectory;
        this.logger = logger;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        logger.info("CrossServerCoreProxy initializing !");
        MessageHandler.init();
        loadConfig();

        server.getEventManager().register(this, new EventRegister(this));
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        logger.info("DataSync shutting down !");
        rabbitmqConnection.close();
    }

    private Toml readConfig() {
        File configFile = new File(dataDirectory.toFile(), "config.toml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            logger.info("Config file not found, creating one !");
            try {
                configFile.createNewFile();
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(configFile));
                writer.write(defaultFileContent());
                writer.close();
            } catch (Exception e) {
                logger.error("Error while creating config file : %s".formatted(e.getMessage()));
            }
        }
        return new Toml().read(configFile);
    }

    private String defaultFileContent(){
        return """
                [rabbitmq]
                host = "localhost"
                port = 5672
                username = "guest"
                password = "guest"
                vhost = "/"
                """;
    }

    public void loadConfig(){
        logger.info("Loading config file");
        if(rabbitmqConnection != null){
            rabbitmqConnection.close();
        }

        Toml config = readConfig();
        RabbitmqConfigData rabbitmqConfigData;
        try {
            rabbitmqConfigData = config.getTable("rabbitmq").to(RabbitmqConfigData.class);
        } catch (Exception e) {
            logger.error("Error while reading config file : %s".formatted(e.getMessage()));
            throw new RuntimeException(e);
        }

        if(this.rabbitmqConnection != null) this.rabbitmqConnection.close();

        try {
            rabbitmqConnection = new RabbitmqConnectionBuilder()
                    .host(rabbitmqConfigData.host)
                    .port(rabbitmqConfigData.port)
                    .username(rabbitmqConfigData.username)
                    .password(rabbitmqConfigData.password)
                    .virtualHost(rabbitmqConfigData.vhost)
                    .build();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
        new RabbitmqDirectPublisher(rabbitmqConnection);
        new RabbitmqDirectSubscriber(rabbitmqConnection);
        new RabbitmqPublisher(rabbitmqConnection);
        new RabbitmqSubscriber(rabbitmqConnection);
        logger.info("Connected to RabbitMQ");

        onConfigLoad.forEach(Runnable::run);
    }

    public void sendMessageToServer(BaseMessage message, String serverName){
        try {
            RabbitmqDirectPublisher.instance.publish(serverName, message.serializeToString());
        } catch (IOException e) {
            logger.error("Error while sending message to server %s : %s".formatted(serverName, e.getMessage()));
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getProxyServer() {
        return server;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    private class RabbitmqConfigData {
        private String host;
        private int port;
        private String username;
        private String password;
        private String vhost;
    }
}
