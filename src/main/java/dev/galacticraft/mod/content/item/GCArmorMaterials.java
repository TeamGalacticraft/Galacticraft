/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import dev.galacticraft.mod.Constant;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class GCArmorMaterials {
    public static final Holder<ArmorMaterial> SENSOR_GLASSES = register(Constant.id("sensor_glasses"), 0, 0, 0, 0, 0, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> Ingredient.of(GCItems.METEORIC_IRON_INGOT));
    public static final Holder<ArmorMaterial> HEAVY_DUTY = register(Constant.id("heavy_duty"), 3, 6, 8, 3, 30, 9, SoundEvents.ARMOR_EQUIP_IRON, 1.0F, 1.0F, () -> Ingredient.of(GCItems.COMPRESSED_STEEL));
    public static final Holder<ArmorMaterial> DESH = register(Constant.id("desh"), 4, 7, 9, 4, 42, 12, SoundEvents.ARMOR_EQUIP_IRON, 3.0F, 2.0F, () -> Ingredient.of(GCItems.DESH_INGOT));
    public static final Holder<ArmorMaterial> TITANIUM = register(Constant.id("titanium"), 5, 7, 10, 5, 26, 20, SoundEvents.ARMOR_EQUIP_IRON, 1.0F, 0.0F, () -> Ingredient.of(GCItems.COMPRESSED_TITANIUM));

    private static Holder<ArmorMaterial> register(ResourceLocation id,
                                                  int helmet,
                                                  int chest,
                                                  int leggings,
                                                  int boots,
                                                  int body,
                                                  int enchantability,
                                                  Holder<SoundEvent> equipSound,
                                                  float toughness,
                                                  float knockbackResistance,
                                                  Supplier<Ingredient> repairIngredient) {
        return register(id, helmet, chest, leggings, boots, body, enchantability, equipSound, toughness, knockbackResistance, repairIngredient, List.of(new ArmorMaterial.Layer(id)));
    }

    private static Holder<ArmorMaterial> register(ResourceLocation id,
                                                  int helmet,
                                                  int chest,
                                                  int leggings,
                                                  int boots,
                                                  int body,
                                                  int enchantability,
                                                  Holder<SoundEvent> equipSound,
                                                  float toughness,
                                                  float knockbackResistance,
                                                  Supplier<Ingredient> repairIngredient,
                                                  List<ArmorMaterial.Layer> layers) {
        EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.HELMET, helmet);
        defense.put(ArmorItem.Type.CHESTPLATE, chest);
        defense.put(ArmorItem.Type.LEGGINGS, leggings);
        defense.put(ArmorItem.Type.BOOTS, boots);
        defense.put(ArmorItem.Type.BODY, body);

        return Registry.registerForHolder(
                BuiltInRegistries.ARMOR_MATERIAL,
                id,
                new ArmorMaterial(defense, enchantability, equipSound, repairIngredient, layers, toughness, knockbackResistance)
        );
    }
}