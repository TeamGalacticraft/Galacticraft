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

package dev.galacticraft.mod.api.pipe;

import dev.galacticraft.machinelib.api.fluid.FluidStack;
import dev.galacticraft.mod.api.pipe.impl.PipeNetworkImpl;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * The basic 'Pipe Network' spec
 */
public interface PipeNetwork {
    static PipeNetwork create(ServerLevel world, long maxTransferRate) {
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
     * @return the amount of fluid that failed to insert
     */
    long insert(BlockPos pipe, FluidStack amount, Direction direction, TransactionContext transaction);

    FluidStack insertInternal(FluidStack amount, double ratio, FluidStack available, TransactionContext transaction);

    void getNonFullInsertables(Object2LongMap<PipeNetwork> fluidRequirement, BlockPos source, FluidStack amount, TransactionContext context);

    /**
     * Returns the maximum amount of fluid allowed to pass through this network per tick
     * @return the maximum amount of fluid allowed to pass through this network per tick
     */
    long getMaxTransferRate();

    Collection<BlockPos> getAllPipes();

    Map<BlockPos, Storage<FluidVariant>> getInsertable();

    boolean markedForRemoval();

    void markForRemoval();

    boolean isCompatibleWith(Pipe pipe);

}
