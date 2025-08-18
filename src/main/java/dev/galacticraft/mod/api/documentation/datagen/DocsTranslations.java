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

package dev.galacticraft.mod.api.documentation.datagen;

import dev.galacticraft.mod.data.GCTranslationProvider;

public class DocsTranslations {
    public static final String INSPECT_KEY = "key.gc.inspect";
    public static final String INSPECT_HINT = "tooltip.gc.inspect_hint";
    public static final String BACK = "gc.docs.back";
    public static final String UNIMPLEMENTED = "doc.unimplemented";

    public static final String HOME_TITLE = "doc.gc.home.title";
    public static final String HOME_INTRO = "doc.gc.home.intro";
    public static final String HOME_CATEGORY_LABEL = "doc.gc.home.category_label";

    public static final String GENERAL_CATEGORY = "doc.gc.home.category_general";
    public static final String MACHINES_CATEGORY = "doc.gc.home.category_machines";
    public static final String ROCKETS_CATEGORY = "doc.gc.home.category_rockets";
    public static final String PLANETS_CATEGORY = "doc.gc.home.category_planets";
    public static final String ENTITIES_CATEGORY = "doc.gc.home.category_entities";
    public static final String BLOCKS_CATEGORY = "doc.gc.home.category_blocks";
    public static final String ITEMS_CATEGORY = "doc.gc.home.category_items";
    public static final String FLUIDS_AND_GASES_CATEGORY = "doc.gc.home.category_fluids_and_gases";

    public static final String GENERATORS = "doc.gc.machines.generators";
    public static final String MULTIBLOCKS = "doc.gc.machines.multiblocks";
    public static final String FUEL_MACHINES = "doc.gc.machines.fuel_machines";
    public static final String FUNCTIONAL_MACHINES = "doc.gc.machines.functional_machines";
    public static final String ENERGY_MACHINES = "doc.gc.machines.energy_machines";
    public static final String OXYGEN_MACHINES = "doc.gc.machines.oxygen_machines";

    public static final String COAL_GENERATOR = "doc.gc.coal_generator";
    public static final String BASIC_SOLAR_PANEL = "doc.gc.basic_solar_panel";
    public static final String ADVANCED_SOLAR_PANEL = "doc.gc.advanced_solar_panel";
    public static final String OXYGEN_COLLECTOR = "doc.gc.oxygen_collector";
    public static final String OXYGEN_COMPRESSOR = "doc.gc.oxygen_compressor";
    public static final String OXYGEN_DECOMPRESSOR = "doc.gc.oxygen_decompressor";
    public static final String OXYGEN_STORAGE_MODULE = "doc.gc.oxygen_storage_module";
    public static final String OXYGEN_SEALER = "doc.gc.oxygen_sealer";
    public static final String BUBBLE_DISTRIBUTOR = "doc.gc.bubble_distributor";
    public static final String REFINERY = "doc.gc.refinery";
    public static final String FUEL_LOADER = "doc.gc.fuel_loader";
    public static final String ENERGY_STORAGE_MODULE = "doc.gc.energy_storage_module";
    public static final String CIRCUIT_FABRICATOR = "doc.gc.circuit_fabricator";
    public static final String COMPRESSOR = "doc.gc.compressor";
    public static final String ELECTRIC_COMPRESSOR = "doc.gc.electric_compressor";
    public static final String ELECTRIC_FURNACE = "doc.gc.electric_furnace";
    public static final String ELECTRIC_ARC_FURNACE = "doc.gc.electric_arc_furnace";
    public static final String FOOD_CANNER = "doc.gc.food_cannner";
    public static final String AIRLOCK = "doc.gc.airlock";

    public static void generateDocsTranslations(GCTranslationProvider t) {
        // --- GENERAL ---
        t.add(INSPECT_KEY, "Inspect");
        t.add(INSPECT_HINT, "Press %s to inspect");
        t.add(BACK, "Back");
        t.add(UNIMPLEMENTED, "Unimplemented");

        // --- HOME PAGE ---
        t.add(HOME_TITLE, "Galacticraft Documentation");
        t.add(HOME_INTRO, "Galacticraft 5 Rewoven. The number 1 space mod rewritten for the latest version of Minecraft.");
        t.add(HOME_CATEGORY_LABEL, "Categories");
        t.add(GENERAL_CATEGORY, "General");
        t.add(MACHINES_CATEGORY, "Machines");
        t.add(ROCKETS_CATEGORY, "Rockets");
        t.add(PLANETS_CATEGORY, "Planets");
        t.add(ENTITIES_CATEGORY, "Entities");
        t.add(BLOCKS_CATEGORY, "Blocks");
        t.add(ITEMS_CATEGORY, "Items");
        t.add(FLUIDS_AND_GASES_CATEGORY, "Fluids and Gases");

        // --- MACHINES ---
        t.add(GENERATORS, "Generators");
        t.add(MULTIBLOCKS, "Multiblocks");
        t.add(FUEL_MACHINES, "Fuel Machines");
        t.add(FUNCTIONAL_MACHINES, "Functional Machines");
        t.add(ENERGY_MACHINES, "Energy Machines");
        t.add(OXYGEN_MACHINES, "Oxygen Machines");

        t.add(COAL_GENERATOR, "Coal Generator");
        t.add(BASIC_SOLAR_PANEL, "Basic Solar Panel");
        t.add(ADVANCED_SOLAR_PANEL, "Advanced Solar Panel");
        t.add(OXYGEN_COLLECTOR, "Oxygen Collector");
        t.add(OXYGEN_COMPRESSOR, "Oxygen Compressor");
        t.add(OXYGEN_DECOMPRESSOR, "Oxygen Decompressor");
        t.add(OXYGEN_STORAGE_MODULE, "Oxygen Storage Module");
        t.add(OXYGEN_SEALER, "Oxygen Sealer");
        t.add(BUBBLE_DISTRIBUTOR, "Bubble Distributor");
        t.add(REFINERY, "Refinery");
        t.add(FUEL_LOADER, "Fuel Loader");
        t.add(ENERGY_STORAGE_MODULE, "Energy Storage Module");
        t.add(CIRCUIT_FABRICATOR, "Circuit Fabricator");
        t.add(COMPRESSOR, "Compressor");
        t.add(ELECTRIC_COMPRESSOR, "Electric Compressor");
        t.add(ELECTRIC_FURNACE, "Electric Furnace");
        t.add(ELECTRIC_ARC_FURNACE, "Electric Arc Furnace");
        t.add(FOOD_CANNER, "Food Canner");
        t.add(AIRLOCK, "Airlock");

        // --- OTHER ---
        t.add("doc.common.overview", "Overview");
        t.add("doc.gc.airlock.title", "Airlock Controller");
        t.add("doc.gc.airlock.overview", "Place the Airlock Controller and connect frames. Power it with redstone to toggle the door.");
        t.add("doc.gc.home.btn.airlock", "Airlock Controller");
    }
}