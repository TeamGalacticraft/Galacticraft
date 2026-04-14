/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import micdoodle8.mods.galacticraft.annotations.ForRemoval;
import micdoodle8.mods.galacticraft.annotations.ReplaceWith;
import micdoodle8.mods.galacticraft.core.blocks.BlockAirLockFrame;
import micdoodle8.mods.galacticraft.core.blocks.BlockAirLockWall;
import micdoodle8.mods.galacticraft.core.blocks.BlockAluminumWire;
import micdoodle8.mods.galacticraft.core.blocks.BlockBasic;
import micdoodle8.mods.galacticraft.core.blocks.BlockBasicMoon;
import micdoodle8.mods.galacticraft.core.blocks.BlockBasicMoon.EnumBlockBasicMoon;
import micdoodle8.mods.galacticraft.core.blocks.BlockBossSpawner;
import micdoodle8.mods.galacticraft.core.blocks.BlockBreathableAir;
import micdoodle8.mods.galacticraft.core.blocks.BlockBrightAir;
import micdoodle8.mods.galacticraft.core.blocks.BlockBrightBreathableAir;
import micdoodle8.mods.galacticraft.core.blocks.BlockBrightLamp;
import micdoodle8.mods.galacticraft.core.blocks.BlockCargoLoader;
import micdoodle8.mods.galacticraft.core.blocks.BlockCheese;
import micdoodle8.mods.galacticraft.core.blocks.BlockCompactNasaWorkbench;
import micdoodle8.mods.galacticraft.core.blocks.BlockConcealedDetector;
import micdoodle8.mods.galacticraft.core.blocks.BlockConcealedRedstone;
import micdoodle8.mods.galacticraft.core.blocks.BlockConcealedRepeater;
import micdoodle8.mods.galacticraft.core.blocks.BlockCrafting;
import micdoodle8.mods.galacticraft.core.blocks.BlockDish;
import micdoodle8.mods.galacticraft.core.blocks.BlockDoubleSlabGC;
import micdoodle8.mods.galacticraft.core.blocks.BlockEmergencyBox;
import micdoodle8.mods.galacticraft.core.blocks.BlockEnclosed;
import micdoodle8.mods.galacticraft.core.blocks.BlockFallenMeteor;
import micdoodle8.mods.galacticraft.core.blocks.BlockFluidGC;
import micdoodle8.mods.galacticraft.core.blocks.BlockFluidPipe;
import micdoodle8.mods.galacticraft.core.blocks.BlockFluidTank;
import micdoodle8.mods.galacticraft.core.blocks.BlockFuelLoader;
import micdoodle8.mods.galacticraft.core.blocks.BlockGlowstoneTorch;
import micdoodle8.mods.galacticraft.core.blocks.BlockGrating;
import micdoodle8.mods.galacticraft.core.blocks.BlockLandingPad;
import micdoodle8.mods.galacticraft.core.blocks.BlockLandingPadFull;
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine;
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine2;
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine3;
import micdoodle8.mods.galacticraft.core.blocks.BlockMachine4;
import micdoodle8.mods.galacticraft.core.blocks.BlockMachineTiered;
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti;
import micdoodle8.mods.galacticraft.core.blocks.BlockNasaWorkbench;
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenCollector;
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenCompressor;
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenDetector;
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenDistributor;
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenSealer;
import micdoodle8.mods.galacticraft.core.blocks.BlockPanelLighting;
import micdoodle8.mods.galacticraft.core.blocks.BlockParaChest;
import micdoodle8.mods.galacticraft.core.blocks.BlockPlatform;
import micdoodle8.mods.galacticraft.core.blocks.BlockRefinery;
import micdoodle8.mods.galacticraft.core.blocks.BlockScreen;
import micdoodle8.mods.galacticraft.core.blocks.BlockSlabGC;
import micdoodle8.mods.galacticraft.core.blocks.BlockSolar;
import micdoodle8.mods.galacticraft.core.blocks.BlockSpaceGlass;
import micdoodle8.mods.galacticraft.core.blocks.BlockSpaceGlass.GlassFrame;
import micdoodle8.mods.galacticraft.core.blocks.BlockSpaceGlass.GlassType;
import micdoodle8.mods.galacticraft.core.blocks.BlockSpaceStationBase;
import micdoodle8.mods.galacticraft.core.blocks.BlockSpinThruster;
import micdoodle8.mods.galacticraft.core.blocks.BlockStairsGC;
import micdoodle8.mods.galacticraft.core.blocks.BlockTelemetry;
import micdoodle8.mods.galacticraft.core.blocks.BlockTier1TreasureChest;
import micdoodle8.mods.galacticraft.core.blocks.BlockUnlitTorch;
import micdoodle8.mods.galacticraft.core.blocks.BlockWallGC;
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock;
import micdoodle8.mods.galacticraft.core.items.ItemBlockAirLock;
import micdoodle8.mods.galacticraft.core.items.ItemBlockAluminumWire;
import micdoodle8.mods.galacticraft.core.items.ItemBlockArclamp;
import micdoodle8.mods.galacticraft.core.items.ItemBlockBase;
import micdoodle8.mods.galacticraft.core.items.ItemBlockCargoLoader;
import micdoodle8.mods.galacticraft.core.items.ItemBlockCheese;
import micdoodle8.mods.galacticraft.core.items.ItemBlockCreativeGC;
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc;
import micdoodle8.mods.galacticraft.core.items.ItemBlockEmergencyBox;
import micdoodle8.mods.galacticraft.core.items.ItemBlockEnclosed;
import micdoodle8.mods.galacticraft.core.items.ItemBlockGC;
import micdoodle8.mods.galacticraft.core.items.ItemBlockGlassGC;
import micdoodle8.mods.galacticraft.core.items.ItemBlockLandingPad;
import micdoodle8.mods.galacticraft.core.items.ItemBlockMachine;
import micdoodle8.mods.galacticraft.core.items.ItemBlockMoon;
import micdoodle8.mods.galacticraft.core.items.ItemBlockNasaWorkbench;
import micdoodle8.mods.galacticraft.core.items.ItemBlockOxygenCompressor;
import micdoodle8.mods.galacticraft.core.items.ItemBlockPanel;
import micdoodle8.mods.galacticraft.core.items.ItemBlockSlabGC;
import micdoodle8.mods.galacticraft.core.items.ItemBlockSolar;
import micdoodle8.mods.galacticraft.core.items.ItemBlockThruster;
import micdoodle8.mods.galacticraft.core.items.ItemBlockWallGC;
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.StackSorted;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Ordering;

public class GCBlocks
{
    //@noformat
    public static Block breatheableAir;
    public static Block brightAir;
    public static Block brightBreatheableAir;
    public static Block brightLamp;
    public static Block treasureChestTier1;
    public static Block landingPad;
    public static Block unlitTorch;
    public static Block unlitTorchLit;
    public static Block oxygenDistributor;
    public static Block oxygenPipe;
    public static Block oxygenPipePull;
    public static Block oxygenCollector;
    public static Block oxygenCompressor;
    public static Block oxygenSealer;
    public static Block oxygenDetector;
    public static Block nasaWorkbench;
    public static Block compactNasaWorkbench;
    public static Block fallenMeteor;
    public static Block basicBlock;
    public static Block airLockFrame;
    public static Block airLockSeal;
    public static BlockSpaceGlass spaceGlassClear;
    public static BlockSpaceGlass spaceGlassVanilla;
    public static BlockSpaceGlass spaceGlassStrong;
    public static BlockSpaceGlass spaceGlassTinClear;
    public static BlockSpaceGlass spaceGlassTinVanilla;
    public static BlockSpaceGlass spaceGlassTinStrong;
    public static Block crafting;
    public static Block crudeOil;
    public static Block fuel;
    public static Block refinery;
    public static Block fuelLoader;
    public static Block landingPadFull;
    public static Block spaceStationBase;
    public static Block fakeBlock;
    public static Block sealableBlock;
    public static Block cargoLoader;
    public static Block parachest;
    public static Block solarPanel;
    public static Block radioTelescope;
    public static Block machineBase;
    public static Block machineBase2;
    public static Block machineBase3;
    public static Block machineBase4;
    public static Block machineTiered;
    public static Block aluminumWire;
    public static Block panelLighting;
    public static Block glowstoneTorch;
    public static Block blockMoon;
    public static Block cheeseBlock;
    public static Block spinThruster;
    public static Block screen;
    public static Block telemetry;
    public static Block fluidTank;
    public static Block bossSpawner;
    public static Block slabGCHalf;
    public static Block slabGCDouble;
    public static Block tinStairs1;
    public static Block tinStairs2;
    public static Block moonStoneStairs;
    public static Block moonBricksStairs;
    public static Block wallGC;
    public static Block concealedRedstone;
    public static Block concealedRepeater_Powered;
    public static Block concealedRepeater_Unpowered;
    public static Block concealedDetector;
    public static Block platform;
    public static Block emergencyBox;
    public static Block grating;
    public static Block gratingWater;
    public static Block gratingLava;
    // @format

    public static final Material                                machine              = new Material(MapColor.IRON);

    public static ArrayList<Block>                              hiddenBlocks         = new ArrayList<>();
    public static ArrayList<Block>                              otherModTorchesLit   = new ArrayList<>();
    public static ArrayList<Block>                              otherModTorchesUnlit = new ArrayList<>();

    public static Map<EnumSortCategoryBlock, List<StackSorted>> sortMapBlocks        = Maps.newHashMap();
    public static HashMap<Block, Block>                         itemChanges          = new HashMap<>(4, 1.0F);

    public static void initBlocks()
    {
        GCBlocks.breatheableAir = new BlockBreathableAir("breatheable_air");
        GCBlocks.brightAir = new BlockBrightAir("bright_air");
        GCBlocks.brightBreatheableAir = new BlockBrightBreathableAir("bright_breathable_air");
        GCBlocks.brightLamp = new BlockBrightLamp("arclamp");
        GCBlocks.treasureChestTier1 = new BlockTier1TreasureChest("treasure_chest");
        GCBlocks.landingPad = new BlockLandingPad("landing_pad");
        GCBlocks.landingPadFull = new BlockLandingPadFull("landing_pad_full");
        GCBlocks.unlitTorch = new BlockUnlitTorch(false, "unlit_torch");
        GCBlocks.unlitTorchLit = new BlockUnlitTorch(true, "unlit_torch_lit");
        GCBlocks.oxygenDistributor = new BlockOxygenDistributor("distributor");
        GCBlocks.oxygenPipe = new BlockFluidPipe("fluid_pipe", BlockFluidPipe.EnumPipeMode.NORMAL);
        GCBlocks.oxygenPipePull = new BlockFluidPipe("fluid_pipe_pull", BlockFluidPipe.EnumPipeMode.PULL);
        GCBlocks.oxygenCollector = new BlockOxygenCollector("collector");
        GCBlocks.nasaWorkbench = new BlockNasaWorkbench("rocket_workbench");
        GCBlocks.compactNasaWorkbench = new BlockCompactNasaWorkbench("compact_workbench");
        GCBlocks.fallenMeteor = new BlockFallenMeteor("fallen_meteor");
        GCBlocks.basicBlock = new BlockBasic("basic_block_core");
        GCBlocks.airLockFrame = new BlockAirLockFrame("air_lock_frame");
        GCBlocks.airLockSeal = new BlockAirLockWall("air_lock_seal");
        // These glass types have to be registered as 6 separate blocks, 
        //   - (a) to allow different coloring of each one and 
        //   - (b) because the Forge
        // MultiLayer custom model does not allow for different textures to be set for different variants
        GCBlocks.spaceGlassVanilla = (BlockSpaceGlass) new BlockSpaceGlass("space_glass_vanilla", GlassType.VANILLA, GlassFrame.PLAIN, null).setHardness(0.3F).setResistance(3F);
        GCBlocks.spaceGlassClear = (BlockSpaceGlass) new BlockSpaceGlass("space_glass_clear", GlassType.CLEAR, GlassFrame.PLAIN, null).setHardness(0.3F).setResistance(3F);
        GCBlocks.spaceGlassStrong = (BlockSpaceGlass) new BlockSpaceGlass("space_glass_strong", GlassType.STRONG, GlassFrame.PLAIN, null).setHardness(4F).setResistance(35F);
        GCBlocks.spaceGlassTinVanilla = (BlockSpaceGlass) new BlockSpaceGlass("space_glass_vanilla_tin", GlassType.VANILLA, GlassFrame.TIN_DECO, GCBlocks.spaceGlassVanilla).setHardness(0.3F).setResistance(4F);
        GCBlocks.spaceGlassTinClear = (BlockSpaceGlass) new BlockSpaceGlass("space_glass_clear_tin", GlassType.CLEAR, GlassFrame.TIN_DECO, GCBlocks.spaceGlassClear).setHardness(0.3F).setResistance(4F);
        GCBlocks.spaceGlassTinStrong = (BlockSpaceGlass) new BlockSpaceGlass("space_glass_strong_tin", GlassType.STRONG, GlassFrame.TIN_DECO, GCBlocks.spaceGlassStrong).setHardness(4F).setResistance(35F);
        GCBlocks.crafting = new BlockCrafting("magnetic_table");
        GCBlocks.refinery = new BlockRefinery("refinery");
        GCBlocks.oxygenCompressor = new BlockOxygenCompressor(false, "oxygen_compressor");
        GCBlocks.fuelLoader = new BlockFuelLoader("fuel_loader");
        GCBlocks.spaceStationBase = new BlockSpaceStationBase("space_station_base");
        GCBlocks.fakeBlock = new BlockMulti("block_multi");
        GCBlocks.oxygenSealer = new BlockOxygenSealer("sealer");
        GCBlocks.sealableBlock = new BlockEnclosed("enclosed");
        GCBlocks.oxygenDetector = new BlockOxygenDetector("oxygen_detector");
        GCBlocks.cargoLoader = new BlockCargoLoader("cargo");
        GCBlocks.parachest = new BlockParaChest("parachest");
        GCBlocks.solarPanel = new BlockSolar("solar");
        GCBlocks.radioTelescope = new BlockDish("dishbase");
        GCBlocks.machineBase = new BlockMachine("machine");
        GCBlocks.machineBase2 = new BlockMachine2("machine2");
        GCBlocks.machineBase3 = new BlockMachine3("machine3");
        GCBlocks.machineBase4 = new BlockMachine4("machine4");
        GCBlocks.machineTiered = new BlockMachineTiered("machine_tiered");
        GCBlocks.aluminumWire = new BlockAluminumWire("aluminum_wire");
        GCBlocks.panelLighting = new BlockPanelLighting("panel_lighting");
        GCBlocks.glowstoneTorch = new BlockGlowstoneTorch("glowstone_torch");
        GCBlocks.blockMoon = new BlockBasicMoon("basic_block_moon");
        GCBlocks.cheeseBlock = new BlockCheese("cheese");
        GCBlocks.spinThruster = new BlockSpinThruster("spin_thruster");
        GCBlocks.screen = new BlockScreen("view_screen");
        GCBlocks.telemetry = new BlockTelemetry("telemetry");
        GCBlocks.fluidTank = new BlockFluidTank("fluid_tank");
        GCBlocks.bossSpawner = new BlockBossSpawner("boss_spawner");
        GCBlocks.slabGCHalf = new BlockSlabGC("slab_gc_half", Material.ROCK);
        GCBlocks.slabGCDouble = new BlockDoubleSlabGC("slab_gc_double", Material.ROCK);
        GCBlocks.tinStairs1 = new BlockStairsGC("tin_stairs_1", basicBlock.getDefaultState().withProperty(BlockBasic.BASIC_TYPE, BlockBasic.EnumBlockBasic.ALUMINUM_DECORATION_BLOCK_0)).setHardness(2.0F);
        GCBlocks.tinStairs2 = new BlockStairsGC("tin_stairs_2", basicBlock.getDefaultState().withProperty(BlockBasic.BASIC_TYPE, BlockBasic.EnumBlockBasic.ALUMINUM_DECORATION_BLOCK_1)).setHardness(2.0F);
        GCBlocks.moonStoneStairs = new BlockStairsGC("moon_stairs_stone", blockMoon.getDefaultState().withProperty(BlockBasicMoon.BASIC_TYPE_MOON, BlockBasicMoon.EnumBlockBasicMoon.MOON_STONE)).setHardness(1.5F);
        GCBlocks.moonBricksStairs = new BlockStairsGC("moon_stairs_brick", blockMoon.getDefaultState().withProperty(BlockBasicMoon.BASIC_TYPE_MOON, BlockBasicMoon.EnumBlockBasicMoon.MOON_DUNGEON_BRICK)).setHardness(4.0F);
        GCBlocks.wallGC = new BlockWallGC("wall_gc");
        GCBlocks.concealedRedstone = new BlockConcealedRedstone("concealed_redstone");
        GCBlocks.concealedRepeater_Powered = new BlockConcealedRepeater("concealed_repeater_pow", true);
        GCBlocks.concealedRepeater_Unpowered = new BlockConcealedRepeater("concealed_repeater", false);
        GCBlocks.concealedDetector = new BlockConcealedDetector("concealed_detector");
        GCBlocks.platform = new BlockPlatform("platform");
        GCBlocks.emergencyBox = new BlockEmergencyBox("emergency_box");
        GCBlocks.grating = new BlockGrating("grating", ConfigManagerCore.allowLiquidGratings ? Material.CARPET : Material.IRON);
        GCBlocks.gratingWater = new BlockGrating("grating1", Material.WATER);
        GCBlocks.gratingLava = new BlockGrating("grating2", Material.LAVA).setLightLevel(1.0F);

        // Hide certain items from NEI
        GCBlocks.hiddenBlocks.add(GCBlocks.airLockSeal);
        GCBlocks.hiddenBlocks.add(GCBlocks.oxygenPipePull);
        GCBlocks.hiddenBlocks.add(GCBlocks.unlitTorch);
        GCBlocks.hiddenBlocks.add(GCBlocks.unlitTorchLit);
        GCBlocks.hiddenBlocks.add(GCBlocks.landingPadFull);
        GCBlocks.hiddenBlocks.add(GCBlocks.spaceStationBase);
        GCBlocks.hiddenBlocks.add(GCBlocks.bossSpawner);
        GCBlocks.hiddenBlocks.add(GCBlocks.slabGCDouble);
        GCBlocks.hiddenBlocks.add(GCBlocks.concealedRepeater_Powered);

        // Register blocks before register ores, so that the ItemStack picks up
        // the correct item
        GCBlocks.registerBlocks();
        GCBlocks.setHarvestLevels();
    }

    public static void oreDictRegistrations()
    {
        OreDictionary.registerOre("oreCopper", new ItemStack(GCBlocks.basicBlock, 1, 5));
        OreDictionary.registerOre("oreCopper", new ItemStack(GCBlocks.blockMoon, 1, 0));
        OreDictionary.registerOre("oreTin", new ItemStack(GCBlocks.basicBlock, 1, 6));
        OreDictionary.registerOre("oreTin", new ItemStack(GCBlocks.blockMoon, 1, 1));
        OreDictionary.registerOre("oreAluminum", new ItemStack(GCBlocks.basicBlock, 1, 7));
        OreDictionary.registerOre("oreAluminium", new ItemStack(GCBlocks.basicBlock, 1, 7));
        OreDictionary.registerOre("oreNaturalAluminum", new ItemStack(GCBlocks.basicBlock, 1, 7));
        OreDictionary.registerOre("oreSilicon", new ItemStack(GCBlocks.basicBlock, 1, 8));
        OreDictionary.registerOre("oreCheese", new ItemStack(GCBlocks.blockMoon, 1, 2));

        OreDictionary.registerOre("blockCopper", new ItemStack(GCBlocks.basicBlock, 1, 9));
        OreDictionary.registerOre("blockTin", new ItemStack(GCBlocks.basicBlock, 1, 10));
        OreDictionary.registerOre("blockAluminum", new ItemStack(GCBlocks.basicBlock, 1, 11));
        OreDictionary.registerOre("blockAluminium", new ItemStack(GCBlocks.basicBlock, 1, 11));
        OreDictionary.registerOre("blockSilicon", new ItemStack(GCBlocks.basicBlock, 1, 13));

        OreDictionary.registerOre("turfMoon", new ItemStack(GCBlocks.blockMoon, 1, EnumBlockBasicMoon.MOON_TURF.getMeta()));
        OreDictionary.registerOre("itemCharcoal", new ItemStack(Items.COAL, 1, 1));
        OreDictionary.registerOre("itemCoal", new ItemStack(Items.COAL, 1));
    }

    public static void finalizeSort()
    {
        List<StackSorted> itemOrderListBlocks = Lists.newArrayList();
        for (EnumSortCategoryBlock type : EnumSortCategoryBlock.values())
        {
            if (type == EnumSortCategoryBlock.EGG)
                continue;
            List<StackSorted> stackSorteds = sortMapBlocks.get(type);
            if (stackSorteds != null)
            {
                itemOrderListBlocks.addAll(stackSorteds);
            }
            else
            {
                System.out.println("ERROR: null sort stack: " + type.toString());
            }
        }
        Comparator<ItemStack> tabSorterBlocks = Ordering.explicit(itemOrderListBlocks).onResultOf(input -> new StackSorted(input.getItem(), input.getItemDamage()));
        GalacticraftCore.galacticraftBlocksTab.setTabSorter(tabSorterBlocks);
    }

    private static void setHarvestLevel(Block block, String toolClass, int level, int meta)
    {
        block.setHarvestLevel(toolClass, level, block.getStateFromMeta(meta));
    }

    public static void doOtherModsTorches(IForgeRegistry<Block> registry)
    {
        BlockUnlitTorch torch;
        BlockUnlitTorch torchLit;

        if (CompatibilityManager.isTConstructLoaded)
        {
            Block modTorch = null;
            try
            {
                // tconstruct.world.TinkerWorld.stoneTorch
                Class<?> clazz = Class.forName("slimeknights.tconstruct.gadgets.TinkerGadgets");
                modTorch = (Block) clazz.getDeclaredField("stoneTorch").get(null);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            if (modTorch != null)
            {
                torch = new BlockUnlitTorch(false, "unlit_torch_stone");
                torchLit = new BlockUnlitTorch(true, "unlit_torch_stone_lit");
                GCBlocks.hiddenBlocks.add(torch);
                GCBlocks.hiddenBlocks.add(torchLit);
                GCBlocks.otherModTorchesUnlit.add(torch);
                GCBlocks.otherModTorchesLit.add(torchLit);
                registerBlock(torch, ItemBlockGC.class);
                registerBlock(torchLit, ItemBlockGC.class);
                registry.register(torch);
                registry.register(torchLit);
                BlockUnlitTorch.register(torch, torchLit, modTorch);
                GalacticraftCore.logger.info("Galacticraft: activating Tinker's Construct compatibility.");
            }
        }
    }

    public static void registerFuel()
    {
        GCBlocks.fuel = new BlockFluidGC(GCFluids.fluidFuel, "fuel");
        ((BlockFluidGC) GCBlocks.fuel).setQuantaPerBlock(3);
        GCBlocks.fuel.setTranslationKey("fuel");
        GCBlocks.registerBlock(GCBlocks.fuel, ItemBlockGC.class);
    }

    public static void registerOil()
    {
        GCBlocks.crudeOil = new BlockFluidGC(GCFluids.fluidOil, "oil");
        ((BlockFluidGC) GCBlocks.crudeOil).setQuantaPerBlock(3);
        GCBlocks.crudeOil.setTranslationKey("crude_oil_still");
        GCBlocks.registerBlock(GCBlocks.crudeOil, ItemBlockGC.class);
    }

    public static void setHarvestLevels()
    {
        setHarvestLevel(GCBlocks.basicBlock, "pickaxe", 1, 5); // Copper ore
        setHarvestLevel(GCBlocks.basicBlock, "pickaxe", 1, 6); // Tin ore
        setHarvestLevel(GCBlocks.basicBlock, "pickaxe", 1, 7); // Aluminium ore
        setHarvestLevel(GCBlocks.basicBlock, "pickaxe", 2, 8); // Silicon ore
        setHarvestLevel(GCBlocks.fallenMeteor, "pickaxe", 3, 0);
        setHarvestLevel(GCBlocks.blockMoon, "pickaxe", 1, 0); // Copper ore
        setHarvestLevel(GCBlocks.blockMoon, "pickaxe", 1, 1); // Tin ore
        setHarvestLevel(GCBlocks.blockMoon, "pickaxe", 1, 2); // Cheese ore
        setHarvestLevel(GCBlocks.blockMoon, "shovel", 0, 3); // Moon dirt
        setHarvestLevel(GCBlocks.blockMoon, "pickaxe", 0, 4); // Moon rock

        setHarvestLevel(GCBlocks.slabGCHalf, "pickaxe", 1, 0);
        setHarvestLevel(GCBlocks.slabGCHalf, "pickaxe", 1, 1);
        setHarvestLevel(GCBlocks.slabGCHalf, "pickaxe", 1, 2);
        setHarvestLevel(GCBlocks.slabGCHalf, "pickaxe", 3, 3);
        setHarvestLevel(GCBlocks.slabGCHalf, "pickaxe", 1, 4);
        setHarvestLevel(GCBlocks.slabGCHalf, "pickaxe", 3, 5);
        setHarvestLevel(GCBlocks.slabGCHalf, "pickaxe", 1, 6);

        setHarvestLevel(GCBlocks.slabGCDouble, "pickaxe", 1, 0);
        setHarvestLevel(GCBlocks.slabGCDouble, "pickaxe", 1, 1);
        setHarvestLevel(GCBlocks.slabGCDouble, "pickaxe", 1, 2);
        setHarvestLevel(GCBlocks.slabGCDouble, "pickaxe", 3, 3);
        setHarvestLevel(GCBlocks.slabGCDouble, "pickaxe", 1, 4);
        setHarvestLevel(GCBlocks.slabGCDouble, "pickaxe", 3, 5);
        setHarvestLevel(GCBlocks.slabGCDouble, "pickaxe", 1, 6);

        setHarvestLevel(GCBlocks.tinStairs1, "pickaxe", 1, 0);
        setHarvestLevel(GCBlocks.tinStairs1, "pickaxe", 1, 0);

        setHarvestLevel(GCBlocks.moonStoneStairs, "pickaxe", 1, 0);
        setHarvestLevel(GCBlocks.moonBricksStairs, "pickaxe", 3, 0);

        setHarvestLevel(GCBlocks.wallGC, "pickaxe", 1, 0);
        setHarvestLevel(GCBlocks.wallGC, "pickaxe", 1, 1);
        setHarvestLevel(GCBlocks.wallGC, "pickaxe", 1, 2);
        setHarvestLevel(GCBlocks.wallGC, "pickaxe", 3, 3);
        setHarvestLevel(GCBlocks.wallGC, "pickaxe", 0, 4);
        setHarvestLevel(GCBlocks.wallGC, "pickaxe", 3, 5);

        setHarvestLevel(GCBlocks.wallGC, "shovel", 0, 5);

        // Moon dungeon brick (actually unharvestable)
        setHarvestLevel(GCBlocks.blockMoon, "pickaxe", 3, 14);
    }

    public static void register(String modid, Block block, Class<? extends ItemBlock> itemClass, Object... itemCtorArgs)
    {
        String key = block.getTranslationKey().substring(5);
        ResourceLocation resourceLocation = new ResourceLocation(modid, key);
        if (block.getRegistryName() == null)
        {
            block.setRegistryName(resourceLocation);
        }
        GCCoreUtil.registerGalacticraftBlock(key, block);

        if (itemClass != null)
        {
            ItemBlock item = null;
            Class<?>[] ctorArgClasses = new Class<?>[itemCtorArgs.length + 1];
            ctorArgClasses[0] = Block.class;
            for (int idx = 1; idx < ctorArgClasses.length; idx++)
            {
                ctorArgClasses[idx] = itemCtorArgs[idx - 1].getClass();
            }

            try
            {
                Constructor<? extends ItemBlock> constructor = itemClass.getConstructor(ctorArgClasses);
                item = constructor.newInstance(ObjectArrays.concat(block, itemCtorArgs));
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            if (item != null)
            {
                GCCoreUtil.registerGalacticraftItem(key, item);
                if (item.getRegistryName() == null)
                {
                    item.setRegistryName(resourceLocation);
                }
            }
        }

    }

    @Deprecated
    @ForRemoval(deadline = "4.1.0")
    @ReplaceWith("Use Your Own Registry")
    public static void registerBlock(Block block, Class<? extends ItemBlock> itemClass, Object... itemCtorArgs)
    {
        String name = block.getTranslationKey().substring(5);
        if (block.getRegistryName() == null)
        {
            block.setRegistryName(name);
        }
        GCCoreUtil.registerGalacticraftBlock(name, block);

        if (itemClass != null)
        {
            ItemBlock item = null;
            Class<?>[] ctorArgClasses = new Class<?>[itemCtorArgs.length + 1];
            ctorArgClasses[0] = Block.class;
            for (int idx = 1; idx < ctorArgClasses.length; idx++)
            {
                ctorArgClasses[idx] = itemCtorArgs[idx - 1].getClass();
            }

            try
            {
                Constructor<? extends ItemBlock> constructor = itemClass.getConstructor(ctorArgClasses);
                item = constructor.newInstance(ObjectArrays.concat(block, itemCtorArgs));
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            if (item != null)
            {
                GCCoreUtil.registerGalacticraftItem(name, item);
                if (item.getRegistryName() == null)
                {
                    item.setRegistryName(name);
                }
            }
        }
    }

    @Deprecated
    @ForRemoval(deadline = "4.1.0")
    @ReplaceWith("Use Your Own Registry")
    public static void registerBlocks(IForgeRegistry<Block> registry)
    {
        for (Block block : GalacticraftCore.blocksList)
        {
            registry.register(block);
        }

        // Complete registration of various types of torches
        BlockUnlitTorch.register((BlockUnlitTorch) GCBlocks.unlitTorch, (BlockUnlitTorch) GCBlocks.unlitTorchLit, Blocks.TORCH);
    }

    public static boolean registeringSorted = false;

    public static void registerSorted(Block block)
    {
        if (block instanceof ISortableBlock)
        {
            Item item = Item.getItemFromBlock(block);
            if (item == Items.AIR)
            {
                return;
            }
            ISortableBlock sortableBlock = (ISortableBlock) block;
            NonNullList<ItemStack> blocks = NonNullList.create();
            registeringSorted = true;
            block.getSubBlocks(null, blocks);
            registeringSorted = false;
            if (blocks.isEmpty())
            {
                blocks.add(new ItemStack(block));
            }
            for (ItemStack stack : blocks)
            {
                EnumSortCategoryBlock categoryBlock = sortableBlock.getCategory(stack.getItemDamage());
                if (!sortMapBlocks.containsKey(categoryBlock))
                {
                    sortMapBlocks.put(categoryBlock, new ArrayList<>());
                }
                sortMapBlocks.get(categoryBlock).add(new StackSorted(stack.getItem(), stack.getItemDamage()));
            }
        }
        else if (block.getCreativeTab() == GalacticraftCore.galacticraftBlocksTab)
        {
            throw new RuntimeException(block.getClass() + " must inherit " + ISortableBlock.class.getSimpleName() + "!");
        }
    }

    private static void registerBlock(Block block, Class<? extends ItemBlock> itemClass)
    {
        register(Constants.MOD_ID_CORE, block, itemClass);
    }

    public static void registerBlocks()
    {
        registerBlock(GCBlocks.landingPad, ItemBlockLandingPad.class);
        registerBlock(GCBlocks.landingPadFull, ItemBlockGC.class);
        registerBlock(GCBlocks.unlitTorch, ItemBlockGC.class);
        registerBlock(GCBlocks.unlitTorchLit, ItemBlockGC.class);
        registerBlock(GCBlocks.breatheableAir, null);
        registerBlock(GCBlocks.brightAir, null);
        registerBlock(GCBlocks.brightBreatheableAir, null);
        registerBlock(GCBlocks.oxygenDistributor, ItemBlockDesc.class);
        registerBlock(GCBlocks.oxygenCollector, ItemBlockDesc.class);
        registerBlock(GCBlocks.oxygenCompressor, ItemBlockOxygenCompressor.class);
        registerBlock(GCBlocks.oxygenSealer, ItemBlockDesc.class);
        registerBlock(GCBlocks.oxygenDetector, ItemBlockDesc.class);
        registerBlock(GCBlocks.aluminumWire, ItemBlockAluminumWire.class);
        registerBlock(GCBlocks.oxygenPipe, ItemBlockDesc.class);
        registerBlock(GCBlocks.oxygenPipePull, ItemBlockDesc.class);
        registerBlock(GCBlocks.refinery, ItemBlockDesc.class);
        registerBlock(GCBlocks.fuelLoader, ItemBlockDesc.class);
        registerBlock(GCBlocks.cargoLoader, ItemBlockCargoLoader.class);
        registerBlock(GCBlocks.nasaWorkbench, ItemBlockNasaWorkbench.class);
        registerBlock(GCBlocks.compactNasaWorkbench, ItemBlockDesc.class);
        registerBlock(GCBlocks.basicBlock, ItemBlockBase.class);
        registerBlock(GCBlocks.airLockFrame, ItemBlockAirLock.class);
        registerBlock(GCBlocks.airLockSeal, ItemBlockGC.class);
        registerBlock(GCBlocks.spaceGlassClear, ItemBlockGlassGC.class);
        registerBlock(GCBlocks.spaceGlassVanilla, ItemBlockGlassGC.class);
        registerBlock(GCBlocks.spaceGlassStrong, ItemBlockGlassGC.class);
        registerBlock(GCBlocks.spaceGlassTinClear, null); // The corresponding item is already registered
        registerBlock(GCBlocks.spaceGlassTinVanilla, null); // The corresponding item is already registered
        registerBlock(GCBlocks.spaceGlassTinStrong, null); // The corresponding item is already registered
        registerBlock(GCBlocks.crafting, ItemBlockDesc.class);
        registerBlock(GCBlocks.sealableBlock, ItemBlockEnclosed.class);
        registerBlock(GCBlocks.spaceStationBase, ItemBlockGC.class);
        registerBlock(GCBlocks.fakeBlock, null);
        registerBlock(GCBlocks.parachest, ItemBlockDesc.class);
        registerBlock(GCBlocks.solarPanel, ItemBlockSolar.class);
        registerBlock(GCBlocks.radioTelescope, ItemBlockGC.class);
        registerBlock(GCBlocks.machineBase, ItemBlockMachine.class);
        registerBlock(GCBlocks.machineBase2, ItemBlockMachine.class);
        registerBlock(GCBlocks.machineBase3, ItemBlockMachine.class);
        registerBlock(GCBlocks.machineTiered, ItemBlockMachine.class);
        registerBlock(GCBlocks.machineBase4, ItemBlockMachine.class);
        registerBlock(GCBlocks.panelLighting, ItemBlockPanel.class);
        registerBlock(GCBlocks.glowstoneTorch, ItemBlockDesc.class);
        registerBlock(GCBlocks.fallenMeteor, ItemBlockDesc.class);
        registerBlock(GCBlocks.blockMoon, ItemBlockMoon.class);
        registerBlock(GCBlocks.cheeseBlock, ItemBlockCheese.class);
        registerBlock(GCBlocks.spinThruster, ItemBlockThruster.class);
        registerBlock(GCBlocks.screen, ItemBlockDesc.class);
        registerBlock(GCBlocks.telemetry, ItemBlockDesc.class);
        registerBlock(GCBlocks.brightLamp, ItemBlockArclamp.class);
        registerBlock(GCBlocks.treasureChestTier1, ItemBlockDesc.class);
        registerBlock(GCBlocks.fluidTank, ItemBlockDesc.class);
        registerBlock(GCBlocks.bossSpawner, ItemBlockGC.class);
        registerBlock(GCBlocks.tinStairs1, ItemBlockGC.class);
        registerBlock(GCBlocks.tinStairs2, ItemBlockGC.class);
        registerBlock(GCBlocks.moonStoneStairs, ItemBlockGC.class);
        registerBlock(GCBlocks.moonBricksStairs, ItemBlockGC.class);
        registerBlock(GCBlocks.wallGC, ItemBlockWallGC.class);
        registerBlock(GCBlocks.slabGCHalf, ItemBlockSlabGC.class, GCBlocks.slabGCHalf, GCBlocks.slabGCDouble);
        registerBlock(GCBlocks.slabGCDouble, ItemBlockSlabGC.class, GCBlocks.slabGCHalf, GCBlocks.slabGCDouble);
        registerBlock(GCBlocks.concealedRedstone, ItemBlockGC.class);
        registerBlock(GCBlocks.concealedRepeater_Powered, ItemBlockGC.class);
        registerBlock(GCBlocks.concealedRepeater_Unpowered, ItemBlockGC.class);
        registerBlock(GCBlocks.concealedDetector, ItemBlockCreativeGC.class);
        registerBlock(GCBlocks.platform, ItemBlockDesc.class);
        registerBlock(GCBlocks.emergencyBox, ItemBlockEmergencyBox.class);
        registerBlock(GCBlocks.grating, ItemBlockGC.class);
        registerBlock(GCBlocks.gratingWater, null);
        registerBlock(GCBlocks.gratingLava, null);
    }
}
