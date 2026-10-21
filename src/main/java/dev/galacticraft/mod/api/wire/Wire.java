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

package dev.galacticraft.mod.api.wire;

import dev.galacticraft.mod.api.block.entity.Connected;
import dev.galacticraft.mod.api.grid.energy.EnergyDuct;
import team.reborn.energy.api.EnergyStorage;

/**
 * A wire: a duct of an {@link dev.galacticraft.mod.api.grid.energy.EnergyGrid}.
 */
public interface Wire extends Connected, EnergyDuct {
    /**
     * {@return the energy storage this wire exposes to the blocks around it}
     */
    EnergyStorage getEnergyStorage();

    /**
     * Returns the maximum amount of energy (in gJ) allowed to be transferred through this wire per tick.
     *
     * @return the maximum amount of energy (in gJ) allowed to be transferred through this wire per tick.
     */
    int getMaxTransferRate();

    @Override
    default long getTransferRate() {
        return this.getMaxTransferRate();
    }
}
