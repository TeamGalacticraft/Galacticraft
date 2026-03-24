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

package dev.galacticraft.mod.compat.rei.common.display;

import dev.galacticraft.mod.content.item.FlagItem;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FlagDisplayGenerator implements DynamicDisplayGenerator<DefaultCraftingDisplay<?>> {
    @Override
    public Optional<List<DefaultCraftingDisplay<?>>> getRecipeFor(EntryStack<?> entry) {
        if (entry.getValue() instanceof ItemStack stack && FlagItem.isFlagItem(stack)) {
            return Optional.of(Collections.singletonList(new DefaultFlagDisplay(stack)));
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<DefaultCraftingDisplay<?>>> getUsageFor(EntryStack<?> entry) {
        if (entry.getValue() instanceof ItemStack stack && stack.getItem() instanceof BannerItem) {
            return Optional.of(Collections.singletonList(DefaultFlagDisplay.fromBanner(stack)));
        }
        return Optional.empty();
    }
}
