package fr.modcraftmc.crossservercoreproxyextension.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercoreproxyextension.CrossServerCoreProxy;
import fr.modcraftmc.crossservercoreproxyextension.annotation.AutoRegister;
import fr.modcraftmc.crossservercoreproxyextension.annotation.AutoSerialize;

@AutoRegister("proxy_extension_handshake")
public class ProxyExtensionHandshake extends BaseMessage {

    @AutoSerialize
    public String serverName;

    ProxyExtensionHandshake() {}

    public ProxyExtensionHandshake(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void handle() {
        CrossServerCoreProxy.instance.getProxyServer().getServer(serverName).ifPresent(server -> {
            CrossServerCoreProxy.instance.sendMessageToServer(new ProxyExtensionHandshakeResponse(), server.getServerInfo().getName());
        });
    }
}
