package fr.modcraftmc.crossservercore.api.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

public class SendMessage extends BaseMessage {
    public static String MESSAGE_NAME = "send_message";

    public Component message;
    public String playerName;

    public SendMessage(Component message, String playerName) {
        super(MESSAGE_NAME);
        this.message = message;
        this.playerName = playerName;
    }

    public JsonObject serialize() {
        JsonObject json = super.serialize();
        json.addProperty("message", Component.Serializer.toJson(message));
        json.addProperty("playerName", playerName);
        return json;
    }

    @Override
    public String getMessageName() {
        return MESSAGE_NAME;
    }

    public static SendMessage deserialize(JsonObject json) {
        Component message = Component.Serializer.fromJson(json.get("message").getAsString());
        String playerName = json.get("playerName").getAsString();
        return new SendMessage(message, playerName);
    }

    public void send(){
        CrossServerCore.getPlayersLocation().getPlayerLocation(playerName).ifPresent(server -> {
            server.sendMessage(this.serializeToString());
        });
    }

    @Override
    public void handle() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerPlayer player = server.getPlayerList().getPlayerByName(playerName);
        if (player != null) {
            player.sendSystemMessage(message, false);
        }
    }
}
