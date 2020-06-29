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

package com.hrznstudio.galacticraft.screen;

import com.hrznstudio.galacticraft.Constants;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftScreenHandlerTypes {

    public static final Identifier BASIC_SOLAR_PANEL_HANDLER_ID = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.BASIC_SOLAR_SCREEN_HANDLER);
    public static final ScreenHandlerType<BasicSolarPanelScreenHandler> BASIC_SOLAR_PANEL_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                BASIC_SOLAR_PANEL_HANDLER_ID,
                BasicSolarPanelScreenHandler::new
            );

    public static final Identifier CIRCUIT_FABRICATOR_HANDLER_ID = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.CIRCUIT_FABRICATOR_SCREEN_HANDLER);
    public static final ScreenHandlerType<CircuitFabricatorScreenHandler> CIRCUIT_FABRICATOR_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    CIRCUIT_FABRICATOR_HANDLER_ID,
                    CircuitFabricatorScreenHandler::new
            );

    public static final Identifier COAL_GENERATOR_HANDLER_ID = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.COAL_GENERATOR_SCREEN_HANDLER);
    public static final ScreenHandlerType<CoalGeneratorScreenHandler> COAL_GENERATOR_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    COAL_GENERATOR_HANDLER_ID,
                    CoalGeneratorScreenHandler::new
            );

    public static final Identifier COMPRESSOR_HANDLER_ID = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.COMPRESSOR_SCREEN_HANDLER);
    public static final ScreenHandlerType<CompressorScreenHandler> COMPRESSOR_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    COMPRESSOR_HANDLER_ID,
                    CompressorScreenHandler::new
            );

    public static final Identifier ELECTRIC_COMPRESSOR_HANDLER_ID = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.ELECTRIC_COMPRESSOR_SCREEN_HANDLER);
    public static final ScreenHandlerType<ElectricCompressorScreenHandler> ELECTRIC_COMPRESSOR_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    ELECTRIC_COMPRESSOR_HANDLER_ID,
                    ElectricCompressorScreenHandler::new
            );

    public static final Identifier ENERGY_STORAGE_MODULE_HANDLER_ID = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.ENERGY_STORAGE_MODULE_SCREEN_HANDLER);
    public static final ScreenHandlerType<EnergyStorageModuleScreenHandler> ENERGY_STORAGE_MODULE_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    ENERGY_STORAGE_MODULE_HANDLER_ID,
                    EnergyStorageModuleScreenHandler::new
            );

    public static final Identifier OXYGEN_COLLECTOR_HANDLER_ID = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.OXYGEN_COLLECTOR_SCREEN_HANDLER);
    public static final ScreenHandlerType<OxygenCollectorScreenHandler> OXYGEN_COLLECTOR_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    OXYGEN_COLLECTOR_HANDLER_ID,
                    OxygenCollectorScreenHandler::new
            );

    public static final Identifier PLAYER_INVENTORY_HANDLER_ID = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.PLAYER_INVENTORY_SCREEN_HANDLER);
    public static final ScreenHandlerType<PlayerInventoryGCScreenHandler> PLAYER_INV_GC_HANDLER =
            ScreenHandlerRegistry.registerSimple(
                    PLAYER_INVENTORY_HANDLER_ID,
                    (syncId, inv) -> new PlayerInventoryGCScreenHandler(inv, inv.player)
            );

    public static final Identifier REFINERY_HANDLER_ID = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.REFINERY_SCREEN_HANDLER);
    public static final ScreenHandlerType<RefineryScreenHandler> REFINERY_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    REFINERY_HANDLER_ID,
                    RefineryScreenHandler::new
            );

    public static final Identifier ROCKET_DESIGNER_HANDLER_ID = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.ROCKET_DESIGNER_SCREEN_HANDLER);
    public static final ScreenHandlerType<RocketDesignerScreenHandler> ROCKET_DESIGNER_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    ROCKET_DESIGNER_HANDLER_ID,
                    RocketDesignerScreenHandler::new
            );

    public static final Identifier ROCKET_ASSEMBLER_HANDLER_ID = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.ROCKET_ASSEMBLER_SCREEN_HANDLER);
    public static final ScreenHandlerType<RocketAssemblerScreenHandler> ROCKET_ASSEMBLER_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    ROCKET_ASSEMBLER_HANDLER_ID,
                    RocketAssemblerScreenHandler::new
            );

    public static final Identifier FUEL_LOADER_HANDLER_ID = new Identifier(Constants.MOD_ID, Constants.ScreenHandler.FUEL_LOADER_SCREEN_HANDLER);
    public static final ScreenHandlerType<FuelLoaderScreenHandler> FUEL_LOADER_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    FUEL_LOADER_HANDLER_ID,
                    FuelLoaderScreenHandler::new
            );

    public static void register() {}
}
