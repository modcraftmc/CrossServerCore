package fr.modcraftmc.crossservercore.rabbitmq;

import com.rabbitmq.client.Channel;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.References;

import java.io.IOException;

public class RabbitmqDirectPublisher {
    public static RabbitmqDirectPublisher instance;

    private final Channel rabbitmqChannel;

    public RabbitmqDirectPublisher(RabbitmqConnection rabbitmqConnection) {
        try {
            this.rabbitmqChannel = rabbitmqConnection.createChannel();
            rabbitmqChannel.exchangeDeclare(References.DIRECT_EXCHANGE_NAME, "direct");
        } catch (IOException e) {
            CrossServerCore.LOGGER.error("Error while creating RabbitMQ exchange");
            throw new RuntimeException(e);
        }
        instance = this;
    }

    public void publish(String routingKey, String message) throws IOException {
        CrossServerCore.LOGGER.debug(String.format("Publishing message to %s with routing key %s", References.DIRECT_EXCHANGE_NAME, routingKey));
        rabbitmqChannel.basicPublish(References.DIRECT_EXCHANGE_NAME, routingKey, null, message.getBytes());
    }
}
