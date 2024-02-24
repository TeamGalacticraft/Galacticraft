/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

/**
 * The basic 'Pipe Network' spec
 */
public interface PipeNetwork {
    /**
     * Updates the pipe's connection to the updated block
     *
     * @param pipePos The pipe that is adjacent to the updated pos
     * @param adjacentPos        The position of the block that was updated
     * @param direction         The direction of the updated block
     */
    void updateConnection(@NotNull BlockPos pipePos, @NotNull BlockPos adjacentPos, @NotNull Direction direction);

    /**
     * Inserts fluid into the network
     *
     * @return the amount of fluid that failed to insert
     */
    long insert(@NotNull FluidVariant resource, long amount, @NotNull TransactionContext transaction);

    /**
     * Returns the maximum amount of fluid allowed to pass through this network per tick
     *
     * @return the maximum amount of fluid allowed to pass through this network per tick
     */
    long getMaxTransferRate();

    boolean markedForRemoval();

    void markForRemoval();

    boolean isCompatibleWith(@NotNull Pipe pipe);
}
