/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.content.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.machinelib.api.component.MLDataComponents;
import dev.galacticraft.mod.content.item.OxygenTankItem;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;


public record ItemFullTankPredicate(MinMaxBounds.Ints amount) implements SingleComponentItemPredicate<Long> {
    public static final Codec<ItemFullTankPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(MinMaxBounds.Ints.CODEC.optionalFieldOf("machinelib:amount", MinMaxBounds.Ints.ANY).forGetter(ItemFullTankPredicate::amount)).apply(instance, ItemFullTankPredicate::new));

    @Override
    public DataComponentType<Long> componentType() {
        return MLDataComponents.AMOUNT;
    }

    @Override
    public boolean matches(ItemStack itemStack, Long amount) {
        if (itemStack.getItem() instanceof OxygenTankItem) {
            var storage = OxygenTankItem.getStorage(itemStack);
            return storage.getAmount() == storage.getCapacity();
        }
        return false;
    }

    public static ItemFullTankPredicate any() {
        return new ItemFullTankPredicate(MinMaxBounds.Ints.ANY);
    }
}