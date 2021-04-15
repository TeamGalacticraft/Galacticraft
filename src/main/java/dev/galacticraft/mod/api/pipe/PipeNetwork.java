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

package dev.galacticraft.mod.api.pipe;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import dev.galacticraft.mod.api.pipe.impl.PipeNetworkImpl;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 * The basic 'Wire Network' spec
 */
public interface PipeNetwork {
    static PipeNetwork create(ServerWorld world) {
        return new PipeNetworkImpl(world);
    }

    /**
     * Adds a pipe to the network
     * @param pos The position of the pipe being added
     * @see #addPipe(BlockPos, Pipe)
     */
    default void addPipe(@NotNull BlockPos pos) {
        addPipe(pos, null);
    }

    /**
     * Adds a pipe to the network
     * @param pos The position of the pipe being added
     * @param pipe The data container of the pipe being connected (can be null)
     */
    void addPipe(@NotNull BlockPos pos, @Nullable Pipe pipe);

    /**
     * Removes a pipe from the network
     * @param pos The position of the pipe being removed
     */
    void removePipe(@NotNull BlockPos pos);

    /**
     * Updates the pipe's connection to the updated block
     * @param adjacentToUpdated The pipe that is adjacent to the updated pos
     * @param updatedPos The position of the block that was updated
     */
    void updateConnections(@NotNull BlockPos adjacentToUpdated, @NotNull BlockPos updatedPos);

    /**
     * Returns the relationship between the two positions
     * @param from The position to check from
     * @param to The position to go to
     * @return The relationship between the two positions
     */
    @NotNull PipeConnectionType getConnection(BlockPos from, BlockPos to);

    /**
     * Inserts energy into the network
     * @param fromPipe The pipe that received the energy
     * @param fromBlock The block that inserted the energy
     * @param amount The amount of fluid, in to insert
     * @param simulation Whether to simulate the action or actually perform it
     * @return the amount of fluid that succeeded to insert
     */
    @Nullable Pipe.FluidData insertFluid(@NotNull BlockPos fromPipe, @Nullable BlockPos fromBlock, FluidVolume amount, @NotNull Simulation simulation);

    /**
     * Returns the adjacent connections from a position
     * @param from The position that will be checked for adjacent connections
     * @return The adjacent connections from a position
     */
    @NotNull Map<Direction, PipeConnectionType> getAdjacent(BlockPos from);

    /**
     * Returns whether or not you can traverse the network from {@code from} to {@code to}
     * @param from The position to check from
     * @param to The position to go to
     * @return whether or not you can traverse the network from {@code from} to {@code to}
     */
    boolean canReach(@NotNull BlockPos from, @NotNull BlockPos to);
}
