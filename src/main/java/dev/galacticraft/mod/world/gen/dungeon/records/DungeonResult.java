package dev.galacticraft.mod.world.gen.dungeon.records;

import net.minecraft.core.SectionPos;

import java.util.HashMap;
import java.util.List;

public record DungeonResult(HashMap<SectionPos, List<BlockData>> blockData) {
}
