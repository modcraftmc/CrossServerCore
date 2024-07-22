package fr.modcraftmc.crossservercore.api.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Type;

public class ComponentSerializer extends FieldSerializer<Component> {
    @Override
    public JsonElement serialize(Component value) {
        return new JsonPrimitive(Component.Serializer.toJson(value));
    }

    @Override
    public Component deserialize(JsonElement json, Type typeOfT) {
        return Component.Serializer.fromJson(json.getAsString());
    }

    @Override
    public Type getType() {
        return Component.class;
    }
}
