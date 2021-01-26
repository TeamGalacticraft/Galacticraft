package com.hrznstudio.galacticraft.screen;

import com.hrznstudio.galacticraft.block.entity.ElectricArcFurnaceBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.ChargeSlot;
import com.hrznstudio.galacticraft.screen.slot.OutputSlot;
import com.hrznstudio.galacticraft.screen.slot.RecipeInputSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.Property;

public class ElectricArcFurnaceScreenHandler extends MachineScreenHandler<ElectricArcFurnaceBlockEntity> {

    protected ElectricArcFurnaceScreenHandler(int syncId, PlayerEntity player, ElectricArcFurnaceBlockEntity machine) {
        super(syncId, player, machine, GalacticraftScreenHandlerTypes.ELECTRIC_ARC_FURNACE_HANDLER);
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
        this.addSlot(new ChargeSlot(machine.getWrappedInventory(), ElectricArcFurnaceBlockEntity.CHARGE_SLOT, 8, 7));
        this.addSlot(new RecipeInputSlot<>(machine.getWrappedInventory(), ElectricArcFurnaceBlockEntity.INPUT_SLOT, 56, 25, machine.getWorld(), RecipeType.SMELTING));
        this.addSlot(new OutputSlot(machine.getWrappedInventory(), ElectricArcFurnaceBlockEntity.OUTPUT_SLOT_1, 109, 25));
        this.addSlot(new OutputSlot(machine.getWrappedInventory(), ElectricArcFurnaceBlockEntity.OUTPUT_SLOT_2, 127, 25));
        this.addPlayerInventorySlots(0, 84);
    }

    public ElectricArcFurnaceScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (ElectricArcFurnaceBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
    }
}
