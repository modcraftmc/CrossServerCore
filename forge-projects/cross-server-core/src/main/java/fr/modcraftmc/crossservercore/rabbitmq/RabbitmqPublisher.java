package fr.modcraftmc.crossservercore.rabbitmq;

import com.rabbitmq.client.Channel;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.References;

import java.io.IOException;

public class RabbitmqPublisher {
    public static RabbitmqPublisher instance;

    private final Channel rabbitmqChannel;

    public RabbitmqPublisher(RabbitmqConnection rabbitmqConnection) {
        try {
            this.rabbitmqChannel = rabbitmqConnection.createChannel();
            rabbitmqChannel.exchangeDeclare(References.GLOBAL_EXCHANGE_NAME, "fanout");
        } catch (IOException e) {
            CrossServerCore.LOGGER.error("Error while creating RabbitMQ exchange");
            throw new RuntimeException(e);
        }
        instance = this;
    }

    public void publish(String message) throws IOException {
        CrossServerCore.LOGGER.debug(String.format("Publishing message to %s", References.GLOBAL_EXCHANGE_NAME));
        rabbitmqChannel.basicPublish(References.GLOBAL_EXCHANGE_NAME, "", null, message.getBytes());
    }
}
