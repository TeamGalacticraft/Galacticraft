/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import dev.galacticraft.mod.block.entity.RocketDesignerBlockEntity;
import dev.galacticraft.mod.item.GalacticraftItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class RocketDesignerScreenHandler extends ScreenHandler {

    public final PlayerEntity player;
    protected Inventory inventory;
    public RocketDesignerBlockEntity designer;

    public RocketDesignerScreenHandler(int syncId, PlayerEntity player, RocketDesignerBlockEntity machine) {
        super(GalacticraftScreenHandlerType.ROCKET_DESIGNER_HANDLER, syncId);
        this.designer = machine;
        this.inventory = new InventoryFixedWrapper(machine.getInventory()) {
            @Override
            public boolean canPlayerUse(PlayerEntity invPlayer) {
                return invPlayer == RocketDesignerScreenHandler.this.player;
            }
        };

        int playerInvYOffset = 84;
        int playerInvXOffset = 148;

        // Output slot
        this.addSlot(new Slot(this.inventory, RocketDesignerBlockEntity.SCHEMATIC_OUTPUT_SLOT, 8 + (8 * 18) + playerInvXOffset + 3 - 1, (playerInvYOffset - 21) - 6) {
            @Override
            public boolean canInsert(ItemStack itemStack_1) {
                return itemStack_1.getItem() == GalacticraftItem.ROCKET_SCHEMATIC;
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity_1) {
                return true;
            }
        });

        // Hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(player.getInventory(), i, 8 + i * 18 + playerInvXOffset, playerInvYOffset + 58));
        }

        // Player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(player.getInventory(), j + i * 9 + 9, 8 + (j * 18) + playerInvXOffset, playerInvYOffset + i * 18));
            }
        }
        this.player = player;
    }

    public RocketDesignerScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (RocketDesignerBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
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

            if (slotId < this.designer.getInventory().getSlotCount()) {

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

//    @Override
//    public void onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
//        if (actionType == SlotActionType.QUICK_MOVE) {
//            if (slots.get(i).getStack().getItem() != GalacticraftItem.ROCKET_SCHEMATIC) {
//                return ItemStack.EMPTY;
//            } else {
//                if(inventory.getStack(0).isEmpty()) {
//                    inventory.setStack(0, slots.get(i).getStack().copy());
//                    slots.get(i).setStack(ItemStack.EMPTY);
//                    return inventory.getStack(0);
//                } else {
//                    return ItemStack.EMPTY;
//                }
//            }
//        }
//        return super.onSlotClick(i, j, actionType, playerEntity);
//    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return true;
    }
}
