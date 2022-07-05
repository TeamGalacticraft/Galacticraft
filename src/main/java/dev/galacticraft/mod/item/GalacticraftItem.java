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

package dev.galacticraft.mod.item;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.fluid.GalacticraftFluid;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.ItemLike;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@SuppressWarnings("unused")
public class GalacticraftItem {
    public static final List<ItemLike> HIDDEN_ITEMS = new ArrayList<>(1);

    // === START BLOCKS ===
    // TORCHES
    public static final Item GLOWSTONE_TORCH = new StandingAndWallBlockItem(GalacticraftBlock.GLOWSTONE_TORCH, GalacticraftBlock.GLOWSTONE_WALL_TORCH, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item UNLIT_TORCH = new StandingAndWallBlockItem(GalacticraftBlock.UNLIT_TORCH, GalacticraftBlock.UNLIT_WALL_TORCH, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // LANTERNS
    public static final Item GLOWSTONE_LANTERN = new BlockItem(GalacticraftBlock.GLOWSTONE_LANTERN, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // DECORATION BLOCKS
    public static final Item ALUMINUM_DECORATION = new BlockItem(GalacticraftBlock.ALUMINUM_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item ALUMINUM_DECORATION_SLAB = new BlockItem(GalacticraftBlock.ALUMINUM_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item ALUMINUM_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.ALUMINUM_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item ALUMINUM_DECORATION_WALL = new BlockItem(GalacticraftBlock.ALUMINUM_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_ALUMINUM_DECORATION = new BlockItem(GalacticraftBlock.DETAILED_ALUMINUM_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_ALUMINUM_DECORATION_SLAB = new BlockItem(GalacticraftBlock.DETAILED_ALUMINUM_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_ALUMINUM_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.DETAILED_ALUMINUM_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_ALUMINUM_DECORATION_WALL = new BlockItem(GalacticraftBlock.DETAILED_ALUMINUM_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item BRONZE_DECORATION = new BlockItem(GalacticraftBlock.BRONZE_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item BRONZE_DECORATION_SLAB = new BlockItem(GalacticraftBlock.BRONZE_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item BRONZE_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.BRONZE_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item BRONZE_DECORATION_WALL = new BlockItem(GalacticraftBlock.BRONZE_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_BRONZE_DECORATION = new BlockItem(GalacticraftBlock.DETAILED_BRONZE_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_BRONZE_DECORATION_SLAB = new BlockItem(GalacticraftBlock.DETAILED_BRONZE_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_BRONZE_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.DETAILED_BRONZE_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_BRONZE_DECORATION_WALL = new BlockItem(GalacticraftBlock.DETAILED_BRONZE_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item COPPER_DECORATION = new BlockItem(GalacticraftBlock.COPPER_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item COPPER_DECORATION_SLAB = new BlockItem(GalacticraftBlock.COPPER_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item COPPER_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.COPPER_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item COPPER_DECORATION_WALL = new BlockItem(GalacticraftBlock.COPPER_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_COPPER_DECORATION = new BlockItem(GalacticraftBlock.DETAILED_COPPER_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_COPPER_DECORATION_SLAB = new BlockItem(GalacticraftBlock.DETAILED_COPPER_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_COPPER_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.DETAILED_COPPER_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_COPPER_DECORATION_WALL = new BlockItem(GalacticraftBlock.DETAILED_COPPER_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item IRON_DECORATION = new BlockItem(GalacticraftBlock.IRON_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item IRON_DECORATION_SLAB = new BlockItem(GalacticraftBlock.IRON_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item IRON_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.IRON_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item IRON_DECORATION_WALL = new BlockItem(GalacticraftBlock.IRON_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_IRON_DECORATION = new BlockItem(GalacticraftBlock.DETAILED_IRON_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_IRON_DECORATION_SLAB = new BlockItem(GalacticraftBlock.DETAILED_IRON_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_IRON_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.DETAILED_IRON_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_IRON_DECORATION_WALL = new BlockItem(GalacticraftBlock.DETAILED_IRON_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item METEORIC_IRON_DECORATION = new BlockItem(GalacticraftBlock.METEORIC_IRON_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item METEORIC_IRON_DECORATION_SLAB = new BlockItem(GalacticraftBlock.METEORIC_IRON_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item METEORIC_IRON_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.METEORIC_IRON_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item METEORIC_IRON_DECORATION_WALL = new BlockItem(GalacticraftBlock.METEORIC_IRON_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_METEORIC_IRON_DECORATION = new BlockItem(GalacticraftBlock.DETAILED_METEORIC_IRON_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_METEORIC_IRON_DECORATION_SLAB = new BlockItem(GalacticraftBlock.DETAILED_METEORIC_IRON_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_METEORIC_IRON_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.DETAILED_METEORIC_IRON_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_METEORIC_IRON_DECORATION_WALL = new BlockItem(GalacticraftBlock.DETAILED_METEORIC_IRON_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item STEEL_DECORATION = new BlockItem(GalacticraftBlock.STEEL_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item STEEL_DECORATION_SLAB = new BlockItem(GalacticraftBlock.STEEL_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item STEEL_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.STEEL_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item STEEL_DECORATION_WALL = new BlockItem(GalacticraftBlock.STEEL_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_STEEL_DECORATION = new BlockItem(GalacticraftBlock.DETAILED_STEEL_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_STEEL_DECORATION_SLAB = new BlockItem(GalacticraftBlock.DETAILED_STEEL_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_STEEL_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.DETAILED_STEEL_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_STEEL_DECORATION_WALL = new BlockItem(GalacticraftBlock.DETAILED_STEEL_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item TIN_DECORATION = new BlockItem(GalacticraftBlock.TIN_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item TIN_DECORATION_SLAB = new BlockItem(GalacticraftBlock.TIN_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item TIN_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.TIN_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item TIN_DECORATION_WALL = new BlockItem(GalacticraftBlock.TIN_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_TIN_DECORATION = new BlockItem(GalacticraftBlock.DETAILED_TIN_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_TIN_DECORATION_SLAB = new BlockItem(GalacticraftBlock.DETAILED_TIN_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_TIN_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.DETAILED_TIN_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_TIN_DECORATION_WALL = new BlockItem(GalacticraftBlock.DETAILED_TIN_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item TITANIUM_DECORATION = new BlockItem(GalacticraftBlock.TITANIUM_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item TITANIUM_DECORATION_SLAB = new BlockItem(GalacticraftBlock.TITANIUM_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item TITANIUM_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.TITANIUM_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item TITANIUM_DECORATION_WALL = new BlockItem(GalacticraftBlock.TITANIUM_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_TITANIUM_DECORATION = new BlockItem(GalacticraftBlock.DETAILED_TITANIUM_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_TITANIUM_DECORATION_SLAB = new BlockItem(GalacticraftBlock.DETAILED_TITANIUM_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_TITANIUM_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.DETAILED_TITANIUM_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_TITANIUM_DECORATION_WALL = new BlockItem(GalacticraftBlock.DETAILED_TITANIUM_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item DARK_DECORATION = new BlockItem(GalacticraftBlock.DARK_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DARK_DECORATION_SLAB = new BlockItem(GalacticraftBlock.DARK_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DARK_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.DARK_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DARK_DECORATION_WALL = new BlockItem(GalacticraftBlock.DARK_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_DARK_DECORATION = new BlockItem(GalacticraftBlock.DETAILED_DARK_DECORATION, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_DARK_DECORATION_SLAB = new BlockItem(GalacticraftBlock.DETAILED_DARK_DECORATION_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_DARK_DECORATION_STAIRS = new BlockItem(GalacticraftBlock.DETAILED_DARK_DECORATION_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DETAILED_DARK_DECORATION_WALL = new BlockItem(GalacticraftBlock.DETAILED_DARK_DECORATION_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // MOON NATURAL
    public static final Item MOON_TURF = new BlockItem(GalacticraftBlock.MOON_TURF, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MOON_DIRT = new BlockItem(GalacticraftBlock.MOON_DIRT, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MOON_DIRT_PATH = new BlockItem(GalacticraftBlock.MOON_DIRT_PATH, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MOON_SURFACE_ROCK = new BlockItem(GalacticraftBlock.MOON_SURFACE_ROCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item MOON_ROCK = new BlockItem(GalacticraftBlock.MOON_ROCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MOON_ROCK_SLAB = new BlockItem(GalacticraftBlock.MOON_ROCK_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MOON_ROCK_STAIRS = new BlockItem(GalacticraftBlock.MOON_ROCK_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MOON_ROCK_WALL = new BlockItem(GalacticraftBlock.MOON_ROCK_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item COBBLED_MOON_ROCK = new BlockItem(GalacticraftBlock.COBBLED_MOON_ROCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item COBBLED_MOON_ROCK_SLAB = new BlockItem(GalacticraftBlock.COBBLED_MOON_ROCK_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item COBBLED_MOON_ROCK_STAIRS = new BlockItem(GalacticraftBlock.COBBLED_MOON_ROCK_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item COBBLED_MOON_ROCK_WALL = new BlockItem(GalacticraftBlock.COBBLED_MOON_ROCK_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item LUNASLATE = new BlockItem(GalacticraftBlock.LUNASLATE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item LUNASLATE_SLAB = new BlockItem(GalacticraftBlock.LUNASLATE_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item LUNASLATE_STAIRS = new BlockItem(GalacticraftBlock.LUNASLATE_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item LUNASLATE_WALL = new BlockItem(GalacticraftBlock.LUNASLATE_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item COBBLED_LUNASLATE = new BlockItem(GalacticraftBlock.COBBLED_LUNASLATE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item COBBLED_LUNASLATE_SLAB = new BlockItem(GalacticraftBlock.COBBLED_LUNASLATE_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item COBBLED_LUNASLATE_STAIRS = new BlockItem(GalacticraftBlock.COBBLED_LUNASLATE_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item COBBLED_LUNASLATE_WALL = new BlockItem(GalacticraftBlock.COBBLED_LUNASLATE_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item MOON_BASALT = new BlockItem(GalacticraftBlock.MOON_BASALT, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MOON_BASALT_SLAB = new BlockItem(GalacticraftBlock.MOON_BASALT_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MOON_BASALT_STAIRS = new BlockItem(GalacticraftBlock.MOON_BASALT_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MOON_BASALT_WALL = new BlockItem(GalacticraftBlock.MOON_BASALT_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item MOON_BASALT_BRICK = new BlockItem(GalacticraftBlock.MOON_BASALT_BRICK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MOON_BASALT_BRICK_SLAB = new BlockItem(GalacticraftBlock.MOON_BASALT_BRICK_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MOON_BASALT_BRICK_STAIRS = new BlockItem(GalacticraftBlock.MOON_BASALT_BRICK_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MOON_BASALT_BRICK_WALL = new BlockItem(GalacticraftBlock.MOON_BASALT_BRICK_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item CRACKED_MOON_BASALT_BRICK = new BlockItem(GalacticraftBlock.CRACKED_MOON_BASALT_BRICK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item CRACKED_MOON_BASALT_BRICK_SLAB = new BlockItem(GalacticraftBlock.CRACKED_MOON_BASALT_BRICK_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item CRACKED_MOON_BASALT_BRICK_STAIRS = new BlockItem(GalacticraftBlock.CRACKED_MOON_BASALT_BRICK_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item CRACKED_MOON_BASALT_BRICK_WALL = new BlockItem(GalacticraftBlock.CRACKED_MOON_BASALT_BRICK_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // MARS NATURAL
    public static final Item MARS_SURFACE_ROCK = new BlockItem(GalacticraftBlock.MARS_SURFACE_ROCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MARS_SUB_SURFACE_ROCK = new BlockItem(GalacticraftBlock.MARS_SUB_SURFACE_ROCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MARS_STONE = new BlockItem(GalacticraftBlock.MARS_STONE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MARS_COBBLESTONE = new BlockItem(GalacticraftBlock.MARS_COBBLESTONE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MARS_COBBLESTONE_SLAB = new BlockItem(GalacticraftBlock.MARS_COBBLESTONE_SLAB, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MARS_COBBLESTONE_STAIRS = new BlockItem(GalacticraftBlock.MARS_COBBLESTONE_STAIRS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MARS_COBBLESTONE_WALL = new BlockItem(GalacticraftBlock.MARS_COBBLESTONE_WALL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // ASTEROID NATURAL
    public static final Item ASTEROID_ROCK = new BlockItem(GalacticraftBlock.ASTEROID_ROCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item ASTEROID_ROCK_1 = new BlockItem(GalacticraftBlock.ASTEROID_ROCK_1, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item ASTEROID_ROCK_2 = new BlockItem(GalacticraftBlock.ASTEROID_ROCK_2, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // VENUS NATURAL
    public static final Item SOFT_VENUS_ROCK = new BlockItem(GalacticraftBlock.SOFT_VENUS_ROCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item HARD_VENUS_ROCK = new BlockItem(GalacticraftBlock.HARD_VENUS_ROCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item SCORCHED_VENUS_ROCK = new BlockItem(GalacticraftBlock.SCORCHED_VENUS_ROCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item VOLCANIC_ROCK = new BlockItem(GalacticraftBlock.VOLCANIC_ROCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item PUMICE = new BlockItem(GalacticraftBlock.PUMICE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item VAPOR_SPOUT = new BlockItem(GalacticraftBlock.VAPOR_SPOUT, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // MISC DECOR
    public static final Item WALKWAY = new BlockItem(GalacticraftBlock.WALKWAY, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item PIPE_WALKWAY = new BlockItem(GalacticraftBlock.PIPE_WALKWAY, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item WIRE_WALKWAY = new BlockItem(GalacticraftBlock.WIRE_WALKWAY, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item TIN_LADDER = new BlockItem(GalacticraftBlock.TIN_LADDER, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item GRATING = new BlockItem(GalacticraftBlock.GRATING, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // SPECIAL
    public static final Item ALUMINUM_WIRE = new BlockItem(GalacticraftBlock.ALUMINUM_WIRE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item SEALABLE_ALUMINUM_WIRE = new BlockItem(GalacticraftBlock.SEALABLE_ALUMINUM_WIRE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item HEAVY_SEALABLE_ALUMINUM_WIRE = new BlockItem(GalacticraftBlock.HEAVY_SEALABLE_ALUMINUM_WIRE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item GLASS_FLUID_PIPE = new BlockItem(GalacticraftBlock.GLASS_FLUID_PIPE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // LIGHT PANELS
    public static final Item SQUARE_LIGHT_PANEL = new BlockItem(GalacticraftBlock.SQUARE_LIGHT_PANEL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item SPOTLIGHT_LIGHT_PANEL = new BlockItem(GalacticraftBlock.SPOTLIGHT_LIGHT_PANEL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item LINEAR_LIGHT_PANEL = new BlockItem(GalacticraftBlock.LINEAR_LIGHT_PANEL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DASHED_LIGHT_PANEL = new BlockItem(GalacticraftBlock.DASHED_LIGHT_PANEL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DIAGONAL_LIGHT_PANEL = new BlockItem(GalacticraftBlock.DIAGONAL_LIGHT_PANEL, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // VACUUM GLASS
    public static final Item VACUUM_GLASS = new BlockItem(GalacticraftBlock.VACUUM_GLASS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item CLEAR_VACUUM_GLASS = new BlockItem(GalacticraftBlock.CLEAR_VACUUM_GLASS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item STRONG_VACUUM_GLASS = new BlockItem(GalacticraftBlock.STRONG_VACUUM_GLASS, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // ORES
    public static final Item SILICON_ORE = new BlockItem(GalacticraftBlock.SILICON_ORE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DEEPSLATE_SILICON_ORE = new BlockItem(GalacticraftBlock.DEEPSLATE_SILICON_ORE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item MOON_COPPER_ORE = new BlockItem(GalacticraftBlock.MOON_COPPER_ORE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item LUNASLATE_COPPER_ORE = new BlockItem(GalacticraftBlock.LUNASLATE_COPPER_ORE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item TIN_ORE = new BlockItem(GalacticraftBlock.TIN_ORE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DEEPSLATE_TIN_ORE = new BlockItem(GalacticraftBlock.DEEPSLATE_TIN_ORE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MOON_TIN_ORE = new BlockItem(GalacticraftBlock.MOON_TIN_ORE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item LUNASLATE_TIN_ORE = new BlockItem(GalacticraftBlock.LUNASLATE_TIN_ORE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item ALUMINUM_ORE = new BlockItem(GalacticraftBlock.ALUMINUM_ORE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DEEPSLATE_ALUMINUM_ORE = new BlockItem(GalacticraftBlock.DEEPSLATE_ALUMINUM_ORE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item DESH_ORE = new BlockItem(GalacticraftBlock.DESH_ORE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item ILMENITE_ORE = new BlockItem(GalacticraftBlock.ILMENITE_ORE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    public static final Item GALENA_ORE = new BlockItem(GalacticraftBlock.GALENA_ORE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // COMPACT MINERAL BLOCKS
    public static final Item MOON_CHEESE_BLOCK = new BlockItem(GalacticraftBlock.MOON_CHEESE_BLOCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item SILICON_BLOCK = new BlockItem(GalacticraftBlock.SILICON_BLOCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item METEORIC_IRON_BLOCK = new BlockItem(GalacticraftBlock.METEORIC_IRON_BLOCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item DESH_BLOCK = new BlockItem(GalacticraftBlock.DESH_BLOCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item TITANIUM_BLOCK = new BlockItem(GalacticraftBlock.TITANIUM_BLOCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item LEAD_BLOCK = new BlockItem(GalacticraftBlock.LEAD_BLOCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item LUNAR_SAPPHIRE_BLOCK = new BlockItem(GalacticraftBlock.LUNAR_SAPPHIRE_BLOCK, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // MOON VILLAGER SPECIAL
    public static final Item LUNAR_CARTOGRAPHY_TABLE = new BlockItem(GalacticraftBlock.LUNAR_CARTOGRAPHY_TABLE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // MISC WORLD GEN
    public static final Item CAVERNOUS_VINE = new BlockItem(GalacticraftBlock.CAVERNOUS_VINE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item POISONOUS_CAVERNOUS_VINE = new BlockItem(GalacticraftBlock.POISONOUS_CAVERNOUS_VINE, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));
    public static final Item MOON_BERRY_BUSH = new BlockItem(GalacticraftBlock.MOON_BERRY_BUSH, new Item.Properties().tab(GalacticraftItemGroup.BLOCKS_GROUP));

    // MACHINES
    public static final Item CIRCUIT_FABRICATOR = new BlockItem(GalacticraftBlock.CIRCUIT_FABRICATOR, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item COMPRESSOR = new BlockItem(GalacticraftBlock.COMPRESSOR, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item ELECTRIC_COMPRESSOR = new BlockItem(GalacticraftBlock.ELECTRIC_COMPRESSOR, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item COAL_GENERATOR = new BlockItem(GalacticraftBlock.COAL_GENERATOR, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item BASIC_SOLAR_PANEL = new BlockItem(GalacticraftBlock.BASIC_SOLAR_PANEL, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item ADVANCED_SOLAR_PANEL = new BlockItem(GalacticraftBlock.ADVANCED_SOLAR_PANEL, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item ENERGY_STORAGE_MODULE = new BlockItem(GalacticraftBlock.ENERGY_STORAGE_MODULE, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item ELECTRIC_FURNACE = new BlockItem(GalacticraftBlock.ELECTRIC_FURNACE, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item ELECTRIC_ARC_FURNACE = new BlockItem(GalacticraftBlock.ELECTRIC_ARC_FURNACE, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item REFINERY = new BlockItem(GalacticraftBlock.REFINERY, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item OXYGEN_COLLECTOR = new BlockItem(GalacticraftBlock.OXYGEN_COLLECTOR, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item OXYGEN_SEALER = new BlockItem(GalacticraftBlock.OXYGEN_SEALER, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item OXYGEN_BUBBLE_DISTRIBUTOR = new BlockItem(GalacticraftBlock.BUBBLE_DISTRIBUTOR, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item OXYGEN_DECOMPRESSOR = new BlockItem(GalacticraftBlock.OXYGEN_DECOMPRESSOR, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item OXYGEN_COMPRESSOR = new BlockItem(GalacticraftBlock.OXYGEN_COMPRESSOR, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    public static final Item OXYGEN_STORAGE_MODULE = new BlockItem(GalacticraftBlock.OXYGEN_STORAGE_MODULE, new Item.Properties().tab(GalacticraftItemGroup.MACHINES_GROUP));
    // === END BLOCKS ===
    
    // MATERIALS
    public static final Item RAW_SILICON = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    
    public static final Item RAW_METEORIC_IRON = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item METEORIC_IRON_INGOT = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item METEORIC_IRON_NUGGET = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item COMPRESSED_METEORIC_IRON = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    public static final Item RAW_DESH = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item DESH_INGOT = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item DESH_NUGGET = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item COMPRESSED_DESH = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    public static final Item RAW_LEAD = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item LEAD_INGOT = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item LEAD_NUGGET = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    
    public static final Item RAW_ALUMINUM = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item ALUMINUM_INGOT = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item ALUMINUM_NUGGET = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item COMPRESSED_ALUMINUM = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    public static final Item RAW_TIN = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item TIN_INGOT = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item TIN_NUGGET = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item COMPRESSED_TIN = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    public static final Item RAW_TITANIUM = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item TITANIUM_INGOT = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item TITANIUM_NUGGET = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item COMPRESSED_TITANIUM = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    public static final Item COMPRESSED_BRONZE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item COMPRESSED_COPPER = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item COMPRESSED_IRON = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item COMPRESSED_STEEL = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    
    public static final Item LUNAR_SAPPHIRE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item DESH_STICK = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item CARBON_FRAGMENTS = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item IRON_SHARD = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item SOLAR_DUST = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item BASIC_WAFER = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item ADVANCED_WAFER = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item BEAM_CORE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item CANVAS = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    
    public static final Item FLUID_MANIPULATOR = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item OXYGEN_CONCENTRATOR = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item OXYGEN_FAN = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item OXYGEN_VENT = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item SENSOR_LENS = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item BLUE_SOLAR_WAFER = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item SINGLE_SOLAR_MODULE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item FULL_SOLAR_PANEL = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item SOLAR_ARRAY_WAFER = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item STEEL_POLE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item COPPER_CANISTER = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item TIN_CANISTER = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item THERMAL_CLOTH = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item ISOTHERMAL_FABRIC = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item ORION_DRIVE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item ATMOSPHERIC_VALVE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item AMBIENT_THERMAL_CONTROLLER = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    
    // FOOD
    public static final Item MOON_BERRIES = new Item(new Item.Properties().food(GalacticraftFoodComponent.MOON_BERRIES).tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item CHEESE_CURD = new Item(new Item.Properties().food(GalacticraftFoodComponent.CHEESE_CURD).tab(GalacticraftItemGroup.ITEMS_GROUP));
    
    public static final Item CHEESE_SLICE = new Item(new Item.Properties().food(GalacticraftFoodComponent.CHEESE_SLICE).tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item BURGER_BUN = new Item(new Item.Properties().food(GalacticraftFoodComponent.BURGER_BUN).tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item GROUND_BEEF = new Item(new Item.Properties().food(GalacticraftFoodComponent.GROUND_BEEF).tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item BEEF_PATTY = new Item(new Item.Properties().food(GalacticraftFoodComponent.BEEF_PATTY).tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item CHEESEBURGER = new Item(new Item.Properties().food(GalacticraftFoodComponent.CHEESEBURGER).tab(GalacticraftItemGroup.ITEMS_GROUP));
    
    public static final Item CANNED_DEHYDRATED_APPLE = new CannedFoodItem(new Item.Properties().food(GalacticraftFoodComponent.DEHYDRATED_APPLE).tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item CANNED_DEHYDRATED_CARROT = new CannedFoodItem(new Item.Properties().food(GalacticraftFoodComponent.DEHYDRATED_CARROT).tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item CANNED_DEHYDRATED_MELON = new CannedFoodItem(new Item.Properties().food(GalacticraftFoodComponent.DEHYDRATED_MELON).tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item CANNED_DEHYDRATED_POTATO = new CannedFoodItem(new Item.Properties().food(GalacticraftFoodComponent.DEHYDRATED_POTATO).tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item CANNED_BEEF = new CannedFoodItem(new Item.Properties().food(GalacticraftFoodComponent.CANNED_BEEF).tab(GalacticraftItemGroup.ITEMS_GROUP));
    
    // ROCKET PLATES
    public static final Item TIER_1_HEAVY_DUTY_PLATE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item TIER_2_HEAVY_DUTY_PLATE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item TIER_3_HEAVY_DUTY_PLATE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    // ARMOR
    public static final Item HEAVY_DUTY_HELMET = new ArmorItem(GalacticraftArmorMaterial.HEAVY_DUTY, EquipmentSlot.HEAD, (new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_CHESTPLATE = new ArmorItem(GalacticraftArmorMaterial.HEAVY_DUTY, EquipmentSlot.CHEST, (new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_LEGGINGS = new ArmorItem(GalacticraftArmorMaterial.HEAVY_DUTY, EquipmentSlot.LEGS, (new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_BOOTS = new ArmorItem(GalacticraftArmorMaterial.HEAVY_DUTY, EquipmentSlot.FEET, (new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP)));

    public static final Item DESH_HELMET = new ArmorItem(GalacticraftArmorMaterial.DESH, EquipmentSlot.HEAD, (new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP)));
    public static final Item DESH_CHESTPLATE = new ArmorItem(GalacticraftArmorMaterial.DESH, EquipmentSlot.CHEST, (new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP)));
    public static final Item DESH_LEGGINGS = new ArmorItem(GalacticraftArmorMaterial.DESH, EquipmentSlot.LEGS, (new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP)));
    public static final Item DESH_BOOTS = new ArmorItem(GalacticraftArmorMaterial.DESH, EquipmentSlot.FEET, (new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP)));

    public static final Item TITANIUM_HELMET = new ArmorItem(GalacticraftArmorMaterial.TITANIUM, EquipmentSlot.HEAD, (new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP)));
    public static final Item TITANIUM_CHESTPLATE = new ArmorItem(GalacticraftArmorMaterial.TITANIUM, EquipmentSlot.CHEST, (new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP)));
    public static final Item TITANIUM_LEGGINGS = new ArmorItem(GalacticraftArmorMaterial.TITANIUM, EquipmentSlot.LEGS, (new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP)));
    public static final Item TITANIUM_BOOTS = new ArmorItem(GalacticraftArmorMaterial.TITANIUM, EquipmentSlot.FEET, (new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP)));

    public static final Item SENSOR_GLASSES = new ArmorItem(GalacticraftArmorMaterial.SENSOR_GLASSES, EquipmentSlot.HEAD, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    // TOOLS + WEAPONS
    public static final Item HEAVY_DUTY_SWORD = new BrittleSwordItem(GalacticraftToolMaterial.STEEL, 3, -2.4F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item HEAVY_DUTY_SHOVEL = new ShovelItem(GalacticraftToolMaterial.STEEL, -1.5F, -3.0F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item HEAVY_DUTY_PICKAXE = new PickaxeItem(GalacticraftToolMaterial.STEEL, 1, -2.8F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item HEAVY_DUTY_AXE = new AxeItem(GalacticraftToolMaterial.STEEL, 6.0F, -3.1F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item HEAVY_DUTY_HOE = new HoeItem(GalacticraftToolMaterial.STEEL, -2, -1.0F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    public static final Item DESH_SWORD = new SwordItem(GalacticraftToolMaterial.DESH, 3, -2.4F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item DESH_SHOVEL = new ShovelItem(GalacticraftToolMaterial.DESH, -1.5F, -3.0F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item DESH_PICKAXE = new PickaxeItem(GalacticraftToolMaterial.DESH, 1, -2.8F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item DESH_AXE = new AxeItem(GalacticraftToolMaterial.DESH, 6.0F, -3.1F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item DESH_HOE = new HoeItem(GalacticraftToolMaterial.DESH, -3, -1.0F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    public static final Item TITANIUM_SWORD = new BrittleSwordItem(GalacticraftToolMaterial.TITANIUM, 3, -2.4F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item TITANIUM_SHOVEL = new ShovelItem(GalacticraftToolMaterial.TITANIUM, -1.5F, -3.0F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item TITANIUM_PICKAXE = new PickaxeItem(GalacticraftToolMaterial.TITANIUM, 1, -2.8F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item TITANIUM_AXE = new AxeItem(GalacticraftToolMaterial.TITANIUM, 6.0F, -3.1F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item TITANIUM_HOE = new HoeItem(GalacticraftToolMaterial.TITANIUM, -3, -1.0F, new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    public static final Item STANDARD_WRENCH = new StandardWrenchItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    // BATTERIES
    public static final Item BATTERY = new BatteryItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP), 15000, 500);
    public static final Item INFINITE_BATTERY = new InfiniteBatteryItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).rarity(Rarity.EPIC));

    //FLUID BUCKETS
    public static final Item CRUDE_OIL_BUCKET = new BucketItem(GalacticraftFluid.CRUDE_OIL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item FUEL_BUCKET = new BucketItem(GalacticraftFluid.FUEL, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(GalacticraftItemGroup.ITEMS_GROUP));

    //GALACTICRAFT INVENTORY
    public static final Item PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item ORANGE_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item MAGENTA_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item LIGHT_BLUE_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item YELLOW_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item LIME_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item PINK_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item GRAY_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item LIGHT_GRAY_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item CYAN_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item PURPLE_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item BLUE_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item BROWN_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item GREEN_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item RED_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));
    public static final Item BLACK_PARACHUTE = new Item(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP).stacksTo(1));

    public static final Item OXYGEN_MASK = new OxygenMaskItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item OXYGEN_GEAR = new OxygenGearItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    public static final Item SMALL_OXYGEN_TANK = new OxygenTankItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP), 1620 * 10); // 16200 ticks
    public static final Item MEDIUM_OXYGEN_TANK = new OxygenTankItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP), 1620 * 20); //32400 ticks
    public static final Item LARGE_OXYGEN_TANK = new OxygenTankItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP), 1620 * 30); //48600 ticks
    public static final Item INFINITE_OXYGEN_TANK = new InfiniteOxygenTankItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    public static final Item SHIELD_CONTROLLER = new AccessoryItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item FREQUENCY_MODULE = new FrequencyModuleItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    public static final Item THERMAL_PADDING_HELMET = new ThermalArmorItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP), EquipmentSlot.HEAD);
    public static final Item THERMAL_PADDING_CHESTPIECE = new ThermalArmorItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP), EquipmentSlot.CHEST);
    public static final Item THERMAL_PADDING_LEGGINGS = new ThermalArmorItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP), EquipmentSlot.LEGS);
    public static final Item THERMAL_PADDING_BOOTS = new ThermalArmorItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP), EquipmentSlot.FEET);

    // SCHEMATICS
    public static final Item TIER_2_ROCKET_SCHEMATIC = new SchematicItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item CARGO_ROCKET_SCHEMATIC = new SchematicItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item MOON_BUGGY_SCHEMATIC = new SchematicItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item TIER_3_ROCKET_SCHEMATIC = new SchematicItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));
    public static final Item ASTRO_MINER_SCHEMATIC = new SchematicItem(new Item.Properties().tab(GalacticraftItemGroup.ITEMS_GROUP));

    
    public static void register() {
        // === START BLOCKS ===
        // TORCHES
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.GLOWSTONE_TORCH), GLOWSTONE_TORCH);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.UNLIT_TORCH), UNLIT_TORCH);

        // LANTERNS
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.GLOWSTONE_LANTERN), GLOWSTONE_LANTERN);

        // DECORATION BLOCKS
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ALUMINUM_DECORATION), ALUMINUM_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ALUMINUM_DECORATION_SLAB), ALUMINUM_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ALUMINUM_DECORATION_STAIRS), ALUMINUM_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ALUMINUM_DECORATION_WALL), ALUMINUM_DECORATION_WALL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION), DETAILED_ALUMINUM_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION_SLAB), DETAILED_ALUMINUM_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION_STAIRS), DETAILED_ALUMINUM_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_ALUMINUM_DECORATION_WALL), DETAILED_ALUMINUM_DECORATION_WALL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.BRONZE_DECORATION), BRONZE_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.BRONZE_DECORATION_SLAB), BRONZE_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.BRONZE_DECORATION_STAIRS), BRONZE_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.BRONZE_DECORATION_WALL), BRONZE_DECORATION_WALL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION), DETAILED_BRONZE_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION_SLAB), DETAILED_BRONZE_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION_STAIRS), DETAILED_BRONZE_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_BRONZE_DECORATION_WALL), DETAILED_BRONZE_DECORATION_WALL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.COPPER_DECORATION), COPPER_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.COPPER_DECORATION_SLAB), COPPER_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.COPPER_DECORATION_STAIRS), COPPER_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.COPPER_DECORATION_WALL), COPPER_DECORATION_WALL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION), DETAILED_COPPER_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION_SLAB), DETAILED_COPPER_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION_STAIRS), DETAILED_COPPER_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_COPPER_DECORATION_WALL), DETAILED_COPPER_DECORATION_WALL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.IRON_DECORATION), IRON_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.IRON_DECORATION_SLAB), IRON_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.IRON_DECORATION_STAIRS), IRON_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.IRON_DECORATION_WALL), IRON_DECORATION_WALL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_IRON_DECORATION), DETAILED_IRON_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_IRON_DECORATION_SLAB), DETAILED_IRON_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_IRON_DECORATION_STAIRS), DETAILED_IRON_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_IRON_DECORATION_WALL), DETAILED_IRON_DECORATION_WALL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.METEORIC_IRON_DECORATION), METEORIC_IRON_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.METEORIC_IRON_DECORATION_SLAB), METEORIC_IRON_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.METEORIC_IRON_DECORATION_STAIRS), METEORIC_IRON_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.METEORIC_IRON_DECORATION_WALL), METEORIC_IRON_DECORATION_WALL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION), DETAILED_METEORIC_IRON_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION_SLAB), DETAILED_METEORIC_IRON_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION_STAIRS), DETAILED_METEORIC_IRON_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_METEORIC_IRON_DECORATION_WALL), DETAILED_METEORIC_IRON_DECORATION_WALL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.STEEL_DECORATION), STEEL_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.STEEL_DECORATION_SLAB), STEEL_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.STEEL_DECORATION_STAIRS), STEEL_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.STEEL_DECORATION_WALL), STEEL_DECORATION_WALL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION), DETAILED_STEEL_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION_SLAB), DETAILED_STEEL_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION_STAIRS), DETAILED_STEEL_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_STEEL_DECORATION_WALL), DETAILED_STEEL_DECORATION_WALL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.TIN_DECORATION), TIN_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.TIN_DECORATION_SLAB), TIN_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.TIN_DECORATION_STAIRS), TIN_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.TIN_DECORATION_WALL), TIN_DECORATION_WALL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_TIN_DECORATION), DETAILED_TIN_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_TIN_DECORATION_SLAB), DETAILED_TIN_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_TIN_DECORATION_STAIRS), DETAILED_TIN_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_TIN_DECORATION_WALL), DETAILED_TIN_DECORATION_WALL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.TITANIUM_DECORATION), TITANIUM_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.TITANIUM_DECORATION_SLAB), TITANIUM_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.TITANIUM_DECORATION_STAIRS), TITANIUM_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.TITANIUM_DECORATION_WALL), TITANIUM_DECORATION_WALL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION), DETAILED_TITANIUM_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION_SLAB), DETAILED_TITANIUM_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION_STAIRS), DETAILED_TITANIUM_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_TITANIUM_DECORATION_WALL), DETAILED_TITANIUM_DECORATION_WALL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DARK_DECORATION), DARK_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DARK_DECORATION_SLAB), DARK_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DARK_DECORATION_STAIRS), DARK_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DARK_DECORATION_WALL), DARK_DECORATION_WALL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_DARK_DECORATION), DETAILED_DARK_DECORATION);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_DARK_DECORATION_SLAB), DETAILED_DARK_DECORATION_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_DARK_DECORATION_STAIRS), DETAILED_DARK_DECORATION_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DETAILED_DARK_DECORATION_WALL), DETAILED_DARK_DECORATION_WALL);

        // MOON NATURAL
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_TURF), MOON_TURF);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_DIRT), MOON_DIRT);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_DIRT_PATH), MOON_DIRT_PATH);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_SURFACE_ROCK), MOON_SURFACE_ROCK);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_ROCK), MOON_ROCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_ROCK_SLAB), MOON_ROCK_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_ROCK_STAIRS), MOON_ROCK_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_ROCK_WALL), MOON_ROCK_WALL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.COBBLED_MOON_ROCK), COBBLED_MOON_ROCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.COBBLED_MOON_ROCK_SLAB), COBBLED_MOON_ROCK_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.COBBLED_MOON_ROCK_STAIRS), COBBLED_MOON_ROCK_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.COBBLED_MOON_ROCK_WALL), COBBLED_MOON_ROCK_WALL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.LUNASLATE), LUNASLATE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.LUNASLATE_SLAB), LUNASLATE_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.LUNASLATE_STAIRS), LUNASLATE_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.LUNASLATE_WALL), LUNASLATE_WALL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.COBBLED_LUNASLATE), COBBLED_LUNASLATE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.COBBLED_LUNASLATE_SLAB), COBBLED_LUNASLATE_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.COBBLED_LUNASLATE_STAIRS), COBBLED_LUNASLATE_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.COBBLED_LUNASLATE_WALL), COBBLED_LUNASLATE_WALL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_BASALT), MOON_BASALT);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_BASALT_SLAB), MOON_BASALT_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_BASALT_STAIRS), MOON_BASALT_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_BASALT_WALL), MOON_BASALT_WALL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_BASALT_BRICK), MOON_BASALT_BRICK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_BASALT_BRICK_SLAB), MOON_BASALT_BRICK_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_BASALT_BRICK_STAIRS), MOON_BASALT_BRICK_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_BASALT_BRICK_WALL), MOON_BASALT_BRICK_WALL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK), CRACKED_MOON_BASALT_BRICK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK_SLAB), CRACKED_MOON_BASALT_BRICK_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK_STAIRS), CRACKED_MOON_BASALT_BRICK_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.CRACKED_MOON_BASALT_BRICK_WALL), CRACKED_MOON_BASALT_BRICK_WALL);

        // MARS NATURAL
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MARS_SURFACE_ROCK), MARS_SURFACE_ROCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MARS_SUB_SURFACE_ROCK), MARS_SUB_SURFACE_ROCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MARS_STONE), MARS_STONE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MARS_COBBLESTONE), MARS_COBBLESTONE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MARS_COBBLESTONE_SLAB), MARS_COBBLESTONE_SLAB);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MARS_COBBLESTONE_STAIRS), MARS_COBBLESTONE_STAIRS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MARS_COBBLESTONE_WALL), MARS_COBBLESTONE_WALL);

        // ASTEROID NATURAL
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ASTEROID_ROCK), ASTEROID_ROCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ASTEROID_ROCK_1), ASTEROID_ROCK_1);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ASTEROID_ROCK_2), ASTEROID_ROCK_2);

        // VENUS NATURAL
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.SOFT_VENUS_ROCK), SOFT_VENUS_ROCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.HARD_VENUS_ROCK), HARD_VENUS_ROCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.SCORCHED_VENUS_ROCK), SCORCHED_VENUS_ROCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.VOLCANIC_ROCK), VOLCANIC_ROCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.PUMICE), PUMICE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.VAPOR_SPOUT), VAPOR_SPOUT);

        // MISC DECOR
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.WALKWAY), WALKWAY);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.PIPE_WALKWAY), PIPE_WALKWAY);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.WIRE_WALKWAY), WIRE_WALKWAY);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.TIN_LADDER), TIN_LADDER);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.GRATING), GRATING);

        // SPECIAL
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ALUMINUM_WIRE), ALUMINUM_WIRE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.SEALABLE_ALUMINUM_WIRE), SEALABLE_ALUMINUM_WIRE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.HEAVY_SEALABLE_ALUMINUM_WIRE), HEAVY_SEALABLE_ALUMINUM_WIRE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.GLASS_FLUID_PIPE), GLASS_FLUID_PIPE);

        // LIGHT PANELS
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.SQUARE_LIGHT_PANEL), SQUARE_LIGHT_PANEL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.SPOTLIGHT_LIGHT_PANEL), SPOTLIGHT_LIGHT_PANEL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.LINEAR_LIGHT_PANEL), LINEAR_LIGHT_PANEL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DASHED_LIGHT_PANEL), DASHED_LIGHT_PANEL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DIAGONAL_LIGHT_PANEL), DIAGONAL_LIGHT_PANEL);

        // VACUUM GLASS
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.VACUUM_GLASS), VACUUM_GLASS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.CLEAR_VACUUM_GLASS), CLEAR_VACUUM_GLASS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.STRONG_VACUUM_GLASS), STRONG_VACUUM_GLASS);

        // ORES
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.SILICON_ORE), SILICON_ORE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DEEPSLATE_SILICON_ORE), DEEPSLATE_SILICON_ORE);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_COPPER_ORE), MOON_COPPER_ORE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.LUNASLATE_COPPER_ORE), LUNASLATE_COPPER_ORE);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.TIN_ORE), TIN_ORE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DEEPSLATE_TIN_ORE), DEEPSLATE_TIN_ORE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_TIN_ORE), MOON_TIN_ORE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.LUNASLATE_TIN_ORE), LUNASLATE_TIN_ORE);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ALUMINUM_ORE), ALUMINUM_ORE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DEEPSLATE_ALUMINUM_ORE), DEEPSLATE_ALUMINUM_ORE);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DESH_ORE), DESH_ORE);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ILMENITE_ORE), ILMENITE_ORE);

        Registry.register(Registry.ITEM, Constant.id(Constant.Block.GALENA_ORE), GALENA_ORE);

        // COMPACT MINERAL BLOCKS
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_CHEESE_BLOCK), MOON_CHEESE_BLOCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.SILICON_BLOCK), SILICON_BLOCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.METEORIC_IRON_BLOCK), METEORIC_IRON_BLOCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.DESH_BLOCK), DESH_BLOCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.TITANIUM_BLOCK), TITANIUM_BLOCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.LEAD_BLOCK), LEAD_BLOCK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.LUNAR_SAPPHIRE_BLOCK), LUNAR_SAPPHIRE_BLOCK);

        // MOON VILLAGER SPECIAL
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.LUNAR_CARTOGRAPHY_TABLE), LUNAR_CARTOGRAPHY_TABLE);

        // MISC WORLD GEN
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.CAVERNOUS_VINE), CAVERNOUS_VINE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.POISONOUS_CAVERNOUS_VINE), POISONOUS_CAVERNOUS_VINE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.MOON_BERRY_BUSH), MOON_BERRY_BUSH);

        // MACHINES
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.CIRCUIT_FABRICATOR), CIRCUIT_FABRICATOR);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.COMPRESSOR), COMPRESSOR);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ELECTRIC_COMPRESSOR), ELECTRIC_COMPRESSOR);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.COAL_GENERATOR), COAL_GENERATOR);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.BASIC_SOLAR_PANEL), BASIC_SOLAR_PANEL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ADVANCED_SOLAR_PANEL), ADVANCED_SOLAR_PANEL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ENERGY_STORAGE_MODULE), ENERGY_STORAGE_MODULE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ELECTRIC_FURNACE), ELECTRIC_FURNACE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.ELECTRIC_ARC_FURNACE), ELECTRIC_ARC_FURNACE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.REFINERY), REFINERY);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.OXYGEN_COLLECTOR), OXYGEN_COLLECTOR);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.OXYGEN_SEALER), OXYGEN_SEALER);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.OXYGEN_BUBBLE_DISTRIBUTOR), OXYGEN_BUBBLE_DISTRIBUTOR);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.OXYGEN_DECOMPRESSOR), OXYGEN_DECOMPRESSOR);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.OXYGEN_COMPRESSOR), OXYGEN_COMPRESSOR);
        Registry.register(Registry.ITEM, Constant.id(Constant.Block.OXYGEN_STORAGE_MODULE), OXYGEN_STORAGE_MODULE);
        // === END BLOCKS ===

        // MATERIALS
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.RAW_SILICON), RAW_SILICON);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.RAW_METEORIC_IRON), RAW_METEORIC_IRON);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.METEORIC_IRON_INGOT), METEORIC_IRON_INGOT);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.METEORIC_IRON_NUGGET), METEORIC_IRON_NUGGET);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.COMPRESSED_METEORIC_IRON), COMPRESSED_METEORIC_IRON);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.RAW_DESH), RAW_DESH);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.DESH_INGOT), DESH_INGOT);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.DESH_NUGGET), DESH_NUGGET);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.COMPRESSED_DESH), COMPRESSED_DESH);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.RAW_LEAD), RAW_LEAD);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.LEAD_INGOT), LEAD_INGOT);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.LEAD_NUGGET), LEAD_NUGGET);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.RAW_ALUMINUM), RAW_ALUMINUM);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.ALUMINUM_INGOT), ALUMINUM_INGOT);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.ALUMINUM_NUGGET), ALUMINUM_NUGGET);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.COMPRESSED_ALUMINUM), COMPRESSED_ALUMINUM);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.RAW_TIN), RAW_TIN);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TIN_INGOT), TIN_INGOT);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TIN_NUGGET), TIN_NUGGET);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.COMPRESSED_TIN), COMPRESSED_TIN);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.RAW_TITANIUM), RAW_TITANIUM);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TITANIUM_INGOT), TITANIUM_INGOT);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TITANIUM_NUGGET), TITANIUM_NUGGET);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.COMPRESSED_TITANIUM), COMPRESSED_TITANIUM);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.COMPRESSED_BRONZE), COMPRESSED_BRONZE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.COMPRESSED_COPPER), COMPRESSED_COPPER);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.COMPRESSED_IRON), COMPRESSED_IRON);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.COMPRESSED_STEEL), COMPRESSED_STEEL);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.LUNAR_SAPPHIRE), LUNAR_SAPPHIRE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.DESH_STICK), DESH_STICK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.CARBON_FRAGMENTS), CARBON_FRAGMENTS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.IRON_SHARD), IRON_SHARD);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.SOLAR_DUST), SOLAR_DUST);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.BASIC_WAFER), BASIC_WAFER);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.ADVANCED_WAFER), ADVANCED_WAFER);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.BEAM_CORE), BEAM_CORE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.CANVAS), CANVAS);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.FLUID_MANIPULATOR), FLUID_MANIPULATOR);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.OXYGEN_CONCENTRATOR), OXYGEN_CONCENTRATOR);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.OXYGEN_FAN), OXYGEN_FAN);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.OXYGEN_VENT), OXYGEN_VENT);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.SENSOR_LENS), SENSOR_LENS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.BLUE_SOLAR_WAFER), BLUE_SOLAR_WAFER);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.SINGLE_SOLAR_MODULE), SINGLE_SOLAR_MODULE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.FULL_SOLAR_PANEL), FULL_SOLAR_PANEL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.SOLAR_ARRAY_WAFER), SOLAR_ARRAY_WAFER);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.STEEL_POLE), STEEL_POLE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.COPPER_CANISTER), COPPER_CANISTER);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TIN_CANISTER), TIN_CANISTER);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.THERMAL_CLOTH), THERMAL_CLOTH);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.ISOTHERMAL_FABRIC), ISOTHERMAL_FABRIC);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.ORION_DRIVE), ORION_DRIVE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.ATMOSPHERIC_VALVE), ATMOSPHERIC_VALVE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.AMBIENT_THERMAL_CONTROLLER), AMBIENT_THERMAL_CONTROLLER);

        // FOOD
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.MOON_BERRIES), MOON_BERRIES);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.CHEESE_CURD), CHEESE_CURD);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.CHEESE_SLICE), CHEESE_SLICE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.BURGER_BUN), BURGER_BUN);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.GROUND_BEEF), GROUND_BEEF);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.BEEF_PATTY), BEEF_PATTY);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.CHEESEBURGER), CHEESEBURGER);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_APPLE), CANNED_DEHYDRATED_APPLE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_CARROT), CANNED_DEHYDRATED_CARROT);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_MELON), CANNED_DEHYDRATED_MELON);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.CANNED_DEHYDRATED_POTATO), CANNED_DEHYDRATED_POTATO);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.CANNED_BEEF), CANNED_BEEF);

        // ROCKET PLATES
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TIER_1_HEAVY_DUTY_PLATE), TIER_1_HEAVY_DUTY_PLATE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TIER_2_HEAVY_DUTY_PLATE), TIER_2_HEAVY_DUTY_PLATE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TIER_3_HEAVY_DUTY_PLATE), TIER_3_HEAVY_DUTY_PLATE);

        // ARMOR
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_HELMET), HEAVY_DUTY_HELMET);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_CHESTPLATE), HEAVY_DUTY_CHESTPLATE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_LEGGINGS), HEAVY_DUTY_LEGGINGS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_BOOTS), HEAVY_DUTY_BOOTS);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.DESH_HELMET), DESH_HELMET);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.DESH_CHESTPLATE), DESH_CHESTPLATE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.DESH_LEGGINGS), DESH_LEGGINGS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.DESH_BOOTS), DESH_BOOTS);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TITANIUM_HELMET), TITANIUM_HELMET);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TITANIUM_CHESTPLATE), TITANIUM_CHESTPLATE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TITANIUM_LEGGINGS), TITANIUM_LEGGINGS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TITANIUM_BOOTS), TITANIUM_BOOTS);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.SENSOR_GLASSES), SENSOR_GLASSES);

        // TOOLS + WEAPONS
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_SWORD), HEAVY_DUTY_SWORD);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_SHOVEL), HEAVY_DUTY_SHOVEL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_PICKAXE), HEAVY_DUTY_PICKAXE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_AXE), HEAVY_DUTY_AXE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.HEAVY_DUTY_HOE), HEAVY_DUTY_HOE);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.DESH_SWORD), DESH_SWORD);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.DESH_SHOVEL), DESH_SHOVEL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.DESH_PICKAXE), DESH_PICKAXE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.DESH_AXE), DESH_AXE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.DESH_HOE), DESH_HOE);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TITANIUM_SWORD), TITANIUM_SWORD);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TITANIUM_SHOVEL), TITANIUM_SHOVEL);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TITANIUM_PICKAXE), TITANIUM_PICKAXE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TITANIUM_AXE), TITANIUM_AXE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TITANIUM_HOE), TITANIUM_HOE);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.STANDARD_WRENCH), STANDARD_WRENCH);

        // BATTERIES
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.BATTERY), BATTERY);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.INFINITE_BATTERY), INFINITE_BATTERY);

        //FLUID BUCKETS
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.CRUDE_OIL_BUCKET), CRUDE_OIL_BUCKET);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.FUEL_BUCKET), FUEL_BUCKET);

        //GALACTICRAFT INVENTORY
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.PARACHUTE), PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.ORANGE_PARACHUTE), ORANGE_PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.MAGENTA_PARACHUTE), MAGENTA_PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.LIGHT_BLUE_PARACHUTE), LIGHT_BLUE_PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.YELLOW_PARACHUTE), YELLOW_PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.LIME_PARACHUTE), LIME_PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.PINK_PARACHUTE), PINK_PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.GRAY_PARACHUTE), GRAY_PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.LIGHT_GRAY_PARACHUTE), LIGHT_GRAY_PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.CYAN_PARACHUTE), CYAN_PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.PURPLE_PARACHUTE), PURPLE_PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.BLUE_PARACHUTE), BLUE_PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.BROWN_PARACHUTE), BROWN_PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.GREEN_PARACHUTE), GREEN_PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.RED_PARACHUTE), RED_PARACHUTE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.BLACK_PARACHUTE), BLACK_PARACHUTE);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.OXYGEN_MASK), OXYGEN_MASK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.OXYGEN_GEAR), OXYGEN_GEAR);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.SMALL_OXYGEN_TANK), SMALL_OXYGEN_TANK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.MEDIUM_OXYGEN_TANK), MEDIUM_OXYGEN_TANK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.LARGE_OXYGEN_TANK), LARGE_OXYGEN_TANK);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.INFINITE_OXYGEN_TANK), INFINITE_OXYGEN_TANK);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.SHIELD_CONTROLLER), SHIELD_CONTROLLER);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.FREQUENCY_MODULE), FREQUENCY_MODULE);

        Registry.register(Registry.ITEM, Constant.id(Constant.Item.THERMAL_PADDING_HELMET), THERMAL_PADDING_HELMET);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.THERMAL_PADDING_CHESTPIECE), THERMAL_PADDING_CHESTPIECE);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.THERMAL_PADDING_LEGGINGS), THERMAL_PADDING_LEGGINGS);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.THERMAL_PADDING_BOOTS), THERMAL_PADDING_BOOTS);

        // SCHEMATICS
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TIER_2_ROCKET_SCHEMATIC), TIER_2_ROCKET_SCHEMATIC);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.CARGO_ROCKET_SCHEMATIC), CARGO_ROCKET_SCHEMATIC);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.MOON_BUGGY_SCHEMATIC), MOON_BUGGY_SCHEMATIC);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.TIER_3_ROCKET_SCHEMATIC), TIER_3_ROCKET_SCHEMATIC);
        Registry.register(Registry.ITEM, Constant.id(Constant.Item.ASTRO_MINER_SCHEMATIC), ASTRO_MINER_SCHEMATIC);
    }
}
