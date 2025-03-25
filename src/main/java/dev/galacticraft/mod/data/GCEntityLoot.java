/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.GCRegistry;
import dev.galacticraft.mod.data.loot.GCEntityLootSubProvider;
import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class GCEntityLoot extends GCEntityLootSubProvider {
    private final HolderLookup.Provider lookup;

    protected GCEntityLoot(HolderLookup.Provider lookup) {
        super(FeatureFlags.REGISTRY.allFlags(), lookup);
        this.lookup = lookup;
    }

    @Override
    public void generate() {
        add(GCEntityTypes.EVOLVED_ZOMBIE,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(Items.ROTTEN_FLESH)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(Items.IRON_INGOT))
                                        .add(LootItem.lootTableItem(Items.CARROT))
                                        .add(
                                                LootItem.lootTableItem(Items.POTATO)
                                                        .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                                        )
                                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                                        .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.lookup, 0.025F, 0.01F))
                        )
        );
        add(GCEntityTypes.EVOLVED_CREEPER,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(Items.GUNPOWDER)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .add(TagEntry.expandTag(GCTags.EVOLVED_CREEPER_DROP_MUSIC_DISCS))
                                        .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity().of(EntityTypeTags.SKELETONS)))
                        )
        );
        add(GCEntityTypes.EVOLVED_SKELETON,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(Items.ARROW)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(Items.BONE)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                        )
        );
        add(GCEntityTypes.EVOLVED_SPIDER,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(Items.STRING)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(Items.SPIDER_EYE)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(-1.0F, 1.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        )
        );
        add(GCEntityTypes.EVOLVED_ENDERMAN,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(Items.ENDER_PEARL)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                        )
        );
        add(GCEntityTypes.EVOLVED_WITCH,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(UniformGenerator.between(0.0F, 1.0F))
                                        .add(
                                                LootItem.lootTableItem(Items.GLOWSTONE_DUST)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                                        .add(
                                                LootItem.lootTableItem(Items.SUGAR)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                                        .add(
                                                LootItem.lootTableItem(Items.SPIDER_EYE)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                                        .add(
                                                LootItem.lootTableItem(Items.GLASS_BOTTLE)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                                        .add(
                                                LootItem.lootTableItem(Items.GUNPOWDER)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                                        .add(
                                                LootItem.lootTableItem(Items.STICK)
                                                        .setWeight(2)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(Items.REDSTONE)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 8.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                        )
        );
        add(GCEntityTypes.EVOLVED_PILLAGER, noDrops());
        add(GCEntityTypes.EVOLVED_EVOKER,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Items.TOTEM_OF_UNDYING)))
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(Items.EMERALD)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        )
        );
        add(GCEntityTypes.EVOLVED_VINDICATOR,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(Items.EMERALD)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.lookup, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        )
        );
        add(GCEntityTypes.GAZER, noDrops());
        add(GCEntityTypes.RUMBLER, noDrops());
        add(GCEntityTypes.COMET_CUBE, noDrops());
        add(GCEntityTypes.OLI_GRUB, noDrops());
        add(GCEntityTypes.GREY, noDrops());
        add(GCEntityTypes.ARCH_GREY, noDrops());

        add(GCEntityTypes.SKELETON_BOSS,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(Items.ARROW)) // Replace with key
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(Items.WITHER_SKELETON_SKULL))
                        )
        );
    }

    @Override
    protected GCRegistry<EntityType<?>> getRegistry() {
        return GCEntityTypes.ENTITIES;
    }
}
