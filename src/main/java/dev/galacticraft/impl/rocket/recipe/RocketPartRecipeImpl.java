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

import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartTypes;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipeSlot;
import dev.galacticraft.api.rocket.recipe.config.RocketPartRecipeConfig;
import dev.galacticraft.api.rocket.recipe.type.RocketPartRecipeType;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RocketPartRecipeImpl<C extends RocketPartRecipeConfig, T extends RocketPartRecipeType<C>>(T type, C config) implements RocketPartRecipe<C, T> {
    @Override
    public RocketPartTypes partType() {
        return this.type().partType(this.config);
    }

    @Override
    public int width() {
        return this.type().width(this.config);
    }

    @Override
    public int height() {
        return this.type().height(this.config);
    }

    @Override
    public @NotNull List<RocketPartRecipeSlot> slots() {
        return this.type().slots(this.config);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(this.slots().size());
        for (RocketPartRecipeSlot slot : this.slots()) {
            ingredients.add(slot.ingredient());
        }
        return ingredients;
    }

    @Override
    public Holder.Reference<? extends RocketPart<?, ?>> output() {
        return this.type().output(this.config);
    }
}
