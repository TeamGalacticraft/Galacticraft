/*
 * Copyright (c) 2019-2021 Team Galacticraft
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
import dev.galacticraft.mod.item.GalacticraftItem;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class DefaultFabricationDisplay extends BasicDisplay {

    protected DefaultFabricationDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<Identifier> location) {
        super(inputs, outputs, location);
    }

    public DefaultFabricationDisplay(@Nullable FabricationRecipe recipe) {
        super(getInputs(recipe), recipe == null ? Collections.emptyList() : Collections.singletonList(EntryIngredients.of(recipe.getOutput())));
    }


    public static DefaultFabricationDisplay createRaw(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<Identifier> location) {
        return new DefaultFabricationDisplay(inputs, outputs, location);
    }

    @Override
    public CategoryIdentifier<? extends DefaultFabricationDisplay> getCategoryIdentifier() {
        return GalacticraftREIServerPlugin.CIRCUIT_FABRICATION;
    }

    private static List<EntryIngredient> getInputs(@Nullable FabricationRecipe recipe) {
        if (recipe == null) return Collections.emptyList();
        List<EntryIngredient> list = new ArrayList<>(5);
        list.add(EntryIngredients.of(Items.DIAMOND));
        list.add(EntryIngredients.of(GalacticraftItem.RAW_SILICON));
        list.add(EntryIngredients.of(GalacticraftItem.RAW_SILICON));
        list.add(EntryIngredients.of(Items.REDSTONE));
        list.add(EntryIngredients.ofIngredient(recipe.getIngredients().get(0)));
        return list;
    }
}