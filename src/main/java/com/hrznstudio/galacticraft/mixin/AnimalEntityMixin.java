/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.mixin;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import com.hrznstudio.galacticraft.accessor.AnimalGearAccessor;
import com.hrznstudio.galacticraft.items.AnimalThermalArmorItem;
import com.hrznstudio.galacticraft.items.OxygenGearItem;
import com.hrznstudio.galacticraft.items.OxygenMaskItem;
import com.hrznstudio.galacticraft.items.OxygenTankItem;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin implements AnimalGearAccessor {
    @Unique
    private FullFixedItemInv gearInv = new FullFixedItemInv(5);

    @Override
    public FullFixedItemInv getGearInv() {
        return gearInv;
    }

    @Override
    public CompoundTag writeGearToNbt(CompoundTag tag) {
        return this.gearInv.toTag(tag);
    }

    @Override
    public void readGearFromNbt(CompoundTag tag) {
        this.gearInv.fromTag(tag);
    }

    public void setGearInv(FullFixedItemInv gearInv) {
        this.gearInv = gearInv;
    }

    @Override
    public boolean hasThermalPadding() {
        return !gearInv.getInvStack(4).isEmpty();
    }

    @Override
    public boolean hasOxygenMask() {
        return !gearInv.getInvStack(0).isEmpty();
    }

    @Inject(method = "interactMob", at = @At("RETURN"), cancellable = true)
    public void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() != ActionResult.SUCCESS || cir.getReturnValue() != ActionResult.CONSUME) {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() instanceof OxygenMaskItem) {
                if (!gearInv.getInvStack(0).isEmpty()) {
                    player.setStackInHand(hand, gearInv.getInvStack(0).copy());
                }
                gearInv.setInvStack(0, stack, Simulation.ACTION);
                cir.setReturnValue(ActionResult.success(player.world.isClient));
            } else if (stack.getItem() instanceof OxygenGearItem) {
                if (!gearInv.getInvStack(1).isEmpty()) {
                    player.setStackInHand(hand, gearInv.getInvStack(1).copy());
                }
                gearInv.setInvStack(1, stack, Simulation.ACTION);
                cir.setReturnValue(ActionResult.success(player.world.isClient));
            } else if (stack.getItem() instanceof OxygenTankItem) {
                if (gearInv.getInvStack(2).isEmpty()) {
                    gearInv.setInvStack(2, stack, Simulation.ACTION);
                } else if (gearInv.getInvStack(3).isEmpty()) {
                    gearInv.setInvStack(3, stack, Simulation.ACTION);
                } else {
                    if (gearInv.getInvStack(2).getDamage() < gearInv.getInvStack(3).getDamage()) {
                        player.setStackInHand(hand, gearInv.getInvStack(3).copy());
                        gearInv.setInvStack(3, stack, Simulation.ACTION);
                    } else {
                        player.setStackInHand(hand, gearInv.getInvStack(2).copy());
                        gearInv.setInvStack(2, stack, Simulation.ACTION);
                    }
                }
                cir.setReturnValue(ActionResult.success(player.world.isClient));
            } else if (stack.getItem() instanceof AnimalThermalArmorItem) {
                if (!gearInv.getInvStack(4).isEmpty()) {
                    player.setStackInHand(hand, gearInv.getInvStack(4).copy());
                }
                gearInv.setInvStack(4, stack, Simulation.ACTION);
                cir.setReturnValue(ActionResult.success(player.world.isClient));
            }
        }
    }
}
