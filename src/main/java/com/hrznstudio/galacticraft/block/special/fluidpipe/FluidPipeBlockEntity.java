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

import com.google.common.collect.ImmutableSet;
import com.hrznstudio.galacticraft.api.pipe.PipeNetwork;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.TankComponentHelper;
import io.github.cottonmc.component.fluid.impl.SimpleTankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import nerdhub.cardinal.components.api.component.Component;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FluidPipeBlockEntity extends BlockEntity implements BlockEntityClientSerializable, BlockComponentProvider, Tickable {
    private PipeNetwork network = null;
    private @NotNull FluidData data = FluidData.EMPTY;
    private byte timeUntilPush = 0;
    private final TankComponent component = new SimpleTankComponent(1, Fraction.of(1, 10)) {
        @Override
        public FluidVolume insertFluid(FluidVolume fluid, ActionType action) {
            if (data == FluidData.EMPTY) {
                if (network != null) {
                    FluidData data = network.spreadFluid(pos, fluid, action);
                    if (action == ActionType.PERFORM) {
                        if (data == null) {
                            return fluid;
                        }
                        FluidPipeBlockEntity.this.data = data;
                        return data.fluid;
                    }
                }
            }
            return fluid;
        }

        @Override
        public FluidVolume insertFluid(int tank, FluidVolume fluid, ActionType action) {
            return insertFluid(fluid, action);
        }

        @Override
        public FluidVolume removeFluid(int slot, ActionType action) {
            return FluidVolume.EMPTY;
        }

        @Override
        public FluidVolume takeFluid(int slot, Fraction amount, ActionType action) {
            return FluidVolume.EMPTY;
        }

        @Override
        public void setFluid(int slot, FluidVolume stack) {
        }

        @Override
        public boolean isAcceptableFluid(int tank) {
            return true;
        }

        @Override
        public FluidVolume getContents(int slot) {
            return FluidVolume.EMPTY;
        }
    };

    public FluidPipeBlockEntity() {
        super(GalacticraftBlockEntities.FLUID_PIPE_TYPE);
    }

    public FluidPipeBlockEntity(BlockEntityType<? extends FluidPipeBlockEntity> entityType) {
        super(entityType);
    }

    @Override
    public void setLocation(World world, BlockPos pos) {
        super.setLocation(world, pos);

        if (!world.isClient()) {
            if (this.network == null) {
                for (Direction direction : Direction.values()) {
                    BlockEntity entity = world.getBlockEntity(pos.offset(direction));
                    if (entity instanceof FluidPipeBlockEntity) {
                        if (((FluidPipeBlockEntity) entity).network != null && !((FluidPipeBlockEntity) entity).network.isInvalid()) {
                            ((FluidPipeBlockEntity) entity).network.addPipe(pos, this);
                            break;
                        }
                    }
                }
                if (this.network == null) {
                    this.network = new PipeNetwork(pos, (ServerWorld)world, this);
                }
            }
        }
    }

    public @NotNull FluidData getData() {
        return data;
    }

    public PipeNetwork getNetwork() {
        return network;
    }

    public void setNetwork(PipeNetwork network) {
        this.network = network;
    }

    public void setFluid(@NotNull FluidData data) {
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
        this.data = FluidData.fromTag(tag);
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.data = FluidData.fromTag(compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return this.data.toTag(compoundTag);
    }

    @Override
    public <T extends Component> boolean hasComponent(BlockView blockView, BlockPos pos, ComponentType<T> type, Direction side) {
        return type == UniversalComponents.TANK_COMPONENT;
    }

    @Override
    public <T extends Component> T getComponent(BlockView blockView, BlockPos pos, ComponentType<T> type, Direction side) {
        return type == UniversalComponents.TANK_COMPONENT ? (T) component : null;
    }

    @Override
    public Set<ComponentType<?>> getComponentTypes(BlockView blockView, BlockPos pos, Direction side) {
        return ImmutableSet.of(UniversalComponents.TANK_COMPONENT);
    }

    @Override
    public void tick() {
        if (world.isClient) return;
        if (data != FluidData.EMPTY) {
            if (++timeUntilPush >= 5) {
                this.timeUntilPush = 0;
                if (!this.data.path.isEmpty()) {
                    BlockPos pos = this.data.path.getLast();
                    if (pos.equals(this.pos)) {
                        this.data.path.pollLast();
                        pos = this.data.path.getLast();
                    }
                    if (pos.getManhattanDistance(this.pos) > 1) {
                        throw new RuntimeException();
                    }
                    BlockEntity entity = world.getBlockEntity(pos);
                    if (entity instanceof FluidPipeBlockEntity) {
                        if (((FluidPipeBlockEntity) entity).data.fluid.isEmpty()) {
                            this.data.path.pollLast();
                            ((FluidPipeBlockEntity) entity).data = this.data;
                            ((FluidPipeBlockEntity) entity).timeUntilPush = 0;
                            this.data = FluidData.EMPTY;
                        }
                    } else {
                        if (this.data.path.size() != 1) {
                            FluidData dat = network.spreadFluid(this.pos, this.data.fluid, ActionType.PERFORM);
                            if (dat != null) {
                                this.data = dat;
                            }
                            return;
                        }
                        TankComponent component = TankComponentHelper.INSTANCE.getComponent(world, pos, this.data.getEndDir().getOpposite());
                        if (component != null) {
                            FluidVolume volume = component.insertFluid(data.getFluid(), ActionType.PERFORM);
                            if (!volume.isEmpty()) {
                                FluidData dat = network.spreadFluid(this.pos, volume, ActionType.PERFORM);
                                if (dat != null) {
                                    this.data = dat;
                                } else {
                                    BlockPos finalPos = pos;
                                    this.data = new FluidData(pos, Util.make(new LinkedList<>(), l -> l.add(finalPos)), volume, this.data.endDir);
                                }
                            } else {
                                this.data = FluidData.EMPTY;
                            }
                        } else {
                            FluidData dat = network.spreadFluid(this.pos, this.data.fluid, ActionType.PERFORM);
                            if (dat != null) {
                                this.data = dat;
                            }
                        }
                    }
                } else {
                    FluidData dat = network.spreadFluid(this.pos, this.data.fluid, ActionType.PERFORM);
                    if (dat != null) {
                        this.data = dat;
                    }
                }
            }
        }
    }

    public static class FluidData {
        public static final FluidData EMPTY = new FluidData(BlockPos.ORIGIN, new ArrayDeque<>(), FluidVolume.EMPTY, null);
        private final BlockPos source;
        private final Deque<BlockPos> path;
        private final FluidVolume fluid;
        private final Direction endDir;

        public FluidData(BlockPos source, Deque<BlockPos> path, FluidVolume fluid, Direction endDir) {
            this.source = source;
            this.path = path;
            this.fluid = fluid;
            this.endDir = endDir;
        }

        public BlockPos getSource() {
            return source;
        }

        public Deque<BlockPos> getPath() {
            return path;
        }

        public Direction getEndDir() {
            return endDir;
        }

        public FluidVolume getFluid() {
            return fluid;
        }

        public static FluidData fromTag(CompoundTag compoundTag) {
            if (compoundTag.getBoolean("empty")) return EMPTY;
            long[] longs = compoundTag.getLongArray("path");
            Deque<BlockPos> queue = new ArrayDeque<>(longs.length);
            for (long l : longs) {
                queue.add(BlockPos.fromLong(l));
            }
            Direction dir = null;
            if (compoundTag.getBoolean("hasDir")) {
                dir = Direction.values()[compoundTag.getInt("dir")];
            }
            return new FluidData(BlockPos.fromLong(compoundTag.getLong("source")), queue, FluidVolume.fromTag(compoundTag), dir);
        }

        public CompoundTag toTag(CompoundTag compoundTag) {
            if (this == EMPTY) {
                compoundTag.putBoolean("empty", true);
                return compoundTag;
            }
            compoundTag.putBoolean("empty", false);

            this.fluid.toTag(compoundTag);
            compoundTag.putLong("source", this.source.asLong());
            List<Long> list = new ArrayList<>(this.path.size());
            for (BlockPos pos : this.path) {
                list.add(pos.asLong());
            }
            compoundTag.putLongArray("path", list);
            compoundTag.putBoolean("hasDir", this.endDir != null);
            if (this.endDir != null) {
                compoundTag.putInt("dir", this.endDir.ordinal());
            }
            return compoundTag;
        }
    }
}
