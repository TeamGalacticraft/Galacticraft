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
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidExtractable;
import alexiil.mc.lib.attributes.fluid.impl.RejectingFluidInsertable;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.api.pipe.PipeConnectionType;
import dev.galacticraft.mod.api.pipe.PipeNetwork;
import dev.galacticraft.mod.attribute.fluid.PipeFluidInsertable;
import dev.galacticraft.mod.block.entity.GalacticraftBlockEntityType;
import dev.galacticraft.mod.util.FluidUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class PipeBlockEntity extends BlockEntity implements Pipe, AttributeProviderBlockEntity {
    private @Nullable PipeNetwork network = null;
    private @Nullable PipeFluidInsertable insertable = null;
    private static final FluidAmount MAX_TRANSFER_RATE = FluidAmount.of(5, 100);

    public PipeBlockEntity(BlockPos pos, BlockState state) {
        this(GalacticraftBlockEntityType.FLUID_PIPE, pos, state);
    }

    public PipeBlockEntity(BlockEntityType<? extends PipeBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void setNetwork(@Nullable PipeNetwork network) {
        this.network = network;
        this.getInsertable().setNetwork(network);
    }

    @Override
    public @NotNull PipeNetwork getNetwork() {
        if (this.network == null) {
            if (!this.world.isClient()) {
                for (Direction direction : Constant.Misc.DIRECTIONS) {
                    BlockEntity entity = world.getBlockEntity(pos.offset(direction));
                    if (entity instanceof Pipe pipe && pipe.getNetworkNullable() != null) {
                        pipe.getNetwork().addPipe(pos, this);
                    }
                }
                if (this.network == null) {
                    this.setNetwork(PipeNetwork.create((ServerWorld) world));
                    this.network.addPipe(pos, this);
                }
            }
        }
        return this.network;
    }

    @Override
    public @Nullable PipeNetwork getNetworkNullable() {
        return this.network;
    }

    public PipeFluidInsertable getInsertable() {
        if (this.insertable == null) this.insertable = new PipeFluidInsertable(this.getMaxTransferRate(), this.pos);
        return this.insertable;
    }

    @Override
    public @NotNull PipeConnectionType getConnection(Direction direction, @NotNull BlockEntity entity) {
        if (!this.canConnect(direction)) return PipeConnectionType.NONE;
        if (entity instanceof Pipe pipe && pipe.canConnect(direction.getOpposite())) return PipeConnectionType.PIPE;
        FluidInsertable insertable = FluidUtil.getInsertable(world, entity.getPos(), direction);
        FluidExtractable extractable = FluidUtil.getExtractable(world, entity.getPos(), direction);
        if (insertable != RejectingFluidInsertable.NULL && extractable != EmptyFluidExtractable.NULL) {
            return PipeConnectionType.FLUID_IO;
        } else if (insertable != RejectingFluidInsertable.NULL) {
            return PipeConnectionType.FLUID_INPUT;
        } else if (extractable != EmptyFluidExtractable.NULL) {
            return PipeConnectionType.FLUID_OUTPUT;
        }
        return PipeConnectionType.NONE;
    }

    @Override
    public FluidAmount getMaxTransferRate() {
        return MAX_TRANSFER_RATE;
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        if (this.getNetworkNullable() != null) {
            this.getNetwork().removePipe(this.pos);
        }
    }

    @Override
    public void addAllAttributes(AttributeList<?> to) {
        to.offer(this.getInsertable());
    }
}
