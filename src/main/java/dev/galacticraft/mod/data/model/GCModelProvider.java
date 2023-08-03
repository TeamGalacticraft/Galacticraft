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

package dev.galacticraft.mod.data.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import com.google.common.collect.Maps;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import dev.galacticraft.mod.content.item.GCItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class GCModelProvider extends FabricModelProvider {
    private static final TexturedModel.Provider DETAILED_DECORATION = TexturedModel.createDefault(GCModelProvider::createDetailedTexture, ModelTemplates.CUBE_BOTTOM_TOP);

    public GCModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        // GENERATE ALL GCBlockFamilies
        generator.texturedModels = Maps.newHashMap(generator.texturedModels);

        putDetailedTextured(generator, GCBlocks.DETAILED_ALUMINUM_DECORATION);
        putDetailedTextured(generator, GCBlocks.DETAILED_BRONZE_DECORATION);
        putDetailedTextured(generator, GCBlocks.DETAILED_COPPER_DECORATION);
        putDetailedTextured(generator, GCBlocks.DETAILED_IRON_DECORATION);
        putDetailedTextured(generator, GCBlocks.DETAILED_METEORIC_IRON_DECORATION);
        putDetailedTextured(generator, GCBlocks.DETAILED_STEEL_DECORATION);
        putDetailedTextured(generator, GCBlocks.DETAILED_TIN_DECORATION);
        putDetailedTextured(generator, GCBlocks.DETAILED_TITANIUM_DECORATION);
        putDetailedTextured(generator, GCBlocks.DETAILED_DARK_DECORATION);

        GCBlockFamilies.getAllFamilies()
                .filter(BlockFamily::shouldGenerateModel)
                .forEach(blockFamily -> generator.family(blockFamily.getBaseBlock()).generateFor(blockFamily));

        // TORCHES
        generator.createNormalTorch(GCBlocks.GLOWSTONE_TORCH, GCBlocks.GLOWSTONE_WALL_TORCH);
        generator.createNormalTorch(GCBlocks.UNLIT_TORCH, GCBlocks.UNLIT_WALL_TORCH);

        // LANTERNS
        generator.createLantern(GCBlocks.GLOWSTONE_LANTERN);
        generator.createLantern(GCBlocks.UNLIT_LANTERN);

        // MOON NAUTRAL
        var textureMapping = new TextureMapping()
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(GCBlocks.MOON_DIRT))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(GCBlocks.MOON_TURF))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(GCBlocks.MOON_TURF, "_side"));
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(GCBlocks.MOON_TURF, ModelTemplates.CUBE_BOTTOM_TOP.create(GCBlocks.MOON_TURF, textureMapping, generator.modelOutput)));

        generator.createTrivialCube(GCBlocks.MOON_DIRT);
        createRotatedDelegate(generator, GCBlocks.MOON_DIRT_PATH);
        generator.createTrivialCube(GCBlocks.MOON_SURFACE_ROCK);

        generator.createNonTemplateModelBlock(GCBlocks.FALLEN_METEOR);

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
        generator.createNonTemplateModelBlock(GCBlocks.VAPOR_SPOUT);

        // MISC DECOR
//        generator.createNonTemplateModelBlock(GCBlocks.WALKWAY);
//        generator.createNonTemplateModelBlock(GCBlocks.PIPE_WALK WAY); // todo: autogenerated models
//        generator.createNonTemplateModelBlock(GCBlocks.WIRE_WALKWAY);
        generator.createNonTemplateHorizontalBlock(GCBlocks.TIN_LADDER);
        generator.createSimpleFlatItemModel(GCBlocks.TIN_LADDER);
//        generator.createNonTemplateModelBlock(GCBlocks.GRATING);

        // SPECIAL
//        generator.createNonTemplateModelBlock(GCBlocks.ALUMINUM_WIRE);
        generator.createTrivialCube(GCBlocks.SEALABLE_ALUMINUM_WIRE);
        generator.createTrivialCube(GCBlocks.HEAVY_SEALABLE_ALUMINUM_WIRE);
//        generator.createNonTemplateModelBlock(GCBlocks.GLASS_FLUID_PIPE);
        this.createRocketLaunchPadBlock(generator);
        generator.createNonTemplateModelBlock(GCBlocks.ROCKET_WORKBENCH);

        // LIGHT PANELS
        generator.createNonTemplateModelBlock(GCBlocks.SQUARE_LIGHT_PANEL); //todo
        generator.createNonTemplateModelBlock(GCBlocks.SPOTLIGHT_LIGHT_PANEL);
        generator.createNonTemplateModelBlock(GCBlocks.LINEAR_LIGHT_PANEL);
        generator.createNonTemplateModelBlock(GCBlocks.DASHED_LIGHT_PANEL);
        generator.createNonTemplateModelBlock(GCBlocks.DIAGONAL_LIGHT_PANEL);

        // VACUUM GLASS
        generator.createNonTemplateModelBlock(GCBlocks.VACUUM_GLASS); //todo
        generator.createNonTemplateModelBlock(GCBlocks.CLEAR_VACUUM_GLASS);
        generator.createNonTemplateModelBlock(GCBlocks.STRONG_VACUUM_GLASS);

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

        this.createCheeseBlock(generator);
        this.createCandleCheeseBlock(generator, Blocks.CANDLE, GCBlocks.CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.WHITE_CANDLE, GCBlocks.WHITE_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.ORANGE_CANDLE, GCBlocks.ORANGE_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.MAGENTA_CANDLE, GCBlocks.MAGENTA_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.LIGHT_BLUE_CANDLE, GCBlocks.LIGHT_BLUE_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.YELLOW_CANDLE, GCBlocks.YELLOW_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.LIME_CANDLE, GCBlocks.LIME_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.PINK_CANDLE, GCBlocks.PINK_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.GRAY_CANDLE, GCBlocks.GRAY_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.LIGHT_GRAY_CANDLE, GCBlocks.LIGHT_GRAY_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.CYAN_CANDLE, GCBlocks.CYAN_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.PURPLE_CANDLE, GCBlocks.PURPLE_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.BLUE_CANDLE, GCBlocks.BLUE_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.BROWN_CANDLE, GCBlocks.BROWN_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.GREEN_CANDLE, GCBlocks.GREEN_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.RED_CANDLE, GCBlocks.RED_CANDLE_MOON_CHEESE_BLOCK);
        this.createCandleCheeseBlock(generator, Blocks.BLACK_CANDLE, GCBlocks.BLACK_CANDLE_MOON_CHEESE_BLOCK);

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
//        generator.createNonTemplateModelBlock(GCBlocks.CAVERNOUS_VINE);
//        generator.createNonTemplateModelBlock(GCBlocks.POISONOUS_CAVERNOUS_VINE);
        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(GCBlocks.MOON_BERRY_BUSH)
                        .with(PropertyDispatch.property(BlockStateProperties.AGE_3)
                                        .generate(integer -> Variant.variant().with(VariantProperties.MODEL, generator.createSuffixedVariant(GCBlocks.MOON_BERRY_BUSH, "_stage" + integer, ModelTemplates.CROSS, TextureMapping::cross)))
                        )
        );

        // DUMMY
        generator.createAirLikeBlock(GCBlocks.SOLAR_PANEL_PART, GCItems.BLUE_SOLAR_WAFER);
//        generator.createAirLikeBlock(GCBlocks.CRYOGENIC_CHAMBER_PART, GCItems.DARK_DECORATION); //todo

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

        generator.createTrivialCube(GCBlocks.AIR_LOCK_FRAME);
        generator.createNonTemplateModelBlock(GCBlocks.AIR_LOCK_CONTROLLER);
        generator.createNonTemplateModelBlock(GCBlocks.AIR_LOCK_SEAL);
    }

    private static void putDetailedTextured(BlockModelGenerators generator, Block detailedBlock) {
        generator.texturedModels.put(detailedBlock, DETAILED_DECORATION.get(detailedBlock));
    }

    private static void createRotatedDelegate(BlockModelGenerators generator, Block block) {
        generator.blockStateOutput.accept(BlockModelGenerators.createRotatedVariant(block, ModelLocationUtils.getModelLocation(block)));
    }

    private static void createMachineDelegate(BlockModelGenerators generator, Block block) { //todo: look into why we need this prefix
        generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, ModelLocationUtils.getModelLocation(block).withPrefix("models/")));
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
        generator.generateFlatItem(GCItems.MOON_BERRIES, ModelTemplates.FLAT_ITEM);
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

        // ROCKET PLATES
        generator.generateFlatItem(GCItems.TIER_1_HEAVY_DUTY_PLATE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TIER_2_HEAVY_DUTY_PLATE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TIER_3_HEAVY_DUTY_PLATE, ModelTemplates.FLAT_ITEM);

        // ARMOR
        generator.generateFlatItem(GCItems.HEAVY_DUTY_HELMET, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.HEAVY_DUTY_CHESTPLATE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.HEAVY_DUTY_LEGGINGS, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.HEAVY_DUTY_BOOTS, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.DESH_HELMET, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.DESH_CHESTPLATE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.DESH_LEGGINGS, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.DESH_BOOTS, ModelTemplates.FLAT_ITEM);

        generator.generateFlatItem(GCItems.TITANIUM_HELMET, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TITANIUM_CHESTPLATE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TITANIUM_LEGGINGS, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TITANIUM_BOOTS, ModelTemplates.FLAT_ITEM);

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

        // BATTERIES
        generator.generateFlatItem(GCItems.BATTERY, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.INFINITE_BATTERY, GCItems.BATTERY, ModelTemplates.FLAT_ITEM);

        //FLUID BUCKETS
        generator.generateFlatItem(GCItems.CRUDE_OIL_BUCKET, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.FUEL_BUCKET, ModelTemplates.FLAT_ITEM);

        //GALACTICRAFT INVENTORY
        generator.generateFlatItem(GCItems.PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.ORANGE_PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.MAGENTA_PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.LIGHT_BLUE_PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.YELLOW_PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.LIME_PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.PINK_PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.GRAY_PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.LIGHT_GRAY_PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.CYAN_PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.PURPLE_PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.BLUE_PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.BROWN_PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.GREEN_PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.RED_PARACHUTE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.BLACK_PARACHUTE, ModelTemplates.FLAT_ITEM);

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
        generator.generateFlatItem(GCItems.ROCKET_FINS, ModelTemplates.FLAT_ITEM);
//        generator.generateFlatItem(GCItems.ROCKET, ModelTemplates.FLAT_ITEM);

        // SCHEMATICS
        generator.generateFlatItem(GCItems.BASIC_ROCKET_BODY_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.BASIC_ROCKET_BOTTOM_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.BASIC_ROCKET_CONE_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.BASIC_ROCKET_FINS_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TIER_2_ROCKET_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.CARGO_ROCKET_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.MOON_BUGGY_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.TIER_3_ROCKET_SCHEMATIC, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(GCItems.ASTRO_MINER_SCHEMATIC, ModelTemplates.FLAT_ITEM);
    }

    @Contract("_ -> new")
    private static @NotNull TextureMapping createDetailedTexture(@NotNull Block block) {
        var side = TextureMapping.getBlockTexture(block, "_side");
        return new TextureMapping()
                .put(TextureSlot.WALL, side)
                .put(TextureSlot.SIDE, side)
                .put(TextureSlot.TOP, new ResourceLocation(TextureMapping.getBlockTexture(block).toString().replace("detailed_", "")))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_top"));
    }

    private void createCheeseBlock(BlockModelGenerators generators) {
        generators.createSimpleFlatItemModel(GCItems.MOON_CHEESE_BLOCK);
        generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(GCBlocks.MOON_CHEESE_BLOCK).with(PropertyDispatch.property(BlockStateProperties.BITES)
                .select(0, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(GCBlocks.MOON_CHEESE_BLOCK)))
                .select(1, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(GCBlocks.MOON_CHEESE_BLOCK, "_slice1")))
                .select(2, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(GCBlocks.MOON_CHEESE_BLOCK, "_slice2")))
                .select(3, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(GCBlocks.MOON_CHEESE_BLOCK, "_slice3")))
                .select(4, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(GCBlocks.MOON_CHEESE_BLOCK, "_slice4")))
                .select(5, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(GCBlocks.MOON_CHEESE_BLOCK, "_slice5")))
                .select(6, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(GCBlocks.MOON_CHEESE_BLOCK, "_slice6")))));
    }

    private void createCandleCheeseBlock(BlockModelGenerators generators, Block candle, Block cheese) {
        var cheeseCake = ModelTemplates.CANDLE_CAKE.create(cheese, candleCheeseBlock(candle, false), generators.modelOutput);
        var litCheeseCake = ModelTemplates.CANDLE_CAKE.createWithSuffix(cheese, "_lit", candleCheeseBlock(candle, true), generators.modelOutput);
        generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(cheese).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, litCheeseCake, cheeseCake)));
    }

    private void createRocketLaunchPadBlock(BlockModelGenerators generator) {
        var block = GCBlocks.ROCKET_LAUNCH_PAD;
        var centerModel = ModelLocationUtils.getModelLocation(block, "_center");
        var corner = GCModelTemplates.ROCKET_LAUNCH_PAD_PART.createWithSuffix(block, "_corner", rocketLaunchPadPart("_corner"), generator.modelOutput);
        var side = GCModelTemplates.ROCKET_LAUNCH_PAD_PART.createWithSuffix(block, "_side", rocketLaunchPadPart("_side"), generator.modelOutput);
        var defaultModel = Variant.variant().with(VariantProperties.MODEL, centerModel);

        generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(PropertyDispatch.property(RocketLaunchPadBlock.PART)
                .select(RocketLaunchPadBlock.Part.NONE, defaultModel)
                .select(RocketLaunchPadBlock.Part.CENTER, defaultModel)
                .select(RocketLaunchPadBlock.Part.NORTH_WEST, Variant.variant().with(VariantProperties.MODEL, corner))
                .select(RocketLaunchPadBlock.Part.NORTH_EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.MODEL, corner))
                .select(RocketLaunchPadBlock.Part.SOUTH_WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.MODEL, corner))
                .select(RocketLaunchPadBlock.Part.SOUTH_EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.MODEL, corner))
                .select(RocketLaunchPadBlock.Part.NORTH, Variant.variant().with(VariantProperties.MODEL, side))
                .select(RocketLaunchPadBlock.Part.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.MODEL, side))
                .select(RocketLaunchPadBlock.Part.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.MODEL, side))
                .select(RocketLaunchPadBlock.Part.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.MODEL, side))
        ));
        generator.delegateItemModel(block, centerModel);
    }

    private static TextureMapping rocketLaunchPadPart(String suffix) {
        return new TextureMapping()
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(GCBlocks.ROCKET_LAUNCH_PAD, suffix))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(GCBlocks.ROCKET_LAUNCH_PAD, suffix))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(GCBlocks.ROCKET_LAUNCH_PAD))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(GCBlocks.ROCKET_LAUNCH_PAD));
    }

    private static TextureMapping candleCheeseBlock(Block block, boolean lit) {
        return new TextureMapping()
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(GCBlocks.MOON_CHEESE_BLOCK, "_side"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(GCBlocks.MOON_CHEESE_BLOCK))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(GCBlocks.MOON_CHEESE_BLOCK))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(GCBlocks.MOON_CHEESE_BLOCK, "_side"))
                .put(TextureSlot.CANDLE, TextureMapping.getBlockTexture(block, lit ? "_lit" : ""));
    }
}