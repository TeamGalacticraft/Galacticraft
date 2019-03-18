package io.github.teamgalacticraft.galacticraft.blocks;

import io.github.teamgalacticraft.galacticraft.Constants;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.OreBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftBlocks {

    private static final Marker BLOCKS = MarkerManager.getMarker("Blocks"); // Galacticraft/Blocks

    // Blocks
    public static final Block TEST_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).build());

    //What used to be block_basic_core
    public static final Block TIN_DECORATION_BLOCK = new OreBlock(Block.Settings.of(Material.STONE).strength(2.0F, 2.0F));
    public static final Block TIN_WALL_BLOCK = new OreBlock(Block.Settings.of(Material.STONE).strength(2.0F, 2.0F));
    public static final Block COPPER_ORE = new OreBlock(Block.Settings.of(Material.STONE).strength(5.0F, 3.0F));
    public static final Block TIN_ORE = new OreBlock(Block.Settings.of(Material.STONE).strength(5.0F, 3.0F));
    public static final Block ALUMINUM_ORE = new OreBlock(Block.Settings.of(Material.STONE).strength(5.0F, 3.0F));
    public static final Block SILICON_ORE = new SiliconOreBlock(Block.Settings.of(Material.STONE).strength(5.0F, 3.0F));
    public static final Block COPPER_BLOCK = new  Block(Block.Settings.of(Material.METAL).strength(5.0F, 6.0F));
    public static final Block TIN_BLOCK = new  Block(Block.Settings.of(Material.METAL).strength(5.0F, 6.0F));
    public static final Block ALUMINUM_BLOCK = new  Block(Block.Settings.of(Material.METAL).strength(5.0F, 6.0F));
    public static final Block SILICON_BLOCK = new  Block(Block.Settings.of(Material.METAL).strength(5.0F, 6.0F));


    // Block Items
    public static final BlockItem TEST_BLOCK_ITEM = new BlockItem(TEST_BLOCK, new Item.Settings());

    public static final BlockItem TIN_DECORATION_BLOCK_ITEM = new BlockItem(TIN_DECORATION_BLOCK, new Item.Settings());
    public static final BlockItem TIN_WALL_BLOCK_ITEM = new BlockItem(TIN_WALL_BLOCK, new Item.Settings());
    public static final BlockItem COPPER_ORE_ITEM = new BlockItem(COPPER_ORE, new Item.Settings());
    public static final BlockItem TIN_ORE_ITEM = new BlockItem(TIN_ORE, new Item.Settings());
    public static final BlockItem ALUMINUM_ORE_ITEM = new BlockItem(ALUMINUM_ORE, new Item.Settings());
    public static final BlockItem SILICON_ORE_ITEM = new BlockItem(SILICON_ORE, new Item.Settings());
    public static final BlockItem COPPER_BLOCK_ITEM = new BlockItem(COPPER_BLOCK, new Item.Settings());
    public static final BlockItem TIN_BLOCK_ITEM = new BlockItem(TIN_BLOCK, new Item.Settings());
    public static final BlockItem ALUMINUM_BLOCK_ITEM = new BlockItem(ALUMINUM_BLOCK, new Item.Settings());
    public static final BlockItem SILICON_BLOCK_ITEM = new BlockItem(SILICON_BLOCK, new Item.Settings());

    public static ItemGroup BLOCKS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.BLOCKS_GROUP))
            .stacksForDisplay(itemStack -> {
                itemStack.add(new ItemStack(TEST_BLOCK_ITEM));
                itemStack.add(new ItemStack(TIN_DECORATION_BLOCK_ITEM));
                itemStack.add(new ItemStack(TIN_WALL_BLOCK_ITEM));
                itemStack.add(new ItemStack(COPPER_ORE_ITEM));
                itemStack.add(new ItemStack(TIN_ORE_ITEM));
                itemStack.add(new ItemStack(SILICON_ORE_ITEM));
                itemStack.add(new ItemStack(COPPER_BLOCK_ITEM));
                itemStack.add(new ItemStack(TIN_BLOCK_ITEM));
                itemStack.add(new ItemStack(ALUMINUM_BLOCK_ITEM));
                itemStack.add(new ItemStack(SILICON_BLOCK_ITEM));
                // add blocks to creative menu
            })
            .build();


    public static void init() {
        // Register Blocks
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, "test_block"), TEST_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, "tin_decoration_block"), TIN_DECORATION_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, "tin_wall_block"), TIN_WALL_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, "copper_ore"), COPPER_ORE);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, "tin_ore"), TIN_ORE);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, "aluminum_ore"), ALUMINUM_ORE);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, "silicon_ore"), SILICON_ORE);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, "copper_block"), COPPER_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, "tin_block"), TIN_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, "aluminum_block"), ALUMINUM_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, "silicon_block"), SILICON_BLOCK);

        // Register Block items
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, "test_block"), TEST_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, "tin_decoration_block"), TIN_DECORATION_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, "tin_wall_block"), TIN_WALL_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, "copper_ore"), COPPER_ORE_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, "tin_ore"), TIN_ORE_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, "aluminum_ore"), ALUMINUM_ORE_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, "silicon_ore"), SILICON_ORE_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, "copper_block"), COPPER_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, "tin_block"), TIN_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, "aluminum_block"), ALUMINUM_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, "silicon_block"), SILICON_BLOCK_ITEM);
    }

}
