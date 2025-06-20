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

import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.storage.PlaceholderItemStorage;
import dev.galacticraft.mod.util.TooltipUtil;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class OxygenTankItem extends AccessoryItem {
    public final long capacity;

    public static StorageView<FluidVariant> getStorage(ItemStack stack) {
        StorageView<FluidVariant> storage = (StorageView<FluidVariant>) ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
        assert storage != null;
        return storage;
    }

    public static ItemStack getFullTank(Item item) {
        try (Transaction t = Transaction.openOuter()) {
            PlaceholderItemStorage itemStorage = new PlaceholderItemStorage();
            ContainerItemContext context = ContainerItemContext.ofSingleSlot(itemStorage);

            OxygenTankItem tankItem = (OxygenTankItem) item.asItem();
            itemStorage.setItem(tankItem);
            long capacity = tankItem.capacity;
            long inserted;

            do {
                inserted = context.find(FluidStorage.ITEM).insert(FluidVariant.of(Gases.OXYGEN), capacity, t);
            } while (inserted > 0 && capacity > 0);

            return itemStorage.variant.toStack();
        }
    }

    public OxygenTankItem(Properties settings, long capacity) {
        super(settings);
        this.capacity = capacity;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        StorageView<FluidVariant> storage = OxygenTankItem.getStorage(stack);
        return Math.round(13.0F - (float) (storage.getCapacity() - storage.getAmount()) * 13.0F / (float) storage.getCapacity());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        StorageView<FluidVariant> storage = OxygenTankItem.getStorage(stack);
        float scale = 1.0F - ((float) storage.getAmount() / (float) storage.getCapacity());
        return Constant.Text.getStorageLevelColor(scale);
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
    public boolean isFoil(ItemStack stack) {
        return this.capacity <= 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        this.appendOxygenTankTooltip(stack, context, tooltip, type);
        super.appendHoverText(stack, context, tooltip, type);
    }

    protected void appendOxygenTankTooltip(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        StorageView<FluidVariant> storage = OxygenTankItem.getStorage(stack);
        TooltipUtil.appendFluidRemainingTooltip(Translations.Tooltip.OXYGEN_REMAINING, storage.getAmount(), storage.getCapacity(), tooltip);
    }

}
