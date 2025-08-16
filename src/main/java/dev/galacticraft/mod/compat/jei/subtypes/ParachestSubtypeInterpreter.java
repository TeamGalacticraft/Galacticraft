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

package dev.galacticraft.mod.compat.jei.subtypes;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class ParachestSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    public static final ParachestSubtypeInterpreter INSTANCE = new ParachestSubtypeInterpreter();

    @Override
    public String getSubtypeData(ItemStack itemStack, UidContext context) {
        if (context == UidContext.Recipe) return "";
        DyeColor color = itemStack.get(DataComponents.BASE_COLOR);
        return color != null ? color.getName() : "";
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
        return "";
    }
}