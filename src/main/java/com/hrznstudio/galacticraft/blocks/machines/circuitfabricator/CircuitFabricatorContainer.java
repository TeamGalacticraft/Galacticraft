package com.hrznstudio.galacticraft.blocks.machines.circuitfabricator;

import alexiil.mc.lib.attributes.item.impl.PartialInventoryFixedWrapper;
import com.hrznstudio.galacticraft.container.slot.ChargeSlot;
import com.hrznstudio.galacticraft.container.slot.ItemSpecificSlot;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.FurnaceOutputSlot;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CircuitFabricatorContainer extends Container {

    //TODO not use this. recipes are added with json so we cant hardcode this anymore really.
    public static Item[] materials = new Item[]{Items.LAPIS_LAZULI, Items.REDSTONE_TORCH, Items.REPEATER, GalacticraftItems.SOLAR_DUST};
    private Inventory inventory;
    private BlockPos blockPos;
    private CircuitFabricatorBlockEntity fabricator;
    private PlayerEntity playerEntity;


    public CircuitFabricatorContainer(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(null, syncId);
        this.blockPos = blockPos;
        this.playerEntity = playerEntity;

        BlockEntity blockEntity = playerEntity.world.getBlockEntity(blockPos);

        if (!(blockEntity instanceof CircuitFabricatorBlockEntity)) {
            // TODO: Move this logic somewhere else to just not open this at all.
            throw new IllegalStateException("Found " + blockEntity + " instead of a circuit fabricator!");
        }
        this.fabricator = (CircuitFabricatorBlockEntity) blockEntity;
        this.inventory = new PartialInventoryFixedWrapper(fabricator.getInventory()) {
            @Override
            public void markDirty() {
                fabricator.markDirty();
            }

            @Override
            public boolean canPlayerUseInv(PlayerEntity player) {
                return CircuitFabricatorContainer.this.canUse(player);
            }
        };
        // Energy slot
        this.addSlot(new ChargeSlot(this.inventory, 0, 8, 79));
        this.addSlot(new ItemSpecificSlot(this.inventory, 1, 8, 15, Items.DIAMOND));
        this.addSlot(new ItemSpecificSlot(this.inventory, 2, 8 + (18 * 3), 79, GalacticraftItems.RAW_SILICON));
        this.addSlot(new ItemSpecificSlot(this.inventory, 3, 8 + (18 * 3), 79 - 18, GalacticraftItems.RAW_SILICON));
        this.addSlot(new ItemSpecificSlot(this.inventory, 4, 8 + (18 * 6), 79 - 18, Items.REDSTONE));
        this.addSlot(new ItemSpecificSlot(this.inventory, 5, 8 + (18 * 7), 15, materials));
        this.addSlot(new FurnaceOutputSlot(playerEntity, this.inventory, 6, 8 + (18 * 8), 79));


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
        Slot slot = this.slotList.get(slotId);

        if (slot != null && slot.hasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (itemStack.isEmpty()) {
                return itemStack;
            }

            if (slotId < this.fabricator.getInventory().getSlotCount()) {

                if (!this.insertItem(itemStack1, this.inventory.getInvSize(), this.slotList.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack1, 0, this.inventory.getInvSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack1.getAmount() == 0) {
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

}
