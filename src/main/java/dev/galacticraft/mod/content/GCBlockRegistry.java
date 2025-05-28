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

package dev.galacticraft.mod.content;

import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;

public class GCBlockRegistry extends GCRegistry<Block> {
    private final GCRegistry<Item> ITEMS = new GCRegistry<>(BuiltInRegistries.ITEM);
    private final List<DecorationSet> decorations = new ArrayList<>();

    public GCBlockRegistry() {
        super(BuiltInRegistries.BLOCK);
    }

    public <T extends Block> T registerWithItem(String id, T val) {
        register(id, val);
        ITEMS.register(id, new BlockItem(val, new Item.Properties()));
        return val;
    }

    public DecorationSet registerDecoration(String id, BlockBehaviour.Properties properties, float slabHardness, float slabResistance) {
        Block decoration = register(id, new Block(properties));
        BlockBehaviour.Properties slabProperties = BlockBehaviour.Properties.ofFullCopy(decoration).strength(slabHardness, slabResistance);
        StairBlock decorationStairs = register(id + "_stairs", new StairBlock(decoration.defaultBlockState(), properties));
        SlabBlock decorationSlab = register(id + "_slab", new SlabBlock(slabProperties));
        WallBlock decorationWall = register(id + "_wall", new WallBlock(properties));
        Block detailedDecoration = register("detailed_" + id, new Block(properties));
        StairBlock detailedDecorationStairs = register("detailed_" + id + "_stairs", new StairBlock(detailedDecoration.defaultBlockState(), properties));
        SlabBlock detailedDecorationSlab = register("detailed_" + id + "_slab", new SlabBlock(slabProperties));
        WallBlock detailedDecorationWall = register("detailed_" + id + "_wall", new WallBlock(properties));
        Item.Properties itemProperties = new Item.Properties();
        BlockItem decorationItem = ITEMS.register(id, new BlockItem(decoration, itemProperties));
        BlockItem decorationStairsItem = ITEMS.register(id + "_stairs", new BlockItem(decorationStairs, itemProperties));
        BlockItem decorationSlabItem = ITEMS.register(id + "_slab", new BlockItem(decorationSlab, itemProperties));
        BlockItem decorationWallItem = ITEMS.register(id + "_wall", new BlockItem(decorationWall, itemProperties));
        BlockItem detailedDecorationItem = ITEMS.register("detailed_" + id, new BlockItem(detailedDecoration, itemProperties));
        BlockItem detailedDecorationStairsItem = ITEMS.register("detailed_" + id + "_stairs", new BlockItem(detailedDecorationStairs, itemProperties));
        BlockItem detailedDecorationSlabItem = ITEMS.register("detailed_" + id + "_slab", new BlockItem(detailedDecorationSlab, itemProperties));
        BlockItem detailedDecorationWallItem = ITEMS.register("detailed_" + id + "_wall", new BlockItem(detailedDecorationWall, itemProperties));
        DecorationSet decorationSet = new DecorationSet(
                decoration, decorationStairs, decorationSlab, decorationWall, detailedDecoration, detailedDecorationStairs, detailedDecorationSlab, detailedDecorationWall,
                decorationItem, decorationStairsItem, decorationSlabItem, decorationWallItem, detailedDecorationItem, detailedDecorationStairsItem, detailedDecorationSlabItem, detailedDecorationWallItem
        );
        // Fabric registry sync issue?
        if (FabricDataGenHelper.ENABLED) {
            Item.BY_BLOCK.put(decoration, decorationItem);
            Item.BY_BLOCK.put(decorationStairs, decorationStairsItem);
            Item.BY_BLOCK.put(decorationSlab, decorationSlabItem);
            Item.BY_BLOCK.put(decorationWall, decorationWallItem);
            Item.BY_BLOCK.put(detailedDecoration, detailedDecorationItem);
            Item.BY_BLOCK.put(detailedDecorationStairs, detailedDecorationStairsItem);
            Item.BY_BLOCK.put(detailedDecorationSlab, detailedDecorationSlabItem);
            Item.BY_BLOCK.put(detailedDecorationWall, detailedDecorationWallItem);
        }
        this.decorations.add(decorationSet);
        return decorationSet;
    }

    public List<DecorationSet> getDecorations() {
        return this.decorations;
    }

    public record DecorationSet(
            Block block, StairBlock stairs, SlabBlock slab, WallBlock wall,
            Block detailedBlock, StairBlock detailedStairs, SlabBlock detailedSlab, WallBlock detailedWall,
            BlockItem item, BlockItem stairsItem, BlockItem slabItem, BlockItem wallItem,
            BlockItem detailedItem, BlockItem detailedStairsItem, BlockItem detailedSlabItem, BlockItem detailedWallItem
    ) implements ItemLike {
        @Override
        public Item asItem() {
            return item;
        }
    }
}
