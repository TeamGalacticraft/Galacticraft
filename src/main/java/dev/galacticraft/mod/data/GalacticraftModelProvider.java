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
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static dev.galacticraft.mod.block.GalacticraftBlock.*;

public class GalacticraftModelProvider extends FabricModelProvider {
    public GalacticraftModelProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        generator.createAirLikeBlock(GLOWSTONE_TORCH, GalacticraftItem.GLOWSTONE_TORCH);
        generator.createAirLikeBlock(GLOWSTONE_WALL_TORCH, GalacticraftItem.GLOWSTONE_TORCH);
        generator.createAirLikeBlock(UNLIT_TORCH, GalacticraftItem.UNLIT_TORCH);
        generator.createAirLikeBlock(UNLIT_WALL_TORCH, GalacticraftItem.UNLIT_TORCH);

        generator.family(ALUMINUM_DECORATION)
                .slab(ALUMINUM_DECORATION_SLAB)
                .stairs(ALUMINUM_DECORATION_STAIRS)
                .wall(ALUMINUM_DECORATION_WALL);
        generator.family(DETAILED_ALUMINUM_DECORATION)
                .slab(DETAILED_ALUMINUM_DECORATION_SLAB)
                .stairs(DETAILED_ALUMINUM_DECORATION_STAIRS)
                .wall(DETAILED_ALUMINUM_DECORATION_WALL);

        generator.family(COPPER_DECORATION)
                .slab(COPPER_DECORATION_SLAB)
                .stairs(COPPER_DECORATION_STAIRS)
                .wall(COPPER_DECORATION_WALL);
        generator.family(DETAILED_COPPER_DECORATION)
                .slab(DETAILED_COPPER_DECORATION_SLAB)
                .stairs(DETAILED_COPPER_DECORATION_STAIRS)
                .wall(DETAILED_COPPER_DECORATION_WALL);

        generator.family(IRON_DECORATION)
                .slab(IRON_DECORATION_SLAB)
                .stairs(IRON_DECORATION_STAIRS)
                .wall(IRON_DECORATION_WALL);
        generator.family(DETAILED_IRON_DECORATION)
                .slab(DETAILED_IRON_DECORATION_SLAB)
                .stairs(DETAILED_IRON_DECORATION_STAIRS)
                .wall(DETAILED_IRON_DECORATION_WALL);

        generator.family(METEORIC_IRON_DECORATION)
                .slab(METEORIC_IRON_DECORATION_SLAB)
                .stairs(METEORIC_IRON_DECORATION_STAIRS)
                .wall(METEORIC_IRON_DECORATION_WALL);
        generator.family(DETAILED_METEORIC_IRON_DECORATION)
                .slab(DETAILED_METEORIC_IRON_DECORATION_SLAB)
                .stairs(DETAILED_METEORIC_IRON_DECORATION_STAIRS)
                .wall(DETAILED_METEORIC_IRON_DECORATION_WALL);

        generator.family(STEEL_DECORATION)
                .slab(STEEL_DECORATION_SLAB)
                .stairs(STEEL_DECORATION_STAIRS)
                .wall(STEEL_DECORATION_WALL);
        generator.family(DETAILED_STEEL_DECORATION)
                .slab(DETAILED_STEEL_DECORATION_SLAB)
                .stairs(DETAILED_STEEL_DECORATION_STAIRS)
                .wall(DETAILED_STEEL_DECORATION_WALL);

        generator.family(TIN_DECORATION)
                .slab(TIN_DECORATION_SLAB)
                .stairs(TIN_DECORATION_STAIRS)
                .wall(TIN_DECORATION_WALL);
        generator.family(DETAILED_TIN_DECORATION)
                .slab(DETAILED_TIN_DECORATION_SLAB)
                .stairs(DETAILED_TIN_DECORATION_STAIRS)
                .wall(DETAILED_TIN_DECORATION_WALL);

        generator.family(TITANIUM_DECORATION)
                .slab(TITANIUM_DECORATION_SLAB)
                .stairs(TITANIUM_DECORATION_STAIRS)
                .wall(TITANIUM_DECORATION_WALL);
        generator.family(DETAILED_TITANIUM_DECORATION)
                .slab(DETAILED_TITANIUM_DECORATION_SLAB)
                .stairs(DETAILED_TITANIUM_DECORATION_STAIRS)
                .wall(DETAILED_TITANIUM_DECORATION_WALL);


        generator.family(DARK_DECORATION)
                .slab(DARK_DECORATION_SLAB)
                .stairs(DARK_DECORATION_STAIRS)
                .wall(DARK_DECORATION_WALL);
        generator.family(DETAILED_DARK_DECORATION)
                .slab(DETAILED_DARK_DECORATION_SLAB)
                .stairs(DETAILED_DARK_DECORATION_STAIRS)
                .wall(DETAILED_DARK_DECORATION_WALL);

        generator.createTrivialCube(MOON_TURF);
        generator.createTrivialCube(MOON_DIRT);
        generator.createTrivialCube(MOON_SURFACE_ROCK);
//        for (Block decor : MOON_ROCKS) generator.registerSimpleState(decor);
//        for (Block decor : COBBLED_MOON_ROCKS) generator.registerSimpleState(decor);
//        for (Block decor : LUNASLATES) generator.registerSimpleState(decor);
//        for (Block decor : COBBLED_LUNASLATES) generator.registerSimpleState(decor);
//        for (Block decor : MOON_BASALTS) generator.registerSimpleState(decor);
//        for (Block decor : MOON_BASALT_BRICKS) generator.registerSimpleState(decor);
//        for (Block decor : CRACKED_MOON_BASALT_BRICKS) generator.registerSimpleState(decor);

        generator.createTrivialCube(MARS_SURFACE_ROCK);
        generator.createTrivialCube(MARS_SUB_SURFACE_ROCK);
        generator.createTrivialCube(MARS_STONE);

        generator.createTrivialCube(ASTEROID_ROCK);
        generator.createTrivialCube(ASTEROID_ROCK_1);
        generator.createTrivialCube(ASTEROID_ROCK_2);

        generator.createTrivialCube(SOFT_VENUS_ROCK);
        generator.createTrivialCube(HARD_VENUS_ROCK);
        generator.createTrivialCube(SCORCHED_VENUS_ROCK);
        generator.createTrivialCube(VOLCANIC_ROCK);
        generator.createTrivialCube(PUMICE);
//        generator.registerSimpleState(VAPOR_SPOUT);

//        generator.registerSimpleState(WALKWAY);
//        generator.registerSimpleState(PIPE_WALKWAY);
//        generator.registerSimpleState(WIRE_WALKWAY);
//        generator.registerSimpleState(TIN_LADDER);
//        generator.registerSimpleState(GRATING);

//        generator.registerSimpleState(ALUMINUM_WIRE);
//        generator.registerSimpleState(SEALABLE_ALUMINUM_WIRE);
//        generator.registerSimpleState(HEAVY_SEALABLE_ALUMINUM_WIRE);
//        generator.registerSimpleState(GLASS_FLUID_PIPE);

//        generator.registerSimpleState(SQUARE_LIGHT_PANEL);
//        generator.registerSimpleState(SPOTLIGHT_LIGHT_PANEL);
//        generator.registerSimpleState(LINEAR_LIGHT_PANEL);
//        generator.registerSimpleState(DASHED_LIGHT_PANEL);
//        generator.registerSimpleState(DIAGONAL_LIGHT_PANEL);

//        generator.registerSimpleState(VACUUM_GLASS);
//        generator.registerSimpleState(CLEAR_VACUUM_GLASS);
//        generator.registerSimpleState(STRONG_VACUUM_GLASS);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(MOON_CHEESE_BLOCK).with(
                PropertyDispatch.property(BlockStateProperties.BITES)
                        .select(0, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(MOON_CHEESE_BLOCK)))
                        .select(1, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(MOON_CHEESE_BLOCK, "_slice1")))
                        .select(2, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(MOON_CHEESE_BLOCK, "_slice2")))
                        .select(3, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(MOON_CHEESE_BLOCK, "_slice3")))
                        .select(4, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(MOON_CHEESE_BLOCK, "_slice4")))
                        .select(5, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(MOON_CHEESE_BLOCK, "_slice5")))
                        .select(6, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(MOON_CHEESE_BLOCK, "_slice6")))
        ));
        generator.createTrivialCube(SILICON_BLOCK);
        generator.createTrivialCube(METEORIC_IRON_BLOCK);
        generator.createTrivialCube(DESH_BLOCK);
        generator.createTrivialCube(TITANIUM_BLOCK);
        generator.createTrivialCube(LEAD_BLOCK);
        generator.createTrivialCube(LUNAR_SAPPHIRE_BLOCK);

//        generator.registerSimpleState(LUNAR_CARTOGRAPHY_TABLE);

//        generator.registerSimpleState(CAVERNOUS_VINE);
//        generator.registerSimpleState(POISONOUS_CAVERNOUS_VINE);
//        generator.registerSimpleState(MOON_BERRY_BUSH);

//        generator.registerSingleton(CIRCUIT_FABRICATOR, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(COMPRESSOR, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(ELECTRIC_COMPRESSOR, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(COAL_GENERATOR, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(BASIC_SOLAR_PANEL, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(ADVANCED_SOLAR_PANEL, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(ENERGY_STORAGE_MODULE, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(ELECTRIC_FURNACE, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(ELECTRIC_ARC_FURNACE, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(REFINERY, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(OXYGEN_COLLECTOR, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(OXYGEN_SEALER, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(BUBBLE_DISTRIBUTOR, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(OXYGEN_DECOMPRESSOR, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(OXYGEN_COMPRESSOR, MachineUnbakedModel.MACHINE_MARKER);
//        generator.registerSingleton(OXYGEN_STORAGE_MODULE, MachineUnbakedModel.MACHINE_MARKER);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {

    }
}
