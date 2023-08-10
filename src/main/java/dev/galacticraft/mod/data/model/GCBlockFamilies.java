/*
 * Copyright (c) 2019-2023 Team Galacticraft
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
    public static final BlockFamily ALUMINUM_DECORATIONS = builder(ALUMINUM_DECORATION)
            .slab(ALUMINUM_DECORATION_SLAB)
            .stairs(ALUMINUM_DECORATION_STAIRS)
            .wall(ALUMINUM_DECORATION_WALL)
            .getFamily();
    public static final BlockFamily DETAILED_ALUMINUM_DECORATIONS = builder(DETAILED_ALUMINUM_DECORATION)
            .slab(DETAILED_ALUMINUM_DECORATION_SLAB)
            .stairs(DETAILED_ALUMINUM_DECORATION_STAIRS)
            .wall(DETAILED_ALUMINUM_DECORATION_WALL)
            .getFamily();

    public static final BlockFamily BRONZE_DECORATIONS = builder(BRONZE_DECORATION)
            .slab(BRONZE_DECORATION_SLAB)
            .stairs(BRONZE_DECORATION_STAIRS)
            .wall(BRONZE_DECORATION_WALL)
            .getFamily();
    public static final BlockFamily DETAILED_BRONZE_DECORATIONS = builder(DETAILED_BRONZE_DECORATION)
            .slab(DETAILED_BRONZE_DECORATION_SLAB)
            .stairs(DETAILED_BRONZE_DECORATION_STAIRS)
            .wall(DETAILED_BRONZE_DECORATION_WALL)
            .getFamily();

    public static final BlockFamily COPPER_DECORATIONS = builder(COPPER_DECORATION)
            .slab(COPPER_DECORATION_SLAB)
            .stairs(COPPER_DECORATION_STAIRS)
            .wall(COPPER_DECORATION_WALL)
            .getFamily();
    public static final BlockFamily DETAILED_COPPER_DECORATIONS = builder(DETAILED_COPPER_DECORATION)
            .slab(DETAILED_COPPER_DECORATION_SLAB)
            .stairs(DETAILED_COPPER_DECORATION_STAIRS)
            .wall(DETAILED_COPPER_DECORATION_WALL)
            .getFamily();

    public static final BlockFamily IRON_DECORATIONS = builder(IRON_DECORATION)
            .slab(IRON_DECORATION_SLAB)
            .stairs(IRON_DECORATION_STAIRS)
            .wall(IRON_DECORATION_WALL)
            .getFamily();
    public static final BlockFamily DETAILED_IRON_DECORATIONS = builder(DETAILED_IRON_DECORATION)
            .slab(DETAILED_IRON_DECORATION_SLAB)
            .stairs(DETAILED_IRON_DECORATION_STAIRS)
            .wall(DETAILED_IRON_DECORATION_WALL)
            .getFamily();

    public static final BlockFamily METEORIC_IRON_DECORATIONS = builder(METEORIC_IRON_DECORATION)
            .slab(METEORIC_IRON_DECORATION_SLAB)
            .stairs(METEORIC_IRON_DECORATION_STAIRS)
            .wall(METEORIC_IRON_DECORATION_WALL)
            .getFamily();
    public static final BlockFamily DETAILED_METEORIC_IRON_DECORATIONS = builder(DETAILED_METEORIC_IRON_DECORATION)
            .slab(DETAILED_METEORIC_IRON_DECORATION_SLAB)
            .stairs(DETAILED_METEORIC_IRON_DECORATION_STAIRS)
            .wall(DETAILED_METEORIC_IRON_DECORATION_WALL)
            .getFamily();

    public static final BlockFamily STEEL_DECORATIONS = builder(STEEL_DECORATION)
            .slab(STEEL_DECORATION_SLAB)
            .stairs(STEEL_DECORATION_STAIRS)
            .wall(STEEL_DECORATION_WALL)
            .getFamily();
    public static final BlockFamily DETAILED_STEEL_DECORATIONS = builder(DETAILED_STEEL_DECORATION)
            .slab(DETAILED_STEEL_DECORATION_SLAB)
            .stairs(DETAILED_STEEL_DECORATION_STAIRS)
            .wall(DETAILED_STEEL_DECORATION_WALL)
            .getFamily();

    public static final BlockFamily TIN_DECORATIONS = builder(TIN_DECORATION)
            .slab(TIN_DECORATION_SLAB)
            .stairs(TIN_DECORATION_STAIRS)
            .wall(TIN_DECORATION_WALL)
            .getFamily();
    public static final BlockFamily DETAILED_TIN_DECORATIONS = builder(DETAILED_TIN_DECORATION)
            .slab(DETAILED_TIN_DECORATION_SLAB)
            .stairs(DETAILED_TIN_DECORATION_STAIRS)
            .wall(DETAILED_TIN_DECORATION_WALL)
            .getFamily();

    public static final BlockFamily TITANIUM_DECORATIONS = builder(TITANIUM_DECORATION)
            .slab(TITANIUM_DECORATION_SLAB)
            .stairs(TITANIUM_DECORATION_STAIRS)
            .wall(TITANIUM_DECORATION_WALL)
            .getFamily();
    public static final BlockFamily DETAILED_TITANIUM_DECORATIONS = builder(DETAILED_TITANIUM_DECORATION)
            .slab(DETAILED_TITANIUM_DECORATION_SLAB)
            .stairs(DETAILED_TITANIUM_DECORATION_STAIRS)
            .wall(DETAILED_TITANIUM_DECORATION_WALL)
            .getFamily();

    public static final BlockFamily DARK_DECORATIONS = builder(DARK_DECORATION)
            .slab(DARK_DECORATION_SLAB)
            .stairs(DARK_DECORATION_STAIRS)
            .wall(DARK_DECORATION_WALL)
            .getFamily();
    public static final BlockFamily DETAILED_DARK_DECORATIONS = builder(DETAILED_DARK_DECORATION)
            .slab(DETAILED_DARK_DECORATION_SLAB)
            .stairs(DETAILED_DARK_DECORATION_STAIRS)
            .wall(DETAILED_DARK_DECORATION_WALL)
            .getFamily();

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

    public static Stream<BlockFamily> getAllFamilies() {
        return MAP.values().stream();
    }
}