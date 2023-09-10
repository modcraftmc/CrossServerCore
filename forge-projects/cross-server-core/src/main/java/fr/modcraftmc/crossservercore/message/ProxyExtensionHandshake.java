package fr.modcraftmc.crossservercore.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercoreapi.message.BaseMessage;

public class ProxyExtensionHandshake extends BaseMessage {
    public static final String MESSAGE_NAME = "proxy_extension_handshake";

    public final String serverName;

    public ProxyExtensionHandshake(String serverName) {
        super(MESSAGE_NAME);
        this.serverName = serverName;
    }

    @Override
    protected JsonObject serialize() {
        JsonObject jsonObject = super.serialize();
        jsonObject.addProperty("serverName", serverName);
        return jsonObject;
    }

    public static ProxyExtensionHandshake deserialize(JsonObject json) {
        String serverName = json.get("serverName").getAsString();
        return new ProxyExtensionHandshake(serverName);
    }

    @Override
    public String getMessageName() {
        return MESSAGE_NAME;
    }

    @Override
    public void handle() {
        // handle is on proxy side
    }
}
