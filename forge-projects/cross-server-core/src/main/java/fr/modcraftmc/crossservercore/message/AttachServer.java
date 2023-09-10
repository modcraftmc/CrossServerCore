package fr.modcraftmc.crossservercore.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncServer;
import fr.modcraftmc.crossservercoreapi.message.BaseMessage;

public class AttachServer extends BaseMessage {
    public static final String MESSAGE_NAME = "AttachServer";
    public final String serverName;

    public AttachServer(String serverName) {
        super(MESSAGE_NAME);
        this.serverName = serverName;
    }

    @Override
    protected JsonObject serialize() {
        JsonObject jsonObject = super.serialize();
        jsonObject.addProperty("serverName", serverName);
        return jsonObject;
    }

    public static AttachServer deserialize(JsonObject json) {
        String serverName = json.get("serverName").getAsString();
        return new AttachServer(serverName);
    }

    @Override
    public void handle() {
        if(serverName.equals(CrossServerCore.getServerName())) return; // this fr.modcraftmc.crossservercoreapi.message is send over all servers, we don't want to add ourself to the cluster
        CrossServerCore.getServerCluster().addServer(new SyncServer(serverName));
        CrossServerCore.LOGGER.debug(String.format("Received attach request from %s and have been attached to the network", serverName));
        CrossServerCore.getServerCluster().getServer(serverName).get().sendMessage(new AttachServerResponse(CrossServerCore.getServerName(), CrossServerCore.getServerCluster().getServer(CrossServerCore.getServerName()).get().getPlayers()).serializeToString());
    }

    @Override
    public String getMessageName() {
        return MESSAGE_NAME;
    }
}
