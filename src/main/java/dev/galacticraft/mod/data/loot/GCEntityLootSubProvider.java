/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.data.loot;

import com.google.common.collect.Sets;
import dev.galacticraft.mod.content.GCRegistry;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class GCEntityLootSubProvider extends EntityLootSubProvider {
    protected final FeatureFlagSet allowed;
    protected final FeatureFlagSet required;

    protected GCEntityLootSubProvider(FeatureFlagSet allowed) {
        this(allowed, allowed);
    }

    protected GCEntityLootSubProvider(FeatureFlagSet allowed, FeatureFlagSet required) {
        super(allowed, required);
        this.allowed = allowed;
        this.required = required;
    }

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
        this.generate();
        Set<ResourceLocation> set = Sets.newHashSet();
        getRegistry().getEntries()
                .forEach(
                        entityType -> {
                            EntityType<?> entityType2 = entityType.value();
                            if (entityType2.isEnabled(this.allowed)) {
                                if (canHaveLootTable(entityType2)) {
                                    Map<ResourceLocation, LootTable.Builder> map = this.map.remove(entityType2);
                                    ResourceLocation resourceLocation = entityType2.getDefaultLootTable();
                                    if (!resourceLocation.equals(BuiltInLootTables.EMPTY) && entityType2.isEnabled(this.required) && (map == null || !map.containsKey(resourceLocation))) {
                                        throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", resourceLocation, entityType.key().location()));
                                    }

                                    if (map != null) {
                                        map.forEach((lootTableId, lootTableBuilder) -> {
                                            if (!set.add(lootTableId)) {
                                                throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", lootTableId, entityType.key().location()));
                                            } else {
                                                biConsumer.accept(lootTableId, lootTableBuilder);
                                            }
                                        });
                                    }
                                } else {
                                    Map<ResourceLocation, LootTable.Builder> map = this.map.remove(entityType2);
                                    if (map != null) {
                                        throw new IllegalStateException(
                                                String.format(
                                                        Locale.ROOT,
                                                        "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot",
                                                        map.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(",")),
                                                        entityType.key().location()
                                                )
                                        );
                                    }
                                }
                            }
                        }
                );
        if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + this.map.keySet());
        }
    }

    public LootTable.Builder noDrops() {
        return LootTable.lootTable();
    }

    protected abstract GCRegistry<EntityType<?>> getRegistry();

    protected boolean canHaveLootTable(EntityType<?> entityType) {
        return SPECIAL_LOOT_TABLE_TYPES.contains(entityType) || entityType.getCategory() != MobCategory.MISC;
    }
}
