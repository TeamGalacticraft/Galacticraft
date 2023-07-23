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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
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

import java.util.function.BiConsumer;

import static dev.galacticraft.mod.content.GCBlocks.*;

public class GCBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected GCBlockLootTableProvider(FabricDataOutput output) {
        super(output);
    }

    public LootTable.Builder siliconOreDrops(Block ore) {
        return createSilkTouchDispatchTable(ore, applyExplosionDecay(ore, LootItem.lootTableItem(GCItems.RAW_SILICON)
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 6.0F)))
                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
        ));
    }

    @Override
    public void generate() {
        dropOther(GLOWSTONE_TORCH, GCItems.GLOWSTONE_TORCH);
        dropOther(GLOWSTONE_WALL_TORCH, GCItems.GLOWSTONE_TORCH);
        dropOther(UNLIT_TORCH, GCItems.UNLIT_TORCH);
        dropOther(UNLIT_WALL_TORCH, GCItems.UNLIT_TORCH);

        dropSelf(GLOWSTONE_LANTERN);
        dropSelf(UNLIT_LANTERN);

        dropSelf(ALUMINUM_DECORATION);
        dropSelf(ALUMINUM_DECORATION_SLAB);
        dropSelf(ALUMINUM_DECORATION_STAIRS);
        dropSelf(ALUMINUM_DECORATION_WALL);
        dropSelf(DETAILED_ALUMINUM_DECORATION);
        dropSelf(DETAILED_ALUMINUM_DECORATION_SLAB);
        dropSelf(DETAILED_ALUMINUM_DECORATION_STAIRS);
        dropSelf(DETAILED_ALUMINUM_DECORATION_WALL);

        dropSelf(BRONZE_DECORATION);
        dropSelf(BRONZE_DECORATION_SLAB);
        dropSelf(BRONZE_DECORATION_STAIRS);
        dropSelf(BRONZE_DECORATION_WALL);
        dropSelf(DETAILED_BRONZE_DECORATION);
        dropSelf(DETAILED_BRONZE_DECORATION_SLAB);
        dropSelf(DETAILED_BRONZE_DECORATION_STAIRS);
        dropSelf(DETAILED_BRONZE_DECORATION_WALL);

        dropSelf(COPPER_DECORATION);
        dropSelf(COPPER_DECORATION_SLAB);
        dropSelf(COPPER_DECORATION_STAIRS);
        dropSelf(COPPER_DECORATION_WALL);
        dropSelf(DETAILED_COPPER_DECORATION);
        dropSelf(DETAILED_COPPER_DECORATION_SLAB);
        dropSelf(DETAILED_COPPER_DECORATION_STAIRS);
        dropSelf(DETAILED_COPPER_DECORATION_WALL);

        dropSelf(IRON_DECORATION);
        dropSelf(IRON_DECORATION_SLAB);
        dropSelf(IRON_DECORATION_STAIRS);
        dropSelf(IRON_DECORATION_WALL);
        dropSelf(DETAILED_IRON_DECORATION);
        dropSelf(DETAILED_IRON_DECORATION_SLAB);
        dropSelf(DETAILED_IRON_DECORATION_STAIRS);
        dropSelf(DETAILED_IRON_DECORATION_WALL);

        dropSelf(METEORIC_IRON_DECORATION);
        dropSelf(METEORIC_IRON_DECORATION_SLAB);
        dropSelf(METEORIC_IRON_DECORATION_STAIRS);
        dropSelf(METEORIC_IRON_DECORATION_WALL);
        dropSelf(DETAILED_METEORIC_IRON_DECORATION);
        dropSelf(DETAILED_METEORIC_IRON_DECORATION_SLAB);
        dropSelf(DETAILED_METEORIC_IRON_DECORATION_STAIRS);
        dropSelf(DETAILED_METEORIC_IRON_DECORATION_WALL);

        dropSelf(STEEL_DECORATION);
        dropSelf(STEEL_DECORATION_SLAB);
        dropSelf(STEEL_DECORATION_STAIRS);
        dropSelf(STEEL_DECORATION_WALL);
        dropSelf(DETAILED_STEEL_DECORATION);
        dropSelf(DETAILED_STEEL_DECORATION_SLAB);
        dropSelf(DETAILED_STEEL_DECORATION_STAIRS);
        dropSelf(DETAILED_STEEL_DECORATION_WALL);

        dropSelf(TIN_DECORATION);
        dropSelf(TIN_DECORATION_SLAB);
        dropSelf(TIN_DECORATION_STAIRS);
        dropSelf(TIN_DECORATION_WALL);
        dropSelf(DETAILED_TIN_DECORATION);
        dropSelf(DETAILED_TIN_DECORATION_SLAB);
        dropSelf(DETAILED_TIN_DECORATION_STAIRS);
        dropSelf(DETAILED_TIN_DECORATION_WALL);

        dropSelf(TITANIUM_DECORATION);
        dropSelf(TITANIUM_DECORATION_SLAB);
        dropSelf(TITANIUM_DECORATION_STAIRS);
        dropSelf(TITANIUM_DECORATION_WALL);
        dropSelf(DETAILED_TITANIUM_DECORATION);
        dropSelf(DETAILED_TITANIUM_DECORATION_SLAB);
        dropSelf(DETAILED_TITANIUM_DECORATION_STAIRS);
        dropSelf(DETAILED_TITANIUM_DECORATION_WALL);

        dropSelf(DARK_DECORATION);
        dropSelf(DARK_DECORATION_SLAB);
        dropSelf(DARK_DECORATION_STAIRS);
        dropSelf(DARK_DECORATION_WALL);
        dropSelf(DETAILED_DARK_DECORATION);
        dropSelf(DETAILED_DARK_DECORATION_SLAB);
        dropSelf(DETAILED_DARK_DECORATION_STAIRS);
        dropSelf(DETAILED_DARK_DECORATION_WALL);

        dropSelf(MOON_TURF);
        dropSelf(MOON_DIRT);
        dropOther(MOON_DIRT_PATH, GCItems.MOON_DIRT);
        dropSelf(MOON_SURFACE_ROCK);

        add(MOON_ROCK, createSingleItemTableWithSilkTouch(MOON_ROCK, COBBLED_MOON_ROCK));
        dropSelf(MOON_ROCK_SLAB);
        dropSelf(MOON_ROCK_STAIRS);
        dropSelf(MOON_ROCK_WALL);

        dropSelf(COBBLED_MOON_ROCK);
        dropSelf(COBBLED_MOON_ROCK_SLAB);
        dropSelf(COBBLED_MOON_ROCK_STAIRS);
        dropSelf(COBBLED_MOON_ROCK_WALL);

        add(LUNASLATE, createSingleItemTableWithSilkTouch(LUNASLATE, COBBLED_LUNASLATE));
        dropSelf(LUNASLATE_SLAB);
        dropSelf(LUNASLATE_STAIRS);
        dropSelf(LUNASLATE_WALL);

        dropSelf(COBBLED_LUNASLATE);
        dropSelf(COBBLED_LUNASLATE_SLAB);
        dropSelf(COBBLED_LUNASLATE_STAIRS);
        dropSelf(COBBLED_LUNASLATE_WALL);

        dropSelf(MOON_BASALT);
        dropSelf(MOON_BASALT_SLAB);
        dropSelf(MOON_BASALT_STAIRS);
        dropSelf(MOON_BASALT_WALL);

        dropSelf(MOON_BASALT_BRICK);
        dropSelf(MOON_BASALT_BRICK_SLAB);
        dropSelf(MOON_BASALT_BRICK_STAIRS);
        dropSelf(MOON_BASALT_BRICK_WALL);

        dropSelf(CRACKED_MOON_BASALT_BRICK);
        dropSelf(CRACKED_MOON_BASALT_BRICK_SLAB);
        dropSelf(CRACKED_MOON_BASALT_BRICK_STAIRS);
        dropSelf(CRACKED_MOON_BASALT_BRICK_WALL);

        dropSelf(MARS_SURFACE_ROCK);
        dropSelf(MARS_SUB_SURFACE_ROCK);

        add(MARS_STONE, createSingleItemTableWithSilkTouch(MARS_STONE, MARS_COBBLESTONE));
        dropSelf(MARS_STONE_SLAB);
        dropSelf(MARS_STONE_STAIRS);
        dropSelf(MARS_STONE_WALL);

        dropSelf(MARS_COBBLESTONE);
        dropSelf(MARS_COBBLESTONE_SLAB);
        dropSelf(MARS_COBBLESTONE_STAIRS);
        dropSelf(MARS_COBBLESTONE_WALL);

        dropSelf(ASTEROID_ROCK);
        dropSelf(ASTEROID_ROCK_1);
        dropSelf(ASTEROID_ROCK_2);

        dropSelf(SOFT_VENUS_ROCK);
        dropSelf(HARD_VENUS_ROCK);
        dropSelf(SCORCHED_VENUS_ROCK);
        add(VOLCANIC_ROCK, createSilkTouchOnlyTable(VOLCANIC_ROCK));
        dropSelf(PUMICE);
        dropWhenSilkTouch(VAPOR_SPOUT);

        dropSelf(WALKWAY);
        dropSelf(PIPE_WALKWAY);
        dropSelf(WIRE_WALKWAY);
        dropSelf(TIN_LADDER);
        dropSelf(GRATING);

        dropSelf(ALUMINUM_WIRE);
        dropSelf(SEALABLE_ALUMINUM_WIRE);
        dropSelf(HEAVY_SEALABLE_ALUMINUM_WIRE);
        dropSelf(GLASS_FLUID_PIPE);

        dropSelf(SQUARE_LIGHT_PANEL);
        dropSelf(SPOTLIGHT_LIGHT_PANEL);
        dropSelf(LINEAR_LIGHT_PANEL);
        dropSelf(DASHED_LIGHT_PANEL);
        dropSelf(DIAGONAL_LIGHT_PANEL);

        dropSelf(VACUUM_GLASS);
        dropSelf(CLEAR_VACUUM_GLASS);
        dropSelf(STRONG_VACUUM_GLASS);

        add(SILICON_ORE, siliconOreDrops(SILICON_ORE));
        add(DEEPSLATE_SILICON_ORE, siliconOreDrops(DEEPSLATE_SILICON_ORE));
        add(MOON_COPPER_ORE, createCopperOreDrops(MOON_COPPER_ORE));
        add(LUNASLATE_COPPER_ORE, createCopperOreDrops(MOON_COPPER_ORE));

        add(TIN_ORE, createOreDrop(TIN_ORE, GCItems.RAW_TIN));
        add(DEEPSLATE_TIN_ORE, createOreDrop(DEEPSLATE_TIN_ORE, GCItems.RAW_TIN));
        add(MOON_TIN_ORE, createOreDrop(MOON_TIN_ORE, GCItems.RAW_TIN));
        add(LUNASLATE_TIN_ORE, createOreDrop(LUNASLATE_TIN_ORE, GCItems.RAW_TIN));

        add(ALUMINUM_ORE, createOreDrop(ALUMINUM_ORE, GCItems.RAW_ALUMINUM));
        add(DEEPSLATE_ALUMINUM_ORE, createOreDrop(ALUMINUM_ORE, GCItems.RAW_ALUMINUM));

        add(DESH_ORE, createOreDrop(DESH_ORE, GCItems.RAW_DESH));

        add(ILMENITE_ORE, createOreDrop(ILMENITE_ORE, GCItems.RAW_TITANIUM));

        add(GALENA_ORE, createOreDrop(GALENA_ORE, GCItems.RAW_LEAD));

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

        dropSelf(SILICON_BLOCK);
        dropSelf(METEORIC_IRON_BLOCK);
        dropSelf(DESH_BLOCK);
        dropSelf(TITANIUM_BLOCK);
        dropSelf(LEAD_BLOCK);
        dropSelf(LUNAR_SAPPHIRE_BLOCK);

        add(FALLEN_METEOR, block -> BlockLootSubProvider.createSilkTouchDispatchTable(block, this.applyExplosionDecay(block, LootItem.lootTableItem(GCItems.RAW_METEORIC_IRON).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 2.0f))))));

        dropSelf(LUNAR_CARTOGRAPHY_TABLE);

        add(CAVERNOUS_VINE, BlockLootSubProvider::createShearsOnlyDrop);
        add(POISONOUS_CAVERNOUS_VINE, BlockLootSubProvider::createShearsOnlyDrop);
        add(
                MOON_BERRY_BUSH,
                blockx -> applyExplosionDecay(
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

        dropSelf(ROCKET_LAUNCH_PAD);
        dropSelf(AIR_LOCK_CONTROLLER);
        dropSelf(AIR_LOCK_FRAME);

        dropOther(CRYOGENIC_CHAMBER_PART, CRYOGENIC_CHAMBER);

        dropSelf(CRYOGENIC_CHAMBER);
        dropSelf(CIRCUIT_FABRICATOR);
        dropSelf(COMPRESSOR);
        dropSelf(ELECTRIC_COMPRESSOR);
        dropSelf(COAL_GENERATOR);
        dropSelf(BASIC_SOLAR_PANEL);
        dropSelf(ADVANCED_SOLAR_PANEL);
        dropSelf(ENERGY_STORAGE_MODULE);
        dropSelf(ELECTRIC_FURNACE);
        dropSelf(ELECTRIC_ARC_FURNACE);
        dropSelf(REFINERY);
        dropSelf(OXYGEN_COLLECTOR);
        dropSelf(OXYGEN_SEALER);
        dropSelf(OXYGEN_BUBBLE_DISTRIBUTOR);
        dropSelf(OXYGEN_DECOMPRESSOR);
        dropSelf(OXYGEN_COMPRESSOR);
        dropSelf(OXYGEN_STORAGE_MODULE);
        dropSelf(FUEL_LOADER);

        dropSelf(PLAYER_TRANSPORT_TUBE);
        dropSelf(ROCKET_WORKBENCH);

        add(AIR_LOCK_SEAL, noDrop());
    }
}
