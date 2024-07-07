package fr.modcraftmc.crossservercore.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.annotation.AutoSerialize;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;

import java.util.UUID;

@AutoRegister("transfer_player")
public class TransferPlayer extends BaseMessage {

    @AutoSerialize
    public UUID playerUUID;
    @AutoSerialize
    public String serverName;

    public TransferPlayer() {}

    public TransferPlayer(ISyncPlayer player, ISyncServer server) {
        this.playerUUID = player.getUUID();
        this.serverName = server.getName();
    }

    @Override
    public void handle() {
        //handle is on proxy side
    }
}
