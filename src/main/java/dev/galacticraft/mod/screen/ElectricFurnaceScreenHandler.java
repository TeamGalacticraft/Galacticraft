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

package dev.galacticraft.mod.screen;

import dev.galacticraft.mod.block.entity.ElectricFurnaceBlockEntity;
import dev.galacticraft.mod.screen.slot.FilteredSlot;
import dev.galacticraft.mod.screen.slot.OutputSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.Property;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ElectricFurnaceScreenHandler extends MachineScreenHandler<ElectricFurnaceBlockEntity> {
    public ElectricFurnaceScreenHandler(int syncId, PlayerEntity player, ElectricFurnaceBlockEntity machine) {
        super(syncId, player, machine, GalacticraftScreenHandlerTypes.ELECTRIC_FURNACE_HANDLER);

        this.addSlot(new FilteredSlot(machine, ElectricFurnaceBlockEntity.CHARGE_SLOT, 8, 7)); //charge
        this.addSlot(new FilteredSlot(machine, ElectricFurnaceBlockEntity.INPUT_SLOT, 56, 25)); //in
        this.addSlot(new OutputSlot(machine.getWrappedInventory(), ElectricFurnaceBlockEntity.OUTPUT_SLOT, 109, 25)); //out
        this.addPlayerInventorySlots(0, 84);

        this.addProperty(new Property() {
            @Override
            public int get() {
                return machine.cookTime;
            }

            @Override
            public void set(int value) {
                machine.cookTime = value;
            }
        });

        this.addProperty(new Property() {
            @Override
            public int get() {
                return machine.cookLength;
            }

            @Override
            public void set(int value) {
                machine.cookLength = value;
            }
        });
    }

    public ElectricFurnaceScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (ElectricFurnaceBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }
}
