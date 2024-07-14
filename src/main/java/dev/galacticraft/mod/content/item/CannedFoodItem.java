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
import dev.galacticraft.mod.content.CannedFoodTooltip;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static dev.galacticraft.mod.content.item.GCItems.CANNED_FOOD;
import static dev.galacticraft.mod.content.item.GCItems.EMPTY_CANNED_FOOD;

public class CannedFoodItem extends Item implements FabricItemStack {
    private int color;

    public static final int MAX_FOOD = 16;


    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide)
        {
            int consumingItems = getItemsToBeConsumed(itemStack);
            assert itemStack.getTag() != null;
            CompoundTag copyTag = itemStack.getTag().copy();
            super.finishUsingItem(itemStack, level, livingEntity);
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
                serverPlayer.awardStat(Stats.ITEM_USED.get(this));
            }
            ItemStack can = new ItemStack(CANNED_FOOD);
            can.setTag(copyTag.copy());
            for (int i = 0; i < consumingItems; i++) {
                removeOne(can);
            }
            if (itemStack.isEmpty()) {
                if (getContents(can).findAny().isEmpty())
                {
                    can = new ItemStack(EMPTY_CANNED_FOOD);
                }
                return can;
            }else
            {
                if (livingEntity instanceof Player player) {
                    if (!player.getAbilities().instabuild) {

                        if (getContents(can).findAny().isEmpty())
                        {
                            can = new ItemStack(EMPTY_CANNED_FOOD);
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

    public static void registerCan(ItemStack cannedFoodType)
    {
        ItemGroupEvents.modifyEntriesEvent(ResourceKey.create(Registries.CREATIVE_MODE_TAB, Constant.id(Constant.Item.ITEM_GROUP_CANS))).register(entries -> {
            ItemStack cannedFoodItem = CANNED_FOOD.getDefaultInstance();
            add(cannedFoodItem, cannedFoodType);
            entries.accept(cannedFoodItem);
        });
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        if (getContents(stack).findAny().isEmpty())
        {
            return Component.literal("Empty Food Can");
        }else
        {
            String result = getContents(stack)
                    .map(CannedFoodItem::getItemDisplayName)
                    .collect(new TopNCollector<>(3));
            return Component.literal("Canned " + result);
        }
    }

    public void setColor(int color)
    {
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
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack)
    {
        if (getContents(stack).findAny().isPresent())
        {
            NonNullList<ItemStack> nonNullList = NonNullList.create();
            Stream<ItemStack> stream = getContents(stack);
            Objects.requireNonNull(nonNullList);
            stream.forEach(nonNullList::add);
            return Optional.of(new CannedFoodTooltip(nonNullList));
        }else
        {
            return Optional.empty();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        if (getContents(stack).findAny().isPresent()) {
            tooltip.add(Component.literal("Total Nutrition: " + getTotalNutrition(stack)).withColor(ChatFormatting.DARK_GRAY.getColor()));
        }
    }

    public static Stream<ItemStack> getContents(ItemStack stack) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag == null) {
            return Stream.empty();
        } else {
            ListTag listTag = compoundTag.getList("Items", ListTag.TAG_COMPOUND);
            Stream<Tag> stream = listTag.stream();
            Objects.requireNonNull(CompoundTag.class);
            return stream.map(CompoundTag.class::cast).map(ItemStack::of);
        }
    }

    public static boolean isCannedFoodItem(ItemStack stack) {
        return stack.getItem() instanceof CannedFoodItem;
    }

    private static void removeOne(ItemStack stack) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        if (compoundTag.contains("Items"))
        {
            ListTag listTag = compoundTag.getList("Items", 10);
            if (!listTag.isEmpty())
            {
                Tag tag = listTag.get(0);
                if (tag instanceof CompoundTag tagCompound) {
                    ItemStack itemStack = ItemStack.of(tagCompound);
                    int itemCount = itemStack.getCount();
                    if (itemCount != 1)
                    {
                        itemStack.shrink(1);
                        CompoundTag compoundNew = new CompoundTag();
                        itemStack.save(compoundNew);
                        listTag.set(0, compoundNew);
                    }else
                    {
                        listTag.remove(0);
                    }
                }
                if (listTag.isEmpty()) {
                    stack.removeTagKey("Items");
                }
            }
        }
    }

    public static void add(ItemStack cannedFood, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem().canFitInsideContainerItems()) {
            CompoundTag compoundTag = cannedFood.getOrCreateTag();
            if (!compoundTag.contains("Items")) {
                compoundTag.put("Items", new ListTag());
            }

            //the max that the canned food item can hold
            int k = Math.min(stack.getCount(), MAX_FOOD);
            if (k != 0)
            {
                ListTag listTag = compoundTag.getList("Items", 10);
                ItemStack itemStack2 = stack.copyWithCount(k);
                CompoundTag compoundTag3 = new CompoundTag();
                itemStack2.save(compoundTag3);
                int iter = -1;
                for (int i = 0; i < listTag.size(); i++) {
                    if (listTag.getCompound(i).get("id").getAsString().equals(compoundTag3.get("id").getAsString()))
                    {
                        iter = i;
                    }
                }
                if (iter == -1)
                {
                    listTag.add(0, compoundTag3);
                }else
                {
                    itemStack2.setCount(listTag.getCompound(iter).getInt("Count") + 1);
                    itemStack2.save(compoundTag3);
                    listTag.set(iter, compoundTag3);
                }
            }
        }
    }

    public static List<ItemStack> addToCan(List<ItemStack> items, ItemStack can)
    {
        int size = getSize(can);
        int iter = 0;
        if (size < MAX_FOOD)
        {
            for (int i = 0; i < MAX_FOOD - size; i++) {
                if (!items.isEmpty())
                {
                    if (iter > 3)
                    {
                        return items;
                    }
                    if (items.get(iter).getCount() == 0)
                    {
                        ++iter;
                        i--;
                    }else if (items.get(iter).getCount() > 1)
                    {
                        add(can,items.get(iter).copyWithCount(1));
                        items.set(iter, items.get(iter).copyWithCount(items.get(iter).getCount() - 1));
                    }
                    else
                    {
                        add(can,items.get(iter).copyWithCount(1));
                        items.set(iter, Items.AIR.getDefaultInstance());
                        ++iter;
                    }
                }
            }
        }
        return items;
    }

    public static int getSize(ItemStack can)
    {
        int size = 0;
        List<ItemStack> contents = getContents(can).toList();
        if (!contents.isEmpty())
        {
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

    public static class TopNCollector<T> implements Collector<T, List<T>, String> {
        private final int n;

        public TopNCollector(int n) {
            this.n = n;
        }

        @Override
        public Supplier<List<T>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<T>, T> accumulator() {
            return (list, item) -> {
                if (list.size() < n) {
                    list.add(item);
                } else if (list.size() == n) {
                    list.add((T)"...");
                }
            };
        }

        @Override
        public BinaryOperator<List<T>> combiner() {
            return (list1, list2) -> {
                list1.addAll(list2);
                if (list1.size() > n) {
                    list1 = list1.subList(0, n);
                    list1.add((T) "...");
                }
                return list1;
            };
        }

        @Override
        public Function<List<T>, String> finisher() {
            return list -> {
                boolean greaterThanN = false;
                if (list.size() > n) {
                    greaterThanN = true;
                    list.remove(3);
                }
                String string = String.join(", ", list.toArray(new String[0]));
                if (greaterThanN)
                {
                    string = string + "...";
                }
                return string;
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return new HashSet<>();
        }
    }

    public static FoodProperties getCanFoodProperties(ItemStack stack) {
        int playerHunger = 0;
        if (Minecraft.getInstance().player != null) {
            playerHunger = Minecraft.getInstance().player.getFoodData().getFoodLevel();
        }
        int nutritionRequired = 20 - playerHunger;
        int canNutrition = 0;
        float canSaturation = 0f;
        if (nutritionRequired != 0)
        {
            Stream<ItemStack> stream = getContents(stack);
            for (ItemStack foodItem : stream.toList())
            {
                int itemCount = foodItem.getCount();
                for (int i = 0; i < itemCount; i++)
                {
                    canNutrition += Objects.requireNonNull(foodItem.getFoodComponent()).getNutrition();
                    canSaturation += foodItem.getFoodComponent().getSaturationModifier();
                    if (canNutrition >= nutritionRequired)
                    {
                        return new FoodProperties.Builder().nutrition(canNutrition).saturationMod(canSaturation).build();
                    }
                }
            }
            if (canNutrition == 0)
            {
                return null;
            }else
            {
                return new FoodProperties.Builder().nutrition(canNutrition).saturationMod(canSaturation).build();
            }
        }
        return null;
    }

    public static int getTotalNutrition(ItemStack stack) {
        int canNutrition = 0;
        Stream<ItemStack> stream = getContents(stack);
        for (ItemStack foodItem : stream.toList())
        {
            int itemCount = foodItem.getCount();
            for (int i = 0; i < itemCount; i++)
            {
                canNutrition += Objects.requireNonNull(foodItem.getFoodComponent()).getNutrition();
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
        if (nutritionRequired != 0)
        {
            Stream<ItemStack> stream = getContents(stack);
            for (ItemStack foodItem : stream.toList())
            {
                int itemCount = foodItem.getCount();
                for (int i = 0; i < itemCount; i++)
                {
                    itemsToBeConsumed += 1;
                    canNutrition += Objects.requireNonNull(foodItem.getFoodComponent()).getNutrition();
                    if (canNutrition >= nutritionRequired)
                    {
                        return itemsToBeConsumed;
                    }
                }
            }
        }
        return itemsToBeConsumed;
    }
}