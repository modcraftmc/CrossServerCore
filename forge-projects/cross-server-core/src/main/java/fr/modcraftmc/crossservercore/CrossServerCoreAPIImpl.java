package fr.modcraftmc.crossservercore;

import fr.modcraftmc.crossservercore.api.CrossServerCoreAPI;
import fr.modcraftmc.crossservercore.api.dataintegrity.ISecurityWatcher;
import fr.modcraftmc.crossservercore.api.message.IMessageHandler;
import fr.modcraftmc.crossservercore.api.message.autoserializer.IMessageAutoPropertySerializer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.IServerCluster;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;

public class CrossServerCoreAPIImpl extends CrossServerCoreAPI {
    public CrossServerCoreAPIImpl(ISyncServer api_server, IServerCluster api_serverCluster, IMessageHandler api_messageHandler, IMessageAutoPropertySerializer api_messageAutoPropertySerializer, ISecurityWatcher api_securityWatcher) {
        CrossServerCore.LOGGER.info("Initializing cross-server-core api");

        server = api_server;
        serverCluster = api_serverCluster;
        messageHandler = api_messageHandler;
        messageAutoPropertySerializer = api_messageAutoPropertySerializer;
        securityWatcher = api_securityWatcher;

        instance = this;
        loaded = true;
    }
}
