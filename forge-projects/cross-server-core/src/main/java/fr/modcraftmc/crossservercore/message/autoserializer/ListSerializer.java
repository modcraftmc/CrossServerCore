package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import fr.modcraftmc.crossservercore.CrossServerCore;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ListSerializer extends FieldSerializer<List> {

    @Override
    public JsonElement serialize(List value) {
        JsonArray jsonArray = new JsonArray();
        for (Object o : value) {
            jsonArray.add(CrossServerCore.getMessageAutoPropertySerializer().serializeObject(o));
        }
        return jsonArray;
    }

    @Override
    public List deserialize(JsonElement json, Type type) {
        JsonArray jsonArray = json.getAsJsonArray();
        List list = new java.util.ArrayList();

        if(!(type instanceof ParameterizedType)) throw new IllegalArgumentException("Type is not a parameterized type");
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type genericType = parameterizedType.getActualTypeArguments()[0];


        for (JsonElement jsonElement : jsonArray) {
            list.add(CrossServerCore.getMessageAutoPropertySerializer().deserializeObject(jsonElement, genericType));
        }
        return list;
    }

    @Override
    public Type getType() {
        return List.class;
    }
}
