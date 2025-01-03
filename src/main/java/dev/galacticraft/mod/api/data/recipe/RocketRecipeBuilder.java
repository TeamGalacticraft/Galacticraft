package dev.galacticraft.mod.api.data.recipe;

import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.mod.recipe.RocketRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class RocketRecipeBuilder extends GCRecipeBuilder {
    private int bodyHeight;
    private Ingredient body, cone, engine, fins;
    private Ingredient boosters = Ingredient.EMPTY;
    private RocketData rocketData;

    protected RocketRecipeBuilder(ItemLike result, int count) {
        super("rocket", result, count);
    }

    public static RocketRecipeBuilder create(ItemLike result) {
        return new RocketRecipeBuilder(result, 1);
    }

    public RocketRecipeBuilder bodyHeight(int bodyHeight) {
        this.bodyHeight = bodyHeight;
        return this;
    }

    public RocketRecipeBuilder body(ItemLike body) {
        this.body = Ingredient.of(body);
        return this;
    }

    public RocketRecipeBuilder body(TagKey<Item> body) {
        this.body = Ingredient.of(body);
        return this;
    }

    public RocketRecipeBuilder cone(ItemLike cone) {
        this.cone = Ingredient.of(cone);
        return this;
    }

    public RocketRecipeBuilder cone(TagKey<Item> cone) {
        this.cone = Ingredient.of(cone);
        return this;
    }

    public RocketRecipeBuilder engine(ItemLike engine) {
        this.engine = Ingredient.of(engine);
        return this;
    }

    public RocketRecipeBuilder engine(TagKey<Item> engine) {
        this.engine = Ingredient.of(engine);
        return this;
    }

    public RocketRecipeBuilder fins(ItemLike fins) {
        this.fins = Ingredient.of(fins);
        return this;
    }

    public RocketRecipeBuilder fins(TagKey<Item> fins) {
        this.fins = Ingredient.of(fins);
        return this;
    }

    public RocketRecipeBuilder boosters(ItemLike boosters) {
        this.boosters = Ingredient.of(boosters);
        return this;
    }

    public RocketRecipeBuilder boosters(TagKey<Item> boosters) {
        this.boosters = Ingredient.of(boosters);
        return this;
    }

    public RocketRecipeBuilder rocketData(RocketData rocketData) {
        this.rocketData = rocketData;
        return this;
    }

    @Override
    public Recipe<?> createRecipe(ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }

        ItemStack result = new ItemStack(this.result, this.count);
        result.set(GCDataComponents.ROCKET_DATA, this.rocketData);
        return new RocketRecipe(this.group, result, this.bodyHeight, this.body, this.cone, this.engine, this.fins, this.boosters);
    }
}
