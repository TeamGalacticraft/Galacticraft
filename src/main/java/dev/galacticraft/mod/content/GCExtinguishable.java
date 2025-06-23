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

package dev.galacticraft.mod.content;

import dev.galacticraft.api.registry.ExtinguishableBlockRegistry;
import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class GCExtinguishable {
    public static void register() {
        // Normal Fire
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.LANTERN, state ->
                GCBlocks.UNLIT_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, state.getValue(LanternBlock.HANGING)).setValue(LanternBlock.WATERLOGGED, state.getValue(LanternBlock.WATERLOGGED))
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.TORCH, state ->
                GCBlocks.UNLIT_TORCH.defaultBlockState()
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.WALL_TORCH, state ->
                GCBlocks.UNLIT_WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, state.getValue(WallTorchBlock.FACING))
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.CAMPFIRE, state ->
                state.setValue(BlockStateProperties.LIT, false)
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.FIRE, Blocks.AIR.defaultBlockState());

        // Soul Fire
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.SOUL_LANTERN, state ->
                GCBlocks.UNLIT_SOUL_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, state.getValue(LanternBlock.HANGING)).setValue(LanternBlock.WATERLOGGED, state.getValue(LanternBlock.WATERLOGGED))
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.SOUL_TORCH, state ->
                GCBlocks.UNLIT_SOUL_TORCH.defaultBlockState()
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.SOUL_WALL_TORCH, state ->
                GCBlocks.UNLIT_SOUL_WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, state.getValue(WallTorchBlock.FACING))
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.SOUL_CAMPFIRE, state ->
                state.setValue(BlockStateProperties.LIT, false)
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.SOUL_FIRE, Blocks.AIR.defaultBlockState());

        // Candles
        ExtinguishableBlockRegistry.INSTANCE.add(BlockTags.CANDLES, state ->
                state.setValue(BlockStateProperties.LIT, false)
        );
        ExtinguishableBlockRegistry.INSTANCE.add(BlockTags.CANDLE_CAKES, state ->
                state.setValue(BlockStateProperties.LIT, false)
        );
    }
}
