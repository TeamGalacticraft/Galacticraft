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

package dev.galacticraft.mod.gametest.test;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public interface GalacticraftGameTest extends FabricGameTest {
    String SINGLE_BLOCK = "gc-test:single_block";

    default void runNext(GameTestHelper context, Runnable runnable) {
        context.runAtTickTime(context.getTick() + 1, runnable);
    }

    default void runAt(GameTestHelper context, int tick, Runnable runnable) {
        context.runAtTickTime(context.getTick() + tick, runnable);
    }

    default void runFinalTaskNext(GameTestHelper context, Runnable runnable) {
        context.runAtTickTime(context.getTick() + 1, () -> context.succeedWhen(runnable));
    }

    default void runFinalTaskAt(GameTestHelper context, int time, Runnable runnable) {
        context.runAtTickTime(context.getTick() + time, () -> context.succeedWhen(runnable));
    }

    default String formatItemStack(ItemStack stack) {
        if (stack == null) {
            return "null";
        } else if (stack.isEmpty()) {
            return "empty";
        } else {
            return String.format("%s %s", stack.getCount(), Registry.ITEM.getKey(stack.getItem()));
        }
    }
}
