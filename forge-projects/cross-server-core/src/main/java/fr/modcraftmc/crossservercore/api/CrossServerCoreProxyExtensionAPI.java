package fr.modcraftmc.crossservercore.api;

import com.mojang.logging.LogUtils;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;
import org.slf4j.Logger;

public class CrossServerCoreProxyExtensionAPI {
    private static final Logger logger = LogUtils.getLogger();
    private static boolean loaded = false;
    public static ICrossServerCoreProxyExtension proxyExtension;

    public static void initProxyExtensionAPI(ICrossServerCoreProxyExtension api_proxyExtension) {
        logger.info("Initializing CrossServerCoreProxyExtension API");

        proxyExtension = api_proxyExtension;

        loaded = true;
    }

    public static boolean isProxyExtensionEnabled() {
        return proxyExtension.isProxyExtensionEnabled();
    }

    public static void transferPlayer(ISyncPlayer player, ISyncServer server){
        proxyExtension.transferPlayer(player, server);
    }

    public static boolean isLoaded() {
        return loaded;
    }
}
