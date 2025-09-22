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
import dev.galacticraft.api.oxygen.OxygenUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Iterator;

public interface GCBlockExtensions {
    /**
     * {@return whether this block will transform on placement due to a lack of atmosphere}
     * This should only be implemented if it is not possible to handle the transformation elsewhere.
     * @param state the state being placed
     */
    default boolean galacticraft$hasLegacyExtinguishTransform(BlockState state) {
        return false;
    }

    /**
     * Extinguishes the given state due to a lack of atmosphere.
     * @param pos the position of the block in-world
     * @param state the state being placed
     * @return the transformed block state.
     */
    default BlockState galacticraft$extinguishBlockPlace(BlockPos pos, BlockState state) {
        return state;
    }

    /**
     * {@return whether the given block state needs to be alerted to changes in atmospheric composition}
     * @param state the state being checked
     */
    default boolean galacticraft$hasAtmosphereListener(BlockState state) {
        return false;
    }

    /**
     * Called when the atmospheric composition changes.
     * @param level the current level
     * @param pos the position of the block
     * @param state the current state of the block
     * @param iterator all atmosphere providers at the given position
     * @see #galacticraft$onAtmosphereChange(ServerLevel, BlockPos, BlockState, boolean)
     */
    default void galacticraft$onAtmosphereChange(ServerLevel level, BlockPos pos, BlockState state, Iterator<AtmosphereProvider> iterator) {
        this.galacticraft$onAtmosphereChange(level, pos, state, OxygenUtil.isBreathable(pos, iterator));
    }


    /**
     * Called when the atmospheric composition changes.
     * For blocks that just need to know if the position is breathable or not. Should NOT be called outside this class.
     * @param level the current level
     * @param pos the position of the block
     * @param state the current state of the block
     * @param breathable whether the given position is currently breathable
     * @see #galacticraft$onAtmosphereChange(ServerLevel, BlockPos, BlockState, Iterator)
     */
    default void galacticraft$onAtmosphereChange(ServerLevel level, BlockPos pos, BlockState state, boolean breathable) {
    }
}
