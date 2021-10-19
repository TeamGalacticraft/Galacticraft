/*
 * Copyright (c) 2019-2021 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.entity.damage.GalacticraftDamageSource;
import dev.galacticraft.mod.tag.GalacticraftTag;
import dev.galacticraft.mod.world.dimension.GalacticraftDimension;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract Vec3d getVelocity();

    @Shadow
    private float yaw;

    @Shadow
    private float pitch;

    @Shadow
    public World world;

    @Inject(method = "getTeleportTarget", at = @At("HEAD"), cancellable = true)
    private void getTeleportTargetGC(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir) {
        if (destination.getRegistryKey().equals(GalacticraftDimension.MOON) || this.world.getRegistryKey().equals(GalacticraftDimension.MOON)) { //TODO lander/parachute stuff
            BlockPos pos = destination.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, destination.getSpawnPos());
            cir.setReturnValue(new TeleportTarget(new Vec3d((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D), this.getVelocity(), this.yaw, this.pitch));
        }
    }

    @Shadow
    public abstract boolean updateMovementInFluid(Tag<Fluid> tag, double d);

    @Shadow
    public abstract boolean isOnFire();

    @Shadow
    private Vec3d pos;

    @Shadow
    protected UUID uuid;

    @Shadow
    public abstract boolean isPlayer();

    @Shadow
    private int id;

    @Shadow
    public abstract boolean isInvulnerable();

    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    @Inject(method = "checkWaterState", at = @At("TAIL"), cancellable = true)
    private void checkWaterStateGC(CallbackInfo ci) {
        if (this.updateMovementInFluid(GalacticraftTag.OIL, 0.0028d) || this.updateMovementInFluid(GalacticraftTag.FUEL, 0.0028d)) {
            if (this.isOnFire())
            {
                world.createExplosion(world.getEntityById(id), pos.x, pos.y, pos.z, 0f, Explosion.DestructionType.NONE);
                if ((this.isPlayer() && !world.getPlayerByUuid(uuid).isCreative()) || !this.isInvulnerable()) {
                    this.damage(GalacticraftDamageSource.OIL_BOOM, 20.0f);
                }
            }
        }
    }
}
