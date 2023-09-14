package fr.modcraftmc.crossservercore.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;

public class DetachServer extends BaseMessage {
    public static final String MESSAGE_NAME = "Detach";

    public final String serverName;


    public DetachServer(String serverName) {
        super(MESSAGE_NAME);
        this.serverName = serverName;
    }

    @Override
    protected JsonObject serialize() {
        JsonObject jsonObject = super.serialize();
        jsonObject.addProperty("serverName", serverName);
        return jsonObject;
    }

    public static DetachServer deserialize(JsonObject json) {
        String serverName = json.get("serverName").getAsString();
        return new DetachServer(serverName);
    }

    @Override
    public void handle() {
        CrossServerCore.getServerCluster().removeServer(serverName);
        CrossServerCore.LOGGER.debug(String.format("Server %s have been detached from the network", serverName));
    }

    @Override
    public String getMessageName() {
        return MESSAGE_NAME;
    }
}
