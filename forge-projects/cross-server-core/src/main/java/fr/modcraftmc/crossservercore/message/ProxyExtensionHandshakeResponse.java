package fr.modcraftmc.crossservercore.message;

import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.api.message.BaseMessage;

@AutoRegister("proxy_extension_handshake_response")
public class ProxyExtensionHandshakeResponse extends BaseMessage {

    ProxyExtensionHandshakeResponse() {}

    @Override
    public void handle() {
        CrossServerCore.getCrossServerCoreProxyExtension().enable();
    }
}
