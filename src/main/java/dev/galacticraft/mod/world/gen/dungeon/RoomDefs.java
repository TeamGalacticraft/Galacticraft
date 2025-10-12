package dev.galacticraft.mod.world.gen.dungeon;

import dev.galacticraft.mod.Constant;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Random;

public final class RoomDefs {
    public static final List<ResourceLocation> END_VARIANTS = List.of(
            Constant.id("dungeon/end_room_1")
    );
    public static final List<RoomTemplateDef> QUEEN = List.of(
            new RoomTemplateDef(Constant.id("dungeon/queen_room_1"), RoomTemplateDef.RoomType.QUEEN, 6, 1),
            new RoomTemplateDef(Constant.id("dungeon/queen_room_2"), RoomTemplateDef.RoomType.QUEEN, 6, 1)
    );
    public static final RoomTemplateDef ENTRANCE = new RoomTemplateDef(
            Constant.id("dungeon/entrance_shaft"),
            RoomTemplateDef.RoomType.ENTRANCE,
            0, /* cost */
            1  /* weight */
    );
    public static final List<RoomTemplateDef> BASIC = List.of(
            new RoomTemplateDef(Constant.id("dungeon/room_1"), RoomTemplateDef.RoomType.BASIC, 1, 10),
            new RoomTemplateDef(Constant.id("dungeon/room_2"), RoomTemplateDef.RoomType.BASIC, 1, 10),
            new RoomTemplateDef(Constant.id("dungeon/room_3"), RoomTemplateDef.RoomType.BASIC, 1, 10),
            new RoomTemplateDef(Constant.id("dungeon/room_4"), RoomTemplateDef.RoomType.BASIC, 1, 10),
            new RoomTemplateDef(Constant.id("dungeon/room_5"), RoomTemplateDef.RoomType.BASIC, 1, 10),
            new RoomTemplateDef(Constant.id("dungeon/room_6"), RoomTemplateDef.RoomType.BASIC, 1, 10)
            // add more BASIC rooms (just IDs / cost / weight)
    );
    public static final List<RoomTemplateDef> BRANCH_END = List.of(
            new RoomTemplateDef(Constant.id("dungeon/treasure_room_1"), RoomTemplateDef.RoomType.BRANCH_END, 4, 2),
            new RoomTemplateDef(Constant.id("dungeon/treasure_room_2"), RoomTemplateDef.RoomType.BRANCH_END, 4, 2)
    );

    private RoomDefs() {
    }

    public static RoomTemplateDef pickEnd(Random r) {
        var id = END_VARIANTS.get(r.nextInt(END_VARIANTS.size()));
        return new RoomTemplateDef(id, RoomTemplateDef.RoomType.END, 10, 1);
    }
}