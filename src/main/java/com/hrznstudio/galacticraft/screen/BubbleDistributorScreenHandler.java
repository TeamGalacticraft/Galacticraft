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

import com.hrznstudio.galacticraft.block.entity.BubbleDistributorBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.ChargeSlot;
import com.hrznstudio.galacticraft.screen.slot.OxygenTankSlot;
import io.github.cottonmc.component.compat.vanilla.InventoryWrapper;
import io.github.cottonmc.component.item.InventoryComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.Slot;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BubbleDistributorScreenHandler extends MachineScreenHandler<BubbleDistributorBlockEntity> {
    public final Property status = Property.create();
    public final Property oxygen = Property.create();
    private final Inventory inventory;

    public BubbleDistributorScreenHandler(int syncId, PlayerEntity playerEntity, BubbleDistributorBlockEntity blockEntity) {
        super(syncId, playerEntity, blockEntity, GalacticraftScreenHandlerTypes.BUBBLE_DISTRIBUTOR_HANDLER);
        this.inventory = new InventoryWrapper() {
            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return BubbleDistributorScreenHandler.this.canUse(player);
            }

            @Override
            public InventoryComponent getComponent() {
                return blockEntity.getInventory();
            }
        };

        addProperty(status);
        addProperty(oxygen);

        // Charging Slot
        this.addSlot(new ChargeSlot(this.inventory, 0, 8, 56));

        this.addSlot(new OxygenTankSlot(this.inventory, 1, 31, 56));

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

    public BubbleDistributorScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (BubbleDistributorBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity, int slotId) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);

        if (slot != null && slot.hasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (itemStack.isEmpty()) {
                return itemStack;
            }

            if (slotId < this.blockEntity.getInventory().getSize()) {

                if (!this.insertItem(itemStack1, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack1, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack1.getCount() == 0) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void sendContentUpdates() {
        status.set(blockEntity.status.ordinal());
        oxygen.set((int) (blockEntity.getFluidTank().getContents(0).getAmount().doubleValue() * 100));
        super.sendContentUpdates();
    }

    @Override
    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        blockEntity.status = BubbleDistributorBlockEntity.BubbleDistributorStatus.get(status.get());
    }
}
