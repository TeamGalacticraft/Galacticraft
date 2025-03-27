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

package dev.galacticraft.mod.data.tag;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlockRegistry;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.tag.GCBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class GCBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public GCBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(GCBlockTags.INFINIBURN_MOON)
                .add(GCBlocks.MOON_BASALT_BRICK);
        this.tag(GCBlockTags.BASE_STONE_MOON)
                .add(GCBlocks.MOON_ROCK);
        this.tag(GCBlockTags.MOON_CARVER_REPLACEABLES)
                .add(GCBlocks.MOON_ROCK)
                .add(GCBlocks.MOON_SURFACE_ROCK)
                .add(GCBlocks.MOON_BASALT)
                .add(GCBlocks.MOON_DIRT)
                .add(GCBlocks.MOON_TURF);
        this.tag(GCBlockTags.MOON_CRATER_CARVER_REPLACEABLES)
                .add(GCBlocks.MOON_ROCK)
                .add(GCBlocks.MOON_SURFACE_ROCK)
                .add(GCBlocks.MOON_BASALT)
                .add(GCBlocks.MOON_DIRT)
                .add(GCBlocks.MOON_TURF);
        this.tag(GCBlockTags.MOON_STONE_ORE_REPLACEABLES)
                .add(GCBlocks.MOON_ROCK);
                // .add(GCBlocks.MOON_BASALT);
        this.tag(GCBlockTags.LUNASLATE_ORE_REPLACEABLES)
                .add(GCBlocks.LUNASLATE);
        this.tag(GCBlockTags.MACHINES)
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

        this.tag(BlockTags.CLIMBABLE)
                .add(
                        GCBlocks.TIN_LADDER,
                        GCBlocks.CAVERNOUS_VINES,
                        GCBlocks.CAVERNOUS_VINES_PLANT
                );

        var stairs = new Block[] {
                GCBlocks.MOON_ROCK_STAIRS,
                GCBlocks.MOON_ROCK_BRICK_STAIRS,
                GCBlocks.CRACKED_MOON_ROCK_BRICK_STAIRS,
                GCBlocks.POLISHED_MOON_ROCK_STAIRS,
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
                GCBlocks.MOON_ROCK_WALL,
                GCBlocks.MOON_ROCK_BRICK_WALL,
                GCBlocks.CRACKED_MOON_ROCK_BRICK_WALL,
                GCBlocks.POLISHED_MOON_ROCK_WALL,
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
                GCBlocks.MOON_ROCK_SLAB,
                GCBlocks.MOON_ROCK_BRICK_SLAB,
                GCBlocks.CRACKED_MOON_ROCK_BRICK_SLAB,
                GCBlocks.POLISHED_MOON_ROCK_SLAB,
                GCBlocks.LUNASLATE_SLAB,
                GCBlocks.COBBLED_MOON_ROCK_SLAB,
                GCBlocks.COBBLED_LUNASLATE_SLAB,
                GCBlocks.MOON_BASALT_SLAB,
                GCBlocks.MOON_BASALT_BRICK_SLAB,
                GCBlocks.CRACKED_MOON_BASALT_BRICK_SLAB,
                GCBlocks.MARS_STONE_SLAB,
                GCBlocks.MARS_COBBLESTONE_SLAB
        };

        List<GCBlockRegistry.DecorationSet> decorations = GCBlocks.BLOCKS.getDecorations();

        Map<String, TagKey<Block>> decoTags = new HashMap<>();
 
        decoTags.put("aluminum_decoration", GCBlockTags.ALUMINUM_DECORATION_BLOCKS);
        decoTags.put("bronze_decoration", GCBlockTags.BRONZE_DECORATION_BLOCKS);
        decoTags.put("copper_decoration", GCBlockTags.COPPER_DECORATION_BLOCKS);
        decoTags.put("iron_decoration", GCBlockTags.IRON_DECORATION_BLOCKS);
        decoTags.put("meteoric_iron_decoration", GCBlockTags.METEORIC_IRON_DECORATION_BLOCKS);
        decoTags.put("steel_decoration", GCBlockTags.STEEL_DECORATION_BLOCKS);
        decoTags.put("tin_decoration", GCBlockTags.TIN_DECORATION_BLOCKS);
        decoTags.put("titanium_decoration", GCBlockTags.TITANIUM_DECORATION_BLOCKS);
        decoTags.put("dark_decoration", GCBlockTags.DARK_DECORATION_BLOCKS);

        this.tag(BlockTags.SLABS).addTag(GCBlockTags.SLABS);
        this.tag(BlockTags.STAIRS).addTag(GCBlockTags.STAIRS);
        this.tag(BlockTags.WALLS).addTag(GCBlockTags.WALLS);

        var slabBuilder = this.tag(GCBlockTags.SLABS).add(slab);
        var stairsBuilder = this.tag(GCBlockTags.STAIRS).add(stairs);
        var wallBuilder = this.tag(GCBlockTags.WALLS).add(wall);

        String decoSetId;
        for (GCBlockRegistry.DecorationSet decorationSet : decorations) {
            slabBuilder.add(decorationSet.slab(), decorationSet.detailedSlab());
            stairsBuilder.add(decorationSet.stairs(), decorationSet.detailedStairs());
            wallBuilder.add(decorationSet.wall(), decorationSet.detailedWall());

            decoSetId = decorationSet.block().getDescriptionId();
            decoSetId = decoSetId.substring(decoSetId.lastIndexOf(".") + 1);
            this.tag(decoTags.get(decoSetId))
                .add(decorationSet.block(), decorationSet.slab(), decorationSet.stairs(), decorationSet.wall())
                .add(decorationSet.detailedBlock(), decorationSet.detailedSlab(), decorationSet.detailedStairs(), decorationSet.detailedWall());
        }

        this.tag(GCBlockTags.DECORATION_BLOCKS)
                .addTag(GCBlockTags.ALUMINUM_DECORATION_BLOCKS)
                .addTag(GCBlockTags.BRONZE_DECORATION_BLOCKS)
                .addTag(GCBlockTags.COPPER_DECORATION_BLOCKS)
                .addTag(GCBlockTags.IRON_DECORATION_BLOCKS)
                .addTag(GCBlockTags.METEORIC_IRON_DECORATION_BLOCKS)
                .addTag(GCBlockTags.STEEL_DECORATION_BLOCKS)
                .addTag(GCBlockTags.TIN_DECORATION_BLOCKS)
                .addTag(GCBlockTags.TITANIUM_DECORATION_BLOCKS)
                .addTag(GCBlockTags.DARK_DECORATION_BLOCKS);

        // ORE MINING TAGS
        var ores = new Block[] {
                GCBlocks.MARS_IRON_ORE, GCBlocks.ASTEROID_IRON_ORE,
                GCBlocks.MOON_COPPER_ORE, GCBlocks.LUNASLATE_COPPER_ORE, GCBlocks.MARS_COPPER_ORE, GCBlocks.VENUS_COPPER_ORE,
                GCBlocks.SILICON_ORE, GCBlocks.DEEPSLATE_SILICON_ORE,
                GCBlocks.TIN_ORE, GCBlocks.DEEPSLATE_TIN_ORE, GCBlocks.MOON_TIN_ORE, GCBlocks.LUNASLATE_TIN_ORE, GCBlocks.MARS_TIN_ORE, GCBlocks.VENUS_TIN_ORE,
                GCBlocks.ALUMINUM_ORE, GCBlocks.DEEPSLATE_ALUMINUM_ORE, GCBlocks.ASTEROID_ALUMINUM_ORE, GCBlocks.VENUS_ALUMINUM_ORE,
                GCBlocks.FALLEN_METEOR,
                GCBlocks.MOON_CHEESE_ORE,
                GCBlocks.LUNAR_SAPPHIRE_ORE,
                GCBlocks.OLIVINE_BASALT, GCBlocks.RICH_OLIVINE_BASALT,
                GCBlocks.DESH_ORE,
                GCBlocks.ILMENITE_ORE,
                GCBlocks.GALENA_ORE,
                GCBlocks.SOLAR_ORE
        };

        this.tag(ConventionalBlockTags.ORES).add(ores);

        var clusters = new Block[] {
                GCBlocks.OLIVINE_CLUSTER
        };

        this.tag(ConventionalBlockTags.CLUSTERS).add(clusters);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .forceAddTag(GCBlockTags.MACHINES)
                .add(ores)
                .add(clusters)
                .add(slab)
                .add(stairs)
                .add(
                        GCBlocks.FALLEN_METEOR,
                        GCBlocks.TIN_LADDER,
                        GCBlocks.FUELING_PAD,
                        GCBlocks.ROCKET_LAUNCH_PAD,
                        GCBlocks.GLOWSTONE_LANTERN,
                        GCBlocks.UNLIT_LANTERN,
                        GCBlocks.IRON_GRATING,
                        GCBlocks.WALKWAY,
                        GCBlocks.WIRE_WALKWAY,
                        GCBlocks.FLUID_PIPE_WALKWAY,
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
                        GCBlocks.ALUMINUM_BLOCK,
                        GCBlocks.TIN_BLOCK,
                        GCBlocks.TITANIUM_BLOCK,
                        GCBlocks.LEAD_BLOCK,
                        GCBlocks.LUNAR_SAPPHIRE_BLOCK,
                        GCBlocks.OLIVINE_BLOCK,
                        GCBlocks.RAW_METEORIC_IRON_BLOCK,
                        GCBlocks.RAW_DESH_BLOCK,
                        GCBlocks.RAW_ALUMINUM_BLOCK,
                        GCBlocks.RAW_TIN_BLOCK,
                        GCBlocks.RAW_TITANIUM_BLOCK,
                        GCBlocks.RAW_LEAD_BLOCK,

                        GCBlocks.MOON_TURF,
                        GCBlocks.MOON_DIRT,
                        GCBlocks.MOON_DIRT_PATH,
                        GCBlocks.MOON_SURFACE_ROCK,
                        GCBlocks.MOON_DUNGEON_BRICK,
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

                        GCBlocks.TIN_DECORATION.block(),
                        GCBlocks.COPPER_DECORATION.block(),
                        GCBlocks.BRONZE_DECORATION.block(),
                        GCBlocks.STEEL_DECORATION.block(),
                        GCBlocks.TITANIUM_DECORATION.block(),
                        GCBlocks.IRON_DECORATION.block(),
                        GCBlocks.ALUMINUM_DECORATION.block(),
                        GCBlocks.DARK_DECORATION.block(),
                        GCBlocks.METEORIC_IRON_DECORATION.block(),
                        GCBlocks.TIN_DECORATION.detailedBlock(),
                        GCBlocks.COPPER_DECORATION.detailedBlock(),
                        GCBlocks.BRONZE_DECORATION.detailedBlock(),
                        GCBlocks.STEEL_DECORATION.detailedBlock(),
                        GCBlocks.TITANIUM_DECORATION.detailedBlock(),
                        GCBlocks.IRON_DECORATION.detailedBlock(),
                        GCBlocks.ALUMINUM_DECORATION.detailedBlock(),
                        GCBlocks.METEORIC_IRON_DECORATION.detailedBlock(),
                        GCBlocks.DARK_DECORATION.detailedBlock(),
                        GCBlocks.MOON_ROCK,
                        GCBlocks.MOON_ROCK_BRICK,
                        GCBlocks.CRACKED_MOON_ROCK_BRICK,
                        GCBlocks.POLISHED_MOON_ROCK,
                        GCBlocks.CHISELED_MOON_ROCK_BRICK,
                        GCBlocks.MOON_ROCK_PILLAR,
                        GCBlocks.LUNASLATE,
                        GCBlocks.COBBLED_MOON_ROCK,
                        GCBlocks.COBBLED_LUNASLATE,
                        GCBlocks.MOON_BASALT,
                        GCBlocks.MOON_BASALT_BRICK,
                        GCBlocks.CRACKED_MOON_BASALT_BRICK,
                        GCBlocks.MARS_STONE,
                        GCBlocks.MARS_COBBLESTONE
                );

        this.tag(BlockTags.MINEABLE_WITH_AXE).add(GCBlocks.PARACHEST);

        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(
                        GCBlocks.MOON_TURF,
                        GCBlocks.MOON_DIRT,
                        GCBlocks.MOON_DIRT_PATH
                );


        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(
                        GCBlocks.IRON_GRATING,
                        GCBlocks.WALKWAY,
                        GCBlocks.WIRE_WALKWAY,
                        GCBlocks.FLUID_PIPE_WALKWAY,
                        GCBlocks.MARS_IRON_ORE,
                        GCBlocks.ASTEROID_IRON_ORE,
                        GCBlocks.MARS_IRON_ORE,
                        GCBlocks.ASTEROID_IRON_ORE,
                        GCBlocks.MOON_COPPER_ORE,
                        GCBlocks.LUNASLATE_COPPER_ORE,
                        GCBlocks.MARS_COPPER_ORE,
                        GCBlocks.VENUS_COPPER_ORE,
                        GCBlocks.MARS_COPPER_ORE,
                        GCBlocks.VENUS_COPPER_ORE,
                        GCBlocks.TIN_ORE,
                        GCBlocks.DEEPSLATE_TIN_ORE,
                        GCBlocks.MOON_TIN_ORE,
                        GCBlocks.LUNASLATE_TIN_ORE,
                        GCBlocks.MARS_TIN_ORE,
                        GCBlocks.VENUS_TIN_ORE,
                        GCBlocks.MARS_TIN_ORE,
                        GCBlocks.VENUS_TIN_ORE,
                        GCBlocks.ALUMINUM_ORE,
                        GCBlocks.DEEPSLATE_ALUMINUM_ORE,
                        GCBlocks.ASTEROID_ALUMINUM_ORE,
                        GCBlocks.VENUS_ALUMINUM_ORE,
                        GCBlocks.MOON_CHEESE_ORE,
                        GCBlocks.ASTEROID_ALUMINUM_ORE,
                        GCBlocks.VENUS_ALUMINUM_ORE,
                        GCBlocks.MOON_CHEESE_ORE,
                        GCBlocks.GALENA_ORE,
                        GCBlocks.SOLAR_ORE,
                        GCBlocks.SOLAR_ORE,
                        GCBlocks.RAW_TIN_BLOCK,
                        GCBlocks.TIN_BLOCK,
                        GCBlocks.RAW_ALUMINUM_BLOCK,
                        GCBlocks.ALUMINUM_BLOCK,
                        GCBlocks.RAW_LEAD_BLOCK,
                        GCBlocks.LEAD_BLOCK,
                        GCBlocks.RAW_METEORIC_IRON_BLOCK,
                        GCBlocks.METEORIC_IRON_BLOCK
                );

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(
                        GCBlocks.SILICON_ORE,
                        GCBlocks.DEEPSLATE_SILICON_ORE,
                        GCBlocks.FALLEN_METEOR,
                        GCBlocks.LUNAR_SAPPHIRE_ORE,
                        GCBlocks.LUNAR_SAPPHIRE_ORE,
                        GCBlocks.SILICON_BLOCK
                );

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(
                        GCBlocks.DESH_ORE,
                        GCBlocks.ILMENITE_ORE,
                        GCBlocks.RAW_DESH_BLOCK,
                        GCBlocks.DESH_BLOCK,
                        GCBlocks.RAW_TITANIUM_BLOCK,
                        GCBlocks.TITANIUM_BLOCK
                );

        this.tag(BlockTags.DRAGON_IMMUNE)
                .add(
                        GCBlocks.AIR_LOCK_SEAL);

        this.tag(BlockTags.WITHER_IMMUNE)
                .add(
                        GCBlocks.AIR_LOCK_SEAL);

        this.tag(BlockTags.WALL_POST_OVERRIDE)
                .add(
                        GCBlocks.GLOWSTONE_TORCH,
                        GCBlocks.UNLIT_TORCH);

        // Cheese Candle Tags
        this.tag(BlockTags.CANDLE_CAKES)
                .add(
                        GCBlocks.CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.WHITE_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.ORANGE_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.MAGENTA_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.LIGHT_BLUE_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.YELLOW_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.LIME_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.PINK_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.GRAY_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.LIGHT_GRAY_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.CYAN_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.PURPLE_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.BLUE_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.BROWN_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.GREEN_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.RED_CANDLE_MOON_CHEESE_WHEEL,
                        GCBlocks.BLACK_CANDLE_MOON_CHEESE_WHEEL
                );

        this.tag(ConventionalBlockTags.PLAYER_WORKSTATIONS_FURNACES)
                .add(GCBlocks.ELECTRIC_FURNACE)
                .add(GCBlocks.ELECTRIC_ARC_FURNACE);

        var replaceableTagAppender = this.tag(BlockTags.REPLACEABLE);
        provider.lookupOrThrow(Registries.BLOCK)
                .filterElements(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(Constant.MOD_ID) && block.defaultBlockState().canBeReplaced())
                .listElementIds()
                .forEach(replaceableTagAppender::add);

        this.tag(GCBlockTags.FOOTPRINTS)
                .add(GCBlocks.MOON_TURF);
    }

    protected FabricTagProvider<Block>.FabricTagBuilder tag(TagKey<Block> tag) {
        return this.getOrCreateTagBuilder(tag);
    }
}