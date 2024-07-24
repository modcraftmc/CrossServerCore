package fr.modcraftmc.crossservercore;

import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;
import fr.modcraftmc.crossservercore.message.TransferPlayer;
import fr.modcraftmc.crossservercore.api.ICrossServerCoreProxyExtension;
import fr.modcraftmc.crossservercore.message.TransferPlayerEventMessage;

public class CrossServerCoreProxyExtension implements ICrossServerCoreProxyExtension {
    private boolean proxyExtensionEnabled = false;

    public void enable() {
        proxyExtensionEnabled = true;
    }

    public boolean isEnable() {
        return proxyExtensionEnabled;
    }

    public void transferPlayer(ISyncPlayer player, ISyncServer serverDestination){
        if(!proxyExtensionEnabled){
            CrossServerCore.LOGGER.warn("Trying to transfer a player but the proxy extension is not enabled");
            return;
        }

        TransferPlayerEventMessage transferPlayerEventMessage = new TransferPlayerEventMessage(player.proxy(), serverDestination);
        CrossServerCore.getServerCluster().sendMessageExceptCurrent(transferPlayerEventMessage);
        CrossServerCore.sendProxyMessage(new TransferPlayer(player, serverDestination));
    }
}
