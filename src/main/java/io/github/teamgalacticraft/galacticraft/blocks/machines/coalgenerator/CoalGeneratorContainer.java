package io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator;

import alexiil.mc.lib.attributes.item.impl.PartialInventoryFixedWrapper;
import io.github.teamgalacticraft.galacticraft.container.slot.ChargeSlot;
import io.github.teamgalacticraft.galacticraft.container.slot.ItemSpecificSlot;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CoalGeneratorContainer extends Container {

    private static Item[] fuel = new Item[]{Items.COAL_BLOCK, Items.COAL, Items.CHARCOAL};
    private ItemStack itemStack;
    private Inventory inventory;
    private BlockPos blockPos;
    private CoalGeneratorBlockEntity generator;
    private PlayerEntity playerEntity;

    public CoalGeneratorContainer(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(null, syncId);
        this.blockPos = blockPos;
        this.playerEntity = playerEntity;

        BlockEntity blockEntity = playerEntity.world.getBlockEntity(blockPos);

        if (!(blockEntity instanceof CoalGeneratorBlockEntity)) {
            // TODO: Move this logic somewhere else to just not open this at all.
            throw new IllegalStateException("Found " + blockEntity + " instead of a coal generator!");
        }
        this.generator = (CoalGeneratorBlockEntity) blockEntity;
        this.inventory = new PartialInventoryFixedWrapper(generator.getInventory()) {
            @Override
            public void markDirty() {
                generator.markDirty();
            }

            @Override
            public boolean canPlayerUseInv(PlayerEntity player) {
                return CoalGeneratorContainer.this.canUse(player);
            }
        };
        // Coal Generator fuel slot
        this.addSlot(new ItemSpecificSlot(this.inventory, 0, 8, 72, fuel));
        this.addSlot(new ChargeSlot(this.inventory, 1, 8, 8));

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
    public ItemStack transferSlot(PlayerEntity playerEntity, int slotId) {

        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slotList.get(slotId);

        if (slot != null && slot.hasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (itemStack.isEmpty()) {
                return itemStack;
            }

            if (slotId < this.generator.getInventory().getSlotCount()) {

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