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

package dev.galacticraft.mod.gametest;

import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

public class ExtinguishableTestSuite implements GalacticraftGameTest {
    @GameTest(batch = "nonBreathable", template = SINGLE_BLOCK)
    public void extinguishTorchTest(GameTestHelper context) {
        final var torchPos = new BlockPos(0, 0, 0);

        context.setBlock(torchPos, Blocks.TORCH.defaultBlockState());

        context.succeedWhenBlockPresent(GCBlocks.UNLIT_TORCH, torchPos);
    }

    @GameTest(template = SINGLE_BLOCK)
    public void reigniteTorchTest(GameTestHelper context) {
        final var torchPos = new BlockPos(0, 0, 0);

        context.setBlock(torchPos, GCBlocks.UNLIT_TORCH.defaultBlockState());

        Player player = context.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.FLINT_AND_STEEL.getDefaultInstance());
        context.useBlock(torchPos, player);

        context.succeedWhenBlockPresent(Blocks.TORCH, torchPos);
    }
}
