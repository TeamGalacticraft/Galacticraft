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

package dev.galacticraft.mod.content.item;

import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.mod.content.CannedFoodTooltip;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.decoration.CannedFoodBlock;
import dev.galacticraft.mod.content.block.entity.decoration.CannedFoodBlockEntity;
import dev.galacticraft.mod.tag.GCItemTags;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.galacticraft.mod.content.item.GCItems.CANNED_FOOD;
import static dev.galacticraft.mod.content.item.GCItems.EMPTY_CAN;

public class CannedFoodItem extends Item implements FabricItemStack {
    public static final int MAX_CANS = 8;
    public static final int MAX_FOOD = 16;

    private static final int DEFAULT_CAN_COLOR = 0xFFFFFF;
    private static final Map<CannedFoodColorKey, Integer> COLOR_CACHE = new ConcurrentHashMap<>();
    private static FoodColorProvider foodColorProvider = CannedFoodItem::getServerSafeFoodColor;

    public CannedFoodItem(Properties settings) {
        super(settings);
    }

    /**
     * Sets the colour provider used for generated can colours.
     *
     * <p>This is intended to be called from client initialization with a provider
     * that reads actual item texture pixels. Dedicated servers keep the default
     * server-safe hash fallback.</p>
     */
    public static void setFoodColorProvider(FoodColorProvider provider) {
        foodColorProvider = provider == null ? CannedFoodItem::getServerSafeFoodColor : provider;
        clearColorCache();
    }

    /**
     * Clears cached generated can colours.
     *
     * <p>Call this after resource reloads if item texture colours may have changed.</p>
     */
    public static void clearColorCache() {
        COLOR_CACHE.clear();
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (player != null && getNumberToBeConsumed(stack, player) > 0 && !player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        Direction face = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        BlockState oldState = level.getBlockState(pos);
        Block clickedBlock = oldState.getBlock();

        if (!this.canInsertCan(level, pos, clickedBlock instanceof CannedFoodBlock)) {
            pos = pos.relative(face);
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);

            if (!belowState.isFaceSturdy(level, below, Direction.UP) && this.canInsertCan(level, below, true)) {
                return InteractionResult.FAIL;
            }

            oldState = level.getBlockState(pos);
            if ((!oldState.isAir() && !oldState.is(GCBlocks.CANNED_FOOD)) || !this.canInsertCan(level, pos, true)) {
                return InteractionResult.FAIL;
            }
        }

        BlockState newState = GCBlocks.CANNED_FOOD.defaultBlockState()
                .setValue(CannedFoodBlock.FACING, context.getHorizontalDirection().getOpposite());

        if (level.getBlockEntity(pos) instanceof CannedFoodBlockEntity canEntity) {
            newState = newState.setValue(CannedFoodBlock.MAX, canEntity.getCanCount() + 1 == MAX_CANS);
        }

        if (newState != oldState) {
            level.setBlock(pos, newState, Block.UPDATE_ALL);
        }

        this.playEvent(level, pos, player, newState);

        if (level.getBlockEntity(pos) instanceof CannedFoodBlockEntity canEntity) {
            canEntity.addCanItem(stack);
            stack.consume(1, player);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if (isCannedFoodItem(itemStack)) {
            FoodProperties foodProperties = getCanFoodProperties(itemStack, player);
            if (foodProperties != null) {
                if (player.canEat(foodProperties.canAlwaysEat())) {
                    player.startUsingItem(interactionHand);
                    return InteractionResultHolder.consume(itemStack);
                }

                return InteractionResultHolder.fail(itemStack);
            }
        }

        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide && livingEntity instanceof Player player) {
            int consumingItems = getNumberToBeConsumed(itemStack, player);
            DataComponentMap components = itemStack.getComponents();
            List<ItemStack> contents = getContents(itemStack);

            int toConsume = consumingItems;
            for (ItemStack foodItemStack : contents) {
                int itemCount = Math.min(foodItemStack.getCount(), toConsume);
                Item foodItem = foodItemStack.getItem();
                ItemStack foodItemCopy = foodItemStack.copy();

                foodItemCopy.remove(DataComponents.FOOD);

                for (int i = 0; i < itemCount; i++) {
                    foodItem.finishUsingItem(foodItemCopy, level, livingEntity);
                    if (livingEntity instanceof ServerPlayer serverPlayer) {
                        serverPlayer.awardStat(Stats.ITEM_USED.get(foodItem));
                    }
                }

                toConsume -= itemCount;
                if (toConsume == 0) {
                    break;
                }
            }

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

            if (getContents(can).isEmpty()) {
                can = EMPTY_CAN.getDefaultInstance();
            }

            if (itemStack.isEmpty()) {
                return can;
            }

            if (!player.getAbilities().instabuild && !player.getInventory().add(can)) {
                player.drop(can, false);
            }
        }

        return itemStack;
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        super.onCraftedBy(stack, world, player);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        List<ItemStack> contents = getContents(stack);

        if (contents.isEmpty()) {
            return Component.translatable(Translations.Items.EMPTY_CAN);
        }

        String result = summarizeTopN(contents.stream().map(CannedFoodItem::getItemDisplayName), 3);
        return Component.translatable(Translations.Items.CANNED_FOOD_TEMPLATE, result);
    }

    public static String summarizeTopN(Stream<String> stream, int n) {
        List<String> list = stream.distinct().limit(n + 1).collect(Collectors.toCollection(ArrayList::new));
        boolean hasMore = list.size() > n;

        if (hasMore) {
            list = list.subList(0, n);
        }

        String result = String.join(", ", list);
        return hasMore ? result + "..." : result;
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        List<ItemStack> contents = getContents(stack);

        if (contents.isEmpty()) {
            return Optional.empty();
        }

        NonNullList<ItemStack> nonNullList = NonNullList.create();
        contents.forEach(nonNullList::add);
        return Optional.of(new CannedFoodTooltip(nonNullList));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        if (!getContents(stack).isEmpty()) {
            tooltip.add(Component.translatable(Translations.Ui.TOTAL_NUTRITION, getTotalNutrition(stack)).withStyle(ChatFormatting.GRAY));
        }
    }

    public static int getCanColor(ItemStack stack) {
        Integer override = stack.get(GCDataComponents.COLOR);
        if (override != null) {
            return override;
        }

        return getGeneratedCanColor(getContents(stack));
    }

    public static int getGeneratedCanColor(List<ItemStack> contents) {
        if (contents.isEmpty()) {
            return DEFAULT_CAN_COLOR;
        }

        CannedFoodColorKey key = CannedFoodColorKey.of(contents);
        return COLOR_CACHE.computeIfAbsent(key, CannedFoodItem::calculateCanColor);
    }

    private static int calculateCanColor(CannedFoodColorKey key) {
        if (key.entries().isEmpty()) {
            return DEFAULT_CAN_COLOR;
        }

        long sumRed = 0L;
        long sumGreen = 0L;
        long sumBlue = 0L;
        int totalCount = 0;

        for (CannedFoodColorKey.Entry entry : key.entries()) {
            int color = foodColorProvider.getColor(entry.item());
            int count = entry.count();

            sumRed += (long) ((color >> 16) & 0xFF) * count;
            sumGreen += (long) ((color >> 8) & 0xFF) * count;
            sumBlue += (long) (color & 0xFF) * count;
            totalCount += count;
        }

        if (totalCount <= 0) {
            return DEFAULT_CAN_COLOR;
        }

        int avgRed = (int) (sumRed / totalCount);
        int avgGreen = (int) (sumGreen / totalCount);
        int avgBlue = (int) (sumBlue / totalCount);

        return avgRed << 16 | avgGreen << 8 | avgBlue;
    }

    private static int getServerSafeFoodColor(Item item) {
        String id = BuiltInRegistries.ITEM.getKey(item).toString();
        int hash = id.hashCode();

        int red = 96 + Math.floorMod(hash, 128);
        int green = 96 + Math.floorMod(hash >> 8, 128);
        int blue = 96 + Math.floorMod(hash >> 16, 128);

        return red << 16 | green << 8 | blue;
    }

    public static List<ItemStack> getContents(ItemStack stack) {
        DataComponentMap components = stack.getComponents();

        if (components.isEmpty()) {
            return List.of();
        }

        ItemContainerContents contents = components.get(DataComponents.CONTAINER);
        if (contents == null) {
            return List.of();
        }

        return contents.stream().toList();
    }

    public static ItemStack getFirst(ItemStack stack) {
        List<ItemStack> contents = getContents(stack);
        return contents.isEmpty() ? ItemStack.EMPTY : contents.getFirst();
    }

    public static boolean isCannedFoodItem(ItemStack stack) {
        return stack.getItem() == CANNED_FOOD && !getContents(stack).isEmpty();
    }

    public static boolean isEmptyCan(ItemStack stack) {
        Item item = stack.getItem();
        return item == EMPTY_CAN || item == CANNED_FOOD && getContents(stack).isEmpty();
    }

    public static void removeOne(ItemStack stack) {
        ItemContainerContents itemContainerContents = stack.get(DataComponents.CONTAINER);
        if (itemContainerContents == null) {
            return;
        }

        List<ItemStack> items = new ArrayList<>(itemContainerContents.stream().toList());
        if (items.isEmpty()) {
            return;
        }

        ItemStack itemStack = items.getFirst();

        if (itemStack.getCount() > 1) {
            itemStack.shrink(1);
            items.set(0, itemStack);
        } else {
            items.removeFirst();
        }

        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }

    public static void add(ItemStack cannedFood, ItemStack stack) {
        if (stack.isEmpty() || !stack.getItem().canFitInsideContainerItems()) {
            return;
        }

        if (!cannedFood.has(DataComponents.CONTAINER)) {
            cannedFood.set(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        }

        List<ItemStack> cannedFoodItems = new ArrayList<>(getContents(cannedFood));
        int countToAdd = Math.min(stack.getCount(), MAX_FOOD);

        if (countToAdd == 0) {
            return;
        }

        ItemStack itemStackToAdd = stack.copyWithCount(countToAdd);
        int existingIndex = -1;

        for (int i = 0; i < cannedFoodItems.size(); i++) {
            if (ItemStack.isSameItemSameComponents(cannedFoodItems.get(i).copyWithCount(countToAdd), itemStackToAdd)) {
                existingIndex = i;
                break;
            }
        }

        if (existingIndex == -1) {
            cannedFoodItems.add(itemStackToAdd);
        } else {
            ItemStack existing = cannedFoodItems.get(existingIndex).copy();
            existing.grow(1);
            cannedFoodItems.set(existingIndex, existing);
        }

        cannedFood.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(cannedFoodItems));
    }

    public static boolean canAddToCan(Item item) {
        if (item == null || !item.components().has(DataComponents.FOOD)) {
            return false;
        }

        if (item instanceof CannedFoodItem) {
            return false;
        }

        return !item.getDefaultInstance().is(GCItemTags.UNCANNABLE_FOODS);
    }

    public static List<ItemStack> addToCan(List<ItemStack> items, ItemStack can) {
        int size = getSize(can);
        int iter = 0;

        if (size >= MAX_FOOD || items.isEmpty()) {
            return items;
        }

        for (int i = 0; i < MAX_FOOD - size; i++) {
            ItemStack itemStack = items.get(iter).copy();

            if (itemStack.getCount() == 0) {
                ++iter;
                i--;
            } else if (itemStack.getCount() > 1) {
                add(can, itemStack.split(1));
                items.set(iter, itemStack);
            } else {
                add(can, itemStack.copyWithCount(1));
                items.set(iter, ItemStack.EMPTY);
                ++iter;
            }

            if (iter > 3) {
                return items;
            }
        }

        return items;
    }

    public static List<ItemStack> addToCanEvenly(List<ItemStack> items, ItemStack can) {
        int space = MAX_FOOD - getSize(can);
        int n = items.size();

        if (space <= 0 || n <= 0) {
            return items;
        }

        int i = 0;
        boolean changed = false;

        while (space > 0) {
            ItemStack itemStack = items.get(i).copy();

            if (itemStack.getCount() > 0) {
                add(can, itemStack.split(1));
                items.set(i, itemStack);
                --space;
                changed = true;
            }

            if (++i == n) {
                if (!changed) {
                    break;
                }

                i = 0;
                changed = false;
            }
        }

        return items;
    }

    public static int getSize(ItemStack can) {
        int size = 0;

        for (ItemStack content : getContents(can)) {
            size += content.getCount();
        }

        return size;
    }

    public static boolean isFull(ItemStack can) {
        return getSize(can) >= MAX_FOOD;
    }

    private static String getItemDisplayName(ItemStack itemStack) {
        Component displayName = itemStack.getDisplayName();
        return stripControlCodes(displayName.getString());
    }

    private static String stripControlCodes(String input) {
        return input.replaceAll("]", "").replaceAll("\\[", "");
    }

    public static FoodProperties getCanFoodProperties(ItemStack stack, Player player) {
        int playerHunger = player != null ? player.getFoodData().getFoodLevel() : 0;
        int nutritionRequired = 20 - playerHunger;
        int canNutrition = 0;
        float canSaturation = 0.0F;
        boolean alwaysEdible = true;
        boolean fastFood = true;
        List<ItemStack> contents = getContents(stack);

        if (nutritionRequired != 0) {
            FoodProperties.Builder builder = new FoodProperties.Builder();

            for (ItemStack foodItem : contents) {
                FoodProperties foodProperties = foodItem.get(DataComponents.FOOD);
                if (foodProperties == null) {
                    continue;
                }

                for (int i = 0; i < foodItem.getCount(); i++) {
                    canNutrition += foodProperties.nutrition();
                    canSaturation += foodProperties.saturation();
                    alwaysEdible = alwaysEdible && foodProperties.canAlwaysEat();
                    fastFood = fastFood && foodProperties.eatSeconds() == 0.8F;

                    for (FoodProperties.PossibleEffect entry : foodProperties.effects()) {
                        builder.effect(entry.effect(), entry.probability());
                    }

                    if (canNutrition >= nutritionRequired) {
                        if (alwaysEdible) {
                            builder.alwaysEdible();
                        }

                        if (fastFood) {
                            builder.fast();
                        }

                        return builder.nutrition(canNutrition).saturationModifier(canSaturation).build();
                    }
                }
            }

            if (canNutrition != 0 || alwaysEdible) {
                if (alwaysEdible) {
                    builder.alwaysEdible();
                }

                if (fastFood) {
                    builder.fast();
                }

                return builder.nutrition(canNutrition).saturationModifier(canSaturation).build();
            }
        } else if (!contents.isEmpty()) {
            ItemStack foodItem = contents.getFirst();
            FoodProperties foodProperties = foodItem.get(DataComponents.FOOD);

            if (foodProperties != null && player != null && player.canEat(foodProperties.canAlwaysEat())) {
                FoodProperties.Builder builder = new FoodProperties.Builder();

                builder.nutrition(foodProperties.nutrition());
                builder.saturationModifier(foodProperties.saturation());

                if (foodProperties.eatSeconds() == 0.8F) {
                    builder.fast();
                }

                for (FoodProperties.PossibleEffect entry : foodProperties.effects()) {
                    builder.effect(entry.effect(), entry.probability());
                }

                return builder.alwaysEdible().build();
            }
        }

        return null;
    }

    public static int getTotalNutrition(ItemStack stack) {
        int canNutrition = 0;

        for (ItemStack foodItem : getContents(stack)) {
            FoodProperties foodProperties = foodItem.get(DataComponents.FOOD);
            if (foodProperties == null) {
                continue;
            }

            canNutrition += foodProperties.nutrition() * foodItem.getCount();
        }

        return canNutrition;
    }

    public static int getNumberToBeConsumed(ItemStack stack, Player player) {
        int playerHunger = player != null ? player.getFoodData().getFoodLevel() : 0;
        int nutritionRequired = 20 - playerHunger;
        int canNutrition = 0;
        int itemsToBeConsumed = 0;
        List<ItemStack> contents = getContents(stack);

        if (nutritionRequired != 0) {
            for (ItemStack foodItem : contents) {
                FoodProperties foodProperties = foodItem.get(DataComponents.FOOD);
                if (foodProperties == null) {
                    continue;
                }

                for (int i = 0; i < foodItem.getCount(); i++) {
                    itemsToBeConsumed++;
                    canNutrition += foodProperties.nutrition();

                    if (canNutrition >= nutritionRequired) {
                        return itemsToBeConsumed;
                    }
                }
            }
        } else if (!contents.isEmpty()) {
            ItemStack itemStack = contents.getFirst();
            FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);

            if (foodProperties != null && player != null && player.canEat(foodProperties.canAlwaysEat())) {
                return 1;
            }
        }

        return itemsToBeConsumed;
    }

    public static List<ItemStack> getItemsToBeConsumed(ItemStack stack, Player player) {
        int playerHunger = player != null ? player.getFoodData().getFoodLevel() : 0;
        int nutritionRequired = 20 - playerHunger;
        int canNutrition = 0;
        List<ItemStack> contents = getContents(stack);
        List<ItemStack> itemsToBeConsumed = new ArrayList<>();

        if (nutritionRequired != 0) {
            for (ItemStack foodItem : contents) {
                FoodProperties foodProperties = foodItem.get(DataComponents.FOOD);
                if (foodProperties == null) {
                    continue;
                }

                int consumedFromStack = 0;

                for (int i = 0; i < foodItem.getCount(); i++) {
                    consumedFromStack++;
                    canNutrition += foodProperties.nutrition();

                    if (canNutrition >= nutritionRequired) {
                        itemsToBeConsumed.add(foodItem.copyWithCount(consumedFromStack));
                        return itemsToBeConsumed;
                    }
                }

                itemsToBeConsumed.add(foodItem.copyWithCount(consumedFromStack));
            }
        } else if (!contents.isEmpty()) {
            ItemStack itemStack = contents.getFirst();
            FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);

            if (foodProperties != null && player != null && player.canEat(foodProperties.canAlwaysEat())) {
                itemsToBeConsumed.add(itemStack.copyWithCount(1));
            }
        }

        return itemsToBeConsumed;
    }

    public static List<ItemStack> getDefaultCannedFoods() {
        List<ItemStack> cannedFoods = new ArrayList<>();

        for (Item item : BuiltInRegistries.ITEM) {
            if (CannedFoodItem.canAddToCan(item)) {
                ItemStack cannedFoodItem = CANNED_FOOD.getDefaultInstance();
                CannedFoodItem.add(cannedFoodItem, new ItemStack(item, CannedFoodItem.MAX_FOOD));
                cannedFoods.add(cannedFoodItem);
            }
        }

        return cannedFoods;
    }

    private boolean canInsertCan(Level level, BlockPos blockPos, boolean canPlace) {
        if (!canPlace) {
            return false;
        }

        if (level.getBlockEntity(blockPos) instanceof CannedFoodBlockEntity canEntity) {
            return canEntity.getCanCount() < MAX_CANS;
        }

        return true;
    }

    private void playEvent(Level level, BlockPos blockPos, Player player, BlockState blockState) {
        SoundType soundType = blockState.getSoundType();

        level.playSound(
                player,
                blockPos,
                blockState.getSoundType().getPlaceSound(),
                SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F,
                soundType.getPitch() * 0.8F
        );

        level.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(player, blockState));
    }

    @FunctionalInterface
    public interface FoodColorProvider {
        int getColor(Item item);
    }

    public record CannedFoodColorKey(List<Entry> entries) {
        public static CannedFoodColorKey of(List<ItemStack> stacks) {
            Map<Item, Integer> counts = new HashMap<>();

            for (ItemStack stack : stacks) {
                if (!stack.isEmpty()) {
                    counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
                }
            }

            List<Entry> entries = counts.entrySet().stream()
                    .map(entry -> new Entry(entry.getKey(), entry.getValue()))
                    .sorted(Comparator.comparing(entry -> BuiltInRegistries.ITEM.getKey(entry.item()).toString()))
                    .toList();

            return new CannedFoodColorKey(entries);
        }

        public record Entry(Item item, int count) {
        }
    }
}