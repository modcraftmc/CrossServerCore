package fr.modcraftmc.crossservercore.api.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.modcraftmc.crossservercore.api.CrossServerCoreAPI;
import fr.modcraftmc.crossservercore.api.networkdiscovery.ISyncPlayer;

import java.util.concurrent.CompletableFuture;

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

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder suggestionsBuilder) {
        for (ISyncPlayer player : CrossServerCoreAPI.getAllPlayersOnCluster()) {
            suggestionsBuilder.suggest(player.getName());
        }

        return suggestionsBuilder.buildFuture();
    }
}
