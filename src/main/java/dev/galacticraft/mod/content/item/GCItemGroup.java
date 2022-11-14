/*
 * Copyright (c) 2019-2022 Team Galacticraft
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
import dev.galacticraft.mod.content.GCBlocks;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GCItemGroup {
    public static final CreativeModeTab ITEMS_GROUP = FabricItemGroupBuilder.create(new ResourceLocation(Constant.MOD_ID, Constant.Item.ITEM_GROUP))
            .icon(() -> new ItemStack(GCItem.CANVAS))
            .appendItems((itemStacks, creativeModeTab) -> {
                // todo: add rockets here
                for (Item item : Registry.ITEM)
                    item.fillItemCategory(creativeModeTab, (NonNullList<ItemStack>) itemStacks);
            })
            .build();

    public static final CreativeModeTab BLOCKS_GROUP = FabricItemGroupBuilder.create(
            new ResourceLocation(Constant.MOD_ID, Constant.Block.ITEM_GROUP_BLOCKS))
            .icon(() -> new ItemStack(GCBlocks.MOON_TURF)).build();

    public static final CreativeModeTab MACHINES_GROUP = FabricItemGroupBuilder.create(
            new ResourceLocation(Constant.MOD_ID, Constant.Block.ITEM_GROUP_MACHINES))
            .icon(() -> new ItemStack(GCBlocks.COAL_GENERATOR)).build();
}
