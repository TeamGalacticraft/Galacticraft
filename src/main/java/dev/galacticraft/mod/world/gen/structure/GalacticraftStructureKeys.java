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

package dev.galacticraft.mod.world.gen.structure;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

public class GalacticraftStructureKeys {
    public static final ResourceKey<Structure> MOON_PILLAGER_BASE = ResourceKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(Constant.MOD_ID, "moon_pillager_base"));
    public static final ResourceKey<Structure> MOON_RUINS = ResourceKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(Constant.MOD_ID, "moon_ruins"));
    public static final ResourceKey<Structure> MOON_VILLAGE_HIGHLANDS = ResourceKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(Constant.MOD_ID, "moon_village_highlands"));
}
