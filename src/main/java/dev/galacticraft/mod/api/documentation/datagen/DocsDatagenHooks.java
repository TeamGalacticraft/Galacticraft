package dev.galacticraft.mod.api.documentation.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public final class DocsDatagenHooks {
    private DocsDatagenHooks() {}
    public static void register(FabricDataGenerator.Pack pack) {
        pack.addProvider(DocsJsonProvider::new);
    }
}