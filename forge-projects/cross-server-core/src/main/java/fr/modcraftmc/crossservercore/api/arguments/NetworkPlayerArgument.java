package fr.modcraftmc.crossservercore.api.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class NetworkPlayerArgument implements ArgumentType<String> {
    public NetworkPlayerArgument() {
    }

    public static NetworkPlayerArgument networkPlayer() {
        return new NetworkPlayerArgument();
    }

    public static String getNetworkPlayer(CommandContext<?> context, String player) {
        return context.getArgument(player, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }
}
