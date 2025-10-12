package dev.galacticraft.mod.world.gen.dungeon.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DungeonConfig(
        int maxRooms, // Maximum rooms of dungeon
        int minRooms, // Minimum rooms of dungeon
        int criticalPaths, // Dungeon critical path amount
        float treasureRooms, // Percentage of rooms that should be treasure rooms
        float criticalPathRooms // Percentage of rooms that should be part of critical paths

) {
    public static final Codec<DungeonConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("max_rooms").forGetter(DungeonConfig::maxRooms),
            Codec.INT.fieldOf("min_rooms").forGetter(DungeonConfig::minRooms),
            Codec.INT.fieldOf("critical_paths").forGetter(DungeonConfig::criticalPaths),
            Codec.FLOAT.fieldOf("treasure_rooms").forGetter(DungeonConfig::treasureRooms),
            Codec.FLOAT.fieldOf("critical_path_rooms").forGetter(DungeonConfig::criticalPathRooms)
    ).apply(i, DungeonConfig::new));
}