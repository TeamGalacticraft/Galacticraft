/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.mod.api.config.ConfigManager;
import dev.galacticraft.mod.api.rocket.part.GalacticraftRocketParts;
import dev.galacticraft.mod.block.GCBlocks;
import dev.galacticraft.mod.block.entity.GCBlockEntityTypes;
import dev.galacticraft.mod.command.GCCommand;
import dev.galacticraft.mod.config.ConfigManagerImpl;
import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.entity.data.GCTrackedDataHandler;
import dev.galacticraft.mod.events.GCEventHandler;
import dev.galacticraft.mod.fluid.GCFluid;
import dev.galacticraft.mod.item.GCItem;
import dev.galacticraft.mod.lookup.GCApiLookupProviders;
import dev.galacticraft.mod.loot.GCLootTable;
import dev.galacticraft.mod.machine.GCMachineStatus;
import dev.galacticraft.mod.misc.banner.GCBannerPattern;
import dev.galacticraft.mod.network.GCServerPacketReceiver;
import dev.galacticraft.mod.particle.GCParticleType;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import dev.galacticraft.mod.screen.GCScreenHandlerType;
import dev.galacticraft.mod.solarpanel.GCLightSource;
import dev.galacticraft.mod.sound.GCSounds;
import dev.galacticraft.mod.structure.GCStructurePieceType;
import dev.galacticraft.mod.structure.GalacticraftStructureSet;
import dev.galacticraft.mod.tag.GCTags;
import dev.galacticraft.mod.village.GCVillagerProfession;
import dev.galacticraft.mod.village.MoonVillagerType;
import dev.galacticraft.mod.world.biome.GCBiome;
import dev.galacticraft.mod.world.biome.source.GCBiomeParameters;
import dev.galacticraft.mod.world.dimension.GCGas;
import dev.galacticraft.mod.world.gen.carver.GCCarver;
import dev.galacticraft.mod.world.gen.feature.GCConfiguredFeature;
import dev.galacticraft.mod.world.gen.feature.GCOreConfiguredFeature;
import dev.galacticraft.mod.world.gen.feature.GCOrePlacedFeature;
import dev.galacticraft.mod.world.gen.feature.GCPlacedFeature;
import dev.galacticraft.mod.world.gen.structure.GCStructure;
import dev.galacticraft.mod.world.gen.structure.GCStructureType;
import dev.galacticraft.mod.world.gen.surfacebuilder.MoonSurfaceRules;
import dev.galacticraft.mod.world.poi.GCPointOfInterestType;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class Galacticraft implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Galacticraft");

    public static final ConfigManager CONFIG_MANAGER = new ConfigManagerImpl();

    @Override
    public void onInitialize() {
        long startInitTime = System.currentTimeMillis();
        LOGGER.info("Starting initialization.");
        GCTags.register();
        GCBlocks.register();
        GCFluid.register();
        GCBlockEntityTypes.register();
        GCItem.register();
        GCApiLookupProviders.register();
        GalacticraftRecipe.register();
        GCTrackedDataHandler.register();
        GalacticraftEntityType.register();
        GCLootTable.register();
        GCGas.register();
        GCOreConfiguredFeature.register();
        GCOrePlacedFeature.register();
        GCConfiguredFeature.register();
        GCPlacedFeature.register();
        GCBiomeParameters.register();
        GCStructurePieceType.register();
        GCStructureType.register();
        GCStructure.register();
        GalacticraftStructureSet.register();
        GCStructure.register();
        GCCarver.register();
        GCBiome.register();
        MoonSurfaceRules.register();
        GCScreenHandlerType.register();
        GCParticleType.register();
        GCCommand.register();
        GCLightSource.register();
        GCServerPacketReceiver.register();
        GCSounds.register();
        GCPointOfInterestType.register();
        MoonVillagerType.register();
        GalacticraftRocketParts.register();
        GCVillagerProfession.register();
        GCMachineStatus.register();
        GCBannerPattern.register();
        GCEventHandler.init();

        LOGGER.info("Initialization complete. (Took {}ms.)", System.currentTimeMillis() - startInitTime);
    }
}
