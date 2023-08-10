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

package dev.galacticraft.mod.data.tag;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.tag.GCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class GCBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public GCBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.getOrCreateTagBuilder(GCTags.INFINIBURN_MOON)
                .add(GCBlocks.MOON_BASALT_BRICK);
        this.getOrCreateTagBuilder(GCTags.BASE_STONE_MOON)
                .add(GCBlocks.MOON_ROCK);
        this.getOrCreateTagBuilder(GCTags.MOON_CARVER_REPLACEABLES)
                .add(GCBlocks.MOON_ROCK)
                .add(GCBlocks.MOON_SURFACE_ROCK)
                .add(GCBlocks.MOON_BASALT)
                .add(GCBlocks.MOON_DIRT)
                .add(GCBlocks.MOON_TURF);
        this.getOrCreateTagBuilder(GCTags.MOON_CRATER_CARVER_REPLACEABLES)
                .add(GCBlocks.MOON_ROCK)
                .add(GCBlocks.MOON_SURFACE_ROCK)
                .add(GCBlocks.MOON_BASALT)
                .add(GCBlocks.MOON_DIRT)
                .add(GCBlocks.MOON_TURF);
        this.getOrCreateTagBuilder(GCTags.MOON_STONE_ORE_REPLACABLES)
                .add(GCBlocks.MOON_ROCK)
                .add(GCBlocks.MOON_BASALT);
        this.getOrCreateTagBuilder(GCTags.LUNASLATE_ORE_REPLACABLES)
                .add(GCBlocks.LUNASLATE);
        this.getOrCreateTagBuilder(GCTags.MACHINES)
                .add(
                        GCBlocks.CIRCUIT_FABRICATOR,
                        GCBlocks.COMPRESSOR,
                        GCBlocks.ELECTRIC_COMPRESSOR,
                        GCBlocks.COAL_GENERATOR,
                        GCBlocks.BASIC_SOLAR_PANEL,
                        GCBlocks.ADVANCED_SOLAR_PANEL,
                        GCBlocks.ENERGY_STORAGE_MODULE,
                        GCBlocks.ELECTRIC_FURNACE,
                        GCBlocks.ELECTRIC_ARC_FURNACE,
                        GCBlocks.REFINERY,
                        GCBlocks.OXYGEN_COLLECTOR,
                        GCBlocks.OXYGEN_SEALER,
                        GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR,
                        GCBlocks.OXYGEN_DECOMPRESSOR,
                        GCBlocks.OXYGEN_COMPRESSOR,
                        GCBlocks.OXYGEN_STORAGE_MODULE,
                        GCBlocks.FUEL_LOADER
                );

        this.getOrCreateTagBuilder(BlockTags.CLIMBABLE)
                .add(GCBlocks.TIN_LADDER);

        var stairs = new Block[] {
                GCBlocks.TIN_DECORATION_STAIRS,
                GCBlocks.COPPER_DECORATION_STAIRS,
                GCBlocks.BRONZE_DECORATION_STAIRS,
                GCBlocks.STEEL_DECORATION_STAIRS,
                GCBlocks.TITANIUM_DECORATION_STAIRS,
                GCBlocks.IRON_DECORATION_STAIRS,
                GCBlocks.ALUMINUM_DECORATION_STAIRS,
                GCBlocks.DARK_DECORATION_STAIRS,
                GCBlocks.METEORIC_IRON_DECORATION_STAIRS,
                GCBlocks.DETAILED_TIN_DECORATION_STAIRS,
                GCBlocks.DETAILED_COPPER_DECORATION_STAIRS,
                GCBlocks.DETAILED_BRONZE_DECORATION_STAIRS,
                GCBlocks.DETAILED_STEEL_DECORATION_STAIRS,
                GCBlocks.DETAILED_TITANIUM_DECORATION_STAIRS,
                GCBlocks.DETAILED_IRON_DECORATION_STAIRS,
                GCBlocks.DETAILED_ALUMINUM_DECORATION_STAIRS,
                GCBlocks.DETAILED_METEORIC_IRON_DECORATION_STAIRS,
                GCBlocks.DETAILED_DARK_DECORATION_STAIRS,
                GCBlocks.MOON_ROCK_STAIRS,
                GCBlocks.LUNASLATE_STAIRS,
                GCBlocks.COBBLED_MOON_ROCK_STAIRS,
                GCBlocks.COBBLED_LUNASLATE_STAIRS,
                GCBlocks.MOON_BASALT_STAIRS,
                GCBlocks.MOON_BASALT_BRICK_STAIRS,
                GCBlocks.CRACKED_MOON_BASALT_BRICK_STAIRS,
                GCBlocks.MARS_STONE_STAIRS,
                GCBlocks.MARS_COBBLESTONE_STAIRS
        };

        var wall = new Block[] {
                GCBlocks.TIN_DECORATION_WALL,
                GCBlocks.COPPER_DECORATION_WALL,
                GCBlocks.BRONZE_DECORATION_WALL,
                GCBlocks.STEEL_DECORATION_WALL,
                GCBlocks.TITANIUM_DECORATION_WALL,
                GCBlocks.IRON_DECORATION_WALL,
                GCBlocks.ALUMINUM_DECORATION_WALL,
                GCBlocks.DARK_DECORATION_WALL,
                GCBlocks.METEORIC_IRON_DECORATION_WALL,
                GCBlocks.DETAILED_TIN_DECORATION_WALL,
                GCBlocks.DETAILED_COPPER_DECORATION_WALL,
                GCBlocks.DETAILED_BRONZE_DECORATION_WALL,
                GCBlocks.DETAILED_STEEL_DECORATION_WALL,
                GCBlocks.DETAILED_TITANIUM_DECORATION_WALL,
                GCBlocks.DETAILED_IRON_DECORATION_WALL,
                GCBlocks.DETAILED_ALUMINUM_DECORATION_WALL,
                GCBlocks.DETAILED_METEORIC_IRON_DECORATION_WALL,
                GCBlocks.DETAILED_DARK_DECORATION_WALL,
                GCBlocks.MOON_ROCK_WALL,
                GCBlocks.LUNASLATE_WALL,
                GCBlocks.COBBLED_MOON_ROCK_WALL,
                GCBlocks.COBBLED_LUNASLATE_WALL,
                GCBlocks.MOON_BASALT_WALL,
                GCBlocks.MOON_BASALT_BRICK_WALL,
                GCBlocks.CRACKED_MOON_BASALT_BRICK_WALL,
                GCBlocks.MARS_STONE_WALL,
                GCBlocks.MARS_COBBLESTONE_WALL
        };

        var slab = new Block[] {
                GCBlocks.TIN_DECORATION_SLAB,
                GCBlocks.COPPER_DECORATION_SLAB,
                GCBlocks.BRONZE_DECORATION_SLAB,
                GCBlocks.STEEL_DECORATION_SLAB,
                GCBlocks.TITANIUM_DECORATION_SLAB,
                GCBlocks.IRON_DECORATION_SLAB,
                GCBlocks.ALUMINUM_DECORATION_SLAB,
                GCBlocks.DARK_DECORATION_SLAB,
                GCBlocks.METEORIC_IRON_DECORATION_SLAB,
                GCBlocks.DETAILED_TIN_DECORATION_SLAB,
                GCBlocks.DETAILED_COPPER_DECORATION_SLAB,
                GCBlocks.DETAILED_BRONZE_DECORATION_SLAB,
                GCBlocks.DETAILED_STEEL_DECORATION_SLAB,
                GCBlocks.DETAILED_TITANIUM_DECORATION_SLAB,
                GCBlocks.DETAILED_IRON_DECORATION_SLAB,
                GCBlocks.DETAILED_ALUMINUM_DECORATION_SLAB,
                GCBlocks.DETAILED_METEORIC_IRON_DECORATION_SLAB,
                GCBlocks.DETAILED_DARK_DECORATION_SLAB,
                GCBlocks.MOON_ROCK_SLAB,
                GCBlocks.LUNASLATE_SLAB,
                GCBlocks.COBBLED_MOON_ROCK_SLAB,
                GCBlocks.COBBLED_LUNASLATE_SLAB,
                GCBlocks.MOON_BASALT_SLAB,
                GCBlocks.MOON_BASALT_BRICK_SLAB,
                GCBlocks.CRACKED_MOON_BASALT_BRICK_SLAB,
                GCBlocks.MARS_STONE_SLAB,
                GCBlocks.MARS_COBBLESTONE_SLAB
        };

        this.getOrCreateTagBuilder(BlockTags.SLABS)
                .add(slab);

        this.getOrCreateTagBuilder(BlockTags.STAIRS)
                .add(stairs);

        this.getOrCreateTagBuilder(BlockTags.WALLS)
                .add(wall);

        // ORE MINING TAGS
        var ores = new Block[] {
                GCBlocks.SILICON_ORE, GCBlocks.DEEPSLATE_SILICON_ORE,
                GCBlocks.MOON_COPPER_ORE, GCBlocks.LUNASLATE_COPPER_ORE,
                GCBlocks.TIN_ORE, GCBlocks.DEEPSLATE_TIN_ORE, GCBlocks.MOON_TIN_ORE, GCBlocks.LUNASLATE_TIN_ORE,
                GCBlocks.ALUMINUM_ORE, GCBlocks.DEEPSLATE_ALUMINUM_ORE,
                GCBlocks.DESH_ORE,
                GCBlocks.ILMENITE_ORE,
                GCBlocks.GALENA_ORE
        };

        this.getOrCreateTagBuilder(ConventionalBlockTags.ORES).add(ores);

        this.getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .forceAddTag(GCTags.MACHINES)
                .add(ores)
                .add(slab)
                .add(stairs)
                .add(
                        GCBlocks.FALLEN_METEOR,
                        GCBlocks.TIN_LADDER,
                        GCBlocks.ROCKET_LAUNCH_PAD,
                        GCBlocks.GLOWSTONE_LANTERN,
                        GCBlocks.UNLIT_LANTERN,
                        GCBlocks.GRATING,
                        GCBlocks.WALKWAY,
                        GCBlocks.WIRE_WALKWAY,
                        GCBlocks.PIPE_WALKWAY,
                        GCBlocks.CRYOGENIC_CHAMBER,
                        GCBlocks.CRYOGENIC_CHAMBER_PART,
                        GCBlocks.SOLAR_PANEL_PART,
                        GCBlocks.AIR_LOCK_FRAME,
                        GCBlocks.AIR_LOCK_CONTROLLER,
                        GCBlocks.SEALABLE_ALUMINUM_WIRE,
                        GCBlocks.HEAVY_SEALABLE_ALUMINUM_WIRE,
                        GCBlocks.GLASS_FLUID_PIPE,

                        GCBlocks.SILICON_BLOCK,
                        GCBlocks.METEORIC_IRON_BLOCK,
                        GCBlocks.DESH_BLOCK,
                        GCBlocks.TITANIUM_BLOCK,
                        GCBlocks.LEAD_BLOCK,
                        GCBlocks.LUNAR_SAPPHIRE_BLOCK,

                        GCBlocks.MOON_TURF,
                        GCBlocks.MOON_DIRT,
                        GCBlocks.MOON_DIRT_PATH,
                        GCBlocks.MOON_SURFACE_ROCK,
                        GCBlocks.MARS_SURFACE_ROCK,
                        GCBlocks.MARS_SUB_SURFACE_ROCK,
                        GCBlocks.SOFT_VENUS_ROCK,
                        GCBlocks.HARD_VENUS_ROCK,
                        GCBlocks.SCORCHED_VENUS_ROCK,
                        GCBlocks.VOLCANIC_ROCK,
                        GCBlocks.PUMICE,
                        GCBlocks.VAPOR_SPOUT,
                        GCBlocks.ASTEROID_ROCK,
                        GCBlocks.ASTEROID_ROCK_1,
                        GCBlocks.ASTEROID_ROCK_2,

                        GCBlocks.TIN_DECORATION,
                        GCBlocks.COPPER_DECORATION,
                        GCBlocks.BRONZE_DECORATION,
                        GCBlocks.STEEL_DECORATION,
                        GCBlocks.TITANIUM_DECORATION,
                        GCBlocks.IRON_DECORATION,
                        GCBlocks.ALUMINUM_DECORATION,
                        GCBlocks.DARK_DECORATION,
                        GCBlocks.METEORIC_IRON_DECORATION,
                        GCBlocks.DETAILED_TIN_DECORATION,
                        GCBlocks.DETAILED_COPPER_DECORATION,
                        GCBlocks.DETAILED_BRONZE_DECORATION,
                        GCBlocks.DETAILED_STEEL_DECORATION,
                        GCBlocks.DETAILED_TITANIUM_DECORATION,
                        GCBlocks.DETAILED_IRON_DECORATION,
                        GCBlocks.DETAILED_ALUMINUM_DECORATION,
                        GCBlocks.DETAILED_METEORIC_IRON_DECORATION,
                        GCBlocks.DETAILED_DARK_DECORATION,
                        GCBlocks.MOON_ROCK,
                        GCBlocks.LUNASLATE,
                        GCBlocks.COBBLED_MOON_ROCK,
                        GCBlocks.COBBLED_LUNASLATE,
                        GCBlocks.MOON_BASALT,
                        GCBlocks.MOON_BASALT_BRICK,
                        GCBlocks.CRACKED_MOON_BASALT_BRICK,
                        GCBlocks.MARS_STONE,
                        GCBlocks.MARS_COBBLESTONE
                );

        this.getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(
                        GCBlocks.MOON_TURF,
                        GCBlocks.MOON_DIRT,
                        GCBlocks.MOON_DIRT_PATH
                );

        this.getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(
                        GCBlocks.GRATING,
                        GCBlocks.WALKWAY,
                        GCBlocks.WIRE_WALKWAY,
                        GCBlocks.PIPE_WALKWAY,
                        GCBlocks.MOON_COPPER_ORE,
                        GCBlocks.LUNASLATE_COPPER_ORE,
                        GCBlocks.TIN_ORE,
                        GCBlocks.DEEPSLATE_TIN_ORE,
                        GCBlocks.MOON_TIN_ORE,
                        GCBlocks.LUNASLATE_TIN_ORE,
                        GCBlocks.ALUMINUM_ORE,
                        GCBlocks.DEEPSLATE_ALUMINUM_ORE,
                        GCBlocks.GALENA_ORE
                );

        this.getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(
                        GCBlocks.SILICON_ORE,
                        GCBlocks.DEEPSLATE_SILICON_ORE,
                        GCBlocks.FALLEN_METEOR
                );

        this.getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(
                        GCBlocks.DESH_ORE,
                        GCBlocks.ILMENITE_ORE
                );

        this.getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE)
                .add(
                        GCBlocks.AIR_LOCK_SEAL);

        this.getOrCreateTagBuilder(BlockTags.WITHER_IMMUNE)
                .add(
                        GCBlocks.AIR_LOCK_SEAL);

        this.getOrCreateTagBuilder(BlockTags.WALL_POST_OVERRIDE)
                .add(
                        GCBlocks.GLOWSTONE_TORCH,
                        GCBlocks.UNLIT_TORCH);

        // Cheese Candle Tags
        this.getOrCreateTagBuilder(BlockTags.CANDLE_CAKES)
                .add(
                        GCBlocks.CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.WHITE_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.ORANGE_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.MAGENTA_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.LIGHT_BLUE_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.YELLOW_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.LIME_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.PINK_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.GRAY_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.LIGHT_GRAY_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.CYAN_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.PURPLE_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.BLUE_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.BROWN_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.GREEN_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.RED_CANDLE_MOON_CHEESE_BLOCK,
                        GCBlocks.BLACK_CANDLE_MOON_CHEESE_BLOCK
                );

        var replaceableTagAppender = this.getOrCreateTagBuilder(BlockTags.REPLACEABLE);
        provider.lookupOrThrow(Registries.BLOCK)
                .filterElements(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(Constant.MOD_ID) && block.defaultBlockState().canBeReplaced())
                .listElementIds()
                .forEach(replaceableTagAppender::add);
    }
}