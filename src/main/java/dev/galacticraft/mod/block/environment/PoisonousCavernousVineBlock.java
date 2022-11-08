/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.block.environment;

import dev.galacticraft.mod.block.GCBlocks;
import dev.galacticraft.mod.entity.damage.GCDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class PoisonousCavernousVineBlock extends CavernousVineBlock {
    public PoisonousCavernousVineBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void onCollided(LivingEntity entity) {
        super.onCollided(entity);
        entity.hurt(GCDamageSources.VINE_POISON, 5.0f);
        entity.setYRot(entity.getYRot() + 0.4F); // Spin the player
    }

    @Override
    public InteractionResult use(BlockState blockState, Level world, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if (player.getItemInHand(hand).getItem() instanceof ShearsItem) {
            world.setBlockAndUpdate(blockPos, GCBlocks.CAVERNOUS_VINE.defaultBlockState().setValue(VINES, blockState.getValue(VINES)));
            world.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.GRASS_BREAK, SoundSource.BLOCKS, 1f, 1f, true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }
}
