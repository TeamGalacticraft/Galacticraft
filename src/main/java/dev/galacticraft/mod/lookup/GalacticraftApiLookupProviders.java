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

import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.block.entity.WireBlockEntity;
import dev.galacticraft.mod.block.entity.GalacticraftBlockEntityType;
import net.minecraft.block.entity.BlockEntityType;
import team.reborn.energy.api.EnergyStorage;

public class GalacticraftApiLookupProviders {
    @SuppressWarnings("rawtypes")
    private static final BlockEntityType[] MACHINE_TYPES = new BlockEntityType[]{
            GalacticraftBlockEntityType.COAL_GENERATOR
    };
    @SuppressWarnings("rawtypes")
    private static final BlockEntityType[] WIRE_TYPES = new BlockEntityType[]{
            GalacticraftBlockEntityType.WIRE_T1,
            GalacticraftBlockEntityType.WIRE_T2,
            GalacticraftBlockEntityType.WIRE_WALKWAY
    };

    public static void register() {
        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            assert blockEntity instanceof MachineBlockEntity;
            return ((MachineBlockEntity) blockEntity).getExposedCapacitor(direction);
        }, MACHINE_TYPES);

        EnergyStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            assert blockEntity instanceof WireBlockEntity;
            return ((WireBlockEntity) blockEntity).getInsertables()[direction.ordinal()];
        }, WIRE_TYPES);

//        FluidStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
//            assert blockEntity instanceof MachineBlockEntity;
//            return ((MachineBlockEntity) blockEntity).getExposedFluidInv(direction);
//        }, MACHINE_TYPES);
//
//        ItemStorage.SIDED.registerForBlockEntities((blockEntity, direction) -> {
//            assert blockEntity instanceof MachineBlockEntity;
//            return ((MachineBlockEntity) blockEntity).getExposedItemInv(direction);
//        }, MACHINE_TYPES);
    }
}
