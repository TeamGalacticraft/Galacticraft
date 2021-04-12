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

import com.hrznstudio.galacticraft.block.entity.ElectricCompressorBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.FilteredSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.Slot;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ElectricCompressorScreenHandler extends MachineScreenHandler<ElectricCompressorBlockEntity> {
    public ElectricCompressorScreenHandler(int syncId, Player player, ElectricCompressorBlockEntity blockEntity) {
        super(syncId, player, blockEntity, GalacticraftScreenHandlerTypes.ELECTRIC_COMPRESSOR_HANDLER);
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return machine.progress;
            }

            @Override
            public void set(int value) {
                machine.progress = value;
            }
        });

        // 3x3 compressor input grid
        int slot = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new Slot(machine.getWrappedInventory(), slot, x * 18 + 19, y * 18 + 18));
                slot++;
            }
        }

        this.addSlot(new FurnaceResultSlot(this.player, machine.getWrappedInventory(), ElectricCompressorBlockEntity.OUTPUT_SLOT, 138, 29));
        this.addSlot(new FurnaceResultSlot(player, machine.getWrappedInventory(), ElectricCompressorBlockEntity.SECOND_OUTPUT_SLOT, 138, 47));
        this.addSlot(new FilteredSlot(machine, ElectricCompressorBlockEntity.CHARGE_SLOT, 3 * 18 + 1, 75));

        this.addPlayerInventorySlots(0, 117);
    }

    public ElectricCompressorScreenHandler(int syncId, Inventory inv, FriendlyByteBuf buf) {
        this(syncId, inv.player, (ElectricCompressorBlockEntity) inv.player.level.getBlockEntity(buf.readBlockPos()));
    }
}
