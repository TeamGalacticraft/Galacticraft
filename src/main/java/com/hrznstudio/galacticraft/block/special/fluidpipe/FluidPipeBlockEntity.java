/*
 * Copyright (c) 2020 HRZN LTD
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

import com.hrznstudio.galacticraft.api.pipe.Pipe;
import com.hrznstudio.galacticraft.api.pipe.PipeConnectionType;
import com.hrznstudio.galacticraft.api.pipe.PipeNetwork;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.TankComponentHelper;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public class FluidPipeBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Tickable, Pipe {
    private PipeNetwork network = null;
    private @NotNull Pipe.FluidData data = Pipe.FluidData.EMPTY;

    public FluidPipeBlockEntity() {
        super(GalacticraftBlockEntities.FLUID_PIPE_TYPE);
    }

    public FluidPipeBlockEntity(BlockEntityType<? extends FluidPipeBlockEntity> entityType) {
        super(entityType);
    }

    @Override
    public void setLocation(World world, BlockPos pos) {
        super.setLocation(world, pos);
    }

    @Override
    public @NotNull Pipe.FluidData getData() {
        return data;
    }

    @Override
    public PipeNetwork getNetwork() {
        return network;
    }

    @Override
    public @NotNull PipeConnectionType getConnection(@NotNull Direction direction, @Nullable BlockEntity entity) {
        if (entity == null || !canConnect(direction)) return PipeConnectionType.NONE;
        if (entity instanceof Pipe && ((Pipe) entity).canConnect(direction.getOpposite())) return PipeConnectionType.PIPE;
        TankComponent component = TankComponentHelper.INSTANCE.getComponent(world, entity.getPos(), direction.getOpposite());
        if (component != null) {
            if (component.canInsert(0) && component.canExtract(0)) {
                return PipeConnectionType.FLUID_IO;
            } else if (component.canInsert(0)) {
                return PipeConnectionType.FLUID_INPUT;
            } else if (component.canExtract(0)) {
                return PipeConnectionType.FLUID_OUTPUT;
            }
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
    public void setData(@NotNull Pipe.FluidData data) {
        this.data = data;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        this.data.toTag(tag);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.data = Pipe.FluidData.fromTag(tag);
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.data = Pipe.FluidData.fromTag(compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return this.data.toTag(compoundTag);
    }

    @Override
    public void tick() {
        if (world.isClient) return;
        if (this.network == null) {
            for (Direction direction : Direction.values()) {
                BlockEntity entity = world.getBlockEntity(pos.offset(direction));
                if (entity instanceof FluidPipeBlockEntity) {
                    if (((FluidPipeBlockEntity) entity).network != null) {
                        ((FluidPipeBlockEntity) entity).network.addPipe(pos, this);
                        break;
                    }
                }
            }
            if (this.network == null) {
                this.network = PipeNetwork.create((ServerWorld) world);
                this.network.addPipe(pos, this);
            }
        }
        if (data != Pipe.FluidData.EMPTY) {
            if (!this.data.getPath().isEmpty()) {
                BlockPos pos = this.data.getPath().getLast();
                if (pos.equals(this.pos)) {
                    this.data.getPath().pollLast();
                    pos = this.data.getPath().getLast();
                }
                if (pos.getManhattanDistance(this.pos) > 1) {
                    throw new RuntimeException();
                }
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof FluidPipeBlockEntity) {
                    if (((FluidPipeBlockEntity) entity).data.getFluidVolume().isEmpty()) {
                        this.data.getPath().pollLast();
                        ((FluidPipeBlockEntity) entity).data = this.data;
                        this.data = Pipe.FluidData.EMPTY;
                    }
                } else {
                    if (this.data.getPath().size() != 1) {
                        Pipe.FluidData dat = network.insertFluid(this.pos, null, this.data.getFluidVolume(), ActionType.PERFORM);
                        if (dat != null) {
                            this.data = dat;
                        }
                        return;
                    }
                    TankComponent component = TankComponentHelper.INSTANCE.getComponent(world, pos, this.data.getEndDir().getOpposite());
                    if (component != null) {
                        FluidVolume volume = component.insertFluid(data.getFluidVolume(), ActionType.PERFORM);
                        this.data.getPath().clear();
                        this.data = Pipe.FluidData.EMPTY;
                        if (!volume.isEmpty()) {
                            Pipe.FluidData dat = network.insertFluid(this.pos, null, volume, ActionType.PERFORM);
                            if (dat != null) {
                                this.data = dat;
                            } else {
                                BlockPos finalPos = pos;
                                this.data = new Pipe.FluidData(pos, Util.make(new LinkedList<>(), l -> l.add(finalPos)), volume, this.data.getEndDir());
                            }
                        }
                    } else {
                        Pipe.FluidData dat = network.insertFluid(this.pos, null, this.data.getFluidVolume(), ActionType.PERFORM);
                        if (dat != null) {
                            this.data = dat;
                        }
                    }
                }
            } else {
                Pipe.FluidData dat = network.insertFluid(this.pos, null, this.data.getFluidVolume(), ActionType.PERFORM);
                if (dat != null) {
                    this.data = dat;
                }
            }
        }
    }
}
