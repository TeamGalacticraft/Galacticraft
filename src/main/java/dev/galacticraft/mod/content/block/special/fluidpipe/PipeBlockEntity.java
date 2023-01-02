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

package dev.galacticraft.mod.content.block.special.fluidpipe;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.api.pipe.PipeNetwork;
import dev.galacticraft.mod.attribute.fluid.PipeFluidInsertable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public abstract class PipeBlockEntity extends BlockEntity implements Pipe {
    private @NotNull PipeFluidInsertable @Nullable[] insertables = null;
    private @Nullable PipeNetwork network = null;
    private DyeColor color = DyeColor.WHITE;
    private final long maxTransferRate; // 1 bucket per second
    private final boolean[] connections = new boolean[6];


    public PipeBlockEntity(BlockEntityType<? extends PipeBlockEntity> type, BlockPos pos, BlockState state, long maxTransferRate) {
        super(type, pos, state);
        this.maxTransferRate = maxTransferRate;
    }
    
    @Override
    public void setNetwork(@Nullable PipeNetwork network) {
        this.network = network;
        for (PipeFluidInsertable insertable : this.getInsertables()) {
            insertable.setNetwork(network);
        }
    }

    @Override
    public @NotNull PipeNetwork getOrCreateNetwork() {
        if (this.network == null) {
            if (!this.level.isClientSide()) {
                for (Direction direction : Constant.Misc.DIRECTIONS) {
                    if (this.canConnect(direction)) {
                        BlockEntity entity = level.getBlockEntity(worldPosition.relative(direction));
                        if (entity instanceof Pipe pipe && pipe.getNetwork() != null) {
                            if (pipe.canConnect(direction.getOpposite())) {
                                if (pipe.getOrCreateNetwork().isCompatibleWith(this)) {
                                    pipe.getNetwork().addPipe(worldPosition, this);
                                }
                            }
                        }
                    }
                }
                if (this.network == null) {
                    this.setNetwork(PipeNetwork.create((ServerLevel) level, this.getMaxTransferRate()));
                    this.network.addPipe(worldPosition, this);
                }
            }
        }
        return this.network;
    }

    @Override
    public @Nullable PipeNetwork getNetwork() {
        return this.network;
    }

    public @NotNull PipeFluidInsertable[] getInsertables() {
        if (this.insertables == null) {
            this.insertables = new PipeFluidInsertable[6];
            for (Direction direction : Constant.Misc.DIRECTIONS) {
                this.insertables[direction.ordinal()] = new PipeFluidInsertable(direction, this.getMaxTransferRate(), this.worldPosition);
            }
        }
        return this.insertables;
    }

    @Override
    public long getMaxTransferRate() {
        return maxTransferRate;
    }

    @Override
    public boolean[] getConnections() {
        return this.connections;
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public void setColor(DyeColor color) {
        this.color = color;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.readColorNbt(nbt);
        this.readConnectionNbt(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        this.writeColorNbt(nbt);
        this.writeConnectionNbt(nbt);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.getNetwork() != null) {
            this.getNetwork().removePipe(this, this.worldPosition);
        }
    }
}
