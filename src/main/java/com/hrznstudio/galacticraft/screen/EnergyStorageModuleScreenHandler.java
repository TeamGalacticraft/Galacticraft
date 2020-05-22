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
import com.hrznstudio.galacticraft.block.entity.EnergyStorageModuleBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.ChargeSlot;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class EnergyStorageModuleScreenHandler extends MachineScreenHandler<EnergyStorageModuleBlockEntity> {
    public static final ContainerFactory<ScreenHandler> FACTORY = createFactory(EnergyStorageModuleBlockEntity.class, EnergyStorageModuleScreenHandler::new);

    public EnergyStorageModuleScreenHandler(int syncId, PlayerEntity playerEntity, EnergyStorageModuleBlockEntity blockEntity) {
        super(syncId, playerEntity, blockEntity);
        Inventory inventory = new InventoryFixedWrapper(blockEntity.getInventory()) {
            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return EnergyStorageModuleScreenHandler.this.canUse(player);
            }
        };

        // Battery slots
        this.addSlot(new ChargeSlot(inventory, 0, 18 * 6 - 6, 18 + 6));
        this.addSlot(new ChargeSlot(inventory, 1, 18 * 6 - 6, 18 * 2 + 12));

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
}