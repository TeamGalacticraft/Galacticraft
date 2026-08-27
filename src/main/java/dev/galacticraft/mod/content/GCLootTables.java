package dev.galacticraft.mod.content;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public final class GCLootTables {
    public static final ResourceKey<LootTable> BASIC_MOON_RUINS_CHEST = create(Constant.LootTable.BASIC_MOON_RUINS_CHEST);

    public static final ResourceKey<LootTable> MOON_DUNGEON_BASIC_CHEST = create(Constant.LootTable.MOON_DUNGEON_BASIC_CHEST);

    public static final ResourceKey<LootTable> MOON_DUNGEON_RARE_CHEST = create(Constant.LootTable.MOON_DUNGEON_RARE_CHEST);

    public static final ResourceKey<LootTable> MOON_DUNGEON_TREASURE_CHEST = create(Constant.LootTable.MOON_DUNGEON_TREASURE_CHEST);

    private GCLootTables() {
    }

    private static ResourceKey<LootTable> create(String path) {
        return Constant.key(
                Registries.LOOT_TABLE,
                path
        );
    }
}