package com.hrznstudio.galacticraft.blocks.machines.circuitfabricator;

import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import com.hrznstudio.galacticraft.blocks.machines.MachineContainer;
import com.hrznstudio.galacticraft.container.slot.ChargeSlot;
import com.hrznstudio.galacticraft.container.slot.ItemSpecificSlot;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.container.Container;
import net.minecraft.container.FurnaceOutputSlot;
import net.minecraft.container.Property;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CircuitFabricatorContainer extends MachineContainer<CircuitFabricatorBlockEntity> {

    //TODO not use this. recipes are added with json so we cant hardcode this anymore really.
    public static Item[] materials = new Item[]{Items.LAPIS_LAZULI, Items.REDSTONE_TORCH, Items.REPEATER, GalacticraftItems.SOLAR_DUST};
    public static final ContainerFactory<Container> FACTORY = createFactory(CircuitFabricatorBlockEntity.class, CircuitFabricatorContainer::new);
    public final Property progress = Property.create();
    private final Property status = Property.create();
    private Inventory inventory;

    public CircuitFabricatorContainer(int syncId, PlayerEntity playerEntity, CircuitFabricatorBlockEntity blockEntity) {
        super(syncId, playerEntity, blockEntity);
        addProperty(progress);
        addProperty(status);
        this.inventory = new InventoryFixedWrapper(blockEntity.getInventory()) {
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
        progress.set(blockEntity.getProgress());
        status.set(blockEntity.status.ordinal());
        super.sendContentUpdates();
    }

    @Override
    public void setProperties(int index, int value) {
        super.setProperties(index, value);
        blockEntity.progress = progress.get();
        blockEntity.status = CircuitFabricatorStatus.values()[status.get()];
    }
}
