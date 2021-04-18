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

import dev.galacticraft.mod.block.entity.ElectricCompressorBlockEntity;
import dev.galacticraft.mod.screen.slot.FilteredSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ElectricCompressorScreenHandler extends MachineScreenHandler<ElectricCompressorBlockEntity> {
    public ElectricCompressorScreenHandler(int syncId, PlayerEntity player, ElectricCompressorBlockEntity blockEntity) {
        super(syncId, player, blockEntity, GalacticraftScreenHandlerTypes.ELECTRIC_COMPRESSOR_HANDLER);
        this.addProperty(new Property() {
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

        this.addSlot(new FurnaceOutputSlot(this.player, machine.getWrappedInventory(), ElectricCompressorBlockEntity.OUTPUT_SLOT, 138, 29));
        this.addSlot(new FurnaceOutputSlot(player, machine.getWrappedInventory(), ElectricCompressorBlockEntity.SECOND_OUTPUT_SLOT, 138, 47));
        this.addSlot(new FilteredSlot(machine, ElectricCompressorBlockEntity.CHARGE_SLOT, 3 * 18 + 1, 75));

        this.addPlayerInventorySlots(0, 117);
    }

    public ElectricCompressorScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (ElectricCompressorBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }
}
