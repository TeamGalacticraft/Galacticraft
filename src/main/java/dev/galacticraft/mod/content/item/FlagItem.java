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

package dev.galacticraft.mod.content.item;

import dev.galacticraft.mod.content.block.entity.decoration.FlagBlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FlagItem extends BlockItem {
    public FlagItem(Block block, Properties properties) {
        super(block, properties);
    }

    public DyeColor getColor() {
        if (this.getBlock() instanceof AbstractBannerBlock banner) {
            return banner.getColor();
        } else {
            return DyeColor.WHITE;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        BannerItem.appendHoverTextFromBannerBlockEntityTag(stack, tooltip);
    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        if (result.indicateItemUse() && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof FlagBlockEntity flag) {
            float rotation = 0;
            if (context.getPlayer() != null) {
                rotation = context.getPlayer().getYHeadRot();
            }
            flag.setYaw(rotation);
        }

        return result;
    }

    public static boolean isFlagItem(ItemStack stack) {
        return stack.getItem() instanceof FlagItem && stack.has(DataComponents.BANNER_PATTERNS);
    }

    public static ItemStack fromBanner(ItemStack stack) {
        DyeColor color = DyeColor.WHITE;
        if (stack.getItem() instanceof BannerItem banner) {
            color = banner.getColor();
        }

        ItemStack flag = new ItemStack(GCItems.FLAGS.get(color));
        flag.set(DataComponents.BANNER_PATTERNS, stack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
        flag.set(DataComponents.CUSTOM_NAME, stack.get(DataComponents.CUSTOM_NAME));
        return flag;
    }

    public static ItemStack toBanner(ItemStack stack) {
        DyeColor color = DyeColor.WHITE;
        if (stack.getItem() instanceof FlagItem flag) {
            color = flag.getColor();
        }

        ItemStack banner = new ItemStack(BannerBlock.byColor(color).asItem());
        banner.set(DataComponents.BANNER_PATTERNS, stack.get(DataComponents.BANNER_PATTERNS));
        return banner;
    }
}