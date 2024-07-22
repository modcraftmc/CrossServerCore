package fr.modcraftmc.crossservercore.networkdiscovery;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.message.AttachServer;
import fr.modcraftmc.crossservercore.message.DetachServer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.IServerCluster;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

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

    public SyncServerProxy getImmediateServer(String serverName) {
        return new SyncServerProxy(serverName);
    }

    public SyncServer attach(){
        SyncServer syncServer = new SyncServer(CrossServerCore.getServerName());
        addServer(syncServer);
        CrossServerCore.LOGGER.debug("Attaching to server cluster");
        broadcastMessage(new AttachServer(CrossServerCore.getServerName()));
        return syncServer;
    }

    public void detach(){
        CrossServerCore.LOGGER.debug("Detaching from server cluster");
        SyncServer server = CrossServerCore.getServer();
        broadcastMessage(new DetachServer(server));
        removeServer(server);
    }

    public void broadcastMessage(BaseMessage message) {
        CrossServerCore.getMessageStreamsManager().sendBroadcastMessage(message.serialize().toString());
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

    public void removeServer(SyncServer server) {
        server.invalidate();
        servers.remove(server);
    }

    public Optional<SyncPlayer> getPlayer(UUID playerUUID) {
        for (SyncPlayer syncPlayer : players) {
            if(syncPlayer.getUUID().equals(playerUUID))
                return Optional.of(syncPlayer);
        }

        return Optional.empty();
    }

    public Optional<SyncPlayer> getPlayer(String playerName) {
        for (SyncPlayer syncPlayer : players) {
            if(syncPlayer.getName().equals(playerName))
                return Optional.of(syncPlayer);
        }

        return Optional.empty();
    }

    public Optional<SyncPlayer> getPlayer(ServerPlayer player) {
        return getPlayer(player.getUUID());
    }

    public SyncPlayerProxy getImmediatePlayer(UUID playerUUID, String playerName) {
        return new SyncPlayerProxy(playerUUID, playerName);
    }

    public SyncPlayerProxy getImmediatePlayer(Player player) {
        return new SyncPlayerProxy(player.getUUID(), player.getName().getString());
    }

    public SyncPlayer getOrCreatePlayer(UUID playerUUID, String playerName) {
        Optional<SyncPlayer> optionalSyncPlayer = getPlayer(playerUUID);

        if(!optionalSyncPlayer.isEmpty())
            return optionalSyncPlayer.get();

        SyncPlayer player = createPlayer(playerUUID, playerName, null);
        return player;
    }

    public SyncPlayer createPlayer(UUID playerUUID, String playerName, SyncServer location) {
        Optional<SyncPlayer> optionalSyncPlayer = getPlayer(playerUUID);
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
        player.invalidate();
        players.remove(player);
    }

    public Optional<SyncPlayer> removePlayer(String player) {
        Optional<SyncPlayer> syncPlayer = getPlayer(player);
        syncPlayer.ifPresent(this::removePlayer);

        return syncPlayer;
    }

    public List<SyncPlayer> getPlayers() {
        return players;
    }
}
