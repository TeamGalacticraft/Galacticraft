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

package dev.galacticraft.mod.block.entity;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.FluidPipe;
import dev.galacticraft.mod.api.block.entity.WireBlockEntity;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.block.special.fluidpipe.PipeBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftBlockEntityType {
    public static final BlockEntityType<CoalGeneratorBlockEntity> COAL_GENERATOR = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.COAL_GENERATOR), BlockEntityType.Builder.create(CoalGeneratorBlockEntity::new, GalacticraftBlock.COAL_GENERATOR).build(null));
    public static final BlockEntityType<BasicSolarPanelBlockEntity> BASIC_SOLAR_PANEL = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.BASIC_SOLAR_PANEL), BlockEntityType.Builder.create(BasicSolarPanelBlockEntity::new, GalacticraftBlock.BASIC_SOLAR_PANEL).build(null));
    public static final BlockEntityType<SolarPanelPartBlockEntity> SOLAR_PANEL_PART = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.SOLAR_PANEL_PART), BlockEntityType.Builder.create(SolarPanelPartBlockEntity::new, GalacticraftBlock.SOLAR_PANEL_PART).build(null));
    public static final BlockEntityType<CircuitFabricatorBlockEntity> CIRCUIT_FABRICATOR = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.CIRCUIT_FABRICATOR), BlockEntityType.Builder.create(CircuitFabricatorBlockEntity::new, GalacticraftBlock.CIRCUIT_FABRICATOR).build(null));
    public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.COMPRESSOR), BlockEntityType.Builder.create(CompressorBlockEntity::new, GalacticraftBlock.COMPRESSOR).build(null));
    public static final BlockEntityType<ElectricCompressorBlockEntity> ELECTRIC_COMPRESSOR = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.ELECTRIC_COMPRESSOR), BlockEntityType.Builder.create(ElectricCompressorBlockEntity::new, GalacticraftBlock.ELECTRIC_COMPRESSOR).build(null));
    public static final BlockEntityType<EnergyStorageModuleBlockEntity> ENERGY_STORAGE_MODULE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.ENERGY_STORAGE_MODULE), BlockEntityType.Builder.create(EnergyStorageModuleBlockEntity::new, GalacticraftBlock.ENERGY_STORAGE_MODULE).build(null));
    public static final BlockEntityType<RefineryBlockEntity> REFINERY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.REFINERY), BlockEntityType.Builder.create(RefineryBlockEntity::new, GalacticraftBlock.REFINERY).build(null));
    public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.ELECTRIC_FURNACE), BlockEntityType.Builder.create(ElectricFurnaceBlockEntity::new, GalacticraftBlock.ELECTRIC_FURNACE).build(null));
    public static final BlockEntityType<ElectricArcFurnaceBlockEntity> ELECTRIC_ARC_FURNACE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.ELECTRIC_ARC_FURNACE), BlockEntityType.Builder.create(ElectricArcFurnaceBlockEntity::new, GalacticraftBlock.ELECTRIC_ARC_FURNACE).build(null));
    public static final BlockEntityType<OxygenCollectorBlockEntity> OXYGEN_COLLECTOR = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.OXYGEN_COLLECTOR), BlockEntityType.Builder.create(OxygenCollectorBlockEntity::new, GalacticraftBlock.OXYGEN_COLLECTOR).build(null));
    public static final BlockEntityType<OxygenSealerBlockEntity> OXYGEN_SEALER = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.OXYGEN_SEALER), BlockEntityType.Builder.create(OxygenSealerBlockEntity::new, GalacticraftBlock.OXYGEN_SEALER).build(null));
    public static final BlockEntityType<WireBlockEntity> WIRE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.ALUMINUM_WIRE), BlockEntityType.Builder.create(WireBlockEntity::new, GalacticraftBlock.ALUMINUM_WIRE, GalacticraftBlock.SEALABLE_ALUMINUM_WIRE).build(null));
    public static final BlockEntityType<PipeBlockEntity> FLUID_PIPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.FLUID_PIPE), BlockEntityType.Builder.create(PipeBlockEntity::new, new net.minecraft.block.Block(FabricBlockSettings.of(Material.AIR)) {
        @Override
        public boolean equals(Object obj) {
            return obj instanceof FluidPipe; //bad stuff
        }
    }).build(null));
    public static final BlockEntityType<PipeBlockEntity> GLASS_FLUID_PIPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.GLASS_FLUID_PIPE), BlockEntityType.Builder.create(PipeBlockEntity::new, GalacticraftBlock.GLASS_FLUID_PIPE).build(null));
    public static final BlockEntityType<AdvancedSolarPanelBlockEntity> ADVANCED_SOLAR_PANEL = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.ADVANCED_SOLAR_PANEL), BlockEntityType.Builder.create(AdvancedSolarPanelBlockEntity::new, GalacticraftBlock.ADVANCED_SOLAR_PANEL).build(null));
    public static final BlockEntityType<BubbleDistributorBlockEntity> BUBBLE_DISTRIBUTOR = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.OXYGEN_BUBBLE_DISTRIBUTOR), BlockEntityType.Builder.create(BubbleDistributorBlockEntity::new, GalacticraftBlock.BUBBLE_DISTRIBUTOR).build(null));
    public static final BlockEntityType<OxygenCompressorBlockEntity> OXYGEN_COMPRESSOR = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.OXYGEN_COMPRESSOR), BlockEntityType.Builder.create(OxygenCompressorBlockEntity::new, GalacticraftBlock.OXYGEN_COMPRESSOR).build(null));
    public static final BlockEntityType<OxygenDecompressorBlockEntity> OXYGEN_DECOMPRESSOR = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.OXYGEN_DECOMPRESSOR), BlockEntityType.Builder.create(OxygenDecompressorBlockEntity::new, GalacticraftBlock.OXYGEN_DECOMPRESSOR).build(null));
    public static final BlockEntityType<OxygenStorageModuleBlockEntity> OXYGEN_STORAGE_MODULE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.OXYGEN_STORAGE_MODULE), BlockEntityType.Builder.create(OxygenStorageModuleBlockEntity::new, GalacticraftBlock.OXYGEN_STORAGE_MODULE).build(null));
    public static final BlockEntityType<FallenMeteorBlockEntity> FALLEN_METEOR = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.FALLEN_METEOR), BlockEntityType.Builder.create(FallenMeteorBlockEntity::new, GalacticraftBlock.FALLEN_METEOR).build(null));

    public static void register() {
    }
}