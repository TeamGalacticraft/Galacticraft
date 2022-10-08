/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.mod.api.wire.impl.WireNetworkImpl;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Collection;
import java.util.Map;

/**
 * The basic 'Wire Network' spec
 */
public interface WireNetwork {
    static WireNetwork create(ServerLevel world, long maxTransferRate) {
        return new WireNetworkImpl(world, maxTransferRate);
    }

    /**
     * Adds a wire to the network
     * @param pos The position of the wire being added
     * @param wire The data container of the wire being connected (can be null)
     * @return
     */
    boolean addWire(@NotNull BlockPos pos, @Nullable Wire wire);

    /**
     * Removes a wire from the network
     * @param removedPos The position of the wire being removed
     */
    void removeWire(Wire wire, @NotNull BlockPos removedPos);

    /**
     * Updates the wire's connection to the updated block
     * @param adjacentToUpdated The wire that is adjacent to the updated pos
     * @param updatedPos The position of the block that was updated
     * @return
     */
    boolean updateConnection(@NotNull BlockPos adjacentToUpdated, @NotNull BlockPos updatedPos);

    /**
     * Inserts energy into the network
     * @param fromWire The wire that received the energy
     * @param amount The amount of energy to insert
     * @param direction
     * @param transaction Whether to perform the action or not
     * @return the amount of energy that failed to insert
     */
    long insert(@NotNull BlockPos fromWire, long amount, Direction direction, @NotNull TransactionContext transaction);

    long insertInternal(long amount, double ratio, long available, TransactionContext transaction);

    void getNonFullInsertables(Object2LongMap<WireNetwork> energyRequirement, BlockPos source, long amount, @NotNull TransactionContext transaction);

    /**
     * Returns the maximum amount of energy allowed to pass through this network per tick
     * @return the maximum amount of energy allowed to pass through this network per tick
     */
    long getMaxTransferRate();

    Collection<BlockPos> getAllWires();

    Map<BlockPos, EnergyStorage> getStorages();

    boolean markedForRemoval();

    void markForRemoval();

    boolean isCompatibleWith(Wire wire);
}
