/*
 * Copyright (c) 2019-2024 Team Galacticraft
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
import dev.galacticraft.mod.content.GCRegistry;
import dev.galacticraft.mod.structure.dungeon.DungeonStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class GCStructureTypes {
    public static final GCRegistry<StructureType<?>> STRUCTURES = new GCRegistry<>(BuiltInRegistries.STRUCTURE_TYPE);
    public static final StructureType<MoonRuinsStructure> MOON_RUINS = STRUCTURES.register("moon_ruins", () -> MoonRuinsStructure.CODEC);
    public static final StructureType<DungeonStructure> MOON_DUNGEON = STRUCTURES.register("moon_dungeon", () -> DungeonStructure.CODEC);

    public static void register() {
    }
}
