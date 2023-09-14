package fr.modcraftmc.crossservercore.api.networkdiscovery;

import fr.modcraftmc.crossservercore.api.message.IMessageSender;

import java.util.List;

public interface ISyncServer extends IMessageSender {
    public void sendMessage(String message);
    public String getName();
    public List<String> getPlayers();
}
