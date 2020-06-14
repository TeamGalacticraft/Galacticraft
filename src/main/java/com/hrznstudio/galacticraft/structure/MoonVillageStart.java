package com.hrznstudio.galacticraft.structure;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.*;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.*;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class MoonVillageStart extends StructureStart<StructurePoolFeatureConfig> {

    private static final Identifier EMPTY = new Identifier("empty");
    private static final Identifier TOWN_CENTERS = new Identifier(Constants.MOD_ID, "moon_village/town_centers");
    public static final Identifier BASE_POOL = TOWN_CENTERS;
    private static final Identifier STREETS = new Identifier(Constants.MOD_ID, "moon_village/streets");
    private static final Identifier DEAD_STREETS = new Identifier(Constants.MOD_ID, "moon_village/dead/streets");
    private static final Identifier TERMINATORS = new Identifier(Constants.MOD_ID, "moon_village/terminators");
    private static final Identifier HOUSES = new Identifier(Constants.MOD_ID, "moon_village/houses");
    private static final Identifier DEAD_HOUSES = new Identifier(Constants.MOD_ID, "moon_village/dead/houses");
    private static final Identifier TREES = new Identifier(Constants.MOD_ID, "moon_village/trees");
    private static final Identifier DECOR = new Identifier(Constants.MOD_ID, "moon_village/decor");
    private static final Identifier DEAD_DECOR = new Identifier(Constants.MOD_ID, "moon_village/dead/decor");
    private static final Identifier VILLAGERS = new Identifier(Constants.MOD_ID, "moon_village/villagers");
    private static final Identifier DEAD_VILLAGERS = new Identifier(Constants.MOD_ID, "moon_village/dead/villagers");
    private static final Identifier ANIMALS = new Identifier(Constants.MOD_ID, "moon_village/animals");
    private static final Identifier SHEEP = new Identifier(Constants.MOD_ID, "moon_village/sheep");
    private static final Identifier CATS = new Identifier(Constants.MOD_ID, "moon_village/cats");
    private static final Identifier BUTCHER_ANIMALS = new Identifier(Constants.MOD_ID, "moon_village/butcher_animals");
    private static final Identifier IRON_GOLEM = new Identifier(Constants.MOD_ID, "moon_village/iron_golem");
    private static final Identifier WELL_BOTTOMS = new Identifier(Constants.MOD_ID, "moon_village/well_bottoms");

    static {
        ImmutableList<StructureProcessor> immutableList = ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.8F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()),
                new StructureProcessorRule(new TagMatchRuleTest(BlockTags.DOORS), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
                new StructureProcessorRule(new BlockMatchRuleTest(Blocks.TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
                new StructureProcessorRule(new BlockMatchRuleTest(Blocks.WALL_TORCH), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.MOSSY_COBBLESTONE, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHITE_TERRACOTTA, 0.07F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.OAK_LOG, 0.05F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.OAK_PLANKS, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.OAK_STAIRS, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.STRIPPED_OAK_LOG, 0.02F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GLASS_PANE, 0.5F), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
                new StructureProcessorRule(new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true).with(PaneBlock.SOUTH, true)), AlwaysTrueRuleTest.INSTANCE, Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.NORTH, true).with(PaneBlock.SOUTH, true)),
                new StructureProcessorRule(new BlockStateMatchRuleTest(Blocks.GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true).with(PaneBlock.WEST, true)), AlwaysTrueRuleTest.INSTANCE, Blocks.BROWN_STAINED_GLASS_PANE.getDefaultState().with(PaneBlock.EAST, true).with(PaneBlock.WEST, true)),
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CARROTS.getDefaultState()),
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState()),
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.BEETROOTS.getDefaultState()))));

        ImmutableList<StructureProcessor> immutableList2 = ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new StructureProcessorRule(
                new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.1F),
                AlwaysTrueRuleTest.INSTANCE,
                Blocks.MOSSY_COBBLESTONE.getDefaultState()))));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(TOWN_CENTERS, EMPTY, ImmutableList.of(
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/town_centers/fountain_01")), ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState())))), StructurePool.Projection.RIGID), 50),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/town_centers/meeting_point_1")), ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState())))), StructurePool.Projection.RIGID), 50),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/town_centers/meeting_point_2")), ImmutableList.of(), StructurePool.Projection.RIGID), 50),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/town_centers/meeting_point_3")), ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.7F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState())))), StructurePool.Projection.RIGID), 50),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/town_centers/fountain_01")), immutableList, StructurePool.Projection.RIGID), 1),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/town_centers/meeting_point_1")), immutableList, StructurePool.Projection.RIGID), 1),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/town_centers/meeting_point_2")), immutableList, StructurePool.Projection.RIGID), 1),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/town_centers/meeting_point_3")), immutableList, StructurePool.Projection.RIGID), 1)), StructurePool.Projection.RIGID));

        ImmutableList<StructureProcessor> immutableList3 = ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(
                new StructureProcessorRule(new BlockMatchRuleTest(Blocks.GRASS_PATH), new BlockMatchRuleTest(Blocks.WATER), Blocks.OAK_PLANKS.getDefaultState()),
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.GRASS_PATH, 0.1F), AlwaysTrueRuleTest.INSTANCE, GalacticraftBlocks.MOON_TURF.getDefaultState()),
                new StructureProcessorRule(new BlockMatchRuleTest(GalacticraftBlocks.MOON_TURF), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState()),
                new StructureProcessorRule(new BlockMatchRuleTest(GalacticraftBlocks.MOON_DIRT), new BlockMatchRuleTest(Blocks.WATER), Blocks.WATER.getDefaultState()))));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(STREETS, TERMINATORS, ImmutableList.of(
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/corner_01")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/corner_02")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/corner_03")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/straight_01")), immutableList3, StructurePool.Projection.RIGID), 4),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/straight_02")), immutableList3, StructurePool.Projection.RIGID), 4),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/straight_03")), immutableList3, StructurePool.Projection.RIGID), 7),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/straight_04")), immutableList3, StructurePool.Projection.RIGID), 7),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/straight_05")), immutableList3, StructurePool.Projection.RIGID), 3),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/straight_06")), immutableList3, StructurePool.Projection.RIGID), 4),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/crossroad_01")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/crossroad_02")), immutableList3, StructurePool.Projection.RIGID), 1),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/crossroad_03")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/crossroad_04")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/crossroad_05")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/crossroad_06")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/streets/turn_01")), immutableList3, StructurePool.Projection.RIGID), 3)), StructurePool.Projection.TERRAIN_MATCHING));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(DEAD_STREETS, TERMINATORS, ImmutableList.of(
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/corner_01")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/corner_02")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/corner_03")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/straight_01")), immutableList3, StructurePool.Projection.RIGID), 4),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/straight_02")), immutableList3, StructurePool.Projection.RIGID), 4),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/straight_03")), immutableList3, StructurePool.Projection.RIGID), 7),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/straight_04")), immutableList3, StructurePool.Projection.RIGID), 7),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/straight_05")), immutableList3, StructurePool.Projection.RIGID), 3),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/straight_06")), immutableList3, StructurePool.Projection.RIGID), 4),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/crossroad_01")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/crossroad_02")), immutableList3, StructurePool.Projection.RIGID), 1),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/crossroad_03")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/crossroad_04")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/crossroad_05")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/crossroad_06")), immutableList3, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/streets/turn_01")), immutableList3, StructurePool.Projection.RIGID), 3)), StructurePool.Projection.TERRAIN_MATCHING));

        ImmutableList<StructureProcessor> immutableList4 = ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.3F), AlwaysTrueRuleTest.INSTANCE, Blocks.CARROTS.getDefaultState()),
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.2F), AlwaysTrueRuleTest.INSTANCE, Blocks.POTATOES.getDefaultState()),
                new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHEAT, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.BEETROOTS.getDefaultState()))));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(HOUSES, TERMINATORS, ImmutableList.of(
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/small_house_1")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/small_house_2")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/small_house_3")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/small_house_4")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/small_house_5")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/small_house_6")), immutableList2, StructurePool.Projection.RIGID), 1),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/small_house_7")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/small_house_8")), immutableList2, StructurePool.Projection.RIGID), 3),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/medium_house_1")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/medium_house_2")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/big_house_1")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/butcher_shop_1")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/butcher_shop_2")), immutableList2, StructurePool.Projection.RIGID), 2),

                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/tool_smith_1")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/fletcher_house_1")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/shepherds_house_1")), ImmutableList.of(), StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/armorer_house_1")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/fisher_cottage_1")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/tannery_1")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/cartographer_1")), immutableList2, StructurePool.Projection.RIGID), 1),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/library_1")), immutableList2, StructurePool.Projection.RIGID), 5),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/library_2")), immutableList2, StructurePool.Projection.RIGID), 1),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/masons_house_1")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/weaponsmith_1")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/temple_3")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/temple_4")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/stable_1")), immutableList2, StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/stable_2")), ImmutableList.of(), StructurePool.Projection.RIGID), 2),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/large_farm_1")), immutableList4, StructurePool.Projection.RIGID), 4),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/small_farm_1")), immutableList4, StructurePool.Projection.RIGID), 4),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/animal_pen_1")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/animal_pen_2")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/animal_pen_3")), ImmutableList.of(), StructurePool.Projection.RIGID), 5),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/accessory_1")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/meeting_point_4")), ImmutableList.of(new RuleStructureProcessor(ImmutableList.of(new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.7F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState())))), StructurePool.Projection.RIGID), 3),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/meeting_point_5")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                Pair.of(EmptyPoolElement.INSTANCE, 10)),
                StructurePool.Projection.RIGID));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(DEAD_HOUSES, TERMINATORS,
                ImmutableList.of(
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/small_house_1")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/small_house_2")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/small_house_3")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/small_house_4")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/small_house_5")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/small_house_6")), immutableList, StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/small_house_7")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/small_house_8")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/medium_house_1")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/medium_house_2")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/big_house_1")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/butcher_shop_1")), immutableList, StructurePool.Projection.RIGID), 2),

                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/butcher_shop_2")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/tool_smith_1")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/fletcher_house_1")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/shepherds_house_1")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/armorer_house_1")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/fisher_cottage_1")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/tannery_1")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/cartographer_1")), immutableList, StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/library_1")), immutableList, StructurePool.Projection.RIGID), 3),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/library_2")), immutableList, StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/masons_house_1")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/weaponsmith_1")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/temple_3")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/temple_4")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/stable_1")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/stable_2")), immutableList, StructurePool.Projection.RIGID), 2),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/large_farm_1")), immutableList, StructurePool.Projection.RIGID), 4),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/small_farm_1")), immutableList, StructurePool.Projection.RIGID), 4),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/animal_pen_1")), immutableList, StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/houses/animal_pen_2")), immutableList, StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/animal_pen_3")), immutableList, StructurePool.Projection.RIGID), 5),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/meeting_point_4")), immutableList, StructurePool.Projection.RIGID), 3),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/houses/meeting_point_5")), immutableList, StructurePool.Projection.RIGID), 1),
                        Pair.of(EmptyPoolElement.INSTANCE, 10)), StructurePool.Projection.RIGID));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(TERMINATORS, EMPTY,
                ImmutableList.of(
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/terminators/terminator_01")), immutableList3, StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/terminators/terminator_02")), immutableList3, StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/terminators/terminator_03")), immutableList3, StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/terminators/terminator_04")), immutableList3, StructurePool.Projection.RIGID), 1)),
                StructurePool.Projection.TERRAIN_MATCHING));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(TREES, EMPTY,
                ImmutableList.of(Pair.of(new FeaturePoolElement(Feature.TREE.configure(DefaultBiomeFeatures.OAK_TREE_CONFIG), StructurePool.Projection.RIGID), 1)),
                StructurePool.Projection.RIGID));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(DECOR, EMPTY,
                ImmutableList.of(Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/lamp_1")), ImmutableList.of(), StructurePool.Projection.RIGID), 2),
                        Pair.of(new FeaturePoolElement(Feature.TREE.configure(DefaultBiomeFeatures.OAK_TREE_CONFIG), StructurePool.Projection.RIGID), 1), //// I cant use the non-deprecated version as its private >:(
                        Pair.of(new FeaturePoolElement(Feature.FLOWER.configure(GalacticraftFeatures.MOON_FLOWER_CONFIG), StructurePool.Projection.RIGID), 1),
                        Pair.of(new FeaturePoolElement(Feature.BLOCK_PILE.configure(GalacticraftFeatures.CHEESE_LOG_PILE_CONFIG), StructurePool.Projection.RIGID), 1),
                        Pair.of(EmptyPoolElement.INSTANCE, 2)),
                StructurePool.Projection.RIGID));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(DEAD_DECOR, EMPTY,
                ImmutableList.of(Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/lamp_1")), immutableList, StructurePool.Projection.RIGID), 1),
                        Pair.of(new FeaturePoolElement(Feature.TREE.configure(DefaultBiomeFeatures.OAK_TREE_CONFIG), StructurePool.Projection.RIGID), 1),
                        Pair.of(new FeaturePoolElement(Feature.FLOWER.configure(GalacticraftFeatures.MOON_FLOWER_CONFIG), StructurePool.Projection.RIGID), 1),
                        Pair.of(new FeaturePoolElement(Feature.BLOCK_PILE.configure(GalacticraftFeatures.CHEESE_LOG_PILE_CONFIG), StructurePool.Projection.RIGID), 1),
                        Pair.of(EmptyPoolElement.INSTANCE, 2)),
                StructurePool.Projection.RIGID));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(VILLAGERS, EMPTY,
                ImmutableList.of(Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/villagers/nitwit")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/villagers/baby")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/villagers/unemployed")), ImmutableList.of(), StructurePool.Projection.RIGID), 10)),
                StructurePool.Projection.RIGID));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(DEAD_VILLAGERS, EMPTY, ImmutableList.of(
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/villagers/nitwit")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/dead/villagers/unemployed")), ImmutableList.of(), StructurePool.Projection.RIGID), 10)), StructurePool.Projection.RIGID));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(ANIMALS, EMPTY,
                ImmutableList.of(Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/cows_1")), ImmutableList.of(), StructurePool.Projection.RIGID), 7),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/pigs_1")), ImmutableList.of(), StructurePool.Projection.RIGID), 7),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/horses_1")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/horses_2")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/horses_3")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/horses_4")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/horses_5")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/sheep_1")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/sheep_2")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(EmptyPoolElement.INSTANCE, 5)),
                StructurePool.Projection.RIGID));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(SHEEP,
                EMPTY,
                ImmutableList.of(Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/sheep_1")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/sheep_2")), ImmutableList.of(), StructurePool.Projection.RIGID), 1)),
                StructurePool.Projection.RIGID));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(CATS, EMPTY,
                ImmutableList.of(
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/cat_black")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/cat_british")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/cat_calico")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/cat_persian")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/cat_ragdoll")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/cat_red")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/cat_siamese")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/cat_tabby")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/cat_white")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/cat_jellie")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(EmptyPoolElement.INSTANCE, 3)), StructurePool.Projection.RIGID));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(BUTCHER_ANIMALS, EMPTY,
                ImmutableList.of(Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/cows_1")), ImmutableList.of(), StructurePool.Projection.RIGID), 3),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/pigs_1")), ImmutableList.of(), StructurePool.Projection.RIGID), 3),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/sheep_1")), ImmutableList.of(), StructurePool.Projection.RIGID), 1),
                        Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/animals/sheep_2")), ImmutableList.of(), StructurePool.Projection.RIGID), 1)),
                StructurePool.Projection.RIGID));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(IRON_GOLEM, EMPTY,
                ImmutableList.of(Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/iron_golem")), ImmutableList.of(), StructurePool.Projection.RIGID), 1)),
                StructurePool.Projection.RIGID));

        StructurePoolBasedGenerator.REGISTRY.add(new StructurePool(WELL_BOTTOMS, EMPTY,
                ImmutableList.of(Pair.of(new LegacySinglePoolElement(Either.left(new Identifier(Constants.MOD_ID, "moon_village/well_bottom")), ImmutableList.of(), StructurePool.Projection.RIGID), 1)),
                StructurePool.Projection.RIGID));
    }

    public MoonVillageStart(StructureFeature<StructurePoolFeatureConfig> structureFeature, int chunkX, int chunkZ, BlockBox blockBox, int i, long l) {
        super(structureFeature, chunkX, chunkZ, blockBox, i, l);
    }

    @Override
    public void init(ChunkGenerator chunkGenerator, StructureManager structureManager, int x, int z, Biome biome, StructurePoolFeatureConfig featureConfig) {
        StructurePoolBasedGenerator.addPieces(BASE_POOL, 6, MoonVillagePiece::new, chunkGenerator, structureManager, new BlockPos(x * 16, 70, z * 16), this.children, random, true, true);
        this.setBoundingBoxFromChildren();
    }

    @Override
    protected void setBoundingBoxFromChildren() {
        super.setBoundingBoxFromChildren();
        BlockBox var10000 = this.boundingBox;
        var10000.minX -= 12;
        var10000 = this.boundingBox;
        var10000.minY -= 12;
        var10000 = this.boundingBox;
        var10000.minZ -= 12;
        var10000 = this.boundingBox;
        var10000.maxX += 12;
        var10000 = this.boundingBox;
        var10000.maxY += 12;
        var10000 = this.boundingBox;
        var10000.maxZ += 12;
    }
}
