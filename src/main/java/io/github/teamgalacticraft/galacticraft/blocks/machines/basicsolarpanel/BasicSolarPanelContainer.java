package io.github.teamgalacticraft.galacticraft.blocks.machines.basicsolarpanel;

import alexiil.mc.lib.attributes.item.impl.PartialInventoryFixedWrapper;
import io.github.teamgalacticraft.galacticraft.container.slot.ItemSpecificSlot;
import io.github.teamgalacticraft.galacticraft.items.GalacticraftItems;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class BasicSolarPanelContainer extends Container {
    private final Inventory inventory;
    private BlockPos blockPos;
    private BasicSolarPanelBlockEntity solarPanel;
    private PlayerEntity playerEntity;

    public BasicSolarPanelContainer(int syncId, BlockPos blockPos, PlayerEntity playerEntity) {
        super(null, syncId);
        this.blockPos = blockPos;
        this.playerEntity = playerEntity;

        BlockEntity blockEntity = playerEntity.world.getBlockEntity(blockPos);

        if (!(blockEntity instanceof BasicSolarPanelBlockEntity)) {
            // TODO: Move this logic somewhere else to just not open this at all.
            throw new IllegalStateException("Found " + blockEntity + " instead of a solar panel!");
        }
        this.solarPanel = (BasicSolarPanelBlockEntity) blockEntity;
        this.inventory = new PartialInventoryFixedWrapper(solarPanel.getInventory()) {
            @Override
            public void markDirty() {
                solarPanel.markDirty();
            }

            @Override
            public boolean canPlayerUseInv(PlayerEntity player) {
                return BasicSolarPanelContainer.this.canUse(player);
            }
        };

        // Coal Generator fuel slot
        this.addSlot(new ItemSpecificSlot(this.inventory, 0, 8, 53, GalacticraftItems.BATTERY));

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
    public ItemStack transferSlot(PlayerEntity playerEntity, int slotId) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slotList.get(slotId);

        if (slot != null && slot.hasStack()) {
            ItemStack itemStack1 = slot.getStack();
            itemStack = itemStack1.copy();

            if (itemStack.isEmpty()) {
                return itemStack;
            }

            if (slotId < this.solarPanel.getInventory().getSlotCount()) {
                if (!this.insertItem(itemStack1, this.solarPanel.getInventory().getSlotCount(), this.slotList.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack1, 0, this.solarPanel.getInventory().getSlotCount(), false)) {
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
