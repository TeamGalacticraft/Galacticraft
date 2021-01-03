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

import com.hrznstudio.galacticraft.block.entity.CircuitFabricatorBlockEntity;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.screen.slot.ChargeSlot;
import com.hrznstudio.galacticraft.screen.slot.ItemSpecificSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CircuitFabricatorScreenHandler extends MachineScreenHandler<CircuitFabricatorBlockEntity> {

    //TODO not use this. recipes are added with json so we cant hardcode this anymore really.
    public static final Item[] materials = new Item[]{Items.LAPIS_LAZULI, Items.REDSTONE_TORCH, Items.REPEATER, GalacticraftItems.SOLAR_DUST};
    public final Property progress = Property.create();

    public CircuitFabricatorScreenHandler(int syncId, PlayerEntity playerEntity, CircuitFabricatorBlockEntity blockEntity) {
        super(syncId, playerEntity, blockEntity, GalacticraftScreenHandlerTypes.CIRCUIT_FABRICATOR_HANDLER);
        addProperty(progress);
        Inventory inventory = blockEntity.getInventory().asInventory();
        // Energy slot
        this.addSlot(new ChargeSlot(inventory, 0, 8, 86));
        this.addSlot(new ItemSpecificSlot(inventory, 1, 8, 8, Items.DIAMOND));
        this.addSlot(new ItemSpecificSlot(inventory, 2, 62, 68, GalacticraftItems.RAW_SILICON));
        this.addSlot(new ItemSpecificSlot(inventory, 3, 62, 86, GalacticraftItems.RAW_SILICON));
        this.addSlot(new ItemSpecificSlot(inventory, 4, 116, 68, Items.REDSTONE));
        this.addSlot(new ItemSpecificSlot(inventory, 5, 134, 8, materials));
        this.addSlot(new FurnaceOutputSlot(playerEntity, inventory, 6, 152, 86));


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

    public CircuitFabricatorScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (CircuitFabricatorBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public void sendContentUpdates() {
        progress.set(blockEntity.getProgress());
        super.sendContentUpdates();
    }

    @Override
    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        blockEntity.progress = progress.get();
    }
}
