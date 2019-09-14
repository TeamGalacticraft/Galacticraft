/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.energy;

import io.github.cottonmc.energy.api.ElectricalEnergyType;
import io.github.cottonmc.energy.api.EnergyType;
import net.minecraft.text.TranslatableText;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftEnergyType implements EnergyType {

    @Override
    public int getMaximumTransferSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public TranslatableText getDisplayAmount(int amount) {
        float fAmount = (float) amount;

        if (fAmount < 1000) { // x < 1K
            return new TranslatableText("tooltip.galacticraft-rewoven.energy", fAmount);
        } else if (fAmount < 1_000_000) { // 1K < x < 1M
            float tAmount = fAmount / 1000;
            return new TranslatableText("tooltip.galacticraft-rewoven.energy.k", tAmount);
        } else if (fAmount < 1_000_000_000) { // 1M < x < 1G
            float tAmount = fAmount / 1_000_1000;
            return new TranslatableText("tooltip.galacticraft-rewoven.energy.m", tAmount);
        } else { // 1G < x
            float tAmount = fAmount / 1_000_000_000;
            return new TranslatableText("tooltip.galacticraft-rewoven.energy.g", tAmount);
        }
    }

    @Override
    public boolean isCompatibleWith(EnergyType type) {
        return type == this || type instanceof ElectricalEnergyType;
    }

    @Override
    public int convertFrom(EnergyType type, int amount) {
        if (type == this) return amount;
        return (type instanceof ElectricalEnergyType) ? amount * 30 : 0;
    }

    @Override
    public int convertTo(EnergyType type, int amount) {
        if (type == this) return amount;
        return (type instanceof ElectricalEnergyType) ? (int) Math.floor(amount / 30f) : 0;
    }
}
