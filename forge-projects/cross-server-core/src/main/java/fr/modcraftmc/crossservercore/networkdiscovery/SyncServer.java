package fr.modcraftmc.crossservercore.networkdiscovery;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.rabbitmq.RabbitmqDirectStream;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyncServer implements ISyncServer {
    private final String serverName;
    private final List<SyncPlayer> players;
    private boolean valid = true;

    public SyncServer(String serverName) {
        this.serverName = serverName;
        players = new ArrayList<>();
    }

    @Override
    public void sendMessage(BaseMessage message) {
        if (!valid)
            throw new RuntimeException("Trying to send message to an invalid SyncServer");

        CrossServerCore.getMessageStreamsManager().sendDirectMessage(serverName, message.serialize().toString());
    }

    public void addPlayer(SyncPlayer playerName){
        if(!players.contains(playerName))
            players.add(playerName);
    }

    public void removePlayer(ISyncPlayer playerName){
        players.remove(playerName);
    }

    public String getName() {
        return serverName;
    }

    public List<? extends ISyncPlayer> getPlayers() {
        if(!valid)
            throw new RuntimeException("Trying to get players of an invalid SyncServer");

        return players;
    }

    public void invalidate() {
        valid = false;
    }

    public List<SyncPlayer> internalGetPlayers() {
        return players;
    }

    @Override
    public int hashCode() {
        return serverName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SyncServer syncServer) {
            return syncServer.getName().equals(serverName);
        }

        return false;
    }

    @Override
    public String toString() {
        return serverName;
    }
}
