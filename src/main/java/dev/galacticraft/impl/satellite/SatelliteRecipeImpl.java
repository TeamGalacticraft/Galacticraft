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

package dev.galacticraft.impl.satellite;

import dev.galacticraft.api.satellite.SatelliteRecipe;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class SatelliteRecipeImpl implements SatelliteRecipe {
    private final Int2ObjectMap<Ingredient> ingredients;

    public SatelliteRecipeImpl(Int2ObjectMap<Ingredient> list) {
        this.ingredients = list;
    }

    @Override
    public Int2ObjectMap<Ingredient> ingredients() {
        return ingredients;
    }

    @Override
    public boolean test(@NotNull Container inventory) {
        int invSize = inventory.getContainerSize();
        if (invSize == 0) return false;
        
        IntArrayList slotModifiers = new IntArrayList();
        slotModifiers.size(invSize);

        for (Int2ObjectMap.Entry<Ingredient> ingredient : this.ingredients.int2ObjectEntrySet()) {
            int amount = ingredient.getIntKey();
            for (int i = 0; i < invSize; i++) {
                ItemStack stack = inventory.getItem(i);
                if (ingredient.getValue().test(stack)) {
                    amount -= (stack.getCount() - slotModifiers.getInt(i));
                    slotModifiers.set(i, stack.getCount() - Math.min(amount, 0));
                    if (amount <= 0) break;
                }
            }
            if (amount > 0) return false;
        }
        return true;
    }
}
