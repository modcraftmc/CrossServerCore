package fr.modcraftmc.crossservercore.message;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.annotation.AutoSerialize;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncServer;

@AutoRegister("Detach")
public class DetachServer extends BaseMessage {

    @AutoSerialize
    public SyncServer server;

    DetachServer() {}

    public DetachServer(SyncServer server) {
        this.server = server;
    }

    @Override
    public void handle() {
        CrossServerCore.getServerCluster().removeServer(server);
        CrossServerCore.LOGGER.debug(String.format("Server %s have been detached from the network", server.getName()));
    }
}
