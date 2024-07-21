package fr.modcraftmc.crossservercore.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.DeliverCallback;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.References;

import java.io.IOException;

public class RabbitmqDirectStream extends RabbitmqStream {

    public RabbitmqDirectStream(RabbitmqConnection rabbitmqConnection, String exchangeName) {
        super(rabbitmqConnection, exchangeName, BuiltinExchangeType.DIRECT);
    }

    public void publish(String routingKey, String message) throws IOException {
        CrossServerCore.LOGGER.info("sending : " + message); //todo: delete
        CrossServerCore.LOGGER.debug(String.format("Publishing message to %s with routing key %s", exchangeName, routingKey));
        rabbitmqChannel.basicPublish(exchangeName, routingKey, null, message.getBytes());
    }

    public void subscribe(String routingKey, DeliverCallback listener) {
        try {
            String queueName = rabbitmqChannel.queueDeclare().getQueue();
            rabbitmqChannel.queueBind(queueName, exchangeName, routingKey);
            rabbitmqChannel.basicConsume(queueName, true, listener, consumerTag -> {});
        } catch (IOException e) {
            CrossServerCore.LOGGER.error("Error while subscribing to RabbitMQ exchange");
            throw new RuntimeException(e);
        }
    }
}
