/*
 * Copyright (c) 2019-2021 HRZN LTD
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
import com.hrznstudio.galacticraft.api.regisry.AddonRegistry;
import dev.galacticraft.mod.block.GalacticraftBlocks;
import dev.galacticraft.mod.config.ConfigManagerImpl;
import dev.galacticraft.mod.entity.GalacticraftBlockEntities;
import dev.galacticraft.mod.entity.GalacticraftEntityTypes;
import dev.galacticraft.mod.fluids.GalacticraftFluids;
import dev.galacticraft.mod.items.GalacticraftItems;
import dev.galacticraft.mod.loot.GalacticraftLootTables;
import dev.galacticraft.mod.misc.banner.GalacticraftBannerPatterns;
import dev.galacticraft.mod.network.GalacticraftS2CPacketReceivers;
import dev.galacticraft.mod.particle.GalacticraftParticles;
import dev.galacticraft.mod.recipe.GalacticraftRecipes;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerTypes;
import dev.galacticraft.mod.server.command.GalacticraftCommands;
import dev.galacticraft.mod.sounds.GalacticraftSounds;
import dev.galacticraft.mod.structure.GalacticraftStructures;
import dev.galacticraft.mod.tag.GalacticraftTags;
import dev.galacticraft.mod.village.GalacticraftVillagerProfessions;
import dev.galacticraft.mod.village.MoonVillagerType;
import dev.galacticraft.mod.world.biome.GalacticraftBiomes;
import dev.galacticraft.mod.world.biome.source.GalacticraftBiomeSources;
import dev.galacticraft.mod.world.dimension.GalacticraftCelestialBodyTypes;
import dev.galacticraft.mod.world.dimension.GalacticraftDimensions;
import dev.galacticraft.mod.world.dimension.GalacticraftGases;
import dev.galacticraft.mod.world.gen.carver.GalacticraftCarvers;
import dev.galacticraft.mod.world.gen.feature.GalacticraftFeatures;
import dev.galacticraft.mod.world.gen.surfacebuilder.GalacticraftSurfaceBuilders;
import dev.galacticraft.mod.world.poi.GalacticraftPointOfInterestType;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.village.VillagerProfession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class Galacticraft implements ModInitializer {

    public static final Registry<VillagerProfession> MOON_VILLAGER_PROFESSION_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(Constants.MOD_ID, "moon_villager_profession")), Lifecycle.stable());

    public static final Logger logger = LogManager.getLogger("Galacticraft-Rewoven");

    public static final ConfigManager configManager = new ConfigManagerImpl();

    @Override
    public void onInitialize() {
        long startInitTime = System.currentTimeMillis();
        logger.info("[Galacticraft] Starting initialization.");
        GalacticraftFluids.register();
        GalacticraftBlocks.register();
        GalacticraftBlockEntities.init();
        GalacticraftItems.register();
        GalacticraftTags.register();
        GalacticraftRecipes.register();
        GalacticraftEntityTypes.register();
        GalacticraftLootTables.register();
        GalacticraftStructures.register();
        GalacticraftFeatures.register();
        GalacticraftSurfaceBuilders.register();
        GalacticraftCarvers.register();
        GalacticraftBiomes.register();
        GalacticraftBiomeSources.register();
        GalacticraftDimensions.register();
        GalacticraftScreenHandlerTypes.register();
        GalacticraftParticles.register();
        GalacticraftCommands.register();
        GalacticraftS2CPacketReceivers.register();
        GalacticraftSounds.register();
        GalacticraftBannerPatterns.register();
        GalacticraftPointOfInterestType.register();
        MoonVillagerType.register();
        GalacticraftVillagerProfessions.register();

//        AtmosphericGasRegistryCallback.EVENT.register(registry -> {
            Registry.register(AddonRegistry.ATMOSPHERIC_GASES, GalacticraftGases.HYDROGEN_DEUTERIUM_OXYGEN.getId(), GalacticraftGases.HYDROGEN_DEUTERIUM_OXYGEN);
            Registry.register(AddonRegistry.ATMOSPHERIC_GASES, GalacticraftGases.NITROGEN_OXIDE.getId(), GalacticraftGases.NITROGEN_OXIDE);
//        });

//        CelestialBodyRegistryCallback.EVENT.register(registry -> {
            Registry.register(AddonRegistry.CELESTIAL_BODIES, GalacticraftCelestialBodyTypes.THE_MOON.getId(), GalacticraftCelestialBodyTypes.THE_MOON);
//        });
        logger.info("[Galacticraft] Initialization complete. (Took {}ms.)", System.currentTimeMillis() - startInitTime);
    }
}
