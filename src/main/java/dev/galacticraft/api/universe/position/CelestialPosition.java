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
import dev.galacticraft.api.registry.BuiltInAddonRegistries;

public record CelestialPosition<C extends CelestialPositionConfig, T extends CelestialPositionType<C>>(T type,
                                                                                                       C config) {
    public static final Codec<CelestialPosition<?, ?>> CODEC = BuiltInAddonRegistries.CELESTIAL_POSITION_TYPE.byNameCodec().dispatch(CelestialPosition::type, CelestialPositionType::codec);

    public double x(long worldTime, float delta) {
        return this.type().x(this.config(), worldTime, delta);
    }

    public double y(long worldTime, float delta) {
        return this.type().y(this.config(), worldTime, delta);
    }

    public float lineScale() {
        return this.type().lineScale(this.config());
    }
}
