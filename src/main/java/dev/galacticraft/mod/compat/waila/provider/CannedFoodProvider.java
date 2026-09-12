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

import dev.galacticraft.mod.content.block.decoration.CannedFoodBlock;
import dev.galacticraft.mod.content.block.entity.decoration.CannedFoodBlockEntity;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum CannedFoodProvider implements IBlockComponentProvider {
    INSTANCE;

    @Nullable
    @Override
    public ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        ItemStack stack = getTargetedCan(accessor);
        return stack.isEmpty() ? null : new ItemComponent(stack);
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        ItemStack stack = getTargetedCan(accessor);
        if (!stack.isEmpty()) {
            tooltip.addLine(stack.getHoverName());
        }
    }

    private static ItemStack getTargetedCan(IBlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof CannedFoodBlockEntity blockEntity)) {
            return ItemStack.EMPTY;
        }

        BlockPos pos = accessor.getPosition();
        Vec3 location = accessor.getBlockHitResult().getLocation();
        Direction direction = accessor.getBlockState().getValue(CannedFoodBlock.FACING);

        double minDist = 1.0D;
        ItemStack selected = ItemStack.EMPTY;

        float a = direction.getStepX();
        float b = direction.getStepZ();

        List<ItemStack> canContents = blockEntity.getCanContents();
        int canCount = canContents.size();

        if (canCount <= 0 || canCount >= CannedFoodBlock.POSITIONS.length) {
            return ItemStack.EMPTY;
        }

        for (int i = 0; i < canCount; i++) {
            float[] position = CannedFoodBlock.POSITIONS[canCount][i];

            double x = pos.getX() + (a * (position[2] - 8) + b * (position[0] - 8) + 5) / 16.0D;
            double y = pos.getY() + position[1] / 16.0D;
            double z = pos.getZ() + (b * (position[2] - 8) - a * (position[0] - 8) + 5) / 16.0D;

            AABB shape = new AABB(x, y, z, x + 0.375D, y + 0.5D, z + 0.375D);
            double dist = shape.distanceToSqr(location);

            if (dist < minDist) {
                minDist = dist;
                selected = canContents.get(i);
            }
        }

        return CannedFoodItem.getSize(selected) > 0 ? selected.copy() : ItemStack.EMPTY;
    }
}