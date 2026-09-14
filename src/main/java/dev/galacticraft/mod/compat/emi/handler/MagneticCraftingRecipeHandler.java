/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.compat.emi.handler;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.galacticraft.mod.screen.MagneticCraftingTableMenu;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class MagneticCraftingRecipeHandler implements StandardRecipeHandler<MagneticCraftingTableMenu> {
    @Override
    public List<Slot> getInputSources(MagneticCraftingTableMenu menu) {
        List<Slot> slots = this.getCraftingSlots(menu);
        for (int slot = 10; slot < 46; ++slot) {
            slots.add(menu.getSlot(slot));
        }
        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(MagneticCraftingTableMenu menu) {
        List<Slot> slots = new ArrayList<>();
        for (int slot = 1; slot < 10; ++slot) {
            slots.add(menu.getSlot(slot));
        }
        return slots;
    }

    @Override
    public Slot getOutputSlot(MagneticCraftingTableMenu menu) {
        return menu.getSlot(MagneticCraftingTableMenu.RESULT_SLOT);
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING && recipe.supportsRecipeTree();
    }
}
