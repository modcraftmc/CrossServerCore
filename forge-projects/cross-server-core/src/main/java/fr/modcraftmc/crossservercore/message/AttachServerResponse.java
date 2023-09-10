package fr.modcraftmc.crossservercore.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncServer;
import fr.modcraftmc.crossservercoreapi.message.BaseMessage;

import java.util.ArrayList;
import java.util.List;

public class AttachServerResponse extends BaseMessage {
    public static final String MESSAGE_NAME = "AttachServerResponse";
    public final String serverName;
    public final List<String> players;

    AttachServerResponse(String serverName, List<String> players) {
        super(MESSAGE_NAME);
        this.serverName = serverName;
        this.players = players;
    }

    @Override
    protected JsonObject serialize() {
        JsonObject jsonObject = super.serialize();
        jsonObject.addProperty("serverName", serverName);
        JsonArray jsonArray = new JsonArray();
        for (String player : players) {
            jsonArray.add(player);
        }
        jsonObject.add("players", jsonArray);
        return jsonObject;
    }

    public static AttachServerResponse deserialize(JsonObject json) {
        String serverName = json.get("serverName").getAsString();
        JsonArray jsonArray = json.get("players").getAsJsonArray();
        List<String> players = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            players.add(jsonArray.get(i).getAsString());
        }
        return new AttachServerResponse(serverName, players);
    }

    @Override
    public void handle() {
        SyncServer syncServer = new SyncServer(serverName);
        CrossServerCore.getServerCluster().addServer(syncServer);
        for (String player : players) {
            CrossServerCore.getPlayersLocation().setPlayerLocation(player, syncServer);
        }
        CrossServerCore.LOGGER.debug("Server %s responded and have been attached to the network");
    }

    @Override
    public String getMessageName() {
        return MESSAGE_NAME;
    }
}
