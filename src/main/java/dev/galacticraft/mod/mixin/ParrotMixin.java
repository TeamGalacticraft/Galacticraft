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

import dev.galacticraft.api.item.Accessory;
import dev.galacticraft.mod.content.GCAccessorySlots;
import dev.galacticraft.mod.network.c2s.OpenPetInventoryPayload;
import dev.galacticraft.mod.tag.GCItemTags;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Parrot.class)
public abstract class ParrotMixin extends TamableAnimal {

    ParrotMixin() {
        super(null, null);
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Parrot;isFlying()Z"), cancellable = true)
    public void galacticraft$openPetInventoryScreen(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        // super.mobInteract(player, hand);
        if (this.isTame() && this.isOwnedBy(player)) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (player.isSecondaryUseActive()) {
                ClientPlayNetworking.send(new OpenPetInventoryPayload(this.getId()));
                cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide));
            } else if (itemStack.getItem() instanceof Accessory) {
                Container inv = this.galacticraft$getGearInv();
                for (int slot = 0; slot < inv.getContainerSize(); ++slot) {
                    int i = (slot == GCAccessorySlots.PET_THERMAL_SLOT) ? GCAccessorySlots.THERMAL_ARMOR_SLOT_START + 1 : slot;
                    if (itemStack.is(GCAccessorySlots.SLOT_TAGS.get(i))) {
                        ItemStack itemStack2 = inv.getItem(slot);
                        if (ItemStack.matches(itemStack, itemStack2)) {
                            cir.setReturnValue(InteractionResult.FAIL);
                            return;
                        }
                        if (!this.level().isClientSide()) {
                            player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                        }
                        player.galacticraft$onEquipAccessory(itemStack2, itemStack);
                        ItemStack itemStack3 = itemStack2.isEmpty() ? itemStack : itemStack2.copyAndClear();
                        ItemStack itemStack4 = player.isCreative() ? itemStack.copy() : itemStack.copyAndClear();
                        inv.setItem(slot, itemStack4);
                        player.setItemInHand(hand, itemStack3);
                        cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide));
                        return;
                    }
                }
            }
        }
    }
}