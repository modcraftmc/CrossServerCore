package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;

import java.lang.reflect.Type;

public class IntSerializer extends FieldSerializer<Integer> {

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
        return int.class;
    }
}
