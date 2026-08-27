package dev.galacticraft.mod.structure.dungeon;

import java.util.Locale;

public enum DungeonConnectorType {
    STANDARD,
    LARGE,
    MAINTENANCE;

    public static DungeonConnectorType fromId(String id) {
        return DungeonConnectorType.valueOf(id.toUpperCase(Locale.ROOT));
    }
}