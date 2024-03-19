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

package dev.galacticraft.mod.content.block.special;

import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.api.block.MultiBlockBase;
import dev.galacticraft.mod.api.block.MultiBlockPart;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.CryogenicChamberBlockEntity;
import dev.galacticraft.mod.particle.GCParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class CryogenicChamberBlock extends BaseEntityBlock implements MultiBlockBase {
    public static final MapCodec<CryogenicChamberBlock> CODEC = simpleCodec(CryogenicChamberBlock::new);
    private static final List<BlockPos> PARTS = List.of(new BlockPos(0, 1, 0), new BlockPos(0, 2, 0));
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CryogenicChamberBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    @Unmodifiable
    public List<BlockPos> getOtherParts(BlockState blockState) {
        return PARTS;
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
    }

    @Override
    public void onMultiBlockPlaced(Level level, BlockPos blockPos, BlockState blockState) {
        var isTop = false;
        if (!level.isClientSide) {
            for (var otherPart : this.getOtherParts(blockState)) {
                otherPart = otherPart.immutable().offset(blockPos);
                level.setBlockAndUpdate(otherPart, GCBlocks.CRYOGENIC_CHAMBER_PART.defaultBlockState().setValue(FACING, blockState.getValue(FACING)).setValue(CryogenicChamberPart.TOP, isTop));
                isTop = true;

                var part = level.getBlockEntity(otherPart);
                assert part != null; // This will never be null because level.setBlockState will put a blockentity there.
                ((MultiBlockPart) part).setBasePos(blockPos);
                part.setChanged();
            }
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, placer, itemStack);
        this.onMultiBlockPlaced(level, blockPos, blockState);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        BlockState state = super.playerWillDestroy(level, blockPos, blockState, player);
        for (var otherPart : this.getOtherParts(blockState)) {
            otherPart = otherPart.immutable().offset(blockPos);
            level.destroyBlock(otherPart, false);
        }
        return state;
    }

    @Override
    public void onPartDestroyed(Level level, Player player, BlockState blockState, BlockPos blockPos, BlockState partState, BlockPos partPos) {
        level.destroyBlock(blockPos, !player.isCreative());

        for (var otherPart : this.getOtherParts(blockState)) {
            otherPart = otherPart.immutable().offset(blockPos);
            if (!level.getBlockState(otherPart).isAir()) {
                level.destroyBlock(otherPart, false);
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        for (var otherPart : this.getOtherParts(blockState)) {
            otherPart = otherPart.immutable().offset(blockPos);
            if (!level.getBlockState(otherPart).canBeReplaced()) {
                return false;
            }
        }
        return super.canSurvive(blockState, level, blockPos);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CryogenicChamberBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return this.onMultiBlockUse(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    @Override
    public InteractionResult onMultiBlockUse(BlockState blockState, Level level, BlockPos basePos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide()) return InteractionResult.CONSUME;

        if(player.getCryogenicChamberCooldown() == 0) {
            player.beginCyroSleep();

            player.startSleepInBed(basePos).ifLeft(problem -> {
                if (problem.getMessage() != null) player.displayClientMessage(problem.getMessage(), true);

                player.endCyroSleep();
            });
        } else {
            player.displayClientMessage(Component.literal("The chamber is way to hot right now! It needs " + player.getCryogenicChamberCooldown() + " seconds to cool down before I sleep again."), false);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource random) {
        for (var i = 0; i < 3; i++) {
            level.addParticle(GCParticleTypes.CRYOGENIC_PARTICLE, blockPos.getX() + 0.3 + random.nextDouble() * 0.4, blockPos.getY(), blockPos.getZ() + 0.3 + random.nextDouble() * 0.4, 0.0, 0.05 + random.nextDouble() * 0.01, 0.0);
            level.addParticle(GCParticleTypes.CRYOGENIC_PARTICLE, blockPos.getX() + 0.3 + random.nextDouble() * 0.4, blockPos.getY() + 2.9F, blockPos.getZ() + 0.3 + random.nextDouble() * 0.4, 0.0, -0.05 - random.nextDouble() * 0.01, 0.0);
        }
    }

    @Override
    public VoxelShape getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return true;
    }
}