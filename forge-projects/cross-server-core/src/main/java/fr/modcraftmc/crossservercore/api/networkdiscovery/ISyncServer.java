package fr.modcraftmc.crossservercore.api.networkdiscovery;

import fr.modcraftmc.crossservercore.api.message.BaseMessage;
import fr.modcraftmc.crossservercore.api.message.IMessageSender;

import java.util.List;

public interface ISyncServer extends IMessageSender {
    public void sendMessage(BaseMessage message);
    public String getName();
    public List<? extends ISyncPlayer> getPlayers();
    public ISyncServerProxy proxy();
}
