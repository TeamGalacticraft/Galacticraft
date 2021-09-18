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
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import dev.galacticraft.mod.api.pipe.impl.PipeNetworkImpl;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * The basic 'Pipe Network' spec
 */
public interface PipeNetwork {
    static PipeNetwork create(ServerWorld world, FluidAmount maxTransferRate) {
        return new PipeNetworkImpl(world, maxTransferRate);
    }

    /**
     * Adds a pipe to the network
     * @param pos The position of the pipe being added
     * @param pipe The data container of the pipe being connected (can be null)
     * @return
     */
    boolean addPipe(@NotNull BlockPos pos, @Nullable Pipe pipe);

    /**
     * Removes a pipe from the network
     * @param removedPos The position of the pipe being removed
     */
    void removePipe(Pipe pipe, @NotNull BlockPos removedPos);

    /**
     * Updates the pipe's connection to the updated block
     * @param adjacentToUpdated The pipe that is adjacent to the updated pos
     * @param updatedPos The position of the block that was updated
     * @return
     */
    boolean updateConnection(@NotNull BlockPos adjacentToUpdated, @NotNull BlockPos updatedPos);

    /**
     * Inserts fluid into the network
     * @param fromPipe The pipe that received the fluid
     * @param amount The amount of fluid to insert
     * @param direction
     * @param simulate Whether to perform the action or not
     * @return the amount of fluid that failed to insert
     */
    FluidVolume insert(@NotNull BlockPos fromPipe, FluidVolume amount, Direction direction, @NotNull Simulation simulate);

    FluidVolume insertInternal(FluidVolume amount, FluidAmount ratio, FluidVolume available, Simulation simulate);

    void getNonFullInsertables(Object2ObjectMap<PipeNetwork, FluidAmount> fluidRequirement, BlockPos source, FluidVolume amount);

    /**
     * Returns the maximum amount of fluid allowed to pass through this network per tick
     * @return the maximum amount of fluid allowed to pass through this network per tick
     */
    FluidAmount getMaxTransferRate();

    Collection<BlockPos> getAllPipes();

    Map<BlockPos, FluidInsertable> getInsertable();

    boolean markedForRemoval();

    void markForRemoval();

    boolean isCompatibleWith(Pipe pipe);
}
