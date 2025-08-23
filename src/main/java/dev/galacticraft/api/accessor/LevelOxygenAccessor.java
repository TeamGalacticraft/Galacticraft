/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.api.accessor;

import dev.galacticraft.api.block.entity.AtmosphereProvider;
import net.minecraft.core.BlockPos;

import java.util.Iterator;

public interface LevelOxygenAccessor {
    /**
     * {@return the atmospheric providers for the given block position}
     */
    default Iterator<AtmosphereProvider> galacticraft$getAtmosphericProviders(int x, int y, int z) {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    /**
     * {@return the positions of the atmospheric providers for the given block position}
     */
    default Iterator<BlockPos> galacticraft$getAtmosphericProviderLocations(int x, int y, int z) {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    /**
     * {@return whether the given point in the level is breathable}
     */
    default boolean galacticraft$isBreathable(double x, double y, double z) {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    /**
     * {@return whether the block position in the level is breathable}
     * It is undefined whether partially breathable block positions are breathable or not.
     */
    default boolean galacticraft$isBreathable(int x, int y, int z) {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    /**
     * {@return whether the block position in the level is breathable}
     * It is undefined whether partially breathable block positions are breathable or not.
     */
    default boolean galacticraft$isBreathable(BlockPos pos) {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    /**
     * {@return whether this has a breathable atmosphere}
     */
    default boolean galacticraft$isBreathable() {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    /**
     * Adds an atmospheric provider to the given block position.
     * The given provider MUST be allocated on the chunk section before being added.
     *
     * @param x the x-coordinate of the block gaining a provider
     * @param y the y-coordinate of the block gaining a provider
     * @param z the z-coordinate of the block gaining a provider
     * @param providerPos the position of the atmospheric provider
     * @see dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor#galacticraft$ensureSpaceFor(BlockPos)
     */
    default void galacticraft$addAtmosphericProvider(int x, int y, int z, BlockPos providerPos) {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    /**
     * {@return whether the block at the given position has the given atmospheric provider attached}
     */
    default boolean galacticraft$hasAtmosphericProvider(int x, int y, int z, BlockPos providerPos) {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    /**
     * Removes the given atmospheric provider from the given block position.
     * If the provider does not exist, nothing changes.
     */
    default void galacticraft$removeAtmosphericProvider(int x, int y, int z, BlockPos providerPos) {
        throw new RuntimeException("This should be overridden by mixin!");
    }

    default Iterator<BlockPos> galacticraft$getAtmosphericProviderLocations(BlockPos pos) {
        return this.galacticraft$getAtmosphericProviderLocations(pos.getX(), pos.getY(), pos.getZ());
    }

    default Iterator<AtmosphereProvider> galacticraft$getAtmosphericProviders(BlockPos pos) {
        return this.galacticraft$getAtmosphericProviders(pos.getX(), pos.getY(), pos.getZ());
    }

    default void galacticraft$addAtmosphericProvider(BlockPos pos, BlockPos providerPos) {
        this.galacticraft$addAtmosphericProvider(pos.getX(), pos.getY(), pos.getZ(), providerPos);
    }

    default boolean galacticraft$hasAtmosphericProvider(BlockPos pos, BlockPos providerPos) {
        return this.galacticraft$hasAtmosphericProvider(pos.getX(), pos.getY(), pos.getZ(), providerPos);
    }

    default void galacticraft$removeAtmosphericProvider(BlockPos pos, BlockPos providerPos) {
        this.galacticraft$removeAtmosphericProvider(pos.getX(), pos.getY(), pos.getZ(), providerPos);
    }
}
