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
 *
 */

package com.hrznstudio.galacticraft;

import com.hrznstudio.galacticraft.accessor.GCPlayerAccessor;
import com.hrznstudio.galacticraft.api.biome.BiomePropertyType;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.api.config.ConfigManager;
import com.hrznstudio.galacticraft.api.regisry.AddonRegistry;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.config.ConfigManagerImpl;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.loot.GalacticraftLootTables;
import com.hrznstudio.galacticraft.misc.banner.GalacticraftBannerPatterns;
import com.hrznstudio.galacticraft.network.GalacticraftPackets;
import com.hrznstudio.galacticraft.particle.GalacticraftParticles;
import com.hrznstudio.galacticraft.recipe.GalacticraftRecipes;
import com.hrznstudio.galacticraft.screen.GalacticraftScreenHandlerTypes;
import com.hrznstudio.galacticraft.server.command.GalacticraftCommands;
import com.hrznstudio.galacticraft.sounds.GalacticraftSounds;
import com.hrznstudio.galacticraft.structure.GalacticraftStructures;
import com.hrznstudio.galacticraft.structure.moon_village.MoonVillageData;
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
import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.item.impl.EntitySyncedInventoryComponent;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.BlockEntity;
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
public class Galacticraft implements ModInitializer, EntityComponentInitializer, BlockComponentInitializer {

    public static final Registry<VillagerProfession> MOON_VILLAGER_PROFESSION_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(Constants.MOD_ID, "moon_villager_profession")), Lifecycle.stable());
    public static final Registry<BiomePropertyType<?>> BIOME_PROPERTY_TYPE_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(Constants.MOD_ID, "biome_property_type")), Lifecycle.stable());

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
        GalacticraftEnergy.register();
        GalacticraftPackets.register();
        GalacticraftSounds.register();
        GalacticraftBannerPatterns.register();
        GalacticraftPointOfInterestType.register();
        MoonVillageData.register();
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

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {
        entityComponentFactoryRegistry.registerForPlayers(UniversalComponents.INVENTORY_COMPONENT, player -> {
            EntitySyncedInventoryComponent inventory = new EntitySyncedInventoryComponent(12, player);
            ((GCPlayerAccessor) player).setGearInventory(inventory);
            return inventory;
        }, RespawnCopyStrategy.INVENTORY);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
        registry.registerFor(ConfigurableMachineBlockEntity.class, UniversalComponents.CAPACITOR_COMPONENT, ConfigurableMachineBlockEntity::getCapacitor);
        registry.registerFor(ConfigurableMachineBlockEntity.class, UniversalComponents.INVENTORY_COMPONENT, ConfigurableMachineBlockEntity::getInventory);
        registry.registerFor(ConfigurableMachineBlockEntity.class, UniversalComponents.TANK_COMPONENT, ConfigurableMachineBlockEntity::getFluidTank);
        registry.registerFor(ConfigurableMachineBlockEntity.class, UniversalComponents.TANK_COMPONENT, ConfigurableMachineBlockEntity::getOxygenTank);

        registry.registerFor(new Identifier(Constants.MOD_ID, Constants.Blocks.COAL_GENERATOR), UniversalComponents.CAPACITOR_COMPONENT, (state, world, pos, side) -> {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof ConfigurableMachineBlockEntity) {
                return ((ConfigurableMachineBlockEntity) entity).accessCapacitor(state, side);
            }
            return null;
        });

        registry.registerFor(new Identifier(Constants.MOD_ID, Constants.Blocks.COAL_GENERATOR), UniversalComponents.INVENTORY_COMPONENT, (state, world, pos, side) -> {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof ConfigurableMachineBlockEntity) {
                return ((ConfigurableMachineBlockEntity) entity).accessInventory(state, side);
            }
            return null;
        });

        registry.registerFor(new Identifier(Constants.MOD_ID, Constants.Blocks.COAL_GENERATOR), UniversalComponents.TANK_COMPONENT, (state, world, pos, side) -> {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof ConfigurableMachineBlockEntity) {
                return ((ConfigurableMachineBlockEntity) entity).accessFluidTank(state, side);
            }
            return null;
        });

        registry.registerFor(new Identifier(Constants.MOD_ID, Constants.Blocks.COAL_GENERATOR), UniversalComponents.TANK_COMPONENT, (state, world, pos, side) -> {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof ConfigurableMachineBlockEntity) {
                return ((ConfigurableMachineBlockEntity) entity).accessOxygenTank(state, side);
            }
            return null;
        });

    }
}
