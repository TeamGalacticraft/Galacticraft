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
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class OxygenTankItem extends Item {
    public final long capacity;

    public OxygenTankItem(Properties settings, int capacity) {
        super(settings.durability(capacity));
        this.capacity = capacity;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        StorageView<FluidVariant> storage = (StorageView<FluidVariant>) ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
        assert storage != null;

        return Math.round(13.0F - (float) (storage.getCapacity() - storage.getAmount()) * 13.0F / (float) storage.getCapacity());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        StorageView<FluidVariant> storage = (StorageView<FluidVariant>) ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
        assert storage != null;
        float scale = 1.0F - ((float) storage.getAmount() / (float) storage.getCapacity());
        return Constant.Text.Color.getStorageLevelColor(scale);
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
        StorageView<FluidVariant> storage = (StorageView<FluidVariant>) ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
        assert storage != null;
        lines.add(Component.translatable(Translations.Tooltip.OXYGEN_REMAINING, storage.getAmount() + "/" + storage.getCapacity()).setStyle(Constant.Text.Color.getStorageLevelStyle(1.0 - ((double) storage.getAmount() / (double) storage.getCapacity()))));
        super.appendHoverText(stack, world, lines, context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack copy = user.getItemInHand(hand).copy();
        try (Transaction transaction = Transaction.openOuter()) {
            long l = InventoryStorage.of(user.galacticraft$getOxygenTanks(), null).insert(ItemVariant.of(copy), copy.getCount(), transaction);
            if (l == copy.getCount()) {
                transaction.commit();
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, ItemStack.EMPTY);
            }
        }
        return super.use(world, user, hand);
    }
}
