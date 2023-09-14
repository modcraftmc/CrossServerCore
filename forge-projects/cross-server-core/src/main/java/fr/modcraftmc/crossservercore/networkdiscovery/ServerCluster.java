package fr.modcraftmc.crossservercore.networkdiscovery;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.message.AttachServer;
import fr.modcraftmc.crossservercore.message.DetachServer;
import fr.modcraftmc.crossservercore.rabbitmq.RabbitmqPublisher;
import fr.modcraftmc.crossservercore.api.networkdiscovery.IServerCluster;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServerCluster implements IServerCluster {
    public List<SyncServer> servers;
    public PlayersLocation playersLocation;

    public ServerCluster() {
        this.servers = new ArrayList<>();
        this.playersLocation = new PlayersLocation();
    }

    public void addServer(SyncServer server){
        servers.add(server);
    }

    public Optional<SyncServer> getServer(String serverName){
        for (SyncServer server : servers) {
            if(server.getName().equals(serverName)) return Optional.of(server);
        }
        return Optional.empty();
    }

    public void attach(){
        addServer(new SyncServer(CrossServerCore.getServerName()));
        try {
            CrossServerCore.LOGGER.debug("Attaching to server cluster");
            RabbitmqPublisher.instance.publish(new AttachServer(CrossServerCore.getServerName()).serializeToString());
        } catch (IOException e) {
            CrossServerCore.LOGGER.error(String.format("Error while publishing message to rabbitmq cannot attach to server cluster : %s", e.getMessage()));
        }
    }

    public void detach(){
        removeServer(CrossServerCore.getServerName());
        try {
            CrossServerCore.LOGGER.debug("Detaching from server cluster");
            RabbitmqPublisher.instance.publish(new DetachServer(CrossServerCore.getServerName()).serializeToString());
        } catch (IOException e) {
            CrossServerCore.LOGGER.error(String.format("Error while publishing message to rabbitmq cannot detach from server cluster : %s", e.getMessage()));
        }
    }

    @Override
    public void sendMessage(String message) {
        for (SyncServer server : servers) {
            server.sendMessage(message);
        }
    }

    public void sendMessageExceptCurrent(String message) {
        for (SyncServer server : servers) {
            if(!server.getName().equals(CrossServerCore.getServerName()))
                server.sendMessage(message);
        }
    }

    public void removeServer(String serverName) {
        servers.removeIf(server -> server.getName().equals(serverName));
    }

    public List<String> getPlayers() {
        List<String> players = new ArrayList<>();
        for (SyncServer server : servers) {
            players.addAll(server.getPlayers());
        }
        return players;
    }

    public Optional<? extends ISyncServer> findPlayer(String player) {
        var playersLocations = CrossServerCore.getPlayersLocation().getPlayerServerMap();
        for (var entry : playersLocations.entrySet()){
            if (entry.getKey().equals(player)){
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }
}
