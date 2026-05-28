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

package dev.galacticraft.mod.screen;

import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.mod.content.block.entity.machine.CircuitFabricatorBlockEntity;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.tag.GCItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class CircuitFabricatorMenu extends RecipeMachineMenu<RecipeInput, FabricationRecipe, CircuitFabricatorBlockEntity> {
    public CircuitFabricatorMenu(int syncId, Player player, CircuitFabricatorBlockEntity machine) {
        super(GCMenuTypes.CIRCUIT_FABRICATOR, syncId, player, machine);
    }

    public CircuitFabricatorMenu(int syncId, Inventory inv, BlockPos pos) {
        super(GCMenuTypes.CIRCUIT_FABRICATOR, syncId, inv, pos, 8, 94);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slotFrom = this.slots.get(index);
        ItemStack stackFrom = slotFrom.getItem();

        if (index >= this.internalSlots && stackFrom.is(GCItemTags.SILICONS)) {
            Item item = stackFrom.getItem();
            DataComponentPatch dataComponentPatch = stackFrom.getComponentsPatch();
            long available = stackFrom.getCount();

            ItemResourceSlot slot1 = this.itemStorage.slot(CircuitFabricatorBlockEntity.SILICON_SLOT_1);
            ItemResourceSlot slot2 = this.itemStorage.slot(CircuitFabricatorBlockEntity.SILICON_SLOT_2);

            long slot1Count = slot1.getAmount();
            long slot2Count = slot2.getAmount();
            long originalCount = slot1Count + slot2Count;

            long slot1Capacity = slot1.getCapacityFor(item, dataComponentPatch);
            long slot2Capacity = slot2.getCapacityFor(item, dataComponentPatch);

            long toInsert = Mth.clamp(slot1Capacity + slot2Capacity - originalCount, 0, available);

            if (toInsert > 0) {
                long totalCount = originalCount + toInsert;
                long toInsert2 = Mth.clamp((totalCount / 2) - slot2Count, 0, toInsert);
                long toInsert1 = toInsert - toInsert2;

                available -= slot1.insert(item, dataComponentPatch, toInsert1);
                available -= slot2.insert(item, dataComponentPatch, toInsert2);

                if (available == 0) {
                    slotFrom.setByPlayer(ItemStack.EMPTY);
                } else {
                    stackFrom.setCount((int) available);
                    slotFrom.setChanged();
                }

                return ItemStack.EMPTY;
            }
        }

        return super.quickMoveStack(player, index);
    }
}
