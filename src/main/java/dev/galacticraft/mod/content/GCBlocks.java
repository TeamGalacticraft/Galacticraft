/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import dev.galacticraft.machinelib.api.block.SimpleMachineBlock;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlockRegistry.DecorationSet;
import dev.galacticraft.mod.content.block.boss.BossSpawner;
import dev.galacticraft.mod.content.block.decoration.IronGratingBlock;
import dev.galacticraft.mod.content.block.decoration.LightPanelBlock;
import dev.galacticraft.mod.content.block.decoration.LunarCartographyTableBlock;
import dev.galacticraft.mod.content.block.decoration.VacuumGlassBlock;
import dev.galacticraft.mod.content.block.environment.*;
import dev.galacticraft.mod.content.block.machine.*;
import dev.galacticraft.mod.content.block.special.*;
import dev.galacticraft.mod.content.block.special.aluminumwire.tier1.AluminumWireBlock;
import dev.galacticraft.mod.content.block.special.aluminumwire.tier1.SealableAluminumWireBlock;
import dev.galacticraft.mod.content.block.special.aluminumwire.tier2.HeavySealableAluminumWireBlock;
import dev.galacticraft.mod.content.block.special.fluidpipe.GlassFluidPipeBlock;
import dev.galacticraft.mod.content.block.special.launchpad.FuelPadBlock;
import dev.galacticraft.mod.content.block.special.launchpad.LaunchPadBlock;
import dev.galacticraft.mod.content.block.special.walkway.FluidPipeWalkway;
import dev.galacticraft.mod.content.block.special.walkway.WalkwayBlock;
import dev.galacticraft.mod.content.block.special.walkway.WireWalkway;
import dev.galacticraft.mod.util.MultiBlockUtil;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.ToIntFunction;

@SuppressWarnings("unused")
public class GCBlocks {
    public static final GCBlockRegistry BLOCKS = new GCBlockRegistry();

    // TORCHES
    // These 2 torches are special, it's need to register early so others can use dropsLike() reference
    public static final Block GLOWSTONE_TORCH = BLOCKS.register(Constant.Block.GLOWSTONE_TORCH, new GlowstoneTorchBlock(BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel(blockStatex -> 14).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)));
    public static final Block UNLIT_TORCH = BLOCKS.register(Constant.Block.UNLIT_TORCH, new UnlitTorchBlock(BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel(blockStatex -> 0).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)));
    public static final Block GLOWSTONE_WALL_TORCH = BLOCKS.register(Constant.Block.GLOWSTONE_WALL_TORCH, new GlowstoneWallTorchBlock(BlockBehaviour.Properties.ofFullCopy(GLOWSTONE_TORCH).dropsLike(GLOWSTONE_TORCH)));
    public static final Block UNLIT_WALL_TORCH = BLOCKS.register(Constant.Block.UNLIT_WALL_TORCH, new UnlitWallTorchBlock(BlockBehaviour.Properties.ofFullCopy(UNLIT_TORCH).dropsLike(UNLIT_TORCH)));

    // LANTERNS
    public static final Block GLOWSTONE_LANTERN = BLOCKS.registerWithItem(Constant.Block.GLOWSTONE_LANTERN, new GlowstoneLanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN)));
    public static final Block UNLIT_LANTERN = BLOCKS.registerWithItem(Constant.Block.UNLIT_LANTERN, new UnlitLanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 0)));

    // FLUIDS
    public static final LiquidBlock CRUDE_OIL = BLOCKS.register(Constant.Block.CRUDE_OIL,
            new CrudeOilBlock(GCFluids.CRUDE_OIL, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK)
                    .noCollission().pushReaction(PushReaction.DESTROY).ignitedByLava().replaceable().liquid()
                    .strength(100.0F, 1000.0F).noLootTable()));

    public static final LiquidBlock FUEL = BLOCKS.register(Constant.Block.FUEL,
            new LiquidBlock(GCFluids.FUEL, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW)
                    .noCollission().pushReaction(PushReaction.DESTROY).ignitedByLava().replaceable().liquid()
                    .strength(50.0F, 50.0F).noLootTable()));

    public static final LiquidBlock SULFURIC_ACID = BLOCKS.register(Constant.Block.SULFURIC_ACID,
            new LiquidBlock(GCFluids.SULFURIC_ACID, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .noCollission().pushReaction(PushReaction.DESTROY).replaceable().liquid()
                    .strength(50.0F, 50.0F).noLootTable()));

    // DECORATION BLOCKS
    public static final DecorationSet ALUMINUM_DECORATION = BLOCKS.registerDecoration(Constant.Block.ALUMINUM_DECORATION, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F).requiresCorrectToolForDrops(), 2.5F, 3.0F);
    public static final DecorationSet BRONZE_DECORATION = BLOCKS.registerDecoration(Constant.Block.BRONZE_DECORATION, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F).requiresCorrectToolForDrops(), 2.5F, 3.0F);
    public static final DecorationSet COPPER_DECORATION = BLOCKS.registerDecoration(Constant.Block.COPPER_DECORATION, BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F).requiresCorrectToolForDrops(), 2.5F, 3.0F);
    public static final DecorationSet IRON_DECORATION = BLOCKS.registerDecoration(Constant.Block.IRON_DECORATION, BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F).requiresCorrectToolForDrops(), 2.5F, 3.0F);
    public static final DecorationSet METEORIC_IRON_DECORATION = BLOCKS.registerDecoration(Constant.Block.METEORIC_IRON_DECORATION, BlockBehaviour.Properties.ofFullCopy(IRON_DECORATION.block()).mapColor(MapColor.RAW_IRON), 2.5F, 3.0F);
    public static final DecorationSet STEEL_DECORATION = BLOCKS.registerDecoration(Constant.Block.STEEL_DECORATION, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F).requiresCorrectToolForDrops(), 2.5F, 3.0F);
    public static final DecorationSet TIN_DECORATION = BLOCKS.registerDecoration(Constant.Block.TIN_DECORATION, BlockBehaviour.Properties.of().mapColor(MapColor.GLOW_LICHEN).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F).requiresCorrectToolForDrops(), 2.5F, 3.0F);
    public static final DecorationSet TITANIUM_DECORATION = BLOCKS.registerDecoration(Constant.Block.TITANIUM_DECORATION, BlockBehaviour.Properties.of().mapColor(MapColor.CLAY).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F).requiresCorrectToolForDrops(), 2.5F, 3.0F);
    public static final DecorationSet DARK_DECORATION = BLOCKS.registerDecoration(Constant.Block.DARK_DECORATION, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F).requiresCorrectToolForDrops(), 2.5F, 3.0F);

    // MOON NATURAL
    public static final Block MOON_TURF = BLOCKS.registerWithItem(Constant.Block.MOON_TURF, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.5F, 0.5F)));
    public static final Block MOON_DIRT = BLOCKS.registerWithItem(Constant.Block.MOON_DIRT, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.5F, 0.5F).sound(SoundType.GRAVEL)));
    public static final Block MOON_DIRT_PATH = BLOCKS.registerWithItem(Constant.Block.MOON_DIRT_PATH, new MoonDirtPathBlock(BlockBehaviour.Properties.ofFullCopy(MOON_DIRT).strength(0.5F, 0.5F)));
    public static final Block MOON_SURFACE_ROCK = BLOCKS.registerWithItem(Constant.Block.MOON_SURFACE_ROCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
    public static final Block MOON_DUNGEON_BRICK = BLOCKS.registerWithItem(Constant.Block.MOON_DUNGEON_BRICK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 2.5F).requiresCorrectToolForDrops()));

    public static final Block MOON_ROCK = BLOCKS.registerWithItem(Constant.Block.MOON_ROCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
    public static final Block MOON_ROCK_SLAB = BLOCKS.registerWithItem(Constant.Block.MOON_ROCK_SLAB, new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MOON_ROCK).strength(2.5F, 6.0F)));
    public static final Block MOON_ROCK_STAIRS = BLOCKS.registerWithItem(Constant.Block.MOON_ROCK_STAIRS, new StairBlock(MOON_ROCK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MOON_ROCK)));
    public static final Block MOON_ROCK_WALL = BLOCKS.registerWithItem(Constant.Block.MOON_ROCK_WALL, new WallBlock(BlockBehaviour.Properties.ofFullCopy(MOON_ROCK)));

    public static final Block MOON_ROCK_BRICK = BLOCKS.registerWithItem(Constant.Block.MOON_ROCK_BRICK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
    public static final Block MOON_ROCK_BRICK_SLAB = BLOCKS.registerWithItem(Constant.Block.MOON_ROCK_BRICK_SLAB, new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MOON_ROCK_BRICK).strength(2.5F, 6.0F)));
    public static final Block MOON_ROCK_BRICK_STAIRS = BLOCKS.registerWithItem(Constant.Block.MOON_ROCK_BRICK_STAIRS, new StairBlock(MOON_ROCK_BRICK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MOON_ROCK_BRICK)));
    public static final Block MOON_ROCK_BRICK_WALL = BLOCKS.registerWithItem(Constant.Block.MOON_ROCK_BRICK_WALL, new WallBlock(BlockBehaviour.Properties.ofFullCopy(MOON_ROCK_BRICK)));

    public static final Block CRACKED_MOON_ROCK_BRICK = BLOCKS.registerWithItem(Constant.Block.CRACKED_MOON_ROCK_BRICK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
    public static final Block CRACKED_MOON_ROCK_BRICK_SLAB = BLOCKS.registerWithItem(Constant.Block.CRACKED_MOON_ROCK_BRICK_SLAB, new SlabBlock(BlockBehaviour.Properties.ofFullCopy(CRACKED_MOON_ROCK_BRICK).strength(2.5F, 6.0F)));
    public static final Block CRACKED_MOON_ROCK_BRICK_STAIRS = BLOCKS.registerWithItem(Constant.Block.CRACKED_MOON_ROCK_BRICK_STAIRS, new StairBlock(CRACKED_MOON_ROCK_BRICK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(CRACKED_MOON_ROCK_BRICK)));
    public static final Block CRACKED_MOON_ROCK_BRICK_WALL = BLOCKS.registerWithItem(Constant.Block.CRACKED_MOON_ROCK_BRICK_WALL, new WallBlock(BlockBehaviour.Properties.ofFullCopy(CRACKED_MOON_ROCK_BRICK)));

    public static final Block POLISHED_MOON_ROCK = BLOCKS.registerWithItem(Constant.Block.POLISHED_MOON_ROCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
    public static final Block POLISHED_MOON_ROCK_SLAB = BLOCKS.registerWithItem(Constant.Block.POLISHED_MOON_ROCK_SLAB, new SlabBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_MOON_ROCK).strength(2.5F, 6.0F)));
    public static final Block POLISHED_MOON_ROCK_STAIRS = BLOCKS.registerWithItem(Constant.Block.POLISHED_MOON_ROCK_STAIRS, new StairBlock(POLISHED_MOON_ROCK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(POLISHED_MOON_ROCK)));
    public static final Block POLISHED_MOON_ROCK_WALL = BLOCKS.registerWithItem(Constant.Block.POLISHED_MOON_ROCK_WALL, new WallBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_MOON_ROCK)));

    public static final Block CHISELED_MOON_ROCK_BRICK = BLOCKS.registerWithItem(Constant.Block.CHISELED_MOON_ROCK_BRICK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
    public static final Block MOON_ROCK_PILLAR = BLOCKS.registerWithItem(Constant.Block.MOON_ROCK_PILLAR, new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

    public static final Block COBBLED_MOON_ROCK = BLOCKS.registerWithItem(Constant.Block.COBBLED_MOON_ROCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
    public static final Block COBBLED_MOON_ROCK_SLAB = BLOCKS.registerWithItem(Constant.Block.COBBLED_MOON_ROCK_SLAB, new SlabBlock(BlockBehaviour.Properties.ofFullCopy(COBBLED_MOON_ROCK).strength(2.5F, 6.0F)));
    public static final Block COBBLED_MOON_ROCK_STAIRS = BLOCKS.registerWithItem(Constant.Block.COBBLED_MOON_ROCK_STAIRS, new StairBlock(COBBLED_MOON_ROCK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(COBBLED_MOON_ROCK)));
    public static final Block COBBLED_MOON_ROCK_WALL = BLOCKS.registerWithItem(Constant.Block.COBBLED_MOON_ROCK_WALL, new WallBlock(BlockBehaviour.Properties.ofFullCopy(COBBLED_MOON_ROCK)));

    public static final Block LUNASLATE = BLOCKS.registerWithItem(Constant.Block.LUNASLATE, new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)));
    public static final Block LUNASLATE_SLAB = BLOCKS.registerWithItem(Constant.Block.LUNASLATE_SLAB, new SlabBlock(BlockBehaviour.Properties.ofFullCopy(LUNASLATE)));
    public static final Block LUNASLATE_STAIRS = BLOCKS.registerWithItem(Constant.Block.LUNASLATE_STAIRS, new StairBlock(LUNASLATE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(LUNASLATE)));
    public static final Block LUNASLATE_WALL = BLOCKS.registerWithItem(Constant.Block.LUNASLATE_WALL, new WallBlock(BlockBehaviour.Properties.ofFullCopy(LUNASLATE)));

    public static final Block COBBLED_LUNASLATE = BLOCKS.registerWithItem(Constant.Block.COBBLED_LUNASLATE, new Block(BlockBehaviour.Properties.ofFullCopy(LUNASLATE).strength(3.5F, 6.0F)));
    public static final Block COBBLED_LUNASLATE_SLAB = BLOCKS.registerWithItem(Constant.Block.COBBLED_LUNASLATE_SLAB, new SlabBlock(BlockBehaviour.Properties.ofFullCopy(COBBLED_LUNASLATE)));
    public static final Block COBBLED_LUNASLATE_STAIRS = BLOCKS.registerWithItem(Constant.Block.COBBLED_LUNASLATE_STAIRS, new StairBlock(COBBLED_LUNASLATE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(COBBLED_LUNASLATE)));
    public static final Block COBBLED_LUNASLATE_WALL = BLOCKS.registerWithItem(Constant.Block.COBBLED_LUNASLATE_WALL, new WallBlock(BlockBehaviour.Properties.ofFullCopy(COBBLED_LUNASLATE)));

    public static final Block MOON_BASALT = BLOCKS.registerWithItem(Constant.Block.MOON_BASALT, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
    public static final Block MOON_BASALT_SLAB = BLOCKS.registerWithItem(Constant.Block.MOON_BASALT_SLAB, new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MOON_BASALT).strength(2.5F, 6.0F)));
    public static final Block MOON_BASALT_STAIRS = BLOCKS.registerWithItem(Constant.Block.MOON_BASALT_STAIRS, new StairBlock(MOON_BASALT.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MOON_BASALT)));
    public static final Block MOON_BASALT_WALL = BLOCKS.registerWithItem(Constant.Block.MOON_BASALT_WALL, new WallBlock(BlockBehaviour.Properties.ofFullCopy(MOON_BASALT)));

    public static final Block MOON_BASALT_BRICK = BLOCKS.registerWithItem(Constant.Block.MOON_BASALT_BRICK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(2.5F, 6.0F).requiresCorrectToolForDrops()));
    public static final Block MOON_BASALT_BRICK_SLAB = BLOCKS.registerWithItem(Constant.Block.MOON_BASALT_BRICK_SLAB, new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MOON_BASALT_BRICK).strength(3.0F, 6.0F)));
    public static final Block MOON_BASALT_BRICK_STAIRS = BLOCKS.registerWithItem(Constant.Block.MOON_BASALT_BRICK_STAIRS, new StairBlock(MOON_BASALT_BRICK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MOON_BASALT_BRICK)));
    public static final Block MOON_BASALT_BRICK_WALL = BLOCKS.registerWithItem(Constant.Block.MOON_BASALT_BRICK_WALL, new WallBlock(BlockBehaviour.Properties.ofFullCopy(MOON_BASALT_BRICK)));

    public static final Block CRACKED_MOON_BASALT_BRICK = BLOCKS.registerWithItem(Constant.Block.CRACKED_MOON_BASALT_BRICK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
    public static final Block CRACKED_MOON_BASALT_BRICK_SLAB = BLOCKS.registerWithItem(Constant.Block.CRACKED_MOON_BASALT_BRICK_SLAB, new SlabBlock(BlockBehaviour.Properties.ofFullCopy(CRACKED_MOON_BASALT_BRICK).strength(2.5F, 6.0F)));
    public static final Block CRACKED_MOON_BASALT_BRICK_STAIRS = BLOCKS.registerWithItem(Constant.Block.CRACKED_MOON_BASALT_BRICK_STAIRS, new StairBlock(CRACKED_MOON_BASALT_BRICK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(CRACKED_MOON_BASALT_BRICK)));
    public static final Block CRACKED_MOON_BASALT_BRICK_WALL = BLOCKS.registerWithItem(Constant.Block.CRACKED_MOON_BASALT_BRICK_WALL, new WallBlock(BlockBehaviour.Properties.ofFullCopy(CRACKED_MOON_BASALT_BRICK)));

    public static final Block FALLEN_METEOR = BLOCKS.registerWithItem(Constant.Block.FALLEN_METEOR, new FallenMeteorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F).randomTicks().noOcclusion().sound(SoundType.BASALT).requiresCorrectToolForDrops()));

    // MARS NATURAL
    public static final Block MARS_SURFACE_ROCK = BLOCKS.registerWithItem(Constant.Block.MARS_SURFACE_ROCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).destroyTime(2.2F).requiresCorrectToolForDrops()));
    public static final Block MARS_SUB_SURFACE_ROCK = BLOCKS.registerWithItem(Constant.Block.MARS_SUB_SURFACE_ROCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).destroyTime(2.6F).requiresCorrectToolForDrops()));

    public static final Block MARS_STONE = BLOCKS.registerWithItem(Constant.Block.MARS_STONE, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).destroyTime(3.0F).requiresCorrectToolForDrops()));
    public static final Block MARS_STONE_SLAB = BLOCKS.registerWithItem(Constant.Block.MARS_STONE_SLAB, new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MARS_STONE).strength(3.5F, 6.0F)));
    public static final Block MARS_STONE_STAIRS = BLOCKS.registerWithItem(Constant.Block.MARS_STONE_STAIRS, new StairBlock(MARS_STONE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MARS_STONE)));
    public static final Block MARS_STONE_WALL = BLOCKS.registerWithItem(Constant.Block.MARS_STONE_WALL, new WallBlock(BlockBehaviour.Properties.ofFullCopy(MARS_STONE)));

    public static final Block MARS_COBBLESTONE = BLOCKS.registerWithItem(Constant.Block.MARS_COBBLESTONE, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).destroyTime(3.0F).requiresCorrectToolForDrops()));
    public static final Block MARS_COBBLESTONE_SLAB = BLOCKS.registerWithItem(Constant.Block.MARS_COBBLESTONE_SLAB, new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MARS_COBBLESTONE).strength(3.5F, 6.0F)));
    public static final Block MARS_COBBLESTONE_STAIRS = BLOCKS.registerWithItem(Constant.Block.MARS_COBBLESTONE_STAIRS, new StairBlock(MARS_COBBLESTONE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MARS_COBBLESTONE)));
    public static final Block MARS_COBBLESTONE_WALL = BLOCKS.registerWithItem(Constant.Block.MARS_COBBLESTONE_WALL, new WallBlock(BlockBehaviour.Properties.ofFullCopy(MARS_COBBLESTONE)));

    // ASTEROID NATURAL
    public static final Block ASTEROID_ROCK = BLOCKS.registerWithItem(Constant.Block.ASTEROID_ROCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).instrument(NoteBlockInstrument.BASEDRUM).destroyTime(3.0F).requiresCorrectToolForDrops()));
    public static final Block ASTEROID_ROCK_1 = BLOCKS.registerWithItem(Constant.Block.ASTEROID_ROCK_1, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASEDRUM).destroyTime(3.0F).requiresCorrectToolForDrops())); // todo naming
    public static final Block ASTEROID_ROCK_2 = BLOCKS.registerWithItem(Constant.Block.ASTEROID_ROCK_2, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).destroyTime(3.0F).requiresCorrectToolForDrops()));

    // VENUS NATURAL
    public static final Block SOFT_VENUS_ROCK = BLOCKS.registerWithItem(Constant.Block.SOFT_VENUS_ROCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F)));
    public static final Block HARD_VENUS_ROCK = BLOCKS.registerWithItem(Constant.Block.HARD_VENUS_ROCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
    public static final Block SCORCHED_VENUS_ROCK = BLOCKS.registerWithItem(Constant.Block.SCORCHED_VENUS_ROCK, new ScorchedRockBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
    public static final Block VOLCANIC_ROCK = BLOCKS.registerWithItem(Constant.Block.VOLCANIC_ROCK, new VolcanicRockBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.2F, 0.5F).requiresCorrectToolForDrops()));
    public static final Block PUMICE = BLOCKS.registerWithItem(Constant.Block.PUMICE, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
    public static final Block VAPOR_SPOUT = BLOCKS.registerWithItem(Constant.Block.VAPOR_SPOUT, new VaporSpoutBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 2.0F).requiresCorrectToolForDrops()));

    // MISC DECOR
    public static final Block WALKWAY = BLOCKS.registerWithItem(Constant.Block.WALKWAY, new WalkwayBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(5.0f, 5.0f).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public static final Block FLUID_PIPE_WALKWAY = BLOCKS.registerWithItem(Constant.Block.FLUID_PIPE_WALKWAY, new FluidPipeWalkway(BlockBehaviour.Properties.ofFullCopy(WALKWAY)));
    public static final Block WIRE_WALKWAY = BLOCKS.registerWithItem(Constant.Block.WIRE_WALKWAY, new WireWalkway(BlockBehaviour.Properties.ofFullCopy(WALKWAY)));
    public static final Block TIN_LADDER = BLOCKS.registerWithItem(Constant.Block.TIN_LADDER, new TinLadderBlock(BlockBehaviour.Properties.of().forceSolidOff().noOcclusion().pushReaction(PushReaction.DESTROY).strength(1.0f, 1.0f).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public static final Block IRON_GRATING = BLOCKS.registerWithItem(Constant.Block.IRON_GRATING, new IronGratingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(2.5f, 6.0f).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion()));

    // SPECIAL
    public static final Block ALUMINUM_WIRE = BLOCKS.registerWithItem(Constant.Block.ALUMINUM_WIRE, new AluminumWireBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).mapColor(MapColor.NONE)));
    public static final Block SEALABLE_ALUMINUM_WIRE = BLOCKS.registerWithItem(Constant.Block.SEALABLE_ALUMINUM_WIRE, new SealableAluminumWireBlock(BlockBehaviour.Properties.ofFullCopy(ALUMINUM_DECORATION.block())));
    public static final Block HEAVY_SEALABLE_ALUMINUM_WIRE = BLOCKS.registerWithItem(Constant.Block.HEAVY_SEALABLE_ALUMINUM_WIRE, new HeavySealableAluminumWireBlock(BlockBehaviour.Properties.ofFullCopy(ALUMINUM_DECORATION.block())));
    public static final Block GLASS_FLUID_PIPE = BLOCKS.registerWithItem(Constant.Block.GLASS_FLUID_PIPE, new GlassFluidPipeBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).requiresCorrectToolForDrops().forceSolidOn()));
    public static final Block FUELING_PAD = BLOCKS.registerWithItem(Constant.Block.FUELING_PAD, new FuelPadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 10.0F).requiresCorrectToolForDrops()));
    public static final Block ROCKET_LAUNCH_PAD = BLOCKS.registerWithItem(Constant.Block.ROCKET_LAUNCH_PAD, new LaunchPadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 10.0F).requiresCorrectToolForDrops()));
    public static final Block ROCKET_WORKBENCH = BLOCKS.registerWithItem(Constant.Block.ROCKET_WORKBENCH, new RocketWorkbench(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F)));
    public static final Block PARACHEST = BLOCKS.registerWithItem(Constant.Block.PARACHEST, new ParachestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST)));

    // LIGHT PANELS
    public static final Block SQUARE_LIGHT_PANEL = BLOCKS.registerWithItem(Constant.Block.SQUARE_LIGHT_PANEL, new LightPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)));
    public static final Block SPOTLIGHT_LIGHT_PANEL = BLOCKS.registerWithItem(Constant.Block.SPOTLIGHT_LIGHT_PANEL, new LightPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL), 3.0f));
    public static final Block LINEAR_LIGHT_PANEL = BLOCKS.registerWithItem(Constant.Block.LINEAR_LIGHT_PANEL, new LightPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL), 5.0f));
    public static final Block DASHED_LIGHT_PANEL = BLOCKS.registerWithItem(Constant.Block.DASHED_LIGHT_PANEL, new LightPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL), 1.0f));
    public static final Block DIAGONAL_LIGHT_PANEL = BLOCKS.registerWithItem(Constant.Block.DIAGONAL_LIGHT_PANEL, new LightPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL), 1.0f));

    // VACUUM GLASS
    public static final Block VACUUM_GLASS = BLOCKS.registerWithItem(Constant.Block.VACUUM_GLASS, new VacuumGlassBlock(BlockBehaviour.Properties.of().noOcclusion().sound(SoundType.GLASS)));
    public static final Block CLEAR_VACUUM_GLASS = BLOCKS.registerWithItem(Constant.Block.CLEAR_VACUUM_GLASS, new VacuumGlassBlock(BlockBehaviour.Properties.of().noOcclusion().sound(SoundType.GLASS)));
    public static final Block STRONG_VACUUM_GLASS = BLOCKS.registerWithItem(Constant.Block.STRONG_VACUUM_GLASS, new VacuumGlassBlock(BlockBehaviour.Properties.of().noOcclusion().sound(SoundType.GLASS)));

    // ORES
    public static final Block SILICON_ORE = BLOCKS.registerWithItem(Constant.Block.SILICON_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(Blocks.STONE, 3.0F, 3.0F)));
    public static final Block DEEPSLATE_SILICON_ORE = BLOCKS.registerWithItem(Constant.Block.DEEPSLATE_SILICON_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(Blocks.DEEPSLATE, 4.5F, 3.0F)));
    public static final Block TIN_ORE = BLOCKS.registerWithItem(Constant.Block.TIN_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(Blocks.STONE, 3.0F, 3.0F)));
    public static final Block DEEPSLATE_TIN_ORE = BLOCKS.registerWithItem(Constant.Block.DEEPSLATE_TIN_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(Blocks.DEEPSLATE, 4.5F, 3.0F)));
    public static final Block ALUMINUM_ORE = BLOCKS.registerWithItem(Constant.Block.ALUMINUM_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(Blocks.STONE, 3.0F, 3.0F)));
    public static final Block DEEPSLATE_ALUMINUM_ORE = BLOCKS.registerWithItem(Constant.Block.DEEPSLATE_ALUMINUM_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(Blocks.DEEPSLATE, 3.5F, 3.0F)));

    public static final Block MOON_COPPER_ORE = BLOCKS.registerWithItem(Constant.Block.MOON_COPPER_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(MOON_ROCK, 3.0F, 5.0F)));
    public static final Block LUNASLATE_COPPER_ORE = BLOCKS.registerWithItem(Constant.Block.LUNASLATE_COPPER_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(LUNASLATE, 5.0F, 5.0F)));
    public static final Block MOON_TIN_ORE = BLOCKS.registerWithItem(Constant.Block.MOON_TIN_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(MOON_ROCK, 3.0F, 5.0F)));
    public static final Block LUNASLATE_TIN_ORE = BLOCKS.registerWithItem(Constant.Block.LUNASLATE_TIN_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(LUNASLATE, 5.0F, 5.0F)));
    public static final Block MOON_CHEESE_ORE = BLOCKS.registerWithItem(Constant.Block.MOON_CHEESE_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(MOON_ROCK, 3.0F, 5.0F)));
    public static final Block LUNAR_SAPPHIRE_ORE = BLOCKS.registerWithItem(Constant.Block.LUNAR_SAPPHIRE_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(MOON_ROCK, 3.0F, 5.0F)));

    public static final Block MARS_IRON_ORE = BLOCKS.registerWithItem(Constant.Block.MARS_IRON_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(MARS_STONE, 3.5F, 3.0F)));
    public static final Block MARS_COPPER_ORE = BLOCKS.registerWithItem(Constant.Block.MARS_COPPER_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(MARS_STONE, 5.0F, 5.0F)));
    public static final Block MARS_TIN_ORE = BLOCKS.registerWithItem(Constant.Block.MARS_TIN_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(MARS_STONE, 5.0F, 5.0F)));
    public static final Block DESH_ORE = BLOCKS.registerWithItem(Constant.Block.DESH_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(MARS_STONE, 3.0F, 5.0F)));

    public static final Block ASTEROID_IRON_ORE = BLOCKS.registerWithItem(Constant.Block.ASTEROID_IRON_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(ASTEROID_ROCK, 3.5F, 3.0F)));
    public static final Block ASTEROID_ALUMINUM_ORE = BLOCKS.registerWithItem(Constant.Block.ASTEROID_ALUMINUM_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(ASTEROID_ROCK, 3.5F, 3.0F)));
    public static final Block ILMENITE_ORE = BLOCKS.registerWithItem(Constant.Block.ILMENITE_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(ASTEROID_ROCK, 3.0F, 5.0F)));

    public static final Block VENUS_COPPER_ORE = BLOCKS.registerWithItem(Constant.Block.VENUS_COPPER_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(SOFT_VENUS_ROCK, 5.0F, 5.0F)));
    public static final Block VENUS_TIN_ORE = BLOCKS.registerWithItem(Constant.Block.VENUS_TIN_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(SOFT_VENUS_ROCK, 5.0F, 5.0F)));
    public static final Block VENUS_ALUMINUM_ORE = BLOCKS.registerWithItem(Constant.Block.VENUS_ALUMINUM_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(SOFT_VENUS_ROCK, 3.5F, 3.0F)));
    public static final Block GALENA_ORE = BLOCKS.registerWithItem(Constant.Block.GALENA_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(HARD_VENUS_ROCK, 3.0F, 5.0F)));
    public static final Block SOLAR_ORE = BLOCKS.registerWithItem(Constant.Block.SOLAR_ORE, new DropExperienceBlock(ConstantInt.of(0), oreSettings(SOFT_VENUS_ROCK, 3.0F, 5.0F)));

    public static final Block OLIVINE_CLUSTER = BLOCKS.registerWithItem(Constant.Block.OLIVINE_CLUSTER, new OlivineClusterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).mapColor(MapColor.COLOR_LIGHT_GREEN)));
    public static final Block OLIVINE_BASALT = BLOCKS.registerWithItem(Constant.Block.OLIVINE_BASALT, new Block(BlockBehaviour.Properties.ofFullCopy(MOON_BASALT).strength(3.5F, 6.0F)));
    public static final Block RICH_OLIVINE_BASALT = BLOCKS.registerWithItem(Constant.Block.RICH_OLIVINE_BASALT, new Block(BlockBehaviour.Properties.ofFullCopy(OLIVINE_BASALT)));

    // COMPACT MINERAL BLOCKS
    public static final Block SILICON_BLOCK = BLOCKS.registerWithItem(Constant.Block.SILICON_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public static final Block METEORIC_IRON_BLOCK = BLOCKS.registerWithItem(Constant.Block.METEORIC_IRON_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public static final Block DESH_BLOCK = BLOCKS.registerWithItem(Constant.Block.DESH_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public static final Block ALUMINUM_BLOCK = BLOCKS.registerWithItem(Constant.Block.ALUMINUM_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public static final Block TIN_BLOCK = BLOCKS.registerWithItem(Constant.Block.TIN_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.GLOW_LICHEN).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public static final Block TITANIUM_BLOCK = BLOCKS.registerWithItem(Constant.Block.TITANIUM_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.CLAY).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public static final Block LEAD_BLOCK = BLOCKS.registerWithItem(Constant.Block.LEAD_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public static final Block LUNAR_SAPPHIRE_BLOCK = BLOCKS.registerWithItem(Constant.Block.LUNAR_SAPPHIRE_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(5.0F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()));
    public static final Block OLIVINE_BLOCK = BLOCKS.registerWithItem(Constant.Block.OLIVINE_BLOCK, new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(1.5F, 1.5F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops()));
    public static final Block RAW_METEORIC_IRON_BLOCK = BLOCKS.registerWithItem(Constant.Block.RAW_METEORIC_IRON_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(5.0F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()));
    public static final Block RAW_DESH_BLOCK = BLOCKS.registerWithItem(Constant.Block.RAW_DESH_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(5.0F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()));
    public static final Block RAW_ALUMINUM_BLOCK = BLOCKS.registerWithItem(Constant.Block.RAW_ALUMINUM_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(5.0F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()));
    public static final Block RAW_TIN_BLOCK = BLOCKS.registerWithItem(Constant.Block.RAW_TIN_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.GLOW_LICHEN).strength(5.0F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()));
    public static final Block RAW_TITANIUM_BLOCK = BLOCKS.registerWithItem(Constant.Block.RAW_TITANIUM_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.CLAY).strength(5.0F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()));
    public static final Block RAW_LEAD_BLOCK = BLOCKS.registerWithItem(Constant.Block.RAW_LEAD_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLUE).strength(5.0F, 6.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()));

    // CHEESE BLOCKS
    public static final Block MOON_CHEESE_BLOCK = BLOCKS.registerWithItem(Constant.Block.MOON_CHEESE_BLOCK, new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).sound(SoundType.WOOL)));
    public static final Block MOON_CHEESE_LOG = BLOCKS.registerWithItem(Constant.Block.MOON_CHEESE_LOG, new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5F).sound(SoundType.WOOL)));
    public static final Block MOON_CHEESE_LEAVES = BLOCKS.registerWithItem(Constant.Block.MOON_CHEESE_LEAVES, new LeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isSuffocating(GCBlocks::never).isViewBlocking(GCBlocks::never).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor(GCBlocks::never)));

    public static final Block MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.MOON_CHEESE_WHEEL, new MoonCheeseWheel(BlockBehaviour.Properties.of().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY)));
    public static final Block CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.CANDLE, BlockBehaviour.Properties.ofFullCopy(MOON_CHEESE_WHEEL).lightLevel(litBlockEmission(3))));
    public static final Block WHITE_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.WHITE_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.WHITE_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block ORANGE_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.ORANGE_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.ORANGE_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block MAGENTA_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.MAGENTA_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.MAGENTA_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block LIGHT_BLUE_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.LIGHT_BLUE_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.LIGHT_BLUE_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block YELLOW_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.YELLOW_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.YELLOW_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block LIME_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.LIME_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.LIME_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block PINK_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.PINK_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.PINK_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block GRAY_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.GRAY_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.GRAY_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block LIGHT_GRAY_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.LIGHT_GRAY_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.LIGHT_GRAY_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block CYAN_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.CYAN_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.CYAN_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block PURPLE_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.PURPLE_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.PURPLE_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block BLUE_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.BLUE_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.BLUE_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block BROWN_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.BROWN_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.BROWN_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block GREEN_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.GREEN_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.GREEN_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block RED_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.RED_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.RED_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));
    public static final Block BLACK_CANDLE_MOON_CHEESE_WHEEL = BLOCKS.register(Constant.Block.BLACK_CANDLE_MOON_CHEESE_WHEEL, new CandleMoonCheeseWheel(Blocks.BLACK_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_WHEEL)));

    // MOON VILLAGER SPECIAL
    public static final Block LUNAR_CARTOGRAPHY_TABLE = BLOCKS.registerWithItem(Constant.Block.LUNAR_CARTOGRAPHY_TABLE, new LunarCartographyTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD)));

    // MISC WORLD GEN
    public static final Block CAVERNOUS_VINES = BLOCKS.registerWithItem(Constant.Block.CAVERNOUS_VINES, new CavernousVinesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).randomTicks().noCollission().lightLevel(CavernousVines.emission(8)).instabreak().sound(SoundType.CAVE_VINES).pushReaction(PushReaction.DESTROY)));
    public static final Block CAVERNOUS_VINES_PLANT = BLOCKS.register(Constant.Block.CAVERNOUS_VINES_PLANT, new CavernousVinesPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).noCollission().lightLevel(CavernousVines.emission(8)).instabreak().sound(SoundType.CAVE_VINES).pushReaction(PushReaction.DESTROY).dropsLike(CAVERNOUS_VINES)));
    public static final Block BOSS_SPAWNER = BLOCKS.registerWithItem(Constant.Block.BOSS_SPAWNER, new BossSpawner(BlockBehaviour.Properties.ofFullCopy(Blocks.SPAWNER).noLootTable().noCollission()));

    // MUTLIBLOCK PARTS
    public static final BaseEntityBlock SOLAR_PANEL_PART = BLOCKS.register(Constant.Block.SOLAR_PANEL_PART, new SolarPanelPartBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(-1.0F, 5.0F).noLootTable().sound(SoundType.METAL)));
    public static final BaseEntityBlock CRYOGENIC_CHAMBER_PART = BLOCKS.register(Constant.Block.CRYOGENIC_CHAMBER_PART, new CryogenicChamberPart(BlockBehaviour.Properties.of().noOcclusion().isSuffocating(GCBlocks::never).isViewBlocking(GCBlocks::never).mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).noLootTable().sound(SoundType.METAL).requiresCorrectToolForDrops()));

    // MISC MACHINES
    public static final Block CRYOGENIC_CHAMBER = BLOCKS.registerWithItem(Constant.Block.CRYOGENIC_CHAMBER, new CryogenicChamberBlock(BlockBehaviour.Properties.of().noOcclusion().isSuffocating(GCBlocks::never).isViewBlocking(GCBlocks::never).mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public static final Block PLAYER_TRANSPORT_TUBE = BLOCKS.registerWithItem(Constant.Block.PLAYER_TRANSPORT_TUBE, new TransportTube(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL).noCollission()));

    // MACHINES
    public static final Block CIRCUIT_FABRICATOR = BLOCKS.registerWithItem(Constant.Block.CIRCUIT_FABRICATOR, new ElectricGrillBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops(), Constant.id(Constant.Block.CIRCUIT_FABRICATOR)));
    public static final Block COMPRESSOR = BLOCKS.registerWithItem(Constant.Block.COMPRESSOR, new CompressorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(CompressorBlock.ACTIVE) ? 13 : 0)));
    public static final Block ELECTRIC_COMPRESSOR = BLOCKS.registerWithItem(Constant.Block.ELECTRIC_COMPRESSOR, new ElectricGrillBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops(), Constant.id(Constant.Block.ELECTRIC_COMPRESSOR)));
    public static final Block COAL_GENERATOR = BLOCKS.registerWithItem(Constant.Block.COAL_GENERATOR, new CoalGeneratorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(CoalGeneratorBlock.ACTIVE) ? 13 : 0)));
    public static final Block BASIC_SOLAR_PANEL = BLOCKS.registerWithItem(Constant.Block.BASIC_SOLAR_PANEL, SimpleMultiBlockMachineBlock.create(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops(), Constant.id(Constant.Block.BASIC_SOLAR_PANEL), MultiBlockUtil.generateSolarPanelParts(), GCBlocks.SOLAR_PANEL_PART));
    public static final Block ADVANCED_SOLAR_PANEL = BLOCKS.registerWithItem(Constant.Block.ADVANCED_SOLAR_PANEL, SimpleMultiBlockMachineBlock.create(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops(), Constant.id(Constant.Block.ADVANCED_SOLAR_PANEL), MultiBlockUtil.generateSolarPanelParts(), GCBlocks.SOLAR_PANEL_PART));
    public static final Block ENERGY_STORAGE_MODULE = BLOCKS.registerWithItem(Constant.Block.ENERGY_STORAGE_MODULE, new ResourceStorageBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops(), Constant.id(Constant.Block.ENERGY_STORAGE_MODULE)));
    public static final Block ELECTRIC_FURNACE = BLOCKS.registerWithItem(Constant.Block.ELECTRIC_FURNACE, new SimpleMachineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops(), Constant.id(Constant.Block.ELECTRIC_FURNACE)));
    public static final Block ELECTRIC_ARC_FURNACE = BLOCKS.registerWithItem(Constant.Block.ELECTRIC_ARC_FURNACE, new SimpleMachineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops(), Constant.id(Constant.Block.ELECTRIC_ARC_FURNACE)));
    public static final Block REFINERY = BLOCKS.registerWithItem(Constant.Block.REFINERY, new RefineryBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public static final Block OXYGEN_COLLECTOR = BLOCKS.registerWithItem(Constant.Block.OXYGEN_COLLECTOR, new OxygenCollectorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public static final Block OXYGEN_SEALER = BLOCKS.registerWithItem(Constant.Block.OXYGEN_SEALER, new SimpleMachineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops(), Constant.id(Constant.Block.OXYGEN_SEALER)));
    public static final Block OXYGEN_BUBBLE_DISTRIBUTOR = BLOCKS.registerWithItem(Constant.Block.OXYGEN_BUBBLE_DISTRIBUTOR, new SimpleMachineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops(), Constant.id(Constant.Block.OXYGEN_BUBBLE_DISTRIBUTOR)));
    public static final Block OXYGEN_DECOMPRESSOR = BLOCKS.registerWithItem(Constant.Block.OXYGEN_DECOMPRESSOR, new SimpleMachineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops(), Constant.id(Constant.Block.OXYGEN_DECOMPRESSOR)));
    public static final Block OXYGEN_COMPRESSOR = BLOCKS.registerWithItem(Constant.Block.OXYGEN_COMPRESSOR, new SimpleMachineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops(), Constant.id(Constant.Block.OXYGEN_COMPRESSOR)));
    public static final Block OXYGEN_STORAGE_MODULE = BLOCKS.registerWithItem(Constant.Block.OXYGEN_STORAGE_MODULE, new ResourceStorageBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops(), Constant.id(Constant.Block.OXYGEN_STORAGE_MODULE)));
    public static final Block FUEL_LOADER = BLOCKS.registerWithItem(Constant.Block.FUEL_LOADER, new FuelLoaderBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 5.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()));

    public static final AirlockBlock AIR_LOCK_FRAME = BLOCKS.registerWithItem(Constant.Block.AIR_LOCK_FRAME, new AirlockBlock(false, BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).mapColor(MapColor.COLOR_GRAY)));
    public static final AirlockBlock AIR_LOCK_CONTROLLER = BLOCKS.registerWithItem(Constant.Block.AIR_LOCK_CONTROLLER, new AirlockBlock(true, BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).mapColor(MapColor.COLOR_GRAY)));
    public static final Block AIR_LOCK_SEAL = BLOCKS.register(Constant.Block.AIR_LOCK_SEAL, new Block(BlockBehaviour.Properties.ofFullCopy(AIR_LOCK_FRAME)));

    public static void register() {
        FlammableBlockRegistry.getDefaultInstance().add(FUEL, 80, 130);
        FlammableBlockRegistry.getDefaultInstance().add(CRUDE_OIL, 60, 100);
        FlammableBlockRegistry.getDefaultInstance().add(CAVERNOUS_VINES, 15, 60);
        FlammableBlockRegistry.getDefaultInstance().add(CAVERNOUS_VINES_PLANT, 15, 60);
        FlattenableBlockRegistry.register(MOON_TURF, MOON_DIRT_PATH.defaultBlockState());
        FlattenableBlockRegistry.register(MOON_DIRT, MOON_DIRT_PATH.defaultBlockState());
    }

    private static BlockBehaviour.Properties oreSettings(Block ground, float hardness, float resistance) {
        return BlockBehaviour.Properties.ofFullCopy(ground).strength(hardness, resistance).requiresCorrectToolForDrops();
    }

    private static boolean never(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }

    private static ToIntFunction<BlockState> litBlockEmission(int i) {
        return blockState -> blockState.getValue(BlockStateProperties.LIT) ? i : 0;
    }
}