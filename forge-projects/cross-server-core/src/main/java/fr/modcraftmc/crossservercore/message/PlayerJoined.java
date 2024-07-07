package fr.modcraftmc.crossservercore.message;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.annotation.AutoSerialize;
import fr.modcraftmc.crossservercore.api.events.PlayerJoinClusterEvent;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncPlayer;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncServer;
import net.minecraftforge.common.MinecraftForge;

import java.util.UUID;

@AutoRegister("PlayerJoined")
public class PlayerJoined extends BaseMessage {

    @AutoSerialize
    public String playerName;

    @AutoSerialize
    public UUID playerUUID;

    @AutoSerialize
    public SyncServer server;

    PlayerJoined() {}

    public PlayerJoined(UUID playerUUID, String playerName, SyncServer server) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.server = server;
    }

    @Override
    public void handle() {
        CrossServerCore.LOGGER.debug(String.format("Player %s joined server %s", playerName, server.getName()));
        SyncPlayer player = CrossServerCore.getServerCluster().setPlayerLocation(playerUUID, playerName, server);
        MinecraftForge.EVENT_BUS.post(new PlayerJoinClusterEvent(player));
    }
}
