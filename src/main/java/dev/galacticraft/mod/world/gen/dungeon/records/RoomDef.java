package dev.galacticraft.mod.world.gen.dungeon.records;

import dev.galacticraft.mod.world.gen.dungeon.enums.RoomType;

public record RoomDef(
        String id,              // namespaced id: "galacticraft:dungeon/room_4"
        RoomType type,
        int weight,             // for weighted random in pools
        String template,        // structure/template name to scan
        String[] tags,          // arbitrary labels: ["stone","small","round"]
        Constraints constraints,// Fast lookup arrays – precomputed during scan/registry load:
        PortDef[] entrances,
        PortDef[] exits,
        int sizeX, int sizeY, int sizeZ // from template scanner
) {
    public boolean hasAtLeastOneEntrance() {
        return entrances != null && entrances.length > 0;
    }

    public boolean hasAtLeastOneExit() {
        return exits != null && exits.length > 0;
    }

    public boolean hasMoreThanOneExit() {
        return exits != null && exits.length > 1;
    }
}