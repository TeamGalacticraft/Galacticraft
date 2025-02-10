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

package dev.galacticraft.mod.content.block.special.launchpad;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractLaunchPad extends BaseEntityBlock {

    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    private static final Direction[] CARDINAL = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    private static final VoxelShape CENTER_SHAPE = Shapes.create(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D);
    private static final VoxelShape NORMAL_SHAPE = Shapes.create(0.0D, 0.0D, 0.0D, 1.0D, 1D / 8D, 1.0D);

    public AbstractLaunchPad(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(PART, Part.NONE));
    }

    @Override
    protected abstract MapCodec<? extends AbstractLaunchPad> codec();

    public static BlockPos partToCenterPos(Part part) {
        if (part == null) {
            return BlockPos.ZERO;
        }
        return switch (part) {
            case NORTH, SOUTH, EAST, WEST ->
                    new BlockPos(part.getDirection().getFirst().getOpposite().getStepX(), 0, part.getDirection().getFirst().getOpposite().getStepZ());
            case NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST ->
                    new BlockPos(part.getDirection().getSecond().getOpposite().getStepX(), 0, part.getDirection().getFirst().getOpposite().getStepZ());
            default -> BlockPos.ZERO;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return blockState.getValue(PART) == Part.NONE || blockState.getValue(PART) == Part.CENTER ? CENTER_SHAPE : NORMAL_SHAPE;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        if (!level.isClientSide() && player.isCreative()) {
            var part = blockState.getValue(PART);
            if (part != Part.CENTER) {
                var blockPos2 = switch (part) {
                    case NORTH, SOUTH, EAST, WEST -> blockPos.relative(part.getDirection().getFirst().getOpposite());
                    case NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST ->
                            blockPos.relative(part.getDirection().getFirst().getOpposite()).relative(part.getDirection().getSecond().getOpposite());
                    case NONE, CENTER -> blockPos;
                };
                var blockState2 = level.getBlockState(blockPos2);
                if (blockState2.is(this) && blockState2.getValue(PART) == Part.CENTER) {
                    level.destroyBlock(blockPos2, false);
                }
            }
        }
        return super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState newState, boolean moved) {
        // do nothing if it's not part of a multiblock
        if (blockState.getValue(PART) == Part.NONE) {
            return;
        }
        // figure out where the center block is and remove the rocket
        BlockPos center = blockPos.offset(AbstractLaunchPad.partToCenterPos(blockState.getValue(PART)));
        if (level.getBlockEntity(center) instanceof LaunchPadBlockEntity pad) {
            if (pad.hasDockedEntity() && pad.getDockedEntity() != null) {
                pad.getDockedEntity().onPadDestroyed();
            }
        }
        // calling this removes the block entity
        super.onRemove(blockState, level, blockPos, newState, moved);
        // remove the launch pad blocks
        for (var x = -1; x <= 1; x++) {
            for (var z = -1; z <= 1; z++) {
                level.destroyBlock(center.offset(x, 0, z), true);
            }
        }
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState oldState, boolean moved) {
        super.onPlace(blockState, level, blockPos, oldState, moved);
        // the block should not already be assigned to a part of the launch pad
        if (blockState.getValue(PART) != Part.NONE) {
            return;
        }
        // valid connections represent connections to other launch pad block that are not already part of a launch pad
        // options are either 2 (corner), 3 (center edge), or 4 (middle)
        byte validConnections = 0;
        for (Direction direction : CARDINAL) {
            if (isValidSingleLaunchPad(level.getBlockState(blockPos.relative(direction)))) {
                validConnections++;
            }
        }
        // now looks to find where the center block is and if it is part of a valid 3x3 grid
        for (byte c = validConnections; c >= 2; c--) {
            if (c == 4) {
                if (isValid3x3(level, blockPos)) {
                    updateMultiBlock(level, blockPos);
                    return;
                }
            }
            else if (c == 3) {
                for (Direction primaryDir : CARDINAL) {
                    if (level.getBlockState(blockPos.relative(primaryDir)).is(this) && level.getBlockState(blockPos.relative(primaryDir.getOpposite())).is(this)) {
                        for (Direction secondaryDir : CARDINAL) {
                            if (secondaryDir.getAxis() != primaryDir.getAxis() && isValidSingleLaunchPad(level.getBlockState(blockPos.relative(secondaryDir)))) {
                                if (isValid3x3(level, blockPos.relative(secondaryDir))) {
                                    updateMultiBlock(level, blockPos.relative(secondaryDir));
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            else if (c == 2) {
                for (Direction primaryDir : CARDINAL) {
                    if (level.getBlockState(blockPos.relative(primaryDir)).is(this)) {
                        Direction[] secondaryDirs = primaryDir.getAxis() == Direction.Axis.Z ? new Direction[]{Direction.EAST, Direction.WEST} : new Direction[]{Direction.NORTH, Direction.SOUTH};
                        for (Direction secondaryDir : secondaryDirs) {
                            if (isValidSingleLaunchPad(level.getBlockState(blockPos.relative(secondaryDir).relative(primaryDir)))) {
                                if (isValid3x3(level, blockPos.relative(secondaryDir).relative(primaryDir))) {
                                    updateMultiBlock(level, blockPos.relative(secondaryDir).relative(primaryDir));
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Part getPartForOffset(int x, int z) {
        switch (x) {
            case 0:
                switch (z) {
                    case 0 -> {
                        return Part.CENTER;
                    }
                    case -1 -> {
                        return Part.NORTH;
                    }
                    case 1 -> {
                        return Part.SOUTH;
                    }
                }
            case -1:
                switch (z) {
                    case 0 -> {
                        return Part.WEST;
                    }
                    case -1 -> {
                        return Part.NORTH_WEST;
                    }
                    case 1 -> {
                        return Part.SOUTH_WEST;
                    }
                }
            case 1:
                switch (z) {
                    case 0 -> {
                        return Part.EAST;
                    }
                    case -1 -> {
                        return Part.NORTH_EAST;
                    }
                    case 1 -> {
                        return Part.SOUTH_EAST;
                    }
                }
        }
        return Part.NONE;
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return Block.canSupportRigidBlock(levelReader, blockPos.below());
    }

    public enum Part implements StringRepresentable {
        NONE,
        NORTH_WEST(Pair.of(Direction.NORTH, Direction.WEST)),
        NORTH(Pair.of(Direction.NORTH, null)),
        NORTH_EAST(Pair.of(Direction.NORTH, Direction.EAST)),
        WEST(Pair.of(Direction.WEST, null)),
        CENTER,
        EAST(Pair.of(Direction.EAST, null)),
        SOUTH_WEST(Pair.of(Direction.SOUTH, Direction.WEST)),
        SOUTH(Pair.of(Direction.SOUTH, null)),
        SOUTH_EAST(Pair.of(Direction.SOUTH, Direction.EAST));

        private Pair<Direction, Direction> direction;

        Part() {
        }

        Part(Pair<Direction, Direction> direction) {
            this.direction = direction;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }

        @Nullable
        public Pair<Direction, @Nullable Direction> getDirection() {
            return this.direction;
        }
    }

    // helper functions for onPlace()

    // returns true if the block being considered is a LaunchPadBlock and is not part of another launch pad
    private boolean isValidSingleLaunchPad(BlockState blockState) {
        return blockState.is(this) && blockState.getValue(PART) == Part.NONE;
    }

    // returns true if the given block is the center of a valid 3x3 launchpad
    private boolean isValid3x3 (Level level, BlockPos centerBlockPos) {
        // check if any blocks in 3x3 grid are not valid
        for (var x = -1; x <= 1; x++) {
            for (var z = -1; z <= 1; z++) {
                // if one of the surrounding blocks is not a launch pad or is already part of another launch pad
                var block = level.getBlockState(centerBlockPos.offset(x, 0, z));
                if (!block.is(this) || block.getValue(PART) != Part.NONE) {
                    return false;
                }
            }
        }
        return true;
    }

    // updates the launchpad multiblock with directional part names
    private void updateMultiBlock(Level level, BlockPos centerBlockPos) {
        for (var x = -1; x <= 1; x++) {
            for (var z = -1; z <= 1; z++) {
                level.setBlockAndUpdate(centerBlockPos.offset(x, 0, z), level.getBlockState(centerBlockPos.offset(x, 0, z)).setValue(PART, this.getPartForOffset(x, z)));
            }
        }
    }
}