package fr.modcraftmc.crossservercore.message;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.annotation.AutoSerialize;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncPlayer;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncServer;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;

import java.util.List;

@AutoRegister("AttachServerResponse")
public class AttachServerResponse extends BaseMessage {
    @AutoSerialize
    public String serverName;
    @AutoSerialize
    public List<SyncPlayer> players;

    AttachServerResponse() { }

    AttachServerResponse(String serverName, List<SyncPlayer> players) {
        this.serverName = serverName;
        this.players = players;
    }

    @Override
    public void handle() {
        SyncServer syncServer = new SyncServer(serverName);
        CrossServerCore.getServerCluster().addServer(syncServer);
        for (SyncPlayer player : players) {
            player.setServer(syncServer);
        }
        CrossServerCore.LOGGER.debug("Server %s responded and have been attached to the network");
    }
}
