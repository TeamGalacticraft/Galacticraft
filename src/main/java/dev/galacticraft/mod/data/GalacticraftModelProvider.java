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

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;

import static dev.galacticraft.mod.block.GalacticraftBlock.*;

public class GalacticraftModelProvider extends FabricModelProvider {
    public GalacticraftModelProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
//        generator.registerSimpleState(GLOWSTONE_TORCH, GalacticraftItem.GLOWSTONE_TORCH);
//        generator.registerSimpleState(GLOWSTONE_WALL_TORCH, GalacticraftItem.GLOWSTONE_TORCH);
//        generator.registerSimpleState(UNLIT_TORCH, GalacticraftItem.UNLIT_TORCH);
//        generator.registerSimpleState(UNLIT_WALL_TORCH, GalacticraftItem.UNLIT_TORCH);

//        generator.registerCubeAllModelTexturePool(ALUMINUM_DECORATION) TODO: PORT ?
//                .slab(ALUMINUM_DECORATION_SLAB)
//                .stairs(ALUMINUM_DECORATION_STAIRS)
//                .wall(ALUMINUM_DECORATION_WALL)
//                .build();
//        generator.registerCubeAllModelTexturePool(DETAILED_ALUMINUM_DECORATION)
//                .slab(DETAILED_ALUMINUM_DECORATION_SLAB)
//                .stairs(DETAILED_ALUMINUM_DECORATION_STAIRS)
//                .wall(DETAILED_ALUMINUM_DECORATION_WALL)
//                .build();
//
//        generator.registerCubeAllModelTexturePool(COPPER_DECORATION)
//                .slab(COPPER_DECORATION_SLAB)
//                .stairs(COPPER_DECORATION_STAIRS)
//                .wall(COPPER_DECORATION_WALL)
//                .build();
//        generator.registerCubeAllModelTexturePool(DETAILED_COPPER_DECORATION)
//                .slab(DETAILED_COPPER_DECORATION_SLAB)
//                .stairs(DETAILED_COPPER_DECORATION_STAIRS)
//                .wall(DETAILED_COPPER_DECORATION_WALL)
//                .build();
//
//        generator.registerCubeAllModelTexturePool(IRON_DECORATION)
//                .slab(IRON_DECORATION_SLAB)
//                .stairs(IRON_DECORATION_STAIRS)
//                .wall(IRON_DECORATION_WALL)
//                .build();
//        generator.registerCubeAllModelTexturePool(DETAILED_IRON_DECORATION)
//                .slab(DETAILED_IRON_DECORATION_SLAB)
//                .stairs(DETAILED_IRON_DECORATION_STAIRS)
//                .wall(DETAILED_IRON_DECORATION_WALL)
//                .build();
//
//        generator.registerCubeAllModelTexturePool(METEORIC_IRON_DECORATION)
//                .slab(METEORIC_IRON_DECORATION_SLAB)
//                .stairs(METEORIC_IRON_DECORATION_STAIRS)
//                .wall(METEORIC_IRON_DECORATION_WALL)
//                .build();
//        generator.registerCubeAllModelTexturePool(DETAILED_METEORIC_IRON_DECORATION)
//                .slab(DETAILED_METEORIC_IRON_DECORATION_SLAB)
//                .stairs(DETAILED_METEORIC_IRON_DECORATION_STAIRS)
//                .wall(DETAILED_METEORIC_IRON_DECORATION_WALL)
//                .build();
//
//        generator.registerCubeAllModelTexturePool(STEEL_DECORATION)
//                .slab(STEEL_DECORATION_SLAB)
//                .stairs(STEEL_DECORATION_STAIRS)
//                .wall(STEEL_DECORATION_WALL)
//                .build();
//        generator.registerCubeAllModelTexturePool(DETAILED_STEEL_DECORATION)
//                .slab(DETAILED_STEEL_DECORATION_SLAB)
//                .stairs(DETAILED_STEEL_DECORATION_STAIRS)
//                .wall(DETAILED_STEEL_DECORATION_WALL)
//                .build();
//
//        generator.registerCubeAllModelTexturePool(TIN_DECORATION)
//                .slab(TIN_DECORATION_SLAB)
//                .stairs(TIN_DECORATION_STAIRS)
//                .wall(TIN_DECORATION_WALL)
//                .build();
//        generator.registerCubeAllModelTexturePool(DETAILED_TIN_DECORATION)
//                .slab(DETAILED_TIN_DECORATION_SLAB)
//                .stairs(DETAILED_TIN_DECORATION_STAIRS)
//                .wall(DETAILED_TIN_DECORATION_WALL)
//                .build();
//
//        generator.registerCubeAllModelTexturePool(TITANIUM_DECORATION)
//                .slab(TITANIUM_DECORATION_SLAB)
//                .stairs(TITANIUM_DECORATION_STAIRS)
//                .wall(TITANIUM_DECORATION_WALL)
//                .build();
//        generator.registerCubeAllModelTexturePool(DETAILED_TITANIUM_DECORATION)
//                .slab(DETAILED_TITANIUM_DECORATION_SLAB)
//                .stairs(DETAILED_TITANIUM_DECORATION_STAIRS)
//                .wall(DETAILED_TITANIUM_DECORATION_WALL)
//                .build();
//
//
//        generator.registerCubeAllModelTexturePool(DARK_DECORATION)
//                .slab(DARK_DECORATION_SLAB)
//                .stairs(DARK_DECORATION_STAIRS)
//                .wall(DARK_DECORATION_WALL)
//                .build();
//        generator.registerCubeAllModelTexturePool(DETAILED_DARK_DECORATION)
//                .slab(DETAILED_DARK_DECORATION_SLAB)
//                .stairs(DETAILED_DARK_DECORATION_STAIRS)
//                .wall(DETAILED_DARK_DECORATION_WALL)
//                .build();

        generator.registerSimpleCubeAll(MOON_TURF);
        generator.registerSimpleCubeAll(MOON_DIRT);
        generator.registerSimpleCubeAll(MOON_SURFACE_ROCK);
//        for (Block decor : MOON_ROCKS) generator.registerSimpleState(decor);
//        for (Block decor : COBBLED_MOON_ROCKS) generator.registerSimpleState(decor);
//        for (Block decor : LUNASLATES) generator.registerSimpleState(decor);
//        for (Block decor : COBBLED_LUNASLATES) generator.registerSimpleState(decor);
//        for (Block decor : MOON_BASALTS) generator.registerSimpleState(decor);
//        for (Block decor : MOON_BASALT_BRICKS) generator.registerSimpleState(decor);
//        for (Block decor : CRACKED_MOON_BASALT_BRICKS) generator.registerSimpleState(decor);

        generator.registerSimpleCubeAll(MARS_SURFACE_ROCK);
        generator.registerSimpleCubeAll(MARS_SUB_SURFACE_ROCK);
        generator.registerSimpleCubeAll(MARS_STONE);

        generator.registerSimpleCubeAll(ASTEROID_ROCK);
        generator.registerSimpleCubeAll(ASTEROID_ROCK_1);
        generator.registerSimpleCubeAll(ASTEROID_ROCK_2);

        generator.registerSimpleCubeAll(SOFT_VENUS_ROCK);
        generator.registerSimpleCubeAll(HARD_VENUS_ROCK);
        generator.registerSimpleCubeAll(SCORCHED_VENUS_ROCK);
        generator.registerSimpleCubeAll(VOLCANIC_ROCK);
        generator.registerSimpleCubeAll(PUMICE);
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

        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(MOON_CHEESE_BLOCK).coordinate(
                BlockStateVariantMap.create(Properties.BITES)
                        .register(0, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(MOON_CHEESE_BLOCK)))
                        .register(1, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(MOON_CHEESE_BLOCK, "_slice1")))
                        .register(2, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(MOON_CHEESE_BLOCK, "_slice2")))
                        .register(3, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(MOON_CHEESE_BLOCK, "_slice3")))
                        .register(4, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(MOON_CHEESE_BLOCK, "_slice4")))
                        .register(5, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(MOON_CHEESE_BLOCK, "_slice5")))
                        .register(6, BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockSubModelId(MOON_CHEESE_BLOCK, "_slice6")))
        ));
        generator.registerSimpleCubeAll(SILICON_BLOCK);
        generator.registerSimpleCubeAll(METEORIC_IRON_BLOCK);
        generator.registerSimpleCubeAll(DESH_BLOCK);
        generator.registerSimpleCubeAll(TITANIUM_BLOCK);
        generator.registerSimpleCubeAll(LEAD_BLOCK);
        generator.registerSimpleCubeAll(LUNAR_SAPPHIRE_BLOCK);

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
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }
}
