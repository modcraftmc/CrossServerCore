package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class OptionalSerializer extends FieldSerializer<Optional> {
    @Override
    public JsonElement serialize(Optional value) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("isPresent", value.isPresent());
        if (value.isPresent()) {
            jsonObject.add("value", CrossServerCore.getMessageAutoPropertySerializer().serializeObject(value.get()));
        }

        return jsonObject;
    }

    @Override
    public Optional deserialize(JsonElement json, Type type) {
        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.get("isPresent").getAsBoolean()) {
            if(!(type instanceof ParameterizedType)) throw new IllegalArgumentException("Type is not a parameterized type");
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type genericType = parameterizedType.getActualTypeArguments()[0];

            return Optional.of(CrossServerCore.getMessageAutoPropertySerializer().deserializeObject(jsonObject.get("value"), genericType));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Type getType() {
        return Optional.class;
    }
}
