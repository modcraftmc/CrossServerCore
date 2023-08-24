package fr.modcraftmc.crossservercore.mongodb;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MongodbConnectionBuilder {

    private String host;
    private int port;
    private String username;
    private String password;
    private String authsource;
    private String database;
    private Runnable onHeartbeatFailed;
    private Runnable onHeartbeatSucceeded;

    public MongodbConnectionBuilder() {
    }


    public MongodbConnectionBuilder host(String host) {
        this.host = host;
        return this;
    }

    public MongodbConnectionBuilder port(int port) {
        this.port = port;
        return this;
    }

    public MongodbConnectionBuilder username(String username) {
        this.username = username;
        return this;
    }

    public MongodbConnectionBuilder password(String password) {
        this.password = password;
        return this;
    }

    public MongodbConnectionBuilder authsource(String authsource) {
        this.authsource = authsource;
        return this;
    }

    public MongodbConnectionBuilder database(String database) {
        this.database = database;
        return this;
    }

    public MongodbConnectionBuilder onHeartbeatFailed(Runnable onHeartbeatFailed) {
        this.onHeartbeatFailed = onHeartbeatFailed;
        return this;
    }

    // Only triggered if heartbeat had failed before
    public MongodbConnectionBuilder onHeartbeatSucceeded(Runnable onHeartbeatSucceeded) {
        this.onHeartbeatSucceeded = onHeartbeatSucceeded;
        return this;
    }

    public MongodbConnection build() {
        MongoCredential credential = MongoCredential.createCredential(username, authsource, password.toCharArray());
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(List.of(new ServerAddress(host, port))))
                .applyToSocketSettings(builder -> builder.connectTimeout(0, TimeUnit.SECONDS))
                .applyToServerSettings(builder -> builder.addServerMonitorListener(new MongodbServerMonitorListener(onHeartbeatFailed, onHeartbeatSucceeded)))
                .credential(credential)
                .build();

        return new MongodbConnection(MongoClients.create(settings), database);
    }

}