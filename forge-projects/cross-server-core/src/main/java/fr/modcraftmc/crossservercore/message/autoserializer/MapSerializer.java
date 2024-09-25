package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapSerializer extends FieldSerializer<Map> {
    @Override
    public JsonElement serialize(Map value) {
        JsonArray jsonArray = new JsonArray();
        Set<Map.Entry> entries = value.entrySet();
        for (Map.Entry entry : entries) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("key", CrossServerCore.getMessageAutoPropertySerializer().serializeObject(entry.getKey()));
            jsonObject.add("value", CrossServerCore.getMessageAutoPropertySerializer().serializeObject(entry.getValue()));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public Map deserialize(JsonElement json, Type type) {
        JsonArray jsonArray = json.getAsJsonArray();
        Map map = new java.util.HashMap();

        if(!(type instanceof ParameterizedType)) throw new IllegalArgumentException("Type is not a parameterized type");
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type keyGenericType = parameterizedType.getActualTypeArguments()[0];
        Type valueGenericType = parameterizedType.getActualTypeArguments()[1];


        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Object key = CrossServerCore.getMessageAutoPropertySerializer().deserializeObject(jsonObject.get("key"), keyGenericType);
            Object value = CrossServerCore.getMessageAutoPropertySerializer().deserializeObject(jsonObject.get("value"), valueGenericType);

            map.put(key, value);
        }

        return map;
    }

    @Override
    public Type getType() {
        return Map.class;
    }
}
