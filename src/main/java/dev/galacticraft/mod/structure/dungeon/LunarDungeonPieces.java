package dev.galacticraft.mod.structure.dungeon;

import dev.galacticraft.mod.Constant;

public final class LunarDungeonPieces {
    private LunarDungeonPieces() {
    }

    // -------------------------------------------------------------------------
    // LANDMARKS
    // -------------------------------------------------------------------------

    public static final DungeonPieceDefinition HUB =
            landmark("hub", DungeonWing.COMMON);

    public static final DungeonPieceDefinition ENTRANCE =
            landmark("entrance", DungeonWing.COMMON);

    public static final DungeonPieceDefinition SECURITY_OBJECTIVE =
            landmark("security/objective", DungeonWing.SECURITY);

    public static final DungeonPieceDefinition REACTOR_OBJECTIVE =
            landmark("reactor/objective", DungeonWing.REACTOR);

    public static final DungeonPieceDefinition EXCAVATION_OBJECTIVE =
            landmark("excavation/objective", DungeonWing.EXCAVATION);

    public static final DungeonPieceDefinition FINAL_DESCENT =
            landmark("final/descent", DungeonWing.FINAL);

    public static final DungeonPieceDefinition BOSS =
            landmark("final/boss", DungeonWing.FINAL);

    public static final DungeonPieceDefinition TREASURE =
            landmark("final/treasure", DungeonWing.FINAL);

    // -------------------------------------------------------------------------
    // SECURITY
    // -------------------------------------------------------------------------

    public static final DungeonPieceDefinition SECURITY_CORRIDOR_SHORT =
            piece(
                    "security/corridor_short_1",
                    DungeonWing.SECURITY,
                    DungeonPieceCategory.CORRIDOR,
                    12,
                    0,
                    0,
                    100,
                    true
            );

    public static final DungeonPieceDefinition SECURITY_CORRIDOR_LONG =
            piece(
                    "security/corridor_long_1",
                    DungeonWing.SECURITY,
                    DungeonPieceCategory.CORRIDOR,
                    8,
                    0,
                    0,
                    100,
                    true
            );

    public static final DungeonPieceDefinition SECURITY_CORNER =
            piece(
                    "security/corner_1",
                    DungeonWing.SECURITY,
                    DungeonPieceCategory.CORRIDOR,
                    8,
                    0,
                    0,
                    100,
                    true
            );

    public static final DungeonPieceDefinition SECURITY_JUNCTION =
            piece(
                    "security/junction_1",
                    DungeonWing.SECURITY,
                    DungeonPieceCategory.JUNCTION,
                    5,
                    4,
                    2,
                    100,
                    false
            );

    public static final DungeonPieceDefinition SECURITY_ROOM =
            piece(
                    "security/room_1",
                    DungeonWing.SECURITY,
                    DungeonPieceCategory.ROOM,
                    5,
                    3,
                    2,
                    100,
                    true
            );

    public static final DungeonPieceDefinition SECURITY_COMBAT =
            piece(
                    "security/combat_1",
                    DungeonWing.SECURITY,
                    DungeonPieceCategory.COMBAT,
                    3,
                    3,
                    4,
                    100,
                    false
            );

    public static final DungeonPieceDefinition SECURITY_LOOT =
            piece(
                    "security/loot_1",
                    DungeonWing.SECURITY,
                    DungeonPieceCategory.LOOT,
                    1,
                    0,
                    0,
                    100,
                    false
            );

    public static final DungeonPiecePool SECURITY_POOL =
            DungeonPiecePool.of(
                    SECURITY_CORRIDOR_SHORT,
                    SECURITY_CORRIDOR_LONG,
                    SECURITY_CORNER,
                    SECURITY_JUNCTION,
                    SECURITY_ROOM,
                    SECURITY_COMBAT
            );

    // -------------------------------------------------------------------------
    // REACTOR
    // -------------------------------------------------------------------------

    public static final DungeonPieceDefinition REACTOR_CORRIDOR_SHORT =
            piece(
                    "reactor/corridor_short_1",
                    DungeonWing.REACTOR,
                    DungeonPieceCategory.CORRIDOR,
                    12,
                    0,
                    0,
                    100,
                    true
            );

    public static final DungeonPieceDefinition REACTOR_CORRIDOR_LONG =
            piece(
                    "reactor/corridor_long_1",
                    DungeonWing.REACTOR,
                    DungeonPieceCategory.CORRIDOR,
                    8,
                    0,
                    0,
                    100,
                    true
            );

    public static final DungeonPieceDefinition REACTOR_CORNER =
            piece(
                    "reactor/corner_1",
                    DungeonWing.REACTOR,
                    DungeonPieceCategory.CORRIDOR,
                    8,
                    0,
                    0,
                    100,
                    true
            );

    public static final DungeonPieceDefinition REACTOR_JUNCTION =
            piece(
                    "reactor/junction_1",
                    DungeonWing.REACTOR,
                    DungeonPieceCategory.JUNCTION,
                    5,
                    4,
                    2,
                    100,
                    false
            );

    public static final DungeonPieceDefinition REACTOR_ROOM =
            piece(
                    "reactor/room_1",
                    DungeonWing.REACTOR,
                    DungeonPieceCategory.ROOM,
                    5,
                    4,
                    2,
                    100,
                    true
            );

    public static final DungeonPieceDefinition REACTOR_HAZARD =
            piece(
                    "reactor/hazard_1",
                    DungeonWing.REACTOR,
                    DungeonPieceCategory.PUZZLE,
                    3,
                    3,
                    4,
                    100,
                    false
            );

    public static final DungeonPieceDefinition REACTOR_LOOT =
            piece(
                    "reactor/loot_1",
                    DungeonWing.REACTOR,
                    DungeonPieceCategory.LOOT,
                    1,
                    0,
                    0,
                    100,
                    false
            );

    public static final DungeonPiecePool REACTOR_POOL =
            DungeonPiecePool.of(
                    REACTOR_CORRIDOR_SHORT,
                    REACTOR_CORRIDOR_LONG,
                    REACTOR_CORNER,
                    REACTOR_JUNCTION,
                    REACTOR_ROOM,
                    REACTOR_HAZARD
            );

    // -------------------------------------------------------------------------
    // EXCAVATION
    // -------------------------------------------------------------------------

    public static final DungeonPieceDefinition EXCAVATION_TUNNEL_SHORT =
            piece(
                    "excavation/tunnel_short_1",
                    DungeonWing.EXCAVATION,
                    DungeonPieceCategory.CORRIDOR,
                    12,
                    0,
                    0,
                    100,
                    true
            );

    public static final DungeonPieceDefinition EXCAVATION_TUNNEL_LONG =
            piece(
                    "excavation/tunnel_long_1",
                    DungeonWing.EXCAVATION,
                    DungeonPieceCategory.CORRIDOR,
                    8,
                    0,
                    0,
                    100,
                    true
            );

    public static final DungeonPieceDefinition EXCAVATION_CORNER =
            piece(
                    "excavation/corner_1",
                    DungeonWing.EXCAVATION,
                    DungeonPieceCategory.CORRIDOR,
                    8,
                    0,
                    0,
                    100,
                    true
            );

    public static final DungeonPieceDefinition EXCAVATION_JUNCTION =
            piece(
                    "excavation/junction_1",
                    DungeonWing.EXCAVATION,
                    DungeonPieceCategory.JUNCTION,
                    5,
                    4,
                    2,
                    100,
                    false
            );

    public static final DungeonPieceDefinition EXCAVATION_CAVERN =
            piece(
                    "excavation/cavern_1",
                    DungeonWing.EXCAVATION,
                    DungeonPieceCategory.ROOM,
                    4,
                    3,
                    3,
                    100,
                    true
            );

    public static final DungeonPieceDefinition EXCAVATION_COMBAT =
            piece(
                    "excavation/combat_1",
                    DungeonWing.EXCAVATION,
                    DungeonPieceCategory.COMBAT,
                    3,
                    3,
                    4,
                    100,
                    false
            );

    public static final DungeonPieceDefinition EXCAVATION_LOOT =
            piece(
                    "excavation/loot_1",
                    DungeonWing.EXCAVATION,
                    DungeonPieceCategory.LOOT,
                    1,
                    0,
                    0,
                    100,
                    false
            );

    public static final DungeonPiecePool EXCAVATION_POOL =
            DungeonPiecePool.of(
                    EXCAVATION_TUNNEL_SHORT,
                    EXCAVATION_TUNNEL_LONG,
                    EXCAVATION_CORNER,
                    EXCAVATION_JUNCTION,
                    EXCAVATION_CAVERN,
                    EXCAVATION_COMBAT
            );

    // -------------------------------------------------------------------------
    // FINAL SECTION
    // -------------------------------------------------------------------------

    public static final DungeonPieceDefinition FINAL_CORRIDOR =
            piece(
                    "final/corridor_1",
                    DungeonWing.FINAL,
                    DungeonPieceCategory.CORRIDOR,
                    10,
                    0,
                    0,
                    100,
                    true
            );

    public static final DungeonPieceDefinition FINAL_CORNER =
            piece(
                    "final/corner_1",
                    DungeonWing.FINAL,
                    DungeonPieceCategory.CORRIDOR,
                    6,
                    0,
                    0,
                    100,
                    true
            );

    public static final DungeonPieceDefinition FINAL_COMBAT =
            piece(
                    "final/combat_1",
                    DungeonWing.FINAL,
                    DungeonPieceCategory.COMBAT,
                    5,
                    3,
                    1,
                    100,
                    false
            );

    public static final DungeonPiecePool FINAL_POOL =
            DungeonPiecePool.of(
                    FINAL_CORRIDOR,
                    FINAL_CORNER,
                    FINAL_COMBAT
            );

    // -------------------------------------------------------------------------
    // TERMINATORS
    // -------------------------------------------------------------------------

    public static final DungeonPieceDefinition TERMINATOR_STANDARD =
            piece(
                    "common/terminator_standard_1",
                    DungeonWing.COMMON,
                    DungeonPieceCategory.TERMINATOR,
                    10,
                    0,
                    0,
                    100,
                    true
            );

    public static final DungeonPieceDefinition TERMINATOR_LARGE =
            piece(
                    "common/terminator_large_1",
                    DungeonWing.COMMON,
                    DungeonPieceCategory.TERMINATOR,
                    10,
                    0,
                    0,
                    100,
                    true
            );

    public static final DungeonPieceDefinition TERMINATOR_MAINTENANCE =
            piece(
                    "common/terminator_maintenance_1",
                    DungeonWing.COMMON,
                    DungeonPieceCategory.TERMINATOR,
                    10,
                    0,
                    0,
                    100,
                    true
            );

    public static final DungeonPiecePool TERMINATOR_POOL =
            DungeonPiecePool.of(
                    TERMINATOR_STANDARD,
                    TERMINATOR_LARGE,
                    TERMINATOR_MAINTENANCE
            );

    public static DungeonPiecePool poolFor(DungeonWing wing) {
        return switch (wing) {
            case SECURITY -> SECURITY_POOL;
            case REACTOR -> REACTOR_POOL;
            case EXCAVATION -> EXCAVATION_POOL;
            case FINAL -> FINAL_POOL;
            case COMMON -> throw new IllegalArgumentException(
                    "COMMON has no normal dungeon generation pool"
            );
        };
    }

    public static DungeonPieceDefinition objectiveFor(DungeonWing wing) {
        return switch (wing) {
            case SECURITY -> SECURITY_OBJECTIVE;
            case REACTOR -> REACTOR_OBJECTIVE;
            case EXCAVATION -> EXCAVATION_OBJECTIVE;
            default -> throw new IllegalArgumentException(
                    wing + " does not have a normal wing objective"
            );
        };
    }

    public static DungeonPieceDefinition lootFor(DungeonWing wing) {
        return switch (wing) {
            case SECURITY -> SECURITY_LOOT;
            case REACTOR -> REACTOR_LOOT;
            case EXCAVATION -> EXCAVATION_LOOT;
            default -> throw new IllegalArgumentException(
                    wing + " does not have a normal wing loot room"
            );
        };
    }

    private static DungeonPieceDefinition landmark(
            String path,
            DungeonWing wing
    ) {
        return new DungeonPieceDefinition(
                Constant.id("moon_dungeon/" + path),
                wing,
                DungeonPieceCategory.LANDMARK,
                1,
                1,
                0,
                Integer.MAX_VALUE,
                true
        );
    }

    private static DungeonPieceDefinition piece(
            String path,
            DungeonWing wing,
            DungeonPieceCategory category,
            int weight,
            int maximum,
            int minimumDepth,
            int maximumDepth,
            boolean allowConsecutive
    ) {
        return new DungeonPieceDefinition(
                Constant.id("moon_dungeon/" + path),
                wing,
                category,
                weight,
                maximum,
                minimumDepth,
                maximumDepth,
                allowConsecutive
        );
    }
}