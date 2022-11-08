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

package dev.galacticraft.mod.registries.block.special.rocketlaunchpad;

import dev.galacticraft.mod.registries.block.entity.GCBlockEntityTypes;
import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.entity.RocketEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.UUID;

public class RocketLaunchPadBlockEntity extends BlockEntity/* implements BlockEntityClientSerializable*/ {

    private UUID rocketEntityUUID = null;
    private int rocketEntityId = Integer.MIN_VALUE;

    public RocketLaunchPadBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.LAUNCH_PAD_TYPE, pos, state);
    }

    public void setRocketEntityUUID(UUID rocketEntityUUID) {
        this.rocketEntityUUID = rocketEntityUUID;
//        if (!world.isClient) sync();
    }

    public int getRocketEntityId() {
        return rocketEntityId;
    }

    public boolean hasRocket() {
        return this.rocketEntityUUID != null && this.rocketEntityId != Integer.MIN_VALUE;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("rocketUuid")) {
            this.rocketEntityUUID = tag.getUUID("rocketUuid");
            for (Entity entity : level.getEntities(GalacticraftEntityType.ROCKET, new AABB(-3, -2, -3, 3, 9, 3), rocketEntity -> true)) {
                if (entity instanceof RocketEntity) {
                    if (entity.getUUID() == this.rocketEntityUUID) {
                        this.rocketEntityId = entity.getId();
                    }
                }
            }
            if (rocketEntityId == Integer.MIN_VALUE) throw new IllegalStateException("Unable to find linked rocket!");
        } else {
            rocketEntityUUID = null;
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        if (hasRocket()) tag.putUUID("rocketUuid", rocketEntityUUID);
        super.saveAdditional(tag);
    }

    public void setRocketEntityId(int entityId) {
        rocketEntityId = entityId;
//        if (!level.isClientSide) sync();
    }

//    @Override
//    public void fromClientTag(NbtCompound tag) {
//        if (tag.contains("rocketUuid")) {
//            this.rocketEntityUUID = tag.getUuid("rocketUuid");
//        } else {
//            rocketEntityUUID = null;
//        }
//        rocketEntityId = tag.getInt("reid");
//    }
//
//    @Override
//    public NbtCompound toClientTag(NbtCompound nbtCompound) {
//        this.writeNbt(nbtCompound);
//        nbtCompound.putInt("reid", rocketEntityId);
//        return nbtCompound;
//    }
}