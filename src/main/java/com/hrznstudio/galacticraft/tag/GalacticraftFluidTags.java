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
 *
 */

package com.hrznstudio.galacticraft.tag;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftFluidTags {
    // public static final Tag<Fluid> OIL = Tag.Builder.create().add(GalacticraftFluids.CRUDE_OIL, GalacticraftFluids.FLOWING_CRUDE_OIL).build(new Identifier(Constants.MOD_ID, "oil"));
//    public static final Tag<Fluid> FUEL = Tag.Builder.<Fluid>create()/*.add(GalacticraftFluids.FUEL, GalacticraftFluids.FLOWING_FUEL)*/.build(new Identifier(Constants.MOD_ID, "fuel"));
    //public static final Tag<Block> WALLS = Tag.Builder.<Block>create().add(GalacticraftBlocks.DETAILED_TIN_DECORATION_STAIRS, GalacticraftBlocks.MARS_COBBLESTONE_STAIRS, GalacticraftBlocks.MARS_DUNGEON_BRICKS_STAIRS, GalacticraftBlocks.MOON_DUNGEON_BRICKS_STAIRS, GalacticraftBlocks.MOON_ROCK_STAIRS, GalacticraftBlocks.TIN_DECORATION_STAIRS).build(new Identifier("walls"));

    public static final Tag<Fluid> FUEL = FluidTags.LAVA; //todo

    public static void register() {
    }
}
