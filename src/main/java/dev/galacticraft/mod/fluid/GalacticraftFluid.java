/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.fluid;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.SimpleFluidKey;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.GalacticraftBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftFluid {
    public static final FlowableFluid CRUDE_OIL = new CrudeOilFluid.Still();
    public static final FlowableFluid FLOWING_CRUDE_OIL = new CrudeOilFluid.Flowing();
    public static final FlowableFluid FUEL = new FuelFluid.Still();
    public static final FlowableFluid FLOWING_FUEL = new FuelFluid.Flowing();
    public static final Fluid LIQUID_OXYGEN = new OxygenFluid();

    public static void register() {
        register(Constant.Fluid.CRUDE_OIL_STILL, CRUDE_OIL);
        register(Constant.Fluid.CRUDE_OIL_FLOWING, FLOWING_CRUDE_OIL);
        register(Constant.Fluid.FUEL_STILL, FUEL);
        register(Constant.Fluid.FUEL_FLOWING, FLOWING_FUEL);
        register(Constant.Fluid.LIQUID_OXYGEN, LIQUID_OXYGEN);

        FluidKeys.put(CRUDE_OIL, new SimpleFluidKey(
                new FluidKey.FluidKeyBuilder(CRUDE_OIL)
                        .setName(new TranslatableText(GalacticraftBlock.CRUDE_OIL.getTranslationKey())
                                .setStyle(Constant.Text.DARK_GRAY_STYLE)
                        )
                        .setViscosity(FluidAmount.of(30, 5))
                        .setCohesion(FluidAmount.ofWhole(2))
                        .setSprites(Constant.Fluid.getId(Constant.Fluid.CRUDE_OIL_STILL), Constant.Fluid.getId(Constant.Fluid.CRUDE_OIL_FLOWING))
                        .setDensity(FluidAmount.of(825, 1000)) // https://www.engineeringtoolbox.com/liquids-densities-d_743.html relative to water
        ));

        FluidKeys.put(FUEL, new SimpleFluidKey(
                new FluidKey.FluidKeyBuilder(FUEL)
                        .setName(new TranslatableText(GalacticraftBlock.FUEL.getTranslationKey())
                                .setStyle(Constant.Text.YELLOW_STYLE)
                        )
                        .setViscosity(FluidAmount.of(10, 5))
                        .setCohesion(FluidAmount.ofWhole(3))
                        .setSprites(Constant.Fluid.getId(Constant.Fluid.FUEL_STILL), Constant.Fluid.getId(Constant.Fluid.FUEL_FLOWING))
                        .setDensity(FluidAmount.of(900, 1000))
        ));

        FluidKeys.put(LIQUID_OXYGEN, new SimpleFluidKey(
                new FluidKey.FluidKeyBuilder(LIQUID_OXYGEN)
                        .setName(new TranslatableText("block.galacticraft.oxygen")
                                .setStyle(Constant.Text.AQUA_STYLE)
                        )
                        .setGas()
        ));
    }

    private static void register(String id, Fluid fluid) {
        Registry.register(Registry.FLUID, new Identifier(Constant.MOD_ID, id), fluid);
    }
}
