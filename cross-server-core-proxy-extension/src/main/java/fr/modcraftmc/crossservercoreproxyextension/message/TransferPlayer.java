package fr.modcraftmc.crossservercoreproxyextension.message;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.modcraftmc.crossservercoreproxyextension.CrossServerCoreProxy;
import fr.modcraftmc.crossservercoreproxyextension.annotation.AutoRegister;
import fr.modcraftmc.crossservercoreproxyextension.annotation.AutoSerialize;

import java.util.UUID;

@AutoRegister("transfer_player")
public class TransferPlayer extends BaseMessage{

    @AutoSerialize
    public UUID playerUUID;
    @AutoSerialize
    public String serverName;

    public TransferPlayer() {}

    public TransferPlayer(UUID playerUUID, String serverName) {
        this.playerUUID = playerUUID;
        this.serverName = serverName;
    }

    @Override
    public void handle() {
        ProxyServer proxy = CrossServerCoreProxy.instance.getProxyServer();
        proxy.getPlayer(playerUUID).ifPresent( player -> {
            proxy.getServer(serverName).ifPresent( server -> {
                player.createConnectionRequest(server).connect();
            });
        });
    }
}
