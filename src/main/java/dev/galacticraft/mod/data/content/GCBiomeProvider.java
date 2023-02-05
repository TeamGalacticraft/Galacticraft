package dev.galacticraft.mod.data.content;

import dev.galacticraft.mod.world.biome.GCBiomes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class GCBiomeProvider extends FabricDynamicRegistryProvider {
    public GCBiomeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> future) {
        super(output, future);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        GCBiomes.bootstrapRegistries(new GeneratingBootstrapContext<>(registries, entries));
    }

    @Override
    public String getName() {
        return "Galacticraft Biomes";
    }
}
