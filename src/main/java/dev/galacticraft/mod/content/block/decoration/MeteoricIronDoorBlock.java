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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.tag.GCItemTags;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;

public class MeteoricIronDoorBlock extends DoorBlock {
    public static final MapCodec<MeteoricIronDoorBlock> CODEC = simpleCodec(properties -> new MeteoricIronDoorBlock(BlockSetType.IRON, properties, Constant.id(Constant.Block.METEORIC_IRON_DOOR_TOP)));

    private final ResourceLocation topBlockId;

    public MeteoricIronDoorBlock(BlockSetType type, BlockBehaviour.Properties properties, ResourceLocation topBlockId) {
        super(type, properties);
        this.topBlockId = topBlockId;
    }

    @Override
    public MapCodec<? extends DoorBlock> codec() {
        return CODEC;
    }

    public InteractionResult useTopPart(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return this.tryOpenDoor(state, level, pos, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return this.tryOpenDoor(state, level, pos, player);
    }

    private InteractionResult tryOpenDoor(BlockState state, Level level, BlockPos pos, Player player) {
        if (player.isCreative() || hasVillageAccessKey(player)) {
            state = state.cycle(OPEN);
            level.setBlock(pos, state, Block.UPDATE_CLIENTS);
            level.playSound(player, pos, state.getValue(OPEN) ? this.type().doorOpen() : this.type().doorClose(), SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
            this.ensureTopPart(level, pos, state);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable(Translations.Chat.METEORIC_DOOR_LOCKED), true);
        }
        return InteractionResult.CONSUME;
    }

    public static boolean hasVillageAccessKey(Player player) {
        Container accessories = player.galacticraft$getAccessories();
        for (int slot = 0; slot < accessories.getContainerSize(); ++slot) {
            ItemStack stack = accessories.getItem(slot);
            if (stack.is(GCItemTags.VILLAGE_ACCESS_KEYS)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }

        BlockPos topPos = context.getClickedPos().above(2);
        return topPos.getY() < context.getLevel().getMaxBuildHeight() && context.getLevel().getBlockState(topPos).canBeReplaced()
                ? state
                : null;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        super.onPlace(state, level, pos, oldState, moved);
        if (!oldState.is(state.getBlock())) {
            this.ensureTopPart(level, pos, state);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        if (!updated.isAir() && updated.getValue(HALF) == DoubleBlockHalf.UPPER && direction == Direction.UP && !neighborState.is(this.getTopBlock())) {
            return Blocks.AIR.defaultBlockState();
        }
        return updated;
    }

    private void ensureTopPart(Level level, BlockPos pos, BlockState state) {
        BlockPos upperPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos : pos.above();
        BlockState upperState = state.getValue(HALF) == DoubleBlockHalf.UPPER ? state : level.getBlockState(upperPos);
        if (!upperState.is(this) || upperState.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return;
        }

        BlockPos topPos = upperPos.above();
        BlockState topState = level.getBlockState(topPos);
        BlockState desiredState = this.getTopBlock().defaultBlockState()
                .setValue(MeteoricIronDoorTopBlock.FACING, upperState.getValue(FACING))
                .setValue(MeteoricIronDoorTopBlock.OPEN, upperState.getValue(OPEN))
                .setValue(MeteoricIronDoorTopBlock.HINGE, upperState.getValue(HINGE));

        if (topState.is(this.getTopBlock())) {
            if (!topState.equals(desiredState)) {
                level.setBlock(topPos, desiredState, Block.UPDATE_ALL);
            }
        } else if (topState.canBeReplaced()) {
            level.setBlock(topPos, desiredState, Block.UPDATE_ALL);
        }
    }

    private Block getTopBlock() {
        return BuiltInRegistries.BLOCK.get(this.topBlockId);
    }
}