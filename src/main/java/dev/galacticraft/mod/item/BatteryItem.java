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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleBatteryItem;

import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class BatteryItem extends Item implements SimpleBatteryItem {
    private final long capacity;
    private final long transfer;

    public BatteryItem(Properties settings, long capacity, long transfer) {
        super(settings.stacksTo(1));
        this.capacity = capacity;
        this.transfer = transfer;

        EnergyStorage.ITEM.registerForItems((itemStack, context) -> SimpleBatteryItem.createStorage(ContainerItemContext.withInitial(itemStack), this.getEnergyCapacity(), this.getEnergyMaxInput(), this.getEnergyMaxOutput()), this);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> lines, TooltipFlag context) {
        EnergyStorage energyStorage = ContainerItemContext.withInitial(stack).find(EnergyStorage.ITEM);
        lines.add(Component.translatable("tooltip.galacticraft.energy_remaining", DrawableUtil.getEnergyDisplay(energyStorage.getAmount())).setStyle(Constant.Text.Color.getStorageLevelColor(1.0 - ((double)energyStorage.getAmount()) / ((double)energyStorage.getCapacity()))));
        super.appendHoverText(stack, world, lines, context);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> stacks) {
        if (this.allowedIn(group)) {
            ItemStack charged = new ItemStack(this);
            try (Transaction transaction = Transaction.openOuter()) {
                ContainerItemContext.withInitial(charged).find(EnergyStorage.ITEM).insert(Long.MAX_VALUE, transaction);
                transaction.commit();
            }
            stacks.add(charged);

            ItemStack depleted = new ItemStack(this);
            try (Transaction transaction = Transaction.openOuter()) {
                ContainerItemContext.withInitial(charged).find(EnergyStorage.ITEM).extract(Long.MAX_VALUE, transaction);
                transaction.commit();
            }
            stacks.add(depleted);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        EnergyStorage storage = ContainerItemContext.withInitial(stack).find(EnergyStorage.ITEM);
        assert storage != null;

        return (int) Math.round(13.0 - (((double)storage.getAmount() / (double)storage.getCapacity()) * 13.0));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        EnergyStorage storage = ContainerItemContext.withInitial(stack).find(EnergyStorage.ITEM);
        assert storage != null;
        double scale = 1.0 - Math.max(0.0, (double) storage.getAmount() / (double)storage.getCapacity());
        return ((int)(255 * scale) << 16) + (((int)(255 * ( 1.0 - scale))) << 8);
    }

    @Override
    public void onCraftedBy(@NotNull ItemStack battery, Level world, Player player) {
        CompoundTag batteryTag = battery.getOrCreateTag();
        battery.setDamageValue(this.getMaxDamage());
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
    public long getEnergyCapacity() {
        return this.capacity;
    }

    @Override
    public long getEnergyMaxInput() {
        return this.transfer;
    }

    @Override
    public long getEnergyMaxOutput() {
        return this.transfer;
    }
}