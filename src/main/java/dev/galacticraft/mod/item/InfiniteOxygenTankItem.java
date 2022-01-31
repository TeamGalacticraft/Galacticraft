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

import dev.galacticraft.api.gas.Gas;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleViewIterator;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class InfiniteOxygenTankItem extends Item implements Storage<Gas>, StorageView<Gas> {
    private int ticks = (int) (Math.random() * 1000.0);

    public InfiniteOxygenTankItem(Settings settings) {
        super(settings);
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
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(new TranslatableText("tooltip.galacticraft.oxygen_remaining", new TranslatableText("tooltip.galacticraft.infinite").setStyle(Constant.Text.getRainbow(this.ticks))));
        tooltip.add(new TranslatableText("tooltip.galacticraft.creative_only").setStyle(Constant.Text.LIGHT_PURPLE_STYLE));
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return 13;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        if (++this.ticks > 1000) this.ticks -= 1000;
        return MathHelper.hsvToRgb(this.ticks / 1000.0f, 1, 1);
    }

    @Override
    public boolean supportsInsertion() {
        return false;
    }

    @Override
    public long insert(Gas gas, long l, TransactionContext transactionContext) {
        return 0;
    }

    @Override
    public long simulateInsert(Gas resource, long maxAmount, @Nullable TransactionContext transaction) {
        return 0;
    }

    @Override
    public boolean supportsExtraction() {
        return true;
    }

    @Override
    public long extract(Gas gas, long l, TransactionContext transactionContext) {
        if (gas == Gas.OXYGEN) {
            return l;
        }
        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return false;
    }

    @Override
    public Gas getResource() {
        return Gas.OXYGEN;
    }

    @Override
    public long getAmount() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getCapacity() {
        return Long.MAX_VALUE;
    }

    @Override
    public long simulateExtract(Gas resource, long maxAmount, @Nullable TransactionContext transaction) {
        if (resource == Gas.OXYGEN) {
            return maxAmount;
        }
        return 0;
    }

    @Override
    public Iterator<StorageView<Gas>> iterator(TransactionContext transactionContext) {
        return SingleViewIterator.create(this, transactionContext);
    }

    @Override
    public @Nullable StorageView<Gas> exactView(TransactionContext transaction, Gas resource) {
        if (resource == Gas.OXYGEN) {
            return this;
        }
        return null;
    }
}
