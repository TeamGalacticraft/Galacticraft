/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.compat.rei.common.transfer.info;

import dev.galacticraft.machinelib.api.block.entity.RecipeMachineBlockEntity;
import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.machinelib.impl.compat.vanilla.StorageSlot;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoContext;
import me.shedaniel.rei.api.common.transfer.info.clean.InputCleanHandler;
import me.shedaniel.rei.api.common.transfer.info.simple.DumpHandler;
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleGridMenuInfo;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import me.shedaniel.rei.api.common.transfer.info.stack.VanillaSlotAccessor;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.ArrayList;
import java.util.List;

public record SimpleMachineMenuInfo<I extends RecipeInput, R extends Recipe<I>, B extends RecipeMachineBlockEntity<I, R>, T extends RecipeMachineMenu<I, R, B>, D extends SimpleGridMenuDisplay>(int width, int height, int resultIndex, D display) implements SimpleGridMenuInfo<T, D> {
    @Override
    public Iterable<SlotAccessor> getInputSlots(MenuInfoContext<T, ?, D> context) {
        List<SlotAccessor> accessors = new ArrayList<>(this.width() * this.height());
        for (Slot slot : context.getMenu().slots) {
            if (slot instanceof StorageSlot machineSlot) {
                if (machineSlot.getWrapped().transferMode().isInput()) {
                    accessors.add(new VanillaSlotAccessor(machineSlot));
                }
            }
        }
        return accessors;
    }

    @Override
    public D getDisplay() {
        return display;
    }

    @Override
    public int getCraftingResultSlotIndex(T menu) {
        return this.resultIndex;
    }

    @Override
    public int getCraftingWidth(T menu) {
        return this.width();
    }

    @Override
    public int getCraftingHeight(T menu) {
        return this.height();
    }

    public InputCleanHandler<T, D> getInputCleanHandler() {
        return context -> {
            T container = context.getMenu();
            for (SlotAccessor gridStack : getInputSlots(context)) {
                returnSlotsToPlayerInventory(context, getDumpHandler(), gridStack);
            }

            clearInputSlots(container);
        };
    }

    static <T extends AbstractContainerMenu> void returnSlotsToPlayerInventory(MenuInfoContext<T, ?, ?> context, DumpHandler<T, ?> dumpHandler, SlotAccessor slotAccessor) {
        if (!slotAccessor.getItemStack().isEmpty()) {
            for (; slotAccessor.getItemStack().getCount() > 0; slotAccessor.takeStack(1)) {
                ItemStack stackToInsert = slotAccessor.getItemStack().copy();
                stackToInsert.setCount(1);
                if (!InputCleanHandler.dumpGenericsFtw(context, dumpHandler, stackToInsert)) {
                    InputCleanHandler.error("rei.rei.no.slot.in.inv");
                }
            }
        }
    }
}
