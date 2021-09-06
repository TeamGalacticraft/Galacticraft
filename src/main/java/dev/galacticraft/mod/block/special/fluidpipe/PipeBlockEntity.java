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

package dev.galacticraft.mod.block.special.fluidpipe;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProviderBlockEntity;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.api.pipe.PipeNetwork;
import dev.galacticraft.mod.attribute.fluid.PipeFluidInsertable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public abstract class PipeBlockEntity extends BlockEntity implements Pipe, AttributeProviderBlockEntity {
    private @NotNull PipeFluidInsertable @Nullable[] insertables = null;
    private @Nullable PipeNetwork network = null;
    private DyeColor color = DyeColor.WHITE;
    private final FluidAmount maxTransferRate; // 1 bucket per second
    private final boolean[] connections = new boolean[6];


    public PipeBlockEntity(BlockEntityType<? extends PipeBlockEntity> type, BlockPos pos, BlockState state, FluidAmount maxTransferRate) {
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
            if (!this.world.isClient()) {
                for (Direction direction : Constant.Misc.DIRECTIONS) {
                    if (this.canConnect(direction)) {
                        BlockEntity entity = world.getBlockEntity(pos.offset(direction));
                        if (entity instanceof Pipe pipe && pipe.getNetwork() != null) {
                            if (pipe.canConnect(direction.getOpposite())) {
                                if (pipe.getOrCreateNetwork().isCompatibleWith(this)) {
                                    pipe.getNetwork().addPipe(pos, this);
                                }
                            }
                        }
                    }
                }
                if (this.network == null) {
                    this.setNetwork(PipeNetwork.create((ServerWorld) world, this.getMaxTransferRate()));
                    this.network.addPipe(pos, this);
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
                this.insertables[direction.ordinal()] = new PipeFluidInsertable(direction, this.getMaxTransferRate(), this.pos);
            }
        }
        return this.insertables;
    }

    @Override
    public FluidAmount getMaxTransferRate() {
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
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.readColorNbt(nbt);
        this.readConnectionNbt(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        this.writeColorNbt(nbt);
        this.writeConnectionNbt(nbt);
        return super.writeNbt(nbt);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        this.writeColorNbt(tag);
        this.writeConnectionNbt(tag);
        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        this.readColorNbt(tag);
        this.readConnectionNbt(tag);
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        if (this.getNetwork() != null) {
            this.getNetwork().removePipe(this, this.pos);
        }
    }

    @Override
    public void addAllAttributes(AttributeList<?> to) {
        if (to.getSearchDirection() != null) {
            if (this.canConnect(to.getSearchDirection().getOpposite())) {
                to.offer(this.getInsertables()[to.getSearchDirection().ordinal()]);
            }
        }
    }
}
