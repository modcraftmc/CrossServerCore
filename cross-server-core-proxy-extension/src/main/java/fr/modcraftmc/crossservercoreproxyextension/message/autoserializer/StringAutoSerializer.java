package fr.modcraftmc.crossservercoreproxyextension.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

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
