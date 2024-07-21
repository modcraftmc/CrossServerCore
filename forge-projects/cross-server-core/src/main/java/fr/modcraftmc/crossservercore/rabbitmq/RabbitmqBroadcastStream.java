package fr.modcraftmc.crossservercore.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.DeliverCallback;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.References;

import java.io.IOException;

public class RabbitmqBroadcastStream extends RabbitmqStream {

    public RabbitmqBroadcastStream(RabbitmqConnection rabbitmqConnection, String exchangeName) {
        super(rabbitmqConnection, exchangeName, BuiltinExchangeType.FANOUT);
    }

    public void publish(String message) throws IOException {
        CrossServerCore.LOGGER.info("sending : " + message); //todo: delete
        CrossServerCore.LOGGER.debug(String.format("Publishing message to %s", exchangeName));
        rabbitmqChannel.basicPublish(exchangeName, "", null, message.getBytes());
    }

    public void subscribe(DeliverCallback listener) {
        try {
            String queueName = rabbitmqChannel.queueDeclare().getQueue();
            rabbitmqChannel.queueBind(queueName, exchangeName, "");
            rabbitmqChannel.basicConsume(queueName, true, listener, consumerTag -> {});
        } catch (IOException e) {
            CrossServerCore.LOGGER.error("Error while subscribing to RabbitMQ exchange");
            throw new RuntimeException(e);
        }
    }
}
