package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.api.annotation.AutoSerialize;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;
import fr.modcraftmc.crossservercore.api.message.autoserializer.IMessageAutoPropertySerializer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

public class MessageAutoPropertySerializer implements IMessageAutoPropertySerializer {
    private HashMap<Type, FieldSerializer<?>> fieldSerializers = new HashMap<>();

    public void registerFieldSerializer(FieldSerializer<?> fieldSerializer) {
        fieldSerializers.put(fieldSerializer.getType(), fieldSerializer);
    }

    public void init() {
        registerFieldSerializer(new BooleanSerializer());
        registerFieldSerializer(new FloatSerializer());
        registerFieldSerializer(new IntSerializer());
        registerFieldSerializer(new StringSerializer());
        registerFieldSerializer(new ISyncServerSerializer());
        registerFieldSerializer(new SyncServerSerializer());
        registerFieldSerializer(new ISyncPlayerSerializer());
        registerFieldSerializer(new SyncPlayerSerializer());
        registerFieldSerializer(new ListSerializer());
        registerFieldSerializer(new UUIDSerializer());
        registerFieldSerializer(new OptionalSerializer());
        registerFieldSerializer(new ISyncPlayerProxySerializer());
        registerFieldSerializer(new ISyncServerProxySerializer());
        registerFieldSerializer(new JsonElementSerializer());
        registerFieldSerializer(new MapSerializer());
        registerFieldSerializer(new CompoundTagSerializer());
    }

    public <T extends BaseMessage> JsonObject serializeAutoproperty(JsonObject json, T message) {
        Class<T> clazz = (Class<T>) message.getClass();

        for(Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(AutoSerialize.class)) {
                continue;
            }

            field.setAccessible(true);
            Class<?> type = field.getType();

            try {
                JsonElement value = serializeObject(field.get(message), type);
                json.add("auto_"+field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return json;
    }

    public <T extends BaseMessage> T deserializeAutoproperty(JsonObject json, T message) {
        Class<T> clazz = (Class<T>) message.getClass();

        for(Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(AutoSerialize.class)) {
                continue;
            }

            field.setAccessible(true);
            Type type = field.getGenericType();

            try {
                JsonElement value = json.get("auto_"+field.getName());

                if(type instanceof ParameterizedType) {
                    field.set(message, deserializegenericObject(value, (ParameterizedType) type));
                }else{
                    field.set(message, deserializeObject(value, type));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return message;
    }

    public JsonElement serializeObject(Object value) {
        return serializeObject(value, value.getClass());
    }

    public JsonElement serializeObject(Object value, Type typeOfT) {
        FieldSerializer<?> fieldSerializer = fieldSerializers.get(typeOfT);
        if (fieldSerializer == null) {
            throw new RuntimeException("No serializer found for type " + typeOfT);
        }
        return fieldSerializer.serializeFromObject(value);
    }

    public Object deserializeObject(JsonElement json, Type typeOfT) {
        FieldSerializer<?> fieldSerializer = fieldSerializers.get(typeOfT);
        if (fieldSerializer == null) {
            throw new RuntimeException("No deserializer found for type " + typeOfT);
        }
        return fieldSerializer.deserialize(json, typeOfT);
    }

    public <T> T deserializeObject(JsonElement json, Class<T> typeOfT) {
        FieldSerializer<?> fieldSerializer = fieldSerializers.get(typeOfT);
        if (fieldSerializer == null) {
            throw new RuntimeException("No deserializer found for type " + typeOfT);
        }
        return (T) fieldSerializer.deserialize(json, typeOfT);
    }

    public Object deserializegenericObject(JsonElement json, ParameterizedType typeOfT) {
        FieldSerializer<?> fieldSerializer = fieldSerializers.get(typeOfT.getRawType());
        if (fieldSerializer == null) {
            throw new RuntimeException("No deserializer found for type " + typeOfT);
        }
        return fieldSerializer.deserialize(json, typeOfT);
    }
}
