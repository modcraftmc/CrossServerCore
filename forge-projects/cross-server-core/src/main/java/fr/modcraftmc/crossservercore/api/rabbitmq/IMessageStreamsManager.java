package fr.modcraftmc.crossservercore.api.rabbitmq;

import java.util.function.Consumer;

public interface IMessageStreamsManager {
    public void sendDirectMessage(String routingKey, String message);
    public void sendBroadcastMessage(String message);
    public void subscribeDirectMessage(String routingKey, Consumer<String> listener);
    public void subscribeBroadcastMessage(Consumer<String> listener);
}
