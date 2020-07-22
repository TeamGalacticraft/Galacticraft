/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.block;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.block.FluidBlock;
import com.hrznstudio.galacticraft.api.block.StairsBlock;
import com.hrznstudio.galacticraft.block.decoration.GratingBlock;
import com.hrznstudio.galacticraft.block.decoration.LightPanelBlock;
import com.hrznstudio.galacticraft.block.decoration.MoonCheeseBlock;
import com.hrznstudio.galacticraft.block.decoration.VacuumGlassBlock;
import com.hrznstudio.galacticraft.block.environment.*;
import com.hrznstudio.galacticraft.block.machines.*;
import com.hrznstudio.galacticraft.block.special.aluminumwire.tier1.AluminumWireBlock;
import com.hrznstudio.galacticraft.block.special.aluminumwire.tier1.SealableAluminumWireBlock;
import com.hrznstudio.galacticraft.block.special.fluidpipe.FluidPipeBlock;
import com.hrznstudio.galacticraft.block.special.walkway.Walkway;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.mixin.FireBlockAccessor;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Contract;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftBlocks {
//    static {
//        classloadTest();
//    }
//
//    private static void classloadTest() {
//        throw new RuntimeException();
//    }

    // Special Blocks
    public static final Block GLOWSTONE_TORCH = registerBlockWithoutItem(new GlowstoneTorchBlock(FabricBlockSettings.copy(Blocks.TORCH).lightLevel((state) -> 15).sounds(BlockSoundGroup.WOOD)), Constants.Blocks.GLOWSTONE_TORCH);
    public static final Block GLOWSTONE_WALL_TORCH = registerBlockWithoutItem(new GlowstoneWallTorchBlock(FabricBlockSettings.copy(GLOWSTONE_TORCH).dropsLike(GLOWSTONE_TORCH)), Constants.Blocks.GLOWSTONE_WALL_TORCH);
    public static final Block UNLIT_TORCH = registerBlockWithoutItem(new UnlitTorchBlock(FabricBlockSettings.copy(Blocks.TORCH).lightLevel((state) -> 0)), Constants.Blocks.UNLIT_TORCH);
    public static final Block UNLIT_WALL_TORCH = registerBlockWithoutItem(new UnlitWallTorchBlock(FabricBlockSettings.copy(UNLIT_TORCH).dropsLike(UNLIT_TORCH)), Constants.Blocks.UNLIT_WALL_TORCH);
    public static final Block SOLAR_PANEL_PART = registerBlockWithoutItem(new SolarPanelPartBlock(FabricBlockSettings.of(Material.METAL).strength(-1.0F, 5.0F).dropsNothing().sounds(BlockSoundGroup.METAL)), Constants.Blocks.GENERIC_MULTIBLOCK_PART);

    // Liquids
    public static final FluidBlock CRUDE_OIL = registerBlockWithoutItem(new CrudeOilBlock(GalacticraftFluids.CRUDE_OIL, FabricBlockSettings.of(new FabricMaterialBuilder(MaterialColor.BLACK)
            .allowsMovement().destroyedByPiston().burnable().lightPassesThrough().notSolid().replaceable().liquid().build())
            .strength(100.0F, 1000.0F).dropsNothing()), Constants.Blocks.CRUDE_OIL);

    public static final FluidBlock FUEL = registerBlockWithoutItem(new FluidBlock(GalacticraftFluids.FUEL, FabricBlockSettings.of(new FabricMaterialBuilder(MaterialColor.YELLOW)
            .allowsMovement().destroyedByPiston().burnable().lightPassesThrough().notSolid().replaceable().liquid().build())
            .strength(50.0F, 50.0F).dropsNothing()), Constants.Blocks.FUEL);

    // Blocks
    public static final Block WALKWAY = registerBlock(new Walkway(FabricBlockSettings.of(Material.METAL).strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL)), Constants.Blocks.WALKWAY, GalacticraftItems.BLOCKS_GROUP);
    public static final Block WIRE_WALKWAY = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL)), Constants.Blocks.PIPE_WALKWAY, GalacticraftItems.BLOCKS_GROUP);
    public static final Block PIPE_WALKWAY = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL)), Constants.Blocks.WIRE_WALKWAY, GalacticraftItems.BLOCKS_GROUP);

    public static final Block MOON_TURF = registerBlock(new Block(FabricBlockSettings.of(Material.SOLID_ORGANIC, MaterialColor.LIGHT_GRAY).strength(0.5F, 0.5F)), Constants.Blocks.MOON_TURF, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(1.5F, 6.0F)), Constants.Blocks.MOON_ROCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_ROCK_WALL = registerBlock(new WallBlock(FabricBlockSettings.copy(MOON_ROCK).strength(2.0F, 2.0F)), Constants.Blocks.MOON_ROCK_WALL, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_BASALT = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(1.5F, 6.0F)), Constants.Blocks.MOON_BASALT, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_BASALT_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(5.0f, 5.0f)), Constants.Blocks.MOON_BASALT_SLAB, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_BASALT_STAIRS = registerBlock(new StairsBlock(MOON_BASALT.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(5.0f, 5.0f)), Constants.Blocks.MOON_BASALT_STAIRS, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_BASALT_WALL = registerBlock(new WallBlock(FabricBlockSettings.copy(MOON_BASALT).strength(2.0F, 2.0F)), Constants.Blocks.MOON_BASALT_WALL, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_BASALT_BRICKS = registerBlock(new Block(FabricBlockSettings.copy(MOON_BASALT).strength(2.0F, 2.0F)), Constants.Blocks.MOON_BASALT_BRICKS, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_CHEESE_LEAVES = registerBlock(new LeavesBlock(FabricBlockSettings.of(Material.LEAVES).strength(0.2F, 0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque()), Constants.Blocks.MOON_CHEESE_LEAVES, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_CHEESE_LOG = registerBlock(new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.YELLOW).strength(2.0F).sounds(BlockSoundGroup.WOOD)), Constants.Blocks.MOON_CHEESE_LOG, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_DIRT = registerBlock(new Block(FabricBlockSettings.of(Material.SOIL, MaterialColor.LIGHT_GRAY).strength(0.5F, 0.5F).sounds(BlockSoundGroup.GRAVEL)), Constants.Blocks.MOON_DIRT, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_DUNGEON_BRICKS = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(4.0F, 40.0F)), Constants.Blocks.MOON_DUNGEON_BRICK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MARS_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.DIRT).hardness(2.2F)), Constants.Blocks.MARS_SURFACE_ROCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MARS_SUB_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(2.6F)), Constants.Blocks.MARS_SUB_SURFACE_ROCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MARS_STONE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(3.0F)), Constants.Blocks.MARS_STONE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MARS_COBBLESTONE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(2.8F)), Constants.Blocks.MARS_COBBLESTONE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MARS_DUNGEON_BRICKS = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.GREEN).strength(4.0F, 40.0F)), Constants.Blocks.MARS_DUNGEON_BRICK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block ASTEROID_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).hardness(3.0F)), Constants.Blocks.ASTEROID_ROCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block ASTEROID_ROCK_1 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).hardness(3.0F)), Constants.Blocks.ASTEROID_ROCK_1, GalacticraftItems.BLOCKS_GROUP);
    public static final Block ASTEROID_ROCK_2 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).hardness(3.0F)), Constants.Blocks.ASTEROID_ROCK_2, GalacticraftItems.BLOCKS_GROUP);
    public static final Block SOFT_VENUS_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F)), Constants.Blocks.SOFT_VENUS_ROCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block HARD_VENUS_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F)), Constants.Blocks.HARD_VENUS_ROCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block SCORCHED_VENUS_ROCK = registerBlock(new ScorchedRockBlock(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F)), Constants.Blocks.SCORCHED_VENUS_ROCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block VOLCANIC_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(2.2F, 0.5F)), Constants.Blocks.VOLCANIC_ROCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block PUMICE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F)), Constants.Blocks.PUMICE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block VAPOR_SPOUT = registerBlock(new VaporSpoutBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).dropsNothing().strength(1.5F, 2.0F)), Constants.Blocks.VAPOR_SPOUT, GalacticraftItems.BLOCKS_GROUP);
    public static final Block TIN_DECORATION_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F)), Constants.Blocks.TIN_DECORATION, GalacticraftItems.BLOCKS_GROUP);
    public static final Block DETAILED_TIN_DECORATION_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F)), Constants.Blocks.DETAILED_TIN_DECORATION, GalacticraftItems.BLOCKS_GROUP);
    public static final Block DARK_DECORATION_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BLACK).strength(2.0F, 2.0F)), Constants.Blocks.DARK_DECORATION, GalacticraftItems.BLOCKS_GROUP);
    public static final Block GRATING = registerBlock(new GratingBlock(FabricBlockSettings.of(Material.METAL, MaterialColor.STONE).strength(2.5f, 6.0f).sounds(BlockSoundGroup.METAL)), Constants.Blocks.GRATING, GalacticraftItems.BLOCKS_GROUP);
    public static final Block ALUMINUM_WIRE = registerBlock(new AluminumWireBlock(FabricBlockSettings.copy(Blocks.WHITE_WOOL)), Constants.Blocks.ALUMINUM_WIRE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block SEALABLE_ALUMINUM_WIRE = registerBlock(new SealableAluminumWireBlock(FabricBlockSettings.copy(TIN_DECORATION_BLOCK)), Constants.Blocks.SEALABLE_ALUMINUM_WIRE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block FLUID_PIPE = registerBlock(new FluidPipeBlock(FabricBlockSettings.of(Material.GLASS).breakByHand(true).sounds(BlockSoundGroup.GLASS)), Constants.Blocks.FLUID_PIPE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block SQUARE_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL)), Constants.Blocks.SQUARE_LIGHT_PANEL, GalacticraftItems.BLOCKS_GROUP);
    public static final Block SPOTLIGHT_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 3.0f), Constants.Blocks.SPOTLIGHT_LIGHT_PANEL, GalacticraftItems.BLOCKS_GROUP);
    public static final Block LINEAR_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 5.0f), Constants.Blocks.LINEAR_LIGHT_PANEL, GalacticraftItems.BLOCKS_GROUP);
    public static final Block DASHED_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 1.0f), Constants.Blocks.DASHED_LIGHT_PANEL, GalacticraftItems.BLOCKS_GROUP);
    public static final Block DIAGONAL_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 1.0f), Constants.Blocks.DIAGONAL_LIGHT_PANEL, GalacticraftItems.BLOCKS_GROUP);
    public static final Block VACUUM_GLASS = registerBlock(new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS)), Constants.Blocks.VACUUM_GLASS, GalacticraftItems.BLOCKS_GROUP);
    public static final Block CLEAR_VACUUM_GLASS = registerBlock(new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS)), Constants.Blocks.CLEAR_VACUUM_GLASS, GalacticraftItems.BLOCKS_GROUP);
    public static final Block STRONG_VACUUM_GLASS = registerBlock(new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS)), Constants.Blocks.STRONG_VACUUM_GLASS, GalacticraftItems.BLOCKS_GROUP);
    public static final Block TIN_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F)), Constants.Blocks.TIN_DECORATION_SLAB, GalacticraftItems.BLOCKS_GROUP);
    public static final Block TIN_DECORATION_SLAB_1 = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F)), Constants.Blocks.DETAILED_TIN_DECORATION_SLAB, GalacticraftItems.BLOCKS_GROUP);
    public static final Block DARK_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0f, 2.0f)), Constants.Blocks.DARK_DECORATION_SLAB, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MARS_COBBLESTONE_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0f, 2.0f)), Constants.Blocks.MARS_COBBLESTONE_SLAB, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MARS_DUNGEON_BRICKS_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE).strength(5.0f, 5.0f)), Constants.Blocks.MARS_DUNGEON_BRICK_SLAB, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_DUNGEON_BRICKS_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE).strength(5.0f, 5.0f)), Constants.Blocks.MOON_DUNGEON_BRICK_SLAB, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_ROCK_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(5.0f, 5.0f)), Constants.Blocks.MOON_ROCK_SLAB, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_ROCK_STAIRS = registerBlock(new StairsBlock(MOON_ROCK.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(5.0f, 5.0f)), Constants.Blocks.MOON_ROCK_STAIRS, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_DUNGEON_BRICKS_STAIRS = registerBlock(new StairsBlock(MOON_DUNGEON_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(5.0f, 5.0f)), Constants.Blocks.MOON_DUNGEON_BRICK_STAIRS, GalacticraftItems.BLOCKS_GROUP);
    public static final Block TIN_DECORATION_STAIRS = registerBlock(new StairsBlock(TIN_DECORATION_BLOCK.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0f, 2.0f)), Constants.Blocks.TIN_DECORATION_STAIRS, GalacticraftItems.BLOCKS_GROUP);
    public static final Block DETAILED_TIN_DECORATION_STAIRS = registerBlock(new StairsBlock(DETAILED_TIN_DECORATION_BLOCK.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0f, 2.0f)), Constants.Blocks.DETAILED_TIN_DECORATION_STAIRS, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MARS_DUNGEON_BRICKS_STAIRS = registerBlock(new StairsBlock(MARS_DUNGEON_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.GREEN).strength(5.0f, 5.0f)), Constants.Blocks.MARS_DUNGEON_BRICK_STAIRS, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MARS_COBBLESTONE_STAIRS = registerBlock(new StairsBlock(MARS_COBBLESTONE.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(2.8f)), Constants.Blocks.MARS_COBBLESTONE_STAIRS, GalacticraftItems.BLOCKS_GROUP);
    public static final Block TIN_DECORATION_WALL = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE).strength(2.0F, 2.0F)), Constants.Blocks.TIN_DECORATION_WALL, GalacticraftItems.BLOCKS_GROUP);
    public static final Block DETAILED_TIN_DECORATION_WALL = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE).strength(2.0F, 2.0F)), Constants.Blocks.DETAILED_TIN_DECORATION_WALL, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_DUNGEON_BRICKS_WALL = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(5.0F, 5.0F)), Constants.Blocks.MOON_DUNGEON_BRICK_WALL, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MARS_COBBLESTONE_WALL = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F)), Constants.Blocks.MARS_COBBLESTONE_WALL, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MARS_DUNGEON_BRICKS_WALL = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.GREEN).strength(5.0F, 5.0F)), Constants.Blocks.MARS_DUNGEON_BRICK_WALL, GalacticraftItems.BLOCKS_GROUP);
    public static final Block SILICON_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.SILICON_ORE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block ASTEROID_ALUMINUM_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.ASTEROID_ALUMINUM_ORE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_CHEESE_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.MOON_CHEESE_ORE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_CHEESE_BLOCK = registerBlock(new MoonCheeseBlock(FabricBlockSettings.of(Material.CAKE)), Constants.Blocks.MOON_CHEESE_BLOCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_COPPER_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.MOON_COPPER_ORE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MARS_COPPER_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.MARS_COPPER_ORE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block DESH_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.DESH_ORE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block ILMENITE_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.ILMENITE_ORE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MARS_IRON_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.MARS_IRON_ORE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block ASTEROID_IRON_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.ASTEROID_IRON_ORE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_TIN_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.MOON_TIN_ORE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MARS_TIN_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.MARS_TIN_ORE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block GALENA_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.GALENA_ORE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block SILICON_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.SILICON_BLOCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block SOLID_METEORIC_IRON_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.SOLID_METEORIC_IRON_BLOCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block DESH_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.DESH_BLOCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block TITANIUM_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.TITANIUM_BLOCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block LEAD_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.LEAD_BLOCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block LUNAR_SAPPHIRE_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(5.0F, 6.0F).sounds(BlockSoundGroup.STONE)), Constants.Blocks.LUNAR_SAPPHIRE_BLOCK, GalacticraftItems.BLOCKS_GROUP);
    public static final Block CAVERNOUS_VINE = registerBlock(new CavernousVineBlock(FabricBlockSettings.of(Material.CACTUS, MaterialColor.GREEN).dropsNothing().noCollision().lightLevel(0).sounds(BlockSoundGroup.GRASS).ticksRandomly()), Constants.Blocks.CAVERNOUS_VINE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block POISONOUS_CAVERNOUS_VINE = registerBlock(new CavernousVineBlockPoisonous(FabricBlockSettings.of(Material.CACTUS, MaterialColor.GREEN).dropsNothing().noCollision().lightLevel(3).sounds(BlockSoundGroup.GRASS).ticksRandomly()), Constants.Blocks.POISONOUS_CAVERNOUS_VINE, GalacticraftItems.BLOCKS_GROUP);
    public static final Block MOON_BERRY_BUSH = registerBlock(new MoonBerryBushBlock(FabricBlockSettings.of(Material.PLANT, MaterialColor.GREEN).dropsNothing().noCollision().lightLevel(3).sounds(BlockSoundGroup.SWEET_BERRY_BUSH).ticksRandomly()), Constants.Blocks.MOON_BERRY_BUSH, GalacticraftItems.BLOCKS_GROUP);

    // Machines
    public static final ConfigurableElectricMachineBlock CIRCUIT_FABRICATOR = registerBlock(new CircuitFabricatorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.CIRCUIT_FABRICATOR, GalacticraftItems.MACHINES_GROUP);
    public static final CompressorBlock COMPRESSOR = registerBlock(new CompressorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.COMPRESSOR, GalacticraftItems.MACHINES_GROUP);
    public static final ConfigurableElectricMachineBlock ELECTRIC_COMPRESSOR = registerBlock(new ElectricCompressorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.ELECTRIC_COMPRESSOR, GalacticraftItems.MACHINES_GROUP);
    public static final ConfigurableElectricMachineBlock COAL_GENERATOR = registerBlock(new CoalGeneratorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.COAL_GENERATOR, GalacticraftItems.MACHINES_GROUP);
    public static final ConfigurableElectricMachineBlock BASIC_SOLAR_PANEL = registerBlock(new BasicSolarPanelBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.BASIC_SOLAR_PANEL, GalacticraftItems.MACHINES_GROUP);
    public static final ConfigurableElectricMachineBlock ADVANCED_SOLAR_PANEL = registerBlock(new AdvancedSolarPanelBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.ADVANCED_SOLAR_PANEL, GalacticraftItems.MACHINES_GROUP);
    public static final ConfigurableElectricMachineBlock ENERGY_STORAGE_MODULE = registerBlock(new EnergyStorageModuleBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.ENERGY_STORAGE_MODULE, GalacticraftItems.MACHINES_GROUP);
    public static final ConfigurableElectricMachineBlock OXYGEN_COLLECTOR = registerBlock(new OxygenCollectorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.OXYGEN_COLLECTOR, GalacticraftItems.MACHINES_GROUP);
    public static final ConfigurableElectricMachineBlock REFINERY = registerBlock(new RefineryBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.REFINERY, GalacticraftItems.MACHINES_GROUP);

    public static void register() {
        ((FireBlockAccessor) Blocks.FIRE).callRegisterFlammableBlock(FUEL, 80, 80);
        ((FireBlockAccessor) Blocks.FIRE).callRegisterFlammableBlock(CRUDE_OIL, 80, 80);
    }

    private static <T extends Block> T registerBlock(T block, String id, ItemGroup group) {
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, id), block);
        BlockItem item = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, id), new BlockItem(Registry.BLOCK.get(new Identifier(Constants.MOD_ID, id)), new Item.Settings().group(group)));
        item.appendBlocks(Item.BLOCK_ITEMS, item);
        return block;
    }

    private static <T extends Block> T registerBlockWithoutItem(T block, String id) {
        return Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, id), block);
    }
}