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

package dev.galacticraft.api.rocket.part.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.galacticraft.api.rocket.part.RocketBody;
import dev.galacticraft.api.rocket.part.config.RocketBodyConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The body of a rocket. Controls the number of installable rocket upgrades and the maximum passenger count.
 */
public non-sealed abstract class RocketBodyType<C extends RocketBodyConfig> implements RocketPartType<C> {
    private final @NotNull MapCodec<RocketBody<C, RocketBodyType<C>>> codec;

    protected RocketBodyType(@NotNull Codec<C> configCodec) {
        this.codec = configCodec.fieldOf("config").xmap(this::configure, RocketBody::config);
    }

    @Override
    public @NotNull RocketBody<C, RocketBodyType<C>> configure(@NotNull C config) {
        return RocketBody.create(config, this);
    }

    @Override
    public @NotNull MapCodec<? extends RocketBody<C, ? extends RocketBodyType<C>>> codec() {
        return this.codec;
    }

    /**
     * Returns the maximum number of passengers allowed on this rocket.
     * @return the maximum number of passengers allowed on this rocket.
     */
    @Contract(pure = true)
    public abstract int getMaxPassengers(@NotNull C config);
}
