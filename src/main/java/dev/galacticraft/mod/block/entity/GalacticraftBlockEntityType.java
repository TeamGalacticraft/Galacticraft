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
import dev.galacticraft.mod.api.block.entity.WireBlockEntity;
import dev.galacticraft.mod.block.GalacticraftBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftBlockEntityType {
    // POWER GENERATION
    public static final BlockEntityType<CoalGeneratorBlockEntity> COAL_GENERATOR = FabricBlockEntityTypeBuilder.create(CoalGeneratorBlockEntity::new, GalacticraftBlock.COAL_GENERATOR).build();
    public static final BlockEntityType<BasicSolarPanelBlockEntity> BASIC_SOLAR_PANEL = FabricBlockEntityTypeBuilder.create(BasicSolarPanelBlockEntity::new, GalacticraftBlock.BASIC_SOLAR_PANEL).build();
    public static final BlockEntityType<AdvancedSolarPanelBlockEntity> ADVANCED_SOLAR_PANEL = FabricBlockEntityTypeBuilder.create(AdvancedSolarPanelBlockEntity::new, GalacticraftBlock.ADVANCED_SOLAR_PANEL).build();

    // WIRES, PIPES, WALKWAYS
    public static final BlockEntityType<WireBlockEntity> WIRE_T1 = FabricBlockEntityTypeBuilder.create((pos, state) -> WireBlockEntity.createT1(GalacticraftBlockEntityType.WIRE_T1, pos, state), GalacticraftBlock.ALUMINUM_WIRE, GalacticraftBlock.SEALABLE_ALUMINUM_WIRE, GalacticraftBlock.WIRE_WALKWAY).build();
    public static final BlockEntityType<WireBlockEntity> WIRE_T2 = FabricBlockEntityTypeBuilder.create((pos, state) -> WireBlockEntity.createT2(GalacticraftBlockEntityType.WIRE_T2, pos, state)).build();
    public static final BlockEntityType<GlassFluidPipeBlockEntity> GLASS_FLUID_PIPE = FabricBlockEntityTypeBuilder.create(GlassFluidPipeBlockEntity::new, GalacticraftBlock.GLASS_FLUID_PIPE).build();
    public static final BlockEntityType<WalkwayBlockEntity> WALKWAY = FabricBlockEntityTypeBuilder.create(WalkwayBlockEntity::new, GalacticraftBlock.WALKWAY).build();
    public static final BlockEntityType<WireWalkwayBlockEntity> WIRE_WALKWAY = FabricBlockEntityTypeBuilder.create(WireWalkwayBlockEntity::new, GalacticraftBlock.WIRE_WALKWAY).build();
    public static final BlockEntityType<PipeWalkwayBlockEntity> PIPE_WALKWAY = FabricBlockEntityTypeBuilder.create(PipeWalkwayBlockEntity::new, GalacticraftBlock.PIPE_WALKWAY).build();

    // MACHINES
    public static final BlockEntityType<CircuitFabricatorBlockEntity> CIRCUIT_FABRICATOR = FabricBlockEntityTypeBuilder.create(CircuitFabricatorBlockEntity::new, GalacticraftBlock.CIRCUIT_FABRICATOR).build();
    public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR = FabricBlockEntityTypeBuilder.create(CompressorBlockEntity::new, GalacticraftBlock.COMPRESSOR).build();
    public static final BlockEntityType<ElectricCompressorBlockEntity> ELECTRIC_COMPRESSOR = FabricBlockEntityTypeBuilder.create(ElectricCompressorBlockEntity::new, GalacticraftBlock.ELECTRIC_COMPRESSOR).build();
    public static final BlockEntityType<ElectricFurnaceBlockEntity> ELECTRIC_FURNACE = FabricBlockEntityTypeBuilder.create(ElectricFurnaceBlockEntity::new, GalacticraftBlock.ELECTRIC_FURNACE).build();
    public static final BlockEntityType<ElectricArcFurnaceBlockEntity> ELECTRIC_ARC_FURNACE = FabricBlockEntityTypeBuilder.create(ElectricArcFurnaceBlockEntity::new, GalacticraftBlock.ELECTRIC_ARC_FURNACE).build();
    public static final BlockEntityType<RefineryBlockEntity> REFINERY = FabricBlockEntityTypeBuilder.create(RefineryBlockEntity::new, GalacticraftBlock.REFINERY).build();

    // OXYGEN MACHINES
    public static final BlockEntityType<OxygenCollectorBlockEntity> OXYGEN_COLLECTOR = FabricBlockEntityTypeBuilder.create(OxygenCollectorBlockEntity::new, GalacticraftBlock.OXYGEN_COLLECTOR).build();
    public static final BlockEntityType<OxygenCompressorBlockEntity> OXYGEN_COMPRESSOR = FabricBlockEntityTypeBuilder.create(OxygenCompressorBlockEntity::new, GalacticraftBlock.OXYGEN_COMPRESSOR).build();
    public static final BlockEntityType<OxygenDecompressorBlockEntity> OXYGEN_DECOMPRESSOR = FabricBlockEntityTypeBuilder.create(OxygenDecompressorBlockEntity::new, GalacticraftBlock.OXYGEN_DECOMPRESSOR).build();
    public static final BlockEntityType<OxygenSealerBlockEntity> OXYGEN_SEALER = FabricBlockEntityTypeBuilder.create(OxygenSealerBlockEntity::new, GalacticraftBlock.OXYGEN_SEALER).build();
    public static final BlockEntityType<BubbleDistributorBlockEntity> OXYGEN_BUBBLE_DISTRIBUTOR = FabricBlockEntityTypeBuilder.create(BubbleDistributorBlockEntity::new, GalacticraftBlock.BUBBLE_DISTRIBUTOR).build();

    // RESOURCE STORAGE
    public static final BlockEntityType<EnergyStorageModuleBlockEntity> ENERGY_STORAGE_MODULE = FabricBlockEntityTypeBuilder.create(EnergyStorageModuleBlockEntity::new, GalacticraftBlock.ENERGY_STORAGE_MODULE).build();
    public static final BlockEntityType<OxygenStorageModuleBlockEntity> OXYGEN_STORAGE_MODULE = FabricBlockEntityTypeBuilder.create(OxygenStorageModuleBlockEntity::new, GalacticraftBlock.OXYGEN_STORAGE_MODULE).build();

    // ROCKETS

    // MISC
    public static final BlockEntityType<SolarPanelPartBlockEntity> SOLAR_PANEL_PART = FabricBlockEntityTypeBuilder.create(SolarPanelPartBlockEntity::new, GalacticraftBlock.SOLAR_PANEL_PART).build();

    public static void register() {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.COAL_GENERATOR), COAL_GENERATOR);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.BASIC_SOLAR_PANEL), BASIC_SOLAR_PANEL);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.ADVANCED_SOLAR_PANEL), ADVANCED_SOLAR_PANEL);

        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.WIRE_T1), WIRE_T1);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.WIRE_T2), WIRE_T2);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.GLASS_FLUID_PIPE), GLASS_FLUID_PIPE);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.WALKWAY), WALKWAY);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.WIRE_WALKWAY), WIRE_WALKWAY);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.PIPE_WALKWAY), PIPE_WALKWAY);

        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.CIRCUIT_FABRICATOR), CIRCUIT_FABRICATOR);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.COMPRESSOR), COMPRESSOR);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.ELECTRIC_COMPRESSOR), ELECTRIC_COMPRESSOR);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.ELECTRIC_FURNACE), ELECTRIC_FURNACE);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.ELECTRIC_ARC_FURNACE), ELECTRIC_ARC_FURNACE);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.REFINERY), REFINERY);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.ENERGY_STORAGE_MODULE), ENERGY_STORAGE_MODULE);

        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.OXYGEN_COLLECTOR), OXYGEN_COLLECTOR);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.OXYGEN_COMPRESSOR), OXYGEN_COMPRESSOR);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.OXYGEN_DECOMPRESSOR), OXYGEN_DECOMPRESSOR);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.OXYGEN_SEALER), OXYGEN_SEALER);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.OXYGEN_BUBBLE_DISTRIBUTOR), OXYGEN_BUBBLE_DISTRIBUTOR);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.OXYGEN_STORAGE_MODULE), OXYGEN_STORAGE_MODULE);

        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, Constant.Block.SOLAR_PANEL_PART), SOLAR_PANEL_PART);
    }

    private static void register(String id, BlockEntityType<?> type) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Constant.MOD_ID, id), type);
    }
}