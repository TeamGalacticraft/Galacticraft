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

import dev.galacticraft.machinelib.api.gametest.SimpleGameTest;
import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExtinguishableTestSuite extends SimpleGameTest implements GalacticraftGameTest {
    private static final BlockPos TEST_POS = new BlockPos(0, 1, 0);

    @Override
    @GameTestGenerator
    public @NotNull List<TestFunction> registerTests() {
        List<TestFunction> tests = super.registerTests();

        List<TestCase> testCases = List.of(
                new TestCase("torch", Blocks.TORCH, GCBlocks.UNLIT_TORCH),
                new TestCase("wallTorch", Blocks.WALL_TORCH, GCBlocks.UNLIT_WALL_TORCH),
                new TestCase("lantern", Blocks.LANTERN, GCBlocks.UNLIT_LANTERN),
                new TestCase("campfire", Blocks.CAMPFIRE, Blocks.CAMPFIRE.defaultBlockState().setValue(BlockStateProperties.LIT, false)),
                new TestCase("soulTorch", Blocks.SOUL_TORCH, GCBlocks.UNLIT_SOUL_TORCH),
                new TestCase("soulWallTorch", Blocks.SOUL_WALL_TORCH, GCBlocks.UNLIT_SOUL_WALL_TORCH),
                new TestCase("soulLantern", Blocks.SOUL_LANTERN, GCBlocks.UNLIT_SOUL_LANTERN),
                new TestCase("soulCampfire", Blocks.SOUL_CAMPFIRE, Blocks.SOUL_CAMPFIRE.defaultBlockState().setValue(BlockStateProperties.LIT, false)),
                new TestCase("candle", Blocks.CANDLE.defaultBlockState().setValue(BlockStateProperties.LIT, true), Blocks.CANDLE),
                new TestCase("candleCake", Blocks.CANDLE_CAKE.defaultBlockState().setValue(BlockStateProperties.LIT, true), Blocks.CANDLE_CAKE),
                new TestCase("candleCheese", GCBlocks.CANDLE_MOON_CHEESE_WHEEL.defaultBlockState().setValue(BlockStateProperties.LIT, true), GCBlocks.CANDLE_MOON_CHEESE_WHEEL)
        );

        for (TestCase testCase : testCases) {
            tests.add(this.extinguishBlockWithoutOxygenTest(testCase));
            tests.add(this.doNotExtinguishBlockWithOxygenTest(testCase));
            tests.add(this.cannotIgniteBlockWithoutOxygenTest(testCase));
            tests.add(this.canIgniteBlockWithOxygenTest(testCase));
        }

        return tests;
    }

    public TestFunction extinguishBlockWithoutOxygenTest(TestCase testCase) {
        return this.createTest("nonBreathable", "extinguishBlockWithoutOxygen", testCase.name(), SINGLE_BLOCK, 1, 1, helper -> {
            helper.setBlock(TEST_POS, testCase.litState());
            checkBlock(helper, testCase.unlitState());
        });
    }

    public TestFunction doNotExtinguishBlockWithOxygenTest(TestCase testCase) {
        return this.createTest("breathable", "doNotExtinguishBlockWithOxygen", testCase.name(), SINGLE_BLOCK, 1, 1, helper -> {
            helper.setBlock(TEST_POS, testCase.litState());
            checkBlock(helper, testCase.litState());
        });
    }

    public TestFunction cannotIgniteBlockWithoutOxygenTest(TestCase testCase) {
        return this.createTest("nonBreathable", "cannotIgniteBlockWithoutOxygen", testCase.name(), SINGLE_BLOCK, 1, 1, helper -> {
            helper.setBlock(TEST_POS, testCase.unlitState());
            tryToIgniteBlock(helper);
            checkBlock(helper, testCase.unlitState());
        });
    }

    public TestFunction canIgniteBlockWithOxygenTest(TestCase testCase) {
        return this.createTest("breathable", "canIgniteBlockWithOxygen", testCase.name(), SINGLE_BLOCK, 1, 1, helper -> {
            helper.setBlock(TEST_POS, testCase.unlitState());
            tryToIgniteBlock(helper);
            checkBlock(helper, testCase.litState());
        });
    }

    private static void checkBlock(GameTestHelper helper, BlockState expected) {
        helper.succeedIf(() -> {
            BlockState observed = helper.getBlockState(TEST_POS);

            if (!expected.equals(observed)) {
                helper.fail(String.format("Expected %s but found %s", expected, observed));
            }
        });
    }

    private static void tryToIgniteBlock(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.FLINT_AND_STEEL.getDefaultInstance());
        helper.useBlock(TEST_POS, player);
    }

    public record TestCase(String name, BlockState litState, BlockState unlitState) {
        public TestCase(String name, BlockState litState, Block unlitBlock) {
            this(name, litState, unlitBlock.defaultBlockState());
        }

        public TestCase(String name, Block litBlock, BlockState unlitState) {
            this(name, litBlock.defaultBlockState(), unlitState);
        }

        public TestCase(String name, Block litBlock, Block unlitBlock) {
            this(name, litBlock.defaultBlockState(), unlitBlock.defaultBlockState());
        }
    }
}
