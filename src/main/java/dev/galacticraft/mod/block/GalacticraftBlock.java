/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.block;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.MachineBlock;
import dev.galacticraft.mod.block.decoration.GratingBlock;
import dev.galacticraft.mod.block.decoration.LightPanelBlock;
import dev.galacticraft.mod.block.decoration.LunarCartographyTableBlock;
import dev.galacticraft.mod.block.decoration.VacuumGlassBlock;
import dev.galacticraft.mod.block.entity.*;
import dev.galacticraft.mod.block.environment.*;
import dev.galacticraft.mod.block.machine.*;
import dev.galacticraft.mod.block.special.SolarPanelPartBlock;
import dev.galacticraft.mod.block.special.TinLadderBlock;
import dev.galacticraft.mod.block.special.aluminumwire.tier1.AluminumWireBlock;
import dev.galacticraft.mod.block.special.aluminumwire.tier1.SealableAluminumWireBlock;
import dev.galacticraft.mod.block.special.fluidpipe.GlassFluidPipeBlock;
import dev.galacticraft.mod.block.special.walkway.PipeWalkway;
import dev.galacticraft.mod.block.special.walkway.Walkway;
import dev.galacticraft.mod.block.special.walkway.WireWalkway;
import dev.galacticraft.mod.fluid.GalacticraftFluid;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
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
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@SuppressWarnings("unused")
public class GalacticraftBlock {
    //ITEM GROUPS
    public static final ItemGroup BLOCKS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constant.MOD_ID, Constant.Block.ITEM_GROUP_BLOCKS))
            .icon(() -> new ItemStack(GalacticraftBlock.MOON_TURF)).build();

    public static final ItemGroup MACHINES_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constant.MOD_ID, Constant.Block.ITEM_GROUP_MACHINES))
            .icon(() -> new ItemStack(GalacticraftBlock.COAL_GENERATOR)).build();

    // TORCHES
    public static final Block GLOWSTONE_TORCH = registerBlockWithoutItem(new GlowstoneTorchBlock(FabricBlockSettings.copyOf(Blocks.TORCH).sounds(BlockSoundGroup.WOOD)), Constant.Block.GLOWSTONE_TORCH);
    public static final Block GLOWSTONE_WALL_TORCH = registerBlockWithoutItem(new GlowstoneWallTorchBlock(FabricBlockSettings.copyOf(GLOWSTONE_TORCH).dropsLike(GLOWSTONE_TORCH)), Constant.Block.GLOWSTONE_WALL_TORCH);
    public static final Block GLOWSTONE_LANTERN = registerBlock(new GlowstoneLanternBlock(FabricBlockSettings.copyOf(Blocks.LANTERN).requiresTool()), Constant.Block.GLOWSTONE_LANTERN);
    public static final Block UNLIT_TORCH = registerBlockWithoutItem(new UnlitTorchBlock(FabricBlockSettings.copyOf(Blocks.TORCH).luminance(state -> 0)), Constant.Block.UNLIT_TORCH);
    public static final Block UNLIT_WALL_TORCH = registerBlockWithoutItem(new UnlitWallTorchBlock(FabricBlockSettings.copyOf(UNLIT_TORCH).dropsLike(UNLIT_TORCH)), Constant.Block.UNLIT_WALL_TORCH);
    public static final Block UNLIT_LANTERN = registerBlockWithoutItem(new UnlitLanternBlock(FabricBlockSettings.copyOf(Blocks.LANTERN).requiresTool().luminance(state -> 0)), Constant.Block.UNLIT_LANTERN);

    // LIQUIDS
    public static final FluidBlock CRUDE_OIL = registerBlockWithoutItem(new CrudeOilBlock(GalacticraftFluid.CRUDE_OIL, FabricBlockSettings.of(new FabricMaterialBuilder(MaterialColor.BLACK)
            .allowsMovement().destroyedByPiston().burnable().lightPassesThrough().notSolid().replaceable().liquid().build())
            .strength(100.0F, 1000.0F).dropsNothing()), Constant.Block.CRUDE_OIL);

    public static final FluidBlock FUEL = registerBlockWithoutItem(new FluidBlock(GalacticraftFluid.FUEL, FabricBlockSettings.of(new FabricMaterialBuilder(MaterialColor.YELLOW)
            .allowsMovement().destroyedByPiston().burnable().lightPassesThrough().notSolid().replaceable().liquid().build())
            .strength(50.0F, 50.0F).dropsNothing()), Constant.Block.FUEL);

    public static final FluidBlock BACTERIAL_SLUDGE = registerBlockWithoutItem(new BacterialSludgeBlock(GalacticraftFluid.BACTERIAL_SLUDGE, FabricBlockSettings.of(new FabricMaterialBuilder(MaterialColor.GREEN)
            .allowsMovement().destroyedByPiston().lightPassesThrough().notSolid().replaceable().liquid().build())
            .strength(100.0F, 1000.0F).dropsNothing()), Constant.Block.BACTERIAL_SLUDGE);

    // DECORATION BLOCKS
    public static final Block[] ALUMINUM_DECORATIONS = createDecorationBlocks(Constant.Block.ALUMINUM_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 2.0F), true);
    public static final Block[] BRONZE_DECORATIONS = createDecorationBlocks(Constant.Block.BRONZE_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 2.0F), true);
    public static final Block[] COPPER_DECORATIONS = createDecorationBlocks(Constant.Block.COPPER_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 2.0F), true);
    public static final Block[] DARK_DECORATIONS = createDecorationBlocks(Constant.Block.DARK_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.BLACK).requiresTool().strength(2.0F, 2.0F), false);
    public static final Block[] IRON_DECORATIONS = createDecorationBlocks(Constant.Block.IRON_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 2.0F), true);
    public static final Block[] METEORIC_IRON_DECORATIONS = createDecorationBlocks(Constant.Block.METEORIC_IRON_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 2.0F), true);
    public static final Block[] STEEL_DECORATIONS = createDecorationBlocks(Constant.Block.STEEL_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 2.0F), true);
    public static final Block[] TIN_DECORATIONS = createDecorationBlocks(Constant.Block.TIN_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 2.0F), true);
    public static final Block[] TITANIUM_DECORATIONS = createDecorationBlocks(Constant.Block.TITANIUM_DECORATION, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 2.0F), true);

    // MOON NATURAL
    public static final Block MOON_TURF = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.LIGHT_GRAY).strength(0.5F, 0.5F).breakByTool(FabricToolTags.PICKAXES).breakByTool(FabricToolTags.SHOVELS)), Constant.Block.MOON_TURF);
    public static final Block MOON_DIRT = registerBlock(new Block(FabricBlockSettings.of(Material.SOIL, MaterialColor.LIGHT_GRAY).strength(0.5F, 0.5F).breakByTool(FabricToolTags.PICKAXES).breakByTool(FabricToolTags.SHOVELS).sounds(BlockSoundGroup.GRAVEL)), Constant.Block.MOON_DIRT);
    public static final Block MOON_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.GRAY).strength(1.5F, 6.0F).requiresTool()), Constant.Block.MOON_SURFACE_ROCK);
    public static final Block[] MOON_ROCKS = createDecorationBlocks(Constant.Block.MOON_ROCK, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 5.0F), false);
    public static final Block[] COBBLED_MOON_ROCKS = createDecorationBlocks(Constant.Block.COBBLED_MOON_ROCK, FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 5.0F), false);
    public static final Block[] MOON_BASALTS = createDecorationBlocks(Constant.Block.MOON_BASALT, FabricBlockSettings.of(Material.STONE, MaterialColor.BLACK_TERRACOTTA).requiresTool().strength(2.0F, 6.0F), false);
    public static final Block[] MOON_BASALT_BRICKS = createDecorationBlocks(Constant.Block.MOON_BASALT_BRICKS, FabricBlockSettings.of(Material.STONE, MaterialColor.BLACK_TERRACOTTA).requiresTool().strength(2.0F, 6.0F), false);
    public static final Block[] CRACKED_MOON_BASALT_BRICKS = createDecorationBlocks(Constant.Block.CRACKED_MOON_BASALT_BRICKS, FabricBlockSettings.of(Material.STONE, MaterialColor.BLACK_TERRACOTTA).requiresTool().strength(2.0F, 6.0F), false);

    // MARS NATURAL
    public static final Block MARS_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.DIRT).requiresTool().hardness(2.2F)), Constant.Block.MARS_SURFACE_ROCK);
    public static final Block MARS_SUB_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).requiresTool().hardness(2.6F)), Constant.Block.MARS_SUB_SURFACE_ROCK);
    public static final Block MARS_STONE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.RED).requiresTool().hardness(3.0F)), Constant.Block.MARS_STONE);
    public static final Block[] MARS_COBBLESTONES = createDecorationBlocks(Constant.Block.MARS_COBBLESTONE, FabricBlockSettings.of(Material.STONE, MaterialColor.RED).requiresTool().hardness(2.8F), false);

    // ASTEROID NATURAL
    public static final Block ASTEROID_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).requiresTool().hardness(3.0F)), Constant.Block.ASTEROID_ROCK);
    public static final Block ASTEROID_ROCK_1 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).requiresTool().hardness(3.0F)), Constant.Block.ASTEROID_ROCK_1);
    public static final Block ASTEROID_ROCK_2 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).requiresTool().hardness(3.0F)), Constant.Block.ASTEROID_ROCK_2);

    // VENUS NATURAL
    public static final Block SOFT_VENUS_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1.5F, 6.0F)), Constant.Block.SOFT_VENUS_ROCK);
    public static final Block HARD_VENUS_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1.5F, 6.0F)), Constant.Block.HARD_VENUS_ROCK);
    public static final Block SCORCHED_VENUS_ROCK = registerBlock(new ScorchedRockBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1.5F, 6.0F)), Constant.Block.SCORCHED_VENUS_ROCK);
    public static final Block VOLCANIC_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(2.2F, 0.5F)), Constant.Block.VOLCANIC_ROCK);
    public static final Block PUMICE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).requiresTool().strength(1.5F, 6.0F)), Constant.Block.PUMICE);
    public static final Block VAPOR_SPOUT = registerBlock(new VaporSpoutBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.BROWN).dropsNothing().requiresTool().strength(1.5F, 2.0F)), Constant.Block.VAPOR_SPOUT);

    // MISC DECOR
    public static final Block WALKWAY = registerBlock(new Walkway(FabricBlockSettings.of(Material.METAL).requiresTool().strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL)), Constant.Block.WALKWAY);
    public static final Block PIPE_WALKWAY = registerBlock(new PipeWalkway(FabricBlockSettings.of(Material.METAL).requiresTool().strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL)), Constant.Block.PIPE_WALKWAY);
    public static final Block WIRE_WALKWAY = registerBlock(new WireWalkway(FabricBlockSettings.of(Material.METAL).requiresTool().strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL)), Constant.Block.WIRE_WALKWAY);
    public static final Block TIN_LADDER = registerBlock(new TinLadderBlock(FabricBlockSettings.of(Material.METAL).requiresTool().strength(1.0f, 1.0f).sounds(BlockSoundGroup.METAL)), Constant.Block.TIN_LADDER);
    public static final Block GRATING = registerBlock(new GratingBlock(FabricBlockSettings.of(Material.METAL, MaterialColor.STONE).requiresTool().strength(2.5f, 6.0f).sounds(BlockSoundGroup.METAL)), Constant.Block.GRATING);

    // SPECIAL
    public static final Block ALUMINUM_WIRE = registerBlock(new AluminumWireBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).strength(0.2F)), Constant.Block.ALUMINUM_WIRE);
    public static final Block SEALABLE_ALUMINUM_WIRE = registerBlock(new SealableAluminumWireBlock(FabricBlockSettings.copyOf(TIN_DECORATIONS[0])), Constant.Block.SEALABLE_ALUMINUM_WIRE);
    public static final Block GLASS_FLUID_PIPE = registerBlock(new GlassFluidPipeBlock(FabricBlockSettings.of(Material.GLASS).breakByHand(true).sounds(BlockSoundGroup.GLASS)), Constant.Block.GLASS_FLUID_PIPE);
    public static final Block FALLEN_METEOR = registerBlock(new FallenMeteorBlock(FabricBlockSettings.of(Material.STONE).requiresTool().breakByTool(FabricToolTags.PICKAXES, 2).strength(2.0F, 6.0F).sounds(BlockSoundGroup.STONE)), Constant.Block.FALLEN_METEOR);

    // LIGHT PANELS
    public static final Block SQUARE_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL)), Constant.Block.SQUARE_LIGHT_PANEL);
    public static final Block SPOTLIGHT_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 3.0f), Constant.Block.SPOTLIGHT_LIGHT_PANEL);
    public static final Block LINEAR_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 5.0f), Constant.Block.LINEAR_LIGHT_PANEL);
    public static final Block DASHED_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 1.0f), Constant.Block.DASHED_LIGHT_PANEL);
    public static final Block DIAGONAL_LIGHT_PANEL = registerBlock(new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 1.0f), Constant.Block.DIAGONAL_LIGHT_PANEL);

    // VACUUM GLASS
    public static final Block VACUUM_GLASS = registerBlock(new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS)), Constant.Block.VACUUM_GLASS);
    public static final Block CLEAR_VACUUM_GLASS = registerBlock(new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS)), Constant.Block.CLEAR_VACUUM_GLASS);
    public static final Block STRONG_VACUUM_GLASS = registerBlock(new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS)), Constant.Block.STRONG_VACUUM_GLASS);

    // ORES
    public static final Block SILICON_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(5.0F, 3.0F)), Constant.Block.SILICON_ORE);
    public static final Block ASTEROID_ALUMINUM_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).requiresTool().breakByTool(FabricToolTags.PICKAXES, 2).strength(5.0F, 3.0F)), Constant.Block.ASTEROID_ALUMINUM_ORE);
    public static final Block MOON_CHEESE_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(5.0F, 3.0F)), Constant.Block.MOON_CHEESE_ORE);
    public static final Block MOON_CHEESE_BLOCK = registerBlock(new CakeBlock(FabricBlockSettings.of(Material.CAKE).strength(0.5F).sounds(BlockSoundGroup.WOOL)), Constant.Block.MOON_CHEESE_BLOCK);
    public static final Block MOON_COPPER_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(5.0F, 3.0F)), Constant.Block.MOON_COPPER_ORE);
    public static final Block MOON_BASALT_COPPER_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(5.0F, 3.0F)), Constant.Block.MOON_BASALT_COPPER_ORE);
    public static final Block MARS_COPPER_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(5.0F, 3.0F)), Constant.Block.MARS_COPPER_ORE);
    public static final Block DESH_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(5.0F, 3.0F)), Constant.Block.DESH_ORE);
    public static final Block ILMENITE_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).requiresTool().breakByTool(FabricToolTags.PICKAXES, 2).strength(5.0F, 3.0F)), Constant.Block.ILMENITE_ORE);
    public static final Block MARS_IRON_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(5.0F, 3.0F)), Constant.Block.MARS_IRON_ORE);
    public static final Block ASTEROID_IRON_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(5.0F, 3.0F)), Constant.Block.ASTEROID_IRON_ORE);
    public static final Block MOON_TIN_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(5.0F, 3.0F)), Constant.Block.MOON_TIN_ORE);
    public static final Block MARS_TIN_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(5.0F, 3.0F)), Constant.Block.MARS_TIN_ORE);
    public static final Block GALENA_ORE = registerBlock(new OreBlock(FabricBlockSettings.of(Material.STONE).requiresTool().strength(5.0F, 3.0F)), Constant.Block.GALENA_ORE);

    // COMPACT MINERAL BLOCKS
    public static final Block SILICON_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).requiresTool().sounds(BlockSoundGroup.METAL)), Constant.Block.SILICON_BLOCK);
    public static final Block METEORIC_IRON_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).requiresTool().sounds(BlockSoundGroup.METAL)), Constant.Block.METEORIC_IRON_BLOCK);
    public static final Block DESH_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).requiresTool().sounds(BlockSoundGroup.METAL)), Constant.Block.DESH_BLOCK);
    public static final Block TITANIUM_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).requiresTool().sounds(BlockSoundGroup.METAL)), Constant.Block.TITANIUM_BLOCK);
    public static final Block LEAD_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).requiresTool().sounds(BlockSoundGroup.METAL)), Constant.Block.LEAD_BLOCK);
    public static final Block LUNAR_SAPPHIRE_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(5.0F, 6.0F).requiresTool().sounds(BlockSoundGroup.METAL)), Constant.Block.LUNAR_SAPPHIRE_BLOCK);

    // MOON VILLAGER SPECIAL
    public static final Block LUNAR_CARTOGRAPHY_TABLE = registerBlock(new LunarCartographyTableBlock(FabricBlockSettings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)), Constant.Block.LUNAR_CARTOGRAPHY_TABLE);

    // MISC WORLD GEN
    public static final Block CAVERNOUS_VINE = registerBlock(new CavernousVineBlock(FabricBlockSettings.of(Material.CACTUS, MaterialColor.GREEN).dropsNothing().noCollision().luminance(0).sounds(BlockSoundGroup.GRASS).ticksRandomly()), Constant.Block.CAVERNOUS_VINE);
    public static final Block POISONOUS_CAVERNOUS_VINE = registerBlock(new PoisonousCavernousVineBlock(FabricBlockSettings.of(Material.CACTUS, MaterialColor.GREEN).dropsNothing().noCollision().luminance(3).sounds(BlockSoundGroup.GRASS).ticksRandomly()), Constant.Block.POISONOUS_CAVERNOUS_VINE);
    public static final Block MOON_BERRY_BUSH = registerBlock(new MoonBerryBushBlock(FabricBlockSettings.of(Material.PLANT, MaterialColor.GREEN).dropsNothing().noCollision().luminance(3).sounds(BlockSoundGroup.SWEET_BERRY_BUSH).ticksRandomly()), Constant.Block.MOON_BERRY_BUSH);
    public static final Block MOON_CHEESE_LEAVES = registerBlock(new LeavesBlock(FabricBlockSettings.of(Material.LEAVES).strength(0.2F, 0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque()), Constant.Block.MOON_CHEESE_LEAVES);
    public static final Block MOON_CHEESE_LOG = registerBlock(new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, MaterialColor.YELLOW).strength(2.0F).sounds(BlockSoundGroup.WOOD)), Constant.Block.MOON_CHEESE_LOG);

    // DUMMY
    public static final Block SOLAR_PANEL_PART = registerBlockWithoutItem(new SolarPanelPartBlock(FabricBlockSettings.of(Material.METAL).strength(-1.0F, 5.0F).dropsNothing().sounds(BlockSoundGroup.METAL)), Constant.Block.SOLAR_PANEL_PART);

    // MACHINES
    public static final Block CIRCUIT_FABRICATOR = registerMachine(new MachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL), blockView -> new CircuitFabricatorBlockEntity(),
            new TranslatableText("tooltip.galacticraft.circuit_fabricator").setStyle(Constant.Text.DARK_GRAY_STYLE)), Constant.Block.CIRCUIT_FABRICATOR);
    public static final Block COMPRESSOR = registerMachine(new MachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL), blockView -> new CompressorBlockEntity(),
            new TranslatableText("tooltip.galacticraft.compressor").setStyle(Constant.Text.DARK_GRAY_STYLE)), Constant.Block.COMPRESSOR);
    public static final Block ELECTRIC_COMPRESSOR = registerMachine(new MachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL), blockView -> new ElectricCompressorBlockEntity(),
            new TranslatableText("tooltip.galacticraft.electric_compressor").setStyle(Constant.Text.DARK_GRAY_STYLE)), Constant.Block.ELECTRIC_COMPRESSOR);
    public static final Block COAL_GENERATOR = registerMachine(new CoalGeneratorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL).luminance(state -> state.get(Constant.Property.ACTIVE) ? 13 : 0)), Constant.Block.COAL_GENERATOR);
    public static final Block BASIC_SOLAR_PANEL = registerMachine(new BasicSolarPanelBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL)), Constant.Block.BASIC_SOLAR_PANEL);
    public static final Block ADVANCED_SOLAR_PANEL = registerMachine(new AdvancedSolarPanelBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL)), Constant.Block.ADVANCED_SOLAR_PANEL);
    public static final Block ENERGY_STORAGE_MODULE = registerMachine(new MachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL), blockView -> new EnergyStorageModuleBlockEntity(),
            new TranslatableText("tooltip.galacticraft.energy_storage_module").setStyle(Constant.Text.DARK_GRAY_STYLE)), Constant.Block.ENERGY_STORAGE_MODULE);
    public static final Block ELECTRIC_FURNACE = registerMachine(new MachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL), blockView -> new ElectricFurnaceBlockEntity(),
            new TranslatableText("tooltip.galacticraft.electric_furnace").setStyle(Constant.Text.DARK_GRAY_STYLE)), Constant.Block.ELECTRIC_FURNACE);
    public static final Block ELECTRIC_ARC_FURNACE = registerMachine(new MachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL), blockView -> new ElectricArcFurnaceBlockEntity(),
            new TranslatableText("tooltip.galacticraft.electric_arc_furnace").setStyle(Constant.Text.DARK_GRAY_STYLE)), Constant.Block.ELECTRIC_ARC_FURNACE);
    public static final Block OXYGEN_COLLECTOR = registerMachine(new OxygenCollectorBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL)), Constant.Block.OXYGEN_COLLECTOR);
    public static final Block OXYGEN_SEALER = registerMachine(new OxygenSealerBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL)), Constant.Block.OXYGEN_SEALER);
    public static final Block REFINERY = registerMachine(new RefineryBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL)), Constant.Block.REFINERY);
    public static final Block BUBBLE_DISTRIBUTOR = registerMachine(new MachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL), blockView -> new BubbleDistributorBlockEntity(),
            new TranslatableText("tooltip.galacticraft.oxygen_bubble_distributor").setStyle(Constant.Text.DARK_GRAY_STYLE)), Constant.Block.OXYGEN_BUBBLE_DISTRIBUTOR);
    public static final Block OXYGEN_DECOMPRESSOR = registerMachine(new MachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL), blockView -> new OxygenDecompressorBlockEntity(),
            new TranslatableText("tooltip.galacticraft.oxygen_decompressor").setStyle(Constant.Text.DARK_GRAY_STYLE)), Constant.Block.OXYGEN_DECOMPRESSOR);
    public static final Block OXYGEN_COMPRESSOR = registerMachine(new MachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL), blockView -> new OxygenCompressorBlockEntity(),
            new TranslatableText("tooltip.galacticraft.oxygen_compressor").setStyle(Constant.Text.DARK_GRAY_STYLE)), Constant.Block.OXYGEN_COMPRESSOR);
    public static final Block OXYGEN_STORAGE_MODULE = registerMachine(new MachineBlock(FabricBlockSettings.of(Material.METAL).strength(3.0F, 5.0F).requiresTool().sounds(BlockSoundGroup.METAL), blockView -> new OxygenStorageModuleBlockEntity(),
            new TranslatableText("tooltip.galacticraft.oxygen_storage_module").setStyle(Constant.Text.DARK_GRAY_STYLE)), Constant.Block.OXYGEN_STORAGE_MODULE);

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
        blocks[1] = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(baseBlock)), correctBrickName(baseId) + "_slab");
        blocks[2] = registerBlock(new StairsBlock(baseBlock.getDefaultState(), FabricBlockSettings.copyOf(baseBlock)), correctBrickName(baseId) + "_stairs");
        blocks[3] = registerBlock(new WallBlock(FabricBlockSettings.copyOf(baseBlock)), correctBrickName(baseId) + "_wall");

        if (hasDetailed) {
            blocks[4] = registerBlock(new Block(FabricBlockSettings.copyOf(baseBlock)), "detailed_" + baseId);
            blocks[5] = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(baseBlock)), "detailed_" + correctBrickName(baseId) + "_slab");
            blocks[6] = registerBlock(new StairsBlock(baseBlock.getDefaultState(), FabricBlockSettings.copyOf(baseBlock)), "detailed_" + correctBrickName(baseId) + "_stairs");
            blocks[7] = registerBlock(new WallBlock(FabricBlockSettings.copyOf(baseBlock)), "detailed_" + correctBrickName(baseId) + "_wall");
        }
        return blocks;
    }

    private static String correctBrickName(String baseId) {
        return baseId.replace("bricks", "brick");
    }

    private static <T extends Block> T registerBlock(T block, String id) {
        return registerBlock(block, id, BLOCKS_GROUP);
    }

    private static <T extends Block> T registerMachine(T block, String id) {
        return registerBlock(block, id, MACHINES_GROUP);
    }

    private static <T extends Block> T registerBlock(T block, String id, ItemGroup group) {
        Identifier identifier = new Identifier(Constant.MOD_ID, id);
        Registry.register(Registry.BLOCK, identifier, block);
        BlockItem item = Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings().group(group)));
        item.appendBlocks(Item.BLOCK_ITEMS, item);
        return block;
    }

    private static <T extends Block> T registerBlockWithoutItem(T block, String id) {
        return Registry.register(Registry.BLOCK, new Identifier(Constant.MOD_ID, id), block);
    }
}