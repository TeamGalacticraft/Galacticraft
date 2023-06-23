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

import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.machinelib.api.machine.MachineType;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.MachineFluidStorage;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.ResourceFilters;
import dev.galacticraft.machinelib.api.storage.slot.FluidResourceSlot;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.storage.slot.SlotGroup;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.block.entity.machine.*;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.machine.storage.io.GCSlotGroupTypes;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.screen.*;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.jetbrains.annotations.NotNull;

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
                    .single(GCSlotGroupTypes.ENERGY_TO_ITEM, ItemResourceSlot.builder()
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_INSERT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_INSERT_ENERGY_STRICT)
                            ::build
                    ).single(GCSlotGroupTypes.COAL, ItemResourceSlot.builder()
                            .pos(71, 53)
                            .filter((item, tag) -> CoalGeneratorBlockEntity.FUEL_MAP.containsKey(item))
                            ::build
                    )::build
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
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.ENERGY_TO_ITEM, ItemResourceSlot.builder()
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_INSERT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_INSERT_ENERGY_STRICT)
                            ::build
                    )::build
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
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.ENERGY_TO_ITEM, ItemResourceSlot.builder()
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_INSERT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_INSERT_ENERGY_STRICT)
                            ::build
                    )::build
    );

    public static final MachineType<CircuitFabricatorBlockEntity, RecipeMachineMenu<Container, FabricationRecipe, CircuitFabricatorBlockEntity>> CIRCUIT_FABRICATOR = MachineType.create(
            GCBlocks.CIRCUIT_FABRICATOR,
            GCBlockEntityTypes.CIRCUIT_FABRICATOR,
            GCMenuTypes.CIRCUIT_FABRICATOR,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().circuitFabricatorEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG_MANAGER.get().circuitFabricatorEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.ENERGY_TO_SELF, ItemResourceSlot.builder()
                            .pos(8, 70)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_EXTRACT_ENERGY_STRICT)
                            ::build
                    ).single(GCSlotGroupTypes.DIAMOND_INPUT, ItemResourceSlot.builder()
                            .pos(31, 15)
                            .filter(ResourceFilters.itemTag(ConventionalItemTags.DIAMONDS))
                            ::build
                    ).group(GCSlotGroupTypes.SILICON_INPUT, SlotGroup.item()
                            .add(ItemResourceSlot.builder().pos(62, 45).filter(ResourceFilters.ofResource(GCItems.RAW_SILICON))::build)
                            .add(ItemResourceSlot.builder().pos(62, 63).filter(ResourceFilters.ofResource(GCItems.RAW_SILICON))::build)
                            ::build
                    ).single(GCSlotGroupTypes.REDSTONE_INPUT, ItemResourceSlot.builder()
                            .pos(107, 70)
                            .filter(ResourceFilters.ofResource(Items.REDSTONE))
                            ::build
                    ).single(GCSlotGroupTypes.GENERIC_INPUT, ItemResourceSlot.builder()
                            .pos(134, 15)
                            ::build
                    ).single(GCSlotGroupTypes.GENERIC_OUTPUT, ItemResourceSlot.builder()
                            .pos(152, 70)
                            ::build
                    )::build
    );

    public static final MachineType<CompressorBlockEntity, CompressorMenu> COMPRESSOR = MachineType.create(
            GCBlocks.COMPRESSOR,
            GCBlockEntityTypes.COMPRESSOR,
            GCMenuTypes.COMPRESSOR,
            MachineEnergyStorage::empty,
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.SOLID_FUEL, ItemResourceSlot.builder().pos(83, 47).filter((item, tag) -> FuelRegistry.INSTANCE.get(item) > 0)::build)
                    .group(GCSlotGroupTypes.GENERIC_INPUT,
                            generate3x3Grid(17, 17)::build
                    ).single(GCSlotGroupTypes.GENERIC_OUTPUT, ItemResourceSlot.builder()
                            .pos(143, 36)
                            ::build
                    )::build
    );

    public static final MachineType<ElectricArcFurnaceBlockEntity, RecipeMachineMenu<Container, BlastingRecipe, ElectricArcFurnaceBlockEntity>> ELECTRIC_ARC_FURNACE = MachineType.create(
            GCBlocks.ELECTRIC_ARC_FURNACE,
            GCBlockEntityTypes.ELECTRIC_ARC_FURNACE,
            GCMenuTypes.ELECTRIC_ARC_FURNACE,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().electricArcFurnaceEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG_MANAGER.get().electricArcFurnaceEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.ENERGY_TO_SELF, ItemResourceSlot.builder()
                            .pos(8, 61)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_EXTRACT_ENERGY_STRICT)
                            ::build
                    ).single(GCSlotGroupTypes.GENERIC_INPUT, ItemResourceSlot.builder()
                            .pos(44, 35)
                            ::build
                    ).group(GCSlotGroupTypes.GENERIC_OUTPUT, SlotGroup.item()
                            .add(ItemResourceSlot.builder().pos(108, 35)::build)
                            .add(ItemResourceSlot.builder().pos(134, 35)::build)
                            ::build
                    )::build
    );

    public static final MachineType<ElectricCompressorBlockEntity, RecipeMachineMenu<Container, CompressingRecipe, ElectricCompressorBlockEntity>> ELECTRIC_COMPRESSOR = MachineType.create(
            GCBlocks.ELECTRIC_COMPRESSOR,
            GCBlockEntityTypes.ELECTRIC_COMPRESSOR,
            GCMenuTypes.ELECTRIC_COMPRESSOR,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().electricCompressorEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG_MANAGER.get().electricCompressorEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.ENERGY_TO_SELF, ItemResourceSlot.builder()
                            .pos(8, 61)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_EXTRACT_ENERGY_STRICT)
                            ::build
                    ).group(GCSlotGroupTypes.GENERIC_INPUT,
                            generate3x3Grid(30, 17)::build
                    ).group(GCSlotGroupTypes.GENERIC_OUTPUT, SlotGroup.item()
                            .add(ItemResourceSlot.builder().pos(148, 22)::build)
                            .add(ItemResourceSlot.builder().pos(148, 48)::build)
                            ::build
                    )::build
    );

    public static final MachineType<ElectricFurnaceBlockEntity, RecipeMachineMenu<Container, SmeltingRecipe, ElectricFurnaceBlockEntity>> ELECTRIC_FURNACE = MachineType.create(
            GCBlocks.ELECTRIC_FURNACE,
            GCBlockEntityTypes.ELECTRIC_FURNACE,
            GCMenuTypes.ELECTRIC_FURNACE,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().electricFurnaceEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG_MANAGER.get().electricFurnaceEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.ENERGY_TO_SELF, ItemResourceSlot.builder()
                            .pos(8, 61)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_EXTRACT_ENERGY_STRICT)
                            ::build
                    ).single(GCSlotGroupTypes.GENERIC_INPUT, ItemResourceSlot.builder()
                            .pos(52, 35)
                            ::build
                    )
                    .single(GCSlotGroupTypes.GENERIC_OUTPUT, ItemResourceSlot.builder()
                            .pos(113, 35)
                            ::build
                    )::build
    );

    public static final MachineType<EnergyStorageModuleBlockEntity, MachineMenu<EnergyStorageModuleBlockEntity>> ENERGY_STORAGE_MODULE = MachineType.create(
            GCBlocks.ENERGY_STORAGE_MODULE,
            GCBlockEntityTypes.ENERGY_STORAGE_MODULE,
            GCMenuTypes.ENERGY_STORAGE_MODULE,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().energyStorageModuleStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().energyStorageModuleStorageSize() / 200,
                    Galacticraft.CONFIG_MANAGER.get().energyStorageModuleStorageSize() / 200,
                    true,
                    true
            ),
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.ENERGY_TO_SELF, ItemResourceSlot.builder()
                            .pos(102, 24)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_EXTRACT_ENERGY_STRICT)
                            ::build
                    ).single(GCSlotGroupTypes.ENERGY_TO_ITEM, ItemResourceSlot.builder()
                            .pos(102, 48)
                            .filter(ResourceFilters.CAN_INSERT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_INSERT_ENERGY_STRICT)
                            ::build
                    )::build
    );

    public static final MachineType<FuelLoaderBlockEntity, FuelLoaderMenu> FUEL_LOADER = MachineType.create(
            GCBlocks.FUEL_LOADER,
            GCBlockEntityTypes.FUEL_LOADER,
            GCMenuTypes.FUEL_LOADER,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    150 * 2, // fixme
                    150 * 2,
                    true,
                    false
            ),
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.ENERGY_TO_SELF, ItemResourceSlot.builder()
                            .pos(8, 61)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_EXTRACT_ENERGY_STRICT)
                            ::build
                    ).single(GCSlotGroupTypes.FUEL_INPUT, ItemResourceSlot.builder()
                            .pos(80, 61)
                            .filter(ResourceFilters.isFluidStorage())
                            .strictFilter(ResourceFilters.canExtractFluidStrict(GCFluids.FUEL)) // fixme: fuel api?
                            ::build
                    )::build,
            MachineFluidStorage.builder()
                    .single(GCSlotGroupTypes.FUEL_INPUT, FluidResourceSlot.builder()
                            .height(0)
                            .capacity(FluidConstants.BUCKET * 50)
                            .filter(ResourceFilters.ofResource(GCFluids.FUEL)) // fixme: tag?
                            ::build
                    )::build
    );

    public static final MachineType<OxygenBubbleDistributorBlockEntity, OxygenBubbleDistributorMenu> OXYGEN_BUBBLE_DISTRIBUTOR = MachineType.create(
            GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR,
            GCBlockEntityTypes.OXYGEN_BUBBLE_DISTRIBUTOR,
            GCMenuTypes.OXYGEN_BUBBLE_DISTRIBUTOR,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().oxygenCollectorEnergyConsumptionRate() * 2, // fixme
                    Galacticraft.CONFIG_MANAGER.get().oxygenCollectorEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.ENERGY_TO_SELF, ItemResourceSlot.builder()
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_EXTRACT_ENERGY_STRICT)
                            ::build
                    ).single(GCSlotGroupTypes.OXYGEN_TO_SELF, ItemResourceSlot.builder()
                            .pos(31, 62)
                            .filter(ResourceFilters.isFluidStorage())
                            .strictFilter(ResourceFilters.canExtractFluidStrict(Gases.OXYGEN))
                            ::build
                    )::build,
            MachineFluidStorage.builder()
                    .single(GCSlotGroupTypes.OXYGEN_INPUT, FluidResourceSlot.builder()
                            .pos(31, 8)
                            .capacity(OxygenBubbleDistributorBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
                            ::build
                    )::build
    );

    public static final MachineType<OxygenCollectorBlockEntity, OxygenCollectorMenu> OXYGEN_COLLECTOR = MachineType.create(
            GCBlocks.OXYGEN_COLLECTOR,
            GCBlockEntityTypes.OXYGEN_COLLECTOR,
            GCMenuTypes.OXYGEN_COLLECTOR,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().oxygenCollectorEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG_MANAGER.get().oxygenCollectorEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.ENERGY_TO_SELF, ItemResourceSlot.builder()
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_EXTRACT_ENERGY_STRICT)
                            ::build
                    )::build,
            MachineFluidStorage.builder()
                    .single(GCSlotGroupTypes.OXYGEN_OUTPUT, FluidResourceSlot.builder()
                            .pos(31, 8)
                            .capacity(OxygenCollectorBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
                            ::build
                    )::build
    );

    public static final MachineType<OxygenCompressorBlockEntity, MachineMenu<OxygenCompressorBlockEntity>> OXYGEN_COMPRESSOR = MachineType.create(
            GCBlocks.OXYGEN_COMPRESSOR,
            GCBlockEntityTypes.OXYGEN_COMPRESSOR,
            GCMenuTypes.OXYGEN_COMPRESSOR,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().oxygenCompressorEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG_MANAGER.get().oxygenCompressorEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.ENERGY_TO_SELF, ItemResourceSlot.builder()
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_EXTRACT_ENERGY_STRICT)
                            ::build
                    ).single(GCSlotGroupTypes.OXYGEN_TO_ITEM, ItemResourceSlot.builder()
                            .pos(80, 27)
                            .filter(ResourceFilters.isFluidStorage())
                            .strictFilter(ResourceFilters.canInsertFluidStrict(Gases.OXYGEN))
                            ::build
                    )::build,
            MachineFluidStorage.builder()
                    .single(GCSlotGroupTypes.OXYGEN_INPUT, FluidResourceSlot.builder()
                            .pos(31, 8)
                            .capacity(OxygenCompressorBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
                            ::build
                    )::build
    );

    public static final MachineType<OxygenDecompressorBlockEntity, MachineMenu<OxygenDecompressorBlockEntity>> OXYGEN_DECOMPRESSOR = MachineType.create(
            GCBlocks.OXYGEN_DECOMPRESSOR,
            GCBlockEntityTypes.OXYGEN_DECOMPRESSOR,
            GCMenuTypes.OXYGEN_DECOMPRESSOR,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().oxygenDecompressorEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG_MANAGER.get().oxygenDecompressorEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.ENERGY_TO_SELF, ItemResourceSlot.builder()
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_EXTRACT_ENERGY_STRICT)
                            ::build
                    ).single(GCSlotGroupTypes.OXYGEN_TO_SELF, ItemResourceSlot.builder()
                            .pos(80, 27)
                            .filter(ResourceFilters.isFluidStorage())
                            .strictFilter(ResourceFilters.canExtractFluidStrict(Gases.OXYGEN))
                            ::build
                    )::build,
            MachineFluidStorage.builder()
                    .single(GCSlotGroupTypes.OXYGEN_OUTPUT, FluidResourceSlot.builder()
                            .pos(31, 8)
                            .capacity(OxygenDecompressorBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
                            ::build
                    )::build
    );

    public static final MachineType<OxygenSealerBlockEntity, MachineMenu<OxygenSealerBlockEntity>> OXYGEN_SEALER = MachineType.create(
            GCBlocks.OXYGEN_SEALER,
            GCBlockEntityTypes.OXYGEN_SEALER,
            GCMenuTypes.OXYGEN_SEALER,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().oxygenCompressorEnergyConsumptionRate() * 2, // fixme
                    Galacticraft.CONFIG_MANAGER.get().oxygenCompressorEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.ENERGY_TO_SELF, ItemResourceSlot.builder()
                            .pos(8, 62)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_EXTRACT_ENERGY_STRICT)
                            ::build
                    ).single(GCSlotGroupTypes.OXYGEN_TO_SELF, ItemResourceSlot.builder() // todo: drop for decompressor?
                            .pos(31, 62)
                            .filter(ResourceFilters.isFluidStorage())
                            .strictFilter(ResourceFilters.canExtractFluidStrict(Gases.OXYGEN))
                            ::build
                    )::build,
            MachineFluidStorage.builder()
                    .single(GCSlotGroupTypes.OXYGEN_INPUT, FluidResourceSlot.builder()
                            .pos(31, 8)
                            .capacity(OxygenSealerBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
                            ::build
                    )::build
    );

    public static final MachineType<OxygenStorageModuleBlockEntity, MachineMenu<OxygenStorageModuleBlockEntity>> OXYGEN_STORAGE_MODULE = MachineType.create(
            GCBlocks.OXYGEN_STORAGE_MODULE,
            GCBlockEntityTypes.OXYGEN_STORAGE_MODULE,
            GCMenuTypes.OXYGEN_STORAGE_MODULE,
            MachineEnergyStorage::empty,
            MachineItemStorage::empty,
            MachineFluidStorage.builder()
                    .single(GCSlotGroupTypes.OXYGEN_TANK, FluidResourceSlot.builder()
                            .pos(31, 8)
                            .capacity(OxygenStorageModuleBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
                            ::build
                    )::build
    );

    public static final MachineType<RefineryBlockEntity, MachineMenu<RefineryBlockEntity>> REFINERY = MachineType.create(
            GCBlocks.REFINERY,
            GCBlockEntityTypes.REFINERY,
            GCMenuTypes.REFINERY,
            () -> MachineEnergyStorage.of(
                    Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize(),
                    Galacticraft.CONFIG_MANAGER.get().refineryEnergyConsumptionRate() * 2,
                    Galacticraft.CONFIG_MANAGER.get().refineryEnergyConsumptionRate() * 2,
                    true,
                    false
            ),
            MachineItemStorage.builder()
                    .single(GCSlotGroupTypes.ENERGY_TO_SELF, ItemResourceSlot.builder()
                            .pos(8, 7)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .strictFilter(ResourceFilters.CAN_EXTRACT_ENERGY_STRICT)
                            ::build
                    ).single(GCSlotGroupTypes.OIL_FROM_ITEM, ItemResourceSlot.builder()
                            .pos(123, 7)
                            .filter(ResourceFilters.isFluidStorage())
                            .strictFilter(ResourceFilters.canExtractFluidStrict(GCFluids.CRUDE_OIL)) // fixme: tag?
                            ::build
                    ).single(GCSlotGroupTypes.FUEL_TO_ITEM, ItemResourceSlot.builder()
                            .pos(153, 7)
                            .filter(ResourceFilters.isFluidStorage())
                            .strictFilter(ResourceFilters.canInsertFluidStrict(GCFluids.FUEL)) // fixme: tag?
                            ::build
                    )::build,
            MachineFluidStorage.builder()
                    .single(GCSlotGroupTypes.OIL_INPUT, FluidResourceSlot.builder()
                            .pos(122, 28)
                            .capacity(RefineryBlockEntity.MAX_CAPACITY)
                            .filter(ResourceFilters.ofResource(GCFluids.CRUDE_OIL))
                            ::build
                    )
                    .single(GCSlotGroupTypes.FUEL_OUTPUT, FluidResourceSlot.builder()
                            .pos(152, 28)
                            .capacity(RefineryBlockEntity.MAX_CAPACITY)
                            .filter(ResourceFilters.ofResource(GCFluids.FUEL))
                            ::build
                    )::build
    );

    private static @NotNull SlotGroup.Builder<Item, ItemStack, ItemResourceSlot> generate3x3Grid(int xOffset, int yOffset) {
        return generateGrid(xOffset, yOffset, 3, 3);
    }

    private static @NotNull SlotGroup.Builder<Item, ItemStack, ItemResourceSlot> generateGrid(int xOffset, int yOffset, int width, int height) {
        assert width > 0;
        assert height > 0;
        SlotGroup.Builder<Item, ItemStack, ItemResourceSlot> grid = SlotGroup.item();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid.add(ItemResourceSlot.builder().pos(x * 18 + xOffset, y * 18 + yOffset)::build);
            }
        }
        return grid;
    }
}
