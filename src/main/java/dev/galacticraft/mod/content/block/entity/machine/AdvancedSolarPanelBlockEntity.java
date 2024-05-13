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

package dev.galacticraft.mod.content.block.entity.machine;

import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.AbstractSolarPanelBlockEntity;
import dev.galacticraft.mod.content.GCMachineTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedSolarPanelBlockEntity extends AbstractSolarPanelBlockEntity {
    public AdvancedSolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.ADVANCED_SOLAR_PANEL, pos, state);
    }

    @Override
    public boolean followsSun() {
        return true;
    }

    @Override
    public boolean nightCollection() {
        return false;
    }

    @Override
    protected long calculateEnergyProduction(long time, double multiplier) {
        double cos = Math.cos(this.level.getSunAngle(1.0f));
        if (cos <= 0) return 0;
        if (cos <= 0.26761643317033024) {
            return (long) (Galacticraft.CONFIG.solarPanelEnergyProductionRate() * (cos / 0.26761643317033024) * multiplier);
        }
        return (long) (Galacticraft.CONFIG.solarPanelEnergyProductionRate() * multiplier);
    }
}