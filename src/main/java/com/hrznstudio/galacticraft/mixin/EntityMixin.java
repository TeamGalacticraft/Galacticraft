package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.world.dimension.GalacticraftDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract Vec3d getVelocity();

    @Shadow public float yaw;

    @Shadow public float pitch;

    @Shadow public World world;

    @Inject(method = "getTeleportTarget", at = @At("HEAD"), cancellable = true)
    private void getTeleportTargetGC(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir) {
        if (destination.getRegistryKey().equals(GalacticraftDimensions.MOON) || this.world.getRegistryKey().equals(GalacticraftDimensions.MOON)) { //TODO lander/parachute stuff
            BlockPos pos = destination.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, destination.getSpawnPos());
            cir.setReturnValue(new TeleportTarget(new Vec3d((double)pos.getX() + 0.5D, pos.getY(), (double)pos.getZ() + 0.5D), this.getVelocity(), this.yaw, this.pitch));
        }
    }
}
