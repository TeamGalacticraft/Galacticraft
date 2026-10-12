/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.mod.content.GCRocketParts;
import dev.galacticraft.mod.content.entity.vehicle.RocketEntity;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RocketMenu extends AbstractContainerMenu {
    public final Player player;
    public final RocketEntity rocket;

    protected RocketMenu(int syncId, Inventory playerInventory, int id) {
        this(syncId, playerInventory, playerInventory.player, (RocketEntity) playerInventory.player.level().getEntity(id));
    }

    public RocketMenu(int syncId, Inventory playerInventory, Player player, RocketEntity rocket) {
        super(GCMenuTypes.ROCKET, syncId);

        this.player = player;
        this.rocket = rocket;

        // Player main inv
        for (int slotY = 0; slotY < 3; ++slotY) {
            for (int slotX = 0; slotX < 9; ++slotX) {
                this.addSlot(new Slot(playerInventory, slotX + (slotY + 1) * 9, 8 + slotX * 18, 50 + slotY * 18));
            }
        }

        // Player hotbar
        for (int slotY = 0; slotY < 9; ++slotY) {
            this.addSlot(new Slot(playerInventory, slotY, 8 + slotY * 18, 108));
        }

        if (!hasInventory()) return;

        SimpleContainer storage = rocket.getStorage();
        int size = storage.getContainerSize();
        for (int i = 0; i < size; i++) {
            int col = i / (size / 2);
            int row = i % (size / 2);
            this.addSlot(new Slot(storage, i, 116 + row * 18, 9 + col * 18));
        }
    }

    public boolean hasInventory() {
        RocketData data = rocket.getRocketData();
        return data.hasUpgrade(GCRocketParts.STORAGE_UPGRADE);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        if (!hasInventory()) return ItemStack.EMPTY;

        ItemStack movedStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack rawStack = slot.getItem();
            movedStack = rawStack.copy();

            int size = rocket.getStorage().getContainerSize();
            if (i >= 36) {
                if (!this.moveItemStackTo(rawStack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(rawStack, 36, 36 + size, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (rawStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return movedStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getVehicle() instanceof RocketEntity;
    }
}
