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
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import com.hrznstudio.galacticraft.energy.impl.DefaultEnergyType;
import com.hrznstudio.galacticraft.energy.impl.SimpleCapacitor;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BatteryItem extends Item implements AttributeProviderItem {
    public static final int MAX_ENERGY = 15000;

    public BatteryItem(Settings settings) {
        super(settings.maxCount(1).maxDamageIfAbsent(MAX_ENERGY));
    }

    public int getMaxCapacity() {
        return MAX_ENERGY;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> lines, TooltipContext context) {
        int charge = EnergyUtils.getCapacitorView(stack).getEnergy();
        if (charge < (MAX_ENERGY / 3.0)) {
            lines.add(new TranslatableText("tooltip.galacticraft-rewoven.energy_remaining", EnergyUtils.getDisplay(charge)).setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
        } else if (charge < (MAX_ENERGY / 3.0) * 2.0) {
            lines.add(new TranslatableText("tooltip.galacticraft-rewoven.energy_remaining", EnergyUtils.getDisplay(charge)).setStyle(Style.EMPTY.withColor(Formatting.GOLD)));
        } else {
            lines.add(new TranslatableText("tooltip.galacticraft-rewoven.energy_remaining", EnergyUtils.getDisplay(charge)).setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
        }
        super.appendTooltip(stack, world, lines, context);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            ItemStack charged = new ItemStack(this);
            EnergyUtils.setEnergy(charged, getMaxCapacity());
            stacks.add(charged);

            ItemStack depleted = new ItemStack(this);
            EnergyUtils.setEnergy(depleted, 0);
            depleted.setDamage(depleted.getMaxDamage() - 1);
            stacks.add(depleted);
        }
    }

    @Override
    public void onCraft(@NotNull ItemStack battery, World world, PlayerEntity player) {
        CompoundTag batteryTag = battery.getOrCreateTag();
        battery.setDamage(getMaxCapacity());
        battery.setTag(batteryTag);
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
    public boolean canRepair(ItemStack stack, ItemStack repairMaterial) {
        return false;
    }

    @Override
    public void addAllAttributes(Reference<ItemStack> reference, LimitedConsumer<ItemStack> limitedConsumer, ItemAttributeList<?> itemAttributeList) {
        ItemStack ref = reference.get().copy();
        SimpleCapacitor capacitor = new SimpleCapacitor(DefaultEnergyType.INSTANCE, this.getMaxCapacity());
        capacitor.fromTag(ref.getOrCreateTag());
        capacitor.toTag(ref.getOrCreateTag());
        ref.setDamage(capacitor.getMaxCapacity() - capacitor.getEnergy());
        reference.set(ref);
        itemAttributeList.offer(capacitor.addListener(capacitorView -> {
            ItemStack stack = reference.get().copy();
            stack.setDamage(capacitorView.getMaxCapacity() - capacitorView.getEnergy());
            capacitor.toTag(stack.getOrCreateTag());
            reference.set(stack);
        }, () -> {}));
    }
}