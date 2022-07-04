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

import dev.galacticraft.api.accessor.GearInventoryProvider;
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class OxygenTankItem extends Item {
    public final int capacity;

    public OxygenTankItem(Settings settings, int capacity) {
        super(settings.maxDamage(capacity));
        this.capacity = capacity;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
        if (this.isIn(group)) {
            ItemStack charged = new ItemStack(this);
            try (Transaction transaction = Transaction.openOuter()) {
                ContainerItemContext.withInitial(charged).find(FluidStorage.ITEM).insert(FluidVariant.of(Gases.OXYGEN), Long.MAX_VALUE, transaction);
                transaction.commit();
            }
            list.add(charged);

            ItemStack depleted = new ItemStack(this);
            try (Transaction transaction = Transaction.openOuter()) {
                ContainerItemContext.withInitial(charged).find(FluidStorage.ITEM).extract(FluidVariant.of(Gases.OXYGEN), Long.MAX_VALUE, transaction);
                transaction.commit();
            }
            list.add(depleted);
        }
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        try (Transaction transaction = Transaction.openOuter()) {
            StorageView<FluidVariant> storage = ContainerItemContext.withInitial(stack).find(FluidStorage.ITEM).exactView(transaction, FluidVariant.of(Gases.OXYGEN));
            assert storage != null;

            return (int) Math.round(13.0 - (((double) storage.getAmount() / (double) storage.getCapacity()) * 13.0));
        }
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        try (Transaction transaction = Transaction.openOuter()) {
            StorageView<FluidVariant> storage = ContainerItemContext.withInitial(stack).find(FluidStorage.ITEM).exactView(transaction, FluidVariant.of(Gases.OXYGEN));
            assert storage != null;
            double scale = 1.0 - Math.max(0.0, (double) storage.getAmount() / (double)storage.getCapacity());
            return ((int)(255 * scale) << 16) + (((int)(255 * ( 1.0 - scale))) << 8);
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
    public void appendTooltip(ItemStack stack, World world, List<Text> lines, TooltipContext context) {
        try (Transaction transaction = Transaction.openOuter()) {
            StorageView<FluidVariant> storage = ContainerItemContext.withInitial(stack).find(FluidStorage.ITEM).exactView(transaction, FluidVariant.of(Gases.OXYGEN));
            assert storage != null;
            lines.add(Text.translatable("tooltip.galacticraft.oxygen_remaining", storage.getAmount() + "/" + storage.getCapacity()).setStyle(Constant.Text.Color.getStorageLevelColor(1.0 - ((double)storage.getAmount() / (double)storage.getCapacity()))));
        }
        super.appendTooltip(stack, world, lines, context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack copy = user.getStackInHand(hand).copy();
        try (Transaction transaction = Transaction.openOuter()) {
            long l = InventoryStorage.of(((GearInventoryProvider) user).getOxygenTanks(), null).insert(ItemVariant.of(copy), copy.getCount(), transaction);
            if (l == copy.getCount()) {
                transaction.commit();
                return new TypedActionResult<>(ActionResult.SUCCESS, ItemStack.EMPTY);
            }
        }
        return super.use(world, user, hand);
    }
}
