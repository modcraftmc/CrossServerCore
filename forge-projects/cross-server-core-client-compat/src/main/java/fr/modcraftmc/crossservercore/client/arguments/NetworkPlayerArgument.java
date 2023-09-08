package fr.modcraftmc.crossservercore.client.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.modcraftmc.crossservercore.client.CrossServerCoreClient;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if(CrossServerCoreClient.playersOnCluster == null)
            return Suggestions.empty();
        return context.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggest(CrossServerCoreClient.playersOnCluster, builder) : Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("Player");
    }
}
