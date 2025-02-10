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

package dev.galacticraft.api.universe.position;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

public abstract class CelestialPositionType<C extends CelestialPositionConfig> {
    private final MapCodec<CelestialPosition<C, CelestialPositionType<C>>> codec;

    public CelestialPositionType(Codec<C> codec) {
        this.codec = codec.fieldOf("config").xmap((config) -> new CelestialPosition<>(this, config), CelestialPosition::config);
    }

    public abstract double x(C config, long worldTime, float delta);

    public abstract double y(C config, long worldTime, float delta);

    public float lineScale(C config) {
        return Float.NaN;
    }

    public MapCodec<CelestialPosition<C, CelestialPositionType<C>>> codec() {
        return this.codec;
    }

    public CelestialPosition<C, CelestialPositionType<C>> configure(C config) {
        return new CelestialPosition<>(this, config);
    }
}
