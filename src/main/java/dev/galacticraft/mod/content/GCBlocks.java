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

package dev.galacticraft.mod.content;

import dev.galacticraft.machinelib.api.block.MachineBlock;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.decoration.GratingBlock;
import dev.galacticraft.mod.content.block.decoration.LightPanelBlock;
import dev.galacticraft.mod.content.block.decoration.LunarCartographyTableBlock;
import dev.galacticraft.mod.content.block.decoration.VacuumGlassBlock;
import dev.galacticraft.mod.content.block.entity.machine.*;
import dev.galacticraft.mod.content.block.environment.*;
import dev.galacticraft.mod.content.block.machine.*;
import dev.galacticraft.mod.content.block.special.*;
import dev.galacticraft.mod.content.block.special.aluminumwire.tier1.AluminumWireBlock;
import dev.galacticraft.mod.content.block.special.aluminumwire.tier1.SealableAluminumWireBlock;
import dev.galacticraft.mod.content.block.special.aluminumwire.tier2.HeavySealableAluminumWireBlock;
import dev.galacticraft.mod.content.block.special.fluidpipe.GlassFluidPipeBlock;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import dev.galacticraft.mod.content.block.special.walkway.FluidPipeWalkway;
import dev.galacticraft.mod.content.block.special.walkway.WalkwayBlock;
import dev.galacticraft.mod.content.block.special.walkway.WireWalkway;
import dev.galacticraft.mod.util.MultiBlockUtil;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
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

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@SuppressWarnings("unused")
public class GCBlocks {
    public static final GCBlockRegistry BLOCKS = new GCBlockRegistry();

    // TORCHES
    // These 2 torches are special, it's need to register early so others can use dropsLike() reference
    public static final Block GLOWSTONE_TORCH = register(Constant.Block.GLOWSTONE_TORCH, new GlowstoneTorchBlock(BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel(blockStatex -> 14).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)));
    public static final Block UNLIT_TORCH = register(Constant.Block.UNLIT_TORCH, new UnlitTorchBlock(BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel(blockStatex -> 0).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)));
    public static final Block GLOWSTONE_WALL_TORCH = new GlowstoneWallTorchBlock(BlockBehaviour.Properties.ofFullCopy(GLOWSTONE_TORCH).dropsLike(GLOWSTONE_TORCH));
    public static final Block UNLIT_WALL_TORCH = new UnlitWallTorchBlock(BlockBehaviour.Properties.ofFullCopy(UNLIT_TORCH).dropsLike(UNLIT_TORCH));

    // LANTERNS
    public static final Block GLOWSTONE_LANTERN = new GlowstoneLanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN));
    public static final Block UNLIT_LANTERN = new UnlitLanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN).lightLevel(state -> 0));

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
    public static final Block ALUMINUM_DECORATION = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F));
    public static final Block ALUMINUM_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(ALUMINUM_DECORATION).strength(2.5F, 3.0F));
    public static final Block ALUMINUM_DECORATION_STAIRS = BLOCKS.register(Constant.Block.ALUMINUM_DECORATION_STAIRS, new StairBlock(ALUMINUM_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(ALUMINUM_DECORATION)));
    public static final Block ALUMINUM_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(ALUMINUM_DECORATION));
    public static final Block DETAILED_ALUMINUM_DECORATION = BLOCKS.registerWithItem(Constant.Block.DETAILED_ALUMINUM_DECORATION, new Block(BlockBehaviour.Properties.ofFullCopy(ALUMINUM_DECORATION)));
    public static final Block DETAILED_ALUMINUM_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_ALUMINUM_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_ALUMINUM_DECORATION_STAIRS = new StairBlock(DETAILED_ALUMINUM_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(DETAILED_ALUMINUM_DECORATION));
    public static final Block DETAILED_ALUMINUM_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_ALUMINUM_DECORATION));

    public static final Block BRONZE_DECORATION = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F));
    public static final Block BRONZE_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(BRONZE_DECORATION).strength(2.5F, 3.0F));
    public static final Block BRONZE_DECORATION_STAIRS = new StairBlock(BRONZE_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(BRONZE_DECORATION));
    public static final Block BRONZE_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(BRONZE_DECORATION));
    public static final Block DETAILED_BRONZE_DECORATION = new Block(BlockBehaviour.Properties.ofFullCopy(BRONZE_DECORATION));
    public static final Block DETAILED_BRONZE_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_BRONZE_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_BRONZE_DECORATION_STAIRS = new StairBlock(DETAILED_BRONZE_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(DETAILED_BRONZE_DECORATION));
    public static final Block DETAILED_BRONZE_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_BRONZE_DECORATION));

    public static final Block COPPER_DECORATION = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F));
    public static final Block COPPER_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(COPPER_DECORATION).strength(2.5F, 3.0F));
    public static final Block COPPER_DECORATION_STAIRS = new StairBlock(COPPER_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(COPPER_DECORATION));
    public static final Block COPPER_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(COPPER_DECORATION));
    public static final Block DETAILED_COPPER_DECORATION = new Block(BlockBehaviour.Properties.ofFullCopy(COPPER_DECORATION));
    public static final Block DETAILED_COPPER_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_COPPER_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_COPPER_DECORATION_STAIRS = new StairBlock(DETAILED_COPPER_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(DETAILED_COPPER_DECORATION));
    public static final Block DETAILED_COPPER_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_COPPER_DECORATION));

    public static final Block IRON_DECORATION = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F));
    public static final Block IRON_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(IRON_DECORATION).strength(2.5F, 3.0F));
    public static final Block IRON_DECORATION_STAIRS = new StairBlock(IRON_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(IRON_DECORATION));
    public static final Block IRON_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(IRON_DECORATION));
    public static final Block DETAILED_IRON_DECORATION = new Block(BlockBehaviour.Properties.ofFullCopy(IRON_DECORATION));
    public static final Block DETAILED_IRON_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_IRON_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_IRON_DECORATION_STAIRS = new StairBlock(DETAILED_IRON_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(DETAILED_IRON_DECORATION));
    public static final Block DETAILED_IRON_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_IRON_DECORATION));

    public static final Block METEORIC_IRON_DECORATION = new Block(BlockBehaviour.Properties.ofFullCopy(IRON_DECORATION));
    public static final Block METEORIC_IRON_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(METEORIC_IRON_DECORATION).strength(2.5F, 3.0F));
    public static final Block METEORIC_IRON_DECORATION_STAIRS = new StairBlock(METEORIC_IRON_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(METEORIC_IRON_DECORATION));
    public static final Block METEORIC_IRON_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(METEORIC_IRON_DECORATION));
    public static final Block DETAILED_METEORIC_IRON_DECORATION = new Block(BlockBehaviour.Properties.ofFullCopy(METEORIC_IRON_DECORATION));
    public static final Block DETAILED_METEORIC_IRON_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_METEORIC_IRON_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_METEORIC_IRON_DECORATION_STAIRS = new StairBlock(DETAILED_METEORIC_IRON_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(DETAILED_METEORIC_IRON_DECORATION));
    public static final Block DETAILED_METEORIC_IRON_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_METEORIC_IRON_DECORATION));

    public static final Block STEEL_DECORATION = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F));
    public static final Block STEEL_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_DECORATION).strength(2.5F, 3.0F));
    public static final Block STEEL_DECORATION_STAIRS = new StairBlock(STEEL_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STEEL_DECORATION));
    public static final Block STEEL_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_DECORATION));
    public static final Block DETAILED_STEEL_DECORATION = new Block(BlockBehaviour.Properties.ofFullCopy(STEEL_DECORATION));
    public static final Block DETAILED_STEEL_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_STEEL_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_STEEL_DECORATION_STAIRS = new StairBlock(DETAILED_STEEL_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(DETAILED_STEEL_DECORATION));
    public static final Block DETAILED_STEEL_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_STEEL_DECORATION));

    public static final Block TIN_DECORATION = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F));
    public static final Block TIN_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(TIN_DECORATION).strength(2.5F, 3.0F));
    public static final Block TIN_DECORATION_STAIRS = new StairBlock(TIN_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(TIN_DECORATION));
    public static final Block TIN_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(TIN_DECORATION));
    public static final Block DETAILED_TIN_DECORATION = new Block(BlockBehaviour.Properties.ofFullCopy(TIN_DECORATION));
    public static final Block DETAILED_TIN_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_TIN_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_TIN_DECORATION_STAIRS = new StairBlock(DETAILED_TIN_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(DETAILED_TIN_DECORATION));
    public static final Block DETAILED_TIN_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_TIN_DECORATION));

    public static final Block TITANIUM_DECORATION = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F));
    public static final Block TITANIUM_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(TITANIUM_DECORATION).strength(2.5F, 3.0F));
    public static final Block TITANIUM_DECORATION_STAIRS = new StairBlock(TITANIUM_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(TITANIUM_DECORATION));
    public static final Block TITANIUM_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(TITANIUM_DECORATION));
    public static final Block DETAILED_TITANIUM_DECORATION = new Block(BlockBehaviour.Properties.ofFullCopy(TITANIUM_DECORATION));
    public static final Block DETAILED_TITANIUM_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_TITANIUM_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_TITANIUM_DECORATION_STAIRS = new StairBlock(DETAILED_TITANIUM_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(DETAILED_TITANIUM_DECORATION));
    public static final Block DETAILED_TITANIUM_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_TITANIUM_DECORATION));

    public static final Block DARK_DECORATION = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 3.0F));
    public static final Block DARK_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DARK_DECORATION).strength(2.5F, 3.0F));
    public static final Block DARK_DECORATION_STAIRS = new StairBlock(DARK_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(DARK_DECORATION));
    public static final Block DARK_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(DARK_DECORATION));
    public static final Block DETAILED_DARK_DECORATION = new Block(BlockBehaviour.Properties.ofFullCopy(DARK_DECORATION));
    public static final Block DETAILED_DARK_DECORATION_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_DARK_DECORATION).strength(2.5F, 3.0F));
    public static final Block DETAILED_DARK_DECORATION_STAIRS = new StairBlock(DETAILED_DARK_DECORATION.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(DETAILED_DARK_DECORATION));
    public static final Block DETAILED_DARK_DECORATION_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(DETAILED_DARK_DECORATION));

    // MOON NATURAL
    public static final Block MOON_TURF = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.5F, 0.5F));
    public static final Block MOON_DIRT = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.5F, 0.5F).sound(SoundType.GRAVEL));
    public static final Block MOON_DIRT_PATH = new MoonDirtPathBlock(BlockBehaviour.Properties.ofFullCopy(MOON_DIRT).strength(0.5F, 0.5F));
    public static final Block MOON_SURFACE_ROCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F));

    public static final Block MOON_ROCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F));
    public static final Block MOON_ROCK_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MOON_ROCK).strength(2.5F, 6.0F));
    public static final Block MOON_ROCK_STAIRS = new StairBlock(MOON_ROCK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MOON_ROCK));
    public static final Block MOON_ROCK_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(MOON_ROCK));

    public static final Block COBBLED_MOON_ROCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F));
    public static final Block COBBLED_MOON_ROCK_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(COBBLED_MOON_ROCK).strength(2.5F, 6.0F));
    public static final Block COBBLED_MOON_ROCK_STAIRS = new StairBlock(COBBLED_MOON_ROCK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(COBBLED_MOON_ROCK));
    public static final Block COBBLED_MOON_ROCK_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(COBBLED_MOON_ROCK));

    public static final Block LUNASLATE = new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE));
    public static final Block LUNASLATE_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(LUNASLATE));
    public static final Block LUNASLATE_STAIRS = new StairBlock(LUNASLATE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(LUNASLATE));
    public static final Block LUNASLATE_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(LUNASLATE));

    public static final Block COBBLED_LUNASLATE = new Block(BlockBehaviour.Properties.ofFullCopy(LUNASLATE).strength(3.5F, 6.0F));
    public static final Block COBBLED_LUNASLATE_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(COBBLED_LUNASLATE));
    public static final Block COBBLED_LUNASLATE_STAIRS = new StairBlock(COBBLED_LUNASLATE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(COBBLED_LUNASLATE));
    public static final Block COBBLED_LUNASLATE_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(COBBLED_LUNASLATE));

    public static final Block MOON_BASALT = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F));
    public static final Block MOON_BASALT_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MOON_BASALT).strength(2.5F, 6.0F));
    public static final Block MOON_BASALT_STAIRS = new StairBlock(MOON_BASALT.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MOON_BASALT));
    public static final Block MOON_BASALT_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(MOON_BASALT));

    public static final Block MOON_BASALT_BRICK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).instrument(NoteBlockInstrument.BASEDRUM).strength(2.5F, 6.0F));
    public static final Block MOON_BASALT_BRICK_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MOON_BASALT_BRICK).strength(3.0F, 6.0F));
    public static final Block MOON_BASALT_BRICK_STAIRS = new StairBlock(MOON_BASALT_BRICK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MOON_BASALT_BRICK));
    public static final Block MOON_BASALT_BRICK_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(MOON_BASALT_BRICK));

    public static final Block CRACKED_MOON_BASALT_BRICK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F));
    public static final Block CRACKED_MOON_BASALT_BRICK_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(CRACKED_MOON_BASALT_BRICK).strength(2.5F, 6.0F));
    public static final Block CRACKED_MOON_BASALT_BRICK_STAIRS = new StairBlock(CRACKED_MOON_BASALT_BRICK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(CRACKED_MOON_BASALT_BRICK));
    public static final Block CRACKED_MOON_BASALT_BRICK_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(CRACKED_MOON_BASALT_BRICK));

    public static final Block FALLEN_METEOR = new FallenMeteorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F).randomTicks().noOcclusion().sound(SoundType.BASALT));

    // MARS NATURAL
    public static final Block MARS_SURFACE_ROCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).instrument(NoteBlockInstrument.BASEDRUM).destroyTime(2.2F));
    public static final Block MARS_SUB_SURFACE_ROCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM).destroyTime(2.6F));

    public static final Block MARS_STONE = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM).destroyTime(3.0F));
    public static final Block MARS_STONE_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MARS_STONE).strength(3.5F, 6.0F));
    public static final Block MARS_STONE_STAIRS = new StairBlock(MARS_STONE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MARS_STONE));
    public static final Block MARS_STONE_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(MARS_STONE));

    public static final Block MARS_COBBLESTONE = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM).destroyTime(3.0F));
    public static final Block MARS_COBBLESTONE_SLAB = new SlabBlock(BlockBehaviour.Properties.ofFullCopy(MARS_COBBLESTONE).strength(3.5F, 6.0F));
    public static final Block MARS_COBBLESTONE_STAIRS = new StairBlock(MARS_COBBLESTONE.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(MARS_COBBLESTONE));
    public static final Block MARS_COBBLESTONE_WALL = new WallBlock(BlockBehaviour.Properties.ofFullCopy(MARS_COBBLESTONE));

    // ASTEROID NATURAL
    public static final Block ASTEROID_ROCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASEDRUM).destroyTime(3.0F));
    public static final Block ASTEROID_ROCK_1 = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASEDRUM).destroyTime(3.0F)); // todo naming
    public static final Block ASTEROID_ROCK_2 = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASEDRUM).destroyTime(3.0F));

    // VENUS NATURAL
    public static final Block SOFT_VENUS_ROCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F));
    public static final Block HARD_VENUS_ROCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F));
    public static final Block SCORCHED_VENUS_ROCK = new ScorchedRockBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F));
    public static final Block VOLCANIC_ROCK = new VolcanicRockBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(2.2F, 0.5F));
    public static final Block PUMICE = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F));
    public static final Block VAPOR_SPOUT = new VaporSpoutBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 2.0F));

    // MISC DECOR
    public static final Block WALKWAY = new WalkwayBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0f, 5.0f).sound(SoundType.METAL));
    public static final Block FLUID_PIPE_WALKWAY = new FluidPipeWalkway(BlockBehaviour.Properties.ofFullCopy(WALKWAY));
    public static final Block WIRE_WALKWAY = new WireWalkway(BlockBehaviour.Properties.ofFullCopy(WALKWAY));
    public static final Block TIN_LADDER = new TinLadderBlock(BlockBehaviour.Properties.of().forceSolidOff().noOcclusion().pushReaction(PushReaction.DESTROY).strength(1.0f, 1.0f).sound(SoundType.METAL));
    public static final Block GRATING = new GratingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2.5f, 6.0f).sound(SoundType.METAL));

    // SPECIAL
    public static final Block ALUMINUM_WIRE = new AluminumWireBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL));
    public static final Block SEALABLE_ALUMINUM_WIRE = new SealableAluminumWireBlock(BlockBehaviour.Properties.ofFullCopy(TIN_DECORATION));
    public static final Block HEAVY_SEALABLE_ALUMINUM_WIRE = new HeavySealableAluminumWireBlock(BlockBehaviour.Properties.ofFullCopy(TIN_DECORATION));
    public static final Block GLASS_FLUID_PIPE = BLOCKS.register(Constant.Block.GLASS_FLUID_PIPE, new GlassFluidPipeBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS)));
    public static final Block ROCKET_LAUNCH_PAD = new RocketLaunchPadBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F));
    public static final Block ROCKET_WORKBENCH = BLOCKS.register(Constant.Block.ROCKET_WORKBENCH, new RocketWorkbench(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F)));
    public static final Block PARACHEST = BLOCKS.registerWithItem(Constant.Block.PARACHEST, new ParaChestBlock(BlockBehaviour.Properties.of()));

    // LIGHT PANELS
    public static final Block SQUARE_LIGHT_PANEL = new LightPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL));
    public static final Block SPOTLIGHT_LIGHT_PANEL = new LightPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL), 3.0f);
    public static final Block LINEAR_LIGHT_PANEL = new LightPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL), 5.0f);
    public static final Block DASHED_LIGHT_PANEL = new LightPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL), 1.0f);
    public static final Block DIAGONAL_LIGHT_PANEL = new LightPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL), 1.0f);

    // VACUUM GLASS
    public static final Block VACUUM_GLASS = new VacuumGlassBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS));
    public static final Block CLEAR_VACUUM_GLASS = new VacuumGlassBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS));
    public static final Block STRONG_VACUUM_GLASS = new VacuumGlassBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS));

    // ORES
    public static final Block SILICON_ORE = new DropExperienceBlock(ConstantInt.of(0), oreSettings(3.0F, 3.0F, false));
    public static final Block DEEPSLATE_SILICON_ORE = new DropExperienceBlock(ConstantInt.of(0), oreSettings(4.5F, 3.0F, true));

    public static final Block MOON_COPPER_ORE = new DropExperienceBlock(ConstantInt.of(0), oreSettings(3.0F, 5.0F, false));
    public static final Block LUNASLATE_COPPER_ORE = new DropExperienceBlock(ConstantInt.of(0), oreSettings(5.0F, 5.0F, true));

    public static final Block TIN_ORE = new DropExperienceBlock(ConstantInt.of(0), oreSettings(3.0F, 3.0F, false));
    public static final Block DEEPSLATE_TIN_ORE = new DropExperienceBlock(ConstantInt.of(0), oreSettings(4.5F, 3.0F, true));
    public static final Block MOON_TIN_ORE = new DropExperienceBlock(ConstantInt.of(0), oreSettings(3.0F, 5.0F, false));
    public static final Block LUNASLATE_TIN_ORE = new DropExperienceBlock(ConstantInt.of(0), oreSettings(5.0F, 5.0F, true));

    public static final Block ALUMINUM_ORE = new DropExperienceBlock(ConstantInt.of(0), oreSettings(3.0F, 3.0F, false));
    public static final Block DEEPSLATE_ALUMINUM_ORE = new DropExperienceBlock(ConstantInt.of(0), oreSettings(3.5F, 3.0F, true));

    public static final Block DESH_ORE = new DropExperienceBlock(ConstantInt.of(0), oreSettings(3.0F, 5.0F, false));

    public static final Block ILMENITE_ORE = new DropExperienceBlock(ConstantInt.of(0), oreSettings(3.0F, 5.0F, false));

    public static final Block GALENA_ORE = new DropExperienceBlock(ConstantInt.of(0), oreSettings(3.0F, 5.0F, false));

    // CHEESE BLOCK
    public static final Block MOON_CHEESE_BLOCK = new MoonCheeseBlock(BlockBehaviour.Properties.of().forceSolidOn().strength(0.5F).sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY));
    public static final Block CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.CANDLE, BlockBehaviour.Properties.ofFullCopy(MOON_CHEESE_BLOCK).lightLevel(litBlockEmission(3)));
    public static final Block WHITE_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.WHITE_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block ORANGE_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.ORANGE_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block MAGENTA_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.MAGENTA_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block LIGHT_BLUE_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.LIGHT_BLUE_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block YELLOW_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.YELLOW_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block LIME_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.LIME_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block PINK_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.PINK_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block GRAY_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.GRAY_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block LIGHT_GRAY_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.LIGHT_GRAY_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block CYAN_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.CYAN_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block PURPLE_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.PURPLE_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block BLUE_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.BLUE_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block BROWN_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.BROWN_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block GREEN_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.GREEN_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block RED_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.RED_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));
    public static final Block BLACK_CANDLE_MOON_CHEESE_BLOCK = new CandleMoonCheeseBlock(Blocks.BLACK_CANDLE, BlockBehaviour.Properties.ofFullCopy(CANDLE_MOON_CHEESE_BLOCK));

    // COMPACT MINERAL BLOCKS
    public static final Block SILICON_BLOCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL));
    public static final Block METEORIC_IRON_BLOCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL));
    public static final Block DESH_BLOCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL));
    public static final Block TITANIUM_BLOCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL));
    public static final Block LEAD_BLOCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL));
    public static final Block LUNAR_SAPPHIRE_BLOCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 6.0F).sound(SoundType.STONE));

    // MOON VILLAGER SPECIAL
    public static final Block LUNAR_CARTOGRAPHY_TABLE = new LunarCartographyTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD));

    // MISC WORLD GEN
    public static final Block CAVERNOUS_VINES = register(Constant.Block.CAVERNOUS_VINES, new CavernousVinesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).randomTicks().noCollission().lightLevel(CavernousVines.emission(8)).instabreak().sound(SoundType.CAVE_VINES).pushReaction(PushReaction.DESTROY)));
    public static final Block CAVERNOUS_VINES_PLANT = new CavernousVinesPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).noCollission().lightLevel(CavernousVines.emission(8)).instabreak().sound(SoundType.CAVE_VINES).pushReaction(PushReaction.DESTROY).dropsLike(CAVERNOUS_VINES));
    public static final Block MOON_BERRY_BUSH = new MoonBerryBushBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).pushReaction(PushReaction.DESTROY).noLootTable().noCollission().lightLevel(blockstate -> 3).sound(SoundType.SWEET_BERRY_BUSH).randomTicks());

    // DUMMY
    public static final BaseEntityBlock SOLAR_PANEL_PART = new SolarPanelPartBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(-1.0F, 5.0F).noLootTable().sound(SoundType.METAL));
    public static final BaseEntityBlock CRYOGENIC_CHAMBER_PART = new CryogenicChamberPart(BlockBehaviour.Properties.of().noOcclusion().isSuffocating(GCBlocks::never).isViewBlocking(GCBlocks::never).mapColor(MapColor.METAL).strength(3.0F, 5.0F).noLootTable().sound(SoundType.METAL));

    // MISC MACHINES
    public static final Block CRYOGENIC_CHAMBER = new CryogenicChamberBlock(BlockBehaviour.Properties.of().noOcclusion().isSuffocating(GCBlocks::never).isViewBlocking(GCBlocks::never).mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL));
    public static final Block PLAYER_TRANSPORT_TUBE = new TransportTube(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL).noCollission());

    // MACHINES
    public static final Block CIRCUIT_FABRICATOR = new MachineBlock<>(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL), Constant.id(Constant.Block.CIRCUIT_FABRICATOR));
    public static final Block COMPRESSOR = new MachineBlock<>(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL), Constant.id(Constant.Block.COMPRESSOR));
    public static final Block ELECTRIC_COMPRESSOR = new MachineBlock<>(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL), Constant.id(Constant.Block.ELECTRIC_COMPRESSOR));
    public static final Block COAL_GENERATOR = new CoalGeneratorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL).lightLevel(state -> state.getValue(CoalGeneratorBlock.ACTIVE) ? 13 : 0));
    public static final Block BASIC_SOLAR_PANEL = SimpleMultiBlockMachineBlock.create(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL), Constant.id(Constant.Block.BASIC_SOLAR_PANEL), MultiBlockUtil.generateSolarPanelParts(), GCBlocks.SOLAR_PANEL_PART);
    public static final Block ADVANCED_SOLAR_PANEL = SimpleMultiBlockMachineBlock.create(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL), Constant.id(Constant.Block.ADVANCED_SOLAR_PANEL), MultiBlockUtil.generateSolarPanelParts(), GCBlocks.SOLAR_PANEL_PART);
    public static final Block ENERGY_STORAGE_MODULE = new MachineBlock<>(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL), Constant.id(Constant.Block.ENERGY_STORAGE_MODULE));
    public static final Block ELECTRIC_FURNACE = new MachineBlock<>(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL), Constant.id(Constant.Block.ELECTRIC_FURNACE));
    public static final Block ELECTRIC_ARC_FURNACE = new MachineBlock<>(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL), Constant.id(Constant.Block.ELECTRIC_ARC_FURNACE));
    public static final Block REFINERY = new RefineryBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL));
    public static final Block OXYGEN_COLLECTOR = new OxygenCollectorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL));
    public static final Block OXYGEN_SEALER = new MachineBlock<>(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL), Constant.id(Constant.Block.OXYGEN_SEALER));
    public static final Block OXYGEN_BUBBLE_DISTRIBUTOR = new MachineBlock<>(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL), Constant.id(Constant.Block.OXYGEN_BUBBLE_DISTRIBUTOR));
    public static final Block OXYGEN_DECOMPRESSOR = new MachineBlock<>(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL), Constant.id(Constant.Block.OXYGEN_DECOMPRESSOR));
    public static final Block OXYGEN_COMPRESSOR = new MachineBlock<>(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL), Constant.id(Constant.Block.OXYGEN_COMPRESSOR));
    public static final Block OXYGEN_STORAGE_MODULE = new MachineBlock<>(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL), Constant.id(Constant.Block.OXYGEN_STORAGE_MODULE));
    public static final Block FUEL_LOADER = new FuelLoaderBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 5.0F).sound(SoundType.METAL));

    public static final AirlockBlock AIR_LOCK_FRAME = new AirlockBlock(false, BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    public static final AirlockBlock AIR_LOCK_CONTROLLER = new AirlockBlock(true, BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    public static final Block AIR_LOCK_SEAL = new Block(BlockBehaviour.Properties.ofFullCopy(AIR_LOCK_FRAME));

    public static Block register(String id, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, Constant.id(id), block);
    }

    public static void register() {
        FlammableBlockRegistry.getDefaultInstance().add(FUEL, 80, 130);
        FlammableBlockRegistry.getDefaultInstance().add(CRUDE_OIL, 60, 100);
        FlammableBlockRegistry.getDefaultInstance().add(CAVERNOUS_VINES, 15, 60);
        FlammableBlockRegistry.getDefaultInstance().add(CAVERNOUS_VINES_PLANT, 15, 60);
        FlattenableBlockRegistry.register(MOON_DIRT, MOON_DIRT_PATH.defaultBlockState());

        // TORCHES
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.GLOWSTONE_WALL_TORCH), GLOWSTONE_WALL_TORCH);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.UNLIT_WALL_TORCH), UNLIT_WALL_TORCH);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.GLOWSTONE_LANTERN), GLOWSTONE_LANTERN);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.UNLIT_LANTERN), UNLIT_LANTERN);

        // DECORATION BLOCKS
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ALUMINUM_DECORATION), ALUMINUM_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ALUMINUM_DECORATION_SLAB), ALUMINUM_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ALUMINUM_DECORATION_WALL), ALUMINUM_DECORATION_WALL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION_SLAB), DETAILED_ALUMINUM_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION_STAIRS), DETAILED_ALUMINUM_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION_WALL), DETAILED_ALUMINUM_DECORATION_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.BRONZE_DECORATION), BRONZE_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.BRONZE_DECORATION_SLAB), BRONZE_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.BRONZE_DECORATION_STAIRS), BRONZE_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.BRONZE_DECORATION_WALL), BRONZE_DECORATION_WALL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION), DETAILED_BRONZE_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION_SLAB), DETAILED_BRONZE_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION_STAIRS), DETAILED_BRONZE_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION_WALL), DETAILED_BRONZE_DECORATION_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.COPPER_DECORATION), COPPER_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.COPPER_DECORATION_SLAB), COPPER_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.COPPER_DECORATION_STAIRS), COPPER_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.COPPER_DECORATION_WALL), COPPER_DECORATION_WALL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION), DETAILED_COPPER_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION_SLAB), DETAILED_COPPER_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION_STAIRS), DETAILED_COPPER_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION_WALL), DETAILED_COPPER_DECORATION_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.IRON_DECORATION), IRON_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.IRON_DECORATION_SLAB), IRON_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.IRON_DECORATION_STAIRS), IRON_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.IRON_DECORATION_WALL), IRON_DECORATION_WALL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_IRON_DECORATION), DETAILED_IRON_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_IRON_DECORATION_SLAB), DETAILED_IRON_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_IRON_DECORATION_STAIRS), DETAILED_IRON_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_IRON_DECORATION_WALL), DETAILED_IRON_DECORATION_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.METEORIC_IRON_DECORATION), METEORIC_IRON_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.METEORIC_IRON_DECORATION_SLAB), METEORIC_IRON_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.METEORIC_IRON_DECORATION_STAIRS), METEORIC_IRON_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.METEORIC_IRON_DECORATION_WALL), METEORIC_IRON_DECORATION_WALL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION), DETAILED_METEORIC_IRON_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION_SLAB), DETAILED_METEORIC_IRON_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION_STAIRS), DETAILED_METEORIC_IRON_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION_WALL), DETAILED_METEORIC_IRON_DECORATION_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.STEEL_DECORATION), STEEL_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.STEEL_DECORATION_SLAB), STEEL_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.STEEL_DECORATION_STAIRS), STEEL_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.STEEL_DECORATION_WALL), STEEL_DECORATION_WALL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION), DETAILED_STEEL_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION_SLAB), DETAILED_STEEL_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION_STAIRS), DETAILED_STEEL_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION_WALL), DETAILED_STEEL_DECORATION_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.TIN_DECORATION), TIN_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.TIN_DECORATION_SLAB), TIN_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.TIN_DECORATION_STAIRS), TIN_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.TIN_DECORATION_WALL), TIN_DECORATION_WALL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_TIN_DECORATION), DETAILED_TIN_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_TIN_DECORATION_SLAB), DETAILED_TIN_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_TIN_DECORATION_STAIRS), DETAILED_TIN_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_TIN_DECORATION_WALL), DETAILED_TIN_DECORATION_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.TITANIUM_DECORATION), TITANIUM_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.TITANIUM_DECORATION_SLAB), TITANIUM_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.TITANIUM_DECORATION_STAIRS), TITANIUM_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.TITANIUM_DECORATION_WALL), TITANIUM_DECORATION_WALL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION), DETAILED_TITANIUM_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION_SLAB), DETAILED_TITANIUM_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION_STAIRS), DETAILED_TITANIUM_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION_WALL), DETAILED_TITANIUM_DECORATION_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DARK_DECORATION), DARK_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DARK_DECORATION_SLAB), DARK_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DARK_DECORATION_STAIRS), DARK_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DARK_DECORATION_WALL), DARK_DECORATION_WALL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_DARK_DECORATION), DETAILED_DARK_DECORATION);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_DARK_DECORATION_SLAB), DETAILED_DARK_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_DARK_DECORATION_STAIRS), DETAILED_DARK_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DETAILED_DARK_DECORATION_WALL), DETAILED_DARK_DECORATION_WALL);

        // MOON NATURAL
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_TURF), MOON_TURF);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_DIRT), MOON_DIRT);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_DIRT_PATH), MOON_DIRT_PATH);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_SURFACE_ROCK), MOON_SURFACE_ROCK);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_ROCK), MOON_ROCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_ROCK_SLAB), MOON_ROCK_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_ROCK_STAIRS), MOON_ROCK_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_ROCK_WALL), MOON_ROCK_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.COBBLED_MOON_ROCK), COBBLED_MOON_ROCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.COBBLED_MOON_ROCK_SLAB), COBBLED_MOON_ROCK_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.COBBLED_MOON_ROCK_STAIRS), COBBLED_MOON_ROCK_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.COBBLED_MOON_ROCK_WALL), COBBLED_MOON_ROCK_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.LUNASLATE), LUNASLATE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.LUNASLATE_SLAB), LUNASLATE_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.LUNASLATE_STAIRS), LUNASLATE_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.LUNASLATE_WALL), LUNASLATE_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.COBBLED_LUNASLATE), COBBLED_LUNASLATE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.COBBLED_LUNASLATE_SLAB), COBBLED_LUNASLATE_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.COBBLED_LUNASLATE_STAIRS), COBBLED_LUNASLATE_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.COBBLED_LUNASLATE_WALL), COBBLED_LUNASLATE_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_BASALT), MOON_BASALT);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_BASALT_SLAB), MOON_BASALT_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_BASALT_STAIRS), MOON_BASALT_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_BASALT_WALL), MOON_BASALT_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_BASALT_BRICK), MOON_BASALT_BRICK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_BASALT_BRICK_SLAB), MOON_BASALT_BRICK_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_BASALT_BRICK_STAIRS), MOON_BASALT_BRICK_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_BASALT_BRICK_WALL), MOON_BASALT_BRICK_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK), CRACKED_MOON_BASALT_BRICK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK_SLAB), CRACKED_MOON_BASALT_BRICK_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK_STAIRS), CRACKED_MOON_BASALT_BRICK_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK_WALL), CRACKED_MOON_BASALT_BRICK_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.FALLEN_METEOR), FALLEN_METEOR);

        // MARS NATURAL
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MARS_SURFACE_ROCK), MARS_SURFACE_ROCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MARS_SUB_SURFACE_ROCK), MARS_SUB_SURFACE_ROCK);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MARS_STONE), MARS_STONE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MARS_STONE_SLAB), MARS_STONE_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MARS_STONE_STAIRS), MARS_STONE_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MARS_STONE_WALL), MARS_STONE_WALL);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MARS_COBBLESTONE), MARS_COBBLESTONE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MARS_COBBLESTONE_SLAB), MARS_COBBLESTONE_SLAB);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MARS_COBBLESTONE_STAIRS), MARS_COBBLESTONE_STAIRS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MARS_COBBLESTONE_WALL), MARS_COBBLESTONE_WALL);

        // ASTEROID NATURAL
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ASTEROID_ROCK), ASTEROID_ROCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ASTEROID_ROCK_1), ASTEROID_ROCK_1);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ASTEROID_ROCK_2), ASTEROID_ROCK_2);

        // VENUS NATURAL
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.SOFT_VENUS_ROCK), SOFT_VENUS_ROCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.HARD_VENUS_ROCK), HARD_VENUS_ROCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.SCORCHED_VENUS_ROCK), SCORCHED_VENUS_ROCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.VOLCANIC_ROCK), VOLCANIC_ROCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.PUMICE), PUMICE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.VAPOR_SPOUT), VAPOR_SPOUT);

        // MISC DECOR
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.WALKWAY), WALKWAY);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.FLUID_PIPE_WALKWAY), FLUID_PIPE_WALKWAY);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.WIRE_WALKWAY), WIRE_WALKWAY);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.TIN_LADDER), TIN_LADDER);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.GRATING), GRATING);

        // SPECIAL
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ALUMINUM_WIRE), ALUMINUM_WIRE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.SEALABLE_ALUMINUM_WIRE), SEALABLE_ALUMINUM_WIRE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.HEAVY_SEALABLE_ALUMINUM_WIRE), HEAVY_SEALABLE_ALUMINUM_WIRE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ROCKET_LAUNCH_PAD), ROCKET_LAUNCH_PAD);

        // LIGHT PANELS
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.SQUARE_LIGHT_PANEL), SQUARE_LIGHT_PANEL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.SPOTLIGHT_LIGHT_PANEL), SPOTLIGHT_LIGHT_PANEL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.LINEAR_LIGHT_PANEL), LINEAR_LIGHT_PANEL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DASHED_LIGHT_PANEL), DASHED_LIGHT_PANEL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DIAGONAL_LIGHT_PANEL), DIAGONAL_LIGHT_PANEL);

        // VACUUM GLASS
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.VACUUM_GLASS), VACUUM_GLASS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.CLEAR_VACUUM_GLASS), CLEAR_VACUUM_GLASS);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.STRONG_VACUUM_GLASS), STRONG_VACUUM_GLASS);

        // ORES
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.SILICON_ORE), SILICON_ORE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DEEPSLATE_SILICON_ORE), DEEPSLATE_SILICON_ORE);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_COPPER_ORE), MOON_COPPER_ORE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.LUNASLATE_COPPER_ORE), LUNASLATE_COPPER_ORE);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.TIN_ORE), TIN_ORE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DEEPSLATE_TIN_ORE), DEEPSLATE_TIN_ORE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_TIN_ORE), MOON_TIN_ORE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.LUNASLATE_TIN_ORE), LUNASLATE_TIN_ORE);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ALUMINUM_ORE), ALUMINUM_ORE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DEEPSLATE_ALUMINUM_ORE), DEEPSLATE_ALUMINUM_ORE);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DESH_ORE), DESH_ORE);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ILMENITE_ORE), ILMENITE_ORE);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.GALENA_ORE), GALENA_ORE);

        // CHEESE BLOCK
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_CHEESE_BLOCK), MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.CANDLE_MOON_CHEESE_BLOCK), CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.WHITE_CANDLE_MOON_CHEESE_BLOCK), WHITE_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ORANGE_CANDLE_MOON_CHEESE_BLOCK), ORANGE_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MAGENTA_CANDLE_MOON_CHEESE_BLOCK), MAGENTA_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.LIGHT_BLUE_CANDLE_MOON_CHEESE_BLOCK), LIGHT_BLUE_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.YELLOW_CANDLE_MOON_CHEESE_BLOCK), YELLOW_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.LIME_CANDLE_MOON_CHEESE_BLOCK), LIME_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.PINK_CANDLE_MOON_CHEESE_BLOCK), PINK_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.GRAY_CANDLE_MOON_CHEESE_BLOCK), GRAY_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.LIGHT_GRAY_CANDLE_MOON_CHEESE_BLOCK), LIGHT_GRAY_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.CYAN_CANDLE_MOON_CHEESE_BLOCK), CYAN_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.PURPLE_CANDLE_MOON_CHEESE_BLOCK), PURPLE_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.BLUE_CANDLE_MOON_CHEESE_BLOCK), BLUE_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.BROWN_CANDLE_MOON_CHEESE_BLOCK), BROWN_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.GREEN_CANDLE_MOON_CHEESE_BLOCK), GREEN_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.RED_CANDLE_MOON_CHEESE_BLOCK), RED_CANDLE_MOON_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.BLACK_CANDLE_MOON_CHEESE_BLOCK), BLACK_CANDLE_MOON_CHEESE_BLOCK);

        // COMPACT MINERAL BLOCKS
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.SILICON_BLOCK), SILICON_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.METEORIC_IRON_BLOCK), METEORIC_IRON_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.DESH_BLOCK), DESH_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.TITANIUM_BLOCK), TITANIUM_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.LEAD_BLOCK), LEAD_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.LUNAR_SAPPHIRE_BLOCK), LUNAR_SAPPHIRE_BLOCK);

        // MOON VILLAGER SPECIAL
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.LUNAR_CARTOGRAPHY_TABLE), LUNAR_CARTOGRAPHY_TABLE);

        // MISC WORLD GEN
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.CAVERNOUS_VINES_PLANT), CAVERNOUS_VINES_PLANT);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.MOON_BERRY_BUSH), MOON_BERRY_BUSH);

        // DUMMY
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.SOLAR_PANEL_PART), SOLAR_PANEL_PART);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.CRYOGENIC_CHAMBER_PART), CRYOGENIC_CHAMBER_PART);

        // MISC MACHINES
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.CRYOGENIC_CHAMBER), CRYOGENIC_CHAMBER);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.PLAYER_TRANSPORT_TUBE), PLAYER_TRANSPORT_TUBE);

        // MACHINES
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.CIRCUIT_FABRICATOR), CIRCUIT_FABRICATOR);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.COMPRESSOR), COMPRESSOR);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ELECTRIC_COMPRESSOR), ELECTRIC_COMPRESSOR);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.COAL_GENERATOR), COAL_GENERATOR);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.BASIC_SOLAR_PANEL), BASIC_SOLAR_PANEL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ADVANCED_SOLAR_PANEL), ADVANCED_SOLAR_PANEL);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ENERGY_STORAGE_MODULE), ENERGY_STORAGE_MODULE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ELECTRIC_FURNACE), ELECTRIC_FURNACE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.ELECTRIC_ARC_FURNACE), ELECTRIC_ARC_FURNACE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.REFINERY), REFINERY);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.OXYGEN_COLLECTOR), OXYGEN_COLLECTOR);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.OXYGEN_SEALER), OXYGEN_SEALER);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.OXYGEN_BUBBLE_DISTRIBUTOR), OXYGEN_BUBBLE_DISTRIBUTOR);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.OXYGEN_DECOMPRESSOR), OXYGEN_DECOMPRESSOR);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.OXYGEN_COMPRESSOR), OXYGEN_COMPRESSOR);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.OXYGEN_STORAGE_MODULE), OXYGEN_STORAGE_MODULE);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.FUEL_LOADER), FUEL_LOADER);

        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.AIR_LOCK_FRAME), AIR_LOCK_FRAME);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.AIR_LOCK_CONTROLLER), AIR_LOCK_CONTROLLER);
        Registry.register(BuiltInRegistries.BLOCK, Constant.id(Constant.Block.AIR_LOCK_SEAL), AIR_LOCK_SEAL);
    }

    private static BlockBehaviour.Properties oreSettings(float hardness, float resistance, boolean deepslate) {
        if (deepslate) {
            return BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).strength(hardness, resistance).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE);
        }
        return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(hardness, resistance).requiresCorrectToolForDrops();
    }

    private static boolean never(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }

    private static ToIntFunction<BlockState> litBlockEmission(int i) {
        return blockState -> blockState.getValue(BlockStateProperties.LIT) ? i : 0;
    }
}