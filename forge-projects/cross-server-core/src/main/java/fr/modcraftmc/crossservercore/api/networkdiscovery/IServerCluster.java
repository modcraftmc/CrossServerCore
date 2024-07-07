package fr.modcraftmc.crossservercore.api.networkdiscovery;

import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.message.IMessageSender;

import java.util.List;
import java.util.Optional;

public interface IServerCluster extends IMessageSender {
    public Optional<? extends ISyncServer> getServer(String serverName);
    public void sendMessage(BaseMessage message);
    public void sendMessageExceptCurrent(BaseMessage message);
    public List<? extends ISyncPlayer> getPlayers();
    Optional<? extends ISyncPlayer> getPlayer(String playerName);
}
