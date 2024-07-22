package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServerProxy;

import java.lang.reflect.Type;

public class ISyncServerProxySerializer extends FieldSerializer<ISyncServerProxy> {
    @Override
    public JsonElement serialize(ISyncServerProxy value) {
        return new JsonPrimitive(value.getName());
    }

    @Override
    public ISyncServerProxy deserialize(JsonElement json, Type typeOfT) {
        return CrossServerCore.getServerCluster().getImmediateServer(json.getAsString());
    }

    @Override
    public Type getType() {
        return ISyncServerProxy.class;
    }
}
