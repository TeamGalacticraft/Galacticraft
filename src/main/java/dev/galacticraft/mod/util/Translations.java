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

package dev.galacticraft.mod.util;

public interface Translations {
    interface ItemGroup {
        String ITEMS = "itemGroup.galacticraft.items";
        String BLOCKS = "itemGroup.galacticraft.blocks";
        String MACHINES = "itemGroup.galacticraft.machines";
    }

    interface RecipeCategory {
        String CIRCUIT_FABRICATOR = "category.recipe_viewer.circuit_fabricator";
        String COMPRESSOR = "category.recipe_viewer.compressing";
    }

    interface Chat {
        String BED_FAIL = "chat.galacticraft.bed_fail";
        String ROCKET_WARNING = "chat.galacticraft.rocket.warning";
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

        String DEBUG = "config.galacticraft.debug";
        String DEBUG_LOGGING = "config.galacticraft.debug.logging";
        String HIDE_ALPHA_WARNING = "config.galacticraft.debug.hide_alpha_warning";

        String ENERGY = "config.galacticraft.energy";

        String WIRES = "config.galacticraft.energy.wires";
        String WIRE_ENERGY_TRANSFER_LIMIT = "config.galacticraft.energy.wires.transfer_limit";
        String HEAVY_WIRE_ENERGY_TRANSFER_LIMIT = "config.galacticraft.energy.wires.heavy_transfer_limit";

        String MACHINES = "config.galacticraft.energy.machines";
        String COAL_GENERATOR_ENERGY_PRODUCTION_RATE = "config.galacticraft.energy.machines.coal_generator_energy_production_rate";
        String SOLAR_PANEL_ENERGY_PRODUCTION_RATE = "config.galacticraft.energy.machines.solar_panel_energy_production_rate";
        String CIRCUIT_FABRICATOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.circuit_fabricator_energy_consumption_rate";
        String ELECTRIC_COMPRESSOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.electric_compressor_energy_consumption_rate";
        String OXYGEN_COLLECTOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.oxygen_collector_energy_consumption_rate";
        String REFINERY_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.refinery_energy_consumption_rate";
        String ELECTRIC_FURNACE_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.electric_furnace_energy_consumption_rate";
        String ENERGY_STORAGE_MODULE_STORAGE_SIZE = "config.galacticraft.energy.machines.energy_storage_module_storage_size";
        String ENERGY_STORAGE_SIZE = "config.galacticraft.energy.machines.energy_storage_size";
        String OXYGEN_COMPRESSOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.oxygen_compressor_energy_consumption_rate";
        String OXYGEN_DECOMPRESSOR_ENERGY_CONSUMPTION_RATE = "config.galacticraft.energy.machines.oxygen_decompressor_energy_consumption_rate";

        String CLIENT = "config.galacticraft.client";

        String PLAYER = "config.galacticraft.player";
        String PLAYER_LIFE_SUPPORT = "config.galacticraft.lifesupport";
        String PLAYER_OXYGEN_CONSUMPTION_RATE = "config.galacticraft.player.lifesupport.oxygen_consumption_rate";
        String BOSS_HEALTH_MODIFIER = "config.galacticraft.difficulty.dungeon_boss_health_multiplier";
        String BOSS_HEALTH_MODIFIER_DESC = "config.galacticraft.difficulty.dungeon_boss_health_multiplier.desc";
        String SKYBOX = "config.galacticraft.client.skybox";

        String COMMANDS = "config.galacticraft.commands";
        String ENABLE_GC_HOUSTON = "config.galacticraft.commands.enable_gc_houston";
    }

    interface Galaxy {
        String MILKY_WAY = "galaxy.galacticraft.milky_way.name";
        String MILKY_WAY_DESCRIPTION = "galaxy.galacticraft.milky_way.description";
    }

    interface Keybindings {
        String ROCKET_INVENTORY = "key.galacticraft.rocket.inventory";
        String OPEN_CELESTIAL_SCREEN = "key.galacticraft.open_celestial_screen";
    }

    interface CelestialBody {
        String SOL_DESC = "star.galacticraft.sol.description";
        String SOL = "star.galacticraft.sol";

        String ASTEROIDS_DESC = "planet.galacticraft.asteroids.description";
        String ASTEROIDS = "planet.galacticraft.asteroids";
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
    }

    interface Gas {
        String ARGON = "gas.galacticraft.argon";
        String CARBON_DIOXIDE = "gas.galacticraft.carbon_dioxide";
        String CARBON_MONOXIDE = "gas.galacticraft.carbon_monoxide";
        String HELIUM = "gas.galacticraft.helium";
        String HYDROGEN = "gas.galacticraft.hydrogen";
        String IODINE = "gas.galacticraft.iodine";
        String KRYPTON = "gas.galacticraft.krypton";
        String METHANE = "gas.galacticraft.methane";
        String NEON = "gas.galacticraft.neon";
        String NITROGEN = "gas.galacticraft.nitrogen";
        String NITROUS_DIOXIDE = "gas.galacticraft.nitrous_dioxide";
        String NITROUS_OXIDE = "gas.galacticraft.nitrous_oxide";
        String OXYGEN = "gas.galacticraft.oxygen";
        String OZONE = "gas.galacticraft.ozone";
        String WATER_VAPOR = "gas.galacticraft.water_vapor";
        String XENON = "gas.galacticraft.xenon";
        String HYDROGEN_DEUTERIUM_OXYGEN = "gas.galacticraft.hydrogen_deuterium_oxygen";
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
    }

    interface SolarPanel {
        String ATMOSPHERIC_INTERFERENCE = "ui.galacticraft.machine.solar_panel.atmospheric_interference";
        String BLOCKED = "ui.galacticraft.machine.solar_panel.blocked";
        String DAY = "ui.galacticraft.machine.solar_panel.day";
        String MISSING_SOURCE = "ui.galacticraft.machine.solar_panel.missing_source";
        String NIGHT = "ui.galacticraft.machine.solar_panel.night";
        String OVERCAST = "ui.galacticraft.machine.solar_panel.overcast";
        String LIGHT_SOURCE = "ui.galacticraft.machine.solar_panel.source";
        String LIGHT_SOURCE_MOON = "ui.galacticraft.machine.solar_panel.source.moon";
        String LIGHT_SOURCE_EARTH = "ui.galacticraft.machine.solar_panel.source.earth";
        String LIGHT_SOURCE_NONE = "ui.galacticraft.machine.solar_panel.source.none";
        String LIGHT_SOURCE_RAIN = "ui.galacticraft.machine.solar_panel.source.rain";
        String LIGHT_SOURCE_SUN = "ui.galacticraft.machine.solar_panel.source.sun";
        String STATUS = "ui.galacticraft.machine.solar_panel.status";
        String STRENGTH = "ui.galacticraft.machine.solar_panel.strength";
    }

    interface MachineStatus {
        String ALREADY_SEALED = "ui.galacticraft.status.already_sealed";
        String AREA_TOO_LARGE = "ui.galacticraft.status.area_too_large";
        String BLOCKED = "ui.galacticraft.status.blocked";
        String COLLECTING = "ui.galacticraft.status.collecting";
        String COMPRESSING = "ui.galacticraft.status.compressing";
        String COOLING_DOWN = "ui.galacticraft.status.cooling_down";
        String DECOMPRESSING = "ui.galacticraft.status.decompressing";
        String DISTRIBUTING = "ui.galacticraft.status.distributing";
        String EMPTY_OXYGEN_TANK = "ui.galacticraft.status.empty_oxygen_tank";
        String FABRICATING = "ui.galacticraft.status.fabricating";
        String FUEL_TANK_FULL = "ui.galacticraft.status.fuel_tank_full";
        String GENERATING = "ui.galacticraft.status.generating";
        String LOADING = "ui.galacticraft.status.loading";
        String MISSING_OIL = "ui.galacticraft.status.missing_oil";
        String MISSING_OXYGEN_TANK = "ui.galacticraft.status.missing_oxygen_tank";
        String NIGHT = "ui.galacticraft.status.night";
        String NO_FUEL = "ui.galacticraft.status.no_fuel";
        String NOT_ENOUGH_OXYGEN = "ui.galacticraft.status.not_enough_oxygen";
        String OXYGEN_TANK_FULL = "ui.galacticraft.status.oxygen_tank_full";
        String PARTIALLY_BLOCKED = "ui.galacticraft.status.partially_blocked";
        String RAIN = "ui.galacticraft.status.rain";
        String SEALED = "ui.galacticraft.status.sealed";
        String WARMING_UP = "ui.galacticraft.status.warming_up";
        String NOT_ENOUGH_FUEL = "ui.galacticraft.status.not_enough_fuel";
        String NO_ROCKET = "ui.galacticraft.status.no_rocket";
        String ROCKET_IS_FULL ="ui.galacticraft.status.rocket_is_full";
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
        String MAX_OXYGEN = "ui.galacticraft.machine.max_oxygen";
        String MACHINE_STATUS = "ui.galacticraft.machine.status";
        String OXYGEN_TANK_LEVEL = "ui.galacticraft.player_inv_screen.oxygen_tank_level";
        String ROCKET_FUEL = "ui.galacticraft.rocket.fuel";
        String ROCKET_FULL = "ui.galacticraft.rocket.full";
        String ROCKET_NO_FUEL = "ui.galacticraft.rocket.no_fuel";
        String LANDER_VELOCITY = "ui.lander.velocity";
        String LANDER_VELOCITYU = "ui.lander.velocityu";
        String LANDER_WARNING_2 = "ui.lander.warning2";
        String LANDER_WARNING_3 = "ui.lander.warning3";
        String LANDER_WARNING = "ui.warning";

        String SMALL_STEP = "ui.galacticraft.small_step";
        String GIANT_LEAP = "ui.galacticraft.giant_leap";
        String PREPARE_FOR_ENTRY = "ui.galacticraft.prepare_for_entry";
        String TRAVELLING_TO = "ui.galacticraft.travelling_to";
    }

    interface Misc {
        String UPGRADE_TITANIUM_APPLIES_TO = "smithing_template.galacticraft.titanium_upgrade.applies_to";
        String UPGRADE_TITANIUM_INGREDIENTS = "smithing_template.galacticraft.titanium_upgrade.ingredients";
        String UPGRADE_TITANIUM_DESCRIPTION = "smithing_template.galacticraft.titanium_upgrade.description";
        String UPGRADE_TITANIUM_BASE_SLOT_DESCRIPTION = "smithing_template.galacticraft.titanium_upgrade.base_slot_description";
        String UPGRADE_TITANIUM_ADDITIONS_SLOT_DESCRIPTON = "smithing_template.galacticraft.titanium_upgrade.additions_slot_description";
    }
}
