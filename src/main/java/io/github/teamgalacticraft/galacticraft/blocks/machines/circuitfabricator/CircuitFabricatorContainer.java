package io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator;

import alexiil.mc.lib.attributes.item.impl.PartialInventoryFixedWrapper;
import io.github.teamgalacticraft.galacticraft.container.ItemSpecificSlot;
import io.github.teamgalacticraft.galacticraft.items.GalacticraftItems;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.FurnaceOutputSlot;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public class CircuitFabricatorContainer extends Container {

    private Inventory inventory;

    private BlockPos blockPos;
    private CircuitFabricatorBlockEntity fabricator;
    private PlayerEntity playerEntity;

    private static Item[] materials = new Item[] {Items.LAPIS_LAZULI, Items.REDSTONE_TORCH, Items.REPEATER};


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
        this.inventory = new PartialInventoryFixedWrapper(fabricator.inventory) {
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
        this.addSlot(new ItemSpecificSlot(this.inventory, 0, 8, 53, GalacticraftItems.BATTERY));
        this.addSlot(new ItemSpecificSlot(this.inventory, 1, 8, -11, Items.DIAMOND));
        this.addSlot(new ItemSpecificSlot(this.inventory, 2, 8 + (18 * 3), 53, GalacticraftItems.RAW_SILICON));
        this.addSlot(new ItemSpecificSlot(this.inventory, 3, 8 + (18 * 3), 53 - 18, GalacticraftItems.RAW_SILICON));
        this.addSlot(new ItemSpecificSlot(this.inventory, 4, 8 + (18 * 6), 53 - 18, Items.REDSTONE));
        this.addSlot(new ItemSpecificSlot(this.inventory, 5, 8 + (18 * 7), -11, materials));
        this.addSlot(new FurnaceOutputSlot(playerEntity, this.inventory, 6, 8 + (18 * 8), 53));


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

}
