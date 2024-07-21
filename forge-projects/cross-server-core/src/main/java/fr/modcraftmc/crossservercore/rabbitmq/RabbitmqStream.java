package fr.modcraftmc.crossservercore.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import fr.modcraftmc.crossservercore.CrossServerCore;

import java.io.IOException;

public abstract class RabbitmqStream {
    protected final Channel rabbitmqChannel;
    protected final String exchangeName;

    public RabbitmqStream(RabbitmqConnection rabbitmqConnection, String exchangeName, BuiltinExchangeType exchangeType) {
        try {
            this.rabbitmqChannel = rabbitmqConnection.createChannel();
            this.exchangeName = exchangeName;
            rabbitmqChannel.exchangeDeclare(exchangeName, exchangeType);
        } catch (IOException e) {
            CrossServerCore.LOGGER.error("Error while creating RabbitMQ exchange");
            throw new RuntimeException(e);
        }
    }
}
