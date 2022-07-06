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

import dev.galacticraft.impl.client.model.MachineUnbakedModel;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static dev.galacticraft.mod.block.GalacticraftBlock.*;
import static net.minecraft.data.models.BlockModelGenerators.createRotatedVariant;

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

        generator.family(BRONZE_DECORATION)
                .slab(BRONZE_DECORATION_SLAB)
                .stairs(BRONZE_DECORATION_STAIRS)
                .wall(BRONZE_DECORATION_WALL);
        generator.family(DETAILED_BRONZE_DECORATION)
                .slab(DETAILED_BRONZE_DECORATION_SLAB)
                .stairs(DETAILED_BRONZE_DECORATION_STAIRS)
                .wall(DETAILED_BRONZE_DECORATION_WALL);

        generator.family(MOON_ROCK)
                .slab(MOON_ROCK_SLAB)
                .stairs(MOON_ROCK_STAIRS)
                .wall(MOON_ROCK_WALL);
        generator.family(COBBLED_MOON_ROCK)
                .slab(COBBLED_MOON_ROCK_SLAB)
                .stairs(COBBLED_MOON_ROCK_STAIRS)
                .wall(COBBLED_MOON_ROCK_WALL);

        generator.family(LUNASLATE)
                .slab(LUNASLATE_SLAB)
                .stairs(LUNASLATE_STAIRS)
                .wall(LUNASLATE_WALL);
        generator.family(COBBLED_LUNASLATE)
                .slab(COBBLED_LUNASLATE_SLAB)
                .stairs(COBBLED_LUNASLATE_STAIRS)
                .wall(COBBLED_LUNASLATE_WALL);

        generator.family(MOON_BASALT)
                .slab(MOON_BASALT_SLAB)
                .stairs(MOON_BASALT_STAIRS)
                .wall(MOON_BASALT_WALL);
        generator.family(MOON_BASALT_BRICK)
                .slab(MOON_BASALT_BRICK_SLAB)
                .stairs(MOON_BASALT_BRICK_STAIRS)
                .wall(MOON_BASALT_BRICK_WALL);
        generator.family(CRACKED_MOON_BASALT_BRICK)
                .slab(CRACKED_MOON_BASALT_BRICK_SLAB)
                .stairs(CRACKED_MOON_BASALT_BRICK_STAIRS)
                .wall(CRACKED_MOON_BASALT_BRICK_WALL);

        generator.family(MARS_COBBLESTONE)
                .slab(MARS_COBBLESTONE_SLAB)
                .stairs(MARS_COBBLESTONE_STAIRS)
                .wall(MARS_COBBLESTONE_WALL);

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

        // ORES
        generator.createTrivialCube(SILICON_ORE);
        generator.createTrivialCube(DEEPSLATE_SILICON_ORE);
        generator.createTrivialCube(MOON_COPPER_ORE);
        generator.createTrivialCube(LUNASLATE_COPPER_ORE);
        generator.createTrivialCube(TIN_ORE);
        generator.createTrivialCube(DEEPSLATE_TIN_ORE);
        generator.createTrivialCube(MOON_TIN_ORE);
        generator.createTrivialCube(LUNASLATE_TIN_ORE);
        generator.createTrivialCube(ALUMINUM_ORE);
        generator.createTrivialCube(DEEPSLATE_ALUMINUM_ORE);
        generator.createTrivialCube(DESH_ORE);
        generator.createTrivialCube(ILMENITE_ORE);
        generator.createTrivialCube(GALENA_ORE);

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
        generator.createNonTemplateModelBlock(VAPOR_SPOUT);

        generator.createNonTemplateModelBlock(WALKWAY);
        generator.createNonTemplateModelBlock(PIPE_WALKWAY);
        generator.createNonTemplateModelBlock(WIRE_WALKWAY);
        generator.createNonTemplateModelBlock(TIN_LADDER);
        generator.createNonTemplateModelBlock(GRATING);

        generator.createNonTemplateModelBlock(ALUMINUM_WIRE);
        generator.createNonTemplateModelBlock(SEALABLE_ALUMINUM_WIRE);
        generator.createNonTemplateModelBlock(HEAVY_SEALABLE_ALUMINUM_WIRE);
        generator.createNonTemplateModelBlock(GLASS_FLUID_PIPE);

        generator.createNonTemplateModelBlock(SQUARE_LIGHT_PANEL);
        generator.createNonTemplateModelBlock(SPOTLIGHT_LIGHT_PANEL);
        generator.createNonTemplateModelBlock(LINEAR_LIGHT_PANEL);
        generator.createNonTemplateModelBlock(DASHED_LIGHT_PANEL);
        generator.createNonTemplateModelBlock(DIAGONAL_LIGHT_PANEL);

        generator.createNonTemplateModelBlock(VACUUM_GLASS);
        generator.createNonTemplateModelBlock(CLEAR_VACUUM_GLASS);
        generator.createNonTemplateModelBlock(STRONG_VACUUM_GLASS);

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

        generator.createTrivialCube(SOLAR_PANEL_PART);

        generator.createNonTemplateModelBlock(LUNAR_CARTOGRAPHY_TABLE);

        generator.createNonTemplateModelBlock(CAVERNOUS_VINE);
        generator.createNonTemplateModelBlock(POISONOUS_CAVERNOUS_VINE);
        generator.createNonTemplateModelBlock(MOON_BERRY_BUSH);

        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(CIRCUIT_FABRICATOR, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(COMPRESSOR, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ELECTRIC_COMPRESSOR, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(COAL_GENERATOR, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(BASIC_SOLAR_PANEL, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ADVANCED_SOLAR_PANEL, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ENERGY_STORAGE_MODULE, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ELECTRIC_FURNACE, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(ELECTRIC_ARC_FURNACE, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(REFINERY, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(OXYGEN_COLLECTOR, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(OXYGEN_SEALER, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(BUBBLE_DISTRIBUTOR, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(OXYGEN_DECOMPRESSOR, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(OXYGEN_COMPRESSOR, MachineUnbakedModel.MACHINE_MARKER));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(OXYGEN_STORAGE_MODULE, MachineUnbakedModel.MACHINE_MARKER));

        generator.createLantern(GLOWSTONE_LANTERN);
        generator.createLantern(UNLIT_LANTERN);

        generator.createNonTemplateModelBlock(CRUDE_OIL);
        generator.createNonTemplateModelBlock(FUEL);

        generator.blockStateOutput.accept(createRotatedVariant(MOON_DIRT_PATH, ModelLocationUtils.getModelLocation(MOON_DIRT_PATH)));

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {

    }
}
