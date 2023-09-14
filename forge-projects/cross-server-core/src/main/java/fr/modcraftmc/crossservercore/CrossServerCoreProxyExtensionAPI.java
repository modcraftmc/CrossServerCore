package fr.modcraftmc.crossservercore;

import fr.modcraftmc.crossservercore.message.TransferPlayer;

@Deprecated
public class CrossServerCoreProxyExtensionAPI {
    private static boolean proxyExtensionEnabled = false;

    public static void APIInit(){
        CrossServerCore.LOGGER.info("Enabling CrossServerCoreProxyExtension API");
        proxyExtensionEnabled = true;
    }

    public static boolean isProxyExtensionEnabled() {
        return proxyExtensionEnabled;
    }

    public static void transferPlayer(String playerName, String serverName){
        if(!proxyExtensionEnabled){
            CrossServerCore.LOGGER.warn("Trying to transfer a player but the proxy extension is not enabled");
            return;
        }
        CrossServerCore.sendProxyMessage(new TransferPlayer(playerName, serverName));
    }
}