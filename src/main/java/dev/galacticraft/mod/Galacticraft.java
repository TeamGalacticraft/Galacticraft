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

package dev.galacticraft.mod;

import dev.galacticraft.mod.api.config.Config;
import dev.galacticraft.mod.command.GCCommands;
import dev.galacticraft.mod.config.ConfigImpl;
import dev.galacticraft.mod.content.*;
import dev.galacticraft.mod.content.advancements.GCTriggers;
import dev.galacticraft.mod.content.entity.data.GCEntityDataSerializers;
import dev.galacticraft.mod.content.item.GCCreativeModeTabs;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.events.GCEventHandlers;
import dev.galacticraft.mod.lookup.GCApiLookupProviders;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.misc.banner.GCBannerPatterns;
import dev.galacticraft.mod.network.GCServerPacketReceivers;
import dev.galacticraft.mod.particle.GCParticleTypes;
import dev.galacticraft.mod.recipe.GCRecipes;
import dev.galacticraft.mod.screen.GCMenuTypes;
import dev.galacticraft.mod.structure.GCStructurePieceTypes;
import dev.galacticraft.mod.tag.GCTags;
import dev.galacticraft.mod.village.GCVillagerProfessions;
import dev.galacticraft.mod.village.MoonVillagerTypes;
import dev.galacticraft.mod.world.biome.source.GCMultiNoiseBiomeSourceParameterLists;
import dev.galacticraft.mod.world.dimension.GCGases;
import dev.galacticraft.mod.world.gen.carver.GCCarvers;
import dev.galacticraft.mod.world.gen.feature.GCOrePlacedFeatures;
import dev.galacticraft.mod.world.gen.feature.GCPlacedFeatures;
import dev.galacticraft.mod.world.gen.structure.GCStructureTypes;
import dev.galacticraft.mod.world.gen.surfacebuilder.MoonSurfaceRules;
import dev.galacticraft.mod.world.poi.GCPointOfInterestTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Galacticraft implements ModInitializer {
    public static final Config CONFIG = new ConfigImpl(FabricLoader.getInstance().getConfigDir().resolve("galacticraft.json").toFile());

    @Override
    public void onInitialize() {
        long startInitTime = System.currentTimeMillis();
        Constant.LOGGER.info("Starting initialization.");
        GCTags.register();
        GCFluids.register();
        GCBlocks.register();
        GCFluids.registerFluidVariantAttributes(); // Must be called after GCBlocks.register() so that grates can work
        GCBlockEntityTypes.register();
        GCItems.register();
        GCTriggers.register();
        GCCreativeModeTabs.register();
        GCApiLookupProviders.register();
        GCRecipes.register();
        GCEntityDataSerializers.register();
        GCEntityTypes.register();
        GCLootTables.register();
        GCGases.register();
        GCOrePlacedFeatures.register();
        GCPlacedFeatures.register();
        GCStructurePieceTypes.register();
        GCStructureTypes.register();
        GCCarvers.register();
        MoonSurfaceRules.register();
        GCMultiNoiseBiomeSourceParameterLists.register();
        GCMenuTypes.register();
        GCParticleTypes.register();
        GCCommands.register();
        GCLightSources.register();
        GCServerPacketReceivers.register();
        GCSounds.register();
        GCPointOfInterestTypes.register();
        MoonVillagerTypes.register();
        GCVillagerProfessions.register();
        GCMachineStatuses.register();
        GCBannerPatterns.register();
        GCTeleporterTypes.register();
        GCStats.register();
        GCCelestialHandlers.register();
        GCEventHandlers.init();
        Constant.LOGGER.info("Initialization complete. (Took {}ms).", System.currentTimeMillis() - startInitTime);
    }
}
