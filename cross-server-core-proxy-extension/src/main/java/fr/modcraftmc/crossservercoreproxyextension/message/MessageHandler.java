package fr.modcraftmc.crossservercoreproxyextension.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercoreproxyextension.CrossServerCoreProxy;
import fr.modcraftmc.crossservercoreproxyextension.ReflectionUtil;
import fr.modcraftmc.crossservercoreproxyextension.annotation.AutoRegister;
import fr.modcraftmc.crossservercoreproxyextension.rabbitmq.RabbitmqDirectSubscriber;
import fr.modcraftmc.crossservercoreproxyextension.rabbitmq.RabbitmqSubscriber;

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

    public MessageHandler(){
    }

    public void init(){

        registerAutoMessage();

        RabbitmqDirectSubscriber.instance.subscribe("proxy", (consumerTag, message) -> {
            CrossServerCoreProxy.instance.getLogger().info("Received message: " + new String(message.getBody())); //todo: delete
            CrossServerCoreProxy.instance.getLogger().debug("Received message: " + new String(message.getBody()));
            String messageJson = new String(message.getBody(), StandardCharsets.UTF_8);
            try {
                handle(messageJson);
            } catch (Exception e) {
                CrossServerCoreProxy.instance.getLogger().error("Error while handling message", e);
            }
        });
    }

    private void registerAutoMessage() {
        ReflectionUtil.getClassesWithAnnotation(AutoRegister.class).forEach(clazz -> {
            CrossServerCoreProxy.instance.getLogger().info("Auto registering class {}", clazz.getName());
            if(!BaseMessage.class.isAssignableFrom(clazz)){
                CrossServerCoreProxy.instance.getLogger().error("Class {} is not a subclass of BaseMessage", clazz.getName());
                return;
            }

            Class<? extends BaseMessage> messageClazz = (Class<? extends BaseMessage>) clazz;
            registerFullAutoCrossMessage(messageClazz);
        });
    }

    public void registerCrossMessage(String messageName, Function<JsonObject, ? extends BaseMessage> deserializer) {
        messageMap.put(messageName, deserializer);
        CrossServerCoreProxy.instance.getLogger().info("Registered message {}", messageName);
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
                return messageClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                CrossServerCoreProxy.instance.getLogger().error("No default constructor found for class {}", messageClass.getName());
                throw new RuntimeException(e);
            }
        };

        AutoRegister annotation = messageClass.getAnnotation(AutoRegister.class);

        registerCrossMessage(annotation.value(), (json) -> defaultMessage.get());
    }

    public void handle(JsonObject message){
        if(messageMap.containsKey(message.get("messageName").getAsString())) {
            BaseMessage messageObject = messageMap.get(message.get("messageName").getAsString()).apply(message);
            CrossServerCoreProxy.instance.getMessageAutoPropertySerializer().deserializeAutoproperty(message, messageObject);
            messageObject.handle();
        }
        else {
            CrossServerCoreProxy.instance.getLogger().error("Message id {} not found", message.get("messageName").getAsString());
            CrossServerCoreProxy.instance.getLogger().error("Valid message ids are: {}", messageMap.keySet());
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
