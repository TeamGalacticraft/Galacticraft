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

package dev.galacticraft.mod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

import static dev.galacticraft.mod.content.item.CannedFoodItem.getCanFoodProperties;
import static dev.galacticraft.mod.content.item.CannedFoodItem.isCannedFoodItem;

@Mixin(DataComponentHolder.class)
public interface FabricItemMixin {

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private <T> void get(DataComponentType<? extends T> type, CallbackInfoReturnable<T> cir) {
        DataComponentHolder holder = (DataComponentHolder) (Object) this;
        ItemStack stack = getItemStack(holder);
        if (isCannedFoodItem(stack)) {
            //data component holder has parent of canned food item
            if (type.equals(DataComponents.FOOD)) {
                FoodProperties foodProperties = getCanFoodProperties(stack, Minecraft.getInstance().player);
                cir.setReturnValue((T) foodProperties);
            }
        }
    }

    private ItemStack getItemStack(DataComponentHolder holder) {
        if (holder instanceof ItemStack) {
            return (ItemStack) holder;
        }

        try {
            Field itemStackField = holder.getClass().getDeclaredField("itemStack");
            itemStackField.setAccessible(true);
            return (ItemStack) itemStackField.get(holder);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return ItemStack.EMPTY;
    }
}
