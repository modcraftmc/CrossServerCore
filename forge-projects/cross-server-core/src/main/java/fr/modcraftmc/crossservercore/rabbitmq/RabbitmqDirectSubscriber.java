package fr.modcraftmc.crossservercore.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.References;

import java.io.IOException;

public class RabbitmqDirectSubscriber {
    public static RabbitmqDirectSubscriber instance;

    private final Channel rabbitmqChannel;

    public RabbitmqDirectSubscriber(RabbitmqConnection rabbitmqConnection) {
        try {
            this.rabbitmqChannel = rabbitmqConnection.createChannel();
            rabbitmqChannel.exchangeDeclare(References.DIRECT_EXCHANGE_NAME, "direct");
        } catch (IOException e) {
            CrossServerCore.LOGGER.error("Error while creating RabbitMQ exchange");
            throw new RuntimeException(e);
        }
        instance = this;
    }

    public void subscribe(String routingKey, DeliverCallback listener) {
        try {
            String queueName = rabbitmqChannel.queueDeclare().getQueue();
            rabbitmqChannel.queueBind(queueName, References.DIRECT_EXCHANGE_NAME, routingKey);
            rabbitmqChannel.basicConsume(queueName, true, listener, consumerTag -> {});
        } catch (IOException e) {
            CrossServerCore.LOGGER.error("Error while subscribing to RabbitMQ exchange");
            throw new RuntimeException(e);
        }
    }
}
