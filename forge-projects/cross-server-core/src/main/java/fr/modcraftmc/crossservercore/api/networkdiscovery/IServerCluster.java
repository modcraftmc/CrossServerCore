package fr.modcraftmc.crossservercore.api.networkdiscovery;

import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.message.IMessageSender;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IServerCluster extends IMessageSender {
    Optional<? extends ISyncServer> getServer(String serverName);
    ISyncServerProxy getImmediateServer(String serverName);
    void sendMessage(BaseMessage message);
    void sendMessageExceptCurrent(BaseMessage message);
    List<? extends ISyncPlayer> getPlayers();
    Optional<? extends ISyncPlayer> getPlayer(String playerName);
    Optional<? extends ISyncPlayer> getPlayer(UUID playerUUID);
    ISyncPlayerProxy getImmediatePlayer(UUID playerUUID, String playerName);
    ISyncPlayerProxy getImmediatePlayer(Player player);

}
