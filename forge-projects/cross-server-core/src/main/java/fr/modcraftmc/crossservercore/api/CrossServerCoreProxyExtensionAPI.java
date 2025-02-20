package fr.modcraftmc.crossservercore.api;

import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;

public abstract class CrossServerCoreProxyExtensionAPI {
    protected static boolean loaded = false;

    protected static CrossServerCoreProxyExtensionAPI instance;
    public ICrossServerCoreProxyExtension proxyExtension;

    public static boolean isEnable() {
        return instance.proxyExtension.isEnable();
    }

    public static void transferPlayer(ISyncPlayer player, ISyncServer server){
        instance.proxyExtension.transferPlayer(player, server);
    }

    public static boolean isLoaded() {
        return loaded;
    }
}
