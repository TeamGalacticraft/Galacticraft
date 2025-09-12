package dev.galacticraft.mod.content.block.environment;

import dev.galacticraft.mod.content.item.GCItems;
import mcp.mobius.waila.gui.hud.TooltipRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.galacticraft.mod.content.GCBlocks;

public class MoonTangleBlock extends Block implements BonemealableBlock {
    public enum Part implements StringRepresentable {
        MIDDLE("middle"),
        END("end"),
        FRUIT_TOP("fruit_top"),
        FRUIT_BOTTOM("fruit_bottom");

        private final String name;
        Part(String name) { this.name = name; }

        @Override public String getSerializedName() { return this.name; }

        @Override public String toString() { return this.name; }
    }

    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    public MoonTangleBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, Part.END));
    }

    @Override public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(PART)) {
            case MIDDLE, END -> box(4,0,4,12,16,12);
            case FRUIT_TOP -> Shapes.joinUnoptimized(box(2,0,2,14,12,14), box(4,12,4, 12, 16, 12), BooleanOp.OR);
            case FRUIT_BOTTOM -> Shapes.joinUnoptimized(box(2,12,2,14,16,14), box(6,3,6,10,12,10), BooleanOp.OR);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);

        if (aboveState.is(this) && aboveState.getValue(PART) == Part.FRUIT_BOTTOM) {
            return null;
        }

        BlockState placed = this.defaultBlockState();
        return canSurvive(placed, level, pos) ? placed : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide) {
            BlockPos above = pos.above();
            BlockState up = level.getBlockState(above);

            if (up.is(this) && up.getValue(PART) == Part.END) {
                level.setBlock(above, up.setValue(PART, Part.MIDDLE), Block.UPDATE_ALL_IMMEDIATE);
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Part part = state.getValue(PART);
        BlockPos above = pos.above();
        BlockState upState = level.getBlockState(above);

        if (part == Part.FRUIT_BOTTOM) {
            return upState.is(this) && upState.getValue(PART) == Part.FRUIT_TOP;
        }

        if (upState.is(GCBlocks.MOON_MOSS)) return true;
        if (upState.is(this)) {
            Part upPart = upState.getValue(PART);
            return upPart != Part.FRUIT_BOTTOM;
        }
        return false;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (dir == Direction.UP && !canSurvive(state, level, pos)) {
            level.scheduleTick(pos, this, 1);
        }

        if (state.getValue(PART) == Part.FRUIT_TOP && dir == Direction.DOWN) {
            if (!neighbor.is(this) || neighbor.getValue(PART) != Part.FRUIT_BOTTOM) {
                return state.setValue(PART, Part.END);
            }
        }
        if (state.getValue(PART) == Part.FRUIT_BOTTOM && dir == Direction.UP) {
            if (!neighbor.is(this) || neighbor.getValue(PART) != Part.FRUIT_TOP) {
                level.scheduleTick(pos, this, 1);
            }
        }

        return super.updateShape(state, dir, neighbor, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!canSurvive(state, level, pos)) {
            cascadeBreakFrom(level, pos);
            return;
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Only run the growth code 20% of the time
        if (random.nextInt(5) == 0) { //0-4 bounds
            grow(state, level, pos, random);
        }
    }

    private void grow(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(PART) != Part.END) return;

        int length = countVineLengthUp(level, pos);
        if (length >= 8) {
            tryFruitHere(level, pos, state);
            return;
        }

        BlockPos below = pos.below();
        BlockPos below2 = below.below();
        BlockPos below3 = below2.below();

        boolean r1 = isReplaceable(level, below);
        boolean r2 = isReplaceable(level, below2);
        boolean r3 = isReplaceable(level, below3);

        boolean floorTooClose =
                (r1 && !r2) ||
                        (r1 && r2 && !r3);

        if (floorTooClose) {
            tryFruitHere(level, pos, state);
            return;
        }

        if (random.nextFloat() < 0.20f) {
            if (tryFruitHere(level, pos, state)) return;
        }

        if (r1) {
            level.setBlock(pos, state.setValue(PART, Part.MIDDLE), Block.UPDATE_ALL_IMMEDIATE);
            level.setBlock(below, this.defaultBlockState().setValue(PART, Part.END), Block.UPDATE_ALL_IMMEDIATE);
        }
    }

    private static int countVineLengthUp(LevelReader level, BlockPos from) {
        int len = 1;
        BlockPos.MutableBlockPos m = from.mutable();
        m.move(Direction.UP);
        while (true) {
            BlockState s = level.getBlockState(m);
            if (!s.is(s.getBlock())) break;
            if (!(s.getBlock() instanceof MoonTangleBlock)) break;
            MoonTangleBlock.Part p = s.getValue(PART);
            if (p == Part.FRUIT_BOTTOM) break;
            len++;
            m.move(Direction.UP);
        }
        return len;
    }

    private static boolean isReplaceable(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos).isAir() || level.getBlockState(pos).canBeReplaced();
    }

    private boolean tryFruitHere(ServerLevel level, BlockPos pos, BlockState state) {
        BlockPos below = pos.below();
        if (isReplaceable(level, below)) {
            level.setBlock(pos, state.setValue(PART, Part.FRUIT_TOP), Block.UPDATE_ALL_IMMEDIATE);
            level.setBlock(below, this.defaultBlockState().setValue(PART, Part.FRUIT_BOTTOM), Block.UPDATE_ALL_IMMEDIATE);
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        if (state.getValue(PART) != Part.END) return false;
        BlockPos below = pos.below();
        return canReplace(level, below, level.getBlockState(below));
    }

    @Override public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) { return true; }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (state.getValue(PART) != Part.END) return;

        BlockPos below = pos.below();
        if (canReplace(level, below, level.getBlockState(below))) {
            level.setBlock(pos, state.setValue(PART, Part.FRUIT_TOP), Block.UPDATE_ALL_IMMEDIATE);
            level.setBlock(below, defaultBlockState().setValue(PART, Part.FRUIT_BOTTOM), Block.UPDATE_ALL_IMMEDIATE);
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        Part part = state.getValue(PART);

        if (part == Part.FRUIT_TOP) {
            // drop fruit item
            if (!level.isClientSide) {
                ItemStack drop = new ItemStack(GCItems.MOON_TANGLE_FRUIT); // replace with your item
                ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                level.addFreshEntity(entity);
            }

            // clear bottom half if present
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);
            if (belowState.is(this) && belowState.getValue(PART) == Part.FRUIT_BOTTOM) {
                level.setBlock(below, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            }
            // turn top back into END
            level.setBlock(pos, state.setValue(PART, Part.END), Block.UPDATE_ALL_IMMEDIATE);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (part == Part.FRUIT_BOTTOM) {
            // drop fruit item
            if (!level.isClientSide) {
                ItemStack drop = new ItemStack(GCItems.MOON_TANGLE_FRUIT);
                ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                level.addFreshEntity(entity);
            }

            // clear bottom + reset top
            BlockPos above = pos.above();
            BlockState up = level.getBlockState(above);
            if (up.is(this) && up.getValue(PART) == Part.FRUIT_TOP) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
                level.setBlock(above, up.setValue(PART, Part.END), Block.UPDATE_ALL_IMMEDIATE);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                       Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof BoneMealItem && state.getValue(PART) == Part.END) {
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);
            if (canReplace(level, below, belowState)) {
                if (!level.isClientSide) {
                    level.setBlock(pos, state.setValue(PART, Part.FRUIT_TOP), Block.UPDATE_ALL_IMMEDIATE);
                    level.setBlock(below, defaultBlockState().setValue(PART, Part.FRUIT_BOTTOM), Block.UPDATE_ALL_IMMEDIATE);
                    if (!player.getAbilities().instabuild) stack.shrink(1);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        if (!level.isClientSide()) {
            cascadeBreakFrom((ServerLevel) level, pos);
        }
    }

    private void cascadeBreakFrom(ServerLevel level, BlockPos pos) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        cursor.move(Direction.DOWN);
        while (level.getBlockState(cursor).is(this)) {
            level.destroyBlock(cursor, level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS));
            cursor.move(Direction.DOWN);
        }
        BlockPos up = pos.above();
        BlockState upState = level.getBlockState(up);
        if (upState.is(this)) {
            level.setBlock(up, upState.setValue(PART, Part.END), Block.UPDATE_ALL_IMMEDIATE);
        }
        if (level.getBlockState(pos).is(this)) {
            level.destroyBlock(pos, level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS));
        }
    }

    private static boolean canReplace(LevelReader level, BlockPos pos, BlockState state) {
        return state.isAir() || state.canBeReplaced();
    }

    public static boolean canAttachToCeiling(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos).is(GCBlocks.MOON_MOSS);
    }
}