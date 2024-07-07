package fr.modcraftmc.crossservercore.api.events;

import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import net.minecraftforge.eventbus.api.Event;

public class PlayerLeaveClusterEvent extends Event {
    private ISyncPlayer player;

    public PlayerLeaveClusterEvent(ISyncPlayer player) {
        this.player = player;
    }

    public ISyncPlayer getPlayer() {
        return player;
    }
}
