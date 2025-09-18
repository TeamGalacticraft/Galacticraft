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

package dev.galacticraft.mod.content.block.environment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class UnlitWallTorchBlock extends WallTorchBlock {
    public static final MapCodec<UnlitWallTorchBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Block.CODEC.fieldOf("lit_block").forGetter(unlitTorch -> unlitTorch.litBlock),
            propertiesCodec()
    ).apply(instance, UnlitWallTorchBlock::new));

    private final Block litBlock;

    public UnlitWallTorchBlock(Block litBlock, Properties properties) {
        super(null, properties);
        this.litBlock = litBlock;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var itemStack = player.getItemInHand(hand);

        if (itemStack.is(ItemTags.CREEPER_IGNITERS)) {
            level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            level.setBlockAndUpdate(pos, this.litBlock.defaultBlockState().setValue(WallTorchBlock.FACING, state.getValue(WallTorchBlock.FACING)));
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos, itemStack);
                itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }

            return ItemInteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public void animateTick(BlockState blockState, Level world, BlockPos blockPos, RandomSource random) {
        // stop particles from spawning
    }

    @Override
    public boolean galacticraft$hasLegacyExtinguishTransform(BlockState state) {
        return false; // overrides the mixin
    }

    @Override
    public boolean galacticraft$hasAtmosphereListener(BlockState state) {
        return false; // overrides the mixin
    }
}