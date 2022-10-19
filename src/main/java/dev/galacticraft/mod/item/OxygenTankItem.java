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

import dev.galacticraft.machinelib.api.gas.Gases;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class OxygenTankItem extends Item {
    public final int capacity;

    public OxygenTankItem(Properties settings, int capacity) {
        super(settings.durability(capacity));
        this.capacity = capacity;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> list) {
        if (this.allowedIn(group)) {
            ItemStack charged = getDefaultInstance();
            try (Transaction transaction = Transaction.openOuter()) {
                Storage<FluidVariant> storage = ContainerItemContext.withInitial(charged).find(FluidStorage.ITEM);
                storage.insert(FluidVariant.of(Gases.OXYGEN), Long.MAX_VALUE, transaction);
                transaction.commit();
                charged.getOrCreateTag().putLong(Constant.Nbt.VALUE, storage.exactView(FluidVariant.of(Gases.OXYGEN)).getAmount());
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
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        StorageView<FluidVariant> storage = ContainerItemContext.withInitial(stack).find(FluidStorage.ITEM).exactView(FluidVariant.of(Gases.OXYGEN));
        assert storage != null;

        return (int) Math.round(13.0 - (((double) storage.getAmount() / (double) storage.getCapacity()) * 13.0));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        StorageView<FluidVariant> storage = ContainerItemContext.withInitial(stack).find(FluidStorage.ITEM).exactView(FluidVariant.of(Gases.OXYGEN));
        assert storage != null;
        double scale = 1.0 - Math.max(0.0, (double) storage.getAmount() / (double) storage.getCapacity());
        return ((int) (255 * scale) << 16) + (((int) (255 * (1.0 - scale))) << 8);
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
    public void appendHoverText(ItemStack stack, Level world, List<Component> lines, TooltipFlag context) {
        StorageView<FluidVariant> storage = ContainerItemContext.withInitial(stack).find(FluidStorage.ITEM).exactView(FluidVariant.of(Gases.OXYGEN));
        assert storage != null;
        lines.add(Component.translatable("tooltip.galacticraft.oxygen_remaining", storage.getAmount() + "/" + storage.getCapacity()).setStyle(Constant.Text.Color.getStorageLevelColor(1.0 - ((double) storage.getAmount() / (double) storage.getCapacity()))));
        super.appendHoverText(stack, world, lines, context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack copy = user.getItemInHand(hand).copy();
        try (Transaction transaction = Transaction.openOuter()) {
            long l = InventoryStorage.of(user.getOxygenTanks(), null).insert(ItemVariant.of(copy), copy.getCount(), transaction);
            if (l == copy.getCount()) {
                transaction.commit();
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, ItemStack.EMPTY);
            }
        }
        return super.use(world, user, hand);
    }
}
