/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.attachments;

import dev.galacticraft.mod.misc.footprint.Footprint;
import dev.galacticraft.mod.tag.GCBlockTags;
import dev.galacticraft.mod.tag.GCDimensionTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class GCFootprintTracker {
    private double distanceSinceLastStep;
    private int lastStep;

    public double getDistanceSinceLastStep() {
        return this.distanceSinceLastStep;
    }

    public void setDistanceSinceLastStep(double distanceSinceLastStep) {
        this.distanceSinceLastStep = distanceSinceLastStep;
    }

    public int getLastStep() {
        return this.lastStep;
    }

    public void setLastStep(int lastStep) {
        this.lastStep = lastStep;
    }

    public void tickFootprints(Entity entity, MoverType type, Vec3 motion) {
        double motionSqrd = Mth.lengthSquared(motion.x, motion.z);
        Level level = entity.level();

        // If the player is on the moon, not airbourne and not riding anything
        boolean isFlying = false;
        if ((Object) this instanceof Player player)
            isFlying = player.getAbilities().flying;
        if (motionSqrd > 0.001 && level.dimensionTypeRegistration().is(GCDimensionTypeTags.FOOTPRINTS_DIMENSIONS) && entity.getVehicle() == null && !isFlying) {
            // If it has been long enough since the last step
            if (getDistanceSinceLastStep() > 0.35) {
                Vector3d pos = new Vector3d(entity.getX(), Math.floor(entity.getY()), entity.getZ());

                // Adjust footprint to left or right depending on step count
                switch (getLastStep()) {
                    case 0:
                        pos.add(new Vector3d(Math.sin(Math.toRadians(-entity.getYRot() + 90)) * 0.25, 0, Math.cos(Math.toRadians(-entity.getYRot() + 90)) * 0.25));
                        break;
                    case 1:
                        pos.add(new Vector3d(Math.sin(Math.toRadians(-entity.getYRot() - 90)) * 0.25, 0, Math.cos(Math.toRadians(-entity.getYRot() - 90)) * 0.25));
                        break;
                }

                pos = Footprint.getFootprintPosition(level, entity.getYRot() - 180, pos, entity.position());

                int iPosX = Mth.floor(pos.x);
                int iPosY = Mth.floor(pos.y - 0.05);
                int iPosZ = Mth.floor(pos.z);
                BlockPos blockPos = new BlockPos(iPosX, iPosY, iPosZ);
                BlockState state = level.getBlockState(blockPos);

                // If the block below is the moon block
                if (state.is(GCBlockTags.FOOTPRINTS)) {
                    long chunkKey = ChunkPos.asLong(SectionPos.blockToSectionCoord(pos.x), SectionPos.blockToSectionCoord(pos.z));
                    level.getAttachedOrThrow(GCAttachments.FOOTPRINT_MANAGER).addFootprint(chunkKey, new Footprint(level.dimensionTypeRegistration().unwrapKey().get().location(), pos, entity.getYRot(), entity.getUUID()));
                }

                // Increment and cap step counter at 1
                setLastStep((getLastStep() + 1) % 2);
                setDistanceSinceLastStep(0);
            } else {
                setDistanceSinceLastStep(getDistanceSinceLastStep() + motionSqrd);
            }
        }
    }
}
