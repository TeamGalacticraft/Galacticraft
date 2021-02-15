package com.hrznstudio.galacticraft.mixin;

import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import com.hrznstudio.galacticraft.accessor.GearInventoryProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements GearInventoryProvider {
    private @Unique FullFixedItemInv gearInv = new FullFixedItemInv(6);

    @Override
    public FullFixedItemInv getGearInv() {
        return gearInv;
    }

    @Override
    public CompoundTag writeGearToNbt(CompoundTag tag) {
        return getGearInv().toTag(tag);
    }

    @Override
    public void readGearFromNbt(CompoundTag tag) {
        getGearInv().fromTag(tag);
    }
}
