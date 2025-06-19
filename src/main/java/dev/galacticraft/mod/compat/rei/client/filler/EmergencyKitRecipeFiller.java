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

package dev.galacticraft.mod.compat.rei.client.filler;

import dev.galacticraft.mod.content.item.EmergencyKitItem;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.EmergencyKitRecipe;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.client.categories.crafting.filler.CraftingRecipeFiller;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomShapelessDisplay;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.List;

public class EmergencyKitRecipeFiller implements CraftingRecipeFiller<EmergencyKitRecipe> {
    @Override
    public Collection<Display> apply(RecipeHolder<EmergencyKitRecipe> recipe) {
        return List.of(new DefaultCustomShapelessDisplay(recipe,
                EmergencyKitItem.getContents().stream()
                        .map(itemStack -> EntryIngredients.of(itemStack))
                        .collect(Collectors.toList()),
                List.of(EntryIngredients.of(GCItems.EMERGENCY_KIT)))
        );
    }
    
    @Override
    public Class<EmergencyKitRecipe> getRecipeClass() {
        return EmergencyKitRecipe.class;
    }
}