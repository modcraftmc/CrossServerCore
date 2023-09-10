package fr.modcraftmc.crossservercoreapi.networkdiscovery;

import fr.modcraftmc.crossservercoreapi.message.IMessageSender;

import java.util.List;
import java.util.Optional;

public interface IServerCluster extends IMessageSender {
    public Optional<? extends ISyncServer> getServer(String serverName);
    public void sendMessage(String message);
    public void sendMessageExceptCurrent(String message);
    public List<String> getPlayers();
    public Optional<? extends ISyncServer> findPlayer(String player);
}
