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
    public static void generateDocsTranslations(GCTranslationProvider t) {
        // --- GENERAL ---
        t.add("key.gc.inspect", "Inspect");
        t.add("key.categories.galacticraft", "Galacticraft");
        t.add("tooltip.gc.inspect_hint", "Press %s to inspect");
        t.add("gc.docs.back", "Back");
        t.add("doc.unimplemented", "Unimplemented");

        // --- HOME PAGE ---
        t.add("doc.gc.home.title", "Galacticraft Documentation");
        t.add("doc.gc.home.intro", "Galacticraft 5 Rewoven. The number 1 space mod rewritten for the latest version of Minecraft.");
        t.add("doc.gc.home.category_label", "Categories");
        t.add("doc.gc.home.category_general", "General");
        t.add("doc.gc.home.category_machines", "Machines");
        t.add("doc.gc.home.category_rockets", "Rockets");
        t.add("doc.gc.home.category_planets", "Planets");
        t.add("doc.gc.home.category_entities", "Entities");
        t.add("doc.gc.home.category_blocks", "Blocks");
        t.add("doc.gc.home.category_items", "Items");
        t.add("doc.gc.home.category_fluids_and_gasses", "Fluids and Gasses");

        // --- OTHER ---
        t.add("doc.common.overview", "Overview");
        t.add("doc.gc.airlock.title", "Airlock Controller");
        t.add("doc.gc.airlock.overview", "Place the Airlock Controller and connect frames. Power it with redstone to toggle the door.");
        t.add("doc.gc.home.btn.airlock", "Airlock Controller");
    }
}