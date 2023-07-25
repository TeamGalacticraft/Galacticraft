/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import com.mojang.serialization.Lifecycle;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.impl.universe.galaxy.GalaxyImpl;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCCelestialBodies;
import dev.galacticraft.mod.content.GCRocketParts;
import dev.galacticraft.mod.content.GCTeleporterTypes;
import dev.galacticraft.mod.content.entity.damage.GCDamageTypes;
import dev.galacticraft.mod.data.content.*;
import dev.galacticraft.mod.data.model.GCModelProvider;
import dev.galacticraft.mod.data.tag.*;
import dev.galacticraft.mod.structure.GCStructureSets;
import dev.galacticraft.mod.structure.GCStructureTemplatePools;
import dev.galacticraft.mod.world.biome.GCBiomes;
import dev.galacticraft.mod.world.biome.source.GCMultiNoiseBiomeSourceParameterLists;
import dev.galacticraft.mod.world.dimension.GCDimensionTypes;
import dev.galacticraft.mod.world.dimension.GCLevelStems;
import dev.galacticraft.mod.world.gen.GCNoiseGeneratorSettings;
import dev.galacticraft.mod.world.gen.carver.GCConfiguredCarvers;
import dev.galacticraft.mod.world.gen.feature.GCConfiguredFeature;
import dev.galacticraft.mod.world.gen.feature.GCOreConfiguredFeature;
import dev.galacticraft.mod.world.gen.feature.GCOrePlacedFeatures;
import dev.galacticraft.mod.world.gen.feature.GCPlacedFeatures;
import dev.galacticraft.mod.world.gen.structure.GCStructures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import org.jetbrains.annotations.NotNull;

public class GCDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(@NotNull FabricDataGenerator generator) {
        var pack = generator.createPack();

        pack.addProvider(BootstrapDataProvider.create("Galaxies", GalaxyImpl::bootstrapRegistries));

        pack.addProvider(GCBlockLootTableProvider::new);
        pack.addProvider(GCRecipeProvider::new);

        // tags
        pack.addProvider(GCBannerTagProvider::new);
        pack.addProvider(GCBiomeTagProvider::new);
        pack.addProvider(GCBlockTagProvider::new);
        pack.addProvider(GCItemTagProvider::new);
        pack.addProvider(GCFluidTagProvider::new);
        pack.addProvider(GCStructureTagProvider::new);
        pack.addProvider((output, registriesFuture) -> new GCLevelStemProvider(output, registriesFuture,
                GCLevelStems::bootstrapRegistries)); // level stems are special

        // content
        pack.addProvider(BootstrapDataProvider.create("Biomes", GCBiomes::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Celestial Bodies", GCCelestialBodies::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Celestial Teleporters", GCTeleporterTypes::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Dimension Types", GCDimensionTypes::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Noise Generator Settings", GCNoiseGeneratorSettings::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Structures", GCStructures::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Structure Sets", GCStructureSets::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Structure Template Pools", GCStructureTemplatePools::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Configured Carvers", GCConfiguredCarvers::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Configured Features", GCConfiguredFeature::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Ore Configured Features", GCOreConfiguredFeature::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Ore Placed Features", GCOrePlacedFeatures::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Placed Features", GCPlacedFeatures::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Multi Noise Biome Source Parameter Lists", GCMultiNoiseBiomeSourceParameterLists::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Damage Types", GCDamageTypes::bootstrapRegistries));

        // rocket parts
        pack.addProvider(BootstrapDataProvider.create("Rocket Cones", GCRocketParts::bootstrapCone));
        pack.addProvider(BootstrapDataProvider.create("Rocket Bodies", GCRocketParts::bootstrapBody));
        pack.addProvider(BootstrapDataProvider.create("Rocket Fins", GCRocketParts::bootstrapFin));
        pack.addProvider(BootstrapDataProvider.create("Rocket Boosters", GCRocketParts::bootstrapBooster));
        pack.addProvider(BootstrapDataProvider.create("Rocket Bottoms", GCRocketParts::bootstrapBottom));
        pack.addProvider(BootstrapDataProvider.create("Rocket Upgrades", GCRocketParts::bootstrapUpgrade));

        // models
        pack.addProvider(GCModelProvider::new);
    }

    @Override
    public String getEffectiveModId() {
        return Constant.MOD_ID;
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        DataGeneratorEntrypoint.super.buildRegistry(registryBuilder);

        registryBuilder.add(AddonRegistries.GALAXY, GalaxyImpl::bootstrapRegistries);
        registryBuilder.add(RocketRegistries.ROCKET_CONE, GCRocketParts::bootstrapCone);
        registryBuilder.add(RocketRegistries.ROCKET_BODY, GCRocketParts::bootstrapBody);
        registryBuilder.add(RocketRegistries.ROCKET_FIN, GCRocketParts::bootstrapFin);
        registryBuilder.add(RocketRegistries.ROCKET_BOOSTER, GCRocketParts::bootstrapBooster);
        registryBuilder.add(RocketRegistries.ROCKET_BOTTOM, GCRocketParts::bootstrapBottom);
        registryBuilder.add(RocketRegistries.ROCKET_UPGRADE, GCRocketParts::bootstrapUpgrade);
        registryBuilder.add(RocketRegistries.ROCKET_PART_RECIPE, RocketPartRecipe::bootstrapRegistries);

        registryBuilder.add(Registries.BIOME, Lifecycle.stable(), GCBiomes::bootstrapRegistries);
        registryBuilder.add(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, Lifecycle.stable(), GCMultiNoiseBiomeSourceParameterLists::bootstrapRegistries);
        registryBuilder.add(AddonRegistries.CELESTIAL_BODY, Lifecycle.stable(), GCCelestialBodies::bootstrapRegistries);
        registryBuilder.add(AddonRegistries.CELESTIAL_TELEPORTER, Lifecycle.stable(), GCTeleporterTypes::bootstrapRegistries);
        registryBuilder.add(Registries.DIMENSION_TYPE, Lifecycle.stable(), GCDimensionTypes::bootstrapRegistries);
        registryBuilder.add(Registries.LEVEL_STEM, Lifecycle.stable(), GCLevelStems::bootstrapRegistries);
        registryBuilder.add(Registries.NOISE_SETTINGS, Lifecycle.stable(), GCNoiseGeneratorSettings::bootstrapRegistries);
        registryBuilder.add(Registries.STRUCTURE, Lifecycle.stable(), GCStructures::bootstrapRegistries);
        registryBuilder.add(Registries.STRUCTURE_SET, Lifecycle.stable(), GCStructureSets::bootstrapRegistries);
        registryBuilder.add(Registries.TEMPLATE_POOL, Lifecycle.stable(), GCStructureTemplatePools::bootstrapRegistries);
        registryBuilder.add(Registries.CONFIGURED_CARVER, Lifecycle.stable(), GCConfiguredCarvers::bootstrapRegistries);
        registryBuilder.add(Registries.CONFIGURED_FEATURE, Lifecycle.stable(), GCConfiguredFeature::bootstrapRegistries);
        registryBuilder.add(Registries.CONFIGURED_FEATURE, Lifecycle.stable(), GCOreConfiguredFeature::bootstrapRegistries);
        registryBuilder.add(Registries.PLACED_FEATURE, Lifecycle.stable(), GCOrePlacedFeatures::bootstrapRegistries);
        registryBuilder.add(Registries.PLACED_FEATURE, Lifecycle.stable(), GCPlacedFeatures::bootstrapRegistries);
        registryBuilder.add(Registries.DAMAGE_TYPE, Lifecycle.stable(), GCDamageTypes::bootstrapRegistries);
    }
}
