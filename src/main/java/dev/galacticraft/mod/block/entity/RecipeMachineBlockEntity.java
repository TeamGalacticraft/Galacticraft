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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public abstract class RecipeMachineBlockEntity<C extends Inventory, R extends Recipe<C>> extends MachineBlockEntity {
    private final @NotNull RecipeType<R> recipeType;
    private @Nullable R activeRecipe;
    private int progress;
    private int maxProgress = 0;

    public RecipeMachineBlockEntity(BlockEntityType<? extends RecipeMachineBlockEntity<C, R>> type, BlockPos pos, BlockState state, @NotNull RecipeType<R> recipeType) {
        super(type, pos, state);
        this.recipeType = recipeType;
    }

    protected abstract @NotNull C craftingInv();

    protected abstract boolean outputStacks(R recipe, TransactionContext transaction);

    protected abstract boolean extractCraftingMaterials(R recipe, TransactionContext transaction);

    @Override
    public void tickWork() {
        if (this.getStatus().getType().isActive()) {
            R recipe = this.findValidRecipe();
            if (this.canOutput(recipe, null)) {
                if (this.activeRecipe() != recipe) {
                    this.setRecipeAndProgress(recipe);
                } else {
                    if (this.progress(this.progress() + 1) >= this.maxProgress()) {
                        try (Transaction transaction = Transaction.openOuter()) {
                            this.craft(recipe, transaction);
                            transaction.commit();
                        }
                    } else {
                        this.resetRecipeProgress();
                    }
                }
            } else {
                this.resetRecipeProgress();
            }
        }
    }

    protected boolean canOutput(R recipe, @Nullable TransactionContext context) {
        try (Transaction transaction = Transaction.openNested(context)) {
            return outputStacks(recipe, transaction);
        }
    }

    protected void craft(R recipe, TransactionContext transaction) {
        try (Transaction inner = transaction.openNested()) {
            if (this.extractCraftingMaterials(recipe, inner)) {
                if (this.outputStacks(recipe, inner)) {
                    inner.commit();
                }
            }
        }

        recipe = this.findValidRecipe();
        if (recipe == null) this.resetRecipeProgress();
        else this.setRecipeAndProgress(recipe);
    }

    protected void resetRecipeProgress() {
        this.activeRecipe(null);
        this.progress(0);
        this.maxProgress(0);
    }

    protected void setRecipeAndProgress(@NotNull R recipe) {
        this.activeRecipe(recipe);
        this.maxProgress(this.getProcessTime(recipe));
        this.progress(0);
    }

    public RecipeType<R> recipeType() {
        return this.recipeType;
    }

    protected @Nullable R findValidRecipe() {
        assert this.world != null;
        return this.world.getRecipeManager().getFirstMatch(this.recipeType(), this.craftingInv(), this.world).orElse(null);
    }

    protected abstract int getProcessTime(@NotNull R recipe);

    public int progress(int progress) {
        return this.progress = progress;
    }

    public void maxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public int progress() {
        return this.progress;
    }

    public @Nullable R activeRecipe() {
        return this.activeRecipe;
    }

    protected void activeRecipe(@Nullable R activeRecipe) {
        this.activeRecipe = activeRecipe;
    }

    public int maxProgress() {
        return this.maxProgress;
    }

    public boolean active() {
        return this.maxProgress > 0;
    }
    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt(Constant.Nbt.PROGRESS, this.progress());
        tag.putInt(Constant.Nbt.MAX_PROGRESS, this.maxProgress());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.progress(nbt.getInt(Constant.Nbt.PROGRESS));
        this.maxProgress(nbt.getInt(Constant.Nbt.MAX_PROGRESS));
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        this.activeRecipe(this.findValidRecipe());
    }

    @FunctionalInterface
    public interface RecipeTimeFunction<C extends Inventory, R extends Recipe<C>> {
        /**
         * Returns the process length of the recipe.
         * @param recipe The recipe to get the process length of
         * @return the process length of the recipe.
         */
        int getRecipeLength(R recipe);
    }

    @FunctionalInterface
    public interface ItemOutputFunction {
        /**
         * Be sure to copy the stack (even if you don't change anything else)
         */
        ItemStack getOutput(ItemStack stack);
    }
}
