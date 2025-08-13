package dev.galacticraft.mod.api.documentation.datagen;

import dev.galacticraft.mod.data.GCTranslationProvider;

public class DocsTranslations {
    public static void generateDocsTranslations(GCTranslationProvider t) {
        t.add("key.gc.inspect", "Inspect");
        t.add("key.categories.galacticraft", "Galacticraft");
        t.add("tooltip.gc.inspect_hint", "Hold %s to inspect");
        t.add("gc.docs.back", "Back");

        t.add("doc.gc.home.title", "Galacticraft Documentation");
        t.add("doc.common.overview", "Overview");
        t.add("doc.gc.airlock.title", "Airlock Controller");
        t.add("doc.gc.airlock.overview", "Place the Airlock Controller and connect frames. Power it with redstone to toggle the door.");
        t.add("doc.gc.home.intro", "Welcome! Browse categories or search for a block/item and press Inspect.");
        t.add("doc.gc.home.btn.airlock", "Airlock Controller");
    }
}