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

package dev.galacticraft.mod.data;

import com.mojang.serialization.Lifecycle;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.impl.universe.galaxy.GalaxyImpl;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCCelestialBodies;
import dev.galacticraft.mod.content.GCRocketParts;
import dev.galacticraft.mod.content.GCTeleporterTypes;
import dev.galacticraft.mod.content.entity.damage.GCDamageTypes;
import dev.galacticraft.mod.data.content.BootstrapDataProvider;
import dev.galacticraft.mod.data.model.GCModelProvider;
import dev.galacticraft.mod.data.recipes.*;
import dev.galacticraft.mod.data.tag.*;
import dev.galacticraft.mod.structure.GCStructureSets;
import dev.galacticraft.mod.structure.GCStructureTemplatePools;
import dev.galacticraft.mod.world.biome.GCBiomes;
import dev.galacticraft.mod.world.biome.source.GCMultiNoiseBiomeSourceParameterLists;
import dev.galacticraft.mod.world.dimension.GCDimensionTypes;
import dev.galacticraft.mod.world.dimension.GCLevelStems;
import dev.galacticraft.mod.world.gen.GCDensityFunctions;
import dev.galacticraft.mod.world.gen.GCNoiseData;
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

        pack.addProvider(GCTranslationProvider::new);
        pack.addProvider(GCBlockLootTableProvider::new);
        pack.addProvider(GCLootTableProvider::create);

        // recipes
        pack.addProvider(GCDecorationRecipeProvider::new);
        pack.addProvider(GCGearRecipeProvider::new);
        pack.addProvider(GCMachineRecipes::new);
        pack.addProvider(GCMiscRecipeProvider::new);
        pack.addProvider(GCOreRecipeProvider::new);
        pack.addProvider(GCRocketRecipes::new);

        // tags
        pack.addProvider(GCBannerTagProvider::new);
        pack.addProvider(GCBiomeTagProvider::new);
        pack.addProvider(GCBlockTagProvider::new);
        pack.addProvider(GCDimensionTagProvider::new);
        pack.addProvider(GCEntityTypeTagProvider::new);
        pack.addProvider(GCFluidTagProvider::new);
        pack.addProvider(GCItemTagProvider::new);
        pack.addProvider(GCStructureTagProvider::new);

        // world generation
//        pack.addProvider(BootstrapDataProvider.create("Noise", GCNoiseData::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Density Functions", GCDensityFunctions::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Biomes", GCBiomes::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Dimension Types", GCDimensionTypes::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Level Stems", GCLevelStems::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Noise Generator Settings", GCNoiseGeneratorSettings::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Configured Carvers", GCConfiguredCarvers::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Configured Features", GCConfiguredFeature::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Ore Configured Features", GCOreConfiguredFeature::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Ore Placed Features", GCOrePlacedFeatures::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Placed Features", GCPlacedFeatures::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Multi Noise Biome Source Parameter Lists", GCMultiNoiseBiomeSourceParameterLists::bootstrapRegistries));

        // structures
        pack.addProvider(BootstrapDataProvider.create("Structures", GCStructures::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Structure Sets", GCStructureSets::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Structure Template Pools", GCStructureTemplatePools::bootstrapRegistries));

        // universe
        pack.addProvider(BootstrapDataProvider.create("Galaxies", GalaxyImpl::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Celestial Bodies", GCCelestialBodies::bootstrapRegistries));
        pack.addProvider(BootstrapDataProvider.create("Celestial Teleporters", GCTeleporterTypes::bootstrapRegistries));

        // misc
        pack.addProvider(BootstrapDataProvider.create("Damage Types", GCDamageTypes::bootstrapRegistries));

        // rocket parts
        pack.addProvider(BootstrapDataProvider.create("Rocket Cones", GCRocketParts::bootstrapCone));
        pack.addProvider(BootstrapDataProvider.create("Rocket Bodies", GCRocketParts::bootstrapBody));
        pack.addProvider(BootstrapDataProvider.create("Rocket Fins", GCRocketParts::bootstrapFin));
        pack.addProvider(BootstrapDataProvider.create("Rocket Boosters", GCRocketParts::bootstrapBooster));
        pack.addProvider(BootstrapDataProvider.create("Rocket Engines", GCRocketParts::bootstrapEngine));
        pack.addProvider(BootstrapDataProvider.create("Rocket Upgrades", GCRocketParts::bootstrapUpgrade));

        // models
        pack.addProvider(GCModelProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder builder) {

        // world generation
        builder.add(Registries.BIOME, Lifecycle.stable(), GCBiomes::bootstrapRegistries);
        builder.add(Registries.DIMENSION_TYPE, Lifecycle.stable(), GCDimensionTypes::bootstrapRegistries);
        builder.add(Registries.LEVEL_STEM, Lifecycle.stable(), GCLevelStems::bootstrapRegistries);
        builder.add(Registries.NOISE, Lifecycle.stable(), GCNoiseData::bootstrapRegistries);
        builder.add(Registries.DENSITY_FUNCTION, Lifecycle.stable(), GCDensityFunctions::bootstrapRegistries);
        builder.add(Registries.NOISE_SETTINGS, Lifecycle.stable(), GCNoiseGeneratorSettings::bootstrapRegistries);
        builder.add(Registries.CONFIGURED_CARVER, Lifecycle.stable(), GCConfiguredCarvers::bootstrapRegistries);
        builder.add(Registries.CONFIGURED_FEATURE, Lifecycle.stable(), GCConfiguredFeature::bootstrapRegistries);
        builder.add(Registries.CONFIGURED_FEATURE, Lifecycle.stable(), GCOreConfiguredFeature::bootstrapRegistries);
        builder.add(Registries.PLACED_FEATURE, Lifecycle.stable(), GCOrePlacedFeatures::bootstrapRegistries);
        builder.add(Registries.PLACED_FEATURE, Lifecycle.stable(), GCPlacedFeatures::bootstrapRegistries);
        builder.add(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, Lifecycle.stable(), GCMultiNoiseBiomeSourceParameterLists::bootstrapRegistries);

        // structures
        builder.add(Registries.STRUCTURE, Lifecycle.stable(), GCStructures::bootstrapRegistries);
        builder.add(Registries.STRUCTURE_SET, Lifecycle.stable(), GCStructureSets::bootstrapRegistries);
        builder.add(Registries.TEMPLATE_POOL, Lifecycle.stable(), GCStructureTemplatePools::bootstrapRegistries);

        // universe
        builder.add(AddonRegistries.GALAXY, GalaxyImpl::bootstrapRegistries);
        builder.add(AddonRegistries.CELESTIAL_BODY, Lifecycle.stable(), GCCelestialBodies::bootstrapRegistries);
        builder.add(AddonRegistries.CELESTIAL_TELEPORTER, Lifecycle.stable(), GCTeleporterTypes::bootstrapRegistries);

        // misc
        builder.add(Registries.DAMAGE_TYPE, Lifecycle.stable(), GCDamageTypes::bootstrapRegistries);

        // rocket parts
        builder.add(RocketRegistries.ROCKET_CONE, GCRocketParts::bootstrapCone);
        builder.add(RocketRegistries.ROCKET_BODY, GCRocketParts::bootstrapBody);
        builder.add(RocketRegistries.ROCKET_FIN, GCRocketParts::bootstrapFin);
        builder.add(RocketRegistries.ROCKET_BOOSTER, GCRocketParts::bootstrapBooster);
        builder.add(RocketRegistries.ROCKET_ENGINE, GCRocketParts::bootstrapEngine);
        builder.add(RocketRegistries.ROCKET_UPGRADE, GCRocketParts::bootstrapUpgrade);
    }

    @Override
    public String getEffectiveModId() {
        return Constant.MOD_ID;
    }
}
