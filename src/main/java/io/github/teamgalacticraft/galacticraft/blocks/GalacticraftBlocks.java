package io.github.teamgalacticraft.galacticraft.blocks;

import io.github.teamgalacticraft.galacticraft.Constants;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftBlocks {

    // Blocks
    public static final Block TEST_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).build());


    // Block Items
    public static final BlockItem TEST_BLOCK_ITEM = new BlockItem(TEST_BLOCK, new Item.Settings());

    public static ItemGroup BLOCKS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.BLOCKS_GROUP))
            .stacksForDisplay(itemStack -> {
                itemStack.add(new ItemStack(TEST_BLOCK_ITEM));
                // add blocks to creative menu
            })
            .build();


    public static void init() {
        // Register Blocks
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, "test_block"), TEST_BLOCK);

        // Register Block items
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, "test_block"), TEST_BLOCK_ITEM);
    }

}
