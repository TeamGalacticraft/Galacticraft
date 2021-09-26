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
import dev.galacticraft.mod.compat.rei.common.display.DefaultFabricationDisplay;
import dev.galacticraft.mod.compat.rei.common.transfer.info.stack.LBASlotAccessor;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.screen.RecipeMachineScreenHandler;
import me.shedaniel.rei.api.common.transfer.info.MenuInfoContext;
import me.shedaniel.rei.api.common.transfer.info.clean.InputCleanHandler;
import me.shedaniel.rei.api.common.transfer.info.simple.SimplePlayerInventoryMenuInfo;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import net.minecraft.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class FabricationMenuInfo<B extends RecipeMachineBlockEntity<Inventory, FabricationRecipe>, T extends RecipeMachineScreenHandler<Inventory, FabricationRecipe, B>, D extends DefaultFabricationDisplay> implements SimplePlayerInventoryMenuInfo<T, D> {
    @Override
    public Iterable<SlotAccessor> getInputSlots(MenuInfoContext<T, ?, D> context) {
        T menu = context.getMenu();

        List<SlotAccessor> list = new ArrayList<>(5);
        for (int i = 1; i < 6; i++) {
            list.add(new LBASlotAccessor(menu.machine.itemInv(), i));
        }
        return list;
    }


    public InputCleanHandler<T, D> getInputCleanHandler() {
        return context -> {
            T container = context.getMenu();
            for (SlotAccessor gridStack : getInputSlots(context)) {
                SimpleMachineMenuInfo.returnSlotsToPlayerInventory(context, getDumpHandler(), gridStack);
            }

            clearInputSlots(container);
        };
    }
}
