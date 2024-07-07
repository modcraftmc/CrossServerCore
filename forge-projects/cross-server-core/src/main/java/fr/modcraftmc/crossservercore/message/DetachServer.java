package fr.modcraftmc.crossservercore.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.annotation.AutoSerialize;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;

@AutoRegister("Detach")
public class DetachServer extends BaseMessage {

    @AutoSerialize
    public String serverName;

    DetachServer() {}

    public DetachServer(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void handle() {
        CrossServerCore.getServerCluster().removeServer(serverName);
        CrossServerCore.LOGGER.debug(String.format("Server %s have been detached from the network", serverName));
    }
}
