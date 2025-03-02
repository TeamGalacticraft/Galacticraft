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

package dev.galacticraft.mod.tag;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class GCFluidTags {
    public static final TagKey<Fluid> OIL = galacticraftTag("oil");
    public static final TagKey<Fluid> OIL_COMMON = commonTag("oil");
    public static final TagKey<Fluid> FUEL = galacticraftTag("fuel");
    public static final TagKey<Fluid> FUEL_COMMON = commonTag("fuel");
    public static final TagKey<Fluid> SULFURIC_ACID = galacticraftTag("sulfuric_acid");
    public static final TagKey<Fluid> SULFURIC_ACID_COMMON = commonTag("sulfuric_acid");
    public static final TagKey<Fluid> LIQUID_OXYGEN = commonTag("oxygen");
    public static final TagKey<Fluid> OXYGEN = galacticraftTag("oxygen");
    public static final TagKey<Fluid> NON_BREATHABLE = galacticraftTag("non_breathable");

    public static TagKey<Fluid> commonTag(String path) {
        return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(Constant.COMMON_NAMESPACE, path));
    }

    public static TagKey<Fluid> galacticraftTag(String path) {
        return TagKey.create(Registries.FLUID, Constant.id(path));
    }

    public static void register() {
    }
}
