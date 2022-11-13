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

package dev.galacticraft.mod.content;

import dev.galacticraft.machinelib.api.block.MachineBlock;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.decoration.GratingBlock;
import dev.galacticraft.mod.content.block.decoration.LightPanelBlock;
import dev.galacticraft.mod.content.block.decoration.LunarCartographyTableBlock;
import dev.galacticraft.mod.content.block.decoration.VacuumGlassBlock;
import dev.galacticraft.mod.content.block.entity.*;
import dev.galacticraft.mod.content.block.environment.*;
import dev.galacticraft.mod.content.block.machine.*;
import dev.galacticraft.mod.content.block.special.*;
import dev.galacticraft.mod.content.block.special.aluminumwire.tier1.AluminumWireBlock;
import dev.galacticraft.mod.content.block.special.aluminumwire.tier1.SealableAluminumWireBlock;
import dev.galacticraft.mod.content.block.special.aluminumwire.tier2.HeavySealableAluminumWireBlock;
import dev.galacticraft.mod.content.block.special.fluidpipe.GlassFluidPipeBlock;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import dev.galacticraft.mod.content.block.special.walkway.PipeWalkway;
import dev.galacticraft.mod.content.block.special.walkway.WalkwayBlock;
import dev.galacticraft.mod.content.block.special.walkway.WireWalkway;
import dev.galacticraft.mod.util.MultiBlockUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import static dev.galacticraft.mod.content.block.machine.SimpleMachineBlock.MACHINE_DEFAULT_SETTINGS;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@SuppressWarnings("unused")
public class GCBlocks {
    // TORCHES
    public static final Block GLOWSTONE_TORCH = new GlowstoneTorchBlock(FabricBlockSettings.copy(Blocks.TORCH).sound(SoundType.WOOD));
    public static final Block GLOWSTONE_WALL_TORCH = new GlowstoneWallTorchBlock(FabricBlockSettings.copy(GLOWSTONE_TORCH).dropsLike(GLOWSTONE_TORCH));
    public static final Block UNLIT_TORCH = new UnlitTorchBlock(FabricBlockSettings.copy(Blocks.TORCH).lightLevel(state -> 0));
    public static final Block UNLIT_WALL_TORCH = new UnlitWallTorchBlock(FabricBlockSettings.copy(UNLIT_TORCH).dropsLike(UNLIT_TORCH));

    // LANTERNS
    public static final Block GLOWSTONE_LANTERN = new GlowstoneLanternBlock(FabricBlockSettings.copy(Blocks.LANTERN));
    public static final Block UNLIT_LANTERN = new UnlitLanternBlock(FabricBlockSettings.copy(Blocks.LANTERN).lightLevel(state -> 0));

    // FLUIDS
    public static final LiquidBlock CRUDE_OIL = new CrudeOilBlock(GCFluids.CRUDE_OIL, FabricBlockSettings.of(new FabricMaterialBuilder(MaterialColor.COLOR_BLACK)
            .allowsMovement().destroyedByPiston().burnable().lightPassesThrough().nonSolid().replaceable().liquid().build())
            .strength(100.0F, 1000.0F).noLootTable());

    public static final LiquidBlock FUEL = new LiquidBlock(GCFluids.FUEL, FabricBlockSettings.of(new FabricMaterialBuilder(MaterialColor.COLOR_YELLOW)
            .allowsMovement().destroyedByPiston().burnable().lightPassesThrough().nonSolid().replaceable().liquid().build())
            .strength(50.0F, 50.0F).noLootTable());

    // DECORATION BLOCKS
    public static final Block ALUMINUM_DECORATION = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 3.0F));
    public static final Block ALUMINUM_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(ALUMINUM_DECORATION).strength(2.5F, 3.0F));
    public static final Block ALUMINUM_DECORATION_STAIRS = new StairBlock(ALUMINUM_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(ALUMINUM_DECORATION));
    public static final Block ALUMINUM_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(ALUMINUM_DECORATION));
    public static final Block DETAILED_ALUMINUM_DECORATION = new Block(FabricBlockSettings.copyOf(ALUMINUM_DECORATION));
    public static final Block DETAILED_ALUMINUM_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(DETAILED_ALUMINUM_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_ALUMINUM_DECORATION_STAIRS = new StairBlock(DETAILED_ALUMINUM_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(DETAILED_ALUMINUM_DECORATION));
    public static final Block DETAILED_ALUMINUM_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(DETAILED_ALUMINUM_DECORATION));

    public static final Block BRONZE_DECORATION = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 3.0F));
    public static final Block BRONZE_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(BRONZE_DECORATION).strength(2.5F, 3.0F));
    public static final Block BRONZE_DECORATION_STAIRS = new StairBlock(BRONZE_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(BRONZE_DECORATION));
    public static final Block BRONZE_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(BRONZE_DECORATION));
    public static final Block DETAILED_BRONZE_DECORATION = new Block(FabricBlockSettings.copyOf(BRONZE_DECORATION));
    public static final Block DETAILED_BRONZE_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(DETAILED_BRONZE_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_BRONZE_DECORATION_STAIRS = new StairBlock(DETAILED_BRONZE_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(DETAILED_BRONZE_DECORATION));
    public static final Block DETAILED_BRONZE_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(DETAILED_BRONZE_DECORATION));

    public static final Block COPPER_DECORATION = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 3.0F));
    public static final Block COPPER_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(COPPER_DECORATION).strength(2.5F, 3.0F));
    public static final Block COPPER_DECORATION_STAIRS = new StairBlock(COPPER_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(COPPER_DECORATION));
    public static final Block COPPER_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(COPPER_DECORATION));
    public static final Block DETAILED_COPPER_DECORATION = new Block(FabricBlockSettings.copyOf(COPPER_DECORATION));
    public static final Block DETAILED_COPPER_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(DETAILED_COPPER_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_COPPER_DECORATION_STAIRS = new StairBlock(DETAILED_COPPER_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(DETAILED_COPPER_DECORATION));
    public static final Block DETAILED_COPPER_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(DETAILED_COPPER_DECORATION));

    public static final Block IRON_DECORATION = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 3.0F));
    public static final Block IRON_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(IRON_DECORATION).strength(2.5F, 3.0F));
    public static final Block IRON_DECORATION_STAIRS = new StairBlock(IRON_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(IRON_DECORATION));
    public static final Block IRON_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(IRON_DECORATION));
    public static final Block DETAILED_IRON_DECORATION = new Block(FabricBlockSettings.copyOf(IRON_DECORATION));
    public static final Block DETAILED_IRON_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(DETAILED_IRON_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_IRON_DECORATION_STAIRS = new StairBlock(DETAILED_IRON_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(DETAILED_IRON_DECORATION));
    public static final Block DETAILED_IRON_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(DETAILED_IRON_DECORATION));

    public static final Block METEORIC_IRON_DECORATION = new Block(FabricBlockSettings.copyOf(IRON_DECORATION));
    public static final Block METEORIC_IRON_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(METEORIC_IRON_DECORATION).strength(2.5F, 3.0F));
    public static final Block METEORIC_IRON_DECORATION_STAIRS = new StairBlock(METEORIC_IRON_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(METEORIC_IRON_DECORATION));
    public static final Block METEORIC_IRON_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(METEORIC_IRON_DECORATION));
    public static final Block DETAILED_METEORIC_IRON_DECORATION = new Block(FabricBlockSettings.copyOf(METEORIC_IRON_DECORATION));
    public static final Block DETAILED_METEORIC_IRON_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(DETAILED_METEORIC_IRON_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_METEORIC_IRON_DECORATION_STAIRS = new StairBlock(DETAILED_METEORIC_IRON_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(DETAILED_METEORIC_IRON_DECORATION));
    public static final Block DETAILED_METEORIC_IRON_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(DETAILED_METEORIC_IRON_DECORATION));

    public static final Block STEEL_DECORATION = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 3.0F));
    public static final Block STEEL_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(STEEL_DECORATION).strength(2.5F, 3.0F));
    public static final Block STEEL_DECORATION_STAIRS = new StairBlock(STEEL_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(STEEL_DECORATION));
    public static final Block STEEL_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(STEEL_DECORATION));
    public static final Block DETAILED_STEEL_DECORATION = new Block(FabricBlockSettings.copyOf(STEEL_DECORATION));
    public static final Block DETAILED_STEEL_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(DETAILED_STEEL_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_STEEL_DECORATION_STAIRS = new StairBlock(DETAILED_STEEL_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(DETAILED_STEEL_DECORATION));
    public static final Block DETAILED_STEEL_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(DETAILED_STEEL_DECORATION));

    public static final Block TIN_DECORATION = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 3.0F));
    public static final Block TIN_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(TIN_DECORATION).strength(2.5F, 3.0F));
    public static final Block TIN_DECORATION_STAIRS = new StairBlock(TIN_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(TIN_DECORATION));
    public static final Block TIN_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(TIN_DECORATION));
    public static final Block DETAILED_TIN_DECORATION = new Block(FabricBlockSettings.copyOf(TIN_DECORATION));
    public static final Block DETAILED_TIN_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(DETAILED_TIN_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_TIN_DECORATION_STAIRS = new StairBlock(DETAILED_TIN_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(DETAILED_TIN_DECORATION));
    public static final Block DETAILED_TIN_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(DETAILED_TIN_DECORATION));

    public static final Block TITANIUM_DECORATION = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 3.0F));
    public static final Block TITANIUM_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(TITANIUM_DECORATION).strength(2.5F, 3.0F));
    public static final Block TITANIUM_DECORATION_STAIRS = new StairBlock(TITANIUM_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(TITANIUM_DECORATION));
    public static final Block TITANIUM_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(TITANIUM_DECORATION));
    public static final Block DETAILED_TITANIUM_DECORATION = new Block(FabricBlockSettings.copyOf(TITANIUM_DECORATION));
    public static final Block DETAILED_TITANIUM_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(DETAILED_TITANIUM_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_TITANIUM_DECORATION_STAIRS = new StairBlock(DETAILED_TITANIUM_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(DETAILED_TITANIUM_DECORATION));
    public static final Block DETAILED_TITANIUM_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(DETAILED_TITANIUM_DECORATION));

    public static final Block DARK_DECORATION = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).strength(2.0F, 3.0F));
    public static final Block DARK_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(DARK_DECORATION).strength(2.5F, 3.0F));
    public static final Block DARK_DECORATION_STAIRS = new StairBlock(DARK_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(DARK_DECORATION));
    public static final Block DARK_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(DARK_DECORATION));
    public static final Block DETAILED_DARK_DECORATION = new Block(FabricBlockSettings.copyOf(DARK_DECORATION));
    public static final Block DETAILED_DARK_DECORATION_SLAB = new SlabBlock(FabricBlockSettings.copyOf(DETAILED_DARK_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_DARK_DECORATION_STAIRS = new StairBlock(DETAILED_DARK_DECORATION.defaultBlockState(), FabricBlockSettings.copyOf(DETAILED_DARK_DECORATION));
    public static final Block DETAILED_DARK_DECORATION_WALL = new WallBlock(FabricBlockSettings.copyOf(DETAILED_DARK_DECORATION));

    // MOON NATURAL
    public static final Block MOON_TURF = new Block(FabricBlockSettings.of(Material.GRASS, MaterialColor.COLOR_LIGHT_GRAY).strength(0.5F, 0.5F));
    public static final Block MOON_DIRT = new Block(FabricBlockSettings.of(Material.DIRT, MaterialColor.COLOR_LIGHT_GRAY).strength(0.5F, 0.5F).sound(SoundType.GRAVEL));
    public static final Block MOON_DIRT_PATH = new MoonDirtPathBlock(FabricBlockSettings.copyOf(MOON_DIRT).strength(0.5F, 0.5F));
    public static final Block MOON_SURFACE_ROCK = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.COLOR_GRAY).strength(1.5F, 6.0F));

    public static final Block MOON_ROCK = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.COLOR_GRAY).strength(2.0F, 6.0F));
    public static final Block MOON_ROCK_SLAB = new SlabBlock(FabricBlockSettings.copyOf(MOON_ROCK).strength(2.5F, 6.0F));
    public static final Block MOON_ROCK_STAIRS = new StairBlock(MOON_ROCK.defaultBlockState(), FabricBlockSettings.copyOf(MOON_ROCK));
    public static final Block MOON_ROCK_WALL = new WallBlock(FabricBlockSettings.copyOf(MOON_ROCK));

    public static final Block COBBLED_MOON_ROCK = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.COLOR_GRAY).strength(2.0F, 6.0F));
    public static final Block COBBLED_MOON_ROCK_SLAB = new SlabBlock(FabricBlockSettings.copyOf(COBBLED_MOON_ROCK).strength(2.5F, 6.0F));
    public static final Block COBBLED_MOON_ROCK_STAIRS = new StairBlock(COBBLED_MOON_ROCK.defaultBlockState(), FabricBlockSettings.copyOf(COBBLED_MOON_ROCK));
    public static final Block COBBLED_MOON_ROCK_WALL = new WallBlock(FabricBlockSettings.copyOf(COBBLED_MOON_ROCK));

    public static final Block LUNASLATE = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.DEEPSLATE).strength(3.5F, 6.0F));
    public static final Block LUNASLATE_SLAB = new SlabBlock(FabricBlockSettings.copyOf(LUNASLATE).strength(4.0F, 6.0F));
    public static final Block LUNASLATE_STAIRS = new StairBlock(LUNASLATE.defaultBlockState(), FabricBlockSettings.copyOf(LUNASLATE));
    public static final Block LUNASLATE_WALL = new WallBlock(FabricBlockSettings.copyOf(LUNASLATE));

    public static final Block COBBLED_LUNASLATE = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.DEEPSLATE).strength(3.5F, 6.0F));
    public static final Block COBBLED_LUNASLATE_SLAB = new SlabBlock(FabricBlockSettings.copyOf(COBBLED_LUNASLATE).strength(4.0F, 6.0F));
    public static final Block COBBLED_LUNASLATE_STAIRS = new StairBlock(COBBLED_LUNASLATE.defaultBlockState(), FabricBlockSettings.copyOf(COBBLED_LUNASLATE));
    public static final Block COBBLED_LUNASLATE_WALL = new WallBlock(FabricBlockSettings.copyOf(COBBLED_LUNASLATE));

    public static final Block MOON_BASALT = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.TERRACOTTA_BLACK).strength(2.0F, 6.0F));
    public static final Block MOON_BASALT_SLAB = new SlabBlock(FabricBlockSettings.copyOf(MOON_BASALT).strength(2.5F, 6.0F));
    public static final Block MOON_BASALT_STAIRS = new StairBlock(MOON_BASALT.defaultBlockState(), FabricBlockSettings.copyOf(MOON_BASALT));
    public static final Block MOON_BASALT_WALL = new WallBlock(FabricBlockSettings.copyOf(MOON_BASALT));

    public static final Block MOON_BASALT_BRICK = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.TERRACOTTA_BLACK).strength(2.5F, 6.0F));
    public static final Block MOON_BASALT_BRICK_SLAB = new SlabBlock(FabricBlockSettings.copyOf(MOON_BASALT_BRICK).strength(3.0F, 6.0F));
    public static final Block MOON_BASALT_BRICK_STAIRS = new StairBlock(MOON_BASALT_BRICK.defaultBlockState(), FabricBlockSettings.copyOf(MOON_BASALT_BRICK));
    public static final Block MOON_BASALT_BRICK_WALL = new WallBlock(FabricBlockSettings.copyOf(MOON_BASALT_BRICK));

    public static final Block CRACKED_MOON_BASALT_BRICK = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.TERRACOTTA_BLACK).strength(2.0F, 6.0F));
    public static final Block CRACKED_MOON_BASALT_BRICK_SLAB = new SlabBlock(FabricBlockSettings.copyOf(CRACKED_MOON_BASALT_BRICK).strength(2.5F, 6.0F));
    public static final Block CRACKED_MOON_BASALT_BRICK_STAIRS = new StairBlock(CRACKED_MOON_BASALT_BRICK.defaultBlockState(), FabricBlockSettings.copyOf(CRACKED_MOON_BASALT_BRICK));
    public static final Block CRACKED_MOON_BASALT_BRICK_WALL = new WallBlock(FabricBlockSettings.copyOf(CRACKED_MOON_BASALT_BRICK));

    public static final Block FALLEN_METEOR = new FallenMeteorBlock(FabricBlockSettings.of(Material.STONE).strength(2.0F, 6.0F).ticksRandomly().nonOpaque().sounds(SoundType.BASALT));

    // MARS NATURAL
    public static final Block MARS_SURFACE_ROCK = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.DIRT).destroyTime(2.2F));
    public static final Block MARS_SUB_SURFACE_ROCK = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.COLOR_RED).destroyTime(2.6F));
    public static final Block MARS_STONE = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.COLOR_RED).destroyTime(3.0F));
    public static final Block MARS_COBBLESTONE = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.COLOR_RED).destroyTime(3.0F));
    public static final Block MARS_COBBLESTONE_SLAB = new SlabBlock(FabricBlockSettings.copyOf(MARS_COBBLESTONE).strength(3.5F, 6.0F));
    public static final Block MARS_COBBLESTONE_STAIRS = new StairBlock(MARS_COBBLESTONE.defaultBlockState(), FabricBlockSettings.copyOf(MARS_COBBLESTONE));
    public static final Block MARS_COBBLESTONE_WALL = new WallBlock(FabricBlockSettings.copyOf(MARS_COBBLESTONE));

    // ASTEROID NATURAL
    public static final Block ASTEROID_ROCK = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.COLOR_BROWN).destroyTime(3.0F));
    public static final Block ASTEROID_ROCK_1 = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.COLOR_BROWN).destroyTime(3.0F));
    public static final Block ASTEROID_ROCK_2 = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.COLOR_BROWN).destroyTime(3.0F));

    // VENUS NATURAL
    public static final Block SOFT_VENUS_ROCK = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F));
    public static final Block HARD_VENUS_ROCK = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F));
    public static final Block SCORCHED_VENUS_ROCK = new ScorchedRockBlock(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F));
    public static final Block VOLCANIC_ROCK = new VolcanicRockBlock(FabricBlockSettings.of(Material.STONE).strength(2.2F, 0.5F));
    public static final Block PUMICE = new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F));
    public static final Block VAPOR_SPOUT = new VaporSpoutBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.COLOR_BROWN).noLootTable().strength(1.5F, 2.0F));

    // MISC DECOR
    public static final Block WALKWAY = new WalkwayBlock(FabricBlockSettings.of(Material.METAL).strength(5.0f, 5.0f).sound(SoundType.METAL));
    public static final Block PIPE_WALKWAY = new PipeWalkway(FabricBlockSettings.copyOf(WALKWAY));
    public static final Block WIRE_WALKWAY = new WireWalkway(FabricBlockSettings.copyOf(WALKWAY));
    public static final Block TIN_LADDER = new TinLadderBlock(FabricBlockSettings.of(Material.DECORATION).strength(1.0f, 1.0f).sound(SoundType.METAL));
    public static final Block GRATING = new GratingBlock(FabricBlockSettings.of(Material.METAL, MaterialColor.STONE).strength(2.5f, 6.0f).sound(SoundType.METAL));

    // SPECIAL
    public static final Block ALUMINUM_WIRE = new AluminumWireBlock(FabricBlockSettings.copy(Blocks.WHITE_WOOL));
    public static final Block SEALABLE_ALUMINUM_WIRE = new SealableAluminumWireBlock(FabricBlockSettings.copy(TIN_DECORATION));
    public static final Block HEAVY_SEALABLE_ALUMINUM_WIRE = new HeavySealableAluminumWireBlock(FabricBlockSettings.copy(TIN_DECORATION));
    public static final Block GLASS_FLUID_PIPE = new GlassFluidPipeBlock(FabricBlockSettings.of(Material.GLASS).sound(SoundType.GLASS));
    public static final Block ROCKET_LAUNCH_PAD = new RocketLaunchPadBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.COLOR_GRAY).strength(1.5F, 6.0F));
    public static final Block NASA_WORKBENCH = new NasaWorkbench(FabricBlockSettings.of(Material.STONE, MaterialColor.COLOR_GRAY).strength(1.5F, 6.0F));

    // LIGHT PANELS
    public static final Block SQUARE_LIGHT_PANEL = new LightPanelBlock(FabricBlockSettings.of(Material.METAL));
    public static final Block SPOTLIGHT_LIGHT_PANEL = new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 3.0f);
    public static final Block LINEAR_LIGHT_PANEL = new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 5.0f);
    public static final Block DASHED_LIGHT_PANEL = new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 1.0f);
    public static final Block DIAGONAL_LIGHT_PANEL = new LightPanelBlock(FabricBlockSettings.of(Material.METAL), 1.0f);

    // VACUUM GLASS
    public static final Block VACUUM_GLASS = new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sound(SoundType.GLASS));
    public static final Block CLEAR_VACUUM_GLASS = new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sound(SoundType.GLASS));
    public static final Block STRONG_VACUUM_GLASS = new VacuumGlassBlock(FabricBlockSettings.of(Material.GLASS).sound(SoundType.GLASS));

    // ORES
    public static final Block SILICON_ORE = new DropExperienceBlock(oreSettings(3.0F, 3.0F, false));
    public static final Block DEEPSLATE_SILICON_ORE = new DropExperienceBlock(oreSettings(4.5F, 3.0F, true));

    public static final Block MOON_COPPER_ORE = new DropExperienceBlock(oreSettings(3.0F, 5.0F, false));
    public static final Block LUNASLATE_COPPER_ORE = new DropExperienceBlock(oreSettings(5.0F, 5.0F, true));

    public static final Block TIN_ORE = new DropExperienceBlock(oreSettings(3.0F, 3.0F, false));
    public static final Block DEEPSLATE_TIN_ORE = new DropExperienceBlock(oreSettings(4.5F, 3.0F, true));
    public static final Block MOON_TIN_ORE = new DropExperienceBlock(oreSettings(3.0F, 5.0F, false));
    public static final Block LUNASLATE_TIN_ORE = new DropExperienceBlock(oreSettings(5.0F, 5.0F, true));

    public static final Block ALUMINUM_ORE = new DropExperienceBlock(oreSettings(3.0F, 3.0F, false));
    public static final Block DEEPSLATE_ALUMINUM_ORE = new DropExperienceBlock(oreSettings(3.5F, 3.0F, true));

    public static final Block DESH_ORE = new DropExperienceBlock(oreSettings(3.0F, 5.0F, false));

    public static final Block ILMENITE_ORE = new DropExperienceBlock(oreSettings(3.0F, 5.0F, false));

    public static final Block GALENA_ORE = new DropExperienceBlock(oreSettings(3.0F, 5.0F, false));

    // COMPACT MINERAL BLOCKS
    public static final Block MOON_CHEESE_BLOCK = new CakeBlock(FabricBlockSettings.of(Material.CAKE).strength(0.5F).sound(SoundType.WOOL));
    public static final Block SILICON_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL));
    public static final Block METEORIC_IRON_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL));
    public static final Block DESH_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL));
    public static final Block TITANIUM_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL));
    public static final Block LEAD_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL));
    public static final Block LUNAR_SAPPHIRE_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).strength(5.0F, 6.0F).sound(SoundType.STONE));

    // MOON VILLAGER SPECIAL
    public static final Block LUNAR_CARTOGRAPHY_TABLE = new LunarCartographyTableBlock(FabricBlockSettings.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD));

    // MISC WORLD GEN
    public static final Block CAVERNOUS_VINE = new CavernousVineBlock(FabricBlockSettings.of(Material.CACTUS, MaterialColor.COLOR_GREEN).noLootTable().noCollission().lightLevel(blockstate -> 0).sound(SoundType.GRASS).randomTicks());
    public static final Block POISONOUS_CAVERNOUS_VINE = new PoisonousCavernousVineBlock(FabricBlockSettings.of(Material.CACTUS, MaterialColor.COLOR_GREEN).noLootTable().noCollission().lightLevel(blockstate -> 3).sound(SoundType.GRASS).randomTicks());
    public static final Block MOON_BERRY_BUSH = new MoonBerryBushBlock(FabricBlockSettings.of(Material.PLANT, MaterialColor.COLOR_GREEN).noLootTable().noCollission().lightLevel(blockstate -> 3).sound(SoundType.SWEET_BERRY_BUSH).randomTicks());

    // DUMMY
    public static final BaseEntityBlock SOLAR_PANEL_PART = new SolarPanelPartBlock(FabricBlockSettings.of(Material.METAL).strength(-1.0F, 5.0F).noLootTable().sound(SoundType.METAL));
    public static final BaseEntityBlock CRYOGENIC_CHAMBER_PART = new CryogenicChamberPart(FabricBlockSettings.of(Material.METAL).strength(-1.0F, 5.0F).noLootTable().sound(SoundType.METAL));

    // MISC MACHINES
    public static final Block CRYOGENIC_CHAMBER = new CryogenicChamberBlock(FabricBlockSettings.copyOf(MACHINE_DEFAULT_SETTINGS));

    // MACHINES
    public static final MachineBlock<CircuitFabricatorBlockEntity> CIRCUIT_FABRICATOR = SimpleMachineBlock.create(CircuitFabricatorBlockEntity::new);
    public static final MachineBlock<CompressorBlockEntity> COMPRESSOR = SimpleMachineBlock.create(CompressorBlockEntity::new);
    public static final MachineBlock<ElectricCompressorBlockEntity> ELECTRIC_COMPRESSOR = SimpleMachineBlock.create(ElectricCompressorBlockEntity::new);
    public static final MachineBlock<CoalGeneratorBlockEntity> COAL_GENERATOR = new CoalGeneratorBlock(FabricBlockSettings.copyOf(MACHINE_DEFAULT_SETTINGS).lightLevel(state -> state.getValue(MachineBlock.ACTIVE) ? 13 : 0));
    public static final MachineBlock<BasicSolarPanelBlockEntity> BASIC_SOLAR_PANEL = SimpleMultiBlockMachineBlock.create(BasicSolarPanelBlockEntity::new, MultiBlockUtil.generateSolarPanelParts(), GCBlocks.SOLAR_PANEL_PART);
    public static final MachineBlock<AdvancedSolarPanelBlockEntity> ADVANCED_SOLAR_PANEL = SimpleMultiBlockMachineBlock.create(AdvancedSolarPanelBlockEntity::new, MultiBlockUtil.generateSolarPanelParts(), GCBlocks.SOLAR_PANEL_PART);
    public static final MachineBlock<EnergyStorageModuleBlockEntity> ENERGY_STORAGE_MODULE = SimpleMachineBlock.create(EnergyStorageModuleBlockEntity::new);
    public static final MachineBlock<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = SimpleMachineBlock.create(ElectricFurnaceBlockEntity::new);
    public static final MachineBlock<ElectricArcFurnaceBlockEntity> ELECTRIC_ARC_FURNACE = SimpleMachineBlock.create(ElectricArcFurnaceBlockEntity::new);
    public static final MachineBlock<RefineryBlockEntity> REFINERY = new RefineryBlock(FabricBlockSettings.copyOf(MACHINE_DEFAULT_SETTINGS));
    public static final MachineBlock<OxygenCollectorBlockEntity> OXYGEN_COLLECTOR = new OxygenCollectorBlock(FabricBlockSettings.copyOf(MACHINE_DEFAULT_SETTINGS));
    public static final MachineBlock<OxygenSealerBlockEntity> OXYGEN_SEALER = SimpleMachineBlock.create(OxygenSealerBlockEntity::new);
    public static final MachineBlock<OxygenBubbleDistributorBlockEntity> BUBBLE_DISTRIBUTOR = SimpleMachineBlock.create(OxygenBubbleDistributorBlockEntity::new);
    public static final MachineBlock<OxygenDecompressorBlockEntity> OXYGEN_DECOMPRESSOR = SimpleMachineBlock.create(OxygenDecompressorBlockEntity::new);
    public static final MachineBlock<OxygenCompressorBlockEntity> OXYGEN_COMPRESSOR = SimpleMachineBlock.create(OxygenCompressorBlockEntity::new);
    public static final MachineBlock<OxygenStorageModuleBlockEntity> OXYGEN_STORAGE_MODULE = SimpleMachineBlock.create(OxygenStorageModuleBlockEntity::new);
    public static final MachineBlock<FuelLoaderBlockEntity> FUEL_LOADER = new FuelLoaderBlock(FabricBlockSettings.copyOf(MACHINE_DEFAULT_SETTINGS));

    public static final AirlockBlock AIR_LOCK_FRAME = new AirlockBlock(false, FabricBlockSettings.copyOf(Blocks.IRON_BLOCK));
    public static final AirlockBlock AIR_LOCK_CONTROLLER = new AirlockBlock(true, FabricBlockSettings.copyOf(Blocks.IRON_BLOCK));
    public static final Block AIR_LOCK_SEAL = new Block(FabricBlockSettings.copyOf(AIR_LOCK_FRAME));

    public static void register() {
        FlammableBlockRegistry.getDefaultInstance().add(FUEL, 80, 130);
        FlammableBlockRegistry.getDefaultInstance().add(CRUDE_OIL, 60, 100);
        FlattenableBlockRegistry.register(MOON_DIRT, MOON_DIRT_PATH.defaultBlockState());

        // TORCHES
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.GLOWSTONE_TORCH), GLOWSTONE_TORCH);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.GLOWSTONE_WALL_TORCH), GLOWSTONE_WALL_TORCH);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.GLOWSTONE_LANTERN), GLOWSTONE_LANTERN);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.UNLIT_TORCH), UNLIT_TORCH);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.UNLIT_WALL_TORCH), UNLIT_WALL_TORCH);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.UNLIT_LANTERN), UNLIT_LANTERN);

        // FLUIDS
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.CRUDE_OIL), CRUDE_OIL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.FUEL), FUEL);

        // DECORATION BLOCKS
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ALUMINUM_DECORATION), ALUMINUM_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ALUMINUM_DECORATION_SLAB), ALUMINUM_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ALUMINUM_DECORATION_STAIRS), ALUMINUM_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ALUMINUM_DECORATION_WALL), ALUMINUM_DECORATION_WALL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION), DETAILED_ALUMINUM_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION_SLAB), DETAILED_ALUMINUM_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION_STAIRS), DETAILED_ALUMINUM_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION_WALL), DETAILED_ALUMINUM_DECORATION_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.BRONZE_DECORATION), BRONZE_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.BRONZE_DECORATION_SLAB), BRONZE_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.BRONZE_DECORATION_STAIRS), BRONZE_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.BRONZE_DECORATION_WALL), BRONZE_DECORATION_WALL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION), DETAILED_BRONZE_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION_SLAB), DETAILED_BRONZE_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION_STAIRS), DETAILED_BRONZE_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION_WALL), DETAILED_BRONZE_DECORATION_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.COPPER_DECORATION), COPPER_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.COPPER_DECORATION_SLAB), COPPER_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.COPPER_DECORATION_STAIRS), COPPER_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.COPPER_DECORATION_WALL), COPPER_DECORATION_WALL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION), DETAILED_COPPER_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION_SLAB), DETAILED_COPPER_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION_STAIRS), DETAILED_COPPER_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION_WALL), DETAILED_COPPER_DECORATION_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.IRON_DECORATION), IRON_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.IRON_DECORATION_SLAB), IRON_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.IRON_DECORATION_STAIRS), IRON_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.IRON_DECORATION_WALL), IRON_DECORATION_WALL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_IRON_DECORATION), DETAILED_IRON_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_IRON_DECORATION_SLAB), DETAILED_IRON_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_IRON_DECORATION_STAIRS), DETAILED_IRON_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_IRON_DECORATION_WALL), DETAILED_IRON_DECORATION_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.METEORIC_IRON_DECORATION), METEORIC_IRON_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.METEORIC_IRON_DECORATION_SLAB), METEORIC_IRON_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.METEORIC_IRON_DECORATION_STAIRS), METEORIC_IRON_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.METEORIC_IRON_DECORATION_WALL), METEORIC_IRON_DECORATION_WALL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION), DETAILED_METEORIC_IRON_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION_SLAB), DETAILED_METEORIC_IRON_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION_STAIRS), DETAILED_METEORIC_IRON_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION_WALL), DETAILED_METEORIC_IRON_DECORATION_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.STEEL_DECORATION), STEEL_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.STEEL_DECORATION_SLAB), STEEL_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.STEEL_DECORATION_STAIRS), STEEL_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.STEEL_DECORATION_WALL), STEEL_DECORATION_WALL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION), DETAILED_STEEL_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION_SLAB), DETAILED_STEEL_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION_STAIRS), DETAILED_STEEL_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION_WALL), DETAILED_STEEL_DECORATION_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.TIN_DECORATION), TIN_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.TIN_DECORATION_SLAB), TIN_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.TIN_DECORATION_STAIRS), TIN_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.TIN_DECORATION_WALL), TIN_DECORATION_WALL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_TIN_DECORATION), DETAILED_TIN_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_TIN_DECORATION_SLAB), DETAILED_TIN_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_TIN_DECORATION_STAIRS), DETAILED_TIN_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_TIN_DECORATION_WALL), DETAILED_TIN_DECORATION_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.TITANIUM_DECORATION), TITANIUM_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.TITANIUM_DECORATION_SLAB), TITANIUM_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.TITANIUM_DECORATION_STAIRS), TITANIUM_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.TITANIUM_DECORATION_WALL), TITANIUM_DECORATION_WALL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION), DETAILED_TITANIUM_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION_SLAB), DETAILED_TITANIUM_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION_STAIRS), DETAILED_TITANIUM_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION_WALL), DETAILED_TITANIUM_DECORATION_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DARK_DECORATION), DARK_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DARK_DECORATION_SLAB), DARK_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DARK_DECORATION_STAIRS), DARK_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DARK_DECORATION_WALL), DARK_DECORATION_WALL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_DARK_DECORATION), DETAILED_DARK_DECORATION);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_DARK_DECORATION_SLAB), DETAILED_DARK_DECORATION_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_DARK_DECORATION_STAIRS), DETAILED_DARK_DECORATION_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DETAILED_DARK_DECORATION_WALL), DETAILED_DARK_DECORATION_WALL);

        // MOON NATURAL
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_TURF), MOON_TURF);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_DIRT), MOON_DIRT);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_DIRT_PATH), MOON_DIRT_PATH);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_SURFACE_ROCK), MOON_SURFACE_ROCK);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_ROCK), MOON_ROCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_ROCK_SLAB), MOON_ROCK_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_ROCK_STAIRS), MOON_ROCK_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_ROCK_WALL), MOON_ROCK_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.COBBLED_MOON_ROCK), COBBLED_MOON_ROCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.COBBLED_MOON_ROCK_SLAB), COBBLED_MOON_ROCK_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.COBBLED_MOON_ROCK_STAIRS), COBBLED_MOON_ROCK_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.COBBLED_MOON_ROCK_WALL), COBBLED_MOON_ROCK_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.LUNASLATE), LUNASLATE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.LUNASLATE_SLAB), LUNASLATE_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.LUNASLATE_STAIRS), LUNASLATE_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.LUNASLATE_WALL), LUNASLATE_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.COBBLED_LUNASLATE), COBBLED_LUNASLATE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.COBBLED_LUNASLATE_SLAB), COBBLED_LUNASLATE_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.COBBLED_LUNASLATE_STAIRS), COBBLED_LUNASLATE_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.COBBLED_LUNASLATE_WALL), COBBLED_LUNASLATE_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_BASALT), MOON_BASALT);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_BASALT_SLAB), MOON_BASALT_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_BASALT_STAIRS), MOON_BASALT_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_BASALT_WALL), MOON_BASALT_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_BASALT_BRICK), MOON_BASALT_BRICK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_BASALT_BRICK_SLAB), MOON_BASALT_BRICK_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_BASALT_BRICK_STAIRS), MOON_BASALT_BRICK_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_BASALT_BRICK_WALL), MOON_BASALT_BRICK_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK), CRACKED_MOON_BASALT_BRICK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK_SLAB), CRACKED_MOON_BASALT_BRICK_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK_STAIRS), CRACKED_MOON_BASALT_BRICK_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK_WALL), CRACKED_MOON_BASALT_BRICK_WALL);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.FALLEN_METEOR), FALLEN_METEOR);

        // MARS NATURAL
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MARS_SURFACE_ROCK), MARS_SURFACE_ROCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MARS_SUB_SURFACE_ROCK), MARS_SUB_SURFACE_ROCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MARS_STONE), MARS_STONE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MARS_COBBLESTONE), MARS_COBBLESTONE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MARS_COBBLESTONE_SLAB), MARS_COBBLESTONE_SLAB);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MARS_COBBLESTONE_STAIRS), MARS_COBBLESTONE_STAIRS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MARS_COBBLESTONE_WALL), MARS_COBBLESTONE_WALL);

        // ASTEROID NATURAL
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ASTEROID_ROCK), ASTEROID_ROCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ASTEROID_ROCK_1), ASTEROID_ROCK_1);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ASTEROID_ROCK_2), ASTEROID_ROCK_2);

        // VENUS NATURAL
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.SOFT_VENUS_ROCK), SOFT_VENUS_ROCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.HARD_VENUS_ROCK), HARD_VENUS_ROCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.SCORCHED_VENUS_ROCK), SCORCHED_VENUS_ROCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.VOLCANIC_ROCK), VOLCANIC_ROCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.PUMICE), PUMICE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.VAPOR_SPOUT), VAPOR_SPOUT);

        // MISC DECOR
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.WALKWAY), WALKWAY);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.PIPE_WALKWAY), PIPE_WALKWAY);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.WIRE_WALKWAY), WIRE_WALKWAY);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.TIN_LADDER), TIN_LADDER);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.GRATING), GRATING);

        // SPECIAL
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ALUMINUM_WIRE), ALUMINUM_WIRE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.SEALABLE_ALUMINUM_WIRE), SEALABLE_ALUMINUM_WIRE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.HEAVY_SEALABLE_ALUMINUM_WIRE), HEAVY_SEALABLE_ALUMINUM_WIRE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.GLASS_FLUID_PIPE), GLASS_FLUID_PIPE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ROCKET_LAUNCH_PAD), ROCKET_LAUNCH_PAD);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.NASA_WORKBENCH), NASA_WORKBENCH);

        // LIGHT PANELS
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.SQUARE_LIGHT_PANEL), SQUARE_LIGHT_PANEL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.SPOTLIGHT_LIGHT_PANEL), SPOTLIGHT_LIGHT_PANEL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.LINEAR_LIGHT_PANEL), LINEAR_LIGHT_PANEL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DASHED_LIGHT_PANEL), DASHED_LIGHT_PANEL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DIAGONAL_LIGHT_PANEL), DIAGONAL_LIGHT_PANEL);

        // VACUUM GLASS
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.VACUUM_GLASS), VACUUM_GLASS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.CLEAR_VACUUM_GLASS), CLEAR_VACUUM_GLASS);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.STRONG_VACUUM_GLASS), STRONG_VACUUM_GLASS);

        // ORES
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.SILICON_ORE), SILICON_ORE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DEEPSLATE_SILICON_ORE), DEEPSLATE_SILICON_ORE);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_COPPER_ORE), MOON_COPPER_ORE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.LUNASLATE_COPPER_ORE), LUNASLATE_COPPER_ORE);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.TIN_ORE), TIN_ORE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DEEPSLATE_TIN_ORE), DEEPSLATE_TIN_ORE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_TIN_ORE), MOON_TIN_ORE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.LUNASLATE_TIN_ORE), LUNASLATE_TIN_ORE);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ALUMINUM_ORE), ALUMINUM_ORE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DEEPSLATE_ALUMINUM_ORE), DEEPSLATE_ALUMINUM_ORE);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DESH_ORE), DESH_ORE);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ILMENITE_ORE), ILMENITE_ORE);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.GALENA_ORE), GALENA_ORE);

        // COMPACT MINERAL BLOCKS
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_CHEESE_BLOCK), MOON_CHEESE_BLOCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.SILICON_BLOCK), SILICON_BLOCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.METEORIC_IRON_BLOCK), METEORIC_IRON_BLOCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.DESH_BLOCK), DESH_BLOCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.TITANIUM_BLOCK), TITANIUM_BLOCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.LEAD_BLOCK), LEAD_BLOCK);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.LUNAR_SAPPHIRE_BLOCK), LUNAR_SAPPHIRE_BLOCK);

        // MOON VILLAGER SPECIAL
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.LUNAR_CARTOGRAPHY_TABLE), LUNAR_CARTOGRAPHY_TABLE);

        // MISC WORLD GEN
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.CAVERNOUS_VINE), CAVERNOUS_VINE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.POISONOUS_CAVERNOUS_VINE), POISONOUS_CAVERNOUS_VINE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.MOON_BERRY_BUSH), MOON_BERRY_BUSH);

        // DUMMY
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.SOLAR_PANEL_PART), SOLAR_PANEL_PART);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.CRYOGENIC_CHAMBER_PART), CRYOGENIC_CHAMBER_PART);

        // MISC MACHINES
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.CRYOGENIC_CHAMBER), CRYOGENIC_CHAMBER);

        // MACHINES
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.CIRCUIT_FABRICATOR), CIRCUIT_FABRICATOR);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.COMPRESSOR), COMPRESSOR);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ELECTRIC_COMPRESSOR), ELECTRIC_COMPRESSOR);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.COAL_GENERATOR), COAL_GENERATOR);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.BASIC_SOLAR_PANEL), BASIC_SOLAR_PANEL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ADVANCED_SOLAR_PANEL), ADVANCED_SOLAR_PANEL);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ENERGY_STORAGE_MODULE), ENERGY_STORAGE_MODULE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ELECTRIC_FURNACE), ELECTRIC_FURNACE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.ELECTRIC_ARC_FURNACE), ELECTRIC_ARC_FURNACE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.REFINERY), REFINERY);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.OXYGEN_COLLECTOR), OXYGEN_COLLECTOR);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.OXYGEN_SEALER), OXYGEN_SEALER);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.OXYGEN_BUBBLE_DISTRIBUTOR), BUBBLE_DISTRIBUTOR);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.OXYGEN_DECOMPRESSOR), OXYGEN_DECOMPRESSOR);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.OXYGEN_COMPRESSOR), OXYGEN_COMPRESSOR);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.OXYGEN_STORAGE_MODULE), OXYGEN_STORAGE_MODULE);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.FUEL_LOADER), FUEL_LOADER);

        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.AIR_LOCK_FRAME), AIR_LOCK_FRAME);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.AIR_LOCK_CONTROLLER), AIR_LOCK_CONTROLLER);
        Registry.register(Registry.BLOCK, Constant.id(Constant.Block.AIR_LOCK_SEAL), AIR_LOCK_SEAL);
    }

    private static FabricBlockSettings oreSettings(float hardness, float resistance, boolean deepslate) {
        if (deepslate) return FabricBlockSettings.of(Material.STONE, MaterialColor.DEEPSLATE).strength(hardness, resistance).requiresTool().sounds(SoundType.DEEPSLATE);
        return FabricBlockSettings.of(Material.STONE).strength(hardness, resistance).requiresTool();
    }
}