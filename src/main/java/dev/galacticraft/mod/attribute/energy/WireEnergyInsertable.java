/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.attribute.energy;

import alexiil.mc.lib.attributes.Simulation;
import dev.galacticraft.energy.api.EnergyInsertable;
import dev.galacticraft.energy.api.EnergyType;
import dev.galacticraft.energy.impl.DefaultEnergyType;
import dev.galacticraft.mod.api.wire.WireNetwork;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class WireEnergyInsertable implements EnergyInsertable {
    private final int maxTransfer;
    private final BlockPos wire;
    private @Nullable WireNetwork network;

    public WireEnergyInsertable(int maxTransfer, BlockPos wire) {
        this.maxTransfer = maxTransfer;
        this.wire = wire;
    }

    @Override
    public int attemptInsertion(EnergyType type, int amount, Simulation simulation) {
        if (this.network != null) {
            if (this.maxTransfer < amount) {
                int over = amount - this.maxTransfer;
                return this.network.insert(this.wire, type.convertTo(DefaultEnergyType.INSTANCE, amount), simulation) + over;
            }
            return this.network.insert(this.wire, type.convertTo(DefaultEnergyType.INSTANCE, amount), simulation);
        }
        return amount;
    }

    @Override
    public EnergyInsertable getPureInsertable() {
        return this;
    }

    public void setNetwork(@Nullable WireNetwork network) {
        this.network = network;
    }
}
