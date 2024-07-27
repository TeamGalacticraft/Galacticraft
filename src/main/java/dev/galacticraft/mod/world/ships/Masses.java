package dev.galacticraft.mod.world.ships;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class Masses {
    private static HashMap<Item, Integer> masses = new HashMap<>();
    static {
        masses.put(Items.COAL_BLOCK, 2250); //weight of pure carbon. coal is technically 85% carbon but close enough
        masses.put(Items.IRON_BLOCK, 7870);
        masses.put(Items.GOLD_BLOCK, 19300);
        masses.put(Items.EMERALD_BLOCK, 2900);
        masses.put(Items.DIAMOND_BLOCK, 3539);
        masses.put(Items.REDSTONE_BLOCK, 1700);
        masses.put(Items.LAPIS_BLOCK, 2750);
        masses.put(Items.COPPER_BLOCK, 8960);
        masses.put(Items.ANCIENT_DEBRIS, 2550); //estimate based on assumption of ancient debris being broken down portal blocks
        masses.put(Items.NETHERITE_INGOT, 18776);
        masses.put(Items.NETHERITE_BLOCK, 168984);
        masses.put(Items.SLIME_BLOCK, 900); //estimate based on rubber's density
        masses.put(Items.DIRT, 1300);
        masses.put(Items.STONE, 1602);
        masses.put(Items.BRICKS, 1476);
        masses.put(Items.HONEY_BLOCK, 1420);
    }
    public static double get(BlockState initialBlock) {
        return masses.getOrDefault(initialBlock.getBlock().asItem(), 1000);
    }

    public static int getMass(Item item)
    {
        return masses.getOrDefault(item, -1);
    }

    public static void processRecipes(RecipeManager recipeManager)
    {
        List<RecipeHolder> unprocessedRecipes = new java.util.ArrayList<>();

        for (RecipeHolder<?> recipe : recipeManager.getAllRecipesFor(RecipeType.CRAFTING)) {
            int totalMass = 0;

            for (Ingredient ingredient : recipe.value().getIngredients()) {
                for (ItemStack itemStack : ingredient.getItems()) {
                    Item item = itemStack.getItem();
                    int itemMass = getMass(item);
                    if (itemMass == -1)
                    {
                        unprocessedRecipes.add(recipe);
                    }
                    totalMass += itemMass * itemStack.getCount();
                }
            }
            if (!unprocessedRecipes.contains(recipe))
            {
                //ItemStack output = recipe.value().;
                //masses.put(output.getItem(), totalMass);
            }
        }
    }
}
