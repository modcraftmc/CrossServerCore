package fr.modcraftmc.crossservercore.api.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;

import java.lang.reflect.Type;

public interface IMessageAutoPropertySerializer {
    public void registerFieldSerializer(FieldSerializer<?> fieldSerializer);
    public <T extends BaseMessage> JsonObject serializeAutoproperty(JsonObject json, T message);
    public <T extends BaseMessage> T deserializeAutoproperty(JsonObject json, T message);

    public JsonElement serializeObject(Object value);
    public <T> T deserializeObject(JsonElement json, Class<T> typeOfT);
}
