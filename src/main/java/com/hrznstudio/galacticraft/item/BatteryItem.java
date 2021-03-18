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
package com.hrznstudio.galacticraft.item;

import alexiil.mc.lib.attributes.AttributeProviderItem;
import alexiil.mc.lib.attributes.ItemAttributeList;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.attribute.energy.InfiniteCapacitor;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.energy.api.CapacitorView;
import com.hrznstudio.galacticraft.energy.impl.DefaultEnergyType;
import com.hrznstudio.galacticraft.energy.impl.SimpleCapacitor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BatteryItem extends Item implements AttributeProviderItem {
    private int ticks = 0;
    private final int capacity;

    public BatteryItem(Settings settings, int capacity) {
        super(settings.maxDamage(capacity));
        this.capacity = capacity;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
        if (this.isIn(group)) {
            final ItemStack[] stack = new ItemStack[]{new ItemStack(this)};
            stack[0].setDamage(this.capacity);
            list.add(stack[0]);
            stack[0] = stack[0].copy();

            if (this.capacity > 0) {
                GalacticraftEnergy.CAPACITOR.getFirst(new Reference<ItemStack>() {
                    @Override
                    public ItemStack get() {
                        return stack[0];
                    }

                    @Override
                    public boolean set(ItemStack stack1) {
                        stack[0] = stack1;
                        return true;
                    }

                    @Override
                    public boolean isValid(ItemStack stack) {
                        return stack.getItem() instanceof BatteryItem;
                    }
                }).setEnergy(this.capacity);
                list.add(stack[0]);
            }
        }
    }

    @Override
    public int getEnchantability() {
        return -1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return this.capacity <= 0;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> lines, TooltipContext context) {
        if (this.capacity > 0){
            CapacitorView capacitor = GalacticraftEnergy.CAPACITOR_VIEW.getFirst(stack);
            lines.add(new TranslatableText("tooltip.galacticraft-rewoven.energy_remaining", capacitor.getEnergy() + "/" + capacitor.getMaxCapacity()));
        } else {
            lines.add(new TranslatableText("tooltip.galacticraft-rewoven.energy_remaining", new TranslatableText("tooltip.galacticraft-rewoven.infinite").setStyle(Constants.Text.getRainbow(++ticks))));
            lines.add(new TranslatableText("tooltip.galacticraft-rewoven.creative_only").setStyle(Constants.Text.LIGHT_PURPLE_STYLE));
        }
        if (ticks >= 500) ticks -= 500;
        super.appendTooltip(stack, world, lines, context);
    }

    @Override
    public void addAllAttributes(Reference<ItemStack> reference, LimitedConsumer<ItemStack> limitedConsumer, ItemAttributeList<?> itemAttributeList) {
        ItemStack ref = reference.get().copy();
        if (this.capacity > 0) {
            SimpleCapacitor tank = new SimpleCapacitor(DefaultEnergyType.INSTANCE, this.capacity);
            tank.fromTag(ref.getOrCreateTag());
            tank.toTag(ref.getOrCreateTag());
            ref.setDamage(this.capacity - tank.getEnergy());
            reference.set(ref);
            itemAttributeList.offer(tank.addListener(view -> {
                        ItemStack stack = reference.get().copy();
                        stack.setDamage(this.capacity - view.getEnergy());
                        tank.toTag(stack.getOrCreateTag());
                        reference.set(stack);
                    }, () -> {}));
        } else {
            itemAttributeList.offer(new InfiniteCapacitor());
        }
    }
}
