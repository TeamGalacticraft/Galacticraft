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

package dev.galacticraft.mod.api.wire;

import alexiil.mc.lib.attributes.Simulation;
import dev.galacticraft.energy.api.EnergyInsertable;
import dev.galacticraft.mod.api.wire.impl.WireNetworkImpl;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
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
     * Inserts energy into the network
     * @param fromWire The wire that received the energy
     * @param amount The amount of energy to insert
     * @param simulate Whether to perform the action or not
     * @return the amount of energy that failed to insert
     */
    int insert(@NotNull BlockPos fromWire, /*Positive*/ int amount, @NotNull Simulation simulate);

    Collection<BlockPos> getAllWires();
    Map<BlockPos, EnergyInsertable> getInsertable();

    boolean markedForRemoval();

    void markForRemoval();
}
