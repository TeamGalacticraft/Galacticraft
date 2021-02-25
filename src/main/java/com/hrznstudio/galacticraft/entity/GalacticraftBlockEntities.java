/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.FluidPipe;
import com.hrznstudio.galacticraft.api.block.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.block.entity.*;
import com.hrznstudio.galacticraft.block.special.fluidpipe.FluidPipeBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftBlockEntities {
    public static final BlockEntityType<CoalGeneratorBlockEntity> COAL_GENERATOR_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.COAL_GENERATOR), BlockEntityType.Builder.create(CoalGeneratorBlockEntity::new, GalacticraftBlocks.COAL_GENERATOR).build(null));
    public static final BlockEntityType<BasicSolarPanelBlockEntity> BASIC_SOLAR_PANEL_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.BASIC_SOLAR_PANEL), BlockEntityType.Builder.create(BasicSolarPanelBlockEntity::new, GalacticraftBlocks.BASIC_SOLAR_PANEL).build(null));
    public static final BlockEntityType<SolarPanelPartBlockEntity> SOLAR_PANEL_PART_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.SOLAR_PANEL_PART), BlockEntityType.Builder.create(SolarPanelPartBlockEntity::new, GalacticraftBlocks.SOLAR_PANEL_PART).build(null));
    public static final BlockEntityType<CircuitFabricatorBlockEntity> CIRCUIT_FABRICATOR_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.CIRCUIT_FABRICATOR), BlockEntityType.Builder.create(CircuitFabricatorBlockEntity::new, GalacticraftBlocks.CIRCUIT_FABRICATOR).build(null));
    public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.COMPRESSOR), BlockEntityType.Builder.create(CompressorBlockEntity::new, GalacticraftBlocks.COMPRESSOR).build(null));
    public static final BlockEntityType<ElectricCompressorBlockEntity> ELECTRIC_COMPRESSOR_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.ELECTRIC_COMPRESSOR), BlockEntityType.Builder.create(ElectricCompressorBlockEntity::new, GalacticraftBlocks.ELECTRIC_COMPRESSOR).build(null));
    public static final BlockEntityType<EnergyStorageModuleBlockEntity> ENERGY_STORAGE_MODULE_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.ENERGY_STORAGE_MODULE), BlockEntityType.Builder.create(EnergyStorageModuleBlockEntity::new, GalacticraftBlocks.ENERGY_STORAGE_MODULE).build(null));
    public static final BlockEntityType<RefineryBlockEntity> REFINERY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.REFINERY), BlockEntityType.Builder.create(RefineryBlockEntity::new, GalacticraftBlocks.REFINERY).build(null));
    public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.ELECTRIC_FURNACE), BlockEntityType.Builder.create(ElectricFurnaceBlockEntity::new, GalacticraftBlocks.ELECTRIC_FURNACE).build(null));
    public static final BlockEntityType<ElectricArcFurnaceBlockEntity> ELECTRIC_ARC_FURNACE_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.ELECTRIC_ARC_FURNACE), BlockEntityType.Builder.create(ElectricArcFurnaceBlockEntity::new, GalacticraftBlocks.ELECTRIC_ARC_FURNACE).build(null));
    public static final BlockEntityType<OxygenCollectorBlockEntity> OXYGEN_COLLECTOR_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.OXYGEN_COLLECTOR), BlockEntityType.Builder.create(OxygenCollectorBlockEntity::new, GalacticraftBlocks.OXYGEN_COLLECTOR).build(null));
    public static final BlockEntityType<OxygenSealerBlockEntity> OXYGEN_SEALER_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.OXYGEN_SEALER), BlockEntityType.Builder.create(OxygenSealerBlockEntity::new, GalacticraftBlocks.OXYGEN_SEALER).build(null));
    public static final BlockEntityType<WireBlockEntity> WIRE_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.ALUMINUM_WIRE), BlockEntityType.Builder.create(WireBlockEntity::new, GalacticraftBlocks.ALUMINUM_WIRE, GalacticraftBlocks.SEALABLE_ALUMINUM_WIRE).build(null));
    public static final BlockEntityType<FluidPipeBlockEntity> FLUID_PIPE_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.FLUID_PIPE), BlockEntityType.Builder.create(FluidPipeBlockEntity::new, new Block(FabricBlockSettings.of(Material.AIR)) {
        @Override
        public boolean equals(Object obj) {
            return obj instanceof FluidPipe; //bad stuff
        }
    }).build(null));
    public static final BlockEntityType<FluidPipeBlockEntity> GLASS_FLUID_PIPE_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.GLASS_FLUID_PIPE), BlockEntityType.Builder.create(FluidPipeBlockEntity::new, GalacticraftBlocks.GLASS_FLUID_PIPE).build(null));
    public static final BlockEntityType<AdvancedSolarPanelBlockEntity> ADVANCED_SOLAR_PANEL_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.ADVANCED_SOLAR_PANEL), BlockEntityType.Builder.create(AdvancedSolarPanelBlockEntity::new, GalacticraftBlocks.ADVANCED_SOLAR_PANEL).build(null));
    public static final BlockEntityType<BubbleDistributorBlockEntity> BUBBLE_DISTRIBUTOR_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.OXYGEN_BUBBLE_DISTRIBUTOR), BlockEntityType.Builder.create(BubbleDistributorBlockEntity::new, GalacticraftBlocks.BUBBLE_DISTRIBUTOR).build(null));
    public static final BlockEntityType<OxygenCompressorBlockEntity> OXYGEN_COMPRESSOR_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.OXYGEN_COMPRESSOR), BlockEntityType.Builder.create(OxygenCompressorBlockEntity::new, GalacticraftBlocks.OXYGEN_COMPRESSOR).build(null));
    public static final BlockEntityType<OxygenDecompressorBlockEntity> OXYGEN_DECOMPRESSOR_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.OXYGEN_DECOMPRESSOR), BlockEntityType.Builder.create(OxygenDecompressorBlockEntity::new, GalacticraftBlocks.OXYGEN_DECOMPRESSOR).build(null));
    public static final BlockEntityType<OxygenStorageModuleBlockEntity> OXYGEN_STORAGE_MODULE_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constants.MOD_ID, Constants.Blocks.OXYGEN_STORAGE_MODULE), BlockEntityType.Builder.create(OxygenStorageModuleBlockEntity::new, GalacticraftBlocks.OXYGEN_STORAGE_MODULE).build(null));

    public static void init() {
    }
}