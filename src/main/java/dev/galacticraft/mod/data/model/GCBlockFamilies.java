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

package dev.galacticraft.mod.data.model;

import com.google.common.collect.Maps;
import dev.galacticraft.mod.content.GCBlockRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Contract;

import java.util.Map;
import java.util.stream.Stream;

import static dev.galacticraft.mod.content.GCBlocks.*;

@SuppressWarnings("unused")
public class GCBlockFamilies {
    private static final Map<Block, BlockFamily> MAP = Maps.newHashMap();

    // DECORATIONS
    public static final DecorationFamily ALUMINUM_DECORATIONS = decoration(ALUMINUM_DECORATION);
    public static final DecorationFamily BRONZE_DECORATIONS = decoration(BRONZE_DECORATION);
    public static final DecorationFamily COPPER_DECORATIONS = decoration(COPPER_DECORATION);
    public static final DecorationFamily IRON_DECORATIONS = decoration(IRON_DECORATION);
    public static final DecorationFamily METEORIC_IRON_DECORATIONS = decoration(METEORIC_IRON_DECORATION);
    public static final DecorationFamily STEEL_DECORATIONS = decoration(STEEL_DECORATION);
    public static final DecorationFamily TIN_DECORATIONS = decoration(TIN_DECORATION);
    public static final DecorationFamily TITANIUM_DECORATIONS = decoration(TITANIUM_DECORATION);
    public static final DecorationFamily DARK_DECORATIONS = decoration(DARK_DECORATION);

    // STONES
    public static final BlockFamily MOON_ROCKS = builder(MOON_ROCK)
            .slab(MOON_ROCK_SLAB)
            .stairs(MOON_ROCK_STAIRS)
            .wall(MOON_ROCK_WALL)
            .getFamily();
    public static final BlockFamily COBBLED_MOON_ROCKS = builder(COBBLED_MOON_ROCK)
            .slab(COBBLED_MOON_ROCK_SLAB)
            .stairs(COBBLED_MOON_ROCK_STAIRS)
            .wall(COBBLED_MOON_ROCK_WALL)
            .getFamily();

    public static final BlockFamily LUNASLATES = builder(LUNASLATE)
            .slab(LUNASLATE_SLAB)
            .stairs(LUNASLATE_STAIRS)
            .wall(LUNASLATE_WALL)
            .getFamily();
    public static final BlockFamily COBBLED_LUNASLATES = builder(COBBLED_LUNASLATE)
            .slab(COBBLED_LUNASLATE_SLAB)
            .stairs(COBBLED_LUNASLATE_STAIRS)
            .wall(COBBLED_LUNASLATE_WALL)
            .getFamily();

    public static final BlockFamily MOON_BASALTS = builder(MOON_BASALT)
            .slab(MOON_BASALT_SLAB)
            .stairs(MOON_BASALT_STAIRS)
            .wall(MOON_BASALT_WALL)
            .getFamily();
    public static final BlockFamily MOON_BASALT_BRICKS = builder(MOON_BASALT_BRICK)
            .slab(MOON_BASALT_BRICK_SLAB)
            .stairs(MOON_BASALT_BRICK_STAIRS)
            .wall(MOON_BASALT_BRICK_WALL)
            .getFamily();
    public static final BlockFamily CRACKED_MOON_BASALT_BRICKS = builder(CRACKED_MOON_BASALT_BRICK)
            .slab(CRACKED_MOON_BASALT_BRICK_SLAB)
            .stairs(CRACKED_MOON_BASALT_BRICK_STAIRS)
            .wall(CRACKED_MOON_BASALT_BRICK_WALL)
            .getFamily();

    public static final BlockFamily MARS_STONES = builder(MARS_STONE)
            .slab(MARS_STONE_SLAB)
            .stairs(MARS_STONE_STAIRS)
            .wall(MARS_STONE_WALL)
            .getFamily();
    public static final BlockFamily MARS_COBBLESTONES = builder(MARS_COBBLESTONE)
            .slab(MARS_COBBLESTONE_SLAB)
            .stairs(MARS_COBBLESTONE_STAIRS)
            .wall(MARS_COBBLESTONE_WALL)
            .getFamily();

    @Contract(value = "_ -> new", pure = true)
    private static BlockFamily.Builder builder(Block block) {
        var builder = new BlockFamily.Builder(block);
        var blockFamily = MAP.put(block, builder.getFamily());
        if (blockFamily != null) {
            throw new IllegalStateException("Duplicate family definition for " + BuiltInRegistries.BLOCK.getKey(block));
        } else {
            return builder;
        }
    }

    private static DecorationFamily decoration(GCBlockRegistry.DecorationSet decorationSet) {
        BlockFamily original = builder(decorationSet.block())
                .slab(decorationSet.slab())
                .stairs(decorationSet.stairs())
                .wall(decorationSet.wall())
                .getFamily();
        BlockFamily detailed = builder(decorationSet.detailedBlock())
                .slab(decorationSet.detailedSlab())
                .stairs(decorationSet.detailedStairs())
                .getFamily();
        return new DecorationFamily(original, detailed);
    }

    public static Stream<BlockFamily> getAllFamilies() {
        return MAP.values().stream();
    }

    public record DecorationFamily(BlockFamily original, BlockFamily detailed) {}
}