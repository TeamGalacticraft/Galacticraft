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

package dev.galacticraft.mod.compat.rei.common;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.compat.rei.common.display.DefaultCanningDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultCompressingDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultFabricationDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultRocketDisplay;
import dev.galacticraft.mod.compat.rei.common.display.ElectricArcFurnaceDisplay;
import dev.galacticraft.mod.compat.rei.common.display.ElectricCompressingDisplay;
import dev.galacticraft.mod.compat.rei.common.display.ElectricFurnaceDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;

public class GalacticraftREIServerPlugin implements REIServerPlugin {
    public static final CategoryIdentifier<DefaultFabricationDisplay> FABRICATION = CategoryIdentifier.of(Constant.MOD_ID, "plugins/" + Constant.Recipe.FABRICATION);
    public static final CategoryIdentifier<DefaultCompressingDisplay> COMPRESSING = CategoryIdentifier.of(Constant.MOD_ID, "plugins/" + Constant.Recipe.COMPRESSING);
    public static final CategoryIdentifier<ElectricCompressingDisplay> ELECTRIC_COMPRESSING = CategoryIdentifier.of(Constant.MOD_ID, "plugins/" + Constant.Recipe.ELECTRIC_COMPRESSING);
    public static final CategoryIdentifier<ElectricFurnaceDisplay> ELECTRIC_SMELTING = CategoryIdentifier.of(Constant.MOD_ID, "plugins/" + Constant.Recipe.ELECTRIC_SMELTING);
    public static final CategoryIdentifier<ElectricArcFurnaceDisplay> ELECTRIC_BLASTING = CategoryIdentifier.of(Constant.MOD_ID, "plugins/" + Constant.Recipe.ELECTRIC_BLASTING);
    public static final CategoryIdentifier<DefaultCanningDisplay> CANNING = CategoryIdentifier.of(Constant.MOD_ID, "plugins/" + Constant.Recipe.CANNING);
//    public static final CategoryIdentifier<DefaultCompressingDisplay> COAL_GENERATOR_FUEL = CategoryIdentifier.of(Constant.MOD_ID, "plugins/coal_generator_fuel");
    public static final CategoryIdentifier<DefaultRocketDisplay> ROCKET = CategoryIdentifier.of(Constant.MOD_ID, "plugins/" + Constant.Recipe.ROCKET);

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(FABRICATION, DefaultFabricationDisplay.SERIALIZER);
        registry.register(COMPRESSING, DefaultCompressingDisplay.Serializer.INSTANCE);
        registry.register(ELECTRIC_COMPRESSING, ElectricCompressingDisplay.Serializer.INSTANCE);
        registry.register(ELECTRIC_SMELTING, ElectricFurnaceDisplay.SERIALIZER);
        registry.register(ELECTRIC_BLASTING, ElectricArcFurnaceDisplay.SERIALIZER);
        registry.register(CANNING, DefaultCanningDisplay.SERIALIZER);
        registry.register(ROCKET, DefaultRocketDisplay.SERIALIZER);
    }
}
