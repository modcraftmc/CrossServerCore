package fr.modcraftmc.crossservercore.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import fr.modcraftmc.crossservercore.CrossServerCore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RabbitmqConnection {

    private final Connection connection;
    private final List<Channel> channels = new ArrayList<>();

    public RabbitmqConnection(Connection connection) {
        this.connection = connection;
    }

    public Channel createChannel() throws IOException {
        Channel channel = connection.createChannel();
        channels.add(channel);
        return channel;
    }

    public void close() {
        for (Channel channel : channels) {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                CrossServerCore.LOGGER.error(String.format("Error while closing rabbitmq channel %s", e.getMessage()));
            }
        }
        try {
            connection.close();
        } catch (IOException e) {
            CrossServerCore.LOGGER.error(String.format("Error while closing rabbitmq connection %s", e.getMessage()));
        }
    }
}