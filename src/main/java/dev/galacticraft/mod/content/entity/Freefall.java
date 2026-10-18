/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.content.entity;

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.universe.celestialbody.type.SatelliteType;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Floating in zero gravity on space stations, like in Galacticraft 4.
 * An entity is in freefall when nothing is close enough to hold on to. It then keeps its momentum,
 * and a player can only drift slowly with the movement keys. Near a surface, a player can push off it by holding jump.
 */
public final class Freefall {
    /**
     * The speed the movement keys add per tick while floating, instead of the 0.02 of vanilla in the air.
     */
    public static final float MOVEMENT_ACCELERATION = 1.0F / 200.0F;
    /**
     * The vertical speed jumping or sneaking adds per tick while floating.
     */
    public static final double VERTICAL_ACCELERATION = 0.0032;
    /**
     * The vertical speed jumping or sneaking adds per tick while holding on to a wall.
     */
    public static final double WALL_CLIMB_ACCELERATION = 0.015;
    /**
     * The maximum speed of a floating player along each axis.
     */
    public static final double MAX_PLAYER_SPEED = 0.7;
    /**
     * The maximum speed of other floating living entities along each axis, as nothing slows them down.
     */
    public static final double MAX_ENTITY_SPEED = 10.0;
    /**
     * The upward speed each tick of holding jump on a surface adds when it is released.
     */
    public static final double PUSH_OFF_SPEED_PER_TICK = 0.0145;
    /**
     * The maximum number of ticks jump can be held to push off a surface.
     */
    public static final int MAX_PUSH_OFF_TICKS = 25;
    /**
     * How close a block has to be for an entity to hold on to it, horizontally and vertically.
     */
    private static final double REACH = 0.2;
    private static final double VERTICAL_REACH = 0.1;

    private Freefall() {
    }

    /**
     * {@return whether there is no gravity to stand on in the given level}
     */
    public static boolean hasZeroGravity(Level level) {
        Holder<CelestialBody<?, ?>> holder = level.galacticraft$getCelestialBody();
        return holder != null && holder.value().type() instanceof SatelliteType;
    }

    /**
     * {@return whether the entity is floating freely, with nothing close enough to hold on to}
     */
    public static boolean isInFreefall(Entity entity) {
        if (!hasZeroGravity(entity.level()) || entity.onGround() || entity.isPassenger() || entity.isInWater() || entity.isInLava()) {
            return false;
        }
        if (entity instanceof Player player && (player.getAbilities().flying || player.isSpectator())) {
            return false;
        }
        return entity.level().getBlockStates(entity.getBoundingBox().inflate(REACH, VERTICAL_REACH, REACH)).allMatch(BlockState::isAir);
    }
}
