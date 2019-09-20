/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.blocks.special.aluminumwire;

import com.hrznstudio.galacticraft.api.block.WireBlock;
import com.hrznstudio.galacticraft.api.wire.WireConnectionType;
import com.hrznstudio.galacticraft.api.wire.WireUtils;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.util.WireConnectable;
import io.github.cottonmc.energy.api.EnergyAttributeProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class AluminumWireBlock extends BlockWithEntity implements WireConnectable, WireBlock {

    // If we start at 8,8,8 and subtract/add to/from 8, we do operations starting from the centre.
    private static final VoxelShape NORTH = createCuboidShape(8 - 3, 8 - 3, 0, 8 + 3, 8 + 3, 8 + 3);
    private static final VoxelShape EAST = createCuboidShape(8 - 3, 8 - 3, 8 - 3, 16, 8 + 3, 8 + 3);
    private static final VoxelShape SOUTH = createCuboidShape(8 - 3, 8 - 3, 8 - 3, 8 + 3, 8 + 3, 16);
    private static final VoxelShape WEST = createCuboidShape(0, 8 - 3, 8 - 3, 8 + 3, 8 + 3, 8 + 3);
    private static final VoxelShape UP = createCuboidShape(8 - 3, 8 - 3, 8 - 3, 8 + 3, 16, 8 + 3);
    private static final VoxelShape DOWN = createCuboidShape(8 - 3, 0, 8 - 3, 8 + 3, 8 + 3, 8 + 3);
    private static final VoxelShape NONE = createCuboidShape(8 - 3, 8 - 3, 8 - 3, 8 + 3, 8 + 3, 8 + 3);    // 6x6x6 box in the center.
    private static BooleanProperty ATTACHED_NORTH = BooleanProperty.of("attached_north");
    private static BooleanProperty ATTACHED_EAST = BooleanProperty.of("attached_east");
    private static BooleanProperty ATTACHED_SOUTH = BooleanProperty.of("attached_south");
    private static BooleanProperty ATTACHED_WEST = BooleanProperty.of("attached_west");
    private static BooleanProperty ATTACHED_UP = BooleanProperty.of("attached_up");
    private static BooleanProperty ATTACHED_DOWN = BooleanProperty.of("attached_down");

    public AluminumWireBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateFactory().getDefaultState().with(ATTACHED_NORTH, false).with(ATTACHED_EAST, false).with(ATTACHED_SOUTH, false).with(ATTACHED_WEST, false).with(ATTACHED_UP, false).with(ATTACHED_DOWN, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext EntityContext) {
        ArrayList<VoxelShape> shapes = new ArrayList<>();

        if (blockState.get(ATTACHED_NORTH)) {
            shapes.add(NORTH);
        }
        if (blockState.get(ATTACHED_SOUTH)) {
            shapes.add(SOUTH);
        }
        if (blockState.get(ATTACHED_EAST)) {
            shapes.add(EAST);
        }
        if (blockState.get(ATTACHED_WEST)) {
            shapes.add(WEST);
        }
        if (blockState.get(ATTACHED_UP)) {
            shapes.add(UP);
        }
        if (blockState.get(ATTACHED_DOWN)) {
            shapes.add(DOWN);
        }
        if (shapes.isEmpty()) {
            return NONE;
        } else {
            return VoxelShapes.union(NONE, shapes.toArray(new VoxelShape[0]));
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockState state = this.getDefaultState();
        for (Direction direction : Direction.values()) {
            Block block = context.getWorld().getBlockState(WireUtils.getPosFromDirection(direction, context.getBlockPos())).getBlock();
            if (block instanceof WireConnectable) {
                if (((WireConnectable) block).canWireConnect(context.getWorld(), direction.getOpposite(), context.getBlockPos(), WireUtils.getPosFromDirection(direction, context.getBlockPos())) != WireConnectionType.NONE) {
                    state = state.with(propFromDirection(direction), true);
                }
            } else if (block instanceof EnergyAttributeProvider) {
                state = state.with(propFromDirection(direction), true);
            }
        }

        return state;
    }

    private BooleanProperty propFromDirection(Direction direction) {
        switch (direction) {
            case NORTH:
                return ATTACHED_NORTH;
            case SOUTH:
                return ATTACHED_SOUTH;
            case EAST:
                return ATTACHED_EAST;
            case WEST:
                return ATTACHED_WEST;
            case UP:
                return ATTACHED_UP;
            case DOWN:
                return ATTACHED_DOWN;
            default:
                return null;
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity livingEntity, ItemStack stack) {
        if (world.getBlockEntity(pos).getType() == GalacticraftBlockEntities.ALUMINUM_WIRE_TYPE) {
            ((AluminumWireBlockEntity) world.getBlockEntity(pos)).init();
        }
    }

    private BooleanProperty getPropForDirection(Direction dir) {
        switch (dir) {
            case SOUTH:
                return ATTACHED_SOUTH;
            case EAST:
                return ATTACHED_EAST;
            case WEST:
                return ATTACHED_WEST;
            case NORTH:
                return ATTACHED_NORTH;
            case UP:
                return ATTACHED_UP;
            case DOWN:
                return ATTACHED_DOWN;
        }
        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction_1, BlockState blockState_2, IWorld world, BlockPos thisWire, BlockPos otherConnectable) {
        return state.with(getPropForDirection(direction_1), (
                !(blockState_2).isAir()
                        && blockState_2.getBlock() instanceof WireConnectable
                        // get opposite of direction so the WireConnectable can check from its perspective.
                        && (((WireConnectable) blockState_2.getBlock()).canWireConnect(world, direction_1.getOpposite(), thisWire, otherConnectable) != WireConnectionType.NONE)
        ));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactory$Builder_1) {
        super.appendProperties(stateFactory$Builder_1);
        stateFactory$Builder_1.add(ATTACHED_NORTH, ATTACHED_EAST, ATTACHED_SOUTH, ATTACHED_WEST, ATTACHED_UP, ATTACHED_DOWN);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView blockView_1, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView blockView_1, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canSuffocate(BlockState state, BlockView blockView_1, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isSimpleFullBlock(BlockState state, BlockView blockView_1, BlockPos pos) {
        return false;
    }

    @Override
    public boolean allowsSpawning(BlockState state, BlockView blockView_1, BlockPos pos, EntityType<?> entityType_1) {
        return false;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new AluminumWireBlockEntity();
    }

    @Override
    public WireConnectionType canWireConnect(IWorld world, Direction opposite, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        return WireConnectionType.WIRE;
    }
}