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

package dev.galacticraft.mod.compat.jei.replacers;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.item.EmergencyKitItem;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.content.item.ParachuteItem;
import dev.galacticraft.mod.tag.GCItemTags;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.List;

public final class EmergencyKitRecipeMaker {
    private static final String group = "jei.emergency_kit";

    public static List<RecipeHolder<CraftingRecipe>> createRecipes() {
        List<ItemStack> emergencyItems = EmergencyKitItem.getContents(DyeColor.RED);
        NonNullList<Ingredient> inputs = NonNullList.withSize(9, Ingredient.EMPTY);
        for (int i = 0; i < 9; ++i) {
            if (emergencyItems.get(i).getItem() instanceof ParachuteItem) {
                inputs.set(i, Ingredient.of(GCItemTags.PARACHUTES));
            } else {
                inputs.set(i, Ingredient.of(emergencyItems.get(i)));
            }
        }

        ItemStack output = GCItems.EMERGENCY_KIT.getDefaultInstance();
        CraftingRecipe recipe = new ShapelessRecipe(group, CraftingBookCategory.MISC, output, inputs);
        return List.of(new RecipeHolder<>(Constant.id(group), recipe));
    }

    private EmergencyKitRecipeMaker() {

    }
}