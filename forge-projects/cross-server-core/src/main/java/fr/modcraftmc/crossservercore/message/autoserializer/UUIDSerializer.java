package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;

import java.lang.reflect.Type;
import java.util.UUID;

public class UUIDSerializer extends FieldSerializer<UUID> {
    @Override
    public JsonElement serialize(UUID value) {
        return new JsonPrimitive(value.toString());
    }

    @Override
    public UUID deserialize(JsonElement json, Type typeOfT) {
        return UUID.fromString(json.getAsString());
    }

    @Override
    public Type getType() {
        return UUID.class;
    }
}
