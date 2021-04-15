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

package dev.galacticraft.mod.screen;

import dev.galacticraft.mod.block.entity.CompressorBlockEntity;
import dev.galacticraft.mod.screen.slot.ItemSpecificSlot;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CompressorScreenHandler extends MachineScreenHandler<CompressorBlockEntity> {
    public final Property progress = new Property() {
        @Override
        public int get() {
            return CompressorScreenHandler.this.machine.progress;
        }

        @Override
        public void set(int value) {
            CompressorScreenHandler.this.machine.progress = value;
        }
    };
    public final Property fuelTime = new Property() {
        @Override
        public int get() {
            return CompressorScreenHandler.this.machine.fuelTime;
        }

        @Override
        public void set(int value) {
            CompressorScreenHandler.this.machine.fuelTime = value;
        }
    };

    public CompressorScreenHandler(int syncId, PlayerEntity player, CompressorBlockEntity machine) {
        super(syncId, player, machine, GalacticraftScreenHandlerTypes.COMPRESSOR_HANDLER);
        this.addProperty(progress);
        this.addProperty(fuelTime);

        // 3x3 compressor input grid
        int slot = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new Slot(this.machine.getWrappedInventory(), slot++, x * 18 + 19, y * 18 + 18));
            }
        }

        // Fuel slot
        this.addSlot(new ItemSpecificSlot(this.machine.getWrappedInventory(), CompressorBlockEntity.FUEL_INPUT_SLOT, 3 * 18 + 1, 75, AbstractFurnaceBlockEntity.createFuelTimeMap().keySet().toArray(new Item[0])));

        // Output slot
        this.addSlot(new FurnaceOutputSlot(player, this.machine.getWrappedInventory(), CompressorBlockEntity.OUTPUT_SLOT, 138, 38));

        this.addPlayerInventorySlots(0, 110);
    }

    public CompressorScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (CompressorBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }
}
