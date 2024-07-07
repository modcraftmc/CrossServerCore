package fr.modcraftmc.crossservercore.api.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;

public abstract class BaseMessage {
    public JsonObject serialize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("messageName", tryGetAutoMessageName(this));
        CrossServerCore.getMessageAutoPropertySerializer().serializeAutoproperty(jsonObject, this);
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
