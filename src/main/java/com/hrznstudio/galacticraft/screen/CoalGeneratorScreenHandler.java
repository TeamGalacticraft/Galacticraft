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
import com.hrznstudio.galacticraft.block.entity.CoalGeneratorBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.ChargeSlot;
import com.hrznstudio.galacticraft.screen.slot.ItemSpecificSlot;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CoalGeneratorScreenHandler extends MachineScreenHandler<CoalGeneratorBlockEntity> {

    private static final Item[] fuel = new Item[]{Items.COAL_BLOCK, Items.COAL, Items.CHARCOAL, Items.AIR};
    public static final ContainerFactory<ScreenHandler> FACTORY = createFactory(CoalGeneratorBlockEntity.class, CoalGeneratorScreenHandler::new);
    public final Property status = Property.create();

    public CoalGeneratorScreenHandler(int syncId, PlayerEntity playerEntity, CoalGeneratorBlockEntity generator) {
        super(syncId, playerEntity, generator);
        Inventory inventory = new InventoryFixedWrapper(generator.getInventory()) {
            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return CoalGeneratorScreenHandler.this.canUse(player);
            }
        };
        addProperty(status);
        // Coal Generator fuel slot
        this.addSlot(new ItemSpecificSlot(inventory, 0, 8, 72, fuel));
        this.addSlot(new ChargeSlot(inventory, 1, 8, 8));

        // Player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerEntity.inventory, j + i * 9 + 9, 8 + j * 18, 94 + i * 18));
            }
        }

        // Hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerEntity.inventory, i, 8 + i * 18, 152));
        }

    }

    @Override
    public void sendContentUpdates() {
        status.set(blockEntity.status.ordinal());
        super.sendContentUpdates();
    }

    @Override
    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        blockEntity.status = CoalGeneratorBlockEntity.CoalGeneratorStatus.get(status.get());
    }
}
