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

package dev.galacticraft.mod.events;

import dev.galacticraft.api.registry.AcidTransformItemRegistry;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class GCSulfuricAcidHandlers {
    public static void register() {
        // Convert Diamond Ores to Diamonds
        AcidTransformItemRegistry.INSTANCE.add(ConventionalItemTags.DIAMOND_ORES,
                itemStack -> convertToDiamond(itemStack, Items.DIAMOND)
        );

        // Convert Netherite Armor to Diamond Armor
        AcidTransformItemRegistry.INSTANCE.add(Items.NETHERITE_HELMET,
                itemStack -> convertToDiamond(itemStack, Items.DIAMOND_HELMET)
        );
        AcidTransformItemRegistry.INSTANCE.add(Items.NETHERITE_CHESTPLATE,
                itemStack -> convertToDiamond(itemStack, Items.DIAMOND_CHESTPLATE)
        );
        AcidTransformItemRegistry.INSTANCE.add(Items.NETHERITE_LEGGINGS,
                itemStack -> convertToDiamond(itemStack, Items.DIAMOND_LEGGINGS)
        );
        AcidTransformItemRegistry.INSTANCE.add(Items.NETHERITE_BOOTS,
                itemStack -> convertToDiamond(itemStack, Items.DIAMOND_BOOTS)
        );

        // Convert Netherite Tools to Diamond Tools
        AcidTransformItemRegistry.INSTANCE.add(Items.NETHERITE_SWORD,
                itemStack -> convertToDiamond(itemStack, Items.DIAMOND_SWORD)
        );
        AcidTransformItemRegistry.INSTANCE.add(Items.NETHERITE_PICKAXE,
                itemStack -> convertToDiamond(itemStack, Items.DIAMOND_PICKAXE)
        );
        AcidTransformItemRegistry.INSTANCE.add(Items.NETHERITE_AXE,
                itemStack -> convertToDiamond(itemStack, Items.DIAMOND_AXE)
        );
        AcidTransformItemRegistry.INSTANCE.add(Items.NETHERITE_SHOVEL,
                itemStack -> convertToDiamond(itemStack, Items.DIAMOND_SHOVEL)
        );
        AcidTransformItemRegistry.INSTANCE.add(Items.NETHERITE_HOE,
                itemStack -> convertToDiamond(itemStack, Items.DIAMOND_HOE)
        );
    }

    public static ItemStack convertToDiamond(ItemStack original, Item item) {
        ItemStack itemStack = original.transmuteCopy(item);
        if (itemStack.isDamageableItem()) {
            float durability = original.getDamageValue();
            durability *= itemStack.getMaxDamage();
            durability /= original.getMaxDamage();
            itemStack.setDamageValue((int) durability);
        }
        return itemStack;
    }
}
