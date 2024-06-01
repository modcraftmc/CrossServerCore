package fr.modcraftmc.crossservercore.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.api.events.TransferPlayerResponseEvent;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;

public class TransferPlayerResponse extends BaseMessage {

    public static final String MESSAGE_NAME = "transfer_player_response";

    private final String serverName;
    private final String playerName;
    private final boolean isSuccessful;
    private final String transferError;

    public TransferPlayerResponse(String serverName, String playerName, boolean isSuccessful, Component transferError) {
        super(MESSAGE_NAME);
        this.serverName = serverName;
        this.playerName = playerName;
        this.isSuccessful = isSuccessful;
        this.transferError = transferError.toString();
    }

    @Override
    protected JsonObject serialize() {
        JsonObject jsonObject = super.serialize();
        jsonObject.addProperty("targetServer", serverName);
        jsonObject.addProperty("playerName", playerName);
        jsonObject.addProperty("isSuccessful", isSuccessful);
        jsonObject.addProperty("transfer_error", transferError);
        return jsonObject;
    }

    //handled on modded
    public static TransferPlayerResponse deserialize(JsonObject json) {
        String serverName = json.get("targetServer").getAsString();
        String playerName = json.get("targetServer").getAsString();
        boolean isSuccessful = json.get("isSuccessful").getAsBoolean();
        String transferStatus = json.get("transfer_status").getAsString();
        return new TransferPlayerResponse(serverName, playerName, isSuccessful, Component.literal(transferStatus));
    }

    @Override
    public String getMessageName() {
        return MESSAGE_NAME;
    }

    @Override
    public void handle() {
        MinecraftForge.EVENT_BUS.post(new TransferPlayerResponseEvent(this));
    }
}
