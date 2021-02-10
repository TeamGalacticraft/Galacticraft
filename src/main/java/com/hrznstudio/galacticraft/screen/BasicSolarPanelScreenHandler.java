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

import com.hrznstudio.galacticraft.block.entity.BasicSolarPanelBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.FilteredSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BasicSolarPanelScreenHandler extends MachineScreenHandler<BasicSolarPanelBlockEntity> {
    public BasicSolarPanelScreenHandler(int syncId, PlayerEntity player, BasicSolarPanelBlockEntity machine) {
        super(syncId, player, machine, GalacticraftScreenHandlerTypes.BASIC_SOLAR_PANEL_HANDLER);

        this.addSlot(new FilteredSlot(machine, machine.getWrappedInventory(), BasicSolarPanelBlockEntity.CHARGE_SLOT, 8, 62));
        this.addPlayerInventorySlots(0, 84);
    }

    public BasicSolarPanelScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (BasicSolarPanelBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }
}
