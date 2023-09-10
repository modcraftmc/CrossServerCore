package fr.modcraftmc.crossservercore.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercoreapi.message.BaseMessage;

public class PlayerLeaved extends BaseMessage {
    public static final String MESSAGE_NAME = "PlayerLeaved";

    private final String playerName;
    private final String serverName;

    public PlayerLeaved(String playerName, String serverName) {
        super(MESSAGE_NAME);
        this.playerName = playerName;
        this.serverName = serverName;
    }

    @Override
    protected JsonObject serialize() {
        JsonObject jsonObject = super.serialize();
        jsonObject.addProperty("playerName", playerName);
        jsonObject.addProperty("serverName", serverName);
        return jsonObject;
    }

    public static PlayerLeaved deserialize(JsonObject json) {
        String playerName = json.get("playerName").getAsString();
        String serverName = json.get("serverName").getAsString();
        return new PlayerLeaved(playerName, serverName);
    }

    @Override
    public void handle() {
        CrossServerCore.LOGGER.debug(String.format("Player %s leaved server %s", playerName, serverName));
        CrossServerCore.getPlayersLocation().removePlayer(playerName);
    }

    @Override
    public String getMessageName() {
        return MESSAGE_NAME;
    }
}
