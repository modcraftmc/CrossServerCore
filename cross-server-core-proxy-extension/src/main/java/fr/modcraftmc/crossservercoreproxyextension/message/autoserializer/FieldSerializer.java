package fr.modcraftmc.crossservercoreproxyextension.message.autoserializer;

import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public abstract class FieldSerializer<T> {
    public abstract JsonElement serialize(T value);
    public abstract T deserialize(JsonElement json, Type typeOfT);
    public abstract Type getType();

    public final JsonElement serializeFromObject(Object value) {
        return serialize((T) value);
    }
}
