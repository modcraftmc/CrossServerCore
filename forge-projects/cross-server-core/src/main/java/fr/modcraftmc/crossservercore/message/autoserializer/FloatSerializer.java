package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;

import java.lang.reflect.Type;

public class FloatSerializer extends FieldSerializer<Float> {

    @Override
    public JsonElement serialize(Float value) {
        return new JsonPrimitive(value);
    }

    @Override
    public Float deserialize(JsonElement json, Type type) {
        return json.getAsFloat();
    }

    @Override
    public Type getType() {
        return float.class;
    }
}
