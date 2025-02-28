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

package dev.galacticraft.mod.screen.slot;

import com.mojang.datafixers.util.Pair;
import dev.galacticraft.api.item.Accessory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class AccessorySlot extends Slot {
    private final Predicate<ItemStack> stackPredicate;
    private final Pair<ResourceLocation, ResourceLocation> icon;

    public AccessorySlot(Container inventory, int index, int x, int y, Class<? extends Accessory> clazz, ResourceLocation icon) {
        super(inventory, index, x, y);
        this.stackPredicate = itemStack -> itemStack.getItem().getClass().equals(clazz);
        this.icon = Pair.of(InventoryMenu.BLOCK_ATLAS, icon);
    }

    public AccessorySlot(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.stackPredicate = itemStack -> itemStack.getItem() instanceof Accessory;
        this.icon = null;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stackPredicate.test(stack);
    }

    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return this.icon;
    }
}
