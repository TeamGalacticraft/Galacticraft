package io.github.teamgalacticraft.galacticraft.recipes;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CompressingRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final String group;
    private final DefaultedList<Ingredient> inputs;
    private final ItemStack output;
    private int width;
    private int height;

    public CompressingRecipe(Identifier id, String group, int width, int height, DefaultedList<Ingredient> input, ItemStack output) {
        this.id = id;
        this.group = group;
        this.width = width;
        this.height = height;
        this.inputs = input;
        this.output = output;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        for (int int_1 = 0; int_1 <= 3 - this.width; ++int_1) {
            for (int int_2 = 0; int_2 <= 3 - this.height; ++int_2) {
                if (this.matchesSmall(inv, int_1, int_2, true)) {
                    return true;
                }

                if (this.matchesSmall(inv, int_1, int_2, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchesSmall(Inventory craftingInventory_1, int int_1, int int_2, boolean boolean_1) {
        for (int x = 0; x < 2; ++x) {
            for (int y = 0; y < 1; ++y) {
                int int_5 = x - int_1;
                int int_6 = y - int_2;
                Ingredient ingredient_1 = Ingredient.EMPTY;
                if (int_5 >= 0 && int_6 >= 0 && int_5 < this.width && int_6 < this.height) {
                    if (boolean_1) {
                        ingredient_1 = this.inputs.get(this.width - int_5 - 1 + int_6 * this.width);
                    } else {
                        ingredient_1 = this.inputs.get(int_5 + int_6 * this.width);
                    }
                }

                if (!ingredient_1.method_8093(craftingInventory_1.getInvStack(x + y * 3))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int var1, int var2) {
        return true;
    }

    public DefaultedList<Ingredient> getPreviewInputs() {
        DefaultedList<Ingredient> list = DefaultedList.create();
        list.addAll(this.inputs);
        return list;
    }

    @Override
    public RecipeType<?> getType() {
        return GalacticraftRecipes.COMPRESSING_TYPE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GalacticraftRecipes.COMPRESSING_SERIALIZER;
    }

    public List<Ingredient> getInputs() {
        return inputs;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}