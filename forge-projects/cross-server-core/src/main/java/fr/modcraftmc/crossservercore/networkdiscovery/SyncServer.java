package fr.modcraftmc.crossservercore.networkdiscovery;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.rabbitmq.RabbitmqDirectPublisher;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyncServer implements ISyncServer {
    private final String serverName;
    private final List<String> players;

    public SyncServer(String serverName) {
        this.serverName = serverName;
        players = new ArrayList<>();
    }

    @Override
    public void sendMessage(String message) {
        try {
            RabbitmqDirectPublisher.instance.publish(serverName, message);
        } catch (IOException e) {
            CrossServerCore.LOGGER.error(String.format("Error while publishing message to rabbitmq cannot send message to server %s : %s", serverName, e.getMessage()));
        }
    }

    public void addPlayer(String playerName){
        if(!players.contains(playerName))
            players.add(playerName);
    }

    public void removePlayer(String playerName){
        players.remove(playerName);
    }

    public String getName() {
        return serverName;
    }

    public List<String> getPlayers() {
        return players;
    }
}
