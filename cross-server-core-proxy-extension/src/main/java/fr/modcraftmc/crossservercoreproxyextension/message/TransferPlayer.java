package fr.modcraftmc.crossservercoreproxyextension.message;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.modcraftmc.crossservercoreproxyextension.CrossServerCoreProxy;
import net.kyori.adventure.text.Component;

public class TransferPlayer extends BaseMessage{
    public static final String MESSAGE_NAME = "transfer_player";

    public String playerName;
    public String serverName;

    public TransferPlayer(String playerName, String serverName) {
        super(MESSAGE_NAME);
        this.playerName = playerName;
        this.serverName = serverName;
    }

    @Override
    protected JsonObject serialize() {
        JsonObject jsonObject = super.serialize();
        jsonObject.addProperty("serverName", serverName);
        jsonObject.addProperty("playerName", playerName);
        return jsonObject;
    }

    @Override
    public String getMessageName() {
        return MESSAGE_NAME;
    }

    public static TransferPlayer deserialize(JsonObject json) {
        String serverName = json.get("serverName").getAsString();
        String playerName = json.get("playerName").getAsString();
        return new TransferPlayer(playerName, serverName);
    }

    @Override
    protected void handle() {
        ProxyServer proxy = CrossServerCoreProxy.instance.getProxyServer();
        proxy.getPlayer(playerName).ifPresent( player -> {
            proxy.getServer(serverName).ifPresent( server -> {
                player.createConnectionRequest(server).connect().thenAccept((result) -> {
                    CrossServerCoreProxy.instance.sendMessageToServer(new TransferPlayerResponse(serverName, playerName, result.isSuccessful(), result.getReasonComponent().orElse(Component.empty())), serverName);
                });
            });
        });
    }
}
