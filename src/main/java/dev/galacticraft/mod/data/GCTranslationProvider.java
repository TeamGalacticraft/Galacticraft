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

package dev.galacticraft.mod.data;

import dev.galacticraft.api.data.TranslationProvider;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.GCRocketParts;
import dev.galacticraft.mod.content.entity.damage.GCDamageTypes;
import dev.galacticraft.mod.content.item.GCItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static dev.galacticraft.mod.util.Translations.*;

public class GCTranslationProvider extends TranslationProvider {
    public GCTranslationProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void generateTranslations(HolderLookup.@NotNull Provider registries) {
        this.add(ItemGroup.BLOCKS, "Galacticraft Blocks");
        this.add(ItemGroup.ITEMS, "Galacticraft Items");
        this.add(ItemGroup.MACHINES, "Galacticraft Machines");

        this.block(GCBlocks.ASTEROID_ROCK_1, "Asteroid Rock");
        this.block(GCBlocks.ASTEROID_ROCK_2, "Asteroid Rock");

        this.block(GCBlocks.DASHED_LIGHT_PANEL, "Light Panel (Dashed)");
        this.block(GCBlocks.DIAGONAL_LIGHT_PANEL, "Light Panel (Diagonal)");
        this.block(GCBlocks.LINEAR_LIGHT_PANEL, "Light Panel (Linear)");
        this.block(GCBlocks.SPOTLIGHT_LIGHT_PANEL, "Light Panel (Spotlight)");
        this.block(GCBlocks.SQUARE_LIGHT_PANEL, "Light Panel (Square)");

        this.block(GCBlocks.MARS_SUB_SURFACE_ROCK, "Mars Sub-Surface Rock");

        this.block(GCBlocks.MOON_CHEESE_WHEEL, "Moon Cheese Wheel");

        this.block(GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR, "Bubble Distributor");
        this.block(GCBlocks.SOLAR_PANEL_PART, "Solar Panel");

        this.block(GCBlocks.SCORCHED_VENUS_ROCK, "Scorched Rock");

        this.block(GCBlocks.CLEAR_VACUUM_GLASS, "Vacuum Glass (Clear)");
        this.block(GCBlocks.STRONG_VACUUM_GLASS, "Vacuum Glass (Strong)");

        this.block(GCBlocks.GLOWSTONE_TORCH, "Glowstone Torch");
        this.block(GCBlocks.GLOWSTONE_WALL_TORCH, "Glowstone Torch");
        this.block(GCBlocks.UNLIT_TORCH, "Unlit Torch");
        this.block(GCBlocks.UNLIT_WALL_TORCH, "Unlit Torch");

        this.block(GCBlocks.CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Candle");
        this.block(GCBlocks.WHITE_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with White Candle");
        this.block(GCBlocks.ORANGE_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Orange Candle");
        this.block(GCBlocks.MAGENTA_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Magenta Candle");
        this.block(GCBlocks.LIGHT_BLUE_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Light Blue Candle");
        this.block(GCBlocks.YELLOW_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Yellow Candle");
        this.block(GCBlocks.LIME_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Lime Candle");
        this.block(GCBlocks.PINK_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Pink Candle");
        this.block(GCBlocks.GRAY_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Gray Candle");
        this.block(GCBlocks.LIGHT_GRAY_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Light Gray Candle");
        this.block(GCBlocks.CYAN_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Cyan Candle");
        this.block(GCBlocks.PURPLE_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Purple Candle");
        this.block(GCBlocks.BLUE_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Blue Candle");
        this.block(GCBlocks.BROWN_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Brown Candle");
        this.block(GCBlocks.GREEN_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Green Candle");
        this.block(GCBlocks.RED_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Red Candle");
        this.block(GCBlocks.BLACK_CANDLE_MOON_CHEESE_WHEEL, "Block of Cheese with Black Candle");

        this.entity(GCEntityTypes.THROWABLE_METEOR_CHUNK, "Meteor Chunk");

        this.item(GCItems.BEEF_PATTY, "Cooked Beef Patty");
        this.item(GCItems.CRUDE_OIL_BUCKET, "Oil Bucket");
        this.item(GCItems.GROUND_BEEF, "Raw Beef Patty");
        this.item(GCItems.TIER_2_HEAVY_DUTY_PLATE, "Thick Heavy Plating");
        this.item(GCItems.TIER_3_HEAVY_DUTY_PLATE, "Reinforced Heavy Plating");
        this.item(GCItems.RAW_DESH, "Unrefined Desh");
        this.item(GCItems.THERMAL_PADDING_HELMET, "Thermal Padding Helm");
        this.item(GCItems.TITANTIUM_UPGRADE_SMITHING_TEMPLATE, "Smithing Template");
        this.item(GCItems.PARACHUTE.get(DyeColor.WHITE), "Parachute");

        // Block Descriptions
        this.blockDesc(GCBlocks.ADVANCED_SOLAR_PANEL, "Advanced Solar Panels collect energy from the sun, and store it for further use. Adjusts position to face the sun, to collect more electricity.");
        this.blockDesc(GCBlocks.BASIC_SOLAR_PANEL, "Basic Solar Panels collect energy from the sun, and store it for further use. Collects most energy at mid-day (non-adjustable).");
        this.blockDesc(GCBlocks.MOON_CHEESE_WHEEL, "Cheese Blocks are created from the cheeses of the Moon, place-able and edible.");
        this.blockDesc(GCBlocks.CIRCUIT_FABRICATOR, "Circuit Fabricator will process basic materials into silicon wafers, used for advanced machines.");
        this.blockDesc(GCBlocks.COAL_GENERATOR, "Burns coal and charcoal for energy. The simplest but least efficient energy method.");
        this.blockDesc(GCBlocks.COMPRESSOR, "Compressor will process ingots into their compressed equivalents.");
        this.blockDesc(GCBlocks.ELECTRIC_COMPRESSOR, "Electric Compressor will process ingots into their compressed equivalents. Compresses two at a time, making it more effective than its predecessor.");
        this.blockDesc(GCBlocks.ENERGY_STORAGE_MODULE, "Energy Storage Module is used to store large amounts of energy for later use.");
        this.blockDesc(GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR, "Oxygen Bubble Distributor creates a bubble of air around itself. Requires oxygen and electricity.");
        this.blockDesc(GCBlocks.OXYGEN_COLLECTOR, "Oxygen Collector will store oxygen collected from leaves in the surrounding area.");
        this.blockDesc(GCBlocks.PARACHEST, "Parachests will fall from the sky after landing on certain planets/moons, such as Earth. Contains rocket, fuel, and cargo from previous launch.");
        this.blockDesc(GCBlocks.REFINERY, "Refinery will take an input of oil and energy, and output fuel used for rockets and vehicles.");

        this.add(RecipeCategory.CIRCUIT_FABRICATOR, "Circuit Fabricating");
        this.add(RecipeCategory.COMPRESSOR, "Compressing");
        this.add(Chat.BED_FAIL, "Uh oh, what if the oxygen runs out when I am asleep?  I'll need a Cryogenic Chamber to sleep in space!");
        this.add(Chat.ROCKET_WARNING, "Press again to launch.");

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

        this.add(GcHouston.IN_OTHER_DIMENSION, "We cannot locate your signal! Are you sure you're in space?");
        this.add(GcHouston.CONFIRMATION, "Er, Houston, we have a problem... (Run this command again to confirm teleport)");
        this.add(GcHouston.IN_OVERWORLD, "I don't need to be rescued!");
        this.add(GcHouston.SUCCESS, "You have been rescued. Better luck next time...");

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
        this.add(Config.ELECTRIC_FURNACE_ENERGY_CONSUMPTION_RATE, "Electric Compressor Energy Consumption Rate/t");
        this.add(Config.ELECTRIC_FURNACE_ENERGY_CONSUMPTION_RATE, "Electric Furnace Energy Consumption Rate/t");
        this.add(Config.ENERGY_STORAGE_MODULE_STORAGE_SIZE, "Energy Storage Module Energy Storage Size");
        this.add(Config.ENERGY_STORAGE_SIZE, "Default Machine Energy Storage Size");
        this.add(Config.OXYGEN_COLLECTOR_ENERGY_CONSUMPTION_RATE, "Oxygen Collector Energy Consumption Rate/t");
        this.add(Config.OXYGEN_COMPRESSOR_ENERGY_CONSUMPTION_RATE, "Oxygen Compressor Energy Consumption Rate/t");
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

        this.deathBy(GCDamageTypes.OIL_BOOM, "%s tried to put out fire with a very flammable material");
        this.deathBy(GCDamageTypes.SUFFOCATION, "%s died from lack of oxygen");
        this.deathBy(GCDamageTypes.SULFURIC_ACID, "%s succumbed to sulfuric acid");
        this.deathBy(GCDamageTypes.VINE_POISON, "%s succumbed to the poison of some vines");

        this.add(Galaxy.MILKY_WAY_DESCRIPTION, "");
        this.add(Galaxy.MILKY_WAY, "Milky Way");

        this.add(Keybindings.ROCKET_INVENTORY, "Open Rocket Inventory");
        this.add(Keybindings.OPEN_CELESTIAL_SCREEN, "Open Celestial Map");

        this.add(CelestialBody.SOL_DESC, "");
        this.add(CelestialBody.SOL, "Sol");
        this.add(CelestialBody.ASTEROIDS_DESC, "");
        this.add(CelestialBody.ASTEROIDS, "Asteroids");
        this.add(CelestialBody.EARTH_DESC, "The Overworld");
        this.add(CelestialBody.EARTH, "Earth");
        this.add(CelestialBody.JUPITER_DESC, "");
        this.add(CelestialBody.JUPITER, "Jupiter");
        this.add(CelestialBody.MARS_DESC, "");
        this.add(CelestialBody.MARS, "Mars");
        this.add(CelestialBody.MERCURY_DESC, "");
        this.add(CelestialBody.MERCURY, "Mercury");
        this.add(CelestialBody.MOON_DESC, "");
        this.add(CelestialBody.MOON, "Moon");
        this.add(CelestialBody.NEPTUNE_DESC, "");
        this.add(CelestialBody.NEPTUNE, "Neptune");
        this.add(CelestialBody.SATURN_DESC, "");
        this.add(CelestialBody.SATURN, "Saturn");
        this.add(CelestialBody.URANUS_DESC, "");
        this.add(CelestialBody.URANUS, "Uranus");
        this.add(CelestialBody.VENUS_DESC, "");
        this.add(CelestialBody.VENUS, "Venus");
        this.add(CelestialBody.SATELLITE, "Satellite");
        this.add(CelestialBody.SATELLITE_DESC, "");

        this.rocketPart(GCRocketParts.TIER_1_BODY, "Basic Body");
        this.rocketPart(GCRocketParts.ADVANCED_CONE, "Advanced Cone");
        this.rocketPart(GCRocketParts.SLOPED_CONE, "Sloped Cone");
        this.rocketPart(GCRocketParts.TIER_1_CONE, "Basic Cone");
        this.rocketPart(GCRocketParts.TIER_1_ENGINE, "Basic Engine");
        this.rocketPart(GCRocketParts.TIER_1_FIN, "Basic Fins");
        this.rocketPart(GCRocketParts.STORAGE_UPGRADE, "Storage Upgrade");

        this.add(Misc.UPGRADE_TITANIUM_ADDITIONS_SLOT_DESCRIPTON, "Add Compressed Titanium");
        this.add(Misc.UPGRADE_TITANIUM_APPLIES_TO, "Desh Equipment");
        this.add(Misc.UPGRADE_TITANIUM_BASE_SLOT_DESCRIPTION, "Add desh armor, weapon, or tool");
        this.add(Misc.UPGRADE_TITANIUM_DESCRIPTION, "Titanium Upgrade");
        this.add(Misc.UPGRADE_TITANIUM_INGREDIENTS, "Compressed Titanium");

        this.add(Tooltip.CREATIVE_ONLY, "Creative Only");
        this.add(Tooltip.ENERGY_REMAINING, "Energy Remaining: %s");
        this.add(Tooltip.GLOWSTONE_LANTERN, "Glowstone Lanterns are best used to light areas when there is no oxygen for lanterns to burn.");
        this.add(Tooltip.GLOWSTONE_TORCH, "Glowstone Torches are best used to light areas when there is no oxygen for wood torches to burn.");
        this.add(Tooltip.INFINITE, "Infinite");
        this.add(Tooltip.OXYGEN_REMAINING, "Oxygen Remaining: %s");
        this.add(Tooltip.PRESS_SHIFT, "Press LSHIFT for more info");
        this.add(Tooltip.STANDARD_WRENCH, "Most Galacticraft machines can be rotated by right-clicking with the Standard Wrench.");
        this.add(Tooltip.TIME_UNTIL_COOL, "Time Until Cool: %ss");

        this.add(Gas.ARGON, "Argon");
        this.add(Gas.CARBON_DIOXIDE, "Carbon Dioxide");
        this.add(Gas.CARBON_MONOXIDE, "Carbon Monoxide");
        this.add(Gas.HELIUM, "Helium");
        this.add(Gas.HYDROGEN, "Hydrogen");
        this.add(Gas.IODINE, "Iodine");
        this.add(Gas.KRYPTON, "Krypton");
        this.add(Gas.METHANE, "Methane");
        this.add(Gas.NEON, "Neon");
        this.add(Gas.NITROGEN, "Nitrogen");
        this.add(Gas.NITROUS_DIOXIDE, "Nitrous Dioxide");
        this.add(Gas.NITROUS_OXIDE, "Nitrous Oxide");
        this.add(Gas.OXYGEN, "Oxygen");
        this.add(Gas.OZONE, "Ozone");
        this.add(Gas.WATER_VAPOR, "Water Vapor");
        this.add(Gas.XENON, "Xenon");
        this.add(Gas.HYDROGEN_DEUTERIUM_OXYGEN, "Hydrogen Deuterium Oxygen");

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
        this.add(SpaceRace.FLAG_CONFIRM, "Confirm Flag");
        this.add(SpaceRace.FLAG_CONFIRM_MESSAGE, "");

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
        this.add(SolarPanel.LIGHT_SOURCE_RAIN, "Rain");
        this.add(SolarPanel.LIGHT_SOURCE_SUN, "Sun");
        this.add(SolarPanel.STATUS, "Status: ");
        this.add(SolarPanel.STRENGTH, "Strength: %s");

        this.add(MachineStatus.ALREADY_SEALED, "Already Sealed");
        this.add(MachineStatus.AREA_TOO_LARGE, "Area Too Large");
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
        this.add(MachineStatus.SEALED, "Sealed");
        this.add(MachineStatus.WARMING_UP, "Warming up");
        this.add(MachineStatus.NOT_ENOUGH_FUEL, "Not enough fuel");
        this.add(MachineStatus.NO_ROCKET, "No rocket");
        this.add(MachineStatus.ROCKET_IS_FULL, "Rocket is full");

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

        this.add(Ui.CONE, "Cone");
        this.add(Ui.BODY, "Body");
        this.add(Ui.FINS, "Fins");
        this.add(Ui.BOOSTER, "Booster");
        this.add(Ui.ENGINE, "Engine");
        this.add(Ui.UPGRADE, "Upgrade");
        this.add(Ui.COLOR, "Color");

        this.add(Ui.AIRLOCK_REDSTONE_SIGNAL, "Opens on Redstone Signal");
        this.add(Ui.ALPHA_WARNING_1, "Galacticraft is currently in ALPHA.");
        this.add(Ui.ALPHA_WARNING_2, "Please report all issues you find.");
        this.add(Ui.ALPHA_WARNING_3, "Press [ESC] or click to continue.");
        this.add(Ui.ALPHA_WARNING_HEADER, "WARNING");
        this.add(Ui.BUBBLE_CURRENT_SIZE, "Current Size: %s");
        this.add(Ui.BUBBLE_NOT_VISIBLE, "Bubble Not Visible");
        this.add(Ui.BUBBLE_TARGET_SIZE, "Target Size:");
        this.add(Ui.BUBBLE_VISIBLE, "Bubble Visible");

        this.add(Ui.COLLECTING, "Collecting: %s/s");
        this.add(Ui.CURRENT_OXYGEN, "Oxygen: %s");
        this.add(Ui.GJT, "%sGj/t");
        this.add(Ui.MAX_OXYGEN, "Maximum Oxygen: %s");
        this.add(Ui.MACHINE_STATUS, "Status: ");
        this.add(Ui.OXYGEN_TANK_LEVEL, "Oxygen Tank %s: %s/%s");
        this.add(Ui.ROCKET_FUEL, "Fuel:");
        this.add(Ui.ROCKET_FULL, "% full");
        this.add(Ui.ROCKET_NO_FUEL, "No fuel");
        this.add(Ui.LANDER_VELOCITY, "Entry Velocity");
        this.add(Ui.LANDER_VELOCITYU, "m/s");
        this.add(Ui.LANDER_WARNING_2, "Hold \"");
        this.add(Ui.LANDER_WARNING_3, "\" to slow down!");
        this.add(Ui.LANDER_WARNING, "WARNING!");
        this.add(Ui.SMALL_STEP, "Taking one small step");
        this.add(Ui.GIANT_LEAP, "Taking one giant leap");
        this.add(Ui.PREPARE_FOR_ENTRY, "Prepare for entry!");
        this.add(Ui.TRAVELLING_TO, "Travelling to: ");
    }

    protected void blockDesc(Block block, String translation) {
        this.add(block.getDescriptionId() + ".description", translation);
    }

    protected void deathBy(ResourceKey<DamageType> key, String translation) {
        if (!translation.contains("%s")) throw new IllegalArgumentException("Death message must contain %s");
        this.add("death.attack." + key.location().getPath(), translation);
    }

    protected void rocketPart(ResourceKey<? extends RocketPart<?, ?>> key, String translation) {
        this.add(RocketPart.getKey(key), translation);
    }

    @Override
    public @NotNull String getName() {
        return "Translations";
    }
}
