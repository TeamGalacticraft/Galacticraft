/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.mod.content.entity.damage.GCDamageSources;
import dev.galacticraft.mod.data.GCTags;
import dev.galacticraft.mod.world.dimension.GCDimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    private float yRot;

    @Shadow
    private float xRot;

    @Shadow
    public Level level;

    @Inject(method = "findDimensionEntryPoint", at = @At("HEAD"), cancellable = true)
    private void getTeleportTargetGC(ServerLevel destination, CallbackInfoReturnable<PortalInfo> cir) {
        if (destination.dimension().equals(GCDimensionType.MOON_KEY) || this.level.dimension().equals(GCDimensionType.MOON_KEY)) { //TODO lander/parachute stuff
            BlockPos pos = destination.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, destination.getSharedSpawnPos());
            cir.setReturnValue(new PortalInfo(new Vec3((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D), this.getDeltaMovement(), this.yRot, this.xRot));
        }
    }

    @Shadow
    public abstract boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tag, double d);

    @Shadow
    public abstract boolean isOnFire();

    @Shadow
    private Vec3 position;

    @Shadow
    protected UUID uuid;

    @Shadow
    public abstract boolean isAlwaysTicking();

    @Shadow
    private int id;

    @Shadow
    public abstract boolean isInvulnerable();

    @Shadow
    public abstract boolean hurt(DamageSource source, float amount);

    @Inject(method = "updateInWaterStateAndDoWaterCurrentPushing", at = @At("TAIL"), cancellable = true)
    private void checkWaterStateGC(CallbackInfo ci) {
        if (this.updateFluidHeightAndDoFluidPushing(GCTags.OIL, 0.0028d) || this.updateFluidHeightAndDoFluidPushing(GCTags.FUEL, 0.0028d)) {
            if (this.isOnFire())
            {
                level.explode(level.getEntity(id), position.x, position.y, position.z, 0f, Explosion.BlockInteraction.NONE);
                if ((this.isAlwaysTicking() && !level.getPlayerByUUID(uuid).isCreative()) || !this.isInvulnerable()) {
                    this.hurt(GCDamageSources.OIL_BOOM, 20.0f);
                }
            }
        }
    }
}
