/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.block.special.ParaChestBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ParachestMenu extends AbstractContainerMenu {
    private final ParaChestBlockEntity blockEntity;

    public ParachestMenu(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(syncId, playerInventory, GCBlockEntityTypes.PARACHEST.getBlockEntity(playerInventory.player.level(), buf.readBlockPos()));
    }

    public ParachestMenu(int syncId, Inventory playerInventory, ParaChestBlockEntity blockEntity) {
        super(GCMenuTypes.PARACHEST, syncId);
        this.blockEntity = blockEntity;
        int numRows = (blockEntity.getContainerSize() - 3) / 9;
        int i = (numRows - 4) * 18 + 19;
        // Player main inv
        for (int slotY = 0; slotY < 3; ++slotY) {
            for (int slotX = 0; slotX < 9; ++slotX) {
                this.addSlot(new Slot(playerInventory, slotX + (slotY + 1) * 9, 8 + slotX * 18, (numRows == 0 ? 116 : 118) + slotY * 18 + i));
            }
        }

        // Player hotbar
        for (int slotY = 0; slotY < 9; ++slotY) {
            this.addSlot(new Slot(playerInventory, slotY, 8 + slotY * 18, (numRows == 0 ? 174 : 176) + i));
        }

        for (int colum = 0; colum < numRows; ++colum) {
            for (int row = 0; row < 9; ++row) {
                this.addSlot(new Slot(this.blockEntity, row + colum * 9, 8 + row * 18, 18 + colum * 18));
            }
        }

        this.addSlot(new Slot(this.blockEntity, this.blockEntity.getContainerSize() - 3, 125, (numRows == 0 ? 24 : 26) + numRows * 18));
        this.addSlot(new Slot(this.blockEntity, this.blockEntity.getContainerSize() - 2, 125 + 18, (numRows == 0 ? 24 : 26) + numRows * 18));
        this.addSlot(new Slot(this.blockEntity, this.blockEntity.getContainerSize() - 1, 75, (numRows == 0 ? 24 : 26) + numRows * 18));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public ParaChestBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
