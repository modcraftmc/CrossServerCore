package fr.modcraftmc.crossservercore.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercoreapi.message.BaseMessage;

public class ProxyExtensionHandshakeResponse extends BaseMessage {
    public static final String MESSAGE_NAME = "proxy_extension_handshake_response";

    public ProxyExtensionHandshakeResponse() {
        super(MESSAGE_NAME);
    }

    public static ProxyExtensionHandshakeResponse deserialize(JsonObject json) {
        return new ProxyExtensionHandshakeResponse();
    }

    @Override
    public String getMessageName() {
        return MESSAGE_NAME;
    }

    @Override
    public void handle() {
        CrossServerCore.getCrossServerCoreProxyExtension().enable();
    }
}
