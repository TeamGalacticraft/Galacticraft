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

package com.hrznstudio.galacticraft.container;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel.BasicSolarPanelContainer;
import com.hrznstudio.galacticraft.blocks.machines.circuitfabricator.CircuitFabricatorContainer;
import com.hrznstudio.galacticraft.blocks.machines.coalgenerator.CoalGeneratorContainer;
import com.hrznstudio.galacticraft.blocks.machines.compressor.CompressorContainer;
import com.hrznstudio.galacticraft.blocks.machines.electriccompressor.ElectricCompressorContainer;
import com.hrznstudio.galacticraft.blocks.machines.energystoragemodule.EnergyStorageModuleContainer;
import com.hrznstudio.galacticraft.blocks.machines.oxygencollector.OxygenCollectorContainer;
import com.hrznstudio.galacticraft.blocks.machines.refinery.RefineryContainer;
import com.hrznstudio.galacticraft.blocks.machines.rocketassembler.RocketAssemblerContainer;
import com.hrznstudio.galacticraft.blocks.machines.rocketdesigner.RocketDesignerContainer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftContainers {
    public static final Identifier PLAYER_INVENTORY_CONTAINER = new Identifier(Constants.MOD_ID, Constants.Container.PLAYER_INVENTORY_CONTAINER);

    public static final Identifier COAL_GENERATOR_CONTAINER = new Identifier(Constants.MOD_ID, Constants.Container.COAL_GENERATOR_CONTAINER);
    public static final Identifier BASIC_SOLAR_PANEL_CONTAINER = new Identifier(Constants.MOD_ID, Constants.Container.BASIC_SOLAR_PANEL_CONTAINER);
    public static final Identifier CIRCUIT_FABRICATOR_CONTAINER = new Identifier(Constants.MOD_ID, Constants.Container.CIRCUIT_FABRICATOR_CONTAINER);
    public static final Identifier COMPRESSOR_CONTAINER = new Identifier(Constants.MOD_ID, Constants.Container.COMPRESSOR_CONTAINER);
    public static final Identifier ELECTRIC_COMPRESSOR_CONTAINER = new Identifier(Constants.MOD_ID, Constants.Container.ELECTRIC_COMPRESSOR_CONTAINER);
    public static final Identifier ENERGY_STORAGE_MODULE_CONTAINER = new Identifier(Constants.MOD_ID, Constants.Container.ENERGY_STORAGE_MODULE_CONTAINER);
    public static final Identifier REFINERY_CONTAINER = new Identifier(Constants.MOD_ID, Constants.Container.REFINERY_CONTAINER);
    public static final Identifier OXYGEN_COLLECTOR_CONTAINER = new Identifier(Constants.MOD_ID, Constants.Container.OXYGEN_COLLECTOR_CONTAINER);
    public static final Identifier ROCKET_DESIGNER_CONTAINER = new Identifier(Constants.MOD_ID, Constants.Container.ROCKET_DESIGNER_CONTAINER);
    public static final Identifier ROCKET_ASSEMBLER_CONTAINER = new Identifier(Constants.MOD_ID, Constants.Container.ROCKET_ASSEMBLER_CONTAINER);

    public static void register() {
        ContainerProviderRegistry.INSTANCE.registerFactory(PLAYER_INVENTORY_CONTAINER, (syncId, id, player, buf) -> new PlayerInventoryGCContainer(player.inventory, player));

        ContainerProviderRegistry.INSTANCE.registerFactory(COAL_GENERATOR_CONTAINER, CoalGeneratorContainer.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(CIRCUIT_FABRICATOR_CONTAINER, CircuitFabricatorContainer.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(REFINERY_CONTAINER, RefineryContainer.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(BASIC_SOLAR_PANEL_CONTAINER, BasicSolarPanelContainer.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(COMPRESSOR_CONTAINER, CompressorContainer.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(ELECTRIC_COMPRESSOR_CONTAINER, ElectricCompressorContainer.ELECTRIC_FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(ENERGY_STORAGE_MODULE_CONTAINER, EnergyStorageModuleContainer.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(OXYGEN_COLLECTOR_CONTAINER, OxygenCollectorContainer.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(ROCKET_DESIGNER_CONTAINER, RocketDesignerContainer.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(ROCKET_ASSEMBLER_CONTAINER, RocketAssemblerContainer.FACTORY);
    }
}
