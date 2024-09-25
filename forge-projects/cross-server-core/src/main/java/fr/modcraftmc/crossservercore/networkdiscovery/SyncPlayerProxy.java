package fr.modcraftmc.crossservercore.networkdiscovery;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayerProxy;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public class SyncPlayerProxy implements ISyncPlayerProxy {
    UUID proxyUUID = null;
    String proxyName = null;


    public SyncPlayerProxy(ServerPlayer player) {
        proxyUUID = player.getUUID();
        proxyName = player.getName().getString();
    }

    public SyncPlayerProxy(UUID uuid,String name) {
        proxyUUID = uuid;
        proxyName = name;
    }

    public SyncPlayerProxy(ISyncPlayer player) {
        proxyUUID = player.getUUID();
        proxyName = player.getName();
    }

    @Override
    public UUID getUUID() {
        return proxyUUID;
    }

    @Override
    public String getName() {
        return proxyName;
    }

    @Override
    public ISyncServer getServer() {
        Optional<? extends ISyncPlayer> optionalISyncPlayer = unproxy();
        if (optionalISyncPlayer.isPresent()) {
            return optionalISyncPlayer.get().getServer();
        }

        throw new RuntimeException("Trying to get server of a player that was not found in the server cluster");
    }

    @Override
    public Optional<? extends ISyncPlayer> unproxy() {
        return CrossServerCore.getServerCluster().getPlayer(proxyUUID);
    }

    @Override
    public ISyncPlayerProxy proxy() {
        return new SyncPlayerProxy(proxyUUID, proxyName);
    }

    @Override
    public int hashCode() {
        return proxyUUID.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ISyncPlayer syncPlayer) {
            return syncPlayer.getUUID().equals(proxyUUID);
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", proxyName, proxyUUID);
    }
}
