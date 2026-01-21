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

package dev.galacticraft.mod.compat.rei.common.display;

import dev.galacticraft.mod.compat.rei.common.GalacticraftREIServerPlugin;
import dev.galacticraft.mod.recipe.RocketRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DefaultRocketDisplay extends BasicDisplay {
    public static final BasicDisplay.Serializer<DefaultRocketDisplay> SERIALIZER = BasicDisplay.Serializer.of(
            (inputs, outputs, id, tag) -> {
                return new DefaultRocketDisplay(inputs, outputs, id, tag.getInt("BodyHeight"), tag.getBoolean("HasBoosters"));
            },
            (display, tag) -> {
                tag.putInt("BodyHeight", display.bodyHeight);
                tag.putBoolean("HasBoosters", display.hasBoosters);
            }
    );

    public final int bodyHeight;
    public final boolean hasBoosters;

    protected DefaultRocketDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location, int bodyHeight, boolean hasBoosters) {
        super(inputs, outputs, location);
        this.bodyHeight = bodyHeight;
        this.hasBoosters = hasBoosters;
    }

    public DefaultRocketDisplay(@Nullable RecipeHolder<RocketRecipe> recipe) {
        super(getInputs(recipe), recipe == null ? Collections.emptyList() : Collections.singletonList(EntryIngredients.of(recipe.value().getResultItem(registryAccess()))));
        if (recipe != null) {
            this.bodyHeight = recipe.value().bodyHeight();
            this.hasBoosters = !recipe.value().boosters().isEmpty();
        } else {
            this.bodyHeight = 0;
            this.hasBoosters = false;
        }
    }

    @Override
    public CategoryIdentifier<? extends DefaultRocketDisplay> getCategoryIdentifier() {
        return GalacticraftREIServerPlugin.ROCKET;
    }

    private static List<EntryIngredient> getInputs(@Nullable RecipeHolder<RocketRecipe> recipe) {
        if (recipe == null) return Collections.emptyList();
        RocketRecipe rocketRecipe = recipe.value();
        List<EntryIngredient> list = new ArrayList<>();
        list.add(EntryIngredients.ofIngredient(rocketRecipe.cone()));
        for (int i = 0; i < 2 * rocketRecipe.bodyHeight(); i++) {
            list.add(EntryIngredients.ofIngredient(rocketRecipe.body()));
        }
        if (!rocketRecipe.boosters().isEmpty()) {
            for (int i = 0; i < 2; i++) {
                list.add(EntryIngredients.ofIngredient(rocketRecipe.boosters()));
            }
        }
        for (int i = 0; i < 4; i++) {
            list.add(EntryIngredients.ofIngredient(rocketRecipe.fins()));
        }
        list.add(EntryIngredients.ofIngredient(rocketRecipe.engine()));
        list.add(EntryIngredients.ofIngredient(rocketRecipe.storage()));
        return list;
    }
}