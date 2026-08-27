package dev.galacticraft.mod.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class GCLootTableProvider {
    public static LootTableProvider create(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> lookup
    ) {
        return new LootTableProvider(
                output,
                Set.of(),
                List.of(
                        new LootTableProvider.SubProviderEntry(
                                GCEntityLoot::new,
                                LootContextParamSets.ENTITY
                        )
                ),
                lookup
        );
    }
}