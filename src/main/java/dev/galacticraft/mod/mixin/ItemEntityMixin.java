package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.content.item.HotThrowableMeteorChunkItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract ItemStack getItem();

    @Shadow public abstract void setItem(ItemStack stack);

    @Inject(at = @At("RETURN"), method = "tick")
    void coolMeteorChunk(CallbackInfo ci) {
        ItemStack stack = this.getItem();
        CompoundTag tag = stack.getTag();
        if (stack.is(GCItems.HOT_THROWABLE_METEOR_CHUNK)) {
            if (tag != null) {
                int ticksUntilCool = tag.getInt(HotThrowableMeteorChunkItem.TICKS_UNTIL_COOL);
                if (ticksUntilCool == 0) {
                    this.setItem(new ItemStack(GCItems.THROWABLE_METEOR_CHUNK, stack.getCount()));
                }
                tag.putInt(HotThrowableMeteorChunkItem.TICKS_UNTIL_COOL, ticksUntilCool - 1);
            }

            if (this.isInWater()) {
                this.setItem(new ItemStack(GCItems.THROWABLE_METEOR_CHUNK, stack.getCount()));
                this.playEntityOnFireExtinguishedSound();
            }
        }
    }
}
