package fr.modcraftmc.crossservercore.api.events;

import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;
import net.minecraftforge.eventbus.api.Event;

public class TransferPlayerEvent extends Event {
    private ISyncPlayer player;
    private ISyncServer serverDestination;

    public TransferPlayerEvent(ISyncPlayer player, ISyncServer serverDestination) {
        this.player = player;
        this.serverDestination = serverDestination;
    }

    public ISyncPlayer getPlayer() {
        return player;
    }

    public ISyncServer getServerDestination() {
        return serverDestination;
    }
}
