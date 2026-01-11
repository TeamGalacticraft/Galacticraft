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

package dev.galacticraft.mod.util;

public interface Translations {

    interface Advancements {
        String ROOT = "advancement.galacticraft.root";
        String COAL_GENERATOR = "advancement.galacticraft.coal_generator";
        String CIRCUIT_FABRICATOR = "advancement.galacticraft.circuit_fabricator";
        String BASIC_WAFER = "advancement.galacticraft.basic_wafer";
        String ADVANCED_WAFER = "advancement.galacticraft.advanced_wafer";
        String BASIC_SOLAR_PANEL = "advancement.galacticraft.basic_solar_panel";
        String ADVANCED_SOLAR_PANEL = "advancement.galacticraft.advanced_solar_panel";
        String COMPRESSOR = "advancement.galacticraft.compressor";
        String ELECTRIC_COMPRESSOR = "advancement.galacticraft.electric_compressor";
        String OIL = "advancement.galacticraft.oil";
        String REFINERY = "advancement.galacticraft.refinery";
        String FUEL = "advancement.galacticraft.fuel";
        String OXYGEN_COLLECTOR = "advancement.galacticraft.oxygen_collector";
        String OXYGEN_COMPRESSOR = "advancement.galacticraft.oxygen_compressor";
        String FILL_TANK = "advancement.galacticraft.fill_tank";
        String FILL_ALL_TANKS = "advancement.galacticraft.fill_all_tanks";
        String OXYGEN_GEAR = "advancement.galacticraft.oxygen_gear";
        String ROCKET_WORKBENCH = "advancement.galacticraft.rocket_workbench";
        String ROCKET = "advancement.galacticraft.rocket";
        String FUEL_LOADER = "advancement.galacticraft.fuel_loader";
        String LEAVE_ROCKET_DURING_COUNTDOWN = "advancement.galacticraft.leave_rocket_during_countdown";
        String LAUNCH_ROCKET = "advancement.galacticraft.launch_rocket";
        String MOON = "advancement.galacticraft.moon";
        String PARROT_LANDING = "advancement.galacticraft.parrot_landing";
        String EAT_MOON_CHEESE_CURD = "advancement.galacticraft.eat_moon_cheese_curd";
        String CHEESE_AND_CRACKERS = "advancement.galacticraft.cheese_and_crackers";
        String CHEESE_TAX = "advancement.galacticraft.cheese_tax";
        String THROW_METEOR_CHUNK = "advancement.galacticraft.throw_meteor_chunk";
        String SPACE_STATION = "advancement.galacticraft.space_station";
        String MOON_DUNGEON = "advancement.galacticraft.moon_dungeon";
        String MOON_DUNGEON_KEY = "advancement.galacticraft.moon_dungeon_key";
        String BUGGY_SCHEMATIC = "advancement.galacticraft.buggy_schematic";
        String BUGGY = "advancement.galacticraft.buggy";
    }

    interface ItemGroup {
        String ITEMS = "itemGroup.galacticraft.items";
        String BLOCKS = "itemGroup.galacticraft.blocks";
        String MACHINES = "itemGroup.galacticraft.machines";
        String CANNED_FOOD = "itemGroup.galacticraft.canned_food";
    }

    interface RecipeCategory {
        String PREFIX = "category.recipe_viewer.";
        String CIRCUIT_FABRICATOR = PREFIX + "fabrication";
        String COMPRESSOR = PREFIX + "compressing";
        String ELECTRIC_COMPRESSOR = PREFIX + "compressing.electric";
        String ELECTRIC_FURNACE = PREFIX + "smelting.electric";
        String ELECTRIC_ARC_FURNACE = PREFIX + "blasting.electric";
        String CANNING = PREFIX + "canning";
        String ROCKET_WORKBENCH = PREFIX + "rocket";

        String REI_TIME = "category.rei.campfire.time";
        String REI_TIME_AND_XP = "category.rei.cooking.time&xp";
        String JEI_TIME = "gui.jei.category.smelting.time.seconds";
        String JEI_XP = "gui.jei.category.smelting.experience";
        String EMI_TIME = "emi.cooking.time";
        String EMI_XP = "emi.cooking.experience";
    }

    interface Waila {
        String OXYGEN_TANK_LABEL = "tooltip.galacticraft.waila_oxygen_tank";
        String PLUGIN_GALACTICRAFT = "config.waila.plugin_galacticraft";
        String SHOW_OXYGEN_LEVEL = "config.waila.plugin_galacticraft.oxygen_level.enabled";
    }

    interface BannerPattern {
        String ROCKET = "block.galacticraft.banner.rocket";
    }

    interface JukeboxSong {
        String LEGACY_MARS = "jukebox_song.galacticraft.legacy_mars";
        String LEGACY_MIMAS = "jukebox_song.galacticraft.legacy_mimas";
        String LEGACY_ORBIT = "jukebox_song.galacticraft.legacy_orbit";
        String LEGACY_SPACERACE = "jukebox_song.galacticraft.legacy_spacerace";
    }

    interface Chat {
        String BED_FAIL = "chat.galacticraft.bed_fail";
        String CHAMBER_HOT = "chat.galacticraft.chamber_hot";
        String CHAMBER_OBSTRUCTED = "chat.galacticraft.chamber_obstructed";
        String CHAMBER_OCCUPIED = "chat.galacticraft.chamber_occupied";
        String CHAMBER_TOO_FAR_AWAY = "chat.galacticraft.chamber_too_far_away";
        String ROCKET_WARNING = "chat.galacticraft.rocket.warning";
        String CANNOT_EAT_IN_NO_ATMOSPHERE = "chat.galacticraft.cannot_eat_in_no_atmosphere";
        String CANNOT_EAT_WITH_MASK = "chat.galacticraft.cannot_eat_with_mask";
        String CANNOT_FEED_IN_NO_ATMOSPHERE = "chat.galacticraft.cannot_feed_in_no_atmosphere";
        String CANNOT_FEED_WITH_MASK = "chat.galacticraft.cannot_feed_with_mask";
    }

    interface Subtitles {
        String THROW_METEOR_CHUNK = "subtitles.galacticraft.entity.throwable_meteor_chunk.throw";
    }

    interface RegistryDebug {
        String DUMP = "commands.galacticraft.debug.registry.dump";
        String ID = "commands.galacticraft.debug.registry.id";
    }

    interface SetOxygen {
        String SUCCESS_SINGLE = "commands.galacticraft.oxygen.set.single";
        String SUCCESS_MULTIPLE = "commands.galacticraft.oxygen.set.multiple";
        String OXYGEN_EXISTS = "commands.galacticraft.oxygen.get.single.oxygen";
        String NO_OXYGEN_EXISTS = "commands.galacticraft.oxygen.get.single.no_oxygen";
        String FULL_OXYGEN = "commands.galacticraft.oxygen.get.area.full";
        String PARTIAL_OXYGEN = "commands.galacticraft.oxygen.get.area.partial";
        String EMPTY_OXYGEN = "commands.galacticraft.oxygen.get.area.none";
    }

    interface DimensionTp {
        String SUCCESS_MULTIPLE = "commands.galacticraft.dimensiontp.success.multiple";
        String SUCCESS_SINGLE = "commands.galacticraft.dimensiontp.success.single";
        String INVALID_PACKET = "disconnect.galacticraft.dimensiontp.invalidPacket";
    }

    interface GcHouston {
        String IN_OTHER_DIMENSION = "commands.galacticraft.gchouston.cannot_detect_signal";
        String CONFIRMATION = "commands.galacticraft.gchouston.confirm";
        String IN_OVERWORLD = "commands.galacticraft.gchouston.on_earth_already";
        String SUCCESS = "commands.galacticraft.gchouston.success";
    }

    interface Config {
        String TITLE = "config.galacticraft.title";
        String RESET = "config.galacticraft.reset";

        String CLIENT = "config.galacticraft.client";
        String SQUARE_CANNED_FOOD = "config.galacticraft.client.square_canned_food";
        String SKYBOX = "config.galacticraft.client.skybox";
        String PLAYER = "config.galacticraft.player";

        String COMMANDS = "config.galacticraft.commands";
        String ENABLE_GC_HOUSTON = "config.galacticraft.commands.enable_gc_houston";

        String MISC = "config.galacticraft.misc";
        String CREATIVE = "config.galacticraft.misc.creative";
        String ENABLE_CREATIVE_GEARINV = "config.galacticraft.misc.creative.enable_gearinv";

        String DEBUG = "config.galacticraft.debug";
        String DEBUG_LOGGING = "config.galacticraft.debug.logging";
        String HIDE_ALPHA_WARNING = "config.galacticraft.debug.hide_alpha_warning";

        String ENERGY = "config.galacticraft.energy";

        String WIRES = "config.galacticraft.energy.wires";
        String WIRE_ENERGY_TRANSFER_LIMIT = "config.galacticraft.energy.wires.transfer_limit";
        String HEAVY_WIRE_ENERGY_TRANSFER_LIMIT = "config.galacticraft.energy.wires.heavy_transfer_limit";

        String MACHINES = "config.galacticraft.energy.machines";
        String ENERGY_STORAGE_SIZE = "config.galacticraft.energy.machines.energy_storage_size";
        String ENERGY_STORAGE_MODULE_STORAGE_SIZE = "config.galacticraft.energy.machines.energy_storage_module_storage_size";
        String COAL_GENERATOR_ENERGY_PRODUCTION_RATE = "config.galacticraft.energy.machines.coal_generator_energy_production_rate";
        String SOLAR_PANEL_ENERGY_PRODUCTION_RATE = "config.galacticraft.energy.machines.solar_panel_energy_production_rate";
        String CIRCUIT_FABRICATOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.circuit_fabricator_energy_consumption_rate";
        String ELECTRIC_COMPRESSOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.electric_compressor_energy_consumption_rate";
        String ELECTRIC_FURNACE_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.electric_furnace_energy_consumption_rate";
        String ELECTRIC_ARC_FURNACE_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.electric_arc_furnace_energy_consumption_rate";
        String ELECTRIC_ARC_FURNACE_BONUS_CHANCE = "config.galacticraft.energy.machines.electric_arc_furnace_bonus_chance";
        String OXYGEN_COLLECTOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.oxygen_collector_energy_consumption_rate";
        String OXYGEN_COMPRESSOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.oxygen_compressor_energy_consumption_rate";
        String OXYGEN_DECOMPRESSOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.oxygen_decompressor_energy_consumption_rate";
        String OXYGEN_SEALER_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.oxygen_sealer_energy_consumption_rate";
        String OXYGEN_SEALER_OXYGEN_CONSUMPTION_RATE = "config.galacticraft.energy.machines.oxygen_sealer_oxygen_consumption_rate";
        String MAX_SEALING_POWER = "config.galacticraft.machines.max_sealing_power";
        String REFINERY_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.refinery_energy_consumption_rate";
        String FUEL_LOADER_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.fuel_loader_energy_consumption_rate";
        String FOOD_CANNER_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.food_canner_energy_consumption_rate";

        String LIFE_SUPPORT = "config.galacticraft.lifesupport";
        String SMALL_OXYGEN_TANK_CAPACITY = "config.galacticraft.lifesupport.oxygen_tank_capacity.small";
        String MEDIUM_OXYGEN_TANK_CAPACITY = "config.galacticraft.lifesupport.oxygen_tank_capacity.medium";
        String LARGE_OXYGEN_TANK_CAPACITY = "config.galacticraft.lifesupport.oxygen_tank_capacity.large";
        String PLAYER_OXYGEN_CONSUMPTION_RATE = "config.galacticraft.lifesupport.oxygen_consumption_rate.player";
        String WOLF_OXYGEN_CONSUMPTION_RATE = "config.galacticraft.lifesupport.oxygen_consumption_rate.wolf";
        String CAT_OXYGEN_CONSUMPTION_RATE = "config.galacticraft.lifesupport.oxygen_consumption_rate.cat";
        String PARROT_OXYGEN_CONSUMPTION_RATE = "config.galacticraft.lifesupport.oxygen_consumption_rate.parrot";
        String CANNOT_EAT_IN_NO_ATMOSPHERE = "config.galacticraft.lifesupport.cannot_eat_in_no_atmosphere";
        String CANNOT_EAT_WITH_MASK = "config.galacticraft.lifesupport.cannot_eat_with_mask";

        String DIFFICULTY = "config.galacticraft.difficulty";
        String METEOR_SPAWN_MULTIPLIER = "config.galacticraft.difficulty.meteor_spawn_multiplier";
        String BOSS_HEALTH_MODIFIER = "config.galacticraft.difficulty.dungeon_boss_health_multiplier";
        String BOSS_HEALTH_MODIFIER_DESC = "config.galacticraft.difficulty.dungeon_boss_health_multiplier.desc";
    }

    interface Galaxy {
        String MILKY_WAY = "galaxy.galacticraft.milky_way.name";
        String MILKY_WAY_DESCRIPTION = "galaxy.galacticraft.milky_way.description";
    }

    interface Keybindings {
        String ROCKET_INVENTORY = "key.galacticraft.rocket.inventory";
        String OPEN_CELESTIAL_SCREEN = "key.galacticraft.open_celestial_screen";
    }

    interface Items {
        String EMPTY_CAN = "item.galacticraft.empty_can";
        String CANNED_FOOD_TEMPLATE = "item.galacticraft.canned_food.template";
    }

    interface CelestialBody {
        String SOL_DESC = "star.galacticraft.sol.description";
        String SOL = "star.galacticraft.sol";

        String ASTEROID_DESC = "planet.galacticraft.asteroid.description";
        String ASTEROID = "planet.galacticraft.asteroid";
        String EARTH_DESC = "planet.galacticraft.earth.description";
        String EARTH = "planet.galacticraft.earth";
        String JUPITER_DESC = "planet.galacticraft.jupiter.description";
        String JUPITER = "planet.galacticraft.jupiter";
        String MARS_DESC = "planet.galacticraft.mars.description";
        String MARS = "planet.galacticraft.mars";
        String MERCURY_DESC = "planet.galacticraft.mercury.description";
        String MERCURY = "planet.galacticraft.mercury";
        String MOON_DESC = "planet.galacticraft.moon.description";
        String MOON = "planet.galacticraft.moon";
        String NEPTUNE_DESC = "planet.galacticraft.neptune.description";
        String NEPTUNE = "planet.galacticraft.neptune";
        String SATURN_DESC = "planet.galacticraft.saturn.description";
        String SATURN = "planet.galacticraft.saturn";
        String URANUS_DESC = "planet.galacticraft.uranus.description";
        String URANUS = "planet.galacticraft.uranus";
        String VENUS_DESC = "planet.galacticraft.venus.description";
        String VENUS = "planet.galacticraft.venus";
        String SATELLITE = "ui.galacticraft.satellite";
        String SATELLITES = "ui.galacticraft.satellites";
        String SATELLITE_DESC = "ui.galacticraft.satellite.description";
    }

    interface Tooltip {
        String CREATIVE_ONLY = "tooltip.galacticraft.creative_only";
        String ENERGY_REMAINING = "tooltip.galacticraft.energy_remaining";
        String GLOWSTONE_LANTERN = "tooltip.galacticraft.glowstone_lantern";
        String GLOWSTONE_TORCH = "tooltip.galacticraft.glowstone_torch";
        String INFINITE = "tooltip.galacticraft.infinite";
        String OXYGEN_REMAINING = "tooltip.galacticraft.oxygen_remaining";
        String PRESS_SHIFT = "tooltip.galacticraft.press_shift";
        String STANDARD_WRENCH = "tooltip.galacticraft.standard_wrench";
        String TIME_UNTIL_COOL = "tooltip.galacticraft.time_until_cool";
        String SECONDS_UNIT = "tooltip.galacticraft.seconds_unit";
        String INCORRECT_NUMBER_OF_SLOTS = "tooltip.galacticraft.incorrect_number_of_slots";
    }

    interface Gas {
        String ARGON = "gas.galacticraft.argon";
        String CARBON_DIOXIDE = "gas.galacticraft.carbon_dioxide";
        String CARBON_MONOXIDE = "gas.galacticraft.carbon_monoxide";
        String HELIUM = "gas.galacticraft.helium";
        String HYDROGEN = "gas.galacticraft.hydrogen";
        String HYDROGEN_DEUTERIUM_OXIDE = "gas.galacticraft.hydrogen_deuterium_oxide";
        String IODINE = "gas.galacticraft.iodine";
        String KRYPTON = "gas.galacticraft.krypton";
        String METHANE = "gas.galacticraft.methane";
        String NEON = "gas.galacticraft.neon";
        String NITRIC_OXIDE = "gas.galacticraft.nitric_oxide";
        String NITROGEN = "gas.galacticraft.nitrogen";
        String NITROGEN_DIOXIDE = "gas.galacticraft.nitrogen_dioxide";
        String NITROUS_OXIDE = "gas.galacticraft.nitrous_oxide";
        String OXYGEN = "gas.galacticraft.oxygen";
        String OZONE = "gas.galacticraft.ozone";
        String WATER_VAPOR = "gas.galacticraft.water_vapor";
        String XENON = "gas.galacticraft.xenon";
    }

    interface SpaceRace {
        String SPACE_RACE_MANAGER = "ui.galacticraft.space_race_manager";
        String ADD_PLAYERS = "ui.galacticraft.space_race_manager.add_players";
        String BACK = "ui.galacticraft.space_race_manager.back";
        String BUTTON = "ui.galacticraft.space_race_manager.button";
        String BUTTON_2 = "ui.galacticraft.space_race_manager.button_2";
        String COMING_SOON = "ui.galacticraft.space_race_manager.coming_soon";
        String EXIT = "ui.galacticraft.space_race_manager.exit";
        String GLOBAL_STATS = "ui.galacticraft.space_race_manager.global_stats";
        String REMOVE_PLAYERS = "ui.galacticraft.space_race_manager.remove_players";
        String SERVER_STATS = "ui.galacticraft.space_race_manager.server_stats";
        String FLAG_CONFIRM = "ui.galacticraft.space_race_manager.flag.confirm";
        String FLAG_CONFIRM_MESSAGE = "ui.galacticraft.space_race_manager.flag.confirm.message";
        String CUSTOMIZE_FLAG = "ui.galacticraft.space_race_manager.customize_flag";
        String DRAG_AND_DROP_FLAG = "ui.galacticraft.space_race_manager.drap_and_drop_flag";
        String TEAM_COLOR_1 = "ui.galacticraft.space_race_manager.team_color_1";
        String TEAM_COLOR_2 = "ui.galacticraft.space_race_manager.team_color_2";
        String TEAM_COLOR_3 = "ui.galacticraft.space_race_manager.team_color_3";
        String RED = "ui.galacticraft.space_race_manager.color.red";
        String GREEN = "ui.galacticraft.space_race_manager.color.green";
        String BLUE = "ui.galacticraft.space_race_manager.color.blue";
    }

    interface SolarPanel {
        String ATMOSPHERIC_INTERFERENCE = "ui.galacticraft.machine.solar_panel.atmospheric_interference";
        String BLOCKED = "ui.galacticraft.machine.solar_panel.blocked";
        String DAY = "ui.galacticraft.machine.solar_panel.day";
        String MISSING_SOURCE = "ui.galacticraft.machine.solar_panel.missing_source";
        String NIGHT = "ui.galacticraft.machine.solar_panel.night";
        String OVERCAST = "ui.galacticraft.machine.solar_panel.overcast";
        String STORMY = "ui.galacticraft.machine.solar_panel.stormy";
        String LIGHT_SOURCE = "ui.galacticraft.machine.solar_panel.source";
        String LIGHT_SOURCE_MOON = "ui.galacticraft.machine.solar_panel.source.moon";
        String LIGHT_SOURCE_EARTH = "ui.galacticraft.machine.solar_panel.source.earth";
        String LIGHT_SOURCE_NONE = "ui.galacticraft.machine.solar_panel.source.none";
        String LIGHT_SOURCE_RAIN = "ui.galacticraft.machine.solar_panel.source.rain";
        String LIGHT_SOURCE_THUNDER = "ui.galacticraft.machine.solar_panel.source.thunder";
        String LIGHT_SOURCE_SUN = "ui.galacticraft.machine.solar_panel.source.sun";
        String STATUS = "ui.galacticraft.machine.solar_panel.status";
        String STRENGTH = "ui.galacticraft.machine.solar_panel.strength";
    }

    interface MachineStatus {
        String GENERATING = "ui.galacticraft.status.generating";
        String NO_FUEL = "ui.galacticraft.status.no_fuel";
        String WARMING_UP = "ui.galacticraft.status.warming_up";
        String COOLING_DOWN = "ui.galacticraft.status.cooling_down";
        String PARTIALLY_GENERATING = "ui.galacticraft.status.partially_generating";
        String NOT_GENERATING = "ui.galacticraft.status.not_generating";
        String BLOCKED = "ui.galacticraft.status.blocked";
        String FABRICATING = "ui.galacticraft.status.fabricating";
        String COMPRESSING = "ui.galacticraft.status.compressing";
        String SMELTING = "ui.galacticraft.status.smelting";
        String NOT_ENOUGH_OXYGEN = "ui.galacticraft.status.not_enough_oxygen";
        String COLLECTING = "ui.galacticraft.status.collecting";
        String COMPRESSING_OXYGEN = "ui.galacticraft.status.compressing_oxygen";
        String DECOMPRESSING = "ui.galacticraft.status.decompressing";
        String MISSING_OXYGEN_TANK = "ui.galacticraft.status.missing_oxygen_tank";
        String OXYGEN_TANK_FULL = "ui.galacticraft.status.oxygen_tank_full";
        String EMPTY_OXYGEN_TANK = "ui.galacticraft.status.empty_oxygen_tank";
        String ALREADY_SEALED = "ui.galacticraft.status.already_sealed";
        String AREA_TOO_LARGE = "ui.galacticraft.status.area_too_large";
        String SEALED = "ui.galacticraft.status.sealed";
        String DISTRIBUTING = "ui.galacticraft.status.distributing";
        String REFINING = "ui.galacticraft.status.refining";
        String MISSING_OIL = "ui.galacticraft.status.missing_oil";
        String FUEL_TANK_FULL = "ui.galacticraft.status.fuel_tank_full";
        String PREPARING = "ui.galacticraft.status.preparing";
        String LOADING = "ui.galacticraft.status.loading";
        String NOT_ENOUGH_FUEL = "ui.galacticraft.status.not_enough_fuel";
        String NO_ROCKET = "ui.galacticraft.status.no_rocket";
        String ROCKET_IS_FULL = "ui.galacticraft.status.rocket_is_full";
        String CANNING = "ui.galacticraft.status.canning";
        String TRANSFERRING_CAN = "ui.galacticraft.status.transferring_can";
        String NO_FOOD = "ui.galacticraft.status.no_food";
        String MISSING_EMPTY_CAN = "ui.galacticraft.status.missing_empty_can";
    }

    interface CelestialSelection {
        String BACK = "ui.galacticraft.celestialselection.back";
        String CANCEL = "ui.galacticraft.celestialselection.cancel";
        String CATALOG = "ui.galacticraft.celestialselection.catalog";
        String LAUNCH = "ui.galacticraft.celestialselection.launch";
        String RENAME = "ui.galacticraft.celestialselection.rename";
        String TIER = "ui.galacticraft.celestialselection.tier";
        String ASSIGN_NAME = "ui.galacticraft.celestialselection.assign_name";
        String APPLY = "ui.galacticraft.celestialselection.apply";
        String EXIT = "ui.galacticraft.celestialselection.exit";
        String SELECT_SS = "ui.galacticraft.celestialselection.select_ss";
        String SS_OWNER = "ui.galacticraft.celestialselection.ss_owner";
        String CAN_CREATE_SPACE_STATION = "ui.galacticraft.celestialselection.can_create_space_station";
        String CANNOT_CREATE_SPACE_STATION = "ui.galacticraft.celestialselection.cannot_create_space_station";
        String CREATE_SPACE_STATION = "ui.galacticraft.celestialselection.create_ss";
        String DAY_NIGHT_CYCLE = "ui.galacticraft.celestialselection.day_night_cycle";
        String SURFACE_GRAVITY = "ui.galacticraft.celestialselection.surface_gravity";
        String SURFACE_COMPOSITION = "ui.galacticraft.celestialselection.surface_composition";
        String ATMOSPHERE = "ui.galacticraft.celestialselection.atmosphere";
        String MEAN_SURFACE_TEMP = "ui.galacticraft.celestialselection.mean_surface_temp";

        String CLICK_AGAIN = "ui.galacticraft.celestialselection.click_again";
        String CLICK_AGAIN_MOONS = "ui.galacticraft.celestialselection.click_again.moons";
        String CLICK_AGAIN_SATELLITES = "ui.galacticraft.celestialselection.click_again.satellites";
        String CLICK_AGAIN_MOONS_AND_SATELLITES = "ui.galacticraft.celestialselection.click_again.moons_and_satellites";
    }

    interface Ui {
        String CONE = "ui.galacticraft.cone";
        String BODY = "ui.galacticraft.body";
        String FINS = "ui.galacticraft.fins";
        String BOOSTER = "ui.galacticraft.booster";
        String ENGINE = "ui.galacticraft.engine";
        String UPGRADE = "ui.galacticraft.upgrade";
        String COLOR = "ui.galacticraft.color";

        String AIRLOCK_REDSTONE_SIGNAL = "ui.galacticraft.airlock.redstone_signal";
        String AIRLOCK_OWNER = "ui.galacticraft.airlock.owner";
        String ALPHA_WARNING_1 = "ui.galacticraft.alpha_warning.content1";
        String ALPHA_WARNING_2 = "ui.galacticraft.alpha_warning.content2";
        String ALPHA_WARNING_3 = "ui.galacticraft.alpha_warning.content3";
        String ALPHA_WARNING_HEADER = "ui.galacticraft.alpha_warning.header";

        String BUBBLE_CURRENT_SIZE = "ui.galacticraft.bubble_distributor.current_size";
        String BUBBLE_NOT_VISIBLE = "ui.galacticraft.bubble_distributor.not_visible";
        String BUBBLE_TARGET_SIZE = "ui.galacticraft.bubble_distributor.size";
        String BUBBLE_VISIBLE = "ui.galacticraft.bubble_distributor.visible";

        String COLLECTING = "ui.galacticraft.machine.collecting";
        String CURRENT_OXYGEN = "ui.galacticraft.machine.current_oxygen";
        String GJT = "ui.galacticraft.machine.gj_per_t";
        String MILLIBUCKETS = "ui.galacticraft.machine.millibuckets";
        String MAX_OXYGEN = "ui.galacticraft.machine.max_oxygen";
        String MACHINE_STATUS = "ui.galacticraft.machine.status";
        String OXYGEN_TANK_1_LEVEL = "ui.galacticraft.player_inv_screen.oxygen_tank_1_level";
        String OXYGEN_TANK_2_LEVEL = "ui.galacticraft.player_inv_screen.oxygen_tank_2_level";
        String OXYGEN_WARNING = "ui.galacticraft.oxygen.warning";
        String OXYGEN_SETUP_INVALID = "ui.galacticraft.oxygen.invalid_setup";
        String ROCKET_FUEL = "ui.galacticraft.rocket.fuel";
        String ROCKET_FULL = "ui.galacticraft.rocket.full";
        String ROCKET_NO_FUEL = "ui.galacticraft.rocket.no_fuel";
        String LANDER_WARNING = "ui.galacticraft.lander.warning";
        String LANDER_CONTROLS = "ui.galacticraft.lander.controls";
        String LANDER_VELOCITY = "ui.galacticraft.lander.velocity";

        String SMALL_STEP = "ui.galacticraft.small_step";
        String GIANT_LEAP = "ui.galacticraft.giant_leap";
        String PREPARE_FOR_ENTRY = "ui.galacticraft.prepare_for_entry";
        String TRAVELING_TO = "ui.galacticraft.traveling_to";
        String TOTAL_NUTRITION = "ui.galacticraft.total_nutrition";
        String SPACE_STATION_NAME = "ui.galacticraft.space_station_name";

        String CAPE_BUTTON = "ui.options.cape";
        String CAPES_TITLE = "ui.capes.title";
        String CAPES_STATE = "galacticraft.capes.state.";
        String CAPE = "cape.galacticraft.";
    }

    interface Boss {
        String SKELETON_BOSS_DESPAWN = "gui.skeleton_boss.message";
    }

    interface Misc {
        String UPGRADE_TITANIUM_APPLIES_TO = "smithing_template.galacticraft.titanium_upgrade.applies_to";
        String UPGRADE_TITANIUM_INGREDIENTS = "smithing_template.galacticraft.titanium_upgrade.ingredients";
        String UPGRADE_TITANIUM_DESCRIPTION = "smithing_template.galacticraft.titanium_upgrade.description";
        String UPGRADE_TITANIUM_BASE_SLOT_DESCRIPTION = "smithing_template.galacticraft.titanium_upgrade.base_slot_description";
        String UPGRADE_TITANIUM_ADDITIONS_SLOT_DESCRIPTON = "smithing_template.galacticraft.titanium_upgrade.additions_slot_description";
    }
}
