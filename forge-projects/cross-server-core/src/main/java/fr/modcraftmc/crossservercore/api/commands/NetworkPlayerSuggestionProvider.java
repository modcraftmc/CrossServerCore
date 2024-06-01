package fr.modcraftmc.crossservercore.api.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.modcraftmc.crossservercore.api.CrossServerCoreAPI;
import net.minecraft.commands.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

public class NetworkPlayerSuggestionProvider implements SuggestionProvider<CommandSourceStack> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
        for (String player : CrossServerCoreAPI.instance.getPlayerLocationMap().keySet()) {
            suggestionsBuilder.suggest(player);
        }
        return suggestionsBuilder.buildFuture();
    }
}
