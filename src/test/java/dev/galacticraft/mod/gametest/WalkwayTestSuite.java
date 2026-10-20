/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.api.block.entity.WalkwayHolder;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.special.walkway.WalkwayAttachable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Tests for walkways attached to pipes and wires.
 */
public class WalkwayTestSuite implements GalacticraftGameTest {
    private static final BlockPos POS = new BlockPos(1, 1, 1);

    @GameTest(template = EMPTY_STRUCTURE)
    public void attachWalkwayToClickedSideTest(GameTestHelper context) {
        context.setBlock(POS, GCBlocks.GLASS_FLUID_PIPE);
        ItemStack stack = new ItemStack(GCBlocks.WALKWAY, 2);
        this.use(context, stack, Direction.EAST);

        this.assertWalkway(context, Direction.EAST);
        context.assertTrue(stack.getCount() == 1, "Expected one walkway to be used but %s are left".formatted(stack.getCount()));
        context.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void attachWalkwayToWireTest(GameTestHelper context) {
        context.setBlock(POS, GCBlocks.HEAVY_ALUMINUM_WIRE);
        this.use(context, new ItemStack(GCBlocks.WALKWAY), Direction.DOWN);

        this.assertWalkway(context, Direction.DOWN);
        context.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void walkwayAddsShapeTest(GameTestHelper context) {
        context.setBlock(POS, GCBlocks.ALUMINUM_WIRE);
        this.use(context, new ItemStack(GCBlocks.WALKWAY), Direction.UP);

        BlockPos absolute = context.absolutePos(POS);
        var shape = context.getLevel().getBlockState(absolute).getShape(context.getLevel(), absolute);
        context.assertTrue(shape.bounds().maxY == 1.0, "Expected the shape to include the walkway on top but it ends at %s".formatted(shape.bounds().maxY));
        context.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void putPipeInWalkwayTest(GameTestHelper context) {
        context.setBlock(POS, GCBlocks.WALKWAY.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.NORTH));
        ItemStack stack = new ItemStack(GCBlocks.ALUMINUM_WIRE);
        this.use(context, stack, Direction.UP);

        context.assertBlockPresent(GCBlocks.ALUMINUM_WIRE, POS);
        this.assertWalkway(context, Direction.NORTH);
        context.assertTrue(stack.isEmpty(), "Expected the wire to be used");
        context.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void breakWalkwayPartTest(GameTestHelper context) {
        this.placeWithWalkway(context, Direction.UP);
        // A point on top of the platform
        this.breakPart(context, new Vec3(0.5, 1.0, 0.5));

        context.assertBlockPresent(GCBlocks.GLASS_FLUID_PIPE, POS);
        this.assertWalkway(context, null);
        context.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void breakPipePartTest(GameTestHelper context) {
        this.placeWithWalkway(context, Direction.UP);
        // A point on the side of the pipe
        this.breakPart(context, new Vec3(0.375, 0.5, 0.5));

        context.assertBlockState(POS, state -> state.is(GCBlocks.WALKWAY) && state.getValue(BlockStateProperties.FACING) == Direction.UP, () -> "Expected a walkway facing up");
        context.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void dyeingKeepsWalkwayTest(GameTestHelper context) {
        this.placeWithWalkway(context, Direction.SOUTH);
        this.use(context, new ItemStack(Items.RED_DYE), Direction.UP);

        context.assertBlockPresent(GCBlocks.GLASS_FLUID_PIPES.get(PipeColor.fromDye(DyeColor.RED)), POS);
        this.assertWalkway(context, Direction.SOUTH);
        context.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void walkwayIsSavedTest(GameTestHelper context) {
        this.placeWithWalkway(context, Direction.WEST);
        BlockPos absolute = context.absolutePos(POS);
        BlockEntity blockEntity = context.getLevel().getBlockEntity(absolute);
        var registries = context.getLevel().registryAccess();
        BlockEntity loaded = BlockEntity.loadStatic(absolute, blockEntity.getBlockState(), blockEntity.saveWithFullMetadata(registries), registries);

        Direction walkway = loaded instanceof WalkwayHolder holder ? holder.getWalkway() : null;
        context.assertTrue(walkway == Direction.WEST, "Expected the loaded walkway to face west but found %s".formatted(walkway));
        context.succeed();
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void pickWalkwayOrPipeTest(GameTestHelper context) {
        this.placeWithWalkway(context, Direction.UP);
        BlockPos absolute = context.absolutePos(POS);
        BlockState state = context.getLevel().getBlockState(absolute);
        WalkwayAttachable block = (WalkwayAttachable) state.getBlock();

        ItemStack onWalkway = block.getPickedStack(state, context.getLevel(), absolute, null, new BlockHitResult(Vec3.atLowerCornerOf(absolute).add(0.5, 1.0, 0.5), Direction.UP, absolute, false));
        ItemStack onPipe = block.getPickedStack(state, context.getLevel(), absolute, null, new BlockHitResult(Vec3.atLowerCornerOf(absolute).add(0.375, 0.5, 0.5), Direction.WEST, absolute, false));
        context.assertTrue(onWalkway.is(GCBlocks.WALKWAY.asItem()), "Expected to pick a walkway but got %s".formatted(onWalkway));
        context.assertTrue(onPipe.is(GCBlocks.GLASS_FLUID_PIPE.asItem()), "Expected to pick a glass fluid pipe but got %s".formatted(onPipe));
        context.succeed();
    }

    private void placeWithWalkway(GameTestHelper context, Direction walkway) {
        context.setBlock(POS, GCBlocks.GLASS_FLUID_PIPE);
        this.use(context, new ItemStack(GCBlocks.WALKWAY), walkway);
    }

    private void use(GameTestHelper context, ItemStack stack, Direction side) {
        Player player = context.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPos absolute = context.absolutePos(POS);
        BlockState state = context.getLevel().getBlockState(absolute);
        state.useItemOn(stack, context.getLevel(), player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(absolute), side, absolute, false));
    }

    private void breakPart(GameTestHelper context, Vec3 local) {
        BlockPos absolute = context.absolutePos(POS);
        BlockState state = context.getLevel().getBlockState(absolute);
        WalkwayHolder holder = (WalkwayHolder) context.getLevel().getBlockEntity(absolute);
        WalkwayAttachable.breakPart(context.getLevel(), context.makeMockPlayer(GameType.CREATIVE), absolute, state, holder, Vec3.atLowerCornerOf(absolute).add(local));
    }

    private void assertWalkway(GameTestHelper context, @Nullable Direction expected) {
        Direction walkway = context.getLevel().getBlockEntity(context.absolutePos(POS)) instanceof WalkwayHolder holder ? holder.getWalkway() : null;
        context.assertTrue(walkway == expected, "Expected the walkway to be %s but found %s".formatted(expected, walkway));
    }
}
