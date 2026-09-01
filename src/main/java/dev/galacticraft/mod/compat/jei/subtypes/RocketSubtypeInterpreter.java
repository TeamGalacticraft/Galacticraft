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

package dev.galacticraft.mod.compat.jei.subtypes;

import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.api.rocket.RocketPrefabs;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;

public class RocketSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    public static final RocketSubtypeInterpreter INSTANCE = new RocketSubtypeInterpreter();

    @Override
    public DataComponentPatch getSubtypeData(ItemStack itemStack, UidContext context) {
        if (context == UidContext.Recipe) return DataComponentPatch.EMPTY;
        return DataComponentPatch.builder()
                .set(GCDataComponents.CREATIVE, itemStack.getOrDefault(GCDataComponents.CREATIVE, false))
                .set(GCDataComponents.ROCKET_DATA, itemStack.getOrDefault(GCDataComponents.ROCKET_DATA, RocketPrefabs.MISSING))
                .build();
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
        return "";
    }
}