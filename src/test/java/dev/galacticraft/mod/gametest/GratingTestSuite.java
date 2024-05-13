/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.gametest;

import dev.galacticraft.mod.api.block.FluidLoggable;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.decoration.GratingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.material.Fluids;

/**
 * Grating tests.
 * FIXME: Currently custom fluids are broken (blocks are loaded before all fluids are registered)
 */
public class GratingTestSuite implements GalacticraftGameTest {
    @GameTest(template = EMPTY_STRUCTURE)
    public void gratingFlowingWaterTest(GameTestHelper context) {
        final var xz = 4;
        final var pos4 = new BlockPos(xz, 4, xz);
        final var pos3 = new BlockPos(xz, 3, xz);
        final var pos2 = new BlockPos(xz, 2, xz);
        final var pos1 = new BlockPos(xz, 1, xz);
        final var mutable = new BlockPos.MutableBlockPos();
        context.setBlock(pos2, GCBlocks.GRATING.defaultBlockState().setValue(GratingBlock.STATE, GratingBlock.State.LOWER));

        if (!context.getBlockState(pos2).getFluidState().isEmpty()) {
            context.fail(String.format("Expected grating to not be filled with fluid but found %s instead!", BuiltInRegistries.FLUID.getKey(context.getBlockState(pos2).getFluidState().getType())), pos2);
        }
        else {
            for (var x = -1; x < 2; x++) {
                for (var z = -1; z < 2; z++) {
                    if (mutable.set(xz + x, 4, xz + z).equals(pos4)) {
                        continue;
                    }
                    context.setBlock(mutable, Blocks.GLASS);
                }
            }
            context.setBlock(pos4, Blocks.WATER);
            context.runAtTickTime(context.getTick() + 40L, () -> {
                if (!context.getBlockState(pos3).getFluidState().is(FluidTags.WATER)) {
                    context.fail(String.format("Expected water to flow downward but found %s instead!", BuiltInRegistries.FLUID.getKey(context.getBlockState(pos3).getFluidState().getType())), pos3);
                }
                else if (!context.getBlockState(pos2).getFluidState().is(FluidTags.WATER)) {
                    context.fail(String.format("Expected grating to be filled with water but found %s instead!", BuiltInRegistries.FLUID.getKey(context.getBlockState(pos2).getFluidState().getType())), pos2);
                }
                else if (!context.getBlockState(pos1).getFluidState().is(FluidTags.WATER)) {
                    context.fail(String.format("Expected water to be found below grating but found %s instead!", BuiltInRegistries.FLUID.getKey(context.getBlockState(pos1).getFluidState().getType())), pos1);
                }
                else {
                    context.setBlock(pos4, Blocks.AIR);
                    context.runAtTickTime(context.getTick() + 50L, () -> context.succeedWhen(() -> {
                        if (!context.getBlockState(pos3).getFluidState().isEmpty()) {
                            context.fail(String.format("Expected water to drain itself but found %s instead!", BuiltInRegistries.FLUID.getKey(context.getBlockState(pos3).getFluidState().getType())), pos3);
                        }
                        else if (!context.getBlockState(pos2).getFluidState().isEmpty()) {
                            context.fail(String.format("Expected grating to not be filled with fluid but found %s instead!", BuiltInRegistries.FLUID.getKey(context.getBlockState(pos2).getFluidState().getType())), pos2);
                        }
                        else if (!context.getBlockState(pos1).getFluidState().isEmpty()) {
                            context.fail(String.format("Expected no fluid to be found below grating but found %s instead!", BuiltInRegistries.FLUID.getKey(context.getBlockState(pos1).getFluidState().getType())), pos1);
                        }
                    }));
                }
            });
        }
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void gratingFlowingWaterWithFallingStateTest(GameTestHelper context) {
        final var mutable = new BlockPos.MutableBlockPos();
        final var mutable2 = new BlockPos.MutableBlockPos();

        for (var x = 1; x < 4; x++) {
            for (var z = 0; z < 8; z++) {
                if (mutable2.set(x, 0, z).equals(new BlockPos(2, 0, z)) && z > 0 && z < 7 || x == 3 && z == 6) {
                    continue;
                }
                context.setBlock(mutable2, Blocks.GLASS.defaultBlockState());
            }
        }

        var grating = GCBlocks.GRATING.defaultBlockState().setValue(GratingBlock.STATE, GratingBlock.State.LOWER);
        var gratingWaterPos = BlockPos.ZERO;

        for (var z = 1; z < 7; z++) {
            mutable.set(2, 0, z);

            if (z == 1) {
                context.setBlock(mutable, grating.setValue(FluidLoggable.FLUID, BuiltInRegistries.FLUID.getKey(Fluids.WATER)));
                gratingWaterPos = mutable.immutable();
            }
            else {
                context.setBlock(mutable, grating);
            }
        }

        if (context.getBlockState(gratingWaterPos).getFluidState().isEmpty()) {
            context.fail("Expected grating to be filled with water but found %s instead!".formatted(BuiltInRegistries.FLUID.getKey(context.getBlockState(gratingWaterPos).getFluidState().getType())), gratingWaterPos);
        }

        context.setBlock(new BlockPos(3, 0, 6), grating);

        final var waterPos = new BlockPos(4, 0, 6);

        context.runAtTickTime(context.getTick() + 50L, () -> context.succeedWhen(() -> {
            if (!context.getBlockState(waterPos).getFluidState().is(FluidTags.WATER)) {
                context.fail("Expected water but found %s instead!".formatted(BuiltInRegistries.FLUID.getKey(context.getBlockState(waterPos).getFluidState().getType())), waterPos);
            }
        }));
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void dispenserDispenseFluidToGrating(GameTestHelper context) {
        final var pos1 = new BlockPos(3, 0, 5);
        final var pos2 = new BlockPos(3, 1, 5);
        final var pos3 = new BlockPos(3, 0, 4);
        var grating = GCBlocks.GRATING.defaultBlockState().setValue(GratingBlock.STATE, GratingBlock.State.LOWER);

        context.setBlock(pos1, Blocks.DISPENSER.defaultBlockState());
        context.setBlock(pos3, grating);
        var blockEntity = context.getBlockEntity(pos1);

        if (blockEntity instanceof DispenserBlockEntity dispenserBlockEntity) {
            dispenserBlockEntity.addItem(new ItemStack(Items.WATER_BUCKET));
        }

        context.runAtTickTime(context.getTick() + 10L, () -> context.setBlock(pos2, Blocks.REDSTONE_BLOCK.defaultBlockState()));
        context.runAtTickTime(context.getTick() + 50L, () -> context.succeedWhen(() -> {
            if (!context.getBlockState(pos3).getFluidState().is(FluidTags.WATER)) {
                context.fail("Expected water but found %s instead!".formatted(BuiltInRegistries.FLUID.getKey(context.getBlockState(pos3).getFluidState().getType())), pos3);
            }
        }));
    }

//    @GameTest(template = EMPTY_STRUCTURE)
//    public void dispenserDispenseCustomFluidToGrating(GameTestHelper context) {
//        final var pos1 = new BlockPos(3, 0, 5);
//        final var pos2 = new BlockPos(3, 1, 5);
//        final var pos3 = new BlockPos(3, 0, 4);
//        var grating = GCBlocks.GRATING.defaultBlockState().setValue(GratingBlock.STATE, GratingBlock.State.LOWER);
//
//        context.setBlock(pos1, Blocks.DISPENSER.defaultBlockState());
//        context.setBlock(pos3, grating);
//        var blockEntity = context.getBlockEntity(pos1);
//
//        if (blockEntity instanceof DispenserBlockEntity dispenserBlockEntity) {
//            dispenserBlockEntity.addItem(new ItemStack(GCItems.FUEL_BUCKET));
//        }
//
//        context.runAtTickTime(context.getTick() + 10L, () -> context.setBlock(pos2, Blocks.REDSTONE_BLOCK.defaultBlockState()));
//        context.runAtTickTime(context.getTick() + 50L, () -> context.succeedWhen(() -> {
//            if (!context.getBlockState(pos3).getFluidState().is(GCFluids.FUEL)) {
//                context.fail("Expected fuel but found %s instead!".formatted(BuiltInRegistries.FLUID.getKey(context.getBlockState(pos3).getFluidState().getType())), pos3);
//            }
//        }));
//    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void dispenserPickupFluidFromGrating(GameTestHelper context) {
        final var pos1 = new BlockPos(3, 0, 5);
        final var pos2 = new BlockPos(3, 1, 5);
        final var pos3 = new BlockPos(3, 0, 4);
        var grating = GCBlocks.GRATING.defaultBlockState().setValue(GratingBlock.STATE, GratingBlock.State.LOWER).setValue(FluidLoggable.FLUID, BuiltInRegistries.FLUID.getKey(Fluids.WATER));

        context.setBlock(pos1, Blocks.DISPENSER.defaultBlockState());
        context.setBlock(pos3, grating);
        var blockEntity = context.getBlockEntity(pos1);

        if (blockEntity instanceof DispenserBlockEntity dispenserBlockEntity) {
            dispenserBlockEntity.addItem(new ItemStack(Items.BUCKET));
        }

        context.runAtTickTime(context.getTick() + 10L, () -> context.setBlock(pos2, Blocks.REDSTONE_BLOCK.defaultBlockState()));
        context.runAtTickTime(context.getTick() + 50L, () -> context.succeedWhen(() -> {
            if (context.getBlockState(pos3).getFluidState().is(FluidTags.WATER)) {
                context.fail("Expected grating without water but found %s instead!".formatted(BuiltInRegistries.FLUID.getKey(context.getBlockState(pos3).getFluidState().getType())), pos3);
            }
        }));
    }

//    @GameTest(template = EMPTY_STRUCTURE)
//    public void dispenserPickupCustomFluidFromGrating(GameTestHelper context) {
//        final var pos1 = new BlockPos(3, 0, 5);
//        final var pos2 = new BlockPos(3, 1, 5);
//        final var pos3 = new BlockPos(3, 0, 4);
//        var grating = GCBlocks.GRATING.defaultBlockState().setValue(GratingBlock.STATE, GratingBlock.State.LOWER).setValue(FluidLoggable.FLUID, BuiltInRegistries.FLUID.getKey(GCFluids.FUEL));
//
//        context.setBlock(pos1, Blocks.DISPENSER.defaultBlockState());
//        context.setBlock(pos3, grating);
//        var blockEntity = context.getBlockEntity(pos1);
//
//        if (blockEntity instanceof DispenserBlockEntity dispenserBlockEntity) {
//            dispenserBlockEntity.addItem(new ItemStack(Items.BUCKET));
//        }
//
//        context.runAtTickTime(context.getTick() + 10L, () -> context.setBlock(pos2, Blocks.REDSTONE_BLOCK.defaultBlockState()));
//        context.runAtTickTime(context.getTick() + 50L, () -> context.succeedWhen(() -> {
//            if (context.getBlockState(pos3).getFluidState().is(GCFluids.FUEL)) {
//                context.fail("Expected grating without fuel but found %s instead!".formatted(BuiltInRegistries.FLUID.getKey(context.getBlockState(pos3).getFluidState().getType())), pos3);
//            }
//        }));
//    }
}