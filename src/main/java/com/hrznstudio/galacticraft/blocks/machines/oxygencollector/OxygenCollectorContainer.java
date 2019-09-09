package com.hrznstudio.galacticraft.blocks.machines.oxygencollector;

import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import com.hrznstudio.galacticraft.blocks.machines.MachineContainer;
import com.hrznstudio.galacticraft.container.slot.ChargeSlot;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.container.Container;
import net.minecraft.container.Property;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class OxygenCollectorContainer extends MachineContainer<OxygenCollectorBlockEntity> {
    public static final ContainerFactory<Container> FACTORY = createFactory(OxygenCollectorBlockEntity.class, OxygenCollectorContainer::new);
    public final Property status = Property.create();
    public final Property oxygen = Property.create();
    public final Property lastCollectAmount = Property.create();
    private ItemStack itemStack;
    private Inventory inventory;

    public OxygenCollectorContainer(int syncId, PlayerEntity playerEntity, OxygenCollectorBlockEntity blockEntity) {
        super(syncId, playerEntity, blockEntity);
        this.inventory = new InventoryFixedWrapper(blockEntity.getInventory()) {
            @Override
            public boolean canPlayerUseInv(PlayerEntity player) {
                return OxygenCollectorContainer.this.canUse(player);
            }
        };
        addProperty(status);
        addProperty(oxygen);
        addProperty(lastCollectAmount);

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
        oxygen.set(blockEntity.getOxygen().getCurrentEnergy());
        lastCollectAmount.set(blockEntity.lastCollectAmount);
        super.sendContentUpdates();
    }

    @Override
    public void setProperties(int index, int value) {
        super.setProperties(index, value);
        blockEntity.status = CollectorStatus.get(status.get());
    }
}
