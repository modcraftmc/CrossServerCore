package fr.modcraftmc.crossservercore.message;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.annotation.AutoSerialize;
import fr.modcraftmc.crossservercore.api.events.PlayerLeaveClusterEvent;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncPlayer;
import net.minecraftforge.common.MinecraftForge;

@AutoRegister("PlayerLeaved")
public class PlayerLeaved extends BaseMessage {

    @AutoSerialize
    private SyncPlayer player;

    PlayerLeaved() {}

    public PlayerLeaved(SyncPlayer player) {
        this.player = player;
    }

    @Override
    public void handle() {
        CrossServerCore.LOGGER.debug(String.format("Player %s leaved server %s", player.getName(), player.getServer().getName()));
        CrossServerCore.getServerCluster().removePlayer(player);
        MinecraftForge.EVENT_BUS.post(new PlayerLeaveClusterEvent(player));
    }
}
