package fr.modcraftmc.crossservercore.networkdiscovery;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;

import java.util.UUID;

public class SyncPlayer implements ISyncPlayer {
    private UUID uuid;
    private String name;
    private SyncServer syncServer;

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
        return syncServer;
    }

    public void setServer(SyncServer syncServer) {
        if(this.syncServer != null)
            this.syncServer.removePlayer(this);

        if(syncServer != null)
            syncServer.addPlayer(this);

        this.syncServer = syncServer;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
