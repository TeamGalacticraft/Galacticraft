/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content.block.special.rocketlaunchpad;

import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RocketLaunchPadBlockEntity extends BlockEntity {

    private UUID rocketUUID = null;
    private @Nullable Rocket rocket;

    public RocketLaunchPadBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.LAUNCH_PAD_TYPE, pos, state);
    }

    public void setLinkedRocket(@Nullable Rocket rocket) {
        if (rocket == null) {
            this.rocketUUID = null;
            this.rocket = null;
        } else {
            this.rocketUUID = rocket.asEntity().getUUID();
            this.rocket = rocket;
        }
    }

    public boolean hasRocket() {
        if (this.rocketUUID != null) {
            if (this.rocket == null && this.level instanceof ServerLevel) {
                this.rocket = (Rocket) ((ServerLevel) this.level).getEntity(this.rocketUUID);
            }
        } else {
            this.rocket = null;
        }

        return this.rocket != null;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.rocketUUID = null;
        this.rocket = null;

        if (tag.contains(Constant.Nbt.ROCKET_UUID)) {
            this.rocketUUID = tag.getUUID(Constant.Nbt.ROCKET_UUID);
            if (this.level instanceof ServerLevel) {
                this.rocket = (Rocket) ((ServerLevel) this.level).getEntity(this.rocketUUID);
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        if (this.rocketUUID != null) tag.putUUID("RocketUuid", rocketUUID);
        super.saveAdditional(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        if (this.rocketUUID != null) tag.putUUID("RocketUuid", this.rocketUUID);
        return tag;
    }

    public Rocket getRocket() {
        return rocket;
    }

    public UUID getRocketUUID() {
        return rocketUUID;
    }
}