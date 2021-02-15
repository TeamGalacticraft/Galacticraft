package com.hrznstudio.galacticraft.accessor;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import net.minecraft.nbt.CompoundTag;

public interface GearInventoryProvider {
    FixedItemInv getGearInv();

    CompoundTag writeGearToNbt(CompoundTag tag);

    void readGearFromNbt(CompoundTag tag);
}
