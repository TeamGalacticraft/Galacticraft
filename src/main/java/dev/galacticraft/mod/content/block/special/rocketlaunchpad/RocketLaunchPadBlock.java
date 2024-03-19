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

package dev.galacticraft.mod.content.block.special.rocketlaunchpad;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RocketLaunchPadBlock extends BaseEntityBlock {
    public static final MapCodec<RocketLaunchPadBlock> CODEC = simpleCodec(RocketLaunchPadBlock::new);
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    private static final Direction[] CARDINAL = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    private static final VoxelShape CENTER_SHAPE = Shapes.create(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D);
    private static final VoxelShape NORMAL_SHAPE = Shapes.create(0.0D, 0.0D, 0.0D, 1.0D, 1D / 8D, 1.0D);

    public RocketLaunchPadBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(PART, Part.NONE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

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
        super.onRemove(blockState, level, blockPos, newState, moved);
        var part = blockState.getValue(PART);
        switch (part) {
            case NORTH, SOUTH, EAST, WEST -> {
                var center = blockPos.relative(part.getDirection().getFirst().getOpposite());
                for (var x = -1; x <= 1; x++) {
                    for (var z = -1; z <= 1; z++) {
                        var blockState1 = level.getBlockState(center.offset(x, 0, z));

                        if (blockState1.is(this) && blockState1.getValue(PART) != Part.NONE) {
                            if (level.getBlockEntity(center.offset(x, 0, z)) instanceof RocketLaunchPadBlockEntity pad) {
                                if (pad.hasRocket()) {
                                    if (pad.getRocket() != null) {
                                        pad.getRocket().onBaseDestroyed();
                                    }
                                }
                            }
                            level.destroyBlock(center.offset(x, 0, z), true);
                        }
                    }
                }
            }
            case NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST -> {
                var center = blockPos.relative(part.getDirection().getFirst().getOpposite()).relative(part.getDirection().getSecond().getOpposite());
                for (var x = -1; x <= 1; x++) {
                    for (var z = -1; z <= 1; z++) {
                        var blockState1 = level.getBlockState(center.offset(x, 0, z));

                        if (blockState1.is(this) && blockState1.getValue(PART) != Part.NONE) {
                            if (level.getBlockEntity(center.offset(x, 0, z)) instanceof RocketLaunchPadBlockEntity pad) {
                                if (pad.hasRocket()) {
                                    var entity = pad.getRocket();
                                    if (entity != null) {
                                        entity.onBaseDestroyed();
                                    }
                                }
                            }
                            level.destroyBlock(center.offset(x, 0, z), true);
                        }
                    }
                }
            }
            case CENTER -> {
                for (var x = -1; x <= 1; x++) {
                    for (var z = -1; z <= 1; z++) {
                        var blockState1 = level.getBlockState(blockPos.offset(x, 0, z));

                        if (blockState1.is(this) && blockState1.getValue(PART) != Part.NONE) {
                            if (level.getBlockEntity(blockPos.offset(x, 0, z)) instanceof RocketLaunchPadBlockEntity pad) {
                                if (pad.hasRocket()) {
                                    var entity = pad.getRocket();
                                    if (entity != null) {
                                        entity.onBaseDestroyed();
                                    }
                                }
                            }
                            level.destroyBlock(blockPos.offset(x, 0, z), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState oldState, boolean moved) {
        super.onPlace(blockState, level, blockPos, oldState, moved);
        if (blockState.getValue(PART) == Part.NONE) {
            var connections = 0;
            for (var direction : CARDINAL) {
                if (level.getBlockState(blockPos.relative(direction)).is(this)) {
                    connections++;
                }
            }

            if (connections == 4) {
                var allValid = true;
                for (var x = -1; x <= 1; x++) {
                    for (var z = -1; z <= 1; z++) {
                        if (!level.getBlockState(blockPos.offset(x, 0, z)).is(this)) {
                            allValid = false;
                            break;
                        } else if (level.getBlockState(blockPos.offset(x, 0, z)).getValue(PART) != Part.NONE) {
                            allValid = false;
                            break;
                        }
                    }
                }
                if (allValid) {
                    for (var x = -1; x <= 1; x++) {
                        for (var z = -1; z <= 1; z++) {
                            level.setBlockAndUpdate(blockPos.offset(x, 0, z), level.getBlockState(blockPos.offset(x, 0, z)).setValue(PART, this.getPartForOffset(x, z)));
                        }
                    }
                    return;
                } else {
                    connections--;
                }
            }

            if (connections == 3) {
                for (var direction : CARDINAL) {
                    if (level.getBlockState(blockPos.relative(direction)).is(this)) {
                        if (level.getBlockState(blockPos.relative(direction.getOpposite())).is(this)) {
                            for (var dir : CARDINAL) {
                                if (dir.getAxis() != direction.getAxis()) {
                                    var allValid = true;
                                    for (var x = -1; x <= 1; x++) {
                                        for (var z = -1; z <= 1; z++) {
                                            if (!level.getBlockState(blockPos.relative(dir).offset(x, 0, z)).is(this)) {
                                                allValid = false;
                                                break;
                                            } else if (level.getBlockState(blockPos.relative(dir).offset(x, 0, z)).getValue(PART) != Part.NONE) {
                                                allValid = false;
                                                break;
                                            }
                                        }
                                    }

                                    if (allValid) {
                                        for (var x = -1; x <= 1; x++) {
                                            for (var z = -1; z <= 1; z++) {
                                                level.setBlockAndUpdate(blockPos.relative(dir).offset(x, 0, z), level.getBlockState(blockPos.relative(dir).offset(x, 0, z)).setValue(PART, this.getPartForOffset(x, z)));
                                            }
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                connections--;
            }

            if (connections == 2) {
                for (var direction : CARDINAL) {
                    if (level.getBlockState(blockPos.relative(direction)).is(this)) {
                        var dirs = new Direction[] {Direction.NORTH, Direction.SOUTH};
                        if (direction.getAxis() == Direction.Axis.Z) {
                            dirs = new Direction[] {Direction.EAST, Direction.WEST};
                        }

                        for (var dir : dirs) {
                            if (level.getBlockState(blockPos.relative(dir)).is(this)) {
                                if (level.getBlockState(blockPos.relative(dir).relative(direction)).is(this)) {
                                    var allValid = true;
                                    for (var x = -1; x <= 1; x++) {
                                        for (var z = -1; z <= 1; z++) {
                                            if (!level.getBlockState(blockPos.relative(dir).relative(direction).offset(x, 0, z)).is(this)) {
                                                allValid = false;
                                                break;
                                            } else if (level.getBlockState(blockPos.relative(dir).relative(direction).offset(x, 0, z)).getValue(PART) != Part.NONE) {
                                                allValid = false;
                                                break;
                                            }
                                        }
                                    }

                                    if (allValid) {
                                        for (var x = -1; x <= 1; x++) {
                                            for (var z = -1; z <= 1; z++) {
                                                level.setBlockAndUpdate(blockPos.relative(dir).relative(direction).offset(x, 0, z), level.getBlockState(blockPos.relative(dir).relative(direction).offset(x, 0, z)).setValue(PART, this.getPartForOffset(x, z)));
                                            }
                                        }
                                        return;
                                    }
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RocketLaunchPadBlockEntity(pos, state);
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
}