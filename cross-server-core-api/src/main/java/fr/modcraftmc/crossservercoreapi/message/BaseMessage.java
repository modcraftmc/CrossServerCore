package fr.modcraftmc.crossservercoreapi.message;

import com.google.gson.JsonObject;

public abstract class BaseMessage {
    private String messageName;

    public BaseMessage(String messageName) {
        this.messageName = messageName;
    }
    protected JsonObject serialize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("messageName", messageName);
        return jsonObject;
    }

    public String serializeToString() {
        return serialize().toString();
    }

    public abstract String getMessageName();

    public abstract void handle();
}
