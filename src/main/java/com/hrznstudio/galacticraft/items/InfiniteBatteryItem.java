/*
 * Copyright (c) 2019-2021 HRZN LTD
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

import alexiil.mc.lib.attributes.AttributeProviderItem;
import alexiil.mc.lib.attributes.ItemAttributeList;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.energy.impl.DefaultEnergyType;
import com.hrznstudio.galacticraft.energy.impl.SimpleCapacitor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class InfiniteBatteryItem extends Item implements AttributeProviderItem {
    private int ticks = 0;

    public InfiniteBatteryItem(Properties settings) {
        super(settings.stacksTo(1));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(new TranslatableComponent("tooltip.galacticraft-rewoven.energy_remaining", new TranslatableComponent("tooltip.galacticraft-rewoven.infinite").setStyle(Constants.Styles.getRainbow(++ticks))));
        tooltip.add(new TranslatableComponent("tooltip.galacticraft-rewoven.creative_only").setStyle(Constants.Styles.LIGHT_PURPLE_STYLE));
        if (ticks >= 500) ticks -= 500;
        super.appendHoverText(stack, world, tooltip, context);
    }

    @Override
    public void addAllAttributes(Reference<ItemStack> reference, LimitedConsumer<ItemStack> limitedConsumer, ItemAttributeList<?> itemAttributeList) {
        itemAttributeList.offer(new SimpleCapacitor(DefaultEnergyType.INSTANCE, 4096) {
            @Override
            public int getEnergy() {
                return getMaxCapacity();
            }

            @Override
            public int insert(int amount, Simulation simulation) {
                return amount;
            }

            @Override
            public int extract(int amount, Simulation simulation) {
                return amount;
            }
        });
    }
}
