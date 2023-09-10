package fr.modcraftmc.crossservercoreapi;

import org.slf4j.Logger;

public class CrossServerCoreProxyExtensionAPI {
    public static CrossServerCoreProxyExtensionAPI instance;


    private Logger logger;
    public ICrossServerCoreProxyExtension proxyExtension;

    public CrossServerCoreProxyExtensionAPI(Logger logger, ICrossServerCoreProxyExtension proxyExtension) {
        this.logger = logger;
        instance = this;

        logger.info("Enabling CrossServerCoreProxyExtension API");
        this.proxyExtension = proxyExtension;
    }

    public boolean isProxyExtensionEnabled() {
        return proxyExtension.isProxyExtensionEnabled();
    }

    public void transferPlayer(String playerName, String serverName){
        proxyExtension.transferPlayer(playerName, serverName);
    }
}
