package fr.modcraftmc.crossservercoreproxyextension.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercoreproxyextension.CrossServerCoreProxy;
import fr.modcraftmc.crossservercoreproxyextension.annotation.AutoRegister;

public abstract class BaseMessage {
    public JsonObject serialize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("messageName", tryGetAutoMessageName(this));
        CrossServerCoreProxy.instance.getMessageAutoPropertySerializer().serializeAutoproperty(jsonObject, this);
        return jsonObject;
    }

    private static <T extends BaseMessage> String tryGetAutoMessageName(T message) {
        try {
            return message.getClass().getAnnotation(AutoRegister.class).value();
        } catch (NullPointerException e) {
            return message.getMessageName();
        }
    }

    public String getMessageName() {
        return "base_message";
    }

    public abstract void handle();
}
