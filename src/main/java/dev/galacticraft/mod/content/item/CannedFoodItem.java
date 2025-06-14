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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.galacticraft.mod.content.item.GCItems.EMPTY_CAN;
import static dev.galacticraft.mod.util.TextureUtils.getAverageColor;
import static net.minecraft.data.models.model.TextureMapping.getItemTexture;

public class CannedFoodItem extends Item implements FabricItemStack {
    public static final int MAX_CANS = 8;
    public static final int MAX_FOOD = 16;

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (getItemsToBeConsumed(stack, player) > 0 && !player.isSecondaryUseActive()) {
            // Don't place down the can if the player is able to eat from the can
            // and if the player is not holding down the shift key
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(pos);
        Direction face = context.getClickedFace();
        Block clickedBlock = clickedState.getBlock();

        // TODO: prevent can from being placed if the new hitbox would collide with an entity

        if (this.canInsertCan(level, pos, clickedBlock instanceof CannedFoodBlock)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CannedFoodBlockEntity canEntity) {
                canEntity.addCanItem(stack.copyWithCount(1)); // Store canned food item
                this.playEvent(level, pos, player, clickedState);
                stack.consume(1, player);
                return InteractionResult.SUCCESS;
            }
        }

        BlockPos below = pos.relative(face).below();
        BlockState belowState = level.getBlockState(below);

        if (this.canInsertCan(level, below, belowState.getBlock() instanceof CannedFoodBlock)) {
            return InteractionResult.FAIL;
        } else if (!belowState.isFaceSturdy(level, below, Direction.UP, SupportType.FULL)) {
            return InteractionResult.FAIL;
        }
        BlockPos placementPos = pos.relative(face);
        BlockState oldState = level.getBlockState(placementPos);
        if (oldState.isAir() || oldState.is(GCBlocks.CANNED_FOOD)) {
            if (!this.canInsertCan(level, placementPos, true)) {
                return InteractionResult.FAIL;
            }
            BlockState newState = GCBlocks.CANNED_FOOD.defaultBlockState().setValue(CannedFoodBlock.FACING, context.getHorizontalDirection().getOpposite());
            level.setBlock(placementPos, newState, Block.UPDATE_ALL);
            this.playEvent(level, placementPos, player, newState);
            BlockEntity be = level.getBlockEntity(placementPos);
            if (be instanceof CannedFoodBlockEntity canEntity) {
                if (stack.has(GCDataComponents.COLOR)) {
                    canEntity.addCanItem(stack.copyWithCount(1));
                } else {
                    stack.set(GCDataComponents.COLOR, 0xFFFFFF);
                    canEntity.addCanItem(stack.copyWithCount(1));
                }
            }
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
        return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide) {
            int consumingItems = getItemsToBeConsumed(itemStack, (Player) livingEntity);
            DataComponentMap components = itemStack.getComponents();
            List<ItemStack> stream = getContents(itemStack);
            int toConsume = consumingItems;
            for (ItemStack foodItemStack : stream) {
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
                if (toConsume == 0) break;
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
        this.getDefaultInstance().set(GCDataComponents.COLOR, 0xFFFFFF);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        super.onCraftedBy(stack, world, player);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        if (getContents(stack).isEmpty()) {
            return Component.translatable(Translations.Items.EMPTY_CAN);
        } else {
            String result = summarizeTopN(getContents(stack).stream()
                    .map(CannedFoodItem::getItemDisplayName), 3);
            return Component.translatable(Translations.Items.CANNED_FOOD_TEMPLATE, result);
        }
    }

    public static String summarizeTopN(Stream<String> stream, int n) {
        List<String> list = stream.distinct().limit(n + 1).collect(Collectors.toList());
        boolean hasMore = list.size() > n;
        if (hasMore) {
            list = list.subList(0, n);
        }
        String result = String.join(", ", list);
        return hasMore ? result + "..." : result;
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
            tooltip.add(Component.translatable(Translations.Ui.TOTAL_NUTRITION, getTotalNutrition(stack)).withStyle(ChatFormatting.GRAY));
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

            // Recalculate and update the stored color
            int newColor = calculateCanColor(items);
            stack.set(GCDataComponents.COLOR, newColor);
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

            int k = Math.min(stack.getCount(), MAX_FOOD);
            if (k != 0) {
                ItemStack itemStack2 = stack.copyWithCount(k);
                int iter = -1;

                for (int i = 0; i < cannedFoodItems.size(); i++) {
                    if (ItemStack.isSameItemSameComponents(cannedFoodItems.get(i).copyWithCount(k), itemStack2)) {
                        iter = i;
                    }
                }
                if (iter == -1) {
                    cannedFoodItems.add(itemStack2);
                } else {
                    itemStack2.setCount(cannedFoodItems.get(iter).getCount() + 1);
                    cannedFoodItems.set(iter, itemStack2);
                }

                // Update container contents
                cannedFood.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(cannedFoodItems));

                // Recalculate and store the new color inside the stack components
                int newColor = calculateCanColor(cannedFoodItems);
                cannedFood.set(GCDataComponents.COLOR, newColor);
            }
        }
    }

    private static int calculateCanColor(List<ItemStack> items) {
        if (items.isEmpty()) {
            return 0xFFFFFF; // Default white color for empty cans
        }

        long sumRed = 0, sumGreen = 0, sumBlue = 0;
        int count = 0;

        for (ItemStack stack : items) {
            ResourceLocation texture = getItemTexture(stack.getItem());

            // Avoid crashing if the resource manager isn't ready
            int color = getAverageColor(texture);
            int red = color >> 16 & 0xFF;
            int green = color >> 8 & 0xFF;
            int blue = color & 0xFF;

            sumRed += red;
            sumGreen += green;
            sumBlue += blue;
            count++;
        }

        if (count == 0) return 0xFFFFFF;

        int avgRed = (int) (sumRed / count);
        int avgGreen = (int) (sumGreen / count);
        int avgBlue = (int) (sumBlue / count);

        return avgRed << 16 | avgGreen << 8 | avgBlue;
    }

    public static boolean canAddToCan(Item item) {
        if (item != null && item.components().has(DataComponents.FOOD)) {
            if (!(item instanceof CannedFoodItem)) {
                return !item.getDefaultInstance().is(GCItemTags.UNCANNABLE_FOODS);
            }
        }
        return false;
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
        int playerHunger = player != null ? player.getFoodData().getFoodLevel() : 0;
        int nutritionRequired = 20 - playerHunger;
        int canNutrition = 0;
        float canSaturation = 0f;
        boolean alwaysEdible = true;
        boolean fastFood = true;
        List<ItemStack> contents = getContents(stack);
        if (nutritionRequired != 0) {
            FoodProperties.Builder builder = new FoodProperties.Builder();
            for (ItemStack foodItem : contents) {
                FoodProperties foodProperties = foodItem.get(DataComponents.FOOD);
                assert foodProperties != null;
                int itemCount = foodItem.getCount();
                for (int i = 0; i < itemCount; i++) {
                    canNutrition += foodProperties.nutrition();
                    canSaturation += foodProperties.saturation();
                    alwaysEdible = alwaysEdible && foodProperties.canAlwaysEat();
                    fastFood = fastFood && foodProperties.eatSeconds() == 0.8F;
                    for (FoodProperties.PossibleEffect entry : foodProperties.effects()) {
                        builder.effect(entry.effect(), entry.probability());
                    }
                    if (canNutrition >= nutritionRequired) {
                        if (alwaysEdible) builder.alwaysEdible();
                        if (fastFood) builder.fast();
                        return builder.nutrition(canNutrition).saturationModifier(canSaturation).build();
                    }
                }
            }
            if (canNutrition != 0 || alwaysEdible) {
                if (alwaysEdible) builder.alwaysEdible();
                if (fastFood) builder.fast();
                return builder.nutrition(canNutrition).saturationModifier(canSaturation).build();
            }
        } else if (!contents.isEmpty()) {
            ItemStack foodItem = contents.get(0);
            FoodProperties foodProperties = foodItem.get(DataComponents.FOOD);
            assert foodProperties != null;
            if (foodProperties.canAlwaysEat()) {
                FoodProperties.Builder builder = new FoodProperties.Builder();
                builder.nutrition(foodProperties.nutrition());
                builder.saturationModifier(foodProperties.saturation());
                if (foodProperties.eatSeconds() == 0.8F) builder.fast();
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
        List<ItemStack> contents = getContents(stack);
        for (ItemStack foodItem : contents) {
            FoodProperties foodProperties = foodItem.get(DataComponents.FOOD);
            assert foodProperties != null;
            int itemCount = foodItem.getCount();
            for (int i = 0; i < itemCount; i++) {
                canNutrition += foodProperties.nutrition();
            }
        }
        return canNutrition;
    }

    public static int getItemsToBeConsumed(ItemStack stack, Player player) {
        int playerHunger = player != null ? player.getFoodData().getFoodLevel() : 0;
        int nutritionRequired = 20 - playerHunger;
        int canNutrition = 0;
        int itemsToBeConsumed = 0;
        List<ItemStack> contents = getContents(stack);
        if (nutritionRequired != 0) {
            for (ItemStack foodItem : contents) {
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
        } else if (!contents.isEmpty()) {
            ItemStack itemStack = contents.get(0);
            FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
            assert foodProperties != null;
            if (foodProperties.canAlwaysEat()) return 1;
        }
        return itemsToBeConsumed;
    }

    private boolean canInsertCan(Level level, BlockPos blockPos, boolean canPlace) {
        if (canPlace) {
            BlockEntity be = level.getBlockEntity(blockPos);
            if (be instanceof CannedFoodBlockEntity canEntity) {
                return canEntity.getCanCount() < MAX_CANS;
            }
            return true;
        }
        return false;
    }

    private void playEvent(Level level, BlockPos blockPos, Player player, BlockState blockState) {
        SoundType soundType = blockState.getSoundType();
        level.playSound(player, blockPos, blockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        level.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(player, blockState));
    }
}