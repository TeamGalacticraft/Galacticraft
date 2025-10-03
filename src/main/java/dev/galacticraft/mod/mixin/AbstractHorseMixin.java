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

package dev.galacticraft.mod.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin extends LivingEntity {

    AbstractHorseMixin() {
        super(null, null);
    }

    @Shadow
    protected abstract boolean handleEating(Player player, ItemStack itemStack);

    @WrapMethod(method = "isFood")
    public boolean galacticraft$isFood(ItemStack itemStack, Operation<Boolean> original) {
        if (CannedFoodItem.isCannedFoodItem(itemStack)) {
            return original.call(CannedFoodItem.getFirst(itemStack));
        }
        return original.call(itemStack);
    }

    @WrapMethod(method = "fedFood")
    public InteractionResult galacticraft$fedFood(Player player, ItemStack itemStack, Operation<InteractionResult> original) {
        if (CannedFoodItem.isCannedFoodItem(itemStack)) {
            boolean success = this.handleEating(player, CannedFoodItem.getFirst(itemStack));
            if (success) {
                CannedFoodItem.removeOne(itemStack);
            }
            if (this.level().isClientSide()) {
                return InteractionResult.CONSUME;
            }
            return success ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return original.call(player, itemStack);
    }
}
