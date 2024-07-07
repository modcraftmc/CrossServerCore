package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;

import java.lang.reflect.Type;

public class BooleanSerializer extends FieldSerializer<Boolean> {
    @Override
    public JsonElement serialize(Boolean value) {
        return new JsonPrimitive(value);
    }

    @Override
    public Boolean deserialize(JsonElement json, Type typeOfT) {
        return json.getAsBoolean();
    }

    @Override
    public Type getType() {
        return Boolean.class;
    }
}
