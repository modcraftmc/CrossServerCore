package fr.modcraftmc.crossservercore.api.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.lang.reflect.Type;

public class ComponentSerializer extends FieldSerializer<Component> {
    @Override
    public JsonElement serialize(Component value) {
        RegistryAccess access = ServerLifecycleHooks.getCurrentServer().registryAccess();
        return new JsonPrimitive(Component.Serializer.toJson(value, access));
    }

    @Override
    public Component deserialize(JsonElement json, Type typeOfT) {
        RegistryAccess access = ServerLifecycleHooks.getCurrentServer().registryAccess();
        return Component.Serializer.fromJson(json.getAsString(), access);
    }

    @Override
    public Type getType() {
        return Component.class;
    }
}
