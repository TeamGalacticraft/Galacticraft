package dev.galacticraft.mod.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalLong;

@Mixin(DimensionType.class)
public class DimensionTypeMixin {
    @Unique private boolean isMoon;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void gc$initDimType(OptionalLong optionalLong, boolean bl, boolean bl2, boolean bl3, boolean bl4, double d, boolean bl5, boolean bl6, int i, int j, int k, TagKey tagKey, ResourceLocation resourceLocation, float f, DimensionType.MonsterSettings monsterSettings, CallbackInfo ci) {
        isMoon = resourceLocation.getPath().contains("moon");
    }

    @ModifyVariable(method = "timeOfDay", at = @At("HEAD"), index = 1, argsOnly = true)
    private long changeTime(long value) {
        if (isMoon) {
            return value / 16;
        }

        return value;
    }
}
