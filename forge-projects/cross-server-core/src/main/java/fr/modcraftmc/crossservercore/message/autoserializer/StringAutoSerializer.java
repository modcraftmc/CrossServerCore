package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;

import java.lang.reflect.Type;

public class StringAutoSerializer extends FieldSerializer<String> {

    @Override
    public JsonElement serialize(String value) {
        return new JsonPrimitive(value);
    }

    @Override
    public String deserialize(JsonElement json, Type type) {
        return json.getAsString();
    }

    @Override
    public Type getType() {
        return String.class;
    }
}
