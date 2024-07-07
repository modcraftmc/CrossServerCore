package fr.modcraftmc.crossservercoreproxyextension.message;

import fr.modcraftmc.crossservercoreproxyextension.annotation.AutoRegister;

@AutoRegister("proxy_extension_handshake_response")
public class ProxyExtensionHandshakeResponse extends BaseMessage {

    public ProxyExtensionHandshakeResponse() {}

    @Override
    public void handle() { }
}
