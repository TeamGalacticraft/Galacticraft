package io.github.teamgalacticraft.galacticraft.blocks.machines.compressor;

import alexiil.mc.lib.attributes.item.impl.PartialInventoryFixedWrapper;
import io.github.teamgalacticraft.galacticraft.container.ItemSpecificSlot;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CompressorContainer extends Container {
    private ItemStack itemStack;
    private Inventory inventory;

    private BlockPos blockPos;
    private CompressorBlockEntity generator;
    private PlayerEntity playerEntity;

    public CompressorContainer(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(null, syncId);
        this.blockPos = blockPos;
        this.playerEntity = playerEntity;

        BlockEntity blockEntity = playerEntity.world.getBlockEntity(blockPos);

        if (!(blockEntity instanceof CompressorBlockEntity)) {
            // TODO: Move this logic somewhere else to just not open this at all.
            throw new IllegalStateException("Found " + blockEntity + " instead of a coal generator!");
        }
        this.generator = (CompressorBlockEntity) blockEntity;
        this.inventory = new PartialInventoryFixedWrapper(generator.inventory) {
            @Override
            public void markDirty() {
                generator.markDirty();
            }

            @Override
            public boolean canPlayerUseInv(PlayerEntity player) {
                return CompressorContainer.this.canUse(player);
            }
        };

        // 3x3 comprerssor input grid
        int slot = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new Slot(this.inventory, slot, x * 18 + 19, y * 18 + 8));
                slot++;
            }
        }

        // Output slot
        this.addSlot(new Slot(this.inventory, 9, 138, 28) {
            @Override
            public boolean canInsert(ItemStack itemStack_1) {
                return false;
            }
        });
        // Fuel slot
        this.addSlot(new ItemSpecificSlot(this.inventory, 10, 3 * 18 + 1, 65, AbstractFurnaceBlockEntity.createFuelTimeMap().keySet().toArray(new Item[0])));

        // Player inventory slots
        int playerInvYOffset = 100;
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
    public boolean canUse(PlayerEntity playerEntity) {
        return true;
    }

    public class ChargeSlot extends Slot {

        public ChargeSlot(Inventory inventory, int slotId, int x, int y) {
            super(inventory, slotId, x, y);
        }

        @Override
        public boolean canInsert(ItemStack itemStack) {

            return itemStack.hasTag() && itemStack.getTag().containsKey("Energy");
        }
    }
}
