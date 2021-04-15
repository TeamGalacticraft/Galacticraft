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

package dev.galacticraft.mod.tag;

import dev.galacticraft.mod.Constants;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.mixin.tag.extension.AccessorFluidTags;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftTags {
    private static final String COMMON_NAMESPACE = "c";
    public static final Tag.Identified<Fluid> OIL = TagRegistry.create(new Identifier(COMMON_NAMESPACE, "oil"), AccessorFluidTags.getRequiredTags()::getGroup);
    public static final Tag.Identified<Fluid> FUEL = TagRegistry.create(new Identifier(COMMON_NAMESPACE, "fuel"), AccessorFluidTags.getRequiredTags()::getGroup);
    public static final Tag.Identified<Fluid> LIQUID_OXYGEN = TagRegistry.create(new Identifier(COMMON_NAMESPACE, "oxygen"), AccessorFluidTags.getRequiredTags()::getGroup);

    public static final Tag.Identified<Block> INFINIBURN_MOON = TagRegistry.create(new Identifier(Constants.MOD_ID, "infiniburn_moon"), BlockTags::getTagGroup);
    public static final Tag.Identified<Block> MOON_STONE = TagRegistry.create(new Identifier(Constants.MOD_ID, "moon_stone"), BlockTags::getTagGroup);

    public static void register() {
    }
}
