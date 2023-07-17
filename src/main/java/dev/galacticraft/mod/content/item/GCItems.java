/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.content.item;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCFluids;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@SuppressWarnings("unused")
public class GCItems {
    public static final List<ItemLike> HIDDEN_ITEMS = new ArrayList<>(1);

    // === START BLOCKS ===
    // TORCHES
    public static final Item GLOWSTONE_TORCH = new StandingAndWallBlockItem(GCBlocks.GLOWSTONE_TORCH, GCBlocks.GLOWSTONE_WALL_TORCH, new Item.Properties(), Direction.DOWN);
    public static final Item UNLIT_TORCH = new StandingAndWallBlockItem(GCBlocks.UNLIT_TORCH, GCBlocks.UNLIT_WALL_TORCH, new Item.Properties(), Direction.DOWN);

    // LANTERNS
    public static final Item GLOWSTONE_LANTERN = new BlockItem(GCBlocks.GLOWSTONE_LANTERN, new Item.Properties());

    // DECORATION BLOCKS
    public static final Item ALUMINUM_DECORATION = new BlockItem(GCBlocks.ALUMINUM_DECORATION, new Item.Properties());
    public static final Item ALUMINUM_DECORATION_SLAB = new BlockItem(GCBlocks.ALUMINUM_DECORATION_SLAB, new Item.Properties());
    public static final Item ALUMINUM_DECORATION_STAIRS = new BlockItem(GCBlocks.ALUMINUM_DECORATION_STAIRS, new Item.Properties());
    public static final Item ALUMINUM_DECORATION_WALL = new BlockItem(GCBlocks.ALUMINUM_DECORATION_WALL, new Item.Properties());
    public static final Item DETAILED_ALUMINUM_DECORATION = new BlockItem(GCBlocks.DETAILED_ALUMINUM_DECORATION, new Item.Properties());
    public static final Item DETAILED_ALUMINUM_DECORATION_SLAB = new BlockItem(GCBlocks.DETAILED_ALUMINUM_DECORATION_SLAB, new Item.Properties());
    public static final Item DETAILED_ALUMINUM_DECORATION_STAIRS = new BlockItem(GCBlocks.DETAILED_ALUMINUM_DECORATION_STAIRS, new Item.Properties());
    public static final Item DETAILED_ALUMINUM_DECORATION_WALL = new BlockItem(GCBlocks.DETAILED_ALUMINUM_DECORATION_WALL, new Item.Properties());

    public static final Item BRONZE_DECORATION = new BlockItem(GCBlocks.BRONZE_DECORATION, new Item.Properties());
    public static final Item BRONZE_DECORATION_SLAB = new BlockItem(GCBlocks.BRONZE_DECORATION_SLAB, new Item.Properties());
    public static final Item BRONZE_DECORATION_STAIRS = new BlockItem(GCBlocks.BRONZE_DECORATION_STAIRS, new Item.Properties());
    public static final Item BRONZE_DECORATION_WALL = new BlockItem(GCBlocks.BRONZE_DECORATION_WALL, new Item.Properties());
    public static final Item DETAILED_BRONZE_DECORATION = new BlockItem(GCBlocks.DETAILED_BRONZE_DECORATION, new Item.Properties());
    public static final Item DETAILED_BRONZE_DECORATION_SLAB = new BlockItem(GCBlocks.DETAILED_BRONZE_DECORATION_SLAB, new Item.Properties());
    public static final Item DETAILED_BRONZE_DECORATION_STAIRS = new BlockItem(GCBlocks.DETAILED_BRONZE_DECORATION_STAIRS, new Item.Properties());
    public static final Item DETAILED_BRONZE_DECORATION_WALL = new BlockItem(GCBlocks.DETAILED_BRONZE_DECORATION_WALL, new Item.Properties());

    public static final Item COPPER_DECORATION = new BlockItem(GCBlocks.COPPER_DECORATION, new Item.Properties());
    public static final Item COPPER_DECORATION_SLAB = new BlockItem(GCBlocks.COPPER_DECORATION_SLAB, new Item.Properties());
    public static final Item COPPER_DECORATION_STAIRS = new BlockItem(GCBlocks.COPPER_DECORATION_STAIRS, new Item.Properties());
    public static final Item COPPER_DECORATION_WALL = new BlockItem(GCBlocks.COPPER_DECORATION_WALL, new Item.Properties());
    public static final Item DETAILED_COPPER_DECORATION = new BlockItem(GCBlocks.DETAILED_COPPER_DECORATION, new Item.Properties());
    public static final Item DETAILED_COPPER_DECORATION_SLAB = new BlockItem(GCBlocks.DETAILED_COPPER_DECORATION_SLAB, new Item.Properties());
    public static final Item DETAILED_COPPER_DECORATION_STAIRS = new BlockItem(GCBlocks.DETAILED_COPPER_DECORATION_STAIRS, new Item.Properties());
    public static final Item DETAILED_COPPER_DECORATION_WALL = new BlockItem(GCBlocks.DETAILED_COPPER_DECORATION_WALL, new Item.Properties());

    public static final Item IRON_DECORATION = new BlockItem(GCBlocks.IRON_DECORATION, new Item.Properties());
    public static final Item IRON_DECORATION_SLAB = new BlockItem(GCBlocks.IRON_DECORATION_SLAB, new Item.Properties());
    public static final Item IRON_DECORATION_STAIRS = new BlockItem(GCBlocks.IRON_DECORATION_STAIRS, new Item.Properties());
    public static final Item IRON_DECORATION_WALL = new BlockItem(GCBlocks.IRON_DECORATION_WALL, new Item.Properties());
    public static final Item DETAILED_IRON_DECORATION = new BlockItem(GCBlocks.DETAILED_IRON_DECORATION, new Item.Properties());
    public static final Item DETAILED_IRON_DECORATION_SLAB = new BlockItem(GCBlocks.DETAILED_IRON_DECORATION_SLAB, new Item.Properties());
    public static final Item DETAILED_IRON_DECORATION_STAIRS = new BlockItem(GCBlocks.DETAILED_IRON_DECORATION_STAIRS, new Item.Properties());
    public static final Item DETAILED_IRON_DECORATION_WALL = new BlockItem(GCBlocks.DETAILED_IRON_DECORATION_WALL, new Item.Properties());

    public static final Item METEORIC_IRON_DECORATION = new BlockItem(GCBlocks.METEORIC_IRON_DECORATION, new Item.Properties());
    public static final Item METEORIC_IRON_DECORATION_SLAB = new BlockItem(GCBlocks.METEORIC_IRON_DECORATION_SLAB, new Item.Properties());
    public static final Item METEORIC_IRON_DECORATION_STAIRS = new BlockItem(GCBlocks.METEORIC_IRON_DECORATION_STAIRS, new Item.Properties());
    public static final Item METEORIC_IRON_DECORATION_WALL = new BlockItem(GCBlocks.METEORIC_IRON_DECORATION_WALL, new Item.Properties());
    public static final Item DETAILED_METEORIC_IRON_DECORATION = new BlockItem(GCBlocks.DETAILED_METEORIC_IRON_DECORATION, new Item.Properties());
    public static final Item DETAILED_METEORIC_IRON_DECORATION_SLAB = new BlockItem(GCBlocks.DETAILED_METEORIC_IRON_DECORATION_SLAB, new Item.Properties());
    public static final Item DETAILED_METEORIC_IRON_DECORATION_STAIRS = new BlockItem(GCBlocks.DETAILED_METEORIC_IRON_DECORATION_STAIRS, new Item.Properties());
    public static final Item DETAILED_METEORIC_IRON_DECORATION_WALL = new BlockItem(GCBlocks.DETAILED_METEORIC_IRON_DECORATION_WALL, new Item.Properties());

    public static final Item STEEL_DECORATION = new BlockItem(GCBlocks.STEEL_DECORATION, new Item.Properties());
    public static final Item STEEL_DECORATION_SLAB = new BlockItem(GCBlocks.STEEL_DECORATION_SLAB, new Item.Properties());
    public static final Item STEEL_DECORATION_STAIRS = new BlockItem(GCBlocks.STEEL_DECORATION_STAIRS, new Item.Properties());
    public static final Item STEEL_DECORATION_WALL = new BlockItem(GCBlocks.STEEL_DECORATION_WALL, new Item.Properties());
    public static final Item DETAILED_STEEL_DECORATION = new BlockItem(GCBlocks.DETAILED_STEEL_DECORATION, new Item.Properties());
    public static final Item DETAILED_STEEL_DECORATION_SLAB = new BlockItem(GCBlocks.DETAILED_STEEL_DECORATION_SLAB, new Item.Properties());
    public static final Item DETAILED_STEEL_DECORATION_STAIRS = new BlockItem(GCBlocks.DETAILED_STEEL_DECORATION_STAIRS, new Item.Properties());
    public static final Item DETAILED_STEEL_DECORATION_WALL = new BlockItem(GCBlocks.DETAILED_STEEL_DECORATION_WALL, new Item.Properties());

    public static final Item TIN_DECORATION = new BlockItem(GCBlocks.TIN_DECORATION, new Item.Properties());
    public static final Item TIN_DECORATION_SLAB = new BlockItem(GCBlocks.TIN_DECORATION_SLAB, new Item.Properties());
    public static final Item TIN_DECORATION_STAIRS = new BlockItem(GCBlocks.TIN_DECORATION_STAIRS, new Item.Properties());
    public static final Item TIN_DECORATION_WALL = new BlockItem(GCBlocks.TIN_DECORATION_WALL, new Item.Properties());
    public static final Item DETAILED_TIN_DECORATION = new BlockItem(GCBlocks.DETAILED_TIN_DECORATION, new Item.Properties());
    public static final Item DETAILED_TIN_DECORATION_SLAB = new BlockItem(GCBlocks.DETAILED_TIN_DECORATION_SLAB, new Item.Properties());
    public static final Item DETAILED_TIN_DECORATION_STAIRS = new BlockItem(GCBlocks.DETAILED_TIN_DECORATION_STAIRS, new Item.Properties());
    public static final Item DETAILED_TIN_DECORATION_WALL = new BlockItem(GCBlocks.DETAILED_TIN_DECORATION_WALL, new Item.Properties());

    public static final Item TITANIUM_DECORATION = new BlockItem(GCBlocks.TITANIUM_DECORATION, new Item.Properties());
    public static final Item TITANIUM_DECORATION_SLAB = new BlockItem(GCBlocks.TITANIUM_DECORATION_SLAB, new Item.Properties());
    public static final Item TITANIUM_DECORATION_STAIRS = new BlockItem(GCBlocks.TITANIUM_DECORATION_STAIRS, new Item.Properties());
    public static final Item TITANIUM_DECORATION_WALL = new BlockItem(GCBlocks.TITANIUM_DECORATION_WALL, new Item.Properties());
    public static final Item DETAILED_TITANIUM_DECORATION = new BlockItem(GCBlocks.DETAILED_TITANIUM_DECORATION, new Item.Properties());
    public static final Item DETAILED_TITANIUM_DECORATION_SLAB = new BlockItem(GCBlocks.DETAILED_TITANIUM_DECORATION_SLAB, new Item.Properties());
    public static final Item DETAILED_TITANIUM_DECORATION_STAIRS = new BlockItem(GCBlocks.DETAILED_TITANIUM_DECORATION_STAIRS, new Item.Properties());
    public static final Item DETAILED_TITANIUM_DECORATION_WALL = new BlockItem(GCBlocks.DETAILED_TITANIUM_DECORATION_WALL, new Item.Properties());

    public static final Item DARK_DECORATION = new BlockItem(GCBlocks.DARK_DECORATION, new Item.Properties());
    public static final Item DARK_DECORATION_SLAB = new BlockItem(GCBlocks.DARK_DECORATION_SLAB, new Item.Properties());
    public static final Item DARK_DECORATION_STAIRS = new BlockItem(GCBlocks.DARK_DECORATION_STAIRS, new Item.Properties());
    public static final Item DARK_DECORATION_WALL = new BlockItem(GCBlocks.DARK_DECORATION_WALL, new Item.Properties());
    public static final Item DETAILED_DARK_DECORATION = new BlockItem(GCBlocks.DETAILED_DARK_DECORATION, new Item.Properties());
    public static final Item DETAILED_DARK_DECORATION_SLAB = new BlockItem(GCBlocks.DETAILED_DARK_DECORATION_SLAB, new Item.Properties());
    public static final Item DETAILED_DARK_DECORATION_STAIRS = new BlockItem(GCBlocks.DETAILED_DARK_DECORATION_STAIRS, new Item.Properties());
    public static final Item DETAILED_DARK_DECORATION_WALL = new BlockItem(GCBlocks.DETAILED_DARK_DECORATION_WALL, new Item.Properties());

    // MOON NATURAL
    public static final Item MOON_TURF = new BlockItem(GCBlocks.MOON_TURF, new Item.Properties());
    public static final Item MOON_DIRT = new BlockItem(GCBlocks.MOON_DIRT, new Item.Properties());
    public static final Item MOON_DIRT_PATH = new BlockItem(GCBlocks.MOON_DIRT_PATH, new Item.Properties());
    public static final Item MOON_SURFACE_ROCK = new BlockItem(GCBlocks.MOON_SURFACE_ROCK, new Item.Properties());

    public static final Item MOON_ROCK = new BlockItem(GCBlocks.MOON_ROCK, new Item.Properties());
    public static final Item MOON_ROCK_SLAB = new BlockItem(GCBlocks.MOON_ROCK_SLAB, new Item.Properties());
    public static final Item MOON_ROCK_STAIRS = new BlockItem(GCBlocks.MOON_ROCK_STAIRS, new Item.Properties());
    public static final Item MOON_ROCK_WALL = new BlockItem(GCBlocks.MOON_ROCK_WALL, new Item.Properties());

    public static final Item COBBLED_MOON_ROCK = new BlockItem(GCBlocks.COBBLED_MOON_ROCK, new Item.Properties());
    public static final Item COBBLED_MOON_ROCK_SLAB = new BlockItem(GCBlocks.COBBLED_MOON_ROCK_SLAB, new Item.Properties());
    public static final Item COBBLED_MOON_ROCK_STAIRS = new BlockItem(GCBlocks.COBBLED_MOON_ROCK_STAIRS, new Item.Properties());
    public static final Item COBBLED_MOON_ROCK_WALL = new BlockItem(GCBlocks.COBBLED_MOON_ROCK_WALL, new Item.Properties());

    public static final Item LUNASLATE = new BlockItem(GCBlocks.LUNASLATE, new Item.Properties());
    public static final Item LUNASLATE_SLAB = new BlockItem(GCBlocks.LUNASLATE_SLAB, new Item.Properties());
    public static final Item LUNASLATE_STAIRS = new BlockItem(GCBlocks.LUNASLATE_STAIRS, new Item.Properties());
    public static final Item LUNASLATE_WALL = new BlockItem(GCBlocks.LUNASLATE_WALL, new Item.Properties());

    public static final Item COBBLED_LUNASLATE = new BlockItem(GCBlocks.COBBLED_LUNASLATE, new Item.Properties());
    public static final Item COBBLED_LUNASLATE_SLAB = new BlockItem(GCBlocks.COBBLED_LUNASLATE_SLAB, new Item.Properties());
    public static final Item COBBLED_LUNASLATE_STAIRS = new BlockItem(GCBlocks.COBBLED_LUNASLATE_STAIRS, new Item.Properties());
    public static final Item COBBLED_LUNASLATE_WALL = new BlockItem(GCBlocks.COBBLED_LUNASLATE_WALL, new Item.Properties());

    public static final Item MOON_BASALT = new BlockItem(GCBlocks.MOON_BASALT, new Item.Properties());
    public static final Item MOON_BASALT_SLAB = new BlockItem(GCBlocks.MOON_BASALT_SLAB, new Item.Properties());
    public static final Item MOON_BASALT_STAIRS = new BlockItem(GCBlocks.MOON_BASALT_STAIRS, new Item.Properties());
    public static final Item MOON_BASALT_WALL = new BlockItem(GCBlocks.MOON_BASALT_WALL, new Item.Properties());

    public static final Item MOON_BASALT_BRICK = new BlockItem(GCBlocks.MOON_BASALT_BRICK, new Item.Properties());
    public static final Item MOON_BASALT_BRICK_SLAB = new BlockItem(GCBlocks.MOON_BASALT_BRICK_SLAB, new Item.Properties());
    public static final Item MOON_BASALT_BRICK_STAIRS = new BlockItem(GCBlocks.MOON_BASALT_BRICK_STAIRS, new Item.Properties());
    public static final Item MOON_BASALT_BRICK_WALL = new BlockItem(GCBlocks.MOON_BASALT_BRICK_WALL, new Item.Properties());

    public static final Item CRACKED_MOON_BASALT_BRICK = new BlockItem(GCBlocks.CRACKED_MOON_BASALT_BRICK, new Item.Properties());
    public static final Item CRACKED_MOON_BASALT_BRICK_SLAB = new BlockItem(GCBlocks.CRACKED_MOON_BASALT_BRICK_SLAB, new Item.Properties());
    public static final Item CRACKED_MOON_BASALT_BRICK_STAIRS = new BlockItem(GCBlocks.CRACKED_MOON_BASALT_BRICK_STAIRS, new Item.Properties());
    public static final Item CRACKED_MOON_BASALT_BRICK_WALL = new BlockItem(GCBlocks.CRACKED_MOON_BASALT_BRICK_WALL, new Item.Properties());

    // MARS NATURAL
    public static final Item MARS_SURFACE_ROCK = new BlockItem(GCBlocks.MARS_SURFACE_ROCK, new Item.Properties());
    public static final Item MARS_SUB_SURFACE_ROCK = new BlockItem(GCBlocks.MARS_SUB_SURFACE_ROCK, new Item.Properties());
    public static final Item MARS_STONE = new BlockItem(GCBlocks.MARS_STONE, new Item.Properties());
    public static final Item MARS_STONE_SLAB = new BlockItem(GCBlocks.MARS_STONE_SLAB, new Item.Properties());
    public static final Item MARS_STONE_STAIRS = new BlockItem(GCBlocks.MARS_STONE_STAIRS, new Item.Properties());
    public static final Item MARS_STONE_WALL = new BlockItem(GCBlocks.MARS_STONE_WALL, new Item.Properties());
    public static final Item MARS_COBBLESTONE = new BlockItem(GCBlocks.MARS_COBBLESTONE, new Item.Properties());
    public static final Item MARS_COBBLESTONE_SLAB = new BlockItem(GCBlocks.MARS_COBBLESTONE_SLAB, new Item.Properties());
    public static final Item MARS_COBBLESTONE_STAIRS = new BlockItem(GCBlocks.MARS_COBBLESTONE_STAIRS, new Item.Properties());
    public static final Item MARS_COBBLESTONE_WALL = new BlockItem(GCBlocks.MARS_COBBLESTONE_WALL, new Item.Properties());

    // ASTEROID NATURAL
    public static final Item ASTEROID_ROCK = new BlockItem(GCBlocks.ASTEROID_ROCK, new Item.Properties());
    public static final Item ASTEROID_ROCK_1 = new BlockItem(GCBlocks.ASTEROID_ROCK_1, new Item.Properties());
    public static final Item ASTEROID_ROCK_2 = new BlockItem(GCBlocks.ASTEROID_ROCK_2, new Item.Properties());

    // VENUS NATURAL
    public static final Item SOFT_VENUS_ROCK = new BlockItem(GCBlocks.SOFT_VENUS_ROCK, new Item.Properties());
    public static final Item HARD_VENUS_ROCK = new BlockItem(GCBlocks.HARD_VENUS_ROCK, new Item.Properties());
    public static final Item SCORCHED_VENUS_ROCK = new BlockItem(GCBlocks.SCORCHED_VENUS_ROCK, new Item.Properties());
    public static final Item VOLCANIC_ROCK = new BlockItem(GCBlocks.VOLCANIC_ROCK, new Item.Properties());
    public static final Item PUMICE = new BlockItem(GCBlocks.PUMICE, new Item.Properties());
    public static final Item VAPOR_SPOUT = new BlockItem(GCBlocks.VAPOR_SPOUT, new Item.Properties());

    // MISC DECOR
    public static final Item WALKWAY = new BlockItem(GCBlocks.WALKWAY, new Item.Properties());
    public static final Item PIPE_WALKWAY = new BlockItem(GCBlocks.PIPE_WALKWAY, new Item.Properties());
    public static final Item WIRE_WALKWAY = new BlockItem(GCBlocks.WIRE_WALKWAY, new Item.Properties());
    public static final Item TIN_LADDER = new BlockItem(GCBlocks.TIN_LADDER, new Item.Properties());
    public static final Item GRATING = new BlockItem(GCBlocks.GRATING, new Item.Properties());

    // SPECIAL
    public static final Item ALUMINUM_WIRE = new BlockItem(GCBlocks.ALUMINUM_WIRE, new Item.Properties());
    public static final Item SEALABLE_ALUMINUM_WIRE = new BlockItem(GCBlocks.SEALABLE_ALUMINUM_WIRE, new Item.Properties());
    public static final Item HEAVY_SEALABLE_ALUMINUM_WIRE = new BlockItem(GCBlocks.HEAVY_SEALABLE_ALUMINUM_WIRE, new Item.Properties());
    public static final Item GLASS_FLUID_PIPE = new BlockItem(GCBlocks.GLASS_FLUID_PIPE, new Item.Properties());
    public static final Item ROCKET_LAUNCH_PAD = new BlockItem(GCBlocks.ROCKET_LAUNCH_PAD, new Item.Properties());

    // LIGHT PANELS
    public static final Item SQUARE_LIGHT_PANEL = new BlockItem(GCBlocks.SQUARE_LIGHT_PANEL, new Item.Properties());
    public static final Item SPOTLIGHT_LIGHT_PANEL = new BlockItem(GCBlocks.SPOTLIGHT_LIGHT_PANEL, new Item.Properties());
    public static final Item LINEAR_LIGHT_PANEL = new BlockItem(GCBlocks.LINEAR_LIGHT_PANEL, new Item.Properties());
    public static final Item DASHED_LIGHT_PANEL = new BlockItem(GCBlocks.DASHED_LIGHT_PANEL, new Item.Properties());
    public static final Item DIAGONAL_LIGHT_PANEL = new BlockItem(GCBlocks.DIAGONAL_LIGHT_PANEL, new Item.Properties());

    // VACUUM GLASS
    public static final Item VACUUM_GLASS = new BlockItem(GCBlocks.VACUUM_GLASS, new Item.Properties());
    public static final Item CLEAR_VACUUM_GLASS = new BlockItem(GCBlocks.CLEAR_VACUUM_GLASS, new Item.Properties());
    public static final Item STRONG_VACUUM_GLASS = new BlockItem(GCBlocks.STRONG_VACUUM_GLASS, new Item.Properties());

    // ORES
    public static final Item SILICON_ORE = new BlockItem(GCBlocks.SILICON_ORE, new Item.Properties());
    public static final Item DEEPSLATE_SILICON_ORE = new BlockItem(GCBlocks.DEEPSLATE_SILICON_ORE, new Item.Properties());

    public static final Item MOON_COPPER_ORE = new BlockItem(GCBlocks.MOON_COPPER_ORE, new Item.Properties());
    public static final Item LUNASLATE_COPPER_ORE = new BlockItem(GCBlocks.LUNASLATE_COPPER_ORE, new Item.Properties());

    public static final Item TIN_ORE = new BlockItem(GCBlocks.TIN_ORE, new Item.Properties());
    public static final Item DEEPSLATE_TIN_ORE = new BlockItem(GCBlocks.DEEPSLATE_TIN_ORE, new Item.Properties());
    public static final Item MOON_TIN_ORE = new BlockItem(GCBlocks.MOON_TIN_ORE, new Item.Properties());
    public static final Item LUNASLATE_TIN_ORE = new BlockItem(GCBlocks.LUNASLATE_TIN_ORE, new Item.Properties());

    public static final Item ALUMINUM_ORE = new BlockItem(GCBlocks.ALUMINUM_ORE, new Item.Properties());
    public static final Item DEEPSLATE_ALUMINUM_ORE = new BlockItem(GCBlocks.DEEPSLATE_ALUMINUM_ORE, new Item.Properties());

    public static final Item DESH_ORE = new BlockItem(GCBlocks.DESH_ORE, new Item.Properties());

    public static final Item ILMENITE_ORE = new BlockItem(GCBlocks.ILMENITE_ORE, new Item.Properties());

    public static final Item GALENA_ORE = new BlockItem(GCBlocks.GALENA_ORE, new Item.Properties());

    // CHEESE BLOCK
    public static final Item MOON_CHEESE_BLOCK = new BlockItem(GCBlocks.MOON_CHEESE_BLOCK, new Item.Properties());

    // COMPACT MINERAL BLOCKS
    public static final Item SILICON_BLOCK = new BlockItem(GCBlocks.SILICON_BLOCK, new Item.Properties());
    public static final Item METEORIC_IRON_BLOCK = new BlockItem(GCBlocks.METEORIC_IRON_BLOCK, new Item.Properties());
    public static final Item DESH_BLOCK = new BlockItem(GCBlocks.DESH_BLOCK, new Item.Properties());
    public static final Item TITANIUM_BLOCK = new BlockItem(GCBlocks.TITANIUM_BLOCK, new Item.Properties());
    public static final Item LEAD_BLOCK = new BlockItem(GCBlocks.LEAD_BLOCK, new Item.Properties());
    public static final Item LUNAR_SAPPHIRE_BLOCK = new BlockItem(GCBlocks.LUNAR_SAPPHIRE_BLOCK, new Item.Properties());

    // MOON VILLAGER SPECIAL
    public static final Item LUNAR_CARTOGRAPHY_TABLE = new BlockItem(GCBlocks.LUNAR_CARTOGRAPHY_TABLE, new Item.Properties());

    // MISC WORLD GEN
    public static final Item CAVERNOUS_VINE = new BlockItem(GCBlocks.CAVERNOUS_VINE, new Item.Properties());
    public static final Item POISONOUS_CAVERNOUS_VINE = new BlockItem(GCBlocks.POISONOUS_CAVERNOUS_VINE, new Item.Properties());

    // MISC MACHINES
    public static final Item CRYOGENIC_CHAMBER = new BlockItem(GCBlocks.CRYOGENIC_CHAMBER, new Item.Properties());
    public static final Item PLAYER_TRANSPORT_TUBE = new BlockItem(GCBlocks.PLAYER_TRANSPORT_TUBE, new Item.Properties());
    public static final BlockItem AIR_LOCK_FRAME = new BlockItem(GCBlocks.AIR_LOCK_FRAME, new FabricItemSettings());
    public static final BlockItem AIR_LOCK_CONTROLLER = new BlockItem(GCBlocks.AIR_LOCK_CONTROLLER, new FabricItemSettings());
    public static final BlockItem AIR_LOCK_SEAL = new BlockItem(GCBlocks.AIR_LOCK_SEAL, new FabricItemSettings());

    // MACHINES
    public static final Item CIRCUIT_FABRICATOR = new BlockItem(GCBlocks.CIRCUIT_FABRICATOR, new Item.Properties());
    public static final Item COMPRESSOR = new BlockItem(GCBlocks.COMPRESSOR, new Item.Properties());
    public static final Item ELECTRIC_COMPRESSOR = new BlockItem(GCBlocks.ELECTRIC_COMPRESSOR, new Item.Properties());
    public static final Item COAL_GENERATOR = new BlockItem(GCBlocks.COAL_GENERATOR, new Item.Properties());
    public static final Item BASIC_SOLAR_PANEL = new BlockItem(GCBlocks.BASIC_SOLAR_PANEL, new Item.Properties());
    public static final Item ADVANCED_SOLAR_PANEL = new BlockItem(GCBlocks.ADVANCED_SOLAR_PANEL, new Item.Properties());
    public static final Item ENERGY_STORAGE_MODULE = new BlockItem(GCBlocks.ENERGY_STORAGE_MODULE, new Item.Properties());
    public static final Item ELECTRIC_FURNACE = new BlockItem(GCBlocks.ELECTRIC_FURNACE, new Item.Properties());
    public static final Item ELECTRIC_ARC_FURNACE = new BlockItem(GCBlocks.ELECTRIC_ARC_FURNACE, new Item.Properties());
    public static final Item REFINERY = new BlockItem(GCBlocks.REFINERY, new Item.Properties());
    public static final Item OXYGEN_COLLECTOR = new BlockItem(GCBlocks.OXYGEN_COLLECTOR, new Item.Properties());
    public static final Item OXYGEN_SEALER = new BlockItem(GCBlocks.OXYGEN_SEALER, new Item.Properties());
    public static final Item OXYGEN_BUBBLE_DISTRIBUTOR = new BlockItem(GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR, new Item.Properties());
    public static final Item OXYGEN_DECOMPRESSOR = new BlockItem(GCBlocks.OXYGEN_DECOMPRESSOR, new Item.Properties());
    public static final Item OXYGEN_COMPRESSOR = new BlockItem(GCBlocks.OXYGEN_COMPRESSOR, new Item.Properties());
    public static final Item OXYGEN_STORAGE_MODULE = new BlockItem(GCBlocks.OXYGEN_STORAGE_MODULE, new Item.Properties());
    public static final Item FUEL_LOADER = new BlockItem(GCBlocks.FUEL_LOADER, new Item.Properties());
    public static final Item ROCKET_WORKBENCH = new BlockItem(GCBlocks.ROCKET_WORKBENCH, new Item.Properties());
    // === END BLOCKS ===
    
    // MATERIALS
    public static final Item RAW_SILICON = new Item(new Item.Properties());
    
    public static final Item RAW_METEORIC_IRON = new Item(new Item.Properties());
    public static final Item METEORIC_IRON_INGOT = new Item(new Item.Properties());
    public static final Item METEORIC_IRON_NUGGET = new Item(new Item.Properties());
    public static final Item COMPRESSED_METEORIC_IRON = new Item(new Item.Properties());

    public static final Item RAW_DESH = new Item(new Item.Properties());
    public static final Item DESH_INGOT = new Item(new Item.Properties());
    public static final Item DESH_NUGGET = new Item(new Item.Properties());
    public static final Item COMPRESSED_DESH = new Item(new Item.Properties());

    public static final Item RAW_LEAD = new Item(new Item.Properties());
    public static final Item LEAD_INGOT = new Item(new Item.Properties());
    public static final Item LEAD_NUGGET = new Item(new Item.Properties());
    
    public static final Item RAW_ALUMINUM = new Item(new Item.Properties());
    public static final Item ALUMINUM_INGOT = new Item(new Item.Properties());
    public static final Item ALUMINUM_NUGGET = new Item(new Item.Properties());
    public static final Item COMPRESSED_ALUMINUM = new Item(new Item.Properties());

    public static final Item RAW_TIN = new Item(new Item.Properties());
    public static final Item TIN_INGOT = new Item(new Item.Properties());
    public static final Item TIN_NUGGET = new Item(new Item.Properties());
    public static final Item COMPRESSED_TIN = new Item(new Item.Properties());

    public static final Item RAW_TITANIUM = new Item(new Item.Properties());
    public static final Item TITANIUM_INGOT = new Item(new Item.Properties());
    public static final Item TITANIUM_NUGGET = new Item(new Item.Properties());
    public static final Item COMPRESSED_TITANIUM = new Item(new Item.Properties());

    public static final Item COMPRESSED_BRONZE = new Item(new Item.Properties());
    public static final Item COMPRESSED_COPPER = new Item(new Item.Properties());
    public static final Item COMPRESSED_IRON = new Item(new Item.Properties());
    public static final Item COMPRESSED_STEEL = new Item(new Item.Properties());
    
    public static final Item LUNAR_SAPPHIRE = new Item(new Item.Properties());
    public static final Item DESH_STICK = new Item(new Item.Properties());
    public static final Item CARBON_FRAGMENTS = new Item(new Item.Properties());
    public static final Item IRON_SHARD = new Item(new Item.Properties());
    public static final Item SOLAR_DUST = new Item(new Item.Properties());
    public static final Item BASIC_WAFER = new Item(new Item.Properties());
    public static final Item ADVANCED_WAFER = new Item(new Item.Properties());
    public static final Item BEAM_CORE = new Item(new Item.Properties());
    public static final Item CANVAS = new Item(new Item.Properties());
    
    public static final Item FLUID_MANIPULATOR = new Item(new Item.Properties());
    public static final Item OXYGEN_CONCENTRATOR = new Item(new Item.Properties());
    public static final Item OXYGEN_FAN = new Item(new Item.Properties());
    public static final Item OXYGEN_VENT = new Item(new Item.Properties());
    public static final Item SENSOR_LENS = new Item(new Item.Properties());
    public static final Item BLUE_SOLAR_WAFER = new Item(new Item.Properties());
    public static final Item SINGLE_SOLAR_MODULE = new Item(new Item.Properties());
    public static final Item FULL_SOLAR_PANEL = new Item(new Item.Properties());
    public static final Item SOLAR_ARRAY_WAFER = new Item(new Item.Properties());
    public static final Item STEEL_POLE = new Item(new Item.Properties());
    public static final Item COPPER_CANISTER = new Item(new Item.Properties());
    public static final Item TIN_CANISTER = new Item(new Item.Properties());
    public static final Item THERMAL_CLOTH = new Item(new Item.Properties());
    public static final Item ISOTHERMAL_FABRIC = new Item(new Item.Properties());
    public static final Item ORION_DRIVE = new Item(new Item.Properties());
    public static final Item ATMOSPHERIC_VALVE = new Item(new Item.Properties());
    public static final Item AMBIENT_THERMAL_CONTROLLER = new Item(new Item.Properties());
    
    // FOOD
    public static final Item MOON_BERRIES = new ItemNameBlockItem(GCBlocks.MOON_BERRY_BUSH, new Item.Properties().food(GCFoodComponent.MOON_BERRIES));
    public static final Item CHEESE_CURD = new Item(new Item.Properties().food(GCFoodComponent.CHEESE_CURD));
    
    public static final Item CHEESE_SLICE = new Item(new Item.Properties().food(GCFoodComponent.CHEESE_SLICE));
    public static final Item BURGER_BUN = new Item(new Item.Properties().food(GCFoodComponent.BURGER_BUN));
    public static final Item GROUND_BEEF = new Item(new Item.Properties().food(GCFoodComponent.GROUND_BEEF));
    public static final Item BEEF_PATTY = new Item(new Item.Properties().food(GCFoodComponent.BEEF_PATTY));
    public static final Item CHEESEBURGER = new Item(new Item.Properties().food(GCFoodComponent.CHEESEBURGER));
    
    public static final Item CANNED_DEHYDRATED_APPLE = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_APPLE));
    public static final Item CANNED_DEHYDRATED_CARROT = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_CARROT));
    public static final Item CANNED_DEHYDRATED_MELON = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_MELON));
    public static final Item CANNED_DEHYDRATED_POTATO = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_POTATO));
    public static final Item CANNED_BEEF = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.CANNED_BEEF));
    
    // ROCKET PLATES
    public static final Item TIER_1_HEAVY_DUTY_PLATE = new Item(new Item.Properties());
    public static final Item TIER_2_HEAVY_DUTY_PLATE = new Item(new Item.Properties());
    public static final Item TIER_3_HEAVY_DUTY_PLATE = new Item(new Item.Properties());

    // ARMOR
    public static final Item HEAVY_DUTY_HELMET = new ArmorItem(GCArmorMaterial.HEAVY_DUTY, ArmorItem.Type.HELMET, (new Item.Properties()));
    public static final Item HEAVY_DUTY_CHESTPLATE = new ArmorItem(GCArmorMaterial.HEAVY_DUTY, ArmorItem.Type.CHESTPLATE, (new Item.Properties()));
    public static final Item HEAVY_DUTY_LEGGINGS = new ArmorItem(GCArmorMaterial.HEAVY_DUTY, ArmorItem.Type.LEGGINGS, (new Item.Properties()));
    public static final Item HEAVY_DUTY_BOOTS = new ArmorItem(GCArmorMaterial.HEAVY_DUTY, ArmorItem.Type.BOOTS, (new Item.Properties()));

    public static final Item DESH_HELMET = new ArmorItem(GCArmorMaterial.DESH, ArmorItem.Type.HELMET, (new Item.Properties()));
    public static final Item DESH_CHESTPLATE = new ArmorItem(GCArmorMaterial.DESH, ArmorItem.Type.CHESTPLATE, (new Item.Properties()));
    public static final Item DESH_LEGGINGS = new ArmorItem(GCArmorMaterial.DESH, ArmorItem.Type.LEGGINGS, (new Item.Properties()));
    public static final Item DESH_BOOTS = new ArmorItem(GCArmorMaterial.DESH, ArmorItem.Type.BOOTS, (new Item.Properties()));

    public static final Item TITANIUM_HELMET = new ArmorItem(GCArmorMaterial.TITANIUM, ArmorItem.Type.HELMET, (new Item.Properties()));
    public static final Item TITANIUM_CHESTPLATE = new ArmorItem(GCArmorMaterial.TITANIUM, ArmorItem.Type.CHESTPLATE, (new Item.Properties()));
    public static final Item TITANIUM_LEGGINGS = new ArmorItem(GCArmorMaterial.TITANIUM, ArmorItem.Type.LEGGINGS, (new Item.Properties()));
    public static final Item TITANIUM_BOOTS = new ArmorItem(GCArmorMaterial.TITANIUM, ArmorItem.Type.BOOTS, (new Item.Properties()));

    public static final Item SENSOR_GLASSES = new ArmorItem(GCArmorMaterial.SENSOR_GLASSES, ArmorItem.Type.HELMET, new Item.Properties());

    // TOOLS + WEAPONS
    public static final Item HEAVY_DUTY_SWORD = new BrittleSwordItem(GCToolMaterial.STEEL, 3, -2.4F, new Item.Properties());
    public static final Item HEAVY_DUTY_SHOVEL = new ShovelItem(GCToolMaterial.STEEL, -1.5F, -3.0F, new Item.Properties());
    public static final Item HEAVY_DUTY_PICKAXE = new PickaxeItem(GCToolMaterial.STEEL, 1, -2.8F, new Item.Properties());
    public static final Item HEAVY_DUTY_AXE = new AxeItem(GCToolMaterial.STEEL, 6.0F, -3.1F, new Item.Properties());
    public static final Item HEAVY_DUTY_HOE = new HoeItem(GCToolMaterial.STEEL, -2, -1.0F, new Item.Properties());

    public static final Item DESH_SWORD = new SwordItem(GCToolMaterial.DESH, 3, -2.4F, new Item.Properties());
    public static final Item DESH_SHOVEL = new ShovelItem(GCToolMaterial.DESH, -1.5F, -3.0F, new Item.Properties());
    public static final Item DESH_PICKAXE = new PickaxeItem(GCToolMaterial.DESH, 1, -2.8F, new Item.Properties());
    public static final Item DESH_AXE = new AxeItem(GCToolMaterial.DESH, 6.0F, -3.1F, new Item.Properties());
    public static final Item DESH_HOE = new HoeItem(GCToolMaterial.DESH, -3, -1.0F, new Item.Properties());

    public static final Item TITANIUM_SWORD = new BrittleSwordItem(GCToolMaterial.TITANIUM, 3, -2.4F, new Item.Properties());
    public static final Item TITANIUM_SHOVEL = new ShovelItem(GCToolMaterial.TITANIUM, -1.5F, -3.0F, new Item.Properties());
    public static final Item TITANIUM_PICKAXE = new PickaxeItem(GCToolMaterial.TITANIUM, 1, -2.8F, new Item.Properties());
    public static final Item TITANIUM_AXE = new AxeItem(GCToolMaterial.TITANIUM, 6.0F, -3.1F, new Item.Properties());
    public static final Item TITANIUM_HOE = new HoeItem(GCToolMaterial.TITANIUM, -3, -1.0F, new Item.Properties());

    public static final Item STANDARD_WRENCH = new StandardWrenchItem(new Item.Properties());

    // BATTERIES
    public static final Item BATTERY = new BatteryItem(new Item.Properties(), 15000, 500);
    public static final Item INFINITE_BATTERY = new InfiniteBatteryItem(new Item.Properties().rarity(Rarity.EPIC));

    //FLUID BUCKETS
    public static final Item CRUDE_OIL_BUCKET = new BucketItem(GCFluids.CRUDE_OIL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
    public static final Item FUEL_BUCKET = new BucketItem(GCFluids.FUEL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));

    //GALACTICRAFT INVENTORY
    public static final Item PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item ORANGE_PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item MAGENTA_PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item LIGHT_BLUE_PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item YELLOW_PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item LIME_PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item PINK_PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item GRAY_PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item LIGHT_GRAY_PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item CYAN_PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item PURPLE_PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item BLUE_PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item BROWN_PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item GREEN_PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item RED_PARACHUTE = new Item(new Item.Properties().stacksTo(1));
    public static final Item BLACK_PARACHUTE = new Item(new Item.Properties().stacksTo(1));

    public static final Item OXYGEN_MASK = new OxygenMaskItem(new Item.Properties());
    public static final Item OXYGEN_GEAR = new OxygenGearItem(new Item.Properties());

    public static final Item SMALL_OXYGEN_TANK = new OxygenTankItem(new Item.Properties(), 1620 * 10); // 16200 ticks
    public static final Item MEDIUM_OXYGEN_TANK = new OxygenTankItem(new Item.Properties(), 1620 * 20); //32400 ticks
    public static final Item LARGE_OXYGEN_TANK = new OxygenTankItem(new Item.Properties(), 1620 * 30); //48600 ticks
    public static final Item INFINITE_OXYGEN_TANK = new InfiniteOxygenTankItem(new Item.Properties());

    public static final Item SHIELD_CONTROLLER = new AccessoryItem(new Item.Properties());
    public static final Item FREQUENCY_MODULE = new FrequencyModuleItem(new Item.Properties());

    public static final Item THERMAL_PADDING_HELMET = new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.HELMET);
    public static final Item THERMAL_PADDING_CHESTPIECE = new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.CHESTPLATE);
    public static final Item THERMAL_PADDING_LEGGINGS = new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.LEGGINGS);
    public static final Item THERMAL_PADDING_BOOTS = new ThermalArmorItem(new Item.Properties(), ArmorItem.Type.BOOTS);
    // ROCKETS
    public static final Item ROCKET = new RocketItem(new Item.Properties());

    // ROCKET PIECES
    public static final Item ROCKET_FINS = new Item(new Item.Properties());
    public static final Item ROCKET_ENGINE = new Item(new Item.Properties());

    // SCHEMATICS
    public static final Item BASIC_ROCKET_CONE_SCHEMATIC = new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), Constant.id("basic_cone"));
    public static final Item BASIC_ROCKET_BODY_SCHEMATIC = new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), Constant.id("basic_body"));
    public static final Item BASIC_ROCKET_FINS_SCHEMATIC = new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), Constant.id("basic_fins"));
    public static final Item BASIC_ROCKET_BOTTOM_SCHEMATIC = new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), Constant.id("basic_bottom"));

    public static final Item TIER_2_ROCKET_SCHEMATIC = new SchematicItem(new Item.Properties());
    public static final Item CARGO_ROCKET_SCHEMATIC = new SchematicItem(new Item.Properties());
    public static final Item MOON_BUGGY_SCHEMATIC = new SchematicItem(new Item.Properties());
    public static final Item TIER_3_ROCKET_SCHEMATIC = new SchematicItem(new Item.Properties());
    public static final Item ASTRO_MINER_SCHEMATIC = new SchematicItem(new Item.Properties());
    
    public static void register() {
        // === START BLOCKS ===
        // TORCHES
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.GLOWSTONE_TORCH), GLOWSTONE_TORCH);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.UNLIT_TORCH), UNLIT_TORCH);

        // LANTERNS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.GLOWSTONE_LANTERN), GLOWSTONE_LANTERN);

        // DECORATION BLOCKS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ALUMINUM_DECORATION), ALUMINUM_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ALUMINUM_DECORATION_SLAB), ALUMINUM_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ALUMINUM_DECORATION_STAIRS), ALUMINUM_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ALUMINUM_DECORATION_WALL), ALUMINUM_DECORATION_WALL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION), DETAILED_ALUMINUM_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION_SLAB), DETAILED_ALUMINUM_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION_STAIRS), DETAILED_ALUMINUM_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION_WALL), DETAILED_ALUMINUM_DECORATION_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.BRONZE_DECORATION), BRONZE_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.BRONZE_DECORATION_SLAB), BRONZE_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.BRONZE_DECORATION_STAIRS), BRONZE_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.BRONZE_DECORATION_WALL), BRONZE_DECORATION_WALL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION), DETAILED_BRONZE_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION_SLAB), DETAILED_BRONZE_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION_STAIRS), DETAILED_BRONZE_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION_WALL), DETAILED_BRONZE_DECORATION_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.COPPER_DECORATION), COPPER_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.COPPER_DECORATION_SLAB), COPPER_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.COPPER_DECORATION_STAIRS), COPPER_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.COPPER_DECORATION_WALL), COPPER_DECORATION_WALL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION), DETAILED_COPPER_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION_SLAB), DETAILED_COPPER_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION_STAIRS), DETAILED_COPPER_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION_WALL), DETAILED_COPPER_DECORATION_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.IRON_DECORATION), IRON_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.IRON_DECORATION_SLAB), IRON_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.IRON_DECORATION_STAIRS), IRON_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.IRON_DECORATION_WALL), IRON_DECORATION_WALL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_IRON_DECORATION), DETAILED_IRON_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_IRON_DECORATION_SLAB), DETAILED_IRON_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_IRON_DECORATION_STAIRS), DETAILED_IRON_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_IRON_DECORATION_WALL), DETAILED_IRON_DECORATION_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.METEORIC_IRON_DECORATION), METEORIC_IRON_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.METEORIC_IRON_DECORATION_SLAB), METEORIC_IRON_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.METEORIC_IRON_DECORATION_STAIRS), METEORIC_IRON_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.METEORIC_IRON_DECORATION_WALL), METEORIC_IRON_DECORATION_WALL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION), DETAILED_METEORIC_IRON_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION_SLAB), DETAILED_METEORIC_IRON_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION_STAIRS), DETAILED_METEORIC_IRON_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION_WALL), DETAILED_METEORIC_IRON_DECORATION_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.STEEL_DECORATION), STEEL_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.STEEL_DECORATION_SLAB), STEEL_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.STEEL_DECORATION_STAIRS), STEEL_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.STEEL_DECORATION_WALL), STEEL_DECORATION_WALL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION), DETAILED_STEEL_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION_SLAB), DETAILED_STEEL_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION_STAIRS), DETAILED_STEEL_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION_WALL), DETAILED_STEEL_DECORATION_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.TIN_DECORATION), TIN_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.TIN_DECORATION_SLAB), TIN_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.TIN_DECORATION_STAIRS), TIN_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.TIN_DECORATION_WALL), TIN_DECORATION_WALL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_TIN_DECORATION), DETAILED_TIN_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_TIN_DECORATION_SLAB), DETAILED_TIN_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_TIN_DECORATION_STAIRS), DETAILED_TIN_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_TIN_DECORATION_WALL), DETAILED_TIN_DECORATION_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.TITANIUM_DECORATION), TITANIUM_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.TITANIUM_DECORATION_SLAB), TITANIUM_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.TITANIUM_DECORATION_STAIRS), TITANIUM_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.TITANIUM_DECORATION_WALL), TITANIUM_DECORATION_WALL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION), DETAILED_TITANIUM_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION_SLAB), DETAILED_TITANIUM_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION_STAIRS), DETAILED_TITANIUM_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION_WALL), DETAILED_TITANIUM_DECORATION_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DARK_DECORATION), DARK_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DARK_DECORATION_SLAB), DARK_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DARK_DECORATION_STAIRS), DARK_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DARK_DECORATION_WALL), DARK_DECORATION_WALL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_DARK_DECORATION), DETAILED_DARK_DECORATION);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_DARK_DECORATION_SLAB), DETAILED_DARK_DECORATION_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_DARK_DECORATION_STAIRS), DETAILED_DARK_DECORATION_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DETAILED_DARK_DECORATION_WALL), DETAILED_DARK_DECORATION_WALL);

        // MOON NATURAL
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_TURF), MOON_TURF);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_DIRT), MOON_DIRT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_DIRT_PATH), MOON_DIRT_PATH);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_SURFACE_ROCK), MOON_SURFACE_ROCK);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_ROCK), MOON_ROCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_ROCK_SLAB), MOON_ROCK_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_ROCK_STAIRS), MOON_ROCK_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_ROCK_WALL), MOON_ROCK_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.COBBLED_MOON_ROCK), COBBLED_MOON_ROCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.COBBLED_MOON_ROCK_SLAB), COBBLED_MOON_ROCK_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.COBBLED_MOON_ROCK_STAIRS), COBBLED_MOON_ROCK_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.COBBLED_MOON_ROCK_WALL), COBBLED_MOON_ROCK_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.LUNASLATE), LUNASLATE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.LUNASLATE_SLAB), LUNASLATE_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.LUNASLATE_STAIRS), LUNASLATE_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.LUNASLATE_WALL), LUNASLATE_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.COBBLED_LUNASLATE), COBBLED_LUNASLATE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.COBBLED_LUNASLATE_SLAB), COBBLED_LUNASLATE_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.COBBLED_LUNASLATE_STAIRS), COBBLED_LUNASLATE_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.COBBLED_LUNASLATE_WALL), COBBLED_LUNASLATE_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_BASALT), MOON_BASALT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_BASALT_SLAB), MOON_BASALT_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_BASALT_STAIRS), MOON_BASALT_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_BASALT_WALL), MOON_BASALT_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_BASALT_BRICK), MOON_BASALT_BRICK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_BASALT_BRICK_SLAB), MOON_BASALT_BRICK_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_BASALT_BRICK_STAIRS), MOON_BASALT_BRICK_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_BASALT_BRICK_WALL), MOON_BASALT_BRICK_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK), CRACKED_MOON_BASALT_BRICK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK_SLAB), CRACKED_MOON_BASALT_BRICK_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK_STAIRS), CRACKED_MOON_BASALT_BRICK_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK_WALL), CRACKED_MOON_BASALT_BRICK_WALL);

        // MARS NATURAL
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MARS_SURFACE_ROCK), MARS_SURFACE_ROCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MARS_SUB_SURFACE_ROCK), MARS_SUB_SURFACE_ROCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MARS_STONE), MARS_STONE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MARS_STONE_SLAB), MARS_STONE_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MARS_STONE_STAIRS), MARS_STONE_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MARS_STONE_WALL), MARS_STONE_WALL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MARS_COBBLESTONE), MARS_COBBLESTONE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MARS_COBBLESTONE_SLAB), MARS_COBBLESTONE_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MARS_COBBLESTONE_STAIRS), MARS_COBBLESTONE_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MARS_COBBLESTONE_WALL), MARS_COBBLESTONE_WALL);

        // ASTEROID NATURAL
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ASTEROID_ROCK), ASTEROID_ROCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ASTEROID_ROCK_1), ASTEROID_ROCK_1);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ASTEROID_ROCK_2), ASTEROID_ROCK_2);

        // VENUS NATURAL
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.SOFT_VENUS_ROCK), SOFT_VENUS_ROCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.HARD_VENUS_ROCK), HARD_VENUS_ROCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.SCORCHED_VENUS_ROCK), SCORCHED_VENUS_ROCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.VOLCANIC_ROCK), VOLCANIC_ROCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.PUMICE), PUMICE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.VAPOR_SPOUT), VAPOR_SPOUT);

        // MISC DECOR
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.WALKWAY), WALKWAY);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.PIPE_WALKWAY), PIPE_WALKWAY);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.WIRE_WALKWAY), WIRE_WALKWAY);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.TIN_LADDER), TIN_LADDER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.GRATING), GRATING);

        // SPECIAL
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ALUMINUM_WIRE), ALUMINUM_WIRE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.SEALABLE_ALUMINUM_WIRE), SEALABLE_ALUMINUM_WIRE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.HEAVY_SEALABLE_ALUMINUM_WIRE), HEAVY_SEALABLE_ALUMINUM_WIRE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.GLASS_FLUID_PIPE), GLASS_FLUID_PIPE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ROCKET_LAUNCH_PAD), ROCKET_LAUNCH_PAD);

        // LIGHT PANELS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.SQUARE_LIGHT_PANEL), SQUARE_LIGHT_PANEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.SPOTLIGHT_LIGHT_PANEL), SPOTLIGHT_LIGHT_PANEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.LINEAR_LIGHT_PANEL), LINEAR_LIGHT_PANEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DASHED_LIGHT_PANEL), DASHED_LIGHT_PANEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DIAGONAL_LIGHT_PANEL), DIAGONAL_LIGHT_PANEL);

        // VACUUM GLASS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.VACUUM_GLASS), VACUUM_GLASS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.CLEAR_VACUUM_GLASS), CLEAR_VACUUM_GLASS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.STRONG_VACUUM_GLASS), STRONG_VACUUM_GLASS);

        // ORES
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.SILICON_ORE), SILICON_ORE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DEEPSLATE_SILICON_ORE), DEEPSLATE_SILICON_ORE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_COPPER_ORE), MOON_COPPER_ORE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.LUNASLATE_COPPER_ORE), LUNASLATE_COPPER_ORE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.TIN_ORE), TIN_ORE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DEEPSLATE_TIN_ORE), DEEPSLATE_TIN_ORE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_TIN_ORE), MOON_TIN_ORE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.LUNASLATE_TIN_ORE), LUNASLATE_TIN_ORE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ALUMINUM_ORE), ALUMINUM_ORE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DEEPSLATE_ALUMINUM_ORE), DEEPSLATE_ALUMINUM_ORE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DESH_ORE), DESH_ORE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ILMENITE_ORE), ILMENITE_ORE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.GALENA_ORE), GALENA_ORE);

        // CHEESE BLOCK
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_CHEESE_BLOCK), MOON_CHEESE_BLOCK);

        // COMPACT MINERAL BLOCKS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.SILICON_BLOCK), SILICON_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.METEORIC_IRON_BLOCK), METEORIC_IRON_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.DESH_BLOCK), DESH_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.TITANIUM_BLOCK), TITANIUM_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.LEAD_BLOCK), LEAD_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.LUNAR_SAPPHIRE_BLOCK), LUNAR_SAPPHIRE_BLOCK);

        // MOON VILLAGER SPECIAL
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.LUNAR_CARTOGRAPHY_TABLE), LUNAR_CARTOGRAPHY_TABLE);

        // MISC WORLD GEN
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.CAVERNOUS_VINE), CAVERNOUS_VINE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.POISONOUS_CAVERNOUS_VINE), POISONOUS_CAVERNOUS_VINE);

        // MISC MACHINES
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.CRYOGENIC_CHAMBER), CRYOGENIC_CHAMBER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.PLAYER_TRANSPORT_TUBE), PLAYER_TRANSPORT_TUBE);

        // MACHINES
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.CIRCUIT_FABRICATOR), CIRCUIT_FABRICATOR);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.COMPRESSOR), COMPRESSOR);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ELECTRIC_COMPRESSOR), ELECTRIC_COMPRESSOR);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.COAL_GENERATOR), COAL_GENERATOR);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.BASIC_SOLAR_PANEL), BASIC_SOLAR_PANEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ADVANCED_SOLAR_PANEL), ADVANCED_SOLAR_PANEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ENERGY_STORAGE_MODULE), ENERGY_STORAGE_MODULE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ELECTRIC_FURNACE), ELECTRIC_FURNACE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ELECTRIC_ARC_FURNACE), ELECTRIC_ARC_FURNACE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.REFINERY), REFINERY);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.OXYGEN_COLLECTOR), OXYGEN_COLLECTOR);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.OXYGEN_SEALER), OXYGEN_SEALER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.OXYGEN_BUBBLE_DISTRIBUTOR), OXYGEN_BUBBLE_DISTRIBUTOR);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.OXYGEN_DECOMPRESSOR), OXYGEN_DECOMPRESSOR);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.OXYGEN_COMPRESSOR), OXYGEN_COMPRESSOR);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.OXYGEN_STORAGE_MODULE), OXYGEN_STORAGE_MODULE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.FUEL_LOADER), FUEL_LOADER);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ROCKET_WORKBENCH), ROCKET_WORKBENCH);
        // === END BLOCKS ===

        // MATERIALS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RAW_SILICON), RAW_SILICON);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RAW_METEORIC_IRON), RAW_METEORIC_IRON);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.METEORIC_IRON_INGOT), METEORIC_IRON_INGOT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.METEORIC_IRON_NUGGET), METEORIC_IRON_NUGGET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_METEORIC_IRON), COMPRESSED_METEORIC_IRON);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RAW_DESH), RAW_DESH);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_INGOT), DESH_INGOT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_NUGGET), DESH_NUGGET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_DESH), COMPRESSED_DESH);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RAW_LEAD), RAW_LEAD);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.LEAD_INGOT), LEAD_INGOT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.LEAD_NUGGET), LEAD_NUGGET);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RAW_ALUMINUM), RAW_ALUMINUM);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ALUMINUM_INGOT), ALUMINUM_INGOT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ALUMINUM_NUGGET), ALUMINUM_NUGGET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_ALUMINUM), COMPRESSED_ALUMINUM);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RAW_TIN), RAW_TIN);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TIN_INGOT), TIN_INGOT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TIN_NUGGET), TIN_NUGGET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_TIN), COMPRESSED_TIN);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RAW_TITANIUM), RAW_TITANIUM);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_INGOT), TITANIUM_INGOT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_NUGGET), TITANIUM_NUGGET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_TITANIUM), COMPRESSED_TITANIUM);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_BRONZE), COMPRESSED_BRONZE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_COPPER), COMPRESSED_COPPER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_IRON), COMPRESSED_IRON);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COMPRESSED_STEEL), COMPRESSED_STEEL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.LUNAR_SAPPHIRE), LUNAR_SAPPHIRE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_STICK), DESH_STICK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CARBON_FRAGMENTS), CARBON_FRAGMENTS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.IRON_SHARD), IRON_SHARD);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SOLAR_DUST), SOLAR_DUST);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_WAFER), BASIC_WAFER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ADVANCED_WAFER), ADVANCED_WAFER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BEAM_CORE), BEAM_CORE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANVAS), CANVAS);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.FLUID_MANIPULATOR), FLUID_MANIPULATOR);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.OXYGEN_CONCENTRATOR), OXYGEN_CONCENTRATOR);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.OXYGEN_FAN), OXYGEN_FAN);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.OXYGEN_VENT), OXYGEN_VENT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SENSOR_LENS), SENSOR_LENS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BLUE_SOLAR_WAFER), BLUE_SOLAR_WAFER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SINGLE_SOLAR_MODULE), SINGLE_SOLAR_MODULE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.FULL_SOLAR_PANEL), FULL_SOLAR_PANEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SOLAR_ARRAY_WAFER), SOLAR_ARRAY_WAFER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.STEEL_POLE), STEEL_POLE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COPPER_CANISTER), COPPER_CANISTER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TIN_CANISTER), TIN_CANISTER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.THERMAL_CLOTH), THERMAL_CLOTH);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ISOTHERMAL_FABRIC), ISOTHERMAL_FABRIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ORION_DRIVE), ORION_DRIVE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ATMOSPHERIC_VALVE), ATMOSPHERIC_VALVE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.AMBIENT_THERMAL_CONTROLLER), AMBIENT_THERMAL_CONTROLLER);

        // FOOD
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.MOON_BERRIES), MOON_BERRIES);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CHEESE_CURD), CHEESE_CURD);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CHEESE_SLICE), CHEESE_SLICE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BURGER_BUN), BURGER_BUN);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.GROUND_BEEF), GROUND_BEEF);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BEEF_PATTY), BEEF_PATTY);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CHEESEBURGER), CHEESEBURGER);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_APPLE), CANNED_DEHYDRATED_APPLE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_CARROT), CANNED_DEHYDRATED_CARROT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_MELON), CANNED_DEHYDRATED_MELON);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_POTATO), CANNED_DEHYDRATED_POTATO);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_BEEF), CANNED_BEEF);

        // ROCKET PLATES
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TIER_1_HEAVY_DUTY_PLATE), TIER_1_HEAVY_DUTY_PLATE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TIER_2_HEAVY_DUTY_PLATE), TIER_2_HEAVY_DUTY_PLATE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TIER_3_HEAVY_DUTY_PLATE), TIER_3_HEAVY_DUTY_PLATE);

        // ARMOR
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_HELMET), HEAVY_DUTY_HELMET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_CHESTPLATE), HEAVY_DUTY_CHESTPLATE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_LEGGINGS), HEAVY_DUTY_LEGGINGS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_BOOTS), HEAVY_DUTY_BOOTS);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_HELMET), DESH_HELMET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_CHESTPLATE), DESH_CHESTPLATE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_LEGGINGS), DESH_LEGGINGS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_BOOTS), DESH_BOOTS);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_HELMET), TITANIUM_HELMET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_CHESTPLATE), TITANIUM_CHESTPLATE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_LEGGINGS), TITANIUM_LEGGINGS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_BOOTS), TITANIUM_BOOTS);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SENSOR_GLASSES), SENSOR_GLASSES);

        // TOOLS + WEAPONS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_SWORD), HEAVY_DUTY_SWORD);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_SHOVEL), HEAVY_DUTY_SHOVEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_PICKAXE), HEAVY_DUTY_PICKAXE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_AXE), HEAVY_DUTY_AXE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_HOE), HEAVY_DUTY_HOE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_SWORD), DESH_SWORD);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_SHOVEL), DESH_SHOVEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_PICKAXE), DESH_PICKAXE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_AXE), DESH_AXE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.DESH_HOE), DESH_HOE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_SWORD), TITANIUM_SWORD);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_SHOVEL), TITANIUM_SHOVEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_PICKAXE), TITANIUM_PICKAXE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_AXE), TITANIUM_AXE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANIUM_HOE), TITANIUM_HOE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.STANDARD_WRENCH), STANDARD_WRENCH);

        // BATTERIES
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BATTERY), BATTERY);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.INFINITE_BATTERY), INFINITE_BATTERY);

        //FLUID BUCKETS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CRUDE_OIL_BUCKET), CRUDE_OIL_BUCKET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.FUEL_BUCKET), FUEL_BUCKET);

        //GALACTICRAFT INVENTORY
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.PARACHUTE), PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ORANGE_PARACHUTE), ORANGE_PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.MAGENTA_PARACHUTE), MAGENTA_PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.LIGHT_BLUE_PARACHUTE), LIGHT_BLUE_PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.YELLOW_PARACHUTE), YELLOW_PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.LIME_PARACHUTE), LIME_PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.PINK_PARACHUTE), PINK_PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.GRAY_PARACHUTE), GRAY_PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.LIGHT_GRAY_PARACHUTE), LIGHT_GRAY_PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CYAN_PARACHUTE), CYAN_PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.PURPLE_PARACHUTE), PURPLE_PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BLUE_PARACHUTE), BLUE_PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BROWN_PARACHUTE), BROWN_PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.GREEN_PARACHUTE), GREEN_PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.RED_PARACHUTE), RED_PARACHUTE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BLACK_PARACHUTE), BLACK_PARACHUTE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.OXYGEN_MASK), OXYGEN_MASK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.OXYGEN_GEAR), OXYGEN_GEAR);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SMALL_OXYGEN_TANK), SMALL_OXYGEN_TANK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.MEDIUM_OXYGEN_TANK), MEDIUM_OXYGEN_TANK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.LARGE_OXYGEN_TANK), LARGE_OXYGEN_TANK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.INFINITE_OXYGEN_TANK), INFINITE_OXYGEN_TANK);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SHIELD_CONTROLLER), SHIELD_CONTROLLER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.FREQUENCY_MODULE), FREQUENCY_MODULE);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.THERMAL_PADDING_HELMET), THERMAL_PADDING_HELMET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.THERMAL_PADDING_CHESTPIECE), THERMAL_PADDING_CHESTPIECE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.THERMAL_PADDING_LEGGINGS), THERMAL_PADDING_LEGGINGS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.THERMAL_PADDING_BOOTS), THERMAL_PADDING_BOOTS);

        // ROCKETS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ROCKET), ROCKET);

        // ROCKET PIECES
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ROCKET_FINS), ROCKET_FINS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ROCKET_ENGINE), ROCKET_ENGINE);

        // SCHEMATICS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_ROCKET_CONE_SCHEMATIC), BASIC_ROCKET_CONE_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_ROCKET_BODY_SCHEMATIC), BASIC_ROCKET_BODY_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_ROCKET_FINS_SCHEMATIC), BASIC_ROCKET_FINS_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_ROCKET_BOTTOM_SCHEMATIC), BASIC_ROCKET_BOTTOM_SCHEMATIC);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TIER_2_ROCKET_SCHEMATIC), TIER_2_ROCKET_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CARGO_ROCKET_SCHEMATIC), CARGO_ROCKET_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.MOON_BUGGY_SCHEMATIC), MOON_BUGGY_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TIER_3_ROCKET_SCHEMATIC), TIER_3_ROCKET_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ASTRO_MINER_SCHEMATIC), ASTRO_MINER_SCHEMATIC);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.AIR_LOCK_FRAME), AIR_LOCK_FRAME);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.AIR_LOCK_CONTROLLER), AIR_LOCK_CONTROLLER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.AIR_LOCK_SEAL), AIR_LOCK_SEAL);

        DispenserBlock.registerBehavior(FUEL_BUCKET, DispenserBlock.DISPENSER_REGISTRY.get(Items.WATER_BUCKET));
        DispenserBlock.registerBehavior(CRUDE_OIL_BUCKET, DispenserBlock.DISPENSER_REGISTRY.get(Items.WATER_BUCKET));
    }
}
