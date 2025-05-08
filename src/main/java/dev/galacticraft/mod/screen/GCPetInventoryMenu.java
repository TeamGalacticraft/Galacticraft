/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import dev.galacticraft.mod.content.GCAccessorySlots;
import dev.galacticraft.mod.screen.slot.AccessorySlot;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.ticks.ContainerSingleItem;

public class GCPetInventoryMenu extends AbstractContainerMenu {
    public final Container inventory;

    public final Player player;

    public final TamableAnimal animal;

    private final Container armorContainer = new ContainerSingleItem(){

        @Override
        public ItemStack getTheItem() {
            return GCPetInventoryMenu.this.animal.getBodyArmorItem();
        }

        @Override
        public void setTheItem(ItemStack itemStack) {
            GCPetInventoryMenu.this.animal.setBodyArmorItem(itemStack);
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player player) {
            return GCPetInventoryMenu.this.stillValid(player);
        }
    };

    protected GCPetInventoryMenu(int syncId, Inventory playerInventory, int petId) {
        this(syncId, playerInventory, playerInventory.player, (TamableAnimal) playerInventory.player.level().getEntity(petId));
    }

    public GCPetInventoryMenu(int syncId, Inventory playerInventory, Player player, TamableAnimal animal) {
        super(GCMenuTypes.PET_INV_GC, syncId);

        this.player = player;
        this.animal = animal;
        this.inventory = animal.galacticraft$getGearInv();

        this.addSlot(new AccessorySlot(inventory, animal, 0, 8, 18));

        // Galacticraft inv
        for (int i = 4; i < 7; ++i) {
            this.addSlot(new AccessorySlot(inventory, animal, i - 3, 80, (i % 4 + 1) * 18, GCAccessorySlots.SLOT_TAGS.get(i), GCAccessorySlots.SLOT_SPRITES.get(i)));
        }

        // Player main inv
        for (int slotY = 0; slotY < 3; ++slotY) {
            for (int slotX = 0; slotX < 9; ++slotX) {
                this.addSlot(new Slot(playerInventory, slotX + (slotY + 1) * 9, 8 + slotX * 18, 84 + slotY * 18));
            }
        }

        // Player hotbar
        for (int slotY = 0; slotY < 9; ++slotY) {
            this.addSlot(new Slot(playerInventory, slotY, 8 + slotY * 18, 142));
        }

        // Wolf armor
        if (animal instanceof Wolf wolf) {
            this.addSlot(new Slot(this.armorContainer, 0, 8, 36) {

                @Override
                public boolean mayPlace(ItemStack itemStack) {
                    return itemStack.is(Items.WOLF_ARMOR);
                }

                @Override
                public boolean isActive() {
                    return wolf.canUseSlot(EquipmentSlot.BODY);
                }
            });
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getUUID().equals(this.player.getUUID()) && player.canInteractWithEntity(this.animal, 4.0);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotFrom = this.slots.get(index);
        if (slotFrom.hasItem()) {
            ItemStack stackFrom = slotFrom.getItem();
            stack = stackFrom.copy();

            // Index of Indexes :)
            // 0 (1): GC, thermal armor slots;
            // 1-2 (2): GC, oxygen mask/gear slots;
            // 3 (1): GC, oxygen tank slots;
            // 4-30 (27): MC, non-hotbar inventory slots;
            // 31-39 (9): MC, hotbar slots.
            if (index < 4) {
                if (!this.moveItemStackTo(stackFrom, 31, 40, false) &&
                    !this.moveItemStackTo(stackFrom, 4, 31, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 31) {
                if (!this.moveItemStackTo(stackFrom, 0, 4, false) &&
                    !this.moveItemStackTo(stackFrom, 31, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 40) {
                if (!this.moveItemStackTo(stackFrom, 0, 4, false) &&
                    !this.moveItemStackTo(stackFrom, 4, 31, false)) {
                    return ItemStack.EMPTY;
                }
            }

            slotFrom.onQuickCraft(stackFrom, stack);

            if (stackFrom.isEmpty()) {
                slotFrom.set(ItemStack.EMPTY);
            } else {
                slotFrom.setChanged();
            }

            if (stackFrom.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slotFrom.onTake(player, stackFrom);
            if (index == 0) {
                player.drop(stackFrom, false);
            }
        }

        return stack;
    }
}
