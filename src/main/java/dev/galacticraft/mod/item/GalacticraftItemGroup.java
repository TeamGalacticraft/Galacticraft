package dev.galacticraft.mod.item;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.GalacticraftBlock;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class GalacticraftItemGroup {
    public static final ItemGroup ITEMS_GROUP = FabricItemGroupBuilder.create(new Identifier(Constant.MOD_ID, Constant.Item.ITEM_GROUP))
            .icon(() -> new ItemStack(GalacticraftItem.CANVAS))
            .build();

    public static final ItemGroup BLOCKS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constant.MOD_ID, Constant.Block.ITEM_GROUP_BLOCKS))
            .icon(() -> new ItemStack(GalacticraftBlock.MOON_TURF)).build();

    public static final ItemGroup MACHINES_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constant.MOD_ID, Constant.Block.ITEM_GROUP_MACHINES))
            .icon(() -> new ItemStack(GalacticraftBlock.COAL_GENERATOR)).build();
}
