package dev.galacticraft.mod.world.ships;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NameSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        for (String item : BlockGroupRenderer.groups.keySet()) {
            if (item.startsWith(builder.getRemaining())) {
                builder.suggest(item);
            }
        }
        return builder.buildFuture();
    }
}
