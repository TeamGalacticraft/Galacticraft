package dev.galacticraft.mod.data.tag;

import dev.galacticraft.mod.tag.GCTags;
import dev.galacticraft.mod.world.gen.structure.GCStructures;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.concurrent.CompletableFuture;

public class GCStructureTagProvider extends FabricTagProvider<Structure> {
    public GCStructureTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.STRUCTURE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        tag(GCTags.MOON_RUINS)
                .add(GCStructures.Moon.RUINS);
    }
}
