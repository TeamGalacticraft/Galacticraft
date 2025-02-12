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

package dev.galacticraft.api.rocket;

import dev.galacticraft.mod.util.StreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Defines the 5 stages of launching a rocket.
 */
public enum LaunchStage implements StringRepresentable {
    /**
     * The rocket is on a launch pad.
     */
    IDLE,
    /**
     * The launch button has been pressed once, and the player has been prompted about potential problems with their gear.
     */
    WARNING,
    /**
     * The launch button has been pressed a second time, and the 20-second countdown timer has begun,
     */
    IGNITED,
    /**
     * The rocket has launched.
     */
    LAUNCHED,
    /**
     * The rocket has run out of fuel, hit a wall or some other problem has occurred.
     * The rocket is probably falling out of the sky.
     */
    FAILED;

    public static final StreamCodec<ByteBuf, LaunchStage> STREAM_CODEC = StreamCodecs.ofEnum(LaunchStage.values());

    public LaunchStage next() {
        if (this.ordinal() < LAUNCHED.ordinal()) {
            return LaunchStage.values()[ordinal() + 1];
        } else {
            return LAUNCHED;
        }
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }
}
