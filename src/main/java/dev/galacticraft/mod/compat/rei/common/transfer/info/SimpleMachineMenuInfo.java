/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import dev.galacticraft.mod.block.entity.RecipeMachineBlockEntity;
import dev.galacticraft.mod.compat.rei.common.transfer.info.stack.LBASlotAccessor;
import dev.galacticraft.mod.screen.RecipeMachineScreenHandler;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoContext;
import me.shedaniel.rei.api.common.transfer.info.clean.InputCleanHandler;
import me.shedaniel.rei.api.common.transfer.info.simple.DumpHandler;
import me.shedaniel.rei.api.common.transfer.info.simple.SimpleGridMenuInfo;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;

import java.util.stream.Collectors;

public record SimpleMachineMenuInfo<C extends Inventory, R extends Recipe<C>, B extends RecipeMachineBlockEntity<C, R>, T extends RecipeMachineScreenHandler<C, R, B>, D extends SimpleGridMenuDisplay>(int width, int height, int resultIndex, int offset) implements SimpleGridMenuInfo<T, D> {
    @Override
    public Iterable<SlotAccessor> getInputSlots(MenuInfoContext<T, ?, D> context) {
        return getInputStackSlotIds(context)
                .mapToObj(value -> new LBASlotAccessor(context.getMenu().machine.itemInv(), value + offset))
                .collect(Collectors.toList());
    }

    @Override
    public int getCraftingResultSlotIndex(T menu) {
        return this.resultIndex();
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

    static <T extends ScreenHandler> void returnSlotsToPlayerInventory(MenuInfoContext<T, ?, ?> context, DumpHandler<T, ?> dumpHandler, SlotAccessor slotAccessor) {
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
