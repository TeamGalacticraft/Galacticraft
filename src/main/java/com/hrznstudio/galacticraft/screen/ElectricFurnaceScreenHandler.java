package com.hrznstudio.galacticraft.screen;

import com.hrznstudio.galacticraft.block.entity.ElectricFurnaceBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.FilteredSlot;
import com.hrznstudio.galacticraft.screen.slot.OutputSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.Slot;

public class ElectricFurnaceScreenHandler extends MachineScreenHandler<ElectricFurnaceBlockEntity> {
    private final Property time = Property.create();
    private final Property maxTime = Property.create();

    protected ElectricFurnaceScreenHandler(int syncId, PlayerEntity player, ElectricFurnaceBlockEntity machine) {
        super(syncId, player, machine, GalacticraftScreenHandlerTypes.ELECTRIC_FURNACE_HANDLER);

        this.addSlot(new FilteredSlot(machine, machine.getWrappedInventory(), ElectricFurnaceBlockEntity.CHARGE_SLOT, 8, 7)); //charge
        this.addSlot(new FilteredSlot(machine, machine.getWrappedInventory(), ElectricFurnaceBlockEntity.INPUT_SLOT, 56, 25)); //in
        this.addSlot(new OutputSlot(machine.getWrappedInventory(), ElectricFurnaceBlockEntity.OUTPUT_SLOT, 109, 25)); //out
        this.addPlayerInventorySlots(0, 84);

        this.addProperty(new Property() {
            @Override
            public int get() {
                return machine.cookTime;
            }

            @Override
            public void set(int value) {
                machine.cookTime = value;
            }
        });

        this.addProperty(new Property() {
            @Override
            public int get() {
                return machine.cookLength;
            }

            @Override
            public void set(int value) {
                machine.cookLength = value;
            }
        });
    }

    public ElectricFurnaceScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (ElectricFurnaceBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }
}
