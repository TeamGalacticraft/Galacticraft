/*
 * Copyright (c) 2019-2026 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

        output.accept(
                GCLootTables.MOON_DUNGEON_TREASURE_CHEST,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(UniformGenerator.between(1.0F, 1.0F))
                                        .add(
                                                LootItem.lootTableItem(GCItems.TIER_2_ROCKET_SCHEMATIC)
                                                        .setWeight(1)
                                                        .apply(
                                                                SetItemCountFunction.setCount(
                                                                        UniformGenerator.between(1.0F, 1.0F)
                                                                )
                                                        )
                                        )
                        )
        );
    }
}