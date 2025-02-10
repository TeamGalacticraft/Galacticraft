/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.impl.command.argument;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.galacticraft.api.registry.AddonRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RegistryArgumentType<T> implements ArgumentType<Registry<T>> {
    private static final ImmutableList<String> EXAMPLES = ImmutableList.of(
            Registries.BLOCK.location().toString(),
            Registries.ITEM.location().toString(),
            Registries.ENTITY_TYPE.location().toString(),
            Registries.DIMENSION_TYPE.location().toString(),
            Registries.BIOME.location().toString(),
            Registries.SOUND_EVENT.location().toString(),
            AddonRegistries.CELESTIAL_BODY_TYPE.location().toString()
    );

    private RegistryArgumentType() {
    }

    @Contract(value = " -> new", pure = true)
    public static <T> @NotNull RegistryArgumentType<T> create() {
        return new RegistryArgumentType<>();
    }

    public static <T> @NotNull Registry<T> getRegistry(@NotNull CommandContext<CommandSourceStack> context, String id) {
        Registry<T> registry = context.getArgument(id, Registry.class);
        Optional<? extends Registry<T>> dynamic = context.getSource().registryAccess().registry(registry.key());
        return dynamic.isPresent() ? dynamic.get() : registry;
    }

    @Override
    public Registry<T> parse(StringReader reader) throws CommandSyntaxException {
        ResourceKey<Registry<T>> key = ResourceKey.createRegistryKey(ResourceLocation.read(reader));
        return ((Registry<Registry<T>>) BuiltInRegistries.REGISTRY).get(key);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(BuiltInRegistries.REGISTRY.keySet().stream(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
