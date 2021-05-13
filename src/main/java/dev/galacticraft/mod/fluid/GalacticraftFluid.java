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

package dev.galacticraft.mod.fluid;

import dev.galacticraft.mod.Constant;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftFluid {

    public static final FlowableFluid CRUDE_OIL = Registry.register(Registry.FLUID, new Identifier(Constant.MOD_ID, Constant.Fluid.CRUDE_OIL_STILL), new CrudeOilFluid.Still());
    public static final FlowableFluid FLOWING_CRUDE_OIL = Registry.register(Registry.FLUID, new Identifier(Constant.MOD_ID, Constant.Fluid.CRUDE_OIL_FLOWING), new CrudeOilFluid.Flowing());
    public static final FlowableFluid FUEL = Registry.register(Registry.FLUID, new Identifier(Constant.MOD_ID, Constant.Fluid.FUEL_STILL), new FuelFluid.Still());
    public static final FlowableFluid FLOWING_FUEL = Registry.register(Registry.FLUID, new Identifier(Constant.MOD_ID, Constant.Fluid.FUEL_FLOWING), new FuelFluid.Flowing());
    public static final FlowableFluid BACTERIAL_SLUDGE = Registry.register(Registry.FLUID, new Identifier(Constant.MOD_ID, Constant.Fluid.BACTERIAL_SLUDGE_STILL), new BacterialSludgeFluid.Still());
    public static final FlowableFluid FLOWING_BACTERIAL_SLUDGE = Registry.register(Registry.FLUID, new Identifier(Constant.MOD_ID, Constant.Fluid.BACTERIAL_SLUDGE_FLOWING), new BacterialSludgeFluid.Flowing());
    public static final Fluid LIQUID_OXYGEN = Registry.register(Registry.FLUID, new Identifier(Constant.MOD_ID, Constant.Fluid.LIQUID_OXYGEN), new OxygenFluid());

    public static void register() {

    }
}
