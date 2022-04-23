/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.mod.item.GalacticraftItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.StatePredicate;

import static dev.galacticraft.mod.block.GalacticraftBlock.*;

public class GalacticraftBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected GalacticraftBlockLootTableProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateBlockLootTables() {
        addDrop(GLOWSTONE_TORCH, GalacticraftItem.GLOWSTONE_TORCH);
        addDrop(GLOWSTONE_WALL_TORCH, GalacticraftItem.GLOWSTONE_TORCH);
        addDrop(UNLIT_TORCH, GalacticraftItem.UNLIT_TORCH);
        addDrop(UNLIT_WALL_TORCH, GalacticraftItem.UNLIT_TORCH);
        
        addDrop(GLOWSTONE_LANTERN);
        addDrop(UNLIT_LANTERN, Items.LANTERN);

        addDrop(ALUMINUM_DECORATION);
        addDrop(ALUMINUM_DECORATION_SLAB);
        addDrop(ALUMINUM_DECORATION_STAIRS);
        addDrop(ALUMINUM_DECORATION_WALL);
        addDrop(DETAILED_ALUMINUM_DECORATION);
        addDrop(DETAILED_ALUMINUM_DECORATION_SLAB);
        addDrop(DETAILED_ALUMINUM_DECORATION_STAIRS);
        addDrop(DETAILED_ALUMINUM_DECORATION_WALL);

        addDrop(BRONZE_DECORATION);
        addDrop(BRONZE_DECORATION_SLAB);
        addDrop(BRONZE_DECORATION_STAIRS);
        addDrop(BRONZE_DECORATION_WALL);
        addDrop(DETAILED_BRONZE_DECORATION);
        addDrop(DETAILED_BRONZE_DECORATION_SLAB);
        addDrop(DETAILED_BRONZE_DECORATION_STAIRS);
        addDrop(DETAILED_BRONZE_DECORATION_WALL);

        addDrop(COPPER_DECORATION);
        addDrop(COPPER_DECORATION_SLAB);
        addDrop(COPPER_DECORATION_STAIRS);
        addDrop(COPPER_DECORATION_WALL);
        addDrop(DETAILED_COPPER_DECORATION);
        addDrop(DETAILED_COPPER_DECORATION_SLAB);
        addDrop(DETAILED_COPPER_DECORATION_STAIRS);
        addDrop(DETAILED_COPPER_DECORATION_WALL);

        addDrop(IRON_DECORATION);
        addDrop(IRON_DECORATION_SLAB);
        addDrop(IRON_DECORATION_STAIRS);
        addDrop(IRON_DECORATION_WALL);
        addDrop(DETAILED_IRON_DECORATION);
        addDrop(DETAILED_IRON_DECORATION_SLAB);
        addDrop(DETAILED_IRON_DECORATION_STAIRS);
        addDrop(DETAILED_IRON_DECORATION_WALL);

        addDrop(METEORIC_IRON_DECORATION);
        addDrop(METEORIC_IRON_DECORATION_SLAB);
        addDrop(METEORIC_IRON_DECORATION_STAIRS);
        addDrop(METEORIC_IRON_DECORATION_WALL);
        addDrop(DETAILED_METEORIC_IRON_DECORATION);
        addDrop(DETAILED_METEORIC_IRON_DECORATION_SLAB);
        addDrop(DETAILED_METEORIC_IRON_DECORATION_STAIRS);
        addDrop(DETAILED_METEORIC_IRON_DECORATION_WALL);

        addDrop(STEEL_DECORATION);
        addDrop(STEEL_DECORATION_SLAB);
        addDrop(STEEL_DECORATION_STAIRS);
        addDrop(STEEL_DECORATION_WALL);
        addDrop(DETAILED_STEEL_DECORATION);
        addDrop(DETAILED_STEEL_DECORATION_SLAB);
        addDrop(DETAILED_STEEL_DECORATION_STAIRS);
        addDrop(DETAILED_STEEL_DECORATION_WALL);

        addDrop(TIN_DECORATION);
        addDrop(TIN_DECORATION_SLAB);
        addDrop(TIN_DECORATION_STAIRS);
        addDrop(TIN_DECORATION_WALL);
        addDrop(DETAILED_TIN_DECORATION);
        addDrop(DETAILED_TIN_DECORATION_SLAB);
        addDrop(DETAILED_TIN_DECORATION_STAIRS);
        addDrop(DETAILED_TIN_DECORATION_WALL);

        addDrop(TITANIUM_DECORATION);
        addDrop(TITANIUM_DECORATION_SLAB);
        addDrop(TITANIUM_DECORATION_STAIRS);
        addDrop(TITANIUM_DECORATION_WALL);
        addDrop(DETAILED_TITANIUM_DECORATION);
        addDrop(DETAILED_TITANIUM_DECORATION_SLAB);
        addDrop(DETAILED_TITANIUM_DECORATION_STAIRS);
        addDrop(DETAILED_TITANIUM_DECORATION_WALL);

        addDrop(DARK_DECORATION);
        addDrop(DARK_DECORATION_SLAB);
        addDrop(DARK_DECORATION_STAIRS);
        addDrop(DARK_DECORATION_WALL);
        addDrop(DETAILED_DARK_DECORATION);
        addDrop(DETAILED_DARK_DECORATION_SLAB);
        addDrop(DETAILED_DARK_DECORATION_STAIRS);
        addDrop(DETAILED_DARK_DECORATION_WALL);

        addDrop(MOON_TURF);
        addDrop(MOON_DIRT);
        addDrop(MOON_DIRT_PATH, GalacticraftItem.MOON_DIRT);
        addDrop(MOON_SURFACE_ROCK);

        addDrop(MOON_ROCK, drops(MOON_ROCK, COBBLED_MOON_ROCK));
        addDrop(MOON_ROCK_SLAB);
        addDrop(MOON_ROCK_STAIRS);
        addDrop(MOON_ROCK_WALL);

        addDrop(COBBLED_MOON_ROCK);
        addDrop(COBBLED_MOON_ROCK_SLAB);
        addDrop(COBBLED_MOON_ROCK_STAIRS);
        addDrop(COBBLED_MOON_ROCK_WALL);

        addDrop(LUNASLATE, drops(LUNASLATE, COBBLED_LUNASLATE));
        addDrop(LUNASLATE_SLAB);
        addDrop(LUNASLATE_STAIRS);
        addDrop(LUNASLATE_WALL);

        addDrop(COBBLED_LUNASLATE);
        addDrop(COBBLED_LUNASLATE_SLAB);
        addDrop(COBBLED_LUNASLATE_STAIRS);
        addDrop(COBBLED_LUNASLATE_WALL);

        addDrop(MOON_BASALT);
        addDrop(MOON_BASALT_SLAB);
        addDrop(MOON_BASALT_STAIRS);
        addDrop(MOON_BASALT_WALL);

        addDrop(MOON_BASALT_BRICK);
        addDrop(MOON_BASALT_BRICK_SLAB);
        addDrop(MOON_BASALT_BRICK_STAIRS);
        addDrop(MOON_BASALT_BRICK_WALL);

        addDrop(CRACKED_MOON_BASALT_BRICK);
        addDrop(CRACKED_MOON_BASALT_BRICK_SLAB);
        addDrop(CRACKED_MOON_BASALT_BRICK_STAIRS);
        addDrop(CRACKED_MOON_BASALT_BRICK_WALL);

        addDrop(MARS_SURFACE_ROCK);
        addDrop(MARS_SUB_SURFACE_ROCK);
        addDrop(MARS_STONE, drops(MARS_STONE, MARS_COBBLESTONE));
        addDrop(MARS_COBBLESTONE);
        addDrop(MARS_COBBLESTONE_SLAB);
        addDrop(MARS_COBBLESTONE_STAIRS);
        addDrop(MARS_COBBLESTONE_WALL);

        addDrop(ASTEROID_ROCK);
        addDrop(ASTEROID_ROCK_1);
        addDrop(ASTEROID_ROCK_2);

        addDrop(SOFT_VENUS_ROCK);
        addDrop(HARD_VENUS_ROCK);
        addDrop(SCORCHED_VENUS_ROCK);
        addDrop(VOLCANIC_ROCK, dropsWithSilkTouch(VOLCANIC_ROCK));
        addDrop(PUMICE);
        addDropWithSilkTouch(VAPOR_SPOUT);

        addDrop(WALKWAY);
        addDrop(PIPE_WALKWAY);
        addDrop(WIRE_WALKWAY);
        addDrop(TIN_LADDER);
        addDrop(GRATING);

        addDrop(ALUMINUM_WIRE);
        addDrop(SEALABLE_ALUMINUM_WIRE);
        addDrop(HEAVY_SEALABLE_ALUMINUM_WIRE);
        addDrop(GLASS_FLUID_PIPE);

        addDrop(SQUARE_LIGHT_PANEL);
        addDrop(SPOTLIGHT_LIGHT_PANEL);
        addDrop(LINEAR_LIGHT_PANEL);
        addDrop(DASHED_LIGHT_PANEL);
        addDrop(DIAGONAL_LIGHT_PANEL);

        addDrop(VACUUM_GLASS);
        addDrop(CLEAR_VACUUM_GLASS);
        addDrop(STRONG_VACUUM_GLASS);

        addDrop(SILICON_ORE, siliconOreDrops(SILICON_ORE));
        addDrop(DEEPSLATE_SILICON_ORE, siliconOreDrops(DEEPSLATE_SILICON_ORE));
        addDrop(MOON_COPPER_ORE, copperOreDrops(MOON_COPPER_ORE));
        addDrop(LUNASLATE_COPPER_ORE, copperOreDrops(MOON_COPPER_ORE));

        addDrop(TIN_ORE, oreDrops(TIN_ORE, GalacticraftItem.RAW_TIN));
        addDrop(DEEPSLATE_TIN_ORE, oreDrops(DEEPSLATE_TIN_ORE, GalacticraftItem.RAW_TIN));
        addDrop(MOON_TIN_ORE, oreDrops(MOON_TIN_ORE, GalacticraftItem.RAW_TIN));
        addDrop(LUNASLATE_TIN_ORE, oreDrops(LUNASLATE_TIN_ORE, GalacticraftItem.RAW_TIN));

        addDrop(ALUMINUM_ORE, oreDrops(ALUMINUM_ORE, GalacticraftItem.RAW_ALUMINUM));
        addDrop(DEEPSLATE_ALUMINUM_ORE, oreDrops(ALUMINUM_ORE, GalacticraftItem.RAW_ALUMINUM));

        addDrop(DESH_ORE, oreDrops(DESH_ORE, GalacticraftItem.RAW_DESH));

        addDrop(ILMENITE_ORE, oreDrops(ILMENITE_ORE, GalacticraftItem.RAW_TITANIUM));

        addDrop(GALENA_ORE, oreDrops(GALENA_ORE, GalacticraftItem.RAW_LEAD));

        addDrop(MOON_CHEESE_BLOCK, dropsNothing());
        addDrop(SILICON_BLOCK);
        addDrop(METEORIC_IRON_BLOCK);
        addDrop(DESH_BLOCK);
        addDrop(TITANIUM_BLOCK);
        addDrop(LEAD_BLOCK);
        addDrop(LUNAR_SAPPHIRE_BLOCK);

        addDrop(LUNAR_CARTOGRAPHY_TABLE);

        addDrop(CAVERNOUS_VINE, BlockLootTableGenerator::dropsWithShears);
        addDrop(POISONOUS_CAVERNOUS_VINE, BlockLootTableGenerator::dropsWithShears);
        addDrop(
                MOON_BERRY_BUSH,
                blockx -> applyExplosionDecay(
                        blockx,
                        LootTable.builder()
                                .pool(
                                        LootPool.builder()
                                                .conditionally(
                                                        BlockStatePropertyLootCondition.builder(MOON_BERRY_BUSH).properties(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 3))
                                                )
                                                .with(ItemEntry.builder(GalacticraftItem.MOON_BERRIES))
                                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 3.0F)))
                                                .apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
                                )
                                .pool(
                                        LootPool.builder()
                                                .conditionally(
                                                        BlockStatePropertyLootCondition.builder(MOON_BERRY_BUSH).properties(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 2))
                                                )
                                                .with(ItemEntry.builder(GalacticraftItem.MOON_BERRIES))
                                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F)))
                                                .apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
                                )
                )
        );

        addDrop(CIRCUIT_FABRICATOR);
        addDrop(COMPRESSOR);
        addDrop(ELECTRIC_COMPRESSOR);
        addDrop(COAL_GENERATOR);
        addDrop(BASIC_SOLAR_PANEL);
        addDrop(ADVANCED_SOLAR_PANEL);
        addDrop(ENERGY_STORAGE_MODULE);
        addDrop(ELECTRIC_FURNACE);
        addDrop(ELECTRIC_ARC_FURNACE);
        addDrop(REFINERY);
        addDrop(OXYGEN_COLLECTOR);
        addDrop(OXYGEN_SEALER);
        addDrop(BUBBLE_DISTRIBUTOR);
        addDrop(OXYGEN_DECOMPRESSOR);
        addDrop(OXYGEN_COMPRESSOR);
        addDrop(OXYGEN_STORAGE_MODULE);
    }

    public static LootTable.Builder siliconOreDrops(Block ore) {
        return dropsWithSilkTouch(ore, applyExplosionDecay(ore, ItemEntry.builder(GalacticraftItem.RAW_SILICON)
                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 6.0F)))
                .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
        ));
    }
}
