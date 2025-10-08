package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DungeonConfig(
        int minPoints,
        int maxPoints,
        int sphereRadiusMin,
        int sphereRadiusMax,
        int corridorsMaxTurns,
        int corridorStep,       // blocks per step for corridor carving (1-3 is fine)
        boolean carveEntranceShaft
) {
    public static final Codec<DungeonConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("min_points").forGetter(DungeonConfig::minPoints),
            Codec.INT.fieldOf("max_points").forGetter(DungeonConfig::maxPoints),
            Codec.INT.fieldOf("sphere_radius_min").forGetter(DungeonConfig::sphereRadiusMin),
            Codec.INT.fieldOf("sphere_radius_max").forGetter(DungeonConfig::sphereRadiusMax),
            Codec.INT.fieldOf("corridors_max_turns").forGetter(DungeonConfig::corridorsMaxTurns),
            Codec.INT.fieldOf("corridor_step").forGetter(DungeonConfig::corridorStep),
            Codec.BOOL.fieldOf("carve_entrance_shaft").forGetter(DungeonConfig::carveEntranceShaft)
    ).apply(i, DungeonConfig::new));
}