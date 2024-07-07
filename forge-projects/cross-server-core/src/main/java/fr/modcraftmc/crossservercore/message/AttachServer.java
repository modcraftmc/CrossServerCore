package fr.modcraftmc.crossservercore.message;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.annotation.AutoSerialize;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncServer;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;

@AutoRegister("AttachServer")
public class AttachServer extends BaseMessage {
    @AutoSerialize
    public String serverName;

    AttachServer() {}

    public AttachServer(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void handle() {
        if(serverName.equals(CrossServerCore.getServerName())) return; // this message is send over all servers, we don't want to add ourselves to the cluster
        CrossServerCore.getServerCluster().addServer(new SyncServer(serverName));
        CrossServerCore.LOGGER.debug(String.format("Received attach request from %s and have been attached to the network", serverName));
        CrossServerCore.getServerCluster().getServer(serverName).get().sendMessage(new AttachServerResponse(CrossServerCore.getServerName(), CrossServerCore.getServerCluster().getServer(CrossServerCore.getServerName()).get().internalGetPlayers()));
    }
}
