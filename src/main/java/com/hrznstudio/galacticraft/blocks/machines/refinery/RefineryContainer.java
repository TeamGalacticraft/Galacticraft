/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.blocks.machines.refinery;

import alexiil.mc.lib.attributes.fluid.FluidProviderItem;
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import com.hrznstudio.galacticraft.blocks.machines.MachineContainer;
import com.hrznstudio.galacticraft.container.slot.ChargeSlot;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RefineryContainer extends MachineContainer<RefineryBlockEntity> {

    public static final ContainerFactory<ScreenHandler> FACTORY = createFactory(RefineryBlockEntity.class, RefineryContainer::new);
    private final Property status = Property.create();
    private Inventory inventory;

    public RefineryContainer(int syncId, PlayerEntity playerEntity, RefineryBlockEntity blockEntity) {
        super(syncId, playerEntity, blockEntity);
        addProperty(status);
        this.inventory = new InventoryFixedWrapper(blockEntity.getInventory()) {
            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return RefineryContainer.this.canUse(player);
            }

            @Override
            public int size() {
                return blockEntity.getInvSize();
            }
        };
        // Energy slot
        this.addSlot(new ChargeSlot(this.inventory, 0, 8, 79));
        this.addSlot(new Slot(this.inventory, 1, 8, 15) {
            @Override
            public boolean canInsert(ItemStack itemStack_1) {
                return itemStack_1.getItem() instanceof FluidProviderItem;
            }

            @Override
            public int getMaxStackAmount() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.inventory, 2, 8 + (18 * 3), 79) {
            @Override
            public boolean canInsert(ItemStack itemStack_1) {
                return itemStack_1.getItem() instanceof FluidProviderItem;
            }

            @Override
            public int getMaxStackAmount() {
                return 1;
            }
        });


        // Player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerEntity.inventory, j + i * 9 + 9, 8 + j * 18, 110 + i * 18));
            }
        }

        // Hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerEntity.inventory, i, 8 + i * 18, 168));
        }

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

            if (slotId < this.blockEntity.getInventory().getSlotCount()) {

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
    public boolean canUse(PlayerEntity playerEntity) {
        return true;
    }

    @Override
    public void sendContentUpdates() {
        status.set(blockEntity.status.ordinal());
        super.sendContentUpdates();
    }

    @Override
    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        blockEntity.status = RefineryStatus.values()[status.get()];
    }
}
