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
import dev.galacticraft.mod.content.block.entity.decoration.CannedFoodBlockEntity;
import dev.galacticraft.mod.content.block.entity.machine.*;
import dev.galacticraft.mod.content.block.entity.networked.PipeBlockEntity;
import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import dev.galacticraft.mod.content.block.special.launchpad.LaunchPadBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GCBlockEntityTypes {
    public static final GCRegistry<BlockEntityType<?>> BLOCK_ENTITIES = new GCRegistry<>(BuiltInRegistries.BLOCK_ENTITY_TYPE);
    // POWER GENERATION
    public static final BlockEntityType<CoalGeneratorBlockEntity> COAL_GENERATOR = register(Constant.Block.COAL_GENERATOR, CoalGeneratorBlockEntity::new, GCBlocks.COAL_GENERATOR);
    public static final BlockEntityType<BasicSolarPanelBlockEntity> BASIC_SOLAR_PANEL = register(Constant.Block.BASIC_SOLAR_PANEL, BasicSolarPanelBlockEntity::new, GCBlocks.BASIC_SOLAR_PANEL);
    public static final BlockEntityType<AdvancedSolarPanelBlockEntity> ADVANCED_SOLAR_PANEL = register(Constant.Block.ADVANCED_SOLAR_PANEL, AdvancedSolarPanelBlockEntity::new, GCBlocks.ADVANCED_SOLAR_PANEL);

    // WIRES, PIPES, WALKWAYS
    public static final BlockEntityType<WireBlockEntity> WIRE = register(Constant.Block.ALUMINUM_WIRE, WireBlockEntity::new, GCBlocks.ALUMINUM_WIRE, GCBlocks.SEALABLE_ALUMINUM_WIRE, GCBlocks.WIRE_WALKWAY, /*GCBlocks.HEAVY_ALUMINUM_WIRE,*/ GCBlocks.HEAVY_SEALABLE_ALUMINUM_WIRE);
    public static final BlockEntityType<PipeBlockEntity> FLUID_PIPE = register(Constant.Block.GLASS_FLUID_PIPE, PipeBlockEntity::new, PipeBlockEntity.COMPATIBLE_BLOCKS.toArray(new Block[0]));

    // MACHINES
    public static final BlockEntityType<CircuitFabricatorBlockEntity> CIRCUIT_FABRICATOR = register(Constant.Block.CIRCUIT_FABRICATOR, CircuitFabricatorBlockEntity::new, GCBlocks.CIRCUIT_FABRICATOR);
    public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR = register(Constant.Block.COMPRESSOR, CompressorBlockEntity::new, GCBlocks.COMPRESSOR);
    public static final BlockEntityType<ElectricCompressorBlockEntity> ELECTRIC_COMPRESSOR = register(Constant.Block.ELECTRIC_COMPRESSOR, ElectricCompressorBlockEntity::new, GCBlocks.ELECTRIC_COMPRESSOR);
    public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = register(Constant.Block.ELECTRIC_FURNACE, ElectricFurnaceBlockEntity::new, GCBlocks.ELECTRIC_FURNACE);
    public static final BlockEntityType<ElectricArcFurnaceBlockEntity> ELECTRIC_ARC_FURNACE = register(Constant.Block.ELECTRIC_ARC_FURNACE, ElectricArcFurnaceBlockEntity::new, GCBlocks.ELECTRIC_ARC_FURNACE);
    public static final BlockEntityType<RefineryBlockEntity> REFINERY = register(Constant.Block.REFINERY, RefineryBlockEntity::new, GCBlocks.REFINERY);
    public static final BlockEntityType<FoodCannerBlockEntity> FOOD_CANNER = register(Constant.Block.FOOD_CANNER, FoodCannerBlockEntity::new, GCBlocks.FOOD_CANNER);

    // OXYGEN MACHINES
    public static final BlockEntityType<OxygenCollectorBlockEntity> OXYGEN_COLLECTOR = register(Constant.Block.OXYGEN_COLLECTOR, OxygenCollectorBlockEntity::new, GCBlocks.OXYGEN_COLLECTOR);
    public static final BlockEntityType<OxygenCompressorBlockEntity> OXYGEN_COMPRESSOR = register(Constant.Block.OXYGEN_COMPRESSOR, OxygenCompressorBlockEntity::new, GCBlocks.OXYGEN_COMPRESSOR);
    public static final BlockEntityType<OxygenDecompressorBlockEntity> OXYGEN_DECOMPRESSOR = register(Constant.Block.OXYGEN_DECOMPRESSOR, OxygenDecompressorBlockEntity::new, GCBlocks.OXYGEN_DECOMPRESSOR);
    public static final BlockEntityType<OxygenSealerBlockEntity> OXYGEN_SEALER = register(Constant.Block.OXYGEN_SEALER, OxygenSealerBlockEntity::new, GCBlocks.OXYGEN_SEALER);
    public static final BlockEntityType<OxygenBubbleDistributorBlockEntity> OXYGEN_BUBBLE_DISTRIBUTOR = register(Constant.Block.OXYGEN_BUBBLE_DISTRIBUTOR, OxygenBubbleDistributorBlockEntity::new, GCBlocks.OXYGEN_BUBBLE_DISTRIBUTOR);

    // RESOURCE STORAGE
    public static final BlockEntityType<EnergyStorageModuleBlockEntity> ENERGY_STORAGE_MODULE = register(Constant.Block.ENERGY_STORAGE_MODULE, EnergyStorageModuleBlockEntity::new, GCBlocks.ENERGY_STORAGE_MODULE);
    public static final BlockEntityType<OxygenStorageModuleBlockEntity> OXYGEN_STORAGE_MODULE = register(Constant.Block.OXYGEN_STORAGE_MODULE, OxygenStorageModuleBlockEntity::new, GCBlocks.OXYGEN_STORAGE_MODULE);

    // ROCKETS
    public static final BlockEntityType<LaunchPadBlockEntity> LAUNCH_PAD = register(Constant.Block.ROCKET_LAUNCH_PAD, LaunchPadBlockEntity::new, GCBlocks.ROCKET_LAUNCH_PAD, GCBlocks.FUELING_PAD);
    public static final BlockEntityType<FuelLoaderBlockEntity> FUEL_LOADER = register(Constant.Block.FUEL_LOADER, FuelLoaderBlockEntity::new, GCBlocks.FUEL_LOADER);
    public static final BlockEntityType<ParachestBlockEntity> PARACHEST = register(Constant.Block.PARACHEST, ParachestBlockEntity::new, GCBlocks.PARACHEST);

    // MISC
    public static final BlockEntityType<SolarPanelPartBlockEntity> SOLAR_PANEL_PART = register(Constant.Block.SOLAR_PANEL_PART, SolarPanelPartBlockEntity::new, GCBlocks.SOLAR_PANEL_PART);
    public static final BlockEntityType<CryogenicChamberBlockEntity> CRYOGENIC_CHAMBER = register(Constant.Block.CRYOGENIC_CHAMBER, CryogenicChamberBlockEntity::new, GCBlocks.CRYOGENIC_CHAMBER);
    public static final BlockEntityType<CryogenicChamberPartBlockEntity> CRYOGENIC_CHAMBER_PART = register(Constant.Block.CRYOGENIC_CHAMBER_PART, CryogenicChamberPartBlockEntity::new, GCBlocks.CRYOGENIC_CHAMBER_PART);
    public static final BlockEntityType<DungeonSpawnerBlockEntity> DUNGEON_BOSS_SPAWNER = register(Constant.Block.BOSS_SPAWNER, DungeonSpawnerBlockEntity::new, GCBlocks.BOSS_SPAWNER);

    // DECORATION
    public static final BlockEntityType<CannedFoodBlockEntity> CANNED_FOOD = register(Constant.Block.CANNED_FOOD, CannedFoodBlockEntity::new, GCBlocks.CANNED_FOOD);

    public static final BlockEntityType<AirlockControllerBlockEntity> AIRLOCK_CONTROLLER = register(Constant.Block.AIR_LOCK_CONTROLLER, AirlockControllerBlockEntity::new, GCBlocks.AIR_LOCK_CONTROLLER);
    public static final BlockEntityType<RocketWorkbenchBlockEntity> ROCKET_WORKBENCH = register(Constant.Block.ROCKET_WORKBENCH, RocketWorkbenchBlockEntity::new, GCBlocks.ROCKET_WORKBENCH);

    private static<T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.BlockEntitySupplier<T> supplier, Block... compatibleBlocks) {
        return BLOCK_ENTITIES.register(id, BlockEntityType.Builder.of(supplier, compatibleBlocks).build());
    }
}