package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;
import fr.modcraftmc.crossservercore.networkdiscovery.SyncServer;

import java.lang.reflect.Type;

public class SyncServerSerializer extends FieldSerializer<SyncServer> {
    @Override
    public JsonElement serialize(SyncServer value) {
        return new JsonPrimitive(value.getName());
    }

    @Override
    public SyncServer deserialize(JsonElement json, Type typeOfT) {
        return CrossServerCore.getServerCluster().getServer(json.getAsString()).orElseThrow();
    }

    @Override
    public Type getType() {
        return SyncServer.class;
    }
}
