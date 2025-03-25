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

import dev.galacticraft.mod.content.CannedFoodTooltip;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.galacticraft.mod.content.item.GCItems.EMPTY_CAN;

public class CannedFoodItem extends Item implements FabricItemStack {
    private int color;

    public static final int MAX_FOOD = 16;


    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide) {
            int consumingItems = getItemsToBeConsumed(itemStack);
            DataComponentMap components = itemStack.getComponents();
            super.finishUsingItem(itemStack, level, livingEntity);
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
                serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            }
            ItemStack can = this.getDefaultInstance();
            can.applyComponents(components);
            for (int i = 0; i < consumingItems; i++) {
                removeOne(can);
            }
            if (itemStack.isEmpty()) {
                if (getContents(can).isEmpty()) {
                    can = new ItemStack(EMPTY_CAN);
                }
                return can;
            } else {
                if (livingEntity instanceof Player player) {
                    if (!player.getAbilities().instabuild) {

                        if (getContents(can).isEmpty()) {
                            can = new ItemStack(EMPTY_CAN);
                        }
                        if (!player.getInventory().add(can)) {
                            player.drop(can, false);
                        }
                    }
                }
            }
        }
        return itemStack;
    }

    public CannedFoodItem(Properties settings) {
        super(settings);
        this.color = 0;
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        super.onCraftedBy(stack, world, player);
        // Add functionality when the item is crafted (optional)
    }


    @Override
    public @NotNull Component getName(ItemStack stack) {
        if (getContents(stack).isEmpty()) {
            return Component.translatable(Translations.Items.EMPTY_CAN);
        } else {
            String result = summarizeTopN(getContents(stack).stream()
                    .map(CannedFoodItem::getItemDisplayName), 3);
            return Component.translatable(Translations.Items.CANNED_FOOD, Component.literal(result));
        }
    }

    public static String summarizeTopN(Stream<String> stream, int n) {
        List<String> list = stream.limit(n + 1).collect(Collectors.toList());
        boolean hasMore = list.size() > n;
        if (hasMore) {
            list = list.subList(0, n);
        }
        String result = String.join(", ", list);
        return hasMore ? result + "..." : result;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor(int layer) {
        // Specify color for each layer, you can add more layers and their colors
        if (layer == 1) {
            return this.color;
        } else {
            return 0xFFFFFF; // Default color (no tint)
        }
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (!getContents(stack).isEmpty()) {
            NonNullList<ItemStack> nonNullList = NonNullList.create();
            Stream<ItemStack> stream = getContents(stack).stream();
            Objects.requireNonNull(nonNullList);
            stream.forEach(nonNullList::add);
            return Optional.of(new CannedFoodTooltip(nonNullList));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        if (!getContents(stack).isEmpty()) {
            tooltip.add(Component.translatable(Translations.Items.TOTAL_NUTRITION, Component.literal(String.valueOf(getTotalNutrition(stack)))).withColor(ChatFormatting.DARK_GRAY.getColor()));
        }
    }

    public static List<ItemStack> getContents(ItemStack stack) {
        DataComponentMap components = stack.getComponents();
        if (components.isEmpty()) {
            return List.of();
        } else {
            ItemContainerContents contents = components.get(DataComponents.CONTAINER);
            if (contents == null) {
                return List.of();
            }
            return contents.stream().toList();
        }
    }

    public static boolean isCannedFoodItem(ItemStack stack) {
        return stack.getItem() instanceof CannedFoodItem;
    }

    private static void removeOne(ItemStack stack) {
        ItemContainerContents itemContainerContents = stack.get(DataComponents.CONTAINER);

        assert itemContainerContents != null;
        List<ItemStack> items = new ArrayList<>(itemContainerContents.stream().toList());
        if (!items.isEmpty()) {
            ItemStack itemStack = items.getFirst();
            int itemCount = itemStack.getCount();
            if (itemCount != 1) {
                itemStack.shrink(1);
                items.set(0, itemStack);
            } else {
                items.removeFirst();
            }
            stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        }
    }

    public static void add(ItemStack cannedFood, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem().canFitInsideContainerItems()) {
            if (!cannedFood.has(DataComponents.CONTAINER)) {
                cannedFood.set(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            }
            ItemContainerContents cannedFoodContainer = cannedFood.get(DataComponents.CONTAINER);
            assert cannedFoodContainer != null;
            List<ItemStack> cannedFoodItems = new ArrayList<>(cannedFoodContainer.stream().toList());

            //the max food items that the canned food item can hold
            int k = Math.min(stack.getCount(), MAX_FOOD);
            if (k != 0) {
                ItemStack itemStack2 = stack.copyWithCount(k);
                int iter = -1;
                for (int i = 0; i < cannedFoodItems.size(); i++) {
                    if (cannedFoodItems.get(i).getItem().toString().equals(itemStack2.getItem().toString())) {
                        iter = i;
                    }
                }
                if (iter == -1) {
                    cannedFoodItems.add(itemStack2);
                } else {
                    itemStack2.setCount(cannedFoodItems.get(iter).getCount() + 1);
                    cannedFoodItems.set(iter, itemStack2);
                }
                cannedFood.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(cannedFoodItems));
            }
        }
    }

    public static List<ItemStack> addToCan(List<ItemStack> items, ItemStack can) {
        int size = getSize(can);
        int iter = 0;
        if (size < MAX_FOOD) {
            for (int i = 0; i < MAX_FOOD - size; i++) {
                if (!items.isEmpty()) {
                    if (iter > 3) {
                        return items;
                    }
                    if (items.get(iter).getCount() == 0) {
                        ++iter;
                        i--;
                    } else if (items.get(iter).getCount() > 1) {
                        add(can, items.get(iter).copyWithCount(1));
                        items.set(iter, items.get(iter).copyWithCount(items.get(iter).getCount() - 1));
                    } else {
                        add(can, items.get(iter).copyWithCount(1));
                        items.set(iter, Items.AIR.getDefaultInstance());
                        ++iter;
                    }
                }
            }
        }
        return items;
    }

    public static int getSize(ItemStack can) {
        int size = 0;
        List<ItemStack> contents = getContents(can);
        if (!contents.isEmpty()) {
            //can has something inside it
            for (ItemStack content : contents) {
                size += content.getCount();
            }
        }
        return size;
    }

    private static String getItemDisplayName(ItemStack itemStack) {
        Component displayName = itemStack.getDisplayName();
        return stripControlCodes(displayName.getString());
    }

    private static String stripControlCodes(String input) {
        return input.replaceAll("]", "").replaceAll("\\[", "");
    }

    public static FoodProperties getCanFoodProperties(ItemStack stack, Player player) {
        int playerHunger = 0;
        if (player != null) {
            playerHunger = player.getFoodLevel();
        }
        int nutritionRequired = 20 - playerHunger;
        int canNutrition = 0;
        float canSaturation = 0f;
        if (nutritionRequired != 0) {
            List<ItemStack> stream = getContents(stack);
            for (ItemStack foodItem : stream) {
                FoodProperties foodProperties = foodItem.get(DataComponents.FOOD);
                assert foodProperties != null;
                int itemCount = foodItem.getCount();
                for (int i = 0; i < itemCount; i++) {
                    canNutrition += foodProperties.nutrition();
                    canSaturation += foodProperties.saturation();
                    if (canNutrition >= nutritionRequired) {
                        return new FoodProperties.Builder().nutrition(canNutrition).saturationModifier(canSaturation).build();
                    }
                }
            }
            if (canNutrition == 0) {
                return null;
            } else {
                return new FoodProperties.Builder().nutrition(canNutrition).saturationModifier(canSaturation).build();
            }
        }
        return null;
    }

    public static int getTotalNutrition(ItemStack stack) {
        int canNutrition = 0;
        List<ItemStack> stream = getContents(stack);
        for (ItemStack foodItem : stream) {
            FoodProperties foodProperties = foodItem.get(DataComponents.FOOD);
            assert foodProperties != null;
            int itemCount = foodItem.getCount();
            for (int i = 0; i < itemCount; i++) {
                canNutrition += foodProperties.nutrition();
            }
        }
        return canNutrition;
    }

    public static int getItemsToBeConsumed(ItemStack stack) {
        int playerHunger = 0;
        if (Minecraft.getInstance().player != null) {
            playerHunger = Minecraft.getInstance().player.getFoodData().getFoodLevel();
        }
        int nutritionRequired = 20 - playerHunger;
        int canNutrition = 0;
        int itemsToBeConsumed = 0;
        if (nutritionRequired != 0) {
            List<ItemStack> stream = getContents(stack);
            for (ItemStack foodItem : stream) {
                FoodProperties foodProperties = foodItem.get(DataComponents.FOOD);
                int itemCount = foodItem.getCount();
                for (int i = 0; i < itemCount; i++) {
                    itemsToBeConsumed += 1;
                    canNutrition += foodProperties.nutrition();
                    if (canNutrition >= nutritionRequired) {
                        return itemsToBeConsumed;
                    }
                }
            }
        }
        return itemsToBeConsumed;
    }
}