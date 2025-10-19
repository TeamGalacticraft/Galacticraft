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

package dev.galacticraft.mod.content.block.special;

import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.CocoonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.Nullable;

/**
 * Rotatable 6-way block with 12×8×8 oriented shape,
 * requires solid support (or oliant_web) behind its facing.
 * Hosts a CocoonBlockEntity that absorbs items.
 */
public class CocoonBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    // Base unit shapes (0..16) converted to [0..1]
    // 12 along facing axis, 8×8 thickness on the others.
    private static final VoxelShape SHAPE_N = box16(4, 4, 4, 12, 12, 16); // facing +Z
    private static final VoxelShape SHAPE_S = box16(4, 4, 0, 12, 12, 12); // facing -Z
    private static final VoxelShape SHAPE_E = box16(0, 4, 4, 12, 12, 12); // facing +X
    private static final VoxelShape SHAPE_W = box16(4, 4, 4, 16, 12, 12); // facing -X
    private static final VoxelShape SHAPE_U = box16(4, 0, 4, 12, 12, 12); // facing +Y
    private static final VoxelShape SHAPE_D = box16(4, 4, 4, 12, 16, 12); // facing -Y

    public CocoonBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    /* ---------------------------- Placement --------------------------- */

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction face = ctx.getClickedFace(); // the face we’re attaching to
        // Cocoon points outward from the face we clicked
        BlockState state = this.defaultBlockState().setValue(FACING, face);
        return canStick(ctx.getLevel(), ctx.getClickedPos(), state) ? state : null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canStick(level, pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CocoonBlockEntity cocoon) {
            player.openMenu(cocoon);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hit) {
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CocoonBlockEntity cocoon) {
            player.openMenu(cocoon);
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState other, LevelAccessor level, BlockPos pos, BlockPos fromPos) {
        return canStick(level, pos, state) ? state : Blocks.AIR.defaultBlockState();
    }

    private boolean canStick(LevelReader level, BlockPos pos, BlockState state) {
        Direction back = state.getValue(FACING).getOpposite();
        BlockPos supportPos = pos.relative(back);
        BlockState support = level.getBlockState(supportPos);

        // solid face in the *facing* direction (i.e., we’re stuck to that face)
        boolean solid = support.isFaceSturdy(level, supportPos, state.getValue(FACING));
        boolean web = support.is(GCBlocks.OLIANT_WEB);
        return solid || web;
    }

    /* ------------------------------ Shapes ---------------------------- */

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(FACING)) {
            case NORTH -> SHAPE_N;
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            case UP -> SHAPE_U;
            case DOWN -> SHAPE_D;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return getShape(state, level, pos, ctx);
    }

    private static VoxelShape box16(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return Block.box(minX, minY, minZ, maxX, maxY, maxZ); // these are already in 0..16 coords
    }

    /** World-space AABB (same extents as our shape) for absorption. */
    public static net.minecraft.world.phys.AABB absorptionAabb(BlockPos pos, Direction facing) {
        // small margin so items on the edge still get caught
        final double eps = 0.0005;

        double x1, x2, y1, y2, z1, z2;

        switch (facing) {
            case SOUTH -> { // +Z
                x1 = 4/16.0; x2 = 12/16.0;
                y1 = 4/16.0; y2 = 12/16.0;
                z1 = 4/16.0; z2 = 16/16.0;
            }
            case NORTH -> { // -Z
                x1 = 4/16.0; x2 = 12/16.0;
                y1 = 4/16.0; y2 = 12/16.0;
                z1 = 0/16.0; z2 = 12/16.0;
            }
            case EAST -> { // +X
                x1 = 4/16.0; x2 = 16/16.0;
                y1 = 4/16.0; y2 = 12/16.0;
                z1 = 4/16.0; z2 = 12/16.0;
            }
            case WEST -> { // -X
                x1 = 0/16.0; x2 = 12/16.0;
                y1 = 4/16.0; y2 = 12/16.0;
                z1 = 4/16.0; z2 = 12/16.0;
            }
            case UP -> { // +Y
                x1 = 4/16.0; x2 = 12/16.0;
                y1 = 4/16.0; y2 = 16/16.0;
                z1 = 4/16.0; z2 = 12/16.0;
            }
            case DOWN -> { // -Y
                x1 = 4/16.0; x2 = 12/16.0;
                y1 = 0/16.0; y2 = 12/16.0;
                z1 = 4/16.0; z2 = 12/16.0;
            }
            default -> { // fallback: centered 8×8×12
                x1 = 4/16.0; x2 = 12/16.0;
                y1 = 4/16.0; y2 = 12/16.0;
                z1 = 2/16.0; z2 = 14/16.0;
            }
        }

        return new net.minecraft.world.phys.AABB(
                pos.getX() + x1 - eps, pos.getY() + y1 - eps, pos.getZ() + z1 - eps,
                pos.getX() + x2 + eps, pos.getY() + y2 + eps, pos.getZ() + z2 + eps
        );
    }

    /* -------------------------- Block entity -------------------------- */

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CocoonBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : (type == dev.galacticraft.mod.content.GCBlockEntityTypes.COCOON
                ? (lvl, pos, st, be) -> CocoonBlockEntity.serverTick(lvl, pos, st, (CocoonBlockEntity) be)
                : null);
    }

    /* -------------------------- Break behavior ------------------------ */

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CocoonBlockEntity cocoon) {
                cocoon.dropAllContents(level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    /* ------------------------- State container ------------------------ */

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    /* ---------------------- Rotation / mirroring ---------------------- */

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }
}