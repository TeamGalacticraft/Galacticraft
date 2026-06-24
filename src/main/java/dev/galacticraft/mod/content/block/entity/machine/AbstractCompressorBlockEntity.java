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

import dev.galacticraft.machinelib.api.block.entity.BasicRecipeMachineBlockEntity;
import dev.galacticraft.machinelib.api.compat.transfer.MachineInsertHandler;
import dev.galacticraft.machinelib.api.storage.ResourceStorage;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.GCRecipes;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractCompressorBlockEntity extends BasicRecipeMachineBlockEntity<CraftingInput, CompressingRecipe> implements MachineInsertHandler<Item, ItemResourceSlot> {
    protected AbstractCompressorBlockEntity(BlockEntityType<? extends AbstractCompressorBlockEntity> type,
                                            BlockPos pos, BlockState state, StorageSpec spec,
                                            int inputSlots, int inputSlotsLen, int outputSlots, int outputSlotsLen) {
        super(type, pos, state, GCRecipes.COMPRESSING_TYPE, spec, inputSlots, inputSlotsLen, outputSlots, outputSlotsLen);
    }

    @Override
    public long insert(ResourceStorage<Item, ItemResourceSlot> storage, TransferVariant<Item> variant, long maxAmount, TransactionContext transaction) {
        if (!(variant instanceof ItemVariant incomingItemVariant)) {
            return 0;
        }

        // Stage 1: Find a valid recipe
        ItemStack incomingItemStack = incomingItemVariant.toStack((int) maxAmount);
        Int2ObjectMap<ItemStack> initialItemStacks = collectItemStacks(storage, incomingItemStack);

        CompressingRecipe recipe = findRecipe(initialItemStacks.values());
        if (recipe == null) {
            return 0;
        }

        // Stage 2: Create a matching between the available item stacks and the recipe's ingredients
        // This approach doesn't always handle the case where we are trying to craft
        // compressed steel using both coal and charcoal, for example

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        List<SharedIngredient> sharedIngredients = new ArrayList<>();

        for (int i = 0; i < this.inputSlotsLen; i++) {
            Ingredient ingredient = i < ingredients.size() ? ingredients.get(i) : Ingredient.EMPTY;

            handled: {
                for (SharedIngredient shared : sharedIngredients) {
                    if (ingredient.equals(shared.ingredient())) {
                        shared.slots().add(this.inputSlotsStart + i); // Skip the fuel/battery slot
                        break handled;
                    }
                }

                // Only reach this point if it has not been handled
                sharedIngredients.add(new SharedIngredient(ingredient, IntArrayList.of(this.inputSlotsStart + i)));
            }
        }

        Int2ObjectMap<ItemStack> toInsert = new Int2ObjectArrayMap<>(9);

        for (SharedIngredient shared : sharedIngredients) {
            if (shared.ingredient().isEmpty()) {
                continue;
            }

            for (ItemStack stack : initialItemStacks.values()) {
                if (shared.ingredient().test(stack)) {
                    int n = shared.slots().size();
                    int count = stack.getCount();

                    for (int i = 0; i < n; i++) {
                        int key = shared.slots().get(i);

                        if (toInsert.containsKey(key)) {
                            return 0;
                        }

                        toInsert.put(key, stack.copyWithCount((count + n - 1 - i) / n));
                    }
                }
            }
        }

        try (Transaction tx = transaction.openNested()) {
            // Stage 3: Rearrange items into the correct slots
            for (int index = this.inputSlotsStart; index < this.inputSlotsStart + this.inputSlotsLen; index++) {
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
            Int2ObjectMap<ItemStack> pendingItemStacks = collectItemStacks(storage, ItemStack.EMPTY);

            if (!initialItemStacks.keySet().equals(pendingItemStacks.keySet())) {
                return 0;
            }

            int incomingKey = ItemStack.hashItemAndComponents(incomingItemStack);

            for (var entry : initialItemStacks.int2ObjectEntrySet()) {
                int key = entry.getKey();
                if (key == incomingKey) {
                    continue;
                }

                ItemStack itemStack1 = entry.getValue();
                ItemStack itemStack2 = pendingItemStacks.get(key);

                if (itemStack1.getCount() != itemStack2.getCount()) {
                    return 0;
                }
            }

            ItemStack itemStack1 = initialItemStacks.get(incomingKey);
            ItemStack itemStack2 = pendingItemStacks.get(incomingKey);

            long inserted = maxAmount - itemStack1.getCount() + itemStack2.getCount();
            if (inserted < 0 || inserted > maxAmount) {
                return 0;
            }

            tx.commit();
            return inserted;
        }
    }

    private Int2ObjectMap<ItemStack> collectItemStacks(ResourceStorage<Item, ItemResourceSlot> storage, ItemStack incomingItemStack) {
        Int2ObjectMap<ItemStack> itemStacks = new Int2ObjectArrayMap<>();

        if (!incomingItemStack.isEmpty()) {
            itemStacks.put(ItemStack.hashItemAndComponents(incomingItemStack), incomingItemStack.copy());
        }

        for (int i = this.inputSlotsStart; i < this.inputSlotsStart + this.inputSlotsLen; i++) {
            ItemResourceSlot slot = storage.slot(i);
            if (!slot.isEmpty()) {
                ItemStack stack = new ItemStack(slot.getResource(), (int) slot.getAmount());
                stack.applyComponents(slot.getComponents());

                int key = ItemStack.hashItemAndComponents(stack);
                if (itemStacks.containsKey(key)) {
                    itemStacks.get(key).grow(stack.getCount());
                } else {
                    itemStacks.put(key, stack);
                }
            }
        }

        return itemStacks;
    }

    private @Nullable CompressingRecipe findRecipe(Collection<ItemStack> items) {
        Predicate<CompressingRecipe> predicate = recipe -> {
            return items.stream().allMatch(stack -> recipe.getIngredients().stream().anyMatch(ingredient -> ingredient.test(stack)));
        };

        RecipeHolder<?> holder = this.getActiveRecipe();
        if (holder != null && holder.value() instanceof CompressingRecipe recipe && predicate.test(recipe)) {
            return recipe;
        }

        if (this.getLevel() == null) {
            return null;
        }

        RecipeManager recipeManager = this.getLevel().getRecipeManager();
        List<CompressingRecipe> recipes = recipeManager.getAllRecipesFor(GCRecipes.COMPRESSING_TYPE)
                .stream().map(RecipeHolder::value).filter(predicate).toList();

        if (recipes.size() > 1) {
            Constant.LOGGER.info("Multiple compressing recipes found for the current set of items, arbitrarily choosing the first one.");
            // TODO: Prioritise recipes with fewer missing ingredients, I'm not sure how else to make a more informed decision between these recipes
        }

        return recipes.isEmpty() ? null : recipes.getFirst();
    }

    private record SharedIngredient(Ingredient ingredient, IntList slots) {
    }
}
