package io.github.teamgalacticraft.galacticraft.items;

import io.github.teamgalacticraft.galacticraft.tag.GCItemTags;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Lazy;

import java.util.function.Supplier;

public enum GCToolMaterials implements ToolMaterial {

    DESH(3, 1024, 5.0F, 2.5F, 10, () -> {
        return Ingredient.fromTag(GCItemTags.DESH_INGOT);
    }),

    TITANIUM(4, 760, 14.0F, 4.0F, 16, () -> {
        return Ingredient.fromTag(GCItemTags.TITANIUM_INGOT);
    });


    private final int miningLevel;
    private final int durability;
    private final float blockBreakSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Lazy<Ingredient> repairIngredient;

    GCToolMaterials(int miningLevel, int durability, float breakSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.miningLevel = miningLevel;
        this.durability = durability;
        this.blockBreakSpeed = breakSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = new Lazy<>(repairIngredient);
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public float getBlockBreakingSpeed() {
        return blockBreakSpeed;
    }

    @Override
    public float getAttackDamage() {
        return attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return miningLevel;
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }
}
