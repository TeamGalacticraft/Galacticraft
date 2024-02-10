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

package dev.galacticraft.impl.universe.position.type;

import dev.galacticraft.api.universe.position.CelestialPositionType;
import dev.galacticraft.impl.universe.position.config.OrbitalCelestialPositionConfig;

public class OrbitalCelestialPositionType extends CelestialPositionType<OrbitalCelestialPositionConfig> {
    public static final OrbitalCelestialPositionType INSTANCE = new OrbitalCelestialPositionType();

    protected OrbitalCelestialPositionType() {
        super(OrbitalCelestialPositionConfig.CODEC);
    }

    @Override
    public double x(OrbitalCelestialPositionConfig config, long worldTime, float delta) {
        double distanceFromCenter = 3.0 * config.distance() * (config.planet() ? 25.0 : (1.0 / 5.0));
        return Math.sin(((double) (worldTime + delta)) / ((config.planet() ? 200.0 : 2.0) * (config.orbitTime())) + config.phaseShift()) * distanceFromCenter;
    }

    @Override
    public double y(OrbitalCelestialPositionConfig config, long worldTime, float delta) {
        double distanceFromCenter = 3.0 * config.distance() * (config.planet() ? 25.0 : (1.0 / 5.0));
        return Math.cos(((double) (worldTime + delta)) / ((config.planet() ? 200.0 : 2.0) * (config.orbitTime())) + config.phaseShift()) * distanceFromCenter;
    }

    @Override
    public float lineScale(OrbitalCelestialPositionConfig config) {
        return (float) config.distance();
    }
}
