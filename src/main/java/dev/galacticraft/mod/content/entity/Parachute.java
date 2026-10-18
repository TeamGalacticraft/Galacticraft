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

import dev.galacticraft.mod.content.GCSounds;
import dev.galacticraft.mod.content.item.ParachuteItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * The parachute that opens when a player wearing one re-enters a planet's atmosphere, like in Galacticraft 4.
 */
public final class Parachute {
    /**
     * The constant speed at which a player falls with an open parachute, in blocks per tick.
     */
    public static final double FALL_SPEED = 0.5;
    /**
     * The factor the horizontal speed is multiplied by each tick while the parachute is open.
     */
    public static final double HORIZONTAL_DRAG = 0.5;

    private Parachute() {
    }

    /**
     * Returns the parachute in the player's accessory slots, or {@code null} if they don't wear one.
     */
    public static @Nullable ParachuteItem getParachute(Player player) {
        Container accessories = player.galacticraft$getAccessories();
        for (int slot = 0; slot < accessories.getContainerSize(); slot++) {
            ItemStack stack = accessories.getItem(slot);
            if (stack.getItem() instanceof ParachuteItem parachute) {
                return parachute;
            }
        }
        return null;
    }

    /**
     * Opens the parachute of a player who has just entered the atmosphere, if they wear one.
     */
    public static void deploy(ServerPlayer player) {
        if (getParachute(player) == null) {
            return;
        }
        // The position was just changed by a teleport, so the player cannot be standing on anything yet
        player.setOnGround(false);
        player.galacticraft$setParachuting(true);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), GCSounds.PARACHUTE, SoundSource.PLAYERS, 0.95F + player.getRandom().nextFloat() * 0.1F, 1.0F);
    }

    /**
     * Whether the slow fall applies this tick.
     */
    public static boolean slowsFall(Player player) {
        return !player.getAbilities().flying && !player.isFallFlying() && !player.isInLiquid();
    }

    /**
     * Whether the parachute should close: the player landed, stopped falling some other way or took the parachute off.
     */
    public static boolean shouldClose(Player player) {
        return player.onGround() || player.isInLiquid() || player.isPassenger() || player.isSpectator()
                || player.getAbilities().flying || player.isFallFlying() || getParachute(player) == null;
    }
}
