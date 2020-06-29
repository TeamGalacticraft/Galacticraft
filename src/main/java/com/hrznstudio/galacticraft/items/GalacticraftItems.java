/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.items;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.item.HoeItem;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftItems {
    public static final Item GLOWSTONE_TORCH = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.GLOWSTONE_TORCH), new WallStandingBlockItem(GalacticraftBlocks.GLOWSTONE_TORCH, GalacticraftBlocks.GLOWSTONE_WALL_TORCH, (new Item.Settings()).group(GalacticraftBlocks.BLOCKS_GROUP)));
    public static final Item UNLIT_TORCH = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Blocks.UNLIT_TORCH), new WallStandingBlockItem(GalacticraftBlocks.UNLIT_TORCH, GalacticraftBlocks.UNLIT_WALL_TORCH, (new Item.Settings()).group(GalacticraftBlocks.BLOCKS_GROUP)));
    public static final ItemGroup ITEMS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.Items.ITEM_GROUP))
            .icon(() -> new ItemStack(GalacticraftItems.CANVAS))
            .build();
    // MATERIALS
    public static final Item LEAD_INGOT = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.LEAD_INGOT), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item RAW_SILICON = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.RAW_SILICON), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item RAW_METEORIC_IRON = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.RAW_METEORIC_IRON), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item METEORIC_IRON_INGOT = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.METEORIC_IRON_INGOT), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item LUNAR_SAPPHIRE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.LUNAR_SAPPHIRE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item UNREFINED_DESH = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.UNREFINED_DESH), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_INGOT = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_INGOT), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_STICK = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_STICK), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item CARBON_FRAGMENTS = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CARBON_FRAGMENTS), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item IRON_SHARD = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.IRON_SHARD), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_SHARD = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_SHARD), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_INGOT = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_INGOT), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_DUST = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_DUST), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SOLAR_DUST = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.SOLAR_DUST), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BASIC_WAFER = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BASIC_WAFER), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ADVANCED_WAFER = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ADVANCED_WAFER), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BEAM_CORE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BEAM_CORE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item CANVAS = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANVAS), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_ALUMINUM = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_ALUMINUM), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_TIN = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_TIN), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_BRONZE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_BRONZE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_COPPER = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_COPPER), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_IRON = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_IRON), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_STEEL = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_STEEL), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_METEORIC_IRON = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_METEORIC_IRON), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_DESH = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_DESH), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_TITANIUM = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COMPRESSED_TITANIUM), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item FLUID_MANIPULATOR = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.FLUID_MANIPULATOR), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_CONCENTRATOR = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.OXYGEN_CONCENTRATOR), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_FAN = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.OXYGEN_FAN), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_VENT = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.OXYGEN_VENT), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SENSOR_LENS = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.SENSOR_LENS), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BLUE_SOLAR_WAFER = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BLUE_SOLAR_WAFER), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SINGLE_SOLAR_MODULE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.SINGLE_SOLAR_MODULE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item FULL_SOLAR_PANEL = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.FULL_SOLAR_PANEL), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SOLAR_ARRAY_WAFER = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.SOLAR_ARRAY_WAFER), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item STEEL_POLE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.STEEL_POLE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COPPER_CANISTER = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.COPPER_CANISTER), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIN_CANISTER = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIN_CANISTER), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item THERMAL_CLOTH = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.THERMAL_CLOTH), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ISOTHERMAL_FABRIC = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ISOTHERMAL_FABRIC), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ORION_DRIVE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ORION_DRIVE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ATMOSPHERIC_VALVE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ATMOSPHERIC_VALVE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    //FOOD
    public static final Item MOON_BERRIES = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.MOON_BERRIES), new Item(new Item.Settings().food(GCFoodSettings.MOON_BERRIES).group(ITEMS_GROUP)));
    public static final Item CHEESE_CURD = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CHEESE_CURD), new Item(new Item.Settings().food(GCFoodSettings.CHEESE_CURD).group(ITEMS_GROUP)));
    public static final Item CHEESE_SLICE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CHEESE_SLICE), new Item(new Item.Settings().food(GCFoodSettings.CHEESE_SLICE).group(ITEMS_GROUP)));
    public static final Item BURGER_BUN = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BURGER_BUN), new Item(new Item.Settings().food(GCFoodSettings.BURGER_BUN).group(ITEMS_GROUP)));
    public static final Item GROUND_BEEF = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.GROUND_BEEF), new Item(new Item.Settings().food(GCFoodSettings.GROUND_BEEF).group(ITEMS_GROUP)));
    public static final Item BEEF_PATTY = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BEEF_PATTY), new Item(new Item.Settings().food(GCFoodSettings.BEEF_PATTY).group(ITEMS_GROUP)));
    public static final Item CHEESEBURGER = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CHEESEBURGER), new Item(new Item.Settings().food(GCFoodSettings.CHEESEBURGER).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_APPLE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANNED_DEHYDRATED_APPLE), new CannedFoodItem(new Item.Settings().food(GCFoodSettings.DEHYDRATED_APPLE).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_CARROT = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANNED_DEHYDRATED_CARROT), new CannedFoodItem(new Item.Settings().food(GCFoodSettings.DEHYDRATED_CARROT).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_MELON = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANNED_DEHYDRATED_MELON), new CannedFoodItem(new Item.Settings().food(GCFoodSettings.DEHYDRATED_MELON).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_POTATO = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANNED_DEHYDRATED_POTATO), new CannedFoodItem(new Item.Settings().food(GCFoodSettings.DEHYDRATED_POTATO).group(ITEMS_GROUP)));
    public static final Item CANNED_BEEF = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CANNED_BEEF), new CannedFoodItem(new Item.Settings().food(GCFoodSettings.CANNED_BEEF).group(ITEMS_GROUP)));
    //ROCKET PARTS
    public static final Item TIER_1_HEAVY_DUTY_PLATE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIER_1_HEAVY_DUTY_PLATE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIER_2_HEAVY_DUTY_PLATE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIER_2_HEAVY_DUTY_PLATE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIER_3_HEAVY_DUTY_PLATE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIER_3_HEAVY_DUTY_PLATE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item NOSE_CONE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.NOSE_CONE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_NOSE_CONE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_NOSE_CONE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ROCKET_ENGINE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ROCKET_ENGINE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_ROCKET_ENGINE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_ROCKET_ENGINE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ROCKET_FIN = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ROCKET_FIN), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_ROCKET_FIN = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_ROCKET_FIN), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIER_1_BOOSTER = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIER_1_BOOSTER), new Item(new Item.Settings().group(ITEMS_GROUP)));
    //BUGGY PARTS
    public static final Item BUGGY_SEAT = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BUGGY_SEAT), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BUGGY_STORAGE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BUGGY_STORAGE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BUGGY_WHEEL = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BUGGY_WHEEL), new Item(new Item.Settings().group(ITEMS_GROUP)));
    //ARMOR
    public static final Item HEAVY_DUTY_HELMET = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_DUTY_HELMET), new ArmorItem(GalacticraftArmorMaterials.HEAVY_DUTY, EquipmentSlot.HEAD, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item HEAVY_DUTY_CHESTPLATE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_DUTY_CHESTPLATE), new ArmorItem(GalacticraftArmorMaterials.HEAVY_DUTY, EquipmentSlot.CHEST, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item HEAVY_DUTY_LEGGINGS = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_DUTY_LEGGINGS), new ArmorItem(GalacticraftArmorMaterials.HEAVY_DUTY, EquipmentSlot.LEGS, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item HEAVY_DUTY_BOOTS = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_DUTY_BOOTS), new ArmorItem(GalacticraftArmorMaterials.HEAVY_DUTY, EquipmentSlot.FEET, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_HELMET = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_HELMET), new ArmorItem(GalacticraftArmorMaterials.DESH, EquipmentSlot.HEAD, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_CHESTPLATE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_CHESTPLATE), new ArmorItem(GalacticraftArmorMaterials.DESH, EquipmentSlot.CHEST, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_LEGGINGS = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_LEGGINGS), new ArmorItem(GalacticraftArmorMaterials.DESH, EquipmentSlot.LEGS, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_BOOTS = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_BOOTS), new ArmorItem(GalacticraftArmorMaterials.DESH, EquipmentSlot.FEET, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_HELMET = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_HELMET), new ArmorItem(GalacticraftArmorMaterials.TITANIUM, EquipmentSlot.HEAD, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_CHESTPLATE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_CHESTPLATE), new ArmorItem(GalacticraftArmorMaterials.TITANIUM, EquipmentSlot.CHEST, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_LEGGINGS = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_LEGGINGS), new ArmorItem(GalacticraftArmorMaterials.TITANIUM, EquipmentSlot.LEGS, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_BOOTS = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_BOOTS), new ArmorItem(GalacticraftArmorMaterials.TITANIUM, EquipmentSlot.FEET, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item SENSOR_GLASSES = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.SENSOR_GLASSES), new ArmorItem(GalacticraftArmorMaterials.SENSOR_GLASSES, EquipmentSlot.HEAD, new Item.Settings().group(ITEMS_GROUP)));
    //TOOLS + WEAPONS
    public static final Item HEAVY_DUTY_SWORD = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_DUTY_SWORD), new SwordItem(GalacticraftToolMaterials.STEEL, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP)) {
        @Override
        public boolean postMine(ItemStack stack, World world, BlockState blockState, BlockPos blockPos, LivingEntity entityLiving) {
            //Stronger than vanilla
            if (blockState.getHardness(null, blockPos) > 0.2001F) {
                stack.damage(2, entityLiving, (livingEntity) -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
            return true;
        }
    });
    public static final Item HEAVY_DUTY_SHOVEL = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_DUTY_SHOVEL), new ShovelItem(GalacticraftToolMaterials.STEEL, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_PICKAXE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_DUTY_PICKAXE), new com.hrznstudio.galacticraft.api.item.PickaxeItem(GalacticraftToolMaterials.STEEL, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_AXE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_DUTY_AXE), new com.hrznstudio.galacticraft.api.item.AxeItem(GalacticraftToolMaterials.STEEL, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_HOE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HEAVY_DUTY_HOE), new com.hrznstudio.galacticraft.api.item.HoeItem(GalacticraftToolMaterials.STEEL, -1.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_SWORD = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_SWORD), new SwordItem(GalacticraftToolMaterials.DESH, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_SHOVEL = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_SHOVEL), new ShovelItem(GalacticraftToolMaterials.DESH, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_PICKAXE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_PICKAXE), new com.hrznstudio.galacticraft.api.item.PickaxeItem(GalacticraftToolMaterials.DESH, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_AXE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_AXE), new com.hrznstudio.galacticraft.api.item.AxeItem(GalacticraftToolMaterials.DESH, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_HOE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.DESH_HOE), new com.hrznstudio.galacticraft.api.item.HoeItem(GalacticraftToolMaterials.DESH, -1.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_SWORD = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_SWORD), new SwordItem(GalacticraftToolMaterials.TITANIUM, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP)) {
        @Override
        public boolean postMine(ItemStack stack, World world, BlockState blockState, BlockPos blockPos, LivingEntity entityLiving) {
            //Stronger than vanilla
            if (blockState.getHardness(null, blockPos) > 0.2001F) {
                stack.damage(2, entityLiving, (livingEntity) -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
            return true;
        }
    });
    public static final Item TITANIUM_SHOVEL = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_SHOVEL), new ShovelItem(GalacticraftToolMaterials.TITANIUM, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_PICKAXE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_PICKAXE), new com.hrznstudio.galacticraft.api.item.PickaxeItem(GalacticraftToolMaterials.TITANIUM, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_AXE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_AXE), new com.hrznstudio.galacticraft.api.item.AxeItem(GalacticraftToolMaterials.TITANIUM, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_HOE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TITANIUM_HOE), new HoeItem(GalacticraftToolMaterials.TITANIUM, -1.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item STANDARD_WRENCH = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.STANDARD_WRENCH), new StandardWrenchItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BATTERY = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BATTERY), new BatteryItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(BatteryItem.MAX_ENERGY)));
    public static final Item INFINITE_BATTERY = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.INFINITE_BATTERY), new InfiniteBatteryItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(Integer.MAX_VALUE)));
    //Fluid buckets
    public static final BucketItem CRUDE_OIL_BUCKET = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CRUDE_OIL_BUCKET), new BucketItem(GalacticraftFluids.CRUDE_OIL, new Item.Settings().recipeRemainder(Items.BUCKET).group(ITEMS_GROUP)));
    public static final BucketItem FUEL_BUCKET = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.FUEL_BUCKET), new BucketItem(GalacticraftFluids.FUEL, new Item.Settings().recipeRemainder(Items.BUCKET).group(ITEMS_GROUP)));
    //GC INVENTORY
    public static final Item PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ORANGE_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ORANGE_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item MAGENTA_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.MAGENTA_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item LIGHT_BLUE_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.LIGHT_BLUE_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item YELLOW_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.YELLOW_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item LIME_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.LIME_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item PINK_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.PINK_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item GRAY_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.GRAY_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item LIGHT_GRAY_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.LIGHT_GRAY_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item CYAN_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CYAN_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item PURPLE_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.PURPLE_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BLUE_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BLUE_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BROWN_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BROWN_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item GREEN_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.GREEN_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item RED_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.RED_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BLACK_PARACHUTE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.BLACK_PARACHUTE), new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_MASK = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.OXYGEN_MASK), new OxygenMaskItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_GEAR = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.OXYGEN_GEAR), new OxygenGearItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SHIELD_CONTROLLER = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.SHIELD_CONTROLLER), new GCAccessories(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item FREQUENCY_MODULE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.FREQUENCY_MODULE), new GCAccessories(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SMALL_OXYGEN_TANK = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.SMALL_OXYGEN_TANK), new OxygenTankItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(900)));
    public static final Item MEDIUM_OXYGEN_TANK = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.MEDIUM_OXYGEN_TANK), new OxygenTankItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(1800)));
    public static final Item LARGE_OXYGEN_TANK = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.LARGE_OXYGEN_TANK), new OxygenTankItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(2700)));
    public static final Item THERMAL_PADDING_HELMET = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.THERMAL_PADDING_HELMET), new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.HEAD));
    public static final Item THERMAL_PADDING_CHESTPIECE = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.THERMAL_PADDING_CHESTPIECE), new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.CHEST));
    public static final Item THERMAL_PADDING_LEGGINGS = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.THERMAL_PADDING_LEGGINGS), new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.LEGS));
    public static final Item THERMAL_PADDING_BOOTS = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.THERMAL_PADDING_BOOTS), new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.FEET));
    public static final Item TIER_2_ROCKET_SCHEMATIC = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIER_2_ROCKET_SCHEMATIC), new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item CARGO_ROCKET_SCHEMATIC = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.CARGO_ROCKET_SCHEMATIC), new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item MOON_BUGGY_SCHEMATIC = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.MOON_BUGGY_SCHEMATIC), new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIER_3_ROCKET_SCHEMATIC = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.TIER_3_ROCKET_SCHEMATIC), new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ASTRO_MINER_SCHEMATIC = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.ASTRO_MINER_SCHEMATIC), new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    // SPAWN EGGS
    //public static final Item MOON_VILLAGER_SPAWN_EGG = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.MOON_VILLAGER_SPAWN_EGG), new SpawnEggItem(GalacticraftEntityTypes.MOON_VILLAGER, 0xC0C9C0, 0x5698D8, new Item.Settings().group(ITEMS_GROUP)));
    //public static final Item EVOLVED_ZOMBIE_SPAWN_EGG = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.EVOLVED_ZOMBIE_SPAWN_EGG), new SpawnEggItem(GalacticraftEntityTypes.EVOLVED_ZOMBIE, 0xC0CCC0, 0x99EE99, new Item.Settings().group(ITEMS_GROUP)));
    // THROWABLE METEOR CHUNKS
    public static final Item THROWABLE_METEOR_CHUNK = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.THROWABLE_METEOR_CHUNK), new ThrowableMeteorChunkItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HOT_THROWABLE_METEOR_CHUNK = Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, Constants.Items.HOT_THROWABLE_METEOR_CHUNK), new HotThrowableMeteorChunkItem(new Item.Settings().group(ITEMS_GROUP)));

    public static void register() {}
}
