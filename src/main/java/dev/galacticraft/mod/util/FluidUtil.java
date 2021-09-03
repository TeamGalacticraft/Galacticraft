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

package dev.galacticraft.mod.util;

import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.EmptyFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidExtractable;
import alexiil.mc.lib.attributes.fluid.impl.RejectingFluidInsertable;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.misc.Ref;
import alexiil.mc.lib.attributes.misc.Reference;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class FluidUtil {
    private FluidUtil() {}

    public static FluidVolume extractFluid(Reference<ItemStack> stackRef, FluidAmount maxAmount, FluidFilter filter) {
        return extractFluid(stackRef, maxAmount, filter, Simulation.ACTION);
    }

    public static FluidVolume insertFluid(Reference<ItemStack> stackRef, FluidVolume volume) {
        return insertFluid(stackRef, volume, Simulation.ACTION);
    }

    public static FluidVolume extractFluid(Reference<ItemStack> stackRef, FluidAmount maxAmount, FluidFilter filter, Simulation simulation) {
        return FluidAttributes.EXTRACTABLE.get(stackRef).attemptExtraction(filter, maxAmount, simulation);
    }

    public static FluidVolume insertFluid(Reference<ItemStack> stackRef, FluidVolume volume, Simulation simulation) {
        return FluidAttributes.INSERTABLE.get(stackRef).attemptInsertion(volume, simulation);
    }

    public static boolean canInsertFluids(Reference<ItemStack> stack) {
        return FluidAttributes.INSERTABLE.get(stack) != RejectingFluidInsertable.NULL;
    }

    public static boolean canInsertFluids(ItemStack stack) {
        return canInsertFluids(new Ref<>(stack));
    }

    public static boolean canInsertFluids(Reference<ItemStack> stack, FluidKey key) {
        return FluidAttributes.INSERTABLE.get(stack).getInsertionFilter().matches(key);
    }

    public static boolean canInsertFluids(ItemStack stack, FluidKey key) {
        return canInsertFluids(new Ref<>(stack), key);
    }
    
    public static boolean canInsertFluids(Reference<ItemStack> stack, Fluid fluid) {
        return canInsertFluids(stack, FluidKeys.get(fluid));
    }

    public static boolean canInsertFluids(ItemStack stack, Fluid fluid) {
        return canInsertFluids(new Ref<>(stack), fluid);
    }

    public static boolean canExtractFluids(Reference<ItemStack> stack) {
        return FluidAttributes.EXTRACTABLE.get(stack) != EmptyFluidExtractable.NULL;
    }

    public static boolean canExtractFluids(ItemStack stack) {
        return canExtractFluids(new Ref<>(stack));
    }

    public static boolean canExtractFluids(Reference<ItemStack> stack, FluidFilter filter) {
        return FluidAttributes.EXTRACTABLE.get(stack).filtered(filter).couldExtractAnything();
    }

    public static boolean canExtractFluids(ItemStack stack, FluidFilter filter) {
        return canExtractFluids(new Ref<>(stack), filter);
    }

    public static boolean canExtractFluids(Reference<ItemStack> stack, Fluid fluid) {
        return canExtractFluids(stack, key -> key.getRawFluid() == fluid);
    }

    public static boolean canExtractFluids(ItemStack stack, Fluid fluid) {
        return canExtractFluids(new Ref<>(stack), fluid);
    }

    public static boolean canExtractFluids(Reference<ItemStack> stack, Tag<Fluid> tag) {
        return canExtractFluids(stack, key -> tag.contains(key.getRawFluid()));
    }

    public static boolean canExtractFluids(ItemStack stack, Tag<Fluid> tag) {
        return canExtractFluids(new Ref<>(stack), tag);
    }

    public static boolean isFixedFluidInv(Reference<ItemStack> stack) {
        return FluidAttributes.FIXED_INV.get(stack) != EmptyFixedFluidInv.INSTANCE;
    }

    public static boolean isFixedFluidInvView(Reference<ItemStack> stack) {
        return FluidAttributes.FIXED_INV_VIEW.get(stack) != EmptyFixedFluidInv.INSTANCE;
    }

    public static FluidInsertable getInsertable(World world, BlockPos pos, Direction direction) {
        return FluidAttributes.INSERTABLE.getFirst(world, pos, SearchOptions.inDirection(direction));
    }

    public static FluidExtractable getExtractable(World world, BlockPos pos, Direction direction) {
        return FluidAttributes.EXTRACTABLE.getFirst(world, pos, SearchOptions.inDirection(direction));
    }

    public static boolean canAccessFluid(World world, BlockPos pos, Direction direction) {
        return FluidAttributes.EXTRACTABLE.getFirstOrNull(world, pos, SearchOptions.inDirection(direction)) != null
                || FluidAttributes.INSERTABLE.getFirstOrNull(world, pos, SearchOptions.inDirection(direction)) != null;
    }
}
