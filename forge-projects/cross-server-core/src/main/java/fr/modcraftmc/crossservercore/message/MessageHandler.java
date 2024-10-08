package fr.modcraftmc.crossservercore.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.ReflectionUtil;
import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.message.SendMessage;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.message.IMessageHandler;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class MessageHandler implements IMessageHandler {
    private final Map<String, Function<JsonObject, ? extends BaseMessage>> messageMap = new HashMap<>();
    private Gson GSON = new Gson();

    public void init(){

        registerAutoMessage();

        CrossServerCore.getMessageStreamsManager().subscribeDirectMessage(CrossServerCore.getServerName(), (message) -> {
            CrossServerCore.LOGGER.info("Received message: " + message); //todo: delete
            CrossServerCore.LOGGER.debug("Received message: " + message);
            try {
                handle(message);
            } catch (Exception e) {
                CrossServerCore.LOGGER.error("Error while handling message", e);
            }
        });

        CrossServerCore.getMessageStreamsManager().subscribeBroadcastMessage((message) -> {
            CrossServerCore.LOGGER.info("Received message: " + message); //todo: delete
            CrossServerCore.LOGGER.debug("Received message: " + message);
            try {
                handle(message);
            } catch (Exception e) {
                CrossServerCore.LOGGER.error("Error while handling message", e);
            }
        });
    }

    private void registerAutoMessage() {
        ReflectionUtil.getClassesWithAnnotation(AutoRegister.class).forEach(clazz -> {
            CrossServerCore.LOGGER.info("Auto registering class {}", clazz.getName());
            if(!BaseMessage.class.isAssignableFrom(clazz)){
                CrossServerCore.LOGGER.error("Class {} is not a subclass of BaseMessage", clazz.getName());
                return;
            }

            Class<? extends BaseMessage> messageClazz = (Class<? extends BaseMessage>) clazz;
            registerFullAutoCrossMessage(messageClazz);
        });
    }

    public void registerCrossMessage(String messageName, Function<JsonObject, ? extends BaseMessage> deserializer) {
        messageMap.put(messageName, deserializer);
        CrossServerCore.LOGGER.info("Registered message {}", messageName);
    }

    public <T extends BaseMessage> void registerCrossMessageWithoutCustomDeserializerAndMessageNameGetter(String messageName, Supplier<T> defaultMessage){
        registerCrossMessage(messageName, (json) -> defaultMessage.get());
    }

    public <T extends BaseMessage> void registerCrossMessageWithoutCustomDeserializer(Supplier<T> defaultMessage){
        registerCrossMessage(defaultMessage.get().getMessageName(), (json) -> defaultMessage.get());
    }

    public <T extends BaseMessage> void registerFullAutoCrossMessage(Class<T> messageClass){
        Supplier<T> defaultMessage = () -> {
            try {
                Constructor<T> constructor = messageClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                CrossServerCore.LOGGER.error("No default constructor found for class {}", messageClass.getName());
                throw new RuntimeException(e);
            }
        };

        AutoRegister annotation = messageClass.getAnnotation(AutoRegister.class);

        registerCrossMessage(annotation.value(), (json) -> defaultMessage.get());
    }

    public void handle(JsonObject message){
        ServerLifecycleHooks.getCurrentServer().execute(() -> {
            if(messageMap.containsKey(message.get("messageName").getAsString())) {
                BaseMessage messageObject = messageMap.get(message.get("messageName").getAsString()).apply(message);
                CrossServerCore.getMessageAutoPropertySerializer().deserializeAutoproperty(message, messageObject);
                messageObject.handle();
            }
            else {
                CrossServerCore.LOGGER.error("Message id {} not found", message.get("messageName").getAsString());
                CrossServerCore.LOGGER.error("Valid message ids are: {}", messageMap.keySet());
            }
        });
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
