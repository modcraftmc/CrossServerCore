package fr.modcraftmc.crossservercore.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;

public class PlayerJoined extends BaseMessage{
    public static final String MESSAGE_NAME = "PlayerJoined";

    public final String playerName;
    public final String serverName;

    public PlayerJoined(String playerName, String serverName) {
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

    public static PlayerJoined deserialize(JsonObject json) {
        String serverName = json.get("serverName").getAsString();
        String playerName = json.get("playerName").getAsString();
        return new PlayerJoined(playerName, serverName);
    }

    @Override
    protected void handle() {
        CrossServerCore.getServerCluster().getServer(serverName).ifPresentOrElse(
        syncServer -> {
            CrossServerCore.LOGGER.debug(String.format("Player %s joined server %s", playerName, serverName));
            CrossServerCore.getPlayersLocation().setPlayerLocation(playerName, syncServer);
        },
        () -> {
            CrossServerCore.getPlayersLocation().removePlayer(playerName);
        });
    }

    @Override
    public String getMessageName() {
        return MESSAGE_NAME;
    }
}
