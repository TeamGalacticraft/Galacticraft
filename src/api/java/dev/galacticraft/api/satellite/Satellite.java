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

package dev.galacticraft.api.satellite;

import dev.galacticraft.api.universe.celestialbody.CelestialBodyConfig;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a player-made satellite which orbits another planet
 *
 * @param <C> the type of configuration
 */
public interface Satellite<C extends CelestialBodyConfig> {
    /**
     * Returns the ownership data of this satellite
     *
     * @param config the satellite configuration to be queried
     * @return the ownership data of this satellite
     * @see SatelliteOwnershipData
     */
    SatelliteOwnershipData ownershipData(C config);

    /**
     * Returns the custom name of this satellite
     * By default it is {@code <Player Name>'s satellite}
     *
     * @param config the satellite configuration to be queried
     * @return the custom name of this satellite
     */
    @NotNull String getCustomName(C config);

    /**
     * Sets the custom name of this satellite
     *
     * @param text   the text to set
     * @param config the satellite configuration to be set
     */
    void setCustomName(@NotNull String text, C config);
}
