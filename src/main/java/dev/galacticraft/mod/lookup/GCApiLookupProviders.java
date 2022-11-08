/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.lookup;

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.gas.Gases;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.WireBlockEntity;
import dev.galacticraft.mod.block.GCBlocks;
import dev.galacticraft.mod.block.entity.GCBlockEntityTypes;
import dev.galacticraft.mod.block.special.fluidpipe.PipeBlockEntity;
import dev.galacticraft.mod.item.GCItem;
import dev.galacticraft.mod.item.OxygenTankItem;
import dev.galacticraft.mod.storage.SingleTypeStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import team.reborn.energy.api.EnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public class GCApiLookupProviders {
    @SuppressWarnings("rawtypes")
    private static final Block[] MACHINE_BLOCKS = new Block[]{
            GCBlocks.COAL_GENERATOR,
            GCBlocks.BASIC_SOLAR_PANEL,
            GCBlocks.ADVANCED_SOLAR_PANEL,
            GCBlocks.CIRCUIT_FABRICATOR,
            GCBlocks.COMPRESSOR,
            GCBlocks.ELECTRIC_COMPRESSOR,
            GCBlocks.ELECTRIC_FURNACE,
            GCBlocks.ELECTRIC_ARC_FURNACE,
            GCBlocks.REFINERY,
            GCBlocks.OXYGEN_COLLECTOR,
            GCBlocks.OXYGEN_COMPRESSOR,
            GCBlocks.OXYGEN_DECOMPRESSOR,
            GCBlocks.OXYGEN_SEALER,
            GCBlocks.BUBBLE_DISTRIBUTOR,
            GCBlocks.ENERGY_STORAGE_MODULE,
            GCBlocks.OXYGEN_STORAGE_MODULE
    };
    @SuppressWarnings("rawtypes")
    private static final BlockEntityType[] WIRE_TYPES = new BlockEntityType[]{
            GCBlockEntityTypes.WIRE_T1,
//            GCBlocksEntityType.WIRE_T2,
            GCBlockEntityTypes.WIRE_WALKWAY
    };

    @SuppressWarnings("rawtypes")
    private static final BlockEntityType[] PIPE_TYPES = new BlockEntityType[]{
            GCBlockEntityTypes.GLASS_FLUID_PIPE,
            GCBlockEntityTypes.PIPE_WALKWAY
    };

    public static void register() {
        MachineBlockEntity.registerComponents(MACHINE_BLOCKS);

        FluidStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            assert blockEntity instanceof PipeBlockEntity;
            return ((PipeBlockEntity) blockEntity).getInsertables()[direction.ordinal()];
        }, PIPE_TYPES);

        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            assert blockEntity instanceof WireBlockEntity;
            return ((WireBlockEntity) blockEntity).getInsertables()[direction.ordinal()];
        }, WIRE_TYPES);

        FluidStorage.ITEM.registerForItems((itemStack, context) -> {
            long amount = itemStack.getTag() != null ? itemStack.getTag().getLong(Constant.Nbt.VALUE) : 0;
            return new SingleTypeStorage<>(FluidVariant.of(Gases.OXYGEN), ((OxygenTankItem) itemStack.getItem()).capacity, FluidVariant.blank(), amount) {
                @Override
                protected void onFinalCommit() {
                    super.onFinalCommit();
                    itemStack.getOrCreateTag().putLong(Constant.Nbt.VALUE, this.getAmount());
                }
            };
        }, GCItem.SMALL_OXYGEN_TANK, GCItem.MEDIUM_OXYGEN_TANK, GCItem.LARGE_OXYGEN_TANK);
        FluidStorage.ITEM.registerSelf(GCItem.INFINITE_OXYGEN_TANK);
    }
}
