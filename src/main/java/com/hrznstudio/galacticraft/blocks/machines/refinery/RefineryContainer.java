package com.hrznstudio.galacticraft.blocks.machines.refinery;

import alexiil.mc.lib.attributes.item.impl.PartialInventoryFixedWrapper;
import com.hrznstudio.galacticraft.container.slot.ChargeSlot;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class RefineryContainer extends Container {


    private ItemStack itemStack;
    private Inventory inventory;

    private BlockPos blockPos;
    private RefineryBlockEntity refinery;
    private PlayerEntity playerEntity;

    public RefineryContainer(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(null, syncId);
        this.blockPos = blockPos;
        this.playerEntity = playerEntity;

        BlockEntity blockEntity = playerEntity.world.getBlockEntity(blockPos);

        if (!(blockEntity instanceof RefineryBlockEntity)) {
            // TODO: Move this logic somewhere else to just not open this at all.
            throw new IllegalStateException("Found " + blockEntity + " instead of an OxygenCollectorBlockEntity!");
        }
        this.refinery = (RefineryBlockEntity) blockEntity;
        this.inventory = new PartialInventoryFixedWrapper(refinery.getInventory()) {
            @Override
            public void markDirty() {
                refinery.markDirty();
            }

            @Override
            public boolean canPlayerUseInv(PlayerEntity player) {
                return RefineryContainer.this.canUse(player);
            }
        };

        // Oxy tank slot
//        this.addSlot(new OxygenTankSlot(this.inventory, 0, 18 * 2 - 3, 18 * 1 + 6));
        // Oxy tank slot
        this.addSlot(new ChargeSlot(this.inventory, 0, 20, 70));

        // Player inventory slots
        int playerInvYOffset = 99;

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

            if (slotId < this.refinery.getInventory().getSlotCount()) {

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
