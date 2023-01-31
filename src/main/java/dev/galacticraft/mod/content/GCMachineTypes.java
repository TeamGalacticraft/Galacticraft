/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.content;

import dev.galacticraft.machinelib.api.machine.MachineType;
import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.ResourceFilters;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.storage.slot.SlotGroup;
import dev.galacticraft.machinelib.api.storage.slot.display.ItemSlotDisplay;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.block.entity.*;
import dev.galacticraft.mod.machine.storage.io.GCSlotGroupTypes;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.screen.CoalGeneratorMenu;
import dev.galacticraft.mod.screen.CompressorMenu;
import dev.galacticraft.mod.screen.GCMenuTypes;
import dev.galacticraft.mod.screen.SolarPanelMenu;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GCMachineTypes {
    public static final MachineType<CoalGeneratorBlockEntity, CoalGeneratorMenu> COAL_GENERATOR = MachineType.create(
            GCBlocks.COAL_GENERATOR,
            GCBlockEntityTypes.COAL_GENERATOR,
            GCMenuTypes.COAL_GENERATOR,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().coalGeneratorEnergyProductionRate() * 2,
                    Galacticraft.CONFIG_MANAGER.get().coalGeneratorEnergyProductionRate() * 2,
                    false,
                    true
            ),
            MachineItemStorage.builder()
                    .addSingle(GCSlotGroupTypes.ENERGY_TO_ITEM, ItemResourceSlot.create(ItemSlotDisplay.create(8, 62), ResourceFilters.CAN_INSERT_ENERGY, ResourceFilters.CAN_EXTRACT_ENERGY_STRICT))
                    .addSingle(GCSlotGroupTypes.COAL, ItemResourceSlot.create(ItemSlotDisplay.create(71, 53), (item, tag) -> CoalGeneratorBlockEntity.FUEL_MAP.containsKey(item)))
                    ::build
    );

    public static final MachineType<BasicSolarPanelBlockEntity, SolarPanelMenu<BasicSolarPanelBlockEntity>> BASIC_SOLAR_PANEL = MachineType.create(
            GCBlocks.BASIC_SOLAR_PANEL,
            GCBlockEntityTypes.BASIC_SOLAR_PANEL,
            GCMenuTypes.BASIC_SOLAR_PANEL,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().solarPanelEnergyProductionRate() * 2,
                    Galacticraft.CONFIG_MANAGER.get().solarPanelEnergyProductionRate() * 2,
                    false,
                    true
            ),
            () -> MachineItemStorage.builder()
                    .addSingle(GCSlotGroupTypes.ENERGY_TO_ITEM, ItemResourceSlot.create(ItemSlotDisplay.create(8, 62), ResourceFilters.CAN_INSERT_ENERGY, ResourceFilters.CAN_EXTRACT_ENERGY_STRICT))
                    .build()
    );

    public static final MachineType<AdvancedSolarPanelBlockEntity, SolarPanelMenu<AdvancedSolarPanelBlockEntity>> ADVANCED_SOLAR_PANEL = MachineType.create(
            GCBlocks.ADVANCED_SOLAR_PANEL,
            GCBlockEntityTypes.ADVANCED_SOLAR_PANEL,
            GCMenuTypes.ADVANCED_SOLAR_PANEL,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().solarPanelEnergyProductionRate() * 2,
                    Galacticraft.CONFIG_MANAGER.get().solarPanelEnergyProductionRate() * 2,
                    false,
                    true
            ),
            () -> MachineItemStorage.builder()
                    .addSingle(GCSlotGroupTypes.ENERGY_TO_ITEM, ItemResourceSlot.create(ItemSlotDisplay.create(8, 62), ResourceFilters.CAN_INSERT_ENERGY, ResourceFilters.CAN_EXTRACT_ENERGY_STRICT))
                    .build()
    );

    public static final MachineType<CircuitFabricatorBlockEntity, RecipeMachineMenu<Container, FabricationRecipe, CircuitFabricatorBlockEntity>> CIRCUIT_FABRICATOR = MachineType.create(
            GCBlocks.CIRCUIT_FABRICATOR,
            GCBlockEntityTypes.CIRCUIT_FABRICATOR,
            GCMenuTypes.CIRCUIT_FABRICATOR,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().circuitFabricatorEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG_MANAGER.get().circuitFabricatorEnergyConsumptionRate() * 2,
                    false,
                    true
            ),
            () -> MachineItemStorage.builder()
                    .addSingle(GCSlotGroupTypes.ENERGY_TO_SELF, ItemResourceSlot.create(ItemSlotDisplay.create(8, 70), ResourceFilters.CAN_INSERT_ENERGY, ResourceFilters.CAN_INSERT_ENERGY_STRICT))
                    .addSingle(GCSlotGroupTypes.DIAMOND_INPUT, ItemResourceSlot.create(ItemSlotDisplay.create(31, 15), Constant.Filter.Item.DIAMOND))
                    .addGroup(SlotGroup.<Item, ItemStack, ItemResourceSlot>create(GCSlotGroupTypes.SILICON_INPUT)
                            .add(ItemResourceSlot.create(ItemSlotDisplay.create(62, 45), Constant.Filter.Item.SILICON))
                            .add(ItemResourceSlot.create(ItemSlotDisplay.create(62, 63), Constant.Filter.Item.SILICON))
                            .build())
                    .addSingle(GCSlotGroupTypes.REDSTONE_INPUT, ItemResourceSlot.create(ItemSlotDisplay.create(107, 70), Constant.Filter.Item.REDSTONE))
                    .addSingle(GCSlotGroupTypes.GENERIC_INPUT, ItemResourceSlot.create(ItemSlotDisplay.create(134, 15), ResourceFilters.always()))
                    .addSingle(GCSlotGroupTypes.GENERIC_OUTPUT, ItemResourceSlot.create(ItemSlotDisplay.create(152, 70), ResourceFilters.always()))
                    .build()
    );

    public static final MachineType<CompressorBlockEntity, CompressorMenu> COMPRESSOR = MachineType.create(
            GCBlocks.COMPRESSOR,
            GCBlockEntityTypes.COMPRESSOR,
            GCMenuTypes.COMPRESSOR,
            MachineEnergyStorage::empty,
            () -> {
                SlotGroup.Builder<Item, ItemStack, ItemResourceSlot> craftingSlots = SlotGroup.create(GCSlotGroupTypes.SILICON_INPUT);
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 3; x++) {
                        craftingSlots.add(ItemResourceSlot.create(ItemSlotDisplay.create(x * 18 + 17, y * 18 + 17), ResourceFilters.always()));
                    }
                }

                return MachineItemStorage.builder()
                        .addSingle(GCSlotGroupTypes.SOLID_FUEL, ItemResourceSlot.create(ItemSlotDisplay.create(8, 70), (item, tag) -> FuelRegistry.INSTANCE.get(item) > 0))
                        .addGroup(craftingSlots.build())
                        .addSingle(GCSlotGroupTypes.GENERIC_OUTPUT, ItemResourceSlot.create(ItemSlotDisplay.create(143, 36), ResourceFilters.always()))
                        .build();
            }
    );

}
