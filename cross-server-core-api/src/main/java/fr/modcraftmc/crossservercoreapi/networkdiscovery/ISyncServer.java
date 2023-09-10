package fr.modcraftmc.crossservercoreapi.networkdiscovery;

import fr.modcraftmc.crossservercoreapi.message.IMessageSender;

import java.util.List;

public interface ISyncServer extends IMessageSender {
    public void sendMessage(String message);
    public String getName();
    public List<String> getPlayers();
}
