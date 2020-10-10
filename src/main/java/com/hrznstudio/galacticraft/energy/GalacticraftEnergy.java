/*
 * Copyright (c) 2020 HRZN LTD
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

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import io.github.cottonmc.component.energy.CapacitorComponentHelper;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.TankComponentHelper;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.energy.impl.ElectricalEnergyType;
import io.github.cottonmc.component.energy.impl.WUEnergyType;
import io.github.cottonmc.component.energy.type.EnergyType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftEnergy {
    public static final EnergyType GALACTICRAFT_JOULES = Registry.register(UniversalComponents.ENERGY_TYPES, new Identifier(Constants.MOD_ID, Constants.Energy.GALACTICRAFT_JOULES), new GalacticraftJoules());

    public static final Predicate<ItemStack> ENERGY_HOLDER_ITEM_FILTER = EnergyUtils::isEnergyItem;

    public static void register() {
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private static final class GalacticraftJoules extends ElectricalEnergyType {

        @Override
        public int getMaximumTransferSize() {
            return Integer.MAX_VALUE;
        }

        @Override
        public float getEnergyPerFuelTick() {
            return 120;
        }

        @Override
        public String getDisplaySubkey() {
            return "galacticraft-rewoven";
        }

        @Override
        public int convertFrom(EnergyType type, int amount) {
            if (type == this) return amount;
            return (type instanceof WUEnergyType) ? amount * 192 : 0;
        }

        @Override
        public int convertTo(EnergyType type, int amount) {
            if (type == this) return amount;
            return (type instanceof WUEnergyType) ? (int) Math.floor(amount / 192f) : 0;
        }
    }
}
