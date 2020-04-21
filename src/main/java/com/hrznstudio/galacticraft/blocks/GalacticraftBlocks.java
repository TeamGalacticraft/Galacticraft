/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.blocks;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.blocks.decoration.CheeseBlock;
import com.hrznstudio.galacticraft.blocks.decoration.GratingBlock;
import com.hrznstudio.galacticraft.blocks.decoration.LightingPanelBlock;
import com.hrznstudio.galacticraft.blocks.decoration.VacuumGlassBlock;
import com.hrznstudio.galacticraft.blocks.environment.*;
import com.hrznstudio.galacticraft.blocks.fluid.CrudeOilBlock;
import com.hrznstudio.galacticraft.blocks.fluid.FuelBlock;
import com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel.BasicSolarPanelBlock;
import com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel.BasicSolarPanelPartBlock;
import com.hrznstudio.galacticraft.blocks.machines.bubbledistributor.BubbleDistributorBlock;
import com.hrznstudio.galacticraft.blocks.machines.circuitfabricator.CircuitFabricatorBlock;
import com.hrznstudio.galacticraft.blocks.machines.coalgenerator.CoalGeneratorBlock;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorBlock;
import com.hrznstudio.galacticraft.blocks.machines.electriccompressor.ElectricCompressorBlock;
import com.hrznstudio.galacticraft.blocks.machines.energystoragemodule.EnergyStorageModuleBlock;
import com.hrznstudio.galacticraft.blocks.machines.oxygencollector.OxygenCollectorBlock;
import com.hrznstudio.galacticraft.blocks.machines.refinery.RefineryBlock;
import com.hrznstudio.galacticraft.blocks.natural.ScorchedRockBlock;
import com.hrznstudio.galacticraft.blocks.natural.VaporSpoutBlock;
import com.hrznstudio.galacticraft.blocks.special.aluminumwire.tier1.AluminumWireBlock;
import com.hrznstudio.galacticraft.blocks.special.aluminumwire.tier1.SealableAluminumWireBlock;
import com.hrznstudio.galacticraft.blocks.special.walkway.Walkway;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftBlocks {

    // Special Blocks
    public static final Block GLOWSTONE_TORCH = registerBlockWithoutItem(new GlowstoneTorchBlock(FabricBlockSettings.copy(Blocks.TORCH).lightLevel(15).sounds(BlockSoundGroup.WOOD).build()), Constants.Blocks.GLOWSTONE_TORCH);
    public static final Block GLOWSTONE_WALL_TORCH = registerBlockWithoutItem(new GlowstoneWallTorchBlock(FabricBlockSettings.copy(GLOWSTONE_TORCH).dropsLike(GLOWSTONE_TORCH).build()), Constants.Blocks.GLOWSTONE_WALL_TORCH);
    public static final Block UNLIT_TORCH = registerBlockWithoutItem(new UnlitTorchBlock(FabricBlockSettings.copy(Blocks.TORCH).lightLevel(0).build()), Constants.Blocks.UNLIT_TORCH);
    public static final Block UNLIT_WALL_TORCH = registerBlockWithoutItem(new UnlitWallTorchBlock(FabricBlockSettings.copy(UNLIT_TORCH).dropsLike(UNLIT_TORCH).build()), Constants.Blocks.UNLIT_WALL_TORCH);
    public static final Block BASIC_SOLAR_PANEL_PART = registerBlockWithoutItem(new BasicSolarPanelPartBlock(FabricBlockSettings.of(Material.METAL).strength(-1.0F, 5.0F).dropsNothing().sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.BASIC_SOLAR_PANEL_PART);

    // Liquids
    public static final FluidBlock CRUDE_OIL = registerFlammableFluidBlock(new CrudeOilBlock(GalacticraftFluids.CRUDE_OIL, FabricBlockSettings.of(new FabricMaterialBuilder(MaterialColor.BLACK)
            .allowsMovement().destroyedByPiston().burnable().lightPassesThrough().notSolid().replaceable().liquid().build())
            .strength(100.0F, 1000.0F).dropsNothing().build()), Constants.Blocks.CRUDE_OIL);

    public static final FluidBlock FUEL = registerFlammableFluidBlock(new FuelBlock(GalacticraftFluids.FUEL, FabricBlockSettings.of(new FabricMaterialBuilder(MaterialColor.YELLOW)
            .allowsMovement().destroyedByPiston().burnable().lightPassesThrough().notSolid().replaceable().liquid().build())
            .strength(50.0F, 50.0F).dropsNothing().build()), Constants.Blocks.FUEL);

    public static ItemGroup BLOCKS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.Blocks.ITEM_GROUP_BLOCKS))
            // Set the tab icon
            .icon(() -> new ItemStack(GalacticraftBlocks.MOON_TURF))
            .build();
    // Blocks
    public static final Block WALKWAY = registerBlock(new Walkway(FabricBlockSettings.of(Material.METAL).strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.WALKWAY);
    public static final Block WIRE_WALKWAY = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.PIPE_WALKWAY);
    public static final Block PIPE_WALKWAY = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.WIRE_WALKWAY);

    public static final Block MOON_TURF = registerBlock(new Block(FabricBlockSettings.of(Material.ORGANIC, MaterialColor.LIGHT_GRAY).strength(0.5F, 0.5F).build()), Constants.Blocks.MOON_TURF);
    public static final Block MOON_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(1.5F, 6.0F).build()), Constants.Blocks.MOON_ROCK);
    public static final Block MOON_DIRT = registerBlock(new Block(FabricBlockSettings.of(Material.EARTH, MaterialColor.LIGHT_GRAY).strength(0.5F, 0.5F).sounds(BlockSoundGroup.GRAVEL).build()), Constants.Blocks.MOON_DIRT);
    public static final Block MOON_DUNGEON_BRICKS = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(4.0F, 40.0F).build()), Constants.Blocks.MOON_DUNGEON_BRICK);
    public static final Block MARS_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.DIRT).hardness(2.2F).build()), Constants.Blocks.MARS_SURFACE_ROCK);
    public static final Block MARS_SUB_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(2.6F).build()), Constants.Blocks.MARS_SUB_SURFACE_ROCK);
    public static final Block MARS_STONE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(3.0F).build()), Constants.Blocks.MARS_STONE);
    public static final Block MARS_COBBLESTONE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(2.8F).build()), Constants.Blocks.MARS_COBBLESTONE);
    public static final Block MARS_DUNGEON_BRICKS = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.GREEN).strength(4.0F, 40.0F).build()), Constants.Blocks.MARS_DUNGEON_BRICK);
    // Dense Ice has been replaced by Blue Ice
    //public static final Block DENSE_ICE = registerBlock(new Block(FabricBlockSettings.of(Material.ICE, MaterialColor.ICE).hardness(1.0F).slipperiness(0.90F).sounds(BlockSoundGroup.GLASS).build()), Constants.Blocks.DENSE_ICE);
    public static final Block ASTEROID_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).hardness(3.0F).build()), Constants.Blocks.ASTEROID_ROCK);
    public static final Block ASTEROID_ROCK_1 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).hardness(3.0F).build()), Constants.Blocks.ASTEROID_ROCK_1);
    public static final Block ASTEROID_ROCK_2 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).hardness(3.0F).build()), Constants.Blocks.ASTEROID_ROCK_2);
    public static final Block SOFT_VENUS_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F).build()), Constants.Blocks.SOFT_VENUS_ROCK);
    public static final Block HARD_VENUS_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F).build()), Constants.Blocks.HARD_VENUS_ROCK);
    public static final Block SCORCHED_VENUS_ROCK = registerBlock(new ScorchedRockBlock(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F).build()), Constants.Blocks.SCORCHED_VENUS_ROCK);
    public static final Block VOLCANIC_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(2.2F, 0.5F).build()), Constants.Blocks.VOLCANIC_ROCK);
    public static final Block PUMICE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F).build()), Constants.Blocks.PUMICE);
    public static final Block VAPOR_SPOUT = registerBlock(new VaporSpoutBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).dropsNothing().strength(1.5F, 2.0F).build()), Constants.Blocks.VAPOR_SPOUT);
    public static final Block TIN_DECORATION_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F).build()), Constants.Blocks.TIN_DECORATION);
    public static final Block DETAILED_TIN_DECORATION_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F).build()), Constants.Blocks.DETAILED_TIN_DECORATION);
    public static final Block DARK_DECORATION_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BLACK).strength(2.0F, 2.0F).build()), Constants.Blocks.DARK_DECORATION);
    public static final Block GRATING = registerBlock(new GratingBlock(FabricBlockSettings.of(Material.METAL, MaterialColor.STONE).strength(2.5f, 6.0f).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.GRATING);
    public static final Block ALUMINUM_WIRE = registerBlock(new AluminumWireBlock(FabricBlockSettings.copy(Blocks.WHITE_WOOL).build()), Constants.Blocks.ALUMINUM_WIRE);
    public static final Block SEALABLE_ALUMINUM_WIRE = registerBlock(new SealableAluminumWireBlock(FabricBlockSettings.copy(TIN_DECORATION_BLOCK).build()), Constants.Blocks.SEALABLE_ALUMINUM_WIRE);
    public static final Block OXYGEN_PIPE = registerBlock(new Block(FabricBlockSettings.of(Material.WOOL).breakByHand(true).sounds(BlockSoundGroup.GLASS).build()), Constants.Blocks.OXYGEN_PIPE);
    public static final Block SQUARE_LIGHTING_PANEL = registerBlock(new LightingPanelBlock(FabricBlockSettings.of(Material.METAL).build()), Constants.Blocks.SQUARE_LIGHTING_PANEL);
    public static final Block SPOTLIGHT_LIGHTING_PANEL = registerBlock(new LightingPanelBlock(FabricBlockSettings.of(Material.METAL).build(), 3.0f), Constants.Blocks.SPOTLIGHT_LIGHTING_PANEL);
    public static final Block LINEAR_LIGHTING_PANEL = registerBlock(new LightingPanelBlock(FabricBlockSettings.of(Material.METAL).build(), 5.0f), Constants.Blocks.LINEAR_LIGHTING_PANEL);
    public static final Block DARK_LIGHTING_PANEL = registerBlock(new LightingPanelBlock(FabricBlockSettings.of(Material.METAL).build(), 1.0f), Constants.Blocks.DARK_LIGHTING_PANEL);
    public static final Block DARK_ANGLE_LIGHTING_PANEL = registerBlock(new LightingPanelBlock(FabricBlockSettings.of(Material.METAL).build(), 1.0f), Constants.Blocks.DARK_ANGLE_LIGHTING_PANEL);
    public static final Block VACUUM_GLASS = registerBlock(new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).build()), Constants.Blocks.VACUUM_GLASS);
    public static final Block CLEAR_VACUUM_GLASS = registerBlock(new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).build()), Constants.Blocks.CLEAR_VACUUM_GLASS);
    public static final Block TIN_VACUUM_GLASS = registerBlock(new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).build()), Constants.Blocks.TIN_VACUUM_GLASS);
    public static final Block STRONG_VACUUM_GLASS = registerBlock(new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).build()), Constants.Blocks.STRONG_VACUUM_GLASS);
    public static final Block TIN_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F).build()), Constants.Blocks.TIN_DECORATION_SLAB);
    public static final Block TIN_DECORATION_SLAB_1 = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F).build()), Constants.Blocks.DETAILED_TIN_DECORATION_SLAB);
    public static final Block DARK_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0f, 2.0f).build()), Constants.Blocks.DARK_DECORATION_SLAB);
    public static final Block MARS_COBBLESTONE_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0f, 2.0f).build()), Constants.Blocks.MARS_COBBLESTONE_SLAB);
    public static final Block MARS_DUNGEON_BRICKS_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE).strength(5.0f, 5.0f).build()), Constants.Blocks.MARS_DUNGEON_BRICK_SLAB);
    public static final Block MOON_DUNGEON_BRICKS_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE).strength(5.0f, 5.0f).build()), Constants.Blocks.MOON_DUNGEON_BRICK_SLAB);
    public static final Block MOON_ROCK_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(5.0f, 5.0f).build()), Constants.Blocks.MOON_ROCK_SLAB);
    public static final Block MOON_ROCK_STAIRS = registerBlock(new com.hrznstudio.galacticraft.api.block.StairsBlock(MOON_ROCK.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(5.0f, 5.0f).build()), Constants.Blocks.MOON_ROCK_STAIRS);
    public static final Block MOON_DUNGEON_BRICKS_STAIRS = registerBlock(new com.hrznstudio.galacticraft.api.block.StairsBlock(MOON_DUNGEON_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(5.0f, 5.0f).build()), Constants.Blocks.MOON_DUNGEON_BRICK_STAIRS);
    public static final Block TIN_DECORATION_STAIRS = registerBlock(new com.hrznstudio.galacticraft.api.block.StairsBlock(TIN_DECORATION_BLOCK.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0f, 2.0f).build()), Constants.Blocks.TIN_DECORATION_STAIRS);
    public static final Block DETAILED_TIN_DECORATION_STAIRS = registerBlock(new com.hrznstudio.galacticraft.api.block.StairsBlock(DETAILED_TIN_DECORATION_BLOCK.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0f, 2.0f).build()), Constants.Blocks.DETAILED_TIN_DECORATION_STAIRS);
    public static final Block MARS_DUNGEON_BRICKS_STAIRS = registerBlock(new com.hrznstudio.galacticraft.api.block.StairsBlock(MARS_DUNGEON_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.GREEN).strength(5.0f, 5.0f).build()), Constants.Blocks.MARS_DUNGEON_BRICK_STAIRS);
    public static final Block MARS_COBBLESTONE_STAIRS = registerBlock(new com.hrznstudio.galacticraft.api.block.StairsBlock(MARS_COBBLESTONE.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(2.8f).build()), Constants.Blocks.MARS_COBBLESTONE_STAIRS);
    public static final Block TIN_DECORATION_WALL = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE).strength(2.0F, 2.0F).build()), Constants.Blocks.TIN_DECORATION_WALL);
    public static final Block DETAILED_TIN_DECORATION_WALL = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE).strength(2.0F, 2.0F).build()), Constants.Blocks.DETAILED_TIN_DECORATION_WALL);
    public static final Block MOON_ROCK_WALL = registerBlock(new WallBlock(FabricBlockSettings.copy(MOON_ROCK).strength(2.0F, 2.0F).build()), Constants.Blocks.MOON_ROCK_WALL);
    public static final Block MOON_DUNGEON_BRICKS_WALL = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(5.0F, 5.0F).build()), Constants.Blocks.MOON_DUNGEON_BRICK_WALL);
    public static final Block MARS_COBBLESTONE_WALL = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F).build()), Constants.Blocks.MARS_COBBLESTONE_WALL);
    public static final Block MARS_DUNGEON_BRICKS_WALL = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.GREEN).strength(5.0F, 5.0F).build()), Constants.Blocks.MARS_DUNGEON_BRICK_WALL);
    public static final Block COPPER_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.COPPER_ORE);
    public static final Block TIN_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.TIN_ORE);
    public static final Block ALUMINUM_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.ALUMINUM_ORE);
    public static final Block SILICON_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.SILICON_ORE);
    public static final Block ASTEROID_ALUMINUM_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.ASTEROID_ALUMINUM_ORE);
    public static final Block CHEESE_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.CHEESE_ORE);
    public static final Block CHEESE_BLOCK = registerBlock(new CheeseBlock(FabricBlockSettings.of(Material.CAKE).build()), Constants.Blocks.CHEESE);
    public static final Block MOON_COPPER_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.MOON_COPPER_ORE);
    public static final Block MARS_COPPER_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.MARS_COPPER_ORE);
    public static final Block DESH_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.DESH_ORE);
    public static final Block ILMENITE_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.ILMENITE_ORE);
    public static final Block MARS_IRON_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.MARS_IRON_ORE);
    public static final Block ASTEROID_IRON_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.ASTEROID_IRON_ORE);
    public static final Block MOON_TIN_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.MOON_TIN_ORE);
    public static final Block MARS_TIN_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.MARS_TIN_ORE);
    public static final Block GALENA_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.GALENA_ORE);
    public static final Block COPPER_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.COPPER_BLOCK);
    public static final Block TIN_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.TIN_BLOCK);
    public static final Block ALUMINUM_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.ALUMINUM_BLOCK);
    public static final Block SILICON_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.SILICON_BLOCK);
    public static final Block SOLID_METEORIC_IRON_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.SOLID_METEORIC_IRON_BLOCK);
    public static final Block DESH_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.DESH_BLOCK);
    public static final Block TITANIUM_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.TITANIUM_BLOCK);
    public static final Block LEAD_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.LEAD_BLOCK);
    public static final Block OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.AIR).strength(10000.0F, 10000.0F).sounds(BlockSoundGroup.SNOW).build()), Constants.Blocks.OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK);
    public static final Block CAVERNOUS_VINE = registerBlock(new CavernousVineBlock(FabricBlockSettings.of(Material.CACTUS, MaterialColor.GREEN).dropsNothing().noCollision().lightLevel(0).sounds(BlockSoundGroup.GRASS).ticksRandomly().build()), Constants.Blocks.CAVERNOUS_VINE);
    public static final Block POISONOUS_CAVERNOUS_VINE = registerBlock(new CavernousVineBlockPoisonous(FabricBlockSettings.of(Material.CACTUS, MaterialColor.GREEN).dropsNothing().noCollision().lightLevel(3).sounds(BlockSoundGroup.GRASS).ticksRandomly().build()), Constants.Blocks.POISONOUS_CAVERNOUS_VINE);
    public static final Block MOON_BERRY_BUSH = registerBlock(new MoonBerryBushBlock(FabricBlockSettings.of(Material.PLANT, MaterialColor.GREEN).dropsNothing().noCollision().lightLevel(3).sounds(BlockSoundGroup.SWEET_BERRY_BUSH).ticksRandomly().build()), Constants.Blocks.MOON_BERRY_BUSH);

    // Machines
    public static ItemGroup MACHINES_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.Blocks.ITEM_GROUP_MACHINES))
            // Set the the tab icon
            .icon(() -> new ItemStack(GalacticraftBlocks.COAL_GENERATOR))
            .build();
    public static final Block CIRCUIT_FABRICATOR = registerMachine(new CircuitFabricatorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.CIRCUIT_FABRICATOR);
    public static final Block COMPRESSOR = registerMachine(new CompressorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.COMPRESSOR);
    public static final Block ELECTRIC_COMPRESSOR = registerMachine(new ElectricCompressorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.ELECTRIC_COMPRESSOR);
    public static final Block COAL_GENERATOR = registerMachine(new CoalGeneratorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.COAL_GENERATOR);
    public static final Block BASIC_SOLAR_PANEL = registerMachine(new BasicSolarPanelBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.BASIC_SOLAR_PANEL);
    public static final Block ENERGY_STORAGE_MODULE = registerMachine(new EnergyStorageModuleBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.ENERGY_STORAGE_MODULE);
    public static final Block OXYGEN_COLLECTOR = registerMachine(new OxygenCollectorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.OXYGEN_COLLECTOR);
    public static final Block REFINERY = registerMachine(new RefineryBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.REFINERY);
    public static final Block BUBBLE_DISTRIBUTOR = registerMachine(new BubbleDistributorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.OXYGEN_BUBBLE_DISTRIBUTOR);

    public static void register() {
    }

    private static Block registerBlockWithoutItem(Block block, String id) {
        return Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, id), block);
    }

    private static FluidBlock registerFlammableFluidBlock(FluidBlock block, String id) {
        FluidBlock registered =  Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, id), block);
        ((FireBlock) Blocks.FIRE).registerFlammableBlock(registered, 80, 80);
        return registered;
    }

    private static Block registerBlock(Block block, String id) {
        Block registered = registerBlockWithoutItem(block, id);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, id), new BlockItem(registered, new Item.Settings().group(BLOCKS_GROUP)));
        return registered;
    }

    private static Block registerMachine(Block block, String id) {
        Block registered = Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, id), block);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, id), new BlockItem(registered, new Item.Settings().group(MACHINES_GROUP)));
        return registered;
    }
}