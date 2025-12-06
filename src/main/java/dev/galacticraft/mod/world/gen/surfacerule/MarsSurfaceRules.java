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

package dev.galacticraft.mod.world.gen.surfacerule;

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.biome.GCBiomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import org.jetbrains.annotations.NotNull;

public class MarsSurfaceRules {
    private static final SurfaceRules.RuleSource BEDROCK          = block(Blocks.BEDROCK);
    private static final SurfaceRules.RuleSource MARS_STONE       = block(GCBlocks.MARS_STONE);
    private static final SurfaceRules.RuleSource SUB_SURFACE_ROCK = block(GCBlocks.MARS_SUB_SURFACE_ROCK);
    private static final SurfaceRules.RuleSource SURFACE_ROCK     = block(GCBlocks.MARS_SURFACE_ROCK);

    private static final SurfaceRules.ConditionSource IS_MARS = SurfaceRules.isBiome(
            GCBiomes.Mars.MARS_HIGHLANDS,
            GCBiomes.Mars.MARS_LOWLANDS
    );

    // Depth 0 from floor = Surface block
    private static final SurfaceRules.ConditionSource MARS_FLOOR =
            SurfaceRules.stoneDepthCheck(0, false, 0, CaveSurface.FLOOR);

    // Depth 1 - 6 below the surface = Sub-surface block
    private static final SurfaceRules.ConditionSource MARS_SUB_SURFACE =
            SurfaceRules.stoneDepthCheck(1, false, 4, CaveSurface.FLOOR);

    private static final SurfaceRules.RuleSource SURFACE_GENERATION = SurfaceRules.sequence(
            SurfaceRules.ifTrue(MARS_FLOOR, SURFACE_ROCK),
            SurfaceRules.ifTrue(MARS_SUB_SURFACE, SUB_SURFACE_ROCK),
            // Everything deeper in the column falls back to Mars stone
            MARS_STONE
    );

    public static final SurfaceRules.RuleSource MARS = SurfaceRules.ifTrue(
            IS_MARS,
            SurfaceRules.sequence(
                    SurfaceRules.ifTrue(
                            SurfaceRules.verticalGradient(
                                    "bedrock_floor",
                                    VerticalAnchor.bottom(),
                                    VerticalAnchor.aboveBottom(5)
                            ),
                            BEDROCK
                    ),
                    // Surface & sub-surface for the rest of the column
                    SURFACE_GENERATION
            )
    );

    private static @NotNull SurfaceRules.RuleSource block(@NotNull Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
}
