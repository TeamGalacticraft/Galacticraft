package dev.galacticraft.mod.structure.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.Locale;
import java.util.Objects;

public record DungeonConnector(
        BlockPos localPosition,
        Direction direction,
        DungeonConnectorType type,
        String name
) {
    public static final String PREFIX = "gc_connector:";

    public DungeonConnector {
        Objects.requireNonNull(localPosition, "localPosition");
        Objects.requireNonNull(direction, "direction");
        Objects.requireNonNull(type, "type");

        name = name == null ? "" : name.toLowerCase(Locale.ROOT);
    }

    public boolean isNamed(String name) {
        return this.name.equalsIgnoreCase(name);
    }

    public boolean isNamed() {
        return !this.name.isEmpty();
    }

    /**
     * Parses:
     *
     * gc_connector:standard:north
     * gc_connector:standard:north:name
     */
    public static DungeonConnector parse(BlockPos position, String metadata) {
        if (!metadata.startsWith(PREFIX)) {
            throw new IllegalArgumentException(
                    "Not a dungeon connector marker: " + metadata
            );
        }

        String[] parts = metadata.split(":", 4);

        if (parts.length < 3) {
            throw new IllegalArgumentException(
                    "Invalid dungeon connector marker: " + metadata
            );
        }

        DungeonConnectorType type = DungeonConnectorType.fromId(parts[1]);

        Direction direction = Direction.byName(parts[2]);
        if (direction == null) {
            throw new IllegalArgumentException(
                    "Unknown connector direction '" + parts[2] + "' in " + metadata
            );
        }

        String name = parts.length >= 4 ? parts[3] : "";

        return new DungeonConnector(
                position.immutable(),
                direction,
                type,
                name
        );
    }
}