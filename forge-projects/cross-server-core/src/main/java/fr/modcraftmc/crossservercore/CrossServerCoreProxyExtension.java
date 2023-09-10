package fr.modcraftmc.crossservercore;

import fr.modcraftmc.crossservercore.message.TransferPlayer;
import fr.modcraftmc.crossservercoreapi.ICrossServerCoreProxyExtension;

public class CrossServerCoreProxyExtension implements ICrossServerCoreProxyExtension {
    private boolean proxyExtensionEnabled = false;

    public void enable() {
        proxyExtensionEnabled = true;
    }

    public boolean isProxyExtensionEnabled() {
        return proxyExtensionEnabled;
    }

    public void transferPlayer(String playerName, String serverName){
        if(!proxyExtensionEnabled){
            CrossServerCore.LOGGER.warn("Trying to transfer a player but the proxy extension is not enabled");
            return;
        }
        CrossServerCore.sendProxyMessage(new TransferPlayer(playerName, serverName));
    }
}
