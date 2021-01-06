package com.hrznstudio.galacticraft.screen;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.entity.ElectricFurnaceBlockEntity;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.screen.slot.ChargeSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.Slot;

public class ElectricFurnaceScreenHandler extends MachineScreenHandler<ElectricFurnaceBlockEntity> {
    private final Property time = Property.create();
    private final Property maxTime = Property.create();

    protected ElectricFurnaceScreenHandler(int syncId, PlayerEntity playerEntity, ElectricFurnaceBlockEntity blockEntity) {
        super(syncId, playerEntity, blockEntity, GalacticraftScreenHandlerTypes.ELECTRIC_FURNACE_HANDLER);
        Inventory inventory = blockEntity.getInventory().asInventory();

        this.addSlot(new Slot(inventory, 0, 56, 25) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() != GalacticraftItems.BATTERY;
            }
        }); //in
        this.addSlot(new Slot(inventory, 1, 109, 25) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        }); //out

        this.addSlot(new ChargeSlot(inventory, 2, 8, 7)); //charge

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

        addProperty(time);
        addProperty(maxTime);
    }

    public ElectricFurnaceScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (ElectricFurnaceBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public void setProperty(int id, int value) {
        super.setProperty(id, value);
        blockEntity.cookTime = time.get();
        blockEntity.maxCookTime = maxTime.get();
    }

    @Override
    public void sendContentUpdates() {
        time.set(blockEntity.cookTime);
        maxTime.set(blockEntity.maxCookTime);
        super.sendContentUpdates();
    }
}
