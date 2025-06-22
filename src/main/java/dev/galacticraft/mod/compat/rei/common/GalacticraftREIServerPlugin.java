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
import dev.galacticraft.mod.compat.rei.common.display.DefaultCompressingDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultFabricationDisplay;
import dev.galacticraft.mod.compat.rei.common.display.DefaultRocketDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;

public class GalacticraftREIServerPlugin implements REIServerPlugin {
    public static final CategoryIdentifier<DefaultFabricationDisplay> CIRCUIT_FABRICATION = CategoryIdentifier.of(Constant.MOD_ID, "plugins/circuit_fabricator");
    public static final CategoryIdentifier<DefaultCompressingDisplay> COMPRESSING = CategoryIdentifier.of(Constant.MOD_ID, "plugins/compressing");
//    public static final CategoryIdentifier<DefaultCompressingDisplay> COAL_GENERATOR_FUEL = CategoryIdentifier.of(Constant.MOD_ID, "plugins/coal_generator_fuel");
    public static final CategoryIdentifier<DefaultRocketDisplay> ROCKET = CategoryIdentifier.of(Constant.MOD_ID, "plugins/rocket");

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(CIRCUIT_FABRICATION, BasicDisplay.Serializer.ofSimple(DefaultFabricationDisplay::createRaw));
        registry.register(COMPRESSING, DefaultCompressingDisplay.Serializer.INSTANCE);
        registry.register(ROCKET, BasicDisplay.Serializer.ofSimple(DefaultFabricationDisplay::createRaw));
    }
}
