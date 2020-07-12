package com.hrznstudio.galacticraft.structure.moon_village;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.*;
import net.minecraft.structure.processor.ProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeatures;

public class MoonVillageData {
    
    public static final StructurePool BASE_POOL;
    
    private static final String BASE_ID = Constants.MOD_ID + ":moon_village/";

    private static final Identifier EMPTY = new Identifier("empty");
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

    public static final Identifier TOWN_CENTERS = new Identifier(Constants.MOD_ID, "moon_village/town_centers");

    static {
        BASE_POOL = TemplatePools.register(new StructurePool(TOWN_CENTERS,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "town_centers/fountain_01"), 50),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "town_centers/meeting_point_1"), 50),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "town_centers/meeting_point_2"), 50),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "town_centers/meeting_point_3"), 50),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/town_centers/fountain_01"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/town_centers/meeting_point_1"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/town_centers/meeting_point_2"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/town_centers/meeting_point_3"), 1)),
                StructurePool.Projection.RIGID));
        
        TemplatePools.register(new StructurePool(STREETS,
                TERMINATORS,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/corner_01"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/corner_02"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/corner_03"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/straight_01"), 4),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/straight_02"), 4),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/straight_03"), 7),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/straight_04"), 7),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/straight_05"), 3),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/straight_06"), 4),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/crossroad_01"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/crossroad_02"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/crossroad_03"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/crossroad_04"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/crossroad_05"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/crossroad_06"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "streets/turn_01"), 3)),
                StructurePool.Projection.TERRAIN_MATCHING));
        
        TemplatePools.register(new StructurePool(DEAD_STREETS,
                TERMINATORS,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/corner_01"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/corner_02"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/corner_03"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/straight_01"), 4),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/straight_02"), 4),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/straight_03"), 7),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/straight_04"), 7),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/straight_05"), 3),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/straight_06"), 4),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/crossroad_01"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/crossroad_02"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/crossroad_03"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/crossroad_04"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/crossroad_05"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/crossroad_06"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/streets/turn_01"), 3)),
                StructurePool.Projection.TERRAIN_MATCHING));
        
        TemplatePools.register(new StructurePool(HOUSES,
                TERMINATORS,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/small_house_1"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/small_house_2"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/small_house_3"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/small_house_4"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/small_house_5"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/small_house_6"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/small_house_7"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/small_house_8"), 3),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/medium_house_1"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/medium_house_2"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/big_house_1"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/butcher_shop_1"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/butcher_shop_2"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/tool_smith_1"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/fletcher_house_1"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/shepherds_house_1"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/armorer_house_1"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/fisher_cottage_1"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/tannery_1"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/cartographer_1"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/library_1"), 5),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/library_2"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/masons_house_1"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/weaponsmith_1"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/temple_3"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/temple_4"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/stable_1"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/stable_2"), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/large_farm_1"), 4),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/small_farm_1"), 4),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/animal_pen_1"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/animal_pen_2"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/animal_pen_3"), 5),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/accessory_1"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/meeting_point_4"), 3),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/meeting_point_5"), 1),
                        Pair.of(StructurePoolElement.method_30438(), 10)),
                StructurePool.Projection.RIGID));
        
        TemplatePools.register(new StructurePool(DEAD_HOUSES, TERMINATORS, ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/small_house_1"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/small_house_2"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/small_house_3"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/small_house_4"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/small_house_5"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/small_house_6"), 1),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/small_house_7"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/small_house_8"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/medium_house_1"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/medium_house_2"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/big_house_1"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/butcher_shop_1"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/butcher_shop_2"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/tool_smith_1"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/fletcher_house_1"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/shepherds_house_1"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/armorer_house_1"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/fisher_cottage_1"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/tannery_1"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/cartographer_1"), 1),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/library_1"), 3),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/library_2"), 1),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/masons_house_1"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/weaponsmith_1"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/temple_3"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/temple_4"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/stable_1"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/stable_2"), 2),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/large_farm_1"), 4),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/small_farm_1"), 4),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/animal_pen_1"), 1),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/animal_pen_2"), 1),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/animal_pen_3"), 5),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/meeting_point_4"), 3),
                Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/houses/meeting_point_5"), 1),
                Pair.of(StructurePoolElement.method_30438(), 10)), StructurePool.Projection.RIGID));
        
        TemplatePools.register(new StructurePool(TERMINATORS,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "terminators/terminator_01"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "terminators/terminator_02"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "terminators/terminator_03"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "terminators/terminator_04"), 1)),
                StructurePool.Projection.TERRAIN_MATCHING));
        
        TemplatePools.register(new StructurePool(TREES,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.TREES_GIANT_SPRUCE), 1)),///test
                StructurePool.Projection.RIGID));
        
        TemplatePools.register(new StructurePool(DECOR,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "lamp_1"), 2),
                        Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.OAK), 1),
                        Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.FLOWER_PLAIN), 1),
                        Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PILE_HAY), 1),
                        Pair.of(StructurePoolElement.method_30438(), 2)),
                StructurePool.Projection.RIGID));
        
        TemplatePools.register(new StructurePool(DEAD_DECOR,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "lamp_1"), 1),
                        Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.OAK), 1),
                        Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.FLOWER_PLAIN), 1),
                        Pair.of(StructurePoolElement.method_30421(ConfiguredFeatures.PILE_HAY), 1),
                        Pair.of(StructurePoolElement.method_30438(), 2)),
                StructurePool.Projection.RIGID));
        
        TemplatePools.register(new StructurePool(VILLAGERS,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "villagers/nitwit"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "villagers/baby"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "villagers/unemployed"), 10)),
                StructurePool.Projection.RIGID));
        
        TemplatePools.register(new StructurePool(DEAD_VILLAGERS,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/villagers/nitwit"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/villagers/unemployed"), 10)),
                StructurePool.Projection.RIGID));
        
        TemplatePools.register(new StructurePool(ANIMALS,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/cows_1"), 7),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/pigs_1"), 7),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/horses_1"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/horses_2"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/horses_3"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/horses_4"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/horses_5"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/sheep_1"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/sheep_2"), 1),
                        Pair.of(StructurePoolElement.method_30438(), 5)),
                StructurePool.Projection.RIGID));
        
        TemplatePools.register(new StructurePool(SHEEP,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/sheep_1"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/sheep_2"), 1)),
                StructurePool.Projection.RIGID));
        
        TemplatePools.register(new StructurePool(CATS,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/cat_black"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/cat_british"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/cat_calico"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/cat_persian"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/cat_ragdoll"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/cat_red"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/cat_siamese"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/cat_tabby"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/cat_white"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/cat_jellie"), 1),
                        Pair.of(StructurePoolElement.method_30438(), 3)),
                StructurePool.Projection.RIGID));

        TemplatePools.register(new StructurePool(BUTCHER_ANIMALS,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/cows_1"), 3),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/pigs_1"), 3),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/sheep_1"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/sheep_2"), 1)),
                StructurePool.Projection.RIGID));

        TemplatePools.register(new StructurePool(IRON_GOLEM,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "iron_golem"), 1)),
                StructurePool.Projection.RIGID));

        TemplatePools.register(new StructurePool(WELL_BOTTOMS,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "well_bottom"), 1)),
                StructurePool.Projection.RIGID));
    }

    public static void init() {

    }
}
