/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import dev.galacticraft.mod.content.item.GCItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import static dev.galacticraft.mod.content.GCBlocks.*;

public class GCBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected GCBlockLootTableProvider(FabricDataOutput output) {
        super(output);
    }

    public LootTable.Builder siliconOreDrops(Block ore) {
        return createSilkTouchDispatchTable(ore, this.applyExplosionDecay(ore, LootItem.lootTableItem(GCItems.RAW_SILICON)
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 6.0F)))
                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
        ));
    }

    @Override
    public void generate() {
        this.dropSelf(GLOWSTONE_TORCH);
        this.dropSelf(UNLIT_TORCH);

        this.dropSelf(GLOWSTONE_LANTERN);
        this.dropSelf(UNLIT_LANTERN);

        this.dropSelf(ALUMINUM_DECORATION);
        this.dropSelf(ALUMINUM_DECORATION_SLAB);
        this.dropSelf(ALUMINUM_DECORATION_STAIRS);
        this.dropSelf(ALUMINUM_DECORATION_WALL);
        this.dropSelf(DETAILED_ALUMINUM_DECORATION);
        this.dropSelf(DETAILED_ALUMINUM_DECORATION_SLAB);
        this.dropSelf(DETAILED_ALUMINUM_DECORATION_STAIRS);
        this.dropSelf(DETAILED_ALUMINUM_DECORATION_WALL);

        this.dropSelf(BRONZE_DECORATION);
        this.dropSelf(BRONZE_DECORATION_SLAB);
        this.dropSelf(BRONZE_DECORATION_STAIRS);
        this.dropSelf(BRONZE_DECORATION_WALL);
        this.dropSelf(DETAILED_BRONZE_DECORATION);
        this.dropSelf(DETAILED_BRONZE_DECORATION_SLAB);
        this.dropSelf(DETAILED_BRONZE_DECORATION_STAIRS);
        this.dropSelf(DETAILED_BRONZE_DECORATION_WALL);

        this.dropSelf(COPPER_DECORATION);
        this.dropSelf(COPPER_DECORATION_SLAB);
        this.dropSelf(COPPER_DECORATION_STAIRS);
        this.dropSelf(COPPER_DECORATION_WALL);
        this.dropSelf(DETAILED_COPPER_DECORATION);
        this.dropSelf(DETAILED_COPPER_DECORATION_SLAB);
        this.dropSelf(DETAILED_COPPER_DECORATION_STAIRS);
        this.dropSelf(DETAILED_COPPER_DECORATION_WALL);

        this.dropSelf(IRON_DECORATION);
        this.dropSelf(IRON_DECORATION_SLAB);
        this.dropSelf(IRON_DECORATION_STAIRS);
        this.dropSelf(IRON_DECORATION_WALL);
        this.dropSelf(DETAILED_IRON_DECORATION);
        this.dropSelf(DETAILED_IRON_DECORATION_SLAB);
        this.dropSelf(DETAILED_IRON_DECORATION_STAIRS);
        this.dropSelf(DETAILED_IRON_DECORATION_WALL);

        this.dropSelf(METEORIC_IRON_DECORATION);
        this.dropSelf(METEORIC_IRON_DECORATION_SLAB);
        this.dropSelf(METEORIC_IRON_DECORATION_STAIRS);
        this.dropSelf(METEORIC_IRON_DECORATION_WALL);
        this.dropSelf(DETAILED_METEORIC_IRON_DECORATION);
        this.dropSelf(DETAILED_METEORIC_IRON_DECORATION_SLAB);
        this.dropSelf(DETAILED_METEORIC_IRON_DECORATION_STAIRS);
        this.dropSelf(DETAILED_METEORIC_IRON_DECORATION_WALL);

        this.dropSelf(STEEL_DECORATION);
        this.dropSelf(STEEL_DECORATION_SLAB);
        this.dropSelf(STEEL_DECORATION_STAIRS);
        this.dropSelf(STEEL_DECORATION_WALL);
        this.dropSelf(DETAILED_STEEL_DECORATION);
        this.dropSelf(DETAILED_STEEL_DECORATION_SLAB);
        this.dropSelf(DETAILED_STEEL_DECORATION_STAIRS);
        this.dropSelf(DETAILED_STEEL_DECORATION_WALL);

        this.dropSelf(TIN_DECORATION);
        this.dropSelf(TIN_DECORATION_SLAB);
        this.dropSelf(TIN_DECORATION_STAIRS);
        this.dropSelf(TIN_DECORATION_WALL);
        this.dropSelf(DETAILED_TIN_DECORATION);
        this.dropSelf(DETAILED_TIN_DECORATION_SLAB);
        this.dropSelf(DETAILED_TIN_DECORATION_STAIRS);
        this.dropSelf(DETAILED_TIN_DECORATION_WALL);

        this.dropSelf(TITANIUM_DECORATION);
        this.dropSelf(TITANIUM_DECORATION_SLAB);
        this.dropSelf(TITANIUM_DECORATION_STAIRS);
        this.dropSelf(TITANIUM_DECORATION_WALL);
        this.dropSelf(DETAILED_TITANIUM_DECORATION);
        this.dropSelf(DETAILED_TITANIUM_DECORATION_SLAB);
        this.dropSelf(DETAILED_TITANIUM_DECORATION_STAIRS);
        this.dropSelf(DETAILED_TITANIUM_DECORATION_WALL);

        this.dropSelf(DARK_DECORATION);
        this.dropSelf(DARK_DECORATION_SLAB);
        this.dropSelf(DARK_DECORATION_STAIRS);
        this.dropSelf(DARK_DECORATION_WALL);
        this.dropSelf(DETAILED_DARK_DECORATION);
        this.dropSelf(DETAILED_DARK_DECORATION_SLAB);
        this.dropSelf(DETAILED_DARK_DECORATION_STAIRS);
        this.dropSelf(DETAILED_DARK_DECORATION_WALL);

        this.dropSelf(MOON_TURF);
        this.dropSelf(MOON_DIRT);
        this.dropOther(MOON_DIRT_PATH, GCItems.MOON_DIRT);
        this.dropSelf(MOON_SURFACE_ROCK);

        this.add(MOON_ROCK, this.createSingleItemTableWithSilkTouch(MOON_ROCK, COBBLED_MOON_ROCK));
        this.dropSelf(MOON_ROCK_SLAB);
        this.dropSelf(MOON_ROCK_STAIRS);
        this.dropSelf(MOON_ROCK_WALL);

        this.dropSelf(COBBLED_MOON_ROCK);
        this.dropSelf(COBBLED_MOON_ROCK_SLAB);
        this.dropSelf(COBBLED_MOON_ROCK_STAIRS);
        this.dropSelf(COBBLED_MOON_ROCK_WALL);

        this.add(LUNASLATE, this.createSingleItemTableWithSilkTouch(LUNASLATE, COBBLED_LUNASLATE));
        this.dropSelf(LUNASLATE_SLAB);
        this.dropSelf(LUNASLATE_STAIRS);
        this.dropSelf(LUNASLATE_WALL);

        this.dropSelf(COBBLED_LUNASLATE);
        this.dropSelf(COBBLED_LUNASLATE_SLAB);
        this.dropSelf(COBBLED_LUNASLATE_STAIRS);
        this.dropSelf(COBBLED_LUNASLATE_WALL);

        this.dropSelf(MOON_BASALT);
        this.dropSelf(MOON_BASALT_SLAB);
        this.dropSelf(MOON_BASALT_STAIRS);
        this.dropSelf(MOON_BASALT_WALL);

        this.dropSelf(MOON_BASALT_BRICK);
        this.dropSelf(MOON_BASALT_BRICK_SLAB);
        this.dropSelf(MOON_BASALT_BRICK_STAIRS);
        this.dropSelf(MOON_BASALT_BRICK_WALL);

        this.dropSelf(CRACKED_MOON_BASALT_BRICK);
        this.dropSelf(CRACKED_MOON_BASALT_BRICK_SLAB);
        this.dropSelf(CRACKED_MOON_BASALT_BRICK_STAIRS);
        this.dropSelf(CRACKED_MOON_BASALT_BRICK_WALL);

        this.dropSelf(MARS_SURFACE_ROCK);
        this.dropSelf(MARS_SUB_SURFACE_ROCK);

        this.add(MARS_STONE, this.createSingleItemTableWithSilkTouch(MARS_STONE, MARS_COBBLESTONE));
        this.dropSelf(MARS_STONE_SLAB);
        this.dropSelf(MARS_STONE_STAIRS);
        this.dropSelf(MARS_STONE_WALL);

        this.dropSelf(MARS_COBBLESTONE);
        this.dropSelf(MARS_COBBLESTONE_SLAB);
        this.dropSelf(MARS_COBBLESTONE_STAIRS);
        this.dropSelf(MARS_COBBLESTONE_WALL);

        this.dropSelf(ASTEROID_ROCK);
        this.dropSelf(ASTEROID_ROCK_1);
        this.dropSelf(ASTEROID_ROCK_2);

        this.dropSelf(SOFT_VENUS_ROCK);
        this.dropSelf(HARD_VENUS_ROCK);
        this.dropSelf(SCORCHED_VENUS_ROCK);
        this.add(VOLCANIC_ROCK, createSilkTouchOnlyTable(VOLCANIC_ROCK));
        this.dropSelf(PUMICE);
        this.add(VAPOR_SPOUT, this.createSingleItemTableWithSilkTouch(VAPOR_SPOUT, SOFT_VENUS_ROCK));

        this.dropSelf(WALKWAY);
        this.dropSelf(PIPE_WALKWAY);
        this.dropSelf(WIRE_WALKWAY);
        this.dropSelf(TIN_LADDER);
        this.dropSelf(GRATING);

        this.dropSelf(ALUMINUM_WIRE);
        this.dropSelf(SEALABLE_ALUMINUM_WIRE);
        this.dropSelf(HEAVY_SEALABLE_ALUMINUM_WIRE);
        this.dropSelf(GLASS_FLUID_PIPE);

        this.dropSelf(SQUARE_LIGHT_PANEL);
        this.dropSelf(SPOTLIGHT_LIGHT_PANEL);
        this.dropSelf(LINEAR_LIGHT_PANEL);
        this.dropSelf(DASHED_LIGHT_PANEL);
        this.dropSelf(DIAGONAL_LIGHT_PANEL);

        this.dropSelf(VACUUM_GLASS);
        this.dropSelf(CLEAR_VACUUM_GLASS);
        this.dropSelf(STRONG_VACUUM_GLASS);

        this.add(SILICON_ORE, this.siliconOreDrops(SILICON_ORE));
        this.add(DEEPSLATE_SILICON_ORE, this.siliconOreDrops(DEEPSLATE_SILICON_ORE));
        this.add(MOON_COPPER_ORE, this.createCopperOreDrops(MOON_COPPER_ORE));
        this.add(LUNASLATE_COPPER_ORE, this.createCopperOreDrops(MOON_COPPER_ORE));

        this.add(TIN_ORE, this.createOreDrop(TIN_ORE, GCItems.RAW_TIN));
        this.add(DEEPSLATE_TIN_ORE, this.createOreDrop(DEEPSLATE_TIN_ORE, GCItems.RAW_TIN));
        this.add(MOON_TIN_ORE, this.createOreDrop(MOON_TIN_ORE, GCItems.RAW_TIN));
        this.add(LUNASLATE_TIN_ORE, this.createOreDrop(LUNASLATE_TIN_ORE, GCItems.RAW_TIN));

        this.add(ALUMINUM_ORE, this.createOreDrop(ALUMINUM_ORE, GCItems.RAW_ALUMINUM));
        this.add(DEEPSLATE_ALUMINUM_ORE, this.createOreDrop(ALUMINUM_ORE, GCItems.RAW_ALUMINUM));

        this.add(DESH_ORE, this.createOreDrop(DESH_ORE, GCItems.RAW_DESH));

        this.add(ILMENITE_ORE, this.createOreDrop(ILMENITE_ORE, GCItems.RAW_TITANIUM));

        this.add(GALENA_ORE, this.createOreDrop(GALENA_ORE, GCItems.RAW_LEAD));

        this.add(MOON_CHEESE_BLOCK, noDrop());
        this.add(CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.CANDLE));
        this.add(WHITE_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.WHITE_CANDLE));
        this.add(ORANGE_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.ORANGE_CANDLE));
        this.add(MAGENTA_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.MAGENTA_CANDLE));
        this.add(LIGHT_BLUE_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.LIGHT_BLUE_CANDLE));
        this.add(YELLOW_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.YELLOW_CANDLE));
        this.add(LIME_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.LIME_CANDLE));
        this.add(PINK_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.PINK_CANDLE));
        this.add(GRAY_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.GRAY_CANDLE));
        this.add(LIGHT_GRAY_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.LIGHT_GRAY_CANDLE));
        this.add(CYAN_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.CYAN_CANDLE));
        this.add(PURPLE_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.PURPLE_CANDLE));
        this.add(BLUE_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.BLUE_CANDLE));
        this.add(BROWN_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.BROWN_CANDLE));
        this.add(GREEN_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.GREEN_CANDLE));
        this.add(RED_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.RED_CANDLE));
        this.add(BLACK_CANDLE_MOON_CHEESE_BLOCK, createCandleCakeDrops(Blocks.BLACK_CANDLE));

        this.dropSelf(SILICON_BLOCK);
        this.dropSelf(METEORIC_IRON_BLOCK);
        this.dropSelf(DESH_BLOCK);
        this.dropSelf(TITANIUM_BLOCK);
        this.dropSelf(LEAD_BLOCK);
        this.dropSelf(LUNAR_SAPPHIRE_BLOCK);

        this.add(FALLEN_METEOR, block -> BlockLootSubProvider.createSilkTouchDispatchTable(block, this.applyExplosionDecay(block, LootItem.lootTableItem(GCItems.RAW_METEORIC_IRON).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))));

        this.dropSelf(LUNAR_CARTOGRAPHY_TABLE);

        this.add(CAVERNOUS_VINE, BlockLootSubProvider::createShearsOnlyDrop);
        this.add(POISONOUS_CAVERNOUS_VINE, BlockLootSubProvider::createShearsOnlyDrop);
        this.add(
                MOON_BERRY_BUSH,
                blockx -> this.applyExplosionDecay(
                        blockx,
                        LootTable.lootTable()
                                .withPool(
                                        LootPool.lootPool()
                                                .when(
                                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(MOON_BERRY_BUSH).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))
                                                )
                                                .add(LootItem.lootTableItem(GCItems.MOON_BERRIES))
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                                .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))
                                )
                                .withPool(
                                        LootPool.lootPool()
                                                .when(
                                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(MOON_BERRY_BUSH).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))
                                                )
                                                .add(LootItem.lootTableItem(GCItems.MOON_BERRIES))
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                                .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))
                                )
                )
        );

        //TODO Drop with 9 pads
        this.dropSelf(ROCKET_LAUNCH_PAD);
        this.dropSelf(AIR_LOCK_CONTROLLER);
        this.dropSelf(AIR_LOCK_FRAME);

        //TODO Fix part table
        this.dropOther(CRYOGENIC_CHAMBER_PART, CRYOGENIC_CHAMBER);

        this.dropSelf(CRYOGENIC_CHAMBER);
        this.dropSelf(CIRCUIT_FABRICATOR);
        this.dropSelf(COMPRESSOR);
        this.dropSelf(ELECTRIC_COMPRESSOR);
        this.dropSelf(COAL_GENERATOR);
        this.dropSelf(BASIC_SOLAR_PANEL);
        this.dropSelf(ADVANCED_SOLAR_PANEL);
        this.dropSelf(ENERGY_STORAGE_MODULE);
        this.dropSelf(ELECTRIC_FURNACE);
        this.dropSelf(ELECTRIC_ARC_FURNACE);
        this.dropSelf(REFINERY);
        this.dropSelf(OXYGEN_COLLECTOR);
        this.dropSelf(OXYGEN_SEALER);
        this.dropSelf(OXYGEN_BUBBLE_DISTRIBUTOR);
        this.dropSelf(OXYGEN_DECOMPRESSOR);
        this.dropSelf(OXYGEN_COMPRESSOR);
        this.dropSelf(OXYGEN_STORAGE_MODULE);
        this.dropSelf(FUEL_LOADER);

        this.dropSelf(PLAYER_TRANSPORT_TUBE);
        this.dropSelf(ROCKET_WORKBENCH);

        this.add(AIR_LOCK_SEAL, noDrop());
    }
}
