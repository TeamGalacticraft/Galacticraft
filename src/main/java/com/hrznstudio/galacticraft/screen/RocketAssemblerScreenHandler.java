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

package com.hrznstudio.galacticraft.screen;

import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.block.entity.RocketAssemblerBlockEntity;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketAssemblerScreenHandler extends ScreenHandler {

    public static final ContainerFactory<ScreenHandler> FACTORY = (syncId, id, player, buffer) -> {
        BlockPos pos = buffer.readBlockPos();
        BlockEntity be = player.world.getBlockEntity(pos);
        if (be instanceof RocketAssemblerBlockEntity) {
            return new RocketAssemblerScreenHandler(syncId, player, (RocketAssemblerBlockEntity) be);
        } else {
            return null;
        }
    };

    protected Inventory inventory;
    protected RocketAssemblerBlockEntity blockEntity;

    public RocketAssemblerScreenHandler(int syncId, PlayerEntity playerEntity, RocketAssemblerBlockEntity blockEntity) {
        super(null, syncId);
        this.blockEntity = blockEntity;
        this.inventory = new InventoryFixedWrapper(blockEntity.getInventory()) {
            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return RocketAssemblerScreenHandler.this.canUse(player);
            }
        };

        final int playerInvYOffset = 94;
        final int playerInvXOffset = 148;

        // Output slot
        this.addSlot(new Slot(this.inventory, RocketAssemblerBlockEntity.SCHEMATIC_INPUT_SLOT, 235, 19) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == GalacticraftItems.ROCKET_SCHEMATIC;
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return true;
            }
        });

        this.addSlot(new Slot(this.inventory, RocketAssemblerBlockEntity.ROCKET_OUTPUT_SLOT, 299, 19) {
            @Override
            public boolean canInsert(ItemStack itemStack_1) {
                return itemStack_1.getItem() == GalacticraftItems.ROCKET;
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity_1) {
                return true;
            }
        });

        this.addSlot(new Slot(this.inventory, RocketAssemblerBlockEntity.ENERGY_INPUT_SLOT, 156, 72) {
            @Override
            public boolean canInsert(ItemStack itemStack_1) {
                return GalacticraftEnergy.isEnergyItem(itemStack_1);
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity_1) {
                return true;
            }
        });

        // Hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerEntity.inventory, i, 8 + i * 18 + playerInvXOffset, playerInvYOffset + 58));
        }

        // Player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerEntity.inventory, j + i * 9 + 9, 8 + (j * 18) + playerInvXOffset, playerInvYOffset + i * 18));
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
