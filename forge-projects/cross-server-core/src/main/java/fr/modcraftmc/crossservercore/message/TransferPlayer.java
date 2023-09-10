package fr.modcraftmc.crossservercore.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercoreapi.message.BaseMessage;

public class TransferPlayer extends BaseMessage {
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
    public void handle() {
        //handle is on proxy side
    }
}
