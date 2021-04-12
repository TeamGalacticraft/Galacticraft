/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.tag;

import com.hrznstudio.galacticraft.Constants;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.mixin.tag.extension.AccessorFluidTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftTags {
    private static final String COMMON_NAMESPACE = "c";
    public static final Tag.Named<Fluid> OIL = TagRegistry.create(new ResourceLocation(COMMON_NAMESPACE, "oil"), AccessorFluidTags.getRequiredTags()::getAllTags);
    public static final Tag.Named<Fluid> FUEL = TagRegistry.create(new ResourceLocation(COMMON_NAMESPACE, "fuel"), AccessorFluidTags.getRequiredTags()::getAllTags);
    public static final Tag.Named<Fluid> OXYGEN = TagRegistry.create(new ResourceLocation(COMMON_NAMESPACE, "oxygen"), AccessorFluidTags.getRequiredTags()::getAllTags);

    public static final Tag.Named<Block> INFINIBURN_MOON = TagRegistry.create(new ResourceLocation(Constants.MOD_ID, "infiniburn_moon"), BlockTags::getAllTags);
    public static final Tag.Named<Block> MOON_STONE = TagRegistry.create(new ResourceLocation(Constants.MOD_ID, "moon_stone"), BlockTags::getAllTags);

    public static void register() {
    }
}
