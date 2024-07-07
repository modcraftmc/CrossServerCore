package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;

import java.lang.reflect.Type;
import java.util.UUID;

public class ISyncPlayerSerializer extends FieldSerializer<ISyncPlayer> {

    @Override
    public JsonElement serialize(ISyncPlayer value) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("playerName", new JsonPrimitive(value.getName()));
        jsonObject.add("playerUUID", new JsonPrimitive(value.getUUID().toString()));
        return jsonObject;
    }

    @Override
    public ISyncPlayer deserialize(JsonElement json, Type type) {
        JsonObject jsonObject = json.getAsJsonObject();
        String playerName = jsonObject.get("playerName").getAsString();
        UUID playerUUID = UUID.fromString(jsonObject.get("playerUUID").getAsString());

        return CrossServerCore.getServerCluster().getOrCreatePlayer(playerUUID, playerName);
    }

    @Override
    public Type getType() {
        return ISyncPlayer.class;
    }
}
