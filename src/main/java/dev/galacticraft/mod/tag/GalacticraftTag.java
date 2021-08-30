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

package dev.galacticraft.mod.tag;

import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftTag {

    public static final Tag.Identified<Fluid> OIL = TagFactory.FLUID.create(new Identifier(Constant.COMMON_NAMESPACE, "oil"));
    public static final Tag.Identified<Fluid> FUEL = TagFactory.FLUID.create(new Identifier(Constant.COMMON_NAMESPACE, "fuel"));
    public static final Tag.Identified<Fluid> LIQUID_OXYGEN = TagFactory.FLUID.create(new Identifier(Constant.COMMON_NAMESPACE, "oxygen"));

    public static final Tag.Identified<Block> INFINIBURN_MOON = TagFactory.BLOCK.create(new Identifier(Constant.MOD_ID, "infiniburn_moon"));
    public static final Tag.Identified<Block> MOON_STONE = TagFactory.BLOCK.create(new Identifier(Constant.MOD_ID, "moon_stone"));

    public static final Tag.Identified<Biome> MOON = TagFactory.BIOME.create(new Identifier(Constant.MOD_ID, "moon"));
    public static final Tag.Identified<Biome> MOON_MARE = TagFactory.BIOME.create(new Identifier(Constant.MOD_ID, "moon_mare"));

    public static void register() {
    }
}
