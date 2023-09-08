package fr.modcraftmc.crossservercore.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;

public class NetworkPlayerArgument implements ArgumentType<String> {
    public NetworkPlayerArgument() {
    }

    public static NetworkPlayerArgument networkPlayer() {
        return new NetworkPlayerArgument();
    }

    public static String getNetworkPlayer(CommandContext<CommandSourceStack> context, String player) {
        return context.getArgument(player, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }
}
