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

package dev.galacticraft.api.entity;

import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;

public interface Rocket {
    /**
     * Returns the colour of the rocket in ARGB format (8 bits per colour)
     *
     * @return the colour of the rocket
     */
    int getColor();

    /**
     * Sets the colour of the rocket in ARGB format (8 bits per colour)
     *
     * @param color the colour of the rocket
     */
    void setColor(int color);

    /**
     * Returns the launch stage of this rocket
     *
     * @return the launch stage of this rocket
     */
    LaunchStage getStage();

    /**
     * Sets the launch stage of this rocket
     *
     * @param stage the launch stage to set
     */
    void setStage(LaunchStage stage);

    /**
     * Returns all six rocket parts applied to this rocket
     *
     * @return all parts applied to this rocket
     */
    ResourceLocation[/*6*/] getPartIds();

    /**
     * Returns the rocket launch pad linked to this rocket or {@link BlockPos#ZERO} if it is not linked to one
     *
     * @return the rocket launch pad linked to this rocket
     */
    @NotNull BlockPos getLinkedPad();

    /**
     * Sets the rocket launch pad linked to this rocket
     * If the rocket is not linked to a launchpad, use {@link BlockPos#ZERO}
     *
     * @param linkedPad the launchpad to link this rocket with
     */
    void setLinkedPad(@NotNull BlockPos linkedPad);

    /**
     * Returns whether this rocket is able to travel to the supplied celestial body
     *
     * @param body the celestial body to test against
     * @return whether this rocket is able to travel to the supplied celestial body
     */
    boolean canTravelTo(CelestialBody<?, ?> body);

    /**
     * Returns the speed of the rocket
     * This does equate to the vertical velocity of this rocket, as rockets may turn
     *
     * @return the speed of the rocket
     */
    double getSpeed();

    /**
     * Sets the speed of the rocket
     *
     * @param speed the speed to force the rocket to
     */
    void setSpeed(double speed);

    /**
     * Called when the player riding the rocket jumps
     * Used to initiate the launch countdown in the rocket
     */
    void onJump();


    /**
     * Called when the rocket launch pad linked to this rocket is destroyed
     */
    void onBaseDestroyed();

    /**
     * Called when the rocket is destroyed
     *
     * @param source   the type of damage inflicted on this rocket
     * @param exploded whether the damage is self-inflicted (the rocket failed)
     */
    void dropItems(DamageSource source, boolean exploded);

    /**
     * Swaps out a part of this rocket with the provided one
     *
     * @param part the rocket part to swap in
     * @param type the type of rocket part to swap
     * @throws AssertionError {@code if (part.type != type) } and assertions are enabled
     */
    void setPart(ResourceLocation part, RocketPartType type);

    /**
     * Replaces all the current parts of this rocket with new ones.
     * The part type of a rocket part at any given index should equal to
     *
     * @param parts the parts to put in
     */
    void setParts(ResourceLocation[/*6*/] parts);

    /**
     * Returns the id of the rocket part applied to this rocket of this {@code type}
     *
     * @param type the type of part to return
     * @return the id of the rocket part applied to this rocket of this {@code type}
     */
    ResourceLocation getPartForType(RocketPartType type);
}
