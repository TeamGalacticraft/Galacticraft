package dev.galacticraft.mod.attribute.item;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.compat.InventoryFixedWrapper;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import net.minecraft.entity.player.PlayerEntity;

public class MachineInvWrapper extends InventoryFixedWrapper {
    private final MachineBlockEntity machine;

    public MachineInvWrapper(MachineBlockEntity machine, FixedItemInv inv) {
        super(inv);
        this.machine = machine;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.machine.getConfiguration().getSecurity().hasAccess(player);
    }
}
