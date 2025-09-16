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

package dev.galacticraft.mod.content;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Constant.Fluid;
import dev.galacticraft.mod.Constant.Fluid.Gas;
import dev.galacticraft.mod.content.fluid.*;
import dev.galacticraft.mod.content.fluid.gas.GasFluid;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.FlowingFluid;

public class GCFluids {
    public static final GCFluidRegistry FLUIDS = new GCFluidRegistry();
    public static final FlowingFluid CRUDE_OIL = FLUIDS.register(Fluid.CRUDE_OIL_STILL, new CrudeOilFluid.Still());
    public static final FlowingFluid FLOWING_CRUDE_OIL = FLUIDS.register(Fluid.CRUDE_OIL_FLOWING, new CrudeOilFluid.Flowing());
    public static final FlowingFluid FUEL = FLUIDS.register(Fluid.FUEL_STILL, new FuelFluid.Still());
    public static final FlowingFluid FLOWING_FUEL = FLUIDS.register(Fluid.FUEL_FLOWING, new FuelFluid.Flowing());
    public static final FlowingFluid SULFURIC_ACID = FLUIDS.register(Fluid.SULFURIC_ACID_STILL, new SulfuricAcidFluid.Still());
    public static final FlowingFluid FLOWING_SULFURIC_ACID = FLUIDS.register(Fluid.SULFURIC_ACID_FLOWING, new SulfuricAcidFluid.Flowing());

    public static final OxygenFluid LIQUID_OXYGEN = FLUIDS.register(Fluid.LIQUID_OXYGEN, new OxygenFluid());

    // Gases
    public static final class Gases {
        public static final GasFluid HYDROGEN = FLUIDS.registerGas(Gas.HYDROGEN, GasFluid.create(
                Component.translatable(Translations.Gas.HYDROGEN),
                Constant.id("gas/hydrogen"), "H2"
        ));
        public static final GasFluid HELIUM = FLUIDS.registerGas(Gas.HELIUM, GasFluid.create(
                Component.translatable(Translations.Gas.HELIUM),
                Constant.id("gas/helium"), "He"
        ));
        public static final GasFluid NITROGEN = FLUIDS.registerGas(Gas.NITROGEN, GasFluid.create(
                Component.translatable(Translations.Gas.NITROGEN),
                Constant.id("gas/nitrogen"), "N2"
        ));
        public static final GasFluid OXYGEN = FLUIDS.registerGas(Gas.OXYGEN, GasFluid.create(
                Component.translatable(Translations.Gas.OXYGEN),
                Constant.id("gas/oxygen"), "O2"
        ));
        public static final GasFluid NEON = FLUIDS.registerGas(Gas.NEON, GasFluid.create(
                Component.translatable(Translations.Gas.NEON),
                Constant.id("gas/neon"), "Ne"
        ));
        public static final GasFluid OZONE = FLUIDS.registerGas(Gas.OZONE, GasFluid.create(
                Component.translatable(Translations.Gas.OZONE),
                Constant.id("gas/ozone"), "O3"
        ));
        public static final GasFluid WATER_VAPOR = FLUIDS.registerGas(Gas.WATER_VAPOR, GasFluid.create(
                Component.translatable(Translations.Gas.WATER_VAPOR),
                Constant.id("gas/water_vapor"), "H2O"
        ));
    }

    public static void register() {}

    public static void registerFluidVariantAttributes() {
        FluidVariantAttributes.register(CRUDE_OIL, new GCFluidAttribute(
                Component.translatable(GCBlocks.CRUDE_OIL.getDescriptionId())
                        .setStyle(Constant.Text.DARK_GRAY_STYLE),
                FluidConstants.LAVA_VISCOSITY,
                false
        ));
        FluidVariantAttributes.register(FUEL, new GCFluidAttribute(
                Component.translatable(GCBlocks.FUEL.getDescriptionId())
                        .setStyle(Constant.Text.GOLD_STYLE),
                2000,
                false
        ));
        FluidVariantAttributes.register(SULFURIC_ACID, new GCFluidAttribute(
                Component.translatable(GCBlocks.SULFURIC_ACID.getDescriptionId())
                        .setStyle(Constant.Text.GREEN_STYLE),
                FluidConstants.WATER_VISCOSITY,
                false
        ));
        FluidVariantAttributes.register(LIQUID_OXYGEN, new GCFluidAttribute(
                Component.translatable("block.galacticraft.oxygen")
                        .setStyle(Constant.Text.AQUA_STYLE),
                500,
                true
        ));
    }
}
