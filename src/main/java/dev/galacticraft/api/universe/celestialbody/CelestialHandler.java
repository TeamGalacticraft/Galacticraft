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

package dev.galacticraft.api.universe.celestialbody;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.registry.BuiltInAddonRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Used for direct logic
 */
public interface CelestialHandler {
    Codec<CelestialHandler> CODEC = BuiltInAddonRegistries.CELESTIAL_HANDLER.byNameCodec();

    /**
     * Gets the parachest spawn location when entering this dimension. Return
     * null for no parachest spawn
     *
     * @param world The world to be spawned into
     * @param player The player being teleported
     * @param rand The random instance
     * @return a vector3 object containing the coordinates to be spawned into
     *         the world with. Return null for no spawn
     */
    @Nullable
    Vec3 getParachestSpawnLocation(Level world, Player player, RandomSource rand);

    /**
     * This method is used to determine if a player will open parachute upon
     * entering the dimension
     *
     * @return whether player will set parachute open upon entering this
     *         dimension
     */
    boolean useParachute();
}