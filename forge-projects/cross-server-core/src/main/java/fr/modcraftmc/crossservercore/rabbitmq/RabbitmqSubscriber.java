package fr.modcraftmc.crossservercore.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.References;

import java.io.IOException;

public class RabbitmqSubscriber {
    public static RabbitmqSubscriber instance;

    private final Channel rabbitmqChannel;

    public RabbitmqSubscriber(RabbitmqConnection rabbitmqConnection) {
        try {
            this.rabbitmqChannel = rabbitmqConnection.createChannel();
            rabbitmqChannel.exchangeDeclare(References.GLOBAL_EXCHANGE_NAME, "fanout");
        } catch (IOException e) {
            CrossServerCore.LOGGER.error("Error while creating RabbitMQ exchange");
            throw new RuntimeException(e);
        }
        instance = this;
    }

    public void subscribe(DeliverCallback listener) {
        try {
            String queueName = rabbitmqChannel.queueDeclare().getQueue();
            rabbitmqChannel.queueBind(queueName, References.GLOBAL_EXCHANGE_NAME, "");
            rabbitmqChannel.basicConsume(queueName, true, listener, consumerTag -> {});
        } catch (IOException e) {
            CrossServerCore.LOGGER.error("Error while subscribing to RabbitMQ exchange");
            throw new RuntimeException(e);
        }
    }
}
