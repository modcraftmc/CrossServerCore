package fr.modcraftmc.crossservercore.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.rabbitmq.RabbitmqDirectSubscriber;
import fr.modcraftmc.crossservercore.rabbitmq.RabbitmqSubscriber;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MessageHandler {
    static final Map<String, Function<JsonObject, ? extends BaseMessage>> messageMap = new HashMap<>();
    public static Gson GSON = new Gson();

    public static void init(){
        messageMap.put(AttachServer.MESSAGE_NAME, AttachServer::deserialize);
        messageMap.put(AttachServerResponse.MESSAGE_NAME, AttachServerResponse::deserialize);
        messageMap.put(DetachServer.MESSAGE_NAME, DetachServer::deserialize);
        messageMap.put(PlayerJoined.MESSAGE_NAME, PlayerJoined::deserialize);
        messageMap.put(PlayerLeaved.MESSAGE_NAME, PlayerLeaved::deserialize);

        CrossServerCore.registerOnConfigLoad(() -> {
            RabbitmqDirectSubscriber.instance.subscribe(CrossServerCore.getServerName(), (consumerTag, message) -> {
                CrossServerCore.LOGGER.debug("Received message: " + new String(message.getBody()));
                String messageJson = new String(message.getBody(), StandardCharsets.UTF_8);
                try {
                    MessageHandler.handle(messageJson);
                } catch (Exception e) {
                    CrossServerCore.LOGGER.error("Error while handling message", e);
                }
            });

            RabbitmqSubscriber.instance.subscribe((consumerTag, message) -> {
                CrossServerCore.LOGGER.debug("Received message: " + new String(message.getBody()));
                String messageJson = new String(message.getBody(), StandardCharsets.UTF_8);
                try {
                    MessageHandler.handle(messageJson);
                } catch (Exception e) {
                    CrossServerCore.LOGGER.error("Error while handling message", e);
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
            CrossServerCore.LOGGER.error("Message id not found");
    }

    public static void handle(String message){
        handle(GSON.fromJson(message, JsonObject.class));
    }

    public static boolean isMessageRegistered(String messageName){
        return messageMap.containsKey(messageName);
    }
}
