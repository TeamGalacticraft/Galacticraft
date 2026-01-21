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
import dev.galacticraft.mod.recipe.FabricationRecipe;
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

public class DefaultFabricationDisplay extends BasicDisplay {
    public static final BasicDisplay.Serializer<DefaultFabricationDisplay> SERIALIZER = BasicDisplay.Serializer.of(
            (inputs, outputs, id, tag) -> {
                return new DefaultFabricationDisplay(inputs, outputs, id, tag.getInt(Constant.Nbt.PROCESSING_TIME));
            },
            (display, tag) -> {
                tag.putInt(Constant.Nbt.PROCESSING_TIME, display.getProcessingTime());
            }
    );

    private final int processingTime;

    protected DefaultFabricationDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location, int processingTime) {
        super(inputs, outputs, location);
        this.processingTime = processingTime;
    }

    public DefaultFabricationDisplay(@Nullable RecipeHolder<FabricationRecipe> recipe) {
        super(getInputs(recipe), recipe == null ? Collections.emptyList() : Collections.singletonList(EntryIngredients.of(recipe.value().getResultItem(registryAccess()))));
        this.processingTime = recipe.value().getProcessingTime();
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    @Override
    public CategoryIdentifier<? extends DefaultFabricationDisplay> getCategoryIdentifier() {
        return GalacticraftREIServerPlugin.FABRICATION;
    }

    private static List<EntryIngredient> getInputs(@Nullable RecipeHolder<FabricationRecipe> recipe) {
        if (recipe == null) return Collections.emptyList();
        int n = recipe.value().getIngredients().size();
        List<EntryIngredient> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(EntryIngredients.ofIngredient(recipe.value().getIngredients().get(i)));
        }
        return list;
    }
}