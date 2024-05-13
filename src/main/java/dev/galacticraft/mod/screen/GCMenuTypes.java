/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.content.block.entity.machine.*;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class GCMenuTypes {
    public static final MenuType<CoalGeneratorMenu> COAL_GENERATOR = MachineMenu.createType(CoalGeneratorMenu::new);
    public static final MenuType<SolarPanelMenu<BasicSolarPanelBlockEntity>> BASIC_SOLAR_PANEL = MachineMenu.createType(SolarPanelMenu::new, () -> GCMachineTypes.BASIC_SOLAR_PANEL);
    public static final MenuType<SolarPanelMenu<AdvancedSolarPanelBlockEntity>> ADVANCED_SOLAR_PANEL = MachineMenu.createType(SolarPanelMenu::new, () -> GCMachineTypes.ADVANCED_SOLAR_PANEL);

    public static final MenuType<RecipeMachineMenu<Container, FabricationRecipe, CircuitFabricatorBlockEntity>> CIRCUIT_FABRICATOR = RecipeMachineMenu.createType(() -> GCMachineTypes.CIRCUIT_FABRICATOR, 94);
    public static final MenuType<CompressorMenu> COMPRESSOR = MachineMenu.createType(CompressorMenu::new);

    public static final MenuType<RecipeMachineMenu<CraftingContainer, CompressingRecipe, ElectricCompressorBlockEntity>> ELECTRIC_COMPRESSOR = RecipeMachineMenu.createType(() -> GCMachineTypes.ELECTRIC_COMPRESSOR);
    public static final MenuType<RecipeMachineMenu<Container, SmeltingRecipe, ElectricFurnaceBlockEntity>> ELECTRIC_FURNACE = RecipeMachineMenu.createType(() -> GCMachineTypes.ELECTRIC_FURNACE);
    public static final MenuType<RecipeMachineMenu<Container, BlastingRecipe, ElectricArcFurnaceBlockEntity>> ELECTRIC_ARC_FURNACE = RecipeMachineMenu.createType(() -> GCMachineTypes.ELECTRIC_ARC_FURNACE);

    public static final MenuType<MachineMenu<RefineryBlockEntity>> REFINERY = MachineMenu.createSimple(86, () -> GCMachineTypes.REFINERY);

    public static final MenuType<OxygenCollectorMenu> OXYGEN_COLLECTOR = MachineMenu.createType(OxygenCollectorMenu::new);

    public static final MenuType<MachineMenu<OxygenCompressorBlockEntity>> OXYGEN_COMPRESSOR = MachineMenu.createSimple(() -> GCMachineTypes.OXYGEN_COMPRESSOR);
    public static final MenuType<MachineMenu<OxygenDecompressorBlockEntity>> OXYGEN_DECOMPRESSOR = MachineMenu.createSimple(() -> GCMachineTypes.OXYGEN_DECOMPRESSOR);

    public static final MenuType<MachineMenu<OxygenSealerBlockEntity>> OXYGEN_SEALER = MachineMenu.createSimple(() -> GCMachineTypes.OXYGEN_SEALER);

    public static final MenuType<OxygenBubbleDistributorMenu> OXYGEN_BUBBLE_DISTRIBUTOR = MachineMenu.createType(OxygenBubbleDistributorMenu::new);

    public static final MenuType<MachineMenu<OxygenStorageModuleBlockEntity>> OXYGEN_STORAGE_MODULE = MachineMenu.createSimple(() -> GCMachineTypes.OXYGEN_STORAGE_MODULE);
    public static final MenuType<MachineMenu<EnergyStorageModuleBlockEntity>> ENERGY_STORAGE_MODULE = MachineMenu.createSimple(() -> GCMachineTypes.ENERGY_STORAGE_MODULE);

    public static final MenuType<GCPlayerInventoryMenu> PLAYER_INV_GC = new MenuType<>(GCPlayerInventoryMenu::new, FeatureFlags.VANILLA_SET);

    public static final ExtendedScreenHandlerType<FuelLoaderMenu> FUEL_LOADER = new ExtendedScreenHandlerType<>(FuelLoaderMenu::new);

    public static final MenuType<AirlockControllerMenu> AIRLOCK_CONTROLLER_MENU = new MenuType<>(AirlockControllerMenu::new, FeatureFlags.VANILLA_SET);
    public static final MenuType<RocketWorkbenchMenu> ROCKET_WORKBENCH = new ExtendedScreenHandlerType<>(RocketWorkbenchMenu::new);
    public static final ExtendedScreenHandlerType<RocketMenu> ROCKET = new ExtendedScreenHandlerType<>(RocketMenu::new);
    public static final MenuType<ParachestMenu> PARACHEST = new ExtendedScreenHandlerType<>(ParachestMenu::new);

    public static void register() {
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.PLAYER_INVENTORY_MENU), PLAYER_INV_GC);

        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.COAL_GENERATOR_MENU), COAL_GENERATOR);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.BASIC_SOLAR_PANEL_MENU), BASIC_SOLAR_PANEL);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.ADVANCED_SOLAR_PANEL_MENU), ADVANCED_SOLAR_PANEL);

        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.CIRCUIT_FABRICATOR_MENU), CIRCUIT_FABRICATOR);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.COMPRESSOR_MENU), COMPRESSOR);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.ELECTRIC_COMPRESSOR_MENU), ELECTRIC_COMPRESSOR);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.ELECTRIC_FURNACE_MENU), ELECTRIC_FURNACE);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.ELECTRIC_ARC_FURNACE_MENU), ELECTRIC_ARC_FURNACE);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.REFINERY_MENU), REFINERY);

        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.OXYGEN_COLLECTOR_MENU), OXYGEN_COLLECTOR);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.OXYGEN_COMPRESSOR_MENU), OXYGEN_COMPRESSOR);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.OXYGEN_DECOMPRESSOR_MENU), OXYGEN_DECOMPRESSOR);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.BUBBLE_DISTRIBUTOR_MENU), OXYGEN_BUBBLE_DISTRIBUTOR);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.OXYGEN_SEALER_MENU), OXYGEN_SEALER);

        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.ENERGY_STORAGE_MODULE_MENU), ENERGY_STORAGE_MODULE);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.OXYGEN_STORAGE_MODULE_MENU), OXYGEN_STORAGE_MODULE);

        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.FUEL_LOADER_MENU), FUEL_LOADER);

        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.AIR_LOCK_CONTROLLER_MENU), AIRLOCK_CONTROLLER_MENU);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.ROCKET_WORKBENCH_MENU), ROCKET_WORKBENCH);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.ROCKET), ROCKET);
        Registry.register(BuiltInRegistries.MENU, Constant.id(Constant.Menu.PARACHEST), PARACHEST);
    }
}
