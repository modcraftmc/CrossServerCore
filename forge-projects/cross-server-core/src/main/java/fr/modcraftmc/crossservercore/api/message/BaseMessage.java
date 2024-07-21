package fr.modcraftmc.crossservercore.api.message;

import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.annotation.AutoRegister;
import fr.modcraftmc.crossservercore.message.NoopMessage;

public abstract class BaseMessage {
    public JsonObject serialize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("messageName", getMessageName());
        CrossServerCore.getMessageAutoPropertySerializer().serializeAutoproperty(jsonObject, this);
        return jsonObject;
    }

    private static <T extends BaseMessage> String tryGetAutoMessageName(T message) {
        try {
            return message.getClass().getAnnotation(AutoRegister.class).value();
        } catch (NullPointerException e) {
            return NoopMessage.MESSAGE_NAME;
        }
    }

    public String getMessageName() {
        return tryGetAutoMessageName(this);
    }

    public abstract void handle();
}
