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

package dev.galacticraft.mod.data.model;

import com.google.common.collect.Maps;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlockRegistry;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.decoration.GratingBlock;
import dev.galacticraft.mod.content.block.environment.CavernousVines;
import dev.galacticraft.mod.content.block.special.ParaChestBlock;
import dev.galacticraft.mod.content.block.special.launchpad.AbstractLaunchPad;
import dev.galacticraft.mod.content.item.GCItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Contract;

import java.util.List;

public class GCModelProvider extends FabricModelProvider {
    private static final TexturedModel.Provider DETAILED_DECORATION = TexturedModel.createDefault(GCModelProvider::detailedTexture, ModelTemplates.CUBE_BOTTOM_TOP);

    public GCModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        // GENERATE ALL GCBlockFamilies
        generator.texturedModels = Maps.newHashMap(generator.texturedModels);
        generator.fullBlockModelCustomGenerators = Maps.newHashMap(generator.fullBlockModelCustomGenerators);

        generator.fullBlockModelCustomGenerators.put(GCBlocks.LUNASLATE, BlockModelGenerators::createMirroredColumnGenerator);

        List<GCBlockRegistry.DecorationSet> decorations = GCBlocks.BLOCKS.getDecorations();

        decorations.forEach(decorationSet -> putDetailedTextured(generator, decorationSet.detailedBlock()));
        generator.texturedModels.put(GCBlocks.LUNASLATE, TexturedModel.COLUMN_WITH_WALL.get(GCBlocks.LUNASLATE));

        GCBlockFamilies.getAllFamilies()
                .filter(BlockFamily::shouldGenerateModel)
                .forEach(blockFamily -> generator.family(blockFamily.getBaseBlock()).generateFor(blockFamily));

        // DETAILED WALL - Special case!
        for (GCBlockRegistry.DecorationSet decorationSet : decorations) {
            this.detailedWall(generator, decorationSet.detailedBlock(), decorationSet.detailedWall());
        }

        // TORCHES
        generator.createNormalTorch(GCBlocks.GLOWSTONE_TORCH, GCBlocks.GLOWSTONE_WALL_TORCH);
        generator.createNormalTorch(GCBlocks.UNLIT_TORCH, GCBlocks.UNLIT_WALL_TORCH);

        // LANTERNS
        generator.createLantern(GCBlocks.GLOWSTONE_LANTERN);
        generator.createLantern(GCBlocks.UNLIT_LANTERN);

        // MOON NAUTRAL
        this.createMoonTurf(generator);
        generator.createTrivialCube(GCBlocks.MOON_DIRT);
        createRotatedDelegate(generator, GCBlocks.MOON_DIRT_PATH);
        generator.createTrivialCube(GCBlocks.MOON_SURFACE_ROCK);
        generator.createTrivialCube(GCBlocks.MOON_DUNGEON_BRICK);
        generator.createTrivialCube(GCBlocks.CHISELED_MOON_ROCK_BRICK);
        generator.createAxisAlignedPillarBlock(GCBlocks.MOON_ROCK_PILLAR, TexturedModel.COLUMN);
        generator.createAxisAlignedPillarBlock(GCBlocks.OLIVINE_BLOCK, TexturedModel.COLUMN);

        // MARS NATURAL
        generator.createTrivialCube(GCBlocks.MARS_SURFACE_ROCK);
        generator.createTrivialCube(GCBlocks.MARS_SUB_SURFACE_ROCK);

        // ASTEROID NATURAL
        generator.createTrivialCube(GCBlocks.ASTEROID_ROCK);
        generator.createTrivialCube(GCBlocks.ASTEROID_ROCK_1);
        generator.createTrivialCube(GCBlocks.ASTEROID_ROCK_2);

        // VENUS NATURAL
        generator.createTrivialCube(GCBlocks.SOFT_VENUS_ROCK);
        generator.createTrivialCube(GCBlocks.HARD_VENUS_ROCK);
        generator.createTrivialCube(GCBlocks.SCORCHED_VENUS_ROCK);
        generator.createTrivialCube(GCBlocks.VOLCANIC_ROCK);
        generator.createTrivialCube(GCBlocks.PUMICE);
        this.createVaporSpout(generator);

        // MISC DECOR
        generator.createNonTemplateHorizontalBlock(GCBlocks.TIN_LADDER);
        generator.createSimpleFlatItemModel(GCBlocks.TIN_LADDER);
        this.createGrating(generator);

        // SPECIAL
        this.createAutoGeneratedModel(generator, GCBlocks.WALKWAY, Constant.BakedModel.WALKWAY_MARKER);
        this.createAutoGeneratedModel(generator, GCBlocks.FLUID_PIPE_WALKWAY, Constant.BakedModel.FLUID_PIPE_WALKWAY_MARKER);
        this.createAutoGeneratedModel(generator, GCBlocks.WIRE_WALKWAY, Constant.BakedModel.WIRE_WALKWAY_MARKER);
        this.createAutoGeneratedModel(generator, GCBlocks.ALUMINUM_WIRE, Constant.BakedModel.WIRE_MARKER);
        this.createAutoGeneratedModel(generator, GCBlocks.GLASS_FLUID_PIPE, Constant.BakedModel.GLASS_FLUID_PIPE_MARKER);

        generator.delegateItemModel(GCBlocks.ALUMINUM_WIRE, ModelLocationUtils.getModelLocation(GCBlocks.ALUMINUM_WIRE, "_inventory"));
        generator.delegateItemModel(GCBlocks.GLASS_FLUID_PIPE, ModelLocationUtils.getModelLocation(GCBlocks.GLASS_FLUID_PIPE, "_inventory"));
        this.createGlassFluidPipeAndWalkway(generator);
        generator.createTrivialCube(GCBlocks.SEALABLE_ALUMINUM_WIRE);
        generator.createTrivialCube(GCBlocks.HEAVY_SEALABLE_ALUMINUM_WIRE);
        createLaunchPadBlock(GCBlocks.FUELING_PAD, generator);
        createLaunchPadBlock(GCBlocks.ROCKET_LAUNCH_PAD, generator);
        generator.createNonTemplateModelBlock(GCBlocks.ROCKET_WORKBENCH);
        generator.createNonTemplateModelBlock(GCBlocks.FALLEN_METEOR);

        // LIGHT PANELS
        generator.createNonTemplateModelBlock(GCBlocks.SQUARE_LIGHT_PANEL); //todo
        generator.createNonTemplateModelBlock(GCBlocks.SPOTLIGHT_LIGHT_PANEL);
        generator.createNonTemplateModelBlock(GCBlocks.LINEAR_LIGHT_PANEL);
        generator.createNonTemplateModelBlock(GCBlocks.DASHED_LIGHT_PANEL);
        generator.createNonTemplateModelBlock(GCBlocks.DIAGONAL_LIGHT_PANEL);

        // VACUUM GLASS
        this.createAutoGeneratedModel(generator, GCBlocks.VACUUM_GLASS, Constant.BakedModel.VACUUM_GLASS_MODEL);
        this.createAutoGeneratedModel(generator, GCBlocks.CLEAR_VACUUM_GLASS, Constant.BakedModel.VACUUM_GLASS_MODEL);
        this.createAutoGeneratedModel(generator, GCBlocks.STRONG_VACUUM_GLASS, Constant.BakedModel.VACUUM_GLASS_MODEL);

        // ORES
        generator.createTrivialCube(GCBlocks.SILICON_ORE);
        generator.createTrivialCube(GCBlocks.DEEPSLATE_SILICON_ORE);

        generator.createTrivialCube(GCBlocks.MOON_COPPER_ORE);
        generator.createTrivialCube(GCBlocks.LUNASLATE_COPPER_ORE);

        generator.createTrivialCube(GCBlocks.TIN_ORE);
        generator.createTrivialCube(GCBlocks.DEEPSLATE_TIN_ORE);
        generator.createTrivialCube(GCBlocks.MOON_TIN_ORE);
        generator.createTrivialCube(GCBlocks.LUNASLATE_TIN_ORE);

        generator.createTrivialCube(GCBlocks.ALUMINUM_ORE);
        generator.createTrivialCube(GCBlocks.DEEPSLATE_ALUMINUM_ORE);

        generator.createTrivialCube(GCBlocks.DESH_ORE);

        generator.createTrivialCube(GCBlocks.ILMENITE_ORE);

        generator.createTrivialCube(GCBlocks.GALENA_ORE);

        this.createOlivineCluster(generator, GCBlocks.OLIVINE_CLUSTER);
        generator.createSimpleFlatItemModel(GCBlocks.OLIVINE_CLUSTER, "_vertical");
        generator.createTrivialCube(GCBlocks.OLIVINE_BASALT);
        generator.createTrivialCube(GCBlocks.RICH_OLIVINE_BASALT);

        this.createCheeseBlock(generator);
        this.createCandleCheeseBlock(generator, Blocks.CANDLE, GCBlocks.CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.WHITE_CANDLE, GCBlocks.WHITE_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.ORANGE_CANDLE, GCBlocks.ORANGE_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.MAGENTA_CANDLE, GCBlocks.MAGENTA_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.LIGHT_BLUE_CANDLE, GCBlocks.LIGHT_BLUE_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.YELLOW_CANDLE, GCBlocks.YELLOW_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.LIME_CANDLE, GCBlocks.LIME_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.PINK_CANDLE, GCBlocks.PINK_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.GRAY_CANDLE, GCBlocks.GRAY_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.LIGHT_GRAY_CANDLE, GCBlocks.LIGHT_GRAY_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.CYAN_CANDLE, GCBlocks.CYAN_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.PURPLE_CANDLE, GCBlocks.PURPLE_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.BLUE_CANDLE, GCBlocks.BLUE_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.BROWN_CANDLE, GCBlocks.BROWN_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.GREEN_CANDLE, GCBlocks.GREEN_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.RED_CANDLE, GCBlocks.RED_CANDLE_MOON_CHEESE_WHEEL);
        this.createCandleCheeseBlock(generator, Blocks.BLACK_CANDLE, GCBlocks.BLACK_CANDLE_MOON_CHEESE_WHEEL);

        // COMPACT MINERAL BLOCKS
        generator.createTrivialCube(GCBlocks.SILICON_BLOCK);
        generator.createTrivialCube(GCBlocks.METEORIC_IRON_BLOCK);
        generator.createTrivialCube(GCBlocks.DESH_BLOCK);
        generator.createTrivialCube(GCBlocks.TITANIUM_BLOCK);
        generator.createTrivialCube(GCBlocks.LEAD_BLOCK);
        generator.createTrivialCube(GCBlocks.LUNAR_SAPPHIRE_BLOCK);

        // MOON VILLAGER SPECIAL
        generator.copyModel(Blocks.CARTOGRAPHY_TABLE, GCBlocks.LUNAR_CARTOGRAPHY_TABLE);

        // MISC WORLD GEN
        this.createCavernousVines(generator);

        // DUMMY
        generator.createAirLikeBlock(GCBlocks.SOLAR_PANEL_PART, GCItems.BLUE_SOLAR_WAFER);

        // MISC MACHINES
//        generator.createNonTemplateModelBlock(GCBlocks.CRYOGENIC_CHAMBER);
        generator.createNonTemplateModelBlock(GCBlocks.PLAYER_TRANSPORT_TUBE);

        //todo gen models (not just blockstates)
        createMachineDelegate(generator, GCBlocks.CIRCUIT_FABRICATOR);
        createMachineDelegate(generator, GCBlocks.COMPRESSOR);
        createMachineDelegate(generator, GCBlocks.ELECTRIC_COMPRESSOR);
        createMachineDelegate(generator, GCBlocks.COAL_GENERATOR);
        createMachineDelegate(generator, GCBlocks.BASIC_SOLAR_PANEL);
        createMachineDelegate(generator, GCBlocks.ADVANCED_SOLAR_PANEL);
        createMachineDelegate(generator, GCBlocks.ENERGY_STORAGE_MODULE);
        createMachineDelegate(generator, GCBlocks.ELECTRIC_FURNACE);
        createMachineDelegate(generator, GCBlocks.ELECTRIC_ARC_FURNACE);
        createMachineDelegate(generator, GCBlocks.REFINERY);
        createMachineDelegate(generator, GCBlocks.OXYGEN_COLLECTOR);
        createMachineDelegate(generator, GCBlocks.OXYGEN_SEALER);
        createMachineDelegate(generator, GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR);
        createMachineDelegate(generator, GCBlocks.OXYGEN_DECOMPRESSOR);
        createMachineDelegate(generator, GCBlocks.OXYGEN_COMPRESSOR);
        createMachineDelegate(generator, GCBlocks.OXYGEN_STORAGE_MODULE);
        createMachineDelegate(generator, GCBlocks.FUEL_LOADER);

        generator.createNonTemplateModelBlock(GCBlocks.CRUDE_OIL);
        generator.createNonTemplateModelBlock(GCBlocks.FUEL);
        generator.createNonTemplateModelBlock(GCBlocks.SULFURIC_ACID);

        generator.createTrivialCube(GCBlocks.AIR_LOCK_FRAME);
        this.createAirLockController(generator);
        generator.createNonTemplateModelBlock(GCBlocks.AIR_LOCK_SEAL);

        var para = MultiPartGenerator.multiPart(GCBlocks.PARACHEST);
        GCBlocks.PARACHEST.getStateDefinition().getPossibleStates().forEach(state -> {
            para.with(Condition.condition().term(ParaChestBlock.FACING, state.getValue(ParaChestBlock.FACING))/*.term(ParaChestBlock.COLOR, state.getValue(ParaChestBlock.COLOR))*/, Variant.variant()
                    .with(VariantProperties.Y_ROT, getRotationFromDirection(state.getValue(ParaChestBlock.FACING)))
                    .with(VariantProperties.MODEL, ResourceLocation.parse("galacticraft:block/parachest/parachest")));
            para.with(Condition.condition().term(ParaChestBlock.COLOR, state.getValue(ParaChestBlock.COLOR)), Variant.variant()
                    .with(VariantProperties.Y_ROT, getRotationFromDirection(state.getValue(ParaChestBlock.FACING)))
                    .with(VariantProperties.MODEL, ResourceLocation.parse("galacticraft:block/parachest/" + state.getValue(ParaChestBlock.COLOR) + "_chute")));
        });
        generator.blockStateOutput.accept(para);
    }

    public static VariantProperties.Rotation getRotationFromDirection(Direction direction) {
        return switch (direction) {
            case DOWN, UP, NORTH -> VariantProperties.Rotation.R0;
            case SOUTH -> VariantProperties.Rotation.R180;
            case WEST -> VariantProperties.Rotation.R270;
            case EAST -> VariantProperties.Rotation.R90;
        };
    }

    private void createMoonTurf(BlockModelGenerators generator) {
        var block = GCBlocks.MOON_TURF;
        var textureMapping = new TextureMapping()
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(GCBlocks.MOON_DIRT))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"));
        generator.createTrivialBlock(block, textureMapping, ModelTemplates.CUBE_BOTTOM_TOP);
    }

    private void createVaporSpout(BlockModelGenerators generator) {
        var textureMapping = new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(GCBlocks.SOFT_VENUS_ROCK)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(GCBlocks.VAPOR_SPOUT));
        generator.createTrivialBlock(GCBlocks.VAPOR_SPOUT, textureMapping, ModelTemplates.CUBE_TOP);
    }

    private void createAirLockController(BlockModelGenerators generator) {
        var block = GCBlocks.AIR_LOCK_CONTROLLER;
        var textureMapping = TextureMapping.column(TextureMapping.getBlockTexture(block), TextureMapping.getBlockTexture(GCBlocks.AIR_LOCK_FRAME));
        generator.createTrivialBlock(block, textureMapping, ModelTemplates.CUBE_COLUMN);
    }

    private void createGlassFluidPipeAndWalkway(BlockModelGenerators generator) {
        for (var color : DyeColor.values()) {
            GCModelTemplates.GLASS_FLUID_PIPE.create(Constant.id(color + "_" + BuiltInRegistries.BLOCK.getKey(GCBlocks.GLASS_FLUID_PIPE).getPath()).withPrefix("block/"), color(TextureMapping.getBlockTexture(GCBlocks.GLASS_FLUID_PIPE, "/" + color)), generator.modelOutput);
            GCModelTemplates.FLUID_PIPE_WALKWAY.create(Constant.id(color + "_" + BuiltInRegistries.BLOCK.getKey(GCBlocks.FLUID_PIPE_WALKWAY).getPath()).withPrefix("block/"), color(TextureMapping.getBlockTexture(GCBlocks.GLASS_FLUID_PIPE, "/" + color)), generator.modelOutput);
        }
        generator.delegateItemModel(GCItems.FLUID_PIPE_WALKWAY, Constant.id("white_fluid_pipe_walkway").withPrefix("block/"));
    }

    private void createAutoGeneratedModel(BlockModelGenerators generator, Block block, ResourceLocation id) {
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, id));
    }

    private void createGrating(BlockModelGenerators generator) {
        var block = GCBlocks.GRATING;
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block)
                .with(PropertyDispatch.property(GratingBlock.STATE)
                        .select(GratingBlock.State.LOWER, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(block, "_lower")))
                        .select(GratingBlock.State.UPPER, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(block, "_upper")))));
        generator.delegateItemModel(block, ModelLocationUtils.getModelLocation(block, "_lower"));
    }

    private void detailedWall(BlockModelGenerators generator, Block base, Block wall) {
        var mapping = GCModelProvider.detailedTexture(base);
        var wallPost = GCModelTemplates.DETAILED_WALL_POST.create(wall, mapping, generator.modelOutput);
        var wallLowSide = GCModelTemplates.DETAILED_WALL_LOW_SIDE.create(wall, mapping, generator.modelOutput);
        var wallTallSide = GCModelTemplates.DETAILED_WALL_TALL_SIDE.create(wall, mapping, generator.modelOutput);
        generator.blockStateOutput.accept(BlockModelGenerators.createWall(wall, wallPost, wallLowSide, wallTallSide));
        var wallInventory = GCModelTemplates.DETAILED_WALL_INVENTORY.create(wall, mapping, generator.modelOutput);
        generator.delegateItemModel(wall, wallInventory);
    }

    private static void putDetailedTextured(BlockModelGenerators generator, Block detailedBlock) {
        generator.texturedModels.put(detailedBlock, DETAILED_DECORATION.get(detailedBlock));
    }

    private static void createRotatedDelegate(BlockModelGenerators generator, Block block) {
        generator.blockStateOutput.accept(BlockModelGenerators.createRotatedVariant(block, ModelLocationUtils.getModelLocation(block)));
    }

    public static ResourceLocation getMachineModelLocation(Block block) {
        ResourceLocation resourceLocation = BuiltInRegistries.BLOCK.getKey(block);
        return resourceLocation.withPrefix("machine/");
    }

    private static void createMachineDelegate(BlockModelGenerators generator, Block block) { //todo: look into why we need this prefix
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, getMachineModelLocation(block)));
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        // MATERIALS
        generator.generateFlatItem(GCItems.RAW_SILICON, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.RAW_METEORIC_IRON, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.METEORIC_IRON_INGOT, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.METEORIC_IRON_NUGGET, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.COMPRESSED_METEORIC_IRON, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.RAW_DESH, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.DESH_INGOT, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.DESH_NUGGET, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.COMPRESSED_DESH, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.RAW_LEAD, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.LEAD_INGOT, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.LEAD_NUGGET, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.RAW_ALUMINUM, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.ALUMINUM_INGOT, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.ALUMINUM_NUGGET, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.COMPRESSED_ALUMINUM, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.RAW_TIN, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TIN_INGOT, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TIN_NUGGET, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.COMPRESSED_TIN, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.RAW_TITANIUM, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TITANIUM_INGOT, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TITANIUM_NUGGET, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.COMPRESSED_TITANIUM, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.COMPRESSED_BRONZE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.COMPRESSED_COPPER, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.COMPRESSED_IRON, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.COMPRESSED_STEEL, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.LUNAR_SAPPHIRE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.OLIVINE_SHARD, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.DESH_STICK, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(GCItems.CARBON_FRAGMENTS, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.IRON_SHARD, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.SOLAR_DUST, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.BASIC_WAFER, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.ADVANCED_WAFER, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.BEAM_CORE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.CANVAS, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.FLUID_MANIPULATOR, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.OXYGEN_CONCENTRATOR, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.OXYGEN_FAN, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.OXYGEN_VENT, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.SENSOR_LENS, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.BLUE_SOLAR_WAFER, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.SINGLE_SOLAR_MODULE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.FULL_SOLAR_PANEL, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.SOLAR_ARRAY_WAFER, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.STEEL_POLE, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(GCItems.COPPER_CANISTER, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TIN_CANISTER, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.THERMAL_CLOTH, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.ISOTHERMAL_FABRIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.ORION_DRIVE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.ATMOSPHERIC_VALVE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.AMBIENT_THERMAL_CONTROLLER, ModelTemplates.FLAT_ITEM);

        // FOOD
        generator.generateFlatItem(GCItems.CHEESE_CURD, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.CHEESE_SLICE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.BURGER_BUN, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.GROUND_BEEF, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.BEEF_PATTY, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.CHEESEBURGER, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.CANNED_DEHYDRATED_APPLE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.CANNED_DEHYDRATED_CARROT, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.CANNED_DEHYDRATED_MELON, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.CANNED_DEHYDRATED_POTATO, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.CANNED_BEEF, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.NOSE_CONE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.HEAVY_NOSE_CONE, "_joined", ModelTemplates.FLAT_ITEM);

        // ROCKET PLATES
        generator.generateFlatItem(GCItems.TIER_1_HEAVY_DUTY_PLATE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TIER_2_HEAVY_DUTY_PLATE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TIER_3_HEAVY_DUTY_PLATE, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.THROWABLE_METEOR_CHUNK, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.HOT_THROWABLE_METEOR_CHUNK, ModelTemplates.FLAT_ITEM);

        // ARMOR
        for (var item : BuiltInRegistries.ITEM.asLookup().filterElements(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(Constant.MOD_ID)).listElements().map(Holder::value).toList()) {
            if (item == GCItems.SENSOR_GLASSES) {
                continue;
            }
            if (item instanceof ArmorItem armorItem) {
                generator.generateArmorTrims(armorItem);
            }
        }

        generator.generateFlatItem(GCItems.SENSOR_GLASSES, ModelTemplates.FLAT_ITEM);

        // TOOLS + WEAPONS
        generator.generateFlatItem(GCItems.HEAVY_DUTY_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(GCItems.HEAVY_DUTY_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(GCItems.HEAVY_DUTY_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(GCItems.HEAVY_DUTY_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(GCItems.HEAVY_DUTY_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);

        generator.generateFlatItem(GCItems.DESH_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(GCItems.DESH_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(GCItems.DESH_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(GCItems.DESH_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(GCItems.DESH_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);

        generator.generateFlatItem(GCItems.TITANIUM_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(GCItems.TITANIUM_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(GCItems.TITANIUM_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(GCItems.TITANIUM_AXE, ModelTemplates.FLAT_HANDHELD_ITEM);
        generator.generateFlatItem(GCItems.TITANIUM_HOE, ModelTemplates.FLAT_HANDHELD_ITEM);

        generator.generateFlatItem(GCItems.STANDARD_WRENCH, ModelTemplates.FLAT_HANDHELD_ITEM);

        // SMITHING TEMPLATES
        generator.generateFlatItem(GCItems.TITANTIUM_UPGRADE_SMITHING_TEMPLATE, ModelTemplates.FLAT_HANDHELD_ITEM);

        // BATTERIES
        generator.generateFlatItem(GCItems.BATTERY, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.INFINITE_BATTERY, GCItems.BATTERY, ModelTemplates.FLAT_ITEM);

        //FLUID BUCKETS
        generator.generateFlatItem(GCItems.CRUDE_OIL_BUCKET, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.FUEL_BUCKET, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.SULFURIC_ACID_BUCKET, ModelTemplates.FLAT_ITEM);

        //GALACTICRAFT INVENTORY
        GCItems.PARACHUTE.colorMap().forEach((color, parachute) -> generator.generateFlatItem(parachute, ModelTemplates.FLAT_ITEM));

        generator.generateFlatItem(GCItems.OXYGEN_MASK, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.OXYGEN_GEAR, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.SMALL_OXYGEN_TANK, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.MEDIUM_OXYGEN_TANK, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.LARGE_OXYGEN_TANK, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.INFINITE_OXYGEN_TANK, GCItems.LARGE_OXYGEN_TANK, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.SHIELD_CONTROLLER, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.FREQUENCY_MODULE, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.THERMAL_PADDING_HELMET, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.THERMAL_PADDING_CHESTPIECE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.THERMAL_PADDING_LEGGINGS, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.THERMAL_PADDING_BOOTS, ModelTemplates.FLAT_ITEM);

        // ROCKETS
        generator.generateFlatItem(GCItems.ROCKET_ENGINE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.ROCKET_FIN, ModelTemplates.FLAT_ITEM);

        // SCHEMATICS
        generator.generateFlatItem(GCItems.BASIC_ROCKET_BODY_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.BASIC_ROCKET_ENGINE_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.BASIC_ROCKET_CONE_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.BASIC_ROCKET_FINS_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TIER_2_ROCKET_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.CARGO_ROCKET_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.MOON_BUGGY_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TIER_3_ROCKET_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.ASTRO_MINER_SCHEMATIC, ModelTemplates.FLAT_ITEM);
    }

    @Contract("_ -> new")
    private static TextureMapping detailedTexture(Block block) {
        var resourceLocation = TextureMapping.getBlockTexture(block, "_side");
        return new TextureMapping()
                .put(TextureSlot.WALL, resourceLocation)
                .put(TextureSlot.SIDE, resourceLocation)
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_top"))
                .put(TextureSlot.BOTTOM, ResourceLocation.parse(TextureMapping.getBlockTexture(block).toString().replace("detailed_", "")));
    }

    private void createCheeseBlock(BlockModelGenerators generators) {
        var block = GCBlocks.MOON_CHEESE_WHEEL;
        generators.createSimpleFlatItemModel(GCItems.MOON_CHEESE_WHEEL);
        generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.property(BlockStateProperties.BITES)
                .select(0, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(block)))
                .select(1, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(block, "_slice1")))
                .select(2, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(block, "_slice2")))
                .select(3, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(block, "_slice3")))
                .select(4, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(block, "_slice4")))
                .select(5, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(block, "_slice5")))
                .select(6, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(block, "_slice6")))));
    }

    private void createCandleCheeseBlock(BlockModelGenerators generators, Block candle, Block cheese) {
        var cheeseCake = ModelTemplates.CANDLE_CAKE.create(cheese, candleCheeseBlock(candle, false), generators.modelOutput);
        var litCheeseCake = ModelTemplates.CANDLE_CAKE.createWithSuffix(cheese, "_lit", candleCheeseBlock(candle, true), generators.modelOutput);
        generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(cheese).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, litCheeseCake, cheeseCake)));
    }

    private void createLaunchPadBlock(Block pad, BlockModelGenerators generator) {
        var centerModel = ModelLocationUtils.getModelLocation(pad, "_center");
        var corner = GCModelTemplates.ROCKET_LAUNCH_PAD_PART.createWithSuffix(pad, "_corner", rocketLaunchPadPart(pad, "_corner"), generator.modelOutput);
        var side = GCModelTemplates.ROCKET_LAUNCH_PAD_PART.createWithSuffix(pad, "_side", rocketLaunchPadPart(pad, "_side"), generator.modelOutput);
        var defaultModel = Variant.variant().with(VariantProperties.MODEL, centerModel);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(pad).with(PropertyDispatch.property(AbstractLaunchPad.PART)
                .select(AbstractLaunchPad.Part.NONE, defaultModel)
                .select(AbstractLaunchPad.Part.CENTER, defaultModel)
                .select(AbstractLaunchPad.Part.NORTH_WEST, Variant.variant().with(VariantProperties.MODEL, corner))
                .select(AbstractLaunchPad.Part.NORTH_EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.MODEL, corner))
                .select(AbstractLaunchPad.Part.SOUTH_WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.MODEL, corner))
                .select(AbstractLaunchPad.Part.SOUTH_EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.MODEL, corner))
                .select(AbstractLaunchPad.Part.NORTH, Variant.variant().with(VariantProperties.MODEL, side))
                .select(AbstractLaunchPad.Part.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.MODEL, side))
                .select(AbstractLaunchPad.Part.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.MODEL, side))
                .select(AbstractLaunchPad.Part.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.MODEL, side))
        ));
        generator.delegateItemModel(pad, centerModel);
    }

    private void createCavernousVines(BlockModelGenerators generator) {
        var cavernousVines = GCBlocks.CAVERNOUS_VINES;
        var cavernousVinesPlant = GCBlocks.CAVERNOUS_VINES_PLANT;
        generator.skipAutoItemBlock(cavernousVinesPlant);
        generator.createSimpleFlatItemModel(cavernousVines);
        var resourceLocation = generator.createSuffixedVariant(cavernousVines, "", ModelTemplates.CROSS, TextureMapping::cross);
        var resourceLocation2 = generator.createSuffixedVariant(cavernousVines, "_poisonous", ModelTemplates.CROSS, TextureMapping::cross);
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(cavernousVines).with(BlockModelGenerators.createBooleanModelDispatch(CavernousVines.POISONOUS, resourceLocation2, resourceLocation)));
        var resourceLocation3 = generator.createSuffixedVariant(cavernousVinesPlant, "", ModelTemplates.CROSS, TextureMapping::cross);
        var resourceLocation4 = generator.createSuffixedVariant(cavernousVinesPlant, "_poisonous", ModelTemplates.CROSS, TextureMapping::cross);
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(cavernousVinesPlant).with(BlockModelGenerators.createBooleanModelDispatch(CavernousVines.POISONOUS, resourceLocation4, resourceLocation3)));
    }

    private ResourceLocation blockTextureWithSuffix(Block block, String suffix) {
        ResourceLocation resourceLocation = BuiltInRegistries.BLOCK.getKey(block);
        return resourceLocation.withPrefix("block/").withSuffix(suffix);
    }

    public final void createOlivineCluster(BlockModelGenerators generator, Block block) {
        ResourceLocation verticalModel = ModelTemplates.CROSS.createWithSuffix(block, "_vertical", TextureMapping.cross(blockTextureWithSuffix(block, "_vertical")), generator.modelOutput);
        ResourceLocation horizontalModel = ModelTemplates.CROSS.createWithSuffix(block, "_horizontal", TextureMapping.cross(blockTextureWithSuffix(block, "_horizontal")), generator.modelOutput);
        generator.skipAutoItemBlock(block);
        generator.blockStateOutput
                .accept(
                        MultiVariantGenerator.multiVariant(block)
                                .with(PropertyDispatch.property(BlockStateProperties.FACING)
                                        .select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.MODEL, verticalModel))
                                        .select(Direction.UP, Variant.variant()
                                                .with(VariantProperties.MODEL, verticalModel))
                                        .select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                                .with(VariantProperties.MODEL, horizontalModel))
                                        .select(
                                                Direction.SOUTH,
                                                Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                                        .with(VariantProperties.MODEL, horizontalModel))
                                        .select(
                                                Direction.WEST,
                                                Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                                        .with(VariantProperties.MODEL, horizontalModel))
                                        .select(
                                                Direction.EAST,
                                                Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                                        .with(VariantProperties.MODEL, horizontalModel))
                                )
                );
    }

    private static TextureMapping rocketLaunchPadPart(Block block, String suffix) {
        return new TextureMapping()
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block, suffix))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, suffix))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block));
    }

    private static TextureMapping candleCheeseBlock(Block candle, boolean lit) {
        return new TextureMapping()
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(GCBlocks.MOON_CHEESE_WHEEL, "_side"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(GCBlocks.MOON_CHEESE_WHEEL))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(GCBlocks.MOON_CHEESE_WHEEL))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(GCBlocks.MOON_CHEESE_WHEEL, "_side"))
                .put(TextureSlot.CANDLE, TextureMapping.getBlockTexture(candle, lit ? "_lit" : ""));
    }

    private static TextureMapping color(ResourceLocation resourceLocation) {
        return new TextureMapping().put(GCTextureSlot.COLOR, resourceLocation);
    }
}