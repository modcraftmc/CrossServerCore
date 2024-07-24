package fr.modcraftmc.crossservercore.api.events;

import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayerProxy;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;
import net.minecraftforge.eventbus.api.Event;

public class TransferPlayerEvent extends Event {
    private ISyncPlayerProxy player;
    private ISyncServer destination;

    public TransferPlayerEvent(ISyncPlayerProxy player, ISyncServer destination) {
        this.player = player;
        this.destination = destination;
    }

    public ISyncPlayerProxy getPlayer() {
        return player;
    }

    public ISyncServer getDestination() {
        return destination;
    }
}
