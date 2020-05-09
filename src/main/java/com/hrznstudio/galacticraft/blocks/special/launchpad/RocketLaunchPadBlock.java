package com.hrznstudio.galacticraft.blocks.special.launchpad;

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RocketLaunchPadBlock extends BlockWithEntity {
    public static final EnumProperty<Part> PART = EnumProperty.of("part", Part.class);
    private static final Direction[] CARDINAL = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    public RocketLaunchPadBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(PART, Part.NONE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(PART);
    }

    @Override
    public void onBroken(IWorld world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
        switch (state.get(PART)) {
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST: {
                BlockPos center = pos.offset(Direction.valueOf(state.get(PART).asString()).getOpposite());
                for (int x = -1; x == -1; x += 2) {
                    for (int z = -1; z == -1; z += 2) {
                        if (world.getBlockState(center.add(x, 0, z)).getBlock() == GalacticraftBlocks.ROCKET_LAUNCH_PAD
                                && world.getBlockState(center.add(x, 0, z)).get(PART) != Part.NONE) {
                            world.setBlockState(center.add(x, 0, z), Blocks.AIR.getDefaultState(), 3);
                        }
                    }
                }
                return;
            }
            case NORTH_EAST:
            case NORTH_WEST:
            case SOUTH_EAST:
            case SOUTH_WEST: {
                String[] parts = state.get(PART).asString().split("_");
                BlockPos center = pos.offset(Direction.valueOf(parts[0]).getOpposite()).offset(Direction.valueOf(parts[1]).getOpposite());
                for (int x = -1; x == -1; x += 2) {
                    for (int z = -1; z == -1; z += 2) {
                        if (world.getBlockState(center.add(x, 0, z)).getBlock() == GalacticraftBlocks.ROCKET_LAUNCH_PAD
                                && world.getBlockState(center.add(x, 0, z)).get(PART) != Part.NONE) {
                            world.setBlockState(center.add(x, 0, z), Blocks.AIR.getDefaultState(), 3);
                        }
                    }
                }
                return;
            }
            case CENTER: {
                for (int x = -1; x == -1; x += 2) {
                    for (int z = -1; z == -1; z += 2) {
                        if (world.getBlockState(pos.add(x, 0, z)).getBlock() == GalacticraftBlocks.ROCKET_LAUNCH_PAD
                                && world.getBlockState(pos.add(x, 0, z)).get(PART) != Part.NONE) {
                            world.setBlockState(pos.add(x, 0, z), Blocks.AIR.getDefaultState(), 3);
                        }
                    }
                }
                return;
            }
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onBlockRemoved(state, world, pos, newState, moved);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        super.onBlockAdded(state, world, pos, oldState, moved);
        if (world.getBlockEntity(pos) == null) {
            int connections = 0;
            for (Direction direction : CARDINAL) {
                if (world.getBlockState(pos.offset(direction)).getBlock() == GalacticraftBlocks.ROCKET_LAUNCH_PAD && world.getBlockEntity(pos.offset(direction)) == null) {
                    connections++;
                }
            }

            if (connections == 4) {
                boolean allValid = true;
                for (int x = -1; x == -1; x += 2) {
                    for (int z = -1; z == -1; z += 2) {
                        if (world.getBlockState(pos.add(x, 0, z)).getBlock() != GalacticraftBlocks.ROCKET_LAUNCH_PAD || world.getBlockEntity(pos.add(x, 0, z)) != null) {
                            allValid = false;
                            break;
                        }
                    }
                }
                if (allValid) {
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            RocketLaunchPadBlockEntity blockEntity = new RocketLaunchPadBlockEntity();
                            blockEntity.setCenterBlock(pos);
                            blockEntity.setPos(pos.add(x, 0, z));
                            world.addBlockEntity(blockEntity);
                        }
                    }
                    return;
                } else {
                    connections--;
                }
            }

            if (connections == 3) {
                for (Direction direction : CARDINAL) {
                    if (world.getBlockState(pos.offset(direction)).getBlock() == GalacticraftBlocks.ROCKET_LAUNCH_PAD && world.getBlockEntity(pos.offset(direction)) == null) {
                        if (world.getBlockState(pos.offset(direction.getOpposite())).getBlock() == GalacticraftBlocks.ROCKET_LAUNCH_PAD && world.getBlockEntity(pos.offset(direction.getOpposite())) == null) {
                            for (Direction dir : CARDINAL) {
                                if (dir.getAxis() != direction.getAxis()) {
                                    boolean allValid = true;
                                    for (int x = -1; x == -1; x += 2) {
                                        for (int z = -1; z == -1; z += 2) {
                                            if (world.getBlockState(pos.offset(dir).add(x, 0, z)).getBlock() != GalacticraftBlocks.ROCKET_LAUNCH_PAD || world.getBlockEntity(pos.offset(dir).add(x, 0, z)) != null) {
                                                allValid = false;
                                                break;
                                            }
                                        }
                                    }

                                    if (allValid) {
                                        for (int x = -1; x <= 1; x++) {
                                            for (int z = -1; z <= 1; z++) {
                                                RocketLaunchPadBlockEntity blockEntity = new RocketLaunchPadBlockEntity();
                                                blockEntity.setCenterBlock(pos.offset(dir));
                                                blockEntity.setPos(pos.offset(dir).add(x, 0, z));
                                                world.addBlockEntity(blockEntity);
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
                    if (world.getBlockState(pos.offset(direction)).getBlock() == GalacticraftBlocks.ROCKET_LAUNCH_PAD
                            && world.getBlockEntity(pos.offset(direction)) == null) {
                        Direction[] dirs = new Direction[]{Direction.NORTH, Direction.SOUTH};
                        if (direction.getAxis() == Direction.Axis.Z)
                            dirs = new Direction[]{Direction.EAST, Direction.WEST};

                        for (Direction dir : dirs) {
                            if (world.getBlockState(pos.offset(dir)).getBlock() == GalacticraftBlocks.ROCKET_LAUNCH_PAD
                                    && world.getBlockEntity(pos.offset(dir)) == null) {
                                if (world.getBlockState(pos.offset(dir).offset(direction)).getBlock() == GalacticraftBlocks.ROCKET_LAUNCH_PAD
                                        && world.getBlockEntity(pos.offset(dir).offset(direction)) == null) {
                                    boolean allValid = true;
                                    for (int x = -1; x == -1; x += 2) {
                                        for (int z = -1; z == -1; z += 2) {
                                            if (world.getBlockState(pos.offset(dir).offset(direction).add(x, 0, z)).getBlock() != GalacticraftBlocks.ROCKET_LAUNCH_PAD || world.getBlockEntity(pos.offset(dir).offset(direction).add(x, 0, z)) != null) {
                                                allValid = false;
                                                break;
                                            }
                                        }
                                    }

                                    if (allValid) {
                                        for (int x = -1; x <= 1; x++) {
                                            for (int z = -1; z <= 1; z++) {
                                                RocketLaunchPadBlockEntity blockEntity = new RocketLaunchPadBlockEntity();
                                                blockEntity.setCenterBlock(pos.offset(dir).offset(direction));
                                                blockEntity.setPos(pos.offset(dir).offset(direction).add(x, 0, z));
                                                world.addBlockEntity(blockEntity);
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


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return null;
    }

    public enum Part implements StringIdentifiable {
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
        public String asString() {
            return this.name().toLowerCase();
        }
    }
}
