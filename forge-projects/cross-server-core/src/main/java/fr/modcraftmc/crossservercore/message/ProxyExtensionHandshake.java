package fr.modcraftmc.crossservercore.message;

import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.annotation.AutoSerialize;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;

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
        // handle is on proxy side
    }
}
