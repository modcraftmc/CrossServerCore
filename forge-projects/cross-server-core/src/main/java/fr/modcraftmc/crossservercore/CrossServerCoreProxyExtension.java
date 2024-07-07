package fr.modcraftmc.crossservercore;

import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;
import fr.modcraftmc.crossservercore.message.TransferPlayer;
import fr.modcraftmc.crossservercore.api.ICrossServerCoreProxyExtension;
import fr.modcraftmc.crossservercore.message.TransferPlayerEvent;

public class CrossServerCoreProxyExtension implements ICrossServerCoreProxyExtension {
    private boolean proxyExtensionEnabled = false;

    public void enable() {
        proxyExtensionEnabled = true;
    }

    public boolean isProxyExtensionEnabled() {
        return proxyExtensionEnabled;
    }

    public void transferPlayer(ISyncPlayer player, ISyncServer serverDestination){
        if(!proxyExtensionEnabled){
            CrossServerCore.LOGGER.warn("Trying to transfer a player but the proxy extension is not enabled");
            return;
        }

        TransferPlayerEvent transferPlayerEvent = new TransferPlayerEvent(player, serverDestination);
        CrossServerCore.getServerCluster().sendMessageExceptCurrent(transferPlayerEvent);
        CrossServerCore.sendProxyMessage(new TransferPlayer(player, serverDestination));
    }
}
