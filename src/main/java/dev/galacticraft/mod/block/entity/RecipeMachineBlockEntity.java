/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.block.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RecipeMachineBlockEntity<C extends Inventory, R extends Recipe<C>> extends MachineBlockEntity {
    private final @NotNull RecipeType<R> recipeType;
    private final @NotNull RecipeTimeFunction<C, R> recipeTimeFunction;
    private final ItemOutputFunction itemOutputFunction;
    private @Nullable R activeRecipe;
    private int progress;
    private int maxProgress = -1;

    public RecipeMachineBlockEntity(BlockEntityType<? extends RecipeMachineBlockEntity<C, R>> type, BlockPos pos, BlockState state, @NotNull RecipeType<R> recipeType, @NotNull RecipeTimeFunction<C, R> recipeTimeFunction) {
        this(type, pos, state, recipeType, recipeTimeFunction, ItemStack::copy);
    }

    public RecipeMachineBlockEntity(BlockEntityType<? extends RecipeMachineBlockEntity<C, R>> type, BlockPos pos, BlockState state, @NotNull RecipeType<R> recipeType, @NotNull RecipeTimeFunction<C, R> recipeTimeFunction, @NotNull ItemOutputFunction itemOutputFunction) {
        super(type, pos, state);
        this.recipeType = recipeType;
        this.recipeTimeFunction = recipeTimeFunction;
        this.itemOutputFunction = itemOutputFunction;
    }

    public int progress() {
        return this.progress;
    }

    public @Nullable R activeRecipe() {
        return this.activeRecipe;
    }

    public void activeRecipe(@Nullable R activeRecipe) {
        this.activeRecipe = activeRecipe;
    }

    public int maxProgress() {
        return this.maxProgress;
    }

    public boolean active() {
        return this.maxProgress >= 0;
    }
    
    public abstract @NotNull C craftingInv();
    
    public abstract @NotNull FixedItemInv outputInv();

    @Override
    public void tickWork() {
        if (this.getStatus().getType().isActive()) {
            R recipe = this.recipe();
            if (this.activeRecipe() == null) {
                if (this.canCraft(recipe)) {
                    this.recipe(recipe);
                }
            } else {
                if (this.canCraft(recipe)) {
                    if (this.activeRecipe() == recipe) {
                        if (this.progress(this.progress() + 1) >= this.maxProgress()) {
                            this.craft(recipe);
                        }
                    } else {
                        this.recipe(recipe);
                    }
                } else {
                    this.resetRecipe();
                }
            }
        } else {
            this.resetRecipe();
        }
    }

    protected void craft(R recipe) {
        DefaultedList<ItemStack> list = recipe.getRemainder(this.craftingInv());
        ItemStack output = itemOutputFunction.getOutput(recipe.craft(this.craftingInv()));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != ItemStack.EMPTY) {
                this.craftingInv().setStack(i, list.get(i));
            } else {
                this.craftingInv().removeStack(i, 1);
            }
        }
        assert this.outputInv().getInsertable().insert(output).isEmpty();

        recipe = this.recipe();
        if (recipe == null) this.resetRecipe();
        else this.recipe(recipe);
    }

    protected void resetRecipe() {
        this.activeRecipe(null);
        this.progress(0);
        this.maxProgress(-1);
    }

    protected void recipe(@NotNull R recipe) {
        this.activeRecipe(recipe);
        this.maxProgress(this.getProcessTime(recipe));
        this.progress(0);
    }

    public RecipeType<R> recipeType() {
        return this.recipeType;
    }

    protected @Nullable R recipe() {
        assert this.world != null;
        return this.world.getRecipeManager().getFirstMatch(this.recipeType(), this.craftingInv(), this.world).orElse(null);
    }

    protected int getProcessTime(@NotNull R recipe) {
        return this.recipeTimeFunction.getRecipeLength(recipe);
    }

    @Contract("null->false")
    protected boolean canCraft(@Nullable R recipe) {
        assert recipe == this.recipe();
        if (recipe != null) {
            return this.outputInv().getInsertable().attemptInsertion(itemOutputFunction.getOutput(recipe.craft(this.craftingInv())), Simulation.SIMULATE).isEmpty();
        }
        return false;
    }

    public int progress(int progress) {
        return this.progress = progress;
    }

    public void maxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putInt("Progress", this.progress());
        tag.putInt("MaxProgress", this.maxProgress());
        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.progress(tag.getInt(Constant.Nbt.PROGRESS));
        this.maxProgress(tag.getInt(Constant.Nbt.MAX_PROGRESS));
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        this.activeRecipe(this.recipe());
    }

    @FunctionalInterface
    public interface RecipeTimeFunction<C extends Inventory, R extends Recipe<C>> {
        /**
         * Returns the process length of the recipe, or {@code 0} if it should be instantly processed.
         * @param recipe The recipe to get the process length of
         * @return the process length of the recipe, or {@code 0} if it should be instantly processed.
         */
        int getRecipeLength(R recipe);
    }

    @FunctionalInterface
    public interface ItemOutputFunction {
        /**
         * Be sure to copy the stack (even if you dont change anything else)
         */
        ItemStack getOutput(ItemStack stack);
    }
}
