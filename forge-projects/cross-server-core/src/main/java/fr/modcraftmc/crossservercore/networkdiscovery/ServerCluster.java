package fr.modcraftmc.crossservercore.networkdiscovery;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.message.AttachServer;
import fr.modcraftmc.crossservercore.message.DetachServer;
import fr.modcraftmc.crossservercore.rabbitmq.RabbitmqPublisher;
import fr.modcraftmc.crossservercore.api.networkdiscovery.IServerCluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServerCluster implements IServerCluster {
    private final List<SyncPlayer> players;
    public final List<SyncServer> servers;

    public ServerCluster() {
        this.players = new ArrayList<>();
        this.servers = new ArrayList<>();
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

    public SyncServer attach(){
        SyncServer syncServer = new SyncServer(CrossServerCore.getServerName());
        addServer(syncServer);
        CrossServerCore.LOGGER.debug("Attaching to server cluster");
        broadcastMessage(new AttachServer(CrossServerCore.getServerName()));
        return syncServer;
    }

    public void detach(){
        removeServer(CrossServerCore.getServerName());
        CrossServerCore.LOGGER.debug("Detaching from server cluster");
        broadcastMessage(new DetachServer(CrossServerCore.getServerName()));
    }

    public void broadcastMessage(BaseMessage message) {
        try {
            RabbitmqPublisher.instance.publish(message.serialize().toString());
        } catch (IOException e) {
            CrossServerCore.LOGGER.error(String.format("Error while publishing message to rabbitmq cannot broadcast message : %s", e.getMessage()));
        }
    }

    @Override
    public void sendMessage(BaseMessage message) {
        for (SyncServer server : servers) {
            server.sendMessage(message);
        }
    }

    public void sendMessageExceptCurrent(BaseMessage message) {
        for (SyncServer server : servers) {
            if(!server.getName().equals(CrossServerCore.getServerName()))
                server.sendMessage(message);
        }
    }

    public void removeServer(String serverName) {
        servers.removeIf(server -> server.getName().equals(serverName));
    }

    public Optional<SyncPlayer> internalGetPlayer(UUID playerUUID) {
        for (SyncPlayer syncPlayer : players) {
            if(syncPlayer.getUUID().equals(playerUUID))
                return Optional.of(syncPlayer);
        }

        return Optional.empty();
    }

    public Optional<SyncPlayer> internalGetPlayer(String playerName) {
        for (SyncPlayer syncPlayer : players) {
            if(syncPlayer.getName().equals(playerName))
                return Optional.of(syncPlayer);
        }

        return Optional.empty();
    }

    public Optional<ISyncPlayer> getPlayer(String playerName) {
        Optional<SyncPlayer> player = internalGetPlayer(playerName);
        if (player.isPresent())
            return Optional.of(player.get());

        return Optional.empty();
    }

    public Optional<ISyncPlayer> getPlayer(UUID playerUUID) {
        Optional<SyncPlayer> player = internalGetPlayer(playerUUID);
        if (player.isPresent())
            return Optional.of(player.get());

        return Optional.empty();
    }

    public SyncPlayer getOrCreatePlayer(UUID playerUUID, String playerName) {
        Optional<SyncPlayer> optionalSyncPlayer = internalGetPlayer(playerUUID);

        if(!optionalSyncPlayer.isEmpty())
            return optionalSyncPlayer.get();

        SyncPlayer player = createPlayer(playerUUID, playerName, null);
        return player;
    }

    public SyncPlayer createPlayer(UUID playerUUID, String playerName, SyncServer location) {
        Optional<SyncPlayer> optionalSyncPlayer = internalGetPlayer(playerUUID);
        if (optionalSyncPlayer.isPresent()) {
            CrossServerCore.LOGGER.warn("Attempt to create a player that already exists in the cluster.");
            return optionalSyncPlayer.get();
        }

        SyncPlayer player = new SyncPlayer(playerUUID, playerName, location);
        players.add(player);
        return player;
    }

    public SyncPlayer setPlayerLocation(UUID uuid,String player, SyncServer location) {
        SyncPlayer syncPlayer = getOrCreatePlayer(uuid, player);
        syncPlayer.setServer(location);
        return syncPlayer;
    }

    public void removePlayer(SyncPlayer player) {
        player.setServer(null);
        players.remove(player);
    }

    public Optional<SyncPlayer> removePlayer(String player) {
        Optional<SyncPlayer> syncPlayer = internalGetPlayer(player);
        syncPlayer.ifPresent(this::removePlayer);

        return syncPlayer;
    }

    public List<? extends ISyncPlayer> getPlayers() {
        return players;
    }
}
