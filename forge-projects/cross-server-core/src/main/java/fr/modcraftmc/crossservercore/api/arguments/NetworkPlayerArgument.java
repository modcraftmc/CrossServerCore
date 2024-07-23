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

public class NetworkPlayerArgument {

    public static String getNetworkPlayerName(CommandContext<?> context, String player) {
        return context.getArgument(player, String.class);
    }

    public static ISyncPlayer getNetworkPlayer(CommandContext<?> context, String player) {
        return CrossServerCoreAPI.getPlayer(context.getArgument(player, String.class)).orElseThrow();
    }

    public static <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder suggestionsBuilder) {
        for (ISyncPlayer player : CrossServerCoreAPI.getAllPlayersOnCluster()) {
            suggestionsBuilder.suggest(player.getName());
        }

        return suggestionsBuilder.buildFuture();
    }
}
