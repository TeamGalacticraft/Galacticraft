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

import com.hrznstudio.galacticraft.Constants;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftTags {
    public static final Tag<Fluid> OIL = TagRegistry.fluid(new Identifier(Constants.Compatibility.COMMON_MOD_ID, "oil"));
    public static final Tag<Fluid> FUEL = TagRegistry.fluid(new Identifier(Constants.Compatibility.COMMON_MOD_ID, "fuel"));
    public static final Tag<Fluid> OXYGEN = TagRegistry.fluid(new Identifier(Constants.Compatibility.COMMON_MOD_ID, "oxygen"));

    public static final Tag<Block> INFINIBURN_MOON = TagRegistry.block(new Identifier(Constants.MOD_ID, "infiniburn_moon"));
    public static final Tag<Block> MOON_STONE = TagRegistry.block(new Identifier(Constants.MOD_ID, "moon_stone"));

    //COTTON RESOURCES IDS (they haven't updated to 1.16.2)
    public static final Tag<Item> C_COPPER_INGOTS = TagRegistry.item(new Identifier(Constants.Compatibility.COMMON_MOD_ID, "copper_ingots"));
    public static final Tag<Item> C_TIN_INGOTS = TagRegistry.item(new Identifier(Constants.Compatibility.COMMON_MOD_ID, "tin_ingots"));
    public static final Tag<Item> C_ALU_INGOTS = TagRegistry.item(new Identifier(Constants.Compatibility.COMMON_MOD_ID, "aluminum_ingots"));

    public static void register() {
    }
}
