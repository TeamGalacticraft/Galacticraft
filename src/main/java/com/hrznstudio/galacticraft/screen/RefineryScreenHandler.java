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

import com.hrznstudio.galacticraft.block.entity.RefineryBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.AutoFilteredSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RefineryScreenHandler extends MachineScreenHandler<RefineryBlockEntity> {

    public RefineryScreenHandler(int syncId, PlayerEntity player, RefineryBlockEntity machine) {
        super(syncId, player, machine, GalacticraftScreenHandlerTypes.REFINERY_HANDLER);

        // Energy slot
        this.addSlot(new AutoFilteredSlot(this.machine, 0, 8, 7));
        this.addSlot(new AutoFilteredSlot(this.machine, 1, 123, 7));
        this.addSlot(new AutoFilteredSlot(this.machine, 2, 153, 7));

        this.addPlayerInventorySlots(0, 86);
    }

    public RefineryScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (RefineryBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }
}
