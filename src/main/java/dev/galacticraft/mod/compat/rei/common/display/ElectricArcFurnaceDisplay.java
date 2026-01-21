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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.compat.rei.common.GalacticraftREIServerPlugin;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.BlastingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ElectricArcFurnaceDisplay extends BasicDisplay {
    public static final BasicDisplay.Serializer<ElectricArcFurnaceDisplay> SERIALIZER = BasicDisplay.Serializer.of(
            (inputs, outputs, id, tag) -> {
                return new ElectricArcFurnaceDisplay(inputs, outputs, id, tag.getInt(Constant.Nbt.PROCESSING_TIME), tag.getFloat(Constant.Nbt.XP));
            },
            (display, tag) -> {
                tag.putInt(Constant.Nbt.PROCESSING_TIME, display.getProcessingTime());
                tag.putFloat(Constant.Nbt.XP, display.getXp());
            }
    );

    private int processingTime;
    private float xp;

    protected ElectricArcFurnaceDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location, int processingTime, float xp) {
        super(inputs, outputs, location);
        this.processingTime = processingTime;
        this.xp = xp;
    }

    public ElectricArcFurnaceDisplay(@Nullable RecipeHolder<BlastingRecipe> recipe) {
        super(getInputs(recipe), recipe == null ? Collections.emptyList() : Collections.singletonList(
                EntryIngredients.ofItemStacks(List.of(recipe.value().getResultItem(registryAccess()),
                        recipe.value().getResultItem(registryAccess()).copyWithCount(recipe.value().getResultItem(registryAccess()).getCount() * 2)
                ))
        ));
        this.processingTime = (int) (recipe.value().getCookingTime() / 1.5F);
        this.xp = recipe.value().getExperience();
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    public float getXp() {
        return this.xp;
    }

    @Override
    public CategoryIdentifier<? extends ElectricArcFurnaceDisplay> getCategoryIdentifier() {
        return GalacticraftREIServerPlugin.ELECTRIC_BLASTING;
    }

    private static List<EntryIngredient> getInputs(@Nullable RecipeHolder<BlastingRecipe> recipe) {
        if (recipe == null) return Collections.emptyList();
        int n = recipe.value().getIngredients().size();
        List<EntryIngredient> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(EntryIngredients.ofIngredient(recipe.value().getIngredients().get(i)));
        }
        return list;
    }
}