package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.accessor.ServerPlayerEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements ServerPlayerEntityAccessor {
    @Shadow
    @Final
    public MinecraftServer server;

    @Unique
    private double researchScrollX = 0.0D;
    @Unique
    private double researchScrollY = 0.0D;

    @Inject(at = @At("RETURN"), method = "writeCustomDataToTag")
    private void writeCustomDataGC(CompoundTag tag, CallbackInfo ci) {
        tag.putDouble("gc_research_x", researchScrollX);
        tag.putDouble("gc_research_y", researchScrollY);
    }

    @Inject(at = @At("RETURN"), method = "readCustomDataFromTag")
    private void readCustomDataGC(CompoundTag tag, CallbackInfo ci) {
        this.researchScrollX = tag.getDouble("gc_research_x");
        this.researchScrollY = tag.getDouble("gc_research_y");
    }

    @Override
    public double getResearchScrollX() {
        return researchScrollX;
    }

    @Override
    public double getResearchScrollY() {
        return researchScrollY;
    }

    @Override
    public void setResearchScroll(double x, double y) {
        this.researchScrollX = x;
        this.researchScrollY = y;
    }
}
