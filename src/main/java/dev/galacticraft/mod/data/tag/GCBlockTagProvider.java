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
import dev.galacticraft.mod.content.block.GCBlock;
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
                .add(GCBlocks.MOON_TURF)
                .add(GCBlocks.LUNASLATE);
        this.tag(GCBlockTags.MOON_CRATER_CARVER_REPLACEABLES)
                .add(GCBlocks.MOON_ROCK)
                .add(GCBlocks.MOON_SURFACE_ROCK)
                .add(GCBlocks.MOON_BASALT)
                .add(GCBlocks.MOON_DIRT)
                .add(GCBlocks.MOON_TURF)
                .add(GCBlocks.LUNASLATE);

        this.tag(GCBlockTags.MOON_STONE_ORE_REPLACEABLES)
                .add(GCBlocks.MOON_ROCK);
        this.tag(GCBlockTags.LUNASLATE_ORE_REPLACEABLES)
                .add(GCBlocks.LUNASLATE);
        this.tag(GCBlockTags.MOON_BASALT_ORE_REPLACEABLES)
                .add(GCBlocks.MOON_BASALT);
        this.tag(GCBlockTags.MARS_STONE_ORE_REPLACEABLES)
                .add(GCBlocks.MARS_STONE);
        this.tag(GCBlockTags.ASTEROID_ROCK_ORE_REPLACEABLES)
                .add(GCBlocks.ASTEROID_ROCK)
                .add(GCBlocks.ASTEROID_ROCK_1)
                .add(GCBlocks.ASTEROID_ROCK_2);
        this.tag(GCBlockTags.SOFT_VENUS_ROCK_ORE_REPLACEABLES)
                .add(GCBlocks.SOFT_VENUS_ROCK);
        this.tag(GCBlockTags.HARD_VENUS_ROCK_ORE_REPLACEABLES)
                .add(GCBlocks.HARD_VENUS_ROCK);

        this.tag(GCBlockTags.ORE_BEARING_GROUND_MOON_STONE)
                .add(GCBlocks.MOON_ROCK);
        this.tag(GCBlockTags.ORE_BEARING_GROUND_LUNASLATE)
                .add(GCBlocks.LUNASLATE);
        this.tag(GCBlockTags.ORE_BEARING_GROUND_MOON_BASALT)
                .add(GCBlocks.MOON_BASALT);
        this.tag(GCBlockTags.ORE_BEARING_GROUND_MARS_STONE)
                .add(GCBlocks.MARS_STONE);
        this.tag(GCBlockTags.ORE_BEARING_GROUND_ASTEROID_ROCK)
                .add(GCBlocks.ASTEROID_ROCK);
        this.tag(GCBlockTags.ORE_BEARING_GROUND_SOFT_VENUS_ROCK)
                .add(GCBlocks.SOFT_VENUS_ROCK);
        this.tag(GCBlockTags.ORE_BEARING_GROUND_HARD_VENUS_ROCK)
                .add(GCBlocks.HARD_VENUS_ROCK);
        this.tag(GCBlockTags.ORE_BEARING_GROUND_VENUS_ROCK)
                .addTag(GCBlockTags.ORE_BEARING_GROUND_SOFT_VENUS_ROCK)
                .addTag(GCBlockTags.ORE_BEARING_GROUND_HARD_VENUS_ROCK);

        this.tag(ConventionalBlockTags.ORES_IN_GROUND_STONE)
                .add(GCBlocks.SILICON_ORE)
                .add(GCBlocks.TIN_ORE)
                .add(GCBlocks.ALUMINUM_ORE);
        this.tag(ConventionalBlockTags.ORES_IN_GROUND_DEEPSLATE)
                .add(GCBlocks.DEEPSLATE_SILICON_ORE)
                .add(GCBlocks.DEEPSLATE_TIN_ORE)
                .add(GCBlocks.DEEPSLATE_ALUMINUM_ORE);
        this.tag(GCBlockTags.ORES_IN_GROUND_MOON_STONE)
                .add(GCBlocks.MOON_COPPER_ORE)
                .add(GCBlocks.MOON_TIN_ORE)
                .add(GCBlocks.MOON_CHEESE_ORE)
                .add(GCBlocks.LUNAR_SAPPHIRE_ORE);
        this.tag(GCBlockTags.ORES_IN_GROUND_LUNASLATE)
                .add(GCBlocks.LUNASLATE_COPPER_ORE)
                .add(GCBlocks.LUNASLATE_TIN_ORE);
        this.tag(GCBlockTags.ORES_IN_GROUND_MOON_BASALT)
                .add(GCBlocks.OLIVINE_BASALT)
                .add(GCBlocks.RICH_OLIVINE_BASALT);
        this.tag(GCBlockTags.ORES_IN_GROUND_MARS_STONE)
                .add(GCBlocks.MARS_IRON_ORE)
                .add(GCBlocks.MARS_COPPER_ORE)
                .add(GCBlocks.MARS_TIN_ORE)
                .add(GCBlocks.DESH_ORE);
        this.tag(GCBlockTags.ORES_IN_GROUND_ASTEROID_ROCK)
                .add(GCBlocks.ASTEROID_IRON_ORE)
                .add(GCBlocks.ASTEROID_SILICON_ORE)
                .add(GCBlocks.ASTEROID_ALUMINUM_ORE)
                .add(GCBlocks.ILMENITE_ORE);
        this.tag(GCBlockTags.ORES_IN_GROUND_SOFT_VENUS_ROCK)
                .add(GCBlocks.VENUS_COPPER_ORE)
                .add(GCBlocks.VENUS_TIN_ORE)
                .add(GCBlocks.VENUS_ALUMINUM_ORE)
                .add(GCBlocks.SOLAR_ORE);
        this.tag(GCBlockTags.ORES_IN_GROUND_HARD_VENUS_ROCK)
                .add(GCBlocks.GALENA_ORE);
        this.tag(GCBlockTags.ORES_IN_GROUND_VENUS_ROCK)
                .addTag(GCBlockTags.ORES_IN_GROUND_SOFT_VENUS_ROCK)
                .addTag(GCBlockTags.ORES_IN_GROUND_HARD_VENUS_ROCK);

        // Ores that drop more than one item on average without fortune
        this.tag(ConventionalBlockTags.ORE_RATES_DENSE)
                .add(GCBlocks.MOON_COPPER_ORE)
                .add(GCBlocks.LUNASLATE_COPPER_ORE)
                .add(GCBlocks.MARS_COPPER_ORE)
                .add(GCBlocks.VENUS_COPPER_ORE)
                .add(GCBlocks.SILICON_ORE)
                .add(GCBlocks.DEEPSLATE_SILICON_ORE)
                .add(GCBlocks.ASTEROID_SILICON_ORE)
                .add(GCBlocks.TIN_ORE)
                .add(GCBlocks.DEEPSLATE_TIN_ORE)
                .add(GCBlocks.MOON_TIN_ORE)
                .add(GCBlocks.LUNASLATE_TIN_ORE)
                .add(GCBlocks.MARS_TIN_ORE)
                .add(GCBlocks.VENUS_TIN_ORE)
                .add(GCBlocks.MOON_CHEESE_ORE)
                .add(GCBlocks.OLIVINE_BASALT);

        // Ores that drop one item on average without fortune
        this.tag(ConventionalBlockTags.ORE_RATES_SINGULAR)
                .add(GCBlocks.MARS_IRON_ORE)
                .add(GCBlocks.ASTEROID_IRON_ORE)
                .add(GCBlocks.ALUMINUM_ORE)
                .add(GCBlocks.DEEPSLATE_ALUMINUM_ORE)
                .add(GCBlocks.ASTEROID_ALUMINUM_ORE)
                .add(GCBlocks.VENUS_ALUMINUM_ORE)
                .add(GCBlocks.LUNAR_SAPPHIRE_ORE)
                .add(GCBlocks.RICH_OLIVINE_BASALT)
                .add(GCBlocks.FALLEN_METEOR)
                .add(GCBlocks.DESH_ORE)
                .add(GCBlocks.ILMENITE_ORE)
                .add(GCBlocks.GALENA_ORE)
                .add(GCBlocks.SOLAR_ORE);

        // ORE TAGS
        this.tag(BlockTags.IRON_ORES)
                .add(GCBlocks.MARS_IRON_ORE)
                .add(GCBlocks.ASTEROID_IRON_ORE);
        this.tag(BlockTags.COPPER_ORES)
                .add(GCBlocks.MOON_COPPER_ORE)
                .add(GCBlocks.LUNASLATE_COPPER_ORE)
                .add(GCBlocks.MARS_COPPER_ORE)
                .add(GCBlocks.VENUS_COPPER_ORE);
        this.tag(GCBlockTags.SILICON_ORES)
                .add(GCBlocks.SILICON_ORE)
                .add(GCBlocks.DEEPSLATE_SILICON_ORE)
                .add(GCBlocks.ASTEROID_SILICON_ORE);
        this.tag(GCBlockTags.TIN_ORES)
                .add(GCBlocks.TIN_ORE)
                .add(GCBlocks.DEEPSLATE_TIN_ORE)
                .add(GCBlocks.MOON_TIN_ORE)
                .add(GCBlocks.LUNASLATE_TIN_ORE)
                .add(GCBlocks.MARS_TIN_ORE)
                .add(GCBlocks.VENUS_TIN_ORE);
        this.tag(GCBlockTags.ALUMINUM_ORES)
                .add(GCBlocks.ALUMINUM_ORE)
                .add(GCBlocks.DEEPSLATE_ALUMINUM_ORE)
                .add(GCBlocks.ASTEROID_ALUMINUM_ORE)
                .add(GCBlocks.VENUS_ALUMINUM_ORE);
        this.tag(GCBlockTags.CHEESE_ORES)
                .add(GCBlocks.MOON_CHEESE_ORE);
        this.tag(GCBlockTags.LUNAR_SAPPHIRE_ORES)
                .add(GCBlocks.LUNAR_SAPPHIRE_ORE);
        this.tag(GCBlockTags.OLIVINE_ORES)
                .add(GCBlocks.OLIVINE_BASALT)
                .add(GCBlocks.RICH_OLIVINE_BASALT);
        this.tag(GCBlockTags.METEORIC_IRON_ORES)
                .add(GCBlocks.FALLEN_METEOR);
        this.tag(GCBlockTags.DESH_ORES)
                .add(GCBlocks.DESH_ORE);
        this.tag(GCBlockTags.TITANIUM_ORES)
                .add(GCBlocks.ILMENITE_ORE);
        this.tag(GCBlockTags.LEAD_ORES)
                .add(GCBlocks.GALENA_ORE);
        this.tag(GCBlockTags.SOLAR_ORES)
                .add(GCBlocks.SOLAR_ORE);
        this.tag(ConventionalBlockTags.ORES)
                .addTag(GCBlockTags.SILICON_ORES)
                .addTag(GCBlockTags.TIN_ORES)
                .addTag(GCBlockTags.ALUMINUM_ORES)
                .addTag(GCBlockTags.CHEESE_ORES)
                .addTag(GCBlockTags.LUNAR_SAPPHIRE_ORES)
                .addTag(GCBlockTags.OLIVINE_ORES)
                .addTag(GCBlockTags.METEORIC_IRON_ORES)
                .addTag(GCBlockTags.DESH_ORES)
                .addTag(GCBlockTags.TITANIUM_ORES)
                .addTag(GCBlockTags.LEAD_ORES)
                .addTag(GCBlockTags.SOLAR_ORES);

        // STORAGE BLOCK TAGS
        this.tag(GCBlockTags.TIN_BLOCKS)
                .add(GCBlocks.TIN_BLOCK);
        this.tag(GCBlockTags.ALUMINUM_BLOCKS)
                .add(GCBlocks.ALUMINUM_BLOCK);
        this.tag(GCBlockTags.METEORIC_IRON_BLOCKS)
                .add(GCBlocks.METEORIC_IRON_BLOCK);
        this.tag(GCBlockTags.DESH_BLOCKS)
                .add(GCBlocks.DESH_BLOCK);
        this.tag(GCBlockTags.TITANIUM_BLOCKS)
                .add(GCBlocks.TITANIUM_BLOCK);
        this.tag(GCBlockTags.LEAD_BLOCKS)
                .add(GCBlocks.LEAD_BLOCK);

        this.tag(GCBlockTags.SILICON_BLOCKS)
                .add(GCBlocks.SILICON_BLOCK);
        this.tag(GCBlockTags.CHEESE_BLOCKS)
                .add(GCBlocks.MOON_CHEESE_BLOCK);
        this.tag(GCBlockTags.LUNAR_SAPPHIRE_BLOCKS)
                .add(GCBlocks.LUNAR_SAPPHIRE_BLOCK);
        this.tag(GCBlockTags.OLIVINE_BLOCKS)
                .add(GCBlocks.OLIVINE_BLOCK);

        this.tag(GCBlockTags.RAW_TIN_BLOCKS)
                .add(GCBlocks.RAW_TIN_BLOCK);
        this.tag(GCBlockTags.RAW_ALUMINUM_BLOCKS)
                .add(GCBlocks.RAW_ALUMINUM_BLOCK);
        this.tag(GCBlockTags.RAW_METEORIC_IRON_BLOCKS)
                .add(GCBlocks.RAW_METEORIC_IRON_BLOCK);
        this.tag(GCBlockTags.RAW_DESH_BLOCKS)
                .add(GCBlocks.RAW_DESH_BLOCK);
        this.tag(GCBlockTags.RAW_TITANIUM_BLOCKS)
                .add(GCBlocks.RAW_TITANIUM_BLOCK);
        this.tag(GCBlockTags.RAW_LEAD_BLOCKS)
                .add(GCBlocks.RAW_LEAD_BLOCK);
        this.tag(ConventionalBlockTags.STORAGE_BLOCKS)
                .addTag(GCBlockTags.TIN_BLOCKS)
                .addTag(GCBlockTags.ALUMINUM_BLOCKS)
                .addTag(GCBlockTags.METEORIC_IRON_BLOCKS)
                .addTag(GCBlockTags.DESH_BLOCKS)
                .addTag(GCBlockTags.TITANIUM_BLOCKS)
                .addTag(GCBlockTags.LEAD_BLOCKS)
                .addTag(GCBlockTags.SILICON_BLOCKS)
                .addTag(GCBlockTags.CHEESE_BLOCKS)
                .addTag(GCBlockTags.LUNAR_SAPPHIRE_BLOCKS)
                .addTag(GCBlockTags.OLIVINE_BLOCKS)
                .addTag(GCBlockTags.RAW_TIN_BLOCKS)
                .addTag(GCBlockTags.RAW_ALUMINUM_BLOCKS)
                .addTag(GCBlockTags.RAW_METEORIC_IRON_BLOCKS)
                .addTag(GCBlockTags.RAW_DESH_BLOCKS)
                .addTag(GCBlockTags.RAW_TITANIUM_BLOCKS)
                .addTag(GCBlockTags.RAW_LEAD_BLOCKS);

        this.tag(GCBlockTags.MACHINES)
                .add(GCBlocks.CIRCUIT_FABRICATOR)
                .add(GCBlocks.COMPRESSOR)
                .add(GCBlocks.ELECTRIC_COMPRESSOR)
                .add(GCBlocks.COAL_GENERATOR)
                .add(GCBlocks.BASIC_SOLAR_PANEL)
                .add(GCBlocks.ADVANCED_SOLAR_PANEL)
                .add(GCBlocks.ENERGY_STORAGE_MODULE)
                .add(GCBlocks.ELECTRIC_FURNACE)
                .add(GCBlocks.ELECTRIC_ARC_FURNACE)
                .add(GCBlocks.REFINERY)
                .add(GCBlocks.FUEL_LOADER)
                .add(GCBlocks.OXYGEN_COLLECTOR)
                .add(GCBlocks.OXYGEN_SEALER)
                .add(GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR)
                .add(GCBlocks.OXYGEN_DECOMPRESSOR)
                .add(GCBlocks.OXYGEN_COMPRESSOR)
                .add(GCBlocks.OXYGEN_STORAGE_MODULE)
                .add(GCBlocks.FOOD_CANNER)
                .add(GCBlocks.ROCKET_WORKBENCH);

        this.tag(BlockTags.CLIMBABLE)
                .add(GCBlocks.TIN_LADDER)
                .add(GCBlocks.CAVERNOUS_VINES)
                .add(GCBlocks.CAVERNOUS_VINES_PLANT);

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
            stairsBuilder.add(decorationSet.stairs(), decorationSet.detailedStairs());
            slabBuilder.add(decorationSet.slab(), decorationSet.detailedSlab());
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

        this.tag(ConventionalBlockTags.CLUSTERS)
                .add(GCBlocks.OLIVINE_CLUSTER);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .forceAddTag(GCBlockTags.MACHINES)
                .add(slab)
                .add(stairs)
                .add(
                        GCBlocks.MARS_IRON_ORE,
                        GCBlocks.ASTEROID_IRON_ORE,
                        GCBlocks.MOON_COPPER_ORE,
                        GCBlocks.LUNASLATE_COPPER_ORE,
                        GCBlocks.MARS_COPPER_ORE,
                        GCBlocks.VENUS_COPPER_ORE,
                        GCBlocks.SILICON_ORE,
                        GCBlocks.DEEPSLATE_SILICON_ORE,
                        GCBlocks.ASTEROID_SILICON_ORE,
                        GCBlocks.TIN_ORE,
                        GCBlocks.DEEPSLATE_TIN_ORE,
                        GCBlocks.MOON_TIN_ORE,
                        GCBlocks.LUNASLATE_TIN_ORE,
                        GCBlocks.MARS_TIN_ORE, GCBlocks.VENUS_TIN_ORE,
                        GCBlocks.ALUMINUM_ORE,
                        GCBlocks.DEEPSLATE_ALUMINUM_ORE,
                        GCBlocks.ASTEROID_ALUMINUM_ORE,
                        GCBlocks.VENUS_ALUMINUM_ORE,
                        GCBlocks.MOON_CHEESE_ORE,
                        GCBlocks.LUNAR_SAPPHIRE_ORE,
                        GCBlocks.OLIVINE_BASALT,
                        GCBlocks.RICH_OLIVINE_BASALT,
                        GCBlocks.FALLEN_METEOR,
                        GCBlocks.DESH_ORE,
                        GCBlocks.ILMENITE_ORE,
                        GCBlocks.GALENA_ORE,
                        GCBlocks.SOLAR_ORE,

                        GCBlocks.OLIVINE_CLUSTER,
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
                        GCBlocks.DENSE_ICE,

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

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(GCBlocks.PARACHEST);

        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(GCBlocks.MOON_TURF)
                .add(GCBlocks.MOON_DIRT)
                .add(GCBlocks.MOON_DIRT_PATH);

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(
                        GCBlocks.IRON_GRATING,
                        GCBlocks.WALKWAY,
                        GCBlocks.WIRE_WALKWAY,
                        GCBlocks.FLUID_PIPE_WALKWAY,
                        GCBlocks.MARS_IRON_ORE,
                        GCBlocks.ASTEROID_IRON_ORE,
                        GCBlocks.MOON_COPPER_ORE,
                        GCBlocks.LUNASLATE_COPPER_ORE,
                        GCBlocks.MARS_COPPER_ORE,
                        GCBlocks.VENUS_COPPER_ORE,
                        GCBlocks.TIN_ORE,
                        GCBlocks.DEEPSLATE_TIN_ORE,
                        GCBlocks.MOON_TIN_ORE,
                        GCBlocks.LUNASLATE_TIN_ORE,
                        GCBlocks.MARS_TIN_ORE,
                        GCBlocks.VENUS_TIN_ORE,
                        GCBlocks.ALUMINUM_ORE,
                        GCBlocks.DEEPSLATE_ALUMINUM_ORE,
                        GCBlocks.ASTEROID_ALUMINUM_ORE,
                        GCBlocks.VENUS_ALUMINUM_ORE,
                        GCBlocks.MOON_CHEESE_ORE,
                        GCBlocks.GALENA_ORE,
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
                        GCBlocks.ASTEROID_SILICON_ORE,
                        GCBlocks.FALLEN_METEOR,
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
                .add(GCBlocks.AIR_LOCK_SEAL);

        this.tag(BlockTags.WITHER_IMMUNE)
                .add(GCBlocks.AIR_LOCK_SEAL);

        this.tag(BlockTags.WALL_POST_OVERRIDE)
                .add(GCBlocks.GLOWSTONE_TORCH)
                .add(GCBlocks.UNLIT_TORCH);

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

        this.tag(ConventionalBlockTags.STONES)
                .add(GCBlocks.MOON_SURFACE_ROCK)
                .add(GCBlocks.MOON_ROCK)
                .add(GCBlocks.LUNASLATE)
                .add(GCBlocks.MARS_SURFACE_ROCK)
                .add(GCBlocks.MARS_SUB_SURFACE_ROCK)
                .add(GCBlocks.MARS_STONE)
                .add(GCBlocks.SOFT_VENUS_ROCK)
                .add(GCBlocks.HARD_VENUS_ROCK);

        this.tag(GCBlockTags.MOON_COBBLESTONES)
                .add(GCBlocks.COBBLED_MOON_ROCK);
        this.tag(GCBlockTags.LUNASLATE_COBBLESTONES)
                .add(GCBlocks.COBBLED_LUNASLATE);
        this.tag(GCBlockTags.MARS_COBBLESTONES)
                .add(GCBlocks.MARS_COBBLESTONE);
        this.tag(ConventionalBlockTags.COBBLESTONES)
                .addTag(GCBlockTags.MOON_COBBLESTONES)
                .addTag(GCBlockTags.LUNASLATE_COBBLESTONES)
                .addTag(GCBlockTags.MARS_COBBLESTONES);

        this.tag(BlockTags.ICE)
                .add(GCBlocks.DENSE_ICE);

        this.tag(ConventionalBlockTags.PLAYER_WORKSTATIONS_FURNACES)
                .add(GCBlocks.ELECTRIC_FURNACE)
                .add(GCBlocks.ELECTRIC_ARC_FURNACE);

        this.tag(ConventionalBlockTags.VILLAGER_JOB_SITES)
                .add(GCBlocks.LUNAR_CARTOGRAPHY_TABLE);

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