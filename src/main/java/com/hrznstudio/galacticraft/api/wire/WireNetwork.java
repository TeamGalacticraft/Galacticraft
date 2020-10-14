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

package com.hrznstudio.galacticraft.api.wire;

import com.hrznstudio.galacticraft.api.wire.impl.WireNetworkImpl;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.type.EnergyType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * The basic 'Wire Network' spec
 */
public interface WireNetwork {
    static WireNetwork create(ServerWorld world) {
        return new WireNetworkImpl(world);
    }

    /**
     * Adds a wire to the network
     * @param pos The position of the wire being added
     * @see #addWire(BlockPos, Wire)
     */
    default void addWire(@NotNull BlockPos pos) {
        addWire(pos, null);
    }

    /**
     * Adds a wire to the network
     * @param pos The position of the wire being added
     * @param wire The data container of the wire being connected (can be null)
     */
    void addWire(@NotNull BlockPos pos, @Nullable Wire wire);

    /**
     * Removes a wire from the network
     * @param pos The position of the wire being removed
     */
    void removeWire(@NotNull BlockPos pos);

    /**
     * Updates the wire's connection to the updated block
     * @param adjacentToUpdated The wire that is adjacent to the updated pos
     * @param updatedPos The position of the block that was updated
     */
    void updateConnections(@NotNull BlockPos adjacentToUpdated, @NotNull BlockPos updatedPos);

    /**
     * Returns the relationship between the two positions
     * @param from The position to check from
     * @param to The position to go to
     * @return The relationship between the two positions
     */
    @NotNull WireConnectionType getConnection(BlockPos from, BlockPos to);

    /**
     * Inserts energy into the network
     * @param fromWire The wire that received the energy
     * @param fromBlock The block that inserted the energy
     * @param energyType The type of energy being inserted
     * @param amount The amount of energy, in {@code energyType} to insert
     * @param type The type of action to perform
     * @return the amount of energy that failed to insert
     */
    int insertEnergy(@NotNull BlockPos fromWire, @Nullable BlockPos fromBlock, @NotNull EnergyType energyType, /*Positive*/ int amount, @NotNull ActionType type);

    /**
     * Returns the adjacent connections from a position
     * @param from The position that will be checked for adjacent connections
     * @return The adjacent connections from a position
     */
    @NotNull Map<Direction, WireConnectionType> getAdjacent(BlockPos from);

    /**
     * Returns whether or not you can traverse the network from {@code from} to {@code to}
     * @param from The position to check from
     * @param to The position to go to
     * @return whether or not you can traverse the network from {@code from} to {@code to}
     */
    boolean canReach(@NotNull BlockPos from, @NotNull BlockPos to);
}
