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

import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.io.IOException;
import java.util.Collection;

import static dev.galacticraft.mod.Constant.MOD_ID;

public class CannedFoodItem extends Item {
    private final String name;
    private final String displayName;
    private final Item cannedItem;
    private int color;

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        //COURTESY OF POTATO
        super.finishUsingItem(itemStack, level, livingEntity);
        if (itemStack.isEmpty()) {
            return new ItemStack(GCItems.TIN_CANISTER);
        }

        if (livingEntity instanceof Player player) {
            if (!player.getAbilities().instabuild) {
                ItemStack canStack = new ItemStack(GCItems.EMPTY_FOOD_CAN);
                if (!player.getInventory().add(canStack)) {
                    player.drop(canStack, false);
                }
            }
        }
        return itemStack;
    }

    public CannedFoodItem(Properties settings, String name, String displayName, Item cannedItem) {
        super(settings);
        this.name = name;
        this.displayName = displayName;
        this.cannedItem = cannedItem;
        this.color = 0;
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        super.onCraftedBy(stack, world, player);
        // Add functionality when the item is crafted (optional)
    }

    public static CannedFoodItem newCan(Item cannedItem, String itemName,  String displayName)
    {
        Properties settings = new Item.Properties().food(cannedItem.getFoodProperties()).rarity(cannedItem.getDefaultInstance().getRarity());
        CannedFoodItem item = new CannedFoodItem(settings, itemName, displayName, cannedItem);
        //registers the new item ingame
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, itemName), item);
        //sets the model of the item
        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.resolveModel().register(context -> {
                if (Constant.id("item/" + itemName).equals(context.id()))
                {
                    return context.getOrLoadModel(Constant.id("item/canned_food_template"));
                }
                return null;
            });
        });
        //adds the item to the galacticraft cans creative tab
        ItemGroupEvents.modifyEntriesEvent(ResourceKey.create(Registries.CREATIVE_MODE_TAB, Constant.id(Constant.Item.ITEM_GROUP_CANS))).register(entries -> {
            entries.accept(item.getDefaultInstance());
        });
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            return item.getColor(tintIndex);
        },item);
        return item;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(this.displayName);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return this.cannedItem.getDefaultInstance().hasFoil();
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public int getColor(int layer) {
        // Specify color for each layer, you can add more layers and their colors
        if (layer == 1) {
            return this.color;
        } else {
            return 0xFFFFFF; // Default color (no tint)
        }
    }

    public Item getCannedItem()
    {
        return this.cannedItem;
    }
}