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

package dev.galacticraft.mod.events;

import dev.galacticraft.api.item.Accessory;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.accessor.PetInventoryOpener;
import dev.galacticraft.mod.content.GCAccessorySlots;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public class GCInteractionEventHandlers {
    public static void init() {
        UseItemCallback.EVENT.register(GCInteractionEventHandlers::onPlayerUseItem);
        UseEntityCallback.EVENT.register(GCInteractionEventHandlers::onPlayerUseEntity);
    }

    public static InteractionResultHolder<ItemStack> onPlayerUseItem(Player player, Level level, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (CannedFoodItem.canAddToCan(itemStack.getItem())) {
            if (player.isCreative()) {
                return InteractionResultHolder.pass(itemStack);
            } else if (cannotEatWithMask(player) || cannotEatInNoAtmosphere(player, level)) {
                return InteractionResultHolder.fail(itemStack);
            }
        }
        if (CannedFoodItem.isCannedFoodItem(itemStack)) {
            if (!player.galacticraft$hasMask() && cannotEatInNoAtmosphere(player, level)) {
                return InteractionResultHolder.fail(itemStack);
            }
        }
        return InteractionResultHolder.pass(itemStack);
    }

    public static InteractionResult onPlayerUseEntity(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (entity instanceof TamableAnimal animal && animal.isTame() && animal.isOwnedBy(player)) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (player.isSecondaryUseActive() || player.isSpectator()) {
                ((PetInventoryOpener) player).galacticraft$sendOpenPetInventory(animal.getId());
                return InteractionResult.SUCCESS_NO_ITEM_USED;
            } else if (itemStack.getItem() instanceof Accessory) {
                return equipAccessory(player, level, hand, animal, itemStack);
            } else if (animal instanceof Parrot parrot) {
                return feedParrot(player, level, parrot, itemStack);
            } else {
                return feedAnimal(player, level, animal, itemStack);
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult equipAccessory(Player player, Level level, InteractionHand hand, TamableAnimal animal, ItemStack itemStack) {
        Container inv = animal.galacticraft$getGearInv();
        for (int slot = 0; slot < inv.getContainerSize(); ++slot) {
            int i = (slot == GCAccessorySlots.PET_THERMAL_SLOT) ? GCAccessorySlots.THERMAL_ARMOR_SLOT_START + 1 : slot;
            if (itemStack.is(GCAccessorySlots.SLOT_TAGS.get(i))) {
                ItemStack itemStack2 = inv.getItem(slot);
                if (ItemStack.matches(itemStack, itemStack2)) {
                    return InteractionResult.FAIL;
                }
                if (!level.isClientSide()) {
                    player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                }
                player.galacticraft$onEquipAccessory(itemStack2, itemStack);
                ItemStack itemStack3 = itemStack2.isEmpty() ? itemStack : itemStack2.copyAndClear();
                ItemStack itemStack4 = player.isCreative() ? itemStack.copy() : itemStack.copyAndClear();
                inv.setItem(slot, itemStack4);
                player.setItemInHand(hand, itemStack3);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult feedAnimal(Player player, Level level, TamableAnimal animal, ItemStack itemStack) {
        if (animal.isFood(itemStack) && CannedFoodItem.canAddToCan(itemStack.getItem())) {
            if (cannotFeedWithMask(player, animal) || cannotFeedInNoAtmosphere(player, level, animal)) {
                return InteractionResult.FAIL;
            }
        }
        if (CannedFoodItem.isCannedFoodItem(itemStack)) {
            ItemStack food = CannedFoodItem.getFirst(itemStack);
            if (food.isEmpty() || !animal.isFood(food)) return InteractionResult.PASS;

            if (animal.getHealth() < animal.getMaxHealth()) {
                if (!animal.galacticraft$hasMask() && cannotFeedInNoAtmosphere(player, level, animal)) {
                    return InteractionResult.FAIL;
                }

                FoodProperties foodProperties = food.get(DataComponents.FOOD);
                float healAmount = foodProperties != null ? (float) foodProperties.nutrition() : 1.0F;
                if (animal instanceof Wolf) {
                    healAmount *= 2.0F;
                }
                animal.heal(healAmount);
                if (!player.isCreative()) {
                    CannedFoodItem.removeOne(itemStack);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult feedParrot(Player player, Level level, Parrot parrot, ItemStack itemStack) {
        if (itemStack.is(ItemTags.PARROT_POISONOUS_FOOD) && CannedFoodItem.canAddToCan(itemStack.getItem())) {
            if (cannotFeedWithMask(player, parrot) || cannotFeedInNoAtmosphere(player, level, parrot)) {
                return InteractionResult.FAIL;
            }
        }
        if (CannedFoodItem.isCannedFoodItem(itemStack)) {
            ItemStack food = CannedFoodItem.getFirst(itemStack);
            if (food.isEmpty() || !food.is(ItemTags.PARROT_POISONOUS_FOOD)) return InteractionResult.PASS;

            if (!parrot.galacticraft$hasMask() && cannotFeedInNoAtmosphere(player, level, parrot)) {
                return InteractionResult.FAIL;
            }

            if (!player.isCreative()) {
                CannedFoodItem.removeOne(itemStack);
            }
            parrot.addEffect(new MobEffectInstance(MobEffects.POISON, 900));
            if (player.isCreative() || !parrot.isInvulnerable()) {
                parrot.hurt(parrot.damageSources().playerAttack(player), Float.MAX_VALUE);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    private static boolean cannotEatInNoAtmosphere(Player player, Level level) {
        if (Galacticraft.CONFIG.cannotEatInNoAtmosphere() && !level.galacticraft$isBreathable() && !level.galacticraft$isBreathable(player.getEyePosition())) {
            player.displayClientMessage(Component.translatable(Translations.Chat.CANNOT_EAT_IN_NO_ATMOSPHERE).withStyle(Constant.Text.RED_STYLE), true);
            return true;
        }
        return false;
    }

    private static boolean cannotFeedInNoAtmosphere(Player player, Level level, LivingEntity entity) {
        if (Galacticraft.CONFIG.cannotEatInNoAtmosphere() && !level.galacticraft$isBreathable() && !level.galacticraft$isBreathable(player.getEyePosition())) {
            player.displayClientMessage(Component.translatable(Translations.Chat.CANNOT_FEED_IN_NO_ATMOSPHERE).withStyle(Constant.Text.RED_STYLE), true);
            return true;
        }
        return false;
    }

    private static boolean cannotEatWithMask(Player player) {
        if (Galacticraft.CONFIG.cannotEatWithMask() && player.galacticraft$hasMask()) {
            player.displayClientMessage(Component.translatable(Translations.Chat.CANNOT_EAT_WITH_MASK).withStyle(Constant.Text.RED_STYLE), true);
            return true;
        }
        return false;
    }

    private static boolean cannotFeedWithMask(Player player, LivingEntity entity) {
        if (Galacticraft.CONFIG.cannotEatWithMask() && entity.galacticraft$hasMask()) {
            player.displayClientMessage(Component.translatable(Translations.Chat.CANNOT_FEED_WITH_MASK).withStyle(Constant.Text.RED_STYLE), true);
            return true;
        }
        return false;
    }
}