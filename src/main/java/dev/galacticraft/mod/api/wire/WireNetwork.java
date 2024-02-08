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

package dev.galacticraft.mod.api.wire;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The basic 'Wire Network' spec
 */
public interface WireNetwork {
    /**
     * Adds a wire to the network
     * @param pos The position of the wire being added
     * @param wire The data container of the wire being connected (can be null)
     * @return
     */
    void addWire(@NotNull BlockPos pos, @Nullable Wire wire);

    /**
     * Removes a wire from the network
     * @param removedPos The position of the wire being removed
     */
    void removeWire(@NotNull BlockPos removedPos);

    /**
     * Updates the wire's connection to the updated block
     *
     * @param adjacentToUpdated The wire that is adjacent to the updated pos
     * @param updatedPos        The position of the block that was updated
     * @param direction
     * @return
     */
    boolean updateConnection(@NotNull BlockPos adjacentToUpdated, @NotNull BlockPos updatedPos, Direction direction);

    /**
     * Inserts energy into the network
     * @param amount The amount of energy to insert
     * @param transaction Whether to perform the action or not
     * @return the amount of energy that failed to insert
     */
    long insert(long amount, @NotNull TransactionContext transaction);

    long insertInternal(long amount, double ratio, long available, TransactionContext transaction);

    void getNonFullInsertables(Object2LongMap<WireNetwork> energyRequirement, long amount, @NotNull TransactionContext transaction);

    /**
     * Returns the maximum amount of energy allowed to pass through this network per tick
     * @return the maximum amount of energy allowed to pass through this network per tick
     */
    long getMaxTransferRate();

    boolean markedForRemoval();

    void markForRemoval();

    boolean isCompatibleWith(Wire wire);
}
