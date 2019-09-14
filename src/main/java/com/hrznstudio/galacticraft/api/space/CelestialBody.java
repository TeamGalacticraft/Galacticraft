/*
 * Copyright (c) 2018-2019 Horizon Studio
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

package com.hrznstudio.galacticraft.api.space;


/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public interface CelestialBody {
    /**
     * @return minimum rocket tier required to access the body
     */
    RocketTier accessTier();

    /**
     * @return the body that this body orbits.
     */
    CelestialBody getParent();

    /**
     * @return distance from parent, used to show orbit on travel map
     */
    double getOrbitSize();

    /**
     * @return gravity of the body
     */
    float getGravity();

    /**
     * @return whether or not the body has breathable atmosphere.
     */
    boolean hasOxygen();

    /**
     * @return the icon to display on the galaxy map
     */
    CelestialBodyIcon getIcon();

    /**
     * @return translated string of body's name.
     */
    String getName();
}
