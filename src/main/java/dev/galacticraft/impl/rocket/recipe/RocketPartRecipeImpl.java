/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.impl.rocket.recipe;

import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.api.rocket.recipe.config.RocketPartRecipeConfig;
import dev.galacticraft.api.rocket.recipe.type.RocketPartRecipeType;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record RocketPartRecipeImpl<C extends RocketPartRecipeConfig, T extends RocketPartRecipeType<C>>(T type, C config) implements RocketPartRecipe<C, T> {
    @Override
    public int slots() {
        return this.type.slots(this.config);
    }

    @Override
    public int height() {
        return this.type.height(this.config);
    }

    @Override
    public void place(@NotNull RocketPartRecipeType.SlotConsumer consumer, int leftEdge, int rightEdge, int bottomEdge) {
        this.type.place(consumer, leftEdge, rightEdge, bottomEdge, this.config);
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.type.ingredients(this.config);
    }

    @Override
    public boolean matches(Container container, Level level) {
        return this.type.matches(container, level, this.config);
    }

    @Override
    public @NotNull ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        throw new AssertionError();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        throw new AssertionError();
    }
}
