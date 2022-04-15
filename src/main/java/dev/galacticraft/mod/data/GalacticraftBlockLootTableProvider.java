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

import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.item.GalacticraftItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.enchantment.Enchantments;
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

        for (Block decor : ALUMINUM_DECORATIONS) addDrop(decor);
        for (Block decor : BRONZE_DECORATIONS) addDrop(decor);
        for (Block decor : COPPER_DECORATIONS) addDrop(decor);
        for (Block decor : DARK_DECORATIONS) addDrop(decor);
        for (Block decor : IRON_DECORATIONS) addDrop(decor);
        for (Block decor : METEORIC_IRON_DECORATIONS) addDrop(decor);
        for (Block decor : STEEL_DECORATIONS) addDrop(decor);
        for (Block decor : TIN_DECORATIONS) addDrop(decor);
        for (Block decor : TITANIUM_DECORATIONS) addDrop(decor);

        addDrop(MOON_TURF);
        addDrop(MOON_DIRT);
        addDrop(MOON_SURFACE_ROCK);
        addDrop(MOON_ROCKS[0], drops(MOON_ROCKS[0], COBBLED_MOON_ROCKS[0]));
        for (int i = 1; i < MOON_ROCKS.length; i++) addDrop(MOON_ROCKS[i]);

        for (Block decor : COBBLED_MOON_ROCKS) addDrop(decor);
        addDrop(LUNASLATES[0], drops(LUNASLATES[0], COBBLED_LUNASLATES[0]));
        for (int i = 1; i < LUNASLATES.length; i++) addDrop(LUNASLATES[i]);
        for (Block decor : COBBLED_LUNASLATES) addDrop(decor);
        for (Block decor : MOON_BASALTS) addDrop(decor);
        for (Block decor : MOON_BASALT_BRICKS) addDrop(decor);
        for (Block decor : CRACKED_MOON_BASALT_BRICKS) addDrop(decor);

        addDrop(MARS_SURFACE_ROCK);
        addDrop(MARS_SUB_SURFACE_ROCK);
        addDrop(MARS_STONE, drops(MARS_STONE, MARS_COBBLESTONES[0]));

        addDrop(ASTEROID_ROCK);
        addDrop(ASTEROID_ROCK_1);
        addDrop(ASTEROID_ROCK_2);

        addDrop(SOFT_VENUS_ROCK);
        addDrop(HARD_VENUS_ROCK);
        addDrop(SCORCHED_VENUS_ROCK);
        addDrop(VOLCANIC_ROCK);
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

        addDrop(MOON_CHEESE_BLOCK);
        addDrop(SILICON_BLOCK);
        addDrop(METEORIC_IRON_BLOCK);
        addDrop(DESH_BLOCK);
        addDrop(TITANIUM_BLOCK);
        addDrop(LEAD_BLOCK);
        addDrop(LUNAR_SAPPHIRE_BLOCK);

        addDrop(LUNAR_CARTOGRAPHY_TABLE);

        addDrop(CAVERNOUS_VINE);
        addDrop(POISONOUS_CAVERNOUS_VINE);
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
}
