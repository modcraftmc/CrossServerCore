package fr.modcraftmc.crossservercoreproxyextension.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercoreproxyextension.CrossServerCoreProxy;
import fr.modcraftmc.crossservercoreproxyextension.rabbitmq.RabbitmqDirectSubscriber;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MessageHandler {
    static final Map<String, Function<JsonObject, ? extends BaseMessage>> messageMap = new HashMap<>();
    public static Gson GSON = new Gson();

    public static void init(){
        messageMap.put(TransferPlayer.MESSAGE_NAME, TransferPlayer::deserialize);
        messageMap.put(ProxyExtensionHandshake.MESSAGE_NAME, ProxyExtensionHandshake::deserialize);
        messageMap.put(ProxyExtensionHandshakeResponse.MESSAGE_NAME, ProxyExtensionHandshakeResponse::deserialize);

        CrossServerCoreProxy.instance.onConfigLoad.add(() -> {
            RabbitmqDirectSubscriber.instance.subscribe("proxy", (consumerTag, message) -> {
                CrossServerCoreProxy.instance.getLogger().debug("Received message: " + new String(message.getBody()));
                String messageJson = new String(message.getBody(), StandardCharsets.UTF_8);
                try {
                    MessageHandler.handle(messageJson);
                } catch (Exception e) {
                    CrossServerCoreProxy.instance.getLogger().error("Error while handling message", e);
                }
            });
        });
    }

    public static void registerCrossMessage(String messageName, Function<JsonObject, ? extends BaseMessage> deserializer) {
        messageMap.put(messageName, deserializer);
    }

    public static void handle(JsonObject message){
        if(messageMap.containsKey(message.get("messageName").getAsString()))
            messageMap.get(message.get("messageName").getAsString()).apply(message).handle();
        else
            CrossServerCoreProxy.instance.getLogger().error("Message id {} not found", message.get("messageName").getAsString());
    }

    public static void handle(String message){
        handle(GSON.fromJson(message, JsonObject.class));
    }

    public static boolean isMessageRegistered(String messageName){
        return messageMap.containsKey(messageName);
    }
}
