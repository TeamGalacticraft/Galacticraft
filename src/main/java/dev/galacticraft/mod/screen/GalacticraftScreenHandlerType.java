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

package dev.galacticraft.mod.screen;

import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftScreenHandlerType {

    public static final Identifier BASIC_SOLAR_PANEL_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.BASIC_SOLAR_SCREEN_HANDLER);
    public static final ScreenHandlerType<BasicSolarPanelScreenHandler> BASIC_SOLAR_PANEL_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    BASIC_SOLAR_PANEL_HANDLER_ID,
                    BasicSolarPanelScreenHandler::new
            );

    public static final Identifier ADVANCED_SOLAR_PANEL_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.ADVANCED_SOLAR_SCREEN_HANDLER);
    public static final ScreenHandlerType<AdvancedSolarPanelScreenHandler> ADVANCED_SOLAR_PANEL_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    ADVANCED_SOLAR_PANEL_HANDLER_ID,
                    AdvancedSolarPanelScreenHandler::new
            );

    public static final Identifier CIRCUIT_FABRICATOR_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.CIRCUIT_FABRICATOR_SCREEN_HANDLER);
    public static final ScreenHandlerType<CircuitFabricatorScreenHandler> CIRCUIT_FABRICATOR_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    CIRCUIT_FABRICATOR_HANDLER_ID,
                    CircuitFabricatorScreenHandler::new
            );

    public static final Identifier COAL_GENERATOR_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.COAL_GENERATOR_SCREEN_HANDLER);
    public static final ScreenHandlerType<CoalGeneratorScreenHandler> COAL_GENERATOR_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    COAL_GENERATOR_HANDLER_ID,
                    CoalGeneratorScreenHandler::new
            );

    public static final Identifier COMPRESSOR_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.COMPRESSOR_SCREEN_HANDLER);
    public static final ScreenHandlerType<CompressorScreenHandler> COMPRESSOR_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    COMPRESSOR_HANDLER_ID,
                    CompressorScreenHandler::new
            );

    public static final Identifier ELECTRIC_COMPRESSOR_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.ELECTRIC_COMPRESSOR_SCREEN_HANDLER);
    public static final ScreenHandlerType<ElectricCompressorScreenHandler> ELECTRIC_COMPRESSOR_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    ELECTRIC_COMPRESSOR_HANDLER_ID,
                    ElectricCompressorScreenHandler::new
            );

    public static final Identifier ENERGY_STORAGE_MODULE_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.ENERGY_STORAGE_MODULE_SCREEN_HANDLER);
    public static final ScreenHandlerType<EnergyStorageModuleScreenHandler> ENERGY_STORAGE_MODULE_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    ENERGY_STORAGE_MODULE_HANDLER_ID,
                    EnergyStorageModuleScreenHandler::new
            );

    public static final Identifier OXYGEN_COLLECTOR_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.OXYGEN_COLLECTOR_SCREEN_HANDLER);
    public static final ScreenHandlerType<OxygenCollectorScreenHandler> OXYGEN_COLLECTOR_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    OXYGEN_COLLECTOR_HANDLER_ID,
                    OxygenCollectorScreenHandler::new
            );

    public static final Identifier OXYGEN_COMPRESSOR_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.OXYGEN_COMPRESSOR_SCREEN_HANDLER);
    public static final ScreenHandlerType<OxygenCompressorScreenHandler> OXYGEN_COMPRESSOR_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    OXYGEN_COMPRESSOR_HANDLER_ID,
                    OxygenCompressorScreenHandler::new
            );

    public static final Identifier OXYGEN_SEALER_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.OXYGEN_SEALER_SCREEN_HANDLER);
    public static final ScreenHandlerType<OxygenSealerScreenHandler> OXYGEN_SEALER_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    OXYGEN_SEALER_HANDLER_ID,
                    OxygenSealerScreenHandler::new
            );

    public static final Identifier OXYGEN_DECOMPRESSOR_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.OXYGEN_DECOMPRESSOR_SCREEN_HANDLER);
    public static final ScreenHandlerType<OxygenDecompressorScreenHandler> OXYGEN_DECOMPRESSOR_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    OXYGEN_DECOMPRESSOR_HANDLER_ID,
                    OxygenDecompressorScreenHandler::new
            );

    public static final Identifier PLAYER_INVENTORY_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.PLAYER_INVENTORY_SCREEN_HANDLER);
    public static final ScreenHandlerType<GalacticraftPlayerInventoryScreenHandler> PLAYER_INV_GC_HANDLER =
            ScreenHandlerRegistry.registerSimple(
                    PLAYER_INVENTORY_HANDLER_ID,
                    GalacticraftPlayerInventoryScreenHandler::new
            );

    public static final Identifier REFINERY_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.REFINERY_SCREEN_HANDLER);
    public static final ScreenHandlerType<RefineryScreenHandler> REFINERY_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    REFINERY_HANDLER_ID,
                    RefineryScreenHandler::new
            );

    public static final Identifier ELECTRIC_FURNACE_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.ELECTRIC_FURNACE_SCREEN_HANDLER);
    public static final ScreenHandlerType<ElectricFurnaceScreenHandler> ELECTRIC_FURNACE_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    ELECTRIC_FURNACE_HANDLER_ID,
                    ElectricFurnaceScreenHandler::new
            );

    public static final Identifier ELECTRIC_ARC_FURNACE_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.ELECTRIC_ARC_FURNACE_SCREEN_HANDLER);
    public static final ScreenHandlerType<ElectricArcFurnaceScreenHandler> ELECTRIC_ARC_FURNACE_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    ELECTRIC_ARC_FURNACE_HANDLER_ID,
                    ElectricArcFurnaceScreenHandler::new
            );

    public static final Identifier BUBBLE_DISTRIBUTOR_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.BUBBLE_DISTRIBUTOR_SCREEN_HANDLER);
    public static final ScreenHandlerType<BubbleDistributorScreenHandler> BUBBLE_DISTRIBUTOR_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    BUBBLE_DISTRIBUTOR_HANDLER_ID,
                    BubbleDistributorScreenHandler::new
            );

    public static final Identifier OXYGEN_STORAGE_MODULE_HANDLER_ID = new Identifier(Constant.MOD_ID, Constant.ScreenHandler.OXYGEN_STORAGE_MODULE_SCREEN_HANDLER);
    public static final ScreenHandlerType<OxygenStorageModuleScreenHandler> OXYGEN_STORAGE_MODULE_HANDLER =
            ScreenHandlerRegistry.registerExtended(
                    OXYGEN_STORAGE_MODULE_HANDLER_ID,
                    OxygenStorageModuleScreenHandler::new
            );

    public static void register() {
    }
}
