/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import com.google.common.base.Suppliers;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum GCArmorMaterial implements ArmorMaterial {
    SENSOR_GLASSES("sensor_glasses",
            0,
            new int[]{0, 0, 0, 0},
            0,
            SoundEvents.ARMOR_EQUIP_IRON,
            0.0f,
            () -> Ingredient.of(GCItems.METEORIC_IRON_INGOT),
            0.0f
    ), // TODO: add actual functionality
    HEAVY_DUTY(
            "heavy_duty",
            30,
            new int[]{3, 6, 8, 3},
            9,
            SoundEvents.ARMOR_EQUIP_IRON,
            1.0f,
            () -> Ingredient.of(GCItems.COMPRESSED_STEEL),
            1.0f
    ),
    DESH("desh", 42,
            new int[]{4, 7, 9, 4},
            12,
            SoundEvents.ARMOR_EQUIP_IRON,
            3.0f,
            () -> Ingredient.of(GCItems.DESH_INGOT),
            2.0f
    ),
    TITANIUM(
            "titanium",
            26, new int[]{5, 7, 10, 5},
            20,
            SoundEvents.ARMOR_EQUIP_IRON,
            1.0f,
            () -> Ingredient.of(GCItems.COMPRESSED_TITANIUM),
            0.0f
    );

    private static final int[] BASE_DURABILITY = {462, 672, 630, 546};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionValues;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final Supplier<Ingredient> repairIngredient;
    private final float knockbackResistance;

    GCArmorMaterial(String name, int durabilityMultiplier, int[] protectionValues, int enchantability, SoundEvent equipSound, float toughness, Supplier<Ingredient> repairIngredient, float knockbackResistance) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionValues = protectionValues;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.repairIngredient = Suppliers.memoize(repairIngredient::get);
        this.knockbackResistance = knockbackResistance;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return BASE_DURABILITY[type.getSlot().getIndex()] * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return this.protectionValues[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }
}