package fr.modcraftmc.crossservercore.message.autoserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.modcraftmc.crossservercore.api.message.autoserializer.FieldSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

import java.lang.reflect.Type;

public class CompoundTagSerializer extends FieldSerializer<CompoundTag> {
    @Override
    public JsonElement serialize(CompoundTag value) {
        return new JsonPrimitive(NbtUtils.structureToSnbt(value));
    }

    @Override
    public CompoundTag deserialize(JsonElement json, Type typeOfT) {
        try {
            return NbtUtils.snbtToStructure(json.getAsString());
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Type getType() {
        return CompoundTag.class;
    }
}
