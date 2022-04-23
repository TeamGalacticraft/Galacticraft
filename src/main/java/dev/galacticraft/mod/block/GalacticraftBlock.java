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

package dev.galacticraft.mod.block;

import dev.galacticraft.api.block.MachineBlock;
import dev.galacticraft.mod.Constant;
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
import dev.galacticraft.mod.block.special.aluminumwire.tier2.HeavySealableAluminumWireBlock;
import dev.galacticraft.mod.block.special.fluidpipe.GlassFluidPipeBlock;
import dev.galacticraft.mod.block.special.walkway.PipeWalkway;
import dev.galacticraft.mod.block.special.walkway.WalkwayBlock;
import dev.galacticraft.mod.block.special.walkway.WireWalkway;
import dev.galacticraft.mod.fluid.GalacticraftFluid;
import dev.galacticraft.mod.util.MultiBlockUtil;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricMaterialBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@SuppressWarnings("unused")
public class GalacticraftBlock {
    @ApiStatus.Internal
    public static final Map<Identifier, Block> BLOCKS = new HashMap<>();
    //ITEM GROUPS
    public static final ItemGroup BLOCKS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constant.MOD_ID, Constant.Block.ITEM_GROUP_BLOCKS))
            .icon(() -> new ItemStack(GalacticraftBlock.MOON_TURF)).build();

    public static final ItemGroup MACHINES_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constant.MOD_ID, Constant.Block.ITEM_GROUP_MACHINES))
            .icon(() -> new ItemStack(GalacticraftBlock.COAL_GENERATOR)).build();

    // TORCHES
    public static final Block GLOWSTONE_TORCH = registerBlockWithoutItem(new GlowstoneTorchBlock(FabricBlockSettings.copy(Blocks.TORCH).sounds(BlockSoundGroup.WOOD)), Constant.Block.GLOWSTONE_TORCH);
    public static final Block GLOWSTONE_WALL_TORCH = registerBlockWithoutItem(new GlowstoneWallTorchBlock(FabricBlockSettings.copy(GLOWSTONE_TORCH).dropsLike(GLOWSTONE_TORCH)), Constant.Block.GLOWSTONE_WALL_TORCH);
    public static final Block GLOWSTONE_LANTERN = registerBlock(new GlowstoneLanternBlock(FabricBlockSettings.copy(Blocks.LANTERN)), Constant.Block.GLOWSTONE_LANTERN);
    public static final Block UNLIT_TORCH = registerBlockWithoutItem(new UnlitTorchBlock(FabricBlockSettings.copy(Blocks.TORCH).luminance(state -> 0)), Constant.Block.UNLIT_TORCH);
    public static final Block UNLIT_WALL_TORCH = registerBlockWithoutItem(new UnlitWallTorchBlock(FabricBlockSettings.copy(UNLIT_TORCH).dropsLike(UNLIT_TORCH)), Constant.Block.UNLIT_WALL_TORCH);
    public static final Block UNLIT_LANTERN = registerBlockWithoutItem(new UnlitLanternBlock(FabricBlockSettings.copy(Blocks.LANTERN).luminance(state -> 0)), Constant.Block.UNLIT_LANTERN);

    // LIQUIDS
    public static final FluidBlock CRUDE_OIL = registerBlockWithoutItem(new CrudeOilBlock(GalacticraftFluid.CRUDE_OIL, FabricBlockSettings.of(new FabricMaterialBuilder(MapColor.BLACK)
            .allowsMovement().destroyedByPiston().burnable().lightPassesThrough().notSolid().replaceable().liquid().build())
            .strength(100.0F, 1000.0F).dropsNothing()), Constant.Block.CRUDE_OIL);

    public static final FluidBlock FUEL = registerBlockWithoutItem(new FluidBlock(GalacticraftFluid.FUEL, FabricBlockSettings.of(new FabricMaterialBuilder(MapColor.YELLOW)
            .allowsMovement().destroyedByPiston().burnable().lightPassesThrough().notSolid().replaceable().liquid().build())
            .strength(50.0F, 50.0F).dropsNothing()), Constant.Block.FUEL);

    // DECORATION BLOCKS
    public static final Block ALUMINUM_DECORATION = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(2.0F, 3.0F)), Constant.Block.ALUMINUM_DECORATION);
    public static final Block ALUMINUM_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(ALUMINUM_DECORATION).strength(2.5F, 3.0F)), Constant.Block.ALUMINUM_DECORATION_SLAB);
    public static final Block ALUMINUM_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(ALUMINUM_DECORATION)), Constant.Block.ALUMINUM_DECORATION_STAIRS);
    public static final Block ALUMINUM_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(ALUMINUM_DECORATION)), Constant.Block.ALUMINUM_DECORATION_WALL);
    public static final Block DETAILED_ALUMINUM_DECORATION = registerBlock(new Block(FabricBlockSettings.copyOf(ALUMINUM_DECORATION)), Constant.Block.DETAILED_ALUMINUM_DECORATION);
    public static final Block DETAILED_ALUMINUM_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_ALUMINUM_DECORATION).strength(2.5F, 3.0F)), Constant.Block.DETAILED_ALUMINUM_DECORATION_SLAB);
    public static final Block DETAILED_ALUMINUM_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_ALUMINUM_DECORATION)), Constant.Block.DETAILED_ALUMINUM_DECORATION_STAIRS);
    public static final Block DETAILED_ALUMINUM_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_ALUMINUM_DECORATION)), Constant.Block.DETAILED_ALUMINUM_DECORATION_WALL);

    public static final Block BRONZE_DECORATION = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(2.0F, 3.0F)), Constant.Block.BRONZE_DECORATION);
    public static final Block BRONZE_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(BRONZE_DECORATION).strength(2.5F, 3.0F)), Constant.Block.BRONZE_DECORATION_SLAB);
    public static final Block BRONZE_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(BRONZE_DECORATION)), Constant.Block.BRONZE_DECORATION_STAIRS);
    public static final Block BRONZE_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(BRONZE_DECORATION)), Constant.Block.BRONZE_DECORATION_WALL);
    public static final Block DETAILED_BRONZE_DECORATION = registerBlock(new Block(FabricBlockSettings.copyOf(BRONZE_DECORATION)), Constant.Block.DETAILED_BRONZE_DECORATION);
    public static final Block DETAILED_BRONZE_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_BRONZE_DECORATION).strength(2.5F, 3.0F)), Constant.Block.DETAILED_BRONZE_DECORATION_SLAB);
    public static final Block DETAILED_BRONZE_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_BRONZE_DECORATION)), Constant.Block.DETAILED_BRONZE_DECORATION_STAIRS);
    public static final Block DETAILED_BRONZE_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_BRONZE_DECORATION)), Constant.Block.DETAILED_BRONZE_DECORATION_WALL);

    public static final Block COPPER_DECORATION = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(2.0F, 3.0F)), Constant.Block.COPPER_DECORATION);
    public static final Block COPPER_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(COPPER_DECORATION).strength(2.5F, 3.0F)), Constant.Block.COPPER_DECORATION_SLAB);
    public static final Block COPPER_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(COPPER_DECORATION)), Constant.Block.COPPER_DECORATION_STAIRS);
    public static final Block COPPER_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(COPPER_DECORATION)), Constant.Block.COPPER_DECORATION_WALL);
    public static final Block DETAILED_COPPER_DECORATION = registerBlock(new Block(FabricBlockSettings.copyOf(COPPER_DECORATION)), Constant.Block.DETAILED_COPPER_DECORATION);
    public static final Block DETAILED_COPPER_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_COPPER_DECORATION).strength(2.5F, 3.0F)), Constant.Block.DETAILED_COPPER_DECORATION_SLAB);
    public static final Block DETAILED_COPPER_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_COPPER_DECORATION)), Constant.Block.DETAILED_COPPER_DECORATION_STAIRS);
    public static final Block DETAILED_COPPER_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_COPPER_DECORATION)), Constant.Block.DETAILED_COPPER_DECORATION_WALL);

    public static final Block IRON_DECORATION = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(2.0F, 3.0F)), Constant.Block.IRON_DECORATION);
    public static final Block IRON_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(IRON_DECORATION).strength(2.5F, 3.0F)), Constant.Block.IRON_DECORATION_SLAB);
    public static final Block IRON_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(IRON_DECORATION)), Constant.Block.IRON_DECORATION_STAIRS);
    public static final Block IRON_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(IRON_DECORATION)), Constant.Block.IRON_DECORATION_WALL);
    public static final Block DETAILED_IRON_DECORATION = registerBlock(new Block(FabricBlockSettings.copyOf(IRON_DECORATION)), Constant.Block.DETAILED_IRON_DECORATION);
    public static final Block DETAILED_IRON_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_IRON_DECORATION).strength(2.5F, 3.0F)), Constant.Block.DETAILED_IRON_DECORATION_SLAB);
    public static final Block DETAILED_IRON_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_IRON_DECORATION)), Constant.Block.DETAILED_IRON_DECORATION_STAIRS);
    public static final Block DETAILED_IRON_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_IRON_DECORATION)), Constant.Block.DETAILED_IRON_DECORATION_WALL);

    public static final Block METEORIC_IRON_DECORATION = registerBlock(new Block(FabricBlockSettings.copyOf(IRON_DECORATION)), Constant.Block.METEORIC_IRON_DECORATION);
    public static final Block METEORIC_IRON_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(METEORIC_IRON_DECORATION).strength(2.5F, 3.0F)), Constant.Block.METEORIC_IRON_DECORATION_SLAB);
    public static final Block METEORIC_IRON_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(METEORIC_IRON_DECORATION)), Constant.Block.METEORIC_IRON_DECORATION_STAIRS);
    public static final Block METEORIC_IRON_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(METEORIC_IRON_DECORATION)), Constant.Block.METEORIC_IRON_DECORATION_WALL);
    public static final Block DETAILED_METEORIC_IRON_DECORATION = registerBlock(new Block(FabricBlockSettings.copyOf(METEORIC_IRON_DECORATION)), Constant.Block.DETAILED_METEORIC_IRON_DECORATION);
    public static final Block DETAILED_METEORIC_IRON_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_METEORIC_IRON_DECORATION).strength(2.5F, 3.0F)), Constant.Block.DETAILED_METEORIC_IRON_DECORATION_SLAB);
    public static final Block DETAILED_METEORIC_IRON_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_METEORIC_IRON_DECORATION)), Constant.Block.DETAILED_METEORIC_IRON_DECORATION_STAIRS);
    public static final Block DETAILED_METEORIC_IRON_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_METEORIC_IRON_DECORATION)), Constant.Block.DETAILED_METEORIC_IRON_DECORATION_WALL);

    public static final Block STEEL_DECORATION = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(2.0F, 3.0F)), Constant.Block.STEEL_DECORATION);
    public static final Block STEEL_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(STEEL_DECORATION).strength(2.5F, 3.0F)), Constant.Block.STEEL_DECORATION_SLAB);
    public static final Block STEEL_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(STEEL_DECORATION)), Constant.Block.STEEL_DECORATION_STAIRS);
    public static final Block STEEL_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(STEEL_DECORATION)), Constant.Block.STEEL_DECORATION_WALL);
    public static final Block DETAILED_STEEL_DECORATION = registerBlock(new Block(FabricBlockSettings.copyOf(STEEL_DECORATION)), Constant.Block.DETAILED_STEEL_DECORATION);
    public static final Block DETAILED_STEEL_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_STEEL_DECORATION).strength(2.5F, 3.0F)), Constant.Block.DETAILED_STEEL_DECORATION_SLAB);
    public static final Block DETAILED_STEEL_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_STEEL_DECORATION)), Constant.Block.DETAILED_STEEL_DECORATION_STAIRS);
    public static final Block DETAILED_STEEL_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_STEEL_DECORATION)), Constant.Block.DETAILED_STEEL_DECORATION_WALL);

    public static final Block TIN_DECORATION = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(2.0F, 3.0F)), Constant.Block.TIN_DECORATION);
    public static final Block TIN_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(TIN_DECORATION).strength(2.5F, 3.0F)), Constant.Block.TIN_DECORATION_SLAB);
    public static final Block TIN_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(TIN_DECORATION)), Constant.Block.TIN_DECORATION_STAIRS);
    public static final Block TIN_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(TIN_DECORATION)), Constant.Block.TIN_DECORATION_WALL);
    public static final Block DETAILED_TIN_DECORATION = registerBlock(new Block(FabricBlockSettings.copyOf(TIN_DECORATION)), Constant.Block.DETAILED_TIN_DECORATION);
    public static final Block DETAILED_TIN_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_TIN_DECORATION).strength(2.5F, 3.0F)), Constant.Block.DETAILED_TIN_DECORATION_SLAB);
    public static final Block DETAILED_TIN_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_TIN_DECORATION)), Constant.Block.DETAILED_TIN_DECORATION_STAIRS);
    public static final Block DETAILED_TIN_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_TIN_DECORATION)), Constant.Block.DETAILED_TIN_DECORATION_WALL);

    public static final Block TITANIUM_DECORATION = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(2.0F, 3.0F)), Constant.Block.TITANIUM_DECORATION);
    public static final Block TITANIUM_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(TITANIUM_DECORATION).strength(2.5F, 3.0F)), Constant.Block.TITANIUM_DECORATION_SLAB);
    public static final Block TITANIUM_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(TITANIUM_DECORATION)), Constant.Block.TITANIUM_DECORATION_STAIRS);
    public static final Block TITANIUM_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(TITANIUM_DECORATION)), Constant.Block.TITANIUM_DECORATION_WALL);
    public static final Block DETAILED_TITANIUM_DECORATION = registerBlock(new Block(FabricBlockSettings.copyOf(TITANIUM_DECORATION)), Constant.Block.DETAILED_TITANIUM_DECORATION);
    public static final Block DETAILED_TITANIUM_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_TITANIUM_DECORATION).strength(2.5F, 3.0F)), Constant.Block.DETAILED_TITANIUM_DECORATION_SLAB);
    public static final Block DETAILED_TITANIUM_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_TITANIUM_DECORATION)), Constant.Block.DETAILED_TITANIUM_DECORATION_STAIRS);
    public static final Block DETAILED_TITANIUM_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_TITANIUM_DECORATION)), Constant.Block.DETAILED_TITANIUM_DECORATION_WALL);

    public static final Block DARK_DECORATION = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.STONE_GRAY).strength(2.0F, 3.0F)), Constant.Block.DARK_DECORATION);
    public static final Block DARK_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DARK_DECORATION).strength(2.5F, 3.0F)), Constant.Block.DARK_DECORATION_SLAB);
    public static final Block DARK_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DARK_DECORATION)), Constant.Block.DARK_DECORATION_STAIRS);
    public static final Block DARK_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DARK_DECORATION)), Constant.Block.DARK_DECORATION_WALL);
    public static final Block DETAILED_DARK_DECORATION = registerBlock(new Block(FabricBlockSettings.copyOf(DARK_DECORATION)), Constant.Block.DETAILED_DARK_DECORATION);
    public static final Block DETAILED_DARK_DECORATION_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_DARK_DECORATION).strength(2.5F, 3.0F)), Constant.Block.DETAILED_DARK_DECORATION_SLAB);
    public static final Block DETAILED_DARK_DECORATION_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_DARK_DECORATION)), Constant.Block.DETAILED_DARK_DECORATION_STAIRS);
    public static final Block DETAILED_DARK_DECORATION_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(DETAILED_DARK_DECORATION)), Constant.Block.DETAILED_DARK_DECORATION_WALL);

    // MOON NATURAL
    public static final Block MOON_TURF = registerBlock(new Block(FabricBlockSettings.of(Material.SOLID_ORGANIC, MapColor.LIGHT_GRAY).strength(0.5F, 0.5F)), Constant.Block.MOON_TURF);
    public static final Block MOON_DIRT = registerBlock(new Block(FabricBlockSettings.of(Material.SOIL, MapColor.LIGHT_GRAY).strength(0.5F, 0.5F).sounds(BlockSoundGroup.GRAVEL)), Constant.Block.MOON_DIRT);
    public static final Block MOON_DIRT_PATH = registerBlock(new MoonDirtPathBlock(FabricBlockSettings.copyOf(MOON_DIRT).strength(0.5F, 0.5F)), Constant.Block.MOON_DIRT_PATH);
    public static final Block MOON_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.GRAY).strength(1.5F, 6.0F)), Constant.Block.MOON_SURFACE_ROCK);

    public static final Block MOON_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.GRAY).strength(2.0F, 6.0F)), Constant.Block.MOON_ROCK);
    public static final Block MOON_ROCK_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(MOON_ROCK).strength(2.5F, 6.0F)), Constant.Block.MOON_ROCK_SLAB);
    public static final Block MOON_ROCK_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(MOON_ROCK)), Constant.Block.MOON_ROCK_STAIRS);
    public static final Block MOON_ROCK_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(MOON_ROCK)), Constant.Block.MOON_ROCK_WALL);

    public static final Block COBBLED_MOON_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.GRAY).strength(2.0F, 6.0F)), Constant.Block.COBBLED_MOON_ROCK);
    public static final Block COBBLED_MOON_ROCK_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(COBBLED_MOON_ROCK).strength(2.5F, 6.0F)), Constant.Block.COBBLED_MOON_ROCK_SLAB);
    public static final Block COBBLED_MOON_ROCK_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(COBBLED_MOON_ROCK)), Constant.Block.COBBLED_MOON_ROCK_STAIRS);
    public static final Block COBBLED_MOON_ROCK_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(COBBLED_MOON_ROCK)), Constant.Block.COBBLED_MOON_ROCK_WALL);

    public static final Block LUNASLATE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.DEEPSLATE_GRAY).strength(3.5F, 6.0F)), Constant.Block.LUNASLATE);
    public static final Block LUNASLATE_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(LUNASLATE).strength(4.0F, 6.0F)), Constant.Block.LUNASLATE_SLAB);
    public static final Block LUNASLATE_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(LUNASLATE)), Constant.Block.LUNASLATE_STAIRS);
    public static final Block LUNASLATE_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(LUNASLATE)), Constant.Block.LUNASLATE_WALL);

    public static final Block COBBLED_LUNASLATE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.DEEPSLATE_GRAY).strength(3.5F, 6.0F)), Constant.Block.COBBLED_LUNASLATE);
    public static final Block COBBLED_LUNASLATE_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(COBBLED_LUNASLATE).strength(4.0F, 6.0F)), Constant.Block.COBBLED_LUNASLATE_SLAB);
    public static final Block COBBLED_LUNASLATE_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(COBBLED_LUNASLATE)), Constant.Block.COBBLED_LUNASLATE_STAIRS);
    public static final Block COBBLED_LUNASLATE_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(COBBLED_LUNASLATE)), Constant.Block.COBBLED_LUNASLATE_WALL);

    public static final Block MOON_BASALT = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.TERRACOTTA_BLACK).strength(2.0F, 6.0F)), Constant.Block.MOON_BASALT);
    public static final Block MOON_BASALT_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(MOON_BASALT).strength(2.5F, 6.0F)), Constant.Block.MOON_BASALT_SLAB);
    public static final Block MOON_BASALT_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(MOON_BASALT)), Constant.Block.MOON_BASALT_STAIRS);
    public static final Block MOON_BASALT_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(MOON_BASALT)), Constant.Block.MOON_BASALT_WALL);

    public static final Block MOON_BASALT_BRICK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.TERRACOTTA_BLACK).strength(2.5F, 6.0F)), Constant.Block.MOON_BASALT_BRICK);
    public static final Block MOON_BASALT_BRICK_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(MOON_BASALT_BRICK).strength(3.0F, 6.0F)), Constant.Block.MOON_BASALT_BRICK_SLAB);
    public static final Block MOON_BASALT_BRICK_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(MOON_BASALT_BRICK)), Constant.Block.MOON_BASALT_BRICK_STAIRS);
    public static final Block MOON_BASALT_BRICK_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(MOON_BASALT_BRICK)), Constant.Block.MOON_BASALT_BRICK_WALL);

    public static final Block CRACKED_MOON_BASALT_BRICK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.TERRACOTTA_BLACK).strength(2.0F, 6.0F)), Constant.Block.CRACKED_MOON_BASALT_BRICK);
    public static final Block CRACKED_MOON_BASALT_BRICK_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(CRACKED_MOON_BASALT_BRICK).strength(2.5F, 6.0F)), Constant.Block.CRACKED_MOON_BASALT_BRICK_SLAB);
    public static final Block CRACKED_MOON_BASALT_BRICK_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(CRACKED_MOON_BASALT_BRICK)), Constant.Block.CRACKED_MOON_BASALT_BRICK_STAIRS);
    public static final Block CRACKED_MOON_BASALT_BRICK_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(CRACKED_MOON_BASALT_BRICK)), Constant.Block.CRACKED_MOON_BASALT_BRICK_WALL);

    // MARS NATURAL
    public static final Block MARS_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.DIRT_BROWN).hardness(2.2F)), Constant.Block.MARS_SURFACE_ROCK);
    public static final Block MARS_SUB_SURFACE_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.RED).hardness(2.6F)), Constant.Block.MARS_SUB_SURFACE_ROCK);
    public static final Block MARS_STONE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.RED).hardness(3.0F)), Constant.Block.MARS_STONE);
    public static final Block MARS_COBBLESTONE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.RED).hardness(3.0F)), Constant.Block.MARS_COBBLESTONE);
    public static final Block MARS_COBBLESTONE_SLAB = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(MARS_COBBLESTONE).strength(3.5F, 6.0F)), Constant.Block.MARS_COBBLESTONE_SLAB);
    public static final Block MARS_COBBLESTONE_STAIRS = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(MARS_COBBLESTONE)), Constant.Block.MARS_COBBLESTONE_STAIRS);
    public static final Block MARS_COBBLESTONE_WALL = registerBlock(new SlabBlock(FabricBlockSettings.copyOf(MARS_COBBLESTONE)), Constant.Block.MARS_COBBLESTONE_WALL);

    // ASTEROID NATURAL
    public static final Block ASTEROID_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.BROWN).hardness(3.0F)), Constant.Block.ASTEROID_ROCK);
    public static final Block ASTEROID_ROCK_1 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.BROWN).hardness(3.0F)), Constant.Block.ASTEROID_ROCK_1);
    public static final Block ASTEROID_ROCK_2 = registerBlock(new Block(FabricBlockSettings.of(Material.STONE, MapColor.BROWN).hardness(3.0F)), Constant.Block.ASTEROID_ROCK_2);

    // VENUS NATURAL
    public static final Block SOFT_VENUS_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F)), Constant.Block.SOFT_VENUS_ROCK);
    public static final Block HARD_VENUS_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F)), Constant.Block.HARD_VENUS_ROCK);
    public static final Block SCORCHED_VENUS_ROCK = registerBlock(new ScorchedRockBlock(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F)), Constant.Block.SCORCHED_VENUS_ROCK);
    public static final Block VOLCANIC_ROCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(2.2F, 0.5F)), Constant.Block.VOLCANIC_ROCK);
    public static final Block PUMICE = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(1.5F, 6.0F)), Constant.Block.PUMICE);
    public static final Block VAPOR_SPOUT = registerBlock(new VaporSpoutBlock(FabricBlockSettings.of(Material.STONE, MapColor.BROWN).dropsNothing().strength(1.5F, 2.0F)), Constant.Block.VAPOR_SPOUT);

    // MISC DECOR
    public static final Block WALKWAY = registerBlock(new WalkwayBlock(FabricBlockSettings.of(Material.METAL).strength(5.0f, 5.0f).sounds(BlockSoundGroup.METAL)), Constant.Block.WALKWAY);
    public static final Block PIPE_WALKWAY = registerBlock(new PipeWalkway(FabricBlockSettings.copyOf(WALKWAY)), Constant.Block.PIPE_WALKWAY);
    public static final Block WIRE_WALKWAY = registerBlock(new WireWalkway(FabricBlockSettings.copyOf(WALKWAY)), Constant.Block.WIRE_WALKWAY);
    public static final Block TIN_LADDER = registerBlock(new TinLadderBlock(FabricBlockSettings.of(Material.DECORATION).strength(1.0f, 1.0f).sounds(BlockSoundGroup.METAL)), Constant.Block.TIN_LADDER);
    public static final Block GRATING = registerBlock(new GratingBlock(FabricBlockSettings.of(Material.METAL, MapColor.STONE_GRAY).strength(2.5f, 6.0f).sounds(BlockSoundGroup.METAL)), Constant.Block.GRATING);

    // SPECIAL
    public static final Block ALUMINUM_WIRE = registerBlock(new AluminumWireBlock(FabricBlockSettings.copy(Blocks.WHITE_WOOL)), Constant.Block.WIRE_T1);
    public static final Block SEALABLE_ALUMINUM_WIRE = registerBlock(new SealableAluminumWireBlock(FabricBlockSettings.copy(TIN_DECORATION)), Constant.Block.SEALABLE_ALUMINUM_WIRE);
    public static final Block HEAVY_SEALABLE_ALUMINUM_WIRE = registerBlock(new HeavySealableAluminumWireBlock(FabricBlockSettings.copy(TIN_DECORATION)), Constant.Block.HEAVY_SEALABLE_ALUMINUM_WIRE);
    public static final Block GLASS_FLUID_PIPE = registerBlock(new GlassFluidPipeBlock(FabricBlockSettings.of(Material.GLASS).breakByHand(true).sounds(BlockSoundGroup.GLASS)), Constant.Block.GLASS_FLUID_PIPE);

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
    public static final Block SILICON_ORE = registerBlock(new OreBlock(oreSettings(3.0F, 3.0F, false)), Constant.Block.SILICON_ORE);
    public static final Block DEEPSLATE_SILICON_ORE = registerBlock(new OreBlock(oreSettings(4.5F, 3.0F, true)), Constant.Block.DEEPSLATE_SILICON_ORE);

    public static final Block MOON_COPPER_ORE = registerBlock(new OreBlock(oreSettings(3.0F, 5.0F, false)), Constant.Block.MOON_COPPER_ORE);
    public static final Block LUNASLATE_COPPER_ORE = registerBlock(new OreBlock(oreSettings(5.0F, 5.0F, true)), Constant.Block.LUNASLATE_COPPER_ORE);

    public static final Block TIN_ORE = registerBlock(new OreBlock(oreSettings(3.0F, 3.0F, false)), Constant.Block.TIN_ORE);
    public static final Block DEEPSLATE_TIN_ORE = registerBlock(new OreBlock(oreSettings(4.5F, 3.0F, true)), Constant.Block.DEEPSLATE_TIN_ORE);
    public static final Block MOON_TIN_ORE = registerBlock(new OreBlock(oreSettings(3.0F, 5.0F, false)), Constant.Block.MOON_TIN_ORE);
    public static final Block LUNASLATE_TIN_ORE = registerBlock(new OreBlock(oreSettings(5.0F, 5.0F, true)), Constant.Block.LUNASLATE_TIN_ORE);

    public static final Block ALUMINUM_ORE = registerBlock(new OreBlock(oreSettings(3.0F, 3.0F, false)), Constant.Block.ALUMINUM_ORE);
    public static final Block DEEPSLATE_ALUMINUM_ORE = registerBlock(new OreBlock(oreSettings(3.5F, 3.0F, true)), Constant.Block.DEEPSLATE_ALUMINUM_ORE);

    public static final Block DESH_ORE = registerBlock(new OreBlock(oreSettings(3.0F, 5.0F, false)), Constant.Block.DESH_ORE);

    public static final Block ILMENITE_ORE = registerBlock(new OreBlock(oreSettings(3.0F, 5.0F, false)), Constant.Block.ILMENITE_ORE);

    public static final Block GALENA_ORE = registerBlock(new OreBlock(oreSettings(3.0F, 5.0F, false)), Constant.Block.GALENA_ORE);

    // COMPACT MINERAL BLOCKS
    public static final Block MOON_CHEESE_BLOCK = registerBlock(new CakeBlock(FabricBlockSettings.of(Material.CAKE).strength(0.5F).sounds(BlockSoundGroup.WOOL)), Constant.Block.MOON_CHEESE_BLOCK);
    public static final Block SILICON_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constant.Block.SILICON_BLOCK);
    public static final Block METEORIC_IRON_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constant.Block.METEORIC_IRON_BLOCK);
    public static final Block DESH_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constant.Block.DESH_BLOCK);
    public static final Block TITANIUM_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constant.Block.TITANIUM_BLOCK);
    public static final Block LEAD_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), Constant.Block.LEAD_BLOCK);
    public static final Block LUNAR_SAPPHIRE_BLOCK = registerBlock(new Block(FabricBlockSettings.of(Material.STONE).strength(5.0F, 6.0F).sounds(BlockSoundGroup.STONE)), Constant.Block.LUNAR_SAPPHIRE_BLOCK);

    // MOON VILLAGER SPECIAL
    public static final Block LUNAR_CARTOGRAPHY_TABLE = registerBlock(new LunarCartographyTableBlock(FabricBlockSettings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)), Constant.Block.LUNAR_CARTOGRAPHY_TABLE);

    // MISC WORLD GEN
    public static final Block CAVERNOUS_VINE = registerBlock(new CavernousVineBlock(FabricBlockSettings.of(Material.CACTUS, MapColor.GREEN).dropsNothing().noCollision().luminance(0).sounds(BlockSoundGroup.GRASS).ticksRandomly()), Constant.Block.CAVERNOUS_VINE);
    public static final Block POISONOUS_CAVERNOUS_VINE = registerBlock(new PoisonousCavernousVineBlock(FabricBlockSettings.of(Material.CACTUS, MapColor.GREEN).dropsNothing().noCollision().luminance(3).sounds(BlockSoundGroup.GRASS).ticksRandomly()), Constant.Block.POISONOUS_CAVERNOUS_VINE);
    public static final Block MOON_BERRY_BUSH = registerBlock(new MoonBerryBushBlock(FabricBlockSettings.of(Material.PLANT, MapColor.GREEN).dropsNothing().noCollision().luminance(3).sounds(BlockSoundGroup.SWEET_BERRY_BUSH).ticksRandomly()), Constant.Block.MOON_BERRY_BUSH);

    // DUMMY
    public static final BlockWithEntity SOLAR_PANEL_PART = registerBlockWithoutItem(new SolarPanelPartBlock(FabricBlockSettings.of(Material.METAL).strength(-1.0F, 5.0F).dropsNothing().sounds(BlockSoundGroup.METAL)), Constant.Block.SOLAR_PANEL_PART);

    // MACHINES
    public static final MachineBlock<CircuitFabricatorBlockEntity> CIRCUIT_FABRICATOR = registerMachine(SimpleMachineBlock.create(CircuitFabricatorBlockEntity::new), Constant.Block.CIRCUIT_FABRICATOR);
    public static final MachineBlock<CompressorBlockEntity> COMPRESSOR = registerMachine(SimpleMachineBlock.create(CompressorBlockEntity::new), Constant.Block.COMPRESSOR);
    public static final MachineBlock<ElectricCompressorBlockEntity> ELECTRIC_COMPRESSOR = registerMachine(SimpleMachineBlock.create(ElectricCompressorBlockEntity::new), Constant.Block.ELECTRIC_COMPRESSOR);
    public static final MachineBlock<CoalGeneratorBlockEntity> COAL_GENERATOR = registerMachine(new CoalGeneratorBlock(FabricBlockSettings.copyOf(SimpleMachineBlock.MACHINE_DEFAULT_SETTINGS).luminance(state -> state.get(MachineBlock.ACTIVE) ? 13 : 0)), Constant.Block.COAL_GENERATOR);
    public static final MachineBlock<BasicSolarPanelBlockEntity> BASIC_SOLAR_PANEL = registerMachine(SimpleMultiBlockMachineBlock.create(BasicSolarPanelBlockEntity::new, MultiBlockUtil.generateSolarPanelParts(), GalacticraftBlock.SOLAR_PANEL_PART), Constant.Block.BASIC_SOLAR_PANEL);
    public static final MachineBlock<AdvancedSolarPanelBlockEntity> ADVANCED_SOLAR_PANEL = registerMachine(SimpleMultiBlockMachineBlock.create(AdvancedSolarPanelBlockEntity::new, MultiBlockUtil.generateSolarPanelParts(), GalacticraftBlock.SOLAR_PANEL_PART), Constant.Block.ADVANCED_SOLAR_PANEL);
    public static final MachineBlock<EnergyStorageModuleBlockEntity> ENERGY_STORAGE_MODULE = registerMachine(SimpleMachineBlock.create(EnergyStorageModuleBlockEntity::new), Constant.Block.ENERGY_STORAGE_MODULE);
    public static final MachineBlock<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = registerMachine(SimpleMachineBlock.create(ElectricFurnaceBlockEntity::new), Constant.Block.ELECTRIC_FURNACE);
    public static final MachineBlock<ElectricArcFurnaceBlockEntity> ELECTRIC_ARC_FURNACE = registerMachine(SimpleMachineBlock.create(ElectricArcFurnaceBlockEntity::new), Constant.Block.ELECTRIC_ARC_FURNACE);
    public static final MachineBlock<RefineryBlockEntity> REFINERY = registerMachine(new RefineryBlock(FabricBlockSettings.copyOf(SimpleMachineBlock.MACHINE_DEFAULT_SETTINGS)), Constant.Block.REFINERY);
    public static final MachineBlock<OxygenCollectorBlockEntity> OXYGEN_COLLECTOR = registerMachine(new OxygenCollectorBlock(FabricBlockSettings.copyOf(SimpleMachineBlock.MACHINE_DEFAULT_SETTINGS)), Constant.Block.OXYGEN_COLLECTOR);
    public static final MachineBlock<OxygenSealerBlockEntity> OXYGEN_SEALER = registerMachine(SimpleMachineBlock.create(OxygenSealerBlockEntity::new), Constant.Block.OXYGEN_SEALER);
    public static final MachineBlock<BubbleDistributorBlockEntity> BUBBLE_DISTRIBUTOR = registerMachine(SimpleMachineBlock.create(BubbleDistributorBlockEntity::new), Constant.Block.OXYGEN_BUBBLE_DISTRIBUTOR);
    public static final MachineBlock<OxygenDecompressorBlockEntity> OXYGEN_DECOMPRESSOR = registerMachine(SimpleMachineBlock.create(OxygenDecompressorBlockEntity::new), Constant.Block.OXYGEN_DECOMPRESSOR);
    public static final MachineBlock<OxygenCompressorBlockEntity> OXYGEN_COMPRESSOR = registerMachine(SimpleMachineBlock.create(OxygenCompressorBlockEntity::new), Constant.Block.OXYGEN_COMPRESSOR);
    public static final MachineBlock<OxygenStorageModuleBlockEntity> OXYGEN_STORAGE_MODULE = registerMachine(SimpleMachineBlock.create(OxygenStorageModuleBlockEntity::new), Constant.Block.OXYGEN_STORAGE_MODULE);

    public static void register() {
        FlammableBlockRegistry.getDefaultInstance().add(FUEL, 80, 130);
        FlammableBlockRegistry.getDefaultInstance().add(CRUDE_OIL, 60, 100);
        FlattenableBlockRegistry.register(MOON_DIRT, MOON_DIRT_PATH.getDefaultState());
        BLOCKS.forEach((identifier, block) -> Registry.register(Registry.BLOCK, identifier, block));
    }

    private static FabricBlockSettings oreSettings(float hardness, float resistance, boolean deepslate) {
        if (deepslate) return FabricBlockSettings.of(Material.STONE, MapColor.DEEPSLATE_GRAY).strength(hardness, resistance).requiresTool().sounds(BlockSoundGroup.DEEPSLATE);
        return FabricBlockSettings.of(Material.STONE).strength(hardness, resistance).requiresTool();
    }

    private static <T extends Block> T registerBlock(T block, String id) {
        return registerBlock(block, id, BLOCKS_GROUP);
    }

    private static <T extends Block> T registerMachine(T block, String id) {
        return registerBlock(block, id, MACHINES_GROUP);
    }

    private static <T extends Block> T registerBlock(T block, String id, ItemGroup group) {
        Identifier identifier = new Identifier(Constant.MOD_ID, id);
        BLOCKS.put(identifier, block);
        BlockItem item = Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings().group(group)));
        item.appendBlocks(Item.BLOCK_ITEMS, item);
        return block;
    }

    private static <T extends Block> T registerBlockWithoutItem(T block, String id) {
        BLOCKS.put(new Identifier(Constant.MOD_ID, id), block);
        return block;
    }
}