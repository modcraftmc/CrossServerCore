package fr.modcraftmc.crossservercore.message;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.annotation.AutoSerialize;
import fr.modcraftmc.crossservercore.api.events.SyncServerAttachEvent;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncServer;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import net.minecraftforge.common.MinecraftForge;

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
        SyncServer syncServer = new SyncServer(serverName);
        CrossServerCore.getServerCluster().addServer(syncServer);
        MinecraftForge.EVENT_BUS.post(new SyncServerAttachEvent(syncServer, SyncServerAttachEvent.AttachType.NEW));
        CrossServerCore.LOGGER.debug(String.format("Received attach request from %s and have been attached to the network", serverName));
        syncServer.sendMessage(new AttachServerResponse(CrossServerCore.getServerName(), CrossServerCore.getServer().internalGetPlayers()));
    }
}
