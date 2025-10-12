package dev.galacticraft.mod.world.gen.dungeon;

import dev.galacticraft.mod.world.gen.dungeon.util.RoomRegistry;

public final class RoomDefsBootstrap {
    private RoomDefsBootstrap() {}

    public static void define(RoomRegistry.Registrar r) {
        // ENTRANCES
        r.entrance(id("entrance_1"), templateId("entrance_shaft"))
                .tags("start")
                .weight(1)
                .register();

        // END
        r.end(id("end_1"), templateId("end_room_1"))
                .tags("boss","goal")
                .register();

        // BASICS
        r.basic(id("room_1"), templateId("room_1"))
                .weight(1).tags("small").register();

        r.basic(id("room_2"), templateId("room_2"))
                .weight(1).tags("small").register();

        r.basic(id("room_3"), templateId("room_3"))
                .weight(1).tags("small").register();

        r.basic(id("room_4"), templateId("room_4"))
                .weight(1).tags("small").register();

        r.basic(id("room_5"), templateId("room_5"))
                .weight(1).tags("small").register();

        r.basic(id("room_6"), templateId("room_6"))
                .weight(1).tags("small").register();

        // QUEENS
        r.queen(id("queen_1"), templateId("queen_room_1"))
                .weight(1).tags("queen").register();

        r.queen(id("queen_2"), templateId("queen_room_2"))
                .weight(1).tags("queen").register();

        // TREASURE (entrances only)
        r.treasure(id("treasure_1"), templateId("treasure_room_1"))
                .weight(1).tags("loot").register();

        r.treasure(id("treasure_2"), templateId("treasure_room_2"))
                .weight(1).tags("loot").register();
    }

    private static String templateId(String value) {
        return "galacticraft:dungeon/" + value;
    }

    private static String id(String value) {
        return "galacticraft:dungeon/" + value;
    }
}