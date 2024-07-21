package fr.modcraftmc.crossservercore.events;

import fr.modcraftmc.crossservercore.rabbitmq.RabbitmqConnection;
import net.minecraftforge.eventbus.api.Event;

public class RabbitmqConnectionReadyEvent extends Event {
    private RabbitmqConnection rabbitmqConnection;

    public RabbitmqConnectionReadyEvent(RabbitmqConnection rabbitmqConnection) {
        this.rabbitmqConnection = rabbitmqConnection;
    }

    public RabbitmqConnection getRabbitmqConnection() {
        return rabbitmqConnection;
    }
}
