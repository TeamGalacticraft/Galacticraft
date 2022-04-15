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
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.model.BlockStateModelGenerator;

import static dev.galacticraft.mod.block.GalacticraftBlock.*;

public class GalacticraftModelProvider extends FabricModelProvider {
    public GalacticraftModelProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
//        blockStateModelGenerator.registerSimpleState(GLOWSTONE_TORCH, GalacticraftItem.GLOWSTONE_TORCH);
//        blockStateModelGenerator.registerSimpleState(GLOWSTONE_WALL_TORCH, GalacticraftItem.GLOWSTONE_TORCH);
//        blockStateModelGenerator.registerSimpleState(UNLIT_TORCH, GalacticraftItem.UNLIT_TORCH);
//        blockStateModelGenerator.registerSimpleState(UNLIT_WALL_TORCH, GalacticraftItem.UNLIT_TORCH);

//        for (Block decor : ALUMINUM_DECORATIONS) blockStateModelGenerator.registerSimpleState(decor);
//        for (Block decor : BRONZE_DECORATIONS) blockStateModelGenerator.registerSimpleState(decor);
//        for (Block decor : COPPER_DECORATIONS) blockStateModelGenerator.registerSimpleState(decor);
//        for (Block decor : DARK_DECORATIONS) blockStateModelGenerator.registerSimpleState(decor);
//        for (Block decor : IRON_DECORATIONS) blockStateModelGenerator.registerSimpleState(decor);
//        for (Block decor : METEORIC_IRON_DECORATIONS) blockStateModelGenerator.registerSimpleState(decor);
//        for (Block decor : STEEL_DECORATIONS) blockStateModelGenerator.registerSimpleState(decor);
//        for (Block decor : TIN_DECORATIONS) blockStateModelGenerator.registerSimpleState(decor);
//        for (Block decor : TITANIUM_DECORATIONS) blockStateModelGenerator.registerSimpleState(decor);

        blockStateModelGenerator.registerSimpleCubeAll(MOON_TURF);
        blockStateModelGenerator.registerSimpleCubeAll(MOON_DIRT);
        blockStateModelGenerator.registerSimpleCubeAll(MOON_SURFACE_ROCK);
//        for (Block decor : MOON_ROCKS) blockStateModelGenerator.registerSimpleState(decor);
//        for (Block decor : COBBLED_MOON_ROCKS) blockStateModelGenerator.registerSimpleState(decor);
//        for (Block decor : LUNASLATES) blockStateModelGenerator.registerSimpleState(decor);
//        for (Block decor : COBBLED_LUNASLATES) blockStateModelGenerator.registerSimpleState(decor);
//        for (Block decor : MOON_BASALTS) blockStateModelGenerator.registerSimpleState(decor);
//        for (Block decor : MOON_BASALT_BRICKS) blockStateModelGenerator.registerSimpleState(decor);
//        for (Block decor : CRACKED_MOON_BASALT_BRICKS) blockStateModelGenerator.registerSimpleState(decor);

        blockStateModelGenerator.registerSimpleCubeAll(MARS_SURFACE_ROCK);
        blockStateModelGenerator.registerSimpleCubeAll(MARS_SUB_SURFACE_ROCK);
        blockStateModelGenerator.registerSimpleCubeAll(MARS_STONE);

        blockStateModelGenerator.registerSimpleCubeAll(ASTEROID_ROCK);
        blockStateModelGenerator.registerSimpleCubeAll(ASTEROID_ROCK_1);
        blockStateModelGenerator.registerSimpleCubeAll(ASTEROID_ROCK_2);

        blockStateModelGenerator.registerSimpleCubeAll(SOFT_VENUS_ROCK);
        blockStateModelGenerator.registerSimpleCubeAll(HARD_VENUS_ROCK);
        blockStateModelGenerator.registerSimpleCubeAll(SCORCHED_VENUS_ROCK);
        blockStateModelGenerator.registerSimpleCubeAll(VOLCANIC_ROCK);
        blockStateModelGenerator.registerSimpleCubeAll(PUMICE);
//        blockStateModelGenerator.registerSimpleState(VAPOR_SPOUT);

//        blockStateModelGenerator.registerSimpleState(WALKWAY);
//        blockStateModelGenerator.registerSimpleState(PIPE_WALKWAY);
//        blockStateModelGenerator.registerSimpleState(WIRE_WALKWAY);
//        blockStateModelGenerator.registerSimpleState(TIN_LADDER);
//        blockStateModelGenerator.registerSimpleState(GRATING);

//        blockStateModelGenerator.registerSimpleState(ALUMINUM_WIRE);
//        blockStateModelGenerator.registerSimpleState(SEALABLE_ALUMINUM_WIRE);
//        blockStateModelGenerator.registerSimpleState(HEAVY_SEALABLE_ALUMINUM_WIRE);
//        blockStateModelGenerator.registerSimpleState(GLASS_FLUID_PIPE);

//        blockStateModelGenerator.registerSimpleState(SQUARE_LIGHT_PANEL);
//        blockStateModelGenerator.registerSimpleState(SPOTLIGHT_LIGHT_PANEL);
//        blockStateModelGenerator.registerSimpleState(LINEAR_LIGHT_PANEL);
//        blockStateModelGenerator.registerSimpleState(DASHED_LIGHT_PANEL);
//        blockStateModelGenerator.registerSimpleState(DIAGONAL_LIGHT_PANEL);

//        blockStateModelGenerator.registerSimpleState(VACUUM_GLASS);
//        blockStateModelGenerator.registerSimpleState(CLEAR_VACUUM_GLASS);
//        blockStateModelGenerator.registerSimpleState(STRONG_VACUUM_GLASS);

        blockStateModelGenerator.registerSimpleCubeAll(MOON_CHEESE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(SILICON_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(METEORIC_IRON_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(DESH_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(TITANIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(LEAD_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(LUNAR_SAPPHIRE_BLOCK);

//        blockStateModelGenerator.registerSimpleState(LUNAR_CARTOGRAPHY_TABLE);

//        blockStateModelGenerator.registerSimpleState(CAVERNOUS_VINE);
//        blockStateModelGenerator.registerSimpleState(POISONOUS_CAVERNOUS_VINE);
//        blockStateModelGenerator.registerSimpleState(MOON_BERRY_BUSH);

//        blockStateModelGenerator.registerSingleton(CIRCUIT_FABRICATOR, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(COMPRESSOR, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(ELECTRIC_COMPRESSOR, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(COAL_GENERATOR, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(BASIC_SOLAR_PANEL, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(ADVANCED_SOLAR_PANEL, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(ENERGY_STORAGE_MODULE, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(ELECTRIC_FURNACE, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(ELECTRIC_ARC_FURNACE, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(REFINERY, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(OXYGEN_COLLECTOR, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(OXYGEN_SEALER, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(BUBBLE_DISTRIBUTOR, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(OXYGEN_DECOMPRESSOR, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(OXYGEN_COMPRESSOR, MachineUnbakedModel.MACHINE_MARKER);
//        blockStateModelGenerator.registerSingleton(OXYGEN_STORAGE_MODULE, MachineUnbakedModel.MACHINE_MARKER);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }
}
