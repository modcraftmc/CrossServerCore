package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonElement;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;

import java.lang.reflect.Type;

public class JsonElementSerializer extends FieldSerializer<JsonElement> {
    @Override
    public JsonElement serialize(JsonElement value) {
        return value;
    }

    @Override
    public JsonElement deserialize(JsonElement json, Type typeOfT) {
        return json;
    }

    @Override
    public Type getType() {
        return JsonElement.class;
    }
}
