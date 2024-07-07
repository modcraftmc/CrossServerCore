package fr.modcraftmc.crossservercoreproxyextension.message.autoserializer;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercoreproxyextension.message.BaseMessage;

public interface IMessageAutoPropertySerializer {
    public void registerFieldSerializer(FieldSerializer<?> fieldSerializer);
    public <T extends BaseMessage> JsonObject serializeAutoproperty(JsonObject json, T message);
    public <T extends BaseMessage> T deserializeAutoproperty(JsonObject json, T message);
}
