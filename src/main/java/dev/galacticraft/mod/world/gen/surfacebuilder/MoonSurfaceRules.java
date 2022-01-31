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

package dev.galacticraft.mod.world.gen.surfacebuilder;

import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.world.biome.GalacticraftBiomeKey;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.surfacebuilder.MaterialRules.MaterialCondition;
import net.minecraft.world.gen.surfacebuilder.MaterialRules.MaterialRule;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MoonSurfaceRules {
    private static final MaterialCondition IS_MARE = biome(GalacticraftBiomeKey.Moon.MARE, GalacticraftBiomeKey.Moon.MARE_EDGE, GalacticraftBiomeKey.Moon.MARE_FLAT, GalacticraftBiomeKey.Moon.MARE_HILLS, GalacticraftBiomeKey.Moon.MARE_VALLEY);
    private static final MaterialCondition IS_HIGHLANDS = biome(GalacticraftBiomeKey.Moon.HIGHLANDS, GalacticraftBiomeKey.Moon.HIGHLANDS_EDGE, GalacticraftBiomeKey.Moon.HIGHLANDS_FLAT, GalacticraftBiomeKey.Moon.HIGHLANDS_HILLS, GalacticraftBiomeKey.Moon.HIGHLANDS_VALLEY);

    private static final MaterialRule BEDROCK = block(Blocks.BEDROCK);
    private static final MaterialRule LUNASLATE = block(GalacticraftBlock.LUNASLATES[0]);
    private static final MaterialRule MOON_DIRT = block(GalacticraftBlock.MOON_DIRT);
    private static final MaterialRule MOON_TURF = block(GalacticraftBlock.MOON_TURF);
    private static final MaterialRule MOON_BASALT = block(GalacticraftBlock.MOON_BASALTS[0]);
    private static final MaterialRule DEBUG_STATE = block(GalacticraftBlock.ALUMINUM_DECORATIONS[0]);

    private static final MaterialRule SECONDARY_MATERIAL = MaterialRules.sequence(
            MaterialRules.condition(IS_MARE, MOON_BASALT),
            MaterialRules.condition(IS_HIGHLANDS, MOON_DIRT),
            DEBUG_STATE
    );
    private static final MaterialRule SURFACE_MATERIAL = MaterialRules.sequence(
            MaterialRules.condition(IS_MARE, MOON_BASALT),
            MaterialRules.condition(IS_HIGHLANDS, MOON_TURF),
            DEBUG_STATE
    );
    private static final MaterialRule SURFACE_GENERATION = MaterialRules.sequence(
            MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, SURFACE_MATERIAL),
            MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR_WITH_SURFACE_DEPTH, SECONDARY_MATERIAL)
    );

    @Contract("_ -> new")
    private static @NotNull MaterialRule block(@NotNull Block block) {
        return MaterialRules.block(block.getDefaultState());
    }

    public static @NotNull MaterialRule createDefaultRule() {
        return MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.verticalGradient("bedrock_floor", YOffset.getBottom(), YOffset.aboveBottom(5)), BEDROCK),
                MaterialRules.condition(MaterialRules.surface(), SURFACE_GENERATION),
                MaterialRules.condition(MaterialRules.verticalGradient("lunaslate", YOffset.fixed(-4), YOffset.fixed(4)), LUNASLATE)
        );
    }

    @Contract("_ -> new")
    public static MaterialRules.@NotNull MaterialCondition biome(@NotNull Tag<Biome> biome) {
        return new BiomeTagRule(biome);
    }

    @SafeVarargs
    @Contract("_ -> new")
    public static MaterialRules.@NotNull MaterialCondition biome(@NotNull RegistryKey<Biome> @NotNull... keys) {
        return MaterialRules.biome(keys);
    }
}
