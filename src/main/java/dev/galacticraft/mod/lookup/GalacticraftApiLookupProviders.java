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

package dev.galacticraft.mod.lookup;

import dev.galacticraft.api.attribute.GasStorage;
import dev.galacticraft.api.gas.Gas;
import dev.galacticraft.impl.attribute.SingleTypeSlotGasStorage;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.block.entity.WireBlockEntity;
import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.block.entity.GalacticraftBlockEntityType;
import dev.galacticraft.mod.block.special.fluidpipe.PipeBlockEntity;
import dev.galacticraft.mod.item.GalacticraftItem;
import dev.galacticraft.mod.item.OxygenTankItem;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.entity.BlockEntityType;
import team.reborn.energy.api.EnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public class GalacticraftApiLookupProviders {
    @SuppressWarnings("rawtypes")
    private static final BlockEntityType[] MACHINE_TYPES = new BlockEntityType[]{
            GalacticraftBlockEntityType.COAL_GENERATOR
    };
    @SuppressWarnings("rawtypes")
    private static final BlockEntityType[] WIRE_TYPES = new BlockEntityType[]{
            GalacticraftBlockEntityType.WIRE_T1,
//            GalacticraftBlockEntityType.WIRE_T2,
            GalacticraftBlockEntityType.WIRE_WALKWAY
    };

    @SuppressWarnings("rawtypes")
    private static final BlockEntityType[] PIPE_TYPES = new BlockEntityType[]{
            GalacticraftBlockEntityType.GLASS_FLUID_PIPE,
            GalacticraftBlockEntityType.PIPE_WALKWAY
    };

    public static void register() {
        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            assert blockEntity instanceof MachineBlockEntity;
            return ((MachineBlockEntity) blockEntity).getExposedCapacitor(direction);
        }, MACHINE_TYPES);

        ItemStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            assert blockEntity instanceof MachineBlockEntity;
            return ((MachineBlockEntity) blockEntity).getExposedItemStorage(direction);
        }, MACHINE_TYPES);

        FluidStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            assert blockEntity instanceof MachineBlockEntity;
            return ((MachineBlockEntity) blockEntity).getExposedFluidInv(direction);
        }, MACHINE_TYPES);

        FluidStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            assert blockEntity instanceof PipeBlockEntity;
            return ((PipeBlockEntity) blockEntity).getInsertables()[direction.ordinal()];
        }, PIPE_TYPES);

        GasStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            assert blockEntity instanceof MachineBlockEntity;
            return ((MachineBlockEntity) blockEntity).getExposedGasInv(direction);
        }, MACHINE_TYPES);

        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            assert blockEntity instanceof WireBlockEntity;
            return ((WireBlockEntity) blockEntity).getInsertables()[direction.ordinal()];
        }, WIRE_TYPES);

        GasStorage.ITEM.registerForItems((itemStack, context) -> {
            long amount = itemStack.getNbt() != null ? itemStack.getNbt().getLong(Constant.Nbt.VALUE) : 0;
            return new SingleTypeSlotGasStorage(((OxygenTankItem) itemStack.getItem()).capacity, Gas.OXYGEN, amount) {
                @Override
                protected void onFinalCommit() {
                    super.onFinalCommit();
                    itemStack.getOrCreateNbt().putLong(Constant.Nbt.VALUE, this.getAmount());
                }
            };
        }, GalacticraftItem.SMALL_OXYGEN_TANK, GalacticraftItem.MEDIUM_OXYGEN_TANK, GalacticraftItem.LARGE_OXYGEN_TANK);
    }
}
