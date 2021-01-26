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

import com.hrznstudio.galacticraft.block.entity.CoalGeneratorBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.FilteredSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.Property;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CoalGeneratorScreenHandler extends MachineScreenHandler<CoalGeneratorBlockEntity> {

    public CoalGeneratorScreenHandler(int syncId, PlayerEntity player, CoalGeneratorBlockEntity machine) {
        super(syncId, player, machine, GalacticraftScreenHandlerTypes.COAL_GENERATOR_HANDLER);
        this.addSlot(new FilteredSlot(machine, machine.getWrappedInventory(), CoalGeneratorBlockEntity.FUEL_SLOT, 8, 74));
        this.addSlot(new FilteredSlot(machine, machine.getWrappedInventory(), CoalGeneratorBlockEntity.CHARGE_SLOT, 8, 8));
        this.addPlayerInventorySlots(0, 94);
    }

    public CoalGeneratorScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (CoalGeneratorBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }
}
