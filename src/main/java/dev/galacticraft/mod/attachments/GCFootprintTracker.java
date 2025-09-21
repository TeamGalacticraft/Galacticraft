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
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
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
        if ((Object) this instanceof Player player && player.getAbilities().flying) {
            return;
        } else if (entity.getVehicle() != null) {
            return;
        }
        Level level = entity.level();
        // The entity has footprints, is not flying and is not riding anything

        double motionSqrd = motion.horizontalDistanceSqr();
        Holder<DimensionType> dimensionType = level.dimensionTypeRegistration();

        // Check that the entity is moving fast enough and is in a footprint dimension
        if (motionSqrd > 0.001D && dimensionType.is(GCDimensionTypeTags.FOOTPRINTS_DIMENSIONS)) {
            // If it has been long enough since the last step
            if (this.getDistanceSinceLastStep() > 0.35D) {
                float rotation = entity.getYRot() * Mth.DEG_TO_RAD;

                // Set the footprint position to the block below
                Vector3d pos = new Vector3d(
                        entity.getX() + getLastStep() * Mth.cos(rotation) * 0.25D,
                        Math.floor(entity.getY()),
                        entity.getZ() + getLastStep() * Mth.sin(rotation) * 0.25D
                );
                pos = Footprint.getFootprintPosition(level, rotation - Mth.PI, pos, entity.position());

                BlockPos blockPos = new BlockPos(Mth.floor(pos.x), Mth.floor(pos.y - 0.05D), Mth.floor(pos.z));
                BlockState state = level.getBlockState(blockPos);

                // If the block below is the moon block
                if (state.is(GCBlockTags.FOOTPRINTS)) {
                    long chunkKey = ChunkPos.asLong(SectionPos.blockToSectionCoord(pos.x), SectionPos.blockToSectionCoord(pos.z));
                    short age = (short) (level.getGameTime() % 20);
                    level.getAttachedOrThrow(GCAttachments.FOOTPRINT_MANAGER).addFootprint(chunkKey, new Footprint(dimensionType.unwrapKey().get().location(), pos, rotation, age, entity.getUUID()));
                }

                // Change the sign of the lastStep variable
                setLastStep(-this.lastStep);
                setDistanceSinceLastStep(0);
            } else {
                setDistanceSinceLastStep(getDistanceSinceLastStep() + motionSqrd);
            }
        }
    }
}
