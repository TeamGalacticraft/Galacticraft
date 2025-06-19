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

package dev.galacticraft.mod.content.item;

import dev.galacticraft.mod.content.GCAccessorySlots;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class EmergencyKitItem extends Item {

    public static List<ItemStack> getContents() {
        List<ItemStack> emergencyItems = new ArrayList<ItemStack>();

        ItemStack cannedFoodItem = GCItems.CANNED_FOOD.getDefaultInstance();
        CannedFoodItem.add(cannedFoodItem, new ItemStack(Items.APPLE, CannedFoodItem.MAX_FOOD));

        emergencyItems.add(GCItems.HEAVY_DUTY_PICKAXE.getDefaultInstance());
        emergencyItems.add(GCItems.OXYGEN_MASK.getDefaultInstance());
        emergencyItems.add(GCItems.OXYGEN_GEAR.getDefaultInstance());
        emergencyItems.add(OxygenTankItem.getFullTank(GCItems.SMALL_OXYGEN_TANK));
        emergencyItems.add(GCItems.PARACHUTE.get(DyeColor.RED).getDefaultInstance());
        emergencyItems.add(OxygenTankItem.getFullTank(GCItems.SMALL_OXYGEN_TANK));
        emergencyItems.add(cannedFoodItem);
        emergencyItems.add(PotionContents.createItemStack(Items.POTION, Potions.HEALING));
        emergencyItems.add(PotionContents.createItemStack(Items.POTION, Potions.LONG_NIGHT_VISION));

        return emergencyItems;
    }

    public EmergencyKitItem(Properties settings) {
        super(settings);
    }

    @Override //should sync with server
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        Container inv = player.galacticraft$getGearInv();
        int n = inv.getContainerSize();
        for (ItemStack itemStack : getContents()) {
            for (int slot = 0; slot < n; ++slot) {
                if (inv.getItem(slot).isEmpty() && itemStack.is(GCAccessorySlots.SLOT_TAGS.get(slot))) {
                    ItemStack itemStack2 = itemStack.split(1);
                    inv.setItem(slot, itemStack2);
                    player.galacticraft$onEquipAccessory(itemStack, itemStack2);
                    break;
                } else if (slot == n - 1) {
                    player.addItem(itemStack);
                }
            }
        }

        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
        }

        ItemStack itemStack2 = player.isCreative() ? itemStack.copy() : ItemStack.EMPTY;
        return InteractionResultHolder.sidedSuccess(itemStack2, level.isClientSide());
    }
}