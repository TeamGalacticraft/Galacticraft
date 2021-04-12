/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.block.special.fluidpipe;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProviderBlockEntity;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidExtractable;
import alexiil.mc.lib.attributes.fluid.impl.RejectingFluidInsertable;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.pipe.Pipe;
import com.hrznstudio.galacticraft.api.pipe.PipeConnectionType;
import com.hrznstudio.galacticraft.api.pipe.PipeNetwork;
import com.hrznstudio.galacticraft.attribute.fluid.PipeFixedFluidInv;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public class FluidPipeBlockEntity extends BlockEntity implements TickableBlockEntity, Pipe, AttributeProviderBlockEntity {
    private PipeNetwork network = null;
    private @NotNull Pipe.FluidData fluidData = Pipe.FluidData.EMPTY;
    private byte timeUntilPush = 0;

    public FluidPipeBlockEntity() {
        super(GalacticraftBlockEntities.FLUID_PIPE_TYPE);
    }

    public FluidPipeBlockEntity(BlockEntityType<? extends FluidPipeBlockEntity> entityType) {
        super(entityType);
    }

    @Override
    public @NotNull Pipe.FluidData getFluidData() {
        return fluidData;
    }

    @Override
    public @NotNull PipeNetwork getNetwork() {
        if (!level.isClientSide()) {
            if (this.network == null) {
                for (Direction direction : Constants.Misc.DIRECTIONS) {
                    BlockEntity entity = level.getBlockEntity(worldPosition.relative(direction));
                    if (entity instanceof Pipe) {
                        if (((Pipe) entity).getNetworkNullable() != null) {
                            ((Pipe) entity).getNetworkNullable().addPipe(worldPosition, this);
                            break;
                        }
                    }
                }
                if (this.network == null) {
                    this.network = PipeNetwork.create((ServerLevel)level);
                    this.network.addPipe(worldPosition, this);
                }
            }
        }
        return this.network;
    }

    @Override
    public @Nullable PipeNetwork getNetworkNullable() {
        return this.network;
    }

    @Override
    public @NotNull PipeConnectionType getConnection(@NotNull Direction direction, @Nullable BlockEntity entity) {
        if (entity == null || !this.canConnect(direction.getOpposite())) return PipeConnectionType.NONE;
        if (entity instanceof Pipe && ((Pipe) entity).canConnect(direction)) return PipeConnectionType.PIPE;
        boolean insertable = FluidAttributes.INSERTABLE.getFromNeighbour(this, direction) != RejectingFluidInsertable.NULL;
        boolean extractable = FluidAttributes.EXTRACTABLE.getFromNeighbour(this, direction) != EmptyFluidExtractable.NULL;
        if (insertable && extractable) {
            return PipeConnectionType.FLUID_IO;
        } else if (insertable) {
            return PipeConnectionType.FLUID_INPUT;
        } else if (extractable) {
            return PipeConnectionType.FLUID_OUTPUT;
        }

        return PipeConnectionType.NONE;
    }

    @Override
    public boolean canConnect(Direction direction) {
        //todo dye check
        return true;
    }

    @Override
    public void setNetwork(@NotNull PipeNetwork network) {
        this.network = network;
    }

    @Override
    public void setFluidData(@NotNull Pipe.FluidData data) {
        this.fluidData = data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        this.fluidData.toTag(tag);
        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        this.fluidData = Pipe.FluidData.fromTag(tag);
    }

    @Override
    public void tick() {
        if (level.isClientSide) return;
        if (fluidData != Pipe.FluidData.EMPTY) {
            if (++timeUntilPush >= 5) {
                this.timeUntilPush = 0;
                if (!this.fluidData.getPath().isEmpty()) {
                    BlockPos pos = this.fluidData.getPath().getLast();
                    if (pos.equals(this.worldPosition)) {
                        this.fluidData.getPath().pollLast();
                        pos = this.fluidData.getPath().getLast();
                    }
                    if (pos.distManhattan(this.worldPosition) > 1) {
                        throw new RuntimeException();
                    }
                    BlockEntity entity = level.getBlockEntity(pos);
                    if (entity instanceof Pipe) {
                        if (((Pipe) entity).getFluidData().getFluid().isEmpty()) {
                            this.fluidData.getPath().pollLast();
                            ((Pipe) entity).setFluidData(this.fluidData);
                            if (entity instanceof FluidPipeBlockEntity) {
                                ((FluidPipeBlockEntity) entity).timeUntilPush = 0; //todo remove tup
                            }
                            this.fluidData = Pipe.FluidData.EMPTY;
                        }
                    } else {
                        if (this.fluidData.getPath().size() != 1) {
                            Pipe.FluidData dat = network.insertFluid(this.worldPosition, null, this.fluidData.getFluid(), Simulation.ACTION);
                            if (dat != null) {
                                this.fluidData = dat;
                            }
                            return;
                        }
                        FluidInsertable insertable = FluidAttributes.INSERTABLE.get(level, pos, SearchOptions.inDirection(this.fluidData.getEndDir()));
                        FluidVolume volume = insertable.attemptInsertion(fluidData.getFluid(), Simulation.ACTION);
                        if (!volume.isEmpty()) {
                            Pipe.FluidData dat = network.insertFluid(this.worldPosition, null, volume, Simulation.ACTION);
                            if (dat != null) {
                                this.fluidData = dat;
                            } else {
                                BlockPos finalPos = pos;
                                this.fluidData = new Pipe.FluidData(pos, Util.make(new LinkedList<>(), l -> l.add(finalPos)), volume, this.fluidData.getEndDir());
                            }
                        } else {
                            this.fluidData = Pipe.FluidData.EMPTY;
                        }
                    }
                } else {
                    Pipe.FluidData dat = network.insertFluid(this.worldPosition, null, this.fluidData.getFluid(), Simulation.ACTION);
                    if (dat != null) {
                        this.fluidData = dat;
                    }
                }
            }
        }
    }

    @Override
    public void addAllAttributes(AttributeList<?> attributeList) {
        attributeList.offer(new PipeFixedFluidInv(this));
    }
}
