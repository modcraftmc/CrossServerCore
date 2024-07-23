package fr.modcraftmc.crossservercore.api.events;

import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import net.minecraftforge.eventbus.api.Event;

public class PlayerLeaveClusterEvent extends Event {
    private boolean currentServer = false;
    private ISyncPlayer player;

    public PlayerLeaveClusterEvent(ISyncPlayer player, boolean currentServer) {
        this.player = player;
        this.currentServer = currentServer;
    }

    public ISyncPlayer getPlayer() {
        return player;
    }

    public boolean isCurrentServer() {
        return currentServer;
    }
}
