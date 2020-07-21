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
import com.hrznstudio.galacticraft.api.item.AxeItem;
import com.hrznstudio.galacticraft.api.item.HoeItem;
import com.hrznstudio.galacticraft.api.item.PickaxeItem;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.*;
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
    public static final Item GLOWSTONE_TORCH = registerItem(Constants.Blocks.GLOWSTONE_TORCH, new WallStandingBlockItem(GalacticraftBlocks.GLOWSTONE_TORCH, GalacticraftBlocks.GLOWSTONE_WALL_TORCH, (new Item.Settings())/*.group(GalacticraftBlocks.BLOCKS_GROUP)*/));
    public static final Item UNLIT_TORCH = registerItem(Constants.Blocks.UNLIT_TORCH, new WallStandingBlockItem(GalacticraftBlocks.UNLIT_TORCH, GalacticraftBlocks.UNLIT_WALL_TORCH, (new Item.Settings())/*.group(GalacticraftBlocks.BLOCKS_GROUP)*/));

    public static final ItemGroup ITEMS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.Items.ITEM_GROUP))
            .icon(() -> new ItemStack(GalacticraftItems.CANVAS))
            .build();

    public static final ItemGroup BLOCKS_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.Blocks.ITEM_GROUP_BLOCKS))
            .icon(() -> new ItemStack(GalacticraftBlocks.MOON_TURF)).build();

    public static final ItemGroup MACHINES_GROUP = FabricItemGroupBuilder.create(
            new Identifier(Constants.MOD_ID, Constants.Blocks.ITEM_GROUP_MACHINES))
            .icon(() -> new ItemStack(GalacticraftBlocks.COAL_GENERATOR)).build();

    // MATERIALS
    public static final Item LEAD_INGOT = registerItem(Constants.Items.LEAD_INGOT, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item RAW_SILICON = registerItem(Constants.Items.RAW_SILICON, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item RAW_METEORIC_IRON = registerItem(Constants.Items.RAW_METEORIC_IRON, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item METEORIC_IRON_INGOT = registerItem(Constants.Items.METEORIC_IRON_INGOT, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item LUNAR_SAPPHIRE = registerItem(Constants.Items.LUNAR_SAPPHIRE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item UNREFINED_DESH = registerItem(Constants.Items.UNREFINED_DESH, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_INGOT = registerItem(Constants.Items.DESH_INGOT, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_STICK = registerItem(Constants.Items.DESH_STICK, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item CARBON_FRAGMENTS = registerItem(Constants.Items.CARBON_FRAGMENTS, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item IRON_SHARD = registerItem(Constants.Items.IRON_SHARD, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_SHARD = registerItem(Constants.Items.TITANIUM_SHARD, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_INGOT = registerItem(Constants.Items.TITANIUM_INGOT, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_DUST = registerItem(Constants.Items.TITANIUM_DUST, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SOLAR_DUST = registerItem(Constants.Items.SOLAR_DUST, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BASIC_WAFER = registerItem(Constants.Items.BASIC_WAFER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ADVANCED_WAFER = registerItem(Constants.Items.ADVANCED_WAFER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BEAM_CORE = registerItem(Constants.Items.BEAM_CORE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item CANVAS = registerItem(Constants.Items.CANVAS, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_ALUMINUM = registerItem(Constants.Items.COMPRESSED_ALUMINUM, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_TIN = registerItem(Constants.Items.COMPRESSED_TIN, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_BRONZE = registerItem(Constants.Items.COMPRESSED_BRONZE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_COPPER = registerItem(Constants.Items.COMPRESSED_COPPER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_IRON = registerItem(Constants.Items.COMPRESSED_IRON, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_STEEL = registerItem(Constants.Items.COMPRESSED_STEEL, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_METEORIC_IRON = registerItem(Constants.Items.COMPRESSED_METEORIC_IRON, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_DESH = registerItem(Constants.Items.COMPRESSED_DESH, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COMPRESSED_TITANIUM = registerItem(Constants.Items.COMPRESSED_TITANIUM, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item FLUID_MANIPULATOR = registerItem(Constants.Items.FLUID_MANIPULATOR, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_CONCENTRATOR = registerItem(Constants.Items.OXYGEN_CONCENTRATOR, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_FAN = registerItem(Constants.Items.OXYGEN_FAN, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_VENT = registerItem(Constants.Items.OXYGEN_VENT, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SENSOR_LENS = registerItem(Constants.Items.SENSOR_LENS, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BLUE_SOLAR_WAFER = registerItem(Constants.Items.BLUE_SOLAR_WAFER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SINGLE_SOLAR_MODULE = registerItem(Constants.Items.SINGLE_SOLAR_MODULE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item FULL_SOLAR_PANEL = registerItem(Constants.Items.FULL_SOLAR_PANEL, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SOLAR_ARRAY_WAFER = registerItem(Constants.Items.SOLAR_ARRAY_WAFER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item STEEL_POLE = registerItem(Constants.Items.STEEL_POLE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item COPPER_CANISTER = registerItem(Constants.Items.COPPER_CANISTER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIN_CANISTER = registerItem(Constants.Items.TIN_CANISTER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item THERMAL_CLOTH = registerItem(Constants.Items.THERMAL_CLOTH, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ISOTHERMAL_FABRIC = registerItem(Constants.Items.ISOTHERMAL_FABRIC, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ORION_DRIVE = registerItem(Constants.Items.ORION_DRIVE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ATMOSPHERIC_VALVE = registerItem(Constants.Items.ATMOSPHERIC_VALVE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    //FOOD
    public static final Item MOON_BERRIES = registerItem(Constants.Items.MOON_BERRIES, new Item(new Item.Settings().food(GalacticraftFoodComponents.MOON_BERRIES).group(ITEMS_GROUP)));
    public static final Item CHEESE_CURD = registerItem(Constants.Items.CHEESE_CURD, new Item(new Item.Settings().food(GalacticraftFoodComponents.CHEESE_CURD).group(ITEMS_GROUP)));
    public static final Item CHEESE_SLICE = registerItem(Constants.Items.CHEESE_SLICE, new Item(new Item.Settings().food(GalacticraftFoodComponents.CHEESE_SLICE).group(ITEMS_GROUP)));
    public static final Item BURGER_BUN = registerItem(Constants.Items.BURGER_BUN, new Item(new Item.Settings().food(GalacticraftFoodComponents.BURGER_BUN).group(ITEMS_GROUP)));
    public static final Item GROUND_BEEF = registerItem(Constants.Items.GROUND_BEEF, new Item(new Item.Settings().food(GalacticraftFoodComponents.GROUND_BEEF).group(ITEMS_GROUP)));
    public static final Item BEEF_PATTY = registerItem(Constants.Items.BEEF_PATTY, new Item(new Item.Settings().food(GalacticraftFoodComponents.BEEF_PATTY).group(ITEMS_GROUP)));
    public static final Item CHEESEBURGER = registerItem(Constants.Items.CHEESEBURGER, new Item(new Item.Settings().food(GalacticraftFoodComponents.CHEESEBURGER).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_APPLE = registerItem(Constants.Items.CANNED_DEHYDRATED_APPLE, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponents.DEHYDRATED_APPLE).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_CARROT = registerItem(Constants.Items.CANNED_DEHYDRATED_CARROT, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponents.DEHYDRATED_CARROT).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_MELON = registerItem(Constants.Items.CANNED_DEHYDRATED_MELON, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponents.DEHYDRATED_MELON).group(ITEMS_GROUP)));
    public static final Item CANNED_DEHYDRATED_POTATO = registerItem(Constants.Items.CANNED_DEHYDRATED_POTATO, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponents.DEHYDRATED_POTATO).group(ITEMS_GROUP)));
    public static final Item CANNED_BEEF = registerItem(Constants.Items.CANNED_BEEF, new CannedFoodItem(new Item.Settings().food(GalacticraftFoodComponents.CANNED_BEEF).group(ITEMS_GROUP)));
    //ROCKET PARTS
    public static final Item TIER_1_HEAVY_DUTY_PLATE = registerItem(Constants.Items.TIER_1_HEAVY_DUTY_PLATE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIER_2_HEAVY_DUTY_PLATE = registerItem(Constants.Items.TIER_2_HEAVY_DUTY_PLATE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIER_3_HEAVY_DUTY_PLATE = registerItem(Constants.Items.TIER_3_HEAVY_DUTY_PLATE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item NOSE_CONE = registerItem(Constants.Items.NOSE_CONE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_NOSE_CONE = registerItem(Constants.Items.HEAVY_NOSE_CONE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ROCKET_ENGINE = registerItem(Constants.Items.ROCKET_ENGINE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_ROCKET_ENGINE = registerItem(Constants.Items.HEAVY_ROCKET_ENGINE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ROCKET_FIN = registerItem(Constants.Items.ROCKET_FIN, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_ROCKET_FIN = registerItem(Constants.Items.HEAVY_ROCKET_FIN, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIER_1_BOOSTER = registerItem(Constants.Items.TIER_1_BOOSTER, new Item(new Item.Settings().group(ITEMS_GROUP)));
    //BUGGY PARTS
    public static final Item BUGGY_SEAT = registerItem(Constants.Items.BUGGY_SEAT, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BUGGY_STORAGE = registerItem(Constants.Items.BUGGY_STORAGE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BUGGY_WHEEL = registerItem(Constants.Items.BUGGY_WHEEL, new Item(new Item.Settings().group(ITEMS_GROUP)));
    //ARMOR
    public static final Item HEAVY_DUTY_HELMET = registerItem(Constants.Items.HEAVY_DUTY_HELMET, new ArmorItem(GalacticraftArmorMaterials.HEAVY_DUTY, EquipmentSlot.HEAD, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item HEAVY_DUTY_CHESTPLATE = registerItem(Constants.Items.HEAVY_DUTY_CHESTPLATE, new ArmorItem(GalacticraftArmorMaterials.HEAVY_DUTY, EquipmentSlot.CHEST, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item HEAVY_DUTY_LEGGINGS = registerItem(Constants.Items.HEAVY_DUTY_LEGGINGS, new ArmorItem(GalacticraftArmorMaterials.HEAVY_DUTY, EquipmentSlot.LEGS, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item HEAVY_DUTY_BOOTS = registerItem(Constants.Items.HEAVY_DUTY_BOOTS, new ArmorItem(GalacticraftArmorMaterials.HEAVY_DUTY, EquipmentSlot.FEET, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_HELMET = registerItem(Constants.Items.DESH_HELMET, new ArmorItem(GalacticraftArmorMaterials.DESH, EquipmentSlot.HEAD, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_CHESTPLATE = registerItem(Constants.Items.DESH_CHESTPLATE, new ArmorItem(GalacticraftArmorMaterials.DESH, EquipmentSlot.CHEST, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_LEGGINGS = registerItem(Constants.Items.DESH_LEGGINGS, new ArmorItem(GalacticraftArmorMaterials.DESH, EquipmentSlot.LEGS, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item DESH_BOOTS = registerItem(Constants.Items.DESH_BOOTS, new ArmorItem(GalacticraftArmorMaterials.DESH, EquipmentSlot.FEET, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_HELMET = registerItem(Constants.Items.TITANIUM_HELMET, new ArmorItem(GalacticraftArmorMaterials.TITANIUM, EquipmentSlot.HEAD, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_CHESTPLATE = registerItem(Constants.Items.TITANIUM_CHESTPLATE, new ArmorItem(GalacticraftArmorMaterials.TITANIUM, EquipmentSlot.CHEST, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_LEGGINGS = registerItem(Constants.Items.TITANIUM_LEGGINGS, new ArmorItem(GalacticraftArmorMaterials.TITANIUM, EquipmentSlot.LEGS, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item TITANIUM_BOOTS = registerItem(Constants.Items.TITANIUM_BOOTS, new ArmorItem(GalacticraftArmorMaterials.TITANIUM, EquipmentSlot.FEET, (new Item.Settings().group(ITEMS_GROUP))));
    public static final Item SENSOR_GLASSES = registerItem(Constants.Items.SENSOR_GLASSES, new ArmorItem(GalacticraftArmorMaterials.SENSOR_GLASSES, EquipmentSlot.HEAD, new Item.Settings().group(ITEMS_GROUP)));
    //TOOLS + WEAPONS
    public static final Item HEAVY_DUTY_SWORD = registerItem(Constants.Items.HEAVY_DUTY_SWORD, new SwordItem(GalacticraftToolMaterials.STEEL, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP)) {
        @Override
        public boolean postMine(ItemStack stack, World world, BlockState blockState, BlockPos blockPos, LivingEntity entityLiving) {
            //Stronger than vanilla
            if (blockState.getHardness(null, blockPos) > 0.2001F) {
                stack.damage(2, entityLiving, (livingEntity) -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
            return true;
        }
    });
    public static final Item HEAVY_DUTY_SHOVEL = registerItem(Constants.Items.HEAVY_DUTY_SHOVEL, new ShovelItem(GalacticraftToolMaterials.STEEL, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_PICKAXE = registerItem(Constants.Items.HEAVY_DUTY_PICKAXE, new PickaxeItem(GalacticraftToolMaterials.STEEL, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_AXE = registerItem(Constants.Items.HEAVY_DUTY_AXE, new AxeItem(GalacticraftToolMaterials.STEEL, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HEAVY_DUTY_HOE = registerItem(Constants.Items.HEAVY_DUTY_HOE, new HoeItem(GalacticraftToolMaterials.STEEL, -1.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_SWORD = registerItem(Constants.Items.DESH_SWORD, new SwordItem(GalacticraftToolMaterials.DESH, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_SHOVEL = registerItem(Constants.Items.DESH_SHOVEL, new ShovelItem(GalacticraftToolMaterials.DESH, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_PICKAXE = registerItem(Constants.Items.DESH_PICKAXE, new PickaxeItem(GalacticraftToolMaterials.DESH, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_AXE = registerItem(Constants.Items.DESH_AXE, new AxeItem(GalacticraftToolMaterials.DESH, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item DESH_HOE = registerItem(Constants.Items.DESH_HOE, new HoeItem(GalacticraftToolMaterials.DESH, -1.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_SWORD = registerItem(Constants.Items.TITANIUM_SWORD, new SwordItem(GalacticraftToolMaterials.TITANIUM, 3, -2.4F, new Item.Settings().group(ITEMS_GROUP)) {
        @Override
        public boolean postMine(ItemStack stack, World world, BlockState blockState, BlockPos blockPos, LivingEntity entityLiving) {
            //Stronger than vanilla
            if (blockState.getHardness(null, blockPos) > 0.2001F) {
                stack.damage(2, entityLiving, (livingEntity) -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
            return true;
        }
    });
    public static final Item TITANIUM_SHOVEL = registerItem(Constants.Items.TITANIUM_SHOVEL, new ShovelItem(GalacticraftToolMaterials.TITANIUM, -1.5F, -3.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_PICKAXE = registerItem(Constants.Items.TITANIUM_PICKAXE, new PickaxeItem(GalacticraftToolMaterials.TITANIUM, 1, -2.8F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_AXE = registerItem(Constants.Items.TITANIUM_AXE, new AxeItem(GalacticraftToolMaterials.TITANIUM, 6.0F, -3.1F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TITANIUM_HOE = registerItem(Constants.Items.TITANIUM_HOE, new HoeItem(GalacticraftToolMaterials.TITANIUM, -1.0F, new Item.Settings().group(ITEMS_GROUP)));
    public static final Item STANDARD_WRENCH = registerItem(Constants.Items.STANDARD_WRENCH, new StandardWrenchItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BATTERY = registerItem(Constants.Items.BATTERY, new BatteryItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(BatteryItem.MAX_ENERGY)));
    public static final Item INFINITE_BATTERY = registerItem(Constants.Items.INFINITE_BATTERY, new InfiniteBatteryItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(Integer.MAX_VALUE)));
    //Fluid buckets
    public static final BucketItem CRUDE_OIL_BUCKET = registerItem(Constants.Items.CRUDE_OIL_BUCKET, new BucketItem(GalacticraftFluids.CRUDE_OIL, new Item.Settings().recipeRemainder(Items.BUCKET).group(ITEMS_GROUP)));
    public static final BucketItem FUEL_BUCKET = registerItem(Constants.Items.FUEL_BUCKET, new BucketItem(GalacticraftFluids.FUEL, new Item.Settings().recipeRemainder(Items.BUCKET).group(ITEMS_GROUP)));
    //GC INVENTORY
    public static final Item PARACHUTE = registerItem(Constants.Items.PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ORANGE_PARACHUTE = registerItem(Constants.Items.ORANGE_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item MAGENTA_PARACHUTE = registerItem(Constants.Items.MAGENTA_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item LIGHT_BLUE_PARACHUTE = registerItem(Constants.Items.LIGHT_BLUE_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item YELLOW_PARACHUTE = registerItem(Constants.Items.YELLOW_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item LIME_PARACHUTE = registerItem(Constants.Items.LIME_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item PINK_PARACHUTE = registerItem(Constants.Items.PINK_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item GRAY_PARACHUTE = registerItem(Constants.Items.GRAY_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item LIGHT_GRAY_PARACHUTE = registerItem(Constants.Items.LIGHT_GRAY_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item CYAN_PARACHUTE = registerItem(Constants.Items.CYAN_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item PURPLE_PARACHUTE = registerItem(Constants.Items.PURPLE_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BLUE_PARACHUTE = registerItem(Constants.Items.BLUE_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BROWN_PARACHUTE = registerItem(Constants.Items.BROWN_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item GREEN_PARACHUTE = registerItem(Constants.Items.GREEN_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item RED_PARACHUTE = registerItem(Constants.Items.RED_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item BLACK_PARACHUTE = registerItem(Constants.Items.BLACK_PARACHUTE, new Item(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_MASK = registerItem(Constants.Items.OXYGEN_MASK, new OxygenMaskItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item OXYGEN_GEAR = registerItem(Constants.Items.OXYGEN_GEAR, new OxygenGearItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SHIELD_CONTROLLER = registerItem(Constants.Items.SHIELD_CONTROLLER, new GCAccessories(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item FREQUENCY_MODULE = registerItem(Constants.Items.FREQUENCY_MODULE, new GCAccessories(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item SMALL_OXYGEN_TANK = registerItem(Constants.Items.SMALL_OXYGEN_TANK, new OxygenTankItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(900)));
    public static final Item MEDIUM_OXYGEN_TANK = registerItem(Constants.Items.MEDIUM_OXYGEN_TANK, new OxygenTankItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(1800)));
    public static final Item LARGE_OXYGEN_TANK = registerItem(Constants.Items.LARGE_OXYGEN_TANK, new OxygenTankItem(new Item.Settings().group(ITEMS_GROUP).maxDamage(2700)));
    public static final Item THERMAL_PADDING_HELMET = registerItem(Constants.Items.THERMAL_PADDING_HELMET, new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.HEAD));
    public static final Item THERMAL_PADDING_CHESTPIECE = registerItem(Constants.Items.THERMAL_PADDING_CHESTPIECE, new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.CHEST));
    public static final Item THERMAL_PADDING_LEGGINGS = registerItem(Constants.Items.THERMAL_PADDING_LEGGINGS, new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.LEGS));
    public static final Item THERMAL_PADDING_BOOTS = registerItem(Constants.Items.THERMAL_PADDING_BOOTS, new ThermalArmorItem(new Item.Settings().group(ITEMS_GROUP), EquipmentSlot.FEET));
    public static final Item TIER_2_ROCKET_SCHEMATIC = registerItem(Constants.Items.TIER_2_ROCKET_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item CARGO_ROCKET_SCHEMATIC = registerItem(Constants.Items.CARGO_ROCKET_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item MOON_BUGGY_SCHEMATIC = registerItem(Constants.Items.MOON_BUGGY_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item TIER_3_ROCKET_SCHEMATIC = registerItem(Constants.Items.TIER_3_ROCKET_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item ASTRO_MINER_SCHEMATIC = registerItem(Constants.Items.ASTRO_MINER_SCHEMATIC, new SchematicItem(new Item.Settings().group(ITEMS_GROUP)));
    // SPAWN EGGS
    //public static final Item MOON_VILLAGER_SPAWN_EGG = registerItem(Constants.Items.MOON_VILLAGER_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityTypes.MOON_VILLAGER, 0xC0C9C0, 0x5698D8, new Item.Settings().group(ITEMS_GROUP)));
    //public static final Item EVOLVED_ZOMBIE_SPAWN_EGG = registerItem(Constants.Items.EVOLVED_ZOMBIE_SPAWN_EGG, new SpawnEggItem(GalacticraftEntityTypes.EVOLVED_ZOMBIE, 0xC0CCC0, 0x99EE99, new Item.Settings().group(ITEMS_GROUP)));
    // THROWABLE METEOR CHUNKS
    public static final Item THROWABLE_METEOR_CHUNK = registerItem(Constants.Items.THROWABLE_METEOR_CHUNK, new ThrowableMeteorChunkItem(new Item.Settings().group(ITEMS_GROUP)));
    public static final Item HOT_THROWABLE_METEOR_CHUNK = registerItem(Constants.Items.HOT_THROWABLE_METEOR_CHUNK, new HotThrowableMeteorChunkItem(new Item.Settings().group(ITEMS_GROUP)));
    
    //BLOCK ITEMS
    public static final Item WALKWAY = registerBlockItem(Constants.Blocks.WALKWAY, BLOCKS_GROUP);
    public static final Item WIRE_WALKWAY = registerBlockItem(Constants.Blocks.PIPE_WALKWAY, BLOCKS_GROUP);
    public static final Item PIPE_WALKWAY = registerBlockItem(Constants.Blocks.WIRE_WALKWAY, BLOCKS_GROUP);
    public static final Item MOON_TURF = registerBlockItem(Constants.Blocks.MOON_TURF, BLOCKS_GROUP);
    public static final Item MOON_ROCK = registerBlockItem(Constants.Blocks.MOON_ROCK, BLOCKS_GROUP);
    public static final Item MOON_ROCK_WALL = registerBlockItem(Constants.Blocks.MOON_ROCK_WALL, BLOCKS_GROUP);
    public static final Item MOON_BASALT = registerBlockItem(Constants.Blocks.MOON_BASALT, BLOCKS_GROUP);
    public static final Item MOON_BASALT_SLAB = registerBlockItem(Constants.Blocks.MOON_BASALT_SLAB, BLOCKS_GROUP);
    public static final Item MOON_BASALT_STAIRS = registerBlockItem(Constants.Blocks.MOON_BASALT_STAIRS, BLOCKS_GROUP);
    public static final Item MOON_BASALT_WALL = registerBlockItem(Constants.Blocks.MOON_BASALT_WALL, BLOCKS_GROUP);
    public static final Item MOON_BASALT_BRICKS = registerBlockItem(Constants.Blocks.MOON_BASALT_BRICKS, BLOCKS_GROUP);
    public static final Item MOON_CHEESE_LEAVES = registerBlockItem(Constants.Blocks.MOON_CHEESE_LEAVES, BLOCKS_GROUP);
    public static final Item MOON_CHEESE_LOG = registerBlockItem(Constants.Blocks.MOON_CHEESE_LOG, BLOCKS_GROUP);
    public static final Item MOON_DIRT = registerBlockItem(Constants.Blocks.MOON_DIRT, BLOCKS_GROUP);
    public static final Item MOON_DUNGEON_BRICKS = registerBlockItem(Constants.Blocks.MOON_DUNGEON_BRICK, BLOCKS_GROUP);
    public static final Item MARS_SURFACE_ROCK = registerBlockItem(Constants.Blocks.MARS_SURFACE_ROCK, BLOCKS_GROUP);
    public static final Item MARS_SUB_SURFACE_ROCK = registerBlockItem(Constants.Blocks.MARS_SUB_SURFACE_ROCK, BLOCKS_GROUP);
    public static final Item MARS_STONE = registerBlockItem(Constants.Blocks.MARS_STONE, BLOCKS_GROUP);
    public static final Item MARS_COBBLESTONE = registerBlockItem(Constants.Blocks.MARS_COBBLESTONE, BLOCKS_GROUP);
    public static final Item MARS_DUNGEON_BRICKS = registerBlockItem(Constants.Blocks.MARS_DUNGEON_BRICK, BLOCKS_GROUP);
    public static final Item ASTEROID_ROCK = registerBlockItem(Constants.Blocks.ASTEROID_ROCK, BLOCKS_GROUP);
    public static final Item ASTEROID_ROCK_1 = registerBlockItem(Constants.Blocks.ASTEROID_ROCK_1, BLOCKS_GROUP);
    public static final Item ASTEROID_ROCK_2 = registerBlockItem(Constants.Blocks.ASTEROID_ROCK_2, BLOCKS_GROUP);
    public static final Item SOFT_VENUS_ROCK = registerBlockItem(Constants.Blocks.SOFT_VENUS_ROCK, BLOCKS_GROUP);
    public static final Item HARD_VENUS_ROCK = registerBlockItem(Constants.Blocks.HARD_VENUS_ROCK, BLOCKS_GROUP);
    public static final Item SCORCHED_VENUS_ROCK = registerBlockItem(Constants.Blocks.SCORCHED_VENUS_ROCK, BLOCKS_GROUP);
    public static final Item VOLCANIC_ROCK = registerBlockItem(Constants.Blocks.VOLCANIC_ROCK, BLOCKS_GROUP);
    public static final Item PUMICE = registerBlockItem(Constants.Blocks.PUMICE, BLOCKS_GROUP);
    public static final Item VAPOR_SPOUT = registerBlockItem(Constants.Blocks.VAPOR_SPOUT, BLOCKS_GROUP);
    public static final Item TIN_DECORATION_BLOCK = registerBlockItem(Constants.Blocks.TIN_DECORATION, BLOCKS_GROUP);
    public static final Item DETAILED_TIN_DECORATION_BLOCK = registerBlockItem(Constants.Blocks.DETAILED_TIN_DECORATION, BLOCKS_GROUP);
    public static final Item DARK_DECORATION_BLOCK = registerBlockItem(Constants.Blocks.DARK_DECORATION, BLOCKS_GROUP);
    public static final Item GRATING = registerBlockItem(Constants.Blocks.GRATING, BLOCKS_GROUP);
    public static final Item ALUMINUM_WIRE = registerBlockItem(Constants.Blocks.ALUMINUM_WIRE, BLOCKS_GROUP);
    public static final Item SEALABLE_ALUMINUM_WIRE = registerBlockItem(Constants.Blocks.SEALABLE_ALUMINUM_WIRE, BLOCKS_GROUP);
    public static final Item FLUID_PIPE = registerBlockItem(Constants.Blocks.FLUID_PIPE, BLOCKS_GROUP);
    public static final Item SQUARE_LIGHT_PANEL = registerBlockItem(Constants.Blocks.SQUARE_LIGHT_PANEL, BLOCKS_GROUP);
    public static final Item SPOTLIGHT_LIGHT_PANEL = registerBlockItem(Constants.Blocks.SPOTLIGHT_LIGHT_PANEL, BLOCKS_GROUP);
    public static final Item LINEAR_LIGHT_PANEL = registerBlockItem(Constants.Blocks.LINEAR_LIGHT_PANEL, BLOCKS_GROUP);
    public static final Item DASHED_LIGHT_PANEL = registerBlockItem(Constants.Blocks.DASHED_LIGHT_PANEL, BLOCKS_GROUP);
    public static final Item DIAGONAL_LIGHT_PANEL = registerBlockItem(Constants.Blocks.DIAGONAL_LIGHT_PANEL, BLOCKS_GROUP);
    public static final Item VACUUM_GLASS = registerBlockItem(Constants.Blocks.VACUUM_GLASS, BLOCKS_GROUP);
    public static final Item CLEAR_VACUUM_GLASS = registerBlockItem(Constants.Blocks.CLEAR_VACUUM_GLASS, BLOCKS_GROUP);
    public static final Item STRONG_VACUUM_GLASS = registerBlockItem(Constants.Blocks.STRONG_VACUUM_GLASS, BLOCKS_GROUP);
    public static final Item TIN_DECORATION_SLAB = registerBlockItem(Constants.Blocks.TIN_DECORATION_SLAB, BLOCKS_GROUP);
    public static final Item TIN_DECORATION_SLAB_1 = registerBlockItem(Constants.Blocks.DETAILED_TIN_DECORATION_SLAB, BLOCKS_GROUP);
    public static final Item DARK_DECORATION_SLAB = registerBlockItem(Constants.Blocks.DARK_DECORATION_SLAB, BLOCKS_GROUP);
    public static final Item MARS_COBBLESTONE_SLAB = registerBlockItem(Constants.Blocks.MARS_COBBLESTONE_SLAB, BLOCKS_GROUP);
    public static final Item MARS_DUNGEON_BRICKS_SLAB = registerBlockItem(Constants.Blocks.MARS_DUNGEON_BRICK_SLAB, BLOCKS_GROUP);
    public static final Item MOON_DUNGEON_BRICKS_SLAB = registerBlockItem(Constants.Blocks.MOON_DUNGEON_BRICK_SLAB, BLOCKS_GROUP);
    public static final Item MOON_ROCK_SLAB = registerBlockItem(Constants.Blocks.MOON_ROCK_SLAB, BLOCKS_GROUP);
    public static final Item MOON_ROCK_STAIRS = registerBlockItem(Constants.Blocks.MOON_ROCK_STAIRS, BLOCKS_GROUP);
    public static final Item MOON_DUNGEON_BRICKS_STAIRS = registerBlockItem(Constants.Blocks.MOON_DUNGEON_BRICK_STAIRS, BLOCKS_GROUP);
    public static final Item TIN_DECORATION_STAIRS = registerBlockItem(Constants.Blocks.TIN_DECORATION_STAIRS, BLOCKS_GROUP);
    public static final Item DETAILED_TIN_DECORATION_STAIRS = registerBlockItem(Constants.Blocks.DETAILED_TIN_DECORATION_STAIRS, BLOCKS_GROUP);
    public static final Item MARS_DUNGEON_BRICKS_STAIRS = registerBlockItem(Constants.Blocks.MARS_DUNGEON_BRICK_STAIRS, BLOCKS_GROUP);
    public static final Item MARS_COBBLESTONE_STAIRS = registerBlockItem(Constants.Blocks.MARS_COBBLESTONE_STAIRS, BLOCKS_GROUP);
    public static final Item TIN_DECORATION_WALL = registerBlockItem(Constants.Blocks.TIN_DECORATION_WALL, BLOCKS_GROUP);
    public static final Item DETAILED_TIN_DECORATION_WALL = registerBlockItem(Constants.Blocks.DETAILED_TIN_DECORATION_WALL, BLOCKS_GROUP);
    public static final Item MOON_DUNGEON_BRICKS_WALL = registerBlockItem(Constants.Blocks.MOON_DUNGEON_BRICK_WALL, BLOCKS_GROUP);
    public static final Item MARS_COBBLESTONE_WALL = registerBlockItem(Constants.Blocks.MARS_COBBLESTONE_WALL, BLOCKS_GROUP);
    public static final Item MARS_DUNGEON_BRICKS_WALL = registerBlockItem(Constants.Blocks.MARS_DUNGEON_BRICK_WALL, BLOCKS_GROUP);
    public static final Item SILICON_ORE = registerBlockItem(Constants.Blocks.SILICON_ORE, BLOCKS_GROUP);
    public static final Item ASTEROID_ALUMINUM_ORE = registerBlockItem(Constants.Blocks.ASTEROID_ALUMINUM_ORE, BLOCKS_GROUP);
    public static final Item MOON_CHEESE_ORE = registerBlockItem(Constants.Blocks.MOON_CHEESE_ORE, BLOCKS_GROUP);
    public static final Item MOON_CHEESE_BLOCK = registerBlockItem(Constants.Blocks.MOON_CHEESE_BLOCK, BLOCKS_GROUP);
    public static final Item MOON_COPPER_ORE = registerBlockItem(Constants.Blocks.MOON_COPPER_ORE, BLOCKS_GROUP);
    public static final Item MARS_COPPER_ORE = registerBlockItem(Constants.Blocks.MARS_COPPER_ORE, BLOCKS_GROUP);
    public static final Item DESH_ORE = registerBlockItem(Constants.Blocks.DESH_ORE, BLOCKS_GROUP);
    public static final Item ILMENITE_ORE = registerBlockItem(Constants.Blocks.ILMENITE_ORE, BLOCKS_GROUP);
    public static final Item MARS_IRON_ORE = registerBlockItem(Constants.Blocks.MARS_IRON_ORE, BLOCKS_GROUP);
    public static final Item ASTEROID_IRON_ORE = registerBlockItem(Constants.Blocks.ASTEROID_IRON_ORE, BLOCKS_GROUP);
    public static final Item MOON_TIN_ORE = registerBlockItem(Constants.Blocks.MOON_TIN_ORE, BLOCKS_GROUP);
    public static final Item MARS_TIN_ORE = registerBlockItem(Constants.Blocks.MARS_TIN_ORE, BLOCKS_GROUP);
    public static final Item GALENA_ORE = registerBlockItem(Constants.Blocks.GALENA_ORE, BLOCKS_GROUP);
    public static final Item SILICON_BLOCK = registerBlockItem(Constants.Blocks.SILICON_BLOCK, BLOCKS_GROUP);
    public static final Item SOLID_METEORIC_IRON_BLOCK = registerBlockItem(Constants.Blocks.SOLID_METEORIC_IRON_BLOCK, BLOCKS_GROUP);
    public static final Item DESH_BLOCK = registerBlockItem(Constants.Blocks.DESH_BLOCK, BLOCKS_GROUP);
    public static final Item TITANIUM_BLOCK = registerBlockItem(Constants.Blocks.TITANIUM_BLOCK, BLOCKS_GROUP);
    public static final Item LEAD_BLOCK = registerBlockItem(Constants.Blocks.LEAD_BLOCK, BLOCKS_GROUP);
    public static final Item LUNAR_SAPPHIRE_BLOCK = registerBlockItem(Constants.Blocks.LUNAR_SAPPHIRE_BLOCK, BLOCKS_GROUP);
    public static final Item CAVERNOUS_VINE = registerBlockItem(Constants.Blocks.CAVERNOUS_VINE, BLOCKS_GROUP);
    public static final Item POISONOUS_CAVERNOUS_VINE = registerBlockItem(Constants.Blocks.POISONOUS_CAVERNOUS_VINE, BLOCKS_GROUP);
    public static final Item MOON_BERRY_BUSH = registerBlockItem(Constants.Blocks.MOON_BERRY_BUSH, BLOCKS_GROUP);
    public static final Item CIRCUIT_FABRICATOR = registerBlockItem(Constants.Blocks.CIRCUIT_FABRICATOR, MACHINES_GROUP);
    public static final Item COMPRESSOR = registerBlockItem(Constants.Blocks.COMPRESSOR, MACHINES_GROUP);
    public static final Item ELECTRIC_COMPRESSOR = registerBlockItem(Constants.Blocks.ELECTRIC_COMPRESSOR, MACHINES_GROUP);
    public static final Item COAL_GENERATOR = registerBlockItem(Constants.Blocks.COAL_GENERATOR, MACHINES_GROUP);
    public static final Item BASIC_SOLAR_PANEL = registerBlockItem(Constants.Blocks.BASIC_SOLAR_PANEL, MACHINES_GROUP);
    public static final Item ADVANCED_SOLAR_PANEL = registerBlockItem(Constants.Blocks.ADVANCED_SOLAR_PANEL, MACHINES_GROUP);
    public static final Item ENERGY_STORAGE_MODULE = registerBlockItem(Constants.Blocks.ENERGY_STORAGE_MODULE, MACHINES_GROUP);
    public static final Item OXYGEN_COLLECTOR = registerBlockItem(Constants.Blocks.OXYGEN_COLLECTOR, MACHINES_GROUP);
    public static final Item REFINERY = registerBlockItem(Constants.Blocks.REFINERY, MACHINES_GROUP);

    private static BlockItem registerBlockItem(String id, ItemGroup group) {
        BlockItem item = new BlockItem(Registry.BLOCK.get(new Identifier(Constants.MOD_ID, id)), new Item.Settings().group(group));
        item.appendBlocks(Item.BLOCK_ITEMS, item);
        return Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, id), item);
    }

    private static <T extends Item> T registerItem(String id, T item) {
        return Registry.register(Registry.ITEM, new Identifier(Constants.MOD_ID, id), item);
    }
    
    public static void register() {
    }
}
