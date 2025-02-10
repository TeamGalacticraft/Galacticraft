/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.*;
import dev.galacticraft.mod.content.block.entity.machine.*;
import dev.galacticraft.mod.content.block.entity.networked.FluidPipeWalkwayBlockEntity;
import dev.galacticraft.mod.content.block.entity.networked.GlassFluidPipeBlockEntity;
import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import dev.galacticraft.mod.content.block.entity.networked.WireWalkwayBlockEntity;
import dev.galacticraft.mod.content.block.special.launchpad.LaunchPadBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GCBlockEntityTypes {
    public static final GCRegistry<BlockEntityType<?>> BLOCK_ENTITIES = new GCRegistry<>(BuiltInRegistries.BLOCK_ENTITY_TYPE);
    // POWER GENERATION
    public static final BlockEntityType<CoalGeneratorBlockEntity> COAL_GENERATOR = BlockEntityType.Builder.of(CoalGeneratorBlockEntity::new, GCBlocks.COAL_GENERATOR).build();
    public static final BlockEntityType<BasicSolarPanelBlockEntity> BASIC_SOLAR_PANEL = BlockEntityType.Builder.of(BasicSolarPanelBlockEntity::new, GCBlocks.BASIC_SOLAR_PANEL).build();
    public static final BlockEntityType<AdvancedSolarPanelBlockEntity> ADVANCED_SOLAR_PANEL = BlockEntityType.Builder.of(AdvancedSolarPanelBlockEntity::new, GCBlocks.ADVANCED_SOLAR_PANEL).build();

    // WIRES, PIPES, WALKWAYS
    public static final BlockEntityType<WireBlockEntity> WIRE_T1 = BlockEntityType.Builder.of((pos, state) -> WireBlockEntity.createT1(GCBlockEntityTypes.WIRE_T1, pos, state), GCBlocks.ALUMINUM_WIRE, GCBlocks.SEALABLE_ALUMINUM_WIRE).build();
    public static final BlockEntityType<WireBlockEntity> WIRE_T2 = BlockEntityType.Builder.of((pos, state) -> WireBlockEntity.createT2(GCBlockEntityTypes.WIRE_T2, pos, state), /*GCBlocks.HEAVY_ALUMINUM_WIRE,*/ GCBlocks.HEAVY_SEALABLE_ALUMINUM_WIRE).build();
    public static final BlockEntityType<GlassFluidPipeBlockEntity> GLASS_FLUID_PIPE = BlockEntityType.Builder.of(GlassFluidPipeBlockEntity::new, GCBlocks.GLASS_FLUID_PIPE).build();
    public static final BlockEntityType<WalkwayBlockEntity> WALKWAY = BlockEntityType.Builder.of(WalkwayBlockEntity::new, GCBlocks.WALKWAY).build();
    public static final BlockEntityType<WireWalkwayBlockEntity> WIRE_WALKWAY = BlockEntityType.Builder.of(WireWalkwayBlockEntity::new, GCBlocks.WIRE_WALKWAY).build();
    public static final BlockEntityType<FluidPipeWalkwayBlockEntity> FLUID_PIPE_WALKWAY = BlockEntityType.Builder.of(FluidPipeWalkwayBlockEntity::new, GCBlocks.FLUID_PIPE_WALKWAY).build();

    // MACHINES
    public static final BlockEntityType<CircuitFabricatorBlockEntity> CIRCUIT_FABRICATOR = BlockEntityType.Builder.of(CircuitFabricatorBlockEntity::new, GCBlocks.CIRCUIT_FABRICATOR).build();
    public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR = BlockEntityType.Builder.of(CompressorBlockEntity::new, GCBlocks.COMPRESSOR).build();
    public static final BlockEntityType<ElectricCompressorBlockEntity> ELECTRIC_COMPRESSOR = BlockEntityType.Builder.of(ElectricCompressorBlockEntity::new, GCBlocks.ELECTRIC_COMPRESSOR).build();
    public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = BlockEntityType.Builder.of(ElectricFurnaceBlockEntity::new, GCBlocks.ELECTRIC_FURNACE).build();
    public static final BlockEntityType<ElectricArcFurnaceBlockEntity> ELECTRIC_ARC_FURNACE = BlockEntityType.Builder.of(ElectricArcFurnaceBlockEntity::new, GCBlocks.ELECTRIC_ARC_FURNACE).build();
    public static final BlockEntityType<RefineryBlockEntity> REFINERY = BlockEntityType.Builder.of(RefineryBlockEntity::new, GCBlocks.REFINERY).build();

    // OXYGEN MACHINES
    public static final BlockEntityType<OxygenCollectorBlockEntity> OXYGEN_COLLECTOR = BlockEntityType.Builder.of(OxygenCollectorBlockEntity::new, GCBlocks.OXYGEN_COLLECTOR).build();
    public static final BlockEntityType<OxygenCompressorBlockEntity> OXYGEN_COMPRESSOR = BlockEntityType.Builder.of(OxygenCompressorBlockEntity::new, GCBlocks.OXYGEN_COMPRESSOR).build();
    public static final BlockEntityType<OxygenDecompressorBlockEntity> OXYGEN_DECOMPRESSOR = BlockEntityType.Builder.of(OxygenDecompressorBlockEntity::new, GCBlocks.OXYGEN_DECOMPRESSOR).build();
    public static final BlockEntityType<OxygenSealerBlockEntity> OXYGEN_SEALER = BlockEntityType.Builder.of(OxygenSealerBlockEntity::new, GCBlocks.OXYGEN_SEALER).build();
    public static final BlockEntityType<OxygenBubbleDistributorBlockEntity> OXYGEN_BUBBLE_DISTRIBUTOR = BlockEntityType.Builder.of(OxygenBubbleDistributorBlockEntity::new, GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR).build();

    // RESOURCE STORAGE
    public static final BlockEntityType<EnergyStorageModuleBlockEntity> ENERGY_STORAGE_MODULE = BlockEntityType.Builder.of(EnergyStorageModuleBlockEntity::new, GCBlocks.ENERGY_STORAGE_MODULE).build();
    public static final BlockEntityType<OxygenStorageModuleBlockEntity> OXYGEN_STORAGE_MODULE = BlockEntityType.Builder.of(OxygenStorageModuleBlockEntity::new, GCBlocks.OXYGEN_STORAGE_MODULE).build();

    // ROCKETS
    public static final BlockEntityType<LaunchPadBlockEntity> LAUNCH_PAD = BlockEntityType.Builder.of(LaunchPadBlockEntity::new, GCBlocks.ROCKET_LAUNCH_PAD, GCBlocks.FUELING_PAD).build();
    public static final BlockEntityType<FuelLoaderBlockEntity> FUEL_LOADER = BlockEntityType.Builder.of(FuelLoaderBlockEntity::new, GCBlocks.FUEL_LOADER).build();
    public static final BlockEntityType<ParachestBlockEntity> PARACHEST = BlockEntityType.Builder.of(ParachestBlockEntity::new, GCBlocks.PARACHEST).build();

    // MISC
    public static final BlockEntityType<SolarPanelPartBlockEntity> SOLAR_PANEL_PART = BlockEntityType.Builder.of(SolarPanelPartBlockEntity::new, GCBlocks.SOLAR_PANEL_PART).build();
    public static final BlockEntityType<CryogenicChamberBlockEntity> CRYOGENIC_CHAMBER = BlockEntityType.Builder.of(CryogenicChamberBlockEntity::new, GCBlocks.CRYOGENIC_CHAMBER).build();
    public static final BlockEntityType<CryogenicChamberPartBlockEntity> CRYOGENIC_CHAMBER_PART = BlockEntityType.Builder.of(CryogenicChamberPartBlockEntity::new, GCBlocks.CRYOGENIC_CHAMBER_PART).build();
    public static final BlockEntityType<DungeonSpawnerBlockEntity> DUNGEON_BOSS_SPAWNER = BLOCK_ENTITIES.register(Constant.Block.BOSS_SPAWNER, BlockEntityType.Builder.of(DungeonSpawnerBlockEntity::new, GCBlocks.BOSS_SPAWNER).build());

    public static final BlockEntityType<AirlockControllerBlockEntity> AIRLOCK_CONTROLLER = BlockEntityType.Builder.of(AirlockControllerBlockEntity::new, GCBlocks.AIR_LOCK_CONTROLLER).build();
    public static final BlockEntityType<RocketWorkbenchBlockEntity> ROCKET_WORKBENCH = BlockEntityType.Builder.of(RocketWorkbenchBlockEntity::new, GCBlocks.ROCKET_WORKBENCH).build();

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.COAL_GENERATOR), COAL_GENERATOR);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.BASIC_SOLAR_PANEL), BASIC_SOLAR_PANEL);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.ADVANCED_SOLAR_PANEL), ADVANCED_SOLAR_PANEL);

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.ALUMINUM_WIRE), WIRE_T1);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.HEAVY_ALUMINUM_WIRE), WIRE_T2);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.GLASS_FLUID_PIPE), GLASS_FLUID_PIPE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.WALKWAY), WALKWAY);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.WIRE_WALKWAY), WIRE_WALKWAY);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.FLUID_PIPE_WALKWAY), FLUID_PIPE_WALKWAY);

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.CIRCUIT_FABRICATOR), CIRCUIT_FABRICATOR);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.COMPRESSOR), COMPRESSOR);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.ELECTRIC_COMPRESSOR), ELECTRIC_COMPRESSOR);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.ELECTRIC_FURNACE), ELECTRIC_FURNACE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.ELECTRIC_ARC_FURNACE), ELECTRIC_ARC_FURNACE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.REFINERY), REFINERY);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.ENERGY_STORAGE_MODULE), ENERGY_STORAGE_MODULE);

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.OXYGEN_COLLECTOR), OXYGEN_COLLECTOR);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.OXYGEN_COMPRESSOR), OXYGEN_COMPRESSOR);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.OXYGEN_DECOMPRESSOR), OXYGEN_DECOMPRESSOR);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.OXYGEN_SEALER), OXYGEN_SEALER);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.OXYGEN_BUBBLE_DISTRIBUTOR), OXYGEN_BUBBLE_DISTRIBUTOR);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.OXYGEN_STORAGE_MODULE), OXYGEN_STORAGE_MODULE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.FUEL_LOADER), FUEL_LOADER);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.PARACHEST), PARACHEST);

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.ROCKET_LAUNCH_PAD), LAUNCH_PAD);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.SOLAR_PANEL_PART), SOLAR_PANEL_PART);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.CRYOGENIC_CHAMBER_PART), CRYOGENIC_CHAMBER_PART);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.CRYOGENIC_CHAMBER), CRYOGENIC_CHAMBER);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.AIR_LOCK_CONTROLLER), AIRLOCK_CONTROLLER);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(Constant.Block.ROCKET_WORKBENCH), ROCKET_WORKBENCH);
    }

    private static void register(String id, BlockEntityType<?> type) {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constant.id(id), type);
    }
}