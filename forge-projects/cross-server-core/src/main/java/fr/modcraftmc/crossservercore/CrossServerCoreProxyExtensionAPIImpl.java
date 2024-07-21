package fr.modcraftmc.crossservercore;

import fr.modcraftmc.crossservercore.api.CrossServerCoreProxyExtensionAPI;
import fr.modcraftmc.crossservercore.api.ICrossServerCoreProxyExtension;

public class CrossServerCoreProxyExtensionAPIImpl extends CrossServerCoreProxyExtensionAPI {
    public CrossServerCoreProxyExtensionAPIImpl(ICrossServerCoreProxyExtension api_proxyExtension) {
        CrossServerCore.LOGGER.info("Initializing CrossServerCoreProxyExtension API");

        proxyExtension = api_proxyExtension;

        instance = this;
        loaded = true;
    }
}
