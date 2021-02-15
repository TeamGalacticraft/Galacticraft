/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.block;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.ConfigurableMachineBlock;
import com.hrznstudio.galacticraft.api.block.FluidBlock;
import com.hrznstudio.galacticraft.block.decoration.*;
import com.hrznstudio.galacticraft.block.entity.*;
import com.hrznstudio.galacticraft.block.environment.*;
import com.hrznstudio.galacticraft.block.machines.*;
import com.hrznstudio.galacticraft.block.special.SolarPanelPartBlock;
import com.hrznstudio.galacticraft.block.special.TinLadderBlock;
import com.hrznstudio.galacticraft.block.special.aluminumwire.tier1.AluminumWireBlock;
import com.hrznstudio.galacticraft.block.special.aluminumwire.tier1.SealableAluminumWireBlock;
import com.hrznstudio.galacticraft.block.special.fluidpipe.GlassFluidPipeBlock;
import com.hrznstudio.galacticraft.block.special.walkway.Walkway;
import com.hrznstudio.galacticraft.block.special.walkway.WireWalkway;
import com.hrznstudio.galacticraft.component.GalacticraftComponents;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.screen.*;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@SuppressWarnings("unused")
public class GalacticraftBlocks {
    //ITEM GROUPS
    public static final ItemGroup BLOCKS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.Blocks.ITEM_GROUP_BLOCKS))
            .icon(() -> new ItemStack(GalacticraftBlocks.MOON_TURF)).build();

    public static final ItemGroup MACHINES_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.Blocks.ITEM_GROUP_MACHINES))
            .icon(() -> new ItemStack(GalacticraftBlocks.COAL_GENERATOR)).build();

    // TORCHES
    public static final Block GLOWSTONE_TORCH = registerBlockWithoutItem(new GlowstoneTorchBlock(FabricBlockSettings.copy(Blocks.TORCH).luminance((state) -> 15).sounds(BlockSoundGroup.WOOD)), Constants.Blocks.GLOWSTONE_TORCH);
    public static final Block GLOWSTONE_WALL_TORCH = registerBlockWithoutItem(new GlowstoneWallTorchBlock(FabricBlockSettings.copy(GLOWSTONE_TORCH).dropsLike(GLOWSTONE_TORCH)), Constants.Blocks.GLOWSTONE_WALL_TORCH);
    public static final Block GLOWSTONE_LANTERN = registerBlock(new GlowstoneLanternBlock(FabricBlockSettings.copy(Blocks.LANTERN).luminance((state) -> 15)), Constants.Blocks.GLOWSTONE_LANTERN);
    public static final Block UNLIT_TORCH = registerBlockWithoutItem(new UnlitTorchBlock(FabricBlockSettings.copy(Blocks.TORCH).luminance((state) -> 0)), Constants.Blocks.UNLIT_TORCH);
    public static final Block UNLIT_WALL_TORCH = registerBlockWithoutItem(new UnlitWallTorchBlock(FabricBlockSettings.copy(UNLIT_TORCH).dropsLike(UNLIT_TORCH)), Constants.Blocks.UNLIT_WALL_TORCH);
    public static final Block UNLIT_LANTERN = registerBlockWithoutItem(new UnlitLanternBlock(FabricBlockSettings.copy(Blocks.LANTERN).luminance((state) -> 0)), Constants.Blocks.UNLIT_LANTERN);

    // LIQUIDS
    public static final FluidBlock CRUDE_OIL = registerBlockWithoutItem(new CrudeOilBlock(GalacticraftFluids.CRUDE_OIL, FabricBlockSettings.of(new FabricMaterialBuilder(MaterialColor.BLACK)
            .allowsMovement().destroyedByPiston().burnable().lightPassesThrough().notSolid().replaceable().liquid().build())
            .strength(100.0F, 1000.0F).dropsNothing()), Constants.Blocks.CRUDE_OIL);

    public static final FluidBlock FUEL = registerBlockWithoutItem(new FluidBlock(GalacticraftFluids.FUEL, FabricBlockSettings.of(new FabricMaterialBuilder(MaterialColor.YELLOW)
            .allowsMovement().destroyedByPiston().burnable().lightPassesThrough().notSolid().replaceable().liquid().build())
            .strength(50.0F, 50.0F).dropsNothing()), Constants.Blocks.FUEL);

    // DECORATION BLOCKS
    public static final Block[] ALUMINUM_DECORATIONS = createDecorationBlocks(Constants.Blocks.ALUMINUM_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F), true);
    public static final Block[] BRONZE_DECORATIONS = createDecorationBlocks(Constants.Blocks.BRONZE_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F), true);
    public static final Block[] COPPER_DECORATIONS = createDecorationBlocks(Constants.Blocks.COPPER_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F), true);
    public static final Block[] DARK_DECORATIONS = createDecorationBlocks(Constants.Blocks.DARK_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.BLACK).strength(2.0F, 2.0F), false);
    public static final Block[] IRON_DECORATIONS = createDecorationBlocks(Constants.Blocks.IRON_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F), true);
    public static final Block[] METEORIC_IRON_DECORATIONS = createDecorationBlocks(Constants.Blocks.METEORIC_IRON_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F), true);
    public static final Block[] STEEL_DECORATIONS = createDecorationBlocks(Constants.Blocks.STEEL_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F), true);
    public static final Block[] TIN_DECORATIONS = createDecorationBlocks(Constants.Blocks.TIN_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F), true);
    public static final Block[] TITANIUM_DECORATIONS = createDecorationBlocks(Constants.Blocks.TITANIUM_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 2.0F), true);

    // MOON NATURAL
    public static final Block MOON_TURF = registerBlock(new Block(FabricBlockSettings.of(Material.SOLID_ORGANIC, MaterialColor.LIGHT_GRAY).strength(0.5F, 0.5F)), Constants.Blocks.MOON_TURF);
    public static final Block MOON_DIRT = registerBlock(new Block(FabricBlockSettings.of(Material.SOIL, MaterialColor.LIGHT_GRAY).strength(0.5F, 0.5F).sounds(BlockSoundGroup.GRAVEL)), Constants.Blocks.MOON_DIRT);
    public static final Block MOON_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(1.5F, 6.0F)), Constants.Blocks.MOON_SURFACE_ROCK);
    public static final Block[] MOON_ROCKS = createDecorationBlocks(Constants.Blocks.MOON_ROCK, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 5.0F), false);
    public static final Block[] COBBLED_MOON_ROCKS = createDecorationBlocks(Constants.Blocks.COBBLED_MOON_ROCK, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 5.0F), false);
    public static final Block[] MOON_BASALTS = createDecorationBlocks(Constants.Blocks.MOON_BASALT, FabricBlockSettings.of(Material.STONE, MaterialColor.BLACK_TERRACOTTA).strength(2.0F, 6.0F), false);
    public static final Block[] MOON_BASALT_BRICKS = createDecorationBlocks(Constants.Blocks.MOON_BASALT_BRICK, FabricBlockSettings.of(Material.STONE, MaterialColor.BLACK_TERRACOTTA).strength(2.0F, 6.0F), false);
    public static final Block[] CRACKED_MOON_BASALT_BRICKS = createDecorationBlocks(Constants.Blocks.CRACKED_MOON_BASALT_BRICK, FabricBlockSettings.of(Material.STONE, MaterialColor.BLACK_TERRACOTTA).strength(2.0F, 6.0F), false);

    // MARS NATURAL
    public static final Block MARS_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.DIRT).hardness(2.2F)), Constants.Blocks.MARS_SURFACE_ROCK);
    public static final Block MARS_SUB_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(2.6F)), Constants.Blocks.MARS_SUB_SURFACE_ROCK);
    public static final Block MARS_STONE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(3.0F)), Constants.Blocks.MARS_STONE);
    public static final Block[] MARS_COBBLESTONES = createDecorationBlocks(Constants.Blocks.MARS_COBBLESTONE, FabricBlockSettings.of(Material.STONE, MaterialColor.RED).hardness(2.8F), false);

    // ASTEROID NATURAL
    public static final Block ASTEROID_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).hardness(3.0F)), Constants.Blocks.ASTEROID_ROCK);
    public static final Block ASTEROID_ROCK_1 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).hardness(3.0F)), Constants.Blocks.ASTEROID_ROCK_1);
    public static final Block ASTEROID_ROCK_2 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).hardness(3.0F)), Constants.Blocks.ASTEROID_ROCK_2);

    // VENUS NATURAL
    public static final Block SOFT_VENUS_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F)), Constants.Blocks.SOFT_VENUS_ROCK);
    public static final Block HARD_VENUS_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F)), Constants.Blocks.HARD_VENUS_ROCK);
    public static final Block SCORCHED_VENUS_ROCK = registerBlock(new ScorchedRockBlock(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F)), Constants.Blocks.SCORCHED_VENUS_ROCK);
    public static final Block VOLCANIC_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(2.2F, 0.5F)), Constants.Blocks.VOLCANIC_ROCK);
    public static final Block PUMICE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F)), Constants.Blocks.PUMICE);
    public static final Block VAPOR_SPOUT = registerBlock(new VaporSpoutBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).dropsNothing().strength(1.5F, 2.0F)), Constants.Blocks.VAPOR_SPOUT);

    // MISC DECOR
    public static final Block WALKWAY = registerBlock(new Walkway(FabricBlockSettings.of(Material.METAL).strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL)), Constants.Blocks.WALKWAY);
    public static final Block PIPE_WALKWAY = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL)), Constants.Blocks.PIPE_WALKWAY);
    public static final Block WIRE_WALKWAY = registerBlock(new WireWalkway(FabricBlockSettings.of(Material.METAL).strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL)), Constants.Blocks.WIRE_WALKWAY);
    public static final Block TIN_LADDER = registerBlock(new TinLadderBlock(FabricBlockSettings.of(Material.SUPPORTED).strength(1.0f, 1.0f).sounds(BlockSoundGroup.METAL)), Constants.Blocks.TIN_LADDER);
    public static final Block GRATING = registerBlock(new GratingBlock(FabricBlockSettings.of(Material.METAL, MaterialColor.STONE).strength(2.5f, 6.0f).sounds(BlockSoundGroup.METAL)), Constants.Blocks.GRATING);

    // SPECIAL
    public static final Block ALUMINUM_WIRE = registerBlock(new AluminumWireBlock(FabricBlockSettings.copy(Blocks.WHITE_WOOL)), Constants.Blocks.ALUMINUM_WIRE);
    public static final Block SEALABLE_ALUMINUM_WIRE = registerBlock(new SealableAluminumWireBlock(FabricBlockSettings.copy(TIN_DECORATIONS[0])), Constants.Blocks.SEALABLE_ALUMINUM_WIRE);
    public static final Block GLASS_FLUID_PIPE = registerBlock(new GlassFluidPipeBlock(FabricBlockSettings.of(Material.GLASS).breakByHand(true).sounds(BlockSoundGroup.GLASS)), Constants.Blocks.GLASS_FLUID_PIPE);

    // LIGHT PANELS
    public static final Block SQUARE_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL)), Constants.Blocks.SQUARE_LIGHT_PANEL);
    public static final Block SPOTLIGHT_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 3.0f), Constants.Blocks.SPOTLIGHT_LIGHT_PANEL);
    public static final Block LINEAR_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 5.0f), Constants.Blocks.LINEAR_LIGHT_PANEL);
    public static final Block DASHED_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 1.0f), Constants.Blocks.DASHED_LIGHT_PANEL);
    public static final Block DIAGONAL_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 1.0f), Constants.Blocks.DIAGONAL_LIGHT_PANEL);

    // VACUUM GLASS
    public static final Block VACUUM_GLASS = registerBlock(new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS)), Constants.Blocks.VACUUM_GLASS);
    public static final Block CLEAR_VACUUM_GLASS = registerBlock(new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS)), Constants.Blocks.CLEAR_VACUUM_GLASS);
    public static final Block STRONG_VACUUM_GLASS = registerBlock(new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS)), Constants.Blocks.STRONG_VACUUM_GLASS);

    // ORES
    public static final Block SILICON_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.SILICON_ORE);
    public static final Block ASTEROID_ALUMINUM_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.ASTEROID_ALUMINUM_ORE);
    public static final Block MOON_CHEESE_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.MOON_CHEESE_ORE);
    public static final Block MOON_CHEESE_BLOCK = registerBlock(new CakeBlock(FabricBlockSettings.of(Material.CAKE).strength(0.5F).sounds(BlockSoundGroup.WOOL)), Constants.Blocks.MOON_CHEESE_BLOCK);
    public static final Block MOON_COPPER_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.MOON_COPPER_ORE);
    public static final Block MARS_COPPER_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.MARS_COPPER_ORE);
    public static final Block DESH_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.DESH_ORE);
    public static final Block ILMENITE_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.ILMENITE_ORE);
    public static final Block MARS_IRON_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.MARS_IRON_ORE);
    public static final Block ASTEROID_IRON_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.ASTEROID_IRON_ORE);
    public static final Block MOON_TIN_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.MOON_TIN_ORE);
    public static final Block MARS_TIN_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.MARS_TIN_ORE);
    public static final Block GALENA_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).strength(5.0F, 3.0F)), Constants.Blocks.GALENA_ORE);

    // COMPACT MINERAL BLOCKS
    public static final Block SILICON_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.SILICON_BLOCK);
    public static final Block METEORIC_IRON_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.METEORIC_IRON_BLOCK);
    public static final Block DESH_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.DESH_BLOCK);
    public static final Block TITANIUM_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.TITANIUM_BLOCK);
    public static final Block LEAD_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.LEAD_BLOCK);
    public static final Block LUNAR_SAPPHIRE_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(5.0F, 6.0F).sounds(BlockSoundGroup.STONE)), Constants.Blocks.LUNAR_SAPPHIRE_BLOCK);

    // MOON VILLAGER SPECIAL
    public static final Block LUNAR_CARTOGRAPHY_TABLE = registerBlock(new LunarCartographyTableBlock(FabricBlockSettings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)), Constants.Blocks.LUNAR_CARTOGRAPHY_TABLE);

    // MISC WORLD GEN
    public static final Block CAVERNOUS_VINE = registerBlock(new CavernousVineBlock(FabricBlockSettings.of(Material.CACTUS, MaterialColor.GREEN).dropsNothing().noCollision().lightLevel(0).sounds(BlockSoundGroup.GRASS).ticksRandomly()), Constants.Blocks.CAVERNOUS_VINE);
    public static final Block POISONOUS_CAVERNOUS_VINE = registerBlock(new CavernousVineBlockPoisonous(FabricBlockSettings.of(Material.CACTUS, MaterialColor.GREEN).dropsNothing().noCollision().lightLevel(3).sounds(BlockSoundGroup.GRASS).ticksRandomly()), Constants.Blocks.POISONOUS_CAVERNOUS_VINE);
    public static final Block MOON_BERRY_BUSH = registerBlock(new MoonBerryBushBlock(FabricBlockSettings.of(Material.PLANT, MaterialColor.GREEN).dropsNothing().noCollision().lightLevel(3).sounds(BlockSoundGroup.SWEET_BERRY_BUSH).ticksRandomly()), Constants.Blocks.MOON_BERRY_BUSH);
    public static final Block MOON_CHEESE_LEAVES = registerBlock(new LeavesBlock(FabricBlockSettings.of(Material.LEAVES).strength(0.2F, 0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque()), Constants.Blocks.MOON_CHEESE_LEAVES);
    public static final Block MOON_CHEESE_LOG = registerBlock(new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.YELLOW).strength(2.0F).sounds(BlockSoundGroup.WOOD)), Constants.Blocks.MOON_CHEESE_LOG);

    // DUMMY
    public static final Block OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK = registerBlockWithoutItem(new Block(FabricBlockSettings.of(Material.AIR)), Constants.Blocks.OXYGEN_DISTRIBUTOR_BUBBLE_DUMMY_BLOCK);
    public static final Block SOLAR_PANEL_PART = registerBlockWithoutItem(new SolarPanelPartBlock(FabricBlockSettings.of(Material.METAL).strength(-1.0F, 5.0F).dropsNothing().sounds(BlockSoundGroup.METAL)), Constants.Blocks.SOLAR_PANEL_PART);

    // MACHINES
    public static final Block CIRCUIT_FABRICATOR = registerMachine(new ConfigurableMachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL), CircuitFabricatorScreenHandler::new, blockView -> new CircuitFabricatorBlockEntity(),
            new TranslatableText("tooltip.galacticraft-rewoven.circuit_fabricator").setStyle(Constants.Misc.TOOLTIP_STYLE)), Constants.Blocks.CIRCUIT_FABRICATOR);
    public static final Block COMPRESSOR = registerMachine(new ConfigurableMachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL), CompressorScreenHandler::new, blockView -> new CompressorBlockEntity(),
            new TranslatableText("tooltip.galacticraft-rewoven.compressor").setStyle(Constants.Misc.TOOLTIP_STYLE)), Constants.Blocks.COMPRESSOR);
    public static final Block ELECTRIC_COMPRESSOR = registerMachine(new ConfigurableMachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL), ElectricCompressorScreenHandler::new, blockView -> new ElectricCompressorBlockEntity(),
            new TranslatableText("tooltip.galacticraft-rewoven.electric_compressor").setStyle(Constants.Misc.TOOLTIP_STYLE)), Constants.Blocks.ELECTRIC_COMPRESSOR);
    public static final Block COAL_GENERATOR = registerMachine(new CoalGeneratorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.COAL_GENERATOR);
    public static final Block BASIC_SOLAR_PANEL = registerMachine(new BasicSolarPanelBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.BASIC_SOLAR_PANEL);
    public static final Block ADVANCED_SOLAR_PANEL = registerMachine(new AdvancedSolarPanelBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.ADVANCED_SOLAR_PANEL);
    public static final Block ENERGY_STORAGE_MODULE = registerMachine(new ConfigurableMachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL), EnergyStorageModuleScreenHandler::new, blockView -> new EnergyStorageModuleBlockEntity(),
            new TranslatableText("tooltip.galacticraft-rewoven.energy_storage_module").setStyle(Constants.Misc.TOOLTIP_STYLE)), Constants.Blocks.ENERGY_STORAGE_MODULE);
    public static final Block ELECTRIC_FURNACE = registerMachine(new ConfigurableMachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL), ElectricFurnaceScreenHandler::new, blockView -> new ElectricFurnaceBlockEntity(),
            new TranslatableText("tooltip.galacticraft-rewoven.electric_furnace").setStyle(Constants.Misc.TOOLTIP_STYLE)), Constants.Blocks.ELECTRIC_FURNACE);
    public static final Block ELECTRIC_ARC_FURNACE = registerMachine(new ConfigurableMachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL), ElectricArcFurnaceScreenHandler::new, blockView -> new ElectricArcFurnaceBlockEntity(),
            new TranslatableText("tooltip.galacticraft-rewoven.electric_arc_furnace").setStyle(Constants.Misc.TOOLTIP_STYLE)), Constants.Blocks.ELECTRIC_ARC_FURNACE);
    public static final Block OXYGEN_COLLECTOR = registerMachine(new OxygenCollectorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.OXYGEN_COLLECTOR);
    public static final Block REFINERY = registerMachine(new RefineryBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL)), Constants.Blocks.REFINERY);
    public static final Block BUBBLE_DISTRIBUTOR = registerMachine(new ConfigurableMachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL), BubbleDistributorScreenHandler::new, blockView -> new BubbleDistributorBlockEntity(),
            new TranslatableText("tooltip.galacticraft-rewoven.oxygen_bubble_distributor").setStyle(Constants.Misc.TOOLTIP_STYLE)), Constants.Blocks.OXYGEN_BUBBLE_DISTRIBUTOR);
    public static final Block OXYGEN_DECOMPRESSOR = registerMachine(new ConfigurableMachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL), OxygenDecompressorScreenHandler::new, blockView -> new OxygenDecompressorBlockEntity(),
            new TranslatableText("tooltip.galacticraft-rewoven.oxygen_decompressor").setStyle(Constants.Misc.TOOLTIP_STYLE)), Constants.Blocks.OXYGEN_DECOMPRESSOR);
    public static final Block OXYGEN_COMPRESSOR = registerMachine(new ConfigurableMachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL), OxygenCompressorScreenHandler::new, blockView -> new OxygenCompressorBlockEntity(),
            new TranslatableText("tooltip.galacticraft-rewoven.oxygen_compressor").setStyle(Constants.Misc.TOOLTIP_STYLE)), Constants.Blocks.OXYGEN_COMPRESSOR);
    public static final Block OXYGEN_STORAGE_MODULE = registerMachine(new ConfigurableMachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).sounds(BlockSoundGroup.METAL), OxygenStorageModuleScreenHandler::new, blockView -> new OxygenStorageModuleBlockEntity(),
            new TranslatableText("tooltip.galacticraft-rewoven.oxygen_storage_module").setStyle(Constants.Misc.TOOLTIP_STYLE)), Constants.Blocks.OXYGEN_STORAGE_MODULE);

    public static void register() {
        FlammableBlockRegistry.getDefaultInstance().add(FUEL, 80, 130);
        FlammableBlockRegistry.getDefaultInstance().add(CRUDE_OIL, 60, 100);
    }

    private static Block[] createDecorationBlocks(String baseId, AbstractBlock.Settings settings, boolean detailedVariant) {
        return createDecorationBlocks(baseId, new Block(settings), detailedVariant);
    }

    /**
     * Generates a stair, slab and wall block for the given block.
     * @param baseId The base registry id for all the blocks.
     * @param baseBlock The block to model the variants on.
     * @param hasDetailed Whether to create 4 additional 'detailed' blocks.
     * @return An array of different variants of the same block.
     */
    private static Block[] createDecorationBlocks(String baseId, Block baseBlock, boolean hasDetailed) {
        Block[] blocks = new Block[hasDetailed ? 8 : 4];
        blocks[0] = registerBlock(baseBlock, baseId);
        blocks[1] = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(baseBlock)), baseId + "_slab");
        blocks[2] = registerBlock(new StairsBlock(baseBlock.getDefaultState(), FabricBlockSettings.copyOf(baseBlock)), baseId + "_stairs");
        blocks[3] = registerBlock(new WallBlock(FabricBlockSettings.copyOf(baseBlock)), baseId + "_wall");

        if (hasDetailed) {
            blocks[4] = registerBlock(new Block(FabricBlockSettings.copyOf(baseBlock)), "detailed_" + baseId);
            blocks[5] = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(baseBlock)), "detailed_" + baseId + "_slab");
            blocks[6] = registerBlock(new StairsBlock(baseBlock.getDefaultState(), FabricBlockSettings.copyOf(baseBlock)), "detailed_" + baseId + "_stairs");
            blocks[7] = registerBlock(new WallBlock(FabricBlockSettings.copyOf(baseBlock)), "detailed_" + baseId + "_wall");
        }
        return blocks;
    }

    private static <T extends Block> T registerBlock(T block, String id) {
        return registerBlock(block, id, BLOCKS_GROUP);
    }

    private static <T extends Block> T registerMachine(T block, String id) {
        return registerBlock(block, id, MACHINES_GROUP);
    }

    private static <T extends Block> T registerBlock(T block, String id, ItemGroup group) {
        Identifier identifier = new Identifier(Constants.MOD_ID, id);
        Registry.register(Registry.BLOCK, identifier, block);
        BlockItem item = Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings().group(group)));
        item.appendBlocks(Item.BLOCK_ITEMS, item);
        return block;
    }

    private static <T extends Block> T registerBlockWithoutItem(T block, String id) {
        return Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, id), block);
    }
}