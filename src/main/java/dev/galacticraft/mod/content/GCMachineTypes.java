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

package dev.galacticraft.mod.content;

import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.machinelib.api.filter.ResourceFilters;
import dev.galacticraft.machinelib.api.machine.MachineType;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.MachineFluidStorage;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.slot.FluidResourceSlot;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.transfer.InputType;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.block.entity.machine.*;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.screen.*;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class GCMachineTypes {
    public static final MachineType<CoalGeneratorBlockEntity, CoalGeneratorMenu> COAL_GENERATOR = MachineType.create(
            GCBlocks.COAL_GENERATOR,
            GCBlockEntityTypes.COAL_GENERATOR,
            GCMenuTypes.COAL_GENERATOR,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.coalGeneratorEnergyProductionRate() * 2,
                    Galacticraft.CONFIG.coalGeneratorEnergyProductionRate() * 2,
                    false,
                    true
            ),
            MachineItemStorage.of(
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_INSERT_ENERGY),
                    ItemResourceSlot.builder(InputType.INPUT)
                            .pos(71, 53)
                            .filter((item, tag) -> CoalGeneratorBlockEntity.FUEL_MAP.containsKey(item))
            )
    );

    public static final MachineType<BasicSolarPanelBlockEntity, SolarPanelMenu<BasicSolarPanelBlockEntity>> BASIC_SOLAR_PANEL = MachineType.create(
            GCBlocks.BASIC_SOLAR_PANEL,
            GCBlockEntityTypes.BASIC_SOLAR_PANEL,
            GCMenuTypes.BASIC_SOLAR_PANEL,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.solarPanelEnergyProductionRate() * 2,
                    Galacticraft.CONFIG.solarPanelEnergyProductionRate() * 2,
                    false,
                    true
            ),
            MachineItemStorage.of(
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_INSERT_ENERGY)
            )
    );

    public static final MachineType<AdvancedSolarPanelBlockEntity, SolarPanelMenu<AdvancedSolarPanelBlockEntity>> ADVANCED_SOLAR_PANEL = MachineType.create(
            GCBlocks.ADVANCED_SOLAR_PANEL,
            GCBlockEntityTypes.ADVANCED_SOLAR_PANEL,
            GCMenuTypes.ADVANCED_SOLAR_PANEL,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.solarPanelEnergyProductionRate() * 2,
                    Galacticraft.CONFIG.solarPanelEnergyProductionRate() * 2,
                    false,
                    true
            ),
            MachineItemStorage.of(
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_INSERT_ENERGY)
            )
    );

    public static final MachineType<CircuitFabricatorBlockEntity, RecipeMachineMenu<Container, FabricationRecipe, CircuitFabricatorBlockEntity>> CIRCUIT_FABRICATOR = MachineType.create(
            GCBlocks.CIRCUIT_FABRICATOR,
            GCBlockEntityTypes.CIRCUIT_FABRICATOR,
            GCMenuTypes.CIRCUIT_FABRICATOR,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.circuitFabricatorEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG.circuitFabricatorEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.of(
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(8, 70)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY),
                    ItemResourceSlot.builder(InputType.INPUT)
                            .pos(31, 15)
                            .filter(ResourceFilters.itemTag(ConventionalItemTags.DIAMONDS)),
                    ItemResourceSlot.builder(InputType.INPUT)
                            .pos(62, 45)
                            .filter(ResourceFilters.ofResource(GCItems.RAW_SILICON)),
                    ItemResourceSlot.builder(InputType.INPUT)
                            .pos(62, 63)
                            .filter(ResourceFilters.ofResource(GCItems.RAW_SILICON)),
                    ItemResourceSlot.builder(InputType.INPUT)
                            .pos(107, 70)
                            .filter(ResourceFilters.ofResource(Items.REDSTONE)),
                    ItemResourceSlot.builder(InputType.INPUT)
                            .pos(134, 15),
                    ItemResourceSlot.builder(InputType.RECIPE_OUTPUT)
                            .pos(152, 70)
            )
    );

    public static final MachineType<CompressorBlockEntity, CompressorMenu> COMPRESSOR = MachineType.create(
            GCBlocks.COMPRESSOR,
            GCBlockEntityTypes.COMPRESSOR,
            GCMenuTypes.COMPRESSOR,
            MachineEnergyStorage::empty,
            MachineItemStorage.builder()
                    .add(ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(83, 47)
                            .filter((item, tag) -> {
                                Integer integer = FuelRegistry.INSTANCE.get(item);
                                return integer != null && integer > 0;
                            }))
                    .add3x3Grid(InputType.INPUT, 17, 17)
                    .add(ItemResourceSlot.builder(InputType.RECIPE_OUTPUT)
                            .pos(143, 36))
    );

    public static final MachineType<ElectricArcFurnaceBlockEntity, RecipeMachineMenu<Container, BlastingRecipe, ElectricArcFurnaceBlockEntity>> ELECTRIC_ARC_FURNACE = MachineType.create(
            GCBlocks.ELECTRIC_ARC_FURNACE,
            GCBlockEntityTypes.ELECTRIC_ARC_FURNACE,
            GCMenuTypes.ELECTRIC_ARC_FURNACE,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.electricArcFurnaceEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG.electricArcFurnaceEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.of(
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY),
                    ItemResourceSlot.builder(InputType.INPUT)
                            .pos(44, 35),
                    ItemResourceSlot.builder(InputType.RECIPE_OUTPUT)
                            .pos(108, 35),
                    ItemResourceSlot.builder(InputType.RECIPE_OUTPUT)
                            .pos(134, 35)
            )
    );

    public static final MachineType<ElectricCompressorBlockEntity, RecipeMachineMenu<CraftingContainer, CompressingRecipe, ElectricCompressorBlockEntity>> ELECTRIC_COMPRESSOR = MachineType.create(
            GCBlocks.ELECTRIC_COMPRESSOR,
            GCBlockEntityTypes.ELECTRIC_COMPRESSOR,
            GCMenuTypes.ELECTRIC_COMPRESSOR,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.electricCompressorEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG.electricCompressorEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.builder()
                    .add(ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(8, 61)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                    )
                    .add3x3Grid(InputType.INPUT, 30, 17)
                    .add(ItemResourceSlot.builder(InputType.RECIPE_OUTPUT)
                            .pos(148, 22)
                    )
                    .add(ItemResourceSlot.builder(InputType.RECIPE_OUTPUT)
                            .pos(148, 48)
                    )
    );

    public static final MachineType<ElectricFurnaceBlockEntity, RecipeMachineMenu<Container, SmeltingRecipe, ElectricFurnaceBlockEntity>> ELECTRIC_FURNACE = MachineType.create(
            GCBlocks.ELECTRIC_FURNACE,
            GCBlockEntityTypes.ELECTRIC_FURNACE,
            GCMenuTypes.ELECTRIC_FURNACE,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.electricFurnaceEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG.electricFurnaceEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.of(
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(8, 61)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY),
                    ItemResourceSlot.builder(InputType.INPUT)
                            .pos(52, 35),
                    ItemResourceSlot.builder(InputType.RECIPE_OUTPUT)
                            .pos(113, 35)
            )
    );

    public static final MachineType<EnergyStorageModuleBlockEntity, MachineMenu<EnergyStorageModuleBlockEntity>> ENERGY_STORAGE_MODULE = MachineType.create(
            GCBlocks.ENERGY_STORAGE_MODULE,
            GCBlockEntityTypes.ENERGY_STORAGE_MODULE,
            GCMenuTypes.ENERGY_STORAGE_MODULE,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.energyStorageModuleStorageSize(),
                    Galacticraft.CONFIG.energyStorageModuleStorageSize() / 200,
                    Galacticraft.CONFIG.energyStorageModuleStorageSize() / 200,
                    true,
                    true
            ),
            MachineItemStorage.of(
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(102, 48)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY),
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(102, 24)
                            .filter(ResourceFilters.CAN_INSERT_ENERGY)
            )
    );

    public static final MachineType<FuelLoaderBlockEntity, FuelLoaderMenu> FUEL_LOADER = MachineType.create(
            GCBlocks.FUEL_LOADER,
            GCBlockEntityTypes.FUEL_LOADER,
            GCMenuTypes.FUEL_LOADER,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    150 * 2, // fixme
                    150 * 2,
                    true,
                    false
            ),
            MachineItemStorage.of(
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY),
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(80, 62)
                            .filter(ResourceFilters.canExtractFluid(GCFluids.FUEL)) // fixme: fuel tag?,
            ),
            MachineFluidStorage.of(
                    FluidResourceSlot.builder(InputType.INPUT)
                            .hidden()
                            .capacity(FluidConstants.BUCKET * 50)
                            .filter(ResourceFilters.ofResource(GCFluids.FUEL)) // fixme: tag?
            )
    );

    public static final MachineType<OxygenBubbleDistributorBlockEntity, OxygenBubbleDistributorMenu> OXYGEN_BUBBLE_DISTRIBUTOR = MachineType.create(
            GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR,
            GCBlockEntityTypes.OXYGEN_BUBBLE_DISTRIBUTOR,
            GCMenuTypes.OXYGEN_BUBBLE_DISTRIBUTOR,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.oxygenCollectorEnergyConsumptionRate() * 2, // fixme
                    Galacticraft.CONFIG.oxygenCollectorEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.of(
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY),
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(31, 62)
                            .filter(ResourceFilters.canExtractFluid(Gases.OXYGEN))
            ),
            MachineFluidStorage.of(
                    FluidResourceSlot.builder(InputType.INPUT)
                            .pos(31, 8)
                            .capacity(OxygenBubbleDistributorBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
            )
    );

    public static final MachineType<OxygenCollectorBlockEntity, OxygenCollectorMenu> OXYGEN_COLLECTOR = MachineType.create(
            GCBlocks.OXYGEN_COLLECTOR,
            GCBlockEntityTypes.OXYGEN_COLLECTOR,
            GCMenuTypes.OXYGEN_COLLECTOR,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.oxygenCollectorEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG.oxygenCollectorEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.of(
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
            ),
            MachineFluidStorage.of(
                    FluidResourceSlot.builder(InputType.OUTPUT)
                            .pos(31, 8)
                            .capacity(OxygenCollectorBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
            )
    );

    public static final MachineType<OxygenCompressorBlockEntity, MachineMenu<OxygenCompressorBlockEntity>> OXYGEN_COMPRESSOR = MachineType.create(
            GCBlocks.OXYGEN_COMPRESSOR,
            GCBlockEntityTypes.OXYGEN_COMPRESSOR,
            GCMenuTypes.OXYGEN_COMPRESSOR,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.oxygenCompressorEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG.oxygenCompressorEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.of(
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY),
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(80, 27)
                            .filter(ResourceFilters.canInsertFluid(Gases.OXYGEN))
            ),
            MachineFluidStorage.of(
                    FluidResourceSlot.builder(InputType.INPUT)
                            .pos(31, 8)
                            .capacity(OxygenCompressorBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
            )
    );

    public static final MachineType<OxygenDecompressorBlockEntity, MachineMenu<OxygenDecompressorBlockEntity>> OXYGEN_DECOMPRESSOR = MachineType.create(
            GCBlocks.OXYGEN_DECOMPRESSOR,
            GCBlockEntityTypes.OXYGEN_DECOMPRESSOR,
            GCMenuTypes.OXYGEN_DECOMPRESSOR,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.oxygenDecompressorEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG.oxygenDecompressorEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.of(
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY),
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(80, 27)
                            .filter(ResourceFilters.canExtractFluid(Gases.OXYGEN))
            ),
            MachineFluidStorage.of(
                    FluidResourceSlot.builder(InputType.OUTPUT)
                            .pos(31, 8)
                            .capacity(OxygenDecompressorBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
            )
    );

    public static final MachineType<OxygenSealerBlockEntity, MachineMenu<OxygenSealerBlockEntity>> OXYGEN_SEALER = MachineType.create(
            GCBlocks.OXYGEN_SEALER,
            GCBlockEntityTypes.OXYGEN_SEALER,
            GCMenuTypes.OXYGEN_SEALER,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.oxygenCompressorEnergyConsumptionRate() * 2, // fixme
                    Galacticraft.CONFIG.oxygenCompressorEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.of(
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY),
                    ItemResourceSlot.builder(InputType.TRANSFER) // todo: drop for decompressor?
                            .pos(31, 62)
                            .filter(ResourceFilters.canExtractFluid(Gases.OXYGEN))
            ),
            MachineFluidStorage.of(
                    FluidResourceSlot.builder(InputType.INPUT)
                            .pos(30, 8)
                            .capacity(OxygenSealerBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
            )
    );

    public static final MachineType<OxygenStorageModuleBlockEntity, MachineMenu<OxygenStorageModuleBlockEntity>> OXYGEN_STORAGE_MODULE = MachineType.create(
            GCBlocks.OXYGEN_STORAGE_MODULE,
            GCBlockEntityTypes.OXYGEN_STORAGE_MODULE,
            GCMenuTypes.OXYGEN_STORAGE_MODULE,
            MachineEnergyStorage::empty,
            MachineItemStorage::empty,
            MachineFluidStorage.of(
                    FluidResourceSlot.builder(InputType.STORAGE)
                            .hidden()
                            .capacity(OxygenStorageModuleBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
            )
    );

    public static final MachineType<RefineryBlockEntity, MachineMenu<RefineryBlockEntity>> REFINERY = MachineType.create(
            GCBlocks.REFINERY,
            GCBlockEntityTypes.REFINERY,
            GCMenuTypes.REFINERY,
            () -> MachineEnergyStorage.create(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.refineryEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG.refineryEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.of(
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(8, 7)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY),
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(123, 7)
                            .filter(ResourceFilters.canExtractFluid(GCFluids.CRUDE_OIL)), // fixme: tag?,
                    ItemResourceSlot.builder(InputType.TRANSFER)
                            .pos(153, 7)
                            .filter(ResourceFilters.canInsertFluid(GCFluids.FUEL)) // fixme: tag?
            ),
            MachineFluidStorage.of(
                    FluidResourceSlot.builder(InputType.INPUT)
                            .pos(123, 29)
                            .capacity(RefineryBlockEntity.MAX_CAPACITY)
                            .filter(ResourceFilters.ofResource(GCFluids.CRUDE_OIL)),
                    FluidResourceSlot.builder(InputType.RECIPE_OUTPUT)
                            .pos(153, 29)
                            .capacity(RefineryBlockEntity.MAX_CAPACITY)
                            .filter(ResourceFilters.ofResource(GCFluids.FUEL))
            )
    );
}
