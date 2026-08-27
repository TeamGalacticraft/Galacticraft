package dev.galacticraft.mod.data;

import dev.galacticraft.mod.content.GCLootTables;
import dev.galacticraft.mod.content.item.GCItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class GCChestLootTableProvider extends SimpleFabricLootTableProvider {

    public GCChestLootTableProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookup
    ) {
        super(output, registryLookup, LootContextParamSets.CHEST);
    }

    @Override
    public void generate(
            BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output
    ) {
        output.accept(
                GCLootTables.BASIC_MOON_RUINS_CHEST,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(UniformGenerator.between(2.0F, 4.0F))
                                        .add(
                                                LootItem.lootTableItem(GCItems.METEORIC_IRON_NUGGET)
                                                        .setWeight(10)
                                                        .apply(
                                                                SetItemCountFunction.setCount(
                                                                        UniformGenerator.between(1.0F, 4.0F)
                                                                )
                                                        )
                                        )
                                        .add(
                                                LootItem.lootTableItem(GCItems.METEORIC_IRON_INGOT)
                                                        .setWeight(2)
                                                        .apply(
                                                                SetItemCountFunction.setCount(
                                                                        UniformGenerator.between(1.0F, 3.0F)
                                                                )
                                                        )
                                        )
                                        .add(
                                                LootItem.lootTableItem(GCItems.ADVANCED_WAFER)
                                                        .setWeight(1)
                                        )
                        )
        );
    }
}