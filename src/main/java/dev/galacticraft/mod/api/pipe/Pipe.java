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

package dev.galacticraft.mod.api.pipe;

import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import dev.galacticraft.mod.Constants;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public interface Pipe {
    /**
     * Sets the {@link PipeNetwork} associated with this wie
     * @param network The network to associate with
     */
    void setNetwork(@NotNull PipeNetwork network);

    /**
     * Returns the associated {@link PipeNetwork}
     * @return The associated {@link PipeNetwork}
     */
    @NotNull PipeNetwork getNetwork();

    @Nullable PipeNetwork getNetworkNullable();

    /**
     * Returns whether or not this pipe is able to connect to another block on the specified face/direction
     * @param direction the direction offset to the block to check adjacency to
     * @return Whether or not this pipe is able to connect to another block on the specified face/direction
     */
    @NotNull PipeConnectionType getConnection(@NotNull Direction direction, @Nullable BlockEntity entity);

    /**
     * Returns the fluid in the pipe and its transport data
     * @return The fluid in the pipe and its transport data
     */
    @NotNull Pipe.FluidData getFluidData();

    /**
     * Sets the fluid and the transport data of this pipe
     * @param data The fluid/transport data
     */
    void setFluidData(@NotNull Pipe.FluidData data);

    default boolean canConnect(Direction direction) {
        return true;
    }

    BlockPos getPos();

    class FluidData {
        public static final FluidData EMPTY = new FluidData(BlockPos.ORIGIN, new ArrayDeque<>(), FluidVolumeUtil.EMPTY, null);
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
                dir = Constants.Misc.DIRECTIONS[compoundTag.getInt("dir")];
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
            long[] path = new long[this.path.size()];
            Iterator<BlockPos> iterator = this.path.iterator();
            for (int i = 0; i < this.path.size(); i++) {
                path[i] = iterator.next().asLong();
            }
            compoundTag.putLongArray("path", path);
            compoundTag.putBoolean("hasDir", this.endDir != null);
            if (this.endDir != null) {
                compoundTag.putInt("dir", this.endDir.ordinal());
            }
            return compoundTag;
        }
    }
}
