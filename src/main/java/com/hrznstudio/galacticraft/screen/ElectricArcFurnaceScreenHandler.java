/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.screen;

import com.hrznstudio.galacticraft.block.entity.ElectricArcFurnaceBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.ChargeSlot;
import com.hrznstudio.galacticraft.screen.slot.OutputSlot;
import com.hrznstudio.galacticraft.screen.slot.RecipeInputSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.crafting.RecipeType;

public class ElectricArcFurnaceScreenHandler extends MachineScreenHandler<ElectricArcFurnaceBlockEntity> {
    public ElectricArcFurnaceScreenHandler(int syncId, Player player, ElectricArcFurnaceBlockEntity machine) {
        super(syncId, player, machine, GalacticraftScreenHandlerTypes.ELECTRIC_ARC_FURNACE_HANDLER);
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return machine.cookTime;
            }

            @Override
            public void set(int value) {
                machine.cookTime = value;
            }
        });
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return machine.cookLength;
            }

            @Override
            public void set(int value) {
                machine.cookLength = value;
            }
        });
        this.addSlot(new ChargeSlot(machine.getWrappedInventory(), ElectricArcFurnaceBlockEntity.CHARGE_SLOT, 8, 7));
        this.addSlot(new RecipeInputSlot<>(machine.getWrappedInventory(), ElectricArcFurnaceBlockEntity.INPUT_SLOT, 56, 25, machine.getLevel(), RecipeType.SMELTING));
        this.addSlot(new OutputSlot(machine.getWrappedInventory(), ElectricArcFurnaceBlockEntity.OUTPUT_SLOT_1, 109, 25));
        this.addSlot(new OutputSlot(machine.getWrappedInventory(), ElectricArcFurnaceBlockEntity.OUTPUT_SLOT_2, 127, 25));
        this.addPlayerInventorySlots(0, 84);
    }

    public ElectricArcFurnaceScreenHandler(int syncId, Inventory inv, FriendlyByteBuf buf) {
        this(syncId, inv.player, (ElectricArcFurnaceBlockEntity) inv.player.level.getBlockEntity(buf.readBlockPos()));
    }
}
