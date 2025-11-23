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

package dev.galacticraft.mod.compat.emi.handler;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.galacticraft.mod.screen.RocketWorkbenchMenu;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class RocketRecipeHandler implements StandardRecipeHandler<RocketWorkbenchMenu> {
    private final EmiRecipeCategory category;

    public RocketRecipeHandler(EmiRecipeCategory category) {
        this.category = category;
    }

    @Override
    public List<Slot> getInputSources(RocketWorkbenchMenu handler) {
        List<Slot> list = this.getCraftingSlots(handler);
        // Skip the output slot
        int invStart = list.size() + 1;
        // Add inventory + hotbar slots
        for (int i = invStart; i < invStart + 36; i++) {
            list.add(handler.getSlot(i));
        }
        return list;
    }

    @Override
    public List<Slot> getCraftingSlots(RocketWorkbenchMenu handler) {
        List<Slot> list = new ArrayList<>();
        // Less than or equal to to include the chest slot
        for (int i = 0; i <= handler.getRecipeSize(); i++) {
            list.add(handler.getSlot(i));
        }
        return list;
    }

    @Override
    public Slot getOutputSlot(RocketWorkbenchMenu handler) {
        return handler.getSlot(handler.getRecipeSize() + 1);
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory() == category && recipe.supportsRecipeTree();
    }
}