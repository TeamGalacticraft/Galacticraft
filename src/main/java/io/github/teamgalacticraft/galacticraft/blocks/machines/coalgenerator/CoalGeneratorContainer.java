package io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.lib.attributes.item.impl.PartialInventoryFixedWrapper;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CoalGeneratorContainer extends Container {

    private ItemStack itemStack;
    private Inventory inventory;

    private BlockPos blockPos;
    private PlayerEntity playerEntity;

    @Override
    public ItemStack transferSlot(PlayerEntity playerEntity_1, int int_1) {
        ItemStack itemStack_1 = ItemStack.EMPTY;
        Slot slot_1 = this.slotList.get(int_1);
        if (slot_1 != null && slot_1.hasStack()) {
            return slot_1.getStack();
        }
        return itemStack_1;
    }

    public CoalGeneratorContainer(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(null, syncId);
        this.blockPos = blockPos;
        this.playerEntity = playerEntity;

        BlockEntity blockEntity = playerEntity.world.getBlockEntity(blockPos);
        if (!(blockEntity instanceof CoalGeneratorBlockEntity)) {
            // TODO: Move this logic somewhere else to just not open this at all.
            throw new IllegalStateException("Found " + blockEntity + " instead of a coal generator!");
        }
        CoalGeneratorBlockEntity generator = (CoalGeneratorBlockEntity)blockEntity;
        Inventory inv = new PartialInventoryFixedWrapper(generator.inventory) {
            @Override
            public void markDirty() {
                generator.markDirty();
            }
            @Override
            public boolean canPlayerUseInv(PlayerEntity player) {
                return CoalGeneratorContainer.this.canUse(player);
            }
        };
        this.addSlot(new CoalGeneratorFuelSlot(this, inv, 0, 8, 53));

        // Player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerEntity.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerEntity.inventory, i, 8 + i * 18, 142));
        }

    }

    @Override
    public boolean canUse(PlayerEntity playerEntity) {
        return true;
    }

    public class CoalGeneratorFuelSlot extends Slot {
        private final CoalGeneratorContainer container;

        public CoalGeneratorFuelSlot(CoalGeneratorContainer container, Inventory inventory, int int_1, int int_2, int int_3) {
            super(inventory, int_1, int_2, int_3);
            this.container = container;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return true;
        }
    }
}
