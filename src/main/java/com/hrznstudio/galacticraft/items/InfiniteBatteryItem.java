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

import com.hrznstudio.galacticraft.api.item.EnergyHolderItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHolder;
import team.reborn.energy.EnergyTier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class InfiniteBatteryItem extends Item implements EnergyHolderItem, EnergyHolder {
    public InfiniteBatteryItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isInfinite() {
        return true;
    }

    @Override
    public int getMaxEnergy(ItemStack battery) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack itemStack_1) {
        return true;
    }

    @Override
    public double getMaxStoredPower() {
        return Double.MAX_VALUE;
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.INFINITE;
    }

    @Override
    public void onCraft(ItemStack itemStack_1, World world_1, PlayerEntity playerEntity_1) {
        super.onCraft(itemStack_1, world_1, playerEntity_1);
        Energy.of(itemStack_1).set(Double.MAX_VALUE);
    }

    @Override
    public void appendStacks(ItemGroup itemGroup_1, DefaultedList<ItemStack> defaultedList_1) {
        if (itemGroup_1 == GalacticraftItems.ITEMS_GROUP) {
            ItemStack stack = new ItemStack(this);
            Energy.of(stack).set(Double.MAX_VALUE);
            defaultedList_1.add(stack);
        }
    }

    @Override
    public boolean canRepair(ItemStack itemStack_1, ItemStack itemStack_2) {
        return false;
    }
}
