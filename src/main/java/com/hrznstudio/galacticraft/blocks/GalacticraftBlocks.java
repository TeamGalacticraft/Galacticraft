package com.hrznstudio.galacticraft.blocks;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.blocks.GalacticraftStairsBlock;
import com.hrznstudio.galacticraft.blocks.decoration.CheeseBlock;
import com.hrznstudio.galacticraft.blocks.decoration.GratingBlock;
import com.hrznstudio.galacticraft.blocks.decoration.LightingPanelBlock;
import com.hrznstudio.galacticraft.blocks.decoration.VacuumGlassBlock;
import com.hrznstudio.galacticraft.blocks.environment.*;
import com.hrznstudio.galacticraft.blocks.fluid.CrudeOilBlock;
import com.hrznstudio.galacticraft.blocks.fluid.FuelBlock;
import com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel.BasicSolarPanelBlock;
import com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel.BasicSolarPanelPartBlock;
import com.hrznstudio.galacticraft.blocks.machines.circuitfabricator.CircuitFabricatorBlock;
import com.hrznstudio.galacticraft.blocks.machines.coalgenerator.CoalGeneratorBlock;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorBlock;
import com.hrznstudio.galacticraft.blocks.machines.electriccompressor.ElectricCompressorBlock;
import com.hrznstudio.galacticraft.blocks.machines.energystoragemodule.EnergyStorageModuleBlock;
import com.hrznstudio.galacticraft.blocks.machines.oxygencollector.OxygenCollectorBlock;
import com.hrznstudio.galacticraft.blocks.machines.refinery.RefineryBlock;
import com.hrznstudio.galacticraft.blocks.natural.ScorchedRockBlock;
import com.hrznstudio.galacticraft.blocks.natural.VaporSpoutBlock;
import com.hrznstudio.galacticraft.blocks.ore.SiliconOreBlock;
import com.hrznstudio.galacticraft.blocks.special.aluminumwire.AluminumWireBlock;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftBlocks {
    // Blocks
    public static final Block MOON_TURF = registerBlock(new Block(FabricBlockSettings.of(Material.ORGANIC, MaterialColor.LIGHT_GRAY).strength(0.5F, 0.5F).build()), Constants.Blocks.MOON_TURF);
    public static final Block MOON_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(1.5F, 6.0F).build()), Constants.Blocks.MOON_ROCK);
    public static final Block MOON_DIRT = registerBlock(new Block(FabricBlockSettings.of(Material.EARTH, MaterialColor.LIGHT_GRAY).strength(0.5F, 0.5F).sounds(BlockSoundGroup.GRAVEL).build()), Constants.Blocks.MOON_DIRT);
    public static final Block MOON_DUNGEON_BRICKS = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(4.0F, 40.0F).build()), Constants.Blocks.MARS_DUNGEON_BRICKS);
    public static final Block MARS_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.DIRT).hardness(2.2F).build()), Constants.Blocks.MARS_SURFACE_ROCK);
    public static final Block MARS_SUB_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(2.6F).build()), Constants.Blocks.MARS_SUB_SURFACE_ROCK);
    public static final Block MARS_STONE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(3.0F).build()), Constants.Blocks.MARS_STONE);
    public static final Block MARS_COBBLESTONE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(2.8F).build()), Constants.Blocks.MARS_COBBLESTONE);
    public static final Block MARS_DUNGEON_BRICKS = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.GREEN).strength(4.0F, 40.0F).build()), Constants.Blocks.MARS_DUNGEON_BRICKS);
    public static final Block DENSE_ICE = registerBlock(new Block(FabricBlockSettings.of(Material.ICE, MaterialColor.ICE).hardness(1.0F).friction(0.90F).sounds(BlockSoundGroup.GLASS).build()), Constants.Blocks.DENSE_ICE);
    public static final Block ASTEROID_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).hardness(3.0F).build()), Constants.Blocks.ASTEROID_ROCK);
    public static final Block ASTEROID_ROCK_1 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).hardness(3.0F).build()), Constants.Blocks.ASTEROID_ROCK_1);
    public static final Block ASTEROID_ROCK_2 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).hardness(3.0F).build()), Constants.Blocks.ASTEROID_ROCK_2);
    public static final Block VENUS_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F).build()), Constants.Blocks.VENUS_ROCK);
    public static final Block VENUS_ROCK_1 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F).build()), Constants.Blocks.VENUS_ROCK_1);
    public static final Block VENUS_ROCK_2 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F).build()), Constants.Blocks.VENUS_ROCK_2);
    public static final Block VENUS_ROCK_3 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F).build()), Constants.Blocks.VENUS_ROCK_3);
    public static final Block VENUS_ROCK_SCORCHED = registerBlock(new ScorchedRockBlock(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F).build()), Constants.Blocks.VENUS_ROCK_SCORCHED);
    public static final Block VOLCANIC_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(2.2F, 0.5F).build()), Constants.Blocks.VOLCANIC_ROCK);
    public static final Block SCORCHED_ROCK = registerBlock(new ScorchedRockBlock(FabricBlockSettings.of(Material.STONE).ticksRandomly().build()), Constants.Blocks.SCORCHED_ROCK);
    public static final Block PUMICE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).resistance(1.0F).build()), Constants.Blocks.PUMICE);
    public static final Block VAPOR_SPOUT = registerBlock(new VaporSpoutBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).dropsNothing().strength(1.5F, 2.0F).build()), Constants.Blocks.VAPOR_SPOUT);
    public static final Block TIN_DECORATION_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F).build()), Constants.Blocks.TIN_DECORATION);
    public static final Block TIN_DECORATION_BLOCK_1 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F).build()), Constants.Blocks.TIN_DECORATION_BLOCK_1);
    public static final Block DARK_DECORATION_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BLACK).strength(2.0F, 2.0F).build()), Constants.Blocks.DARK_DECORATION);
    public static final Block GRATING = registerBlock(new GratingBlock(FabricBlockSettings.of(Material.METAL, MaterialColor.STONE).strength(2.5f, 6.0f).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.GRATING);
    public static final Block ALUMINUM_WIRE = registerBlock(new AluminumWireBlock(FabricBlockSettings.copy(Blocks.WHITE_WOOL).build()), Constants.Blocks.ALUMINUM_WIRE);
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
    public static final Block WALKWAY = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.WALKWAY);
    public static final Block WIRE_WALKWAY = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.PIPE_WALKWAY);
    public static final Block PIPE_WALKWAY = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.WIRE_WALKWAY);
    public static final Block TIN_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F).build()), Constants.Blocks.TIN_DECORATION_SLAB);
    public static final Block TIN_DECORATION_SLAB_1 = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F).build()), Constants.Blocks.TIN_DECORATION_SLAB_1);
    public static final Block DARK_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0f, 2.0f).build()), Constants.Blocks.DARK_DECORATION_SLAB);
    public static final Block MARS_COBBLESTONE_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0f, 2.0f).build()), Constants.Blocks.MARS_COBBLESTONE_SLAB);
    public static final Block MARS_DUNGEON_BRICKS_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE).strength(5.0f, 5.0f).build()), Constants.Blocks.MARS_DUNGEON_BRICKS_SLAB);
    public static final Block MOON_DUNGEON_BRICKS_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE).strength(5.0f, 5.0f).build()), Constants.Blocks.MOON_DUNGEON_BRICKS_SLAB);
    public static final Block MOON_ROCK_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(5.0f, 5.0f).build()), Constants.Blocks.MOON_ROCK_SLAB);
    public static final Block MOON_ROCK_STAIRS = registerBlock(new GalacticraftStairsBlock(MOON_ROCK.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(5.0f, 5.0f).build()), Constants.Blocks.MOON_ROCK_STAIRS);
    public static final Block MOON_DUNGEON_BRICKS_STAIRS = registerBlock(new GalacticraftStairsBlock(MOON_DUNGEON_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(5.0f, 5.0f).build()), Constants.Blocks.MOON_DUNGEON_BRICKS_STAIRS);
    public static final Block TIN_DECORATION_STAIRS = registerBlock(new GalacticraftStairsBlock(TIN_DECORATION_BLOCK.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0f, 2.0f).build()), Constants.Blocks.TIN_DECORATION_STAIRS);
    public static final Block TIN_DECORATION_STAIRS_1 = registerBlock(new GalacticraftStairsBlock(TIN_DECORATION_BLOCK_1.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0f, 2.0f).build()), Constants.Blocks.TIN_DECORATION_STAIRS_1);
    public static final Block MARS_DUNGEON_BRICKS_STAIRS = registerBlock(new GalacticraftStairsBlock(MARS_DUNGEON_BRICKS.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.GREEN).strength(5.0f, 5.0f).build()), Constants.Blocks.MARS_DUNGEON_BRICKS_STAIRS);
    public static final Block MARS_COBBLESTONE_STAIRS = registerBlock(new GalacticraftStairsBlock(MARS_COBBLESTONE.getDefaultState(), FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(2.8f).build()), Constants.Blocks.MARS_COBBLESTONE_STAIRS);
    public static final Block TIN_WALL = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE).strength(2.0F, 2.0F).build()), Constants.Blocks.TIN_DECORATION_WALL);
    public static final Block TIN_WALL_1 = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE).strength(2.0F, 2.0F).build()), Constants.Blocks.TIN_DECORATION_WALL_1);
    public static final Block MOON_ROCK_WALL = registerBlock(new WallBlock(FabricBlockSettings.copy(MOON_ROCK).strength(2.0F, 2.0F).build()), Constants.Blocks.MOON_ROCK_WALL);
    public static final Block MOON_DUNGEON_BRICKS_WALL = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(5.0F, 5.0F).build()), Constants.Blocks.MOON_DUNGEON_BRICKS_WALL);
    public static final Block MARS_COBBLESTONE_WALL = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F).build()), Constants.Blocks.MARS_COBBLESTONE_WALL);
    public static final Block MARS_DUNGEON_BRICKS_WALL = registerBlock(new WallBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.GREEN).strength(5.0F, 5.0F).build()), Constants.Blocks.MARS_DUNGEON_BRICKS_WALL);
    public static final Block COPPER_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.COPPER_ORE);
    public static final Block TIN_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.TIN_ORE);
    public static final Block ALUMINUM_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.ALUMINUM_ORE);
    public static final Block SILICON_ORE = registerBlock(new SiliconOreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F).build()), Constants.Blocks.SILICON_ORE);
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
    public static final Block UNLIT_TORCH = registerBlock(new UnlitTorchBlock(FabricBlockSettings.copy(Blocks.TORCH).lightLevel(0).build()), Constants.Blocks.UNLIT_TORCH);
    public static final Block GLOWSTONE_TORCH = registerBlock(new GlowstoneTorchBlock(FabricBlockSettings.copy(Blocks.TORCH).lightLevel(15).sounds(BlockSoundGroup.WOOD).build()), Constants.Blocks.GLOWSTONE_TORCH);
    public static final Block GLOWSTONE_WALL_TORCH = registerBlock(new GlowstoneWallTorchBlock(FabricBlockSettings.copy(Blocks.TORCH).lightLevel(15).dropsLike(GLOWSTONE_TORCH).sounds(BlockSoundGroup.WOOD).build()), Constants.Blocks.GLOWSTONE_WALL_TORCH);
    public static final Block CAVERNOUS_VINE = registerBlock(new CavernousVineBlock(FabricBlockSettings.of(Material.CACTUS, MaterialColor.GREEN).dropsNothing().noCollision().lightLevel(0).sounds(BlockSoundGroup.GRASS).ticksRandomly().build()), Constants.Blocks.CAVERNOUS_VINE);
    public static final Block POISONOUS_CAVERNOUS_VINE = registerBlock(new CavernousVineBlockPoisonous(FabricBlockSettings.of(Material.CACTUS, MaterialColor.GREEN).dropsNothing().noCollision().lightLevel(3).sounds(BlockSoundGroup.GRASS).ticksRandomly().build()), Constants.Blocks.POISONOUS_CAVERNOUS_VINE);
    public static final Block MOON_BERRY_BUSH = registerBlock(new MoonBerryBushBlock(FabricBlockSettings.of(Material.PLANT, MaterialColor.GREEN).dropsNothing().noCollision().lightLevel(3).sounds(BlockSoundGroup.SWEET_BERRY_BUSH).ticksRandomly().build()), Constants.Blocks.MOON_BERRY_BUSH);
    // Machines
    public static final Block CIRCUIT_FABRICATOR = registerBlock(new CircuitFabricatorBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.CIRCUIT_FABRICATOR);
    public static final Block COMPRESSOR = registerBlock(new CompressorBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.COMPRESSOR);
    public static final Block ELECTRIC_COMPRESSOR = registerBlock(new ElectricCompressorBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.ELECTRIC_COMPRESSOR);
    public static final Block COAL_GENERATOR = registerBlock(new CoalGeneratorBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.COAL_GENERATOR);
    public static final Block BASIC_SOLAR_PANEL = registerBlock(new BasicSolarPanelBlock(FabricBlockSettings.of(Material.METAL).strength(1.0F, 3600000.0F).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.BASIC_SOLAR_PANEL);
    public static final Block BASIC_SOLAR_PANEL_PART = registerBlock(new BasicSolarPanelPartBlock(FabricBlockSettings.of(Material.METAL).strength(-1.0F, 3600000.0F).dropsNothing().sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.BASIC_SOLAR_PANEL_PART);
    public static final Block ENERGY_STORAGE_MODULE = registerBlock(new EnergyStorageModuleBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.ENERGY_STORAGE_MODULE);
    public static final Block OXYGEN_COLLECTOR = registerBlock(new OxygenCollectorBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.OXYGEN_COLLECTOR);
    public static final Block REFINERY = registerBlock(new RefineryBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).build()), Constants.Blocks.REFINERY);
    // Liquids
    public static final FluidBlock CRUDE_OIL = new CrudeOilBlock(GalacticraftFluids.STILL_CRUDE_OIL, FabricBlockSettings.of(Material.WATER).noCollision().build());
    public static final FluidBlock FUEL = new FuelBlock(GalacticraftFluids.STILL_FUEL, FabricBlockSettings.of(Material.WATER).noCollision().build());


    public static final BlockItem CRUDE_OIL_ITEM = registerBlockItem(new BlockItem(CRUDE_OIL, new Item.Settings()), Constants.Blocks.CRUDE_OIL);
    public static final BlockItem BASIC_SOLAR_PANEL_PART_ITEM = registerBlockItem(new BlockItem(BASIC_SOLAR_PANEL_PART, new Item.Settings()), Constants.Blocks.BASIC_SOLAR_PANEL_PART);

    private static final Marker BLOCKS = MarkerManager.getMarker("Blocks"); // Galacticraft/Blocks
    public static ItemGroup BLOCKS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.Blocks.ITEM_GROUP_BLOCKS))
            // Set the tab icon
            .icon(() -> new ItemStack(GalacticraftBlocks.MOON_TURF))
            .build();
    // Block Items
    public static final BlockItem MOON_TURF_ITEM = registerBlockItem(new BlockItem(MOON_TURF, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MOON_TURF);
    public static final BlockItem MOON_ROCK_ITEM = registerBlockItem(new BlockItem(MOON_ROCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MOON_ROCK);
    public static final BlockItem MOON_DIRT_ITEM = registerBlockItem(new BlockItem(MOON_DIRT, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MOON_DIRT);
    public static final BlockItem MOON_DUNGEON_BRICKS_ITEM = registerBlockItem(new BlockItem(MOON_DUNGEON_BRICKS, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MOON_DUNGEON_BRICKS);
    public static final BlockItem MARS_SURFACE_ROCK_ITEM = registerBlockItem(new BlockItem(MARS_SURFACE_ROCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MARS_SURFACE_ROCK);
    public static final BlockItem MARS_SUB_SURFACE_ROCK_ITEM = registerBlockItem(new BlockItem(MARS_SUB_SURFACE_ROCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MARS_SUB_SURFACE_ROCK);
    public static final BlockItem MARS_STONE_ITEM = registerBlockItem(new BlockItem(MARS_STONE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MARS_STONE);
    public static final BlockItem MARS_COBBLESTONE_ITEM = registerBlockItem(new BlockItem(MARS_COBBLESTONE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MARS_COBBLESTONE);
    public static final BlockItem MARS_DUNGEON_BRICKS_ITEM = registerBlockItem(new BlockItem(MARS_DUNGEON_BRICKS, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MARS_DUNGEON_BRICKS);
    public static final BlockItem DENSE_ICE_ITEM = registerBlockItem(new BlockItem(DENSE_ICE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.DENSE_ICE);
    public static final BlockItem ASTEROID_ROCK_ITEM = registerBlockItem(new BlockItem(ASTEROID_ROCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.ASTEROID_ROCK);
    public static final BlockItem ASTEROID_ROCK_ITEM_1 = registerBlockItem(new BlockItem(ASTEROID_ROCK_1, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.ASTEROID_ROCK_1);
    public static final BlockItem ASTEROID_ROCK_ITEM_2 = registerBlockItem(new BlockItem(ASTEROID_ROCK_2, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.ASTEROID_ROCK_2);
    public static final BlockItem VENUS_ROCK_ITEM = registerBlockItem(new BlockItem(VENUS_ROCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.VENUS_ROCK);
    public static final BlockItem VENUS_ROCK_ITEM_1 = registerBlockItem(new BlockItem(VENUS_ROCK_1, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.VENUS_ROCK_1);
    public static final BlockItem VENUS_ROCK_ITEM_2 = registerBlockItem(new BlockItem(VENUS_ROCK_2, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.VENUS_ROCK_2);
    public static final BlockItem VENUS_ROCK_ITEM_3 = registerBlockItem(new BlockItem(VENUS_ROCK_3, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.VENUS_ROCK_3);
    public static final BlockItem VENUS_ROCK_ITEM_SCORCHED = registerBlockItem(new BlockItem(VENUS_ROCK_SCORCHED, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.VENUS_ROCK_SCORCHED);
    public static final BlockItem VOLCANIC_ROCK_ITEM = registerBlockItem(new BlockItem(VOLCANIC_ROCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.VOLCANIC_ROCK);
    public static final BlockItem SCORCHED_ROCK_ITEM = registerBlockItem(new BlockItem(SCORCHED_ROCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.SCORCHED_ROCK);
    public static final BlockItem PUMICE_ITEM = registerBlockItem(new BlockItem(PUMICE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.PUMICE);
    public static final BlockItem VAPOR_SPOUT_ITEM = registerBlockItem(new BlockItem(VAPOR_SPOUT, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.VAPOR_SPOUT);
    public static final BlockItem COPPER_ORE_ITEM = registerBlockItem(new BlockItem(COPPER_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.COPPER_ORE);
    public static final BlockItem TIN_ORE_ITEM = registerBlockItem(new BlockItem(TIN_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.TIN_ORE);
    public static final BlockItem ALUMINUM_ORE_ITEM = registerBlockItem(new BlockItem(ALUMINUM_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.ALUMINUM_ORE);
    public static final BlockItem SILICON_ORE_ITEM = registerBlockItem(new BlockItem(SILICON_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.SILICON_ORE);
    public static final BlockItem ASTEROID_ALUMINUM_ORE_ITEM = registerBlockItem(new BlockItem(ASTEROID_ALUMINUM_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.ASTEROID_ALUMINUM_ORE);
    public static final BlockItem CHEESE_ORE_ITEM = registerBlockItem(new BlockItem(CHEESE_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.CHEESE_ORE);
    public static final BlockItem CHEESE_BLOCK_ITEM = registerBlockItem(new BlockItem(CHEESE_BLOCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.CHEESE);
    public static final BlockItem MOON_COPPER_ORE_ITEM = registerBlockItem(new BlockItem(MOON_COPPER_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MOON_COPPER_ORE);
    public static final BlockItem MARS_COPPER_ORE_ITEM = registerBlockItem(new BlockItem(MARS_COPPER_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MARS_COPPER_ORE);
    public static final BlockItem DESH_ORE_ITEM = registerBlockItem(new BlockItem(DESH_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.DESH_ORE);
    public static final BlockItem ILMENITE_ORE_ITEM = registerBlockItem(new BlockItem(ILMENITE_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.ILMENITE_ORE);
    public static final BlockItem MARS_IRON_ORE_ITEM = registerBlockItem(new BlockItem(MARS_IRON_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MARS_IRON_ORE);
    public static final BlockItem ASTEROID_IRON_ORE_ITEM = registerBlockItem(new BlockItem(ASTEROID_IRON_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.ASTEROID_IRON_ORE);
    public static final BlockItem MOON_TIN_ORE_ITEM = registerBlockItem(new BlockItem(MOON_TIN_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MOON_TIN_ORE);
    public static final BlockItem MARS_TIN_ORE_ITEM = registerBlockItem(new BlockItem(MARS_TIN_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MARS_TIN_ORE);
    public static final BlockItem GALENA_ORE_ITEM = registerBlockItem(new BlockItem(GALENA_ORE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.GALENA_ORE);
    public static final BlockItem COPPER_BLOCK_ITEM = registerBlockItem(new BlockItem(COPPER_BLOCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.COPPER_BLOCK);
    public static final BlockItem TIN_BLOCK_ITEM = registerBlockItem(new BlockItem(TIN_BLOCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.TIN_BLOCK);
    public static final BlockItem ALUMINUM_BLOCK_ITEM = registerBlockItem(new BlockItem(ALUMINUM_BLOCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.ALUMINUM_BLOCK);
    public static final BlockItem SILICON_BLOCK_ITEM = registerBlockItem(new BlockItem(SILICON_BLOCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.SILICON_BLOCK);
    public static final BlockItem SOLID_METEORIC_IRON_BLOCK_ITEM = registerBlockItem(new BlockItem(SOLID_METEORIC_IRON_BLOCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.SOLID_METEORIC_IRON_BLOCK);
    public static final BlockItem DESH_BLOCK_ITEM = registerBlockItem(new BlockItem(DESH_BLOCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.DESH_BLOCK);
    public static final BlockItem TITANIUM_BLOCK_ITEM = registerBlockItem(new BlockItem(TITANIUM_BLOCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.TITANIUM_BLOCK);
    public static final BlockItem LEAD_BLOCK_ITEM = registerBlockItem(new BlockItem(LEAD_BLOCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.LEAD_BLOCK);
    public static final BlockItem TIN_DECORATION_BLOCK_ITEM = registerBlockItem(new BlockItem(TIN_DECORATION_BLOCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.TIN_DECORATION);
    public static final BlockItem TIN_DECORATION_BLOCK_ITEM_1 = registerBlockItem(new BlockItem(TIN_DECORATION_BLOCK_1, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.TIN_DECORATION_BLOCK_1);
    public static final BlockItem DARK_DECORATION_BLOCK_ITEM = registerBlockItem(new BlockItem(DARK_DECORATION_BLOCK, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.DARK_DECORATION);
    public static final BlockItem GRATING_ITEM = registerBlockItem(new BlockItem(GRATING, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.GRATING);
    public static final BlockItem SQUARE_LIGHTING_PANEL_ITEM = registerBlockItem(new BlockItem(SQUARE_LIGHTING_PANEL, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.SQUARE_LIGHTING_PANEL);
    public static final BlockItem SPOTLIGHT_LIGHTING_PANEL_ITEM = registerBlockItem(new BlockItem(SPOTLIGHT_LIGHTING_PANEL, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.SPOTLIGHT_LIGHTING_PANEL);
    public static final BlockItem LINEAR_LIGHTING_PANEL_ITEM = registerBlockItem(new BlockItem(LINEAR_LIGHTING_PANEL, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.LINEAR_LIGHTING_PANEL);
    public static final BlockItem DARK_LIGHTING_PANEL_ITEM = registerBlockItem(new BlockItem(DARK_LIGHTING_PANEL, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.DARK_LIGHTING_PANEL);
    public static final BlockItem DARK_ANGLE_LIGHTING_PANEL_ITEM = registerBlockItem(new BlockItem(DARK_ANGLE_LIGHTING_PANEL, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.DARK_ANGLE_LIGHTING_PANEL);
    public static final BlockItem VACUUM_GLASS_ITEM = registerBlockItem(new BlockItem(VACUUM_GLASS, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.VACUUM_GLASS);
    public static final BlockItem CLEAR_VACUUM_GLASS_ITEM = registerBlockItem(new BlockItem(CLEAR_VACUUM_GLASS, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.CLEAR_VACUUM_GLASS);
    public static final BlockItem TIN_VACUUM_GLASS_ITEM = registerBlockItem(new BlockItem(TIN_VACUUM_GLASS, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.TIN_VACUUM_GLASS);
    public static final BlockItem STRONG_VACUUM_GLASS_ITEM = registerBlockItem(new BlockItem(STRONG_VACUUM_GLASS, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.STRONG_VACUUM_GLASS);
    public static final BlockItem WALKWAY_ITEM = registerBlockItem(new BlockItem(WALKWAY, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.WALKWAY);
    public static final BlockItem WIRE_WALKWAY_ITEM = registerBlockItem(new BlockItem(WIRE_WALKWAY, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.WIRE_WALKWAY);
    public static final BlockItem PIPE_WALKWAY_ITEM = registerBlockItem(new BlockItem(PIPE_WALKWAY, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.PIPE_WALKWAY);
    public static final BlockItem TIN_DECORATION_SLAB_ITEM = registerBlockItem(new BlockItem(TIN_DECORATION_SLAB, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.TIN_DECORATION_SLAB);
    public static final BlockItem TIN_DECORATION_SLAB_ITEM_1 = registerBlockItem(new BlockItem(TIN_DECORATION_SLAB_1, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.TIN_DECORATION_SLAB_1);
    public static final BlockItem DARK_DECORATION_SLAB_ITEM = registerBlockItem(new BlockItem(DARK_DECORATION_SLAB, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.DARK_DECORATION_SLAB);
    public static final BlockItem MARS_COBBLESTONE_SLAB_ITEM = registerBlockItem(new BlockItem(MARS_COBBLESTONE_SLAB, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MARS_COBBLESTONE_SLAB);
    public static final BlockItem MARS_DUNGEON_BRICKS_SLAB_ITEM = registerBlockItem(new BlockItem(MARS_DUNGEON_BRICKS_SLAB, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MARS_DUNGEON_BRICKS_SLAB);
    public static final BlockItem MOON_DUNGEON_BRICKS_SLAB_ITEM = registerBlockItem(new BlockItem(MOON_DUNGEON_BRICKS_SLAB, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MOON_DUNGEON_BRICKS_SLAB);
    public static final BlockItem MOON_ROCK_SLAB_ITEM = registerBlockItem(new BlockItem(MOON_ROCK_SLAB, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MOON_ROCK_SLAB);
    public static final BlockItem MOON_ROCK_STAIRS_ITEM = registerBlockItem(new BlockItem(MOON_ROCK_STAIRS, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MOON_ROCK_STAIRS);
    public static final BlockItem MOON_DUNGEON_BRICKS_STAIRS_ITEM = registerBlockItem(new BlockItem(MOON_DUNGEON_BRICKS_STAIRS, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MOON_DUNGEON_BRICKS_STAIRS);
    public static final BlockItem TIN_DECORATION_STAIRS_ITEM = registerBlockItem(new BlockItem(TIN_DECORATION_STAIRS, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.TIN_DECORATION_STAIRS);
    public static final BlockItem TIN_DECORATION_STAIRS_ITEM_1 = registerBlockItem(new BlockItem(TIN_DECORATION_STAIRS_1, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.TIN_DECORATION_STAIRS_1);
    public static final BlockItem MARS_DUNGEON_BRICKS_STAIRS_ITEM = registerBlockItem(new BlockItem(MARS_DUNGEON_BRICKS_STAIRS, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MARS_DUNGEON_BRICKS_STAIRS);
    public static final BlockItem MARS_COBBLESTONE_STAIRS_ITEM = registerBlockItem(new BlockItem(MARS_COBBLESTONE_STAIRS, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MARS_COBBLESTONE_STAIRS);
    public static final BlockItem TIN_WALL_ITEM = registerBlockItem(new BlockItem(TIN_WALL, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.TIN_DECORATION_WALL);
    public static final BlockItem TIN_WALL_ITEM_1 = registerBlockItem(new BlockItem(TIN_WALL_1, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.TIN_DECORATION_WALL_1);
    public static final BlockItem MOON_ROCK_WALL_ITEM = registerBlockItem(new BlockItem(MOON_ROCK_WALL, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MOON_ROCK_WALL);
    public static final BlockItem MOON_DUNGEON_BRICKS_WALL_ITEM = registerBlockItem(new BlockItem(MOON_DUNGEON_BRICKS_WALL, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MOON_DUNGEON_BRICKS_WALL);
    public static final BlockItem MARS_COBBLESTONE_WALL_ITEM = registerBlockItem(new BlockItem(MARS_COBBLESTONE_WALL, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MARS_COBBLESTONE_WALL);
    public static final BlockItem MARS_DUNGEON_BRICKS_WALL_ITEM = registerBlockItem(new BlockItem(MARS_DUNGEON_BRICKS_WALL, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MARS_DUNGEON_BRICKS_WALL);
    public static final BlockItem UNLIT_TORCH_ITEM = registerBlockItem(new BlockItem(UNLIT_TORCH, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.UNLIT_TORCH);
    public static final BlockItem GLOWSTONE_TORCH_ITEM = registerBlockItem(new WallStandingBlockItem(GLOWSTONE_TORCH, GLOWSTONE_WALL_TORCH, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.GLOWSTONE_TORCH);
    public static final BlockItem CAVERNOUS_VINE_ITEM = registerBlockItem(new BlockItem(CAVERNOUS_VINE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.CAVERNOUS_VINE);
    public static final BlockItem POISONOUS_CAVERNOUS_VINE_ITEM = registerBlockItem(new BlockItem(POISONOUS_CAVERNOUS_VINE, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.POISONOUS_CAVERNOUS_VINE);
    public static final BlockItem MOON_BERRY_BUSH_ITEM = registerBlockItem(new BlockItem(MOON_BERRY_BUSH, new Item.Settings().itemGroup(BLOCKS_GROUP)), Constants.Blocks.MOON_BERRY_BUSH);

    public static ItemGroup MACHINES_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.Blocks.ITEM_GROUP_MACHINES))
            // Set the the tab icon
            .icon(() -> new ItemStack(GalacticraftBlocks.COAL_GENERATOR))
            .build();

    public static final BlockItem CIRCUIT_FABRICATOR_ITEM = registerBlockItem(new BlockItem(CIRCUIT_FABRICATOR, new Item.Settings().itemGroup(MACHINES_GROUP)), Constants.Blocks.CIRCUIT_FABRICATOR);
    public static final BlockItem COMPRESSOR_ITEM = registerBlockItem(new BlockItem(COMPRESSOR, new Item.Settings().itemGroup(MACHINES_GROUP)), Constants.Blocks.COMPRESSOR);
    public static final BlockItem ELECTRIC_COMPRESSOR_ITEM = registerBlockItem(new BlockItem(ELECTRIC_COMPRESSOR, new Item.Settings().itemGroup(MACHINES_GROUP)), Constants.Blocks.ELECTRIC_COMPRESSOR);
    public static final BlockItem COAL_GENERATOR_ITEM = registerBlockItem(new BlockItem(COAL_GENERATOR, new Item.Settings().itemGroup(MACHINES_GROUP)), Constants.Blocks.COAL_GENERATOR);
    public static final BlockItem BASIC_SOLAR_PANEL_ITEM = registerBlockItem(new BlockItem(BASIC_SOLAR_PANEL, new Item.Settings().itemGroup(MACHINES_GROUP)), Constants.Blocks.BASIC_SOLAR_PANEL);
    public static final BlockItem ENERGY_STORAGE_MODULE_ITEM = registerBlockItem(new BlockItem(ENERGY_STORAGE_MODULE, new Item.Settings().itemGroup(MACHINES_GROUP)), Constants.Blocks.ENERGY_STORAGE_MODULE);
    public static final BlockItem OXYGEN_COLLECTOR_ITEM = registerBlockItem(new BlockItem(OXYGEN_COLLECTOR, new Item.Settings().itemGroup(MACHINES_GROUP)), Constants.Blocks.OXYGEN_COLLECTOR);
    public static final BlockItem REFINERY_ITEM = registerBlockItem(new BlockItem(REFINERY, new Item.Settings().itemGroup(MACHINES_GROUP)), Constants.Blocks.REFINERY);

    public static final BlockItem ALUMINUM_WIRE_ITEM = registerBlockItem(new BlockItem(ALUMINUM_WIRE, new Item.Settings().itemGroup(MACHINES_GROUP)), Constants.Blocks.ALUMINUM_WIRE);
    public static final BlockItem OXYGEN_PIPE_ITEM = registerBlockItem(new BlockItem(OXYGEN_PIPE, new Item.Settings().itemGroup(MACHINES_GROUP)), Constants.Blocks.OXYGEN_PIPE);

    public static void register() {
    }

    private static Block registerBlock(Block block, String id) {
        return Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, id), block);
    }

    private static BlockItem registerBlockItem(BlockItem blockItem, String id) {
        return Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, id), blockItem);
    }
}