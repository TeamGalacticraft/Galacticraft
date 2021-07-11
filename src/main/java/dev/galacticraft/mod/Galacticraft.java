/*
 * Copyright (c) 2019-2021 Team Galacticraft
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
import dev.galacticraft.mod.fluid.GalacticraftFluid;
import dev.galacticraft.mod.item.GalacticraftItem;
import dev.galacticraft.mod.log.GalacticraftPrependingMessageFactory;
import dev.galacticraft.mod.loot.GalacticraftLootTable;
import dev.galacticraft.mod.misc.banner.GalacticraftBannerPattern;
import dev.galacticraft.mod.network.GalacticraftServerPacketReceiver;
import dev.galacticraft.mod.particle.GalacticraftParticle;
import dev.galacticraft.mod.recipe.GalacticraftRecipe;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.sound.GalacticraftSound;
import dev.galacticraft.mod.structure.GalacticraftStructure;
import dev.galacticraft.mod.tag.GalacticraftTag;
import dev.galacticraft.mod.village.GalacticraftVillagerProfession;
import dev.galacticraft.mod.village.MoonVillagerType;
import dev.galacticraft.mod.world.biome.GalacticraftBiome;
import dev.galacticraft.mod.world.biome.source.GalacticraftBiomeSource;
import dev.galacticraft.mod.world.dimension.GalacticraftDimension;
import dev.galacticraft.mod.world.dimension.GalacticraftGas;
import dev.galacticraft.mod.world.gen.carver.GalacticraftCarver;
import dev.galacticraft.mod.world.gen.feature.GalacticraftFeature;
import dev.galacticraft.mod.world.gen.surfacebuilder.GalacticraftSurfaceBuilder;
import dev.galacticraft.mod.world.poi.GalacticraftPointOfInterestType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class Galacticraft implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Galacticraft", new GalacticraftPrependingMessageFactory());

    public static final ConfigManager CONFIG_MANAGER = new ConfigManagerImpl();

    @Override
    public void onInitialize() {
        long startInitTime = System.currentTimeMillis();
        LOGGER.info("Starting initialization.");
        GalacticraftFluid.register();
        GalacticraftBlock.register();
        GalacticraftBlockEntityType.register();
        GalacticraftItem.register();
        GalacticraftTag.register();
        GalacticraftRecipe.register();
        GalacticraftEntityType.register();
        GalacticraftLootTable.register();
        GalacticraftGas.register();
        GalacticraftStructure.register();
        GalacticraftFeature.register();
        GalacticraftSurfaceBuilder.register();
        GalacticraftCarver.register();
        GalacticraftBiome.register();
        GalacticraftBiomeSource.register();
        GalacticraftDimension.register();
        GalacticraftScreenHandlerType.register();
        GalacticraftParticle.register();
        GalacticraftCommand.register();
        GalacticraftServerPacketReceiver.register();
        GalacticraftSound.register();
        GalacticraftPointOfInterestType.register();
        MoonVillagerType.register();
        GalacticraftVillagerProfession.register();

        if (FabricLoader.getInstance().isModLoaded("bannerpp")) {
            GalacticraftBannerPattern.register();
        }
        LOGGER.info("Initialization complete. (Took {}ms.)", System.currentTimeMillis() - startInitTime);
    }
}
