package fr.modcraftmc.crossservercore.networkdiscovery;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;

import java.util.UUID;

public class SyncPlayer implements ISyncPlayer {
    private UUID uuid;
    private String name;
    private SyncServer syncServer;
    private boolean valid = true;

    public SyncPlayer(UUID uuid, String name, SyncServer syncServer) {
        this.uuid = uuid;
        this.name = name;
        this.syncServer = syncServer;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    @Override
    public ISyncServer getServer() {
        if(!valid)
            throw new RuntimeException("Trying to get server of an invalid SyncPlayer");

        return syncServer;
    }

    public void setServer(SyncServer syncServer) {
        if(this.syncServer != null)
            this.syncServer.removePlayer(this);

        if(syncServer != null)
            syncServer.addPlayer(this);

        this.syncServer = syncServer;
    }

    public void invalidate() {
        valid = false;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SyncPlayer syncPlayer) {
            return syncPlayer.getUUID().equals(uuid);
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, uuid);
    }
}
