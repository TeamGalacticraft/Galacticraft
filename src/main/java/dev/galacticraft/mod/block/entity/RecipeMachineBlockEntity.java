package dev.galacticraft.mod.block.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import com.mojang.datafixers.util.Pair;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class RecipeMachineBlockEntity<C extends Inventory> extends MachineBlockEntity {
    private final Object2ObjectMap<RecipeType<? extends Recipe<C>>, Object2IntFunction<Recipe<C>>> recipeTypes;
    private int progress;
    private int maxProgress = -1;

    public RecipeMachineBlockEntity(BlockEntityType<? extends RecipeMachineBlockEntity> type, Object2ObjectMap<RecipeType<? extends Recipe<C>>, Object2IntFunction<Recipe<C>>> recipeTypes, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.recipeTypes = recipeTypes;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }
    
    public abstract C getCraftingSubInventory(); 
    
    public abstract FixedItemInv getOutputSubInventory();

    @Override
    public void tickWork() {
        if (this.getProgress() > 0) {
            if (this.getStatus().getType().isActive()) {
                if (this.getMaxProgress() != -1) {
                    if (this.setProgress(this.getProgress() + 1) >= this.getMaxProgress()) {
                        Pair<Recipe<C>, Integer> recipe = this.getRecipe();
                        this.maxProgress = recipe.getSecond();
                        List<ItemStack> stacks = recipe.getFirst().getRemainder(this.getCraftingSubInventory());
                        for (int i = 0; i < stacks.size(); i++) {
                            if (stacks.get(i).isEmpty()) {
                                ItemStack stack = this.getCraftingSubInventory().getStack(i).copy();
                                stack.decrement(1);
                                this.getCraftingSubInventory().setStack(i, stack);
                            } else {
                                this.getCraftingSubInventory().setStack(i, stacks.get(i));
                            }
                        }
                        if (!this.getOutputSubInventory().getInsertable().attemptInsertion(recipe.getFirst().craft(this.getCraftingSubInventory()), Simulation.ACTION).isEmpty())
                            throw new RuntimeException();
                        this.setProgress(0);
                    }
                } else {
                    if (this.canCraftAnything()) {
                        this.setProgress(this.getProgress() + 1);
                        this.setMaxProgress(this.getRecipe().getSecond());
                    } else {
                        this.setProgress(this.getProgress() - 1);
                    }
                }
            } else {
                this.setProgress(this.getProgress() - 1);
            }
        }
    }
    
    protected Pair<Recipe<C>, Integer> getRecipe() {
        for (Map.Entry<RecipeType<? extends Recipe<C>>, Object2IntFunction<Recipe<C>>> entry : this.recipeTypes.entrySet()) {
            Optional<? extends Recipe<C>> optional = this.world.getRecipeManager().getFirstMatch(entry.getKey(), this.getCraftingSubInventory(), this.world);
            if (optional.isPresent()) {
                return Pair.of(optional.get(), entry.getValue().apply(optional.get()));
            }
        }
        return null;
    }

    protected boolean canCraftAnything() {
        Pair<Recipe<C>, Integer> recipe = this.getRecipe();
        if (recipe != null) {
            return this.getOutputSubInventory().getInsertable().attemptInsertion(recipe.getFirst().craft(this.getCraftingSubInventory()), Simulation.SIMULATE).isEmpty();
        }
        return false;
    }

    protected boolean canCraft(Pair<Recipe<C>, Integer> recipe) {
        if (recipe != null) {
            return this.getOutputSubInventory().getInsertable().attemptInsertion(recipe.getFirst().craft(this.getCraftingSubInventory()), Simulation.SIMULATE).isEmpty();
        }
        return false;
    }

    public int setProgress(int progress) {
        return this.progress = progress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return null;
    }
}
