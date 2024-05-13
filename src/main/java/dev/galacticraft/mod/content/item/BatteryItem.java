/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.util.List;

public class BatteryItem extends Item implements SimpleEnergyItem {
    private final long capacity;
    private final long transfer;

    public BatteryItem(Properties settings, long capacity, long transfer) {
        super(settings.stacksTo(1));
        this.capacity = capacity;
        this.transfer = transfer;

        EnergyStorage.ITEM.registerForItems((stack, context) -> SimpleEnergyItem.createStorage(context, this.getEnergyCapacity(stack), this.getEnergyMaxInput(stack), this.getEnergyMaxOutput(stack)), this);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> lines, TooltipFlag context) {
        lines.add(Component.translatable(Translations.Tooltip.ENERGY_REMAINING, DrawableUtil.getEnergyDisplay(getStoredEnergy(stack)).setStyle(Constant.Text.Color.getStorageLevelStyle(1.0 - ((double)getStoredEnergy(stack)) / ((double)this.getEnergyCapacity(stack))))));
        super.appendHoverText(stack, world, lines, context);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return (int) Math.round(((double)getStoredEnergy(stack) / (double)this.getEnergyCapacity(stack)) * 13.0);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        double scale = 1.0 - Math.max(0.0, (double) getStoredEnergy(stack) / (double)this.getEnergyCapacity(stack));
        return ((int)(255 * scale) << 16) + (((int)(255 * ( 1.0 - scale))) << 8);
    }

    @Override
    public void onCraftedBy(@NotNull ItemStack battery, Level world, Player player) {
        CompoundTag batteryTag = battery.getOrCreateTag();
        battery.setTag(batteryTag);
    }

    @Override
    public int getEnchantmentValue() {
        return -1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairMaterial) {
        return false;
    }

    @Override
    public long getEnergyCapacity(ItemStack stack) {
        return this.capacity;
    }

    @Override
    public long getEnergyMaxInput(ItemStack stack) {
        return this.transfer;
    }

    @Override
    public long getEnergyMaxOutput(ItemStack stack) {
        return this.transfer;
    }
}