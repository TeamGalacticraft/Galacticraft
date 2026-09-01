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

package dev.galacticraft.mod.compat.waila.provider;

import dev.galacticraft.mod.content.block.decoration.FlagBlock;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITargetRedirector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public enum FlagNameProvider implements IBlockComponentProvider {
    INSTANCE;

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public ITargetRedirector.@Nullable Result redirect(ITargetRedirector redirect, IBlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        Level level = accessor.getWorld();
        if (state.getValue(FlagBlock.SECTION) != FlagBlock.Section.BOTTOM) {
            BlockPos basePos = FlagBlock.getBaseBlockPos(state, accessor.getPosition());
            if (level.getBlockState(basePos).getBlock() instanceof FlagBlock) {
                BlockHitResult hitResult = new BlockHitResult(basePos.getCenter(), accessor.getSide(), basePos, accessor.getBlockHitResult().isInside());
                return redirect.to(hitResult);
            }
        }
        return null;
    }
}
