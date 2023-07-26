/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.entity.RocketEntity;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RocketLaunchPadBlock extends BaseEntityBlock {
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    private static final Direction[] CARDINAL = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    private static final VoxelShape CENTER_SHAPE = Shapes.create(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D);
    private static final VoxelShape NORMAL_SHAPE = Shapes.create(0.0D, 0.0D, 0.0D, 1.0D, 1D / 8D, 1.0D);

    public RocketLaunchPadBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(PART, Part.NONE));
    }

    public static BlockPos partToCenterPos(Part part) {
        if (part == null) {
            return BlockPos.ZERO;
        }
        return switch (part) {
            case NORTH, SOUTH, EAST, WEST ->
                    new BlockPos(Direction.valueOf(part.getSerializedName().toUpperCase()).getOpposite().getStepX(), 0, Direction.valueOf(part.getSerializedName().toUpperCase()).getOpposite().getStepZ());
            case NONE, CENTER -> BlockPos.ZERO;
            default ->
                    new BlockPos(Direction.valueOf(part.getSerializedName().split("_")[1].toUpperCase()).getOpposite().getStepX(), 0, Direction.valueOf(part.getSerializedName().split("_")[0].toUpperCase()).getOpposite().getStepZ());
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
        return blockState.getValue(PART) == Part.CENTER ? CENTER_SHAPE : NORMAL_SHAPE;
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState newState, boolean moved) {
        super.onRemove(blockState, level, blockPos, newState, moved);
        switch (blockState.getValue(PART)) {
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST: {
                BlockPos center = blockPos.relative(Direction.valueOf(blockState.getValue(PART).getSerializedName().toUpperCase()).getOpposite());
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (level.getBlockState(center.offset(x, 0, z)).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD
                                && level.getBlockState(center.offset(x, 0, z)).getValue(PART) != Part.NONE) {
                            if (level.getBlockEntity(blockPos) instanceof RocketLaunchPadBlockEntity) {
                                if (((RocketLaunchPadBlockEntity) level.getBlockEntity(blockPos)).hasRocket()) {
                                    Entity entity = level.getEntity(((RocketLaunchPadBlockEntity) level.getBlockEntity(blockPos)).getRocketEntityId());
                                    if (entity instanceof RocketEntity) {
                                        ((RocketEntity) entity).onBaseDestroyed();
                                    }
                                }
                            }
                            level.setBlock(center.offset(x, 0, z), Blocks.AIR.defaultBlockState(), 3);
                            createBlockBreakParticles(level, center.offset(x, 0, z));
                        }
                    }
                }
                return;
            }
            case NORTH_EAST:
            case NORTH_WEST:
            case SOUTH_EAST:
            case SOUTH_WEST: {
                String[] parts = blockState.getValue(PART).getSerializedName().split("_");
                BlockPos center = blockPos.relative(Direction.valueOf(parts[0].toUpperCase()).getOpposite()).relative(Direction.valueOf(parts[1].toUpperCase()).getOpposite());
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (level.getBlockState(center.offset(x, 0, z)).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD
                                && level.getBlockState(center.offset(x, 0, z)).getValue(PART) != Part.NONE) {
                            if (level.getBlockEntity(blockPos) instanceof RocketLaunchPadBlockEntity) {
                                if (((RocketLaunchPadBlockEntity) level.getBlockEntity(blockPos)).hasRocket()) {
                                    Entity entity = level.getEntity(((RocketLaunchPadBlockEntity) level.getBlockEntity(blockPos)).getRocketEntityId());
                                    if (entity instanceof RocketEntity) {
                                        ((RocketEntity) entity).onBaseDestroyed();
                                    }
                                }
                            }
                            level.setBlock(center.offset(x, 0, z), Blocks.AIR.defaultBlockState(), 3);
                            createBlockBreakParticles(level, center.offset(x, 0, z));
                        }
                    }
                }
                return;
            }
            case CENTER: {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (level.getBlockState(blockPos.offset(x, 0, z)).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD
                                && level.getBlockState(blockPos.offset(x, 0, z)).getValue(PART) != Part.NONE) {
                            if (level.getBlockEntity(blockPos) instanceof RocketLaunchPadBlockEntity) {
                                if (((RocketLaunchPadBlockEntity) level.getBlockEntity(blockPos)).hasRocket()) {
                                    Entity entity = level.getEntity(((RocketLaunchPadBlockEntity) level.getBlockEntity(blockPos)).getRocketEntityId());
                                    if (entity instanceof RocketEntity) {
                                        ((RocketEntity) entity).onBaseDestroyed();
                                    }
                                }
                            }
                            level.setBlock(blockPos.offset(x, 0, z), Blocks.AIR.defaultBlockState(), 3);
                            createBlockBreakParticles(level, blockPos.offset(x, 0, z));
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
            int connections = 0;
            for (Direction direction : CARDINAL) {
                if (level.getBlockState(blockPos.relative(direction)).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD) {
                    connections++;
                }
            }

            if (connections == 4) {
                boolean allValid = true;
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (level.getBlockState(blockPos.offset(x, 0, z)).getBlock() != GCBlocks.ROCKET_LAUNCH_PAD) {
                            allValid = false;
                            break;
                        } else if (level.getBlockState(blockPos.offset(x, 0, z)).getValue(PART) != Part.NONE) {
                            allValid = false;
                            break;
                        }
                    }
                }
                if (allValid) {
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            createBlockBreakParticles(level, blockPos);
                            level.setBlockAndUpdate(blockPos.offset(x, 0, z), level.getBlockState(blockPos.offset(x, 0, z)).setValue(PART, getPartForOffset(x, z)));
                        }
                    }
                    return;
                } else {
                    connections--;
                }
            }

            if (connections == 3) {
                for (Direction direction : CARDINAL) {
                    if (level.getBlockState(blockPos.relative(direction)).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD) {
                        if (level.getBlockState(blockPos.relative(direction.getOpposite())).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD) {
                            for (Direction dir : CARDINAL) {
                                if (dir.getAxis() != direction.getAxis()) {
                                    boolean allValid = true;
                                    for (int x = -1; x <= 1; x++) {
                                        for (int z = -1; z <= 1; z++) {
                                            if (level.getBlockState(blockPos.relative(dir).offset(x, 0, z)).getBlock() != GCBlocks.ROCKET_LAUNCH_PAD) {
                                                allValid = false;
                                                break;
                                            } else if (level.getBlockState(blockPos.relative(dir).offset(x, 0, z)).getValue(PART) != Part.NONE) {
                                                allValid = false;
                                                break;
                                            }
                                        }
                                    }

                                    if (allValid) {
                                        for (int x = -1; x <= 1; x++) {
                                            for (int z = -1; z <= 1; z++) {
                                                createBlockBreakParticles(level, blockPos.relative(dir));
                                                level.setBlockAndUpdate(blockPos.relative(dir).offset(x, 0, z), level.getBlockState(blockPos.relative(dir).offset(x, 0, z)).setValue(PART, getPartForOffset(x, z)));
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
                for (Direction direction : CARDINAL) {
                    if (level.getBlockState(blockPos.relative(direction)).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD) {
                        Direction[] dirs = new Direction[]{Direction.NORTH, Direction.SOUTH};
                        if (direction.getAxis() == Direction.Axis.Z)
                            dirs = new Direction[]{Direction.EAST, Direction.WEST};

                        for (Direction dir : dirs) {
                            if (level.getBlockState(blockPos.relative(dir)).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD) {
                                if (level.getBlockState(blockPos.relative(dir).relative(direction)).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD) {
                                    boolean allValid = true;
                                    for (int x = -1; x <= 1; x++) {
                                        for (int z = -1; z <= 1; z++) {
                                            if (level.getBlockState(blockPos.relative(dir).relative(direction).offset(x, 0, z)).getBlock() != GCBlocks.ROCKET_LAUNCH_PAD) {
                                                allValid = false;
                                                break;
                                            } else if (level.getBlockState(blockPos.relative(dir).relative(direction).offset(x, 0, z)).getValue(PART) != Part.NONE) {
                                                allValid = false;
                                                break;
                                            }
                                        }
                                    }

                                    if (allValid) {
                                        for (int x = -1; x <= 1; x++) {
                                            for (int z = -1; z <= 1; z++) {
                                                createBlockBreakParticles(level, blockPos.relative(dir).relative(direction));
                                                level.setBlockAndUpdate(blockPos.relative(dir).relative(direction).offset(x, 0, z), level.getBlockState(blockPos.relative(dir).relative(direction).offset(x, 0, z)).setValue(PART, getPartForOffset(x, z)));
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

    public void createBlockBreakParticles(LevelAccessor world, BlockPos pos) {
        if (!world.getBlockState(pos).isAir()) {
            VoxelShape voxelShape = world.getBlockState(pos).getShape(world, pos, CollisionContext.empty());
            double d = 0.25D;
            voxelShape.forAllBoxes((dx, e, f, g, h, i) -> {
                double j = Math.min(1.0D, g - dx);
                double k = Math.min(1.0D, h - e);
                double l = Math.min(1.0D, i - f);
                int m = Math.max(2, Mth.ceil(j / 0.25D));
                int n = Math.max(2, Mth.ceil(k / 0.25D));
                int o = Math.max(2, Mth.ceil(l / 0.25D));

                for (int p = 0; p < m; ++p) {
                    for (int q = 0; q < n; ++q) {
                        for (int r = 0; r < o; ++r) {
                            double s = ((double) p + 0.5D) / (double) m;
                            double t = ((double) q + 0.5D) / (double) n;
                            double u = ((double) r + 0.5D) / (double) o;
                            double v = s * j + dx;
                            double w = t * k + e;
                            double x = u * l + f;
                            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer()).writeVarInt(Block.BLOCK_STATE_REGISTRY.getId(this.defaultBlockState()));
                            world.addParticle(BlockParticleOption.DESERIALIZER.fromNetwork(ParticleTypes.BLOCK, buf), (double) pos.getX() + v, (double) pos.getY() + w, (double) pos.getZ() + x, s - 0.5D, t - 0.5D, u - 0.5D);
                        }
                    }
                }

            });
        }
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return !levelReader.getBlockState(blockPos.relative(Direction.DOWN)).canBeReplaced();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RocketLaunchPadBlockEntity(pos, state); //i give up on lazily creating the BE
    }

    public enum Part implements StringRepresentable {
        NONE,
        NORTH_WEST,
        NORTH,
        NORTH_EAST,
        WEST,
        CENTER,
        EAST,
        SOUTH_WEST,
        SOUTH,
        SOUTH_EAST;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}