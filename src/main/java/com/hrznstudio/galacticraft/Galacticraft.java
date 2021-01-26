/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft;

import com.hrznstudio.galacticraft.api.biome.BiomePropertyType;
import com.hrznstudio.galacticraft.api.config.ConfigManager;
import com.hrznstudio.galacticraft.api.registry.RocketPartRegistry;
import com.hrznstudio.galacticraft.api.rocket.part.RocketParts;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.component.GalacticraftComponents;
import com.hrznstudio.galacticraft.config.ConfigManagerImpl;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.loot.GalacticraftLootTables;
import com.hrznstudio.galacticraft.misc.banner.GalacticraftBannerPatterns;
import com.hrznstudio.galacticraft.network.GalacticraftS2CPackets;
import com.hrznstudio.galacticraft.particle.GalacticraftParticles;
import com.hrznstudio.galacticraft.recipe.GalacticraftRecipes;
import com.hrznstudio.galacticraft.screen.GalacticraftScreenHandlerTypes;
import com.hrznstudio.galacticraft.server.command.GalacticraftCommands;
import com.hrznstudio.galacticraft.sounds.GalacticraftSounds;
import com.hrznstudio.galacticraft.structure.GalacticraftStructures;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import com.hrznstudio.galacticraft.village.GalacticraftVillagerProfessions;
import com.hrznstudio.galacticraft.village.MoonVillagerType;
import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import com.hrznstudio.galacticraft.world.biome.source.GalacticraftBiomeSources;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftCelestialBodyTypes;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftDimensions;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftGases;
import com.hrznstudio.galacticraft.world.gen.carver.GalacticraftCarvers;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.GalacticraftSurfaceBuilders;
import com.hrznstudio.galacticraft.world.poi.GalacticraftPointOfInterestType;
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
    public static final Registry<BiomePropertyType<?>> BIOME_PROPERTY_TYPE_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(Constants.MOD_ID, "biome_property_type")), Lifecycle.stable());

    public static final Logger logger = LogManager.getLogger("Galacticraft-Rewoven");

    public static final ConfigManager configManager = new ConfigManagerImpl();

    public static final RocketPartRegistry ROCKET_PARTS = new RocketPartRegistry(RegistryKey.ofRegistry(new Identifier(Constants.MOD_ID, "rocket_parts")), Lifecycle.stable());

    @Override
    public void onInitialize() {
        long startInitTime = System.currentTimeMillis();
        logger.info("[Galacticraft] Starting initialization.");
        GalacticraftComponents.register();
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
        GalacticraftGases.register();
        GalacticraftCelestialBodyTypes.register();
        GalacticraftScreenHandlerTypes.register();
        GalacticraftParticles.register();
        GalacticraftCommands.register();
        GalacticraftEnergy.register();
        GalacticraftS2CPackets.register();
        GalacticraftSounds.register();
        GalacticraftBannerPatterns.register();
        GalacticraftPointOfInterestType.register();
        MoonVillagerType.register();
        GalacticraftVillagerProfessions.register();
        RocketParts.register();

        logger.info("[Galacticraft] Initialization complete. (Took {}ms.)", System.currentTimeMillis() - startInitTime);
    }
}
