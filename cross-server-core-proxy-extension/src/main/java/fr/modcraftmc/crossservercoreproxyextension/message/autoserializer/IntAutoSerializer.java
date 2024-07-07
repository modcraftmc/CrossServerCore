package fr.modcraftmc.crossservercoreproxyextension.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class IntAutoSerializer extends FieldSerializer<Integer> {

    @Override
    public JsonElement serialize(Integer value) {
        return new JsonPrimitive(value);
    }

    @Override
    public Integer deserialize(JsonElement json, Type type) {
        return json.getAsInt();
    }

    @Override
    public Type getType() {
        return Integer.class;
    }
}
