package fr.modcraftmc.crossservercore.api;

import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;

public interface ICrossServerCoreProxyExtension {
    public boolean isProxyExtensionEnabled();
    public void transferPlayer(ISyncPlayer player, ISyncServer serverDestination);
}
