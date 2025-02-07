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

package dev.galacticraft.mod.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FabricationRecipe implements Recipe<RecipeInput> {
    private final String group;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients = NonNullList.withSize(1, Ingredient.EMPTY);
    private final int time;

    public FabricationRecipe(String group, Ingredient ingredients, ItemStack result, int time) {
        this.group = group;
        this.ingredients.set(0, ingredients);
        this.result = result;
        this.time = time;
    }

    @Override
    public boolean matches(RecipeInput input, Level world) {
        return this.ingredients.get(0).test(input.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput input, HolderLookup.Provider lookup) {
        return this.getResultItem(lookup).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 0;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return this.result;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return GCRecipes.FABRICATION_TYPE;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return GCRecipes.FABRICATION_SERIALIZER;
    }

    @Override
    public @NotNull String getGroup() {
        return this.group;
    }

    public int getProcessingTime() {
        return this.time;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<FabricationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<FabricationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredients.get(0)),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Codec.INT.optionalFieldOf("time", 300).forGetter(recipe -> recipe.time)
        ).apply(instance, FabricationRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FabricationRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                r -> r.group,
                Ingredient.CONTENTS_STREAM_CODEC,
                r -> r.ingredients.get(0),
                ItemStack.STREAM_CODEC,
                r -> r.result,
                ByteBufCodecs.INT,
                r -> r.time,
                FabricationRecipe::new
        );

        @Override
        public @NotNull MapCodec<FabricationRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, FabricationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}