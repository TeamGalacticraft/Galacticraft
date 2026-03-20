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

package dev.galacticraft.mod.content.block.decoration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.content.block.entity.decoration.FlagBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlagBlock extends AbstractBannerBlock {
    public static final MapCodec<FlagBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            propertiesCodec(), DyeColor.CODEC.fieldOf("color").forGetter(FlagBlock::getColor)
    ).apply(instance, FlagBlock::new));

    public static final EnumProperty<Section> SECTION = EnumProperty.create("section", Section.class);

    public static final int HEIGHT = Section.values().length;
    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(6, 46, 6, 10, 48, 10),
            Block.box(7, 2, 7, 9, 46, 9),
            Block.box(6, 0, 6, 10, 2, 10)
    );

    public FlagBlock(Properties properties, DyeColor color) {
        super(color, properties);

        this.registerDefaultState(
                this.getStateDefinition().any()
                        .setValue(SECTION, Section.BOTTOM)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> compositeStateBuilder) {
        compositeStateBuilder.add(SECTION);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Section section = state.getValue(SECTION);
        return SHAPE.move(0, -section.ordinal(), 0);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis() != Direction.Axis.Y) {
            return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        }

        Section section = state.getValue(SECTION);
        if (section == Section.BOTTOM && direction == Direction.DOWN) { // Break flag if the block below can't support it
            if (!neighborState.isFaceSturdy(level, neighborPos, direction.getOpposite(), SupportType.CENTER)) {
                return Blocks.AIR.defaultBlockState();
            }
        } else if (!(section == Section.TOP && direction == Direction.UP) && !neighborState.is(state.getBlock())) { // Break flag if part of the pole is broken
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();

        if (pos.getY() > level.getMaxBuildHeight() - HEIGHT) {
            return null;
        }

        for (int i = 1; i < HEIGHT; i++) {
            pos = pos.above();
            if (!level.getBlockState(pos).canBeReplaced()) {
                return null;
            }
        }

        return super.getStateForPlacement(ctx);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        for (int i = 1; i < HEIGHT; i++) {
            pos = pos.above();
            level.setBlock(pos, state.setValue(SECTION, Section.values()[i]), 3);
        }

        if (placer != null && level.getBlockEntity(pos) instanceof FlagBlockEntity flag) {
            flag.setYaw(placer.getYHeadRot());
            flag.setChanged();
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        if (state.getValue(SECTION) == Section.BOTTOM) {
            return belowState.isFaceSturdy(level, below, Direction.UP, SupportType.CENTER);
        } else {
            return belowState.getBlock() instanceof FlagBlock;
        }
    }

    // Copy of DoublePlantBlock$preventDropFromBottomPart
    // Stops the flag from dropping when broken by a creative player
    @Override
    public @NotNull BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        Section section = state.getValue(SECTION);
        if (section != Section.BOTTOM && !level.isClientSide && (player.isCreative() || !player.hasCorrectToolForDrops(state))) {
            BlockPos bottomPos = pos.below(section.ordinal());
            BlockState bottomState = level.getBlockState(bottomPos);
            if (bottomState.is(this) && bottomState.getValue(SECTION) == Section.BOTTOM) {
                level.destroyBlock(bottomPos, false);
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(SECTION) == Section.BOTTOM ? new FlagBlockEntity(pos, state, this.getColor()) : null;
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull MapCodec<FlagBlock> codec() {
        return CODEC;
    }

    public enum Section implements StringRepresentable {
        BOTTOM("bottom"),
        MIDDLE("middle"),
        TOP("top");

        public final String name;

        Section(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}