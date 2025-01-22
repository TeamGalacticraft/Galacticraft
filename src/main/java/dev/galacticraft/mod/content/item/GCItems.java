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

package dev.galacticraft.mod.content.item;

import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.api.rocket.RocketPrefabs;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.GCRegistry;
import dev.galacticraft.mod.content.GCRocketParts;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class GCItems {
    public static final GCRegistry<Item> ITEMS = new GCRegistry<>(BuiltInRegistries.ITEM);
    public static final List<ItemLike> HIDDEN_ITEMS = new ArrayList<>(1);

    // === START BLOCKS ===
    // TORCHES
    public static final Item GLOWSTONE_TORCH = ITEMS.register(Constant.Block.GLOWSTONE_TORCH, new StandingAndWallBlockItem(GCBlocks.GLOWSTONE_TORCH, GCBlocks.GLOWSTONE_WALL_TORCH, new Item.Properties(), Direction.DOWN));
    public static final Item UNLIT_TORCH = ITEMS.register(Constant.Block.UNLIT_TORCH, new StandingAndWallBlockItem(GCBlocks.UNLIT_TORCH, GCBlocks.UNLIT_WALL_TORCH, new Item.Properties(), Direction.DOWN));

    // LANTERNS
    public static final Item GLOWSTONE_LANTERN = ITEMS.register(Constant.Block.GLOWSTONE_LANTERN, new BlockItem(GCBlocks.GLOWSTONE_LANTERN, new Item.Properties()));
    public static final Item UNLIT_LANTERN = ITEMS.register(Constant.Block.UNLIT_LANTERN, new BlockItem(GCBlocks.UNLIT_LANTERN, new Item.Properties()));

    // MOON NATURAL
    public static final Item MOON_TURF = new BlockItem(GCBlocks.MOON_TURF, new Item.Properties());
    public static final Item MOON_DIRT = new BlockItem(GCBlocks.MOON_DIRT, new Item.Properties());
    public static final Item MOON_DIRT_PATH = new BlockItem(GCBlocks.MOON_DIRT_PATH, new Item.Properties());
    public static final Item MOON_SURFACE_ROCK = new BlockItem(GCBlocks.MOON_SURFACE_ROCK, new Item.Properties());

    public static final Item MOON_ROCK = new BlockItem(GCBlocks.MOON_ROCK, new Item.Properties());
    public static final Item MOON_ROCK_SLAB = new BlockItem(GCBlocks.MOON_ROCK_SLAB, new Item.Properties());
    public static final Item MOON_ROCK_STAIRS = new BlockItem(GCBlocks.MOON_ROCK_STAIRS, new Item.Properties());
    public static final Item MOON_ROCK_WALL = new BlockItem(GCBlocks.MOON_ROCK_WALL, new Item.Properties());

    public static final Item MOON_ROCK_BRICK = new BlockItem(GCBlocks.MOON_ROCK_BRICK, new Item.Properties());
    public static final Item MOON_ROCK_BRICK_SLAB = new BlockItem(GCBlocks.MOON_ROCK_BRICK_SLAB, new Item.Properties());
    public static final Item MOON_ROCK_BRICK_STAIRS = new BlockItem(GCBlocks.MOON_ROCK_BRICK_STAIRS, new Item.Properties());
    public static final Item MOON_ROCK_BRICK_WALL = new BlockItem(GCBlocks.MOON_ROCK_BRICK_WALL, new Item.Properties());

    public static final Item CRACKED_MOON_ROCK_BRICK = new BlockItem(GCBlocks.CRACKED_MOON_ROCK_BRICK, new Item.Properties());
    public static final Item CRACKED_MOON_ROCK_BRICK_SLAB = new BlockItem(GCBlocks.CRACKED_MOON_ROCK_BRICK_SLAB, new Item.Properties());
    public static final Item CRACKED_MOON_ROCK_BRICK_STAIRS = new BlockItem(GCBlocks.CRACKED_MOON_ROCK_BRICK_STAIRS, new Item.Properties());
    public static final Item CRACKED_MOON_ROCK_BRICK_WALL = new BlockItem(GCBlocks.CRACKED_MOON_ROCK_BRICK_WALL, new Item.Properties());

    public static final Item POLISHED_MOON_ROCK = new BlockItem(GCBlocks.POLISHED_MOON_ROCK, new Item.Properties());
    public static final Item POLISHED_MOON_ROCK_SLAB = new BlockItem(GCBlocks.POLISHED_MOON_ROCK_SLAB, new Item.Properties());
    public static final Item POLISHED_MOON_ROCK_STAIRS = new BlockItem(GCBlocks.POLISHED_MOON_ROCK_STAIRS, new Item.Properties());
    public static final Item POLISHED_MOON_ROCK_WALL = new BlockItem(GCBlocks.POLISHED_MOON_ROCK_WALL, new Item.Properties());

    public static final Item CHISELED_MOON_ROCK_BRICK = new BlockItem(GCBlocks.CHISELED_MOON_ROCK_BRICK, new Item.Properties());
    public static final Item MOON_ROCK_PILLAR = new BlockItem(GCBlocks.MOON_ROCK_PILLAR, new Item.Properties());

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

    public static final Item FALLEN_METEOR = new BlockItem(GCBlocks.FALLEN_METEOR, new Item.Properties());

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
    public static final Item FLUID_PIPE_WALKWAY = new BlockItem(GCBlocks.FLUID_PIPE_WALKWAY, new Item.Properties());
    public static final Item WIRE_WALKWAY = new BlockItem(GCBlocks.WIRE_WALKWAY, new Item.Properties());
    public static final Item TIN_LADDER = new BlockItem(GCBlocks.TIN_LADDER, new Item.Properties());
    public static final Item IRON_GRATING = new BlockItem(GCBlocks.IRON_GRATING, new Item.Properties());

    // SPECIAL
    public static final Item ALUMINUM_WIRE = new BlockItem(GCBlocks.ALUMINUM_WIRE, new Item.Properties());
    public static final Item SEALABLE_ALUMINUM_WIRE = new BlockItem(GCBlocks.SEALABLE_ALUMINUM_WIRE, new Item.Properties());
    public static final Item HEAVY_SEALABLE_ALUMINUM_WIRE = new BlockItem(GCBlocks.HEAVY_SEALABLE_ALUMINUM_WIRE, new Item.Properties());
    public static final Item GLASS_FLUID_PIPE = new BlockItem(GCBlocks.GLASS_FLUID_PIPE, new Item.Properties());
    public static final Item FUELING_PAD = ITEMS.register(Constant.Block.FUELING_PAD, new BlockItem(GCBlocks.FUELING_PAD, new Item.Properties()));
    public static final Item ROCKET_LAUNCH_PAD = ITEMS.register(Constant.Block.ROCKET_LAUNCH_PAD, new BlockItem(GCBlocks.ROCKET_LAUNCH_PAD, new Item.Properties()));

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
    public static final Item MOON_CHEESE_WHEEL = new BlockItem(GCBlocks.MOON_CHEESE_WHEEL, new Item.Properties());

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
    public static final Item CAVERNOUS_VINES = new BlockItem(GCBlocks.CAVERNOUS_VINES, new Item.Properties());

    // MISC MACHINES
    public static final Item CRYOGENIC_CHAMBER = new BlockItem(GCBlocks.CRYOGENIC_CHAMBER, new Item.Properties());
    public static final Item PLAYER_TRANSPORT_TUBE = new BlockItem(GCBlocks.PLAYER_TRANSPORT_TUBE, new Item.Properties());
    public static final Item AIR_LOCK_FRAME = new BlockItem(GCBlocks.AIR_LOCK_FRAME, new Item.Properties());
    public static final Item AIR_LOCK_CONTROLLER = new BlockItem(GCBlocks.AIR_LOCK_CONTROLLER, new Item.Properties());
    public static final Item AIR_LOCK_SEAL = new BlockItem(GCBlocks.AIR_LOCK_SEAL, new Item.Properties());

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
    public static final Item SILICON = new Item(new Item.Properties());
    
    public static final Item RAW_METEORIC_IRON = new Item(new Item.Properties());
    public static final Item METEORIC_IRON_INGOT = new Item(new Item.Properties());
    public static final Item METEORIC_IRON_NUGGET = new Item(new Item.Properties());
    public static final Item COMPRESSED_METEORIC_IRON = new Item(new Item.Properties());

    public static final Item OLIVINE_SHARD = registerItem(Constant.Item.OLIVINE_SHARD, new Item(new Item.Properties()));

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
    public static final Item SOLAR_DUST = new Item(new Item.Properties());
    public static final Item BASIC_WAFER = new Item(new Item.Properties());
    public static final Item ADVANCED_WAFER = new Item(new Item.Properties());
    public static final Item BEAM_CORE = new Item(new Item.Properties());
    public static final Item CANVAS = new Item(new Item.Properties());
    
    public static final Item FLUID_MANIPULATOR = new Item(new Item.Properties());
    public static final Item OXYGEN_CONCENTRATOR = new Item(new Item.Properties());
    public static final Item OXYGEN_FAN = new Item(new Item.Properties());
    public static final Item OXYGEN_VENT = ITEMS.register(Constant.Item.OXYGEN_VENT, new Item(new Item.Properties()));
    public static final Item SENSOR_LENS = new Item(new Item.Properties());
    public static final Item BLUE_SOLAR_WAFER = new Item(new Item.Properties());
    public static final Item SINGLE_SOLAR_MODULE = new Item(new Item.Properties());
    public static final Item FULL_SOLAR_PANEL = new Item(new Item.Properties());
    public static final Item SOLAR_ARRAY_WAFER = new Item(new Item.Properties());
    public static final Item STEEL_POLE = new Item(new Item.Properties());
    public static final Item COPPER_CANISTER = new Item(new Item.Properties());
    public static final Item TIN_CANISTER = ITEMS.register(Constant.Item.TIN_CANISTER, new Item(new Item.Properties()));
    public static final Item THERMAL_CLOTH = new Item(new Item.Properties());
    public static final Item ISOTHERMAL_FABRIC = new Item(new Item.Properties());
    public static final Item ORION_DRIVE = new Item(new Item.Properties());
    public static final Item ATMOSPHERIC_VALVE = new Item(new Item.Properties());
    public static final Item AMBIENT_THERMAL_CONTROLLER = new Item(new Item.Properties());
    
    // FOOD
    public static final Item CHEESE_CURD = new Item(new Item.Properties().food(GCFoodComponent.CHEESE_CURD));
    
    public static final Item CHEESE_SLICE = ITEMS.register(Constant.Item.CHEESE_SLICE, new Item(new Item.Properties().food(GCFoodComponent.CHEESE_SLICE)));
    public static final Item BURGER_BUN = ITEMS.register(Constant.Item.BURGER_BUN, new Item(new Item.Properties().food(GCFoodComponent.BURGER_BUN)));
    public static final Item GROUND_BEEF = ITEMS.register(Constant.Item.GROUND_BEEF, new Item(new Item.Properties().food(GCFoodComponent.GROUND_BEEF)));
    public static final Item BEEF_PATTY = ITEMS.register(Constant.Item.BEEF_PATTY, new Item(new Item.Properties().food(GCFoodComponent.BEEF_PATTY)));
    public static final Item CHEESEBURGER = ITEMS.register(Constant.Item.CHEESEBURGER, new Item(new Item.Properties().food(GCFoodComponent.CHEESEBURGER)));
    
    public static final Item CANNED_DEHYDRATED_APPLE = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_APPLE));
    public static final Item CANNED_DEHYDRATED_CARROT = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_CARROT));
    public static final Item CANNED_DEHYDRATED_MELON = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_MELON));
    public static final Item CANNED_DEHYDRATED_POTATO = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.DEHYDRATED_POTATO));
    public static final Item CANNED_BEEF = new CannedFoodItem(new Item.Properties().food(GCFoodComponent.CANNED_BEEF));
    
    // ROCKET PLATES
    public static final Item TIER_1_HEAVY_DUTY_PLATE = ITEMS.register(Constant.Item.TIER_1_HEAVY_DUTY_PLATE, new Item(new Item.Properties()));
    public static final Item TIER_2_HEAVY_DUTY_PLATE = ITEMS.register(Constant.Item.TIER_2_HEAVY_DUTY_PLATE, new Item(new Item.Properties()));
    public static final Item TIER_3_HEAVY_DUTY_PLATE = ITEMS.register(Constant.Item.TIER_3_HEAVY_DUTY_PLATE, new Item(new Item.Properties()));

    // THROWABLE METEOR CHUNKS
    public static final Item THROWABLE_METEOR_CHUNK = new ThrowableMeteorChunkItem(new Item.Properties().stacksTo(16));
    public static final Item HOT_THROWABLE_METEOR_CHUNK = new HotThrowableMeteorChunkItem(new Item.Properties().stacksTo(16));

    // ARMOR
    public static final Item HEAVY_DUTY_HELMET = new ArmorItem(GCArmorMaterials.HEAVY_DUTY, ArmorItem.Type.HELMET, new Item.Properties());
    public static final Item HEAVY_DUTY_CHESTPLATE = new ArmorItem(GCArmorMaterials.HEAVY_DUTY, ArmorItem.Type.CHESTPLATE, new Item.Properties());
    public static final Item HEAVY_DUTY_LEGGINGS = new ArmorItem(GCArmorMaterials.HEAVY_DUTY, ArmorItem.Type.LEGGINGS, new Item.Properties());
    public static final Item HEAVY_DUTY_BOOTS = new ArmorItem(GCArmorMaterials.HEAVY_DUTY, ArmorItem.Type.BOOTS, new Item.Properties());

    public static final Item DESH_HELMET = new ArmorItem(GCArmorMaterials.DESH, ArmorItem.Type.HELMET, new Item.Properties());
    public static final Item DESH_CHESTPLATE = new ArmorItem(GCArmorMaterials.DESH, ArmorItem.Type.CHESTPLATE, new Item.Properties());
    public static final Item DESH_LEGGINGS = new ArmorItem(GCArmorMaterials.DESH, ArmorItem.Type.LEGGINGS, new Item.Properties());
    public static final Item DESH_BOOTS = new ArmorItem(GCArmorMaterials.DESH, ArmorItem.Type.BOOTS, new Item.Properties());

    public static final Item TITANIUM_HELMET = new ArmorItem(GCArmorMaterials.TITANIUM, ArmorItem.Type.HELMET, new Item.Properties());
    public static final Item TITANIUM_CHESTPLATE = new ArmorItem(GCArmorMaterials.TITANIUM, ArmorItem.Type.CHESTPLATE, new Item.Properties());
    public static final Item TITANIUM_LEGGINGS = new ArmorItem(GCArmorMaterials.TITANIUM, ArmorItem.Type.LEGGINGS, new Item.Properties());
    public static final Item TITANIUM_BOOTS = new ArmorItem(GCArmorMaterials.TITANIUM, ArmorItem.Type.BOOTS, new Item.Properties());

    public static final Item SENSOR_GLASSES = new ArmorItem(GCArmorMaterials.SENSOR_GLASSES, ArmorItem.Type.HELMET, new Item.Properties());

    // TOOLS + WEAPONS
    public static final Item HEAVY_DUTY_SWORD = new BrittleSwordItem(GCTiers.STEEL, new Item.Properties().attributes(SwordItem.createAttributes(GCTiers.STEEL, 3, -2.4F)));
    public static final Item HEAVY_DUTY_SHOVEL = new ShovelItem(GCTiers.STEEL, new Item.Properties().attributes(ShovelItem.createAttributes(GCTiers.STEEL, -1.5F, -3.0F)));
    public static final Item HEAVY_DUTY_PICKAXE = new PickaxeItem(GCTiers.STEEL, new Item.Properties().attributes(PickaxeItem.createAttributes(GCTiers.STEEL, 1, -2.8F)));
    public static final Item HEAVY_DUTY_AXE = new AxeItem(GCTiers.STEEL, new Item.Properties().attributes(AxeItem.createAttributes(GCTiers.STEEL, 6.0F, -3.1F)));
    public static final Item HEAVY_DUTY_HOE = new HoeItem(GCTiers.STEEL, new Item.Properties().attributes(HoeItem.createAttributes(GCTiers.STEEL, -2, -1.0F)));

    public static final Item DESH_SWORD = new SwordItem(GCTiers.DESH, new Item.Properties().attributes(SwordItem.createAttributes(GCTiers.DESH, 3, -2.4F)));
    public static final Item DESH_SHOVEL = new ShovelItem(GCTiers.DESH, new Item.Properties().attributes(ShovelItem.createAttributes(GCTiers.DESH, -1.5F, -3.0F)));
    public static final Item DESH_PICKAXE = new PickaxeItem(GCTiers.DESH, new Item.Properties().attributes(PickaxeItem.createAttributes(GCTiers.DESH, 1.0F, -2.8F)));
    public static final Item DESH_AXE = new AxeItem(GCTiers.DESH, new Item.Properties().attributes(AxeItem.createAttributes(GCTiers.DESH, 6.0F, -3.1F)));
    public static final Item DESH_HOE = new HoeItem(GCTiers.DESH, new Item.Properties().attributes(HoeItem.createAttributes(GCTiers.DESH, -3.0F, -1.0F)));

    public static final Item TITANIUM_SWORD = new BrittleSwordItem(GCTiers.TITANIUM, new Item.Properties().attributes(SwordItem.createAttributes(GCTiers.TITANIUM, 3, -2.4F)));
    public static final Item TITANIUM_SHOVEL = new ShovelItem(GCTiers.TITANIUM, new Item.Properties().attributes(ShovelItem.createAttributes(GCTiers.TITANIUM, -1.5F, -3.0F)));
    public static final Item TITANIUM_PICKAXE = new PickaxeItem(GCTiers.TITANIUM, new Item.Properties().attributes(PickaxeItem.createAttributes(GCTiers.TITANIUM, 1.0F, -2.8F)));
    public static final Item TITANIUM_AXE = new AxeItem(GCTiers.TITANIUM, new Item.Properties().attributes(AxeItem.createAttributes(GCTiers.TITANIUM, 6.0F, -3.1F)));
    public static final Item TITANIUM_HOE = new HoeItem(GCTiers.TITANIUM, new Item.Properties().attributes(HoeItem.createAttributes(GCTiers.TITANIUM, -3.0F, -1.0F)));

    public static final Item STANDARD_WRENCH = new StandardWrenchItem(new Item.Properties().durability(256));

    // SMITHING TEMPLATES
    public static final Item TITANTIUM_UPGRADE_SMITHING_TEMPLATE = new SmithingTemplateItem(
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_APPLIES_TO),
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_INGREDIENTS),
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_DESCRIPTION),
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_BASE_SLOT_DESCRIPTION),
            Component.translatable(Translations.Misc.UPGRADE_TITANIUM_ADDITIONS_SLOT_DESCRIPTON),
            List.of(ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet"),
                    ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate"),
                    ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings"),
                    ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_hoe"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_axe"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_sword"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_shovel"),
                    ResourceLocation.withDefaultNamespace("item/empty_slot_pickaxe")),
            List.of(ResourceLocation.withDefaultNamespace("item/empty_slot_ingot"))
    );
    // 		this.appliesTo = component;
    //		this.ingredients = component2;
    //		this.upgradeDescription = component3;
    //		this.baseSlotDescription = component4;
    //		this.additionsSlotDescription = component5;

    // BATTERIES
    public static final Item BATTERY = new BatteryItem(new Item.Properties().stacksTo(1), 15000, 500);
    public static final Item INFINITE_BATTERY = new InfiniteBatteryItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));

    //FLUID BUCKETS
    public static final Item CRUDE_OIL_BUCKET = new BucketItem(GCFluids.CRUDE_OIL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
    public static final Item FUEL_BUCKET = new BucketItem(GCFluids.FUEL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
    public static final Item SULFURIC_ACID_BUCKET = new BucketItem(GCFluids.SULFURIC_ACID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));

    //GALACTICRAFT INVENTORY
    public static final GCRegistry.ColorSet<ParachuteItem> PARACHUTE = ITEMS.registerColored(Constant.Item.PARACHUTE, color -> new ParachuteItem(color, new Item.Properties()));

    public static final Item OXYGEN_MASK = new AccessoryItem(new Item.Properties());
    public static final Item OXYGEN_GEAR = new AccessoryItem(new Item.Properties());

    public static final Item SMALL_OXYGEN_TANK = new OxygenTankItem(new Item.Properties(), 1620 * 10); // 16200 ticks
    public static final Item MEDIUM_OXYGEN_TANK = new OxygenTankItem(new Item.Properties(), 1620 * 20); //32400 ticks
    public static final Item LARGE_OXYGEN_TANK = new OxygenTankItem(new Item.Properties(), 1620 * 30); //48600 ticks
    public static final Item INFINITE_OXYGEN_TANK = new InfiniteOxygenTankItem(new Item.Properties().rarity(Rarity.EPIC));

    public static final Item SHIELD_CONTROLLER = new AccessoryItem(new Item.Properties());
    public static final Item FREQUENCY_MODULE = new AccessoryItem(new Item.Properties());

    public static final Item THERMAL_PADDING_HELMET = new AccessoryItem(new Item.Properties());
    public static final Item THERMAL_PADDING_CHESTPIECE = new AccessoryItem(new Item.Properties());
    public static final Item THERMAL_PADDING_LEGGINGS = new AccessoryItem(new Item.Properties());
    public static final Item THERMAL_PADDING_BOOTS = new AccessoryItem(new Item.Properties());
    // Vehicles
    public static final Item BUGGY = ITEMS.register(Constant.Item.BUGGY, new BuggyItem(new Item.Properties().stacksTo(1)));
    public static final Item ROCKET = ITEMS.register(Constant.Item.ROCKET, new RocketItem(new Item.Properties()
            .component(GCDataComponents.ROCKET_DATA, RocketPrefabs.TIER_1)
            .stacksTo(1)));

    // ROCKET PIECES
    public static final Item NOSE_CONE = ITEMS.register(Constant.Item.NOSE_CONE, new Item(new Item.Properties()));
    public static final Item HEAVY_NOSE_CONE = ITEMS.register(Constant.Item.HEAVY_NOSE_CONE, new Item(new Item.Properties()));
    public static final Item ROCKET_FIN = ITEMS.register(Constant.Item.ROCKET_FIN, new Item(new Item.Properties()));
    public static final Item ROCKET_ENGINE = ITEMS.register(Constant.Item.ROCKET_ENGINE, new Item(new Item.Properties()));

    // SCHEMATICS
    public static final Item BASIC_ROCKET_CONE_SCHEMATIC = new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), GCRocketParts.TIER_1_CONE);
    public static final Item BASIC_ROCKET_BODY_SCHEMATIC = new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), GCRocketParts.TIER_1_BODY);
    public static final Item BASIC_ROCKET_FINS_SCHEMATIC = new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), GCRocketParts.TIER_1_FIN);
    public static final Item BASIC_ROCKET_ENGINE_SCHEMATIC = new RocketPartSchematic(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), GCRocketParts.TIER_1_ENGINE);

    public static final Item TIER_2_ROCKET_SCHEMATIC = new SchematicItem(new Item.Properties());
    public static final Item CARGO_ROCKET_SCHEMATIC = new SchematicItem(new Item.Properties());
    public static final Item MOON_BUGGY_SCHEMATIC = new SchematicItem(new Item.Properties());
    public static final Item TIER_3_ROCKET_SCHEMATIC = new SchematicItem(new Item.Properties());
    public static final Item ASTRO_MINER_SCHEMATIC = new SchematicItem(new Item.Properties());

    public static Item registerItem(String id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, Constant.id(id), item);
    }
    
    public static void register() {
        // === START BLOCKS ===

        // MOON NATURAL
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_TURF), MOON_TURF);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_DIRT), MOON_DIRT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_DIRT_PATH), MOON_DIRT_PATH);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_SURFACE_ROCK), MOON_SURFACE_ROCK);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_ROCK), MOON_ROCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_ROCK_SLAB), MOON_ROCK_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_ROCK_STAIRS), MOON_ROCK_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_ROCK_WALL), MOON_ROCK_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_ROCK_BRICK), MOON_ROCK_BRICK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_ROCK_BRICK_SLAB), MOON_ROCK_BRICK_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_ROCK_BRICK_STAIRS), MOON_ROCK_BRICK_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_ROCK_BRICK_WALL), MOON_ROCK_BRICK_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.CRACKED_MOON_ROCK_BRICK), CRACKED_MOON_ROCK_BRICK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.CRACKED_MOON_ROCK_BRICK_SLAB), CRACKED_MOON_ROCK_BRICK_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.CRACKED_MOON_ROCK_BRICK_STAIRS), CRACKED_MOON_ROCK_BRICK_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.CRACKED_MOON_ROCK_BRICK_WALL), CRACKED_MOON_ROCK_BRICK_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.POLISHED_MOON_ROCK), POLISHED_MOON_ROCK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.POLISHED_MOON_ROCK_SLAB), POLISHED_MOON_ROCK_SLAB);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.POLISHED_MOON_ROCK_STAIRS), POLISHED_MOON_ROCK_STAIRS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.POLISHED_MOON_ROCK_WALL), POLISHED_MOON_ROCK_WALL);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.CHISELED_MOON_ROCK_BRICK), CHISELED_MOON_ROCK_BRICK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_ROCK_PILLAR), MOON_ROCK_PILLAR);

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

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.FALLEN_METEOR), FALLEN_METEOR);

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
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.FLUID_PIPE_WALKWAY), FLUID_PIPE_WALKWAY);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.WIRE_WALKWAY), WIRE_WALKWAY);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.TIN_LADDER), TIN_LADDER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.IRON_GRATING), IRON_GRATING);

        // SPECIAL
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.ALUMINUM_WIRE), ALUMINUM_WIRE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.SEALABLE_ALUMINUM_WIRE), SEALABLE_ALUMINUM_WIRE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.HEAVY_SEALABLE_ALUMINUM_WIRE), HEAVY_SEALABLE_ALUMINUM_WIRE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.GLASS_FLUID_PIPE), GLASS_FLUID_PIPE);

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
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.MOON_CHEESE_WHEEL), MOON_CHEESE_WHEEL);

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
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Block.CAVERNOUS_VINES), CAVERNOUS_VINES);

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
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SILICON), SILICON);

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
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SOLAR_DUST), SOLAR_DUST);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_WAFER), BASIC_WAFER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ADVANCED_WAFER), ADVANCED_WAFER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BEAM_CORE), BEAM_CORE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANVAS), CANVAS);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.FLUID_MANIPULATOR), FLUID_MANIPULATOR);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.OXYGEN_CONCENTRATOR), OXYGEN_CONCENTRATOR);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.OXYGEN_FAN), OXYGEN_FAN);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SENSOR_LENS), SENSOR_LENS);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BLUE_SOLAR_WAFER), BLUE_SOLAR_WAFER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SINGLE_SOLAR_MODULE), SINGLE_SOLAR_MODULE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.FULL_SOLAR_PANEL), FULL_SOLAR_PANEL);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SOLAR_ARRAY_WAFER), SOLAR_ARRAY_WAFER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.STEEL_POLE), STEEL_POLE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.COPPER_CANISTER), COPPER_CANISTER);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.THERMAL_CLOTH), THERMAL_CLOTH);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ISOTHERMAL_FABRIC), ISOTHERMAL_FABRIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ORION_DRIVE), ORION_DRIVE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.ATMOSPHERIC_VALVE), ATMOSPHERIC_VALVE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.AMBIENT_THERMAL_CONTROLLER), AMBIENT_THERMAL_CONTROLLER);

        // FOOD
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CHEESE_CURD), CHEESE_CURD);

        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_APPLE), CANNED_DEHYDRATED_APPLE);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_CARROT), CANNED_DEHYDRATED_CARROT);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_MELON), CANNED_DEHYDRATED_MELON);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_POTATO), CANNED_DEHYDRATED_POTATO);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CANNED_BEEF), CANNED_BEEF);

        // THROWABLE METEOR CHUNKS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.THROWABLE_METEOR_CHUNK), THROWABLE_METEOR_CHUNK);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.HOT_THROWABLE_METEOR_CHUNK), HOT_THROWABLE_METEOR_CHUNK);

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

        // SMITHING TEMPLATES
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.TITANTIUM_UPGRADE_SMITHING_TEMPLATE), TITANTIUM_UPGRADE_SMITHING_TEMPLATE);

        // BATTERIES
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BATTERY), BATTERY);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.INFINITE_BATTERY), INFINITE_BATTERY);

        //FLUID BUCKETS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.CRUDE_OIL_BUCKET), CRUDE_OIL_BUCKET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.FUEL_BUCKET), FUEL_BUCKET);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.SULFURIC_ACID_BUCKET), SULFURIC_ACID_BUCKET);

        //GALACTICRAFT INVENTORY
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

        // SCHEMATICS
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_ROCKET_CONE_SCHEMATIC), BASIC_ROCKET_CONE_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_ROCKET_BODY_SCHEMATIC), BASIC_ROCKET_BODY_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_ROCKET_FINS_SCHEMATIC), BASIC_ROCKET_FINS_SCHEMATIC);
        Registry.register(BuiltInRegistries.ITEM, Constant.id(Constant.Item.BASIC_ROCKET_ENGINE_SCHEMATIC), BASIC_ROCKET_ENGINE_SCHEMATIC);

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
        DispenserBlock.registerBehavior(SULFURIC_ACID_BUCKET, DispenserBlock.DISPENSER_REGISTRY.get(Items.WATER_BUCKET));
    }
}
