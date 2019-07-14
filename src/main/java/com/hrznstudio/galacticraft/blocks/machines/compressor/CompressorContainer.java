package com.hrznstudio.galacticraft.blocks.machines.compressor;

import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import com.hrznstudio.galacticraft.blocks.machines.MachineContainer;
import com.hrznstudio.galacticraft.blocks.machines.electriccompressor.ElectricCompressorBlockEntity;
import com.hrznstudio.galacticraft.container.slot.ItemSpecificSlot;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.FurnaceOutputSlot;
import net.minecraft.container.Property;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CompressorContainer extends MachineContainer<CompressorBlockEntity> {

    public static final ContainerFactory<Container> FACTORY = createFactory(CompressorBlockEntity.class, CompressorContainer::new);

    protected Inventory inventory;
    protected int outputSlotId = 0;
    private ItemStack itemStack;

    public final Property status = Property.create();
    public final Property progress = Property.create();
    public final Property fuelTime = Property.create();

    public CompressorContainer(int syncId, PlayerEntity playerEntity, CompressorBlockEntity blockEntity) {
        super(syncId, playerEntity, blockEntity);
        this.inventory = new InventoryFixedWrapper(blockEntity.getInventory()) {
            @Override
            public boolean canPlayerUseInv(PlayerEntity player) {
                return CompressorContainer.this.canUse(player);
            }
        };
        addProperty(status);
        addProperty(progress);
        addProperty(fuelTime);

        // 3x3 comprerssor input grid
        int slot = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new Slot(this.inventory, slot, x * 18 + 19, y * 18 + 18));
                slot++;
            }
        }

        // Fuel slot
        this.addSlot(new ItemSpecificSlot(this.inventory, CompressorBlockEntity.FUEL_INPUT_SLOT, 3 * 18 + 1, 75, AbstractFurnaceBlockEntity.createFuelTimeMap().keySet().toArray(new Item[0])));

        // Output slot
        this.addSlot(new FurnaceOutputSlot(playerEntity, this.inventory, CompressorBlockEntity.OUTPUT_SLOT, getOutputSlotPos()[0], getOutputSlotPos()[1]));

        // Player inventory slots
        int playerInvYOffset = getPlayerInvYOffset();
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

    protected int[] getOutputSlotPos() {
        return new int[]{138, 38};
    }

    protected int getPlayerInvYOffset() {
        return 110;
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

            if (slotId < this.blockEntity.getInventory().getSlotCount()) {

                if (!this.insertItem(itemStack1, this.inventory.getInvSize(), this.slotList.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack1, 0, this.inventory.getInvSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack1.getCount() == 0) {
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

    @Override
    public void sendContentUpdates() {
        status.set(blockEntity.status.ordinal());
        progress.set(blockEntity.getProgress());
        fuelTime.set(blockEntity.fuelTime);
        super.sendContentUpdates();
    }
    
    @Override
    public void setProperties(int index, int value) {
        super.setProperties(index, value);
        blockEntity.status = CompressorStatus.get(status.get());
        blockEntity.progress = progress.get();
        blockEntity.fuelTime = fuelTime.get();
    }
}
