/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.content.block.entity.networked;

import dev.galacticraft.mod.api.block.entity.Colored;
import dev.galacticraft.mod.api.block.entity.Walkway;
import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.block.special.fluidpipe.PipeBlockEntity;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PipeWalkwayBlockEntity extends PipeBlockEntity implements Walkway, Colored {
    @Nullable
    private Direction direction;

    public PipeWalkwayBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.PIPE_WALKWAY, pos, state, FluidConstants.BUCKET / 50);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        this.writeWalkwayNbt(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.readWalkwayNbt(nbt);
        this.setSectionDirty(this.level, this.worldPosition);
    }

    @Override
    @Nullable
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public void setDirection(@NotNull Direction direction) {
        this.direction = direction;
    }

    @Override
    public boolean canConnect(Direction direction) {
        if (this.direction == null) return false;
        return direction != this.direction;
    }

    @Override
    public void calculateConnections() {
        for (Direction direction : Direction.values()) {
            if (getDirection() != direction) {
                if (level.getBlockEntity(this.worldPosition.relative(direction)) instanceof Pipe pipe) {
                    if (pipe.canConnect(direction.getOpposite())) {
                        getConnections()[direction.ordinal()] = true;
                        continue;
                    }
                } else if (FluidUtil.canAccessFluid(level, this.worldPosition.relative(direction), direction)) {
                    getConnections()[direction.ordinal()] = true;
                    continue;
                }
            }
            getConnections()[direction.ordinal()] = false;
        }
    }
}
