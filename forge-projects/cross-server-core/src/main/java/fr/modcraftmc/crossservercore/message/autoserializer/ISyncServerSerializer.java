package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncServer;

import java.lang.reflect.Type;

public class ISyncServerSerializer extends FieldSerializer<ISyncServer> {


    @Override
    public JsonElement serialize(ISyncServer value) {
        return new JsonPrimitive(value.getName());
    }

    @Override
    public ISyncServer deserialize(JsonElement json, Type type) {
        return CrossServerCore.getServerCluster().getServer(json.getAsString()).orElseThrow();
    }

    @Override
    public Type getType() {
        return ISyncServer.class;
    }
}
