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
    public static final String FLUIDS_AND_GASSES_CATEGORY = "doc.gc.home.category_fluids_and_gasses";

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
        t.add(FLUIDS_AND_GASSES_CATEGORY, "Fluids and Gasses");

        // --- OTHER ---
        t.add("doc.common.overview", "Overview");
        t.add("doc.gc.airlock.title", "Airlock Controller");
        t.add("doc.gc.airlock.overview", "Place the Airlock Controller and connect frames. Power it with redstone to toggle the door.");
        t.add("doc.gc.home.btn.airlock", "Airlock Controller");
    }
}