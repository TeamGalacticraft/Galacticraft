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
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.entity.GalacticraftBlockEntityType;
import dev.galacticraft.mod.command.GalacticraftCommand;
import dev.galacticraft.mod.config.ConfigManagerImpl;
import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.entity.data.GalacticraftTrackedDataHandler;
import dev.galacticraft.mod.fluid.GalacticraftFluid;
import dev.galacticraft.mod.item.GalacticraftItem;
import dev.galacticraft.mod.lookup.GalacticraftApiLookupProviders;
import dev.galacticraft.mod.loot.GalacticraftLootTable;
import dev.galacticraft.mod.machine.GalacticraftMachineStatus;
import dev.galacticraft.mod.misc.banner.GalacticraftBannerPattern;
import dev.galacticraft.mod.network.GalacticraftServerPacketReceiver;
import dev.galacticraft.mod.particle.GalacticraftParticleType;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.solarpanel.GalacticraftLightSource;
import dev.galacticraft.mod.sound.GalacticraftSound;
import dev.galacticraft.mod.structure.GalacticraftStructurePieceType;
import dev.galacticraft.mod.structure.GalacticraftStructureSet;
import dev.galacticraft.mod.tag.GalacticraftTag;
import dev.galacticraft.mod.village.GalacticraftVillagerProfession;
import dev.galacticraft.mod.village.MoonVillagerType;
import dev.galacticraft.mod.world.biome.GalacticraftBiome;
import dev.galacticraft.mod.world.biome.source.GalacticraftBiomeParameters;
import dev.galacticraft.mod.world.dimension.GalacticraftGas;
import dev.galacticraft.mod.world.gen.carver.GalacticraftCarver;
import dev.galacticraft.mod.world.gen.feature.GalacticraftConfiguredFeature;
import dev.galacticraft.mod.world.gen.feature.GalacticraftOreConfiguredFeature;
import dev.galacticraft.mod.world.gen.feature.GalacticraftOrePlacedFeature;
import dev.galacticraft.mod.world.gen.feature.GalacticraftPlacedFeature;
import dev.galacticraft.mod.world.gen.structure.GalacticraftStructure;
import dev.galacticraft.mod.world.gen.structure.GalacticraftStructureType;
import dev.galacticraft.mod.world.poi.GalacticraftPointOfInterestType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
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
        GalacticraftTag.register();
        GalacticraftFluid.register();
        GalacticraftBlock.register();
        GalacticraftBlockEntityType.register();
        GalacticraftItem.register();
        GalacticraftApiLookupProviders.register();
        GalacticraftRecipe.register();
        GalacticraftTrackedDataHandler.register();
        GalacticraftEntityType.register();
        GalacticraftLootTable.register();
        GalacticraftGas.register();
        GalacticraftOreConfiguredFeature.register();
        GalacticraftOrePlacedFeature.register();
        GalacticraftConfiguredFeature.register();
        GalacticraftPlacedFeature.register();
        GalacticraftBiomeParameters.register();
        GalacticraftStructurePieceType.register();
        GalacticraftStructureType.register();
        GalacticraftStructure.register();
        GalacticraftStructureSet.register();
        GalacticraftStructure.register();
        GalacticraftCarver.register();
        GalacticraftBiome.register();
        GalacticraftScreenHandlerType.register();
        GalacticraftParticleType.register();
        GalacticraftCommand.register();
        GalacticraftLightSource.register();
        GalacticraftServerPacketReceiver.register();
        GalacticraftSound.register();
        GalacticraftPointOfInterestType.register();
        MoonVillagerType.register();
        GalacticraftVillagerProfession.register();
        GalacticraftMachineStatus.register();

        if (FabricLoader.getInstance().isModLoaded("bannerpp")) {
            GalacticraftBannerPattern.register();
        }
        LOGGER.info("Initialization complete. (Took {}ms.)", System.currentTimeMillis() - startInitTime);
    }
}
