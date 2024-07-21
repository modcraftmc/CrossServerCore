package fr.modcraftmc.crossservercore.message.streams;

import com.mojang.datafixers.util.Pair;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.References;
import fr.modcraftmc.crossservercore.events.RabbitmqConnectionReadyEvent;
import fr.modcraftmc.crossservercore.rabbitmq.RabbitmqBroadcastStream;
import fr.modcraftmc.crossservercore.rabbitmq.RabbitmqDirectStream;
import net.minecraftforge.common.MinecraftForge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MessageStreamsManager {
    RabbitmqDirectStream directStream;
    RabbitmqBroadcastStream broadcastStream;

    private boolean ready = false;

    private final List<Pair<String, Consumer<String>>> directSubscriptions = new ArrayList<>();
    private final List<Consumer<String>> broadcastSubscription = new ArrayList<>();

    public MessageStreamsManager() {
        MinecraftForge.EVENT_BUS.addListener(this::onRabbitmqConnectionReady);
    }

    public void onRabbitmqConnectionReady(RabbitmqConnectionReadyEvent event) {
        CrossServerCore.LOGGER.debug("Initializing message streams");
        directStream = new RabbitmqDirectStream(event.getRabbitmqConnection(), References.DIRECT_EXCHANGE_NAME);
        broadcastStream = new RabbitmqBroadcastStream(event.getRabbitmqConnection(), References.GLOBAL_EXCHANGE_NAME);

        ready = true;

        for (Pair<String, Consumer<String>> subscription : directSubscriptions) {
            subscribeDirectMessage(subscription.getFirst(), subscription.getSecond());
        }

        for (Consumer<String> subscription : broadcastSubscription) {
            subscribeBroadcastMessage(subscription);
        }

        CrossServerCore.LOGGER.debug("Message streams initialized");
    }

    public void sendDirectMessage(String routingKey, String message) {
        if (!ready) {
            CrossServerCore.LOGGER.error("Cannot send direct message, rabbitmq connection is not ready");
            return;
        }

        try {
            directStream.publish(routingKey, message);
        } catch (IOException e) {
            CrossServerCore.LOGGER.error(String.format("Error while publishing message to rabbitmq cannot send message to route point %s : %s", routingKey, e.getMessage()));
        }
    }

    public void sendBroadcastMessage(String message) {
        if (!ready) {
            CrossServerCore.LOGGER.error("Cannot send global message, rabbitmq connection is not ready");
            return;
        }

        try {
            broadcastStream.publish(message);
        } catch (IOException e) {
            CrossServerCore.LOGGER.error(String.format("Error while publishing message to rabbitmq cannot send message to global exchange : %s", e.getMessage()));
        }
    }

    public void subscribeDirectMessage(String routingKey, Consumer<String> listener) {
        if (!ready) {
            directSubscriptions.add(Pair.of(routingKey, listener));
            return;
        }

        directStream.subscribe(routingKey, ((consumerTag, message) -> listener.accept(new String(message.getBody()))));
    }

    public void subscribeBroadcastMessage(Consumer<String> listener) {
        if (!ready) {
            broadcastSubscription.add(listener);
            return;
        }

        broadcastStream.subscribe((consumerTag, message) -> listener.accept(new String(message.getBody())));
    }
}
