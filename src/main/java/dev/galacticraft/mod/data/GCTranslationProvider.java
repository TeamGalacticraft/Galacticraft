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

package dev.galacticraft.mod.data;

import dev.galacticraft.api.data.TranslationProvider;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.content.GCBlockRegistry.DecorationSet;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.GCRocketParts;
import dev.galacticraft.mod.content.GCStats;
import dev.galacticraft.mod.content.entity.damage.GCDamageTypes;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.tag.GCItemTags;
import dev.galacticraft.mod.world.biome.GCBiomes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.galacticraft.mod.util.Translations.*;

public class GCTranslationProvider extends TranslationProvider {
    public GCTranslationProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void generateTranslations(HolderLookup.@NotNull Provider registries) {
        this.generateBlockTranslations();
        this.generateItemTranslations();
        this.generateTagTranslations();
        this.generateGasTranslations();
        this.generateEntityTranslations();
        this.generateCelestialBodyTranslations();
        this.generateBiomeTranslations();
        this.generateChatTranslations();
        this.generateRocketPartTranslations();
        this.generateSmithingTranslations();
        this.generateTooltipTranslations();
        this.generateConfigTranslations();
        this.generateSpaceRaceTranslations();
        this.generateSolarPanelTranslations();
        this.generateMachineStatusTranslations();
        this.generateCelestialSelectionTranslations();
        this.generateUiTranslations();
        this.generateStatsTranslations();

        // Tabs in the creative inventory
        this.add(ItemGroup.BLOCKS, "Galacticraft Blocks");
        this.add(ItemGroup.ITEMS, "Galacticraft Items");
        this.add(ItemGroup.MACHINES, "Galacticraft Machines");
        this.add(ItemGroup.CANNED_FOOD, "Canned Food");

        this.add(RecipeCategory.CIRCUIT_FABRICATOR, "Circuit Fabricating");
        this.add(RecipeCategory.COMPRESSOR, "Compressing");
        this.add(RecipeCategory.ROCKET_WORKBENCH, "Rocket Crafting");

        this.add(BannerPattern.ROCKET + ".white", "White Rocket");
        this.add(BannerPattern.ROCKET + ".orange", "Orange Rocket");
        this.add(BannerPattern.ROCKET + ".magenta", "Magenta Rocket");
        this.add(BannerPattern.ROCKET + ".light_blue", "Light Blue Rocket");
        this.add(BannerPattern.ROCKET + ".yellow", "Yellow Rocket");
        this.add(BannerPattern.ROCKET + ".lime", "Lime Rocket");
        this.add(BannerPattern.ROCKET + ".pink", "Pink Rocket");
        this.add(BannerPattern.ROCKET + ".gray", "Gray Rocket");
        this.add(BannerPattern.ROCKET + ".light_gray", "Light Gray Rocket");
        this.add(BannerPattern.ROCKET + ".cyan", "Cyan Rocket");
        this.add(BannerPattern.ROCKET + ".purple", "Purple Rocket");
        this.add(BannerPattern.ROCKET + ".blue", "Blue Rocket");
        this.add(BannerPattern.ROCKET + ".brown", "Brown Rocket");
        this.add(BannerPattern.ROCKET + ".red", "Red Rocket");
        this.add(BannerPattern.ROCKET + ".green", "Green Rocket");
        this.add(BannerPattern.ROCKET + ".black", "Black Rocket");
    }

    protected void generateBlockTranslations() {
        // TORCHES
        this.block(GCBlocks.GLOWSTONE_TORCH, "Glowstone Torch");
        this.block(GCBlocks.GLOWSTONE_WALL_TORCH, "Glowstone Torch");
        this.block(GCBlocks.UNLIT_TORCH, "Unlit Torch");
        this.block(GCBlocks.UNLIT_WALL_TORCH, "Unlit Torch");

        // LANTERNS
        this.block(GCBlocks.GLOWSTONE_LANTERN, "Glowstone Lantern");
        this.block(GCBlocks.UNLIT_LANTERN, "Unlit Lantern");

        // FLUIDS
        this.block(GCBlocks.CRUDE_OIL, "Crude Oil");
        this.block(GCBlocks.FUEL, "Fuel");
        this.block(GCBlocks.SULFURIC_ACID, "Sulfuric Acid");

        // DECORATION BLOCKS
        this.decorationSet(GCBlocks.ALUMINUM_DECORATION, "Aluminum Decoration");
        this.decorationSet(GCBlocks.BRONZE_DECORATION, "Bronze Decoration");
        this.decorationSet(GCBlocks.COPPER_DECORATION, "Copper Decoration");
        this.decorationSet(GCBlocks.IRON_DECORATION, "Iron Decoration");
        this.decorationSet(GCBlocks.METEORIC_IRON_DECORATION, "Meteoric Iron Decoration");
        this.decorationSet(GCBlocks.STEEL_DECORATION, "Steel Decoration");
        this.decorationSet(GCBlocks.TIN_DECORATION, "Tin Decoration");
        this.decorationSet(GCBlocks.TITANIUM_DECORATION, "Titanium Decoration");
        this.decorationSet(GCBlocks.DARK_DECORATION, "Dark Decoration");

        // MOON NATURAL
        this.block(GCBlocks.MOON_TURF, "Moon Turf");
        this.block(GCBlocks.MOON_DIRT, "Moon Dirt");
        this.block(GCBlocks.MOON_DIRT_PATH, "Moon Dirt Path");
        this.block(GCBlocks.MOON_SURFACE_ROCK, "Moon Surface Rock");
        this.block(GCBlocks.MOON_DUNGEON_BRICK, "Moon Dungeon Brick");

        this.block(GCBlocks.MOON_ROCK, "Moon Rock");
        this.block(GCBlocks.MOON_ROCK_SLAB, "Moon Rock Slab");
        this.block(GCBlocks.MOON_ROCK_STAIRS, "Moon Rock Stairs");
        this.block(GCBlocks.MOON_ROCK_WALL, "Moon Rock Wall");

        this.block(GCBlocks.MOON_ROCK_BRICK, "Moon Rock Brick");
        this.block(GCBlocks.MOON_ROCK_BRICK_SLAB, "Moon Rock Brick Slab");
        this.block(GCBlocks.MOON_ROCK_BRICK_STAIRS, "Moon Rock Brick Stairs");
        this.block(GCBlocks.MOON_ROCK_BRICK_WALL, "Moon Rock Brick Wall");

        this.block(GCBlocks.CRACKED_MOON_ROCK_BRICK, "Cracked Moon Rock Brick");
        this.block(GCBlocks.CRACKED_MOON_ROCK_BRICK_SLAB, "Cracked Moon Rock Brick Slab");
        this.block(GCBlocks.CRACKED_MOON_ROCK_BRICK_STAIRS, "Cracked Moon Rock Brick Stairs");
        this.block(GCBlocks.CRACKED_MOON_ROCK_BRICK_WALL, "Cracked Moon Rock Brick Wall");

        this.block(GCBlocks.POLISHED_MOON_ROCK, "Polished Moon Rock");
        this.block(GCBlocks.POLISHED_MOON_ROCK_SLAB, "Polished Moon Rock Slab");
        this.block(GCBlocks.POLISHED_MOON_ROCK_STAIRS, "Polished Moon Rock Stairs");
        this.block(GCBlocks.POLISHED_MOON_ROCK_WALL, "Polished Moon Rock Wall");

        this.block(GCBlocks.CHISELED_MOON_ROCK_BRICK, "Chiseled Moon Rock Brick");
        this.block(GCBlocks.MOON_ROCK_PILLAR, "Moon Rock Pillar");

        this.block(GCBlocks.COBBLED_MOON_ROCK, "Cobbled Moon Rock");
        this.block(GCBlocks.COBBLED_MOON_ROCK_SLAB, "Cobbled Moon Rock Slab");
        this.block(GCBlocks.COBBLED_MOON_ROCK_STAIRS, "Cobbled Moon Rock Stairs");
        this.block(GCBlocks.COBBLED_MOON_ROCK_WALL, "Cobbled Moon Rock Wall");

        this.block(GCBlocks.LUNASLATE, "Lunaslate");
        this.block(GCBlocks.LUNASLATE_SLAB, "Lunaslate Slab");
        this.block(GCBlocks.LUNASLATE_STAIRS, "Lunaslate Stairs");
        this.block(GCBlocks.LUNASLATE_WALL, "Lunaslate Wall");

        this.block(GCBlocks.COBBLED_LUNASLATE, "Cobbled Lunaslate");
        this.block(GCBlocks.COBBLED_LUNASLATE_SLAB, "Cobbled Lunaslate Slab");
        this.block(GCBlocks.COBBLED_LUNASLATE_STAIRS, "Cobbled Lunaslate Stairs");
        this.block(GCBlocks.COBBLED_LUNASLATE_WALL, "Cobbled Lunaslate Wall");

        this.block(GCBlocks.MOON_BASALT, "Moon Basalt");
        this.block(GCBlocks.MOON_BASALT_SLAB, "Moon Basalt Slab");
        this.block(GCBlocks.MOON_BASALT_STAIRS, "Moon Basalt Stairs");
        this.block(GCBlocks.MOON_BASALT_WALL, "Moon Basalt Wall");

        this.block(GCBlocks.MOON_BASALT_BRICK, "Moon Basalt Brick");
        this.block(GCBlocks.MOON_BASALT_BRICK_SLAB, "Moon Basalt Brick Slab");
        this.block(GCBlocks.MOON_BASALT_BRICK_STAIRS, "Moon Basalt Brick Stairs");
        this.block(GCBlocks.MOON_BASALT_BRICK_WALL, "Moon Basalt Brick Wall");

        this.block(GCBlocks.CRACKED_MOON_BASALT_BRICK, "Cracked Moon Basalt Brick");
        this.block(GCBlocks.CRACKED_MOON_BASALT_BRICK_SLAB, "Cracked Moon Basalt Brick Slab");
        this.block(GCBlocks.CRACKED_MOON_BASALT_BRICK_STAIRS, "Cracked Moon Basalt Brick Stairs");
        this.block(GCBlocks.CRACKED_MOON_BASALT_BRICK_WALL, "Cracked Moon Basalt Brick Wall");

        this.block(GCBlocks.FALLEN_METEOR, "Fallen Meteor");

        // MARS NATURAL
        this.block(GCBlocks.MARS_SURFACE_ROCK, "Mars Surface Rock");
        this.block(GCBlocks.MARS_SUB_SURFACE_ROCK, "Mars Sub-Surface Rock");

        this.block(GCBlocks.MARS_STONE, "Mars Stone");
        this.block(GCBlocks.MARS_STONE_SLAB, "Mars Stone Slab");
        this.block(GCBlocks.MARS_STONE_STAIRS, "Mars Stone Stairs");
        this.block(GCBlocks.MARS_STONE_WALL, "Mars Stone Wall");

        this.block(GCBlocks.MARS_COBBLESTONE, "Mars Cobblestone");
        this.block(GCBlocks.MARS_COBBLESTONE_SLAB, "Mars Cobblestone Slab");
        this.block(GCBlocks.MARS_COBBLESTONE_STAIRS, "Mars Cobblestone Stairs");
        this.block(GCBlocks.MARS_COBBLESTONE_WALL, "Mars Cobblestone Wall");

        // ASTEROID NATURAL
        this.block(GCBlocks.ASTEROID_ROCK, "Asteroid Rock");
        this.block(GCBlocks.ASTEROID_ROCK_1, "Asteroid Rock");
        this.block(GCBlocks.ASTEROID_ROCK_2, "Asteroid Rock");

        // VENUS NATURAL
        this.block(GCBlocks.SOFT_VENUS_ROCK, "Soft Venus Rock");
        this.block(GCBlocks.HARD_VENUS_ROCK, "Hard Venus Rock");
        this.block(GCBlocks.SCORCHED_VENUS_ROCK, "Scorched Venus Rock");
        this.block(GCBlocks.VOLCANIC_ROCK, "Volcanic Rock");
        this.block(GCBlocks.PUMICE, "Pumice");
        this.block(GCBlocks.VAPOR_SPOUT, "Vapor Spout");

        // MISC DECOR
        this.block(GCBlocks.WALKWAY, "Walkway");
        this.block(GCBlocks.FLUID_PIPE_WALKWAY, "Fluid Pipe Walkway");
        this.block(GCBlocks.WIRE_WALKWAY, "Wire Walkway");
        this.block(GCBlocks.TIN_LADDER, "Tin Ladder");
        this.block(GCBlocks.IRON_GRATING, "Iron Grating");

        // SPECIAL
        this.block(GCBlocks.ALUMINUM_WIRE, "Aluminum Wire");
        this.block(GCBlocks.SEALABLE_ALUMINUM_WIRE, "Sealable Aluminum Wire");
        this.block(GCBlocks.HEAVY_SEALABLE_ALUMINUM_WIRE, "Heavy Sealable Aluminum Wire");
        this.block(GCBlocks.GLASS_FLUID_PIPE, "Glass Fluid Pipe");
        this.block(GCBlocks.FUELING_PAD, "Fueling Pad");
        this.block(GCBlocks.ROCKET_LAUNCH_PAD, "Rocket Launch Pad");
        this.block(GCBlocks.ROCKET_WORKBENCH, "Rocket Workbench");
        this.block(GCBlocks.PARACHEST, "Parachest");

        for (Map.Entry<PipeColor, Block> entry : GCBlocks.GLASS_FLUID_PIPES.entrySet()) {
            PipeColor color = entry.getKey();
            if (color != PipeColor.CLEAR) {
                this.block(entry.getValue(), TranslationProvider.normalizeName(color.getName()) + " Stained Glass Fluid Pipe");
            }
        }

        // LIGHT PANELS
        this.block(GCBlocks.SQUARE_LIGHT_PANEL, "Light Panel (Square)");
        this.block(GCBlocks.SPOTLIGHT_LIGHT_PANEL, "Light Panel (Spotlight)");
        this.block(GCBlocks.LINEAR_LIGHT_PANEL, "Light Panel (Linear)");
        this.block(GCBlocks.DASHED_LIGHT_PANEL, "Light Panel (Dashed)");
        this.block(GCBlocks.DIAGONAL_LIGHT_PANEL, "Light Panel (Diagonal)");

        // VACUUM GLASS
        this.block(GCBlocks.VACUUM_GLASS, "Vacuum Glass");
        this.block(GCBlocks.CLEAR_VACUUM_GLASS, "Vacuum Glass (Clear)");
        this.block(GCBlocks.STRONG_VACUUM_GLASS, "Vacuum Glass (Strong)");

        // ORES
        this.block(GCBlocks.MARS_IRON_ORE, "Mars Iron Ore");
        this.block(GCBlocks.ASTEROID_IRON_ORE, "Asteroid Iron Ore");

        this.block(GCBlocks.MOON_COPPER_ORE, "Moon Copper Ore");
        this.block(GCBlocks.LUNASLATE_COPPER_ORE, "Lunaslate Copper Ore");
        this.block(GCBlocks.MARS_COPPER_ORE, "Mars Copper Ore");
        this.block(GCBlocks.VENUS_COPPER_ORE, "Venus Copper Ore");

        this.block(GCBlocks.SILICON_ORE, "Silicon Ore");
        this.block(GCBlocks.DEEPSLATE_SILICON_ORE, "Deepslate Silicon Ore");

        this.block(GCBlocks.TIN_ORE, "Tin Ore");
        this.block(GCBlocks.DEEPSLATE_TIN_ORE, "Deepslate Tin Ore");
        this.block(GCBlocks.MOON_TIN_ORE, "Moon Tin Ore");
        this.block(GCBlocks.LUNASLATE_TIN_ORE, "Lunaslate Tin Ore");
        this.block(GCBlocks.MARS_TIN_ORE, "Mars Tin Ore");
        this.block(GCBlocks.VENUS_TIN_ORE, "Venus Tin Ore");

        this.block(GCBlocks.ALUMINUM_ORE, "Aluminum Ore");
        this.block(GCBlocks.DEEPSLATE_ALUMINUM_ORE, "Deepslate Aluminum Ore");
        this.block(GCBlocks.ASTEROID_ALUMINUM_ORE, "Asteroid Aluminum Ore");
        this.block(GCBlocks.VENUS_ALUMINUM_ORE, "Venus Aluminum Ore");

        this.block(GCBlocks.MOON_CHEESE_ORE, "Moon Cheese Ore");

        this.block(GCBlocks.LUNAR_SAPPHIRE_ORE, "Lunar Sapphire Ore");

        this.block(GCBlocks.DESH_ORE, "Desh Ore");

        this.block(GCBlocks.ILMENITE_ORE, "Ilmenite Ore");

        this.block(GCBlocks.GALENA_ORE, "Galena Ore");

        this.block(GCBlocks.SOLAR_ORE, "Solar Ore");

        this.block(GCBlocks.OLIVINE_CLUSTER, "Olivine Cluster");
        this.block(GCBlocks.OLIVINE_BASALT, "Olivine Basalt");
        this.block(GCBlocks.RICH_OLIVINE_BASALT, "Rich Olivine Basalt");

        // COMPACT MINERAL BLOCKS
        this.block(GCBlocks.SILICON_BLOCK, "Block of Silicon");
        this.block(GCBlocks.METEORIC_IRON_BLOCK, "Block of Meteoric Iron");
        this.block(GCBlocks.DESH_BLOCK, "Block of Desh");
        this.block(GCBlocks.ALUMINUM_BLOCK, "Block of Aluminum");
        this.block(GCBlocks.TIN_BLOCK, "Block of Tin");
        this.block(GCBlocks.TITANIUM_BLOCK, "Block of Titanium");
        this.block(GCBlocks.LEAD_BLOCK, "Block of Lead");
        this.block(GCBlocks.LUNAR_SAPPHIRE_BLOCK, "Block of Lunar Sapphire");
        this.block(GCBlocks.OLIVINE_BLOCK, "Block of Olivine");
        this.block(GCBlocks.RAW_METEORIC_IRON_BLOCK, "Block of Raw Meteoric Iron");
        this.block(GCBlocks.RAW_DESH_BLOCK, "Block of Raw Desh");
        this.block(GCBlocks.RAW_ALUMINUM_BLOCK, "Block of Raw Aluminum");
        this.block(GCBlocks.RAW_TIN_BLOCK, "Block of Raw Tin");
        this.block(GCBlocks.RAW_TITANIUM_BLOCK, "Block of Raw Titanium");
        this.block(GCBlocks.RAW_LEAD_BLOCK, "Block of Raw Lead");

        // CHEESE BLOCKS
        this.block(GCBlocks.MOON_CHEESE_BLOCK, "Moon Cheese Block");
        this.block(GCBlocks.MOON_CHEESE_LOG, "Moon Cheese Log");
        this.block(GCBlocks.MOON_CHEESE_LEAVES, "Moon Cheese Leaves");

        this.block(GCBlocks.MOON_CHEESE_WHEEL, "Moon Cheese Wheel");
        this.block(GCBlocks.CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Candle");
        this.block(GCBlocks.WHITE_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with White Candle");
        this.block(GCBlocks.ORANGE_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Orange Candle");
        this.block(GCBlocks.MAGENTA_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Magenta Candle");
        this.block(GCBlocks.LIGHT_BLUE_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Light Blue Candle");
        this.block(GCBlocks.YELLOW_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Yellow Candle");
        this.block(GCBlocks.LIME_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Lime Candle");
        this.block(GCBlocks.PINK_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Pink Candle");
        this.block(GCBlocks.GRAY_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Gray Candle");
        this.block(GCBlocks.LIGHT_GRAY_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Light Gray Candle");
        this.block(GCBlocks.CYAN_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Cyan Candle");
        this.block(GCBlocks.PURPLE_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Purple Candle");
        this.block(GCBlocks.BLUE_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Blue Candle");
        this.block(GCBlocks.BROWN_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Brown Candle");
        this.block(GCBlocks.GREEN_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Green Candle");
        this.block(GCBlocks.RED_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Red Candle");
        this.block(GCBlocks.BLACK_CANDLE_MOON_CHEESE_WHEEL, "Moon Cheese Wheel with Black Candle");

        // MOON VILLAGER SPECIAL
        this.block(GCBlocks.LUNAR_CARTOGRAPHY_TABLE, "Lunar Cartography Table");

        // MISC WORLD GEN
        this.block(GCBlocks.CAVERNOUS_VINES, "Cavernous Vines");
        this.block(GCBlocks.CAVERNOUS_VINES_PLANT, "Cavernous Vines Plant");
        this.block(GCBlocks.BOSS_SPAWNER, "Boss Spawner");

        // MULTIBLOCK PARTS
        this.block(GCBlocks.SOLAR_PANEL_PART, "Solar Panel");
        this.block(GCBlocks.CRYOGENIC_CHAMBER_PART, "Cryogenic Chamber");

        // MISC MACHINES
        this.block(GCBlocks.CRYOGENIC_CHAMBER, "Cryogenic Chamber");
        this.block(GCBlocks.PLAYER_TRANSPORT_TUBE, "Player Transport Tube");

        // MACHINES
        this.block(GCBlocks.CIRCUIT_FABRICATOR, "Circuit Fabricator");
        this.block(GCBlocks.COMPRESSOR, "Compressor");
        this.block(GCBlocks.ELECTRIC_COMPRESSOR, "Electric Compressor");
        this.block(GCBlocks.COAL_GENERATOR, "Coal Generator");
        this.block(GCBlocks.BASIC_SOLAR_PANEL, "Basic Solar Panel");
        this.block(GCBlocks.ADVANCED_SOLAR_PANEL, "Advanced Solar Panel");
        this.block(GCBlocks.ENERGY_STORAGE_MODULE, "Energy Storage Module");
        this.block(GCBlocks.ELECTRIC_FURNACE, "Electric Furnace");
        this.block(GCBlocks.ELECTRIC_ARC_FURNACE, "Electric Arc Furnace");
        this.block(GCBlocks.REFINERY, "Refinery");
        this.block(GCBlocks.OXYGEN_COLLECTOR, "Oxygen Collector");
        this.block(GCBlocks.OXYGEN_SEALER, "Oxygen Sealer");
        this.block(GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR, "Bubble Distributor");
        this.block(GCBlocks.OXYGEN_DECOMPRESSOR, "Oxygen Decompressor");
        this.block(GCBlocks.OXYGEN_COMPRESSOR, "Oxygen Compressor");
        this.block(GCBlocks.OXYGEN_STORAGE_MODULE, "Oxygen Storage Module");
        this.block(GCBlocks.FUEL_LOADER, "Fuel Loader");

        this.block(GCBlocks.AIR_LOCK_CONTROLLER, "Airlock Controller");
        this.block(GCBlocks.AIR_LOCK_FRAME, "Airlock Frame");
        this.block(GCBlocks.AIR_LOCK_SEAL, "Airlock Seal");
    }

    protected void generateItemTranslations() {
        // MATERIALS
        this.item(GCItems.SILICON, "Silicon");

        this.item(GCItems.RAW_METEORIC_IRON, "Raw Meteoric Iron");
        this.item(GCItems.METEORIC_IRON_INGOT, "Meteoric Iron Ingot");
        this.item(GCItems.METEORIC_IRON_NUGGET, "Meteoric Iron Nugget");
        this.item(GCItems.COMPRESSED_METEORIC_IRON, "Compressed Meteoric Iron");

        this.item(GCItems.OLIVINE_SHARD, "Olivine Shard");

        this.item(GCItems.RAW_DESH, "Raw Desh");
        this.item(GCItems.DESH_INGOT, "Desh Ingot");
        this.item(GCItems.DESH_NUGGET, "Desh Nugget");
        this.item(GCItems.COMPRESSED_DESH, "Compressed Desh");

        this.item(GCItems.RAW_LEAD, "Raw Lead");
        this.item(GCItems.LEAD_INGOT, "Lead Ingot");
        this.item(GCItems.LEAD_NUGGET, "Lead Nugget");

        this.item(GCItems.RAW_ALUMINUM, "Raw Aluminum");
        this.item(GCItems.ALUMINUM_INGOT, "Aluminum Ingot");
        this.item(GCItems.ALUMINUM_NUGGET, "Aluminum Nugget");
        this.item(GCItems.COMPRESSED_ALUMINUM, "Compressed Aluminum");

        this.item(GCItems.RAW_TIN, "Raw Tin");
        this.item(GCItems.TIN_INGOT, "Tin Ingot");
        this.item(GCItems.TIN_NUGGET, "Tin Nugget");
        this.item(GCItems.COMPRESSED_TIN, "Compressed Tin");

        this.item(GCItems.RAW_TITANIUM, "Raw Titanium");
        this.item(GCItems.TITANIUM_INGOT, "Titanium Ingot");
        this.item(GCItems.TITANIUM_NUGGET, "Titanium Nugget");
        this.item(GCItems.COMPRESSED_TITANIUM, "Compressed Titanium");

        this.item(GCItems.COMPRESSED_BRONZE, "Compressed Bronze");
        this.item(GCItems.COMPRESSED_COPPER, "Compressed Copper");
        this.item(GCItems.COMPRESSED_IRON, "Compressed Iron");
        this.item(GCItems.COMPRESSED_STEEL, "Compressed Steel");

        this.item(GCItems.LUNAR_SAPPHIRE, "Lunar Sapphire");
        this.item(GCItems.DESH_STICK, "Desh Stick");
        this.item(GCItems.CARBON_FRAGMENTS, "Carbon Fragments");
        this.item(GCItems.SOLAR_DUST, "Solar Dust");
        this.item(GCItems.BASIC_WAFER, "Basic Wafer");
        this.item(GCItems.ADVANCED_WAFER, "Advanced Wafer");
        this.item(GCItems.BEAM_CORE, "Beam Core");
        this.item(GCItems.CANVAS, "Canvas");

        this.item(GCItems.FLUID_MANIPULATOR, "Fluid Manipulator");
        this.item(GCItems.OXYGEN_CONCENTRATOR, "Oxygen Concentrator");
        this.item(GCItems.OXYGEN_FAN, "Oxygen Fan");
        this.item(GCItems.OXYGEN_VENT, "Oxygen Vent");
        this.item(GCItems.SENSOR_LENS, "Sensor Lens");
        this.item(GCItems.BLUE_SOLAR_WAFER, "Blue Solar Wafer");
        this.item(GCItems.SINGLE_SOLAR_MODULE, "Single Solar Module");
        this.item(GCItems.FULL_SOLAR_PANEL, "Full Solar Panel");
        this.item(GCItems.SOLAR_ARRAY_WAFER, "Solar Array Wafer");
        this.item(GCItems.SOLAR_ARRAY_PANEL, "Solar Array Panel");
        this.item(GCItems.STEEL_POLE, "Steel Pole");
        this.item(GCItems.COPPER_CANISTER, "Copper Canister");
        this.item(GCItems.TIN_CANISTER, "Tin Canister");
        this.item(GCItems.THERMAL_CLOTH, "Thermal Cloth");
        this.item(GCItems.ISOTHERMAL_FABRIC, "Isothermal Fabric");
        this.item(GCItems.ORION_DRIVE, "Orion Drive");
        this.item(GCItems.ATMOSPHERIC_VALVE, "Atmospheric Valve");
        this.item(GCItems.AMBIENT_THERMAL_CONTROLLER, "Ambient Thermal Controller");

        // FOOD
        this.add(Items.CANNED_FOOD_TEMPLATE, "Canned %s");
        this.item(GCItems.CANNED_FOOD, "Canned Food");
        this.item(GCItems.EMPTY_CAN, "Empty Can");

        this.item(GCItems.MOON_CHEESE_CURD, "Moon Cheese Curd");

        this.item(GCItems.MOON_CHEESE_SLICE, "Moon Cheese Slice");
        this.item(GCItems.BURGER_BUN, "Burger Bun");
        this.item(GCItems.GROUND_BEEF, "Raw Beef Patty");
        this.item(GCItems.BEEF_PATTY, "Cooked Beef Patty");
        this.item(GCItems.CHEESEBURGER, "Cheeseburger");

        // ROCKET PLATES
        this.item(GCItems.TIER_1_HEAVY_DUTY_PLATE, "Heavy Plating");
        this.item(GCItems.TIER_2_HEAVY_DUTY_PLATE, "Thick Heavy Plating");
        this.item(GCItems.TIER_3_HEAVY_DUTY_PLATE, "Reinforced Heavy Plating");

        // THROWABLE METEOR CHUNKS
        this.item(GCItems.THROWABLE_METEOR_CHUNK, "Throwable Meteor Chunk");
        this.item(GCItems.HOT_THROWABLE_METEOR_CHUNK, "Hot Throwable Meteor Chunk");

        // ARMOR
        this.item(GCItems.HEAVY_DUTY_HELMET, "Heavy Duty Helmet");
        this.item(GCItems.HEAVY_DUTY_CHESTPLATE, "Heavy Duty Chestplate");
        this.item(GCItems.HEAVY_DUTY_LEGGINGS, "Heavy Duty Leggings");
        this.item(GCItems.HEAVY_DUTY_BOOTS, "Heavy Duty Boots");

        this.item(GCItems.DESH_HELMET, "Desh Helmet");
        this.item(GCItems.DESH_CHESTPLATE, "Desh Chestplate");
        this.item(GCItems.DESH_LEGGINGS, "Desh Leggings");
        this.item(GCItems.DESH_BOOTS, "Desh Boots");

        this.item(GCItems.TITANIUM_HELMET, "Titanium Helmet");
        this.item(GCItems.TITANIUM_CHESTPLATE, "Titanium Chestplate");
        this.item(GCItems.TITANIUM_LEGGINGS, "Titanium Leggings");
        this.item(GCItems.TITANIUM_BOOTS, "Titanium Boots");

        this.item(GCItems.SENSOR_GLASSES, "Sensor Glasses");

        // TOOLS + WEAPONS
        this.item(GCItems.HEAVY_DUTY_SWORD, "Heavy Duty Sword");
        this.item(GCItems.HEAVY_DUTY_SHOVEL, "Heavy Duty Shovel");
        this.item(GCItems.HEAVY_DUTY_PICKAXE, "Heavy Duty Pickaxe");
        this.item(GCItems.HEAVY_DUTY_AXE, "Heavy Duty Axe");
        this.item(GCItems.HEAVY_DUTY_HOE, "Heavy Duty Hoe");

        this.item(GCItems.DESH_SWORD, "Desh Sword");
        this.item(GCItems.DESH_SHOVEL, "Desh Shovel");
        this.item(GCItems.DESH_PICKAXE, "Desh Pickaxe");
        this.item(GCItems.DESH_AXE, "Desh Axe");
        this.item(GCItems.DESH_HOE, "Desh Hoe");

        this.item(GCItems.TITANIUM_SWORD, "Titanium Sword");
        this.item(GCItems.TITANIUM_SHOVEL, "Titanium Shovel");
        this.item(GCItems.TITANIUM_PICKAXE, "Titanium Pickaxe");
        this.item(GCItems.TITANIUM_AXE, "Titanium Axe");
        this.item(GCItems.TITANIUM_HOE, "Titanium Hoe");

        this.item(GCItems.STANDARD_WRENCH, "Standard Wrench");

        // SMITHING TEMPLATES
        this.item(GCItems.TITANTIUM_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");

        // BATTERIES
        this.item(GCItems.BATTERY, "Battery");
        this.item(GCItems.INFINITE_BATTERY, "Infinite Battery");

        // FLUID BUCKETS
        this.item(GCItems.CRUDE_OIL_BUCKET, "Oil Bucket");
        this.item(GCItems.FUEL_BUCKET, "Fuel Bucket");
        this.item(GCItems.SULFURIC_ACID_BUCKET, "Sulfuric Acid Bucket");

        // GALACTICRAFT INVENTORY
        this.item(GCItems.PARACHUTE.get(DyeColor.WHITE), "Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.ORANGE), "Orange Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.MAGENTA), "Magenta Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.LIGHT_BLUE), "Light Blue Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.YELLOW), "Yellow Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.LIME), "Lime Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.PINK), "Pink Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.GRAY), "Gray Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.LIGHT_GRAY), "Light Gray Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.CYAN), "Cyan Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.PURPLE), "Purple Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.BLUE), "Blue Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.BROWN), "Brown Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.GREEN), "Green Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.RED), "Red Parachute");
        this.item(GCItems.PARACHUTE.get(DyeColor.BLACK), "Black Parachute");

        this.item(GCItems.OXYGEN_MASK, "Oxygen Mask");
        this.item(GCItems.OXYGEN_GEAR, "Oxygen Gear");

        this.item(GCItems.SMALL_OXYGEN_TANK, "Small Oxygen Tank");
        this.item(GCItems.MEDIUM_OXYGEN_TANK, "Medium Oxygen Tank");
        this.item(GCItems.LARGE_OXYGEN_TANK, "Large Oxygen Tank");
        this.item(GCItems.INFINITE_OXYGEN_TANK, "Infinite Oxygen Tank");

        this.item(GCItems.SHIELD_CONTROLLER, "Shield Controller");
        this.item(GCItems.FREQUENCY_MODULE, "Frequency Module");

        this.item(GCItems.THERMAL_PADDING_HELMET, "Thermal Padding Helm");
        this.item(GCItems.THERMAL_PADDING_CHESTPIECE, "Thermal Padding Chestpiece");
        this.item(GCItems.THERMAL_PADDING_LEGGINGS, "Thermal Padding Leggings");
        this.item(GCItems.THERMAL_PADDING_BOOTS, "Thermal Padding Boots");

        this.item(GCItems.ISOTHERMAL_PADDING_HELMET, "Isothermal Padding Helm");
        this.item(GCItems.ISOTHERMAL_PADDING_CHESTPIECE, "Isothermal Padding Chestpiece");
        this.item(GCItems.ISOTHERMAL_PADDING_LEGGINGS, "Isothermal Padding Leggings");
        this.item(GCItems.ISOTHERMAL_PADDING_BOOTS, "Isothermal Padding Boots");

        // VEHICLES
        this.item(GCItems.BUGGY, "Buggy");
        this.item(GCItems.ROCKET, "Rocket");

        // ROCKET PARTS
        this.item(GCItems.NOSE_CONE, "Nose Cone");
        this.item(GCItems.HEAVY_NOSE_CONE, "Heavy Nose Cone");
        this.item(GCItems.ROCKET_FIN, "Rocket Fin");
        this.item(GCItems.HEAVY_ROCKET_FIN, "Heavy Rocket Fin");
        this.item(GCItems.ROCKET_ENGINE, "Rocket Engine");
        this.item(GCItems.HEAVY_ROCKET_ENGINE, "Heavy Rocket Engine");
        this.item(GCItems.ROCKET_BOOSTER, "Rocket Booster");

        // BUGGY PARTS
        this.item(GCItems.BUGGY_WHEEL, "Buggy Wheel");
        this.item(GCItems.BUGGY_SEAT, "Buggy Seat");
        this.item(GCItems.BUGGY_STORAGE, "Buggy Storage");

        // SCHEMATICS
        this.item(GCItems.BASIC_ROCKET_CONE_SCHEMATIC, "Basic Rocket Cone Schematic");
        this.item(GCItems.BASIC_ROCKET_BODY_SCHEMATIC, "Basic Rocket Body Schematic");
        this.item(GCItems.BASIC_ROCKET_FINS_SCHEMATIC, "Basic Rocket Fins Schematic");
        this.item(GCItems.BASIC_ROCKET_ENGINE_SCHEMATIC, "Basic Rocket Engine Schematic");

        this.item(GCItems.TIER_2_ROCKET_SCHEMATIC, "Tier 2 Rocket Schematic");
        this.item(GCItems.CARGO_ROCKET_SCHEMATIC, "Cargo Rocket Schematic");
        this.item(GCItems.MOON_BUGGY_SCHEMATIC, "Moon Buggy Schematic");
        this.item(GCItems.TIER_3_ROCKET_SCHEMATIC, "Tier 3 Rocket Schematic");
        this.item(GCItems.ASTRO_MINER_SCHEMATIC, "Astro Miner Schematic");

        // LEGACY_MUSIC_DISCS
        this.musicDisc(GCItems.LEGACY_MUSIC_DISC_MARS, "Legacy Music Disc", "Jackson Cordes - Mars");
        this.add(JukeboxSong.LEGACY_MARS, "Jackson Cordes - Mars");
        this.musicDisc(GCItems.LEGACY_MUSIC_DISC_MIMAS, "Legacy Music Disc", "Jackson Cordes - Mimas");
        this.add(JukeboxSong.LEGACY_MIMAS, "Jackson Cordes - Mimas");
        this.musicDisc(GCItems.LEGACY_MUSIC_DISC_ORBIT, "Legacy Music Disc", "Jackson Cordes - Orbit");
        this.add(JukeboxSong.LEGACY_ORBIT, "Jackson Cordes - Orbit");
        this.musicDisc(GCItems.LEGACY_MUSIC_DISC_SPACERACE, "Legacy Music Disc", "Jackson Cordes - Space Race");
        this.add(JukeboxSong.LEGACY_SPACERACE, "Jackson Cordes - Space Race");

        // SPAWN EGGS
        this.item(GCItems.MOON_VILLAGER_SPAWN_EGG, "Moon Villager Spawn Egg");
        this.item(GCItems.EVOLVED_ZOMBIE_SPAWN_EGG, "Evolved Zombie Spawn Egg");
        this.item(GCItems.EVOLVED_CREEPER_SPAWN_EGG, "Evolved Creeper Spawn Egg");
        this.item(GCItems.EVOLVED_SKELETON_SPAWN_EGG, "Evolved Skeleton Spawn Egg");
        this.item(GCItems.EVOLVED_SPIDER_SPAWN_EGG, "Evolved Spider Spawn Egg");
        this.item(GCItems.EVOLVED_ENDERMAN_SPAWN_EGG, "Evolved Enderman Spawn Egg");
        this.item(GCItems.EVOLVED_WITCH_SPAWN_EGG, "Evolved Witch Spawn Egg");
        this.item(GCItems.EVOLVED_PILLAGER_SPAWN_EGG, "Evolved Pillager Spawn Egg");
        this.item(GCItems.EVOLVED_EVOKER_SPAWN_EGG, "Evolved Evoker Spawn Egg");
        this.item(GCItems.EVOLVED_VINDICATOR_SPAWN_EGG, "Evolved Vindicator Spawn Egg");
        this.item(GCItems.GAZER_SPAWN_EGG, "Gazer Spawn Egg");
        this.item(GCItems.RUMBLER_SPAWN_EGG, "Rumbler Spawn Egg");
        this.item(GCItems.COMET_CUBE_SPAWN_EGG, "Comet Cube Spawn Egg");
        this.item(GCItems.OLI_GRUB_SPAWN_EGG, "Oli Grub Spawn Egg");
        this.item(GCItems.GREY_SPAWN_EGG, "Grey Spawn Egg");
        this.item(GCItems.ARCH_GREY_SPAWN_EGG, "Arch Grey Spawn Egg");
    }

    protected void generateTagTranslations() {
        this.tag(GCItemTags.WRENCHES, "Wrenches");

        this.tag(GCItemTags.THERMAL_HEAD, "Thermal Padding Helmets");
        this.tag(GCItemTags.THERMAL_CHEST, "Thermal Padding Chestpieces");
        this.tag(GCItemTags.THERMAL_PANTS, "Thermal Padding Pants");
        this.tag(GCItemTags.THERMAL_BOOTS, "Thermal Padding Boots");
        this.tag(GCItemTags.OXYGEN_MASKS, "Oxygen Masks");
        this.tag(GCItemTags.OXYGEN_GEAR, "Oxygen Gear");
        this.tag(GCItemTags.OXYGEN_TANKS, "Oxygen Tanks");
        this.tag(GCItemTags.ACCESSORIES, "Galacticraft Accessories");
        this.tag(GCItemTags.PARACHUTES, "Parachutes");
        this.tag(GCItemTags.FREQUENCY_MODULES, "Frequency Modules");
        this.tag(GCItemTags.SHIELD_CONTROLLERS, "Shield Controllers");

        this.tag(GCItemTags.GLASS_FLUID_PIPES, "Glass Fluid Pipes");
        this.tag(GCItemTags.STAINED_GLASS_FLUID_PIPES, "Stained Glass Fluid Pipes");

        this.tag(GCItemTags.BATTERIES, "Batteries");

        this.tag(GCItemTags.SILICONS, "Silicon");
        this.tag(GCItemTags.OLIVINE_SHARDS, "Olivine Shards");
        this.tag(GCItemTags.LUNAR_SAPPHIRES, "Lunar Sapphires");

        this.tag(GCItemTags.ALUMINUM_ORES, "Aluminum Ores");
        this.tag(GCItemTags.CHEESE_ORES, "Cheese Ores");
        this.tag(GCItemTags.DESH_ORES, "Desh Ores");
        this.tag(GCItemTags.LEAD_ORES, "Lead Ores");
        this.tag(GCItemTags.LUNAR_SAPPHIRE_ORES, "Lunar Sapphire Ores");
        this.tag(GCItemTags.METEORIC_IRON_ORES, "Meteoric Iron Ores");
        this.tag(GCItemTags.OLIVINE_ORES, "Olivine Ores");
        this.tag(GCItemTags.SILICON_ORES, "Silicon Ores");
        this.tag(GCItemTags.SOLAR_ORES, "Solar Dust Ores");
        this.tag(GCItemTags.TIN_ORES, "Tin Ores");
        this.tag(GCItemTags.TITANIUM_ORES, "Titanium Ores");

        this.tag(GCItemTags.ALUMINUM_BLOCKS, "Aluminum Blocks");
        this.tag(GCItemTags.DESH_BLOCKS, "Desh Blocks");
        this.tag(GCItemTags.LEAD_BLOCKS, "Lead Blocks");
        this.tag(GCItemTags.METEORIC_IRON_BLOCKS, "Meteoric Iron Blocks");
        this.tag(GCItemTags.TIN_BLOCKS, "Tin Blocks");
        this.tag(GCItemTags.TITANIUM_BLOCKS, "Titanium Blocks");

        this.tag(GCItemTags.SILICON_BLOCKS, "Silicon Blocks");
        this.tag(GCItemTags.CHEESE_BLOCKS, "Cheese Blocks");
        this.tag(GCItemTags.LUNAR_SAPPHIRE_BLOCKS, "Lunar Sapphire Blocks");
        this.tag(GCItemTags.OLIVINE_BLOCKS, "Olivine Blocks");

        this.tag(GCItemTags.RAW_ALUMINUM_BLOCKS, "Raw Aluminum Blocks");
        this.tag(GCItemTags.RAW_DESH_BLOCKS, "Raw Desh Blocks");
        this.tag(GCItemTags.RAW_LEAD_BLOCKS, "Raw Lead Blocks");
        this.tag(GCItemTags.RAW_METEORIC_IRON_BLOCKS, "Raw Meteoric Iron Blocks");
        this.tag(GCItemTags.RAW_TIN_BLOCKS, "Raw Tin Blocks");
        this.tag(GCItemTags.RAW_TITANIUM_BLOCKS, "Raw Titanium Blocks");

        this.tag(GCItemTags.ALUMINUM_INGOTS, "Aluminum Ingots");
        this.tag(GCItemTags.DESH_INGOTS, "Desh Ingots");
        this.tag(GCItemTags.LEAD_INGOTS, "Lead Ingots");
        this.tag(GCItemTags.METEORIC_IRON_INGOTS, "Meteoric Iron Ingots");
        this.tag(GCItemTags.STEEL_INGOTS, "Steel Ingots");
        this.tag(GCItemTags.TIN_INGOTS, "Tin Ingots");
        this.tag(GCItemTags.TITANIUM_INGOTS, "Titanium Ingots");

        this.tag(GCItemTags.ALUMINUM_RAW_MATERIALS, "Raw Aluminum");
        this.tag(GCItemTags.DESH_RAW_MATERIALS, "Raw Desh");
        this.tag(GCItemTags.LEAD_RAW_MATERIALS, "Raw Lead");
        this.tag(GCItemTags.METEORIC_IRON_RAW_MATERIALS, "Raw Meteoric Iron");
        this.tag(GCItemTags.TIN_RAW_MATERIALS, "Raw Tin");
        this.tag(GCItemTags.TITANIUM_RAW_MATERIALS, "Raw Titanium");

        this.tag(GCItemTags.ALUMINUM_NUGGETS, "Aluminum Nuggets");
        this.tag(GCItemTags.DESH_NUGGETS, "Desh Nuggets");
        this.tag(GCItemTags.LEAD_NUGGETS, "Lead Nuggets");
        this.tag(GCItemTags.METEORIC_IRON_NUGGETS, "Meteoric Iron Nuggets");
        this.tag(GCItemTags.TIN_NUGGETS, "Tin Nuggets");
        this.tag(GCItemTags.TITANIUM_NUGGETS, "Titanium Nuggets");

        this.tag(GCItemTags.PLATES, "Metal Plates");
        this.tag(GCItemTags.ALUMINUM_PLATES, "Compressed Aluminum");
        this.tag(GCItemTags.BRONZE_PLATES, "Compressed Bronze");
        this.tag(GCItemTags.COPPER_PLATES, "Compressed Copper");
        this.tag(GCItemTags.DESH_PLATES, "Compressed Desh");
        this.tag(GCItemTags.IRON_PLATES, "Compressed Iron");
        this.tag(GCItemTags.METEORIC_IRON_PLATES, "Compressed Meteoric Iron");
        this.tag(GCItemTags.STEEL_PLATES, "Compressed Steel");
        this.tag(GCItemTags.TIN_PLATES, "Compressed Tin");
        this.tag(GCItemTags.TITANIUM_PLATES, "Compressed Titanium");
        this.tag(GCItemTags.HEAVY_DUTY_PLATES, "Heavy Duty Plates");
        this.tag(GCItemTags.TIER_1_HEAVY_DUTY_PLATES, "Tier 1 Heavy Duty Plates");
        this.tag(GCItemTags.TIER_2_HEAVY_DUTY_PLATES, "Tier 2 Heavy Duty Plates");
        this.tag(GCItemTags.TIER_3_HEAVY_DUTY_PLATES, "Tier 3 Heavy Duty Plates");

        this.tag(GCItemTags.DESH_RODS, "Desh Rods");
        this.tag(GCItemTags.STEEL_RODS, "Steel Rods");

        this.tag(GCItemTags.CANISTERS, "Canisters");
        this.tag(GCItemTags.COPPER_CANISTERS, "Copper Canisters");
        this.tag(GCItemTags.TIN_CANISTERS, "Tin Canisters");

        this.tag(GCItemTags.SOLAR_DUSTS, "Solar Dusts");

        this.tag(GCItemTags.CHEESE_FOODS, "Cheese Foods");
        this.tag(GCItemTags.CANNED_FOODS, "Canned Foods");
        this.tag(GCItemTags.UNCANNABLE_FOODS, "Uncannable Foods");

        this.tag(GCItemTags.ROCKET_STORAGE_UPGRADE_ITEMS, "Rocket Storage Upgrade Items");

        this.tag(GCItemTags.EVOLVED_CREEPER_DROP_MUSIC_DISCS, "Evolved Creeper Music Disc Drops");

        this.tag(GCItemTags.OIL_BUCKETS, "Oil Buckets");
        this.tag(GCItemTags.FUEL_BUCKETS, "Fuel Buckets");
        this.tag(GCItemTags.SULFURIC_ACID_BUCKETS, "Sulfuric Acid Buckets");

        this.tag(GCItemTags.SLABS, "Galacticraft Slabs");
        this.tag(GCItemTags.STAIRS, "Galacticraft Stairs");
        this.tag(GCItemTags.WALLS, "Galacticraft Walls");

        this.tag(GCItemTags.MOON_COBBLESTONES, "Moon Cobblestones");
        this.tag(GCItemTags.LUNASLATE_COBBLESTONES, "Lunaslate Cobblestones");
        this.tag(GCItemTags.MARS_COBBLESTONES, "Mars Cobblestones");
    }

    protected void generateGasTranslations() {
        this.add(Gas.ARGON, "Argon");
        this.add(Gas.CARBON_DIOXIDE, "Carbon Dioxide");
        this.add(Gas.CARBON_MONOXIDE, "Carbon Monoxide");
        this.add(Gas.HELIUM, "Helium");
        this.add(Gas.HYDROGEN, "Hydrogen");
        this.add(Gas.HYDROGEN_DEUTERIUM_OXIDE, "Hydrogen Deuterium Oxide");
        this.add(Gas.IODINE, "Iodine");
        this.add(Gas.KRYPTON, "Krypton");
        this.add(Gas.METHANE, "Methane");
        this.add(Gas.NEON, "Neon");
        this.add(Gas.NITRIC_OXIDE, "Nitric Oxide");
        this.add(Gas.NITROGEN, "Nitrogen");
        this.add(Gas.NITROGEN_DIOXIDE, "Nitrogen Dioxide");
        this.add(Gas.NITROUS_OXIDE, "Nitrous Oxide");
        this.add(Gas.OXYGEN, "Oxygen");
        this.add(Gas.OZONE, "Ozone");
        this.add(Gas.WATER_VAPOR, "Water Vapor");
        this.add(Gas.XENON, "Xenon");
    }

    protected void generateEntityTranslations() {
        this.entity(GCEntityTypes.ARCH_GREY, "Arch Grey");
        this.entity(GCEntityTypes.BUBBLE, "Bubble");
        this.entity(GCEntityTypes.BUGGY, "Buggy");
        this.entity(GCEntityTypes.COMET_CUBE, "Comet Cube");
        this.entity(GCEntityTypes.EVOLVED_CREEPER, "Evolved Creeper");
        this.entity(GCEntityTypes.EVOLVED_ENDERMAN, "Evolved Enderman");
        this.entity(GCEntityTypes.EVOLVED_EVOKER, "Evolved Evoker");
        this.entity(GCEntityTypes.EVOLVED_PILLAGER, "Evolved Pillager");
        this.entity(GCEntityTypes.EVOLVED_SKELETON, "Evolved Skeleton");
        this.entity(GCEntityTypes.SKELETON_BOSS, "Evolved Skeleton Boss");
        this.entity(GCEntityTypes.EVOLVED_SPIDER, "Evolved Spider");
        this.entity(GCEntityTypes.EVOLVED_VINDICATOR, "Evolved Vindicator");
        this.entity(GCEntityTypes.EVOLVED_WITCH, "Evolved Witch");
        this.entity(GCEntityTypes.EVOLVED_ZOMBIE, "Evolved Zombie");
        this.entity(GCEntityTypes.GAZER, "Gazer");
        this.entity(GCEntityTypes.GREY, "Grey");
        this.entity(GCEntityTypes.LANDER, "Lander");
        this.entity(GCEntityTypes.MOON_VILLAGER, "Moon Villager");
        this.add(GCEntityTypes.MOON_VILLAGER.getDescriptionId() + ".none", "Moon Villager");
        this.entity(GCEntityTypes.OLI_GRUB, "Oli Grub");
        this.entity(GCEntityTypes.PARACHEST, "Parachest");
        this.entity(GCEntityTypes.ROCKET, "Rocket");
        this.entity(GCEntityTypes.RUMBLER, "Rumbler");
        this.entity(GCEntityTypes.THROWABLE_METEOR_CHUNK, "Meteor Chunk");
    }

    protected void generateCelestialBodyTranslations() {
        this.add(Galaxy.MILKY_WAY, "Milky Way");
        this.add(Galaxy.MILKY_WAY_DESCRIPTION, "");
        this.add(CelestialBody.SOL, "Sol");
        this.add(CelestialBody.SOL_DESC, "");
        this.add(CelestialBody.ASTEROID, "Asteroids");
        this.add(CelestialBody.ASTEROID_DESC, "");
        this.add(CelestialBody.EARTH, "Earth");
        this.add(CelestialBody.EARTH_DESC, "The Overworld");
        this.add(CelestialBody.JUPITER, "Jupiter");
        this.add(CelestialBody.JUPITER_DESC, "");
        this.add(CelestialBody.MARS, "Mars");
        this.add(CelestialBody.MARS_DESC, "");
        this.add(CelestialBody.MERCURY, "Mercury");
        this.add(CelestialBody.MERCURY_DESC, "");
        this.add(CelestialBody.MOON, "Moon");
        this.add(CelestialBody.MOON_DESC, "");
        this.add(CelestialBody.NEPTUNE, "Neptune");
        this.add(CelestialBody.NEPTUNE_DESC, "");
        this.add(CelestialBody.SATURN, "Saturn");
        this.add(CelestialBody.SATURN_DESC, "");
        this.add(CelestialBody.URANUS, "Uranus");
        this.add(CelestialBody.URANUS_DESC, "");
        this.add(CelestialBody.VENUS, "Venus");
        this.add(CelestialBody.VENUS_DESC, "");
        this.add(CelestialBody.SATELLITE, "Satellite");
        this.add(CelestialBody.SATELLITES, "Satellites");
        this.add(CelestialBody.SATELLITE_DESC, "");
    }

    protected void generateBiomeTranslations() {
        this.add(GCBiomes.Moon.BASALTIC_MARE, "Basaltic Mare");
        this.add(GCBiomes.Moon.COMET_TUNDRA, "Comet Tundra");
        this.add(GCBiomes.Moon.LUNAR_HIGHLANDS, "Lunar Highlands");
        this.add(GCBiomes.Moon.LUNAR_LOWLANDS, "Lunar Lowlands");
        this.add(GCBiomes.Moon.OLIVINE_SPIKES, "Olivine Spikes");
        this.add(GCBiomes.SPACE, "Space");
        this.add(GCBiomes.Venus.VENUS_FLAT, "Venus Flat");
        this.add(GCBiomes.Venus.VENUS_MOUNTAIN, "Venus Mountain");
        this.add(GCBiomes.Venus.VENUS_VALLEY, "Venus Valley");
    }

    protected void generateChatTranslations() {
        this.add(Chat.BED_FAIL, "Uh oh, what if the oxygen runs out when I am asleep? I'll need a Cryogenic Chamber to sleep in space!");
        this.add(Chat.CHAMBER_HOT, "The chamber is way too hot right now! It needs %s seconds to cool down before I sleep again.");
        this.add(Chat.CHAMBER_OBSTRUCTED, "This cryogenic chamber is obstructed");
        this.add(Chat.CHAMBER_OCCUPIED, "This cryogenic chamber is occupied");
        this.add(Chat.CHAMBER_TOO_FAR_AWAY, "You may not rest now; the cryogenic chamber is too far away");
        this.add(Chat.ROCKET_WARNING, "Press again to launch.");

        this.add(Chat.CANNOT_EAT_IN_NO_ATMOSPHERE, "You can't eat that while holding your breath.");
        this.add(Chat.CANNOT_EAT_WITH_MASK, "You can't eat that while wearing a mask.");

        this.add(Subtitles.THROW_METEOR_CHUNK, "Meteor Chunk flies");

        this.add(RegistryDebug.DUMP, "Dumped: %s");
        this.add(RegistryDebug.ID, "%s - %s: %s");

        this.add(SetOxygen.SUCCESS_MULTIPLE, "Set oxygen at blocks");
        this.add(SetOxygen.SUCCESS_SINGLE, "Set oxygen at block");
        this.add(SetOxygen.OXYGEN_EXISTS, "Oxygen exists at block");
        this.add(SetOxygen.NO_OXYGEN_EXISTS, "No oxygen at block");
        this.add(SetOxygen.FULL_OXYGEN, "Area is filled with oxygen");
        this.add(SetOxygen.PARTIAL_OXYGEN, "Area partially contains oxygen");
        this.add(SetOxygen.EMPTY_OXYGEN, "Area contains no oxygen");

        this.add(DimensionTp.SUCCESS_MULTIPLE, "Teleported %s entities to %s");
        this.add(DimensionTp.SUCCESS_SINGLE, "Teleported to %s");
        this.add(DimensionTp.INVALID_PACKET, "Invalid planet teleport packet received.");

        this.add(GcHouston.IN_OTHER_DIMENSION, "We cannot locate your signal! Are you sure you're in space?");
        this.add(GcHouston.CONFIRMATION, "Er, Houston, we have a problem... (Run this command again to confirm teleport)");
        this.add(GcHouston.IN_OVERWORLD, "I don't need to be rescued!");
        this.add(GcHouston.SUCCESS, "You have been rescued. Better luck next time...");

        this.deathBy(GCDamageTypes.CRASH_LANDING, "%s came in too hot");
        this.deathBy(GCDamageTypes.OIL_BOOM, "%s tried to put out fire with a very flammable material",
                "%s tried to put out fire with a very flammable material while trying to escape %s");
        this.deathBy(GCDamageTypes.SUFFOCATION, "%s died from lack of oxygen",
                "%s ran out of oxygen while trying to escape %s");
        this.deathBy(GCDamageTypes.SULFURIC_ACID, "%s was dissolved by sulfuric acid",
                "%s was dissolved by sulfuric acid while trying to escape %s");
        this.deathBy(GCDamageTypes.VINE_POISON, "%s succumbed to the poison of some vines",
                "%s succumbed to the poison of some vines while trying to escape %s");

        this.add(Boss.SKELETON_BOSS_DESPAWN, "Boss despawned, don't leave the boss room while fighting! Re-enter room to respawn boss.");
    }

    protected void generateRocketPartTranslations() {
        this.rocketPart(GCRocketParts.TIER_1_BODY, "Basic Body");
        this.rocketPart(GCRocketParts.ADVANCED_CONE, "Advanced Cone");
        this.rocketPart(GCRocketParts.SLOPED_CONE, "Sloped Cone");
        this.rocketPart(GCRocketParts.TIER_1_CONE, "Basic Cone");
        this.rocketPart(GCRocketParts.TIER_1_ENGINE, "Basic Engine");
        this.rocketPart(GCRocketParts.TIER_1_FIN, "Basic Fins");
        this.rocketPart(GCRocketParts.STORAGE_UPGRADE, "Storage Upgrade");
    }

    protected void generateSmithingTranslations() {
        this.add(Misc.UPGRADE_TITANIUM_ADDITIONS_SLOT_DESCRIPTON, "Add Compressed Titanium");
        this.add(Misc.UPGRADE_TITANIUM_APPLIES_TO, "Desh Equipment");
        this.add(Misc.UPGRADE_TITANIUM_BASE_SLOT_DESCRIPTION, "Add desh armor, weapon, or tool");
        this.add(Misc.UPGRADE_TITANIUM_DESCRIPTION, "Titanium Upgrade");
        this.add(Misc.UPGRADE_TITANIUM_INGREDIENTS, "Compressed Titanium");
    }

    protected void generateTooltipTranslations() {
        // Block Descriptions
        this.blockDesc(GCBlocks.ADVANCED_SOLAR_PANEL, "Advanced Solar Panels collect energy from the sun, and store it for further use. Adjusts position to face the sun, to collect more electricity.");
        this.blockDesc(GCBlocks.BASIC_SOLAR_PANEL, "Basic Solar Panels collect energy from the sun, and store it for further use. Collects most energy at mid-day (non-adjustable).");
        this.blockDesc(GCBlocks.CIRCUIT_FABRICATOR, "Circuit Fabricator will process basic materials into silicon wafers, used for advanced machines.");
        this.blockDesc(GCBlocks.COAL_GENERATOR, "Burns coal and charcoal for energy. The simplest but least efficient energy method.");
        this.blockDesc(GCBlocks.COMPRESSOR, "Compressor will process ingots into their compressed equivalents.");
        this.blockDesc(GCBlocks.ELECTRIC_COMPRESSOR, "Electric Compressor will process ingots into their compressed equivalents. Compresses two at a time, making it more effective than its predecessor.");
        this.blockDesc(GCBlocks.ELECTRIC_FURNACE, "Electric Furnace is used as a faster alternative to traditional coal furnaces.");
        this.blockDesc(GCBlocks.ELECTRIC_ARC_FURNACE, "Electric Arc Furnace is used as a better and faster alternative to both traditional coal and electric furnaces: double output from ores!");
        this.blockDesc(GCBlocks.ENERGY_STORAGE_MODULE, "Energy Storage Module is used to store large amounts of energy for later use.");
        this.blockDesc(GCBlocks.FUEL_LOADER, "After being connected to a launch pad, a Fuel Loader will allow fuel to passed into the connected Rocket or other vehicle.");
        this.blockDesc(GCBlocks.MOON_CHEESE_WHEEL, "Moon Cheese Wheels are created from the cheeses of the Moon, place-able and edible.");
        this.blockDesc(GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR, "Oxygen Bubble Distributor creates a bubble of air around itself. Requires oxygen and electricity.");
        this.blockDesc(GCBlocks.OXYGEN_DECOMPRESSOR, "Oxygen Decompressor will unload oxygen into internal storage from an oxygen tank.");
        this.blockDesc(GCBlocks.OXYGEN_COLLECTOR, "Oxygen Collector will store oxygen collected from leaves in the surrounding area.");
        this.blockDesc(GCBlocks.OXYGEN_COMPRESSOR, "Oxygen Compressor will load oxygen from internal storage into an oxygen tank.");
        this.blockDesc(GCBlocks.OXYGEN_SEALER, "Oxygen Sealer will check for an enclosed space. If the space is enclosed, it will fill with breathable air.");
        this.blockDesc(GCBlocks.OXYGEN_STORAGE_MODULE, "Oxygen Storage Module is used to store large amounts of oxygen for later use.");
        this.blockDesc(GCBlocks.FOOD_CANNER, "Food Canner is used to compress up to %s edible items into a can that allows you to eat the food while wearing an oxygen mask.");
        this.blockDesc(GCBlocks.PARACHEST, "Parachests will fall from the sky after landing on certain planets/moons, such as Earth. Contains rocket, fuel, and cargo from previous launch.");
        this.blockDesc(GCBlocks.REFINERY, "Refinery will take an input of oil and energy, and output fuel used for rockets and vehicles.");

        this.add(Tooltip.CREATIVE_ONLY, "Creative Only");
        this.add(Tooltip.ENERGY_REMAINING, "Energy Remaining: %s");
        this.add(Tooltip.GLOWSTONE_LANTERN, "Glowstone Lanterns are best used to light areas when there is no oxygen for lanterns to burn.");
        this.add(Tooltip.GLOWSTONE_TORCH, "Glowstone Torches are best used to light areas when there is no oxygen for wood torches to burn.");
        this.add(Tooltip.INFINITE, "Infinite");
        this.add(Tooltip.OXYGEN_REMAINING, "Oxygen Remaining: %s");
        this.add(Tooltip.PRESS_SHIFT, "Press LSHIFT for more information.");
        this.add(Tooltip.STANDARD_WRENCH, "Most Galacticraft machines can be rotated by right-clicking with the Standard Wrench.");
        this.add(Tooltip.TIME_UNTIL_COOL, "Time Until Cool: %s");
        this.add(Tooltip.SECONDS_UNIT, "%ss");
    }

    protected void generateConfigTranslations() {
        this.add(Config.CLIENT, "Client");
        this.add(Config.SKYBOX, "Skybox");
        this.add(Config.COMMANDS, "Commands");
        this.add(Config.ENABLE_GC_HOUSTON, "Enable /gchouston");
        this.add(Config.DEBUG, "Debug");
        this.add(Config.HIDE_ALPHA_WARNING, "Hide Alpha Warning");
        this.add(Config.DEBUG_LOGGING, "Debug Logging");
        this.add(Config.ENERGY, "Energy");
        this.add(Config.MACHINES, "Machines");
        this.add(Config.CIRCUIT_FABRICATOR_ENERGY_CONSUMPTION_RATE, "Circuit Fabricator Energy Consumption Rate/t");
        this.add(Config.ELECTRIC_COMPRESSOR_ENERGY_CONSUMPTION_RATE, "Electric Compressor Energy Consumption Rate/t");
        this.add(Config.COAL_GENERATOR_ENERGY_PRODUCTION_RATE, "Coal Generator Energy Production Rate/t");
        this.add(Config.ELECTRIC_FURNACE_ENERGY_CONSUMPTION_RATE, "Electric Furnace Energy Consumption Rate/t");
        this.add(Config.ENERGY_STORAGE_MODULE_STORAGE_SIZE, "Energy Storage Module Energy Storage Size");
        this.add(Config.ENERGY_STORAGE_SIZE, "Default Machine Energy Storage Size");
        this.add(Config.OXYGEN_COLLECTOR_ENERGY_CONSUMPTION_RATE, "Oxygen Collector Energy Consumption Rate/t");
        this.add(Config.OXYGEN_COMPRESSOR_ENERGY_CONSUMPTION_RATE, "Oxygen Compressor Energy Consumption Rate/t");
        this.add(Config.FOOD_CANNER_ENERGY_CONSUMPTION_RATE, "Food Canner Energy Consumption Rate/t");
        this.add(Config.OXYGEN_DECOMPRESSOR_ENERGY_CONSUMPTION_RATE, "Oxygen Decompressor Energy Consumption Rate/t");
        this.add(Config.REFINERY_ENERGY_CONSUMPTION_RATE, "Refinery Energy Consumption Rate/t");
        this.add(Config.SOLAR_PANEL_ENERGY_PRODUCTION_RATE, "Solar Panel Energy Production Rate/t");
        this.add(Config.WIRES, "Wires");
        this.add(Config.HEAVY_WIRE_ENERGY_TRANSFER_LIMIT, "Heavy Wire Transfer Limit/t");
        this.add(Config.WIRE_ENERGY_TRANSFER_LIMIT, "Wire Transfer Limit/t");
        this.add(Config.PLAYER_LIFE_SUPPORT, "Life Support");
        this.add(Config.PLAYER, "Player");
        this.add(Config.PLAYER_OXYGEN_CONSUMPTION_RATE, "Oxygen Consumption Rate/t");
        this.add(Config.RESET, "Reset");
        this.add(Config.TITLE, "Galacticraft Config");

        this.add(Keybindings.ROCKET_INVENTORY, "Open Rocket Inventory");
        this.add(Keybindings.OPEN_CELESTIAL_SCREEN, "Open Celestial Map");
    }

    protected void generateSpaceRaceTranslations() {
        this.add(SpaceRace.SPACE_RACE_MANAGER, "Space Race Manager");
        this.add(SpaceRace.ADD_PLAYERS, "Add Player(s)");
        this.add(SpaceRace.BACK, "Back");
        this.add(SpaceRace.BUTTON, "Space Race");
        this.add(SpaceRace.BUTTON_2, "Manager");
        this.add(SpaceRace.COMING_SOON, "Coming Soon™");
        this.add(SpaceRace.EXIT, "Exit");
        this.add(SpaceRace.GLOBAL_STATS, "Global Statistics");
        this.add(SpaceRace.REMOVE_PLAYERS, "Remove Player(s)");
        this.add(SpaceRace.SERVER_STATS, "Server Statistics");
        this.add(SpaceRace.FLAG_CONFIRM, "Use this team flag?");
        this.add(SpaceRace.FLAG_CONFIRM_MESSAGE, "");
        this.add(SpaceRace.CUSTOMIZE_FLAG, "Customize Flag");
        this.add(SpaceRace.DRAG_AND_DROP_FLAG, "Drag and drop an image to use as a flag");
        this.add(SpaceRace.TEAM_COLOR_1, "Change");
        this.add(SpaceRace.TEAM_COLOR_2, "Team");
        this.add(SpaceRace.TEAM_COLOR_3, "Color");
        this.add(SpaceRace.RED, "Red");
        this.add(SpaceRace.GREEN, "Green");
        this.add(SpaceRace.BLUE, "Blue");
    }

    protected void generateSolarPanelTranslations() {
        this.add(SolarPanel.ATMOSPHERIC_INTERFERENCE, "Atmospheric Interference: %s");
        this.add(SolarPanel.BLOCKED, "Blocked");
        this.add(SolarPanel.DAY, "Day");
        this.add(SolarPanel.MISSING_SOURCE, "Missing Light Source");
        this.add(SolarPanel.NIGHT, "Night");
        this.add(SolarPanel.OVERCAST, "Overcast");
        this.add(SolarPanel.LIGHT_SOURCE, "Light Source: ");
        this.add(SolarPanel.LIGHT_SOURCE_MOON, "Moon");
        this.add(SolarPanel.LIGHT_SOURCE_EARTH, "Earth");
        this.add(SolarPanel.LIGHT_SOURCE_NONE, "Unknown");
        this.add(SolarPanel.LIGHT_SOURCE_RAIN, "Sun (Overcast)");
        this.add(SolarPanel.LIGHT_SOURCE_SUN, "Sun");
        this.add(SolarPanel.LIGHT_SOURCE_THUNDER, "Sun (Stormy)");
        this.add(SolarPanel.STATUS, "Status: ");
        this.add(SolarPanel.STORMY, "Stormy");
        this.add(SolarPanel.STRENGTH, "Strength: %s");
    }

    protected void generateMachineStatusTranslations() {
        this.add(MachineStatus.ALREADY_SEALED, "Already Sealed");
        this.add(MachineStatus.AREA_TOO_LARGE, "Area Too Large");
        this.add(MachineStatus.MISSING_EMPTY_CAN, "Missing Empty Can");
        this.add(MachineStatus.NO_FOOD, "No Food");
        this.add(MachineStatus.TRANSFERRING_CAN, "Transferring Can");
        this.add(MachineStatus.CANNING, "Canning");
        this.add(MachineStatus.BLOCKED, "Blocked");
        this.add(MachineStatus.COLLECTING, "Collecting");
        this.add(MachineStatus.COMPRESSING, "Compressing");
        this.add(MachineStatus.COOLING_DOWN, "Cooling Down");
        this.add(MachineStatus.DECOMPRESSING, "Decompressing");
        this.add(MachineStatus.DISTRIBUTING, "Distributing");
        this.add(MachineStatus.EMPTY_OXYGEN_TANK, "Empty Oxygen Tank");
        this.add(MachineStatus.FABRICATING, "Fabricating");
        this.add(MachineStatus.FUEL_TANK_FULL, "Fuel Tank Full");
        this.add(MachineStatus.GENERATING, "Generating");
        this.add(MachineStatus.LOADING, "Loading");
        this.add(MachineStatus.MISSING_OIL, "Missing Oil");
        this.add(MachineStatus.MISSING_OXYGEN_TANK, "Missing Oxygen Tank");
        this.add(MachineStatus.NIGHT, "Night");
        this.add(MachineStatus.NO_FUEL, "No Fuel");
        this.add(MachineStatus.NOT_ENOUGH_OXYGEN, "Not Enough Oxygen");
        this.add(MachineStatus.OXYGEN_TANK_FULL, "Oxygen Tank Full");
        this.add(MachineStatus.PARTIALLY_BLOCKED, "Partially Blocked");
        this.add(MachineStatus.RAIN, "Rain");
        this.add(MachineStatus.THUNDER, "Thunder");
        this.add(MachineStatus.SEALED, "Sealed");
        this.add(MachineStatus.WARMING_UP, "Warming Up");
        this.add(MachineStatus.NOT_ENOUGH_FUEL, "Not Enough Fuel");
        this.add(MachineStatus.NO_ROCKET, "No Rocket");
        this.add(MachineStatus.ROCKET_IS_FULL, "Rocket Is Full");
    }

    protected void generateCelestialSelectionTranslations() {
        this.add(CelestialSelection.BACK, "Back");
        this.add(CelestialSelection.CANCEL, "Cancel");
        this.add(CelestialSelection.CATALOG, "Catalog");
        this.add(CelestialSelection.LAUNCH, "Launch");
        this.add(CelestialSelection.RENAME, "Rename");
        this.add(CelestialSelection.TIER, "Tier %s");
        this.add(CelestialSelection.ASSIGN_NAME, "Assign Name");
        this.add(CelestialSelection.APPLY, "Apply");
        this.add(CelestialSelection.EXIT, "Exit");
        this.add(CelestialSelection.SELECT_SS, "Select Space Station");
        this.add(CelestialSelection.SS_OWNER, "Space Station Owner");
        this.add(CelestialSelection.CAN_CREATE_SPACE_STATION, "A Space Station can be created here!");
        this.add(CelestialSelection.CANNOT_CREATE_SPACE_STATION, "Cannot Create Space Station");
        this.add(CelestialSelection.CREATE_SPACE_STATION, "Create");
        this.add(CelestialSelection.DAY_NIGHT_CYCLE, "Day/Night Cycle");
        this.add(CelestialSelection.SURFACE_GRAVITY, "Surface Gravity");
        this.add(CelestialSelection.SURFACE_COMPOSITION, "Surface Composition");
        this.add(CelestialSelection.ATMOSPHERE, "Atmosphere");
        this.add(CelestialSelection.MEAN_SURFACE_TEMP, "Mean Surface Temp.");
        this.add(CelestialSelection.CLICK_AGAIN, "Click again to zoom");
        this.add(CelestialSelection.CLICK_AGAIN_MOONS, "Click again to zoom (view moons)");
        this.add(CelestialSelection.CLICK_AGAIN_SATELLITES, "Click again to zoom (view satellites)");
        this.add(CelestialSelection.CLICK_AGAIN_MOONS_AND_SATELLITES, "Click again to zoom (view moons & satellites)");
    }

    protected void generateUiTranslations() {
        this.add(Ui.CONE, "Cone");
        this.add(Ui.BODY, "Body");
        this.add(Ui.FINS, "Fins");
        this.add(Ui.BOOSTER, "Booster");
        this.add(Ui.ENGINE, "Engine");
        this.add(Ui.UPGRADE, "Upgrade");
        this.add(Ui.COLOR, "Color");

        this.add(Ui.AIRLOCK_REDSTONE_SIGNAL, "Opens on Redstone Signal");
        this.add(Ui.AIRLOCK_OWNER, "%s's Airlock Controller");
        this.add(Ui.ALPHA_WARNING_1, "Galacticraft is currently in ALPHA.");
        this.add(Ui.ALPHA_WARNING_2, "Please report all issues you find.");
        this.add(Ui.ALPHA_WARNING_3, "Press [ESC] or click to continue.");
        this.add(Ui.ALPHA_WARNING_HEADER, "WARNING");
        this.add(Ui.BUBBLE_CURRENT_SIZE, "Current Size: %s");
        this.add(Ui.BUBBLE_NOT_VISIBLE, "Bubble Not Visible");
        this.add(Ui.BUBBLE_TARGET_SIZE, "Target Size: ");
        this.add(Ui.BUBBLE_VISIBLE, "Bubble Visible");

        this.add(Ui.COLLECTING, "Collecting: %s/s");
        this.add(Ui.CURRENT_OXYGEN, "Oxygen: %s");
        this.add(Ui.GJT, "%s gJ/t");
        this.add(Ui.MILLIBUCKETS, "mB");
        this.add(Ui.MAX_OXYGEN, "Maximum Oxygen: %s");
        this.add(Ui.MACHINE_STATUS, "Status: %s");
        this.add(Ui.OXYGEN_TANK_1_LEVEL, "Oxygen Tank 1: %s");
        this.add(Ui.OXYGEN_TANK_2_LEVEL, "Oxygen Tank 2: %s");
        this.add(Ui.OXYGEN_WARNING, "WARNING");
        this.add(Ui.OXYGEN_SETUP_INVALID, "Oxygen Setup is Invalid!");
        this.add(Ui.ROCKET_FUEL, "Fuel:");
        this.add(Ui.ROCKET_FULL, "% full");
        this.add(Ui.ROCKET_NO_FUEL, "No fuel");
        this.add(Ui.LANDER_WARNING, "WARNING");
        this.add(Ui.LANDER_CONTROLS, "Hold \"%s\" to slow down!");
        this.add(Ui.LANDER_VELOCITY, "Entry Velocity: %s m/s");
        this.add(Ui.SMALL_STEP, "Taking one small step");
        this.add(Ui.GIANT_LEAP, "Taking one giant leap");
        this.add(Ui.PREPARE_FOR_ENTRY, "Prepare for entry!");
        this.add(Ui.TRAVELING_TO, "Traveling to: %s");

        this.add(Ui.TOTAL_NUTRITION, "Total Nutrition: %s");
        this.add(Ui.SPACE_STATION_NAME, "%s's Space Station");
    }

    protected void generateStatsTranslations() {
        this.stat(GCStats.OPEN_PARACHEST, "Parachests Opened");
        this.stat(GCStats.INTERACT_WITH_ROCKET_WORKBENCH, "Interactions with Rocket Workbench");
    }

    protected void decorationSet(DecorationSet decoSet, String translation) {
        this.block(decoSet.block(), translation);
        this.block(decoSet.stairs(), translation + " Stairs");
        this.block(decoSet.slab(), translation + " Slab");
        this.block(decoSet.wall(), translation + " Wall");
        this.block(decoSet.detailedBlock(), "Detailed " + translation);
        this.block(decoSet.detailedStairs(), "Detailed " + translation + " Stairs");
        this.block(decoSet.detailedSlab(), "Detailed " + translation + " Slab");
        this.block(decoSet.detailedWall(), "Detailed " + translation + " Wall");
    }

    protected void blockDesc(Block block, String translation) {
        this.add(block.getDescriptionId() + ".description", translation);
    }

    protected void musicDisc(Item item, String translation, String description) {
        this.item(item, translation);
        this.add(item.getDescriptionId() + ".desc", description);
    }

    protected void deathBy(ResourceKey<DamageType> key, String translation) {
        if (!translation.contains("%s")) throw new IllegalArgumentException("Death message must contain %s");
        this.add("death.attack." + key.location().getPath(), translation);
    }

    protected void deathBy(ResourceKey<DamageType> key, String translation, String playerTranslation) {
        this.deathBy(key, translation);
        Matcher matcher = Pattern.compile("%s").matcher(playerTranslation);
        if (matcher.results().count() < 2)
            throw new IllegalArgumentException(".player death message must contain two instances of %s");
        this.add("death.attack." + key.location().getPath() + ".player", playerTranslation);
    }

    protected void rocketPart(ResourceKey<? extends RocketPart<?, ?>> key, String translation) {
        this.add(RocketPart.getKey(key), translation);
    }

    @Override
    public @NotNull String getName() {
        return "Translations";
    }
}
