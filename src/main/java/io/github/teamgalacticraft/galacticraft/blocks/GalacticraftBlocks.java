package io.github.teamgalacticraft.galacticraft.blocks;

import io.github.teamgalacticraft.galacticraft.Constants;
import io.netty.util.Constant;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
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
    public static final Block MOON_TURF_BLOCK = new Block(FabricBlockSettings.of(Material.ORGANIC).build());
    public static final Block MOON_ROCK_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).build());
    public static final Block MOON_DIRT_BLOCK = new Block(FabricBlockSettings.of(Material.EARTH).build());
    public static final Block MOON_DUNGEON_BRICK_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).build());
    public static final Block MARS_SURFACE_ROCK_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).build());
    public static final Block MARS_SUB_SURFACE_ROCK_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).build());
    public static final Block MARS_STONE_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).build());
    public static final Block MARS_COBBLESTONE_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).build());
    public static final Block MARS_DUNGEON_BRICK_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).build());
    public static final Block DENSE_ICE_BLOCK = new Block(FabricBlockSettings.of(Material.PACKED_ICE).build());
    public static final Block ASTEROID_ROCK_BLOCK = new AsteroidRockBlock(FabricBlockSettings.of(Material.STONE).build());
    public static final Block VENUS_ROCK_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).build());
    public static final Block VOLCANIC_ROCK_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).build());
    public static final Block SCORCHED_ROCK_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).build());
    public static final Block PUMICE_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).build());
    public static final Block VAPOR_SPOUT_BLOCK = new Block(FabricBlockSettings.of(Material.STONE).build());

    //What used to be block_basic_core
    public static final Block TIN_DECORATION_BLOCK = new OreBlock(Block.Settings.of(Material.STONE).strength(2.0F, 2.0F));
    public static final Block TIN_WALL_BLOCK = new OreBlock(Block.Settings.of(Material.STONE).strength(2.0F, 2.0F));
    public static final Block COPPER_ORE_BLOCK = new OreBlock(Block.Settings.of(Material.STONE).strength(5.0F, 3.0F));
    public static final Block TIN_ORE_BLOCK = new OreBlock(Block.Settings.of(Material.STONE).strength(5.0F, 3.0F));
    public static final Block ALUMINUM_ORE_BLOCK = new OreBlock(Block.Settings.of(Material.STONE).strength(5.0F, 3.0F));
    public static final Block SILICON_ORE_BLOCK = new SiliconOreBlock(Block.Settings.of(Material.STONE).strength(5.0F, 3.0F));
    public static final Block COPPER_BLOCK = new  Block(Block.Settings.of(Material.METAL).strength(5.0F, 6.0F));
    public static final Block TIN_BLOCK = new  Block(Block.Settings.of(Material.METAL).strength(5.0F, 6.0F));
    public static final Block ALUMINUM_BLOCK = new  Block(Block.Settings.of(Material.METAL).strength(5.0F, 6.0F));
    public static final Block SILICON_BLOCK = new  Block(Block.Settings.of(Material.METAL).strength(5.0F, 6.0F));


    // Block Items
    public static final BlockItem MOON_TURF_BLOCK_ITEM = new BlockItem(MOON_TURF_BLOCK, new Item.Settings());
    public static final BlockItem MOON_ROCK_BLOCK_ITEM = new BlockItem(MOON_ROCK_BLOCK, new Item.Settings());
    public static final BlockItem MOON_DIRT_BLOCK_ITEM = new BlockItem(MOON_DIRT_BLOCK, new Item.Settings());
    public static final BlockItem MOON_DUNGEON_BRICK_BLOCK_ITEM = new BlockItem(MOON_DUNGEON_BRICK_BLOCK, new Item.Settings());
    public static final BlockItem MARS_SURFACE_ROCK_BLOCK_ITEM = new BlockItem(MARS_SURFACE_ROCK_BLOCK, new Item.Settings());
    public static final BlockItem MARS_SUB_SURFACE_ROCK_BLOCK_ITEM = new BlockItem(MARS_SUB_SURFACE_ROCK_BLOCK, new Item.Settings());
    public static final BlockItem MARS_STONE_BLOCK_ITEM = new BlockItem(MARS_STONE_BLOCK, new Item.Settings());
    public static final BlockItem MARS_COBBLESTONE_BLOCK_ITEM = new BlockItem(MARS_COBBLESTONE_BLOCK, new Item.Settings());
    public static final BlockItem MARS_DUNGEON_BRICK_BLOCK_ITEM = new BlockItem(MARS_DUNGEON_BRICK_BLOCK, new Item.Settings());
    public static final BlockItem DENSE_ICE_BLOCK_ITEM = new BlockItem(DENSE_ICE_BLOCK, new Item.Settings());
    public static final BlockItem ASTEROID_ROCK_BLOCK_ITEM = new BlockItem(ASTEROID_ROCK_BLOCK, new Item.Settings());
    public static final BlockItem VENUS_ROCK_BLOCK_ITEM = new BlockItem(VENUS_ROCK_BLOCK, new Item.Settings());
    public static final BlockItem VOLCANIC_ROCK_BLOCK_ITEM = new BlockItem(VOLCANIC_ROCK_BLOCK, new Item.Settings());
    public static final BlockItem SCORCHED_ROCK_BLOCK_ITEM = new BlockItem(SCORCHED_ROCK_BLOCK, new Item.Settings());
    public static final BlockItem PUMICE_BLOCK_ITEM = new BlockItem(PUMICE_BLOCK, new Item.Settings());
    public static final BlockItem VAPOR_SPOUT_BLOCK_ITEM = new BlockItem(VAPOR_SPOUT_BLOCK, new Item.Settings());


    public static final BlockItem TIN_DECORATION_BLOCK_ITEM = new BlockItem(TIN_DECORATION_BLOCK, new Item.Settings());
    public static final BlockItem TIN_WALL_BLOCK_ITEM = new BlockItem(TIN_WALL_BLOCK, new Item.Settings());
    public static final BlockItem COPPER_ORE_BLOCK_ITEM = new BlockItem(COPPER_ORE_BLOCK, new Item.Settings());
    public static final BlockItem TIN_ORE_BLOCK_ITEM = new BlockItem(TIN_ORE_BLOCK, new Item.Settings());
    public static final BlockItem ALUMINUM_ORE_BLOCK_ITEM = new BlockItem(ALUMINUM_ORE_BLOCK, new Item.Settings());
    public static final BlockItem SILICON_ORE_BLOCK_ITEM = new BlockItem(SILICON_ORE_BLOCK, new Item.Settings());
    public static final BlockItem COPPER_BLOCK_ITEM = new BlockItem(COPPER_BLOCK, new Item.Settings());
    public static final BlockItem TIN_BLOCK_ITEM = new BlockItem(TIN_BLOCK, new Item.Settings());
    public static final BlockItem ALUMINUM_BLOCK_ITEM = new BlockItem(ALUMINUM_BLOCK, new Item.Settings());
    public static final BlockItem SILICON_BLOCK_ITEM = new BlockItem(SILICON_BLOCK, new Item.Settings());

    public static ItemGroup BLOCKS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.Blocks.ITEM_GROUP))
            .stacksForDisplay(itemStack -> {
                // add blocks to Creative tab
                itemStack.add(new ItemStack(MOON_TURF_BLOCK_ITEM));
                itemStack.add(new ItemStack(MOON_ROCK_BLOCK_ITEM));
                itemStack.add(new ItemStack(MOON_DIRT_BLOCK_ITEM));
                itemStack.add(new ItemStack(MOON_DUNGEON_BRICK_BLOCK_ITEM));
                itemStack.add(new ItemStack(MARS_SURFACE_ROCK_BLOCK_ITEM));
                itemStack.add(new ItemStack(MARS_SUB_SURFACE_ROCK_BLOCK_ITEM));
                itemStack.add(new ItemStack(MARS_STONE_BLOCK_ITEM));
                itemStack.add(new ItemStack(MARS_COBBLESTONE_BLOCK_ITEM));
                itemStack.add(new ItemStack(MARS_DUNGEON_BRICK_BLOCK_ITEM));
                itemStack.add(new ItemStack(DENSE_ICE_BLOCK_ITEM));
                itemStack.add(new ItemStack(ASTEROID_ROCK_BLOCK_ITEM));
                itemStack.add(new ItemStack(VENUS_ROCK_BLOCK_ITEM));
                itemStack.add(new ItemStack(VOLCANIC_ROCK_BLOCK_ITEM));
                itemStack.add(new ItemStack(SCORCHED_ROCK_BLOCK_ITEM));
                itemStack.add(new ItemStack(PUMICE_BLOCK_ITEM));
                itemStack.add(new ItemStack(VAPOR_SPOUT_BLOCK_ITEM));
                itemStack.add(new ItemStack(TIN_DECORATION_BLOCK_ITEM));
                itemStack.add(new ItemStack(TIN_WALL_BLOCK_ITEM));
                itemStack.add(new ItemStack(COPPER_ORE_BLOCK_ITEM));
                itemStack.add(new ItemStack(TIN_ORE_BLOCK_ITEM));
                itemStack.add(new ItemStack(SILICON_ORE_BLOCK_ITEM));
                itemStack.add(new ItemStack(COPPER_BLOCK_ITEM));
                itemStack.add(new ItemStack(TIN_BLOCK_ITEM));
                itemStack.add(new ItemStack(ALUMINUM_BLOCK_ITEM));
                itemStack.add(new ItemStack(SILICON_BLOCK_ITEM));
                // add blocks to creative menu
            })
            // Set the tab icon
            .icon(MOON_TURF_BLOCK_ITEM::getDefaultStack)
            .build();


    public static void init() {
        // Register Blocks
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.MOON_TURF), MOON_TURF_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.MOON_ROCK), MOON_ROCK_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.MOON_DIRT), MOON_DIRT_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.MOON_DUNGEON_BRICK_BLOCK), MOON_DUNGEON_BRICK_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.MARS_SURFACE_ROCK_BLOCK), MARS_SURFACE_ROCK_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.MARS_SUB_SURFACE_ROCK_BLOCK), MARS_SUB_SURFACE_ROCK_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.MARS_STONE), MARS_STONE_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.MARS_COBBLESTONE), MARS_COBBLESTONE_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.MARS_DUNGEON_BRICK_BLOCK), MARS_DUNGEON_BRICK_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.DENSE_ICE_BLOCK), DENSE_ICE_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.ASTEROID_ROCK_BLOCK), ASTEROID_ROCK_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.VENUS_ROCK_BLOCK), VENUS_ROCK_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.VOLCANIC_ROCK_BLOCK), VOLCANIC_ROCK_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.SCORCHED_ROCK_BLOCK), SCORCHED_ROCK_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.PUMICE_BLOCK), PUMICE_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.VAPOR_SPOUT_BLOCK), VAPOR_SPOUT_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.TIN_DECORATION_BLOCK), TIN_DECORATION_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.TIN_WALL_BLOCK), TIN_WALL_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.COPPER_ORE_BLOCK), COPPER_ORE_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.TIN_ORE_BLOCK), TIN_ORE_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.ALUMINUM_ORE_BLOCK), ALUMINUM_ORE_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.SILICON_ORE_BLOCk), SILICON_ORE_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.COPPER_BLOCK), COPPER_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.TIN_BLOCK), TIN_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.ALUMINUM_BLOCK), ALUMINUM_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Constants.MOD_ID, Constants.Blocks.SILICAON_BLOCK), SILICON_BLOCK);

        // Register Block items
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.TIN_DECORATION_BLOCK), TIN_DECORATION_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.TIN_WALL_BLOCK), TIN_WALL_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.COPPER_ORE_BLOCK), COPPER_ORE_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.TIN_ORE_BLOCK), TIN_ORE_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.ALUMINUM_ORE_BLOCK), ALUMINUM_ORE_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.SILICON_ORE_BLOCk), SILICON_ORE_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.COPPER_BLOCK), COPPER_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.TIN_BLOCK), TIN_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.ALUMINUM_BLOCK), ALUMINUM_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.SILICAON_BLOCK), SILICON_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.MOON_TURF), MOON_TURF_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.MOON_ROCK), MOON_ROCK_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.MOON_DIRT), MOON_DIRT_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.MOON_DUNGEON_BRICK_BLOCK), MOON_DUNGEON_BRICK_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.MARS_SURFACE_ROCK_BLOCK), MARS_SURFACE_ROCK_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.MARS_SUB_SURFACE_ROCK_BLOCK), MARS_SUB_SURFACE_ROCK_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.MARS_STONE), MARS_STONE_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.MARS_COBBLESTONE), MARS_COBBLESTONE_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.MARS_DUNGEON_BRICK_BLOCK), MARS_DUNGEON_BRICK_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.DENSE_ICE_BLOCK), DENSE_ICE_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.ASTEROID_ROCK_BLOCK), ASTEROID_ROCK_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.VENUS_ROCK_BLOCK), VENUS_ROCK_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.VOLCANIC_ROCK_BLOCK), VOLCANIC_ROCK_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.SCORCHED_ROCK_BLOCK), SCORCHED_ROCK_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.PUMICE_BLOCK), PUMICE_BLOCK_ITEM);
        Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.VAPOR_SPOUT_BLOCK), VAPOR_SPOUT_BLOCK_ITEM);
    }
}
