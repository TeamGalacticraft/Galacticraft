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

package dev.galacticraft.mod.item;

import dev.galacticraft.api.item.Schematic;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketSchematicItem extends Item implements Schematic {
    public RocketSchematicItem(Properties settings) {
        super(settings.stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);

        CompoundTag tag = stack.getOrCreateTag();
        if (Screen.hasShiftDown()) {
            if (tag.contains("red") && tag.contains("cone")) {
//                tooltip.add(Component.translatable("tooltip.galacticraft.tier", tag.getInt("tier")).setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
                tooltip.add(Component.translatable("tooltip.galacticraft.color"));
                tooltip.add(Component.translatable("tooltip.galacticraft.red", tag.getInt("red")).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                tooltip.add(Component.translatable("tooltip.galacticraft.green", tag.getInt("green")).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                tooltip.add(Component.translatable("tooltip.galacticraft.blue", tag.getInt("blue")).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
                tooltip.add(Component.translatable("tooltip.galacticraft.alpha", tag.getInt("alpha")).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));
                tooltip.add(Component.literal("-----").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));
                for (RocketPartType type : RocketPartType.values()) {
                    tooltip.add(Component.translatable("tooltip.galacticraft.part_type." + type.asString()).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)).append(RocketPart.getById(world.getRegistryManager(), new ResourceLocation(tag.getString(type.asString()))).name()));
                }
            } else {
                tooltip.add(Component.translatable("tooltip.galacticraft.blank").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                tooltip.add(Component.translatable("tooltip.galacticraft.blank_2").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.galacticraft.press_shift").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        }
    }
}
