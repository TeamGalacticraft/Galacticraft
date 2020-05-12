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

package com.hrznstudio.galacticraft.screen;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.client.gui.screen.ingame.RocketDesignerScreen;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftScreenHandlers {
    public static final Identifier PLAYER_INVENTORY_SCREEN_HANDLER = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.PLAYER_INVENTORY_SCREEN_HANDLER);

    public static final Identifier COAL_GENERATOR_SCREEN_HANDLER = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.COAL_GENERATOR_SCREEN_HANDLER);
    public static final Identifier BASIC_SOLAR_PANEL_SCREEN_HANDLER = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.BASIC_SOLAR_SCREEN_HANDLER);
    public static final Identifier CIRCUIT_FABRICATOR_SCREEN_HANDLER = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.CIRCUIT_FABRICATOR_SCREEN_HANDLER);
    public static final Identifier COMPRESSOR_SCREEN_HANDLER = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.COMPRESSOR_SCREEN_HANDLER);
    public static final Identifier ELECTRIC_COMPRESSOR_SCREEN_HANDLER = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.ELECTRIC_COMPRESSOR_SCREEN_HANDLER);
    public static final Identifier ENERGY_STORAGE_MODULE_SCREEN_HANDLER = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.ENERGY_STORAGE_MODULE_SCREEN_HANDLER);
    public static final Identifier REFINERY_SCREEN_HANDLER = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.REFINERY_SCREEN_HANDLER);
    public static final Identifier OXYGEN_COLLECTOR_SCREEN_HANDLER = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.OXYGEN_COLLECTOR_SCREEN_HANDLER);
    public static final Identifier ROCKET_ASSEMBLER_SCREEN_HANDLER = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.ROCKET_ASSEMBLER_SCREEN_HANDLER);
    public static final Identifier ROCKET_DESIGNER_SCREEN_HANDLER = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.ROCKET_DESIGNER_SCREEN_HANDLER);

    public static void register() {
        ContainerProviderRegistry.INSTANCE.registerFactory(PLAYER_INVENTORY_SCREEN_HANDLER, (syncId, id, player, buf) -> new PlayerInventoryGCScreenHandler(player.inventory, player));

        ContainerProviderRegistry.INSTANCE.registerFactory(COAL_GENERATOR_SCREEN_HANDLER, CoalGeneratorScreenHandler.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(CIRCUIT_FABRICATOR_SCREEN_HANDLER, CircuitFabricatorScreenHandler.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(REFINERY_SCREEN_HANDLER, RefineryScreenHandler.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(BASIC_SOLAR_PANEL_SCREEN_HANDLER, BasicSolarPanelScreenHandler.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(COMPRESSOR_SCREEN_HANDLER, CompressorScreenHandler.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(ELECTRIC_COMPRESSOR_SCREEN_HANDLER, ElectricCompressorScreenHandler.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(ENERGY_STORAGE_MODULE_SCREEN_HANDLER, EnergyStorageModuleScreenHandler.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(OXYGEN_COLLECTOR_SCREEN_HANDLER, OxygenCollectorScreenHandler.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(ROCKET_ASSEMBLER_SCREEN_HANDLER, RocketAssemblerScreenHandler.FACTORY);
        ContainerProviderRegistry.INSTANCE.registerFactory(ROCKET_DESIGNER_SCREEN_HANDLER, RocketDesignerScreenHandler.FACTORY);
    }
}
