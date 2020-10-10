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

import com.hrznstudio.galacticraft.block.entity.OxygenCompressorBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.ChargeSlot;
import com.hrznstudio.galacticraft.screen.slot.OxygenTankSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.Slot;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenCompressorScreenHandler extends MachineScreenHandler<OxygenCompressorBlockEntity> {
    public final Property status = Property.create();
    public final Property oxygen = Property.create(); //loses some data (cant send a fraction)

    public OxygenCompressorScreenHandler(int syncId, PlayerEntity playerEntity, OxygenCompressorBlockEntity blockEntity) {
        super(syncId, playerEntity, blockEntity, GalacticraftScreenHandlerTypes.OXYGEN_COMPRESSOR_HANDLER);
        Inventory inventory = blockEntity.getInventory().asInventory();

        addProperty(status);
        addProperty(oxygen);

        // Charging slot
        this.addSlot(new ChargeSlot(inventory, 0, 20, 55));
        this.addSlot(new OxygenTankSlot(inventory, 1, 80, 27));

        // Player inventory slots
        int playerInvYOffset = 84;

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerEntity.inventory, j + i * 9 + 9, 8 + j * 18, playerInvYOffset + i * 18));
            }
        }

        // Hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerEntity.inventory, i, 8 + i * 18, playerInvYOffset + 58));
        }
    }

    public OxygenCompressorScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (OxygenCompressorBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public void sendContentUpdates() {
        status.set(blockEntity.status.ordinal());
        oxygen.set((int) (blockEntity.getFluidTank().getContents(0).getAmount().floatValue() * 100.0D));
        super.sendContentUpdates();
    }

    @Override
    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        blockEntity.status = OxygenCompressorBlockEntity.OxygenCompressorStatus.get(status.get());
    }
}
