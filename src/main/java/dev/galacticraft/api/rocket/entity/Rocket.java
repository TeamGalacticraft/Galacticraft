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

package dev.galacticraft.api.rocket.entity;

import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.List;

public interface Rocket extends RocketData {
    /**
     * Returns the launch stage of this rocket.
     * @return the launch stage of this rocket.
     */
    LaunchStage getLaunchStage();

    void setLaunchStage(LaunchStage stage);

    RocketCone<?, ?> getCone();

    RocketBody<?, ?> getBody();

    RocketFin<?, ?> getFin();

    RocketBooster<?, ?> getBooster();

    RocketBottom<?, ?> getBottom();

    RocketUpgrade<?, ?> getUpgrade();

    @NotNull BlockPos getLinkedPad();

    void setLinkedPad(@NotNull BlockPos linkedPad);

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

    @Nullable Fluid getFuelTankFluid();
    long getFuelTankAmount();
    long getFuelTankCapacity();

    // utility entity methods

    Entity getEntity();

    Level level();

    Vec3 position();

    BlockPos blockPosition();

    Vector3d getVelocity();

    BlockPos getBlockPos();

    double getX();

    double getY();

    double getZ();

    List<Entity> getPassengers();

    // end utility entity methods
}