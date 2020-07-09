package com.hrznstudio.galacticraft.structure.moon_village;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Constants;
import com.mojang.datafixers.util.Pair;
import net.minecraft.class_5464;
import net.minecraft.class_5468;
import net.minecraft.class_5469;
import net.minecraft.structure.pool.*;
import net.minecraft.util.Identifier;

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
    public static final Identifier BASE_POOL_ID = TOWN_CENTERS;

    static { //todo
        BASE_POOL = class_5468.method_30600(new StructurePool(TOWN_CENTERS,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30426(BASE_ID + "town_centers/plains_fountain_01", class_5469.field_26265), 50),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "town_centers/plains_meeting_point_1", class_5469.field_26265), 50),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "town_centers/plains_meeting_point_2"), 50),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "town_centers/plains_meeting_point_3", class_5469.field_26266), 50),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/town_centers/plains_fountain_01", class_5469.field_26259), 1),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/town_centers/plains_meeting_point_1", class_5469.field_26259), 1),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/town_centers/plains_meeting_point_2", class_5469.field_26259), 1),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/town_centers/plains_meeting_point_3", class_5469.field_26259), 1)),
                StructurePool.Projection.RIGID));
        
        class_5468.method_30600(new StructurePool(STREETS,
                TERMINATORS,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/corner_01", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/corner_02", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/corner_03", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/straight_01", class_5469.field_26267), 4),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/straight_02", class_5469.field_26267), 4),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/straight_03", class_5469.field_26267), 7),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/straight_04", class_5469.field_26267), 7),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/straight_05", class_5469.field_26267), 3),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/straight_06", class_5469.field_26267), 4),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/crossroad_01", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/crossroad_02", class_5469.field_26267), 1),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/crossroad_03", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/crossroad_04", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/crossroad_05", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/crossroad_06", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "streets/turn_01", class_5469.field_26267), 3)),
                StructurePool.Projection.TERRAIN_MATCHING));
        
        class_5468.method_30600(new StructurePool(DEAD_STREETS,
                TERMINATORS,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/corner_01", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/corner_02", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/corner_03", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/straight_01", class_5469.field_26267), 4),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/straight_02", class_5469.field_26267), 4),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/straight_03", class_5469.field_26267), 7),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/straight_04", class_5469.field_26267), 7),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/straight_05", class_5469.field_26267), 3),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/straight_06", class_5469.field_26267), 4),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/crossroad_01", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/crossroad_02", class_5469.field_26267), 1),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/crossroad_03", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/crossroad_04", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/crossroad_05", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/crossroad_06", class_5469.field_26267), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/streets/turn_01", class_5469.field_26267), 3)),
                StructurePool.Projection.TERRAIN_MATCHING));
        
        class_5468.method_30600(new StructurePool(HOUSES,
                TERMINATORS,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_small_house_1", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_small_house_2", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_small_house_3", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_small_house_4", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_small_house_5", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_small_house_6", class_5469.field_26264), 1),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_small_house_7", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_small_house_8", class_5469.field_26264), 3),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_medium_house_1", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_medium_house_2", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_big_house_1", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_butcher_shop_1", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_butcher_shop_2", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_tool_smith_1", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_fletcher_house_1", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/plains_shepherds_house_1"), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_armorer_house_1", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_fisher_cottage_1", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_tannery_1", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_cartographer_1", class_5469.field_26264), 1),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_library_1", class_5469.field_26264), 5),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_library_2", class_5469.field_26264), 1),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_masons_house_1", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_weaponsmith_1", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_temple_3", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_temple_4", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_stable_1", class_5469.field_26264), 2),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/plains_stable_2"), 2),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_large_farm_1", class_5469.field_26270), 4),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_small_farm_1", class_5469.field_26270), 4),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/plains_animal_pen_1"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/plains_animal_pen_2"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/plains_animal_pen_3"), 5),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/plains_accessory_1"), 1),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_meeting_point_4", class_5469.field_26266), 3),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "houses/plains_meeting_point_5"), 1),
                        Pair.of(StructurePoolElement.method_30438(), 10)),
                StructurePool.Projection.RIGID));
        
        class_5468.method_30600(new StructurePool(DEAD_HOUSES, TERMINATORS, ImmutableList.of(Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_small_house_1", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_small_house_2", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_small_house_3", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_small_house_4", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_small_house_5", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_small_house_6", class_5469.field_26259), 1),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_small_house_7", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_small_house_8", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_medium_house_1", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_medium_house_2", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_big_house_1", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_butcher_shop_1", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_butcher_shop_2", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_tool_smith_1", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_fletcher_house_1", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_shepherds_house_1", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_armorer_house_1", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_fisher_cottage_1", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_tannery_1", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_cartographer_1", class_5469.field_26259), 1),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_library_1", class_5469.field_26259), 3),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_library_2", class_5469.field_26259), 1),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_masons_house_1", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_weaponsmith_1", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_temple_3", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_temple_4", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_stable_1", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_stable_2", class_5469.field_26259), 2),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_large_farm_1", class_5469.field_26259), 4),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_small_farm_1", class_5469.field_26259), 4),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_animal_pen_1", class_5469.field_26259), 1),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "houses/plains_animal_pen_2", class_5469.field_26259), 1),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_animal_pen_3", class_5469.field_26259), 5),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_meeting_point_4", class_5469.field_26259), 3),
                Pair.of(StructurePoolElement.method_30426(BASE_ID + "dead/houses/plains_meeting_point_5", class_5469.field_26259), 1),
                Pair.of(StructurePoolElement.method_30438(), 10)), StructurePool.Projection.RIGID));
        
        class_5468.method_30600(new StructurePool(TERMINATORS,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30426(BASE_ID + "terminators/terminator_01", class_5469.field_26267), 1),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "terminators/terminator_02", class_5469.field_26267), 1),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "terminators/terminator_03", class_5469.field_26267), 1),
                        Pair.of(StructurePoolElement.method_30426(BASE_ID + "terminators/terminator_04", class_5469.field_26267), 1)),
                StructurePool.Projection.TERRAIN_MATCHING));
        
        class_5468.method_30600(new StructurePool(TREES,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30421(class_5464.field_26036), 1)),
                StructurePool.Projection.RIGID));
        
        class_5468.method_30600(new StructurePool(DECOR,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "plains_lamp_1"), 2),
                        Pair.of(StructurePoolElement.method_30421(class_5464.field_26036), 1),
                        Pair.of(StructurePoolElement.method_30421(class_5464.field_26104), 1),
                        Pair.of(StructurePoolElement.method_30421(class_5464.field_26009), 1),
                        Pair.of(StructurePoolElement.method_30438(), 2)),
                StructurePool.Projection.RIGID));
        
        class_5468.method_30600(new StructurePool(DEAD_DECOR,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30426(BASE_ID + "plains_lamp_1", class_5469.field_26259), 1),
                        Pair.of(StructurePoolElement.method_30421(class_5464.field_26036), 1),
                        Pair.of(StructurePoolElement.method_30421(class_5464.field_26104), 1),
                        Pair.of(StructurePoolElement.method_30421(class_5464.field_26009), 1),
                        Pair.of(StructurePoolElement.method_30438(), 2)),
                StructurePool.Projection.RIGID));
        
        class_5468.method_30600(new StructurePool(VILLAGERS,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "villagers/nitwit"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "villagers/baby"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "villagers/unemployed"), 10)),
                StructurePool.Projection.RIGID));
        
        class_5468.method_30600(new StructurePool(DEAD_VILLAGERS,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/villagers/nitwit"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "dead/villagers/unemployed"), 10)),
                StructurePool.Projection.RIGID));
        
        class_5468.method_30600(new StructurePool(ANIMALS,
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
        
        class_5468.method_30600(new StructurePool(SHEEP,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/sheep_1"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/sheep_2"), 1)),
                StructurePool.Projection.RIGID));
        
        class_5468.method_30600(new StructurePool(CATS,
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

        class_5468.method_30600(new StructurePool(BUTCHER_ANIMALS,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/cows_1"), 3),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/pigs_1"), 3),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/sheep_1"), 1),
                        Pair.of(StructurePoolElement.method_30425(BASE_ID + "animals/sheep_2"), 1)),
                StructurePool.Projection.RIGID));

        class_5468.method_30600(new StructurePool(IRON_GOLEM,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "iron_golem"), 1)),
                StructurePool.Projection.RIGID));

        class_5468.method_30600(new StructurePool(WELL_BOTTOMS,
                EMPTY,
                ImmutableList.of(Pair.of(StructurePoolElement.method_30425(BASE_ID + "well_bottom"), 1)),
                StructurePool.Projection.RIGID));
    }

    public static void init() {

    }
}
