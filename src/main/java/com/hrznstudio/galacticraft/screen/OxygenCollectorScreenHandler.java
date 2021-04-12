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

import com.hrznstudio.galacticraft.block.entity.OxygenCollectorBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.FilteredSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenCollectorScreenHandler extends MachineScreenHandler<OxygenCollectorBlockEntity> {
    public OxygenCollectorScreenHandler(int syncId, Player player, OxygenCollectorBlockEntity blockEntity) {
        super(syncId, player, blockEntity, GalacticraftScreenHandlerTypes.OXYGEN_COLLECTOR_HANDLER);
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return machine.collectionAmount;
            }

            @Override
            public void set(int value) {
                machine.collectionAmount = value;
            }
        });
        this.addSlot(new FilteredSlot(blockEntity, OxygenCollectorBlockEntity.CHARGE_SLOT, 13, 69));
        this.addPlayerInventorySlots(0, 99);
    }

    public OxygenCollectorScreenHandler(int syncId, Inventory inv, FriendlyByteBuf buf) {
        this(syncId, inv.player, (OxygenCollectorBlockEntity) inv.player.level.getBlockEntity(buf.readBlockPos()));
    }
}
