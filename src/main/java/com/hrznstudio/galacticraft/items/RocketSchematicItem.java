/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.items;

import com.hrznstudio.galacticraft.api.item.SchematicItem;
import com.hrznstudio.galacticraft.api.rocket.RocketPartType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketSchematicItem extends Item implements SchematicItem {
    public RocketSchematicItem(Settings settings) {
        super(settings.maxCount(1).rarity(Rarity.EPIC));
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        CompoundTag tag = stack.getOrCreateTag();
        if (Screen.hasShiftDown()) {
            if (tag.containsKey("red") && tag.containsKey("cone") && tag.containsKey("tier")) {
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.tier", tag.getInt("tier")).setStyle(new Style().setColor(Formatting.DARK_GRAY)));
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.color"));
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.red", tag.getInt("red")).setStyle(new Style().setColor(Formatting.DARK_RED)));
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.green", tag.getInt("green")).setStyle(new Style().setColor(Formatting.DARK_GREEN)));
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.blue", tag.getInt("blue")).setStyle(new Style().setColor(Formatting.DARK_BLUE)));
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.alpha", tag.getInt("alpha")).setStyle(new Style().setColor(Formatting.WHITE)));
                tooltip.add(new LiteralText("-----").setStyle(new Style().setColor(Formatting.AQUA)));
                for (RocketPartType type : RocketPartType.values()) {
                    String s = new Identifier(tag.getString(type.asString())).getPath();
                    if (!(new TranslatableText("tooltip." + new Identifier(tag.getString(type.asString())).getNamespace() + "." + new Identifier(tag.getString(type.asString())).getPath() + ".name").asString()
                            .equals("tooltip." + new Identifier(tag.getString(type.asString())).getNamespace() + "." + new Identifier(tag.getString(type.asString())).getPath() + ".name"))) {
                        s = new TranslatableText("tooltip." + new Identifier(tag.getString(type.asString())).getNamespace() +
                                "." + new Identifier(tag.getString(type.asString())).getPath() + ".name").asString();
                    }
                    tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.part_type." + type.asString(), s).setStyle(new Style().setColor(Formatting.GRAY)));
                }
            } else {
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.blank").setStyle(new Style().setColor(Formatting.GRAY)));
                tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.blank_2").setStyle(new Style().setColor(Formatting.GRAY)));
            }
        } else {
            tooltip.add(new TranslatableText("tooltip.galacticraft-rewoven.press_shift").setStyle(new Style().setColor(Formatting.GRAY)));
        }
    }
}
