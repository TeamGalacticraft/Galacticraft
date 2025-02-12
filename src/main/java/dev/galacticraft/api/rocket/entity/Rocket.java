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

package dev.galacticraft.api.rocket.entity;

import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.mod.api.entity.Dockable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Rocket extends Dockable {
    /**
     * Returns the launch stage of this rocket.
     * @return the launch stage of this rocket.
     */
    LaunchStage getLaunchStage();

    void setLaunchStage(LaunchStage stage);

    @NotNull RocketData getRocketData();

    @Nullable Holder<RocketCone<?, ?>> cone();

    @Nullable Holder<RocketBody<?, ?>> body();

    @Nullable Holder<RocketFin<?, ?>> fin();

    @Nullable Holder<RocketBooster<?, ?>> booster();

    @Nullable Holder<RocketEngine<?, ?>> engine();

    @Nullable Holder<RocketUpgrade<?, ?>> upgrade();

    @NotNull BlockPos getLinkedPad();

    /**
     * Called when the player riding the rocket jumps
     * Used to initiate the launch countdown in the rocket
     */
    void onJump();

    /**
     * Called when the rocket is destroyed
     *
     * @param source   the type of damage inflicted on this rocket
     * @param exploded whether the damage is self-inflicted (the rocket failed)
     */
    void dropItems(DamageSource source, boolean exploded);
}