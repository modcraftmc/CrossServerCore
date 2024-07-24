package fr.modcraftmc.crossservercore.networkdiscovery;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServerProxy;

import java.util.List;
import java.util.Optional;

public class SyncServerProxy implements ISyncServerProxy {
    private String proxyServerName;

    public SyncServerProxy(String proxyServerName) {
        this.proxyServerName = proxyServerName;
    }

    @Override
    public void sendMessage(BaseMessage message) {
        Optional<SyncServer> optionalISyncServer = CrossServerCore.getServerCluster().getServer(proxyServerName);
        if (optionalISyncServer.isEmpty()) {
            throw new RuntimeException("Trying to send message to a server that was not found in the server cluster");
        }

        optionalISyncServer.get().sendMessage(message);
    }

    @Override
    public String getName() {
        return proxyServerName;
    }

    @Override
    public List<? extends ISyncPlayer> getPlayers() {
        Optional<SyncServer> optionalISyncServer = CrossServerCore.getServerCluster().getServer(proxyServerName);
        if (optionalISyncServer.isEmpty()) {
            throw new RuntimeException("Trying to send message to a server that was not found in the server cluster");
        }

        return optionalISyncServer.get().getPlayers();
    }

    @Override
    public Optional<? extends ISyncServer> unproxy() {
        return CrossServerCore.getServerCluster().getServer(proxyServerName);
    }

    @Override
    public ISyncServerProxy proxy() {
        return new SyncServerProxy(proxyServerName);
    }

    @Override
    public int hashCode() {
        return proxyServerName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SyncServer syncServer) {
            return syncServer.getName().equals(proxyServerName);
        }

        return false;
    }

    @Override
    public String toString() {
        return proxyServerName;
    }
}
