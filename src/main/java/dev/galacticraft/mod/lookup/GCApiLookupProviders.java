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

package dev.galacticraft.mod.lookup;

import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.item.SingleVariantFixedItemBackedFluidStorage;
import dev.galacticraft.mod.api.pipe.FluidPipe;
import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.content.item.OxygenTankItem;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.world.level.block.entity.BlockEntityType;
import team.reborn.energy.api.EnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public class GCApiLookupProviders {
    private static final BlockEntityType<? extends MachineBlockEntity>[] MACHINE_BLOCKS = new BlockEntityType[]{
            GCBlockEntityTypes.COAL_GENERATOR,
            GCBlockEntityTypes.BASIC_SOLAR_PANEL,
            GCBlockEntityTypes.ADVANCED_SOLAR_PANEL,
            GCBlockEntityTypes.CIRCUIT_FABRICATOR,
            GCBlockEntityTypes.COMPRESSOR,
            GCBlockEntityTypes.ELECTRIC_COMPRESSOR,
            GCBlockEntityTypes.ELECTRIC_FURNACE,
            GCBlockEntityTypes.ELECTRIC_ARC_FURNACE,
            GCBlockEntityTypes.REFINERY,
            GCBlockEntityTypes.FUEL_LOADER,
            GCBlockEntityTypes.OXYGEN_COLLECTOR,
            GCBlockEntityTypes.OXYGEN_COMPRESSOR,
            GCBlockEntityTypes.OXYGEN_DECOMPRESSOR,
            GCBlockEntityTypes.OXYGEN_SEALER,
            GCBlockEntityTypes.OXYGEN_BUBBLE_DISTRIBUTOR,
            GCBlockEntityTypes.ENERGY_STORAGE_MODULE,
            GCBlockEntityTypes.FOOD_CANNER,
            GCBlockEntityTypes.OXYGEN_STORAGE_MODULE
    };
    @SuppressWarnings("rawtypes")
    private static final BlockEntityType[] WIRE_TYPES = new BlockEntityType[]{
            GCBlockEntityTypes.WIRE_T1,
//            GCBlocksEntityType.WIRE_T2,
    };

    @SuppressWarnings("rawtypes")
    private static final BlockEntityType[] PIPE_TYPES = new BlockEntityType[]{
            GCBlockEntityTypes.GLASS_FLUID_PIPE
    };

    public static void register() {
        MachineBlockEntity.registerProviders(MACHINE_BLOCKS);

        FluidStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            if (direction == null || !((FluidPipe) blockEntity).canConnect(direction)) return null;
            return ((FluidPipe) blockEntity).getInsertable();
        }, PIPE_TYPES);

        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            if (direction == null || !((Wire) blockEntity).canConnect(direction)) return null;
            return ((Wire) blockEntity).getInsertable();
        }, WIRE_TYPES);

        FluidStorage.ITEM.registerForItems((itemStack, context) -> {
            long capacity = ((OxygenTankItem) itemStack.getItem()).capacity;
            return new SingleVariantFixedItemBackedFluidStorage(itemStack, context, capacity / 100, capacity / 100, capacity, FluidVariant.of(Gases.OXYGEN));
        }, GCItems.SMALL_OXYGEN_TANK, GCItems.MEDIUM_OXYGEN_TANK, GCItems.LARGE_OXYGEN_TANK);
        FluidStorage.ITEM.registerSelf(GCItems.INFINITE_OXYGEN_TANK);
    }
}
