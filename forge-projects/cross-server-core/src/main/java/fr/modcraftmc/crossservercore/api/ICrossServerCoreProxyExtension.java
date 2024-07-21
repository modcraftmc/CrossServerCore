package fr.modcraftmc.crossservercore.api;

import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;

public interface ICrossServerCoreProxyExtension {
    public boolean isEnable();
    public void transferPlayer(ISyncPlayer player, ISyncServer serverDestination);
}
