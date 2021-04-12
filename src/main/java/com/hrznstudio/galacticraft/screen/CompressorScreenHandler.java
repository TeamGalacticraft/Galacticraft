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

import com.hrznstudio.galacticraft.block.entity.CompressorBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.ItemSpecificSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CompressorScreenHandler extends MachineScreenHandler<CompressorBlockEntity> {
    public final DataSlot progress = new DataSlot() {
        @Override
        public int get() {
            return CompressorScreenHandler.this.machine.progress;
        }

        @Override
        public void set(int value) {
            CompressorScreenHandler.this.machine.progress = value;
        }
    };
    public final DataSlot fuelTime = new DataSlot() {
        @Override
        public int get() {
            return CompressorScreenHandler.this.machine.fuelTime;
        }

        @Override
        public void set(int value) {
            CompressorScreenHandler.this.machine.fuelTime = value;
        }
    };

    public CompressorScreenHandler(int syncId, Player player, CompressorBlockEntity machine) {
        super(syncId, player, machine, GalacticraftScreenHandlerTypes.COMPRESSOR_HANDLER);
        this.addDataSlot(progress);
        this.addDataSlot(fuelTime);

        // 3x3 compressor input grid
        int slot = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new Slot(this.machine.getWrappedInventory(), slot++, x * 18 + 19, y * 18 + 18));
            }
        }

        // Fuel slot
        this.addSlot(new ItemSpecificSlot(this.machine.getWrappedInventory(), CompressorBlockEntity.FUEL_INPUT_SLOT, 3 * 18 + 1, 75, AbstractFurnaceBlockEntity.getFuel().keySet().toArray(new Item[0])));

        // Output slot
        this.addSlot(new FurnaceResultSlot(player, this.machine.getWrappedInventory(), CompressorBlockEntity.OUTPUT_SLOT, 138, 38));

        this.addPlayerInventorySlots(0, 110);
    }

    public CompressorScreenHandler(int syncId, Inventory inv, FriendlyByteBuf buf) {
        this(syncId, inv.player, (CompressorBlockEntity) inv.player.level.getBlockEntity(buf.readBlockPos()));
    }
}
