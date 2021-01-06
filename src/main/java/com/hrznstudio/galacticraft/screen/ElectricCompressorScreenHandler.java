/*
 * Copyright (c) 2020 HRZN LTD
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
import com.hrznstudio.galacticraft.block.entity.ElectricCompressorBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.ChargeSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ElectricCompressorScreenHandler extends MachineScreenHandler<ElectricCompressorBlockEntity> {
    public final Property progress = Property.create();
    protected final Inventory inventory;

    public ElectricCompressorScreenHandler(int syncId, PlayerEntity player, ElectricCompressorBlockEntity blockEntity) {
        super(syncId, player, blockEntity, GalacticraftScreenHandlerTypes.ELECTRIC_COMPRESSOR_HANDLER);
        this.inventory = blockEntity.getInventory().asInventory();
        addProperty(progress);

        // 3x3 compressor input grid
        int slot = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new Slot(this.inventory, slot, x * 18 + 19, y * 18 + 18));
                slot++;
            }
        }

        // Output slot
        this.addSlot(new FurnaceOutputSlot(playerEntity, this.inventory, CompressorBlockEntity.OUTPUT_SLOT, getOutputSlotPos()[0], getOutputSlotPos()[1]));

        // Player inventory slots
        int playerInvYOffset = 117;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerEntity.inventory, j + i * 9 + 9, 8 + j * 18, playerInvYOffset + i * 18));
            }
        }

        // Hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerEntity.inventory, i, 8 + i * 18, playerInvYOffset + 58));
        }

        addSlot(new FurnaceOutputSlot(player, this.inventory, ElectricCompressorBlockEntity.SECOND_OUTPUT_SLOT, getOutputSlotPos()[0], getOutputSlotPos()[1] + 18));
        addSlot(new ChargeSlot(this.inventory, CompressorBlockEntity.FUEL_INPUT_SLOT, 3 * 18 + 1, 75));
    }

    public ElectricCompressorScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (ElectricCompressorBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public void sendContentUpdates() {
        progress.set(blockEntity.getProgress());
        super.sendContentUpdates();
    }

    @Override
    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        blockEntity.progress = progress.get();
    }

    protected int[] getOutputSlotPosO() {
        return new int[]{138, 38};
    }

    protected int[] getOutputSlotPos() {
        int[] outputSlotPos = getOutputSlotPosO();
        // Move output slot up by half a slot
        outputSlotPos[1] = outputSlotPos[1] - (18 / 2);
        return outputSlotPos;
    }
}
