package com.hrznstudio.galacticraft.api.pipe;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

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
    @NotNull Pipe.FluidData getFluid();

    /**
     * Sets the fluid and the transport data of this pipe
     * @param data The fluid/transport data
     */
    void setFluid(@NotNull Pipe.FluidData data);

    default boolean canConnect(Direction direction) {
        return true;
    }

    class FluidData {
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
