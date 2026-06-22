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

package dev.galacticraft.mod.content.block.entity.machine;

import dev.galacticraft.machinelib.api.block.entity.RecipeMachineBlockEntity;
import dev.galacticraft.machinelib.api.storage.ResourceStorage;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class CompressorInsertHandler {
    public static long insert(BlockEntity blockEntity, ResourceStorage<Item, ItemResourceSlot> storage, TransferVariant<Item> variant, long maxAmount, TransactionContext transaction) {
        if (!(variant instanceof ItemVariant incomingItemVariant)) {
            return 0;
        }

        if (!(blockEntity instanceof RecipeMachineBlockEntity<?, ?> machine) || machine.getLevel() == null) {
            return 0;
        }

        // Stage 1: Find a valid recipe
        ItemStack incomingItemStack = incomingItemVariant.toStack((int) maxAmount);
        List<ItemStack> initialItemStacks = collectItemStacks(storage, incomingItemStack);

        CompressingRecipe recipe = findRecipe(machine, initialItemStacks);
        if (recipe == null) {
            Constant.LOGGER.info("No matching CompressingRecipe found.");
            return 0;
        }

        // Stage 2: Create a matching between the available item stacks and the recipe's ingredients
        // This approach doesn't always handle the case where we are trying to craft
        // compressed steel using both coal and charcoal, for example

        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        // TODO: Can't rely on Ingredient as a key as they are not guaranteed to have the same reference even if they are equal
        Map<Ingredient, List<Integer>> slotIds = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            Ingredient ingredient = i < ingredients.size() ? ingredients.get(i) : Ingredient.EMPTY;

            boolean added = false;
            for (var entry : slotIds.entrySet()) {
                if (entry.getKey().equals(ingredient)) {
                    List<Integer> ids = entry.getValue();
                    ids.add(i + 1); // Skip the fuel/battery slot
                    Collections.sort(ids);
                    slotIds.put(entry.getKey(), ids);
                    added = true;
                    break;
                }
            }

            if (!added) {
                List<Integer> ids = new ArrayList<>();
                ids.add(i + 1);
                slotIds.put(ingredient, ids);
            }
        }

        Map<Integer, ItemStack> toInsert = new HashMap<>();

        for (var entry : slotIds.entrySet()) {
            Ingredient ingredient = entry.getKey();
            if (ingredient.isEmpty()) {
                continue;
            }

            for (int i = 0; i < initialItemStacks.size(); i++) {
                ItemStack stack = initialItemStacks.get(i);
                if (ingredient.test(stack)) {
                    List<Integer> ids = entry.getValue();
                    int n = ids.size();
                    int count = stack.getCount();

                    for (int j = 0; j < ids.size(); j++) {
                        int id = ids.get(j);
                        if (toInsert.containsKey(id)) {
                            Constant.LOGGER.info("ItemStack already matched to a different ingredient.");
                            return 0;
                        }
                        toInsert.put(id, stack.copyWithCount((count + n - 1 - j) / n));
                    }
                }
            }
        }

        try (Transaction tx = transaction.openNested()) {
            // Stage 3: Rearrange items into the correct slots
            for (int index = 1; index < 10; index++) {
                ItemResourceSlot slot = storage.slot(index);
                ItemStack stack = toInsert.getOrDefault(index, ItemStack.EMPTY);
                Item item = stack.getItem();
                DataComponentPatch components = stack.getComponentsPatch();

                if (!slot.isEmpty() && !slot.contains(item, components)) {
                    long amount = slot.getAmount();
                    slot.extract(slot.getResource(), slot.getComponents(), amount, tx);
                }

                long difference = stack.getCount() - slot.getAmount();
                if (difference > 0) {
                    slot.insert(item, components, difference, tx);
                } else if (difference < 0) {
                    slot.extract(item, components, -difference, tx);
                }
            }

            // Stage 4: Check that items haven't been created or destroyed and only commit everything matches up
            List<ItemStack> pendingItemStacks = collectItemStacks(storage, ItemStack.EMPTY);

            long inserted = maxAmount;
            for (ItemStack itemStack1 : pendingItemStacks) {
                boolean notFound = true;
                for (ItemStack itemStack2 : initialItemStacks) {
                    if (ItemStack.isSameItemSameComponents(itemStack1, itemStack2)) {
                        if (itemStack1.getCount() == itemStack2.getCount()) {
                            notFound = false;
                            break;
                        } else if (ItemStack.isSameItemSameComponents(itemStack1, incomingItemStack)) {
                            inserted = maxAmount + itemStack1.getCount() - itemStack2.getCount();
                            if (inserted < 0 || inserted > maxAmount) {
                                Constant.LOGGER.info("Aborting: inserted < 0 || inserted > maxAmount");
                                return 0;
                            }
                            notFound = false;
                            break;
                        }
                    }
                }

                if (notFound) {
                    Constant.LOGGER.info("Aborting: notFound == true");
                    return 0;
                }
            }

            tx.commit();
            return inserted;
        }
    }

    private static List<ItemStack> collectItemStacks(ResourceStorage<Item, ItemResourceSlot> storage, ItemStack incomingItemStack) {
        List<ItemStack> itemStacks = new ArrayList<>();
        if (!incomingItemStack.isEmpty()) {
            itemStacks.add(incomingItemStack.copy());
        }

        for (int i = 1; i < 10; i++) {
            ItemResourceSlot slot = storage.slot(i);
            if (!slot.isEmpty()) {
                ItemStack itemStack1 = new ItemStack(slot.getResource(), (int) slot.getAmount());
                itemStack1.applyComponents(slot.getComponents());

                boolean handled = false;

                for (int j = 0; j < itemStacks.size(); j++) {
                    ItemStack itemStack2 = itemStacks.get(j);
                    if (ItemStack.isSameItemSameComponents(itemStack1, itemStack2)) {
                        itemStack2.grow(itemStack1.getCount());
                        handled = true;
                        break;
                    }
                }

                if (!handled) {
                    itemStacks.add(itemStack1);
                }
            }
        }

        return itemStacks;
    }

    private static @Nullable CompressingRecipe findRecipe(RecipeMachineBlockEntity<?, ?> machine, List<ItemStack> items) {
        Predicate<CompressingRecipe> predicate = recipe -> {
            return items.stream().allMatch(stack -> recipe.getIngredients().stream().anyMatch(ingredient -> ingredient.test(stack)));
        };

        RecipeHolder<?> holder = machine.getActiveRecipe();
        if (holder != null && holder.value() instanceof CompressingRecipe recipe && predicate.test(recipe)) {
            return recipe;
        }

        RecipeManager recipeManager = machine.getLevel().getRecipeManager();
        List<CompressingRecipe> recipes = recipeManager.getAllRecipesFor(GCRecipes.COMPRESSING_TYPE)
                .stream().map(RecipeHolder::value).filter(predicate).toList();

        if (recipes.size() > 1) {
            Constant.LOGGER.info("Multiple compressing recipes found for the current set of items, arbitrarily choosing the first one.");
            // TODO: Prioritise recipes with fewer missing ingredients, I'm not sure how else to make a more informed decision between these recipes
        }

        return recipes.isEmpty() ? null : recipes.getFirst();
    }
}
