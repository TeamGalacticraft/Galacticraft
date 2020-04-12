/*
 * Copyright (c) 2019 HRZN LTD
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

import com.hrznstudio.galacticraft.api.config.ConfigManager;
import com.hrznstudio.galacticraft.api.item.EnergyHolderItem;
import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.config.ConfigManagerImpl;
import com.hrznstudio.galacticraft.container.GalacticraftContainers;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.network.packet.GalacticraftPackets;
import com.hrznstudio.galacticraft.particle.GalacticraftParticles;
import com.hrznstudio.galacticraft.recipes.GalacticraftRecipes;
import com.hrznstudio.galacticraft.sounds.GalacticraftSounds;
import com.hrznstudio.galacticraft.tag.GalacticraftFluidTags;
import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import com.hrznstudio.galacticraft.world.biome.source.GalacticraftBiomeSourceTypes;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftDimensions;
import com.hrznstudio.galacticraft.world.gen.WorldGenerator;
import com.hrznstudio.galacticraft.world.gen.chunk.GalacticraftChunkGeneratorTypes;
import com.hrznstudio.galacticraft.world.gen.decorator.GalacticraftDecorators;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.GalacticraftSurfaceBuilders;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.reborn.energy.*;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class Galacticraft implements ModInitializer {

    public static final Logger logger = LogManager.getLogger("Galacticraft-Rewoven");

    public static ConfigManager configManager = new ConfigManagerImpl();

    @Override
    public void onInitialize() {
        long startInitTime = System.currentTimeMillis();
        logger.info("[Galacticraft] Starting initialization.");
        GalacticraftFluids.register();
        GalacticraftBlocks.register();
        GalacticraftItems.register();
        GalacticraftParticles.register();
        GalacticraftRecipes.register();
        GalacticraftSounds.register();
        GalacticraftEnergy.register();
        GalacticraftEntityTypes.register();
        GalacticraftContainers.register();
        GalacticraftCommands.register();
        GalacticraftBlockEntities.init();
        GalacticraftChunkGeneratorTypes.init();
        GalacticraftFeatures.init();
        GalacticraftDecorators.init();
        GalacticraftBiomes.init();
        GalacticraftBiomeSourceTypes.init();
        GalacticraftDimensions.init();
        GalacticraftSurfaceBuilders.init();
        WorldGenerator.register();
        GalacticraftPackets.register();
        GalacticraftFluidTags.register();

        Energy.registerHolder(object -> { //we load before TR/RC so it's ok for now... Unless there's a mod that patches this with their own stuff that loads before us. TODO: make this a more 'safe' implementation
            if(object instanceof ItemStack){
                return !((ItemStack) object).isEmpty() && ((ItemStack) object).getItem() instanceof EnergyHolder;
            }
            return false;
        }, object -> {
            final ItemStack stack = (ItemStack) object;
            final EnergyHolder energyHolder = (EnergyHolder) stack.getItem();
            return new EnergyStorage() {
                @Override
                public double getStored(EnergySide face) {
                    validateNBT();
                    return stack.getOrCreateTag().getDouble("energy");
                }

                @Override
                public void setStored(double amount) {
                    validateNBT();
                    if (stack.getItem() instanceof EnergyHolderItem && stack.hasTag() && !stack.getTag().getBoolean("skipGC")) {
                        if (!((EnergyHolderItem) stack.getItem()).isInfinite()) {
                            if (amount == getMaxStoredPower()) { //5 off :/
                                stack.getTag().putInt("Energy", ((EnergyHolderItem) stack.getItem()).getMaxEnergy(stack));
                                stack.getTag().putInt("Damage", 0);
                            } else {
                                stack.getTag().putInt("Energy", Math.min(GalacticraftEnergy.convertFromTR(amount), ((EnergyHolderItem) stack.getItem()).getMaxEnergy(stack)));
                                stack.setDamage(stack.getMaxDamage() - Math.min(GalacticraftEnergy.convertFromTR(amount), ((EnergyHolderItem) stack.getItem()).getMaxEnergy(stack)));
                            }
                        } else {
                            return;
                        }
                    }
                    stack.getTag().putDouble("energy", amount);
                }

                @Override
                public double getMaxStoredPower() {
                    return energyHolder.getMaxStoredPower();
                }

                @Override
                public EnergyTier getTier() {
                    return energyHolder.getTier();
                }

                private void validateNBT() {
                    if (!stack.hasTag()) {
                        stack.getOrCreateTag().putDouble("energy", 0);
                    }
                }
            };
        });

        logger.info("[Galacticraft] Initialization complete. (Took {}ms.)", System.currentTimeMillis()-startInitTime);
    }
}
