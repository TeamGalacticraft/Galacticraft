package com.hrznstudio.galacticraft.screen;

import com.hrznstudio.galacticraft.block.entity.ElectricArcFurnaceBlockEntity;
import com.hrznstudio.galacticraft.block.entity.ElectricFurnaceBlockEntity;
import com.hrznstudio.galacticraft.screen.slot.ChargeSlot;
import com.hrznstudio.galacticraft.screen.slot.OutputSlot;
import com.hrznstudio.galacticraft.screen.slot.RecipeInputSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.Slot;

public class ElectricArcFurnaceScreenHandler extends MachineScreenHandler<ElectricArcFurnaceBlockEntity> {
    private final Property time = Property.create();
    private final Property maxTime = Property.create();

    protected ElectricArcFurnaceScreenHandler(int syncId, PlayerEntity playerEntity, ElectricArcFurnaceBlockEntity blockEntity) {
        super(syncId, playerEntity, blockEntity, GalacticraftScreenHandlerTypes.ELECTRIC_ARC_FURNACE_HANDLER);
        Inventory inventory = blockEntity.getInventory().asInventory();

        this.addSlot(new ChargeSlot(inventory, 0, 8, 7)); //charge
        this.addSlot(new RecipeInputSlot(inventory, 1, 56, 25, blockEntity.getWorld(), RecipeType.SMELTING)); //in
        this.addSlot(new OutputSlot(inventory, 2, 109, 25)); //out
        this.addSlot(new OutputSlot(inventory, 3, 127, 25)); //out


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

    public ElectricArcFurnaceScreenHandler(int syncId, PlayerInventory inv, PacketByteBuf buf) {
        this(syncId, inv.player, (ElectricArcFurnaceBlockEntity) inv.player.world.getBlockEntity(buf.readBlockPos()));
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
