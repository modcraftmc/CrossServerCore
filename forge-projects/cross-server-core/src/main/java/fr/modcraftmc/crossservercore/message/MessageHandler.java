package fr.modcraftmc.crossservercore.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.rabbitmq.RabbitmqDirectSubscriber;
import fr.modcraftmc.crossservercore.rabbitmq.RabbitmqSubscriber;
import fr.modcraftmc.crossservercoreapi.message.BaseMessage;
import fr.modcraftmc.crossservercoreapi.message.IMessageHandler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class MessageHandler implements IMessageHandler {
    private final Map<String, Function<JsonObject, ? extends BaseMessage>> messageMap = new HashMap<>();
    private Gson GSON = new Gson();

    public MessageHandler(){
    }

    public void init(){
        messageMap.put(AttachServer.MESSAGE_NAME, AttachServer::deserialize);
        messageMap.put(AttachServerResponse.MESSAGE_NAME, AttachServerResponse::deserialize);
        messageMap.put(DetachServer.MESSAGE_NAME, DetachServer::deserialize);
        messageMap.put(PlayerJoined.MESSAGE_NAME, PlayerJoined::deserialize);
        messageMap.put(PlayerLeaved.MESSAGE_NAME, PlayerLeaved::deserialize);
        messageMap.put(TransferPlayer.MESSAGE_NAME, TransferPlayer::deserialize);
        messageMap.put(ProxyExtensionHandshake.MESSAGE_NAME, ProxyExtensionHandshake::deserialize);
        messageMap.put(ProxyExtensionHandshakeResponse.MESSAGE_NAME, ProxyExtensionHandshakeResponse::deserialize);

        CrossServerCore.registerOnConfigLoad(() -> {
            RabbitmqDirectSubscriber.instance.subscribe(CrossServerCore.getServerName(), (consumerTag, message) -> {
                CrossServerCore.LOGGER.debug("Received message: " + new String(message.getBody()));
                String messageJson = new String(message.getBody(), StandardCharsets.UTF_8);
                try {
                    handle(messageJson);
                } catch (Exception e) {
                    CrossServerCore.LOGGER.error("Error while handling message", e);
                }
            });

            RabbitmqSubscriber.instance.subscribe((consumerTag, message) -> {
                CrossServerCore.LOGGER.debug("Received message: " + new String(message.getBody()));
                String messageJson = new String(message.getBody(), StandardCharsets.UTF_8);
                try {
                    handle(messageJson);
                } catch (Exception e) {
                    CrossServerCore.LOGGER.error("Error while handling message", e);
                }
            });
        });
    }

    public void registerCrossMessage(String messageName, Function<JsonObject, ? extends BaseMessage> deserializer) {
        messageMap.put(messageName, deserializer);
        CrossServerCore.LOGGER.info("Registered message {}", messageName);
    }

    public void handle(JsonObject message){
        if(messageMap.containsKey(message.get("messageName").getAsString()))
            messageMap.get(message.get("messageName").getAsString()).apply(message).handle();
        else {
            CrossServerCore.LOGGER.error("Message id {} not found", message.get("messageName").getAsString());
            CrossServerCore.LOGGER.error("Valid message ids are: {}", messageMap.keySet());
        }
    }

    public void handle(String message){
        handle(GSON.fromJson(message, JsonObject.class));
    }

    public boolean isMessageRegistered(String messageName){
        return messageMap.containsKey(messageName);
    }

    public Set<String> getRegisteredMessages() {
        return messageMap.keySet();
    }
}
