package fr.modcraftmc.crossservercoreapi.message;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface IMessageHandler {
    final Map<String, Function<JsonObject, ? extends BaseMessage>> messageMap = new HashMap<>();

    public abstract boolean isMessageRegistered(String messageName);
    public abstract void registerCrossMessage(String messageName, Function<JsonObject, ? extends BaseMessage> deserializer);
}
