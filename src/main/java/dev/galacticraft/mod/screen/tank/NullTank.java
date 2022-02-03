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

package dev.galacticraft.mod.screen.tank;

import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public final class NullTank extends Tank {
    public static final NullTank INSTANCE = new NullTank();

    private NullTank() {
        super(0, null, 0, 0, 0);
    }

    @Override
    public int[] getPositionData() {
        return new int[]{0, 0, 0};
    }

    @Override
    public void render(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY, boolean coloured, Int2IntMap color) {
    }

    @Override
    public void drawTooltip(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY) {
    }

    @Override
    public boolean isHoveredOverTank(int mouseX, int mouseY) {
        return false;
    }

    @Override
    public void renderHighlight(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY) {
    }

    @Override
    public boolean acceptStack(Reference<ItemStack> stack, LimitedConsumer<ItemStack> excess) {
        return false;
    }
}
