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

package dev.galacticraft.mod.content.item;

import dev.galacticraft.mod.util.TooltipUtil;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class StandardWrenchItem extends Item {
    public StandardWrenchItem(Properties settings) {
        super(settings);
    }

    private static <T extends Comparable<T>> BlockState cycle(BlockState state, Property<T> property, boolean reverse) {
        return state.setValue(property, cycle(property.getPossibleValues(), state.getValue(property), reverse));
    }

    private static <T> T cycle(Iterable<T> values, T obj, boolean reverse) {
        return reverse ? Util.findPreviousInIterable(values, obj) : Util.findNextInIterable(values, obj);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        if (!world.isClientSide && player != null) {
            BlockPos pos = context.getClickedPos();
            this.use(player, world.getBlockState(pos), world, pos, context.getHand(), context.getItemInHand());
        }

        return InteractionResult.SUCCESS;
    }

    private void use(Player player, BlockState state, LevelAccessor world, BlockPos pos, InteractionHand hand, ItemStack stack) {
        Block block = state.getBlock();
        if (block.getStateDefinition().getProperty("facing") instanceof EnumProperty property) {
            Collection<?> possibleValues = property.getPossibleValues();
            if (possibleValues.size() <= Direction.values().length) {
                for (Object value : possibleValues) {
                    if (!(value instanceof Direction)) {
                        return;
                    }
                }

                BlockState newState = cycle(state, property, player.isShiftKeyDown());
                world.setBlock(pos, newState, 18);
                stack.hurtAndBreak(2, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag options) {
        TooltipUtil.appendLshiftTooltip(Translations.Tooltip.STANDARD_WRENCH, tooltip);
        super.appendHoverText(stack, context, tooltip, options);
    }
}
