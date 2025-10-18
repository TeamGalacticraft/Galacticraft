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
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ElectricFurnaceDisplay extends BasicDisplay {
    private int processingTime = (int) (200 / 1.5F);
    private float xp = 0.0F;

    protected ElectricFurnaceDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location) {
        super(inputs, outputs, location);
    }

    public ElectricFurnaceDisplay(@Nullable RecipeHolder<SmeltingRecipe> recipe) {
        super(getInputs(recipe), recipe == null ? Collections.emptyList() : Collections.singletonList(EntryIngredients.of(recipe.value().getResultItem(registryAccess()))));
        this.processingTime = (int) (recipe.value().getCookingTime() / 1.5F);
        this.xp = recipe.value().getExperience();
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    public float getXp() {
        return this.xp;
    }

    public static ElectricFurnaceDisplay createRaw(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location) {
        return new ElectricFurnaceDisplay(inputs, outputs, location);
    }

    @Override
    public CategoryIdentifier<? extends ElectricFurnaceDisplay> getCategoryIdentifier() {
        return GalacticraftREIServerPlugin.ELECTRIC_SMELTING;
    }

    private static List<EntryIngredient> getInputs(@Nullable RecipeHolder<SmeltingRecipe> recipe) {
        if (recipe == null) return Collections.emptyList();
        int n = recipe.value().getIngredients().size();
        List<EntryIngredient> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(EntryIngredients.ofIngredient(recipe.value().getIngredients().get(i)));
        }
        return list;
    }

    public List<InputIngredient<EntryStack<?>>> getInputIngredients() {
        List<EntryIngredient> inputEntries = getInputEntries();
        int n = inputEntries.size();
        List<InputIngredient<EntryStack<?>>> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(InputIngredient.of(i, inputEntries.get(i)));
        }
        return list;
    }
}