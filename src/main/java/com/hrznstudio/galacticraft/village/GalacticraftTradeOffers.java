/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.village;

import com.hrznstudio.galacticraft.items.GalacticraftItems;
import net.minecraft.core.BlockPos;
import net.minecraft.item.*;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Random;

public class GalacticraftTradeOffers {

    public static class BuyForOneEmeraldFactory implements VillagerTrades.ItemListing {
        private final Item buy;
        private final int price;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public BuyForOneEmeraldFactory(ItemLike item, int price, int maxUses, int experience) {
            this.buy = item.asItem();
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = 0.05F;
        }

        public MerchantOffer getOffer(Entity entity, Random random) {
            ItemStack itemStack = new ItemStack(this.buy, this.price);
            return new MerchantOffer(itemStack, new ItemStack(GalacticraftItems.LUNAR_SAPPHIRE), this.maxUses, this.experience, this.multiplier);
        }
    }

    public static class SellMapFactory implements VillagerTrades.ItemListing {
        private final int price;
        private final StructureFeature<?> structure;
        private final MapDecoration.Type iconType;
        private final int maxUses;
        private final int experience;

        public SellMapFactory(int price, StructureFeature<?> feature, MapDecoration.Type iconType, int maxUses, int experience) {
            this.price = price;
            this.structure = feature;
            this.iconType = iconType;
            this.maxUses = maxUses;
            this.experience = experience;
        }

        @Nullable
        public MerchantOffer getOffer(Entity entity, Random random) {
            if (!(entity.level instanceof ServerLevel)) {
                return null;
            } else {
                ServerLevel serverWorld = (ServerLevel) entity.level;
                BlockPos blockPos = serverWorld.findNearestMapFeature(this.structure, entity.blockPosition(), 100, true);
                if (blockPos != null) {
                    ItemStack itemStack = MapItem.create(serverWorld, blockPos.getX(), blockPos.getZ(), (byte) 2, true, true);
                    MapItem.renderBiomePreviewMap(serverWorld, itemStack);
                    MapItemSavedData.addTargetDecoration(itemStack, blockPos, "+", this.iconType);
                    itemStack.setHoverName(new TranslatableComponent("filled_map." + this.structure.getFeatureName().toLowerCase(Locale.ROOT)));
                    return new MerchantOffer(new ItemStack(GalacticraftItems.LUNAR_SAPPHIRE, this.price), new ItemStack(Items.COMPASS), itemStack, this.maxUses, this.experience, 0.2F);
                } else {
                    return null;
                }
            }
        }
    }

    public static class SellItemFactory implements VillagerTrades.ItemListing {
        private final ItemStack sell;
        private final int price;
        private final int count;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public SellItemFactory(Block block, int price, int count, int maxUses, int experience) {
            this(new ItemStack(block), price, count, maxUses, experience);
        }

        public SellItemFactory(Item item, int price, int count, int experience) {
            this(new ItemStack(item), price, count, 12, experience);
        }

        public SellItemFactory(Item item, int price, int count, int maxUses, int experience) {
            this(new ItemStack(item), price, count, maxUses, experience);
        }

        public SellItemFactory(ItemStack stack, int price, int count, int maxUses, int experience) {
            this(stack, price, count, maxUses, experience, 0.05F);
        }

        public SellItemFactory(ItemStack stack, int price, int count, int maxUses, int experience, float multiplier) {
            this.sell = stack;
            this.price = price;
            this.count = count;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        public MerchantOffer getOffer(Entity entity, Random random) {
            return new MerchantOffer(new ItemStack(GalacticraftItems.LUNAR_SAPPHIRE, this.price), new ItemStack(this.sell.getItem(), this.count), this.maxUses, this.experience, this.multiplier);
        }
    }
}
