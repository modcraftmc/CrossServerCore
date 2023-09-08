package fr.modcraftmc.crossservercoreproxyextension;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;

public class EventRegister {
    private CrossServerCoreProxy plugin;

    public EventRegister(CrossServerCoreProxy plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {

    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {

    }

    @Subscribe
    public void onPlayerJoin(ServerConnectedEvent event){

    }

    @Subscribe
    public void onProxyReload(ProxyReloadEvent event){
        plugin.loadConfig();
    }
}
